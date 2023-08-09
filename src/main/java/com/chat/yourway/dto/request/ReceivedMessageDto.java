package com.chat.yourway.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedMessageDto {

  private String sentFrom;
  private String sendTo;

  private String text;


}
