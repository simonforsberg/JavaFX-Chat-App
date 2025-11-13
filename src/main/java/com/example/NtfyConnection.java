package com.example;

import java.util.function.Consumer;

public interface NtfyConnection {

    boolean send(String topic, String message);

    void receive(String topic, Consumer<NtfyMessageDto> messageHandler);

}