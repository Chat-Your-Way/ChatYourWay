package com.chat.yourway.service;

import com.chat.yourway.dto.request.MessagePrivateRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import java.util.UUID;

public interface ChatMessageService {


    /**
     * Sends a message to the private topic.
     *
     * @param topicId                  The id of the topic.
     * @param messagePrivateRequestDto The received message details.
     * @param email                    The email of the sender.
     * @return {@link MessageResponseDto} is the sent message.
     */
    MessageResponseDto sendToPrivateTopic(UUID topicId,
        MessagePrivateRequestDto messagePrivateRequestDto, String email);
}
