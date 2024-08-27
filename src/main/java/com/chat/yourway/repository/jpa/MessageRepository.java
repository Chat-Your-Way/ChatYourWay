package com.chat.yourway.repository.jpa;

import com.chat.yourway.dto.response.notification.LastMessageResponseDto;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.chat.yourway.model.TopicScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query(value = """
            SELECT COUNT(c.id) FROM chat.contact_message_report mc 
            JOIN chat.contact c ON mc.contact_id = c.id 
            WHERE mc.message_id = :messageId """, nativeQuery = true)
    Integer getCountReportsByMessageId(UUID messageId);

    @Modifying
    @Query(value = """
                    INSERT INTO chat.contact_message_report (contact_id, message_id) 
                    SELECT c.id, :messageId
                    FROM chat.contact c 
                    WHERE c.email = :email""", nativeQuery = true)
    void saveReportFromContactToMessage(String email, UUID messageId);

    @Query("SELECT m FROM Message m WHERE m.topic.id = :topicId ORDER BY m.timestamp DESC")
    Page<Message> findAllByTopicId(@Param("topicId") UUID topic_id, Pageable pageable);

    @Query(value = """
            SELECT COUNT(*)
            FROM Message m
            WHERE m.timestamp
            BETWEEN :timestamp AND :current_timestamp
            AND m.topic.id = :topicId
            AND NOT m.sentFrom = :sentFrom
            """, nativeQuery = true)
    Integer countMessagesBetweenTimestampByTopicId(UUID topicId, String sentFrom, LocalDateTime timestamp, LocalDateTime current_timestamp);

    @Query("""
        SELECT new com.chat.yourway.dto.response.notification.LastMessageResponseDto(
            tm.timestamp, tm.sender.nickname, tm.content, t.id
        ) FROM Topic t
        LEFT JOIN t.messages tm
        WHERE t.scope = :scope
        AND (tm.timestamp IN (SELECT MAX(tm2.timestamp) FROM Message tm2 WHERE tm2.topic = t) OR tm IS NULL)
        """)
    List<LastMessageResponseDto> getLastMessages(@Param("scope") TopicScope scope);

    @Query("""
        SELECT new com.chat.yourway.dto.response.notification.LastMessageResponseDto(
            tm.timestamp, tm.sender.nickname, tm.content, t.id
        )
        FROM Topic t
        LEFT JOIN t.messages tm
        WHERE t.scope = :scope AND t.id in :topicIds
        AND (tm.timestamp IN (SELECT MAX(tm2.timestamp) FROM Message tm2 WHERE tm2.topic = t) OR tm IS NULL)
        """)
    List<LastMessageResponseDto> getLastMessagesByTopicIds(@Param("scope") TopicScope scope,
                                                           @Param("topicIds") List<UUID> topicIds);
}