package com.gensee.fastsdk;

public enum GenseeServiceType {

    WEBCAST("webcast"),
    TRAINING("training");

    private String value;

    private GenseeServiceType(String service) {
        this.value = service;
    }

    public String getValue() {
        return this.value;
    }
}
