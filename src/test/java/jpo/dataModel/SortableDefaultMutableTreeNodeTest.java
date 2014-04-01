package jpo.dataModel;

import java.util.ArrayList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import junit.framework.TestCase;


/*
 * ApplicationJMenuBarTest.java
 * JUnit based test
 *
 */
/**
 *
 * @author Richard Eigenmann
 */
public class SortableDefaultMutableTreeNodeTest
        extends TestCase {

    public SortableDefaultMutableTreeNodeTest( String testName ) {
        super( testName );
    }

    SortableDefaultMutableTreeNode rootNode;

    GroupInfo gr1 = new GroupInfo( "Group1" );

    SortableDefaultMutableTreeNode group1;

    GroupInfo gr2 = new GroupInfo( "Group2" );

    SortableDefaultMutableTreeNode group2;

    GroupInfo gr3 = new GroupInfo( "Group3" );

    SortableDefaultMutableTreeNode group3;

    GroupInfo gr4 = new GroupInfo( "Group4" );

    SortableDefaultMutableTreeNode group4;

    GroupInfo gr5 = new GroupInfo( "Group5" );

    SortableDefaultMutableTreeNode group5;

    PictureInfo pi1 = new PictureInfo( "file:///images/image1.jpg", "file:///lowresimages/image1lowres.jpg", "Fist Picture", "Reference1" );

    SortableDefaultMutableTreeNode picture1;

    PictureInfo pi2 = new PictureInfo( "file:///images/image2.jpg", "file:///lowresimages/image2lowres.jpg", "Second Picture", "Reference2" );

    SortableDefaultMutableTreeNode picture2;

    PictureInfo pi3 = new PictureInfo( "file:///images/image3.jpg", "file:///lowresimages/image3lowres.jpg", "Third Picture", "Reference3" );

    SortableDefaultMutableTreeNode picture3;

    PictureInfo pi4 = new PictureInfo( "file:///images/image4.jpg", "file:///lowresimages/image4lowres.jpg", "Fourth Picture", "Reference4" );

    SortableDefaultMutableTreeNode picture4;

    PictureInfo pi5 = new PictureInfo( "file:///images/image5.jpg", "file:///lowresimages/image5lowres.jpg", "Fifth Picture", "Reference5" );

    SortableDefaultMutableTreeNode picture5;


    @Override
    protected void setUp() throws Exception {

        rootNode = new SortableDefaultMutableTreeNode();
        group1 = new SortableDefaultMutableTreeNode( gr1 );
        group2 = new SortableDefaultMutableTreeNode( gr2 );
        group3 = new SortableDefaultMutableTreeNode( gr3 );
        group4 = new SortableDefaultMutableTreeNode( gr4 );
        group5 = new SortableDefaultMutableTreeNode( gr5 );
        picture1 = new SortableDefaultMutableTreeNode( pi1 );
        picture2 = new SortableDefaultMutableTreeNode( pi2 );
        picture3 = new SortableDefaultMutableTreeNode( pi3 );
        picture4 = new SortableDefaultMutableTreeNode( pi4 );
        picture5 = new SortableDefaultMutableTreeNode( pi5 );
        
        /*
        
        rootNode
        !----group1
             !----picture1
             !----picture2
        !----group2
             !----picture3
             !----picture4
             !----group3
                  !----picture5
                  !----group4
                       !----group5
        
        */
        
        rootNode.add( group1 );
        rootNode.add( group2 );
        group1.add( picture1 );
        group1.add( picture2 );
        group2.add( picture3 );
        group2.add( picture4 );
        group2.add( group3 );
        group3.add( picture5 );
        group3.add( group4 );
        group4.add( group5 );
    }


    @Override
    protected void tearDown() throws Exception {
    }


    public void testConstructor() {
        assertNotNull( "Checking that rootNode was constructed properly", rootNode );
    }


    public void testGetPreviousPicture() {
        assertSame( "Checking the GetPreviousPicture", picture3, picture4.getPreviousPicture() );
    }


    public void testGetPreviousPictureAcrossGroupBoundary() {
        assertSame( "Checking the GetPreviousPicture across a group boundary", picture2, picture3.getPreviousPicture() );
    }


    public void testGetPreviousPictureAtBeginning() {
        assertSame( "Checking the GetPreviousPicture at the beginning", null, picture1.getPreviousPicture() );
    }


    public void testGetAllowChildren() {
        assertTrue( "Checking that a GroupInfo node allows children", group1.getAllowsChildren() );
        assertFalse( "Checking that a PictureInfo node does not allow children", picture1.getAllowsChildren() );
    }


    public void testMoveToNode() {
        assertTrue( "Before moving, picture4 is owned by Group2", picture4.getParent().equals( group2 ) );
        assertTrue( "Move should work", picture4.moveToLastChild( group1 ) );
        assertTrue( "After moving, picture4 is owned by Group1", picture4.getParent().equals( group1 ) );

        assertNull( "The parent of the root node is null before a move", rootNode.getParent() );
        assertFalse( "Move should fail", rootNode.moveToLastChild( group1 ) );
        assertNull( "The parent of the root node is null after a move because it was not moved", rootNode.getParent() );

        assertTrue( "Before the move group2 was a child of rootNode", group2.getParent().equals( rootNode ) );
        assertFalse( "Move should fail", group2.moveToLastChild( picture2 ) );
        assertTrue( "The group2 is still a child of rootNode because picture2 doesn't allow children", group2.getParent().equals( rootNode ) );
        assertTrue( "Move should work", group2.moveToLastChild( group1 ) );
        assertTrue( "After the move group2 is child of group1", group2.getParent().equals( group1 ) );
    }


    public void testMoveBeforeError1() {
        assertNull( "The parent of the root node is null before a move", rootNode.getParent() );
        assertFalse( "Move should fail", rootNode.moveBefore( group1 ) );
        assertNull( "The parent of the root node is null after a move because it was not moved", rootNode.getParent() );
    }


    public void testMoveBeforeError2() {
        assertFalse( "Move should fail", group1.moveBefore( rootNode ) );
        assertEquals( "The parent of group1 is still the root node because the move before the root node was not done", rootNode, group1.getParent() );
    }


    public void testMoveBeforeNoParentGroup() {
        SortableDefaultMutableTreeNode noParentGroup = new SortableDefaultMutableTreeNode();
        assertTrue( "Move should succeed", noParentGroup.moveBefore( picture4 ) );
        assertEquals( "The parent of the noParentGroup is group2 after a move because", group2, noParentGroup.getParent() );
    }


    public void testMoveBeforeMoveGroup() {
        assertTrue( "Before moving, picture4 is owned by Group2", picture4.getParent().equals( group2 ) );
        assertTrue( "Move should work", picture4.moveBefore( picture2 ) );
        assertTrue( "After moving, picture4 is owned by Group1", picture4.getParent().equals( group1 ) );
        assertEquals( "After moving, picture4 should be at index 1 of Group1", 1, group1.getIndex( picture4 ) );
    }


    public void testMoveBefore2() {
        assertTrue( "Before moving, picture4 is owned by Group2", picture4.getParent().equals( group2 ) );
        assertTrue( "Before moving, picture3 is owned by Group2", picture3.getParent().equals( group2 ) );
        assertTrue( "Move should work", picture4.moveBefore( picture2 ) );
        assertTrue( "Move should work", picture3.moveBefore( picture4 ) );
        assertTrue( "After moving, picture3 is owned by Group1", picture2.getParent().equals( group1 ) );
        assertEquals( "After moving, picture3 should be at index 1 of Group1", 1, group1.getIndex( picture3 ) );
        assertEquals( "After moving, picture4 should have fallen back to index 2 of Group1", 2, group1.getIndex( picture4 ) );
        assertEquals( "After moving, picture2 should have fallen back to index 3 of Group1", 3, group1.getIndex( picture2 ) );
    }


    public void testMoveBeforeMoveUp() {
        assertTrue( "Before moving, picture4 is owned by Group2", picture4.getParent().equals( group2 ) );
        assertTrue( "Before moving, picture3 is owned by Group2", picture3.getParent().equals( group2 ) );
        assertTrue( "Move should work", picture4.moveBefore( picture2 ) );
        assertTrue( "Move should work", picture3.moveBefore( picture4 ) );
        assertTrue( "Move should work", picture2.moveBefore( picture3 ) );
        assertEquals( "After moving, picture2 should be at index 1 of Group1", 1, group1.getIndex( picture2 ) );
        assertEquals( "After moving, picture3 should be at index 2 of Group1", 2, group1.getIndex( picture3 ) );
        assertEquals( "After moving, picture4 should be at index 3 of Group1", 3, group1.getIndex( picture4 ) );
    }


    public void testMoveBeforeMoveDown() {
        assertEquals( "Before moving, picture1 should be at index 0 of Group1", 0, group1.getIndex( picture1 ) );
        assertTrue( "Move should work", picture4.moveBefore( picture2 ) );
        assertTrue( "Move should work", picture3.moveBefore( picture4 ) );
        assertTrue( "Move should work", picture2.moveBefore( picture3 ) );
        assertTrue( "Move should work", picture1.moveBefore( picture4 ) );
        assertEquals( "After moving, picture2 should be at index 0 of Group1", 0, group1.getIndex( picture2 ) );
        assertEquals( "After moving, picture3 should be at index 1 of Group1", 1, group1.getIndex( picture3 ) );
        assertEquals( "After moving, picture1 should be at index 2 of Group1", 2, group1.getIndex( picture1 ) );
        assertEquals( "After moving, picture4 should be at index 3 of Group1", 3, group1.getIndex( picture4 ) );
    }


    public void testMoveToIndex() {
        assertEquals( "Before moving, picture1 should be at index 0 of Group1", 0, group1.getIndex( picture1 ) );
        assertEquals( "Before moving, picture2 should be at index 1 of Group1", 1, group1.getIndex( picture2 ) );
        assertEquals( "Before moving, picture3 should be at index 0 of Group2", 0, group2.getIndex( picture3 ) );
        assertEquals( "Before moving, picture4 should be at index 1 of Group2", 1, group2.getIndex( picture4 ) );
        assertTrue( "Move should work", picture4.moveToIndex( group1, 0 ) );
        assertTrue( "Move should work", picture3.moveToIndex( group1, 0 ) );
        assertEquals( "After moving, picture1 should be at index 2 of Group1", 2, group1.getIndex( picture1 ) );
        assertEquals( "After moving, picture2 should be at index 3 of Group1", 3, group1.getIndex( picture2 ) );
        assertEquals( "After moving, picture3 should be at index 0 of Group1", 0, group1.getIndex( picture3 ) );
        assertEquals( "After moving, picture4 should be at index 1 of Group1", 1, group1.getIndex( picture4 ) );
        assertTrue( "Move should work", picture4.moveToIndex( group1, 3 ) );
        assertTrue( "Move should work", picture3.moveToIndex( group1, 2 ) );
        assertTrue( "Move should work", picture2.moveToIndex( group1, 1 ) );
        assertEquals( "After rearranging, picture1 should be at index 0 of Group1", 0, group1.getIndex( picture1 ) );
        assertEquals( "After rearranging, picture2 should be at index 1 of Group1", 1, group1.getIndex( picture2 ) );
        assertEquals( "After rearranging, picture3 should be at index 2 of Group1", 2, group1.getIndex( picture3 ) );
        assertEquals( "After rearranging, picture4 should be at index 3 of Group1", 3, group1.getIndex( picture4 ) );

        assertEquals( "The parent of the group2 is the rootNode before a move", rootNode, group2.getParent() );
        assertTrue( "Move should work", group2.moveToIndex( group1, 0 ) );
        assertEquals( "The parent of the group2 is group1 after a move", group1, group2.getParent() );
    }


    public void testMoveToIndexErrors1() {
        assertFalse( "Move should fail", group2.moveToIndex( group2, 0 ) );
    }


    public void testMoveToIndexErrors2() {
        assertNull( "The parent of the root node is null before a move", rootNode.getParent() );
        assertFalse( "Move should fail", rootNode.moveToIndex( group1, 0 ) );
        assertNull( "The parent of the root node is null after a move because it was not moved", rootNode.getParent() );

    }


    public void testGetClonePicture() {
        SortableDefaultMutableTreeNode cloneNode = picture1.getClone();
        assertNotSame( "The clone must be a new Object", picture1, cloneNode );
        assertNotSame( "The user object must be a new Object", picture1.getUserObject(), cloneNode.getUserObject() );
        assertNull( "The clone has no parent", cloneNode.getParent() );
        assertEquals( "The clone node has the same highres picture as the original", pi1.getHighresLocation(), ( (PictureInfo) cloneNode.getUserObject() ).getHighresLocation() );
    }


    public void testGetCloneGroup() {
        SortableDefaultMutableTreeNode cloneNode = group2.getClone();
        assertNotSame( "The clone must be a new Object", group2, cloneNode );
        assertNotSame( "The user object must be a new Object", group2.getUserObject(), cloneNode.getUserObject() );
        assertNull( "The clone has no parent", cloneNode.getParent() );
        assertTrue( "The clones userObject is of type GroupInfo", cloneNode.getUserObject() instanceof GroupInfo );
        assertEquals( "The clone has the same number of children", group2.getChildCount(), cloneNode.getChildCount() );
    }


    public void testgetChildPictureNodes() {
        ArrayList<SortableDefaultMutableTreeNode> allPicturesFromRoot = rootNode.getChildPictureNodes( true );
        assertEquals( "There should be 5 pictures in the result set from root, recursive", 5, allPicturesFromRoot.size() );
        assertEquals( "There should be 0 pictures under the root node when nonrecursive", 0, rootNode.getChildPictureNodes( false ).size() );
        assertEquals( "There should be 2 pictures under the group1 node when nonrecursive", 2, group1.getChildPictureNodes( false ).size() );
        assertEquals( "There should be 2 pictures under the group1 node when recursive", 2, group1.getChildPictureNodes( true ).size() );
    }
    
    
    /**
     * Tests the hasChildPictureNodes method
     */
    public void testHasChildPictureNodes() {
        assertTrue( "The root node of the test tree should report it has pictures", rootNode.hasChildPictureNodes() );
        assertTrue( "The group1 node of the test tree should report it has pictures", group1.hasChildPictureNodes() );
        assertTrue( "The group3 node of the test tree should report it has pictures", group3.hasChildPictureNodes() );
        assertFalse( "The group4 node of the test tree should report it has NO pictures", group4.hasChildPictureNodes() );
        assertFalse( "The group5 node of the test tree should report it has NO pictures", group5.hasChildPictureNodes() );
    }
}
