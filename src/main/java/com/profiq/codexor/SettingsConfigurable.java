package com.profiq.codexor;


import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class SettingsConfigurable implements Configurable {
    public static String API_KEY_SETTING_KEY = "codexor.apiKey";
    public static String PROMPT_SETTINGS_KEY = "codexor.prompt";
    public static String PROMPT_DEFAULT = "Generate high-quality verbose documentation for this code";

    private final JTextField apiKeyField;
    private final JTextArea promptField;
    private final JPanel ui;
    private String currentApiKey;
    private String currentPrompt;

    public SettingsConfigurable() {
        currentApiKey = PropertiesComponent.getInstance().getValue(API_KEY_SETTING_KEY);
        currentPrompt = PropertiesComponent.getInstance().getValue(PROMPT_SETTINGS_KEY);

        if(currentPrompt == null) {
            currentPrompt = PROMPT_DEFAULT;
            PropertiesComponent.getInstance().setValue(PROMPT_SETTINGS_KEY, currentPrompt);
        }

        var apiKeyLabel = new JLabel();
        apiKeyLabel.setText("API Key: ");
        apiKeyLabel.setPreferredSize(new Dimension(80, 30));
        apiKeyField = new JTextField();
        apiKeyField.setText(currentApiKey);
        apiKeyField.setSize(250, 30);
        apiKeyField.setPreferredSize(new Dimension(400, 30));

        var promptLabel = new JLabel();
        promptLabel.setText("Prompt: ");
        promptLabel.setPreferredSize(new Dimension(80, 30));
        promptField = new JTextArea();
        promptField.setText(currentPrompt);
        promptField.setSize(250, 30);
        promptField.setPreferredSize(new Dimension(400, 120));

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
        ui.add(promptLabel, c);

        c.gridx = 1;
        ui.add(promptField, c);

        c.gridy = 2;
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
        return !apiKeyField.getText().equals(currentApiKey) || !promptField.getText().equals(currentPrompt);
    }

    @Override
    public void apply() {
        currentApiKey = apiKeyField.getText();
        currentPrompt = promptField.getText();

        if (currentApiKey.length() == 0)
            currentApiKey = null;

        PropertiesComponent.getInstance().setValue(API_KEY_SETTING_KEY, currentApiKey);
        PropertiesComponent.getInstance().setValue(PROMPT_SETTINGS_KEY, currentPrompt);
    }

    @Override
    public void reset() {
        apiKeyField.setText(currentApiKey);
        promptField.setText(currentPrompt);
    }
}
