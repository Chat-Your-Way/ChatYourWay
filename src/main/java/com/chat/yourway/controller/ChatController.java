package com.chat.yourway.controller;

import com.chat.yourway.dto.request.ReceivedMessage;
import com.chat.yourway.dto.response.SendMessage;
import com.chat.yourway.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * {@link ChatController}
 *
 * @author Dmytro Trotsenko on 7/21/23
 */

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;

    @MessageMapping("/application")
    @SendTo("/topic")
    public SendMessage sendToTopic(ReceivedMessage message, Principal principal) {
        String username = principal.getName();
        return messageService.sendToTopic(message, username);
    }

    @MessageMapping("/private")
    public SendMessage sendToUser(@Payload ReceivedMessage message, Principal principal) {
        String username = principal.getName();
        return messageService.sendToUser(message, username);
    }

}
