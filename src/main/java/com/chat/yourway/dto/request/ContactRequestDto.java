package com.chat.yourway.dto.request;

import com.chat.yourway.annotation.EmailValidation;
import com.chat.yourway.annotation.PasswordValidation;
import com.chat.yourway.annotation.NicknameValidation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
  @NicknameValidation
  private String nickname;

  @EmailValidation
  private String email;

  @NotNull(message = "Avatar id should not be null")
  @Min(value = 1, message = "Avatar id should be greater or equals 1")
  @Max(value = 12, message = "Avatar id should be less or equals 12")
  private Byte avatarId;

  @PasswordValidation
  private String password;

}
