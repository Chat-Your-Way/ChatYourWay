package com.chat.yourway.repository;

import com.chat.yourway.model.Topic;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {

  boolean existsByTopicName(String topicName);

  @Query("SELECT t FROM Topic t left join fetch t.tags tag where tag.name=:tagName")
  List<Topic> findAllByTagName(String tagName);

  Optional<Topic> findByTopicName(String name);

  List<Topic> findAllByIsPublicIsTrue();
}
