package com.chat.yourway.exception.handler;

import com.chat.yourway.dto.response.error.MessageErrorResponseDto;
import com.chat.yourway.exception.MessagePermissionDeniedException;
import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import java.util.List;
import java.util.Objects;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class WebsocketExceptionHandler {

  @MessageExceptionHandler({
      TopicNotFoundException.class,
      TopicSubscriberNotFoundException.class,
      MessagePermissionDeniedException.class
  })
  @SendToUser("/specific/error")
  public MessageErrorResponseDto<String> handleException(RuntimeException e) {
    return new MessageErrorResponseDto<>(e.getMessage());
  }

  @MessageExceptionHandler(MethodArgumentNotValidException.class)
  @SendToUser("/specific/error")
  public MessageErrorResponseDto<List<String>> handleValidationException(
      MethodArgumentNotValidException e) {
    List<FieldError> fieldErrors = Objects.requireNonNull(e.getBindingResult()).getFieldErrors();
    List<String> errorMessages = fieldErrors.stream()
        .map(err -> String.format("Invalid '%s': %s", err.getField(), err.getDefaultMessage()))
        .toList();
    return new MessageErrorResponseDto<>(errorMessages);
  }

}
