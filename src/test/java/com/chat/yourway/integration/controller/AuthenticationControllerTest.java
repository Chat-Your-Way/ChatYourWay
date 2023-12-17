package com.chat.yourway.integration.controller;

import static com.github.springtestdbunit.annotation.DatabaseOperation.CLEAN_INSERT;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chat.yourway.dto.request.AuthRequestDto;
import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.exception.InvalidCredentialsException;
import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.chat.yourway.security.JwtService;
import com.chat.yourway.service.interfaces.ActivateAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith({PostgresExtension.class, RedisExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@TestExecutionListeners(
    value = {
        DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        MockitoTestExecutionListener.class,
        ResetMocksTestExecutionListener.class
    })
@DatabaseSetup(value = "/dataset/mockdb/contact.xml", type = CLEAN_INSERT)
public class AuthenticationControllerTest {

  private static final String URI = "/auth";
  private static final String REFERER = "https://referrer.com";
  private static final String TOKEN_TYPE = "Bearer ";
  public static final String EMAIL_TOKEN = "24596e34-cc65-4dce-b374-7419fbfc18e5";

  @MockBean
  private AuthenticationManager authManager;

  @Autowired
  private JwtService jwtService;
  @Autowired
  private ActivateAccountService activateAccountService;
  @Autowired
  MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @SneakyThrows
  @DisplayName("register should register new contact when user inputted existed nickname")
  public void register_shouldRegisterNewContact_whenUserInputtedExistedNickname() {
    // Given
    var nickname = "NewUser";
    var password = "Password-123";
    var avatarId = (byte) 1;
    var request = new ContactRequestDto(nickname, "newuser@gmail.com", avatarId, password);

    // When
    var response =
        mockMvc.perform(
            post(URI + "/register")
                .header("Referer", REFERER)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON));

    // Then
    response
        .andExpect(status().isCreated())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  @SneakyThrows
  @DisplayName("register should return status code 409 when user inputted existed email")
  public void register_shouldReturnStatusCode409_whenUserInputtedExistedEmail() {
    // Given
    var nickname = "NewUser";
    var password = "Password-123";
    var avatarId = (byte) 1;
    var request = new ContactRequestDto(nickname, "vasil@gmail.com", avatarId, password);

    // When
    var response =
        mockMvc.perform(
            post(URI + "/register")
                .header("Referer", REFERER)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON));

    // Then
    response
        .andExpect(status().isConflict())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  @SneakyThrows
  @DisplayName("register should register new contact when user inputted uppercase email")
  public void register_shouldRegisterNewContact_whenUserInputtedUppercaseEmail() {
    // Given
    var nickname = "NewUser";
    var password = "Password-123";
    var avatarId = (byte) 1;
    var request = new ContactRequestDto(nickname, "NEWEMAIL54@GMAIL.COM", avatarId, password);

    // When
    var response =
        mockMvc.perform(
            post(URI + "/register")
                .header("Referer", REFERER)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON));

    // Then
    response
        .andExpect(status().isCreated())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  @SneakyThrows
  @DisplayName("register should return status code 409 when user inputted existed uppercase email")
  public void register_shouldReturnStatusCode409_whenUserInputtedExistedUppercaseEmail() {
    // Given
    var nickname = "NewUser";
    var password = "Password-123";
    var avatarId = (byte) 1;
    var request = new ContactRequestDto(nickname, "VASIL@GMAIL.COM", avatarId, password);

    // When
    var response =
        mockMvc.perform(
            post(URI + "/register")
                .header("Referer", REFERER)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON));

    // Then
    response
        .andExpect(status().isConflict())
        .andExpect(content().contentType(APPLICATION_JSON));
  }


  @Test
  @SneakyThrows
  @DisplayName("login should authenticate user with valid credentials")
  void login_shouldAuthenticateUserWithValidCredentials() {
    // Given
    AuthRequestDto authRequestDto = AuthRequestDto.builder()
        .email("vasil@gmail.com")
        .password("Password-123")
        .build();

    // When
    var response = mockMvc.perform(
        post(URI + "/login")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authRequestDto))
    );

    // Then
    response
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.accessToken").isString())
        .andExpect(jsonPath("$.refreshToken").isString());
  }

  @Test
  @SneakyThrows
  @DisplayName("login should return status code 404 when email was not found")
  void login_shouldReturnStatusCode404WhenEmailWasNotFound() {
    // Given
    AuthRequestDto authRequestDto = AuthRequestDto.builder()
        .email("uncknown@gmail.com")
        .password("Password-123")
        .build();

    // When
    var response = mockMvc.perform(
        post(URI + "/login")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authRequestDto))
    );

    // Then
    response
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  @SneakyThrows
  @DisplayName("login should return status code 401 when wrong password")
  void login_shouldReturnStatusCode401WhenWrongPassword() {
    // Given
    String email = "vasil@gmail.com";
    String password = "Password-321";
    AuthRequestDto authRequestDto = AuthRequestDto.builder()
        .email(email)
        .password(password)
        .build();
    when(authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password)))
        .thenThrow(InvalidCredentialsException.class);

    // When
    var response = mockMvc.perform(
        post(URI + "/login")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(authRequestDto))
    );

    // Then
    response
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  @SneakyThrows
  @DisplayName("refresh should return new access token")
  void refresh_shouldReturnNewAccessToken() {
    // Given
    String refreshToken = getRefreshToken();

    // When
    var response = mockMvc.perform(
        post(URI + "/refresh")
            .with(request -> {
              request.addHeader(AUTHORIZATION, TOKEN_TYPE + refreshToken);
              return request;
            })
            .contentType(APPLICATION_JSON)
    );

    // Then
    response
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.accessToken").isString())
        .andExpect(jsonPath("$.refreshToken").value(refreshToken));
  }

  @Test
  @SneakyThrows
  @DisplayName("refresh should return 401 when invalid refresh token")
  void refresh_shouldReturn401WhenInvalidRefreshToken() {
    // Given
    String refreshToken = "invalidRefreshToken";

    // When
    var response = mockMvc.perform(
        post(URI + "/refresh")
            .with(request -> {
              request.addHeader(AUTHORIZATION, TOKEN_TYPE + refreshToken);
              return request;
            })
            .contentType(APPLICATION_JSON)
    );

    // Then
    response
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  @SneakyThrows
  @DisplayName("activate should activate account successfully")
  @DatabaseSetup(value = "/dataset/mockdb/email_token.xml", type = CLEAN_INSERT)
  void activate_shouldActivateAccountSuccessfully() {
    // When
    var response = mockMvc.perform(
        post(URI + "/activate")
            .param("Email token", EMAIL_TOKEN)
            .contentType(APPLICATION_JSON)
    );

    // Then
    response
        .andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  @DisplayName("activate should return 404 when email token not found")
  @DatabaseSetup(value = "/dataset/mockdb/email_token.xml", type = CLEAN_INSERT)
  void activate_shouldReturn404WhenEmailTokenNotFound() {
    // When
    var response = mockMvc.perform(
        post(URI + "/activate")
            .param("Email token", "invalid_email_token")
            .contentType(APPLICATION_JSON)
    );

    // Then
    response
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  @DisplayName("logout should successfully logged out")
  void logout_shouldSuccessfullyLoggedOut() {
    // Given
    String accessToken = getAccessToken();

    // When
    var response = mockMvc.perform(
        post(URI + "/logout")
            .with(request -> {
              request.addHeader(AUTHORIZATION, TOKEN_TYPE + accessToken);
              return request;
            })
    );

    // Then
    response
        .andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  @DisplayName("logout should return 401 when invalid access token")
  void logout_shouldReturn401WhenInvalidAccessToken() {
    // Given
    String accessToken = "invalidAccessToken";

    // When
    var response = mockMvc.perform(
        post(URI + "/logout")
            .with(request -> {
              request.addHeader(AUTHORIZATION, TOKEN_TYPE + accessToken);
              return request;
            })
    );

    // Then
    response
        .andExpect(status().isUnauthorized());
  }

  private String getAccessToken() {
    return jwtService.generateAccessToken(User
        .withUsername("vasil@gmail.com")
        .password("password")
        .roles("USER")
        .build());
  }

  private String getRefreshToken() {
    return jwtService.generateRefreshToken(User
        .withUsername("vasil@gmail.com")
        .password("password")
        .roles("USER")
        .build());
  }

}
