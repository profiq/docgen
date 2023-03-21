Codexor
==========================

This IntelliJ plugin allows you to generate code documentation using OpenAI's Codex.

Features
--------

*   Generate code documentation using OpenAI's `code-davinci-edit-001` model
*   Supports multiple programming languages
*   Option to use a custom prompt

Installation
------------

1.  Open IntelliJ IDEA
2.  Go to `Preferences`
3.  Select `Plugins`
4.  Click on `Marketplace` and search for `Codexor`
5.  Install the plugin and restart IntelliJ IDEA

Configuration
-------------

1.  To set your OpenAI API key, go to `Preferences > Tools  > Codexor`
2.  Enter your API key in the provided input field and click 'Apply'
3.  Set your keyboard shortcut: go to `Preferences > Keymap` and look for `Plugins > Codexor > Codexor - Generate Documentation`

Usage
-----

1.  Select the code you want to generate documentation for
2.  Press the configured keyboard shortcut
3.  Wait for the OpenAI model to generate the documentation
4.  The generated documentation will be added to the selected code

Prompt
------

The default prompt is:

```css
Generate documentation for {{LANG}} code
```

where `{{LANG}}` will be replaced with the detected programming language.

To use a custom prompt, go to `Preferences > Tools > Codexor` and enter your prompt in the provided input field.

Contributing
------------

Contributions are welcome! Please open an issue or submit a pull request if you have any suggestions or improvements.
