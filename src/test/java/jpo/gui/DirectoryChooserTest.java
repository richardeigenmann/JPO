package jpo.gui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

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
        final Runnable r = new Runnable() {

            @Override
            public void run() {
                final DirectoryChooser dc = new DirectoryChooser( "Title", DirectoryChooser.DIR_MUST_EXIST );
                dc.addChangeListener( new ChangeListener() {

                    @Override
                    public void stateChanged( ChangeEvent e ) {
                        changesReceived++;
                    }
                } );
                dc.setText( "/" );
                result = dc.getDirectory();
            }
        };
        try {
            SwingUtilities.invokeAndWait( r );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( DirectoryChooserTest.class.getName() ).log( Level.SEVERE, null, ex );
        }
        assertEquals( "Checking that what went in is what comes out", new File( "/" ), result );
        assertEquals( "Checking that the changeEvent was fired", 1, changesReceived );

    }
}
