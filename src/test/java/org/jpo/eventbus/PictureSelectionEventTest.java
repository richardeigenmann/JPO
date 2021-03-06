package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

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
        assumeFalse(GraphicsEnvironment.isHeadless());
        final EventBusSubscriber myEventBusSubscriber = new EventBusSubscriber();
        jpoEventBus.register( myEventBusSubscriber );

        final PictureSelectionEvent myPictureSelectionEvent
                = new PictureSelectionEvent(
                        new SortableDefaultMutableTreeNode(
                                new PictureInfo()
                        )
                );
        jpoEventBus.post( myPictureSelectionEvent );
        // After firing a PictureSelectionEvent we expect it to be received by the listener
        assertEquals(  myPictureSelectionEvent, responseEvent );
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
