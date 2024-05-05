package com.chat.yourway.service;

import com.chat.yourway.model.event.EventType;
import com.chat.yourway.service.interfaces.ChatNotificationService;
import com.chat.yourway.service.interfaces.ChatTypingEventService;
import com.chat.yourway.service.interfaces.ContactEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatTypingEventServiceImpl implements ChatTypingEventService {

  private final ContactEventService contactEventService;
  private final ChatNotificationService chatNotificationService;

  @Override
  public void getTypingEvent(Boolean isTyping, String email) {
    log.info("Start getTypingEvent isTyping={}, email={}", isTyping, email);
    contactEventService.updateTypingEvent(email, isTyping);

    Integer topicId = contactEventService.getAllByEmail(email).stream()
        .filter(e -> e.getEventType().equals(EventType.SUBSCRIBED))
        .findFirst()
        .orElseThrow()
        .getTopicId();

    chatNotificationService.updateNotificationForAllWhoSubscribedToTopic(topicId);
  }
}