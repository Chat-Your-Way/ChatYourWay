package com.chat.yourway.service;

import com.chat.yourway.dto.request.MessageRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.dto.response.notification.LastMessageResponseDto;
import com.chat.yourway.exception.MessageHasAlreadyReportedException;
import com.chat.yourway.exception.MessageNotFoundException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.chat.yourway.model.TopicScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    MessageResponseDto sendToTopic(UUID topicId, MessageRequestDto message, String email);

    MessageResponseDto sendToContact(String sendToEmail, MessageRequestDto message, String email);

    /**
     * Reports a message identified by its unique message ID, indicating a violation or
     * inappropriate content, associated with the provided user details.
     *
     * @param messageId The unique identifier of the message to be reported.
     * @param email     The email of the user reporting the message.
     * @throws MessageNotFoundException           If messageId is null or negative, or userDetails
     *                                            is null.
     * @throws MessageHasAlreadyReportedException If the user reporting the message does not have
     *                                            the necessary permissions.
     */
    void reportMessageById(Integer messageId, String email);

    /**
     * Retrieves a list of messages based on the given topic ID.
     *
     * @param topicId  The unique identifier of the topic.
     * @param pageable Set parameters for pagination
     * @return A list of {@link MessageResponseDto} containing messages related to the specified
     * topic ID.
     */
    Page<MessageResponseDto> findAllByTopicId(UUID topicId, Pageable pageable, Principal principal);

    /**
     * Count saved messages by topic id and sander email between current time and set timestamp.
     *
     * @param topicId   topic id.
     * @param sentFrom  sender email.
     * @param timestamp set timestamp.
     * @return number of counted messages.
     */
    int countMessagesBetweenTimestampByTopicId(UUID topicId, String sentFrom,
        LocalDateTime timestamp);

    List<LastMessageResponseDto> getLastMessages(TopicScope aPublic);

}
