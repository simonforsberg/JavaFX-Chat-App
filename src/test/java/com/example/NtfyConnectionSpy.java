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
    public void receive(String topic, Consumer<NtfyMessageDto> messageHandler) {
    }

}