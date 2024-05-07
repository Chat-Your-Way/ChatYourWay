package com.chat.yourway.dto.response.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ApiErrorResponseDto {

  @Schema(description = "HttpStatus of error", example = "BAD_REQUEST")
  private final HttpStatus httpStatus;
  @Schema(description = "Error message", example = "Example error message")
  private final String message;

}
