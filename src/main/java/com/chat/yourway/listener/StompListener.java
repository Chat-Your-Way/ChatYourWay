package com.chat.yourway.listener;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;

import java.util.Objects;

public abstract class StompListener {
    protected String getUserEmail(AbstractSubProtocolEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

        return Objects.requireNonNull(Objects.requireNonNull(headerAccessor.getUser()).getName());
    }
}
