package com.chat.yourway.integration.controller;

import static com.github.springtestdbunit.annotation.DatabaseOperation.CLEAN_INSERT;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.chat.yourway.dto.request.MessagePublicRequestDto;
import com.chat.yourway.dto.response.MessageResponseDto;
import com.chat.yourway.integration.controller.websocketclient.TestStompFrameHandler;
import com.chat.yourway.integration.extension.PostgresExtension;
import com.chat.yourway.integration.extension.RedisExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import java.util.concurrent.CompletableFuture;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
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
}, type = CLEAN_INSERT)
public class ChatControllerTest {

  @Autowired
  private ObjectMapper objectMapper;

  @LocalServerPort
  private int port;
  private CompletableFuture<StompSession> connectSession;

  private CompletableFuture<MessageResponseDto> resultKeeper;

  @BeforeEach
  @SneakyThrows
  void setUp() {

    String URL = "ws://localhost:" + port + "/chat";

    WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());

    connectSession = stompClient.connectAsync(URL, new StompSessionHandlerAdapter() {
      @Override
      public void afterConnected(@NotNull StompSession session, @NotNull StompHeaders connectedHeaders) {
        log.info("Test session was successfully connected {}", session.getSessionId());
      }
    });

    resultKeeper = new CompletableFuture<>();

  }


  @SneakyThrows
  @Test
  void shouldReturnCorrectSendMessage() {
    // Given
    int topicId = 3;
    MessagePublicRequestDto messageRequestDto = new MessagePublicRequestDto("hi");

    // Wait for the connection to be established
    StompSession session = connectSession.join();

    //subscribe to topic
    session.subscribe("/topic/" + topicId,
        new TestStompFrameHandler<>(resultKeeper::complete, MessageResponseDto.class));

    //send message to topic
    byte[] messageBytes = objectMapper.writeValueAsBytes(messageRequestDto);
    session.send("/app/topic/" + topicId, messageBytes);

    //processing result
    MessageResponseDto messageResponseDto = resultKeeper.get(3, SECONDS);
    Assertions.assertThat(messageResponseDto).isNotNull();
  }

}
