package com.chat.yourway.dto.request;

import com.chat.yourway.annotation.PasswordValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class RestorePasswordDto {

  @Schema(description = "New password", example = "Password-321")
  @PasswordValidation
  private String newPassword;

  @Schema(description = "Email token", example = "245034-cc65-4dce-b374-7419fbfc18e5")
  @NotEmpty(message = "Email token cannot be empty")
  private UUID emailToken;
}
