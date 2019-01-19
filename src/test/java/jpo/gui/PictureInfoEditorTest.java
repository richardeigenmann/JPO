package jpo.gui;

import jpo.cache.ThumbnailCreationQueue;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Objects;

import static junit.framework.TestCase.*;

/**
 * Tests for the PictureInfoEditor
 *
 * @author Richard Eigenmann
 */
public class PictureInfoEditorTest {

    @Test
    public void testConstructor() {
        // TravisCI runs headless so we can't execute the below test
        if ( GraphicsEnvironment.isHeadless() ) {
            return;
        }
        try {
            SwingUtilities.invokeAndWait( () -> {
                PictureInfo pictureInfo = new PictureInfo();
                final String JPG_IMAGE_FILE = "exif-test-nikon-d100-1.jpg";
                URL imageUrl = Objects.requireNonNull(PictureInfoEditorTest.class.getClassLoader().getResource(JPG_IMAGE_FILE));
                pictureInfo.setImageLocation(new File(imageUrl.getFile()));

                SortableDefaultMutableTreeNode n = new SortableDefaultMutableTreeNode( pictureInfo );
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
        // TravisCI runs headless so we can't execute the below test
        if ( GraphicsEnvironment.isHeadless() ) {
            return;
        }
        try {
            SwingUtilities.invokeAndWait( () -> {
                PictureInfo pictureInfo = new PictureInfo();
                final String JPG_IMAGE_FILE = "exif-test-nikon-d100-1.jpg";
                URL imageUrl = Objects.requireNonNull(PictureInfoEditorTest.class.getClassLoader().getResource(JPG_IMAGE_FILE));

                File imageFile = new File(imageUrl.getFile());
                pictureInfo.setImageLocation(imageFile);
                assertEquals(pictureInfo.getImageFile(), imageFile);

                SortableDefaultMutableTreeNode n = new SortableDefaultMutableTreeNode( pictureInfo );
                PictureInfoEditor pictureInfoEditor = new PictureInfoEditor(n);
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
