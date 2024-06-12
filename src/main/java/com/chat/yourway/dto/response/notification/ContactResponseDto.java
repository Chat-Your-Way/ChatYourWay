package com.chat.yourway.dto.response.notification;

import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ContactResponseDto {

    private UUID id;
    private boolean online;
    private UUID currentTopicId;
    private boolean typingStatus;

    public ContactResponseDto(UUID id) {
        this.id = id;
    }
}
