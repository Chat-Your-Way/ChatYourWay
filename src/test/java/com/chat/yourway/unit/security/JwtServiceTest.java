package com.chat.yourway.unit.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.chat.yourway.config.security.SecurityJwtProperties;
import com.chat.yourway.exception.InvalidTokenException;
import com.chat.yourway.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

  @Spy
  private SecurityJwtProperties jwtProperties;

  @InjectMocks
  private JwtService jwtService;

  public static final String AUTH_HEADER = "Authorization";
  public static final String EMAIL = "anton@gmail.com";
  private static final String TOKEN_TYPE = "Bearer";
  private static final String TEST_SECRET_KEY = "D9D323C5E55F45C206D7880329B1721A4334C00F336E5F2F1E9DAB745FF44837";
  private static final long ACCESS_TOKEN_EXPIRATION = 3600000L;
  private static final long REFRESH_TOKEN_EXPIRATION = 604800000L;

  private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbnRvbkBnbWFpbC5jb20iLCJpYXQiOjE3MDI1MDQ5OTgsImV4cCI6MTcwMjUwODU5OH0.7wGtLNfSirzsKRFduxQ6Yr5Dd52c85b_PKUKEwjUSfg";

  private static UserDetails userDetails;

  @BeforeEach
  void setUp() {
    jwtProperties.setTokenType(TOKEN_TYPE);
    jwtProperties.setSecretKey(TEST_SECRET_KEY);
    jwtProperties.setExpiration(ACCESS_TOKEN_EXPIRATION);
    jwtProperties.setRefreshExpiration(REFRESH_TOKEN_EXPIRATION);

    userDetails = createUserDetails();
  }

  @Test
  @DisplayName("generateAccessToken should generate access token")
  void generateAccessToken_shouldGenerateAccessToken() {
    // When
    String accessToken = jwtService.generateAccessToken(userDetails);

    // Then
    assertNotNull(accessToken);
  }

  @Test
  @DisplayName("extractEmail should return email from token")
  void extractEmail_shouldReturnEmailFromToken() {
    // Given
    String accessToken = jwtService.generateAccessToken(userDetails);

    // When
    String extractedEmail = jwtService.extractEmail(accessToken);

    // Then
    assertNotNull(accessToken);
    assertEquals(EMAIL, extractedEmail);
  }

  @Test
  @DisplayName("generateRefreshToken should generate refresh token")
  void generateRefreshToken_shouldGenerateRefreshToken() {
    // When
    String refreshToken = jwtService.generateRefreshToken(userDetails);

    // Then
    assertNotNull(refreshToken);
  }

  @Test
  @DisplayName("extractToken should return token from HttpServletRequest")
  void extractToken_shouldReturnTokenFromHttpServletRequest() {
    // Given
    String token = jwtService.generateAccessToken(userDetails);
    HttpServletRequest request = createMockHttpServletRequest(token);

    // When
    String extractedToken = jwtService.extractToken(request);

    // Then
    assertEquals(token, extractedToken);
  }

  @Test
  @DisplayName("extractToken should throw InvalidTokenException when invalid token type")
  void extractToken_shouldThrowInvalidTokenExceptionWhenInvalidTokenType() {
    // Given
    String token = jwtService.generateAccessToken(userDetails);
    String invalidTokenType = "InvalidTokenType";

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(AUTH_HEADER)).thenReturn(invalidTokenType + " " + token);

    // When/Then
    assertThrows(InvalidTokenException.class, () -> jwtService.extractToken(request));
  }

  @Test
  @DisplayName("isTokenValid should return true for a valid token")
  void isTokenValid_shouldReturnTrueForValidToken() {
    // Given
    String token = jwtService.generateAccessToken(userDetails);

    // When
    boolean isValid = jwtService.isTokenValid(token, userDetails);

    // Then
    assertTrue(isValid);
  }

  @Test
  @DisplayName("isTokenValid should return false for invalid token")
  void isTokenValid_shouldReturnFalseForInvalidToken() {
    // Given
    String invalidToken = jwtService.generateAccessToken(User
        .withUsername("user@gmail.com")
        .password("password")
        .roles("USER")
        .build());

    // When
    boolean isValid = jwtService.isTokenValid(invalidToken, userDetails);

    // Then
    assertFalse(isValid);
  }

  @Test
  @DisplayName("isTokenValid should throw ExpiredJwtException when token was expired")
  void isTokenValid_shouldThrowExpiredJwtExceptionWhenTokenWasExpired() {
    // When/Then
    assertThrows(ExpiredJwtException.class,
        () -> jwtService.isTokenValid(EXPIRED_TOKEN, userDetails));
  }

  @Test
  @DisplayName("isTokenValid should throw SignatureException when token signature is invalid")
  void isTokenValid_shouldThrowSignatureExceptionWhenTokenSignatureIsInvalid() {
    // Given
    String invalidToken = jwtService.generateAccessToken(userDetails) + "abc";

    // When/Then
    assertThrows(SignatureException.class,
        () -> jwtService.isTokenValid(invalidToken, userDetails));
  }

  private UserDetails createUserDetails() {
    return User
        .withUsername(EMAIL)
        .password("password")
        .roles("USER")
        .build();
  }

  private HttpServletRequest createMockHttpServletRequest(String token) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(AUTH_HEADER)).thenReturn(TOKEN_TYPE + " " + token);
    return request;
  }
}
