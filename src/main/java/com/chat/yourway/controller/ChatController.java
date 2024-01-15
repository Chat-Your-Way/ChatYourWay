package com.chat.yourway.controller;

import com.chat.yourway.dto.request.MessagePrivateRequestDto;
import com.chat.yourway.dto.request.MessagePublicRequestDto;
import com.chat.yourway.dto.request.PageRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.service.interfaces.ChatMessageService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

  private final ChatMessageService chatMessageService;

  @MessageMapping("/topic/public/{topicId}")
  public MessageResponseDto sendToPublicTopic(@DestinationVariable Integer topicId,
      @Valid @Payload MessagePublicRequestDto message, Principal principal) {
    String email = principal.getName();
    return chatMessageService.sendToPublicTopic(topicId, message, email);
  }

  @MessageMapping("/topic/private/{topicId}")
  public MessageResponseDto sendToPrivateTopic(@DestinationVariable Integer topicId,
      @Valid @Payload MessagePrivateRequestDto message, Principal principal) {
    String email = principal.getName();
    return chatMessageService.sendToPrivateTopic(topicId, message, email);
  }

  @MessageMapping("/history/topic/{topicId}")
  public List<MessageResponseDto> getTopicHistory(@DestinationVariable Integer topicId,
      @Valid @Payload PageRequestDto pageRequestDto, Principal principal) {
    String email = principal.getName();
    return chatMessageService.sendMessageHistoryByTopicId(topicId, pageRequestDto, email);
  }

}
