![tpex Logo](./src/main/resources/images/logo640.png)
# tpex - Takeout Photo Exporter

I have an extensive photo collection stored in Google Photos and would like to back it up to a local drive.

Unfortunately, Google only offers the Takeout format for this purpose. In that format, photos are stored in folders per album and additionally in folders per year. Some metadata I added later (e.g. shooting location or capture date for digitized images) are not included in the EXIF data but provided in separate JSON files. Overall, this leads to a rather confusing structure.

That’s why I looked into the [Google Photos Takeout Helper](https://github.com/TheLastGimbus/GooglePhotosTakeoutHelper). However, I was not fully satisfied with its results either. So I built my own tool — the Takeout Photo Exporter.

## Features
- User-friendly interface
- Multilingual support — currently English, German, and Spanish. Note: Language files are generated using AI. If you find any mistakes, please report them on [GitHub](https://github.com/heinerjost/tpex/issues). More languages are planned.
- Takeout ZIP files do not need to be extracted manually.
- All file types included in the export file are processed.
- Albums are preserved as folders.
- Albums containing only photos from one year are moved into a folder named “Photos yyyy”. Others remain in the main directory.
- Photos from “Photos from yyyy” folders are only considered if they are not already part of an album. This avoids duplicates and improves clarity.
- For JPEG and JPG files:
  - The location data is taken from the JSON file and written into the image’s EXIF data.
  - The capture date is written into the EXIF data. In addition, the file modification date is set to the capture date. Note: On the EXFAT file system, old photos taken before 1980 are assigned the date 1/1/1980.
- The original data in the ZIP and work directories remain unchanged. Exported files are written to the export directory.
- The program runs on macOS, Linux, and Windows.

## Download
You can download it from [GitHub](https://github.com/heinerjost/tpex/releases/tag/V0.9.3).

## Requirements
A Java runtime environment >= 21 is required, e.g. [OpenJDK25](https://jdk.java.net/25/).

## Preparation
The Takeout data must be requested from Google. A button is available for this purpose, or alternatively, you can visit [Google Takeout](https://takeout.google.com). The export process can take several minutes or hours. Google will notify you by email once the export data is ready. Save the data in a directory that contains no other files.

## Running the Program
Double-click the application icon to start the program.

In the first step, the ZIP files are extracted. In the second step, the export process takes place.
