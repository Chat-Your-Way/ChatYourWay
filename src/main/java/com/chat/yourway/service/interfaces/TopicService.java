package com.chat.yourway.service.interfaces;

import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.model.Topic;

public interface TopicService {

  /**
   * Creates a new topic with the specified topic name and email of the creator.
   *
   * @param topicName The name of the topic to create.
   * @param email     The email of the creator.
   * @return Created topic.
   */
  Topic create(String topicName, String email);

  /**
   * Finds a topic by ID.
   *
   * @param id The ID of the topic to find.
   * @return The found topic if it exists.
   * @throws TopicNotFoundException If the topic with the specified ID does not exist.
   */
  Topic findById(Integer id);

}
