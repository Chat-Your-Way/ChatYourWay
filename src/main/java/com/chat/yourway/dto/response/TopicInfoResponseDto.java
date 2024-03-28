package com.chat.yourway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class TopicInfoResponseDto {
  @Schema(description = "ID", example = "1")
  private Integer id;
  @Schema(description = "New Topic name", example = "My programming topic")
  private String topicName;
  @Schema(description = "Email of Topic creator", example = "example@gmail.com")
  private String createdBy;
  @Schema(description = "Created time")
  private LocalDateTime createdAt;

}
