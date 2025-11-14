package com.example;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Represents a connection to a Ntfy-compatible notification service.
 * Implementations of this interface provide basic operations for:
 *  * Sending messages to a specific topic
 *  * Subscribing to incoming messages from a topic
 */
public interface NtfyConnection {

    /**
     * Sends a message to the given topic.
     *
     * @param topic   the topic to publish to (must not be null or blank)
     * @param message the message content to send
     * @throws IOException if the message cannot be delivered due to
     *                     network errors or underlying I/O issues
     */
    void send(String topic, String message) throws IOException;

    /**
     * Subscribes to a topic and receives messages asynchronously.
     *
     * @param topic          the topic to subscribe to
     * @param messageHandler callback invoked for every received message
     * @return a {@link Subscription} that controls the active message stream
     */
    Subscription receive(String topic, Consumer<NtfyMessageDto> messageHandler);

    /**
     * Controls an active topic subscription.
     * Encapsulates the logic to stop an active message stream.
     */
    interface Subscription extends Closeable {

        /**
         * Closes this subscription and stops receiving messages.
         * @throws IOException if closing fails
         */
        @Override
        void close() throws IOException;

        /**
         * Checks whether the subscription is still active.
         * @return true if active, false otherwise
         */
        boolean isOpen();
    }
}