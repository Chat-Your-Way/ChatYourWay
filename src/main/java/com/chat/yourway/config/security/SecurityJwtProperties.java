package com.chat.yourway.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.jwt")
@Getter
@Setter
public class SecurityJwtProperties {

  private String tokenType;
  private String secretKey;
  private long expiration;
  private long refreshExpiration;

}
