# tpex - Takeout Photo Exporter

I have a large photo collection stored in Google Photos and want to back it up on a local drive.

Unfortunately, Google only provides the Takeout format for this. The photos are organized into folders per album and additional folders per year. Some metadata I added later (e.g., location or shooting date for scanned photos) are not stored in the EXIF data but are instead delivered as JSON files. Overall, the structure is quite confusing.

Therefore, I looked at [Google Photos Takeouthelper](https://github.com/TheLastGimbus/GooglePhotosTakeoutHelper). However, I wasn’t satisfied with the results either, so I built my own Takeout Photo Exporter.

## Features
- User-friendly interface
- Multilingual support. Currently available in English, German, and Spanish.
- The Takeout ZIP files do not need to be unpacked manually.
- All file types contained in the export file are taken into account.
- Albums are preserved as folders.
- Albums containing only photos from one year are moved to a folder named “Photos yyyy.” Others remain in the main directory.
- Photos from the “Photos from yyyy” folders are only included if they are not already in an album. This avoids duplicates and improves organization.
- For JPEG and JPG files:
  - The location is transferred from the JSON file into the image’s EXIF data.
  - The capture date is written into the EXIF data. Additionally, the file’s modification date is set to the capture date.  
    Note: On the EXFAT file system, old photos taken before 1980 will get the date 1/1/1980.
- The original data from the ZIP and work directories remain untouched. All exports are written to the export directory.
- The program runs on macOS, Linux, and Windows.

## Download 
You can download it from [GitHub](https://github.com/heinerjost/tpex/tags).

## Requirements
A Java Runtime Environment >= 21 is required, e.g., [OpenJDK25](https://jdk.java.net/25/).

## Preparation
You must request your Takeout data from Google. A button is provided for this purpose, or you can visit [Google Takeout](https://takeout.google.com).  
The export process may take several minutes or hours. Google will notify you via email when the export data is ready. The data must be saved in a directory, and no other files should be stored there.

## Running the Program
```bash
java -jar tpex-0.9.2.jar -z zip-directory -i input-directory -o output-directory -s file -l de
```
