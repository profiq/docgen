package com.profiq.codexor;

import com.google.gson.Gson;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.psi.PsiElement;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateDocumentationAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        event.getPresentation().setEnabledAndVisible(editor != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String apiKey;

        try {
            apiKey = getApiKey();
        } catch (IOException ioException) {
            showError("Open AI API key was not provided");
            return;
        }

        var model = getModel();
        var prompt = getPrompt();
        PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);

        if (element == null) {
            showError("Please select a Python function");
            return;
        }

        String code = element.getText();

        if (code != null) {
            var client = HttpClient.newHttpClient();
            HttpRequest request = buildRequest(apiKey, model, prompt, code);
            ProgressManager.getInstance().run(new Task.Modal(e.getProject(), "Waiting for OpenAI", true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    var responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

                    responseFuture.thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            var responseParsed = (new Gson()).fromJson(response.body(), Response.class);
                            var responseText = responseParsed.getChoices()[0].getMessage().getContent();
                            String docstring = parseDocstring(responseText);
                            var app = ApplicationManager.getApplication();
                            app.invokeLater(() -> showEditor(e, docstring));
                        } else {
                            indicator.cancel();
                            showError(response.body());
                        }
                    });

                    responseFuture.exceptionally(e -> {
                        if (e instanceof IOException || e instanceof InterruptedException) {
                            showError("Can't reach OpenAI at the moment");
                        }
                        return null;
                    });


                    while (!responseFuture.isDone()) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            System.out.println("Interrupted!");
                        }

                        if(indicator.isCanceled()) {
                            responseFuture.cancel(true);
                            indicator.checkCanceled();
                        }
                    }

                    responseFuture.join();
                }
            });
        }
    }

    private static HttpRequest buildRequest(String apiKey, String model, String prompt, String code) {
        var messages = new Message[]{new Message("user", prompt + "```" + code + "```")};
        var requestBody = new Request(model, messages, 0);
        var requestJson = new Gson().toJson(requestBody);

        return HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestJson))
            .build();
    }

    private String parseDocstring(String responseText) {
        Pattern pattern = Pattern.compile(":\n(\\s*\"\"\".*\"\"\")", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(responseText);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
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

    private String getModel() {
        String model = PropertiesComponent.getInstance().getValue(SettingsConfigurable.MODEL_SETTINGS_KEY);

        if (model == null) {
            model = SettingsConfigurable.MODEL_DEFAULT;
        }

        return model;
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

    private void showEditor(AnActionEvent e, String docstring) {
        var resultWindow = new JFrame();
        var virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        var fileType = FileTypeManager.getInstance().getFileTypeByFile(virtualFile);

        if (!fileType.getName().equals("Python")) {
            showError("Only Python is supported at the moment");
            return;
        }

        var newDocument = EditorFactory.getInstance().createDocument(docstring);
        var editor = EditorFactory.getInstance().createEditor(newDocument, null, fileType, false);
        editor.getContentComponent().setPreferredSize(new Dimension(1000, 800));
        var confirmBtn = new JButton("Insert");


        Editor mainEditor = e.getData(CommonDataKeys.EDITOR);
        Document mainDocument = mainEditor.getDocument();
        LogicalPosition caretPosition = mainEditor.getCaretModel().getLogicalPosition();
        int lineEndOffset = mainDocument.getLineEndOffset(caretPosition.line);
        int nextLineStartOffset = mainDocument.getLineStartOffset(caretPosition.line + 1);
        int offset = Math.min(lineEndOffset + 1, nextLineStartOffset);
        String formattedDocstring = docstring + "\n";

        confirmBtn.addActionListener(actionEvent -> {
            var app = ApplicationManager.getApplication();
            app.invokeLater(() -> WriteCommandAction.runWriteCommandAction(
                e.getProject(), () -> mainDocument.insertString(offset, formattedDocstring)));
            resultWindow.dispose();
        });

        var scrollPane = new JBScrollPane(editor.getComponent());
        scrollPane.setPreferredSize(new Dimension(1000, 800));
        resultWindow.setTitle("Generated documentation");
        resultWindow.getContentPane().add(scrollPane, BorderLayout.NORTH);
        resultWindow.getContentPane().add(confirmBtn, BorderLayout.SOUTH);
        resultWindow.pack();
        resultWindow.setVisible(true);
        resultWindow.setLocationRelativeTo(null);
    }
}
