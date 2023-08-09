package com.chat.yourway.security;

import com.chat.yourway.exception.ServiceException;
import com.chat.yourway.repository.TokenRedisRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRedisRepository tokenRedisRepository;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {
    final String jwtToken;

    try {
      jwtToken = jwtService.extractToken(request);
    } catch (ServiceException e) {
      filterChain.doFilter(request, response);
      return;
    }

    final String email = jwtService.extractEmail(jwtToken);

    if (email != null && getAuthentication() == null) {
      var userDetails = userDetailsService.loadUserByUsername(email);

      if (isTokenValid(jwtToken, userDetails)) {
        setAuthentication(userDetails, request);
      }
    }
    filterChain.doFilter(request, response);
  }

  private Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  private Boolean isTokenValid(String jwtToken, UserDetails userDetails) {
    Boolean isSavedTokenValid = tokenRedisRepository.findByToken(jwtToken)
        .map(token -> !token.isExpired() && !token.isRevoked())
        .orElse(false);
    return jwtService.isTokenValid(jwtToken, userDetails) && isSavedTokenValid;
  }

  private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
    var authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
        userDetails.getAuthorities());
    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

}
