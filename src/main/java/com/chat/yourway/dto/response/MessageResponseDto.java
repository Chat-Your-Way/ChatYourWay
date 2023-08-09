package com.chat.yourway.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponseDto {

  private LocalDateTime sentTime;
  private String sentFrom;
  private String sendTo;
  private String text;

}
