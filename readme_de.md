![tpex Logo](./src/main/resources/images/logo640.png)
# tpex - Takeout Photo Exporter

Ich habe eine umfangreiche Bildersammlung bei Google Photos gespeichert und möchte diese auf einem lokalen Laufwerk sichern.

Leider bietet Google hierfür nur das Takeout-Format an. 

Die Struktur von Google Takeout für Fotos ist nach Jahren und Alben organisiert, wobei jede Jahreszahl einen eigenen Ordner bildet. Innerhalb dieser Ordner liegen die Fotos, die jeweils von einer separaten JSON-Datei mit Metadaten wie Aufnahmeort und -datum begleitet werden. Die heruntergeladenen Dateien werden als Zip-Archiv geliefert. 

Insgesamt eine sehr unübersichtliche Struktur.

Deshalb habe ich mir [Google Photos Takeouthelper](https://github.com/TheLastGimbus/GooglePhotosTakeoutHelper) angesehen. Von dem Ergebnis war ich aber nicht überzeugt. Daher habe ich mir den Takeout Photo Exporter gebaut.

## Leistungsmerkmale
- Komfortable Oberfläche
- Mehrsprachigkeit. Zur Zeit stehen dänisch, deutsch, englisch, finnisch, französisch, griechisch, italienisch, niederländisch, norwegisch, polnisch, portogisisch, schwedisch, spanisch und  tschechisch zur Verfügung.  Hinweis: Die Erzeugung der Sprachdateien erfolgt mit KI. Falls es Fehler gibt, bitte bei [Github](https://github.com/heinerjost/tpex/issues) melden. Weitere Sprachen sind möglich. 
- Die Takeout-ZIP-Dateien werden vom Programm entpackt. Eine manuelle Tätigkeit ist nicht erforderlich.
- Alle in der Exportdatei enthaltenen Dateitypen (jpeg, mov, mp4, heic u. a.) werden berücksichtigt.
- Die Alben bleiben als Ordner erhalten.
- Alben, die nur Fotos aus einem Jahr enthalten, werden in einen Ordner „Aufnahmen yyyy“ verschoben. Andere verbleiben im Hauptverzeichnis.
- Fotos aus den Jahresordnern „Photos from yyyy“ werden nur berücksichtigt, wenn sie nicht in einem Album enthalten sind. Hierdurch werden Duplikate vermieden und es wird mehr Übersichtlichkeit geschaffen.
- Für JPEG- und JPG-Dateien:
  - Aufnahmeort wird aus der JSON-Datei in die EXIF-Daten des Bildes übernommen.
  - Das Aufnahmedatum wird in die EXIF-Daten übernommen. Zusätzlich wird das Dateidatum auf das Aufnahmedatum gesetzt. Hinweis: Beim EXFAT-Dateisystem bekommen alte Bilder, die vor 1980 aufgenommen wurden, das Datum 1.1.1980.
- Die ursprünglichen Daten aus dem Zip-Verzeichnis und dem Work-Verzeichnis bleiben unberührt. Es wird in das Export-Verzeichnis exportiert.
- Das Programm läuft unter macOS, Linux und Windows.

## Download 
Der Download kann bei [github](https://github.com/heinerjost/tpex/releases/tag/V0.9.3) erfolgen.

## Voraussetzung für den Ablauf
Es muss eine Java-Laufzeitumgebung >= 21 vorhanden sein. Z. B. [OpenJDK25](https://jdk.java.net/25/)

## Takeout anfordern
Die Takeout-Daten müssen bei Google angefordert werden. Dafür steht eine Schaltfläche zur Verfügung. Alternativ kann der Link [Google-Takeout](https://takeout.google.com) aufgerufen werden. Der Export dauert einige Minuten oder Stunden. Google informiert per Mail über die Bereitstellung der Export-Daten. Die Daten müssen in einem Verzeichnis gespeichert werden. In dem Verzeichnis dürfen keine anderen Dateien gespeichert sein.

## Aufruf des Programmes
Durch einen Doppelklick auf das Icon wird das Programm gestartet.

Im ersten Schritt werden die Zip-Dateien entpackt. Im zweiten Schritt erfolgt der Export.