package com.chat.yourway.dto.response.error;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class MessageErrorResponseDto<T> {

  private final LocalDateTime timestamp = LocalDateTime.now();
  private final String sentFrom = "Server";
  private T message;

}
