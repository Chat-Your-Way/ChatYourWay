package com.chat.yourway.service;

import com.chat.yourway.dto.response.notification.LastMessageResponseDto;
import com.chat.yourway.model.TopicScope;
import com.chat.yourway.repository.jpa.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LastMessagesService {

    private final MessageRepository messageRepository;

    public List<LastMessageResponseDto> getLastMessages(List<UUID> topicIds, TopicScope scope) {
        if (topicIds == null) {
            return messageRepository.getLastMessages(scope);
        } else {
            return messageRepository.getLastMessagesByTopicIds(scope, topicIds);
        }
    }
}
