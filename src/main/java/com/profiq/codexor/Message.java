package com.profiq.codexor;

public class Message {

    private String role;
    private String content;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    String getContent() {
        return content;
    }
}
