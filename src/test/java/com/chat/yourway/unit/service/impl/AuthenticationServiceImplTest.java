package com.chat.yourway.unit.service.impl;

import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.exception.ContactNotFoundException;
import com.chat.yourway.exception.PasswordsAreNotEqualException;
import com.chat.yourway.exception.ValueNotUniqException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.token.Token;
import com.chat.yourway.security.JwtService;
import com.chat.yourway.security.TokenService;
import com.chat.yourway.service.AuthenticationServiceImpl;
import com.chat.yourway.service.interfaces.ActivateAccountService;
import com.chat.yourway.service.interfaces.ContactService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {
  private static final String PATH = "path";
  private static final ArgumentCaptor<Token> TOKEN_CAPTOR = ArgumentCaptor.forClass(Token.class);
  private static final ArgumentCaptor<Contact> CONTACT_CAPTOR =
      ArgumentCaptor.forClass(Contact.class);
  private static final ArgumentCaptor<ContactRequestDto> CONTACT_REQUEST_DTO_CAPTOR =
      ArgumentCaptor.forClass(ContactRequestDto.class);
  private static final ArgumentCaptor<Authentication> AUTHENTICATION_CAPTOR =
      ArgumentCaptor.forClass(Authentication.class);

  @Spy private ContactService contactService;
  @Mock private JwtService jwtService;
  @Mock private TokenService tokenService;
  @Spy private ActivateAccountService activateAccountService;
  @Mock private AuthenticationManager authManager;
  @InjectMocks private AuthenticationServiceImpl authenticationService;

  @Test
  @DisplayName("register should register new user when user passed correct data")
  public void register_shouldRegisterNewUser_whenUserPassedCorrectData() {
    // Given
    var username = "username12345";
    var email = "user@gmail.com";
    var password = "User12346*";
    var request = new ContactRequestDto(username, email, password);
    var contact =
        Contact.builder()
            .id(1)
            .username(username)
            .email(email)
            .password(password)
            .isActive(true)
            .isPrivate(true)
            .build();

    when(contactService.create(any(ContactRequestDto.class))).thenReturn(contact);
    doNothing().when(activateAccountService).sendVerifyEmail(any(Contact.class), any(String.class));
    doNothing().when(tokenService).saveToken(any(Token.class));

    // When
    var response = authenticationService.register(request, PATH);

    // Then
    assertNotNull(response);
    verify(contactService).create(CONTACT_REQUEST_DTO_CAPTOR.capture());
    verify(activateAccountService).sendVerifyEmail(CONTACT_CAPTOR.capture(), any(String.class));
    verify(jwtService).generateAccessToken(CONTACT_CAPTOR.capture());
    verify(jwtService).generateRefreshToken(CONTACT_CAPTOR.capture());
    verify(tokenService).saveToken(TOKEN_CAPTOR.capture());
  }

  @Test
  @DisplayName("register should throw ValueNotUniqException when user passed already used data")
  public void register_shouldThrowValueNotUniqException_whenUserPassedAlreadyUsedData() {
    // Given
    var username = "username12345";
    var email = "user@gmail.com";
    var password = "User12346*";
    var request = new ContactRequestDto(username, email, password);

    doThrow(ValueNotUniqException.class).when(contactService).create(any(ContactRequestDto.class));

    // When
    assertThrows(ValueNotUniqException.class, () -> authenticationService.register(request, PATH));

    // Then
    verify(contactService).create(CONTACT_REQUEST_DTO_CAPTOR.capture());
    verify(activateAccountService, never())
        .sendVerifyEmail(CONTACT_CAPTOR.capture(), any(String.class));
    verify(jwtService, never()).generateAccessToken(CONTACT_CAPTOR.capture());
    verify(jwtService, never()).generateRefreshToken(CONTACT_CAPTOR.capture());
    verify(tokenService, never()).saveToken(TOKEN_CAPTOR.capture());
  }

  @Test
  @DisplayName("authenticate should login user when user passed correct data")
  public void authenticate_shouldLoginUser_whenUserPassedCorrectData() {
    // Given
    var email = "user@gmail.com";
    var password = "User12346*";
    var request = new AuthRequestDto(email, password);
    var contact =
        Contact.builder()
            .id(1)
            .username("username")
            .email(email)
            .password(password)
            .isActive(true)
            .isPrivate(true)
            .build();

    when(contactService.findByEmail(anyString())).thenReturn(contact);
    doNothing().when(tokenService).saveToken(any(Token.class));

    // When
    var response = authenticationService.authenticate(request);

    // Then
    assertNotNull(response);
    verify(authManager).authenticate(AUTHENTICATION_CAPTOR.capture());
    verify(contactService).findByEmail(anyString());
    verify(jwtService).generateAccessToken(CONTACT_CAPTOR.capture());
    verify(jwtService).generateRefreshToken(CONTACT_CAPTOR.capture());
    verify(tokenService).saveToken(TOKEN_CAPTOR.capture());
  }

  @Test
  @DisplayName("authenticate should throw ContactNotFoundException when user passed unused email")
  public void authenticate_shouldContactNotFoundException_whenUserPassedUnusedEmail() {
    // Given
    var email = "user@gmail.com";
    var password = "User12346*";
    var request = new AuthRequestDto(email, password);

    doThrow(ContactNotFoundException.class).when(contactService).findByEmail(anyString());

    // When
    assertThrows(ContactNotFoundException.class, () -> authenticationService.authenticate(request));

    // Then
    verify(authManager).authenticate(AUTHENTICATION_CAPTOR.capture());
    verify(contactService).findByEmail(anyString());
    verify(jwtService, never()).generateAccessToken(CONTACT_CAPTOR.capture());
    verify(jwtService, never()).generateRefreshToken(CONTACT_CAPTOR.capture());
    verify(tokenService, never()).saveToken(TOKEN_CAPTOR.capture());
  }

  @Test
  @DisplayName(
      "authenticate should throw PasswordsAreNotEqualException when user passed incorrect password")
  public void
      authenticate_shouldThrowPasswordsAreNotEqualException_whenUserPassedIncorrectPassword() {
    // Given
    var email = "user@gmail.com";
    var password = "User12346*";
    var request = new AuthRequestDto(email, password);

    doThrow(PasswordsAreNotEqualException.class)
        .when(authManager)
        .authenticate(any(Authentication.class));

    // When
    assertThrows(
        PasswordsAreNotEqualException.class, () -> authenticationService.authenticate(request));

    // Then
    verify(authManager).authenticate(AUTHENTICATION_CAPTOR.capture());
    verify(contactService, never()).findByEmail(anyString());
    verify(jwtService, never()).generateAccessToken(CONTACT_CAPTOR.capture());
    verify(jwtService, never()).generateRefreshToken(CONTACT_CAPTOR.capture());
    verify(tokenService, never()).saveToken(TOKEN_CAPTOR.capture());
  }
}
