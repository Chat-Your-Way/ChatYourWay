package com.chat.yourway.repository;

import com.chat.yourway.model.Message;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
  @Query(value = "SELECT COUNT(c.id) FROM chat.contact_message_report mc " +
          "JOIN chat.contact c ON mc.contact_id = c.id " +
          "WHERE mc.message_id = :messageId", nativeQuery = true)
  Integer getCountReportsByMessageId(Integer messageId);

  @Query(
      "select case when count (m) > 0 then true else false end "
          + "from Message m join m.contacts c where c.email = :email and m.id = :messageId")
  Boolean hasReportByContactEmailAndMessageId(String email, Integer messageId);

  @Modifying
  @Query(
          value = "INSERT INTO chat.contact_message_report (contact_id, message_id) " +
                  "SELECT c.id, :messageId " +
                  "FROM chat.contact c " +
                  "WHERE c.email = :email",
          nativeQuery = true)
  void saveReportFromContactToMessage(String email, Integer messageId);


  List<Message> findAllByTopicId(Integer topic_id);
}
