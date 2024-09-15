package com.chat.yourway.controller.websocket;


import com.chat.yourway.dto.response.MessageResponseDto;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/topic/public/{topicId}")
    public void sendMessage(@DestinationVariable String topicId, MessageResponseDto message) {

        messagingTemplate.convertAndSend("/topic/public/" + topicId, message);
    }
}
