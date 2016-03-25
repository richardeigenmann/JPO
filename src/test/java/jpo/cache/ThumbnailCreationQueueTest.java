package jpo.cache;

import java.awt.Dimension;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Richard Eigenmann
 */
public class ThumbnailCreationQueueTest {

    @Test
    public void requestThumbnailCreationTest() {
        Assert.assertEquals( "Queue should be empty to start off with", 0, ThumbnailCreationQueue.size() );
        MyThumbnailQueueRequestCallbackHandler mtqrch = new MyThumbnailQueueRequestCallbackHandler();
        SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
        ThumbnailCreationQueue.requestThumbnailCreation( mtqrch, node, ThumbnailQueueRequest.QUEUE_PRIORITY.LOW_PRIORITY, new Dimension( 350, 350 ) );
        Assert.assertEquals( "Queue should have one entry after request", 1, ThumbnailCreationQueue.size() );
        ThumbnailCreationQueue.clear();
    }

    @Test
    public void requestThumbnailCreationExistsTest() {
        Assert.assertEquals( "Queue should be empty to start off with", 0, ThumbnailCreationQueue.size() );
        MyThumbnailQueueRequestCallbackHandler mtqrch = new MyThumbnailQueueRequestCallbackHandler();
        SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
        ThumbnailCreationQueue.requestThumbnailCreation( mtqrch, node, ThumbnailQueueRequest.QUEUE_PRIORITY.LOW_PRIORITY, new Dimension( 350, 350 ) );
        Assert.assertEquals( "Queue should have one entry after request", 1, ThumbnailCreationQueue.size() );
        ThumbnailCreationQueue.requestThumbnailCreation( mtqrch, node, ThumbnailQueueRequest.QUEUE_PRIORITY.MEDIUM_PRIORITY, new Dimension( 350, 350 ) );
        Assert.assertEquals( "Queue should have one entry after-re requesting for the same callback handler", 1, ThumbnailCreationQueue.size() );
        ThumbnailQueueRequest findResult1 = ThumbnailCreationQueue.findThumbnailQueueRequest( mtqrch );
        Assert.assertEquals( "Priority should have increased with second request", ThumbnailQueueRequest.QUEUE_PRIORITY.MEDIUM_PRIORITY, findResult1.priority );
        ThumbnailCreationQueue.clear();
    }

    @Test
    public void requestThumbnailCreationExistsDifferentTest() {
        Assert.assertEquals( "Queue should be empty to start off with", 0, ThumbnailCreationQueue.size() );
        MyThumbnailQueueRequestCallbackHandler mtqrch = new MyThumbnailQueueRequestCallbackHandler();
        SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
        ThumbnailCreationQueue.requestThumbnailCreation( mtqrch, node, ThumbnailQueueRequest.QUEUE_PRIORITY.LOW_PRIORITY, new Dimension( 350, 350 ) );
        Assert.assertEquals( "Queue should have one entry after request", 1, ThumbnailCreationQueue.size() );
        ThumbnailQueueRequest findResult1 = ThumbnailCreationQueue.findThumbnailQueueRequest( mtqrch );

        ThumbnailCreationQueue.requestThumbnailCreation( mtqrch, node, ThumbnailQueueRequest.QUEUE_PRIORITY.MEDIUM_PRIORITY, new Dimension( 400, 350 ) );
        Assert.assertEquals( "Queue should have one entry after-re requesting for the same callback handler", 1, ThumbnailCreationQueue.size() );

        ThumbnailQueueRequest findResult2 = ThumbnailCreationQueue.findThumbnailQueueRequest( mtqrch );
        Assert.assertNotEquals( "We should have a new result", findResult2, findResult1 );
        Assert.assertEquals( "New Request was for 400 pixels width", 400, findResult2.size.width );
        ThumbnailCreationQueue.clear();
    }

    /**
     * Tests that after adding two requests with different priorities we get
     * back the one with the higher priority first.
     */
    @Test
    public void pollTest() {
        Assert.assertEquals( "Queue should be empty to start off with", 0, ThumbnailCreationQueue.size() );
        MyThumbnailQueueRequestCallbackHandler mtqrch1 = new MyThumbnailQueueRequestCallbackHandler();
        MyThumbnailQueueRequestCallbackHandler mtqrch2 = new MyThumbnailQueueRequestCallbackHandler();
        SortableDefaultMutableTreeNode node1 = new SortableDefaultMutableTreeNode();
        SortableDefaultMutableTreeNode node2 = new SortableDefaultMutableTreeNode();
        ThumbnailCreationQueue.requestThumbnailCreation( mtqrch1, node1, ThumbnailQueueRequest.QUEUE_PRIORITY.LOW_PRIORITY, new Dimension( 350, 350 ) );
        ThumbnailCreationQueue.requestThumbnailCreation( mtqrch2, node2, ThumbnailQueueRequest.QUEUE_PRIORITY.MEDIUM_PRIORITY, new Dimension( 350, 350 ) );
        Assert.assertEquals( "Queue should have two entries after 2 requests", 2, ThumbnailCreationQueue.size() );
        ThumbnailQueueRequest pollResult1 = ThumbnailCreationQueue.QUEUE.poll();
        ThumbnailQueueRequest pollResult2 = ThumbnailCreationQueue.QUEUE.poll();
        Assert.assertEquals( "The higher priority request should come first", mtqrch2, pollResult1.getThumbnailQueueRequestCallbackHandler() );
        Assert.assertEquals( "The lower priority request should come second", mtqrch1, pollResult2.getThumbnailQueueRequestCallbackHandler() );
        ThumbnailCreationQueue.clear();
    }

    @Test
    public void clearTest() { // also tests size()
        Assert.assertEquals( "Queue should be empty to start off with", 0, ThumbnailCreationQueue.size() );
        MyThumbnailQueueRequestCallbackHandler mtqrch = new MyThumbnailQueueRequestCallbackHandler();
        SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
        ThumbnailCreationQueue.requestThumbnailCreation( mtqrch, node, ThumbnailQueueRequest.QUEUE_PRIORITY.LOW_PRIORITY, new Dimension( 350, 350 ) );
        Assert.assertEquals( "Queue should have one entry after request", 1, ThumbnailCreationQueue.size() );
        ThumbnailCreationQueue.clear();
        Assert.assertEquals( "Queue should be empty after clear", 0, ThumbnailCreationQueue.size() );
    }

    @Test
    public void findThumbnailQueueRequestTest() {
        // chuck 2 requests on the queue
        Assert.assertEquals( "Queue should be empty to start off with", 0, ThumbnailCreationQueue.size() );
        MyThumbnailQueueRequestCallbackHandler mtqrch1 = new MyThumbnailQueueRequestCallbackHandler();
        MyThumbnailQueueRequestCallbackHandler mtqrch2 = new MyThumbnailQueueRequestCallbackHandler();
        SortableDefaultMutableTreeNode node1 = new SortableDefaultMutableTreeNode();
        SortableDefaultMutableTreeNode node2 = new SortableDefaultMutableTreeNode();
        ThumbnailCreationQueue.requestThumbnailCreation( mtqrch1, node1, ThumbnailQueueRequest.QUEUE_PRIORITY.LOW_PRIORITY, new Dimension( 350, 350 ) );
        ThumbnailCreationQueue.requestThumbnailCreation( mtqrch2, node2, ThumbnailQueueRequest.QUEUE_PRIORITY.MEDIUM_PRIORITY, new Dimension( 350, 350 ) );
        Assert.assertEquals( "Queue should have two entries after 2 requests", 2, ThumbnailCreationQueue.size() );

        ThumbnailQueueRequest findResult1 = ThumbnailCreationQueue.findThumbnailQueueRequest( mtqrch1 );
        ThumbnailQueueRequest findResult2 = ThumbnailCreationQueue.findThumbnailQueueRequest( mtqrch2 );

        Assert.assertEquals( "Should have found the first request", mtqrch1, findResult1.getThumbnailQueueRequestCallbackHandler() );
        Assert.assertEquals( "Should have found the second request", mtqrch2, findResult2.getThumbnailQueueRequestCallbackHandler() );
        ThumbnailCreationQueue.clear();
    }

    @Test
    public void removeThumbnailQueueRequestTest() {
        // chuck 2 requests on the queue
        Assert.assertEquals( "Queue should be empty to start off with", 0, ThumbnailCreationQueue.size() );
        MyThumbnailQueueRequestCallbackHandler mtqrch1 = new MyThumbnailQueueRequestCallbackHandler();
        MyThumbnailQueueRequestCallbackHandler mtqrch2 = new MyThumbnailQueueRequestCallbackHandler();
        SortableDefaultMutableTreeNode node1 = new SortableDefaultMutableTreeNode();
        SortableDefaultMutableTreeNode node2 = new SortableDefaultMutableTreeNode();
        ThumbnailQueueRequest req1 = ThumbnailCreationQueue.requestThumbnailCreation( mtqrch1, node1, ThumbnailQueueRequest.QUEUE_PRIORITY.LOW_PRIORITY, new Dimension( 350, 350 ) );
        ThumbnailQueueRequest req2 = ThumbnailCreationQueue.requestThumbnailCreation( mtqrch2, node2, ThumbnailQueueRequest.QUEUE_PRIORITY.MEDIUM_PRIORITY, new Dimension( 350, 350 ) );
        Assert.assertEquals( "Queue should have two entries after 2 requests", 2, ThumbnailCreationQueue.size() );

        ThumbnailCreationQueue.remove( req1 );
        Assert.assertEquals( "Queue should have one entry after removal", 1, ThumbnailCreationQueue.size() );

        ThumbnailQueueRequest findResult1 = ThumbnailCreationQueue.findThumbnailQueueRequest( mtqrch1 );
        ThumbnailQueueRequest findResult2 = ThumbnailCreationQueue.findThumbnailQueueRequest( mtqrch2 );

        Assert.assertNull( "Should not have found the first request", findResult1 );
        Assert.assertEquals( "Should have found the second request", mtqrch2, findResult2.getThumbnailQueueRequestCallbackHandler() );
        ThumbnailCreationQueue.clear();
    }

    private class MyThumbnailQueueRequestCallbackHandler implements ThumbnailQueueRequestCallbackHandler {

        public int notificationsCount = 0;

        @Override
        public void callbackThumbnailCreated( ThumbnailQueueRequest thumbnailQueueRequest ) {
            notificationsCount++;
        }

    }
}
