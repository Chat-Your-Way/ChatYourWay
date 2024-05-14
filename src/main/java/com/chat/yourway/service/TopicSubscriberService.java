package com.chat.yourway.service;

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

  /**
   * Adds the specified topic to the user's list of favorite topics.
   *
   * @param topicId     The ID of the topic to be added to favorites.
   * @param userDetails The details of the user for whom the topic is to be added to favorites.
   * @throws TopicNotFoundException      If topic does not exist.
   * @throws NotSubscribedTopicException If contact does not subscribed to topic.
   */
  void addTopicToFavourite(Integer topicId, UserDetails userDetails);

  /**
   * Removes the specified topic from the user's list of favorite topics.
   *
   * @param topicId     The ID of the topic to be removed from favorites.
   * @param userDetails The details of the user for whom the topic is to be removed from favorites.
   * @throws TopicNotFoundException      If topic does not exist.
   * @throws NotSubscribedTopicException If contact does not subscribed to topic.
   */
  void removeTopicFromFavourite(Integer topicId, UserDetails userDetails);


  /**
   * Checks if sending private messages for a specific topic is prohibited for the given user.
   *
   * @param topicId      the ID of the topic to check for prohibition
   * @return true if sending private messages is prohibited, false otherwise
   */
  boolean hasProhibitionSendingPrivateMessages(Integer topicId);

  /**
   * Registers a complaint for a specific topic.
   *
   * This method allows a user to complain about a particular topic identified by its unique identifier.
   * The complaint details, such as the user's information, will be recorded for further investigation.
   *
   * @param topicId      The unique identifier of the topic being complained about.
   * @param userDetails  The details of the user lodging the complaint.
   *                     This should include relevant information like user ID, username, etc.
   *                     Ensure that the userDetails parameter is not null.
   *
   * @throws TopicNotFoundException      If topic does not exist.
   * @throws NotSubscribedTopicException If contact does not subscribed to topic.
   */
  void complainTopic(Integer topicId, UserDetails userDetails);
}
