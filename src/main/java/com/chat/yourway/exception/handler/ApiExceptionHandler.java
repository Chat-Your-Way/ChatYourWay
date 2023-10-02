package com.chat.yourway.exception.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.chat.yourway.dto.response.ApiErrorResponseDto;
import com.chat.yourway.exception.*;
import com.chat.yourway.exception.ContactAlreadySubscribedToTopicException;
import com.chat.yourway.exception.ContactNotFoundException;
import com.chat.yourway.exception.EmailSendingException;
import com.chat.yourway.exception.EmailTokenNotFoundException;
import com.chat.yourway.exception.InvalidCredentialsException;
import com.chat.yourway.exception.InvalidTokenException;
import com.chat.yourway.exception.PasswordsAreNotEqualException;
import com.chat.yourway.exception.TokenNotFoundException;
import com.chat.yourway.exception.TopicAccessException;
import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import com.chat.yourway.exception.ValueNotUniqException;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ApiResponse(
    responseCode = "ErrorCode",
    description = "Error response",
    content =
        @Content(
            schema = @Schema(implementation = ApiErrorResponseDto.class),
            mediaType = MediaType.APPLICATION_JSON_VALUE))
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler({
    ContactNotFoundException.class,
    EmailTokenNotFoundException.class,
    TokenNotFoundException.class,
    MessageNotFoundException.class
  })
  public ApiErrorResponseDto handleNotFoundException(RuntimeException exception) {
    return new ApiErrorResponseDto(NOT_FOUND, exception.getMessage());
  }

  @ResponseStatus(CONFLICT)
  @ExceptionHandler(ValueNotUniqException.class)
  public ApiErrorResponseDto handleValueNotUniqException(ValueNotUniqException exception) {
    return new ApiErrorResponseDto(CONFLICT, exception.getMessage());
  }

  @ResponseStatus(UNAUTHORIZED)
  @ExceptionHandler(InvalidTokenException.class)
  public ApiErrorResponseDto handleInvalidTokenException(InvalidTokenException exception) {
    return new ApiErrorResponseDto(UNAUTHORIZED, exception.getMessage());
  }

  @ResponseStatus(UNAUTHORIZED)
  @ExceptionHandler(InvalidCredentialsException.class)
  public ApiErrorResponseDto handleInvalidCredentialsException(
      InvalidCredentialsException exception) {
    return new ApiErrorResponseDto(UNAUTHORIZED, exception.getMessage());
  }

  @ResponseStatus(UNAUTHORIZED)
  @ExceptionHandler(ExpiredJwtException.class)
  public ApiErrorResponseDto handleExpiredJwtException(ExpiredJwtException exception) {
    return new ApiErrorResponseDto(UNAUTHORIZED, exception.getMessage());
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(EmailSendingException.class)
  public ApiErrorResponseDto handleEmailSendingException(EmailSendingException exception) {
    return new ApiErrorResponseDto(BAD_REQUEST, exception.getMessage());
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(PasswordsAreNotEqualException.class)
  public ApiErrorResponseDto handleOldPasswordsIsNotEqualToNewException(
      PasswordsAreNotEqualException exception) {
    return new ApiErrorResponseDto(BAD_REQUEST, exception.getMessage());
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(MessageHasAlreadyReportedException.class)
  public ApiErrorResponseDto handleMessageHasAlreadyReportedException(
      PasswordsAreNotEqualException exception) {
    return new ApiErrorResponseDto(BAD_REQUEST, exception.getMessage());
  }

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(TopicSubscriberNotFoundException.class)
  public ApiErrorResponseDto handleTopicSubscriberNotFoundException(
      TopicSubscriberNotFoundException exception) {
    return new ApiErrorResponseDto(NOT_FOUND, exception.getMessage());
  }

  @ResponseStatus(CONFLICT)
  @ExceptionHandler(ContactAlreadySubscribedToTopicException.class)
  public ApiErrorResponseDto handleContactAlreadySubscribedToTopicException(
      ContactAlreadySubscribedToTopicException exception) {
    return new ApiErrorResponseDto(CONFLICT, exception.getMessage());
  }

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(TopicNotFoundException.class)
  public ApiErrorResponseDto handleTopicNotFoundException(TopicNotFoundException exception) {
    return new ApiErrorResponseDto(NOT_FOUND, exception.getMessage());
  }

  @ResponseStatus(FORBIDDEN)
  @ExceptionHandler(TopicAccessException.class)
  public ApiErrorResponseDto handleTopicAccessException(TopicAccessException exception) {
    return new ApiErrorResponseDto(FORBIDDEN, exception.getMessage());
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {

    Map<String, List<String>> errorResponse = new HashMap<>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(
            fieldError ->
                errorResponse
                    .computeIfAbsent(fieldError.getField(), key -> new ArrayList<>())
                    .add(fieldError.getDefaultMessage()));

    return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }
}
