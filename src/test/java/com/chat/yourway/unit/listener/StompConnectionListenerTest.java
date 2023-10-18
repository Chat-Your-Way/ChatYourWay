package com.chat.yourway.unit.listener;

import com.chat.yourway.listener.StompConnectionListener;
import com.chat.yourway.repository.OnlineContactRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StompConnectionListenerTest {
  @Mock private OnlineContactRepository onlineContactRepository;
  @InjectMocks private StompConnectionListener stompConnectionListener;

  @Test
  public void testHandleWebSocketConnectListener() {
    // Given
    SessionConnectEvent event = createConnectEvent();

    // When
    stompConnectionListener.handleWebSocketConnectListener(event);

    // Then
    verify(onlineContactRepository).save(anyString());
  }

  @Test
  public void testHandleWebSocketDisconnectListener() {
    // Given
    SessionDisconnectEvent event = createDisconnectEvent();

    // When
    stompConnectionListener.handleWebSocketDisconnectListener(event);

    // Then
    verify(onlineContactRepository).delete(anyString());
  }

  private SessionConnectEvent createConnectEvent() {
    Principal principal = new UsernamePasswordAuthenticationToken("username", "password");
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);

    accessor.setUser(principal);

    return new SessionConnectEvent(
        this, new GenericMessage<>(new byte[0], accessor.getMessageHeaders()), principal);
  }

  private SessionDisconnectEvent createDisconnectEvent() {
    Principal principal = new UsernamePasswordAuthenticationToken("username", "password");
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
