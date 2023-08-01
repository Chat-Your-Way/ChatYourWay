package com.chat.yourway.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * {@link SendMessage}
 *
 * @author Dmytro Trotsenko on 7/22/23
 */

@Data
public class SendMessage {

    private LocalDateTime sentTime;
    private String sentFrom;
    private String sendTo;
    private String text;

}
