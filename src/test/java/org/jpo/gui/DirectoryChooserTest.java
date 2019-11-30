package org.jpo.gui;

import org.junit.Test;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

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
        // TravisCI runs headless so we can't execute the below test
        if ( GraphicsEnvironment.isHeadless() ) {
            return;
        }
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
            Logger.getLogger( DirectoryChooserTest.class.getName() ).log( Level.SEVERE, null, ex );
            fail("This was not supposed to land in the catch clause");
        }

    }
}
