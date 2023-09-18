package com.chat.yourway.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(schema = "chat", name = "topic")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "topic_seq_gen")
    @SequenceGenerator(name = "topic_seq_gen", sequenceName = "chat.topic_id_seq", allocationSize = 1)
    private Integer id;
    @Column(name = "topic_name", nullable = false, unique = true)
    private String name;
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @ManyToMany
    @JoinTable(
            name = "topic_tag",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
}
