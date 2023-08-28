package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.dto.response.AuthResponseDto;
import com.chat.yourway.exception.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {

  /**
   * Registers a new contact and returns access and refresh tokens.
   *
   * @param contactRequestDto Contact registration details.
   * @param clientAddress The client address for generating the verifying link.
   * @return {@link AuthResponseDto} containing access and refresh tokens.
   */
  AuthResponseDto register(ContactRequestDto contactRequestDto, String clientAddress);

  /**
   * Authenticates a contact's credentials and generates access and refresh tokens.
   *
   * @param authRequestDto Authentication details containing email and password.
   * @return {@link AuthResponseDto} containing access and refresh tokens.
   */
  AuthResponseDto authenticate(AuthRequestDto authRequestDto);

  /**
   * Refreshes an access token using a refresh token.
   *
   * @param request HttpServletRequest.
   * @return {@link AuthResponseDto} containing access and refresh tokens.
   * @throws InvalidTokenException If the refresh token is invalid.
   */
  AuthResponseDto refreshToken(HttpServletRequest request);
}
