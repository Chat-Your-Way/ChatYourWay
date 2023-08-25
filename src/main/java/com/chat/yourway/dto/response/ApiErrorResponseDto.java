package com.chat.yourway.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ApiErrorResponseDto {

  private final HttpStatus httpStatus;
  private final String message;

}
