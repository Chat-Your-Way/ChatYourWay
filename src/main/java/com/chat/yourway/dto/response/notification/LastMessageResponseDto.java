package com.chat.yourway.dto.response.notification;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class LastMessageResponseDto {

  private static final int MAX_LENGTH = 20;

  private LocalDateTime timestamp;
  private String sentFrom;
  private String lastMessage;

  public LastMessageResponseDto(LocalDateTime timestamp, String sentFrom, String lastMessage) {
    this.timestamp = timestamp;
    this.sentFrom = sentFrom;
    this.lastMessage = lastMessage;
  }

  public void setLastMessage(String lastMessage) {
    if (lastMessage.length() <= MAX_LENGTH) {
      this.lastMessage = lastMessage;
    } else {
      this.lastMessage = lastMessage.substring(0, MAX_LENGTH) + "...";
    }

  }

}
