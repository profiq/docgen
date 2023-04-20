Docgen
==========================

This IntelliJ plugin allows you to generate Python function documentation using OpenAI's ChatGPT.  
You can download the plugin from the JetBrains MarketPlace - [Docgen](https://plugins.jetbrains.com/plugin/21294-docgen)

Features
--------

*   Generate code documentation using OpenAI's `gpt-3.5-turbo` model
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
3.  Set your keyboard shortcut: go to `Preferences > Keymap` and look for `Plugins > Docgen > Generate docstring`

Usage
-----

1.  Right-click on the function you want to generate documentation for
2.  Choose `Generate docstring` from the context menu
3.  Wait for the OpenAI model to generate the documentation
4.  The generated documentation will be displayed in a popup window
5.  Click `Insert` to add the documentation to your Python function

Prompt
------

The default prompt is:

```css
Generate high-quality docstring for the following Python function including function signature:
```

To use a custom prompt, go to `Preferences > Tools > Docgen` and enter your prompt in the provided input field.

Contributing
------------

Contributions are welcome! Please open an issue or submit a pull request if you have any suggestions or improvements.
