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

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", referencedColumnName = "id", nullable = false)
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "send_by", referencedColumnName = "id", nullable = false)
    private Contact sender;

    @Column(name = "message_text", nullable = false)
    private String content;

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            schema = "chat",
//            name = "contact_report_messages",
//            joinColumns = @JoinColumn(name = "message_id"),
//            inverseJoinColumns = @JoinColumn(name = "contact_id")
//    )
//    private Set<Contact> reportContacts = new HashSet<>();
}
