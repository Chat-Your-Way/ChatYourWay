package com.chat.yourway.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePassword {
    private String oldPassword;
    private String newPassword;
}
