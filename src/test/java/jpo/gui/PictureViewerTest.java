package jpo.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the Directory Chooser
 *
 * @author Richard Eigenmann
 */
public class PictureViewerTest {

    /**
     * Test the listener
     */
    @Test
    public void testConstructor() {
        try {
            SwingUtilities.invokeAndWait( () -> {
                PictureViewer pictureViewer = new PictureViewer();
                Assert.assertNotNull( pictureViewer );
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( PictureViewerTest.class.getName() ).log( Level.SEVERE, null, ex );
        }

    }
}
