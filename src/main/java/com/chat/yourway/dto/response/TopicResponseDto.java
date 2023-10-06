package com.chat.yourway.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
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
  @Schema(description = "ID", example = "1")
  private Integer id;
  @Schema(description = "New Topic name", example = "My programming topic")
  private String topicName;
  @Schema(description = "Topic status is public", example = "true")
  private Boolean isPublic;
  @Schema(description = "Email of Topic creator", example = "example@gmail.com")
  private String createdBy;
  @Schema(description = "Created time")
  private LocalDateTime createdAt;
  @ArraySchema(schema = @Schema(description = "Tags"))
  private Set<TagResponseDto> tags;
  @ArraySchema(schema = @Schema(description = "TopicSubscribers"))
  private Set<TopicSubscriberResponseDto> topicSubscribers;

}
