package com.chat.yourway.repository.jpa;

import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Topic;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.chat.yourway.model.TopicScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {

  boolean existsByName(String topicName);

  @Query("SELECT t FROM Topic t left join fetch t.tags tag where t.scope != 'DELETED' and tag.name=:tagName")
  List<Topic> findAllByTagName(@Param("tagName") String tagName);

  @Query(
      value =
          """
              SELECT *
              FROM chat.topics t
              WHERE t.scope != 'DELETED' and to_tsvector('english', t.topic_name) @@ to_tsquery('english', :query)
              """,
      nativeQuery = true)
  List<Topic> findAllByName(@Param("query") String query);

  @Query(value = "SELECT t FROM Topic t Where t.name = :name and t.scope != 'DELETED'")
  Optional<Topic> findByName(@Param("name") String name);

  @Query(value = "SELECT t FROM Topic t Where t.id = :id and t.scope != 'DELETED'")
  Optional<Topic> findByIdAndScopeNotDeleted(@Param("id") UUID id);

  @Query(value = """
    SELECT t FROM Topic t JOIN FETCH t.topicSubscribers s
        WHERE t.scope = 'PRIVATE'
          AND EXISTS (SELECT 1 FROM t.topicSubscribers sub WHERE sub = :contact1)
          AND EXISTS (SELECT 1 FROM t.topicSubscribers sub WHERE sub = :contact2)
  """)
  Optional<Topic> findPrivateTopic(@Param("contact1") Contact contact1, @Param("contact2") Contact contact2);

  List<Topic> findAllByScope(TopicScope scope);

  @Query(value = """
    SELECT t FROM Topic t JOIN FETCH t.topicSubscribers s
            WHERE t.scope = 'PRIVATE'
                AND EXISTS (SELECT 1 FROM t.topicSubscribers sub WHERE sub = :contact)
    """)
  List<Topic> findPrivateTopics(@Param("contact") Contact contact);

  @Query(nativeQuery = true, value =
          """
                  SELECT t.*, COUNT(DISTINCT ts.contact_id) AS ts_count, 
                  COUNT(DISTINCT m.id) AS m_count FROM chat.topics t
                  JOIN chat.topic_contacts ts ON t.id = ts.topic_id
                  JOIN chat.topic_messages m ON t.id = m.topic_id
                  WHERE t.scope = 'PUBLIC'
                  GROUP BY t.id
                  ORDER BY m_count DESC, ts_count DESC
                  """)
  List<Topic> findPopularPublicTopics();
}
