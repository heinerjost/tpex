/* Copyright 2025 Heiner Jostkleigrewe

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package de.jostnet.tpex.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.jostnet.tpex.events.InfoEventData;
import de.jostnet.tpex.events.InfoEventListener;
import de.jostnet.tpex.events.InfoEventType;
import lombok.Getter;
import lombok.Setter;

public class UnzipService extends Thread {

    @Getter
    @Setter
    private MessageService messageService;

    @Getter
    @Setter
    private String zip;

    @Getter
    @Setter
    private String work;

    private long counterZipfiles;
    private long counterFiles;

    private long sizeUnzipped;

    private long startEventTime;
    private long lastEventTime;

    public UnzipService() {
        //
    }

    public void run() {
        if (work.isEmpty()) {
            fireEvent(new InfoEventData(InfoEventType.ERROR, messageService.getMessage("error.workfolder.notempty")));
            return;
        }
        if (zip.isEmpty()) {
            fireEvent(new InfoEventData(InfoEventType.ERROR, messageService.getMessage("error.zipfolder.notempty")));
            return;
        }
        if (ToolService.arePathsMutuallyContained(new File(zip).toPath(), new File(work).toPath())) {
            fireEvent(new InfoEventData(InfoEventType.ERROR,
                    messageService.getMessage("error.zipfolder.workfolder.mutuallycontained")));
            return;
        }
        counterFiles = 0;
        counterZipfiles = 0;
        sizeUnzipped = 0;
        File src = new File(zip);
        File dest = new File(work);

        if (!src.exists() || !src.isDirectory()) {
            fireEvent(new InfoEventData(InfoEventType.ERROR,
                    messageService.getMessage("error.zipfolder.notexist") + src.getAbsolutePath()));
            return;
        }
        if (!dest.exists()) {
            dest.mkdirs();
        }

        File[] zipFiles = src.listFiles((dir, name) -> name.toLowerCase().endsWith(".zip"));
        if (zipFiles == null || zipFiles.length == 0) {
            fireEvent(new InfoEventData(InfoEventType.ERROR, messageService.getMessage("error.no.zipsfound")));
            return;
        }
        startEventTime = System.currentTimeMillis();
        lastEventTime = startEventTime;

        // Sortiere Dateien nach Namen
        Arrays.sort(zipFiles, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));

        for (File zipFile : zipFiles) {
            try {
                counterZipfiles++;
                fireEvent(new InfoEventData(InfoEventType.UNZIP_FILE_COUNT,
                        zipFiles.length + "/" + counterZipfiles + ""));
                unzip(zipFile, dest);
            } catch (Exception e) {
                fireEvent(new InfoEventData(InfoEventType.ERROR, e.getMessage()));
                e.printStackTrace();
            }
        }
        fireEvent(new InfoEventData(InfoEventType.STOPPED_UNZIP, "unzipping beendet"));
    }

    /**
     * Entpackt eine einzelne ZIP-Datei ins Zielverzeichnis.
     *
     * @param zipFile ZIP-Datei
     * @param destDir Zielverzeichnis
     * @throws IOException falls beim Entpacken Fehler auftreten
     */
    private void unzip(File zipFile, File destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                // log.info("{} ", entry.getName());
                // Ursprünglichen Pfad zerlegen, Leerzeichen in jedem Segment entfernen
                String cleanedPath = cleanPath(entry.getName());

                // Bereinigten Eintrag verwenden
                File newFile = newFile(destDir, new ZipEntry(cleanedPath));

                if (entry.isDirectory() || cleanedPath.endsWith("/")) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException(messageService.getMessage("error.create.folder") + newFile);
                    }
                } else {
                    // Elternverzeichnis sicherstellen
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException(messageService.getMessage("error.create.folder") + parent);
                    }

                    // Dateiinhalt schreiben
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            sizeUnzipped += len;
                            fos.write(buffer, 0, len);
                        }
                    }
                    counterFiles++;
                    if ((System.currentTimeMillis() - lastEventTime) > 2000) {
                        lastEventTime = System.currentTimeMillis();
                        fireEvent(new InfoEventData(InfoEventType.UNZIP_TIME,
                                ToolService.formatMillisToHHMMSS(System.currentTimeMillis() - startEventTime)));
                        fireEvent(new InfoEventData(InfoEventType.UNZIP_EXTRACTED,
                                counterFiles + ""));
                        fireEvent(new InfoEventData(InfoEventType.UNZIPPED_SIZE,
                                ToolService.formatBytes(sizeUnzipped)));
                    }

                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            fireEvent(new InfoEventData(InfoEventType.ERROR,
                    messageService.getMessage("error.unzip.file") + zipFile.getName() + ": " + e.getMessage()));
            throw e;
        }
    }

    /**
     * Entfernt führende und nachgestellte Leerzeichen in allen
     * Verzeichnisbestandteilen eines Pfades.
     * Beispiel:
     * " Ordner1 / Unterordner / Datei.txt " → "Ordner1/Unterordner/Datei.txt"
     */
    private String cleanPath(String path) {
        // Windows-ZIP-Dateien können Backslashes enthalten → einheitlich '/'
        String normalized = path.replace("\\", "/");

        // Jeden Teil des Pfads trimmen
        String[] parts = normalized.split("/");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
            parts[i] = ToolService.sanitizeFileName(parts[i]);
        }

        // Wieder zusammensetzen, doppelte Slashes vermeiden
        return String.join("/", parts);
    }

    /**
     * Sicherheitsfunktion gegen Zip Slip Attacken
     */
    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Eintrag liegt außerhalb des Zielverzeichnisses: " + zipEntry.getName());
        }

        return destFile;
    }

    private final List<InfoEventListener> listeners = new ArrayList<>();

    public void registerListener(InfoEventListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(InfoEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Benachrichtigt alle registrierten Listener über ein Event.
     */
    private void fireEvent(InfoEventData event) {
        for (InfoEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

}
