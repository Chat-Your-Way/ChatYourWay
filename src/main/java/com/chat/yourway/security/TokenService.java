package com.chat.yourway.security;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.chat.yourway.exception.ServiceException;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.token.Token;
import com.chat.yourway.repository.TokenRedisRepository;
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
        .orElseThrow(() -> new ServiceException(UNAUTHORIZED,
            "Token wasn't found in repository"));
  }

  public void revokeAllContactTokens(Contact contact) {
    var validUserTokens = tokenRedisRepository.findAllByEmail(contact.getEmail());
    if (validUserTokens.isEmpty()) {
      log.info("User doesn't have saved refresh tokens");
      return;
    }
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRedisRepository.saveAll(validUserTokens);
  }

}