package com.chat.yourway.controller.websocket;


import com.chat.yourway.dto.request.MessageRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/app/topic/public/{topicId}")
    public void sendMessage(@DestinationVariable UUID topicId, MessageRequestDto message) {

        log.info("Received message for topic ID: {}", topicId);

        MessageResponseDto savedMessage = messageService.sendToTopic(topicId, message);
        log.info("Message was saved in DB");

        messagingTemplate.convertAndSend("/topic/public/" + topicId, message);
        log.info("Message sent to topic ID: {}", topicId) ;

    }
}
