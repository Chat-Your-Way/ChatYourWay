package com.chat.yourway.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class TopicSubscriberResponseDto {

    private ContactResponseDto contact;

    private boolean isPermittedSendingMessage;

}
