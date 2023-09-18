package com.chat.yourway.repository;

import com.chat.yourway.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
    @Query("SELECT t FROM Topic t join t.tags tag where tag.id = :tagId")
    List<Topic> findAllByTagId(Integer tagId);
}
