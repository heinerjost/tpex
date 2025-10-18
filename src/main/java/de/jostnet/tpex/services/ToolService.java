package de.jostnet.tpex.services;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.stereotype.Service;

@Service
public class ToolService {
    /**
     * Ersetzt alle in gängigen Dateisystemen (Windows, macOS, Linux) unzulässigen
     * oder potenziell problematischen Zeichen durch Unterstrich '_'.
     *
     * - Verbietet alle Windows-reservierten Zeichen: < > : " / \ | ? *
     * - Verbietet Steuerzeichen (ASCII 0–31)
     * - Verbietet auch UNIX-reservierte / und NULL (\0)
     * - Entfernt führende und endende Punkte oder Leerzeichen
     * - Beschränkt Länge auf 255 Zeichen (max. bei den meisten Dateisystemen)
     */
    public String sanitizeFileName(String input) {
        if (input == null || input.isEmpty()) {
            return "_";
        }

        // 1️⃣ Entferne Steuerzeichen (ASCII < 32)
        String sanitized = input.replaceAll("[\\p{Cntrl}]", "_");

        // 2️⃣ Ersetze alle Zeichen, die in Windows unzulässig sind oder in anderen FS
        // potenziell stören
        sanitized = sanitized.replaceAll("[<>:\"/\\\\|?*]", "_");

        // 3️⃣ Ersetze NULL-Zeichen (vorsorglich)
        sanitized = sanitized.replace("\0", "_");

        // 4️⃣ Entferne führende und endende Punkte/Leerzeichen
        sanitized = sanitized.trim().replaceAll("^[. ]+|[. ]+$", "");

        // 5️⃣ Falls leer nach Bereinigung: Fallback
        if (sanitized.isEmpty()) {
            sanitized = "_";
        }

        // 6️⃣ Maximale Länge (meist 255)
        if (sanitized.length() > 255) {
            sanitized = sanitized.substring(0, 255);
        }

        return sanitized;
    }

    public String formatLongLocalized(long value) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
        return formatter.format(value);
    }

    public String formatBytes(long bytes) {
        final long KB = 1024;
        final long MB = KB * 1024;
        final long GB = MB * 1024;
        final long TB = GB * 1024;

        if (bytes < KB) {
            return bytes + " B";
        } else if (bytes < MB) {
            return String.format("%.2f KB", bytes / (double) KB);
        } else if (bytes < GB) {
            return String.format("%.2f MB", bytes / (double) MB);
        } else if (bytes < TB) {
            return String.format("%.2f GB", bytes / (double) GB);
        } else {
            return String.format("%.2f TB", bytes / (double) TB);
        }
    }

}
