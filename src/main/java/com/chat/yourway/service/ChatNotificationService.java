package com.chat.yourway.service;

import java.util.UUID;

public interface ChatNotificationService {

  /**
   * Notify all topic subscribers for chat events.
   *
   * @param topicId The id of the topic.
   */
  void notifyTopicSubscribers(UUID topicId);

  /**
   * Notify everyone who is subscribed to the same topic as the user email.
   *
   * @param userEmail user email.
   */
  void notifyAllWhoSubscribedToSameUserTopic(String userEmail);

  /**
   * Notify all topics for chat events.
   *
   * @param userEmail user email.
   */
  void notifyAllTopics(String userEmail);

  /**
   * Notify everyone who is subscribed to the same topic.
   *
   * @param topicId The id of the topic.
   */
  void notifyAllWhoSubscribedToTopic(UUID topicId);

  /**
   * Update topic notification for user who subscribed to the same topic.
   *
   * @param topicId The id of the topic.
   */
  void updateNotificationForAllWhoSubscribedToTopic(UUID topicId);

  /**
   * Update notification for all topics.
   *
   * @param email user email.
   */
  void updateNotificationForAllTopics(String email);

}
