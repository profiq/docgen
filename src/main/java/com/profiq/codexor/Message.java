package com.profiq.codexor;

public class Message {

    public String role = "user";
    private String content;

    public Message(String content) {
        this.content = content;
    }

    String getContent() {
        return content;
    }
}
