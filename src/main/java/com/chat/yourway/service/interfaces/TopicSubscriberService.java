package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.response.ContactResponseDto;
import com.chat.yourway.exception.ContactAlreadySubscribedToTopicException;
import com.chat.yourway.exception.NotSubscribedTopicException;
import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface TopicSubscriberService {

  /**
   * Subscribes a contact to a topic with the specified email and topic ID.
   *
   * @param email Contact's email.
   * @param topicId Topic ID.
   * @throws ContactAlreadySubscribedToTopicException if the contact is already subscribed to the
   *     topic.
   */
  void subscribeToTopicById(String email, Integer topicId);

  /**
   * Unsubscribes a contact from a topic with the specified email and topic ID.
   *
   * @param email Contact's email.
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
   * @param email Contact's email.
   * @param topicId Topic ID.
   * @return true if the contact is subscribed to the topic.
   */
  boolean hasContactSubscribedToTopic(String email, Integer topicId);

  /**
   * Adds the specified topic to the user's list of favorite topics.
   *
   * @param topicId The ID of the topic to be added to favorites.
   * @param userDetails The details of the user for whom the topic is to be added to favorites.
   * @throws TopicNotFoundException If topic does not exist.
   * @throws NotSubscribedTopicException If contact does not subscribed to topic.
   */
  void addTopicToFavourite(Integer topicId, UserDetails userDetails);

  /**
   * Removes the specified topic from the user's list of favorite topics.
   *
   * @param topicId The ID of the topic to be removed from favorites.
   * @param userDetails The details of the user for whom the topic is to be removed from favorites.
   * @throws TopicNotFoundException If topic does not exist.
   * @throws NotSubscribedTopicException If contact does not subscribed to topic.
   */
  void removeTopicFromFavourite(Integer topicId, UserDetails userDetails);

  /**
   * Retrieves a list of online contacts associated with a specific topic.
   * This method queries the database or data source to find all online contacts that are
   * linked to the provided topic ID. Online contacts are those currently available for
   * communication.
   *
   * @param topicId The unique identifier of the topic for which you want to retrieve online contacts.
   * @return A list of {@link ContactResponseDto} objects representing online contacts
   *         associated with the specified topic. If no online contacts are found for the
   *         given topic ID, an empty list is returned. The list may also be empty if there
   *         are no online contacts available at the moment.
   *
   */
  List<ContactResponseDto> findAllOnlineContactsByTopicId(Integer topicId);
}
