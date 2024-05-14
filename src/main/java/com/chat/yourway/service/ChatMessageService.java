package com.chat.yourway.service;

import com.chat.yourway.dto.request.MessagePrivateRequestDto;
import com.chat.yourway.dto.request.MessagePublicRequestDto;
import com.chat.yourway.dto.request.PageRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import java.util.List;

public interface ChatMessageService {

  /**
   * Sends a message to the public topic.
   *
   * @param topicId                 The id of the topic.
   * @param messagePublicRequestDto The received message details.
   * @param email                   The email of the sender.
   * @return {@link MessageResponseDto} is the sent message.
   */
  MessageResponseDto sendToPublicTopic(Integer topicId,
      MessagePublicRequestDto messagePublicRequestDto,
      String email);

  /**
   * Sends a message to the private topic.
   *
   * @param topicId                  The id of the topic.
   * @param messagePrivateRequestDto The received message details.
   * @param email                    The email of the sender.
   * @return {@link MessageResponseDto} is the sent message.
   */
  MessageResponseDto sendToPrivateTopic(Integer topicId,
      MessagePrivateRequestDto messagePrivateRequestDto, String email);

  /**
   * Send messageHistory by topic id.
   *
   * @param topicId        The id of the topic.
   * @param pageRequestDto Set parameters for pagination.
   * @param email          The email of the sender.
   * @return list topic messages.
   */
  List<MessageResponseDto> sendMessageHistoryByTopicId(Integer topicId,
      PageRequestDto pageRequestDto, String email);

}
