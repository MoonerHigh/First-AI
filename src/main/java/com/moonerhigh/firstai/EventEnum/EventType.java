package com.moonerhigh.firstai.EventEnum;

public enum EventType {

    START("start"),
    MESSAGE("message"),
    DONE("done"),
    ERROR("error");

    private final String eventName;

    EventType(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }
}
