package com.example.app.controllers;

import com.example.app.services.ChatbotService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Map;

public class HeaderController extends BaseController {

	private final ChatbotService chatbotService = new ChatbotService();

	@FXML
	private void openChatbot() {
		if (!ChatbotService.isConfigured()) {
			showError("Chatbot", chatbotService.healthCheck());
			return;
		}

		TextArea conversationArea = new TextArea();
		conversationArea.setEditable(false);
		conversationArea.setWrapText(true);
		conversationArea.setPrefRowCount(12);

		TextField inputField = new TextField();
		inputField.setPromptText("Posez votre question sur la boutique");

		Button sendButton = new Button("Envoyer");
		sendButton.setDefaultButton(true);

		sendButton.setOnAction(event -> {
			String userMessage = inputField.getText();
			if (userMessage == null || userMessage.isBlank()) {
				return;
			}

			if (!conversationArea.getText().isBlank()) {
				conversationArea.appendText("\n\n");
			}
			conversationArea.appendText("Vous: " + userMessage);
			inputField.clear();

			Task<String> task = new Task<>() {
				@Override
				protected String call() {
					return chatbotService.chat(userMessage, Map.of());
				}
			};
			task.setOnSucceeded(workerStateEvent -> conversationArea.appendText("\nAssistant: " + task.getValue()));
			task.setOnFailed(workerStateEvent -> conversationArea.appendText("\nAssistant: Impossible de générer une réponse pour le moment."));

			Thread thread = new Thread(task);
			thread.setDaemon(true);
			thread.start();
		});

		VBox content = new VBox(10, conversationArea, inputField, sendButton);
		content.setStyle("-fx-padding: 12;");

		Alert dialog = new Alert(Alert.AlertType.NONE);
		dialog.setTitle("Assistant Boutique");
		dialog.getDialogPane().setContent(content);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
		dialog.showAndWait();
	}
}
