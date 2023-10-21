package com.chat.yourway.controller;

import com.chat.yourway.dto.request.MessagePrivateRequestDto;
import com.chat.yourway.dto.request.MessagePublicRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.service.interfaces.ChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

  private final ChatMessageService chatMessageService;

  @MessageMapping("/topic/{topicId}")
  public MessageResponseDto sendToTopic(@DestinationVariable Integer topicId,
      @Valid @Payload MessagePublicRequestDto message, Principal principal) {
    String email = principal.getName();
    return chatMessageService.sendToTopic(topicId, message, email);
  }

  @MessageMapping("/private")
  public MessageResponseDto sendToContact(@Valid @Payload MessagePrivateRequestDto message,
      Principal principal) {
    String email = principal.getName();
    return chatMessageService.sendToContact(message, email);
  }
}
