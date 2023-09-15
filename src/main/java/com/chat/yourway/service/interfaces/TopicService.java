package com.chat.yourway.service.interfaces;

import com.chat.yourway.model.Topic;

import java.util.List;

public interface TopicService {
    /**
     * Retrieves a list of topics that are associated with the specified tag identified by its ID.
     *
     * @param tagId The unique identifier of the tag for which topics are to be retrieved.
     * @return A list of {@link Topic} objects associated with the given tag.
     *         An empty list is returned if no topics are found for the specified tag.
     */
    List<Topic> findTopicsByTag(Integer tagId);
}
