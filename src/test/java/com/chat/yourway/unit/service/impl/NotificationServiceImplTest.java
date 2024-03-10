package com.chat.yourway.unit.service.impl;

import static com.chat.yourway.model.event.EventType.OFFLINE;
import static com.chat.yourway.model.event.EventType.ONLINE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chat.yourway.dto.response.LastMessageResponseDto;
import com.chat.yourway.dto.response.MessageNotificationResponseDto;
import com.chat.yourway.mapper.MessageNotificationMapper;
import com.chat.yourway.model.event.ContactEvent;
import com.chat.yourway.service.NotificationServiceImpl;
import com.chat.yourway.service.interfaces.ContactEventService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

  @Mock
  private ContactEventService contactEventService;

  @Mock
  private MessageNotificationMapper notificationMapper;

  @InjectMocks
  private NotificationServiceImpl notificationService;

  @Test
  @DisplayName("notifyTopicSubscribers should return a list of notifications")
  void notifyTopicSubscribers_shouldReturnListOfNotifications() {
    // Given
    Integer topicId = 1;
    var lastMessageDto = new LastMessageResponseDto();
    lastMessageDto.setTimestamp(LocalDateTime.now());
    lastMessageDto.setSentFrom("vasil@gmail.com");
    lastMessageDto.setLastMessage("Hello");

    List<ContactEvent> events = Arrays.asList(
        new ContactEvent("vasil@gmail.com", topicId, ONLINE, LocalDateTime.now(), lastMessageDto),
        new ContactEvent("anton@gmail.com", topicId, OFFLINE, LocalDateTime.now(), lastMessageDto)
    );

    var expectedNotifications = events.stream()
        .map(e -> {
          var messageNotification = new MessageNotificationResponseDto();
          messageNotification.setEmail(e.getEmail());
          messageNotification.setTopicId(e.getTopicId());
          messageNotification.setStatus(e.getEventType());
          messageNotification.setLastRead(e.getTimestamp());
          return messageNotification;
        })
        .toList();

    when(contactEventService.getAllByTopicId(topicId)).thenReturn(events);
    when(notificationMapper.toNotificationResponseDto(any()))
        .thenReturn(expectedNotifications.get(0), expectedNotifications.get(1));

    // When
    List<MessageNotificationResponseDto> result = notificationService.notifyTopicSubscribers(
        topicId);

    // Then
    assertEquals(expectedNotifications, result, "Should return the expected list of notifications");
    verify(notificationMapper, times(2)).toNotificationResponseDto(any());
  }

}
