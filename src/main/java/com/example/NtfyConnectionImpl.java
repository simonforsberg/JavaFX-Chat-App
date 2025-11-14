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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Implementation of {@link NtfyConnection} that communicates with a Ntfy server
 * using Java's built-in {@link HttpClient}.
 * Supports sending messages to a topic and subscribing to a topic to receive streaming
 * JSON messages in real time.
 */
public class NtfyConnectionImpl implements NtfyConnection {

    private final HttpClient http = HttpClient.newHttpClient();
    private final String hostName;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a connection using a hostname loaded from a .env file.
     * Expects the variable HOST_NAME to be present.
     */
    public NtfyConnectionImpl() {
        Dotenv dotenv = Dotenv.load();
        hostName = Objects.requireNonNull(dotenv.get("HOST_NAME"));
    }

    /**
     * Creates a connection using the given hostname.
     *
     * @param hostName Base URL of the Ntfy server
     */
    public NtfyConnectionImpl(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Sends a message to the given topic.
     *
     * @param topic   The topic to publish to.
     * @param message Message body to send.
     * @throws IOException If sending fails or the thread is interrupted.
     */
    @Override
    public void send(String topic, String message) throws IOException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .header("Cache-Control", "no-cache")
                .uri(URI.create(hostName + "/" + topic))
                .build();
        try {
            http.send(httpRequest, HttpResponse.BodyHandlers.discarding());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while sending message", e);
        }
    }

    /**
     * Subscribes to a topic and receives incoming messages as a JSON stream.
     * Each valid message is deserialized into {@link NtfyMessageDto} and passed
     * to the given message handler.
     * <p>
     * The subscription remains active until {@link Subscription#close()} is called.
     *
     * @param topic           Topic to subscribe to.
     * @param messageHandler  Callback invoked for each received message.
     * @return A {@link Subscription} that can be closed to stop listening.
     */
    @Override
    public Subscription receive(String topic, Consumer<NtfyMessageDto> messageHandler) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(hostName + "/" + topic + "/json"))
                .build();

        AtomicBoolean active = new AtomicBoolean(true);

        CompletableFuture<Void> future = http.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .exceptionally(throwable -> {
                    System.err.println("Failed to receive messages: " + throwable.getMessage());
                    return null;
                })
                .thenAccept(response -> {
                    if (response == null) return;
                    response.body()
                            .filter(s -> active.get())
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

        return new Subscription() {
            /**
             * Stops the subscription and cancels the streaming request.
             */
            @Override
            public void close() throws IOException {
                active.set(false);
                future.cancel(true);
            }

            /**
             * Indicates whether the subscription is still active.
             */
            @Override
            public boolean isOpen() {
                return active.get() && !future.isDone();
            }
        };
    }
}