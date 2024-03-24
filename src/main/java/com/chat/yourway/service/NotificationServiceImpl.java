package com.chat.yourway.service;

import com.chat.yourway.dto.response.MessageNotificationResponseDto;
import com.chat.yourway.dto.response.TopicNotificationResponseDto;
import com.chat.yourway.mapper.NotificationMapper;
import com.chat.yourway.model.event.ContactEvent;
import com.chat.yourway.service.interfaces.ContactEventService;
import com.chat.yourway.service.interfaces.MessageService;
import com.chat.yourway.service.interfaces.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

  private final ContactEventService contactEventService;
  private final MessageService messageService;
  private final NotificationMapper notificationMapper;

  @Override
  public List<MessageNotificationResponseDto> notifyTopicSubscribers(Integer topicId) {
    log.trace("Started notifyTopicSubscribers by topic id [{}]", topicId);

    return contactEventService.getAllByTopicId(topicId).stream()
        .map(notificationMapper::toMessageNotificationResponseDto)
        .toList();
  }

  @Override
  public List<TopicNotificationResponseDto> notifyAllTopicsByEmail(String email) {

    return contactEventService.getAllByEmail(email).stream()
        .peek(this::countUnreadMessages)
        .map(notificationMapper::toTopicNotificationResponseDto)
        .toList();
  }

  @Override
  public List<TopicNotificationResponseDto> updateTopicNotification(String email) {
    return contactEventService.getAllByEmail(email).stream()
        .map(notificationMapper::toTopicNotificationResponseDto)
        .toList();
  }

  private void countUnreadMessages(ContactEvent event) {
    event.setUnreadMessages(messageService.countMessagesBetweenTimestampByTopicId(
        event.getTopicId(),
        event.getEmail(),
        event.getTimestamp()));
    contactEventService.save(event);
  }

}
