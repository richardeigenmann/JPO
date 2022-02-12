package org.jpo.datamodel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
Copyright (C) 2002-2022  Richard Eigenmann.
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
                || "application/vnd.oasis.opendocument.text".equals(mimeType);
    }

    public static boolean isAMovie(final File file) {
        var mimeType = getMimeType(file);
        return mimeType.startsWith("video/");
    }

    public static boolean isAPicture(final File file) {
        var mimeType = getMimeType(file);
        return mimeType.startsWith("image/");
    }
}
