package com.chat.yourway.unit.listener;

import static com.chat.yourway.model.event.EventType.SUBSCRIBED;
import static com.chat.yourway.model.event.EventType.UNSUBSCRIBED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.messaging.simp.stomp.StompCommand.SUBSCRIBE;
import static org.springframework.messaging.simp.stomp.StompCommand.UNSUBSCRIBE;

import com.chat.yourway.config.websocket.WebsocketProperties;
import com.chat.yourway.dto.response.LastMessageResponseDto;
import com.chat.yourway.listener.StompSubscriptionListener;
import com.chat.yourway.model.event.ContactEvent;
import com.chat.yourway.service.interfaces.ChatNotificationService;
import com.chat.yourway.service.interfaces.ContactEventService;
import java.security.Principal;
import java.time.LocalDateTime;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@ExtendWith(MockitoExtension.class)
public class StompSubscriptionListenerTest {

  @Spy
  private WebsocketProperties properties;

  @Mock
  private ContactEventService contactEventService;

  @Mock
  private ChatNotificationService chatNotificationService;

  @InjectMocks
  private StompSubscriptionListener stompSubscriptionListener;

  @Captor
  private ArgumentCaptor<ContactEvent> contactEventCaptor;

  private static final String TOPIC_PREFIX = "/topic";

  @BeforeEach
  void init() {
    properties.setTopicPrefix(TOPIC_PREFIX);
  }

  @Test
  public void handleWebSocketSubscribeListener_shouldSaveEvent() {
    // Given
    String email = "anton@gmail.com";
    String password = "Password-123";
    int topicId = 1;
    String destination = "/topic/" + topicId;

    var lastMessageDto = new LastMessageResponseDto();
    lastMessageDto.setTimestamp(LocalDateTime.now());
    lastMessageDto.setSentFrom("vasil@gmail.com");
    lastMessageDto.setLastMessage("Hello");

    ContactEvent contactEvent = new ContactEvent();
    contactEvent.setLastMessage(lastMessageDto);

    var event = createSubscribeEvent(destination, getPrincipal(email, password));

    // When
    when(contactEventService.getByTopicIdAndEmail(anyInt(), anyString())).thenReturn(contactEvent);
    stompSubscriptionListener.handleWebSocketSubscribeListener(event);

    // Then
    verify(contactEventService, times(1)).save(contactEventCaptor.capture());
    ContactEvent capturedEvent = contactEventCaptor.getValue();
    assertThat(capturedEvent).isNotNull().isInstanceOf(ContactEvent.class);
    assertThat(capturedEvent.getId()).isEqualTo(email + "_" + topicId);
    assertThat(capturedEvent.getTopicId()).isEqualTo(topicId);
    assertThat(capturedEvent.getEmail()).isEqualTo(email);
    assertThat(capturedEvent.getTimestamp()).isInstanceOfAny(LocalDateTime.class);
    assertThat(capturedEvent.getEventType()).isEqualTo(SUBSCRIBED);
    assertThat(capturedEvent.getLastMessage()).isEqualTo(lastMessageDto);
  }

  @Test
  public void handleWebSocketSubscribeListener_shouldNotifyTopicSubscribers() {
    // Given
    String email = "anton@gmail.com";
    String password = "Password-123";
    int topicId = 1;
    String destination = "/user/topic/" + topicId;
    var event = createSubscribeEvent(destination, getPrincipal(email, password));

    // When
    stompSubscriptionListener.handleWebSocketSubscribeListener(event);

    // Then
    verify(chatNotificationService, times(1)).notifyTopicSubscribers(topicId);
  }

  @Test
  public void handleWebSocketUnsubscribeListener_shouldSaveEvent() {
    // Given
    String email = "anton@gmail.com";
    String password = "Password-123";
    int topicId = 1;
    String destination = "/topic/" + topicId;

    var event = createUnsubscribeEvent(destination, getPrincipal(email, password));

    // When
    stompSubscriptionListener.handleWebSocketUnsubscribeListener(event);

    // Then
    verify(contactEventService, times(1)).save(contactEventCaptor.capture());
    ContactEvent capturedEvent = contactEventCaptor.getValue();
    assertThat(capturedEvent).isNotNull().isInstanceOf(ContactEvent.class);
    assertThat(capturedEvent.getId()).isEqualTo(email + "_" + topicId);
    assertThat(capturedEvent.getTopicId()).isEqualTo(topicId);
    assertThat(capturedEvent.getEmail()).isEqualTo(email);
    assertThat(capturedEvent.getTimestamp()).isInstanceOfAny(LocalDateTime.class);
    assertThat(capturedEvent.getEventType()).isEqualTo(UNSUBSCRIBED);
  }

  @Test
  public void handleWebSocketUnsubscribeListener_shouldNotifyTopicSubscribers() {
    // Given
    String email = "anton@gmail.com";
    String password = "Password-123";
    int topicId = 1;
    String destination = "/topic/" + topicId;

    var event = createUnsubscribeEvent(destination, getPrincipal(email, password));

    // When
    stompSubscriptionListener.handleWebSocketUnsubscribeListener(event);

    // Then
    verify(chatNotificationService, times(1)).notifyTopicSubscribers(topicId);
  }

  @Test
  public void shouldDoesNotTrowExceptions() {
    // Given
    String email = "anton@gmail.com";
    String password = "Password-123";
    String destination = "/topic/";

    var subscribeEvent = createSubscribeEvent(destination, getPrincipal(email, password));
    var unsubscribeEvent = createUnsubscribeEvent(destination, getPrincipal(email, password));

    // When and Then
    assertDoesNotThrow(() ->
        stompSubscriptionListener.handleWebSocketSubscribeListener(subscribeEvent));
    assertDoesNotThrow(() ->
        stompSubscriptionListener.handleWebSocketUnsubscribeListener(unsubscribeEvent));
  }

  private SessionSubscribeEvent createSubscribeEvent(String destination, Principal principal) {
    StompHeaderAccessor accessor = StompHeaderAccessor.create(SUBSCRIBE);

    accessor.setUser(principal);
    accessor.setDestination(destination);

    return new SessionSubscribeEvent(this, getGenericMessage(accessor), principal);
  }

  private SessionUnsubscribeEvent createUnsubscribeEvent(String destination, Principal principal) {
    StompHeaderAccessor accessor = StompHeaderAccessor.create(UNSUBSCRIBE);

    accessor.setUser(principal);
    accessor.setDestination(destination);

    return new SessionUnsubscribeEvent(this, getGenericMessage(accessor), principal);
  }

  @NotNull
  private static GenericMessage<byte[]> getGenericMessage(StompHeaderAccessor accessor) {
    return new GenericMessage<>(new byte[0], accessor.getMessageHeaders());
  }

  private Principal getPrincipal(String email, String password) {
    return new UsernamePasswordAuthenticationToken(email, password);
  }

}
