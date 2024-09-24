package com.profiq.codexor;


import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class SettingsConfigurable implements Configurable {
    public static String ENDPOINT_SETTINGS_KEY = "codexor.endpoint";
    public static String API_KEY_SETTING_KEY = "codexor.apiKey";
    public static String MODEL_SETTINGS_KEY = "codexor.model";
    public static String MODEL_DEFAULT = "gpt-4o";
    public static String PROMPT_SETTINGS_KEY = "codexor.prompt";
    public static String PROMPT_DEFAULT = "Generate high-quality docstring for the following Python class or function.  Use the reStructuredText docstring format. Only return the docstring for the top-level element:";

    private final JBTextField endpointField;
    private final JBTextField apiKeyField;
    private final JBTextField modelField;
    private final JBTextArea promptField;
    private final JPanel ui;
    private String currentEndpoint;
    private String currentApiKey;
    private String currentModel;
    private String currentPrompt;

    public SettingsConfigurable() {
        currentEndpoint = PropertiesComponent.getInstance().getValue(ENDPOINT_SETTINGS_KEY);
        currentApiKey = PropertiesComponent.getInstance().getValue(API_KEY_SETTING_KEY);
        currentModel = PropertiesComponent.getInstance().getValue(MODEL_SETTINGS_KEY);
        currentPrompt = PropertiesComponent.getInstance().getValue(PROMPT_SETTINGS_KEY);

        if (currentModel == null) {
            currentModel = MODEL_DEFAULT;
            PropertiesComponent.getInstance().setValue(MODEL_SETTINGS_KEY, currentModel);
        }

        var maximumSize = new Dimension(600, 120);

        if (currentPrompt == null) {
            currentPrompt = PROMPT_DEFAULT;
            PropertiesComponent.getInstance().setValue(PROMPT_SETTINGS_KEY, currentPrompt);
        }

        endpointField = new JBTextField();
        endpointField.setText(currentEndpoint);

        apiKeyField = new JBTextField();
        apiKeyField.setText(currentApiKey);

        modelField = new JBTextField();
        modelField.setText(currentModel);

        promptField = new JBTextArea();
        promptField.setText(currentPrompt);
        promptField.setLineWrap(true);
        promptField.setWrapStyleWord(true);
        promptField.setMaximumSize(maximumSize);
        promptField.setMinimumSize(new Dimension(270, 55));
        promptField.setRows(5);

        var instructions = new JBTextArea();
        instructions.setText("* Leave empty to use the default OpenAI API.");
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        instructions.setBackground(null);
        instructions.setEditable(false);
        instructions.setMaximumSize(maximumSize);
        var font = instructions.getFont();
        instructions.setFont(new Font(font.getName(), Font.ITALIC, font.getSize() - 2));

        ui = FormBuilder.createFormBuilder()
            .addLabeledComponent("OpenAI/Azure API Key:", apiKeyField)
            .addLabeledComponent("Model/azure deployment:", modelField)
            .addLabeledComponent("Azure endpoint*:", endpointField)
            .addLabeledComponent("Prompt: ", promptField, 50, true)
            .addComponentFillVertically(instructions, 50)
            .getPanel();
    }

    @Override
    public @Nullable JComponent createComponent() {
        return ui;
    }

    @Override
    public String getDisplayName() {
        return "Docgen";
    }

    @Override
    public boolean isModified() {
        return !apiKeyField.getText().equals(currentApiKey)
            || !promptField.getText().equals(currentPrompt)
            || !modelField.getText().equals(currentModel)
            || !endpointField.getText().equals(currentEndpoint);
    }

    @Override
    public void apply() {
        currentApiKey = apiKeyField.getText();
        currentModel = modelField.getText();
        currentPrompt = promptField.getText();
        currentEndpoint = endpointField.getText();

        if (currentEndpoint.isEmpty())
            currentEndpoint = null;

        if (currentApiKey.isEmpty())
            currentApiKey = null;

        if (currentModel.isEmpty())
            currentModel = null;

        PropertiesComponent.getInstance().setValue(ENDPOINT_SETTINGS_KEY, currentEndpoint);
        PropertiesComponent.getInstance().setValue(API_KEY_SETTING_KEY, currentApiKey);
        PropertiesComponent.getInstance().setValue(MODEL_SETTINGS_KEY, currentModel);
        PropertiesComponent.getInstance().setValue(PROMPT_SETTINGS_KEY, currentPrompt);
    }

    @Override
    public void reset() {
        apiKeyField.setText(currentApiKey);
        modelField.setText(currentModel);
        promptField.setText(currentPrompt);
        endpointField.setText(currentEndpoint);
    }
}
