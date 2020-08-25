package org.jpo.gui;

import org.junit.Test;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;

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
                assertEquals( "Checking that what went in is what comes out", new File( "/" ), result );
                assertEquals( "Checking that the changeEvent was fired", 1, changesReceived );
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail("This was not supposed to land in the catch clause: " + ex.getMessage());
            Thread.currentThread().interrupt();
        }

    }
}
