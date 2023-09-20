package com.chat.yourway.controller;

import com.chat.yourway.dto.request.ReceivedMessageDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.service.interfaces.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

  private final ChatMessageService chatMessageService;

  @MessageMapping("/application")
  @SendTo("/topic")
  public MessageResponseDto sendToTopic(ReceivedMessageDto message, Principal principal) {
    String username = principal.getName();
    return chatMessageService.sendToTopic(message, username);
  }

  @MessageMapping("/private")
  public MessageResponseDto sendToUser(@Payload ReceivedMessageDto message, Principal principal) {
    String username = principal.getName();
    return chatMessageService.sendToUser(message, username);
  }
}
