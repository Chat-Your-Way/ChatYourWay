package com.chat.yourway.repository;

public interface LastMessageRepository {
    /**
     * Sets the last message identifier for a given topic.
     *
     * @param lastMessageId The identifier of the last message in the topic.
     * @param topicId The identifier of the topic for which the last message is being set.
     */
    void setLastMessageIdTopicId(Integer lastMessageId, Integer topicId);

    /**
     * Retrieves the last message identifier for a specific topic.
     *
     * @param topicId The identifier of the topic for which the last message identifier is requested.
     * @return The last message identifier for the specified topic.
     */
    Integer getLastMessageIdByTopicId(Integer topicId);
}
