package com.chat.yourway.service.impl;

import com.chat.yourway.config.websocket.WebsocketProperties;
import com.chat.yourway.service.ChatNotificationService;
import com.chat.yourway.service.ContactEventService;
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
            n.getEmail(), toNotifyMessageDest(topicId), notifications));

    log.info("All subscribers was notified by topic id = [{}]", topicId);
  }

  @Override
  public void notifyAllWhoSubscribedToSameUserTopic(String userEmail) {
    log.trace("Started notifyAllWhoSubscribedToSameUserTopic, user email = [{}]", userEmail);

    contactEventService.getAllByEmail(userEmail)
        .forEach(e -> notifyTopicSubscribers(e.getTopicId()));

    log.info("All subscribers who subscribed to same topic was notified, email = [{}]", userEmail);
  }

  @Override
  public void notifyAllTopics(String email) {
    log.trace("Started notifyAllTopics, email = [{}]", email);

    var notifiedTopics = notificationService.notifyAllTopicsByEmail(email);
    simpMessagingTemplate.convertAndSendToUser(email, toNotifyTopicsDest(), notifiedTopics);

    log.info("All topics was notified for user email = [{}]", email);
  }

  @Override
  public void notifyAllWhoSubscribedToTopic(Integer topicId) {
    log.trace("Started notifyAllWhoSubscribedToTopic, topicId = [{}]", topicId);

    contactEventService.getAllByTopicId(topicId)
        .forEach(e -> notifyAllTopics(e.getEmail()));

    log.info("All subscribed users was notified, topicId = [{}]", topicId);
  }

  @Override
  public void updateNotificationForAllTopics(String email) {
    log.trace("Started updateNotificationForAllTopics, email = [{}]", email);

    var notifiedTopics = notificationService.updateTopicNotification(email);
    simpMessagingTemplate.convertAndSendToUser(email, toNotifyTopicsDest(), notifiedTopics);

    log.info("All topics for user was notified, email = [{}]", email);
  }

  @Override
  public void updateNotificationForAllWhoSubscribedToTopic(Integer topicId) {
    log.trace("Started updateNotificationForAllWhoSubscribedToTopic, topicId = [{}]", topicId);

    contactEventService.getAllByTopicId(topicId)
        .forEach(e -> updateNotificationForAllTopics(e.getEmail()));

    log.info("Topic notifications was updated for all subscribed users, topicId = [{}]", topicId);
  }

  private String toNotifyMessageDest(Integer topicId) {
    return properties.getNotifyPrefix() + "/" + topicId;
  }

  private String toNotifyTopicsDest() {
    return properties.getNotifyPrefix() + "/topics";
  }

}
