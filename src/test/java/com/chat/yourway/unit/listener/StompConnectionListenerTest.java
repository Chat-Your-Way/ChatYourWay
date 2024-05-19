package com.chat.yourway.unit.listener;

import static com.chat.yourway.model.event.EventType.OFFLINE;
import static com.chat.yourway.model.event.EventType.ONLINE;
import static org.mockito.Mockito.verify;

import com.chat.yourway.listener.StompConnectionListener;
import com.chat.yourway.service.interfaces.ChatNotificationService;
import com.chat.yourway.service.interfaces.ChatTypingEventService;
import com.chat.yourway.service.interfaces.ContactEventService;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@ExtendWith(MockitoExtension.class)
public class StompConnectionListenerTest {
  @Mock private ContactEventService contactEventService;
  @Mock private ChatNotificationService chatNotificationService;
  @Mock private ChatTypingEventService chatTypingEventService;
  @InjectMocks private StompConnectionListener stompConnectionListener;

  @Test
  public void testHandleWebSocketConnectListener() {
    // Given
    String email = "anton@gmail.com";
    String password = "Password-123";
    SessionConnectEvent event = createConnectEvent(email, password);

    // When
    stompConnectionListener.handleWebSocketConnectListener(event);

    // Then
    verify(contactEventService).updateEventTypeByEmail(ONLINE, email);
  }

  @Test
  public void testHandleWebSocketDisconnectListener() {
    // Given
    String email = "anton@gmail.com";
    String password = "Password-123";
    SessionDisconnectEvent event = createDisconnectEvent(email, password);

    // When
    stompConnectionListener.handleWebSocketDisconnectListener(event);

    // Then
    verify(contactEventService).updateEventTypeByEmail(OFFLINE, email);
    verify(chatNotificationService).notifyAllWhoSubscribedToSameUserTopic(email);
    verify(chatTypingEventService).updateTypingEvent(false, email);
  }

  private SessionConnectEvent createConnectEvent(String email, String password) {
    Principal principal = new UsernamePasswordAuthenticationToken(email, password);
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);

    accessor.setUser(principal);

    return new SessionConnectEvent(
        this, new GenericMessage<>(new byte[0], accessor.getMessageHeaders()), principal);
  }

  private SessionDisconnectEvent createDisconnectEvent(String email, String password) {
    Principal principal = new UsernamePasswordAuthenticationToken(email, password);
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);

    accessor.setUser(principal);

    return new SessionDisconnectEvent(
        this,
        new GenericMessage<>(new byte[0], accessor.getMessageHeaders()),
        "sessionId",
        CloseStatus.SESSION_NOT_RELIABLE,
        principal);
  }
}
