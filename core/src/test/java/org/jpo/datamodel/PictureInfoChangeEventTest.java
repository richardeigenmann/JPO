package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the PictureInfo class
 *
 * @author Richard Eigenmann
 */
class PictureInfoChangeEventTest {

    /**
     * Constructor and get
     */
    @Test
    void testConstructor() {
        final PictureInfo pictureInfo = new PictureInfo();
        final PictureInfoChangeEvent pictureInfoChangeEvent = new PictureInfoChangeEvent(pictureInfo);
        assertNotNull(pictureInfoChangeEvent);
        assertEquals(pictureInfo, pictureInfoChangeEvent.getPictureInfo());
    }

    @Test
    void testSetThumbnailChanged() {
        final PictureInfo pictureInfo = new PictureInfo();
        final PictureInfoChangeEvent pictureInfoChangeEvent = new PictureInfoChangeEvent(pictureInfo);
        assertFalse(pictureInfoChangeEvent.getThumbnailChanged());

        pictureInfoChangeEvent.setThumbnailChanged();
        assertTrue(pictureInfoChangeEvent.getThumbnailChanged());
    }

    @Test
    void testCreationTimeChanged() {
        final PictureInfo pictureInfo = new PictureInfo();
        final PictureInfoChangeEvent pictureInfoChangeEvent = new PictureInfoChangeEvent(pictureInfo);
        assertFalse(pictureInfoChangeEvent.getCreationTimeChanged());

        pictureInfoChangeEvent.setCreationTimeChanged();
        assertTrue(pictureInfoChangeEvent.getCreationTimeChanged());
    }
    
    
}
