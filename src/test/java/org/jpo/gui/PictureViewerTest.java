package org.jpo.gui;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

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
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final PictureViewer pictureViewer = new PictureViewer();
                assertNotNull( pictureViewer );
            } );
        } catch ( final InterruptedException | InvocationTargetException ex ) {
            fail( ex.getMessage() );
            Thread.currentThread().interrupt();
        }

    }
}
