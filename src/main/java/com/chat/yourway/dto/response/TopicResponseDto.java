package com.chat.yourway.dto.response;

import java.time.LocalDateTime;
import java.util.Set;
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
public class TopicResponseDto {

  private Integer id;

  private String topicName;

  private String createdBy;

  private LocalDateTime createdAt;

  private Set<TopicSubscriberResponseDto> topicSubscribers;

}
