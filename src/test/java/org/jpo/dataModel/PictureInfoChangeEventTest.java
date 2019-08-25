package org.jpo.dataModel;

import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertNotNull( pictureInfoChangeEvent );
        assertEquals( pictureInfo, pictureInfoChangeEvent.getPictureInfo() );
    }

    @Test
    public void testSetThumbnailChanged() {
        PictureInfo pictureInfo = new PictureInfo();
        PictureInfoChangeEvent pictureInfoChangeEvent = new PictureInfoChangeEvent( pictureInfo );
        assertFalse( pictureInfoChangeEvent.getThumbnailChanged() );
        
        pictureInfoChangeEvent.setThumbnailChanged();
        TestCase.assertTrue(pictureInfoChangeEvent.getThumbnailChanged());
    }
    
    @Test
    public void testCreationTimeChanged() {
        PictureInfo pictureInfo = new PictureInfo();
        PictureInfoChangeEvent pictureInfoChangeEvent = new PictureInfoChangeEvent( pictureInfo );
        assertFalse( pictureInfoChangeEvent.getCreationTimeChanged() );
        
        pictureInfoChangeEvent.setCreationTimeChanged();
        TestCase.assertTrue(pictureInfoChangeEvent.getCreationTimeChanged());
    }
    
    
}
