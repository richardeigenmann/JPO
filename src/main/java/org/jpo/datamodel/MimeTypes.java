package org.jpo.datamodel;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
Copyright (C) 2002-2023 Richard Eigenmann.
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


public class MimeTypes {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(MimeTypes.class.getName());
    static {
        LOGGER.setLevel(Level.FINE);
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE);
        LOGGER.addHandler(consoleHandler);
    }


    private MimeTypes() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns the mime type of the supplied file. If something went wrong you get back the string "null" not the object null
     *
     * @param file the file to query
     * @return the mime type as a String or the String "null"
     */
    public static String getMimeType(final File file) {
        try {
            if (! Files.exists(file.toPath())) {
                LOGGER.log(Level.SEVERE, "File {0} does not exist!", file);
                return "null";
            } else if (!file.canRead()) {
                LOGGER.log(Level.SEVERE, "File {0} can not be read!", file);
                return "null";
            }

            var mimeType = Files.probeContentType(file.toPath());
            LOGGER.log(Level.FINE, "File {0} is mime-type: {1}", new Object[]{file, mimeType});
            return mimeType != null ? mimeType : "null";
        } catch (final IOException e) {
            LOGGER.log(Level.WARNING, "Failed to probeContentType of file {0} Exception was: {1}", new Object[]{file, e.getMessage()});
            return "null";
        }
    }

    public static boolean isADocument(final File file) {
        var mimeType = getMimeType(file);
        return "application/msword".equals(mimeType)
                || "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mimeType)
                || "application/vnd.oasis.opendocument.text".equals(mimeType)
                || "application/pdf".equals(mimeType);
    }

    public static boolean isAMovie(final File file) {
        var mimeType = getMimeType(file);
        return mimeType.startsWith("video/") || "application/x-troff-msvideo".equals(mimeType) || "application/x-matroska".equals(mimeType);
    }

    /**
     * TravisCI has an interesting JVM which doesn't detect the mime type of hdr and tga images and thus fails tests.
     */
    static final List<String> OVERRIDE_PICTURE_TYPES = Arrays.asList("iff", "hdr", "pct", "tga", "sgi");

    public static boolean isAPicture(final File file) {
        var mimeType = getMimeType(file);
        if (mimeType.equals("null") && OVERRIDE_PICTURE_TYPES.contains(FilenameUtils.getExtension(file.toPath().toString()).toLowerCase())) {
            LOGGER.log(Level.SEVERE, "The JVM/OS failed to recognize the file {0} as an image. Overriding this because of its filename extension", file);
            return true;
        }
        return mimeType.startsWith("image/");
    }
}
