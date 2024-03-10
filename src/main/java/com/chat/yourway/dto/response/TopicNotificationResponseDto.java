package com.chat.yourway.dto.response;

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
public class TopicNotificationResponseDto {

  private TopicResponseDto topic;

  private Integer unreadMessages;

  private LastMessageResponseDto lastMessage;


}
