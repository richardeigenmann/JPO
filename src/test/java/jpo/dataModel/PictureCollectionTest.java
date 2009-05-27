package jpo.dataModel;

import java.io.File;
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
public class PictureCollectionTest extends TestCase {
    
    public PictureCollectionTest(String testName) {
        super(testName);
    }
    
    PictureCollection pc;
    
    PictureInfo pi1 = new PictureInfo( "/images/image1.jpg", "/lowresimages/image1lowres.jpg", "Picture 1", "Reference1");
    PictureInfo pi2 = new PictureInfo( "/images/image1.jpg", "/lowresimages/image2lowres.jpg", "Picture 2", "Reference2");
    PictureInfo pi3 = new PictureInfo( "/images/image1.jpg", "/lowresimages/image3lowres.jpg", "Picture 3", "Reference3");
    PictureInfo pi4 = new PictureInfo( "/images/image4.jpg", "/lowresimages/image4lowres.jpg", "Picture 4", "Reference4");
    PictureInfo pi5 = new PictureInfo( "/images/image1.jpg", "/lowresimages/image5lowres.jpg", "Picture 5", "Reference5");
    PictureInfo pi6 = new PictureInfo( "/images/image1.jpg", "/lowresimages/image6lowres.jpg", "Picture 6", "Reference6");
    SortableDefaultMutableTreeNode picture1 = new SortableDefaultMutableTreeNode( pi1 );
    SortableDefaultMutableTreeNode picture2 = new SortableDefaultMutableTreeNode( pi2 );
    SortableDefaultMutableTreeNode picture3 = new SortableDefaultMutableTreeNode( pi3 );
    SortableDefaultMutableTreeNode picture4 = new SortableDefaultMutableTreeNode( pi4 );
    SortableDefaultMutableTreeNode picture5 = new SortableDefaultMutableTreeNode( pi5 );
    SortableDefaultMutableTreeNode picture6 = new SortableDefaultMutableTreeNode( pi6 );
    SortableDefaultMutableTreeNode group1 = new SortableDefaultMutableTreeNode( new GroupInfo( "Group1" ) );
    SortableDefaultMutableTreeNode group2 = new SortableDefaultMutableTreeNode( new GroupInfo( "Group2" ) );
    SortableDefaultMutableTreeNode group3 = new SortableDefaultMutableTreeNode( new GroupInfo( "Group3" ) );
    SortableDefaultMutableTreeNode group4 = new SortableDefaultMutableTreeNode( new GroupInfo( "Group4" ) );
    SortableDefaultMutableTreeNode group5 = new SortableDefaultMutableTreeNode( new GroupInfo( "Group5" ) );
    SortableDefaultMutableTreeNode group6 = new SortableDefaultMutableTreeNode( new GroupInfo( "Group6" ) );
    
    
    
    @Override
    protected void setUp() throws Exception {
        pc = new PictureCollection();
        pc.getRootNode().add( group1 );
        pc.getRootNode().add( group2 );
        pc.getRootNode().add( group3 );
        pc.getRootNode().add( group4 );
        group1.add( picture1 );
        group2.add( picture2 );
        group3.add( picture3 );
        group4.add( picture4 );
        group4.add( group5 );
        group5.add( group6 );
        group6.add( picture5 );
        group6.add( picture6 );
    }
    
    @Override
    protected void tearDown() throws Exception {
    }
    
    
    
    
    
    
    public void testFindParentGroups() {
        assertNotNull( "Test that something is returned when looking for parent groups", pc.findParentGroups( picture1 ) );
    }
    
    public void testFindParentGroups1() {
        assertNull( "Test that it returns null if the node is not a PictureInfo node", pc.findParentGroups( group1 ) );
    }
    
    public void testFindParentGroups2() {
        //test that the parent group is one of the returned groups
        SortableDefaultMutableTreeNode[] sdmtns = pc.findParentGroups( picture1 );
        boolean found = false;
        for ( int i=0; i<sdmtns.length; i++ ) {
            found = found || ( sdmtns[i] == group1 );
        }
        assertTrue( "Test that the parent group is amongst the found groups", found );
    }
    
    public void testFindParentGroups3() {
        //test that the 4 groups which refer to the same picture are returned
        SortableDefaultMutableTreeNode[] sdmtns = pc.findParentGroups( picture1 );
        assertEquals( "Test that the 3 groups refering to the same picture are found", 4, sdmtns.length );
    }
    


    public void testSetXmlFile() {
        File f = new File("/dir/test.xml");
        pc.setXmlFile( f );
        File f2 = pc.getXmlFile();
        assertEquals( "Checking that we get the same file back that we put in", f, f2 );

        pc.clearCollection();
        File f3 = pc.getXmlFile();
        assertNull( "Check that a clearCollection sets the file name to null", f3 );

    }


}