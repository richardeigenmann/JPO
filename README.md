# JPO
Java Picture Organizer

[![Build Status](https://app.travis-ci.com/richardeigenmann/JPO.svg?branch=master)](https://app.travis-ci.com/richardeigenmann/JPO)
[![CircleCI](https://circleci.com/gh/richardeigenmann/JPO/tree/master.svg?style=svg)](https://circleci.com/gh/richardeigenmann/JPO/tree/master)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/948fa1d9f4354611a6be88c422505c25)](https://app.codacy.com/gh/richardeigenmann/JPO/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=richardeigenmann_JPO&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=richardeigenmann_JPO)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=richardeigenmann_JPO&metric=bugs)](https://sonarcloud.io/summary/new_code?id=richardeigenmann_JPO)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=richardeigenmann_JPO&metric=coverage)](https://sonarcloud.io/summary/new_code?id=richardeigenmann_JPO)
[![semaphore](https://richardeigenmann.semaphoreci.com/badges/JPO.svg?style=shields)](https://richardeigenmann.semaphoreci.com/)

Homepage: http://j-po.sourceforge.net/

## About JPO the Java Picture Organizer

JPO is a desktop program that helps you organise your digital pictures by putting them in collections that are saved as
plain XML files. You can label the picture, capture its location and associate it with Keywords. 
When you open up a collection you can browse or search the pictures, skip through the thumbnails, share them by 
email or generate a website. A picture viewer allows you to see the pictures in full screen with simple Zoom-in and 
Zoom-out with the left and right mouse buttons or mouse wheel.</p>

A fundamental design principle is that JPO doesn't alter your pictures. They stay unchanged on your disk unless you
ask JPO to move them somewhere or to delete them. It does allow you to "consolidate" all the picture files into
a directory.

JPO is not a photo editing application. There are many excellent packages out there with which you can touch up your
pictures. You can make JPO open such a program for you.

Richard Eigenmann from Z&uuml;rich has spent the last 22 years building and improving JPO as an OpenSource project. He
hopes you will find it useful and enjoys feedback.

### Features

* Quickly Organize digital images into collections and groups
* Allows tagging of pictures with keywords
* Creates web pages from your collection
* Download pictures from Camera with the ability to load only the new ones
* Send rescaled images and originals via email
* View pictures as a slide show
* Simple zoom-in and zoom-out with left / right mouse buttons or mouse wheel
* Rotation on the fly without modifying the original image
* Browse image thumbnails
* Automatically advancing slide shows
* Captures metadata and has search features
* Displays EXIF and IPTC metadata
* Export to directory facility to share via e-mail or CD-ROM
* Open XML data structures
* Pure Java, no native libraries
* Runs on Windows, Linux and macOS, anywhere Java runs
* Can call up outside applications
* Leaves your pictures where they are
* Can move pictures to new locations to tidy up (consolidate)
* Doesn't modify your original pictures
* Open source license

## Installing JPO on Windows 10 & 11

Visit the SourceForge.net download page by clicking this green button. Note that the download starts directly after 5
seconds. Install the application like any other Windows application. Note that the developer has not paid for Microsoft 
Certifications and you will get a lot of warnings about the application being from an "Unknown Publisher". JPO does
not collect data about you. Check the <a href="https://j-po.sourceforge.io/privacy.php.html">Privacy Policy</a>. If you
know how I can improve this at low cost, please let me know.

You can remove it just like any other windows
application by opening the Start menu, clicking on the cogwheel icon (Settings), choosing Apps in the Settings Window
that opens up, scroll down to JPO and there click on the uninstall button. It is not supposed to leave any files 
or registry settings behind.

[![Download Button](https://a.fsdn.com/con/app/sf-download-button)](https://sourceforge.net/projects/j-po/files/JPO-0.14.exe/download)

<p>Alternatively, visit the <a href="http://sourceforge.net/projects/j-po/files">SourceForge download area</a></p> 
I apologise for the mess SourceForge have made of this page with all the ads.

## HIDPI - Problems with super high resolution displays

Many Laptops have super High Resolution Displays (HiDPI) which Java doesn't deal with very well by default. The UI then
shows up with unusably small fonts and icons. You can fix this by setting the environment variables.

Set the GDK_SCALE environment variable to the desired scale factor. This variable controls the scaling of the UI elements. 
Replace 2 with your preferred scale factor.

```bash
export GDK_SCALE=2
```

Set the GDK_DPI_SCALE environment variable to the inverse of the GDK_SCALE value. This variable adjusts the DPI scaling
of fonts and other elements to maintain their proper size. For example, if you set GDK_SCALE to 2, then GDK_DPI_SCALE should be set to 0.5.

```bash
export GDK_SCALE=2
export GDK_DPI_SCALE=0.5
```
## Connecting with the Author

JPO is an Open Source project and you can contribute to it. To connect with the author simply send an email to
<a href="mailto:richard.eigenmann@gmail.com"richard.eigenmann@gmail.com</a>


## Developing JPO with IntelliJ IDEA

As of 2025 JPO is being developed with the community edition of IntelliJ IDEA.

* Ensure you have IntelliJ IDEA set up: https://www.jetbrains.com/idea/
* Have you got a JDK 23 or later? [Oracle Java SDK](https://www.oracle.com/technetwork/java/javase/downloads/index.html)

Once you have your IntelliJ IDEA installed and working you can clone the JPO repository from GitHub and open it in IntelliJ IDEA.

```bash
git clone https://github.com/richardeigenmann/JPO.git
```

To run the project go to the Gradle Tasks Window click
`Jpo > Tasks > application > run`.

Or if you prefer the GUI, you can do the following:

On the Welcome screen click on `Check out from Version Control`

Enter the URL `https://github.com/richardeigenmann/JPO.git` and click `Clone`

Confirm `You have checked out and IntelliJ IDEA project file: .../build.gradle`

On the right margin you have a tab `Gradle` which gives you access to the tasks

Pick `JPO > Tasks > build > build` and it should download dependencies compile and run the tests

## Exploring the code - with SourceTrail

See https://sourcetrail.com

```bash
export LANG=en_US.UTF-8
sourcetrail

# Project > New Project
# give it a name: JPO
# tell it where you want it to create it's working files
# Add Source Groups
# Pick Java > Java Source Group from Gradle > Next
# Java Standard: 12
# Gradle Project File: Jpo/build.gradle
# Next > Next 
# on the New Project screen click on the plus (+) icon
# Add a new Source Group Type - Java - Empty Java Source Group
# Java Standard: 12
# Files and Directories to index: src/main/java
# Next > Create
# Classes > Main > ApplicationStartupRequest > ApplicationEventHandler > handleApplicationStartupRequest
```

## Experimental: Running with Flatpak

```bash
flatpak-builder build-dir --force-clean io.sourceforge.j-po.json
flatpak-builder --run build-dir io.sourceforge.j-po.json flatpak-run.sh
flatpak-builder --repo=repo --force-clean build-dir io.sourceforge.j-po.json
flatpak --user remote-add --no-gpg-verify --if-not-exists tutorial-repo repo
flatpak --user install tutorial-repo org.richinet.jpo
flatpak run org.richinet.jpo
flatpak remove org.richinet.jpo
```


## Developing JPO with Netbeans

Ensure you have Netbeans set up:

* Have you downloaded JDK 23 or
  later? [Oracle Java SDK](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Have you installed Netbeans does it start up? <https://netbeans.org> Like Version 11.2?
* Have you got Gradle installed? <https://gradle.org/install> Use SDKMan for Linux, follow the "Installing manually" for Windows

Check that Netbeans is using the correct version of Gradle by opening Tools > Options > Java > Gradle > Execution . The Gradle Distribution should be Custom and point at your Gradle directory i.e. C:\Gradle\gradle-6.0.1 (knowing that the gradle.exe is actually in C:\Gradle\gradle-6.0.1\bin\gradle.exe)

On the menu pick Team > Git > Clone

Enter the Repository URL `https://github.com/richardeigenmann/JPO.git` and click Next

On the panel the tick should be on the "master" branch. Click Next > Finish

To run the application, click on Projects in the left Panel and expand the JPO project in the left panel, expand the Build Scripts item and click on the green build.gradle item. In the Navigator panel underneath, the various Gradle tasks will appear. Click on Build > run to launch the app.

## Architecture

JPO is a Java Swing application which uses the Guava [EventBus](https://github.com/google/guava/wiki/EventBusExplained) 
to order and fulfil the GUI tasks.


