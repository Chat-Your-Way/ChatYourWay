package com.chat.yourway.listener;

import static com.chat.yourway.model.event.EventType.ONLINE;
import static com.chat.yourway.model.event.EventType.SUBSCRIBED;

import com.chat.yourway.config.websocket.WebsocketProperties;
import com.chat.yourway.dto.response.notification.LastMessageResponseDto;
import com.chat.yourway.model.event.ContactEvent;
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
  private final ChatNotificationService chatNotificationService;

  private static LastMessageResponseDto lastMessageDto;
  private static final String USER_DESTINATION = "/user";
  private static final String TOPICS_DESTINATION = "/topics";
  private static final String SLASH = "/";

  @EventListener
  public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
    String destination = getDestination(event);
    String email = getEmail(event);

    try {
      if (isTopicDestination(destination)) {
        lastMessageDto = contactEventService.getByTopicIdAndEmail(getTopicId(event), email)
            .getLastMessage();
        int unreadMessages = contactEventService.getByTopicIdAndEmail(getTopicId(event), email)
            .getUnreadMessages();

        var contactEvent = new ContactEvent(email, getTopicId(event), SUBSCRIBED,
            getTimestamp(event), unreadMessages, lastMessageDto);
        contactEventService.updateEventTypeByEmail(ONLINE, email);
        contactEventService.save(contactEvent);
      }

      chatNotificationService.notifyAllWhoSubscribedToSameUserTopic(email);
      chatNotificationService.notifyAllWhoSubscribedToTopic(getTopicId(event));

    } catch (NumberFormatException e) {
      log.warn("Contact [{}] subscribe to destination [{}] without topic id", email, destination);
    }

    if (destination.equals(USER_DESTINATION + properties.getNotifyPrefix() + TOPICS_DESTINATION)) {
      chatNotificationService.notifyAllTopics(getEmail(event));
    }

    log.info("Contact [{}] subscribe to [{}]", email, destination);
  }

  @EventListener
  public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
    String destination = getDestination(event);
    String email = getEmail(event);

    try {
      if (isTopicDestination(destination)) {
        var contactEvent = new ContactEvent(email, getTopicId(event), ONLINE,
            getTimestamp(event), 0, lastMessageDto);
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

    if (isNotificationDestination(destination)) {
      return Integer.valueOf(destination.substring(getNotifyDestination().length()));
    } else if (isPrivateTopicDestination(destination)) {
      return Integer.valueOf(destination.substring(getPrivateTopicDestination().length()));
    }
    return Integer.valueOf(destination.substring(getTopicDestination().length()));
  }

  private String getTopicDestination() {
    return properties.getTopicPrefix() + SLASH;
  }

  private String getNotifyDestination() {
    return USER_DESTINATION + properties.getNotifyPrefix() + SLASH;
  }

  private String getPrivateTopicDestination() {
    return USER_DESTINATION + getTopicDestination();
  }

  private boolean isTopicDestination(String destination) {
    return destination.startsWith(getTopicDestination());
  }

  private boolean isPrivateTopicDestination(String destination) {
    return destination.startsWith(getPrivateTopicDestination());
  }

  private boolean isNotificationDestination(String destination) {
    return destination.startsWith(getNotifyDestination());
  }

}