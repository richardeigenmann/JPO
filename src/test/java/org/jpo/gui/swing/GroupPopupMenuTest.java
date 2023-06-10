package org.jpo.gui.swing;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;


/**
 * Tests for the GroupPopupMenu Class
 *
 * @author Richard Eigenmann
 */
class GroupPopupMenuTest {


    @BeforeAll
    static void setUpOnce() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        FailOnThreadViolationRepaintManager.install();
    }



    private GroupPopupMenu myGroupPopupMenu;
    private JMenuItem title;
    private JMenuItem showGroup;
    private JMenuItem showPictures;
    private JMenuItem refreshIcon;
    private JMenuItem editAsTable;
    private JMenuItem add;
    private JMenuItem move;
    private JMenuItem removeNode;
    private JMenuItem consolidate;
    private JMenuItem sortBy;
    private JMenuItem selectAllForEmailing;
    private JMenuItem generateWebsite;
    private JMenuItem exportToCollection;
    private JMenuItem exportToFlatFile;
    private JMenuItem exportToPicasa;
    private JMenuItem properties;

    @BeforeEach
    void setUp() {
        assumeFalse(GraphicsEnvironment.isHeadless());

        try {
            SwingUtilities.invokeAndWait(() -> {
                final var groupInfo = new GroupInfo("My Group");
                final var node = new SortableDefaultMutableTreeNode(groupInfo);
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(node);
                myGroupPopupMenu = new GroupPopupMenu(node);
                title = (JMenuItem) myGroupPopupMenu.getComponent(0);
                showGroup = (JMenuItem) myGroupPopupMenu.getComponent(2);
                showPictures = (JMenuItem) myGroupPopupMenu.getComponent(3);
                refreshIcon = (JMenuItem) myGroupPopupMenu.getComponent(5);
                editAsTable = (JMenuItem) myGroupPopupMenu.getComponent(7);
                add = (JMenuItem) myGroupPopupMenu.getComponent(9);
                move = (JMenuItem) myGroupPopupMenu.getComponent(10);
                removeNode = (JMenuItem) myGroupPopupMenu.getComponent(11);
                consolidate = (JMenuItem) myGroupPopupMenu.getComponent(13);
                sortBy = (JMenuItem) myGroupPopupMenu.getComponent(15);
                selectAllForEmailing = (JMenuItem) myGroupPopupMenu.getComponent(17);
                generateWebsite = (JMenuItem) myGroupPopupMenu.getComponent(18);
                exportToCollection = (JMenuItem) myGroupPopupMenu.getComponent(19);
                exportToFlatFile = (JMenuItem) myGroupPopupMenu.getComponent(20);
                exportToPicasa = (JMenuItem) myGroupPopupMenu.getComponent(21);
                properties = (JMenuItem) myGroupPopupMenu.getComponent(23);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        }

    }

    /**
     * Test that out Group Node was created for the correct node.
     */
    @Test
    void testRememberingPopupNode() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    final Field popupNodeField;
                    popupNodeField = GroupPopupMenu.class.getDeclaredField("popupNode");
                    popupNodeField.setAccessible(true);
                    final var verifyNode = (SortableDefaultMutableTreeNode) popupNodeField.get(myGroupPopupMenu);
                    final var verifyGroupInfo = (GroupInfo) verifyNode.getUserObject();
                    assertEquals(myGroupPopupMenu.getPopupNode().getUserObject(), verifyGroupInfo);
                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(GroupPopupMenuTest.class.getName()).log(Level.SEVERE, null, ex);
                    fail("Should not have hit the catch statement");
                }
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(GroupPopupMenuTest.class.getName()).log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
            fail("Should not have hit the catch statement");
        }
    }


    @Test
    void testGetChildrenShowGroup() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> assertEquals("Show Group", showGroup.getText()));
        } catch (final InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(GroupPopupMenuTest.class.getName()).log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
            fail("Should not have hit the catch statement");
        }
    }

    @Test
    void testGetChildrenShowPictures() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> assertEquals("Show Pictures", showPictures.getText()));
        } catch (final InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(GroupPopupMenuTest.class.getName()).log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
            fail("Should not have hit the catch statement");
        }
    }

    @Test
    void testGetChildren() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                assertEquals("My Group", title.getText());
                assertEquals("Refresh Icon", refreshIcon.getText());
                assertEquals("Edit as Table", editAsTable.getText());
                assertEquals("Add", add.getText());
                assertEquals("Move", move.getText());
                assertEquals("Remove Node", removeNode.getText());
                assertEquals("Consolidate/Move", consolidate.getText());
                assertEquals("Sort by", sortBy.getText());
                assertEquals("Select all for Emailing", selectAllForEmailing.getText());
                assertEquals("Generate Website", generateWebsite.getText());
                assertEquals("Export to Collection", exportToCollection.getText());
                assertEquals("Export to Flat File", exportToFlatFile.getText());
                assertEquals("Export to Picasa", exportToPicasa.getText());
                assertEquals("Properties", properties.getText());
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(GroupPopupMenuTest.class.getName()).log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
            fail("Should not have hit the catch statement");
        }
    }
}
