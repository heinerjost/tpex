package de.jostnet.tpex.events;

import lombok.Getter;
import lombok.ToString;

/**
 * Repräsentiert ein Event mit übergebenen Daten.
 */
@ToString
public class InfoEventData {

    @Getter
    private InfoEventType type;

    @Getter
    private String value;

    public InfoEventData(InfoEventType type, String value) {
        this.type = type;
        this.value = value;
    }

}