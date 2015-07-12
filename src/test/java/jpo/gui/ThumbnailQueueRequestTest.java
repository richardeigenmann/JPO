package jpo.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import jpo.gui.swing.Thumbnail;

import static junit.framework.TestCase.fail;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Richard Eigenmann
 */
public class ThumbnailQueueRequestTest {

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void increasePriorityToTest() {
        try {
            SwingUtilities.invokeAndWait( new Runnable() {

                @Override
                public void run() {
                    ThumbnailController controller = new ThumbnailController( new Thumbnail(), 350 );
                    ThumbnailQueueRequest tqr = new ThumbnailQueueRequest( controller, ThumbnailQueueRequest.QUEUE_PRIORITY.LOWEST_PRIORITY, true );
                    Assert.assertEquals( "Priority should be LOWEST_PRIORITY", ThumbnailQueueRequest.QUEUE_PRIORITY.LOWEST_PRIORITY, tqr.priority);
                    tqr.increasePriorityTo( ThumbnailQueueRequest.QUEUE_PRIORITY.LOW_PRIORITY );
                    Assert.assertEquals( "Priority should have been increased to LOW_PRIORITY", ThumbnailQueueRequest.QUEUE_PRIORITY.LOW_PRIORITY, tqr.priority);
                    tqr.increasePriorityTo( ThumbnailQueueRequest.QUEUE_PRIORITY.MEDIUM_PRIORITY );
                    Assert.assertEquals( "Priority should have been increased to MEDIUM_PRIORITY", ThumbnailQueueRequest.QUEUE_PRIORITY.MEDIUM_PRIORITY, tqr.priority);
                    tqr.increasePriorityTo( ThumbnailQueueRequest.QUEUE_PRIORITY.HIGH_PRIORITY );
                    Assert.assertEquals( "Priority should have been increased to HIGH_PRIORITY", ThumbnailQueueRequest.QUEUE_PRIORITY.HIGH_PRIORITY, tqr.priority);
                    tqr.increasePriorityTo( ThumbnailQueueRequest.QUEUE_PRIORITY.MEDIUM_PRIORITY );
                    Assert.assertEquals( "Priority should stay at HIGH_PRIORITY when increasing to MEDIUM_PRIORITY", ThumbnailQueueRequest.QUEUE_PRIORITY.HIGH_PRIORITY, tqr.priority);
                }
            } );
        } catch ( InterruptedException ex ) {
            System.out.println( "Bye1" );
            Logger.getLogger( ThumbnailQueueRequestTest.class.getName() ).log( Level.SEVERE, null, ex );
            fail( ex.getMessage() );
        } catch ( InvocationTargetException ex ) {
            System.out.println( "Bye2" );
            Logger.getLogger( ThumbnailQueueRequestTest.class.getName() ).log( Level.SEVERE, null, ex );
            fail( ex.getMessage() );
        }
    }
}
