package com.chat.yourway.dto.response;

import com.chat.yourway.dto.response.notification.LastMessageResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class PublicTopicInfoResponseDto {

  @Schema(description = "ID", example = "2D1EBC5B7D2741979CF0E84451C5AAA1")
  private UUID id;

  @Schema(description = "New Topic name", example = "My programming topic")
  private String name;

  private ContactResponseDto contact;

  @Schema(description = "Created time")
  private LocalDateTime createdAt;

  private long unreadMessageCount;

  private LastMessageResponseDto lastMessage;
}
