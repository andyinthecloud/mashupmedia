# Mashup Media
***
Mashup Media is a free open source web media centre designed to play, listen, view, manage and share your music, videos and pictures safely and securely using just a modern web browser on most modern devices.

Mashup Media eliminates the need to worry about online privacy and corporate small print, it puts you back in control of your media content. Define groups to help separate your media with friends, family, colleagues and clients. Your private life remains private and you can choose who you want to share it with.

### Table of contents
* [Music](#music)
* [Photos](#photos)

## Music
Group your media in libraries and Mashup Media will provide a clean, elegant interface to quickly access and play your content.

A lot of time has been dedicated into making the Mashup Media music experience very special. Music is categorised into artists, albums, genres and years. Libraries can contain a large amount of files with minimal impact to performance.
<p />
<img src="https://github.com/andyinthecloud/mashupmedia/assets/2725234/a7342963-bdab-472c-a840-24d4d7d81724" width="200" />

### Compatibility
You can listen to your music on all modern web browsers on desktops, tablets and mobile devices.

### Playlists
You can create and share music playlists.
<p />
<img src="https://github.com/andyinthecloud/mashupmedia/assets/2725234/c0633837-b687-4af2-b50a-6159047108f4" width="200" />

### Support for many music formats 
Mashup Media uses [FFmpeg](https://ffmpeg.org/) to provide support for the most common music encoding formats.
<p />
<img src="https://github.com/andyinthecloud/mashupmedia/assets/2725234/f6c10224-38e6-4670-85e3-5e29fadff17e" width="200" />

## Photos
Finally a secure way to store and view your photos! All you have to do is let Mashup Media know where your images are located and it will index them, create thumbnails and organise them into albums. 
Watch this space!

## Videos
Imagine all your videos available to watch through a web browser! This is what Mashup Media offers. Nearly all formats are supported. Videos are listed clearly and are managed by libraries.
Watch this space!

## Security
Security is a key aspect of Mashup Media. The application can only be accessed with valid credentials. Media access can be finetuned through groups, for example members of the group "family" could just be given access to family photographs. User passwords are encrypted before being stored in the Mashup Media 

## Configuration

### Out of the box
Mashup Media will work out of the box, just add a library and point it to a folder containing your photos, music or videos and it will index them automatically and then in real time whenever a file is added, updated or removed.

### User management
The user set up is very simple. Users can be added to multiple groups which are linked to libraries.
<p />
<img src="https://github.com/andyinthecloud/mashupmedia/assets/2725234/c5111e25-f3cb-45dc-9037-686e7501dab0" width="200" />

## Technology
Mashup Media is developed in Java, React Javascript and an embedded database. 

## Instructions

1. Install [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) 
2. Install [Maven](https://maven.apache.org/download.cgi)
3. Install [Git](https://git-scm.com/downloads)
4. Checkout the Mashup Media project master branch
5. Open a terminal and in the mashupmedia parent folder run the build 
```
mvn -skipTests clean package
```
This will build a file called *mashupmedia-server-x.x.x-RELEASE.jar* located in the the mashupmedia-server/target folder.

6. Start Mashup Media
```
java -DmashupMediaHome="C:\Users\user\stuff\mashup-media" -Dserver.port=8080 -jar mashupmedia-server\target\mashupmedia-server-x.x.x-RELEASE.jar
```
7. Open http://localhost:8080/mashupmedia in a web browser and log in using admin / admin.
8. Create a media library.
9. Recommended. Change the admin password.
10. Optional. Install and configure Ffmpeg at https://ffmpeg.org/download.html to make all your media compatible with Mashup Media.   


