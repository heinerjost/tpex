# tpex – Takeout Photo Exporter

This project is still under development.

I have an extensive photo collection stored in Google Photos and want to back it up to a local drive.

Unfortunately, Google only offers the Takeout format for this. Photos are stored in folders by album and additionally in folders by year. Some metadata I added later (e.g., location or capture date for digitized images) is not stored in the EXIF data but delivered as JSON files. Overall, the structure is quite confusing.

I looked into [Google Photos Takeout Helper](https://github.com/TheLastGimbus/GooglePhotosTakeoutHelper), but I wasn’t satisfied with the results. So I built the Takeout Photo Exporter myself.

## Requirements

- Direct use of ZIP files from the Takeout export.
- All file types included in the export are supported.
- Albums are preserved as folders.
- Albums containing only photos from a single year are moved to a folder named “Photos yyyy”. Others remain in the main directory.
- Photos from the “Photos from yyyy” folders are only considered if they are not part of an album. This avoids duplicates and improves clarity.
- For JPEG and JPG files:
  - Location data from the JSON file is transferred into the image’s EXIF data.
  - Capture date is written into the EXIF data and also set as the file’s modification date. Note: On EXFAT file systems, older images taken before 1980 will be dated 1/1/1980.
- Original data remains untouched and is written to a separate directory.

## Preparation

Export your photo files using [Google Takeout](https://support.google.com/accounts/answer/9666875?hl=en) and save them in a directory on your computer. No other files should be stored in this directory.

## Download

You can download the tool from [GitHub](https://github.com/heinerjost/tpex/tags).

## Runtime Requirements

A Java Runtime Environment >= 21 is required. For example: [OpenJDK25](https://jdk.java.net/25/)

## Program Execution

```bash
java -jar tpex-0.9.1.jar -z zip-directory -i input-directory -o output-directory -s file -l en
```

### Parameter Description

| Short | Long     | Description |
|-------|----------|-------------|
| -z    | --zip    | Directory containing the ZIP files. * |
| -i    | --input  | Directory where the ZIP files are unpacked and used as the source for export. * |
| -o    | --output | Directory where the images and videos will be exported. * |
| -s    | --skip   | Optional: File containing album names to be skipped. It’s better to deselect these albums during the Takeout export. |
| -l    | --language | Language of the Google account. Possible values: de, en, fr, es, it, nl, pt, pl, ja, ko, cs, hu, sv, da, no, fi, ro, el, th, vi, hi, ar |
| -c    | --cmd    | Command. Only needed for development. Possible values: unzip and export |

\* If the path contains spaces, enclose it in single quotes (').
