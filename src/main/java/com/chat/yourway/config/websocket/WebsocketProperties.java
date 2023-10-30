package com.chat.yourway.config.websocket;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "socket")
@Getter
@Setter
public class WebsocketProperties {

  private String[] destPrefixes;
  private String appPrefix;
  private String endpoint;

}
