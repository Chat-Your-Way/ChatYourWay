package com.chat.yourway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ContactResponseDto {

  private UUID id;

  private String nickname;

  private String email;

  private Byte avatarId;

  private boolean isPermittedSendingPrivateMessage;
}
