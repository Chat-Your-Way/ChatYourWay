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
  @JoinColumn(name = "created_by", referencedColumnName = "id", nullable = false)
  private Contact createdBy;

  @Enumerated(EnumType.STRING)
  @Column(name = "scope", nullable = false, length = 50)
  private TopicScope scope;


  @Column(name = "created_at", nullable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @ManyToMany
  @JoinTable(
      schema = "chat",
      name = "topic_tags",
      joinColumns = @JoinColumn(name = "topic_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags;

  @ManyToMany
  @JoinTable(
          schema = "chat",
          name = "topic_contacts",
          joinColumns = @JoinColumn(name = "topic_id"),
          inverseJoinColumns = @JoinColumn(name = "contact_id"))
  private List<Contact> topicSubscribers = new ArrayList<>();

  @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Message> messages;
}
