package jpo.dataModel;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import jpo.gui.ThumbnailController;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 *
 * @author Richard Eigenmann
 */
public class PictureInfoChangeListenerTest
        extends TestCase {

    public PictureInfoChangeListenerTest( String testName ) {
        super( testName );
    }



    // helps with inner class
    ThumbnailController thumbnailController;


    /**
     * helps to get a ThumbnailController from the EDT
     * @return The ThumbnailController
     */
    private ThumbnailController getNewThumbnailController() {
        // create the Thumbnail Controller on EDT
        Runnable r = new Runnable() {

            @Override
            public void run() {
                thumbnailController = new ThumbnailController( 350 );
            }
        };
        try {
            SwingUtilities.invokeAndWait( r );
        } catch ( InterruptedException ex ) {
            Logger.getLogger( PictureInfoChangeListenerTest.class.getName() ).log( Level.SEVERE, null, ex );
            throw new AssertionFailedError( "Got an interrupted exception instead of a ThumbnailController" );
        } catch ( InvocationTargetException ex ) {
            Logger.getLogger( PictureInfoChangeListenerTest.class.getName() ).log( Level.SEVERE, null, ex );
            throw new AssertionFailedError( "Got an exception instead of a ThumbnailController" );
        }
        return thumbnailController;
    }
}
