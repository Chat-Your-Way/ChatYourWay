package com.chat.yourway.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MessageResponseDto {

  private UUID id;
  private UUID topicId;
  private LocalDateTime timestamp;
  private ContactResponseDto sender;
  private String content;
}
