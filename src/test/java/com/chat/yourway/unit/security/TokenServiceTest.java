package com.chat.yourway.unit.security;

import static com.chat.yourway.model.token.TokenType.BEARER;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chat.yourway.exception.TokenNotFoundException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.token.Token;
import com.chat.yourway.repository.TokenRedisRepository;
import com.chat.yourway.security.TokenService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

  @Mock
  private TokenRedisRepository tokenRedisRepository;

  @InjectMocks
  private TokenService tokenService;

  private static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaSIsImlhdCI6MTY5MTYwOTQzMSwiZXhwIjoxNjkxNjEzMDMxfQ.0wOuiehlbH1bGgD7vblqMI4KNhElx5DaEj4l_vEbqkI";
  public static final String EMAIL = "vasil@gmail.com";

  @Test
  @DisplayName("saveToken should save token to the repository")
  public void saveToken_shouldSaveTokenToRepository() {
    // Given
    Token token = new Token(EMAIL, JWT_TOKEN, BEARER, false, false);

    // When
    tokenService.saveToken(token);

    // Then
    verify(tokenRedisRepository, times(1)).save(token);
  }

  @Test
  @DisplayName("findByToken should return token when token exists in the repository")
  public void findByToken_shouldReturnTokenWhenTokenExists() {
    // Given
    Token token = new Token(EMAIL, JWT_TOKEN, BEARER, false, false);

    when(tokenRedisRepository.findByToken(JWT_TOKEN))
        .thenReturn(Optional.of(token));

    // When
    Token resultToken = tokenService.findByToken(JWT_TOKEN);

    // Then
    assertNotNull(resultToken);
    assertEquals(token, resultToken);
    verify(tokenRedisRepository, times(1)).findByToken(JWT_TOKEN);
  }

  @Test
  @DisplayName("findByToken should throw TokenNotFoundException when token is not found")
  void findByToken_shouldThrowTokenNotFoundExceptionWhenNotFound() {
    // Given
    String nonExistingTokenValue = "nonExistingToken";

    when(tokenRedisRepository.findByToken(nonExistingTokenValue)).thenReturn(Optional.empty());

    // When/Then
    assertThrows(TokenNotFoundException.class, () -> tokenService.findByToken(nonExistingTokenValue));
  }

  @Test
  @DisplayName("revokeAllContactTokens should set tokens to expired and revoked for a given contact")
  void revokeAllContactTokens_shouldExpireAndRevokeTokensForContact() {
    // Given
    Contact contact = new Contact();
    contact.setEmail(EMAIL);

    Token token1 = new Token(EMAIL, JWT_TOKEN, BEARER, false, false);
    Token token2 = new Token(EMAIL, JWT_TOKEN, BEARER, false, false);

    List<Token> revokedTokens = List.of(token1, token2);

    when(tokenRedisRepository.findAllByEmail(contact.getEmail())).thenReturn(revokedTokens);

    // When
    tokenService.revokeAllContactTokens(contact);

    // Then
    assertTrue(token1.isExpired() && token1.isRevoked());
    assertTrue(token2.isExpired() && token2.isRevoked());

    verify(tokenRedisRepository, times(1)).saveAll(revokedTokens);
  }

}
