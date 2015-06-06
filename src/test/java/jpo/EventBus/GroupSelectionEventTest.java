package jpo.EventBus;

import com.google.common.eventbus.Subscribe;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import static org.junit.Assert.assertEquals;
import org.junit.Test;


/**
 * Test for Group Selection Events
 * @author Richard Eigenmann
 */
public class GroupSelectionEventTest  {

    /**
     * Constructor
     */
    public GroupSelectionEventTest() {
        jpoEventBus = JpoEventBus.getInstance();
    }

    private final JpoEventBus jpoEventBus;



    /**
     * Test receiving an event.
     */
    @Test
    public void testReceivingEvent() {
        EventBusSubscriber myEventBusSubscriber = new EventBusSubscriber();
        jpoEventBus.register( myEventBusSubscriber );

        GroupSelectionEvent myGroupSelectionEvent
                = new GroupSelectionEvent(
                        new SortableDefaultMutableTreeNode(
                                new GroupInfo( "Empty Group" )
                        )
                );
        jpoEventBus.post( myGroupSelectionEvent );
        assertEquals( "After firing a GroupSelectionEvent we expect it to be received by the listener", myGroupSelectionEvent, responseEvent );
    }

    /**
     * Receives the event.
     */
    private GroupSelectionEvent responseEvent;

    /**
     * Subscribes to the vent.
     */
    private class EventBusSubscriber {

        @Subscribe
        public void handleGroupSelectionEvent( GroupSelectionEvent event ) {
            responseEvent = event;
        }
    }

}
