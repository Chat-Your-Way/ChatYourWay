package com.chat.yourway.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class TopicRequestDto {

  @Schema(description = "New Topic name", example = "My programming topic")
  @NotEmpty(message = "Topic name cannot be empty")
  @Size(min = 2, max = 70, message = "Name length should be from 2 to 70 symbols")
  private String topicName;

  @ArraySchema(schema = @Schema(description = "Tags", example = "#programming"), minItems = 1, uniqueItems = true)
  @NotEmpty(message = "Topic should has at least 1 tag")
  @NotNull(message = "Topic should has at least 1 tag")
  private Set<String> tags;

}
