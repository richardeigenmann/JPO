package org.jpo.datamodel;

import org.jpo.export.WebsiteGenerator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.logging.Logger;

/*
Copyright (C) 2024 Richard Eigenmann.
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version. This program is distributed
in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
more details. You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
The license is in gpl.txt.
See http://www.gnu.org/copyleft/gpl.html for the details.
 */

public class BrokenThumbnailImage {

    private static final String BROKEN_THUMBNAIL_FILENAME = "org/jpo/images/broken_thumbnail.gif";

    private static final String BROKEN_THUMBNAIL_SHA256 = "cb2a91f3116eee469fa3d75b1a8017e49212c96d050ca003e62af0616dcdbdc7";

    private static final Logger LOGGER = Logger.getLogger(BrokenThumbnailImage.class.getName());

    private BrokenThumbnailImage() {
        throw new IllegalStateException("Utility class");
    }


    public static BufferedImage getImage() throws IOException {
        try {
            final var brokenThumbnailImageFile = new File(Objects.requireNonNull(WebsiteGenerator.class.getClassLoader().getResource(BROKEN_THUMBNAIL_FILENAME)).toURI());
            final var sourcePicture = new SourcePicture();
            sourcePicture.loadPicture(BROKEN_THUMBNAIL_SHA256, brokenThumbnailImageFile, 0f);
            return sourcePicture.getSourceBufferedImage();
        } catch (final URISyntaxException e) {
            throw new IOException("Could not load the broken_thumbnail.gif from resource: " + e.getMessage());
        }
    }

    public static void getImage(ScalablePicture scalablePicture) throws IOException {
        try {
            final var brokenThumbnailImageFile = new File(Objects.requireNonNull(WebsiteGenerator.class.getClassLoader().getResource(BROKEN_THUMBNAIL_FILENAME)).toURI());
            scalablePicture.loadPictureImd(BROKEN_THUMBNAIL_SHA256, brokenThumbnailImageFile, 0f);
        } catch (final URISyntaxException e) {
            throw new IOException("Could not load the broken_thumbnail.gif from resource: " + e.getMessage());
        }
    }

}
