package com.chat.yourway.listener;

import static com.chat.yourway.model.event.EventType.OFFLINE;
import static com.chat.yourway.model.event.EventType.ONLINE;

import com.chat.yourway.service.ChatNotificationService;
import com.chat.yourway.service.ContactEventService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompConnectionListener {

  private final ContactEventService contactEventService;
  private final ChatNotificationService chatNotificationService;

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    String email = getEmail(event);
    contactEventService.updateEventTypeByEmail(ONLINE, email);
    log.info("Contact [{}] is connected", getEmail(event));
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    String email = getEmail(event);
    contactEventService.updateEventTypeByEmail(OFFLINE, email);
    chatNotificationService.notifyAllWhoSubscribedToSameUserTopic(email);
    log.info("Contact [{}] is disconnected", email);
  }

  private String getEmail(AbstractSubProtocolEvent event) {
    return Objects.requireNonNull(event.getUser()).getName();
  }

}
