package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class ExportGroupToCollectionRequestTest {

    /**
     * Constructor for the ExportGroupToCollectionRequest tests
     */
    public ExportGroupToCollectionRequestTest() {
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
        final ExportGroupToCollectionRequestTest.EventBusSubscriber myEventBusSubscriber = new ExportGroupToCollectionRequestTest.EventBusSubscriber();
        jpoEventBus.register( myEventBusSubscriber );

        final ExportGroupToCollectionRequest myExportGroupToCollectionRequest
                = new ExportGroupToCollectionRequest(
                new SortableDefaultMutableTreeNode(
                        new PictureInfo()
                ), new File("."), false
        );
        jpoEventBus.post( myExportGroupToCollectionRequest );
        assertEquals(  myExportGroupToCollectionRequest, responseEvent );
    }

    /**
     *Here we are supposed to receive the event
     */
    private ExportGroupToCollectionRequest responseEvent;

    /**
     * Handler to receive the event.
     */
    private class EventBusSubscriber {

        /**
         * Receives the event and stores it in the responseEvent variable
         * @param event The event
         */
        @Subscribe
        public void handleExportGroupToCollectionRequest( ExportGroupToCollectionRequest event ) {
            responseEvent = event;
        }
    }


}