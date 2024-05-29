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
    public static String MODEL_DEFAULT = "gpt-3.5-turbo";
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
        promptField.setMaximumSize(new Dimension(600, 120));
        promptField.setMinimumSize(new Dimension(270, 55));
        promptField.setRows(5);

        var instructions = new JBTextArea();
        instructions.setText(
            "* Leave the endpoint empty to use the default OpenAI API. If you want to use Azure Open AI, set put " +
                "your endpoint URL here.");
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        instructions.setBackground(null);
        instructions.setEditable(false);
        var font = instructions.getFont();
        instructions.setFont(new Font(font.getName(), Font.ITALIC, font.getSize() - 2));


        ui = FormBuilder.createFormBuilder()
            .addLabeledComponent("Open AI API Endpoint*: ", endpointField)
            .addLabeledComponent("OpenAI/Azure API Key: ", apiKeyField)
            .addLabeledComponent("Model: ", modelField)
            .addLabeledComponent("Prompt: ", promptField, 50, true)
            .addComponentFillVertically(instructions, 50)
            .getPanel();

        /*

        Dimension labelDimension = new Dimension(200, 30);

        var endpointLabel = new JLabel();
        endpointLabel.setText("Open AI API Endpoint: ");
        endpointLabel.setPreferredSize(labelDimension);
        endpointLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        endpointField = new JBTextField();
        endpointField.setText("");
        endpointField.setPreferredSize(new Dimension(600, 30));
        endpointField.setToolTipText("You can set the Open AI API endpoint here. For example, you can set set it to your Azure Open AI deployment endpoint. Leave it empty to use the default OpenAI API endpoint.");

        var apiKeyLabel = new JLabel();
        apiKeyLabel.setText("OpenAI/Azure API Key: ");
        apiKeyLabel.setPreferredSize(labelDimension);
        apiKeyField = new JBTextField();
        apiKeyField.setText(currentApiKey);
        apiKeyField.setSize(250, 30);
        apiKeyField.setPreferredSize(new Dimension(600, 30));
        apiKeyField.setToolTipText("You can get your API key from https://openai.com/.");

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
        var layout = new BoxLayout(ui, BoxLayout.Y_AXIS);
        ui.setLayout(layout);
        ui.setAlignmentX(Component.LEFT_ALIGNMENT);

        ui.add(endpointLabel);
        ui.add(endpointField);
        ui.add(Box.createHorizontalGlue());

        ui.add(Box.createRigidArea(new Dimension(0, 10)));
        ui.add(apiKeyLabel);
        ui.add(apiKeyField);

        ui.add(Box.createRigidArea(new Dimension(0, 10)));
        ui.add(modelLabel);
        ui.add(modelField);

        ui.add(Box.createRigidArea(new Dimension(0, 10)));
        ui.add(promptLabel);
        ui.add(promptField);

        ui.add(Box.createGlue());
        var filler = new JPanel();
        filler.setPreferredSize(JBUI.size(0, 0));
        ui.add(filler);
        */
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
