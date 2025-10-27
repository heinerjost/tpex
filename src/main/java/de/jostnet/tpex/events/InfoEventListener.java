package de.jostnet.tpex.events;

/**
 * Listener-Interface: Wird von allen Klassen implementiert,
 * die auf Events reagieren möchten.
 */
public interface InfoEventListener {
    void onEvent(InfoEventData event);
}
