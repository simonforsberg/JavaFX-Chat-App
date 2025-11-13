package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model;

    public HelloController() {
        this(new HelloModel(new NtfyConnectionImpl()));
    }
    public HelloController(HelloModel model) {
        this.model = model;
    }

    @FXML
    public ListView<NtfyMessageDto> messageView;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField inputField;

    @FXML
    private TextField topicField;

    @FXML
    private void initialize() {
        if (messageLabel != null) {
            messageLabel.setText(model.getGreeting());
        }

        inputField.textProperty().bindBidirectional(model.messageToSendProperty());

        topicField.textProperty().bindBidirectional(model.topicProperty());

        messageView.setItems(model.getMessages());

        model.connectToTopic();
    }

    public void sendMessage(ActionEvent actionEvent) {
        if (!inputField.getText().trim().isEmpty()) {
            try {
                model.sendMessage();
                inputField.clear();
            } catch (IOException e) {
                // TODO: Show error message to user
                System.err.println("Failed to send message: " + e.getMessage());
            }
        }
    }

    public void connectToTopic(ActionEvent actionEvent) {
        model.connectToTopic();
    }
}