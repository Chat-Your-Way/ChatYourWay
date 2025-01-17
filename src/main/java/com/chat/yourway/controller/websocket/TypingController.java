package com.chat.yourway.controller.websocket;

import com.chat.yourway.service.ChatTypingEventService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class TypingController {

  private final ChatTypingEventService chatTypingEventService;

  @MessageMapping("/typing/{isTyping}")
  public void updateTypingEvent(@DestinationVariable boolean isTyping, Principal principal) {
    chatTypingEventService.updateTypingEvent(isTyping, principal.getName());
  }
}
