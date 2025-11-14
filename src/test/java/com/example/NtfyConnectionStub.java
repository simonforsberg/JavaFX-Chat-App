package com.example;

import java.util.function.Consumer;

public class NtfyConnectionStub implements NtfyConnection {

    private Consumer<NtfyMessageDto> messageHandler;
    private boolean subscriptionActive = false;

    @Override
    public void send(String topic, String message) {
    }

    @Override
    public Subscription receive(String topic, Consumer<NtfyMessageDto> messageHandler) {
        this.messageHandler = messageHandler;
        subscriptionActive = true;

        return new Subscription() {
            @Override
            public void close() {
                subscriptionActive = false;
            }

            @Override
            public boolean isActive() {
                return subscriptionActive;
            }
        };
    }

    public void simulateIncomingMessage(NtfyMessageDto message) {
        if (messageHandler != null && subscriptionActive) {
            messageHandler.accept(message);
        }
    }
}