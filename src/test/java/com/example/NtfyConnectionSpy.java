package com.example;

import java.util.function.Consumer;

public class NtfyConnectionSpy implements NtfyConnection {

    String message;
    String topic;

    @Override
    public void send(String topic, String message) {
        this.topic = topic;
        this.message = message;
    }

    @Override
    public Subscription receive(String topic, Consumer<NtfyMessageDto> messageHandler) {
        this.topic = topic;

        return new Subscription() {
            private boolean active = true;

            @Override
            public void close() {
                active = false;
            }

            @Override
            public boolean isActive() {
                return active;
            }
        };
    }
}