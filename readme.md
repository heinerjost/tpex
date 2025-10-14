# tpex - Takeout Photo Exporter

**Status:** 🚧 _This project is under development._

I have a large photo collection stored in Google Photos and want to back it up on a local drive.

Unfortunately, Google only offers the Takeout format for this purpose. Photos are stored in folders by album and also by year. Some of the metadata I added later (e.g., location or capture date for digitized photos) are not stored in the EXIF data but are instead provided in a JSON file. This results in a rather confusing structure.

Therefore, I took a look at [Google Photos Takeouthelper](https://github.com/TheLastGimbus/GooglePhotosTakeoutHelper). However, I wasn’t satisfied with the result either. So I built the Takeout Photo Exporter.

## ✅ Requirements
- 📦 Direct use of ZIP files from the Takeout export.
- 🗂️ All file types included in the export are supported.
- 📁 Albums are preserved as folders.
- 📅 Albums containing only photos from a single year are moved to a folder named “Photos yyyy.” Others remain in the main directory.
- 🧹 Photos from the "Photos from yyyy" folders are only processed if they are not already part of an album. This helps avoid duplicates and improves organization.
- 🖼️ For JPEG and JPG files:
  - The photo location is transferred from the JSON file into the image’s EXIF data.
  - The capture date is written to the EXIF data. Additionally, the file’s timestamp is set to the capture date.  
    Note: On the EXFAT file system, old photos taken before 1980 will receive the date 1/1/1980.
- 🔒 The original data remains untouched and is exported to a different directory.

## 📦 Preparation
Export your photo files using [Google Takeout](https://support.google.com/accounts/answer/9666875?hl=en) and save them in a directory on your computer. No other files should be stored in that directory.

## 📥 Download
Downloads are available on [GitHub](https://github.com/heinerjost/tpex/tags).

## 🛠️ Runtime Requirements
A Java Runtime Environment >= 21 is required. For example, [OpenJDK25](https://jdk.java.net/25/).

## Running the Program
```bash
java -jar tpex-0.9.2.jar -z zip-directory -i input-directory -o output-directory -s file -l en
```

### 🔍 Parameter Description
|Short|Long|Description|
|----|----|------------|
|-z|--zip|Directory containing the ZIP files. *|
|-i|--input|Directory where the ZIP files will be extracted and used as the source for export. *| 
|-o|--output|Directory where images and videos will be exported. *|
|-s|--skip|Optional: File containing album names to be skipped. It’s better to deselect those albums directly during the Takeout export.|
|-l|--language|Language of the Google account. Possible values: de, en, fr, es, it, nl, pt, pl, ja, ko, cs, hu, sv, da, no, fi, ro, el, th, vi, hi, ar|
|-c|--cmd|Command, only used for development. Possible values: unzip and export|

\* If a path contains spaces, enclose it in single quotes (`'`).
