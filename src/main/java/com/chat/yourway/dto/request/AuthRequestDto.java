package com.chat.yourway.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
  @NotBlank(message = "The email should not be blank")
  @Size(min = 6, max = 320, message = "The email must be at least 6 characters and not be longer than 320 characters")
  @Pattern(regexp = "[a-z0-9.\\-_]+@[a-z]+\\.[a-z]{2,3}", message = "The email is invalid")
  private String email;

  @NotBlank(message = "The password should not be blank")
  @Size(min = 4, max = 12, message = "The password must be at least 4 characters and not be longer than 12 characters")
  @Pattern(regexp = ".*[.,\\\\-_+&!;:'#*?].*", message = "Password must include at least 1 special symbol: . , - _ + & ! ; : ' # * ?")
  @Pattern(regexp = ".*[A-Z].*", message = "Password must include at least 1 Upper-case letter")
  @Pattern(regexp = ".*\\d.*", message = "Password must include at least 1 digit")
  private String password;
}
