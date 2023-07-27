package com.chat.yourway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link AuthResponseDto}
 *
 * @author Dmytro Trotsenko on 7/26/23
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {

    private String token;

}
