package com.profiq.codexor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GenerateDocumentationAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        String apiKey;

        try {
            apiKey = getApiKey();
        } catch (IOException ioException) {
            showError("Open AI API key was not provided");
            return;
        }

        var language = getLanguage(event);
        var prompt = getPrompt().replace("{{LANG}}", language);

        var editor = event.getData(PlatformDataKeys.EDITOR);
        if (editor != null) {
            var document = editor.getDocument();
            int textStart, textEnd;

            String code = editor.getSelectionModel().getSelectedText();
            if (code != null && !code.isEmpty()) {
                textStart = editor.getSelectionModel().getSelectionStart();
                textEnd = editor.getSelectionModel().getSelectionEnd();
            } else {
                code = document.getText();
                textStart = 0;
                textEnd = document.getTextLength();
            }

            String body = constructQuery(prompt, code);

            var request = HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/edits"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            queryOpenAI(event, document, textStart, textEnd, request);
        }
    }

    @NotNull
    private String constructQuery(String prompt, String code) {
        var body = new JsonObject();
        body.addProperty("model", "code-davinci-edit-001");
        body.addProperty("input", code);
        body.addProperty("instruction", prompt);
        body.addProperty("temperature", 0);
        return body.toString();
    }

    private void queryOpenAI(@NotNull AnActionEvent e, Document document, int textStart, int textEnd, HttpRequest request) {
        ProgressManager.getInstance().run(new Task.Modal(e.getProject(), "Waiting for OpenAI", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    var client = HttpClient.newHttpClient();
                    var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 200) {
                        var responseParsed = (new Gson()).fromJson(response.body(), Response.class);
                        var app = ApplicationManager.getApplication();
                        app.invokeLater(() -> WriteCommandAction.runWriteCommandAction(
                            e.getProject(), () -> {
                                var newText = responseParsed.getChoices()[0].getText();
                                document.replaceString(textStart, textEnd, newText);
                            }));
                    } else {
                        showError(response.body());
                    }
                } catch (IOException | InterruptedException exception) {
                    showError("Can't reach OpenAI at the moment");
                }
            }
        });
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

        if(virtualFile == null) {
            return "";
        }

        var fileType = FileTypeManager.getInstance().getFileTypeByFile(virtualFile);

        if(!(fileType instanceof LanguageFileType)) {
            return "";
        }

        return fileType.getDisplayName();
    }
}
