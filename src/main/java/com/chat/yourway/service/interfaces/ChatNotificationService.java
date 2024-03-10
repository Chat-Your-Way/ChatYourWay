package com.chat.yourway.service.interfaces;

public interface ChatNotificationService {

  /**
   * Notify all topic subscribers for chat events.
   *
   * @param topicId The id of the topic.
   */
  void notifyTopicSubscribers(Integer topicId);

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
  void notifyAllPublicTopics(String userEmail);

  /**
   * Notify everyone who is subscribed to the same topic.
   *
   * @param topicId The id of the topic.
   */
  void notifyAllWhoSubscribedToTopic(Integer topicId);

}
