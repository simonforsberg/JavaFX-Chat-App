package com.example;

import java.io.IOException;
import java.util.function.Consumer;

public interface NtfyConnection {

    void send(String topic, String message) throws IOException;

    Subscription receive(String topic, Consumer<NtfyMessageDto> messageHandler);

    interface Subscription {
        void close();
        boolean isActive();
    }
}