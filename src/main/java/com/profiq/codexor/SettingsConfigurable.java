package com.profiq.docgen;


import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class SettingsConfigurable implements Configurable {
    public static String API_KEY_SETTING_KEY = "docgen.apiKey";
    public static String PROMPT_SETTINGS_KEY = "docgen.prompt";
    public static String PROMPT_DEFAULT = "Generate high-quality docstring for the following Python function: ";

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

        Dimension labelDimension = new Dimension(60, 30);

        var apiKeyLabel = new JLabel();
        apiKeyLabel.setText("API Key: ");
        apiKeyLabel.setPreferredSize(labelDimension);
        apiKeyField = new JBTextField();
        apiKeyField.setText(currentApiKey);
        apiKeyField.setSize(250, 30);
        apiKeyField.setPreferredSize(new Dimension(600, 30));
        apiKeyField.setToolTipText("You can get your API key from https://openai.com/");


        var promptLabel = new JLabel();
        promptLabel.setText("Prompt: ");
        promptLabel.setPreferredSize(labelDimension);
        promptField = new JBTextArea();
        promptField.setText(currentPrompt);
        promptField.setSize(250, 30);
        promptField.setPreferredSize(new Dimension(600, 120));
        promptField.setLineWrap(false);
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
        return "Docgen";
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
