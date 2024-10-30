package com.chat.yourway.listener;

import com.chat.yourway.service.ContactOnlineService;
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
        contactOnlineService.setUserOnline(getUserEmailFromEvent(event));
        log.info("Contact [{}] is connected", getUserEmailFromEvent(event));
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        contactOnlineService.setUserOffline(getUserEmailFromEvent(event));
        log.info("Contact [{}] is disconnected", getUserEmailFromEvent(event));
    }

    private String getUserEmailFromEvent(AbstractSubProtocolEvent event) {
        return Objects.requireNonNull(event.getUser()).getName();
    }

}
