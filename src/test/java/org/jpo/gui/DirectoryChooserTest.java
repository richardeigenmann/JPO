package org.jpo.gui;


import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;


/**
 * Tests for the Directory Chooser
 *
 * @author Richard Eigenmann
 */
public class DirectoryChooserTest {

    private int changesReceived;
    private File result;

    /**
     * Test the listener
     */
    @Test
    public void testListener() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final DirectoryChooser dc = new DirectoryChooser( "Title", DirectoryChooser.DIR_MUST_EXIST );
                dc.addChangeListener( ( ChangeEvent e ) -> changesReceived++);
                dc.setText( "/" );
                result = dc.getDirectory();
                // Checking that what went in is what comes out
                assertEquals( new File( "/" ), result );
                // Checking that the changeEvent was fired
                assertEquals( 1, changesReceived );
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail("This was not supposed to land in the catch clause: " + ex.getMessage());
            Thread.currentThread().interrupt();
        }

    }
}
