package com.chat.yourway.service;

import com.chat.yourway.dto.response.MessageNotificationResponseDto;
import com.chat.yourway.mapper.MessageNotificationMapper;
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

  private final MessageService messageService;
  private final ContactEventService contactEventService;
  private final MessageNotificationMapper notificationMapper;

  public List<MessageNotificationResponseDto> notifyTopicSubscribers(Integer topicId) {
    log.trace("Started notifyTopicSubscribers by topic id [{}]", topicId);

    List<ContactEvent> events = contactEventService.getAllByTopicId(topicId).stream()
        .toList();

    return events.stream()
        .map(notificationMapper::toNotificationResponseDto)
        .peek(n -> n.setUnreadMessages(countUnreadMessages(n)))
        .toList();
  }

  private int countUnreadMessages(MessageNotificationResponseDto notification) {
    return messageService.countMessagesBetweenTimestampByTopicId(
        notification.getTopicId(),
        notification.getEmail(),
        notification.getLastRead());
  }

}
