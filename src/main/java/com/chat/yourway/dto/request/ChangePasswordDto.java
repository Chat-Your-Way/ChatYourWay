package com.chat.yourway.dto.request;

import com.chat.yourway.annotation.PasswordValidation;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePasswordDto {
    @PasswordValidation
    private String oldPassword;

    @PasswordValidation
    private String newPassword;
}
