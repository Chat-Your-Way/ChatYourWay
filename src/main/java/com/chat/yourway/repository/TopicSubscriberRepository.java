package com.chat.yourway.repository;

import com.chat.yourway.model.TopicSubscriber;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicSubscriberRepository extends JpaRepository<TopicSubscriber, Integer> {

  List<TopicSubscriber> findAllByContactEmailAndTopicId(String email, Integer topicId);

}
