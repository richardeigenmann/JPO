package org.jpo.gui.swing;

import org.jpo.cache.ThumbnailCreationQueue;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Objects;

import static junit.framework.TestCase.*;
import static org.junit.Assume.assumeFalse;

/**
 * Tests for the PictureInfoEditor
 *
 * @author Richard Eigenmann
 */
public class PictureInfoEditorTest {

    @Test
    public void testConstructor() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final PictureInfo pictureInfo = new PictureInfo();
                final String JPG_IMAGE_FILE = "exif-test-nikon-d100-1.jpg";
                final URL imageUrl = Objects.requireNonNull(PictureInfoEditorTest.class.getClassLoader().getResource(JPG_IMAGE_FILE));
                pictureInfo.setImageLocation(new File(imageUrl.getFile()));

                final SortableDefaultMutableTreeNode n = new SortableDefaultMutableTreeNode( pictureInfo );
                PictureInfoEditor pictureInfoEditor = new PictureInfoEditor(n);
                assertNotNull( pictureInfoEditor );
                ThumbnailCreationQueue.clear(); // the created request confuses other tests
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail( "This test didn't work. Exception: " + ex.getMessage() );
        }
    }


    @Test
    public void testFileNameCorruption() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final PictureInfo pictureInfo = new PictureInfo();
                final String JPG_IMAGE_FILE = "exif-test-nikon-d100-1.jpg";
                final URL imageUrl = Objects.requireNonNull(PictureInfoEditorTest.class.getClassLoader().getResource(JPG_IMAGE_FILE));

                final File imageFile = new File(imageUrl.getFile());
                pictureInfo.setImageLocation(imageFile);
                assertEquals(pictureInfo.getImageFile(), imageFile);

                final SortableDefaultMutableTreeNode n = new SortableDefaultMutableTreeNode( pictureInfo );
                final PictureInfoEditor pictureInfoEditor = new PictureInfoEditor(n);
                assertNotNull( pictureInfoEditor );

                pictureInfoEditor.callSaveFieldData();
                assertEquals("Expectation is that after saving without changing file stays the same",  imageFile, pictureInfo.getImageFile());
                ThumbnailCreationQueue.clear();  // the created request confuses other tests
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail( "This test didn't work. Exception: " + ex.getMessage() );
        }
    }


}
