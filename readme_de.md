# tpex - Takeout Photo Exporter

**Status:** 🚧 _Dieses Projekt ist in der Entwicklung._

Ich habe eine umfangreiche Bildersammlung bei Google Photos gespeichert und möchte diese auf einem lokalen Laufwerk sichern.

Leider bietet Google hierfür nur das Takeout-Format an. Dabei werden die Fotos in Ordnern pro Album und zusätzlich in Ordnern pro Jahr abgelegt. Einige von mir nachträglich erfasste Metadaten (z. B. Aufnahmeort oder das Aufnahmedatum bei nachträglich digitalisierten Bildern) sind nicht in den EXIF-Daten abgelegt, sondern werden als JSON-Datei geliefert. Insgesamt eine unübersichtliche Struktur.

Deshalb habe ich mir  [Google Photos Takeouthelper](https://github.com/TheLastGimbus/GooglePhotosTakeoutHelper) angesehen. Von dem Ergebnis war ich aber auch nicht überzeugt. Daher habe ich mir den Takeout Photo Exporter gebaut.

## ✅ Anforderungen
- 📦 Direkte Nutzung der ZIP-Dateien des Takeout-Exports.
- 🗂️ Es werden alle Dateitypen, die in der Exportdatei enthalten sind, berücksichtigt.
- 📁 Die Alben bleiben als Ordner erhalten.
- 📅 Alben, die nur Fotos aus einem Jahr enthalten, werden in einen Ordner „Aufnahmen yyyy“ verschoben. Andere bleiben im Hauptverzeichnis.
- 🧹 Fotos aus den Ordnern „Photos from yyyy“ werden nur berücksichtigt, wenn sie nicht in einem Album enthalten sind. Hierdurch werden Duplikate vermieden und es wird mehr Übersichtlichkeit geschaffen.
- 🖼️ Für JPEG- und JPG-Dateien:
  - 📍 Aufnahmeort wird aus der JSON-Datei in die EXIF-Daten des Bildes übernommen.
  - 🕓 Das Aufnahmedatum wird in die EXIF-Daten übernommen. Zusätzlich wird das Dateidatum auf das Aufnahmedatum gesetzt. Hinweis: Beim EXFAT-Dateisystem bekommen alte Bilder, die vor 1980 aufgenommen wurden, das Datum 1.1.1980.
- 🔒 Die ursprünglichen Daten bleiben unberührt und werden in ein anderes Verzeichnis exportiert.
- 💻 Das Programm läuft unter macOS, Linux und Windows.

## 📦 Vorbereitung
Export der Foto-Dateien mit [Google-Takeout](https://support.google.com/accounts/answer/9666875?hl=de) und speichern in einem Verzeichnis auf dem Rechner. In dem Verzeichnis dürfen keine anderen Dateien gespeichert sein.

## 📥 Download 
Der Download kann bei [github](https://github.com/heinerjost/tpex/tags) erfolgen.

## 🛠️ Voraussetzung für den Ablauf
Es muss eine Java-Laufzeitumgebung >= 21 vorhanden sein. Z. B. [OpenJDK25](https://jdk.java.net/25/)

## Aufruf des Programmes
java -jar tpex-0.9.2.jar -z zip-verzeichnis -i input-verzeichnis -o output-verzeichnis -s datei -l de

### 🔍 Beschreibung der Parameter
|Kurz|Lang|Beschreibung|
|----|----|------------|
|-z|--zip| Verzeichnis der Zip-Dateien. *|
|-i|--input|Verzeichnis, in das die Zip-Dateien entpackt werden und das als Quelle für den Export dient. *| 
|-o|--output|Verzeichnis, in das die Bilder und Videos exportiert werden. *|
|-s|--skip|optional: Datei, die Albumnamen enthält, die übersprungen werden sollen. Besser ist es, diese Alben direkt beim Takeout-Export zu deaktivieren.|
|-l|--language|Sprache des Google-Accounts. Mögliche Werte: de, en, fr, es, it, nl, pt, pl, ja, ko, cs, hu, sv, da, no, fi, ro, el, th, vi, hi, ar|
|-c|--cmd| Command. Wird nur zur Entwicklung benötigt. Mögliche Werte sind unzip und export|
 
\* Sofern im Pfad Leerzeichen vorkommen, ist der Pfad von Hochkomma (') einzuschließen.
