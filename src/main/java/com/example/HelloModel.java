package com.example;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;

/**
 * Model layer: encapsulates application data and business logic.
 * Manages the active topic subscription, incoming messages, and
 * message sending through an {@link NtfyConnection}.
 */
public class HelloModel {

    /** Underlying connection for sending and receiving messages. */
    private final NtfyConnection connection;
    /** Observable list of received messages for UI binding. */
    private final ObservableList<NtfyMessageDto> messages = FXCollections.observableArrayList();
    /** Text the user intends to send. */
    private final StringProperty messageToSend = new SimpleStringProperty();
    /** Currently selected topic. */
    private final StringProperty topic = new SimpleStringProperty("mytopic");
    /** Handle for the active subscription, if any. */
    private NtfyConnection.Subscription currentSubscription;
    /** Indicates whether the model is currently connected to a topic. */
    private final ReadOnlyBooleanWrapper connected = new ReadOnlyBooleanWrapper(false);

    /**
     * Creates a new model using the provided {@link NtfyConnection}.
     *
     * @param connection the message connection backend
     */
    public HelloModel(NtfyConnection connection) {
        this.connection = connection;
    }

    /** @return observable list of received messages */
    public ObservableList<NtfyMessageDto> getMessages() {
        return messages;
    }

    public StringProperty messageToSendProperty() {
        return messageToSend;
    }

    public String getMessageToSend() {
        return messageToSend.get();
    }

    public void setMessageToSend(String message) {
        messageToSend.set(message);
    }

    public String getTopic() {
        return topic.get();
    }

    public StringProperty topicProperty() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic.set(topic);
    }

    /**
     * Read-only property indicating whether a subscription is active.
     */
    public ReadOnlyBooleanProperty connectedProperty() {
        return connected.getReadOnlyProperty();
    }

    /**
     * @return true if a subscription is active and open
     */
    public boolean isConnected() {
        return connected.get();
    }

    /**
     * Returns a greeting based on the current Java and JavaFX versions.
     */
    public String getGreeting() {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        return "Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".";
    }

    /**
     * Sends the value of {@link #messageToSend} to the current topic.
     *
     * @throws IOException if sending through the connection fails
     */
    public void sendMessage() throws IOException {
        connection.send(topic.get(), messageToSend.get());
        messageToSend.set("");
    }

    /**
     * Connects to the current topic by creating a new subscription.
     * Any previous subscription is closed first.
     * Old messages are preserved if subscription creation fails.
     * Incoming messages are added to {@link #messages} on the JavaFX thread.
     */
    public void connectToTopic() {
        disconnect();

        // Make a backup of current messages in case subscription fails
        var oldMessages = FXCollections.observableArrayList(messages);
        // Clear messages for the new topic
        messages.clear();

        try {
            // Start receiving new messages asynchronously
            currentSubscription = connection.receive(topic.get(),
                    m -> runOnFx(() -> messages.add(m)));
            // Mark as connected
            connected.set(true);
        } catch (Exception e) {
            // Restore old messages if connection failed
            messages.setAll(oldMessages);
            connected.set(false);
            System.err.println("Failed to connect to topic: " + e.getMessage());
        }
    }


    /**
     * Stops the active subscription, if one exists, and updates connection state.
     */
    public void disconnect() {
        if (currentSubscription != null) {
            try {
                if (currentSubscription.isOpen()) {
                    currentSubscription.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing subscription: " + e.getMessage());
            }
            currentSubscription = null;
            connected.set(false);
        }
    }

    /**
     * Ensures that the given task runs on the JavaFX thread.
     * Falls back to direct execution if JavaFX is not initialized (e.g. in tests).
     */
    private static void runOnFx(Runnable task) {
        try {
            if (Platform.isFxApplicationThread()) task.run();
            else Platform.runLater(task);
        } catch (IllegalStateException notInitialized) {
            task.run();
        }
    }
}