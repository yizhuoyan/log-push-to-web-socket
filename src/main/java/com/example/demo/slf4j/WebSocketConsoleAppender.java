package com.example.demo.slf4j;

import ch.qos.logback.core.OutputStreamAppender;
import com.example.demo.service.WebSocketLogMessageManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class WebSocketConsoleAppender<E> extends OutputStreamAppender<E> {
    @Override
    public void start() {
        this.setOutputStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void write(byte[] b) throws IOException {
                WebSocketLogMessageManager.add(b);
            }
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                throw new UnsupportedOperationException();
            }
        });
        super.start();
    }
}