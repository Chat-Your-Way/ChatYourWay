package com.chat.yourway.dto.response;

import com.chat.yourway.dto.response.notification.LastMessageResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class PrivateTopicInfoResponseDto {

  @Schema(description = "ID", example = "2D1EBC5B7D2741979CF0E84451C5AAA1")
  private UUID id;

  @Schema(description = "Contact name", example = "Andrii")
  private String name;

  private ContactResponseDto contact;

  @Schema(description = "Created time")
  private LocalDateTime createdAt;

  private long unreadMessageCount;

  private LastMessageResponseDto lastMessage;
}
