package com.chat.yourway.repository;

import com.chat.yourway.model.Contact;
import com.chat.yourway.model.TopicSubscriber;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicSubscriberRepository extends JpaRepository<TopicSubscriber, Integer> {

  @Modifying
  @Query(value = """
      INSERT INTO chat.topic_subscriber (contact_id, topic_id, subscribe_at)
      SELECT c.id, t.id, CURRENT_TIMESTAMP
      FROM chat.contact c, chat.topic t
      WHERE c.email = :contactEmail AND t.id = :topicId
      """, nativeQuery = true)
  void subscribe(String contactEmail, Integer topicId);

  @Modifying
  @Query(value = """
      UPDATE TopicSubscriber ts
      SET ts.unsubscribeAt = CURRENT_TIMESTAMP
      WHERE ts.contact.id = (SELECT id FROM Contact WHERE email = :contactEmail)
                             AND ts.topic.id = :topicId
                             AND ts.unsubscribeAt IS NULL
      """)
  void unsubscribe(String contactEmail, Integer topicId);

  boolean existsByContactEmailAndTopicIdAndUnsubscribeAtIsNull(String email, Integer topicId);

  @Query(""" 
      SELECT c FROM TopicSubscriber ts
      JOIN ts.contact c
      WHERE ts.topic.id = :topicId AND ts.unsubscribeAt IS NULL
      """)
  List<Contact> findAllActiveSubscribersByTopicId(Integer topicId);

}
