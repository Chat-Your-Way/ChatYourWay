package com.chat.yourway.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(schema = "chat", name="email_token")
public class EmailToken {
    @Id
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private EmailMessageType messageType;

    @OneToOne
    @JoinColumn(name = "contact_id", referencedColumnName = "id")
    private Contact contact;
}
