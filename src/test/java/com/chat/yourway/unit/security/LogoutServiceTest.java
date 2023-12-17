package com.chat.yourway.unit.security;

import static com.chat.yourway.model.token.TokenType.BEARER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chat.yourway.model.token.Token;
import com.chat.yourway.security.JwtService;
import com.chat.yourway.security.LogoutService;
import com.chat.yourway.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class LogoutServiceTest {
  @Mock
  private TokenService tokenService;

  @Mock
  private JwtService jwtService;

  @InjectMocks
  private LogoutService logoutService;

  private static final String TEST_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbnRvbkBnbWFpbC5jb20iLCJpYXQiOjE3MDI1MDQ5OTgsImV4cCI6MTcwMjUwODU5OH0.7wGtLNfSirzsKRFduxQ6Yr5Dd52c85b_PKUKEwjUSfg";

  @Test
  void logout_shouldLogoutUserAndInvalidateToken() {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    Authentication auth = mock(Authentication.class);

    when(jwtService.extractToken(request)).thenReturn(TEST_TOKEN);
    when(tokenService.findByToken(TEST_TOKEN)).thenReturn(createTestToken());

    // When
    logoutService.logout(request, response, auth);

    // Then
    verify(jwtService, times(1)).extractToken(request);
    verify(tokenService, times(1)).findByToken(TEST_TOKEN);
    verify(tokenService, times(1)).saveToken(any());
  }

  private Token createTestToken() {
    Token token = new Token();
    token.setToken(TEST_TOKEN);
    token.setEmail("vasil@gmail.com");
    token.setTokenType(BEARER);
    token.setExpired(false);
    token.setRevoked(false);
    return token;
  }
}
