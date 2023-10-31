package com.chat.yourway.service.interfaces;

public interface NotificationMessageService {
    /**
     * Marks a specific message within a chat topic as read by the specified user.
     *
     * @param topicId    The identifier of the chat topic.
     * @param messageId  The identifier of the message to be marked as read.
     * @param userEmail  The email address of the user who read the message.
     */
    void readMessage(Integer topicId, Integer messageId, String userEmail);

    /**
     * Marks all unread messages within a chat topic as read by the specified user.
     *
     * @param topicId    The identifier of the chat topic.
     * @param userEmail  The email address of the user who read all messages.
     */
    void readAllMessages(Integer topicId, String userEmail);

    /**
     * Sends a notification to subscribers of a specific chat topic.
     *
     * @param topicId The identifier of the chat topic for which a notification is sent.
     */
    void sendNotification(Integer topicId);

    /**
     * Sets the last message for subscribers of a specific chat topic.
     *
     * @param topicId The identifier of the chat topic for which the last message is set.
     */
    void setLastMessageSubsByTopicId(Integer topicId);
}
