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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExportService {

    // Globale Menge, um bereits kopierte Fotos zu merken
    private static final Set<String> processedPhotos = new HashSet<>();

    private static Integer oldest = null;
    private static Integer newest = null;

    private static ArrayList<String> folderToSkip = new ArrayList<>();

    // private static final List<String> FILES_TO_SKIP =
    // Arrays.asList("print-subscriptions.json", "Metadaten.json",
    // "shared_album_comments.json", "user-generated-memory-titles.json");

    public void export(CliOptions options) throws IOException, ImageReadException, ImageWriteException, ParseException {
        Path inputRoot = Paths.get(options.getInput());
        Path outputRoot = Paths.get(options.getOutput());

        // Ordner zum Überspringen einlesen
        if (options.getFolderstoskip() != null && !options.getFolderstoskip().isEmpty()) {
            readFolderToSkip(options.getFolderstoskip());
        }

        // sicherstellen, dass outputRoot existiert
        if (!Files.exists(outputRoot)) {
            Files.createDirectories(outputRoot);
        }

        // 1) Alle JSON-Dateien sammeln (Stream wird automatisch geschlossen)
        List<Path> allJsonFiles;
        try (Stream<Path> paths = Files.walk(inputRoot)) {
            allJsonFiles = paths
                    .filter(p -> !p.toString().startsWith("."))
                    .filter(p -> p.toString().endsWith(".json"))
                    .collect(Collectors.toList());
        }

        // 2) Eindeutige Eltern-Ordner der JSONs sammeln (z. B. pro Album / Photos from
        // ...)
        List<Path> parentFolders = allJsonFiles.stream()
                .map(Path::getParent)
                .distinct()
                .collect(Collectors.toList());

        // 3) Sortieren: Alben (nicht "Photos from ...") zuerst
        parentFolders.sort((f1, f2) -> {
            String folder1 = f1.getFileName().toString();
            String folder2 = f2.getFileName().toString();
            boolean isAlbum1 = !folder1.startsWith("Photos from ");
            boolean isAlbum2 = !folder2.startsWith("Photos from ");
            // true (Album) soll vor false (Photos from ...) kommen
            return Boolean.compare(!isAlbum1, !isAlbum2);
        });

        // 4) Ordnerweise verarbeiten -> nach jedem Ordner normalizeYearFolder aufrufen
        for (Path folder : parentFolders) {
            // alle JSON-Dateien in diesem Ordner
            List<Path> jsonsInFolder = allJsonFiles.stream()
                    .filter(p -> Objects.equals(p.getParent(), folder))
                    .filter(p -> !p.getFileName().toString().startsWith("."))
                    .collect(Collectors.toList());

            if (folderToSkip.contains(folder.getFileName().toString())) {
                log.info("⏭️ Überspringe Ordner (in skip-Liste): {}", folder.getFileName());
                continue;
            }

            oldest = null;
            newest = null;
            for (Path jsonFile : jsonsInFolder) {
                try {
                    processJsonFile(jsonFile, inputRoot, outputRoot);
                } catch (Exception e) {
                    log.error("Fehler beim Verarbeiten von {}: {}", jsonFile, e.getMessage());
                    log.error("Stacktrace:", e);
                }
            }

            // Nach kompletter Bearbeitung dieses Eingabe-Ordners das entsprechende
            // Ausgabeverzeichnis prüfen
            // Hinweis: processJsonFile sollte die gleiche Logik verwenden, um das
            // Zielverzeichnis zu bestimmen.
            // Hier benutze ich folder.getFileName(), passe das an, falls du tiefere
            // relative Pfade benutzt.
            Path outputDir = outputRoot.resolve(folder.getFileName());
            normalizeYearFolder(outputDir);
        }

    }

    private static void processJsonFile(Path jsonFile, Path inputRoot, Path outputRoot)
            throws IOException, ImageReadException, ImageWriteException, ParseException {

        Path folder = jsonFile.getParent();

        Map<String, Path> shortenedFiles;
        try (Stream<Path> stream = Files.list(folder)) {
            shortenedFiles = stream
                    .filter(f -> !f.toString().endsWith(".json"))
                    .filter(f -> !f.getFileName().toString().startsWith("."))
                    .collect(Collectors.toMap(f -> f.getFileName().toString(), f -> f));
        }

        String content = null;
        try {
            content = Files.readString(jsonFile);
        } catch (IOException e) {
            log.error("Fehler beim Lesen von {}: {}", jsonFile, e.getMessage());
            return;
        }

        JsonObject obj = JsonParser.parseString(content).getAsJsonObject();

        String originalName = getAsStringSafe(obj, "title");
        if (originalName == null)
            return;

        String editedName = addEditedSuffix(originalName);

        Path match = shortenedFiles.get(editedName);
        if (match == null) {
            match = shortenedFiles.get(originalName);
        }
        if (match == null) {
            match = findBestMatch(originalName, shortenedFiles.keySet(), folder);
            if (match == null)
                match = findBestMatch(editedName, shortenedFiles.keySet(), folder);
        }
        if (match == null)
            return;

        JsonObject timeObj = obj.getAsJsonObject("photoTakenTime");
        if (timeObj.has("timestamp")) {
            long ts = timeObj.get("timestamp").getAsLong() * 1000L;
            LocalDate date = Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()).toLocalDate();
            if (oldest == null || date.getYear() < oldest) {
                oldest = date.getYear();
            }
            if (newest == null || date.getYear() > newest) {
                newest = date.getYear();
            }
        }

        String key = originalName;
        if (obj.has("photoTakenTime")) {
            key += "_" + obj.getAsJsonObject("photoTakenTime").get("timestamp").getAsString();
        }

        if (jsonFile.toString().contains("Photos from ") && processedPhotos.contains(key)) {
            log.info("⏭️ Überspringe (bereits verarbeitet): {}", originalName);
            return;
        }

        Path relativePath = inputRoot.relativize(folder);
        Path outputDir = outputRoot.resolve(relativePath);
        Files.createDirectories(outputDir);

        Path target = outputDir.resolve(originalName);
        target = getUniqueTarget(target);

        if (isJpeg(match)) {
            copyAndFixExif(match, target, obj);
        } else {
            Files.copy(match, target, StandardCopyOption.REPLACE_EXISTING);
        }
        setFileTimestamp(target, obj);

        processedPhotos.add(key);
        log.info("✅ [{}] {} → {}", folder, match.getFileName(), target);
    }

    private static void copyAndFixExif(Path source, Path target, JsonObject json) throws IOException {
        byte[] imageBytes = Files.readAllBytes(source);

        TiffOutputSet outputSet = null;
        try {
            JpegImageMetadata jpegMetadata = (JpegImageMetadata) Imaging.getMetadata(source.toFile());
            if (jpegMetadata != null && jpegMetadata.getExif() != null) {
                outputSet = jpegMetadata.getExif().getOutputSet();
            }
        } catch (Exception ignored) {
        }

        if (outputSet == null)
            outputSet = new TiffOutputSet();

        applyGeoAndDate(json, outputSet);

        boolean written = false;
        try (FileOutputStream fos = new FileOutputStream(target.toFile())) {
            new ExifRewriter().updateExifMetadataLossless(imageBytes, fos, outputSet);
            written = true;
        } catch (Exception e) {
            log.warn("⚠️ Lossless fehlgeschlagen für {} ({}), versuche lossy…",
                    source.getFileName(), e.getMessage());
        }

        if (!written) {
            try (FileOutputStream fos = new FileOutputStream(target.toFile())) {
                new ExifRewriter().updateExifMetadataLossy(imageBytes, fos, outputSet);
                written = true;
            } catch (Exception e) {
                log.error("❌ Lossy ebenfalls fehlgeschlagen für {} ({}), kopiere Original",
                        source.getFileName(), e.getMessage());
            }
        }

        if (!written) {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void applyGeoAndDate(JsonObject json, TiffOutputSet outputSet) {
        try {
            Double lat = null, lon = null, alt = null;
            if (json.has("geoData")) {
                JsonObject geo = json.getAsJsonObject("geoData");
                if (geo.has("latitude"))
                    lat = geo.get("latitude").getAsDouble();
                if (geo.has("longitude"))
                    lon = geo.get("longitude").getAsDouble();
                if (geo.has("altitude"))
                    alt = geo.get("altitude").getAsDouble();
            }

            if (lat != null && lon != null) {
                TiffOutputDirectory gpsDir = outputSet.getOrCreateGPSDirectory();
                gpsDir.removeField(GpsTagConstants.GPS_TAG_GPS_LATITUDE);
                gpsDir.removeField(GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
                gpsDir.removeField(GpsTagConstants.GPS_TAG_GPS_LONGITUDE);
                gpsDir.removeField(GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
                gpsDir.removeField(GpsTagConstants.GPS_TAG_GPS_ALTITUDE);
                gpsDir.removeField(GpsTagConstants.GPS_TAG_GPS_ALTITUDE_REF);

                gpsDir.add(GpsTagConstants.GPS_TAG_GPS_LATITUDE, toRationalGPS(lat));
                gpsDir.add(GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF, lat >= 0 ? "N" : "S");

                gpsDir.add(GpsTagConstants.GPS_TAG_GPS_LONGITUDE, toRationalGPS(lon));
                gpsDir.add(GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF, lon >= 0 ? "E" : "W");

                if (alt != null) {
                    gpsDir.add(GpsTagConstants.GPS_TAG_GPS_ALTITUDE,
                            new RationalNumber(Math.abs((int) (alt * 1000)), 1000));
                    gpsDir.add(GpsTagConstants.GPS_TAG_GPS_ALTITUDE_REF,
                            (alt >= 0) ? (byte) 0 : (byte) 1);
                }
            }

            if (json.has("photoTakenTime")) {
                JsonObject timeObj = json.getAsJsonObject("photoTakenTime");
                if (timeObj.has("timestamp")) {
                    long ts = timeObj.get("timestamp").getAsLong() * 1000L;
                    String exifDate = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
                            .format(new Date(ts));

                    TiffOutputDirectory exifDir = outputSet.getOrCreateExifDirectory();
                    exifDir.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
                    exifDir.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED);

                    exifDir.add(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, exifDate);
                    exifDir.add(ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED, exifDate);
                }
            }
        } catch (Exception e) {
            log.error("⚠️ Fehler beim Setzen von Geo/Date: {}", e.getMessage(), e);
        }
    }

    private static RationalNumber[] toRationalGPS(double decimalDegree) {
        decimalDegree = Math.abs(decimalDegree);
        int degrees = (int) decimalDegree;
        double minutesDecimal = (decimalDegree - degrees) * 60;
        int minutes = (int) minutesDecimal;
        double secondsDecimal = (minutesDecimal - minutes) * 60;

        return new RationalNumber[] {
                new RationalNumber(degrees, 1),
                new RationalNumber(minutes, 1),
                new RationalNumber((int) (secondsDecimal * 10000), 10000)
        };
    }

    private static Path findBestMatch(String expectedName, Set<String> shortenedNames, Path folder) {
        if (expectedName == null)
            return null;
        for (String shortName : shortenedNames) {
            if (shortName.contains("…")) {
                String prefix = shortName.substring(0, shortName.indexOf("…"));
                String suffix = shortName.substring(shortName.indexOf("…") + 1);
                if (expectedName.startsWith(prefix) && expectedName.endsWith(suffix)) {
                    return folder.resolve(shortName);
                }
            }
        }
        return null;
    }

    private static Path getUniqueTarget(Path target) {
        if (!Files.exists(target))
            return target;
        String fileName = target.getFileName().toString();
        String baseName;
        String extension = "";
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex >= 0) {
            baseName = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        } else
            baseName = fileName;

        int counter = 1;
        Path newTarget;
        do {
            newTarget = target.getParent().resolve(baseName + "_" + counter + extension);
            counter++;
        } while (Files.exists(newTarget));

        return newTarget;
    }

    private static String addEditedSuffix(String fileName) {
        if (fileName == null)
            return null;
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex >= 0)
            return fileName.substring(0, dotIndex) + "-bearbeitet" + fileName.substring(dotIndex);
        else
            return fileName + "-bearbeitet";
    }

    private static boolean isJpeg(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg");
    }

    private static String getAsStringSafe(JsonObject obj, String memberName) {
        if (!obj.has(memberName))
            return null;
        JsonElement el = obj.get(memberName);
        if (el.isJsonNull())
            return null;
        if (el.isJsonPrimitive())
            return el.getAsString();
        if (el.isJsonArray()) {
            JsonArray arr = el.getAsJsonArray();
            if (arr.size() > 0 && arr.get(0).isJsonPrimitive()) {
                return arr.get(0).getAsString();
            }
        }
        return null;
    }

    private static void setFileTimestamp(Path path, JsonObject json)
            throws IOException, ParseException {

        if (json.has("photoTakenTime")) {
            JsonObject timeObj = json.getAsJsonObject("photoTakenTime");
            if (timeObj.has("timestamp")) {
                long ts = timeObj.get("timestamp").getAsLong() * 1000L;
                FileTime time = FileTime.fromMillis(ts);
                Files.setLastModifiedTime(path, time);
            }
        }
    }

    private static void normalizeYearFolder(Path outputDir) {
        if (outputDir == null || !Files.isDirectory(outputDir))
            return;

        if (oldest == null || newest == null || !oldest.equals(newest)) {
            log.info("ℹ️ {} enthält Fotos aus mehreren Jahren → nicht verschoben", outputDir.getFileName());
            return;
        }

        Path parent = outputDir.getParent();
        if (parent == null)
            return;

        Path yearFolder = parent.resolve("Aufnahmen " + oldest);

        try {
            // Stelle sicher, dass der Jahr-Ordner existiert
            if (!Files.exists(yearFolder)) {
                Files.createDirectories(yearFolder);
            }

            // Ziel: yearFolder/<sourceFolderName> (damit wir das Quellverzeichnis als
            // Unterordner haben)
            Path targetSubdir = yearFolder.resolve(outputDir.getFileName());

            // Falls bereits vorhanden -> eindeutigen Namen finden
            if (Files.exists(targetSubdir)) {
                int counter = 1;
                Path candidate;
                do {
                    candidate = yearFolder.resolve(outputDir.getFileName().toString() + "_" + counter);
                    counter++;
                } while (Files.exists(candidate));
                targetSubdir = candidate;
            }

            // Verschiebe das gesamte Verzeichnis in targetSubdir (move oder Copy+Delete
            // fallback)
            moveDirectory(outputDir, targetSubdir);
            log.info("📂 Ordner {} → {} verschoben", outputDir.getFileName(), yearFolder.getFileName());
        } catch (IOException e) {
            log.error("⚠️ Verschieben fehlgeschlagen: {} → {} ({})", outputDir, yearFolder, e.getMessage());
        }
    }

    /**
     * Versuche ein Verzeichnis zu verschieben; falls move fehlschlägt, mache
     * copy+delete fallback
     */
    private static void moveDirectory(Path source, Path target) throws IOException {
        try {
            // Versuch: verschiebe source zu target (target ist das gewünschte
            // Unterverzeichnis)
            Files.move(source, target);
        } catch (IOException e) {
            // Fallback: rekursiv kopieren und anschließend das Original löschen
            copyDirectoryRecursively(source, target);
            deleteDirectoryRecursively(source);
        }
    }

    private static void copyDirectoryRecursively(Path src, Path dst) throws IOException {
        Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path rel = src.relativize(dir);
                Path targetDir = dst.resolve(rel);
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path rel = src.relativize(file);
                Path targetFile = dst.resolve(rel);
                Files.copy(file, targetFile, StandardCopyOption.COPY_ATTRIBUTES);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void deleteDirectoryRecursively(Path dir) throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path d, IOException exc) throws IOException {
                Files.delete(d);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void readFolderToSkip(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                folderToSkip.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
