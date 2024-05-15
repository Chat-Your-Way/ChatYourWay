package com.chat.yourway.service;

import com.chat.yourway.dto.response.notification.LastMessageResponseDto;
import com.chat.yourway.model.event.ContactEvent;
import com.chat.yourway.model.event.EventType;
import java.util.List;
import java.util.UUID;

public interface ContactEventService {

  /**
   * Save new contact event to repository.
   *
   * @param contactEvent new contact event.
   */
  void save(ContactEvent contactEvent);

  /**
   * Get contact event by topic id and contact email.
   *
   * @param topicId topic id.
   * @param email   contact email.
   * @return contact event.
   */
  ContactEvent getByTopicIdAndEmail(UUID topicId, String email);

  List<ContactEvent> getAllByEmail(String email);

  /**
   * Update event type for all contact events by email.
   *
   * @param type  event type.
   * @param email contact email.
   */
  void updateEventTypeByEmail(EventType type, String email);

  /**
   * Get all contact events by topic id.
   *
   * @param topicId topic id.
   * @return all contact events by topic id.
   */
  List<ContactEvent> getAllByTopicId(UUID topicId);

  /**
   * Set last message to all topic subscribers events.
   *
   * @param topicId        topic id.
   * @param lastMessageDto last message Dto.
   */
  void updateMessageInfoForAllTopicSubscribers(UUID topicId,
                                               LastMessageResponseDto lastMessageDto);

  /**
   * Update typing status.
   *
   * @param email    user email.
   * @param isTyping typing status.
   */
  void updateTypingEvent(String email, boolean isTyping);

}
