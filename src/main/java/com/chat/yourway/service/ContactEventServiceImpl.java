package com.chat.yourway.service;

import static com.chat.yourway.model.event.EventType.ONLINE;

import com.chat.yourway.model.event.ContactEvent;
import com.chat.yourway.model.event.EventType;
import com.chat.yourway.repository.ContactEventRedisRepository;
import com.chat.yourway.service.interfaces.ContactEventService;
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
        .orElse(new ContactEvent(email, topicId, ONLINE, LocalDateTime.now(), ""));
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
  public void setLastMessageToAllTopicSubscribers(Integer topicId, String message) {
    log.trace("Started setLastMessageToAllTopicSubscribers, topic id [{}], last message [{}]",
        topicId, message);

    List<ContactEvent> events = getAllByTopicId(topicId).stream()
        .peek(e -> e.setLastMessage(message))
        .toList();

    log.trace("Last message [{}] was set to all topic id [{}] subscribers", message, topicId);
    contactEventRedisRepository.saveAll(events);
  }

}
