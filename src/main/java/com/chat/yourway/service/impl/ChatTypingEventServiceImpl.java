package com.chat.yourway.service.impl;

import com.chat.yourway.model.event.EventType;
import com.chat.yourway.service.ChatNotificationService;
import com.chat.yourway.service.ChatTypingEventService;
import com.chat.yourway.service.ContactEventService;
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
  public void updateTypingEvent(Boolean isTyping, String email) {
    log.info("Start updateTypingEvent isTyping={}, email={}", isTyping, email);
    contactEventService.updateTypingEvent(email, isTyping);

    Integer topicId = contactEventService.getAllByEmail(email).stream()
        .filter(e -> e.getEventType().equals(EventType.SUBSCRIBED))
        .findFirst()
        .orElseThrow()
        .getTopicId();

    chatNotificationService.updateNotificationForAllWhoSubscribedToTopic(topicId);
  }
}
