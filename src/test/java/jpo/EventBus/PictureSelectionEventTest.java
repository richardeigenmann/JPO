package jpo.EventBus;

import com.google.common.eventbus.Subscribe;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

/**
 * Picture Selection Event Tests
 * @author Richard Eigenmann
 */
public class PictureSelectionEventTest {

    /**
     * Constructor for the PictureSelectionEvent tests 
     */
    public PictureSelectionEventTest() {
        jpoEventBus = JpoEventBus.getInstance();
    }

    /**
     * my instance of the event bus
     */
    private final JpoEventBus jpoEventBus;

    

    /**
     * sends an event and hopes to receive it back
     */
    @Test
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

    /**
     *Here we are supposed to receive the event
     */
    private PictureSelectionEvent responseEvent;

    /**
     * Handler to receive the event.
     */
    private class EventBusSubscriber {

        /**
         * Receives the event and stores it in the responseEvent variable
         * @param event The event
         */
        @Subscribe
        public void handlePictureSelectionEvent( PictureSelectionEvent event ) {
            responseEvent = event;
        }
    }

}
