package com.chat.yourway.listener;

import static com.chat.yourway.model.event.EventType.SUBSCRIBED;
import static com.chat.yourway.model.event.EventType.UNSUBSCRIBED;

import com.chat.yourway.config.websocket.WebsocketProperties;
import com.chat.yourway.model.event.ContactEvent;
import com.chat.yourway.service.interfaces.ChatMessageService;
import com.chat.yourway.service.interfaces.ChatNotificationService;
import com.chat.yourway.service.interfaces.ContactEventService;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompSubscriptionListener {

  private final WebsocketProperties properties;
  private final ContactEventService contactEventService;
  private final ChatMessageService chatMessageService;
  private final ChatNotificationService chatNotificationService;

  private static String lastMessage;

  @EventListener
  public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
    String destination = getDestination(event);
    String email = getEmail(event);

    try {
      if (destination.startsWith("/user" + getTopicDestination())) {
        chatMessageService.sendMessageHistoryByTopicId(getTopicId(event), email);
      }
      if (destination.startsWith(getTopicDestination())) {
        lastMessage = contactEventService.getByTopicIdAndEmail(getTopicId(event), email)
            .getLastMessage();
        var contactEvent = new ContactEvent(email, getTopicId(event), SUBSCRIBED,
            getTimestamp(event), lastMessage);
        contactEventService.save(contactEvent);
      }
      chatNotificationService.notifyTopicSubscribers(getTopicId(event));
    } catch (NumberFormatException e) {
      log.warn("Contact [{}] subscribe to destination [{}] without topic id", email, destination);
    }

    log.info("Contact [{}] subscribe to [{}]", email, destination);
  }

  @EventListener
  public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
    String destination = getDestination(event);
    String email = getEmail(event);

    try {
      if (destination.startsWith(getTopicDestination())) {
        var contactEvent = new ContactEvent(email, getTopicId(event), UNSUBSCRIBED,
            getTimestamp(event), lastMessage);
        contactEventService.save(contactEvent);
      }
      chatNotificationService.notifyTopicSubscribers(getTopicId(event));
    } catch (NumberFormatException e) {
      log.warn("Contact [{}] unsubscribe from destination [{}] without topic id", email,
          destination);
    }
    log.info("Contact [{}] unsubscribe from [{}]", email, destination);
  }

  private String getEmail(AbstractSubProtocolEvent event) {
    return Objects.requireNonNull(event.getUser()).getName();
  }

  private String getDestination(AbstractSubProtocolEvent event) {
    return SimpMessageHeaderAccessor.wrap(event.getMessage())
        .getDestination();
  }

  private LocalDateTime getTimestamp(AbstractSubProtocolEvent event) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getTimestamp()),
        TimeZone.getDefault().toZoneId());
  }

  private Integer getTopicId(AbstractSubProtocolEvent event) throws NumberFormatException {
    String destination = getDestination(event);

    if (destination.startsWith(getNotifyDestination())) {
      return Integer.valueOf(destination.substring(getNotifyDestination().length()));
    } else if (destination.startsWith("/user" + getTopicDestination())) {
      return Integer.valueOf(destination.substring(("/user" + getTopicDestination()).length()));
    }
    return Integer.valueOf(destination.substring(getTopicDestination().length()));
  }

  private String getTopicDestination() {
    return properties.getTopicPrefix() + "/";
  }

  private String getNotifyDestination() {
    return "/user" + properties.getNotifyPrefix() + "/";
  }

}