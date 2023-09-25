package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.response.ContactResponseDto;
import com.chat.yourway.exception.ContactAlreadySubscribedToTopicException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
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
  void subscribeToTopicById(String email, Integer topicId);

  /**
   * Unsubscribes a contact from a topic with the specified email and topic ID.
   *
   * @param email   Contact's email.
   * @param topicId Topic ID.
   * @throws TopicSubscriberNotFoundException if the contact was not subscribed to the topic.
   */
  void unsubscribeFromTopicById(String email, Integer topicId);

  /**
   * Retrieves a list of contacts who are subscribers to the topic by ID.
   *
   * @param id The ID of the topic.
   * @return A list of contacts who are subscribers to the topic.
   */
  List<ContactResponseDto> findAllSubscribersByTopicId(Integer id);

  /**
   * Checks if a contact is subscribed to a topic with the specified email and topic ID.
   *
   * @param email   Contact's email.
   * @param topicId Topic ID.
   * @return true if the contact is subscribed to the topic.
   */
  boolean hasContactSubscribedToTopic(String email, Integer topicId);

}
