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

package de.jostnet.GoPhoEx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.stereotype.Service;

@Service
public class UnzipService {

    /**
     * Entpackt alle ZIP-Dateien aus dem Quellverzeichnis in das Zielverzeichnis.
     *
     * @param sourceDir Pfad zum Verzeichnis mit den ZIP-Dateien
     * @param targetDir Pfad zum Zielverzeichnis
     * @throws IOException falls beim Lesen oder Schreiben Fehler auftreten
     */
    public void extractAllZips(CliOptions options) throws IOException {
        File src = new File(options.getZip());
        File dest = new File(options.getInput());

        if (!src.exists() || !src.isDirectory()) {
            throw new IllegalArgumentException(
                    "Quellverzeichnis existiert nicht oder ist kein Verzeichnis: " + src.getAbsolutePath());
        }
        if (!dest.exists()) {
            dest.mkdirs();
        }

        File[] zipFiles = src.listFiles((dir, name) -> name.toLowerCase().endsWith(".zip"));
        if (zipFiles == null || zipFiles.length == 0) {
            System.out.println("Keine ZIP-Dateien gefunden in: " + src.getAbsolutePath());
            return;
        }

        // Sortiere Dateien nach Namen
        Arrays.sort(zipFiles, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));

        for (File zipFile : zipFiles) {
            unzip(zipFile, dest);
        }
    }

    /**
     * Entpackt eine einzelne ZIP-Datei ins Zielverzeichnis.
     *
     * @param zipFile ZIP-Datei
     * @param destDir Zielverzeichnis
     * @throws IOException falls beim Entpacken Fehler auftreten
     */
    private static void unzip(File zipFile, File destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = newFile(destDir, entry);

                if (entry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Fehler beim Erstellen des Verzeichnisses: " + newFile);
                    }
                } else {
                    // Stelle sicher, dass das Elternverzeichnis existiert
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Fehler beim Erstellen des Verzeichnisses: " + parent);
                    }

                    // Datei überschreiben
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
        System.out.println("Entpackt: " + zipFile.getName());
    }

    /**
     * Sicherheitsfunktion gegen Zip Slip Attacken
     */
    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Eintrag liegt außerhalb des Zielverzeichnisses: " + zipEntry.getName());
        }

        return destFile;
    }

}
