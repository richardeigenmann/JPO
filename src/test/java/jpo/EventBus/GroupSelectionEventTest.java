package jpo.EventBus;

import com.google.common.eventbus.Subscribe;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author Richard Eigenmann
 */
public class GroupSelectionEventTest extends TestCase {

    public GroupSelectionEventTest( String testName ) {
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

        GroupSelectionEvent myGroupSelectionEvent
                = new GroupSelectionEvent(
                        new SortableDefaultMutableTreeNode(
                                new GroupInfo( "Empty Group" )
                        )
                );
        jpoEventBus.post( myGroupSelectionEvent );
        assertEquals( "After firing a GroupSelectionEvent we expect it to be received by the listener", myGroupSelectionEvent, responseEvent );
    }

    GroupSelectionEvent responseEvent;

    private class EventBusSubscriber {

        @Subscribe
        public void handleGroupSelectionEvent( GroupSelectionEvent event ) {
            responseEvent = event;
        }
    }

}
