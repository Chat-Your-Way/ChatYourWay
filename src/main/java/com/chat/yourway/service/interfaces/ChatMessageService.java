package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.request.ReceivedMessageDto;
import com.chat.yourway.dto.response.MessageResponseDto;

public interface ChatMessageService {

  MessageResponseDto sendToTopic(ReceivedMessageDto receivedMessageDto, String username);

  MessageResponseDto sendToUser(ReceivedMessageDto receivedMessageDto, String username);
}
