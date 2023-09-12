package com.chat.yourway.service.interfaces;

import com.chat.yourway.exception.ContactAlreadySubscribedToTopicException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import com.chat.yourway.model.TopicSubscriber;
import java.util.List;

public interface TopicSubscriberService {

  /**
   * Subscribes a contact to a topic with the specified email and topic ID.
   *
   * @param email   Contact's email.
   * @param topicId Topic ID.
   * @throws ContactAlreadySubscribedToTopicException if the contact is already subscribed to the
   *                                                  topic.
   */
  void subscribeToTopic(String email, Integer topicId);

  /**
   * Unsubscribes a contact from a topic with the specified email and topic ID.
   *
   * @param email   Contact's email.
   * @param topicId Topic ID.
   * @throws TopicSubscriberNotFoundException if the contact was not subscribed to the topic.
   */
  void unsubscribeFromTopic(String email, Integer topicId);

  /**
   * Finds and returns the history of a contact's subscriptions to a one topic by email and topic
   * ID.
   *
   * @param email   Contact's email.
   * @param topicId Topic ID.
   * @return List of TopicSubscriber records representing the subscription history.
   */
  List<TopicSubscriber> findTopicSubscriberHistory(String email, Integer topicId);

  /**
   * Checks if a contact is subscribed to a topic with the specified email and topic ID.
   *
   * @param email   Contact's email.
   * @param topicId Topic ID.
   * @return true if the contact is subscribed to the topic.
   */
  boolean hesContactSubscribedToTopic(String email, Integer topicId);

}
