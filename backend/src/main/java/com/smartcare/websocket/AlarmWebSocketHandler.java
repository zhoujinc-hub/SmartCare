package com.smartcare.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private static final Set<WebSocketSession> SESSIONS = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        SESSIONS.add(session);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of(
                "type", "CONNECTED",
                "message", "报警地图WebSocket连接成功"
        ))));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        SESSIONS.remove(session);
    }

    public void broadcast(Object payload) {
        String text;
        try {
            text = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.error("报警消息序列化失败", e);
            return;
        }

        for (WebSocketSession session : SESSIONS) {
            if (!session.isOpen()) {
                SESSIONS.remove(session);
                continue;
            }
            try {
                session.sendMessage(new TextMessage(text));
            } catch (IOException e) {
                log.warn("报警消息推送失败，移除连接", e);
                SESSIONS.remove(session);
            }
        }
    }
}
