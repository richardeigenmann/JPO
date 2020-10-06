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
public class GroupPopupMenuTest {


    @BeforeAll
    public static void setUpOnce() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        FailOnThreadViolationRepaintManager.install();
    }


    private final GroupInfo myGroupInfo = new GroupInfo("My Group");
    private final SortableDefaultMutableTreeNode myNode = new SortableDefaultMutableTreeNode(myGroupInfo);
    private GroupPopupMenu myGroupPopupMenu;
    private JMenuItem showGroup;
    private JMenuItem showPictures;
    private JMenuItem find;
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
    public void setUp() {
        assumeFalse(GraphicsEnvironment.isHeadless());

        try {
            SwingUtilities.invokeAndWait(() -> {
                myGroupPopupMenu = new GroupPopupMenu(myNode);
                showGroup = (JMenuItem) myGroupPopupMenu.getComponent(0);
                showPictures = (JMenuItem) myGroupPopupMenu.getComponent(1);
                find = (JMenuItem) myGroupPopupMenu.getComponent(2);
                categories = (JMenuItem) myGroupPopupMenu.getComponent(4);
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
    public void testRememberingPopupNode() {
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
                }
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(GroupPopupMenuTest.class.getName()).log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
        }

    }

    /**
     * Get the children
     */
    @Test
    public void testGetChildren() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                assertEquals("Show Group", showGroup.getText());
                assertEquals("Show Pictures", showPictures.getText());
                assertEquals("Find", find.getText());
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
        }
    }
}
