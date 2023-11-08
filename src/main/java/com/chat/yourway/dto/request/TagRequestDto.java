package com.chat.yourway.dto.request;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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
public class TagRequestDto {

  @Schema(description = "New Tag name", example = "#programming")
  @NotEmpty(message = "Tag name cannot be empty")
  @Size(min = 2, max = 30, message = "Name length should be from 2 to 30 symbols")
  @JsonValue
  private String name;

}
