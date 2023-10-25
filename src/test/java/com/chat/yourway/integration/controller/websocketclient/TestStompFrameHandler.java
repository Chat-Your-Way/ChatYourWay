package com.chat.yourway.integration.controller.websocketclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

@Slf4j
@AllArgsConstructor
public class TestStompFrameHandler<T> implements StompFrameHandler {

  private final CompletableFuture<T> resultKeeper;
  private final ObjectMapper objectMapper;
  private final Class<T> returnClass;

  @Override
  public @NotNull Type getPayloadType(@NotNull StompHeaders headers) {
    return byte[].class;
  }

  @SneakyThrows
  @Override
  public void handleFrame(@NotNull StompHeaders headers, Object payload) {
    T message = objectMapper.readValue((byte[]) payload, returnClass);
    log.info("received message: {} with headers: {}", message, headers);
    resultKeeper.complete(message);
  }
}
