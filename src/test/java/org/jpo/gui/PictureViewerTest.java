package org.jpo.gui;

import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

/**
 * Tests for the PictureViewer
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
                final PictureViewer pictureViewer = new PictureViewer();
                assertNotNull( pictureViewer );
            } );
        } catch ( final InterruptedException | InvocationTargetException ex ) {
            fail( "This test didn't work. Exception: " + ex.getMessage() );
        }

    }
}
