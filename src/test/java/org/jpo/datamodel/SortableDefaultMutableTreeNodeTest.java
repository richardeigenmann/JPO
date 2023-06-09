package org.jpo.datamodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2023 Richard Eigenmann, Zurich, Switzerland
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
 * Tests for the SortableDefaultMutableTreeNode tests
 *
 * @author Richard Eigenmann
 */
class SortableDefaultMutableTreeNodeTest {

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
    @BeforeEach
    public void setUp() {
        final var pictureCollection = new PictureCollection();
        rootNode = new SortableDefaultMutableTreeNode();
        pictureCollection.getRootNode().add(rootNode);
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
    void testConstructor() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        assertNotNull(rootNode);
    }

    /**
     * tests that we can get to a previous picture
     */
    @Test
    void testGetPreviousPicture() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        assertSame(picture3, picture4.getPreviousPicture());
    }

    /**
     * tests that we can go to a next picture across a group boundary
     */
    @Test
    void testGetPreviousPictureAcrossGroupBoundary() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        assertSame(picture2, picture3.getPreviousPicture());
    }

    /**
     * test that we can't go back further than the beginning
     */
    @Test
    void testGetPreviousPictureAtBeginning() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        assertSame(null, picture1.getPreviousPicture());
    }

    /**
     * assets that group nodes can have children and pictures can't
     */
    @Test
    void testGetAllowChildren() {
        assertTrue(group1.getAllowsChildren());
        assertFalse(picture1.getAllowsChildren());
    }

    /**
     * test for moving a node onto another
     */
    @Test
    void testMoveToNode() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        // Before moving, picture4 is owned by Group2
        assertEquals(picture4.getParent(), group2);
        // Move should work
        assertTrue(picture4.moveToLastChild(group1));
        // After moving, picture4 is owned by Group1
        assertEquals(picture4.getParent(), group1);

        // The parent of the root node is the picture collection root node before a move
        assertEquals(rootNode.getPictureCollection().getRootNode(),rootNode.getParent());
        // Move should fail
        assertFalse(rootNode.moveToLastChild(group1));
        // The parent of the root node is null after a move because it was not moved
        assertEquals(rootNode.getPictureCollection().getRootNode(), rootNode.getParent());

        // Before the move group2 was a child of rootNode
        assertEquals( group2.getParent(), rootNode);
        // Move should fail
        assertFalse( group2.moveToLastChild(picture2));
        // The group2 is still a child of rootNode because picture2 doesn't allow children
        assertEquals( group2.getParent(), rootNode);
        // Move should work
        assertTrue( group2.moveToLastChild(group1));
        // After the move group2 is child of group1
        assertEquals( group2.getParent(), group1);
    }

    /**
     * test for moving a node up
     */
    @Test
    void testMoveBeforeErrorWhenAtTop() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        // The parent of the root node is the collection root node
        assertEquals(rootNode.getPictureCollection().getRootNode(), rootNode.getParent());
        // Move should fail
        assertFalse(rootNode.moveBefore(group1));
        // The parent of the root node is still the root node of the collection
        assertEquals(rootNode.getPictureCollection().getRootNode(), rootNode.getParent());
    }

    /**
     * test for moving a node up
     */
    @Test
    void testMoveBeforeError2() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        // Move should fail
        assertFalse(rootNode.moveBefore(rootNode.getPictureCollection().getRootNode()));
        // The parent of group1 is still the root node because the move before the root node was not done
        assertEquals(rootNode, group1.getParent());
    }

    /**
     * test for moving a node onto it's parent
     */
    @Test
    void testMoveBeforeNoParentGroup() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SortableDefaultMutableTreeNode noParentGroup = new SortableDefaultMutableTreeNode();
        // Move should succeed
        assertTrue(noParentGroup.moveBefore(picture4));
        // The parent of the noParentGroup is group2 after a move because
        assertEquals(group2, noParentGroup.getParent());
    }

    /**
     * test for moving a node before a group
     */
    @Test
    void testMoveBeforeMoveGroup() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        // Before moving, picture4 is owned by Group2
        assertEquals(picture4.getParent(), group2);
        assertTrue(picture4.moveBefore(picture2));
        // After moving, picture4 is owned by Group1
        assertEquals(picture4.getParent(), group1);
        // After moving, picture4 should be at index 1 of Group1
        assertEquals(1, group1.getIndex(picture4));
    }

    /**
     * test for moving a node up
     */
    @Test
    void testMoveBefore2() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        // Before moving, picture4 is owned by Group2
        assertEquals(picture4.getParent(), group2);
        // Before moving, picture3 is owned by Group2
        assertEquals(picture3.getParent(), group2);
        assertTrue(picture4.moveBefore(picture2));
        assertTrue(picture3.moveBefore(picture4));
        // After moving, picture3 is owned by Group1
        assertEquals(picture2.getParent(), group1);
        // After moving, picture3 should be at index 1 of Group1
        assertEquals(1, group1.getIndex(picture3));
                //After moving, picture4 should have fallen back to index 2 of Group1
        assertEquals( 2, group1.getIndex(picture4));
                // After moving, picture2 should have fallen back to index 3 of Group1
        assertEquals( 3, group1.getIndex(picture2));
    }

    /**
     * test for moving a node up
     */
    @Test
    void testMoveBeforeMoveUp() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        // Before moving, picture4 is owned by Group2
        assertEquals(picture4.getParent(), group2);
        // Before moving, picture3 is owned by Group2
        assertEquals(picture3.getParent(), group2);
        assertTrue(picture4.moveBefore(picture2));
        assertTrue(picture3.moveBefore(picture4));
        assertTrue(picture2.moveBefore(picture3));
        // After moving, picture2 should be at index 1 of Group1
        assertEquals(1, group1.getIndex(picture2));
        // After moving, picture3 should be at index 2 of Group1
        assertEquals( 2, group1.getIndex(picture3));
        // After moving, picture4 should be at index 3 of Group1
        assertEquals( 3, group1.getIndex(picture4));
    }

    /**
     * test for moving a node down
     */
    @Test
    void testMoveBeforeMoveDown() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        // Before moving, picture1 should be at index 0 of Group1
        assertEquals(0, group1.getIndex(picture1));
        assertTrue(picture4.moveBefore(picture2));
        assertTrue(picture3.moveBefore(picture4));
        assertTrue(picture2.moveBefore(picture3));
        assertTrue(picture1.moveBefore(picture4));
        // After moving, picture2 should be at index 0 of Group1
        assertEquals(0, group1.getIndex(picture2));
        // After moving, picture3 should be at index 1 of Group1
        assertEquals( 1, group1.getIndex(picture3));
        // After moving, picture1 should be at index 2 of Group1
        assertEquals( 2, group1.getIndex(picture1));
        // After moving, picture4 should be at index 3 of Group1
        assertEquals( 3, group1.getIndex(picture4));
    }

    /**
     * test for moving a node onto an index position
     */
    @Test
    void testMoveToIndex() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        // Before moving, picture1 should be at index 0 of Group1
        assertEquals(0, group1.getIndex(picture1));
        // Before moving, picture2 should be at index 1 of Group1
        assertEquals(1, group1.getIndex(picture2));
        // Before moving, picture3 should be at index 0 of Group2
        assertEquals(0, group2.getIndex(picture3));
        // Before moving, picture4 should be at index 1 of Group2
        assertEquals(1, group2.getIndex(picture4));
        assertTrue(picture4.moveToIndex(group1, 0));
        assertTrue( picture3.moveToIndex(group1, 0));
        // After moving, picture1 should be at index 2 of Group1
        assertEquals(2, group1.getIndex(picture1));
        // After moving, picture2 should be at index 3 of Group1
        assertEquals(3, group1.getIndex(picture2));
        // After moving, picture3 should be at index 0 of Group1
        assertEquals(0, group1.getIndex(picture3));
        // After moving, picture4 should be at index 1 of Group1
        assertEquals(1, group1.getIndex(picture4));
        assertTrue( picture4.moveToIndex(group1, 3));
        assertTrue( picture3.moveToIndex(group1, 2));
        assertTrue( picture2.moveToIndex(group1, 1));
        // After rearranging, picture1 should be at index 0 of Group1
        assertEquals(0, group1.getIndex(picture1));
        // After rearranging, picture2 should be at index 1 of Group1
        assertEquals( 1, group1.getIndex(picture2));
        // After rearranging, picture3 should be at index 2 of Group1
        assertEquals( 2, group1.getIndex(picture3));
        // After rearranging, picture4 should be at index 3 of Group1
        assertEquals( 3, group1.getIndex(picture4));

        // The parent of the group2 is the rootNode before a move
        assertEquals( rootNode, group2.getParent());
        assertTrue(group2.moveToIndex(group1, 0));
        // The parent of the group2 is group1 after a move
        assertEquals( group1, group2.getParent());
    }

    /**
     * test moving to an invalid index
     */
    @Test
    void testMoveToIndexErrors1() {
        assertFalse(group2.moveToIndex(group2, 0));
    }

    /**
     * test moving to an invalid index
     */
    @Test
    void testMoveToIndexErrors2() {
        // The parent of the root node is the collection root
        assertEquals(rootNode.getPictureCollection().getRootNode(), rootNode.getParent());
        assertFalse(rootNode.moveToIndex(group1, 0));
        // The parent of the root node is still the same after a move because it was not moved
        assertEquals(rootNode.getPictureCollection().getRootNode(), rootNode.getParent());

    }

    /**
     * test the cloning of a picture
     */
    @Test
    void testGetClonePicture() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SortableDefaultMutableTreeNode cloneNode = picture1.getClone();
        // The clone must be a new Object
        assertNotSame(picture1, cloneNode);
        // The user object must be a new Object
        assertNotSame(picture1.getUserObject(), cloneNode.getUserObject());
        // The clone has no parent
        assertNull(cloneNode.getParent());
        // The clone node has the same highres picture as the original
        assertEquals(pi1.getImageFile(), ((PictureInfo) cloneNode.getUserObject()).getImageFile());
    }

    /**
     * test the cloning of a group
     */
    @Test
    void testGetCloneGroup() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SortableDefaultMutableTreeNode cloneNode = group2.getClone();
        assertNotSame(group2, cloneNode);
        assertNotSame(group2.getUserObject(), cloneNode.getUserObject());
        assertNull(cloneNode.getParent());
        assertTrue(cloneNode.getUserObject() instanceof GroupInfo);
        assertEquals(group2.getChildCount(), cloneNode.getChildCount());
    }

    /**
     * test that we end up with the correct child nodes
     */
    @Test
    void testGetChildPictureNodes() {
        final List<SortableDefaultMutableTreeNode> allPicturesFromRoot = rootNode.getChildPictureNodes(true);
        assertEquals(5, allPicturesFromRoot.size());
        assertEquals(0, rootNode.getChildPictureNodes(false).size());
        assertEquals(2, group1.getChildPictureNodes(false).size());
        assertEquals(2, group1.getChildPictureNodes(true).size());
    }

    /**
     * test that we end up with the correct child nodes
     */
    @Test
    void testGetChildPictureNodesDFS() {
        assertEquals(5, rootNode.getChildPictureNodesDFS().count());
        assertEquals(2, group1.getChildPictureNodesDFS().count());
    }

    /**
     * Tests the hasChildPictureNodes method
     */
    @Test
    void testHasChildPictureNodes() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        assertTrue(rootNode.hasChildPictureNodes());
        assertTrue(group1.hasChildPictureNodes());
        assertTrue(group3.hasChildPictureNodes());
        assertFalse(group4.hasChildPictureNodes());
        assertFalse(group5.hasChildPictureNodes());
    }

    @Test
    void testSortChildrenGroups() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var root = new SortableDefaultMutableTreeNode();
                final var gA = new GroupInfo("A");
                final var gB = new GroupInfo("B");
                final var gC = new GroupInfo("C");
                final var gD = new GroupInfo("D");
                final var gE = new GroupInfo("E");
                root.add(new SortableDefaultMutableTreeNode(gD));
                root.add(new SortableDefaultMutableTreeNode(gE));
                root.add(new SortableDefaultMutableTreeNode(gC));
                root.add(new SortableDefaultMutableTreeNode(gB));
                root.add(new SortableDefaultMutableTreeNode(gA));
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(root);
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
    void testSortChildrenGroupsOnCreationTime() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var rootNode = new SortableDefaultMutableTreeNode();
                final var groupInfoA = new GroupInfo("A");
                final var groupInfoB = new GroupInfo("B");
                final var groupInfoC = new GroupInfo("C");
                final var groupInfoD = new GroupInfo("D");
                final var groupInfoE = new GroupInfo("E");
                rootNode.add(new SortableDefaultMutableTreeNode(groupInfoD));
                rootNode.add(new SortableDefaultMutableTreeNode(groupInfoE));
                rootNode.add(new SortableDefaultMutableTreeNode(groupInfoC));
                rootNode.add(new SortableDefaultMutableTreeNode(groupInfoB));
                rootNode.add(new SortableDefaultMutableTreeNode(groupInfoA));
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(rootNode);

                rootNode.sortChildren(Settings.FieldCodes.CREATION_TIME);
                assertEquals("A", ((SortableDefaultMutableTreeNode) rootNode.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) rootNode.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) rootNode.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) rootNode.getChildAt(3)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) rootNode.getChildAt(4)).getUserObject().toString());
            });
        } catch (final InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testSortChildrenPicturesByDescription() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var groupNode = new SortableDefaultMutableTreeNode(new GroupInfo("GroupNode"));
                final var pA = new PictureInfo();
                pA.setDescription("A");
                final var pB = new PictureInfo();
                pB.setDescription("B");
                final var pC = new PictureInfo();
                pC.setDescription("C");
                final var pD = new PictureInfo();
                pD.setDescription("D");
                final var pE = new PictureInfo();
                pE.setDescription("E");
                groupNode.add(new SortableDefaultMutableTreeNode(pD));
                groupNode.add(new SortableDefaultMutableTreeNode(pE));
                groupNode.add(new SortableDefaultMutableTreeNode(pC));
                groupNode.add(new SortableDefaultMutableTreeNode(pB));
                groupNode.add(new SortableDefaultMutableTreeNode(pA));
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(groupNode);

                groupNode.sortChildren(Settings.FieldCodes.DESCRIPTION);
                assertEquals("A", ((SortableDefaultMutableTreeNode) groupNode.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) groupNode.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) groupNode.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) groupNode.getChildAt(3)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) groupNode.getChildAt(4)).getUserObject().toString());
            });
        } catch (final InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testSortChildrenPicturesByFilmReference() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {

                final var root = new SortableDefaultMutableTreeNode();
                final var pA = new PictureInfo();
                pA.setFilmReference("A");
                final var pB = new PictureInfo();
                pB.setFilmReference("B");
                final var pC = new PictureInfo();
                pC.setFilmReference("C");
                final var pD = new PictureInfo();
                pD.setFilmReference("D");
                final var pE = new PictureInfo();
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
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(root);

                root.sortChildren(Settings.FieldCodes.FILM_REFERENCE);
                assertEquals("A", ((SortableDefaultMutableTreeNode) root.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) root.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) root.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) root.getChildAt(3)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) root.getChildAt(4)).getUserObject().toString());
            });
        } catch (final InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testSortChildrenPicturesByComment() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var root = new SortableDefaultMutableTreeNode();
                final var pA = new PictureInfo();
                final var pB = new PictureInfo();
                final var pC = new PictureInfo();
                final var pD = new PictureInfo();
                final var pE = new PictureInfo();
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
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(root);

                root.sortChildren(Settings.FieldCodes.COMMENT);
                assertEquals("A", ((SortableDefaultMutableTreeNode) root.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) root.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) root.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) root.getChildAt(3)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) root.getChildAt(4)).getUserObject().toString());
            });
        } catch (final InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testSortChildrenPicturesByPhotographer() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {

                final var root = new SortableDefaultMutableTreeNode();
                final var pA = new PictureInfo();
                pA.setPhotographer("A");
                final var pB = new PictureInfo();
                pB.setPhotographer("B");
                final var pC = new PictureInfo();
                pC.setPhotographer("C");
                final var pD = new PictureInfo();
                pD.setPhotographer("D");
                final var pE = new PictureInfo();
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
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(root);

                root.sortChildren(Settings.FieldCodes.PHOTOGRAPHER);
                assertEquals("A", ((SortableDefaultMutableTreeNode) root.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) root.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) root.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) root.getChildAt(3)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) root.getChildAt(4)).getUserObject().toString());
            });
        } catch (final InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testSortChildrenPicturesByCopyrightHolder() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {

                final var root = new SortableDefaultMutableTreeNode();
                final var pA = new PictureInfo();
                pA.setCopyrightHolder("A");
                final var pB = new PictureInfo();
                pB.setCopyrightHolder("B");
                final var pC = new PictureInfo();
                pC.setCopyrightHolder("C");
                final var pD = new PictureInfo();
                pD.setCopyrightHolder("D");
                final var pE = new PictureInfo();
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
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(root);
                root.sortChildren(Settings.FieldCodes.COPYRIGHT_HOLDER);
                assertEquals("A", ((SortableDefaultMutableTreeNode) root.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) root.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) root.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) root.getChildAt(3)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) root.getChildAt(4)).getUserObject().toString());
            });
        } catch (final InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testSortChildrenPicturesByCreationTime() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {

                final var root = new SortableDefaultMutableTreeNode();
                final var pA = new PictureInfo();
                pA.setDescription("A");
                pA.setCreationTime("2021-10-02 at 14.43.23"); //5
                final var pB = new PictureInfo();
                pB.setDescription("B");
                pB.setCreationTime("2021-10-01 at 18.49.44"); //1
                final var pC = new PictureInfo();
                pC.setDescription("C");
                pC.setCreationTime("2021:10:01 20:03:35"); //2
                final var pD = new PictureInfo();
                pD.setDescription("D");
                pD.setCreationTime("2021:10:02 12:09:42"); //4
                final var pE = new PictureInfo();
                pE.setDescription("E");
                pE.setCreationTime("2021:10:01 09:44:17"); //3
                root.add(new SortableDefaultMutableTreeNode(pA));
                root.add(new SortableDefaultMutableTreeNode(pB));
                root.add(new SortableDefaultMutableTreeNode(pC));
                root.add(new SortableDefaultMutableTreeNode(pD));
                root.add(new SortableDefaultMutableTreeNode(pE));
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(root);

                root.sortChildren(Settings.FieldCodes.CREATION_TIME);
                assertEquals("E", ((SortableDefaultMutableTreeNode) root.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) root.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) root.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) root.getChildAt(3)).getUserObject().toString());
                assertEquals("A", ((SortableDefaultMutableTreeNode) root.getChildAt(4)).getUserObject().toString());
            });
        } catch (final InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }


    @Test
    void testSortChildrenMixedGroups() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {

                final var root = new SortableDefaultMutableTreeNode();
                final var gA = new GroupInfo("A");
                final var gB = new GroupInfo("B");
                final var pC = new PictureInfo();
                pC.setDescription("C");
                final var pD = new PictureInfo();
                pD.setDescription("D");
                final var gE = new GroupInfo("E");
                root.add(new SortableDefaultMutableTreeNode(pD));
                root.add(new SortableDefaultMutableTreeNode(gE));
                root.add(new SortableDefaultMutableTreeNode(pC));
                root.add(new SortableDefaultMutableTreeNode(gB));
                root.add(new SortableDefaultMutableTreeNode(gA));
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(root);

                root.sortChildren(Settings.FieldCodes.DESCRIPTION);

                assertEquals("A", ((SortableDefaultMutableTreeNode) root.getChildAt(0)).getUserObject().toString());
                assertEquals("B", ((SortableDefaultMutableTreeNode) root.getChildAt(1)).getUserObject().toString());
                assertEquals("C", ((SortableDefaultMutableTreeNode) root.getChildAt(2)).getUserObject().toString());
                assertEquals("D", ((SortableDefaultMutableTreeNode) root.getChildAt(3)).getUserObject().toString());
                assertEquals("E", ((SortableDefaultMutableTreeNode) root.getChildAt(4)).getUserObject().toString());
            });
        } catch (final InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testCommon() {
        assertEquals(Paths.get("/a/b"), SortableDefaultMutableTreeNode.commonPath(Paths.get("/a/b/c"), Paths.get("/a/b/d")));
        assertEquals(Paths.get("/a"), SortableDefaultMutableTreeNode.commonPath(Paths.get("/a/"), Paths.get("/a/b/d")));
        assertEquals(Paths.get("/"), SortableDefaultMutableTreeNode.commonPath(Paths.get("/f/b/c"), Paths.get("/a/b/d")));
        assertEquals(Paths.get("/a/b/c"), SortableDefaultMutableTreeNode.commonPath(Paths.get("/a/b/c/d/e"), Paths.get("/a/b/f/../c/g")));
        assertEquals(Paths.get("C:/Winnt"), SortableDefaultMutableTreeNode.commonPath(Paths.get("C:/Winnt/System32"), Paths.get("C:/Winnt/System64")));
    }

    @Test
    void testGetCommonPath() {
        final var g1 = new SortableDefaultMutableTreeNode(new GroupInfo("G1"));
        final var p1 = new SortableDefaultMutableTreeNode(new PictureInfo(new File("/dir1/dir2/dir3"), "P1"));
        g1.add(p1);
        final var p2 = new SortableDefaultMutableTreeNode(new PictureInfo(new File("/dir1/dir2/dir4"), "P2"));
        g1.add(p2);
        final var p3 = new SortableDefaultMutableTreeNode(new PictureInfo(new File("/dir1/dir2/dir5"), "P3"));
        g1.add(p3);
        assertEquals(Paths.get("/dir1/dir2"), g1.getCommonPath());
    }

    @Test
    void testGetCommonPathTwo() {
        final var g1 = new SortableDefaultMutableTreeNode(new GroupInfo("G1"));
        final var p1 = new SortableDefaultMutableTreeNode(new PictureInfo(new File("/dir1/dir2/dir3"), "P1"));
        g1.add(p1);
        final var p2 = new SortableDefaultMutableTreeNode(new PictureInfo(new File("/dir4/dir5/dir6"), "P2"));
        g1.add(p2);
        assertEquals(Paths.get("/"), g1.getCommonPath());
    }


    @Test
    void testGetCommonPathWinMatch() {
        final var g1 = new SortableDefaultMutableTreeNode(new GroupInfo("G1"));
        final var p1 = new SortableDefaultMutableTreeNode(new PictureInfo(new File("C:/dir1/dir2/dir3"), "P1"));
        g1.add(p1);
        final var p3 = new SortableDefaultMutableTreeNode(new PictureInfo(new File("C:/dir1/dir2/dir4"), "P3"));
        g1.add(p3);
        assertEquals(Paths.get("C:/dir1/dir2"), g1.getCommonPath());
    }


    @Test
    void testGetCommonPathWin() {
        final var g1 = new SortableDefaultMutableTreeNode(new GroupInfo("G1"));
        final var p1 = new SortableDefaultMutableTreeNode(new PictureInfo(new File("C:/dir1/dir2/dir3"), "P1"));
        g1.add(p1);
        final var p2 = new SortableDefaultMutableTreeNode(new PictureInfo(new File("D:/dir1/dir2/dir4"), "P2"));
        g1.add(p2);
        assertNull(g1.getCommonPath());
    }

    @Test
    void testIsAncestor() {
        assert(group1.containsAnAncestor(List.of(rootNode)));
        assertFalse(rootNode.containsAnAncestor(List.of(group1, group2)));
        assert(picture1.containsAnAncestor(List.of(group1)));
        assertFalse(group1.containsAnAncestor(List.of(picture1, picture2)));
        assertFalse(group3.containsAnAncestor(List.of(picture1, picture2)));
        assert(group4.containsAnAncestor(List.of(group3)));
    }
}
