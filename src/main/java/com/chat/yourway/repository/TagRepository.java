package com.chat.yourway.repository;

import com.chat.yourway.model.Tag;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

  Set<Tag> findAllByNameIn(Set<String> name);

}
