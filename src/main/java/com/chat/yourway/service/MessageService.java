package com.chat.yourway.service;

import com.chat.yourway.dto.request.ReceivedMessage;
import com.chat.yourway.dto.response.SendMessage;
import com.chat.yourway.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * {@link MessageService}
 *
 * @author Dmytro Trotsenko on 7/22/23
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageMapper messageMapper;

    @Value("${socket.dest-prefixes}")
    private String[] destPrefixes;

    public SendMessage sendToTopic(ReceivedMessage receivedMessage, String username) {

        receivedMessage.setSentFrom(username);
        receivedMessage.setSendTo("topic");

        SendMessage sendMessage = messageMapper.toSendMessage(receivedMessage);

        sendMessage.setSentTime(LocalDateTime.now());
        log.info("{} sent message to topic", username);
        return sendMessage;
    }

    public SendMessage sendToUser(ReceivedMessage receivedMessage, String username) {

        String sendTo = receivedMessage.getSendTo();
        receivedMessage.setSentFrom(username);

        SendMessage sendMessage = messageMapper.toSendMessage(receivedMessage);
        sendMessage.setSentTime(LocalDateTime.now());

        //todo: Check username in DB
        simpMessagingTemplate.convertAndSendToUser(sendTo, destPrefixes[1], sendMessage);
        log.info("{} sent message to {}", username, sendTo);
        return sendMessage;
    }

}
