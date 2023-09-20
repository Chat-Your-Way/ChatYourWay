package com.chat.yourway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ContactResponseDto {

  private Integer id;

  private String nickname;

  private String email;

  private Byte avatarId;

  private Boolean isActive;

  private Boolean isPrivate;

}
