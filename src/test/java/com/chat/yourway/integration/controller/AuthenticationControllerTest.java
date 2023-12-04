package com.chat.yourway.integration.controller;

import com.chat.yourway.dto.request.ContactRequestDto;
import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.chat.yourway.security.JwtService;
import com.chat.yourway.security.TokenService;
import com.chat.yourway.service.interfaces.ActivateAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
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
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({PostgresExtension.class, RedisExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@TestExecutionListeners(
    value = {
      TransactionalTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class,
      DependencyInjectionTestExecutionListener.class,
      DbUnitTestExecutionListener.class,
      MockitoTestExecutionListener.class,
      ResetMocksTestExecutionListener.class
    })
public class AuthenticationControllerTest {
  private static final String URI = "/auth";
  private static final String REFERER = "https://referrer.com";

  @MockBean private JwtService jwtService;
  @MockBean private TokenService tokenService;
  @MockBean private ActivateAccountService activateAccountService;
  @MockBean private AuthenticationManager authManager;
  @Autowired MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  @SneakyThrows
  @DisplayName("should register new contact when user inputted existed nickname")
  @DatabaseSetup(value = "/dataset/contacts.xml", type = DatabaseOperation.INSERT)
  @DatabaseTearDown(value = "/dataset/contacts.xml", type = DatabaseOperation.DELETE)
  public void shouldRegisterNewContact_whenUserInputtedExistedNickname() {
    // Given
    var nickname = "username12345";
    var password = "User12346*";
    var avatarId = (byte) 1;
    var request = new ContactRequestDto(nickname, "user2@gmail.com", avatarId, password);

    // When
    var response =
        mockMvc.perform(
            post(URI + "/register")
                .header("Referer", REFERER)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON));

    // Then
    response.andExpect(status().isCreated()).andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  @SneakyThrows
  @DisplayName("should return status code 409 when user inputted existed email")
  @DatabaseSetup(value = "/dataset/contacts.xml", type = DatabaseOperation.INSERT)
  @DatabaseTearDown(value = "/dataset/contacts.xml", type = DatabaseOperation.DELETE)
  public void shouldReturnStatusCode409_whenUserInputtedExistedEmail() {
    // Given
    var nickname = "username12345";
    var password = "User12346*";
    var avatarId = (byte) 1;
    var request = new ContactRequestDto(nickname, "user@gmail.com", avatarId, password);

    // When
    var response =
        mockMvc.perform(
            post(URI + "/register")
                .header("Referer", REFERER)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON));

    // Then
    response.andExpect(status().isConflict()).andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  @SneakyThrows
  @DisplayName("should register new contact when user inputted uppercase email")
  @DatabaseSetup(value = "/dataset/contacts.xml", type = DatabaseOperation.INSERT)
  @DatabaseTearDown(value = "/dataset/contacts.xml", type = DatabaseOperation.DELETE)
  public void shouldRegisterNewContact_whenUserInputtedUppercaseEmail() {
    // Given
    var nickname = "username12345";
    var password = "User12346*";
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
    response.andExpect(status().isCreated()).andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  @SneakyThrows
  @DisplayName("should return status code 409 when user inputted existed uppercase email")
  @DatabaseSetup(value = "/dataset/contacts.xml", type = DatabaseOperation.INSERT)
  @DatabaseTearDown(value = "/dataset/contacts.xml", type = DatabaseOperation.DELETE)
  public void shouldReturnStatusCode409_whenUserInputtedExistedUppercaseEmail() {
    // Given
    var nickname = "username12345";
    var password = "User12346*";
    var avatarId = (byte) 1;
    var request = new ContactRequestDto(nickname, "NEWEMAIL@GMAIL.COM", avatarId, password);

    // When
    var response =
        mockMvc.perform(
            post(URI + "/register")
                .header("Referer", REFERER)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON));

    // Then
    response.andExpect(status().isConflict()).andExpect(content().contentType(APPLICATION_JSON));
  }
}
