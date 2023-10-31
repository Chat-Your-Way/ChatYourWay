package com.chat.yourway.repository;

import java.util.Set;

public interface ChatTopicSubscriberRepository {
    /**
     * Adds a subscriber's email address to a specific chat topic.
     *
     * @param subEmail The email address of the subscriber to be added.
     * @param topicId The identifier of the chat topic to which the subscriber is added.
     */
    void addSubEmailToTopic(String subEmail, Integer topicId);

    /**
     * Deletes a subscriber's email address from a specific chat topic.
     *
     * @param subEmail The email address of the subscriber to be removed.
     * @param topicId The identifier of the chat topic from which the subscriber is removed.
     */
    void deleteSubEmailToTopic(String subEmail, Integer topicId);

    /**
     * Retrieves a set of email addresses of subscribers who are currently online for a specific chat topic.
     *
     * @param topicId The identifier of the chat topic for which online subscribers are requested.
     * @return A set of email addresses representing online subscribers for the specified topic.
     */
    Set<String> getSubsWhoOnlineByTopicId(Integer topicId);
}
