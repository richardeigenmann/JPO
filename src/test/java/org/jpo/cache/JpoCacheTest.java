package org.jpo.cache;

import org.jpo.datamodel.Settings;
import org.jpo.export.WebsiteGeneratorTest;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
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
    public void testGetThumbnailImageBytes() {
        try {
            final File imageFile = new File(WebsiteGeneratorTest.class.getClassLoader().getResource("exif-test-nikon-d100-1.jpg").toURI());
            final ImageBytes imageBytes = JpoCache.getInstance().getThumbnailImageBytes(imageFile, 0.0f, new Dimension(350, 350));
            assertEquals(imageBytes.getBytes().length, 13094);
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
    }
}
