package com.chat.yourway.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(schema = "chat", name = "topic_subscriber")
public class TopicSubscriber {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "topic_subscriber_seq_gen")
  @SequenceGenerator(
      name = "topic_subscriber_seq_gen",
      sequenceName = "chat.topic_subscriber_id_seq",
      allocationSize = 1)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "contact_id", referencedColumnName = "id")
  private Contact contact;

  @ManyToOne
  @JoinColumn(name = "topic_id", referencedColumnName = "id")
  @JsonIgnore
  private Topic topic;

  @Column(name = "subscribe_at", nullable = false)
  private LocalDateTime subscribeAt;

  @Column(name = "unsubscribe_at")
  private LocalDateTime unsubscribeAt;

  @Column(name = "is_favourite_topic")
  private boolean isFavouriteTopic;

  @Column(name = "is_permitted_sending_message")
  private boolean isPermittedSendingMessage;
}
