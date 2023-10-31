package com.chat.yourway.service;

import com.chat.yourway.repository.impl.ChatTopicSubscriberRepositoryImpl;
import com.chat.yourway.repository.impl.LastMessageRepositoryImpl;
import com.chat.yourway.repository.TopicSubscriberRepository;
import com.chat.yourway.service.interfaces.NotificationMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationMessageServiceImpl implements NotificationMessageService {
    public static final String SEND_NOTIFICATION_DESTINATION = "/notification/topic/{topic-id}/sub/{sub-id}";

    private final ChatTopicSubscriberRepositoryImpl chatTopicSubscriberRepository;
    private final LastMessageRepositoryImpl lastMessageRepository;
    private final TopicSubscriberRepository topicSubscriberRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void readMessage(Integer topicId, Integer messageId, String userEmail) {
        topicSubscriberRepository.setLastMessageByOneSubscriber(userEmail, messageId, topicId);
    }

    @Override
    public void readAllMessages(Integer topicId, String userEmail) {
        var lastMessageId = lastMessageRepository.getLastMessageIdByTopicId(topicId);

        topicSubscriberRepository.setLastMessageByOneSubscriber(userEmail, lastMessageId, topicId);
    }

    @Override
    public void sendNotification(Integer topicId) {
        var subsWhoInChat = chatTopicSubscriberRepository.getSubsWhoOnlineByTopicId(topicId);
        var subsWhoNotInChat = topicSubscriberRepository.findAllSubIdsWhoNotInSubEmails(subsWhoInChat, topicId);

        subsWhoNotInChat.stream()
                .map((subId) -> generateNotificationDestination(subId, topicId))
                .forEach((destination) -> simpMessagingTemplate.convertAndSend(destination, "+1 new message."));
    }

    @Override
    public void setLastMessageSubsByTopicId(Integer topicId) {
        var onlineSubs = chatTopicSubscriberRepository.getSubsWhoOnlineByTopicId(topicId);

        var lastMessageId = lastMessageRepository.getLastMessageIdByTopicId(topicId);
        topicSubscriberRepository.setLastMessageByManySubscribers(onlineSubs, lastMessageId, topicId);
    }

    private String generateNotificationDestination(Integer subId, Integer topicId) {
        return SEND_NOTIFICATION_DESTINATION
                .replace("{topic-id}", String.valueOf(topicId))
                .replace("{sub-id}", String.valueOf(subId));
    }
}
