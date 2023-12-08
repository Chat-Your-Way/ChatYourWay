package com.chat.yourway.unit.service.impl;

import static com.chat.yourway.model.event.EventType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chat.yourway.model.event.ContactEvent;
import com.chat.yourway.model.event.EventType;
import com.chat.yourway.repository.ContactEventRedisRepository;
import com.chat.yourway.service.ContactEventServiceImpl;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContactEventServiceImplTest {

  @Mock
  private ContactEventRedisRepository contactEventRedisRepository;

  @InjectMocks
  private ContactEventServiceImpl contactEventService;

  ContactEventServiceImplTest() {
  }

  @Test
  @DisplayName("save should save to repository")
  void save_shouldSaveToRepository() {
    // Given
    String email = "vasil@gmail.com";
    int topicId = 1;
    ContactEvent contactEvent = new ContactEvent(email, topicId, ONLINE, LocalDateTime.now(), "");

    // When
    contactEventService.save(contactEvent);

    // Then
    verify(contactEventRedisRepository, times(1)).save(contactEvent);
  }

  @Test
  @DisplayName("getByTopicIdAndEmail should return contact event from repository")
  void getByTopicIdAndEmail_shouldReturnContactEventFromRepository() {
    // Given
    Integer topicId = 1;
    String email = "vasil@gmail.com";
    ContactEvent expectedContactEvent = new ContactEvent(email, topicId, ONLINE,
        LocalDateTime.now(), "");
    when(contactEventRedisRepository.findById(email + "_" + topicId))
        .thenReturn(Optional.of(expectedContactEvent));

    // When
    ContactEvent result = contactEventService.getByTopicIdAndEmail(topicId, email);

    // Then
    assertEquals(expectedContactEvent, result, "Should return the expected contact event");
  }

  @Test
  @DisplayName("getByTopicIdAndEmail should return default contact event if not found in repository")
  void getByTopicIdAndEmail_shouldReturnDefaultContactEventIfNotFound() {
    // Given
    Integer topicId = 1;
    String email = "vasil@gmail.com";
    when(contactEventRedisRepository.findById(email + "_" + topicId)).thenReturn(Optional.empty());

    // When
    ContactEvent result = contactEventService.getByTopicIdAndEmail(topicId, email);

    // Then
    assertNotNull(result, "Should return a contact event");
    assertEquals(email, result.getEmail(), "Email should match");
    assertEquals(topicId, result.getTopicId(), "Topic ID should match");
    assertEquals(ONLINE, result.getEventType(), "Event type should be ONLINE");
  }

  @Test
  @DisplayName("getAllByEmail should return contact events from repository")
  void getAllByEmail_shouldReturnContactEventsFromRepository() {
    // Given
    String email = "vasil@gmail.com";
    List<ContactEvent> expectedContactEvents = Arrays.asList(
        new ContactEvent(email, 1, ONLINE, LocalDateTime.now(), ""),
        new ContactEvent(email, 2, OFFLINE, LocalDateTime.now(), "")
    );
    when(contactEventRedisRepository.findAllByEmail(email)).thenReturn(expectedContactEvents);

    // When
    List<ContactEvent> result = contactEventService.getAllByEmail(email);

    // Then
    assertEquals(expectedContactEvents, result, "Should return the expected contact events");
  }

  @Test
  @DisplayName("updateEventTypeByEmail should update event types in repository")
  void updateEventTypeByEmail_shouldUpdateEventTypesInRepository() {
    // Given
    String email = "vasil@gmail.com";
    EventType newEventType = OFFLINE;
    List<ContactEvent> events = Arrays.asList(
        new ContactEvent(email, 1, ONLINE, LocalDateTime.now(), ""),
        new ContactEvent(email, 2, ONLINE, LocalDateTime.now(), "")
    );
    when(contactEventRedisRepository.findAllByEmail(email)).thenReturn(events);

    // When
    contactEventService.updateEventTypeByEmail(newEventType, email);

    // Then
    events.forEach(e -> e.setEventType(newEventType));
    verify(contactEventRedisRepository, times(1)).saveAll(events);
  }

  @Test
  @DisplayName("getAllByTopicId should return contact events from repository")
  void getAllByTopicId_shouldReturnContactEventsFromRepository() {
    // Given
    Integer topicId = 1;
    List<ContactEvent> expectedContactEvents = Arrays.asList(
        new ContactEvent("vasil@gmail.com", topicId, ONLINE, LocalDateTime.now(), ""),
        new ContactEvent("anton@gmail.com", topicId, OFFLINE, LocalDateTime.now(), "")
    );
    when(contactEventRedisRepository.findAllByTopicId(topicId)).thenReturn(expectedContactEvents);

    // When
    List<ContactEvent> result = contactEventService.getAllByTopicId(topicId);

    // Then
    assertEquals(expectedContactEvents, result, "Should return the expected contact events");
  }

  @Test
  @DisplayName("setLastMessageToAllTopicSubscribers should update last messages in repository")
  void setLastMessageToAllTopicSubscribers_shouldUpdateLastMessagesInRepository() {
    // Given
    Integer topicId = 1;
    String newMessage = "New message";
    List<ContactEvent> events = Arrays.asList(
        new ContactEvent("vasil@gmail.com", topicId, ONLINE, LocalDateTime.now(), ""),
        new ContactEvent("anton@gmail.com", topicId, OFFLINE, LocalDateTime.now(), "")
    );
    when(contactEventRedisRepository.findAllByTopicId(topicId)).thenReturn(events);

    // When
    contactEventService.setLastMessageToAllTopicSubscribers(topicId, newMessage);

    // Then
    events.forEach(e -> e.setLastMessage(newMessage));
    verify(contactEventRedisRepository, times(1)).saveAll(events);
  }

}
