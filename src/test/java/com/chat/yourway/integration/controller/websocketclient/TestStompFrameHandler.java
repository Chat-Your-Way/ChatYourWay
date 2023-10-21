package com.chat.yourway.integration.controller.websocketclient;

import java.lang.reflect.Type;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

@Slf4j
@SuppressWarnings("unchecked")
public class TestStompFrameHandler<T> implements StompFrameHandler {

  private final Class<?> returnClass;

  private final Consumer<T> frameHandler;


  public TestStompFrameHandler(Consumer<T> frameHandler, Class<?> clazz) {
    this.frameHandler = frameHandler;
    this.returnClass = clazz;
  }

  @Override
  public @NotNull Type getPayloadType(@NotNull StompHeaders headers) {
    return returnClass;
  }

  @Override
  public void handleFrame(@NotNull StompHeaders headers, Object payload) {
    log.info("received message: {} with headers: {}", payload, headers);
    frameHandler.accept((T) payload);
  }

}
