package com.chat.yourway.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link RegisterRequestDto}
 *
 * @author Dmytro Trotsenko on 7/26/23
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {

    private String username;
    private String email;
    private String password;

}
