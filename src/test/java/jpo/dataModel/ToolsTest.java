package jpo.dataModel;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import junit.framework.TestCase;

/**
 * Tests for the Tools class
 * @author Richard Eigenmann
 */
public class ToolsTest extends TestCase {

    boolean notOnEDT_ErrorThrown;


    /**
     * Constructor for the Tools Test class
     */
    public void testCheckEDT_notOnEDT() {
        // if not on EDT must throw Error
        notOnEDT_ErrorThrown = false;
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Tools.checkEDT();
                } catch ( Error ex ) {
                    notOnEDT_ErrorThrown = true;
                }
            }
        };
        Thread t = new Thread( r );
        t.start();
        try {
            t.join();
            assertEquals( "When not on EDT must throw an error", true, notOnEDT_ErrorThrown );
        } catch ( InterruptedException ex ) {
            Logger.getLogger( ToolsTest.class.getName() ).log( Level.SEVERE, null, ex );
            assertTrue( "Something went wrong", false );
        }
    }

    boolean onEDT_ErrorThrown;
    public void testCheckEDT_OnEDT() {
        // if on EDT must not throw Error
        onEDT_ErrorThrown = false;
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Tools.checkEDT();
                } catch ( Error ex ) {
                    onEDT_ErrorThrown = true;
                }
            }
        };
        try {
            SwingUtilities.invokeAndWait( r );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            assertTrue( "Something went wrong", false );
        }
        assertEquals( "When on EDT must not throw an error", false, onEDT_ErrorThrown );

    }

      /**
     * Test of cleanupFilename method, of class HtmlDistiller.
     */
    public void testCleanupFilename() {
        String filename = "directory\\file.xml";  // actually contains directoy\file.xml
        String wanted = "directory_file.xml";  // actually contains directoy\file.xml
        String got = Tools.cleanupFilename( filename );
        assertEquals( "A backslash could be made into an underscore", wanted, got );
    }

}
