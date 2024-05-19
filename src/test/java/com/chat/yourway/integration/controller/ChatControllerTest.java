package com.chat.yourway.integration.controller;

import static com.chat.yourway.model.Role.USER;
import static com.chat.yourway.model.event.EventType.ONLINE;
import static com.chat.yourway.model.event.EventType.SUBSCRIBED;
import static com.chat.yourway.model.token.TokenType.BEARER;
import static com.github.springtestdbunit.annotation.DatabaseOperation.CLEAN_INSERT;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import com.chat.yourway.dto.request.MessagePrivateRequestDto;
import com.chat.yourway.dto.request.MessagePublicRequestDto;
import com.chat.yourway.dto.request.PageRequestDto;
import com.chat.yourway.dto.response.notification.LastMessageResponseDto;
import com.chat.yourway.dto.response.notification.MessageNotificationResponseDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.dto.response.notification.TypingEventResponseDto;
import com.chat.yourway.integration.controller.websocketclient.TestStompFrameHandler;
import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.chat.yourway.model.Contact;
import com.chat.yourway.model.event.ContactEvent;
import com.chat.yourway.model.token.Token;
import com.chat.yourway.repository.ContactEventRedisRepository;
import com.chat.yourway.repository.TokenRedisRepository;
import com.chat.yourway.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Slf4j
@ExtendWith({PostgresExtension.class, RedisExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DbUnitTestExecutionListener.class,
    MockitoTestExecutionListener.class,
    ResetMocksTestExecutionListener.class
})
@DatabaseSetup(value = {
    "/dataset/mockdb/topic.xml",
    "/dataset/mockdb/contact.xml",
    "/dataset/mockdb/topic_subscriber.xml",
    "/dataset/mockdb/message.xml"
}, type = CLEAN_INSERT)
public class ChatControllerTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private TokenRedisRepository tokenRedisRepository;

  @Autowired
  private ContactEventRedisRepository contactEventRedisRepository;

  @Autowired
  private JwtService jwtService;

  @LocalServerPort
  private int port;
  private StompSession session;

  @BeforeEach
  void setUp() {
    String accessToken = getAccessToken();

    String URL = "ws://localhost:" + port + "/chat?Authorization=" + "Bearer " + accessToken;

    WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    session = createWebSocketSession(URL, headers);
  }

  @AfterEach
  public void cleanup() {
    session.disconnect();
  }


  @Test
  @SneakyThrows
  @DisplayName("sendToTopic should return correct send to public topic and received message from topic")
  void sendToTopic_shouldReturnCorrectSendToPublicTopicAndReceivedMessageFromTopic() {
    // Given
    int topicId = 13;
    MessagePublicRequestDto messageRequestDto = new MessagePublicRequestDto("Hello");
    //Stored subscription results for testing
    CompletableFuture<MessageResponseDto> resultKeeper = new CompletableFuture<>();

    // Subscribe to topic
    session.subscribe("/topic/" + topicId,
        new TestStompFrameHandler<>(resultKeeper, objectMapper, MessageResponseDto.class));

    // Send message to topic
    byte[] messageBytes = objectMapper.writeValueAsBytes(messageRequestDto);
    session.send("/app/topic/public/" + topicId, messageBytes);

    // Then
    MessageResponseDto messageResponseDto = resultKeeper.get(3, SECONDS);
    assertThat(messageResponseDto).isNotNull();
    assertThat(messageResponseDto.getId()).isNotNull();
    assertThat(messageResponseDto.getSentFrom()).isEqualTo("Vasil");
    assertThat(messageResponseDto.getSendTo()).isEqualTo("Topic id=" + topicId);
    assertThat(messageResponseDto.getTimestamp()).isNotNull();
    assertThat(messageResponseDto.getContent()).isEqualTo(messageRequestDto.getContent());
  }

  @Test
  @SneakyThrows
  @DisplayName("sendToContact should return correct received private message from self to self")
  void sendToContact_shouldReturnCorrectReceivedPrivateMessageFromSelfToSelf() {
    // Given
    int topicId = 14;
    MessagePrivateRequestDto messageRequestDto = new MessagePrivateRequestDto();
    messageRequestDto.setSendTo("vasil@gmail.com");
    messageRequestDto.setContent("Hi Vasil!");
    //Stored subscription results for testing
    CompletableFuture<MessageResponseDto> resultKeeper = new CompletableFuture<>();

    // Subscribe to private contact
    session.subscribe("/topic/" + topicId,
        new TestStompFrameHandler<>(resultKeeper, objectMapper, MessageResponseDto.class));

    // Send private message to contact
    byte[] messageBytes = objectMapper.writeValueAsBytes(messageRequestDto);
    session.send("/app/topic/private/" + topicId, messageBytes);

    // Then
    MessageResponseDto messageResponseDto = resultKeeper.get(3, SECONDS);
    assertThat(messageResponseDto).isNotNull();
    assertThat(messageResponseDto.getId()).isNotNull();
    assertThat(messageResponseDto.getSentFrom()).isEqualTo("Vasil");
    assertThat(messageResponseDto.getSendTo()).isEqualTo("Vasil");
    assertThat(messageResponseDto.getTimestamp()).isNotNull();
    assertThat(messageResponseDto.getContent()).isEqualTo(messageRequestDto.getContent());
  }

  @Test
  @SneakyThrows
  @DisplayName("getMessages should return received messages history from topic")
  void getMessages_shouldReturnReceivedMessagesHistoryFromTopic() {
    // Given
    int topicId = 12;
    PageRequestDto pageRequestDto = new PageRequestDto(0, 10);
    //Stored subscription results for testing
    CompletableFuture<MessageResponseDto[]> resultKeeper = new CompletableFuture<>();

    // Subscribe to topic
    session.subscribe("/user/topic/" + topicId,
        new TestStompFrameHandler<>(resultKeeper, objectMapper, MessageResponseDto[].class));

    // Get topic message history
    byte[] pageBytes = objectMapper.writeValueAsBytes(pageRequestDto);
    session.send("/app/history/topic/" + topicId, pageBytes);

    // Then
    MessageResponseDto[] messageResponseDtos = resultKeeper.get(3, SECONDS);
    assertThat(messageResponseDtos).isNotNull();
    assertThat(messageResponseDtos).extracting("id").isNotNull();
    assertThat(messageResponseDtos).extracting("timestamp").isNotNull();
    assertThat(messageResponseDtos).extracting("sentFrom")
        .contains("Anton", "Vasil");
    assertThat(messageResponseDtos).extracting("sendTo")
        .contains("Vasil", "Anton");
    assertThat(messageResponseDtos).extracting("content")
        .contains("hello Vasil!", "hello Anton!");
  }

  @Test
  @SneakyThrows
  @DisplayName("notifyTopicSubscribers should notify topic subscribers if subscribe event")
  void notifyTopicSubscribers_shouldNotifyTopicSubscribersIfSubscribeEvent() {
    // Given
    int topicId = 12;
    var lastMessageDto = new LastMessageResponseDto();
    lastMessageDto.setTimestamp(LocalDateTime.now());
    lastMessageDto.setSentFrom("vasil@gmail.com");
    lastMessageDto.setLastMessage("Hi");
    var typingEventDto = new TypingEventResponseDto("vasil@gmail.com", true);

    var event = new ContactEvent("vasil@gmail.com", topicId, ONLINE, LocalDateTime.now(), 0,
        lastMessageDto, typingEventDto);
    saveContactEvent(event);
    //Stored subscription results for testing
    CompletableFuture<MessageNotificationResponseDto[]> resultKeeper = new CompletableFuture<>();

    // Subscribe to notification
    session.subscribe("/user/specific/notify/" + topicId,
        new TestStompFrameHandler<>(resultKeeper, objectMapper,
            MessageNotificationResponseDto[].class));

    // Then
    var notifications = resultKeeper.get(3, SECONDS);
    assertThat(notifications).isNotNull();
    assertThat(notifications).extracting("email").contains("vasil@gmail.com");
    assertThat(notifications).extracting("topicId").contains(topicId);
    assertThat(notifications).extracting("status").contains(ONLINE);
    assertThat(notifications).extracting("lastRead").isNotNull();
  }

  @Test
  @SneakyThrows
  @DisplayName("notifyTopicSubscribers should notify topic subscribers if any contact subscribed to topic")
  void notifyTopicSubscribers_shouldNotifyTopicSubscribersIfAnyContactSubscribedToTopic() {
    // Given
    int topicId = 12;
    var lastMessageDto = new LastMessageResponseDto();
    lastMessageDto.setTimestamp(LocalDateTime.now());
    lastMessageDto.setSentFrom("vasil@gmail.com");
    lastMessageDto.setLastMessage("Hi");
    var typingEventDto = new TypingEventResponseDto("vasil@gmail.com", true);

    var event = new ContactEvent("vasil@gmail.com", topicId, ONLINE, LocalDateTime.now(), 0,
        lastMessageDto, typingEventDto);
    saveContactEvent(event);
    //Stored subscription results for testing
    CompletableFuture<MessageNotificationResponseDto[]> resultKeeper = new CompletableFuture<>();

    // Subscribe to topic
    session.subscribe("/topic/" + topicId, getFrameHandler());

    // Subscribe to notification
    session.subscribe("/user/specific/notify/" + topicId,
        new TestStompFrameHandler<>(resultKeeper, objectMapper,
            MessageNotificationResponseDto[].class));

    // Then
    var notifications = resultKeeper.get(3, SECONDS);
    assertThat(notifications).isNotNull();
    assertThat(notifications).extracting("email").contains("vasil@gmail.com");
    assertThat(notifications).extracting("topicId").contains(topicId);
    assertThat(notifications).extracting("status").contains(SUBSCRIBED);
    assertThat(notifications).extracting("lastRead").isNotNull();
  }

  //-----------------------------------
  //         Private methods
  //-----------------------------------

  private String getAccessToken() {
    String accessToken = jwtService.generateAccessToken(Contact.builder()
        .email("vasil@gmail.com")
        .password("Password-123")
        .role(USER)
        .build());

    saveTokenToRedis(accessToken);

    return accessToken;
  }

  private void saveTokenToRedis(String accessToken) {
    tokenRedisRepository.save(Token.builder()
        .email("vasil@gmail.com")
        .token(accessToken)
        .tokenType(BEARER)
        .expired(false)
        .revoked(false)
        .build());
  }

  private void saveContactEvent(ContactEvent contactEvent) {
    contactEventRedisRepository.save(contactEvent);
  }

  private StompSession createWebSocketSession(String URL, WebSocketHttpHeaders headers) {
    WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
    var connectSession = stompClient.connectAsync(URL, headers, new StompSessionHandlerAdapter() {
      @Override
      public void afterConnected(@NotNull StompSession session,
          @NotNull StompHeaders connectedHeaders) {
        log.info("Test session was successfully connected {}", session.getSessionId());
      }
    });
    // Wait for the connection to be established
    return connectSession.join();
  }

  @NotNull
  private static StompFrameHandler getFrameHandler() {
    return new StompFrameHandler() {
      @Override
      public @NotNull Type getPayloadType(@NotNull StompHeaders headers) {
        return byte[].class;
      }

      @Override
      public void handleFrame(@NotNull StompHeaders headers, Object payload) {

      }
    };
  }

}
