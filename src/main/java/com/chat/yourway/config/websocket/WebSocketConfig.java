package com.chat.yourway.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final WebsocketProperties properties;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker(properties.getDestPrefixes());
    registry.setApplicationDestinationPrefixes(properties.getAppPrefix());
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint(properties.getEndpoint());
    registry.addEndpoint(properties.getEndpoint()).setAllowedOriginPatterns("*").withSockJS();
  }


  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    registry.setTimeToFirstMessage(properties.getTimeToFirstMessage())
            .setSendTimeLimit(15 * 1000)
            .setSendBufferSizeLimit(512 * 1024)
            .setMessageSizeLimit(128 * 1024);
  }

}
