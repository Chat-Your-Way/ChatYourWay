package com.chat.yourway.listener;

import com.chat.yourway.config.websocket.WebSocketProperties;
import com.chat.yourway.service.ContactOnlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompSubscriptionListener {

    private final WebSocketProperties webSocketProperties;
    private final ContactOnlineService contactOnlineService;

    private static final String SLASH = "/";
    private static final String UUID_REGEX_PATTERN = "\\b[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}\\b";

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        String destination = getDestination(event);
        String email = getEmail(event);

        if (isTopicDestination(destination)) {
            UUID topicId = getTopicId(event);
            contactOnlineService.setUserOnline(email, topicId);
            log.info("Contact [{}] open topic [{}]", email, destination);
        }
    }

    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        String destination = getDestination(event);
        String email = getEmail(event);

        if (isTopicDestination(destination)) {
            UUID topicId = getTopicId(event);
            contactOnlineService.setUserOnline(email);
            log.info("Contact [{}] unsubscribe from [{}]", email, destination);
        }
    }

    private String getEmail(AbstractSubProtocolEvent event) {
        return Objects.requireNonNull(event.getUser()).getName();
    }

    private String getDestination(AbstractSubProtocolEvent event) {
        return SimpMessageHeaderAccessor.wrap(event.getMessage())
                .getDestination();
    }

    private UUID getTopicId(AbstractSubProtocolEvent event) {
        String destination = getDestination(event);
        String topicId = "";

        Pattern pattern = Pattern.compile(UUID_REGEX_PATTERN);
        Matcher matcher = pattern.matcher(destination);

        while (matcher.find()) {
            topicId = matcher.group();
        }

        if (topicId.isEmpty()) {
            return null;
        }

        return UUID.fromString(topicId);
    }

    private String getTopicDestination() {
        return webSocketProperties.getTopicPrefix() + SLASH;
    }


    private boolean isTopicDestination(String destination) {
        return destination.startsWith(getTopicDestination());
    }
}