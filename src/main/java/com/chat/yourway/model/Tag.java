package com.chat.yourway.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(schema = "chat", name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_seq_gen")
    @SequenceGenerator(name = "tag_seq_gen", sequenceName = "chat.tag_id_seq", allocationSize = 1)
    private Integer id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;
}
