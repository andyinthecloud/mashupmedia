# Mashup Media
***
Mashup Media is a free, online, open source HTML5 media centre designed to play, listen, view, manage and share your music, videos and pictures safely and securely just using a modern web browser supported on all devices.

Mashup Media eliminates the need to worry about online privacy and corporate small print, it puts you back in control of your media content. Share your files with friends, family, colleagues using groups to seperate who can access what. Your private life remains private and you can choose who you want to share it with.

### Table of contents
-[Music](#music)
-[Photos](#photos)



## Music
![Mashup Media - music home page](src/misc/screehshots/music-random.png)
Add as many music libraries as you like and Mashup Media will provide a clean, elegant interface to quickly access and play your tunes.

A lot of time has been dedicated into making the Mashup Media music experience very special. Music is categorised into artist, albums, genre and year by the music tags or failing that the folder structure. Album art is extracted either from the music tag or the folder. Libraries can contain very large amount of files with minimal impact to performance. Any changes to the library files are automatically detected making sure that the libraries are always sychronised.

### Compatibility
You can listen to your music on all modern html5 compatible web browsers on desktop, tablet and mobile devices.
### Playlists
You can create and share music playlists.

![Mashup Media - music home page](src/misc/screehshots/music-playlist.png)

### Support for many music formats 
Mashup Media uses ![FFMpeg](https://www.ffmpeg.org/) to provide support for the most common music encoding formats.

## Photos

![Mashup Media - photo home page](src/misc/screehshots/photo-photos.png)

Finally a secure way to store and view your photos! All you have to do is let Mashup Media know where your images are located and it will index them, create thumbnails and organise them into albums. 

## Videos
Imagine all your videos available to watch through a web browser! This is what Mashup Media offers. Nearly all formats are supported. Videos are listed clearly and are managed by libraries.

## Security

Security is a key aspect of Mashup Media. The application can only be accessed with a valid username and password. Media access can be finetuned through groups, for example members of the group "family" could just be given access to family photographs. User passwords are encrypted before being stored in the Mashup Media 


## Configuration
### Out of the box
Mashup Media will work out of the box, just add a library and point it to a folder containing your photos, music or videos and it will index them automatically and then in real time whenever a file is added, updated or removed.
### User management
The user set up is very simple. Users can be added to multiple groups which are linked to libraries.

## Technology
Mashup Media is developed in Java, html and javascript. It will work as either a standalone file or embedded in a Java web server such as ![Tomcat](https://tomcat.apache.org/index.html). Mashup Media will run in almost environments such as Windows, Mac and Linux.

## Instructions

![Apache Maven](https://maven.apache.org/) is used to build and package Mashup Media. Please follow the ![Maven installation page](https://maven.apache.org/install.html) and make sure it is configured correctly. There are two options to run Mashup Media.


1. Create a web container file. 
```
mvn -skipTests package
```
This will build a file called *mashupmedia.war* which can be loaded into a Java web container such as ![Tomcat](http://tomcat.apache.org/), ![Jetty](http://www.eclipse.org/jetty/) or ![Oracle Weblogic](https://www.oracle.com/middleware/weblogic/index.html).

2. Create an executable jar file.
```
mvn -skipTests install tomcat7:exec-war-only
```
This builds a file called *mashupmedia.jar* which contains everything required to run Mashup Media. Go to the folder with mashupmedia.jar and type.
```
java -jar mashupmedia.jar
```



