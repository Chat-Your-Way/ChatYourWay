package com.chat.yourway.security;

import com.chat.yourway.exception.TokenNotFoundException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.token.Token;
import com.chat.yourway.repository.redis.TokenRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

  private final TokenRedisRepository tokenRedisRepository;

  public void saveToken(Token token) {
    tokenRedisRepository.save(token);
  }

  public Token findByToken(String token) {
    return tokenRedisRepository.findByToken(token)
        .orElseThrow(() -> new TokenNotFoundException("Token wasn't found in repository"));
  }

  public void revokeAllContactTokens(Contact contact) {
    var validUserTokens = tokenRedisRepository.findAllByEmail(contact.getEmail());
    if (validUserTokens.isEmpty()) {
      return;
    }
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRedisRepository.saveAll(validUserTokens);
  }

}
