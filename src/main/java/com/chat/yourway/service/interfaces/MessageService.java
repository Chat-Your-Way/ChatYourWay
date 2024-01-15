package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.request.MessagePrivateRequestDto;
import com.chat.yourway.dto.request.MessagePublicRequestDto;
import com.chat.yourway.dto.request.PageRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.exception.MessageHasAlreadyReportedException;
import com.chat.yourway.exception.MessageNotFoundException;
import com.chat.yourway.exception.TopicNotFoundException;
import com.chat.yourway.exception.TopicSubscriberNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {

  /**
   * Creates a public message in the specified public topic. Validates the user's subscription to
   * the topic before sending the message.
   *
   * @param topicId The ID of the public topic where the message will be sent.
   * @param message The message request DTO containing the message content and recipient.
   * @param email   The email of the sender.
   * @return A {@link MessageResponseDto} representing the saved public message.
   * @throws TopicSubscriberNotFoundException If the contact is not subscribed to the public topic.
   * @throws TopicNotFoundException           If the topic with the specified ID does not exist.
   */
  MessageResponseDto createPublic(int topicId, MessagePublicRequestDto message, String email);

  /**
   * Creates a private message to the specified recipient. Generates a unique private topic name for
   * the conversation. Validates the recipient's subscription to the private topic before sending
   * the message.
   *
   * @param message The message request DTO containing the message content and recipient.
   * @param email   The email of the sender.
   * @return A {@link MessageResponseDto} representing the saved private message.
   * @throws TopicNotFoundException If the topic with the specified name does not exist.
   */
  MessageResponseDto createPrivate(MessagePrivateRequestDto message, String email);

  /**
   * Reports a message identified by its unique message ID, indicating a violation or inappropriate
   * content, associated with the provided user details.
   *
   * @param messageId The unique identifier of the message to be reported.
   * @param email     The email of the user reporting the message.
   * @throws MessageNotFoundException           If messageId is null or negative, or userDetails is
   *                                            null.
   * @throws MessageHasAlreadyReportedException If the user reporting the message does not have the
   *                                            necessary permissions.
   */
  void reportMessageById(Integer messageId, String email);

  /**
   * Retrieves a list of messages based on the given topic ID.
   *
   * @param topicId        The unique identifier of the topic.
   * @param pageRequestDto Set parameters for pagination
   * @return A list of {@link MessageResponseDto} containing messages related to the specified topic
   * ID.
   */
  List<MessageResponseDto> findAllByTopicId(Integer topicId, PageRequestDto pageRequestDto);

  /**
   * Count saved messages by topic id and sander email between current time and set timestamp.
   *
   * @param topicId   topic id.
   * @param sentFrom  sender email.
   * @param timestamp set timestamp.
   * @return number of counted messages.
   */
  int countMessagesBetweenTimestampByTopicId(Integer topicId, String sentFrom,
      LocalDateTime timestamp);
}
