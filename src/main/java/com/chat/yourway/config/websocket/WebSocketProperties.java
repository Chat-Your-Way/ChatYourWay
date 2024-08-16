package com.chat.yourway.config.websocket;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "socket")
@Getter
@Setter
public class WebSocketProperties {

  private String[] destPrefixes;
  private String appPrefix;
  private String endpoint;
  private String topicPrefix;
  private String notifyPrefix;
  private String errorPrefix;
  private int TimeToFirstMessage;

}
