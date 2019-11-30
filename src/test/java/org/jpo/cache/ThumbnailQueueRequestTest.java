package org.jpo.cache;

import org.jpo.dataModel.SortableDefaultMutableTreeNode;
import org.jpo.gui.ThumbnailController;
import org.jpo.gui.swing.Thumbnail;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static junit.framework.TestCase.fail;
import static org.junit.Assume.assumeFalse;

/**
 *
 * @author Richard Eigenmann
 */
public class ThumbnailQueueRequestTest {

    @Test
    public void increasePriorityToTest() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                ThumbnailController controller = new ThumbnailController( new Thumbnail(), 350 );
                SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
                ThumbnailQueueRequest tqr = new ThumbnailQueueRequest( controller, node, QUEUE_PRIORITY.LOWEST_PRIORITY, new Dimension( 350, 350 ) );
                Assert.assertEquals( "Priority should be LOWEST_PRIORITY", QUEUE_PRIORITY.LOWEST_PRIORITY, tqr.priority );
                tqr.increasePriorityTo( QUEUE_PRIORITY.LOW_PRIORITY );
                Assert.assertEquals( "Priority should have been increased to LOW_PRIORITY", QUEUE_PRIORITY.LOW_PRIORITY, tqr.priority );
                tqr.increasePriorityTo( QUEUE_PRIORITY.MEDIUM_PRIORITY );
                Assert.assertEquals( "Priority should have been increased to MEDIUM_PRIORITY", QUEUE_PRIORITY.MEDIUM_PRIORITY, tqr.priority );
                tqr.increasePriorityTo( QUEUE_PRIORITY.HIGH_PRIORITY );
                Assert.assertEquals( "Priority should have been increased to HIGH_PRIORITY", QUEUE_PRIORITY.HIGH_PRIORITY, tqr.priority );
                tqr.increasePriorityTo( QUEUE_PRIORITY.MEDIUM_PRIORITY );
                Assert.assertEquals( "Priority should stay at HIGH_PRIORITY when increasing to MEDIUM_PRIORITY", QUEUE_PRIORITY.HIGH_PRIORITY, tqr.priority );
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( ThumbnailQueueRequestTest.class.getName() ).log( Level.SEVERE, null, ex );
            fail( ex.getMessage() );
        }
    }
}
