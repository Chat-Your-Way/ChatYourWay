package com.chat.yourway.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class SecurityJwtProperties {
  private String secretKey;
  private Duration accessExpiration;
  private Duration refreshExpiration;
}