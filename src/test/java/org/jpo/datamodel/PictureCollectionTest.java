package org.jpo.datamodel;

import org.assertj.swing.edt.GuiActionRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2017-2021  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
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
class PictureCollectionTest {

    private static final String SWITZERLAND = "Switzerland";
    private static final String MOUNTAINS = "Mountains";
    private static final String LAKES = "Lakes";

    /**
     * Let's have a nice little collection for some tests....
     */
    private PictureCollection pictureCollection;
    private final PictureInfo pi1 = new PictureInfo(new File("/images/image1.jpg"), "Picture 1");
    // deliberately re-using image1.jpg so that we can find multiple groups referring to the same image.
    private final PictureInfo pi2 = new PictureInfo(new File("/images/image1.jpg"), "Picture 2");
    private final PictureInfo pi3 = new PictureInfo(new File("/images/image1.jpg"), "Picture 3");
    private final PictureInfo pi4 = new PictureInfo(new File("/images/image1.jpg"), "Picture 4");
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
    @BeforeEach
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
    void testFindParentGroups() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        // Test that something is returned when looking for parent groups
        assertNotNull(pictureCollection.findLinkingGroups(picture1));
    }

    /**
     * Test the find parents group method
     */
    @Test
    void testFindLinkingGroups1() {
        // Test that it returns an empty Set if the node is not a PictureInfo node
        assertEquals(0, pictureCollection.findLinkingGroups(group1).size());
    }

    /**
     * Test the find parents group method
     */
    @Test
    void testFindLinkingGroups2() {
        //test that the parent group is one of the returned groups
        final Set<SortableDefaultMutableTreeNode> linkingGroups = pictureCollection.findLinkingGroups(picture1);
        assertTrue(linkingGroups.contains(group1));
        assertFalse(linkingGroups.contains(group6));
    }

    @Test
    void testFindLinkingGroups3() {
        //test that the 4 groups which refer to the same picture are returned
        final Set<SortableDefaultMutableTreeNode> findLinkingGroups = pictureCollection.findLinkingGroups(picture1);
        assertEquals(4, findLinkingGroups.size());
    }

    /**
     * Test remembering the xml file
     */
    @Test
    void testSetXmlFile() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final File f = new File("dir/test.xml");
        pictureCollection.setXmlFile(f);
        final File f2 = pictureCollection.getXmlFile();
        // Checking that we get the same file back that we put in
        assertEquals(f, f2);

        pictureCollection.clearCollection();
        File f3 = pictureCollection.getXmlFile();
        // Check that a clearCollection sets the file name to null
        assertNull( f3 );
    }

    /**
     * I had a concurrent modification problem on "clear selections" so here are
     * a few tests to verify the selection thing works.
     */
    @Test
    void testSelections() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        assertEquals(0, pictureCollection.getSelection().size());
        pictureCollection.addToSelectedNodes(group1);
        pictureCollection.addToSelectedNodes(picture1);
        assertEquals(2, pictureCollection.getSelection().size());
        assertEquals(2, pictureCollection.countSelectedNodes());
        // We should find that the node we selected is actually in the selected set
        assertTrue(pictureCollection.isSelected(group1));
        // We should find that the second node we selected is actually in the selected set
        assertTrue(pictureCollection.isSelected(picture1));
        // A Node that was not selected should not be in the selection
        assertFalse( pictureCollection.isSelected( group2 ) );

        pictureCollection.removeFromSelection( group1 );
        assertEquals(  1, pictureCollection.getSelection().size() );
        // We should find that the node we deselected is actually gone
        assertFalse( pictureCollection.isSelected( group1 ) );
        // We should find that the second node we selected is still in the selected set
        assertTrue(  pictureCollection.isSelected( picture1 ) );
        // A Node that was not selected should not be in the selection
        assertFalse( pictureCollection.isSelected( group2 ) );

        pictureCollection.addToSelectedNodes( group1 );
        pictureCollection.addToSelectedNodes( group1 ); //why not add it again?
        assertEquals( 2, pictureCollection.getSelection().size() );

        pictureCollection.clearSelection(); // this is where we the concurrent modification happened
        assertEquals( 0, pictureCollection.getSelection().size() );

        pictureCollection.removeFromSelection( group1 ); // How about removing something that is not there?
        assertEquals( 0, pictureCollection.getSelection().size() );
    }

    /**
     * I had a concurrent modification problem on "clear selections" so here are
     * a few tests to verify the selection thing works.
     */
    @Test
    void testMailSelections() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        assertEquals(0, pictureCollection.getMailSelectedNodes().size());
        pictureCollection.addToMailSelection(group1);
        pictureCollection.addToMailSelection(picture1);
        assertEquals(2, pictureCollection.getMailSelectedNodes().size());
        // We should find that the node we selected is actually in the selected set
        assertTrue(pictureCollection.isMailSelected(group1));
        // We should find that the second node we selected is actually in the selected set
        assertTrue(pictureCollection.isMailSelected(picture1));
        // A Node that was not selected should not be in the selection
        assertFalse( pictureCollection.isMailSelected( group2 ) );

        pictureCollection.removeFromMailSelection( group1 );
        // We should have 1 nodes selected now
        assertEquals( 1, pictureCollection.getMailSelectedNodes().size() );
        // We should find that the node we deselected is actually gone
        assertFalse(  pictureCollection.isMailSelected( group1 ) );
        // We should find that the second node we selected is still in the selected set
        assertTrue( pictureCollection.isMailSelected( picture1 ) );
        // A Node that was not selected should not be in the selection
        assertFalse(  pictureCollection.isMailSelected( group2 ) );

        pictureCollection.addToMailSelection( group1 );
        pictureCollection.addToMailSelection( group1 ); //why not add it again?
        // Twice the same node plus one picture equals 2
        assertEquals( 2, pictureCollection.getMailSelectedNodes().size() );

        pictureCollection.toggleMailSelected( picture1 );
        // Should be only group1 selected now
        assertEquals( 1, pictureCollection.getMailSelectedNodes().size() );

        pictureCollection.clearMailSelection(); // this is where we the concurrent modification happened
        // Testing that the selection array is empty again
        assertEquals( 0, pictureCollection.getMailSelectedNodes().size() );

        pictureCollection.removeFromMailSelection( group1 ); // How about removing something that is not there?
        assertEquals( 0, pictureCollection.getMailSelectedNodes().size() );
    }

    /**
     * Since I had a concurrent modification problem on the clear selections
     * here are a few tests to verify the selection thing works.
     */
    @Test
    void testAddToMailSelection() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        assertEquals(0, pictureCollection.getMailSelectedNodes().size());
        pictureCollection.addToMailSelection(picture1);
        assertEquals(1, pictureCollection.getMailSelectedNodes().size());
        pictureCollection.addToMailSelection(picture1); //adding the same node again
        assertEquals(1, pictureCollection.getMailSelectedNodes().size());
    }

    private class CountingPictureInfoChangeListener implements PictureInfoChangeListener {
        public int selectedCount;
        public int unselectedCount;
        public int mailSelectedCount;
        public int mailUnselectedCount;

        @Override
        public void pictureInfoChangeEvent(final PictureInfoChangeEvent pictureInfoChangeEvent) {
            if (pictureInfoChangeEvent.getWasSelected()) {
                selectedCount++;
            } else if (pictureInfoChangeEvent.getWasUnselected()) {
                unselectedCount++;
            } else if (pictureInfoChangeEvent.getWasMailSelected()) {
                mailSelectedCount++;
            } else if (pictureInfoChangeEvent.getWasMailUnselected()) {
                mailUnselectedCount++;
            }
        }
    }

    /**
     * Test for the Select notification
     */
    @Test
    void testSelectNotification() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final CountingPictureInfoChangeListener countingPictureInfoChangeListener = new CountingPictureInfoChangeListener();
        pi1.addPictureInfoChangeListener(countingPictureInfoChangeListener);
        pictureCollection.addToSelectedNodes(picture1);
        // We should have received a notification that the picture was selected
        assertEquals(1, countingPictureInfoChangeListener.selectedCount);

        // do it again.
        pictureCollection.addToSelectedNodes(picture1);
        // As we are adding the same node again we should not get a change event
        assertEquals(1, countingPictureInfoChangeListener.selectedCount);

        // add another node where we are not listening.
        pictureCollection.addToSelectedNodes( picture2 );
        // As we are not listening on the second node we should still be with 1 event
        assertEquals(1, countingPictureInfoChangeListener.selectedCount);

        pictureCollection.removeFromSelection( picture1 );
        // We should have received a notification that the picture was unselected
        assertEquals(1, countingPictureInfoChangeListener.unselectedCount);

        pictureCollection.addToSelectedNodes( picture1 );
        // We should have received a notification that the picture was selected
        assertEquals(2, countingPictureInfoChangeListener.selectedCount);

        pictureCollection.clearSelection();
        // We should have received a notification that the picture was unselected
        assertEquals(2, countingPictureInfoChangeListener.unselectedCount);

        pi1.removePictureInfoChangeListener(countingPictureInfoChangeListener);
        pictureCollection.addToSelectedNodes(picture1);
        // We should not have received a notification that the picture was selected
        assertEquals(2, countingPictureInfoChangeListener.selectedCount);

        pictureCollection.clearSelection();
        // We should have received a notification that the picture was unselected
        assertEquals(2, countingPictureInfoChangeListener.unselectedCount);
    }

    /**
     * test the clearMailSelection method
     */
    @Test
    void testClearMailSelection() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final CountingPictureInfoChangeListener countingPictureInfoChangeListener = new CountingPictureInfoChangeListener();
        pi1.addPictureInfoChangeListener(countingPictureInfoChangeListener);
        pictureCollection.addToMailSelection(picture1);
        // We should have received a notification that the picture was selected
        assertEquals(1, countingPictureInfoChangeListener.mailSelectedCount);

        // Before the removal we should have 0 unselect events
        assertEquals(0, countingPictureInfoChangeListener.mailUnselectedCount);
        pictureCollection.clearMailSelection();
        // We should have received a notification that the picture was unselected
        assertEquals(1, countingPictureInfoChangeListener.mailUnselectedCount);
    }

    /**
     * test the mail selection method
     */
    @Test
    void testMailSelectNotification() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final CountingPictureInfoChangeListener countingPictureInfoChangeListener = new CountingPictureInfoChangeListener();
        pi1.addPictureInfoChangeListener(countingPictureInfoChangeListener);
        pictureCollection.addToMailSelection(picture1);
        // We should have received a notification that the picture was selected
        assertEquals(1, countingPictureInfoChangeListener.mailSelectedCount);

        // do it again.
        pictureCollection.addToMailSelection(picture1);
        // As we are adding the same node again we should not get a change event
        assertEquals(1, countingPictureInfoChangeListener.mailSelectedCount);

        // add another node where we are not listening.
        pictureCollection.addToMailSelection( picture2 );
        // As we are not listening on the second node we should still be with 1 event
        assertEquals(1, countingPictureInfoChangeListener.mailSelectedCount);

        // Before the removal we should have 0 unselect events
        assertEquals(0, countingPictureInfoChangeListener.mailUnselectedCount);
        pictureCollection.removeFromMailSelection(picture1);
        // We should have received a notification that the picture was unselected
        assertEquals(1, countingPictureInfoChangeListener.mailUnselectedCount);

        pictureCollection.addToMailSelection( picture1 );
        // We should have received a notification that the picture was selected
        assertEquals(2, countingPictureInfoChangeListener.mailSelectedCount);

        pictureCollection.clearMailSelection();
        // We should have received a notification that the picture was unselected
        assertEquals(2, countingPictureInfoChangeListener.mailUnselectedCount);

        pi1.removePictureInfoChangeListener(countingPictureInfoChangeListener);
        pictureCollection.addToMailSelection(picture1);
        // We should not have received a notification that the picture was selected
        assertEquals(2, countingPictureInfoChangeListener.mailSelectedCount);

        pictureCollection.clearSelection();
        // We should have received a notification that the picture was unselected
        assertEquals(2, countingPictureInfoChangeListener.mailUnselectedCount);

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
    void testChangeNotification() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        nodesChanged = 0;
        nodesInserted = 0;
        nodesRemoved = 0;
        nodeStructureChanged = 0;
        pictureCollection.getTreeModel().addTreeModelListener(new TreeModelListener() {

            @Override
            public void treeNodesChanged(TreeModelEvent e) {
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

        Settings.setPictureCollection( pictureCollection );
        assertEquals( 0, nodesChanged);
        pi1.setDescription( "Changed Description" );
        // After updating the description we should have 1 node changed
        await().until(() -> nodesChanged == 1);
        // No nodes should have been inserted
        assertEquals(  0, nodesInserted);
        // No nodes should have been removed
        assertEquals( 0, nodesRemoved);
        // No nodes structure change should have been notified
        assertEquals( 0, nodeStructureChanged);
    }


    @Test
    void testInsertNotification() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        nodesChanged = 0;
        nodesInserted = 0;
        nodesRemoved = 0;
        nodeStructureChanged = 0;
        pictureCollection.getTreeModel().addTreeModelListener(new TreeModelListener() {

            @Override
            public void treeNodesChanged(TreeModelEvent e) {
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

        Settings.setPictureCollection( pictureCollection );
        assertEquals( 0, nodesChanged);
        SortableDefaultMutableTreeNode newNode = new SortableDefaultMutableTreeNode();
        group1.add(newNode);
        await().until(() -> nodesInserted == 1);
        assertEquals(0, nodesChanged);
        assertEquals( 0, nodesRemoved);
        assertEquals( 0, nodeStructureChanged);
    }

    @Test
    void testRemoveNodesNotification() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        nodesChanged = 0;
        nodesInserted = 0;
        nodesRemoved = 0;
        nodeStructureChanged = 0;
        pictureCollection.getTreeModel().addTreeModelListener(new TreeModelListener() {

            @Override
            public void treeNodesChanged(TreeModelEvent e) {
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

        Settings.setPictureCollection( pictureCollection );
        assertEquals( 0, nodesChanged);
        group1.deleteNode();
        await().until(() -> nodesRemoved == 1);
        assertEquals(0, nodesChanged);
        assertEquals(  0, nodesInserted);
        assertEquals( 0, nodeStructureChanged);
    }

    @Test
    void testStructureChangedNotification() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        nodesChanged = 0;
        nodesInserted = 0;
        nodesRemoved = 0;
        nodeStructureChanged = 0;
        pictureCollection.getTreeModel().addTreeModelListener(new TreeModelListener() {

            @Override
            public void treeNodesChanged(TreeModelEvent e) {
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

        Settings.setPictureCollection( pictureCollection );
        assertEquals( 0, nodesChanged);
        GuiActionRunner.execute(() -> pictureCollection.getRootNode().sortChildren(Settings.FieldCodes.DESCRIPTION));
        await().until(() -> nodeStructureChanged == 1);
        assertEquals(0, nodesChanged);
        assertEquals(  0, nodesInserted);
        assertEquals( 0, nodesRemoved);
    }


    /**
     * Test sendModelUpdates
     */
    @Test
    void testSendModelUpdates() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final PictureCollection pc = new PictureCollection();
        // Default of sendModelUpdates should be true
        assertTrue(pc.getSendModelUpdates());
        pc.setSendModelUpdates(false);
        // sendModelUpdates should be false when changed
        assertFalse(pc.getSendModelUpdates());
        pc.setSendModelUpdates(true);
        // sendModelUpdates should be true when turned on again
        assertTrue(pc.getSendModelUpdates());
    }

    @Test
    void fileSave() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final PictureCollection picCollection = new PictureCollection();
        try {
            SwingUtilities.invokeAndWait(picCollection::clearCollection);
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }

        try {
            final File tempFile = File.createTempFile("fileSave", ".xml");
            picCollection.setXmlFile(tempFile);
            picCollection.fileSave();
            assertTrue(tempFile.exists());
            try (final Stream<String> s = Files.lines(tempFile.toPath())) {
                assertEquals(7, s.count());
            }
            Files.delete(tempFile.toPath());
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void fileSaveNoPriorFile() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final PictureCollection picCollection = new PictureCollection();
        try {
            SwingUtilities.invokeAndWait(picCollection::clearCollection);
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }

        try {
            // createTempFile actually creates the file
            final File tempFile = File.createTempFile("fileSaveNoPriorFile", ".xml");
            // So let's just go and delete it
            Files.delete(tempFile.toPath());
            assertFalse(tempFile.exists());
            picCollection.setXmlFile(tempFile);
            picCollection.fileSave();
            assertTrue(tempFile.exists());
            try (final Stream<String> s = Files.lines(tempFile.toPath())) {
                assertEquals(7, s.count());
            }
            Files.delete(tempFile.toPath());
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void clearCollection() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        assertEquals(4, pictureCollection.getRootNode().getChildCount());
        pictureCollection.clearCollection();
        assertEquals(0, pictureCollection.getRootNode().getChildCount());
    }


    @Test
    void addCategory() {
        final PictureCollection myPictureCollection = new PictureCollection();
        assertEquals(0, myPictureCollection.getCategoryKeySet().size());
        myPictureCollection.addCategory(0, SWITZERLAND);
        assertEquals(1, myPictureCollection.getCategoryKeySet().size());
        myPictureCollection.addCategory(MOUNTAINS);
        assertEquals(2, myPictureCollection.getCategoryKeySet().size());
        // add a duplicate
        myPictureCollection.addCategory(MOUNTAINS);
        assertEquals(2, myPictureCollection.getCategoryKeySet().size());
    }

    @Test
    void getSortedCategoryStream() {
        final PictureCollection myPictureCollection = new PictureCollection();
        myPictureCollection.addCategory(SWITZERLAND);
        myPictureCollection.addCategory(MOUNTAINS);
        myPictureCollection.addCategory(LAKES);
        assertEquals(3, myPictureCollection.getCategoryKeySet().size());
        final String[] result = myPictureCollection.getSortedCategoryStream().map(Map.Entry::getValue).toArray(String[]::new);
        assertArrayEquals(new String[]{LAKES, MOUNTAINS, SWITZERLAND}, result);
    }
}
