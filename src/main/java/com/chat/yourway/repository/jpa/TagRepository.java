package com.chat.yourway.repository.jpa;

import com.chat.yourway.model.Tag;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
  Set<Tag> findAllByNameIn(Set<String> name);
}