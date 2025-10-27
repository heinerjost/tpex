package de.jostnet.tpex.services;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageService {

    private ResourceBundle messages;

    public MessageService() {
    }

    public void setLocale(String land) {
        Locale locale = Locale.forLanguageTag(land);
        messages = loadBundleWithFallback("lang/messages", locale);
    }

    public String getMessage(String key) {
        return messages.getString(key);
    }

    private ResourceBundle loadBundleWithFallback(String baseName, Locale locale) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
            return bundle;
        } catch (Exception e) {
            System.err.println("⚠️  Fehler beim Laden des Bundles für '" + locale + "': " + e.getMessage());
            System.err.println("⚠️  Keine Übersetzung für '" + locale + "' gefunden. Fallback auf Englisch.");
            ResourceBundle englishBundle = ResourceBundle.getBundle(baseName, Locale.ENGLISH);

            // Wenn Englisch identisch mit dem Standard ist → endgültiger Fallback
            if (englishBundle.getLocale().getLanguage().isEmpty()) {
                System.err.println("⚠️  Kein englisches Bundle vorhanden. Verwende Standard-Resource.");
                return ResourceBundle.getBundle(baseName, Locale.ROOT);
            }
            return englishBundle;
        }
    }
}
