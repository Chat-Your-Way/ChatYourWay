package com.chat.yourway.service;

import com.chat.yourway.config.websocket.WebsocketProperties;
import com.chat.yourway.service.interfaces.ChatNotificationService;
import com.chat.yourway.service.interfaces.ContactEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatNotificationServiceImpl implements ChatNotificationService {

  private final WebsocketProperties properties;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final NotificationServiceImpl notificationService;
  private final ContactEventService contactEventService;

  @Override
  public void notifyTopicSubscribers(Integer topicId) {
    log.trace("Started notifyTopicSubscribers, topic id = [{}]", topicId);

    var notifications = notificationService.notifyTopicSubscribers(topicId);
    notifications
        .forEach(n -> simpMessagingTemplate.convertAndSendToUser(
            n.getEmail(), toNotifyDestination(topicId), notifications));

    log.trace("All subscribers was notified by topic id = [{}]", topicId);
  }

  @Override
  public void notifyAllWhoSubscribedToSameUserTopic(String userEmail) {
    contactEventService.getAllByEmail(userEmail)
        .forEach(e -> notifyTopicSubscribers(e.getTopicId()));
  }

  private String toNotifyDestination(Integer topicId) {
    return properties.getNotifyPrefix() + "/" + topicId;
  }

}
