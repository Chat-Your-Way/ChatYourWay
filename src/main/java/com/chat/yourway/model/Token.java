package com.chat.yourway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

/**
 * {@link Token}
 *
 * @author Dmytro Trotsenko on 7/28/23
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Token")
public class Token {

    @Id
    @Indexed
    public String email;
    public String token;
    public TokenType tokenType;
    public boolean revoked;
    public boolean expired;

}
