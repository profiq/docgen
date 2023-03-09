package com.profiq.codexor;


public class Request {

    private String model;
    private float temperature;
    private Message[] messages;

    public Request(String model, Message[] messages, float temperature) {
        this.temperature = temperature;
        this.messages = messages;
        this.model = model;
    }

}
