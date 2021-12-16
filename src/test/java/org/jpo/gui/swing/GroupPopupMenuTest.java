package org.jpo.gui.swing;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.jpo.datamodel.GroupInfo;
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


    private final GroupInfo myGroupInfo = new GroupInfo("My Group");
    private final SortableDefaultMutableTreeNode myNode = new SortableDefaultMutableTreeNode(myGroupInfo);
    private GroupPopupMenu myGroupPopupMenu;
    private JMenuItem showGroup;
    private JMenuItem showPictures;
    private JMenuItem categories;
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
                myGroupPopupMenu = new GroupPopupMenu(myNode);
                showGroup = (JMenuItem) myGroupPopupMenu.getComponent(0);
                showPictures = (JMenuItem) myGroupPopupMenu.getComponent(1);
                categories = (JMenuItem) myGroupPopupMenu.getComponent(3);
                refreshIcon = (JMenuItem) myGroupPopupMenu.getComponent(4);
                editAsTable = (JMenuItem) myGroupPopupMenu.getComponent(6);
                add = (JMenuItem) myGroupPopupMenu.getComponent(8);
                move = (JMenuItem) myGroupPopupMenu.getComponent(9);
                removeNode = (JMenuItem) myGroupPopupMenu.getComponent(10);
                consolidate = (JMenuItem) myGroupPopupMenu.getComponent(12);
                sortBy = (JMenuItem) myGroupPopupMenu.getComponent(14);
                selectAllForEmailing = (JMenuItem) myGroupPopupMenu.getComponent(16);
                generateWebsite = (JMenuItem) myGroupPopupMenu.getComponent(17);
                exportToCollection = (JMenuItem) myGroupPopupMenu.getComponent(18);
                exportToFlatFile = (JMenuItem) myGroupPopupMenu.getComponent(19);
                exportToPicasa = (JMenuItem) myGroupPopupMenu.getComponent(20);
                properties = (JMenuItem) myGroupPopupMenu.getComponent(22);
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
                    final SortableDefaultMutableTreeNode verifyNode = (SortableDefaultMutableTreeNode) popupNodeField.get(myGroupPopupMenu);
                    final GroupInfo verifyGroupInfo = (GroupInfo) verifyNode.getUserObject();
                    assertEquals(myGroupInfo, verifyGroupInfo);
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

    /**
     * Get the children
     */
    @Test
    void testGetChildren() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                assertEquals("Show Group", showGroup.getText());
                assertEquals("Show Pictures", showPictures.getText());
                assertEquals("Categories", categories.getText());
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
