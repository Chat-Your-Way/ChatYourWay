package com.chat.yourway.model.email;

import com.chat.yourway.model.Contact;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(schema = "chat", name = "email_tokens")
public class EmailToken {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID token;

  @Enumerated(EnumType.STRING)
  @Column(name = "message_type")
  private EmailMessageType messageType;

  @OneToOne
  @JoinColumn(name = "contact_id", referencedColumnName = "id")
  private Contact contact;
}
