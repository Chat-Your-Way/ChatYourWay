package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.request.MessagePrivateRequestDto;
import com.chat.yourway.dto.request.MessagePublicRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;

public interface ChatMessageService {

  /**
   * Sends a message to the chat topic.
   *
   * @param topicId                  The id of the topic.
   * @param messagePrivateRequestDto The received message details.
   * @param email                    The email of the sender.
   * @return {@link MessageResponseDto} is the sent message.
   */
  MessageResponseDto sendToTopic(Integer topicId, MessagePublicRequestDto messagePrivateRequestDto,
      String email);

  /**
   * Sends a message to the specific contact.
   *
   * @param messagePrivateRequestDto The received message details.
   * @param email                    The email of the sender.
   * @return {@link MessageResponseDto} is the sent message.
   */
  MessageResponseDto sendToContact(MessagePrivateRequestDto messagePrivateRequestDto, String email);
}
