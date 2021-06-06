package com.example.demo.web.controller;

import com.example.demo.service.WebSocketLogMessageManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;


@Slf4j
public class WebConsoleWebSocketHandler extends AbstractWebSocketHandler implements Runnable {
    private volatile WebSocketSession currentWebSocketSession;

    public WebConsoleWebSocketHandler() {
        new Thread(this, "WebConsole push message Thread").start();
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (log.isWarnEnabled()) {
            log.warn("session-{} occer transport error:{} ", session.getId(), exception.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("session-{}  closed with {}", session.getId(), status.getReason());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        synchronized (this) {
            currentWebSocketSession = session;
            this.notifyAll();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (currentWebSocketSession == null || !currentWebSocketSession.isOpen()) {
                    synchronized (this) {
                        while (currentWebSocketSession == null || !currentWebSocketSession.isOpen()) {
                            this.wait();
                        }
                    }
                }
                currentWebSocketSession.sendMessage(new TextMessage(WebSocketLogMessageManager.take()));
                Thread.sleep(0);
                System.out.println("发送消息");
            } catch (Throwable e) {
                e.printStackTrace();
                System.out.println("发送失败，忽略异常");
            }
        }
    }
}