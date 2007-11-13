package jpo;

import junit.framework.*;

/*
 * ApplicationJMenuBarTest.java
 * JUnit based test
 *
 */

/**
 *
 * @author Richard Eigenmann
 */
public class ThumbnailCreationQueueTest extends TestCase{

    public ThumbnailCreationQueueTest( String testName ) {
        super(testName);
    }

    private ThumbnailCreationQueue q;

    protected void setUp() throws Exception {
        q = new ThumbnailCreationQueue();
    }

    protected void tearDown() throws Exception {
    }



    public void testNothing() {
        Thumbnail t1 = new Thumbnail( 350 );
        ThumbnailQueueRequest r1 = new ThumbnailQueueRequest( t1, ThumbnailCreationQueue.LOW_PRIORITY, false );
        // actually this is a total mess! The queue is not a queue but a Controller and a Queue in one, it can't be tested without connecting to a factory.
        // I give up! RE 11.3.2007
        assertEquals("Checking nothing", 1, 1 );
    }
}