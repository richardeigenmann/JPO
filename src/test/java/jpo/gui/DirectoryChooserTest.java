package jpo.gui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import junit.framework.TestCase;

/**
 *
 * @author Richard Eigenmann
 */
public class DirectoryChooserTest extends TestCase {

    public DirectoryChooserTest( String testName ) {
        super( testName );
    }
    int changesReceived = 0;
    File result = null;

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
        } catch ( InterruptedException ex ) {
            Logger.getLogger( DirectoryChooserTest.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( InvocationTargetException ex ) {
            Logger.getLogger( DirectoryChooserTest.class.getName() ).log( Level.SEVERE, null, ex );
            System.out.println( ex.getCause().getMessage() );

        }
        assertEquals( "Checking that what went in is what comes out", new File( "/" ), result );
        assertEquals( "Checking that the changeEvent was fired", 1, changesReceived );

    }
}
