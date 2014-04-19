package jpo.dataModel;

import junit.framework.TestCase;

/**
 *
 * @author Richard Eigenmann
 */
public class GroupInfoTest
        extends TestCase {

    public GroupInfoTest( String testName ) {
        super( testName );
    }


    /**
     * Test of toString method, of class GroupInfo.
     */
    public void testToString() {
        GroupInfo gi = new GroupInfo( "Test" );
        assertEquals( "To String should give back what whent in", "Test", gi.toString() );
    }


    /**
     * Test of getGroupName method, of class GroupInfo.
     */
    public void testGetGroupName() {
        GroupInfo gi = new GroupInfo( "Test" );
        gi.setGroupName( "Tarrantino" );
        assertEquals( "To String should give back what whent in", "Tarrantino", gi.getGroupName() );
    }



    /**
     * A dumb PictureInfoChangeListener that only counts the events received
     */
    GroupInfoChangeListener groupInfoChangeListener = new GroupInfoChangeListener() {

        public void groupInfoChangeEvent( GroupInfoChangeEvent pice ) {
            eventsReceived++;
        }
    };

    int eventsReceived;


    public void testGroupInfoChangeListener() {
        eventsReceived = 0;
        GroupInfo gi = new GroupInfo( "Step0" );
        assertEquals( "To start off there should be no events", 0, eventsReceived );
        gi.setGroupName( "Step 1" );
        assertEquals( "There is no listener attached so there is no event", 0, eventsReceived );
        gi.addGroupInfoChangeListener( groupInfoChangeListener );
        gi.setGroupName( "Step 2" );
        assertEquals( "The listener should have fired and we should have 1 event", 1, eventsReceived );
        gi.removeGroupInfoChangeListener( groupInfoChangeListener );
        gi.setGroupName( "Step 3" );
        assertEquals( "The detached listener should not have fired", 1, eventsReceived );
    }
}
