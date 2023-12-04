package com.chat.yourway.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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
@Table(schema = "chat", name = "topic")
public class Topic {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "topic_seq_gen")
  @SequenceGenerator(name = "topic_seq_gen", sequenceName = "chat.topic_id_seq", allocationSize = 1)
  private Integer id;

  @Column(name = "topic_name", nullable = false, unique = true)
  private String topicName;

  @Column(name = "is_public", nullable = false)
  private Boolean isPublic;

  @Column(name = "created_by", nullable = false)
  private String createdBy;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      schema = "chat",
      name = "topic_tag",
      joinColumns = @JoinColumn(name = "topic_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags;

  @OneToMany(mappedBy = "topic", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private Set<TopicSubscriber> topicSubscribers;

  @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Message> messages;
}
