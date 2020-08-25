package org.jpo.datamodel;

import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.*;

/**
 * Tests for the SortableDefaultMutableTreeNode tests
 *
 * @author Richard Eigenmann
 */
public class SortableDefaultMutableTreeNodeTest {

    private SortableDefaultMutableTreeNode rootNode;

    private final GroupInfo gr1 = new GroupInfo("Group1");

    private SortableDefaultMutableTreeNode group1;

    private final GroupInfo gr2 = new GroupInfo("Group2");

    private SortableDefaultMutableTreeNode group2;

    private final GroupInfo gr3 = new GroupInfo("Group3");

    private SortableDefaultMutableTreeNode group3;

    private final GroupInfo gr4 = new GroupInfo("Group4");

    private SortableDefaultMutableTreeNode group4;

    private final GroupInfo gr5 = new GroupInfo("Group5");

    private SortableDefaultMutableTreeNode group5;

    private final PictureInfo pi1 = new PictureInfo(new File("/images/image1.jpg"), "Fist Picture");

    private SortableDefaultMutableTreeNode picture1;

    private final PictureInfo pi2 = new PictureInfo(new File("/images/image2.jpg"), "Second Picture");

    private SortableDefaultMutableTreeNode picture2;

    private final PictureInfo pi3 = new PictureInfo(new File("/images/image3.jpg"), "Third Picture");

    private SortableDefaultMutableTreeNode picture3;

    private final PictureInfo pi4 = new PictureInfo(new File("/images/image4.jpg"), "Fourth Picture");

    private SortableDefaultMutableTreeNode picture4;

    private final PictureInfo pi5 = new PictureInfo(new File("/images/image5.jpg"), "Fifth Picture");

    /**
     * Set up for each test
     *
     */
    @Before
    public void setUp() {

        rootNode = new SortableDefaultMutableTreeNode();
        group1 = new SortableDefaultMutableTreeNode(gr1);
        group2 = new SortableDefaultMutableTreeNode(gr2);
        group3 = new SortableDefaultMutableTreeNode(gr3);
        group4 = new SortableDefaultMutableTreeNode(gr4);
        group5 = new SortableDefaultMutableTreeNode(gr5);
        picture1 = new SortableDefaultMutableTreeNode(pi1);
        picture2 = new SortableDefaultMutableTreeNode(pi2);
        picture3 = new SortableDefaultMutableTreeNode(pi3);
        picture4 = new SortableDefaultMutableTreeNode(pi4);
        SortableDefaultMutableTreeNode picture5 = new SortableDefaultMutableTreeNode(pi5);

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
        rootNode.add(group1);
        rootNode.add(group2);
        group1.add(picture1);
        group1.add(picture2);
        group2.add(picture3);
        group2.add(picture4);
        group2.add(group3);
        group3.add(picture5);
        group3.add(group4);
        group4.add(group5);
    }

    /**
     * Tests that the root node gets created
     */
    @Test
    public void testConstructor() {
        assertNotNull("Checking that rootNode was constructed properly", rootNode);
    }

    /**
     * tests that we can get to a previous picture
     */
    @Test
    public void testGetPreviousPicture() {
        assertSame("Checking the GetPreviousPicture", picture3, picture4.getPreviousPicture());
    }

    /**
     * tests that we can go to a next picture across a group boundary
     */
    @Test
    public void testGetPreviousPictureAcrossGroupBoundary() {
        assertSame("Checking the GetPreviousPicture across a group boundary", picture2, picture3.getPreviousPicture());
    }

    /**
     * test that we can't go back further than the beginning
     */
    @Test
    public void testGetPreviousPictureAtBeginning() {
        assertSame("Checking the GetPreviousPicture at the beginning", null, picture1.getPreviousPicture());
    }

    /**
     * assets that group nodes can have children and pictures can't
     */
    @Test
    public void testGetAllowChildren() {
        assertTrue("Checking that a GroupInfo node allows children", group1.getAllowsChildren());
        assertFalse("Checking that a PictureInfo node does not allow children", picture1.getAllowsChildren());
    }

    /**
     * test for moving a node onto another
     */
    @Test
    public void testMoveToNode() {
        assertEquals("Before moving, picture4 is owned by Group2", picture4.getParent(), group2);
        assertTrue("Move should work", picture4.moveToLastChild(group1));
        assertEquals("After moving, picture4 is owned by Group1", picture4.getParent(), group1);

        assertNull("The parent of the root node is null before a move", rootNode.getParent());
        assertFalse("Move should fail", rootNode.moveToLastChild(group1));
        assertNull("The parent of the root node is null after a move because it was not moved", rootNode.getParent());

        assertEquals("Before the move group2 was a child of rootNode", group2.getParent(), rootNode);
        assertFalse("Move should fail", group2.moveToLastChild(picture2));
        assertEquals("The group2 is still a child of rootNode because picture2 doesn't allow children", group2.getParent(), rootNode);
        assertTrue("Move should work", group2.moveToLastChild(group1));
        assertEquals("After the move group2 is child of group1", group2.getParent(), group1);
    }

    /**
     * test for moving a node up
     */
    @Test
    public void testMoveBeforeError1() {
        assertNull("The parent of the root node is null before a move", rootNode.getParent());
        assertFalse("Move should fail", rootNode.moveBefore(group1));
        assertNull("The parent of the root node is null after a move because it was not moved", rootNode.getParent());
    }

    /**
     * test for moving a node up
     */
    @Test
    public void testMoveBeforeError2() {
        assertFalse("Move should fail", group1.moveBefore(rootNode));
        assertEquals("The parent of group1 is still the root node because the move before the root node was not done", rootNode, group1.getParent());
    }

    /**
     * test for moving a node onto it's parent
     */
    @Test
    public void testMoveBeforeNoParentGroup() {
        SortableDefaultMutableTreeNode noParentGroup = new SortableDefaultMutableTreeNode();
        assertTrue("Move should succeed", noParentGroup.moveBefore(picture4));
        assertEquals("The parent of the noParentGroup is group2 after a move because", group2, noParentGroup.getParent());
    }

    /**
     * test for moving a node before a group
     */
    @Test
    public void testMoveBeforeMoveGroup() {
        assertEquals("Before moving, picture4 is owned by Group2", picture4.getParent(), group2);
        assertTrue("Move should work", picture4.moveBefore(picture2));
        assertEquals("After moving, picture4 is owned by Group1", picture4.getParent(), group1);
        assertEquals("After moving, picture4 should be at index 1 of Group1", 1, group1.getIndex(picture4));
    }

    /**
     * test for moving a node up
     */
    @Test
    public void testMoveBefore2() {
        assertEquals("Before moving, picture4 is owned by Group2", picture4.getParent(), group2);
        assertEquals("Before moving, picture3 is owned by Group2", picture3.getParent(), group2);
        assertTrue("Move should work", picture4.moveBefore(picture2));
        assertTrue("Move should work", picture3.moveBefore(picture4));
        assertEquals("After moving, picture3 is owned by Group1", picture2.getParent(), group1);
        assertEquals("After moving, picture3 should be at index 1 of Group1", 1, group1.getIndex(picture3));
        assertEquals("After moving, picture4 should have fallen back to index 2 of Group1", 2, group1.getIndex(picture4));
        assertEquals("After moving, picture2 should have fallen back to index 3 of Group1", 3, group1.getIndex(picture2));
    }

    /**
     * test for moving a node up
     */
    @Test
    public void testMoveBeforeMoveUp() {
        assertEquals("Before moving, picture4 is owned by Group2", picture4.getParent(), group2);
        assertEquals("Before moving, picture3 is owned by Group2", picture3.getParent(), group2);
        assertTrue("Move should work", picture4.moveBefore(picture2));
        assertTrue("Move should work", picture3.moveBefore(picture4));
        assertTrue("Move should work", picture2.moveBefore(picture3));
        assertEquals("After moving, picture2 should be at index 1 of Group1", 1, group1.getIndex(picture2));
        assertEquals("After moving, picture3 should be at index 2 of Group1", 2, group1.getIndex(picture3));
        assertEquals("After moving, picture4 should be at index 3 of Group1", 3, group1.getIndex(picture4));
    }

    /**
     * test for moving a node down
     */
    @Test
    public void testMoveBeforeMoveDown() {
        assertEquals("Before moving, picture1 should be at index 0 of Group1", 0, group1.getIndex(picture1));
        assertTrue("Move should work", picture4.moveBefore(picture2));
        assertTrue("Move should work", picture3.moveBefore(picture4));
        assertTrue("Move should work", picture2.moveBefore(picture3));
        assertTrue("Move should work", picture1.moveBefore(picture4));
        assertEquals("After moving, picture2 should be at index 0 of Group1", 0, group1.getIndex(picture2));
        assertEquals("After moving, picture3 should be at index 1 of Group1", 1, group1.getIndex(picture3));
        assertEquals("After moving, picture1 should be at index 2 of Group1", 2, group1.getIndex(picture1));
        assertEquals("After moving, picture4 should be at index 3 of Group1", 3, group1.getIndex(picture4));
    }

    /**
     * test for moving a node onto an index position
     */
    @Test
    public void testMoveToIndex() {
        assertEquals("Before moving, picture1 should be at index 0 of Group1", 0, group1.getIndex(picture1));
        assertEquals("Before moving, picture2 should be at index 1 of Group1", 1, group1.getIndex(picture2));
        assertEquals("Before moving, picture3 should be at index 0 of Group2", 0, group2.getIndex(picture3));
        assertEquals("Before moving, picture4 should be at index 1 of Group2", 1, group2.getIndex(picture4));
        assertTrue("Move should work", picture4.moveToIndex(group1, 0));
        assertTrue("Move should work", picture3.moveToIndex(group1, 0));
        assertEquals("After moving, picture1 should be at index 2 of Group1", 2, group1.getIndex(picture1));
        assertEquals("After moving, picture2 should be at index 3 of Group1", 3, group1.getIndex(picture2));
        assertEquals("After moving, picture3 should be at index 0 of Group1", 0, group1.getIndex(picture3));
        assertEquals("After moving, picture4 should be at index 1 of Group1", 1, group1.getIndex(picture4));
        assertTrue("Move should work", picture4.moveToIndex(group1, 3));
        assertTrue("Move should work", picture3.moveToIndex(group1, 2));
        assertTrue("Move should work", picture2.moveToIndex(group1, 1));
        assertEquals("After rearranging, picture1 should be at index 0 of Group1", 0, group1.getIndex(picture1));
        assertEquals("After rearranging, picture2 should be at index 1 of Group1", 1, group1.getIndex(picture2));
        assertEquals("After rearranging, picture3 should be at index 2 of Group1", 2, group1.getIndex(picture3));
        assertEquals("After rearranging, picture4 should be at index 3 of Group1", 3, group1.getIndex(picture4));

        assertEquals("The parent of the group2 is the rootNode before a move", rootNode, group2.getParent());
        assertTrue("Move should work", group2.moveToIndex(group1, 0));
        assertEquals("The parent of the group2 is group1 after a move", group1, group2.getParent());
    }

    /**
     * test moving to an invalid index
     */
    @Test
    public void testMoveToIndexErrors1() {
        assertFalse("Move should fail", group2.moveToIndex(group2, 0));
    }

    /**
     * test moving to an invalid index
     */
    @Test
    public void testMoveToIndexErrors2() {
        assertNull("The parent of the root node is null before a move", rootNode.getParent());
        assertFalse("Move should fail", rootNode.moveToIndex(group1, 0));
        assertNull("The parent of the root node is null after a move because it was not moved", rootNode.getParent());

    }

    /**
     * test the cloning of a picture
     */
    @Test
    public void testGetClonePicture() {
        SortableDefaultMutableTreeNode cloneNode = picture1.getClone();
        assertNotSame("The clone must be a new Object", picture1, cloneNode);
        assertNotSame("The user object must be a new Object", picture1.getUserObject(), cloneNode.getUserObject());
        assertNull("The clone has no parent", cloneNode.getParent());
        assertEquals("The clone node has the same highres picture as the original", pi1.getImageFile(), ((PictureInfo) cloneNode.getUserObject()).getImageFile());
    }

    /**
     * test the cloning of a group
     */
    @Test
    public void testGetCloneGroup() {
        SortableDefaultMutableTreeNode cloneNode = group2.getClone();
        assertNotSame("The clone must be a new Object", group2, cloneNode);
        assertNotSame("The user object must be a new Object", group2.getUserObject(), cloneNode.getUserObject());
        assertNull("The clone has no parent", cloneNode.getParent());
        assertTrue("The clones userObject is of type GroupInfo", cloneNode.getUserObject() instanceof GroupInfo);
        assertEquals("The clone has the same number of children", group2.getChildCount(), cloneNode.getChildCount());
    }

    /**
     * test that we end up with the correct child nodes
     */
    @Test
    public void testGetChildPictureNodes() {
        List<SortableDefaultMutableTreeNode> allPicturesFromRoot = rootNode.getChildPictureNodes(true);
        assertEquals("There should be 5 pictures in the result set from root, recursive", 5, allPicturesFromRoot.size());
        assertEquals("There should be 0 pictures under the root node when non recursive", 0, rootNode.getChildPictureNodes(false).size());
        assertEquals("There should be 2 pictures under the group1 node when non recursive", 2, group1.getChildPictureNodes(false).size());
        assertEquals("There should be 2 pictures under the group1 node when recursive", 2, group1.getChildPictureNodes(true).size());
    }

    /**
     * Tests the hasChildPictureNodes method
     */
    @Test
    public void testHasChildPictureNodes() {
        assertTrue("The root node of the test tree should report it has pictures", rootNode.hasChildPictureNodes());
        assertTrue("The group1 node of the test tree should report it has pictures", group1.hasChildPictureNodes());
        assertTrue("The group3 node of the test tree should report it has pictures", group3.hasChildPictureNodes());
        assertFalse("The group4 node of the test tree should report it has NO pictures", group4.hasChildPictureNodes());
        assertFalse("The group5 node of the test tree should report it has NO pictures", group5.hasChildPictureNodes());
    }

    @Test
    public void testSortChildrenGroups() {
        try {
            SwingUtilities.invokeAndWait(() -> {

                SortableDefaultMutableTreeNode root = new SortableDefaultMutableTreeNode();
                GroupInfo gA = new GroupInfo("A");
                GroupInfo gB = new GroupInfo("B");
                GroupInfo gC = new GroupInfo("C");
                GroupInfo gD = new GroupInfo("D");
                GroupInfo gE = new GroupInfo("E");
                root.add(new SortableDefaultMutableTreeNode(gD));
                root.add(new SortableDefaultMutableTreeNode(gE));
                root.add(new SortableDefaultMutableTreeNode(gC));
                root.add(new SortableDefaultMutableTreeNode(gB));
                root.add(new SortableDefaultMutableTreeNode(gA));
                root.sortChildren(Settings.FieldCodes.DESCRIPTION);
                assertEquals("A", ((SortableDefaultMutableTreeNode) root.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) root.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) root.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) root.getChildAt(3)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) root.getChildAt(4)).getUserObject().toString());
            });
        } catch (InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }


    @Test
    public void testSortChildrenGroupsOnCreationTime() {
        try {
            SwingUtilities.invokeAndWait(() -> {

                SortableDefaultMutableTreeNode root = new SortableDefaultMutableTreeNode();
                GroupInfo gA = new GroupInfo("A");
                GroupInfo gB = new GroupInfo("B");
                GroupInfo gC = new GroupInfo("C");
                GroupInfo gD = new GroupInfo("D");
                GroupInfo gE = new GroupInfo("E");
                root.add(new SortableDefaultMutableTreeNode(gD));
                root.add(new SortableDefaultMutableTreeNode(gE));
                root.add(new SortableDefaultMutableTreeNode(gC));
                root.add(new SortableDefaultMutableTreeNode(gB));
                root.add(new SortableDefaultMutableTreeNode(gA));
                root.sortChildren(Settings.FieldCodes.CREATION_TIME);
                assertEquals("D", ((SortableDefaultMutableTreeNode) root.getChildAt(0)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) root.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) root.getChildAt(2)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) root.getChildAt(3)).getUserObject().toString());
                assertEquals("A", ((SortableDefaultMutableTreeNode) root.getChildAt(4)).getUserObject().toString());
            });
        } catch (InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testSortChildrenPicturesByDescription() {
        try {
            SwingUtilities.invokeAndWait(() -> {

                SortableDefaultMutableTreeNode root = new SortableDefaultMutableTreeNode();
                PictureInfo pA = new PictureInfo();
                pA.setDescription("A");
                PictureInfo pB = new PictureInfo();
                pB.setDescription("B");
                PictureInfo pC = new PictureInfo();
                pC.setDescription("C");
                PictureInfo pD = new PictureInfo();
                pD.setDescription("D");
                PictureInfo pE = new PictureInfo();
                pE.setDescription("E");
                root.add(new SortableDefaultMutableTreeNode(pD));
                root.add(new SortableDefaultMutableTreeNode(pE));
                root.add(new SortableDefaultMutableTreeNode(pC));
                root.add(new SortableDefaultMutableTreeNode(pB));
                root.add(new SortableDefaultMutableTreeNode(pA));
                root.sortChildren(Settings.FieldCodes.DESCRIPTION);
                assertEquals("A", ((SortableDefaultMutableTreeNode) root.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) root.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) root.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) root.getChildAt(3)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) root.getChildAt(4)).getUserObject().toString());
            });
        } catch (InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testSortChildrenPicturesByFilmReference() {
        try {
            SwingUtilities.invokeAndWait(() -> {

                SortableDefaultMutableTreeNode root = new SortableDefaultMutableTreeNode();
                PictureInfo pA = new PictureInfo();
                pA.setFilmReference("A");
                PictureInfo pB = new PictureInfo();
                pB.setFilmReference("B");
                PictureInfo pC = new PictureInfo();
                pC.setFilmReference("C");
                PictureInfo pD = new PictureInfo();
                pD.setFilmReference("D");
                PictureInfo pE = new PictureInfo();
                pE.setFilmReference("E");
                pA.setDescription("A");
                pB.setDescription("B");
                pC.setDescription("C");
                pD.setDescription("D");
                pE.setDescription("E");
                root.add(new SortableDefaultMutableTreeNode(pD));
                root.add(new SortableDefaultMutableTreeNode(pE));
                root.add(new SortableDefaultMutableTreeNode(pC));
                root.add(new SortableDefaultMutableTreeNode(pB));
                root.add(new SortableDefaultMutableTreeNode(pA));
                root.sortChildren(Settings.FieldCodes.FILM_REFERENCE);
                assertEquals("A", ((SortableDefaultMutableTreeNode) root.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) root.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) root.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) root.getChildAt(3)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) root.getChildAt(4)).getUserObject().toString());
            });
        } catch (InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testSortChildrenPicturesByComment() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                SortableDefaultMutableTreeNode root = new SortableDefaultMutableTreeNode();
                PictureInfo pA = new PictureInfo();
                PictureInfo pB = new PictureInfo();
                PictureInfo pC = new PictureInfo();
                PictureInfo pD = new PictureInfo();
                PictureInfo pE = new PictureInfo();
                pA.setDescription("A");
                pB.setDescription("B");
                pC.setDescription("C");
                pD.setDescription("D");
                pE.setDescription("E");
                pA.setComment("A");
                pB.setComment("B");
                pC.setComment("C");
                pD.setComment("D");
                pE.setComment("E");
                root.add(new SortableDefaultMutableTreeNode(pD));
                root.add(new SortableDefaultMutableTreeNode(pE));
                root.add(new SortableDefaultMutableTreeNode(pC));
                root.add(new SortableDefaultMutableTreeNode(pB));
                root.add(new SortableDefaultMutableTreeNode(pA));
                root.sortChildren(Settings.FieldCodes.COMMENT);
                assertEquals("A", ((SortableDefaultMutableTreeNode) root.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) root.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) root.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) root.getChildAt(3)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) root.getChildAt(4)).getUserObject().toString());
            });
        } catch (InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testSortChildrenPicturesByPhotographer() {
        try {
            SwingUtilities.invokeAndWait(() -> {

                SortableDefaultMutableTreeNode root = new SortableDefaultMutableTreeNode();
                PictureInfo pA = new PictureInfo();
                pA.setPhotographer("A");
                PictureInfo pB = new PictureInfo();
                pB.setPhotographer("B");
                PictureInfo pC = new PictureInfo();
                pC.setPhotographer("C");
                PictureInfo pD = new PictureInfo();
                pD.setPhotographer("D");
                PictureInfo pE = new PictureInfo();
                pE.setPhotographer("E");
                pA.setDescription("A");
                pB.setDescription("B");
                pC.setDescription("C");
                pD.setDescription("D");
                pE.setDescription("E");
                root.add(new SortableDefaultMutableTreeNode(pD));
                root.add(new SortableDefaultMutableTreeNode(pE));
                root.add(new SortableDefaultMutableTreeNode(pC));
                root.add(new SortableDefaultMutableTreeNode(pB));
                root.add(new SortableDefaultMutableTreeNode(pA));
                root.sortChildren(Settings.FieldCodes.PHOTOGRAPHER);
                assertEquals("A", ((SortableDefaultMutableTreeNode) root.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) root.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) root.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) root.getChildAt(3)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) root.getChildAt(4)).getUserObject().toString());
            });
        } catch (InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testSortChildrenPicturesByCopyrightHolder() {
        try {
            SwingUtilities.invokeAndWait(() -> {

                SortableDefaultMutableTreeNode root = new SortableDefaultMutableTreeNode();
                PictureInfo pA = new PictureInfo();
                pA.setCopyrightHolder("A");
                PictureInfo pB = new PictureInfo();
                pB.setCopyrightHolder("B");
                PictureInfo pC = new PictureInfo();
                pC.setCopyrightHolder("C");
                PictureInfo pD = new PictureInfo();
                pD.setCopyrightHolder("D");
                PictureInfo pE = new PictureInfo();
                pE.setCopyrightHolder("E");
                pA.setDescription("A");
                pB.setDescription("B");
                pC.setDescription("C");
                pD.setDescription("D");
                pE.setDescription("E");
                root.add(new SortableDefaultMutableTreeNode(pD));
                root.add(new SortableDefaultMutableTreeNode(pE));
                root.add(new SortableDefaultMutableTreeNode(pC));
                root.add(new SortableDefaultMutableTreeNode(pB));
                root.add(new SortableDefaultMutableTreeNode(pA));
                root.sortChildren(Settings.FieldCodes.COPYRIGHT_HOLDER);
                assertEquals("A", ((SortableDefaultMutableTreeNode) root.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) root.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) root.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) root.getChildAt(3)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) root.getChildAt(4)).getUserObject().toString());
            });
        } catch (InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testSortChildrenPicturesByCreationTime() {
        try {
            SwingUtilities.invokeAndWait(() -> {

                SortableDefaultMutableTreeNode root = new SortableDefaultMutableTreeNode();
                PictureInfo pA = new PictureInfo();
                pA.setCreationTime("A");
                PictureInfo pB = new PictureInfo();
                pB.setCreationTime("B");
                PictureInfo pC = new PictureInfo();
                pC.setCreationTime("C");
                PictureInfo pD = new PictureInfo();
                pD.setCreationTime("D");
                PictureInfo pE = new PictureInfo();
                pE.setCreationTime("E");
                pA.setDescription("A");
                pB.setDescription("B");
                pC.setDescription("C");
                pD.setDescription("D");
                pE.setDescription("E");
                root.add(new SortableDefaultMutableTreeNode(pD));
                root.add(new SortableDefaultMutableTreeNode(pE));
                root.add(new SortableDefaultMutableTreeNode(pC));
                root.add(new SortableDefaultMutableTreeNode(pB));
                root.add(new SortableDefaultMutableTreeNode(pA));
                root.sortChildren(Settings.FieldCodes.CREATION_TIME);
                assertEquals("A", ((SortableDefaultMutableTreeNode) root.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) root.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) root.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) root.getChildAt(3)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) root.getChildAt(4)).getUserObject().toString());
            });
        } catch (InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }


    @Test
    public void testSortChildrenMixedGroups() {
        try {
            SwingUtilities.invokeAndWait(() -> {

                SortableDefaultMutableTreeNode root = new SortableDefaultMutableTreeNode();
                GroupInfo gA = new GroupInfo("A");
                GroupInfo gB = new GroupInfo("B");
                PictureInfo pC = new PictureInfo();
                pC.setDescription("C");
                PictureInfo pD = new PictureInfo();
                pD.setDescription("D");
                GroupInfo gE = new GroupInfo("E");
                root.add(new SortableDefaultMutableTreeNode(pD));
                root.add(new SortableDefaultMutableTreeNode(gE));
                root.add(new SortableDefaultMutableTreeNode(pC));
                root.add(new SortableDefaultMutableTreeNode(gB));
                root.add(new SortableDefaultMutableTreeNode(gA));
                root.sortChildren(Settings.FieldCodes.DESCRIPTION);
                assertEquals("A", ((SortableDefaultMutableTreeNode) root.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) root.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) root.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) root.getChildAt(3)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) root.getChildAt(4)).getUserObject().toString());
            });
        } catch (InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}
