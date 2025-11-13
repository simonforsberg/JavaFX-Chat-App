package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NtfyConnectionImpl implements NtfyConnection {

    private final HttpClient http = HttpClient.newHttpClient();
    private final String hostName;
    private final ObjectMapper mapper = new ObjectMapper();
    private CompletableFuture<Void> receiveTask;

    public NtfyConnectionImpl() {
        Dotenv dotenv = Dotenv.load();
        hostName = Objects.requireNonNull(dotenv.get("HOST_NAME"));
    }

    public NtfyConnectionImpl(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public void send(String topic, String message) throws IOException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .header("Cache", "no")
                .uri(URI.create(hostName + "/" + topic))
                .build();
        try {
            http.send(httpRequest, HttpResponse.BodyHandlers.discarding());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while sending message", e);
        }
    }

    @Override
    public synchronized void receive(String topic, Consumer<NtfyMessageDto> messageHandler) {
        if (receiveTask != null) {
            receiveTask.cancel(true);
        }
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(hostName + "/" + topic + "/json"))
                .build();

        receiveTask = http.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .exceptionally(throwable -> {
                    System.err.println("Failed to receive messages: " + throwable.getMessage());
                    return null;
                })

                .thenAccept(response -> {
                    if (response == null) return;
                    response.body()
                            .map(s -> {
                                try {
                                    return mapper.readValue(s, NtfyMessageDto.class);
                                } catch (Exception e) {
                                    System.err.println("Failed to parse message: " + e.getMessage());
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .filter(message -> "message".equals(message.event()))
                            .forEach(messageHandler);
                });
    }
}