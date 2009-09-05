/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpo.dataModel;

import junit.framework.TestCase;

/**
 *
 * @author richi
 */
public class NodeStatisticsTest extends TestCase {

    public NodeStatisticsTest( String testName ) {
        super( testName );
    }

    SortableDefaultMutableTreeNode rootNode;

    GroupInfo gr1 = new GroupInfo( "Group1" );

    SortableDefaultMutableTreeNode group1;

    GroupInfo gr2 = new GroupInfo( "Group2" );

    SortableDefaultMutableTreeNode group2;

    GroupInfo gr3 = new GroupInfo( "Group3" );

    SortableDefaultMutableTreeNode group3;

    PictureInfo pi1 = new PictureInfo( "images/image1.jpg", "lowresimages/image1lowres.jpg", "Fist Picture", "Reference1" );

    SortableDefaultMutableTreeNode picture1;

    PictureInfo pi2 = new PictureInfo( "images/image2.jpg", "lowresimages/image2lowres.jpg", "Second Picture", "Reference2" );

    SortableDefaultMutableTreeNode picture2;

    PictureInfo pi3 = new PictureInfo( "images/image3.jpg", "lowresimages/image3lowres.jpg", "Third Picture", "Reference3" );

    SortableDefaultMutableTreeNode picture3;

    PictureInfo pi4 = new PictureInfo( "images/image4.jpg", "lowresimages/image4lowres.jpg", "Fourth Picture", "Reference4" );

    SortableDefaultMutableTreeNode picture4;

    PictureInfo pi5 = new PictureInfo( "images/image5.jpg", "lowresimages/image5lowres.jpg", "Fifth Picture", "Reference5" );

    SortableDefaultMutableTreeNode picture5;


    @Override
    protected void setUp() throws Exception {
        rootNode = new SortableDefaultMutableTreeNode();
        group1 = new SortableDefaultMutableTreeNode( gr1 );
        group2 = new SortableDefaultMutableTreeNode( gr2 );
        picture1 = new SortableDefaultMutableTreeNode( pi1 );
        picture2 = new SortableDefaultMutableTreeNode( pi2 );
        group3 = new SortableDefaultMutableTreeNode( gr3 );
        picture3 = new SortableDefaultMutableTreeNode( pi3 );
        picture4 = new SortableDefaultMutableTreeNode( pi4 );
        picture5 = new SortableDefaultMutableTreeNode( pi5 );
        rootNode.add( group1 );
        rootNode.add( group2 );
        group1.add( picture1 );
        group1.add( picture2 );
        group1.add( group3 );
        group3.add( picture3 );
        group2.add( picture4 );
        group2.add( picture5 );
    }


    /**
     * Test of getNode method, of class NodeStatistics.
     */
    public void testSetGetNode() {
        NodeStatistics ns = new NodeStatistics( null );
        assertNull( "A null node should return null", ns.getNode() );

        NodeStatistics ns1 = new NodeStatistics( rootNode );
        assertEquals( "When we set a node it should be the one coming back", rootNode, ns1.getNode() );

    }


    /**
     * Test of getNumberOfNodes method, of class NodeStatistics.
     */
    public void testGetNumberOfNodes() {
        NodeStatistics ns = new NodeStatistics( null );
        assertEquals( "Counting nodes on a null node should return 0 nodes", 0, ns.getNumberOfNodes() );

        NodeStatistics ns1 = new NodeStatistics( rootNode );
        assertEquals( "Counting number of nodes", 9, ns1.getNumberOfNodes() );

    }


    /**
     * Test of getNumberOfGroups method, of class NodeStatistics.
     */
    public void testGetNumberOfGroups() {
        NodeStatistics ns = new NodeStatistics( rootNode );
        assertEquals( "Counting number of groups", 3, ns.getNumberOfGroups() );
    }


    /**
     * Test of getNumberOfPictures method, of class NodeStatistics.
     */
    public void testGetNumberOfPictures() {
        NodeStatistics ns = new NodeStatistics( rootNode );
        assertEquals( "Counting number of pictures", 5, ns.getNumberOfPictures() );
    }


    /**
     * Test of countPictures method, of class NodeStatistics.
     */
    public void testCountPictures() {
        assertEquals( "Recursive picture count", 3, NodeStatistics.countPictures( group1, true ) );
        assertEquals( "Non Recursive picture count", 2, NodeStatistics.countPictures( group1, false ) );
    }
}
