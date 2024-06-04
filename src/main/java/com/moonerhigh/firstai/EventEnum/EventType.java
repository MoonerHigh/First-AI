package com.moonerhigh.firstai.EventEnum;

import lombok.Getter;

@Getter
public enum EventType {

    START("start"),
    MESSAGE("message"),
    DONE("done"),
    ERROR("error");

    private final String eventName;

    EventType(String eventName) {
        this.eventName = eventName;
    }

}
