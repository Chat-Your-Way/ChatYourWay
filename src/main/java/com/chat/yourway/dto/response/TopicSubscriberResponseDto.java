package com.chat.yourway.dto.response;

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
public class TopicSubscriberResponseDto {

  private ContactResponseDto contact;

  private LocalDateTime subscribeAt;

  private LocalDateTime unsubscribeAt;

  private boolean isPermittedSendingMessage;

}
