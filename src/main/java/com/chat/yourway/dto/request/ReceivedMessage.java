package com.chat.yourway.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link ReceivedMessage}
 *
 * @author Dmytro Trotsenko on 7/21/23
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedMessage {

    private String sentFrom;
    private String sendTo;

    private String text;


}
