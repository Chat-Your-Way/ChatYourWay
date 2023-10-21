package com.chat.yourway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Value("${socket.dest-prefixes}")
  private String[] destPrefixes;

  @Value("${socket.app-prefix}")
  private String appPrefix;

  @Value("${socket.endpoint}")
  private String endpoint;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker(destPrefixes);
    registry.setApplicationDestinationPrefixes(appPrefix);
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint(endpoint);
    registry.addEndpoint(endpoint).withSockJS();
  }
}
