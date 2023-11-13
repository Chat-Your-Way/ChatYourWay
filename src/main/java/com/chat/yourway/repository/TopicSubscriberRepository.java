package com.chat.yourway.repository;

import com.chat.yourway.model.Contact;
import com.chat.yourway.model.TopicSubscriber;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

  @Modifying
  @Query(value = """
    UPDATE chat.topic_subscriber ts
    SET ts.last_message_id = :lastMessageId
    FROM chat.contact c
    WHERE ts.topic_id = :topicId 
    AND ts.contact_id = c.id
    AND c.email = :subEmail
    """, nativeQuery = true)
  void setLastMessageByOneSubscriber(String subEmail, Integer lastMessageId, Integer topicId);

  @Modifying
  @Query(value = """
    UPDATE chat.topic_subscriber ts
    SET ts.last_message_id = :lastMessageId
    WHERE ts.topic_id = :topicId 
    AND EXISTS (
        SELECT 1
        FROM chat.contact c
        WHERE ts.subscriber_id = c.id
        AND c.email IN :subEmails
    )
    """, nativeQuery = true)
  void setLastMessageByManySubscribers(@Param("subEmails") Set<String> subEmails,
                                       @Param("lastMessageId") Integer lastMessageId,
                                       @Param("topicId") Integer topicId);
  @Query(value = """
      SELECT ts.subscriber_id from topic_subscriber ts join chat.contact c on c.id = ts.contact_id 
      WHERE ts.topic_id = :topicId AND c.email not in :subEmails
      """, nativeQuery = true)
  List<Integer> findAllSubIdsWhoNotInSubEmails(Set<String> subEmails, Integer topicId);
}
