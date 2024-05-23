package com.chat.yourway.controller.websocket;

import com.chat.yourway.dto.request.MessagePrivateRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.service.ChatMessageService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/topic/private/{topicId}")
    public MessageResponseDto sendToPrivateTopic(@DestinationVariable UUID topicId,
        @Valid @Payload MessagePrivateRequestDto message, Principal principal) {
        String email = principal.getName();
        return chatMessageService.sendToPrivateTopic(topicId, message, email);
    }
}
