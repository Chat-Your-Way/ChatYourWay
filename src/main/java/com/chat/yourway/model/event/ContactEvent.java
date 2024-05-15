package com.chat.yourway.model.event;

import com.chat.yourway.dto.response.notification.LastMessageResponseDto;
import com.chat.yourway.dto.response.notification.TypingEventResponseDto;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@NoArgsConstructor
@Getter
@Setter
@ToString
@RedisHash("ContactEvent")
public class ContactEvent {

  @Id
  @Indexed
  private String id;
  @Indexed
  private String email;
  @Indexed
  private UUID topicId;
  private EventType eventType;
  private LocalDateTime timestamp;
  private int unreadMessages;
  private LastMessageResponseDto lastMessage;
  private TypingEventResponseDto typingEvent;


  public ContactEvent(String email, UUID topicId, EventType eventType, LocalDateTime timestamp,
      int unreadMessages, LastMessageResponseDto lastMessage, TypingEventResponseDto typingEvent) {
    this.id = email + "_" + topicId;
    this.email = email;
    this.topicId = topicId;
    this.eventType = eventType;
    this.timestamp = timestamp;
    this.unreadMessages = unreadMessages;
    this.lastMessage = lastMessage;
    this.typingEvent = typingEvent;
  }

}
