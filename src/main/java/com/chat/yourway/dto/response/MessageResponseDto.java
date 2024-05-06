package com.chat.yourway.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MessageResponseDto {

  private Integer id;
  private LocalDateTime timestamp;
  private String sentFrom;
  private String sentFromNickname;
  private String sendTo;
  private String sendToNickname;
  private String content;

}
