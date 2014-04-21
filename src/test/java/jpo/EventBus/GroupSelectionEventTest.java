package jpo.EventBus;

import com.google.common.eventbus.Subscribe;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 * Test for Group Selection Events
 * @author Richard Eigenmann
 */
public class GroupSelectionEventTest extends TestCase {

    /**
     * Constructor
     * @param testName test name
     */
    public GroupSelectionEventTest( String testName ) {
        super( testName );
        jpoEventBus = JpoEventBus.getInstance();
    }

    private final JpoEventBus jpoEventBus;



    /**
     * Test receiving an event.
     */
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
