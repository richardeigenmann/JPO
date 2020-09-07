package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the PictureInfo class
 *
 * @author Richard Eigenmann
 */
public class PictureInfoChangeEventTest {

    /**
     * Constructor and get
     */
    @Test
    public void testConstructor() {
        PictureInfo pictureInfo = new PictureInfo();
        PictureInfoChangeEvent pictureInfoChangeEvent = new PictureInfoChangeEvent( pictureInfo );
        assertNotNull( pictureInfoChangeEvent );
        assertEquals( pictureInfo, pictureInfoChangeEvent.getPictureInfo() );
    }

    @Test
    public void testSetThumbnailChanged() {
        PictureInfo pictureInfo = new PictureInfo();
        PictureInfoChangeEvent pictureInfoChangeEvent = new PictureInfoChangeEvent( pictureInfo );
        assertFalse( pictureInfoChangeEvent.getThumbnailChanged() );
        
        pictureInfoChangeEvent.setThumbnailChanged();
        assertTrue(pictureInfoChangeEvent.getThumbnailChanged());
    }
    
    @Test
    public void testCreationTimeChanged() {
        PictureInfo pictureInfo = new PictureInfo();
        PictureInfoChangeEvent pictureInfoChangeEvent = new PictureInfoChangeEvent( pictureInfo );
        assertFalse( pictureInfoChangeEvent.getCreationTimeChanged() );
        
        pictureInfoChangeEvent.setCreationTimeChanged();
        assertTrue(pictureInfoChangeEvent.getCreationTimeChanged());
    }
    
    
}
