package org.jpo.cache;

import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Richard Eigenmann
 */
class ThumbnailCreationQueueTest {

    @Test
    void requestThumbnailCreationTest() {
        assertEquals(0, ThumbnailCreationQueue.size());
        MyThumbnailQueueRequestCallbackHandler mtqrch = new MyThumbnailQueueRequestCallbackHandler();
        SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
        ThumbnailCreationQueue.requestThumbnailCreation(new ThumbnailQueueRequest(mtqrch, node, QUEUE_PRIORITY.LOW_PRIORITY, new Dimension(350, 350)));
        assertEquals(1, ThumbnailCreationQueue.size());
        ThumbnailCreationQueue.clear();
    }

    @Test
    void requestThumbnailCreationExistsTest() {
        assertEquals(0, ThumbnailCreationQueue.size());
        MyThumbnailQueueRequestCallbackHandler mtqrch = new MyThumbnailQueueRequestCallbackHandler();
        SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
        ThumbnailCreationQueue.requestThumbnailCreation(new ThumbnailQueueRequest(mtqrch, node, QUEUE_PRIORITY.LOW_PRIORITY, new Dimension(350, 350)));
        assertEquals(1, ThumbnailCreationQueue.size());
        ThumbnailCreationQueue.requestThumbnailCreation(new ThumbnailQueueRequest(mtqrch, node, QUEUE_PRIORITY.MEDIUM_PRIORITY, new Dimension(350, 350)));
        // Queue should have one entry after-re requesting for the same callback handler
        assertEquals(1, ThumbnailCreationQueue.size());
        ThumbnailQueueRequest findResult1 = ThumbnailCreationQueue.findThumbnailQueueRequest(mtqrch);
        // Priority should have increased with second request
        assertEquals( QUEUE_PRIORITY.MEDIUM_PRIORITY, findResult1.priority );
        ThumbnailCreationQueue.clear();
    }

    @Test
    void requestThumbnailCreationExistsDifferentTest() {
        assertEquals(0, ThumbnailCreationQueue.size());
        MyThumbnailQueueRequestCallbackHandler mtqrch = new MyThumbnailQueueRequestCallbackHandler();
        SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
        ThumbnailCreationQueue.requestThumbnailCreation(new ThumbnailQueueRequest(mtqrch, node, QUEUE_PRIORITY.LOW_PRIORITY, new Dimension(350, 350)));
        assertEquals(1, ThumbnailCreationQueue.size());
        ThumbnailQueueRequest findResult1 = ThumbnailCreationQueue.findThumbnailQueueRequest(mtqrch);

        ThumbnailCreationQueue.requestThumbnailCreation(new ThumbnailQueueRequest(mtqrch, node, QUEUE_PRIORITY.MEDIUM_PRIORITY, new Dimension(400, 350)));
        assertEquals(1, ThumbnailCreationQueue.size());

        ThumbnailQueueRequest findResult2 = ThumbnailCreationQueue.findThumbnailQueueRequest( mtqrch );
        // We should have a new result
        assertNotEquals( findResult2, findResult1 );
        // New Request was for 400 pixels width
        assertEquals( 400, findResult2.size.width );
        ThumbnailCreationQueue.clear();
    }

    /**
     * Tests that after adding two requests with different priorities we get
     * back the one with the higher priority first.
     */
    @Test
    void pollTest() {
        assertEquals(0, ThumbnailCreationQueue.size());
        MyThumbnailQueueRequestCallbackHandler mtqrch1 = new MyThumbnailQueueRequestCallbackHandler();
        MyThumbnailQueueRequestCallbackHandler mtqrch2 = new MyThumbnailQueueRequestCallbackHandler();
        SortableDefaultMutableTreeNode node1 = new SortableDefaultMutableTreeNode();
        SortableDefaultMutableTreeNode node2 = new SortableDefaultMutableTreeNode();
        ThumbnailCreationQueue.requestThumbnailCreation(new ThumbnailQueueRequest(mtqrch1, node1, QUEUE_PRIORITY.LOW_PRIORITY, new Dimension(350, 350)));
        ThumbnailCreationQueue.requestThumbnailCreation(new ThumbnailQueueRequest(mtqrch2, node2, QUEUE_PRIORITY.MEDIUM_PRIORITY, new Dimension(350, 350)));
        assertEquals(2, ThumbnailCreationQueue.size());
        ThumbnailQueueRequest pollResult1 = ThumbnailCreationQueue.QUEUE.poll();
        ThumbnailQueueRequest pollResult2 = ThumbnailCreationQueue.QUEUE.poll();
        // The higher priority request should come first
        assertEquals(mtqrch2, Objects.requireNonNull(pollResult1).getThumbnailQueueRequestCallbackHandler());
        // The lower priority request should come second
        assertEquals(mtqrch1, Objects.requireNonNull(pollResult2).getThumbnailQueueRequestCallbackHandler());
        ThumbnailCreationQueue.clear();
    }

    @Test
    void clearTest() { // also tests size()
        assertEquals(0, ThumbnailCreationQueue.size());
        MyThumbnailQueueRequestCallbackHandler mtqrch = new MyThumbnailQueueRequestCallbackHandler();
        SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
        ThumbnailCreationQueue.requestThumbnailCreation(new ThumbnailQueueRequest(mtqrch, node, QUEUE_PRIORITY.LOW_PRIORITY, new Dimension(350, 350)));
        assertEquals(1, ThumbnailCreationQueue.size());
        ThumbnailCreationQueue.clear();
        assertEquals(0, ThumbnailCreationQueue.size());
    }

    @Test
    void findThumbnailQueueRequestTest() {
        // chuck 2 requests on the queue
        assertEquals(0, ThumbnailCreationQueue.size());
        MyThumbnailQueueRequestCallbackHandler mtqrch1 = new MyThumbnailQueueRequestCallbackHandler();
        MyThumbnailQueueRequestCallbackHandler mtqrch2 = new MyThumbnailQueueRequestCallbackHandler();
        SortableDefaultMutableTreeNode node1 = new SortableDefaultMutableTreeNode();
        SortableDefaultMutableTreeNode node2 = new SortableDefaultMutableTreeNode();
        ThumbnailCreationQueue.requestThumbnailCreation(new ThumbnailQueueRequest(mtqrch1, node1, QUEUE_PRIORITY.LOW_PRIORITY, new Dimension(350, 350)));
        ThumbnailCreationQueue.requestThumbnailCreation(new ThumbnailQueueRequest(mtqrch2, node2, QUEUE_PRIORITY.MEDIUM_PRIORITY, new Dimension(350, 350)));
        // Queue should have two entries after 2 requests
        assertEquals(2, ThumbnailCreationQueue.size());

        ThumbnailQueueRequest findResult1 = ThumbnailCreationQueue.findThumbnailQueueRequest(mtqrch1);
        ThumbnailQueueRequest findResult2 = ThumbnailCreationQueue.findThumbnailQueueRequest(mtqrch2);

        // Should have found the first request
        assertEquals(mtqrch1, findResult1.getThumbnailQueueRequestCallbackHandler());
        // Should have found the second request
        assertEquals(mtqrch2, findResult2.getThumbnailQueueRequestCallbackHandler());
        ThumbnailCreationQueue.clear();
    }

    @Test
    void removeThumbnailQueueRequestTest() {
        // chuck 2 requests on the queue
        assertEquals(0, ThumbnailCreationQueue.size());
        MyThumbnailQueueRequestCallbackHandler mtqrch1 = new MyThumbnailQueueRequestCallbackHandler();
        MyThumbnailQueueRequestCallbackHandler mtqrch2 = new MyThumbnailQueueRequestCallbackHandler();
        SortableDefaultMutableTreeNode node1 = new SortableDefaultMutableTreeNode();
        SortableDefaultMutableTreeNode node2 = new SortableDefaultMutableTreeNode();
        ThumbnailQueueRequest req1 = ThumbnailCreationQueue.requestThumbnailCreation(new ThumbnailQueueRequest(mtqrch1, node1, QUEUE_PRIORITY.LOW_PRIORITY, new Dimension(350, 350)));
        ThumbnailQueueRequest req2 = ThumbnailCreationQueue.requestThumbnailCreation(new ThumbnailQueueRequest(mtqrch2, node2, QUEUE_PRIORITY.MEDIUM_PRIORITY, new Dimension(350, 350)));
        // Queue should have two entries after 2 requests
        assertEquals(2, ThumbnailCreationQueue.size());

        ThumbnailCreationQueue.removeFromQueue(req1);
        assertEquals(1, ThumbnailCreationQueue.size());

        ThumbnailQueueRequest findResult1 = ThumbnailCreationQueue.findThumbnailQueueRequest(mtqrch1);
        ThumbnailQueueRequest findResult2 = ThumbnailCreationQueue.findThumbnailQueueRequest(mtqrch2);

        // Should not have found the first request
        assertNull( findResult1 );
        // Should have found the second request
        assertEquals( mtqrch2, findResult2.getThumbnailQueueRequestCallbackHandler() );
        ThumbnailCreationQueue.clear();
    }

    private static class MyThumbnailQueueRequestCallbackHandler implements ThumbnailQueueRequestCallbackHandler {

        private ArrayList<ThumbnailQueueRequest> receivedNotifiactions = new ArrayList<>();

        @Override
        public void callbackThumbnailCreated( ThumbnailQueueRequest thumbnailQueueRequest ) {
            receivedNotifiactions.add(thumbnailQueueRequest);
        }

    }
}
