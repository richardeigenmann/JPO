package jpo.gui;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;
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
                assertNotNull( pictureViewer );
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail("This test didn't work. Exception: " + ex.getMessage());
        }

    }
}
