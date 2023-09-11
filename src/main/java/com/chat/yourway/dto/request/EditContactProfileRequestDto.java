package com.chat.yourway.dto.request;

import com.chat.yourway.annotation.NicknameValidation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class EditContactProfileRequestDto {
    @NicknameValidation
    private String nickname;

    @NotNull(message = "Avatar id should not be null")
    @Min(value = 1, message = "Avatar id should be greater or equals 1")
    @Max(value = 12, message = "Avatar id should be less or equals 12")
    private Byte avatarId;
}
