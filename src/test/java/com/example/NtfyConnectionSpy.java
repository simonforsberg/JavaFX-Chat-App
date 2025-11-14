package com.example;

import java.util.function.Consumer;

/**
 * Spy implementation of NtfyConnection used in tests.
 * Records sent messages for verification.
 */
public class NtfyConnectionSpy implements NtfyConnection {

    /** Last sent message. */
    String message;
    /** Last topic used. */
    String topic;

    /**
     * Records the topic and message that were sent.
     *
     * @param topic   the topic the message was sent to
     * @param message the message that was sent
     */
    @Override
    public void send(String topic, String message) {
        this.topic = topic;
        this.message = message;
    }

    /**
     * Records the topic and returns a subscription for testing.
     *
     * @param topic          the topic to subscribe to
     * @param messageHandler the handler for incoming messages (not invoked in Spy)
     * @return a Subscription object that can be closed and queried
     */
    @Override
    public Subscription receive(String topic, Consumer<NtfyMessageDto> messageHandler) {
        this.topic = topic;

        return new Subscription() {
            private boolean active = true;

            /**
             * Closes the subscription.
             */
            @Override
            public void close() {
                active = false;
            }

            /**
             * Checks whether the subscription is still open.
             *
             * @return true if open, false if closed
             */
            @Override
            public boolean isOpen() {
                return active;
            }
        };
    }
}