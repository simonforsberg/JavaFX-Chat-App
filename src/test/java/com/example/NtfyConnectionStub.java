package com.example;

import java.util.function.Consumer;

public class NtfyConnectionStub implements NtfyConnection {

    private Consumer<NtfyMessageDto> messageHandler;

    @Override
    public void send(String topic, String message) {
    }

    @Override
    public void receive(String topic, Consumer<NtfyMessageDto> messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void simulateIncomingMessage(NtfyMessageDto message) {
        if (messageHandler != null) {
            messageHandler.accept(message);
        }
    }
}
