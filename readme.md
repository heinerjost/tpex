![tpex Logo](./src/main/resources/images/logo640.png) 

# tpex - Takeout Photo Exporter

I have a large collection of photos stored in Google Photos and want to
back them up on a local drive.

Unfortunately, Google only provides this in the Takeout format.

The structure of Google Takeout for photos is organized by years and
albums, with each year forming its own folder. Inside these folders are
the photos, each accompanied by a separate JSON file containing metadata
such as location and date taken. The downloaded files are delivered as a
zip archive.

Overall, this structure is very confusing.

Therefore, I looked into [Google Photos
Takeouthelper](https://github.com/TheLastGimbus/GooglePhotosTakeoutHelper).
However, I wasn't satisfied with the result. So I built the **Takeout
Photo Exporter**.

## Features

-   User-friendly interface
-   Multilingual support. Currently, Danish, German, English, Finnish, French, Greek, Italian, Dutch, Norwegian, Polish, Portuguese, Swedish, Spanish, and Czech are available. Note: The language files are generated using AI. If there are any errors, please report them on [Github](https://github.com/heinerjost/tpex/issues), . More languages can be added.
-   The program automatically unpacks the Takeout ZIP files --- no
    manual action required.
-   All file types contained in the export (jpeg, mov, mp4, heic, etc.)
    are processed.
-   Albums are preserved as folders.
-   Photos edited in Google Photos are included in Takeout in both the original and edited versions. Only the edited version is included in the export.
-   Albums that contain only photos from a single year are moved to a
    folder named "Photos yyyy". Others remain in the main directory.
-   Photos from the yearly folders "Photos from yyyy" are only included
    if they are not already part of an album. This avoids duplicates and
    improves organization.
-   For JPEG and JPG files:
    -   The location information from the JSON file is written into the
        image's EXIF data.
    -   The date taken is also added to the EXIF data, and the file's
        date is set to the date taken. *Note:* On the EXFAT file system,
        old images taken before 1980 will have the date set to January
        1, 1980.
-   The original data from the ZIP and work directories remain
    untouched. Exporting occurs into a separate export directory.
-   The program runs on macOS, Linux, and Windows.

## Download

The download is available on
[Github](https://github.com/heinerjost/tpex/releases/tag/V0.9.3).

## Requirements

A Java runtime environment ≥ 21 is required,
e.g. [OpenJDK25](https://jdk.java.net/25/).

## Requesting a Takeout

The Takeout data must be requested from Google. A button is provided for
this purpose. Alternatively, the link [Google
Takeout](https://takeout.google.com) can be opened. The export may take
several minutes or hours. Google will notify you by email when the
export data is ready. The data should be saved in a directory that
contains no other files.

## Running the Program

Double-click the icon to start the program.

In the first step, the ZIP files are unpacked. In the second step, the
export is performed.
