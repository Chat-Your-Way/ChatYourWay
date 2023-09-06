package com.chat.yourway.dto.request;

import com.chat.yourway.annotation.EmailValidation;
import com.chat.yourway.annotation.PasswordValidation;
import com.chat.yourway.annotation.UsernameValidation;
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
public class ContactRequestDto {
  @UsernameValidation
  private String username;

  @EmailValidation
  private String email;

  @PasswordValidation
  private String password;

}
