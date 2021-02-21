# JPO
Java Picture Organizer

[![Build Status](https://travis-ci.org/richardeigenmann/JPO.svg?branch=master)](https://travis-ci.org/richardeigenmann/JPO)
[![CircleCI](https://circleci.com/gh/richardeigenmann/JPO.svg?style=svg)](https://circleci.com/gh/richardeigenmann/JPO)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5abc0256877f43e19564e71ca3c8f073)](https://www.codacy.com/app/richardeigenmann/JPO?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=richardeigenmann/JPO&amp;utm_campaign=Badge_Grade)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=JPO&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=JPO)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=JPO&metric=bugs)](https://sonarcloud.io/dashboard?id=JPO)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=JPO&metric=ncloc)](https://sonarcloud.io/dashboard?id=JPO)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=JPO&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=JPO)
[![semaphore](https://richardeigenmann.semaphoreci.com/badges/JPO.svg?style=shields)](https://richardeigenmann.semaphoreci.com/)

Homepage: http://j-po.sourceforge.net/

## About JPO the Java Picture Organizer

JPO is a program that helps you organise your digital pictures by putting them in collections. There you can browse the
pictures, skip through the thumbnails, share them by email or generate a website. A powerful picture viewer allows you
to see the pictures full screen with simple Zoom-in and Zoom-out with the left and right mouse buttons.</p>

A fundamental design principle is that JPO doesn't mess with your pictures. They stay unchanged on your disk unless you
ask JPO to move them somewhere or to delete them.

JPO is not a photo editing application. There are many excellent packages out there with which you can touch up your
pictures. You can make JPO open such a program for you.

Richard Eigenmann from Z&uuml;rich has spent the last 21 years building and improving JPO as an OpenSource project. He
hopes you will find it useful and enjoys feedback.

### Features

* Quickly Organize digital images into collections and groups </li>
* Creates web pages from your collection </li>
* Download pictures from Camera with the ability to load only the new ones</li>
* Send rescaled images and originals via email</li>
* View pictures as a slide show </li>
* Simple zoom-in and zoom-out with left / right mouse buttons</li>
* Rotation on the fly without modifying the original image</li>
* Browse image thumbnails </li>
* Automatically advancing slide shows</li>
* Captures metadata and has search features</li>
* Displays EXIF and IPTC metadata</li>
* Export to directory facility to share via e-mail or CD-ROM </li>
* Open XML data structures </li>
* Pure Java, no native libraries</li>
* Runs on Windows, Linux and Mac OS, anywhere Java runs</li>
* Can call up outside applications</li>
* Leaves your pictures where they are</li>
* Can move pictures to new locations to tidy up</li>
* Doesn't modify your original pictures</li>
* Open source license</li>

## Installing JPO on Windows 10

Visit the SourceForge.net download page by clicking this green button. Note that the download starts directly after 5
seconds. Install the application like any other windows application. You can remove it just like any other windows
application by opening the Start menu, clicking on the cogwheel icon (Settings), choosing Apps in the Settings Window
that opens up, scroll down to JPO and there click on the Uninstall button.

[![Download Button](https://a.fsdn.com/con/app/sf-download-button)](https://sourceforge.net/projects/j-po/files/JPO-0.14.exe/download)

<p>Alternatively, visit the <a href="http://sourceforge.net/projects/j-po/files">SourceForge download area</a></p>

Why the scary warning about the Unknown Publisher? I haven't figured out how to make Microsoft recognize me as a trusted
publisher. If you know how I can improve this, please write to me.

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
# Files and Directoris to index: src/main/java
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

## Developing JPO with IntelliJ IDEA

* Ensure you have IntelliJ IDEA set up: https://www.jetbrains.com/idea/
* Have you got a JDK later than 8 (i.e. with Modules)? [Oracle Java SDK](https://www.oracle.com/technetwork/java/javase/downloads/index.html) for instance 13.0.1?

On the Welcome screen click on `Check out from Version Control`

Enter the URL `https://github.com/richardeigenmann/JPO.git` and click `Clone`

Confirm `You have checked out and IntelliJ IDEA project file: .../build.gradle`

On the right margin you have a tab `Gradle` which gives you access to the tasks

Pick `JPO > Tasks > build > build` and it should download dependencies compile and run the tests

To run the project go to the Gradle Tasks Window click `JPO > build > run`.

## Developing JPO with Netbeans

Ensure you have Netbeans set up:

* Have you downloaded a JDK? [Oracle Java SDK](https://www.oracle.com/technetwork/java/javase/downloads/index.html) for instance 13.0.1?
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