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

  @Query(
      value =
          """
          SELECT *
          FROM chat.topic t
          WHERE to_tsvector('english', t.topic_name) @@ to_tsquery('english', :query)
          """,
      nativeQuery = true)
  List<Topic> findAllByTopicName(String query);

  Optional<Topic> findByTopicName(String name);

  List<Topic> findAllByIsPublicIsTrue();

  @Query(
      "select t from Topic t join fetch t.topicSubscribers ts "
          + "where ts.contact.email = :contactEmail and ts.isFavouriteTopic = true")
  List<Topic> findAllFavouriteTopicsByContactEmail(String contactEmail);

  boolean existsByIdAndIsPublic(int topicId, boolean isPublic);

  @Query(nativeQuery = true, value =
          "SELECT t.*, COUNT(ts.id) AS ts_count, COUNT(m.id) AS m_count " +
                  "FROM topic t " +
                  "JOIN topic_subscriber ts ON t.id = ts.topic_id " +
                  "JOIN message m ON t.id = m.topic_id " +
                  "WHERE t.is_public = true " +
                  "GROUP BY t.id " +
                  "ORDER BY ts_count DESC, m_count DESC")
  List<Topic> findPopularPublicTopics();
}
