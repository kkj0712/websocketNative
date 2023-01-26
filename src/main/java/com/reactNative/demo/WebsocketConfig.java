package com.reactNative.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {
  private final WebsocketHandler websocketHandler = new WebsocketHandler();

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(websocketHandler, "/apTourNoticeList").setAllowedOrigins("*"); // setAllowedOrigins("*") -> 모든 도메인 요청 허용
  }
}
