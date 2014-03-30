package jpo.EventBus;

import com.google.common.eventbus.Subscribe;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import junit.framework.TestCase;

/**
 *
 * @author Richard eigenmann
 */
public class PictureSelectionEventTest extends TestCase {

    public PictureSelectionEventTest( String testName ) {
        super( testName );
    }

    JpoEventBus jpoEventBus;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        jpoEventBus = JpoEventBus.getInstance();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testReceivingEvent() {
        EventBusSubscriber myEventBusSubscriber = new EventBusSubscriber();
        jpoEventBus.register( myEventBusSubscriber );

        PictureSelectionEvent myPictureSelectionEvent
                = new PictureSelectionEvent(
                        new SortableDefaultMutableTreeNode(
                                new PictureInfo()
                        )
                );
        jpoEventBus.post( myPictureSelectionEvent );
        assertEquals( "After firing a PictureSelectionEvent we expect it to be received by the listener", myPictureSelectionEvent, responseEvent );
    }

    PictureSelectionEvent responseEvent;

    private class EventBusSubscriber {

        @Subscribe
        public void handlePictureSelectionEvent( PictureSelectionEvent event ) {
            responseEvent = event;
        }
    }

}
