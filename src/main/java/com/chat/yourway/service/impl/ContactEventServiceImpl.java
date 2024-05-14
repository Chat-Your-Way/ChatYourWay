package com.chat.yourway.service.impl;

import static com.chat.yourway.model.event.EventType.ONLINE;
import static com.chat.yourway.model.event.EventType.SUBSCRIBED;

import com.chat.yourway.dto.response.notification.LastMessageResponseDto;
import com.chat.yourway.dto.response.notification.TypingEventResponseDto;
import com.chat.yourway.model.event.ContactEvent;
import com.chat.yourway.model.event.EventType;
import com.chat.yourway.repository.redis.ContactEventRedisRepository;
import com.chat.yourway.service.ContactEventService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactEventServiceImpl implements ContactEventService {

  private final ContactEventRedisRepository contactEventRedisRepository;

  @Override
  public void save(ContactEvent contactEvent) {
    log.trace("ContactEvent [{}] was saved", contactEvent);
    contactEventRedisRepository.save(contactEvent);
  }

  @Override
  public ContactEvent getByTopicIdAndEmail(Integer topicId, String email) {
    log.trace("Started getByTopicIdAndEmail, topicId [{}], email [{}]", topicId, email);

    return contactEventRedisRepository.findById(email + "_" + topicId)
        .orElse(new ContactEvent(email, topicId, ONLINE, LocalDateTime.now(), 0, null, null));
  }

  @Override
  public List<ContactEvent> getAllByEmail(String email) {
    log.trace("Started getAllByEmail [{}]", email);

    return contactEventRedisRepository.findAllByEmail(email);
  }

  @Override
  public void updateEventTypeByEmail(EventType type, String email) {
    log.trace("Started updateEventTypeByEmail [{}]", email);

    List<ContactEvent> updatedEvents = getAllByEmail(email).stream()
        .peek(event -> event.setEventType(type))
        .toList();

    contactEventRedisRepository.saveAll(updatedEvents);

    log.trace("Events for email: [{}] was updated: {}", email, updatedEvents);
  }

  @Override
  public List<ContactEvent> getAllByTopicId(Integer topicId) {
    log.trace("Started getAllByTopicId [{}]", topicId);

    return contactEventRedisRepository.findAllByTopicId(topicId);
  }

  @Override
  public void updateMessageInfoForAllTopicSubscribers(Integer topicId,
      LastMessageResponseDto message) {
    log.trace("Started updateMessageInfoForAllTopicSubscribers, topic id [{}], last message [{}]",
        topicId, message);

    List<ContactEvent> events = getAllByTopicId(topicId).stream()
        .peek(e -> {
          if (!e.getEventType().equals(SUBSCRIBED)) {
            e.setUnreadMessages(e.getUnreadMessages() + 1);
          }
          e.setLastMessage(message);
        })
        .toList();

    log.trace("Message info [{}] was updated for all topic id [{}] subscribers", message, topicId);
    contactEventRedisRepository.saveAll(events);
  }

  @Override
  public void updateTypingEvent(String email, boolean isTyping) {

    Integer topicId = getAllByEmail(email).stream()
        .filter(e -> e.getEventType().equals(EventType.SUBSCRIBED))
        .findFirst()
        .orElseThrow()
        .getTopicId();

    getAllByTopicId(topicId)
        .forEach(event -> {
          event.setTypingEvent(new TypingEventResponseDto(email, isTyping));
          contactEventRedisRepository.save(event);
        });


  }

}
