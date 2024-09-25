package com.chat.yourway.dto.response.notification;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class LastMessageResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timestamp;
    private String sentFrom;
    private String lastMessage;
    private UUID topicId;

    public LastMessageResponseDto(LocalDateTime timestamp,
                                  String sentFrom,
                                  String lastMessage,
                                  UUID topicId) {
        this.timestamp = timestamp;
        this.sentFrom = sentFrom;
        this.lastMessage = lastMessage;
        this.topicId = topicId;
    }
}