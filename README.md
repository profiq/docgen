Docgen
==========================

This IntelliJ plugin allows you to generate Python function documentation using OpenAI's ChatGPT.  
You can download the plugin from the JetBrains MarketPlace - [Docgen](https://plugins.jetbrains.com/plugin/21294-docgen)

Features
--------

*   Generate code documentation using OpenAI's models
*   Option to use a custom prompt

Installation
------------

1.  Open IntelliJ IDEA
2.  Make sure that the [Python plugin](https://plugins.jetbrains.com/plugin/7322-python-community-edition) is installed
3.  Go to `Preferences`
4.  Select `Plugins`
5.  Click on `Marketplace` and search for `Docgen`
6.  Install the plugin and restart IntelliJ IDEA

Configuration
-------------

1.  To set your OpenAI API key, go to `Preferences > Tools  > Docgen`
2.  Enter your API key in the provided input field and click 'Apply'
3.  Set your keyboard shortcut: go to `Preferences > Keymap` and look for `Plugins > Docgen > Generate Docstring`

Usage
-----

1.  Right-click on the function you want to generate documentation for
2.  Choose `Generate Docstring` from the context menu
3.  Wait for the OpenAI model to generate the documentation
4.  The generated documentation will be displayed in a popup window
5.  Click `Insert` to add the documentation to your Python function

Model
-----

The default model is `gpt-3.5-turbo`. To use another model go to `Preferences > Tools > Docgen` and
enter the model name in the provided input field.

Prompt
------

The default prompt is:

```css
Generate high-quality docstring for the following Python function including function signature:
```

To use a custom prompt, go to `Preferences > Tools > Docgen` and enter your prompt in the provided input field.

Development environment
-----------------------

We recommend using IntelliJ IDEA for development. After installing IDEA you can simply open the project folder.

You also need JDK 17. If IDEA can't find this JDK version on your machine it should
suggest downloading it for you after opening any `.java` file. You can also go to `File > New > Project > JDK > Add SDK` 
and download the correct JDK version manually.

To test the plugin you go to `Run > Run...` and select the `Run Plugin` configuration. This will start a new isolated IDEA
instance with Docgen already installed.

To create a ZIP file for distributing the plugin first open the Gradle tool window by clicking `View > Tool Windows > Gradle`.
Then select `Tasks > intelij > buildPlugin`. If the Gradle tool window is empty click the "reload" button. It will force
IDEA to download all missing tools.

Contributing
------------

Contributions are welcome! Please open an issue or submit a pull request if you have any suggestions or improvements.
