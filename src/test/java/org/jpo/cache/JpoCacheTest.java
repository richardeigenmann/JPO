package org.jpo.cache;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import org.jpo.datamodel.Settings;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2017-2023 Richard Eigenmann.
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

    private static final File IMAGE_FILE_1 = getFileFromResources("exif-test-nikon-d100-1.jpg");
    private static HashCode IMAGE_FILE_1_HASH_CODE;

    static {
        try {
            IMAGE_FILE_1_HASH_CODE = com.google.common.io.Files.asByteSource(Objects.requireNonNull(IMAGE_FILE_1)).hash(Hashing.sha256());
        } catch (IOException e) {
            fail(e);
        }
    }

    private static final long LENGTH_FILE_1 = IMAGE_FILE_1.length();
    private static final File IMAGE_FILE_2 = getFileFromResources("exif-test-samsung-s4.jpg");

    private static final long LENGTH_FILE_2 = IMAGE_FILE_2.length();

    static {
        LOGGER.log(Level.INFO, "Asserting that file {0} has the right number of bytes, 21599 (actual: {1})", new Object[]{IMAGE_FILE_1, LENGTH_FILE_1});
        assertEquals(21599, LENGTH_FILE_1);
        LOGGER.log(Level.INFO, "Asserting that file {0} has the right number of bytes, 2354328 (actual: {1})", new Object[]{IMAGE_FILE_2, LENGTH_FILE_2});
        assertEquals(2354328, LENGTH_FILE_2);
    }

    private static File getFileFromResources(final String resourceName) {
        try {
            return new File(ClassLoader.getSystemResources(resourceName).nextElement().toURI());
        } catch (final URISyntaxException | IOException e) {
            LOGGER.log(Level.SEVERE, "Could not retrieve resource {0}. Exception: {1}", new Object[]{resourceName, e.getMessage()});
            return null;
        }
    }

    @Test
    void testLoadProperties() {
        Settings.loadSettings();
        final var props = JpoCache.loadProperties();
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
            JpoCache.removeFromHighresCache(IMAGE_FILE_1_HASH_CODE.toString());
            final var imageBytes = JpoCache.getHighresImageBytes(IMAGE_FILE_1_HASH_CODE.toString(), IMAGE_FILE_1);
            assertEquals(LENGTH_FILE_1, imageBytes.getBytes().length);
            assertFalse(imageBytes.isRetrievedFromCache());
            final var imageBytes2 = JpoCache.getHighresImageBytes(IMAGE_FILE_1_HASH_CODE.toString(), IMAGE_FILE_1);
            assertEquals(LENGTH_FILE_1, imageBytes2.getBytes().length);
            assertTrue(imageBytes2.isRetrievedFromCache());
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetHighresImageBytesFileChanged() {
        assumeFalse( System.getProperty("os.name").toLowerCase().startsWith("win") );
        // doesn't work on windows.

        try {
            final var tempFile = File.createTempFile("testImage", ".jpg");
            LOGGER.log(Level.INFO, "Copying {0} to temporary file {1}", new Object[]{IMAGE_FILE_1, tempFile});
            com.google.common.io.Files.copy(IMAGE_FILE_1, tempFile);
            final var hashCode1 = com.google.common.io.Files.asByteSource(tempFile).hash(Hashing.sha256());
            LOGGER.log(Level.INFO, "Source vs Target sha256:\n{0}\n{1}", new Object[]{IMAGE_FILE_1_HASH_CODE, hashCode1});

            // make sure the temp file is not in the cache
            JpoCache.removeFromHighresCache(hashCode1.toString());
            final var imageBytes = JpoCache.getHighresImageBytes(hashCode1.toString(), tempFile);
            LOGGER.log(Level.INFO,
                    "asserting that the tempFile {0} was not retrieved from cache (isRetrievedFromCache: {1}) and has {2} bytes ({3})",
                    new Object[]{tempFile, imageBytes.isRetrievedFromCache(), LENGTH_FILE_1, imageBytes.getBytes().length});
            assertEquals(LENGTH_FILE_1, imageBytes.getBytes().length);
            assertFalse(imageBytes.isRetrievedFromCache());

            com.google.common.io.Files.copy(IMAGE_FILE_2, tempFile);
            // some test runs fail. The overwrite may not have happened. Trying to force a sync. Maybe that helps?
            final var dummyFileToForceASync = new File("DummyFileToForceASync");
            try (final var fileOutputStream = new FileOutputStream(dummyFileToForceASync)) {
                fileOutputStream.getFD().sync();
            }
            Files.delete(dummyFileToForceASync.toPath());


            final var hashCode2 = com.google.common.io.Files.asByteSource(tempFile).hash(Hashing.sha256());
            JpoCache.removeFromHighresCache(hashCode2.toString());
            final var imageBytes2 = JpoCache.getHighresImageBytes(hashCode2.toString(), tempFile);
            LOGGER.log(Level.INFO, "Original vs Overwritten Target sha256:\n{0}\n{1}", new Object[]{IMAGE_FILE_1_HASH_CODE, hashCode2});
            LOGGER.log(Level.INFO,
                    "asserting that the overwritten tempFile {0} was not retrieved from cache (actual: {1}) and has 2354328 bytes ({2})",
                    new Object[]{tempFile, imageBytes2.isRetrievedFromCache(), imageBytes2.getBytes().length});
            assertEquals(LENGTH_FILE_2, imageBytes2.getBytes().length);
            assertFalse(imageBytes2.isRetrievedFromCache());

            final var imageBytes3 = JpoCache.getHighresImageBytes(hashCode2.toString(), tempFile);
            LOGGER.log(Level.INFO,
                    "asserting that a new request for unchanged tempFile {0} was retrieved from cache (actual: {1}) and has 2354328 bytes ({2})",
                    new Object[]{tempFile, imageBytes3.isRetrievedFromCache(), imageBytes3.getBytes().length});
            assertEquals(LENGTH_FILE_2, imageBytes3.getBytes().length);
            assertTrue(imageBytes3.isRetrievedFromCache());

            Files.delete(tempFile.toPath());
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testGetThumbnailImageBytes() {
        final var key = String.format("%s-%fdeg-w:%dpx-h:%dpx", IMAGE_FILE_1, 0.0, 350, 350);
        JpoCache.removeFromThumbnailCache(key);
        final var imageBytes = JpoCache.getThumbnailImageBytes(IMAGE_FILE_1, 0.0f, new Dimension(350, 350));
        assertEquals(13094, imageBytes.getBytes().length);
        assertFalse(imageBytes.isRetrievedFromCache());
        final var imageBytes2 = JpoCache.getThumbnailImageBytes(IMAGE_FILE_1, 0.0f, new Dimension(350, 350));
        assertEquals(13094, imageBytes2.getBytes().length);
        assertTrue(imageBytes2.isRetrievedFromCache());
    }

}
