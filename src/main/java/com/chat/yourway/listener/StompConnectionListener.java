package com.chat.yourway.listener;

import com.chat.yourway.repository.impl.OnlineContactRepositoryImpl;
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
public class StompConnectionListener extends StompListener {
  private static final boolean ONLINE = true;

  private final OnlineContactRepositoryImpl onlineContactRepository;

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    log.info("Try to connect session");
    updateContactConnection(event, ONLINE);
    log.info("Session was connected");
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    log.info("Try to disconnect session");
    updateContactConnection(event, !ONLINE);
    log.info("Session was disconnected");
  }

  private void updateContactConnection(AbstractSubProtocolEvent event, boolean isOnline) {
    var userEmail = getUserEmail(event);

    if (isOnline) {
      log.info("Try to save user [{}]", userEmail);
      onlineContactRepository.save(userEmail);
      log.info("User [{}] saved successfully", userEmail);
    } else {
      log.info("Try to delete user [{}]", userEmail);
      onlineContactRepository.delete(userEmail);
      log.info("User [{}] deleted successfully", userEmail);
    }
  }
}
