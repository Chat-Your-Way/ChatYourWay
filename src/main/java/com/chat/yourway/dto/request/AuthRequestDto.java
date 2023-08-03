package com.chat.yourway.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link AuthRequestDto}
 *
 * @author Dmytro Trotsenko on 7/27/23
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDto {

    private String email;
    private String password;

}
