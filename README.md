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

