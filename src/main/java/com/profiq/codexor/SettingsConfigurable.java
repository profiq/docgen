package com.profiq.codexor;


import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class SettingsConfigurable implements Configurable {
    public static String API_KEY_SETTING_KEY = "codexor.apiKey";
    public static String MODEL_SETTINGS_KEY = "codexor.model";
    public static String MODEL_DEFAULT = "gpt-3.5-turbo";
    public static String PROMPT_SETTINGS_KEY = "codexor.prompt";
    public static String PROMPT_DEFAULT = "Generate high-quality docstring for the following Python function including function signature: ";

    private final JTextField apiKeyField;
    private final JTextField modelField;
    private final JTextArea promptField;
    private final JPanel ui;
    private String currentApiKey;
    private String currentModel;
    private String currentPrompt;

    public SettingsConfigurable() {
        currentApiKey = PropertiesComponent.getInstance().getValue(API_KEY_SETTING_KEY);
        currentModel = PropertiesComponent.getInstance().getValue(MODEL_SETTINGS_KEY);
        currentPrompt = PropertiesComponent.getInstance().getValue(PROMPT_SETTINGS_KEY);

        if(currentModel == null) {
            currentModel = MODEL_DEFAULT;
            PropertiesComponent.getInstance().setValue(MODEL_SETTINGS_KEY, currentModel);
        }

        if(currentPrompt == null) {
            currentPrompt = PROMPT_DEFAULT;
            PropertiesComponent.getInstance().setValue(PROMPT_SETTINGS_KEY, currentPrompt);
        }

        Dimension labelDimension = new Dimension(60, 30);

        var apiKeyLabel = new JLabel();
        apiKeyLabel.setText("API Key: ");
        apiKeyLabel.setPreferredSize(labelDimension);
        apiKeyField = new JBTextField();
        apiKeyField.setText(currentApiKey);
        apiKeyField.setSize(250, 30);
        apiKeyField.setPreferredSize(new Dimension(600, 30));
        apiKeyField.setToolTipText("You can get your API key from https://openai.com/");

        var modelLabel = new JLabel();
        modelLabel.setText("Model: ");
        modelLabel.setPreferredSize(labelDimension);
        modelField = new JBTextField();
        modelField.setText(currentModel);
        modelField.setSize(250, 30);
        modelField.setPreferredSize(new Dimension(600, 30));


        var promptLabel = new JLabel();
        promptLabel.setText("Prompt: ");
        promptLabel.setPreferredSize(labelDimension);
        promptField = new JBTextArea();
        promptField.setText(currentPrompt);
        promptField.setSize(250, 30);
        promptField.setPreferredSize(new Dimension(600, 120));
        promptField.setLineWrap(true);
        promptField.setWrapStyleWord(true);

        ui = new JPanel();
        ui.setLayout(new GridBagLayout());

        var c = new GridBagConstraints();
        c.insets = JBUI.insets(5);
        c.anchor = GridBagConstraints.NORTHWEST;

        c.gridx = 0;
        c.gridy = 0;
        ui.add(apiKeyLabel, c);

        c.gridx = 1;
        ui.add(apiKeyField, c);

        c.gridx = 0;
        c.gridy = 1;
        ui.add(modelLabel, c);

        c.gridx = 1;
        ui.add(modelField, c);

        c.gridx = 0;
        c.gridy = 2;
        ui.add(promptLabel, c);

        c.gridx = 1;
        ui.add(promptField, c);

        c.gridy = 3;
        var filler = new JLabel();
        filler.setPreferredSize(new Dimension(0, 200));
        ui.add(filler, c);
    }

    @Override
    public @Nullable JComponent createComponent() {
        return ui;
    }

    @Override
    public String getDisplayName() {
        return "Codexor";
    }

    @Override
    public boolean isModified() {
        return !apiKeyField.getText().equals(currentApiKey)
            || !promptField.getText().equals(currentPrompt)
            || !modelField.getText().equals(currentModel);
    }

    @Override
    public void apply() {
        currentApiKey = apiKeyField.getText();
        currentModel = modelField.getText();
        currentPrompt = promptField.getText();

        if (currentApiKey.length() == 0)
            currentApiKey = null;

        if (currentModel.length() == 0)
            currentModel = null;

        PropertiesComponent.getInstance().setValue(API_KEY_SETTING_KEY, currentApiKey);
        PropertiesComponent.getInstance().setValue(MODEL_SETTINGS_KEY, currentModel);
        PropertiesComponent.getInstance().setValue(PROMPT_SETTINGS_KEY, currentPrompt);
    }

    @Override
    public void reset() {
        apiKeyField.setText(currentApiKey);
        modelField.setText(currentModel);
        promptField.setText(currentPrompt);
    }
}
