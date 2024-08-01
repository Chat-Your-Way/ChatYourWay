package com.chat.yourway.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class SecurityJwtProperties {
  private String tokenType;
  private String secretKey;
  private long accessExpiration;
  private long refreshExpiration;
}