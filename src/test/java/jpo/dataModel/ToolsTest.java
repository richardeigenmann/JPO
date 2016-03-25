package jpo.dataModel;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;

/**
 * Tests for the Tools class
 *
 * @author Richard Eigenmann
 */
public class ToolsTest {

    boolean notOnEDT_ErrorThrown;

    /**
     * Constructor for the Tools Test class
     */
    @Test
    public void testCheckEDT_notOnEDT() {
        // if not on EDT must throw Error
        notOnEDT_ErrorThrown = false;
        Thread t = new Thread( () -> {
            try {
                Tools.checkEDT();
            } catch ( Error ex ) {
                notOnEDT_ErrorThrown = true;
            }
        } );
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

    /**
     * Test that an error is thrown when we are on the EDT and call the checkEDT
     * method
     */
    @Test
    public void testCheckEDT_OnEDT() {
        // if on EDT must not throw Error
        onEDT_ErrorThrown = false;
        try {
            SwingUtilities.invokeAndWait( () -> {
                try {
                    Tools.checkEDT();
                } catch ( Error ex ) {
                    onEDT_ErrorThrown = true;
                }
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            assertTrue( "Something went wrong", false );
        }
        assertEquals( "When on EDT must not throw an error", false, onEDT_ErrorThrown );

    }

    /**
     * Test of cleanupFilename method, of class HtmlDistiller.
     */
    @Test
    public void testCleanupFilename() {
        String filename = "directory\\file.xml";  // actually contains directoy\file.xml
        String wanted = "directory_file.xml";  // actually contains directoy\file.xml
        String got = Tools.cleanupFilename( filename );
        assertEquals( "A backslash could be made into an underscore", wanted, got );
    }

}
