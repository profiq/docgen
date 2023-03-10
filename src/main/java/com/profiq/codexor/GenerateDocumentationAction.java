package com.profiq.codexor;

import com.google.gson.Gson;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GenerateDocumentationAction extends AnAction {

    int textStart;
    int textEnd;
    Document document;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String apiKey;

        try {
            apiKey = getApiKey();
        } catch (IOException ioException) {
            showError("Open AI API key was not provided");
            return;
        }

        var language = getLanguage(e);
        var prompt = getPrompt().replace("{{LANG}}", language);

        var editor = e.getData(PlatformDataKeys.EDITOR);
        if (editor != null) {
            document = editor.getDocument();

            String code = editor.getSelectionModel().getSelectedText();
            if (code != null && !code.isEmpty()) {
                textStart = editor.getSelectionModel().getSelectionStart();
                textEnd = editor.getSelectionModel().getSelectionEnd();
            } else {
                code = document.getText();
                textStart = 0;
                textEnd = document.getTextLength();
            }

            var messages = new Message[]{new Message("user", prompt + "```" + code + "```")};
            var requestBody = new Request("gpt-3.5-turbo", messages, 0);
            var requestJson = new Gson().toJson(requestBody);

            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

            ProgressManager.getInstance().run(new Task.Modal(e.getProject(), "Waiting for OpenAI", true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        if (response.statusCode() == 200) {
                            var responseParsed = (new Gson()).fromJson(response.body(), Response.class);
                            var newMessage = responseParsed.getChoices()[0].getMessage();
                            var app = ApplicationManager.getApplication();
                            app.invokeLater(() -> showEditor(e, cleanAnswer(newMessage.getContent())));
                        } else {
                            showError(response.body());
                        }
                    } catch (IOException | InterruptedException exception) {
                        showError("Can't reach OpenAI at the moment");
                    }
                }
            });
        }
    }

    private String getApiKey() throws IOException {
        String apiKeyInput = PropertiesComponent.getInstance().getValue(SettingsConfigurable.API_KEY_SETTING_KEY);

        if (apiKeyInput == null || apiKeyInput.length() == 0) {
            apiKeyInput = JOptionPane.showInputDialog("Enter your Open AI API key:");
            PropertiesComponent.getInstance().setValue(SettingsConfigurable.API_KEY_SETTING_KEY, apiKeyInput);
        }

        if (apiKeyInput == null) {
            throw new IOException("User did not enter an API key");
        }

        return apiKeyInput;
    }

    private String getPrompt() {
        String prompt = PropertiesComponent.getInstance().getValue(SettingsConfigurable.PROMPT_SETTINGS_KEY);

        if (prompt == null) {
            prompt = SettingsConfigurable.PROMPT_DEFAULT;
        }

        return prompt;
    }

    private void showError(String text) {
        JOptionPane.showMessageDialog(null, text, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String getLanguage(AnActionEvent e) {
        var virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);

        if (virtualFile == null) {
            return "";
        }

        var fileType = FileTypeManager.getInstance().getFileTypeByFile(virtualFile);

        if (!(fileType instanceof LanguageFileType)) {
            return "";
        }

        return fileType.getDisplayName();
    }

    private void showEditor(AnActionEvent e, String text) {
        var resultWindow = new JFrame();
        var virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        var fileType = FileTypeManager.getInstance().getFileTypeByFile(virtualFile);
        var newDocument = EditorFactory.getInstance().createDocument(text);
        var editor = EditorFactory.getInstance().createEditor(newDocument, null, fileType, false);
        editor.getContentComponent().setMaximumSize(new Dimension(600, 800));
        var confirmBtn = new JButton("Replace");
        var mainDocument = this.document;

        confirmBtn.addActionListener(actionEvent -> {
            var app = ApplicationManager.getApplication();
            app.invokeLater(() -> WriteCommandAction.runWriteCommandAction(
                e.getProject(), () -> mainDocument.replaceString(textStart, textEnd, newDocument.getText())));
            resultWindow.dispose();
        });

        var scrollPane = new JBScrollPane(editor.getComponent());
        scrollPane.setPreferredSize(new Dimension(800, 800));
        resultWindow.setTitle("Generated documentation");
        resultWindow.getContentPane().add(scrollPane, BorderLayout.NORTH);
        resultWindow.getContentPane().add(confirmBtn, BorderLayout.SOUTH);
        resultWindow.pack();
        resultWindow.setVisible(true);
        resultWindow.setLocationRelativeTo(null);
    }

    private String cleanAnswer(String answer) {
        if(answer.contains("```")) {
            return answer.split("```")[1];
        } else {
            return answer;
        }
    }
}
