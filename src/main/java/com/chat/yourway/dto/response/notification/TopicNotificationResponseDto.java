package com.chat.yourway.dto.response.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class TopicNotificationResponseDto {

  private UUID topicId;

  private Integer unreadMessages;

  private LastMessageResponseDto lastMessage;

  private TypingEventResponseDto typingEvent;

}
