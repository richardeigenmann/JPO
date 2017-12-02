package jpo.gui;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import jpo.gui.swing.Thumbnail;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import org.junit.Test;

/**
 *
 * @author Richard Eigenmann
 */
public class ThumbnailControllerTest {

    private Thumbnail thumbnail = null;

    @Test
    public void testConstructor() {

        // TravisCI runs headless so we can't execute the below test
        if ( GraphicsEnvironment.isHeadless() ) {
            return;
        }

        try {
            SwingUtilities.invokeAndWait( () -> {
                assertNull( thumbnail );
                thumbnail = new Thumbnail();
                assertNotNull( thumbnail );
                
                ThumbnailController thumbnailController = new ThumbnailController( thumbnail, 350 );
                assertNotNull( thumbnailController );
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( ThumbnailControllerTest.class.getName() ).log( Level.SEVERE, null, ex );
        }

    }
}
