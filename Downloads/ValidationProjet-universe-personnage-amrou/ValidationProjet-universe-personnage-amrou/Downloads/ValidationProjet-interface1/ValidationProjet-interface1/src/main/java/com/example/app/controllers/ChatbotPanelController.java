package com.example.app.controllers;

import com.example.app.services.ChatbotService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.concurrent.Task;

/**
 * Chatbot UI Controller - Chat with characters and universes
 * Can be embedded as a panel in Personnage or Universes pages
 */
public class ChatbotPanelController extends BaseController {

    @FXML private TextArea chatArea;
    @FXML private TextField inputField;
    @FXML private Button sendBtn;
    @FXML private ComboBox<String> modeCombo;

    private String currentCharacterName = "";
    private String currentCharacterDesc = "";
    private String currentUniverseName = "";
    private String currentUniverseContext = "";
    private boolean isCharacterMode = true;

    @FXML
    public void initialize() {
        if (modeCombo != null) {
            modeCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                "Ask Character", "Ask Universe", "Get Suggestions"
            ));
            modeCombo.setValue("Ask Character");
        }

        if (sendBtn != null) {
            sendBtn.setOnAction(e -> sendMessage());
        }

        if (inputField != null) {
            inputField.setOnAction(e -> sendMessage());
        }
    }

    public void setCharacter(String name, String description) {
        currentCharacterName = name;
        currentCharacterDesc = description;
        isCharacterMode = true;
        addChatLine("🎭 Now chatting with: " + name);
    }

    public void setUniverse(String name, String context) {
        currentUniverseName = name;
        currentUniverseContext = context;
        isCharacterMode = false;
        addChatLine("🌍 Now chatting about: " + name);
    }

    private void sendMessage() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) return;

        addChatLine("You: " + userInput);
        inputField.clear();

        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                String mode = modeCombo.getValue();

                if ("Get Suggestions".equals(mode)) {
                    if (isCharacterMode) {
                        return ChatbotService.suggestCharacterName(currentCharacterName);
                    } else {
                        return ChatbotService.suggestUniverseName(currentUniverseName);
                    }
                } else if (isCharacterMode) {
                    return ChatbotService.askAboutCharacter(
                        currentCharacterName,
                        currentCharacterDesc,
                        userInput
                    );
                } else {
                    return ChatbotService.askAboutUniverse(
                        currentUniverseName,
                        currentUniverseContext,
                        userInput
                    );
                }
            }
        };

        task.setOnSucceeded(e -> {
            String response = task.getValue();
            String character = isCharacterMode ? currentCharacterName : "Universe";
            addChatLine(character + ": " + response);
        });

        task.setOnFailed(e -> {
            addChatLine("Error: Could not get response");
        });

        new Thread(task).start();
    }

    private void addChatLine(String text) {
        if (chatArea != null) {
            chatArea.appendText(text + "\n\n");
        }
    }

    public void clearChat() {
        if (chatArea != null) {
            chatArea.clear();
        }
    }
}
