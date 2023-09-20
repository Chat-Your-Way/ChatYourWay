package com.chat.yourway.repository;

import com.chat.yourway.model.Topic;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {

boolean existsByIdAndCreatedBy(Integer id, String createdBy);

  @Query("SELECT t FROM Topic t join t.tags tag where tag.id = :tagId")
  List<Topic> findAllByTagId(Integer tagId);

}
