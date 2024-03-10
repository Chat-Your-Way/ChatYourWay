package com.chat.yourway.service;

import com.chat.yourway.dto.response.MessageNotificationResponseDto;
import com.chat.yourway.dto.response.TopicNotificationResponseDto;
import com.chat.yourway.mapper.MessageNotificationMapper;
import com.chat.yourway.model.event.ContactEvent;
import com.chat.yourway.service.interfaces.ContactEventService;
import com.chat.yourway.service.interfaces.MessageService;
import com.chat.yourway.service.interfaces.NotificationService;
import com.chat.yourway.service.interfaces.TopicService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

  private final ContactEventService contactEventService;
  private final TopicService topicService;
  private final MessageService messageService;
  private final MessageNotificationMapper notificationMapper;

  @Override
  public List<MessageNotificationResponseDto> notifyTopicSubscribers(Integer topicId) {
    log.trace("Started notifyTopicSubscribers by topic id [{}]", topicId);

    return contactEventService.getAllByTopicId(topicId).stream()
        .map(notificationMapper::toNotificationResponseDto)
        .toList();
  }

  @Override
  public List<TopicNotificationResponseDto> notifyAllPublicTopicsByEmail(String email) {

    return topicService.findAllPublic().stream()
        .map(topic -> {
          var event = contactEventService.getByTopicIdAndEmail(topic.getId(), email);
          var topicNotificationDto = new TopicNotificationResponseDto();
          topicNotificationDto.setTopic(topic);
          topicNotificationDto.setUnreadMessages(countUnreadMessages(event));
          topicNotificationDto.setLastMessage(event.getLastMessage());
          return topicNotificationDto;
        })
        .toList();
  }

  private int countUnreadMessages(ContactEvent event) {
    return messageService.countMessagesBetweenTimestampByTopicId(
        event.getTopicId(),
        event.getEmail(),
        event.getTimestamp());
  }

}
