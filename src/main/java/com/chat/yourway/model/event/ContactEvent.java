package com.chat.yourway.model.event;

import java.time.LocalDateTime;
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
  private Integer topicId;
  private EventType eventType;
  private LocalDateTime timestamp;
  private String lastMessage;

  private static final int MAX_LENGTH = 20;

  public ContactEvent(String email, Integer topicId, EventType eventType, LocalDateTime timestamp, String lastMessage) {
    this.id = email + "_" + topicId;
    this.email = email;
    this.topicId = topicId;
    this.eventType = eventType;
    this.timestamp = timestamp;
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
