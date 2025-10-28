# tpex - Takeout Photo Exporter

Ich habe eine umfangreiche Bildersammlung bei Google Photos gespeichert und möchte diese auf einem lokalen Laufwerk sichern.

Leider bietet Google hierfür nur das Takeout-Format an. Dabei werden die Fotos in Ordnern pro Album und zusätzlich in Ordnern pro Jahr abgelegt. Einige von mir nachträglich erfasste Metadaten (z. B. Aufnahmeort oder das Aufnahmedatum bei nachträglich digitalisierten Bildern) sind nicht in den EXIF-Daten abgelegt, sondern werden als JSON-Datei geliefert. Insgesamt eine unübersichtliche Struktur.

Deshalb habe ich mir  [Google Photos Takeouthelper](https://github.com/TheLastGimbus/GooglePhotosTakeoutHelper) angesehen. Von dem Ergebnis war ich aber auch nicht überzeugt. Daher habe ich mir den Takeout Photo Exporter gebaut.

## Leistungsmerkmale
- Komfortable Oberfläche
- Mehrsprachigkeit. Zur Zeit englisch, deutsch und spanisch.
- Die Takeout-ZIP-Dateien müssen nicht manuell entpackt werden.
- Alle in der Exportdatei enthaltenen Dateitypen werden berücksichtigt.
- Die Alben bleiben als Ordner erhalten.
- Alben, die nur Fotos aus einem Jahr enthalten, werden in einen Ordner „Aufnahmen yyyy“ verschoben. Andere bleiben im Hauptverzeichnis.
- Fotos aus den Ordnern „Photos from yyyy“ werden nur berücksichtigt, wenn sie nicht in einem Album enthalten sind. Hierdurch werden Duplikate vermieden und es wird mehr Übersichtlichkeit geschaffen.
- Für JPEG- und JPG-Dateien:
  - Aufnahmeort wird aus der JSON-Datei in die EXIF-Daten des Bildes übernommen.
  - Das Aufnahmedatum wird in die EXIF-Daten übernommen. Zusätzlich wird das Dateidatum auf das Aufnahmedatum gesetzt. Hinweis: Beim EXFAT-Dateisystem bekommen alte Bilder, die vor 1980 aufgenommen wurden, das Datum 1.1.1980.
- Die ursprünglichen Daten aus dem Zip-Verzeichnis und dem Work-Verzeichnis bleiben unberührt. Es wird in das Export-Verzeichnis exportiert.
- Das Programm läuft unter macOS, Linux und Windows.

## Download 
Der Download kann bei [github](https://github.com/heinerjost/tpex/tags) erfolgen.

## Voraussetzung für den Ablauf
Es muss eine Java-Laufzeitumgebung >= 21 vorhanden sein. Z. B. [OpenJDK25](https://jdk.java.net/25/)

## Vorbereitung
Die Takeout-Daten müssen bei Google angefordert werden. Dafür steht eine Schaltfläche zur Verfügung. Alternativ kann der Link [Google-Takeout](https://takeout.google.com) aufgerufen werden. Der Export dauert einige Minuten oder Stunden. Google informiert üper Mail über die Bereitstellung der Export-Daten. Die Daten müssen in einem Verzeichnis gespeichert werden. In dem Verzeichnis dürfen keine anderen Dateien gespeichert sein.

## Aufruf des Programmes
java -jar tpex-0.9.2.jar -z zip-verzeichnis -i input-verzeichnis -o output-verzeichnis -s datei -l de

