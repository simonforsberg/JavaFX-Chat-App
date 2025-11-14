package com.example;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Consumer;

public interface NtfyConnection {

    void send(String topic, String message) throws IOException;

    Subscription receive(String topic, Consumer<NtfyMessageDto> messageHandler);

    interface Subscription extends Closeable {
        @Override
        void close() throws IOException;

        boolean isOpen();
    }
}