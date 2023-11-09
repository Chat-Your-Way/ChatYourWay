package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.request.TopicPrivateRequestDto;
import com.chat.yourway.dto.request.TopicRequestDto;
import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.exception.TopicAccessException;
import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.exception.ValueNotUniqException;
import com.chat.yourway.model.Tag;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;

public interface TopicService {

  /**
   * Creates a new topic with the specified email of the creator.
   *
   * @param topicRequestDto Request object for creating topic.
   * @param email The email of the creator.
   * @return Created topic.
   * @throws ValueNotUniqException If the topic name already in use.
   */
  TopicResponseDto create(TopicRequestDto topicRequestDto, String email);

  /**
   * Creates a new private topic with name sendTo + sendFor, and subscribe to this topic both
   * contacts.
   *
   * @param topicPrivateDto Request object for creating topic.
   * @param email The email of the creator.
   * @return Created private topic.
   * @throws ValueNotUniqException If the topic name already in use.
   */
  TopicResponseDto createPrivate(TopicPrivateRequestDto topicPrivateDto, String email);

  /**
   * Update an existing topic with the specified email of the creator.
   *
   * @param topicId The ID of the topic to find.
   * @param topicRequestDto Request object for creating topic.
   * @param email The email of the creator.
   * @return Updated topic.
   * @throws ValueNotUniqException If the topic name already in use.
   * @throws TopicAccessException if the email is not the creator of the topic.
   */
  TopicResponseDto update(Integer topicId, TopicRequestDto topicRequestDto, String email);

  /**
   * Finds a topic by ID.
   *
   * @param id The ID of the topic to find.
   * @return The found topic if it exists.
   * @throws TopicNotFoundException If the topic with the specified ID does not exist.
   */
  TopicResponseDto findById(Integer id);

  /**
   * Finds a topic by topic name.
   *
   * @param name The name of the topic to find.
   * @return The found topic if it exists.
   * @throws TopicNotFoundException If the topic with the specified name does not exist.
   */
  TopicResponseDto findByName(String name);

  /**
   * Retrieves a list of all public topics.
   *
   * @return A list of public topics.
   */
  List<TopicResponseDto> findAllPublic();

  /**
   * Deletes a topic by ID if the specified email is the creator of the topic.
   *
   * @param id The ID of the topic to delete.
   * @param email The email of the user.
   * @throws TopicAccessException if the email is not the creator of the topic.
   */
  void deleteByCreator(Integer id, String email);

  /**
   * Retrieves a list of topics that are associated with the specified tag identified by its name.
   *
   * @param tagName The unique name of the tag for which topics are to be retrieved.
   * @return A list of {@link TopicResponseDto} objects associated with the given tag. An empty list
   *     is returned if no topics are found for the specified tag.
   */
  List<TopicResponseDto> findTopicsByTagName(String tagName);

  /**
   * Adds unique tags to the repository. It trims and converts tag names to lowercase, and then
   * checks for the existence of these tags in the repository. Tags that do not already exist in the
   * repository are created and saved. The method returns a set of all tags, including both the
   * existing ones and the newly created unique tags.
   *
   * @param tags A set of tag names to be added.
   * @return A set of tags that includes both the existing tags and the newly created unique tags.
   */
  Set<Tag> addUniqTags(Set<String> tags);

  /**
   * Search topics by topic name and return list of topics
   *
   * @param topicName A name for searching topics
   */
  List<TopicResponseDto> findTopicsByTopicName(String topicName);

  /**
   * Generates a unique private topic name based on the email addresses of the sender and receiver.
   * The private name is created by concatenating the email addresses in lexicographical order,
   * separated by "<->" symbol.
   *
   * @param sendTo Email address of the receiver.
   * @param email Email address of the sender.
   * @return Unique private topic name.
   */
  String generatePrivateName(String sendTo, String email);

  /**
   * Retrieves a list of favorite topics for the specified user.
   *
   * @param userDetails The details of the user for whom favorite topics are to be retrieved.
   * @return A list of {@code TopicResponseDto} objects representing the user's favorite topics.
   */
  List<TopicResponseDto> findAllFavouriteTopics(UserDetails userDetails);
}
