package com.chat.yourway.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@EqualsAndHashCode(of = {"id"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(schema = "chat", name = "topics")
public class Topic {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "topic_name", nullable = false, unique = true)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "contact_nickname", referencedColumnName = "nickname", nullable = false)
  private Contact contact;

  @Enumerated(EnumType.STRING)
  private TopicScope scope;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      schema = "chat",
      name = "topic_tags",
      joinColumns = @JoinColumn(name = "topic_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          schema = "chat",
          name = "topic_contacts",
          joinColumns = @JoinColumn(name = "topic_id"),
          inverseJoinColumns = @JoinColumn(name = "contact_id"))
  private List<Contact> topicSubscribers = new ArrayList<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          schema = "chat",
          name = "topic_complaints",
          joinColumns = @JoinColumn(name = "topic_id"),
          inverseJoinColumns = @JoinColumn(name = "contact_id"))
  private List<Contact> topicComplaints = new ArrayList<>();

  @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Message> messages;
}
