package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 * Handles user interaction, initializes UI bindings, and forwards actions
 * (sending messages, switching topics) to the model.
 */
public class HelloController {

    /** The application model injected by the framework or calling code. */
    private final HelloModel model;

    /**
     * Default constructor used by the JavaFX runtime.
     * Creates a model with a production {@link NtfyConnectionImpl}.
     */
    public HelloController() {
        this(new HelloModel(new NtfyConnectionImpl()));
    }

    /**
     * Constructor primarily intended for testing or dependency injection.
     *
     * @param model the model instance this controller should use
     */
    public HelloController(HelloModel model) {
        this.model = model;
    }

    @FXML
    public ListView<NtfyMessageDto> messageView;

    @FXML
    private Label messageLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField inputField;

    @FXML
    private TextField topicField;

    /**
     * Called automatically by JavaFX after FXML fields are injected.
     * Sets up UI bindings, listens for connection status changes,
     * and triggers the initial topic connection.
     */
    @FXML
    private void initialize() {
        if (messageLabel != null) {
            messageLabel.setText(model.getGreeting());
        }

        // Bind input fields to model state
        inputField.textProperty().bindBidirectional(model.messageToSendProperty());
        topicField.textProperty().bindBidirectional(model.topicProperty());
        messageView.setItems(model.getMessages());

        // Update connection status indicator
        model.connectedProperty().addListener((obs, wasConnected, isConnected) -> {
            statusLabel.setText(isConnected ? "🟢 Connected" : "🔴 Disconnected");
        });

        model.connectToTopic();
    }

    /**
     * Handles clicking the "Send" button.
     * Sends the message via the model and clears the input field.
     */
    public void sendMessage(ActionEvent actionEvent) {
        if (!inputField.getText().trim().isEmpty()) {
            try {
                model.sendMessage();
                inputField.clear();
            } catch (IOException e) {
                System.err.println("Failed to send message: " + e.getMessage());
            }
        }
    }

    /**
     * Handles clicking the "Connect" button.
     * Reconnects the model to the current topic.
     */
    public void connectToTopic(ActionEvent actionEvent) {
        model.connectToTopic();
    }
}