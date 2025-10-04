# tpex - Takeout Photo Exporter
This project is still under development.

I have a large photo collection stored in Google Photos and want to back it up to a local drive.  

Unfortunately, Google only provides the Takeout format for this. In this format, photos are stored in folders per album and additionally in folders per year. Some metadata I added later (e.g., location or capture date for digitized images) are not included in the EXIF data, but delivered as JSON files. Overall, this results in a confusing structure.  

That’s why I looked into [Google Photos Takeouthelper](https://github.com/TheLastGimbus/GooglePhotosTakeoutHelper). However, I wasn’t fully convinced by the result either. Therefore, I built the Takeout Photo Exporter.  

## Features
- Direct use of the ZIP files from the Takeout export.  
- All file types included in the export are supported.  
- Albums remain preserved as folders.  
- Albums containing only photos from a single year are moved into a folder named “Photos yyyy”. Others remain in the root directory.  
- Photos from the “Photos from yyyy” folders are only considered if they are not already part of an album. This avoids duplicates and provides better clarity.  
- For JPEG and JPG files:  
  - Location data from the JSON file is written into the image’s EXIF data.  
  - Capture date is added to the EXIF data. Additionally, the file’s modification date is set to the capture date. Note: On the EXFAT file system, older images taken before 1980 will be assigned the date 1.1.1980.  
- The original data remains untouched and is written to a different directory.  

## Preparation
Export photo files with [Google Takeout](https://support.google.com/accounts/answer/9666875?hl=en) and store them in a directory on your computer. No other files should be stored in this directory.  

## Download
Downloads are available on [GitHub](https://github.com/heinerjost/tpex/tags).  

## Requirements
A Java runtime environment >= 21 is required, e.g., [OpenJDK25](https://jdk.java.net/25/).  

## Running the Program
```bash
java -jar tpex-0.9.1.jar -z zip-directory -i input-directory -o output-directory -s file
```

### Parameter Description
|Short|Long|Description|
|----|----|------------|
|-z|--zip| Directory of the ZIP files. *|
|-i|--input| Directory where the ZIP files are unpacked and used as the source for export. *|  
|-o|--output| Directory where images and videos are exported to. *|  
|-s|--skip| Optional: File containing album names that should be skipped. It’s better to disable these albums directly in the Takeout export.|
|-c|--cmd| Command. Only needed for development. Possible values: unzip, export|  

\* If the path contains spaces, enclose it in single quotes (`'`).  
