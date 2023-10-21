package com.chat.yourway.dto.request;

import com.chat.yourway.annotation.EmailValidation;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class TopicPrivateRequestDto {

  @Schema(description = "Contact email for sending message", example = "contact@gmail.com")
  @EmailValidation
  private String sendTo;

}
