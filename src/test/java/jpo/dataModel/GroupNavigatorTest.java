package jpo.dataModel;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import junit.framework.TestCase;

/**
 *
 * @author richi
 */
public class GroupNavigatorTest extends TestCase {

    /**
     * Tests for the Group Navigator
     *
     * @param testName the test name
     */
    public GroupNavigatorTest( String testName ) {
        super( testName );
    }

    private final GroupInfo groupInfo = new GroupInfo( "Group1" );
    private final SortableDefaultMutableTreeNode groupNode = new SortableDefaultMutableTreeNode( groupInfo );

    private final PictureInfo pictureInfo1 = new PictureInfo();
    private final SortableDefaultMutableTreeNode pictureNode1 = new SortableDefaultMutableTreeNode( pictureInfo1 );

    private final PictureInfo pictureInfo2 = new PictureInfo();
    private final SortableDefaultMutableTreeNode pictureNode2 = new SortableDefaultMutableTreeNode( pictureInfo2 );

    private final GroupInfo groupInfo2 = new GroupInfo( "Group2" );
    private final SortableDefaultMutableTreeNode groupNode2 = new SortableDefaultMutableTreeNode( groupInfo2 );

    /**
     * Test of setNode method, of class GroupNavigator.
     */
    public void testSetNode() {
        GroupNavigator gn = new GroupNavigator();
        gn.setNode( groupNode2 );
        assertEquals( "After setNode the Navigator should return the new node", groupNode2, gn.getGroupNode() );
    }

    /**
     * Test of getTitle method, of class GroupNavigator.
     */
    public void testGetTitle() {
        GroupNavigator gn = new GroupNavigator();
        gn.setNode( groupNode );
        assertEquals( "After creation of the Navigator we should be able to retrieve the correct title", "Group1", gn.getTitle() );
    }

    /**
     * Test of getNumberOfNodes method, of class GroupNavigator.
     */
    public void testGetNumberOfNodes() {
        GroupNavigator gn = new GroupNavigator();
        gn.setNode( groupNode );
        assertEquals( "Empty group has no nodes", 0, gn.getNumberOfNodes() );
        groupNode.add( pictureNode1 );
        groupNode.add( pictureNode2 );
        assertEquals( "After adding 2 nodes we expect to have 2 nodes", 2, gn.getNumberOfNodes() );
        Runnable r = new Runnable() {

            @Override
            public void run() {
                groupNode.removeAllChildren();
            }
        };
        try {
            SwingUtilities.invokeAndWait( r );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( GroupNavigatorTest.class.getName() ).log( Level.SEVERE, null, ex );
        }
            
        assertEquals( "After removing all children we expect to have 0 nodes", 0, gn.getNumberOfNodes() );
    }

}
