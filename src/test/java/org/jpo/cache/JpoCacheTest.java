package org.jpo.cache;

import org.jpo.datamodel.Settings;
import org.jpo.export.WebsiteGeneratorTest;
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
 Copyright (C) 2017-2020  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 without even the implied warranty of MERCHANTABILITY or FITNESS 
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
public class JpoCacheTest {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger(JpoCacheTest.class.getName());

    private static final File imageFile1 = getFileFromResouces("exif-test-nikon-d100-1.jpg");
    private static final long lenghtFile1 = imageFile1.length();
    private static final File imageFile2 = getFileFromResouces("exif-test-samsung-s4.jpg");
    private static final long lenghtFile2 = imageFile2.length();

    static {
        LOGGER.log(Level.INFO, "Asserting that file {0} has the right number ob bytes, 21599 (actual: {1})", new Object[]{imageFile1, lenghtFile1});
        assertEquals(21599, lenghtFile1);
        LOGGER.log(Level.INFO, "Asserting that file {0} has the right number ob bytes, 2354328 (actual: {1})", new Object[]{imageFile2, lenghtFile2});
        assertEquals(2354328, lenghtFile2);
    }

    private static File getFileFromResouces(final String filename) {
        try {
            return new File(WebsiteGeneratorTest.class.getClassLoader().getResource(filename).toURI());
        } catch (URISyntaxException e) {
            LOGGER.log(Level.SEVERE, "Could not retrieve test resource image {0}", filename);
            return null;
        }
    }

    @Test
    public void testLoadProperties() {
        Settings.loadSettings();
        Properties props = JpoCache.loadProperties();
        // Expecting more than x properties to be defined in the cache.ccf file
        assertTrue(Objects.requireNonNull(props).entrySet().size() > 15);
    }

    @Test
    public void testGetFolderIconDimensions() {
        assertEquals(new Dimension(350, 295), JpoCache.getGroupThumbnailDimension());
    }

    @Test
    public void testGetHighresImageBytes() {
        try {
            JpoCache.removeFromHighresCache(imageFile1);
            final ImageBytes imageBytes = JpoCache.getHighresImageBytes(imageFile1);
            assertEquals(lenghtFile1, imageBytes.getBytes().length);
            assertFalse(imageBytes.isRetrievedFromCache());
            final ImageBytes imageBytes2 = JpoCache.getHighresImageBytes(imageFile1);
            assertEquals(lenghtFile1, imageBytes2.getBytes().length);
            assertTrue(imageBytes2.isRetrievedFromCache());
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetHighresImageBytesFileChanged() {
        try {
            final File tempFile = File.createTempFile("testImage", ".jpg");
            LOGGER.log(Level.INFO, "Creating temporary file {0}", tempFile);
            com.google.common.io.Files.copy(imageFile1, tempFile);

            // make sure the temp file is not in the cache
            JpoCache.removeFromHighresCache(tempFile);
            final ImageBytes imageBytes = JpoCache.getHighresImageBytes(tempFile);
            LOGGER.log(Level.INFO,
                    "asserting that the tempFile {0} was not retrieved from cache (actual: {1}) and has 21599 bytes ({2})",
                    new Object[]{tempFile, imageBytes.isRetrievedFromCache(), imageBytes.getBytes().length});
            assertEquals(lenghtFile1, imageBytes.getBytes().length);
            assertFalse(imageBytes.isRetrievedFromCache());

            com.google.common.io.Files.copy(imageFile2, tempFile);
            // some test runs fail. The overwrite may not have happened. Trying to force a sync. Maybe that helps?
            new FileOutputStream("gaga").getFD().sync();
            final ImageBytes imageBytes2 = JpoCache.getHighresImageBytes(tempFile);
            LOGGER.log(Level.INFO,
                    "asserting that the overwritten tempFile {0} was not retrieved from cache (actual: {1}) and has 2354328 bytes ({2})",
                    new Object[]{tempFile, imageBytes.isRetrievedFromCache(), imageBytes.getBytes().length});
            assertEquals(lenghtFile2, imageBytes2.getBytes().length);
            assertFalse(imageBytes2.isRetrievedFromCache());

            final ImageBytes imageBytes3 = JpoCache.getHighresImageBytes(tempFile);
            LOGGER.log(Level.INFO,
                    "asserting that a new request for unchanged tempFile {0} was retrieved from cache (actual: {1}) and has 2354328 bytes ({2})",
                    new Object[]{tempFile, imageBytes.isRetrievedFromCache(), imageBytes.getBytes().length});
            assertEquals(lenghtFile2, imageBytes3.getBytes().length);
            assertTrue(imageBytes3.isRetrievedFromCache());

            Files.delete(tempFile.toPath());
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetThumbnailImageBytes() {
        final String key = String.format("%s-%fdeg-w:%dpx-h:%dpx", imageFile1, 0.0, 350, 350);
        JpoCache.removeFromThumbnailCache(key);
        final ImageBytes imageBytes = JpoCache.getThumbnailImageBytes(imageFile1, 0.0f, new Dimension(350, 350));
        assertEquals(13094, imageBytes.getBytes().length);
        assertFalse(imageBytes.isRetrievedFromCache());
        final ImageBytes imageBytes2 = JpoCache.getThumbnailImageBytes(imageFile1, 0.0f, new Dimension(350, 350));
        assertEquals(13094, imageBytes2.getBytes().length);
        assertTrue(imageBytes2.isRetrievedFromCache());
    }

    @Test
    public void testGetThumbnailImageBytesFileChanged() {
        try {
            final File tempFile = File.createTempFile("testImage", ".jpg");
            com.google.common.io.Files.copy(imageFile1, tempFile);

            final String key = String.format("%s-%fdeg-w:%dpx-h:%dpx", tempFile, 0.0, 350, 350);
            JpoCache.removeFromThumbnailCache(key);
            final ImageBytes imageBytes = JpoCache.getThumbnailImageBytes(tempFile, 0.0f, new Dimension(350, 350));
            assertEquals(13094, imageBytes.getBytes().length);
            assertFalse(imageBytes.isRetrievedFromCache());

            com.google.common.io.Files.copy(imageFile2, tempFile);
            final ImageBytes imageBytes2 = JpoCache.getThumbnailImageBytes(tempFile, 0.0f, new Dimension(350, 350));
            assertEquals(16076, imageBytes2.getBytes().length);
            assertFalse(imageBytes2.isRetrievedFromCache());

            final ImageBytes imageBytes3 = JpoCache.getThumbnailImageBytes(tempFile, 0.0f, new Dimension(350, 350));
            assertEquals(16076, imageBytes3.getBytes().length);
            assertTrue(imageBytes3.isRetrievedFromCache());
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

}
