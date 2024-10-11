package com.chat.yourway.security;

import com.chat.yourway.exception.TokenNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

  private final TokenService tokenService;
  private final JwtService jwtService;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
    log.trace("Started logout");

    final String token = jwtService.extractToken(request);
    final String email = jwtService.extractEmail(token);

    try {
      var storedToken = tokenService.findByToken(token);
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      tokenService.saveToken(storedToken);
    } catch (TokenNotFoundException e) {
      log.warn(e.getMessage());
    }

    SecurityContextHolder.clearContext();

    log.info("Contact email [{}] successfully logged out", email);
  }

}
