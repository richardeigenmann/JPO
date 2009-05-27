package jpo.dataModel;

import junit.framework.*;

/*
 * ApplicationJMenuBarTest.java
 * JUnit based test
 *
 */

/**
 *
 * @author Richard Eigenmann
 */
public class SortableDefaultMutableTreeNodeTest extends TestCase {
    
    public SortableDefaultMutableTreeNodeTest(String testName) {
        super(testName);
    }
    
    
    SortableDefaultMutableTreeNode rootNode;
    GroupInfo gr1 = new GroupInfo( "Group1" );
    SortableDefaultMutableTreeNode group1;
    GroupInfo gr2 = new GroupInfo( "Group2" );
    SortableDefaultMutableTreeNode group2;
    PictureInfo pi1 = new PictureInfo( "images/image1.jpg", "lowresimages/image1lowres.jpg", "Fist Picture", "Reference1");
    SortableDefaultMutableTreeNode picture1;
    PictureInfo pi2 = new PictureInfo( "images/image2.jpg", "lowresimages/image2lowres.jpg", "Second Picture", "Reference2");
    SortableDefaultMutableTreeNode picture2;
    PictureInfo pi3 = new PictureInfo( "images/image3.jpg", "lowresimages/image3lowres.jpg", "Third Picture", "Reference3");
    SortableDefaultMutableTreeNode picture3;
    PictureInfo pi4 = new PictureInfo( "images/image4.jpg", "lowresimages/image4lowres.jpg", "Fourth Picture", "Reference4");
    SortableDefaultMutableTreeNode picture4;
    
    
    
    @Override
    protected void setUp() throws Exception {
        rootNode = new SortableDefaultMutableTreeNode();
        group1 = new SortableDefaultMutableTreeNode( gr1 );
        group2 = new SortableDefaultMutableTreeNode( gr2 );
        picture1 = new SortableDefaultMutableTreeNode( pi1 );
        picture2 = new SortableDefaultMutableTreeNode( pi2 );
        picture3 = new SortableDefaultMutableTreeNode( pi3 );
        picture4 = new SortableDefaultMutableTreeNode( pi4 );
        rootNode.add( group1 );
        rootNode.add( group2 );
        group1.add( picture1 );
        group1.add( picture2 );
        group2.add( picture3 );
        group2.add( picture4 );
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
    
    
    
}