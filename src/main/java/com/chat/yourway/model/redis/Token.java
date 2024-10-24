package com.chat.yourway.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@RedisHash("Token")
public class Token {
  @Id
  @Indexed
  public String email;
  @Indexed
  public String token;
  public String tokenType;
  public boolean revoked;
  public boolean expired;
}