package com.chat.yourway.model.redis;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@RedisHash("ContactOnline")
public class ContactOnline {

    @Id
    @Indexed
    private String id;
    private LocalDateTime timestamp;
    @Indexed
    private UUID topicId;
    private boolean typingStatus;

}
