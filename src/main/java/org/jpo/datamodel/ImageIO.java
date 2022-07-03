package org.jpo.datamodel;

/*
 Copyright (C) 2022  Richard Eigenmann.
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

import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageIO {

    private static final Logger LOGGER = Logger.getLogger(ImageIO.class.getName());

    private ImageIO() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * This method checks whether the JVM has an image reader for the supplied
     * File.
     *
     * @param file The file to be checked
     * @return true if the JVM has a reader false if not.
     */
    public static boolean jvmHasReader(final File file) {
        LOGGER.log(Level.FINE, "checking jvmHasReader for file {0}", file);
        if (!MimeTypes.isAPicture(file)) {
            LOGGER.log(Level.INFO, "File {0} ist not a picture according the MimeType {1}", new Object[]{file, MimeTypes.getMimeType(file)});
            return false;
        }
        try (final var testStream = new FileImageInputStream(file)) {
            final ImageReader reader = javax.imageio.ImageIO.getImageReaders(testStream).next();
            if (reader != null) {
                LOGGER.log(Level.FINE, "File {0} has a picture mime type and ImageIO has a reader for it. Class is {1}", new Object[]{file, reader.getClass()});
                return true;
            } else {
                LOGGER.log(Level.INFO, "Cant find an ImageIO reader for file {0}", file);
            }
        } catch (final IOException x) {
            LOGGER.log(Level.INFO, "IOException testing file {0}: {1}", new Object[]{file, x.getMessage()});
        } catch (final Exception e) {
            LOGGER.log(Level.INFO, "An unexpected error was caught: {0}", e.getMessage());
            Thread.dumpStack();
        }
        return false;
    }

    /**
     * Returns a reader for the Image
     * Can throw a java.util.NoSuchElementException if there is no available reader.
     */
    public static ImageReader getImageIOReader(final ImageInputStream iis) {
        final var readerIterator = javax.imageio.ImageIO.getImageReaders(iis);
        return readerIterator.next();
    }
}
