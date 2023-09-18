package com.chat.yourway.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(schema = "chat", name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_seq_gen")
    @SequenceGenerator(name = "message_seq_gen", sequenceName = "chat.message_id_seq", allocationSize = 1)
    private Integer id;
    @Column(name = "sent_from", nullable = false)
    private String sentFrom;
    @Column(name = "send_to")
    private String sendTo;
    @Column(name = "content", nullable = false)
    private String content;
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    @ManyToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "id", nullable = false)
    private Topic topic;
}
