package org.jpo.datamodel;

import org.junit.Before;
import org.junit.Test;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static junit.framework.TestCase.*;

/*
 Copyright (C) 2017-2019  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 without even the implied warranty of MERCHANTABILITY or FITNESS 
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */


/**
 * tests for the Picture Collection
 *
 * @author Richard Eigenmann
 */
public class PictureCollectionTest {

    /**
     * Let's have a nice little collection for some tests....
     */
    private PictureCollection pictureCollection;
    private final PictureInfo pi1 = new PictureInfo( new File("/images/image1.jpg"), "Picture 1" );
    // deliberately re-using image1.jpg so that we can find multiple groups referring to the same image.
    private final PictureInfo pi2 = new PictureInfo( new File("/images/image1.jpg"), "Picture 2");
    private final PictureInfo pi3 = new PictureInfo( new File("/images/image1.jpg"), "Picture 3" );
    private final PictureInfo pi4 = new PictureInfo( new File("/images/image1.jpg"), "Picture 4" );
    private final PictureInfo pi5 = new PictureInfo( new File("/images/image5.jpg"), "Picture 5" );
    private final PictureInfo pi6 = new PictureInfo( new File("/images/image6.jpg"), "Picture 6" );
    private final SortableDefaultMutableTreeNode picture1 = new SortableDefaultMutableTreeNode( pi1 );
    private final SortableDefaultMutableTreeNode picture2 = new SortableDefaultMutableTreeNode( pi2 );
    private final SortableDefaultMutableTreeNode picture3 = new SortableDefaultMutableTreeNode( pi3 );
    private final SortableDefaultMutableTreeNode picture4 = new SortableDefaultMutableTreeNode( pi4 );
    private final SortableDefaultMutableTreeNode picture5 = new SortableDefaultMutableTreeNode( pi5 );
    private final SortableDefaultMutableTreeNode picture6 = new SortableDefaultMutableTreeNode( pi6 );
    private final SortableDefaultMutableTreeNode group1 = new SortableDefaultMutableTreeNode( new GroupInfo( "Group1" ) );
    private final SortableDefaultMutableTreeNode group2 = new SortableDefaultMutableTreeNode( new GroupInfo( "Group2" ) );
    private final SortableDefaultMutableTreeNode group3 = new SortableDefaultMutableTreeNode( new GroupInfo( "Group3" ) );
    private final SortableDefaultMutableTreeNode group4 = new SortableDefaultMutableTreeNode( new GroupInfo( "Group4" ) );
    private final SortableDefaultMutableTreeNode group5 = new SortableDefaultMutableTreeNode( new GroupInfo( "Group5" ) );
    private final SortableDefaultMutableTreeNode group6 = new SortableDefaultMutableTreeNode( new GroupInfo( "Group6" ) );

    /**
     * Set up tests
     */
    @Before
    public void setUp()  {
        pictureCollection = new PictureCollection();
        pictureCollection.getRootNode().add( group1 );
        pictureCollection.getRootNode().add( group2 );
        pictureCollection.getRootNode().add( group3 );
        pictureCollection.getRootNode().add( group4 );
        group1.add( picture1 );
        group2.add( picture2 );
        group3.add( picture3 );
        group4.add( picture4 );
        group4.add( group5 );
        group5.add( group6 );
        group6.add( picture5 );
        group6.add( picture6 );
    }

    /**
     * Test the find parents group method
     */
    @Test
    public void testFindParentGroups() {
        assertNotNull( "Test that something is returned when looking for parent groups", pictureCollection.findParentGroups( picture1 ) );
    }

    /**
     * Test the find parents group method
     */
    @Test
    public void testFindParentGroups1() {
        assertNull( "Test that it returns null if the node is not a PictureInfo node", pictureCollection.findParentGroups( group1 ) );
    }

    /**
     * Test the find parents group method
     */
    @Test
    public void testFindParentGroups2() {
        //test that the parent group is one of the returned groups
        SortableDefaultMutableTreeNode[] sdmtns = pictureCollection.findParentGroups( picture1 );
        boolean found = false;
        for ( SortableDefaultMutableTreeNode sdmtn : sdmtns ) {
            found = found || ( sdmtn.equals( group1 ) );
        }
        assertTrue( "Test that the parent group is amongst the found groups", found );
    }

    /**
     * Test the find parents group method
     */
    @Test
    public void testFindParentGroups3() {
        //test that the 4 groups which refer to the same picture are returned
        SortableDefaultMutableTreeNode[] sdmtns = pictureCollection.findParentGroups( picture1 );
        assertEquals( "Test that the 3 groups referring to the same picture are found", 4, sdmtns.length );
    }

    /**
     * Test remembering the xml file
     */
    @Test
    public void testSetXmlFile() {
        File f = new File( "dir/test.xml" );
        pictureCollection.setXmlFile( f );
        File f2 = pictureCollection.getXmlFile();
        assertEquals( "Checking that we get the same file back that we put in", f, f2 );

        pictureCollection.clearCollection();
        File f3 = pictureCollection.getXmlFile();
        assertNull( "Check that a clearCollection sets the file name to null", f3 );
    }

    /**
     * I had a concurrent modification problem on "clear selections" so here are
     * a few tests to verify the selection thing works.
     */
    @Test
    public void testSelections() {
        assertEquals( "Testing that the selection array is empty before we start", 0, pictureCollection.getSelection().size() );
        pictureCollection.addToSelectedNodes( group1 );
        pictureCollection.addToSelectedNodes( picture1 );
        assertEquals( "We should have 2 nodes selected now", 2, pictureCollection.getSelection().size() );
        assertEquals( "We should have 2 nodes selected now", 2, pictureCollection.countSelectedNodes() );
        assertTrue( "We should find that the node we selected is actually in the selected set", pictureCollection.isSelected( group1 ) );
        assertTrue( "We should find that the second node we selected is actually in the selected set", pictureCollection.isSelected( picture1 ) );
        assertFalse( "A Node that was not selected should not be in the selection", pictureCollection.isSelected( group2 ) );

        pictureCollection.removeFromSelection( group1 );
        assertEquals( "We should have 1 nodes selected now", 1, pictureCollection.getSelection().size() );
        assertFalse( "We should find that the node we deselected is actually gone", pictureCollection.isSelected( group1 ) );
        assertTrue( "We should find that the second node we selected is still in the selected set", pictureCollection.isSelected( picture1 ) );
        assertFalse( "A Node that was not selected should not be in the selection", pictureCollection.isSelected( group2 ) );

        pictureCollection.addToSelectedNodes( group1 );
        pictureCollection.addToSelectedNodes( group1 ); //why not add it again?
        assertEquals( "Twice the same node plus one equals 2", 2, pictureCollection.getSelection().size() );

        pictureCollection.clearSelection(); // this is where we the concurrent modification happened
        assertEquals( "Testing that the selection array is empty again", 0, pictureCollection.getSelection().size() );

        pictureCollection.removeFromSelection( group1 ); // How about removing something that is not there?
        assertEquals( "Testing that the selection array stayed", 0, pictureCollection.getSelection().size() );
    }

    /**
     * I had a concurrent modification problem on "clear selections" so here are
     * a few tests to verify the selection thing works.
     */
    @Test
    public void testMailSelections() {
        assertEquals( "Testing that the mail selection array is empty before we start", 0, pictureCollection.getMailSelectedNodes().size() );
        pictureCollection.addToMailSelection( group1 );
        pictureCollection.addToMailSelection( picture1 );
        assertEquals( "We should have 2 nodes selected now", 2, pictureCollection.getMailSelectedNodes().size() );
        assertTrue( "We should find that the node we selected is actually in the selected set", pictureCollection.isMailSelected( group1 ) );
        assertTrue( "We should find that the second node we selected is actually in the selected set", pictureCollection.isMailSelected( picture1 ) );
        assertFalse( "A Node that was not selected should not be in the selection", pictureCollection.isMailSelected( group2 ) );

        pictureCollection.removeFromMailSelection( group1 );
        assertEquals( "We should have 1 nodes selected now", 1, pictureCollection.getMailSelectedNodes().size() );
        assertFalse( "We should find that the node we deselected is actually gone", pictureCollection.isMailSelected( group1 ) );
        assertTrue( "We should find that the second node we selected is still in the selected set", pictureCollection.isMailSelected( picture1 ) );
        assertFalse( "A Node that was not selected should not be in the selection", pictureCollection.isMailSelected( group2 ) );

        pictureCollection.addToMailSelection( group1 );
        pictureCollection.addToMailSelection( group1 ); //why not add it again?
        assertEquals( "Twice the same node plus one picture equals 2", 2, pictureCollection.getMailSelectedNodes().size() );

        pictureCollection.toggleMailSelected( picture1 );
        assertEquals( "Should be only group1 selected now", 1, pictureCollection.getMailSelectedNodes().size() );

        pictureCollection.clearMailSelection(); // this is where we the concurrent modification happened
        assertEquals( "Testing that the selection array is empty again", 0, pictureCollection.getMailSelectedNodes().size() );

        pictureCollection.removeFromMailSelection( group1 ); // How about removing something that is not there?
        assertEquals( "Testing that the selection array stayed", 0, pictureCollection.getMailSelectedNodes().size() );
    }

    /**
     * Since I had a concurrent modification problem on the clear selections
     * here are a few tests to verify the selection thing works.
     */
    @Test
    public void testAddToMailSelection() {
        assertEquals( "Testing that the mail selection array is empty before we start", 0, pictureCollection.getMailSelectedNodes().size() );
        pictureCollection.addToMailSelection( picture1 );
        assertEquals( "We should have 1 nodes selected now", 1, pictureCollection.getMailSelectedNodes().size() );
        pictureCollection.addToMailSelection( picture1 ); //adding the same node again
        assertEquals( "We should have 1 nodes selected now", 1, pictureCollection.getMailSelectedNodes().size() );
    }
    /**
     * Let's create a quick and dirty change listener
     */
    private final PictureInfoChangeListener listener = new PictureInfoChangeListener() {

        @Override
        public void pictureInfoChangeEvent( PictureInfoChangeEvent e ) {
            if (e.getWasSelected()) {
                selectedCount++;
            } else if (e.getWasUnselected()) {
                unselectedCount++;
            } else if (e.getWasMailSelected()) {
                mailSelectedCount++;
            } else if (e.getWasMailUnselected()) {
                mailUnselectedCount++;
            }
        }
    };
    private int selectedCount;
    private int unselectedCount;
    private int mailSelectedCount;
    private int mailUnselectedCount;

    /**
     * Test for the Select notification
     */
    @Test
    public void testSelectNotification() {
        pi1.addPictureInfoChangeListener( listener );
        selectedCount = 0;
        unselectedCount = 0;
        pictureCollection.addToSelectedNodes( picture1 );
        assertEquals( "We should have received a notification that the picture was selected", 1, selectedCount );

        // do it again.
        pictureCollection.addToSelectedNodes( picture1 );
        assertEquals( "As we are adding the same node again we should not get a change event", 1, selectedCount );

        // add another node where we are not listening.
        pictureCollection.addToSelectedNodes( picture2 );
        assertEquals( "As we are not listening on the second node we should still be with 1 event", 1, selectedCount );

        pictureCollection.removeFromSelection( picture1 );
        assertEquals( "We should have received a notification that the picture was unselected", 1, unselectedCount );

        pictureCollection.addToSelectedNodes( picture1 );
        assertEquals( "We should have received a notification that the picture was selected", 2, selectedCount );

        pictureCollection.clearSelection();
        assertEquals( "We should have received a notification that the picture was unselected", 2, unselectedCount );

        pi1.removePictureInfoChangeListener( listener );
        pictureCollection.addToSelectedNodes( picture1 );
        assertEquals( "We should not have received a notification that the picture was selected", 2, selectedCount );

        pictureCollection.clearSelection();
        assertEquals( "We should have received a notification that the picture was unselected", 2, unselectedCount );
    }

    /**
     * test the clearMailSelection method
     */
    @Test
    public void testClearMailSelection() {
        pi1.addPictureInfoChangeListener( listener );
        mailSelectedCount = 0;
        mailUnselectedCount = 0;
        pictureCollection.addToMailSelection( picture1 );
        assertEquals( "We should have received a notification that the picture was selected", 1, mailSelectedCount );

        assertEquals( "Before the removal we should have 0 unselect events", 0, mailUnselectedCount );
        pictureCollection.clearMailSelection();
        assertEquals( "We should have received a notification that the picture was unselected", 1, mailUnselectedCount );
    }

    /**
     * test the mail selection method
     */
    @Test
    public void testMailSelectNotification() {
        pi1.addPictureInfoChangeListener( listener );
        mailSelectedCount = 0;
        mailUnselectedCount = 0;
        pictureCollection.addToMailSelection( picture1 );
        assertEquals( "We should have received a notification that the picture was selected", 1, mailSelectedCount );

        // do it again.
        pictureCollection.addToMailSelection( picture1 );
        assertEquals( "As we are adding the same node again we should not get a change event", 1, mailSelectedCount );

        // add another node where we are not listening.
        pictureCollection.addToMailSelection( picture2 );
        assertEquals( "As we are not listening on the second node we should still be with 1 event", 1, mailSelectedCount );

        assertEquals( "Before the removal we should have 0 unselect events", 0, mailUnselectedCount );
        pictureCollection.removeFromMailSelection( picture1 );
        assertEquals( "We should have received a notification that the picture was unselected", 1, mailUnselectedCount );

        pictureCollection.addToMailSelection( picture1 );
        assertEquals( "We should have received a notification that the picture was selected", 2, mailSelectedCount );

        pictureCollection.clearMailSelection();
        assertEquals( "We should have received a notification that the picture was unselected", 2, mailUnselectedCount );

        pi1.removePictureInfoChangeListener( listener );
        pictureCollection.addToMailSelection( picture1 );
        assertEquals( "We should not have received a notification that the picture was selected", 2, mailSelectedCount );

        pictureCollection.clearSelection();
        assertEquals( "We should have received a notification that the picture was unselected", 2, mailUnselectedCount );

    }

    private int nodesChanged;  // default is 0
    private int nodesInserted;
    private int nodesRemoved;
    private int nodeStructureChanged;

    /**
     * In this test we want to see whether a change to an attribute in the
     * picture results in a treeModel change event being fired
     */
    @Test
    public void testChangeNotification() {
        nodesChanged = 0;
        nodesInserted = 0;
        nodesRemoved = 0;
        nodeStructureChanged = 0;
        pictureCollection.getTreeModel().addTreeModelListener( new TreeModelListener() {

            @Override
            public void treeNodesChanged( TreeModelEvent e ) {
                nodesChanged++;
            }

            @Override
            public void treeNodesInserted( TreeModelEvent e ) {
                nodesInserted++;
            }

            @Override
            public void treeNodesRemoved( TreeModelEvent e ) {
                nodesRemoved++;
            }

            @Override
            public void treeStructureChanged( TreeModelEvent e ) {
                nodeStructureChanged++;
            }
        } );

        //TODO: review this; why does the root node, the model and the picture collection have to be tied together
        // via the Settings?
        Settings.setPictureCollection( pictureCollection );
        assertEquals( "Before updating the description we should have 0 nodes changed: ", 0, nodesChanged);
        pi1.setDescription( "Changed Description" );
        try {
            Thread.sleep( 80 );  // give the threads some time to do the notifications.
        } catch ( InterruptedException ex ) {
            Logger.getLogger( PictureCollectionTest.class.getName() ).log( Level.SEVERE, null, ex );
            Thread.currentThread().interrupt();
        }
        assertEquals( "After updating the description we should have 1 node changed: ", 1, nodesChanged);
        assertEquals( "No nodes should have been inserted: ", 0, nodesInserted);
        assertEquals( "No nodes should have been removed: ", 0, nodesRemoved);
        assertEquals( "No nodes structure change should have been notified: ", 0, nodeStructureChanged);
    }
    
     /**
     * Test sendModelUpdates
     */
    @Test
    public void testSendModelUpdates() {
        PictureCollection pc = new PictureCollection();
        assertTrue("Default of sendModelUpdates should be true", pc.getSendModelUpdates());
        pc.setSendModelUpdates( false );
        assertFalse("sendModelUpdates should be false when changed", pc.getSendModelUpdates());
        pc.setSendModelUpdates( true );
        assertTrue("sendModelUpdates should be true when turned on again", pc.getSendModelUpdates());
    }
        
}
