package org.jpo.gui;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;
import org.junit.Test;

/**
 * Tests for the PictureViewe
 *
 * @author Richard Eigenmann
 */
public class PictureViewerTest {

    /**
     * Test the listener
     */
    @Test
    public void testConstructor() {
        // TravisCI runs headless so we can't execute the below test
        if ( GraphicsEnvironment.isHeadless() ) {
            return;
        }
        try {
            SwingUtilities.invokeAndWait( () -> {
                PictureViewer pictureViewer = new PictureViewer();
                assertNotNull( pictureViewer );
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail( "This test didn't work. Exception: " + ex.getMessage() );
        }

    }
}
