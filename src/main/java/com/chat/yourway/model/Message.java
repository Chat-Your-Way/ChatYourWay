package com.chat.yourway.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(schema = "chat", name = "topic_messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id", referencedColumnName = "id", nullable = false)
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "send_by", referencedColumnName = "id", nullable = false)
    private Contact sender;

    @Column(name = "message_text", nullable = false)
    private String content;

    public Message(Topic topic, Contact sender, String content) {
        this.topic = topic;
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
}