package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.response.TopicResponseDto;
import com.chat.yourway.exception.TopicAccessException;
import com.chat.yourway.exception.TopicNotFoundException;
import java.util.List;

public interface TopicService {

  /**
   * Creates a new topic with the specified topic name and email of the creator.
   *
   * @param topicName The name of the topic to create.
   * @param email     The email of the creator.
   * @return Created topic.
   */
  TopicResponseDto create(String topicName, String email);

  /**
   * Finds a topic by ID.
   *
   * @param id The ID of the topic to find.
   * @return The found topic if it exists.
   * @throws TopicNotFoundException If the topic with the specified ID does not exist.
   */
  TopicResponseDto findById(Integer id);

  /**
   * Retrieves a list of all topics.
   *
   * @return A list of topics.
   */
  List<TopicResponseDto> findAll();

  /**
   * Deletes a topic by ID if the specified email is the creator of the topic.
   *
   * @param id    The ID of the topic to delete.
   * @param email The email of the user.
   * @throws TopicAccessException if the email is not the creator of the topic.
   */
  void deleteByCreator(Integer id, String email);

}
