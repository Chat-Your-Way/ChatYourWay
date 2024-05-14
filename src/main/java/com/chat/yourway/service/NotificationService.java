package com.chat.yourway.service;

import com.chat.yourway.dto.response.notification.MessageNotificationResponseDto;
import com.chat.yourway.dto.response.notification.TopicNotificationResponseDto;
import java.util.List;

public interface NotificationService {

  /**
   * Notify all topic subscribers by topic id.
   *
   * @param topicId topic id.
   * @return list of notification messages.
   */
  List<MessageNotificationResponseDto> notifyTopicSubscribers(Integer topicId);

  /**
   * Retrieves a list of notifying all topics.
   *
   * @param email user email.
   * @return A list of topic's information.
   */
  List<TopicNotificationResponseDto> notifyAllTopicsByEmail(String email);

  /**
   * Update info and return a list of notifying all topics.
   *
   * @param email user email.
   * @return A list of topic's information.
   */
  List<TopicNotificationResponseDto> updateTopicNotification(String email);

}
