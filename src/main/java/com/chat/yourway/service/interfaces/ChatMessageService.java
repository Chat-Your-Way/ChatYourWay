package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.request.ReceivedMessageDto;
import com.chat.yourway.dto.response.MessageResponseDto;

public interface ChatMessageService {

  /**
   * Sends a message to the chat topic.
   *
   * @param receivedMessageDto The received message details.
   * @param username           The username of the sender.
   * @return {@link MessageResponseDto} is the sent message.
   */
  MessageResponseDto sendToTopic(ReceivedMessageDto receivedMessageDto, String username);

  /**
   * Sends a message to the specific user.
   *
   * @param receivedMessageDto The received message details.
   * @param username           The username of the sender.
   * @return {@link MessageResponseDto} is the sent message.
   */
  MessageResponseDto sendToUser(ReceivedMessageDto receivedMessageDto, String username);
}
