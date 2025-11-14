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
 */
public class HelloModel {

    private final NtfyConnection connection;
    private final ObservableList<NtfyMessageDto> messages = FXCollections.observableArrayList();
    private final StringProperty messageToSend = new SimpleStringProperty();
    private final StringProperty topic = new SimpleStringProperty("mytopic");
    private NtfyConnection.Subscription currentSubscription;
    private final ReadOnlyBooleanWrapper connected = new ReadOnlyBooleanWrapper(false);

    public HelloModel(NtfyConnection connection) {
        this.connection = connection;
    }

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

    public ReadOnlyBooleanProperty connectedProperty() {
        return connected.getReadOnlyProperty();
    }

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

    public void sendMessage() throws IOException {
        connection.send(topic.get(), messageToSend.get());
        messageToSend.set("");
    }

    public void connectToTopic() {
        disconnect();
        messages.clear();

        currentSubscription = connection.receive(topic.get(),
                m -> runOnFx(() -> messages.add(m)));

        connected.set(true);
    }

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

    private static void runOnFx(Runnable task) {
        try {
            if (Platform.isFxApplicationThread()) task.run();
            else Platform.runLater(task);
        } catch (IllegalStateException notInitialized) {
            task.run();
        }
    }
}