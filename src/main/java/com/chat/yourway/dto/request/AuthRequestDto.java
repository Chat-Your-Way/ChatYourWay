package com.chat.yourway.dto.request;

import com.chat.yourway.annotation.ContactEmail;
import com.chat.yourway.annotation.Password;
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
public class AuthRequestDto {
  @ContactEmail
  private String email;
  @Password
  private String password;
}
