package org.jpo.cache;

import org.jpo.datamodel.Settings;
import org.jpo.export.WebsiteGeneratorTest;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Properties;

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
 *
 * @author Richard Eigenmann
 */
public class JpoCacheTest {


    @Test
    public void testLoadProperties() {
        Settings.loadSettings();
        Properties props = JpoCache.loadProperties();
        // Expecting more than 30 properties to be defined
        assertTrue(Objects.requireNonNull(props).entrySet().size() > 30);
    }

    @Test
    public void testCreateCacheInstance() {
        JpoCache jpoCache = JpoCache.getInstance();
        jpoCache.shutdown();
    }

    @Test
    public void testGetFolderIconDimensions() {
        assertEquals(new Dimension(350, 295), JpoCache.getGroupThumbnailDimension());
    }

    @Test
    public void testGetHighresImageBytes() {
        try {
            final File imageFile = new File(WebsiteGeneratorTest.class.getClassLoader().getResource("exif-test-nikon-d100-1.jpg").toURI());
            JpoCache.getInstance().removeFromHighresCache(imageFile);
            final ImageBytes imageBytes = JpoCache.getInstance().getHighresImageBytes(imageFile);
            assertEquals(21599, imageBytes.getBytes().length);
            assertFalse(imageBytes.isRetrievedFromCache());
            final ImageBytes imageBytes2 = JpoCache.getInstance().getHighresImageBytes(imageFile);
            assertEquals(21599, imageBytes2.getBytes().length);
            assertTrue(imageBytes2.isRetrievedFromCache());
        } catch (final URISyntaxException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetHighresImageBytesFileChanged() {
        try {
            final File imageFile1 = new File(WebsiteGeneratorTest.class.getClassLoader().getResource("exif-test-nikon-d100-1.jpg").toURI());
            final File tempFile = File.createTempFile("testImage", ".jpg");
            com.google.common.io.Files.copy(imageFile1, tempFile);

            JpoCache.getInstance().removeFromHighresCache(tempFile);
            final ImageBytes imageBytes = JpoCache.getInstance().getHighresImageBytes(tempFile);
            assertEquals(21599, imageBytes.getBytes().length);
            assertFalse(imageBytes.isRetrievedFromCache());

            final File imageFile2 = new File(WebsiteGeneratorTest.class.getClassLoader().getResource("exif-test-samsung-s4.jpg").toURI());
            com.google.common.io.Files.copy(imageFile2, tempFile);
            final ImageBytes imageBytes2 = JpoCache.getInstance().getHighresImageBytes(tempFile);
            assertEquals(2354328, imageBytes2.getBytes().length);
            assertFalse(imageBytes2.isRetrievedFromCache());

            final ImageBytes imageBytes3 = JpoCache.getInstance().getHighresImageBytes(tempFile);
            assertEquals(2354328, imageBytes3.getBytes().length);
            assertTrue(imageBytes3.isRetrievedFromCache());
        } catch (final URISyntaxException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetThumbnailImageBytes() {
        try {
            final File imageFile = new File(WebsiteGeneratorTest.class.getClassLoader().getResource("exif-test-nikon-d100-1.jpg").toURI());
            final String key = String.format("%s-%fdeg-w:%dpx-h:%dpx", imageFile, 0.0, 350, 350);
            JpoCache.getInstance().removeFromThumbnailCache(key);
            final ImageBytes imageBytes = JpoCache.getInstance().getThumbnailImageBytes(imageFile, 0.0f, new Dimension(350, 350));
            assertEquals(13094, imageBytes.getBytes().length);
            assertFalse(imageBytes.isRetrievedFromCache());
            final ImageBytes imageBytes2 = JpoCache.getInstance().getThumbnailImageBytes(imageFile, 0.0f, new Dimension(350, 350));
            assertEquals(13094, imageBytes2.getBytes().length);
            assertTrue(imageBytes2.isRetrievedFromCache());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetThumbnailImageBytesFileChanged() {
        try {
            final File imageFile1 = new File(WebsiteGeneratorTest.class.getClassLoader().getResource("exif-test-nikon-d100-1.jpg").toURI());
            final File tempFile = File.createTempFile("testImage", ".jpg");
            com.google.common.io.Files.copy(imageFile1, tempFile);

            final String key = String.format("%s-%fdeg-w:%dpx-h:%dpx", tempFile, 0.0, 350, 350);
            JpoCache.getInstance().removeFromThumbnailCache(key);
            final ImageBytes imageBytes = JpoCache.getInstance().getThumbnailImageBytes(tempFile, 0.0f, new Dimension(350, 350));
            assertEquals(13094, imageBytes.getBytes().length);
            assertFalse(imageBytes.isRetrievedFromCache());

            final File imageFile2 = new File(WebsiteGeneratorTest.class.getClassLoader().getResource("exif-test-samsung-s4.jpg").toURI());
            com.google.common.io.Files.copy(imageFile2, tempFile);
            final ImageBytes imageBytes2 = JpoCache.getInstance().getThumbnailImageBytes(tempFile, 0.0f, new Dimension(350, 350));
            assertEquals(16076, imageBytes2.getBytes().length);
            assertFalse(imageBytes2.isRetrievedFromCache());

            final ImageBytes imageBytes3 = JpoCache.getInstance().getThumbnailImageBytes(tempFile, 0.0f, new Dimension(350, 350));
            assertEquals(16076, imageBytes3.getBytes().length);
            assertTrue(imageBytes3.isRetrievedFromCache());
        } catch (final URISyntaxException | IOException e) {
            fail(e.getMessage());
        }
    }

}
