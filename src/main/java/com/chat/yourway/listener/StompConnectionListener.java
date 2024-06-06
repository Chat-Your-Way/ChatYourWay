package com.chat.yourway.listener;

import com.chat.yourway.service.impl.ContactOnlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompConnectionListener {

    private final ContactOnlineService contactOnlineService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        String email = getUserEmailFromEvent(event);
        contactOnlineService.setUserOnline(email);
        log.info("Contact [{}] is connected", email);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String email = getUserEmailFromEvent(event);
        contactOnlineService.setUserOffline(email);
        log.info("Contact [{}] is disconnected", email);
    }

    private String getUserEmailFromEvent(AbstractSubProtocolEvent event) {
        return Objects.requireNonNull(event.getUser()).getName();
    }

}
