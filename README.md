# JPO
Java Picture Organizer

[![Build Status](https://travis-ci.org/richardeigenmann/JPO.svg?branch=master)](https://travis-ci.org/richardeigenmann/JPO)
[![Build Status](https://semaphoreci.com/api/v1/richardeigenmann/jpo/branches/master/badge.svg)](https://semaphoreci.com/richardeigenmann/jpo)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5abc0256877f43e19564e71ca3c8f073)](https://www.codacy.com/app/richardeigenmann/JPO?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=richardeigenmann/JPO&amp;utm_campaign=Badge_Grade)
[![Sonarcloud Bugs](https://sonarcloud.io/api/project_badges/measure?project=Jpo&metric=bugs)](https://sonarcloud.io/api/project_badges/measure?project=Jpo&metric=bugs)
[![Sonarcloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=Jpo&metric=coverage)](https://sonarcloud.io/api/project_badges/measure?project=Jpo&metric=coverage)
[![Sonarcloud LinesOfCode](https://sonarcloud.io/api/project_badges/measure?project=Jpo&metric=ncloc)](https://sonarcloud.io/api/project_badges/measure?project=Jpo&metric=ncloc)
[![Sonarcloud Maintainability](https://sonarcloud.io/api/project_badges/measure?project=Jpo&metric=sqale_rating)](https://sonarcloud.io/api/project_badges/measure?project=Jpo&metric=sqale_rating)


Homepage: http://j-po.sourceforge.net/

Sonacloud: https://sonarcloud.io/dashboard?id=Jpo


## Experimental: Running with Flatpak

```bash
flatpak-builder build-dir --force-clean org.richinet.jpo.json
flatpak-builder --run build-dir org.richinet.jpo.json flatpak-run.sh
flatpak-builder --repo=repo --force-clean build-dir org.richinet.jpo.json
flatpak --user remote-add --no-gpg-verify --if-not-exists tutorial-repo repo
flatpak --user install tutorial-repo org.richinet.jpo
flatpak run org.richinet.jpo
flatpak remove org.richinet.jpo
```

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
