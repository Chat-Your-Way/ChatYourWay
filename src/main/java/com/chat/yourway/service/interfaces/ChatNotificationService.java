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

}
