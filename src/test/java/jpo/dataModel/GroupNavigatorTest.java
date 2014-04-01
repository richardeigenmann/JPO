package jpo.dataModel;

import junit.framework.TestCase;

/**
 *
 * @author richi
 */
public class GroupNavigatorTest extends TestCase {

    public GroupNavigatorTest( String testName ) {
        super( testName );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    GroupInfo groupInfo = new GroupInfo( "Group1" );
    SortableDefaultMutableTreeNode groupNode = new SortableDefaultMutableTreeNode( groupInfo );

    PictureInfo pictureInfo1 = new PictureInfo();
    SortableDefaultMutableTreeNode pictureNode1 = new SortableDefaultMutableTreeNode( pictureInfo1 );

    PictureInfo pictureInfo2 = new PictureInfo();
    SortableDefaultMutableTreeNode pictureNode2 = new SortableDefaultMutableTreeNode( pictureInfo2 );
    
    GroupInfo groupInfo2 = new GroupInfo( "Group2" );
    SortableDefaultMutableTreeNode groupNode2 = new SortableDefaultMutableTreeNode( groupInfo2 );
    

    /**
     * Test of constructor, of class GroupNavigator.
     */
    public void testGetGroupNode() {
        GroupNavigator gn = new GroupNavigator( groupNode );
        assertEquals( "After creation of the Navigator we should be able to retrieve the node", groupNode, gn.getGroupNode());
    }

    /**
     * Test of setNode method, of class GroupNavigator.
     */
    public void testSetNode() {
        GroupNavigator gn = new GroupNavigator( groupNode );
        gn.setNode( groupNode2 );
        assertEquals( "After setNode the Navigator should return the new node", groupNode2, gn.getGroupNode());
    }

    /**
     * Test of getTitle method, of class GroupNavigator.
     */
    public void testGetTitle() {
        GroupNavigator gn = new GroupNavigator( groupNode );
        assertEquals( "After creation of the Navigator we should be able to retrieve the correct title", "Group1", gn.getTitle());
    }

    /**
     * Test of getNumberOfNodes method, of class GroupNavigator.
     */
    public void testGetNumberOfNodes() {
        GroupNavigator gn = new GroupNavigator(groupNode );
        assertEquals( "Empty group has no nodes", 0, gn.getNumberOfNodes() );
        groupNode.add(  pictureNode1 );
        groupNode.add(  pictureNode2 );
        assertEquals( "After adding 2 nodes we expect to have 2 nodes", 2, gn.getNumberOfNodes() );
        groupNode.removeAllChildren();
        assertEquals( "After removing all children we expect to have 0 nodes", 0, gn.getNumberOfNodes() );
    }


}
