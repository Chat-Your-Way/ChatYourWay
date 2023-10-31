package com.chat.yourway.listener;

import com.chat.yourway.repository.impl.ChatTopicSubscriberRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompSubscriptionListener extends StompListener {
  private final ChatTopicSubscriberRepositoryImpl chatTopicSubscriberRepository;

  @EventListener
  public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
    String userEmail = getUserEmail(event);
    Integer topicId = getTopicId(event);
    log.info("User [{}] subscribe to topic {}", userEmail, topicId);

    chatTopicSubscriberRepository.addSubEmailToTopic(userEmail, topicId);
  }

  @EventListener
  public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
    String userEmail = getUserEmail(event);
    Integer topicId = getTopicId(event);
    log.info("User [{}] unsubscribe from topic {}", userEmail, topicId);

    chatTopicSubscriberRepository.deleteSubEmailToTopic(userEmail, topicId);
  }

  private Integer getTopicId(AbstractSubProtocolEvent event) {
    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

    return Integer.valueOf(Objects.requireNonNull(headerAccessor.getDestination()));
  }
}
