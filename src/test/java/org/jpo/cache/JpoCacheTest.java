package org.jpo.cache;

import org.jpo.datamodel.Settings;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/*
 Copyright (C) 2017-2021  Richard Eigenmann.
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


/**
 * @author Richard Eigenmann
 */
class JpoCacheTest {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger(JpoCacheTest.class.getName());

    private static final File IMAGE_FILE_1 = getFileFromResouces("exif-test-nikon-d100-1.jpg");
    private static final long LENGHT_FILE_1 = IMAGE_FILE_1.length();
    private static final File IMAGE_FILE_2 = getFileFromResouces("exif-test-samsung-s4.jpg");
    private static final long LENGHT_FILE_2 = IMAGE_FILE_2.length();

    static {
        LOGGER.log(Level.INFO, "Asserting that file {0} has the right number ob bytes, 21599 (actual: {1})", new Object[]{IMAGE_FILE_1, LENGHT_FILE_1});
        assertEquals(21599, LENGHT_FILE_1);
        LOGGER.log(Level.INFO, "Asserting that file {0} has the right number ob bytes, 2354328 (actual: {1})", new Object[]{IMAGE_FILE_2, LENGHT_FILE_2});
        assertEquals(2354328, LENGHT_FILE_2);
    }

    private static File getFileFromResouces(final String filename) {
        try {
            return new File(JpoCacheTest.class.getClassLoader().getResource(filename).toURI());
        } catch (URISyntaxException e) {
            LOGGER.log(Level.SEVERE, "Could not retrieve test resource image {0}", filename);
            return null;
        }
    }

    @Test
    void testLoadProperties() {
        Settings.loadSettings();
        Properties props = JpoCache.loadProperties();
        // Expecting more than x properties to be defined in the cache.ccf file
        assertTrue(Objects.requireNonNull(props).entrySet().size() > 15);
    }

    @Test
    void testGetFolderIconDimensions() {
        assertEquals(new Dimension(350, 295), JpoCache.getGroupThumbnailDimension());
    }

    @Test
    void testGetHighresImageBytes() {
        try {
            JpoCache.removeFromHighresCache(IMAGE_FILE_1);
            final var imageBytes = JpoCache.getHighresImageBytes(IMAGE_FILE_1);
            assertEquals(LENGHT_FILE_1, imageBytes.getBytes().length);
            assertFalse(imageBytes.isRetrievedFromCache());
            final var imageBytes2 = JpoCache.getHighresImageBytes(IMAGE_FILE_1);
            assertEquals(LENGHT_FILE_1, imageBytes2.getBytes().length);
            assertTrue(imageBytes2.isRetrievedFromCache());
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetHighresImageBytesFileChanged() {
        try {
            final var tempFile = File.createTempFile("testImage", ".jpg");
            LOGGER.log(Level.INFO, "Creating temporary file {0}", tempFile);
            com.google.common.io.Files.copy(IMAGE_FILE_1, tempFile);

            // make sure the temp file is not in the cache
            JpoCache.removeFromHighresCache(tempFile);
            final var imageBytes = JpoCache.getHighresImageBytes(tempFile);
            LOGGER.log(Level.INFO,
                    "asserting that the tempFile {0} was not retrieved from cache (actual: {1}) and has 21599 bytes ({2})",
                    new Object[]{tempFile, imageBytes.isRetrievedFromCache(), imageBytes.getBytes().length});
            assertEquals(LENGHT_FILE_1, imageBytes.getBytes().length);
            assertFalse(imageBytes.isRetrievedFromCache());

            com.google.common.io.Files.copy(IMAGE_FILE_2, tempFile);
            // some test runs fail. The overwrite may not have happened. Trying to force a sync. Maybe that helps?
            final var dummyFileToForceASync = new File("DummyFileToForceASync");
            try (final var fos = new FileOutputStream(dummyFileToForceASync)) {
                fos.getFD().sync();
            }
            Files.delete(dummyFileToForceASync.toPath());


            final var imageBytes2 = JpoCache.getHighresImageBytes(tempFile);
            LOGGER.log(Level.INFO,
                    "asserting that the overwritten tempFile {0} was not retrieved from cache (actual: {1}) and has 2354328 bytes ({2})",
                    new Object[]{tempFile, imageBytes.isRetrievedFromCache(), imageBytes.getBytes().length});
            assertEquals(LENGHT_FILE_2, imageBytes2.getBytes().length);
            assertFalse(imageBytes2.isRetrievedFromCache());

            final var imageBytes3 = JpoCache.getHighresImageBytes(tempFile);
            LOGGER.log(Level.INFO,
                    "asserting that a new request for unchanged tempFile {0} was retrieved from cache (actual: {1}) and has 2354328 bytes ({2})",
                    new Object[]{tempFile, imageBytes.isRetrievedFromCache(), imageBytes.getBytes().length});
            assertEquals(LENGHT_FILE_2, imageBytes3.getBytes().length);
            assertTrue(imageBytes3.isRetrievedFromCache());

            Files.delete(tempFile.toPath());
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetThumbnailImageBytes() {
        final String key = String.format("%s-%fdeg-w:%dpx-h:%dpx", IMAGE_FILE_1, 0.0, 350, 350);
        JpoCache.removeFromThumbnailCache(key);
        final ImageBytes imageBytes = JpoCache.getThumbnailImageBytes(IMAGE_FILE_1, 0.0f, new Dimension(350, 350));
        assertEquals(13094, imageBytes.getBytes().length);
        assertFalse(imageBytes.isRetrievedFromCache());
        final ImageBytes imageBytes2 = JpoCache.getThumbnailImageBytes(IMAGE_FILE_1, 0.0f, new Dimension(350, 350));
        assertEquals(13094, imageBytes2.getBytes().length);
        assertTrue(imageBytes2.isRetrievedFromCache());
    }

    @Test
    void testGetThumbnailImageBytesFileChanged() {
        try {
            final var tempFile = File.createTempFile("testImage", ".jpg");
            com.google.common.io.Files.copy(IMAGE_FILE_1, tempFile);

            final var key = String.format("%s-%fdeg-w:%dpx-h:%dpx", tempFile, 0.0, 350, 350);
            JpoCache.removeFromThumbnailCache(key);
            final var imageBytes = JpoCache.getThumbnailImageBytes(tempFile, 0.0f, new Dimension(350, 350));
            assertEquals(13094, imageBytes.getBytes().length);
            assertFalse(imageBytes.isRetrievedFromCache());

            com.google.common.io.Files.copy(IMAGE_FILE_2, tempFile);
            final var imageBytes2 = JpoCache.getThumbnailImageBytes(tempFile, 0.0f, new Dimension(350, 350));
            assertEquals(16076, imageBytes2.getBytes().length);
            assertFalse(imageBytes2.isRetrievedFromCache());

            final var imageBytes3 = JpoCache.getThumbnailImageBytes(tempFile, 0.0f, new Dimension(350, 350));
            assertEquals(16076, imageBytes3.getBytes().length);
            assertTrue(imageBytes3.isRetrievedFromCache());

            Files.delete(tempFile.toPath());
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

}
