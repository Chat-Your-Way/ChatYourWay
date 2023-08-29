package com.chat.yourway.integration.service.impl;

import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.exception.ContactNotFoundException;
import com.chat.yourway.exception.PasswordsAreNotEqualException;
import com.chat.yourway.exception.ValueNotUniqException;
import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.token.Token;
import com.chat.yourway.security.JwtService;
import com.chat.yourway.security.TokenService;
import com.chat.yourway.service.AuthenticationServiceImpl;
import com.chat.yourway.service.interfaces.ActivateAccountService;
import com.chat.yourway.service.interfaces.ContactService;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({PostgresExtension.class, RedisExtension.class})
@SpringBootTest
@TestExecutionListeners(
    value = {
      TransactionalTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class,
      DependencyInjectionTestExecutionListener.class,
      DbUnitTestExecutionListener.class,
      MockitoTestExecutionListener.class,
      ResetMocksTestExecutionListener.class
    })
public class AuthenticationServiceImplTest {
  private static final String PATH = "path";
  private static final String EMAIL = "user@gmail.com";
  private static final ArgumentCaptor<Token> TOKEN_CAPTOR = ArgumentCaptor.forClass(Token.class);
  private static final ArgumentCaptor<Contact> CONTACT_CAPTOR =
      ArgumentCaptor.forClass(Contact.class);
  private static final ArgumentCaptor<Authentication> AUTHENTICATION_CAPTOR =
      ArgumentCaptor.forClass(Authentication.class);

  @Autowired private ContactService contactService;
  @MockBean private JwtService jwtService;
  @MockBean private TokenService tokenService;
  @MockBean private ActivateAccountService activateAccountService;
  @MockBean private AuthenticationManager authManager;
  @Autowired private AuthenticationServiceImpl authenticationService;

  @Test
  @DisplayName("should register new user when user passed correct data")
  @DatabaseSetup(value = "/dataset/contacts.xml", type = DatabaseOperation.INSERT)
  @DatabaseTearDown(value = "/dataset/contacts.xml", type = DatabaseOperation.DELETE)
  public void shouldRegisterNewUser_whenUserPassedCorrectData() {
    // Given
    var username = "new_username12345";
    var email = "new_user@gmail.com";
    var password = "User12346*";
    var request = new ContactRequestDto(username, email, password);

    doNothing().when(activateAccountService).sendVerifyEmail(any(Contact.class), any(String.class));
    doNothing().when(tokenService).saveToken(any(Token.class));

    // When
    var response = authenticationService.register(request, PATH);
    var newContact = contactService.findByEmail(email);

    // Then
    assertAll(
        () -> assertThat(newContact).withFailMessage("Expecting user to exist").isNotNull(),
        () -> assertThat(response).withFailMessage("Expecting response is present").isNotNull());

    verify(activateAccountService).sendVerifyEmail(CONTACT_CAPTOR.capture(), any(String.class));
    verify(jwtService).generateAccessToken(CONTACT_CAPTOR.capture());
    verify(jwtService).generateRefreshToken(CONTACT_CAPTOR.capture());
    verify(tokenService).saveToken(TOKEN_CAPTOR.capture());
  }

  @Test
  @DisplayName("should throw ValueNotUniqException when user passed already used data")
  @DatabaseSetup(value = "/dataset/contacts.xml", type = DatabaseOperation.INSERT)
  @DatabaseTearDown(value = "/dataset/contacts.xml", type = DatabaseOperation.DELETE)
  public void shouldThrowValueNotUniqException_whenUserPassedAlreadyUsedData() {
    // Given
    var username = "username12345";
    var password = "User12346*";
    var request = new ContactRequestDto(username, EMAIL, password);

    // When
    assertThrows(ValueNotUniqException.class, () -> authenticationService.register(request, PATH));

    // Then
    verify(activateAccountService, never())
        .sendVerifyEmail(CONTACT_CAPTOR.capture(), any(String.class));
    verify(jwtService, never()).generateAccessToken(CONTACT_CAPTOR.capture());
    verify(jwtService, never()).generateRefreshToken(CONTACT_CAPTOR.capture());
    verify(tokenService, never()).saveToken(TOKEN_CAPTOR.capture());
  }

  @Test
  @DisplayName("should login user when user passed correct data")
  @DatabaseSetup(value = "/dataset/contacts.xml", type = DatabaseOperation.INSERT)
  @DatabaseTearDown(value = "/dataset/contacts.xml", type = DatabaseOperation.DELETE)
  public void shouldLoginUser_whenUserPassedCorrectData() {
    // Given
    var password = "oldPassword";
    var request = new AuthRequestDto(EMAIL, password);

    doNothing().when(tokenService).saveToken(any(Token.class));

    // When
    var response = authenticationService.authenticate(request);

    // Then
    assertNotNull(response);
    verify(authManager).authenticate(AUTHENTICATION_CAPTOR.capture());
    verify(jwtService).generateAccessToken(CONTACT_CAPTOR.capture());
    verify(jwtService).generateRefreshToken(CONTACT_CAPTOR.capture());
    verify(tokenService).saveToken(TOKEN_CAPTOR.capture());
  }

  @Test
  @DisplayName("authenticate should throw ContactNotFoundException when user passed unused email")
  @DatabaseSetup(value = "/dataset/contacts.xml", type = DatabaseOperation.INSERT)
  @DatabaseTearDown(value = "/dataset/contacts.xml", type = DatabaseOperation.DELETE)
  public void authenticate_shouldContactNotFoundException_whenUserPassedUnusedEmail() {
    // Given
    var email = "not_user@gmail.com";
    var password = "User12346*";
    var request = new AuthRequestDto(email, password);

    // When
    assertThrows(ContactNotFoundException.class, () -> authenticationService.authenticate(request));

    // Then
    verify(authManager).authenticate(AUTHENTICATION_CAPTOR.capture());
    verify(jwtService, never()).generateAccessToken(CONTACT_CAPTOR.capture());
    verify(jwtService, never()).generateRefreshToken(CONTACT_CAPTOR.capture());
    verify(tokenService, never()).saveToken(TOKEN_CAPTOR.capture());
  }

  @Test
  @DisplayName(
      "authenticate should throw PasswordsAreNotEqualException when user passed incorrect password")
  @DatabaseSetup(value = "/dataset/contacts.xml", type = DatabaseOperation.INSERT)
  @DatabaseTearDown(value = "/dataset/contacts.xml", type = DatabaseOperation.DELETE)
  public void
      authenticate_shouldThrowPasswordsAreNotEqualException_whenUserPassedIncorrectPassword() {
    // Given
    var email = "user@gmail.com";
    var password = "User12346*";
    var request = new AuthRequestDto(email, password);

    // When
    assertThrows(
        PasswordsAreNotEqualException.class, () -> authenticationService.authenticate(request));

    // Then
    verify(authManager).authenticate(AUTHENTICATION_CAPTOR.capture());
    verify(jwtService, never()).generateAccessToken(CONTACT_CAPTOR.capture());
    verify(jwtService, never()).generateRefreshToken(CONTACT_CAPTOR.capture());
    verify(tokenService, never()).saveToken(TOKEN_CAPTOR.capture());
  }
}
