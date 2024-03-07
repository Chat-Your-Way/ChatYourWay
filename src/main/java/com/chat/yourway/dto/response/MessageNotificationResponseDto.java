package com.chat.yourway.dto.response;

import com.chat.yourway.model.event.EventType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class MessageNotificationResponseDto {

  private String email;
  private Integer topicId;
  private EventType status;
  private Integer unreadMessages;
  private LocalDateTime lastRead;
  private LastMessageResponseDto lastMessage;

}
