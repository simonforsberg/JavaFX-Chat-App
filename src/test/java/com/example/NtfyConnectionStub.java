package com.example;

import java.util.function.Consumer;

/**
 * Stub implementation of NtfyConnection used in tests.
 * Allows simulating incoming messages and tracking subscription state.
 */
public class NtfyConnectionStub implements NtfyConnection {

    /** Handler to call when simulating incoming messages. */
    private Consumer<NtfyMessageDto> messageHandler;
    /** Flag indicating if the subscription is active. */
    private boolean subscriptionActive = false;

    /**
     * Does nothing in this stub; included for interface completeness.
     *
     * @param topic   the topic the message would be sent to
     * @param message the message content
     */
    @Override
    public void send(String topic, String message) {
    }

    /**
     * Simulates subscribing to a topic.
     * Stores the handler for later invocation via {@link #simulateIncomingMessage}.
     *
     * @param topic          the topic to subscribe to
     * @param messageHandler the callback for received messages
     * @return a Subscription object to control the simulated subscription
     */
    @Override
    public Subscription receive(String topic, Consumer<NtfyMessageDto> messageHandler) {
        this.messageHandler = messageHandler;
        subscriptionActive = true;

        return new Subscription() {
            /**
             * Closes the simulated subscription.
             */
            @Override
            public void close() {
                subscriptionActive = false;
            }

            /**
             * Checks whether the simulated subscription is active.
             *
             * @return true if active, false if closed
             */
            @Override
            public boolean isOpen() {
                return subscriptionActive;
            }
        };
    }

    /**
     * Simulates an incoming message by invoking the registered handler
     * if the subscription is active.
     *
     * @param message the message to simulate
     */
    public void simulateIncomingMessage(NtfyMessageDto message) {
        if (messageHandler != null && subscriptionActive) {
            messageHandler.accept(message);
        }
    }
}