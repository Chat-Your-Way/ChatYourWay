package com.chat.yourway.service;

import static com.chat.yourway.model.token.TokenType.*;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.dto.response.AuthResponseDto;
import com.chat.yourway.exception.ServiceException;
import com.chat.yourway.model.token.Token;
import com.chat.yourway.security.JwtService;
import com.chat.yourway.security.TokenService;
import com.chat.yourway.service.interfaces.ActivateAccountService;
import com.chat.yourway.service.interfaces.AuthenticationService;
import com.chat.yourway.service.interfaces.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

  private final ContactService contactService;
  private final JwtService jwtService;
  private final TokenService tokenService;
  private final ActivateAccountService activateAccountService;
  private final AuthenticationManager authManager;


  @Transactional
  @Override
  public AuthResponseDto register(ContactRequestDto contactRequestDto, HttpServletRequest request) {
    log.info("Started registration contact email: {}", contactRequestDto.getEmail());

    var contact = contactService.create(contactRequestDto);
    activateAccountService.sendVerifyEmail(contact, request);

    var accessToken = jwtService.generateAccessToken(contact);
    var refreshToken = jwtService.generateRefreshToken(contact);

    saveContactToken(contact.getEmail(), accessToken);
    log.info("Saved registered contact to repository");

    return AuthResponseDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Transactional
  @Override
  public AuthResponseDto authenticate(AuthRequestDto authRequestDto) {
    log.info("Started authenticate contact email: {}", authRequestDto.getEmail());
    authenticateCredentials(authRequestDto.getEmail(), authRequestDto.getPassword());

    var contact = contactService.findByEmail(authRequestDto.getEmail());

    var accessToken = jwtService.generateAccessToken(contact);
    var refreshToken = jwtService.generateRefreshToken(contact);

    saveContactToken(contact.getEmail(), accessToken);

    log.info("Contact authenticated");
    return AuthResponseDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Transactional
  @Override
  public AuthResponseDto refreshToken(HttpServletRequest request) {
    log.info("Started refreshing access token");
    final String refreshToken = jwtService.extractToken(request);
    final String email = jwtService.extractEmail(refreshToken);

    var contact = contactService.findByEmail(email);

    if (!jwtService.isTokenValid(refreshToken, contact)) {
      throw new ServiceException(UNAUTHORIZED, "Invalid refresh token");
    }

    var accessToken = jwtService.generateAccessToken(contact);
    tokenService.revokeAllContactTokens(contact);
    saveContactToken(email, accessToken);

    log.info("Refreshed access token for contact email: {}", email);
    return AuthResponseDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  private void saveContactToken(String email, String jwtToken) {
    var token = Token.builder()
        .email(email)
        .token(jwtToken)
        .tokenType(BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenService.saveToken(token);
  }

  private void authenticateCredentials(String email, String password) {
    try {
      authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    } catch (AuthenticationException e) {
      throw new ServiceException(UNAUTHORIZED, "Authentication failed, invalid email or password");
    }
  }

}
