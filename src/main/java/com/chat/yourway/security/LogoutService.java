package com.chat.yourway.security;

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
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication auth) {
    log.info("Started logout");
    final String token = jwtService.extractToken(request);

    var storedToken = tokenService.findByToken(token);

    storedToken.setExpired(true);
    storedToken.setRevoked(true);
    tokenService.saveToken(storedToken);

    SecurityContextHolder.clearContext();
    log.info("Logout for contact email: {}", storedToken.email);
  }

}
