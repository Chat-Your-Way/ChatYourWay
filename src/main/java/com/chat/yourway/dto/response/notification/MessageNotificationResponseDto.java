package com.chat.yourway.dto.response.notification;

import com.chat.yourway.model.event.EventType;
import java.time.LocalDateTime;
import java.util.UUID;

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

  private UUID topicId;
  private String email;
  private EventType status;
  private LocalDateTime lastRead;

}
