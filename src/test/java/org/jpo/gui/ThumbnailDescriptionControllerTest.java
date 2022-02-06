package org.jpo.gui;

import org.jpo.datamodel.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2022  Richard Eigenmann.
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

public class ThumbnailDescriptionControllerTest {

    @BeforeAll
    public static void beforeAll() {
        Settings.loadSettings(); // We need to start the cache
    }

    @Test
    void testConstructor() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final ThumbnailDescriptionController panel = new ThumbnailDescriptionController();
                assertNotNull(panel);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }


    @Test
    void testSetNodeEmpty() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final ThumbnailDescriptionController panel = new ThumbnailDescriptionController();
                final SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
                panel.setNode(node);
                assertEquals("Error", panel.getDescription());
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testSetNodePictureInfo() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final ThumbnailDescriptionController controller = new ThumbnailDescriptionController();
                final PictureInfo pictureInfo = new PictureInfo();
                final String pictureInfoDescription = "A PictureInfo description";
                pictureInfo.setDescription(pictureInfoDescription);
                final SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode(pictureInfo);
                controller.setNode(node);
                assertEquals(pictureInfoDescription, controller.getDescription());
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testSetNodeGroupInfo() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final ThumbnailDescriptionController controller = new ThumbnailDescriptionController();
                final String groupDescription = "A GroupInfo description";
                final GroupInfo groupInfo = new GroupInfo(groupDescription);
                final SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode(groupInfo);
                controller.setNode(node);
                assertEquals(groupDescription, controller.getDescription());
            });
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testSetNodeNull() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final ThumbnailDescriptionController panel = new ThumbnailDescriptionController();
                final SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
                panel.setNode(node);
                panel.setNode(null);
                assertEquals("No node for this position.", panel.getDescription());
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testSetNodeNull2() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final ThumbnailDescriptionController panel = new ThumbnailDescriptionController();
                panel.setNode(null);
                assertEquals("", panel.getDescription());
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }


    @Test
    void testSetNodeChangeListener() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final ThumbnailDescriptionController panel = new ThumbnailDescriptionController();
                // First attach a pictureInfo
                PictureInfo pictureInfo = new PictureInfo();
                final String pictureInfoDescription = "A PictureInfo description";
                pictureInfo.setDescription(pictureInfoDescription);
                SortableDefaultMutableTreeNode pinode = new SortableDefaultMutableTreeNode(pictureInfo);
                panel.setNode(pinode);
                assertEquals(pictureInfoDescription, panel.getDescription());

                // then replace the node with a GroupInfo node
                final String groupInfoDescription = "A GroupInfo description";
                final GroupInfo groupInfo = new GroupInfo(groupInfoDescription);
                final SortableDefaultMutableTreeNode ginode = new SortableDefaultMutableTreeNode(groupInfo);
                panel.setNode(ginode);
                assertEquals(groupInfoDescription, panel.getDescription());

            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testNodeListenerWorks() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final ThumbnailDescriptionController panel = new ThumbnailDescriptionController();
                final PictureInfo pictureInfo = new PictureInfo();
                final String pictureInfoDescription = "A PictureInfo description";
                pictureInfo.setDescription(pictureInfoDescription);
                final SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode(pictureInfo);
                panel.setNode(node);
                assertEquals(pictureInfoDescription, panel.getDescription());

                final String newDescription = "A changed description";
                pictureInfo.setDescription(newDescription);
                assertEquals(newDescription, panel.getDescription());
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testNodeListenerWorksElaborate() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                // set up a Picture collection and bind it to the Settings
                final PictureCollection pc = new PictureCollection();
                Settings.setPictureCollection(pc);
                // The controller binds to the TreeModelListener of the Settings.getPictureCopllection root node.
                final ThumbnailDescriptionController controller = new ThumbnailDescriptionController();

                final SortableDefaultMutableTreeNode childNode1 = new SortableDefaultMutableTreeNode(new GroupInfo("First Child is a GroupInfo node"));
                pc.getRootNode().add(childNode1);
                final String CHILD_GROUP_INFO_TEXT = "Second Child is a GroupInfo node";
                final SortableDefaultMutableTreeNode childNode2= new SortableDefaultMutableTreeNode(new GroupInfo(CHILD_GROUP_INFO_TEXT));
                pc.getRootNode().add(childNode2);
                final PictureInfo pictureInfo = new PictureInfo();
                final String pictureInfoDescription ="A PictureInfo description";
                pictureInfo.setDescription(pictureInfoDescription);
                final SortableDefaultMutableTreeNode pictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
                childNode2.add(pictureNode);
                controller.setNode(childNode2);
                assertEquals(CHILD_GROUP_INFO_TEXT, controller.getDescription());

                final String CHANGED_GROUP_NAME = "A new group name";
                ((GroupInfo) childNode2.getUserObject()).setGroupName(CHANGED_GROUP_NAME);
                assertEquals(CHANGED_GROUP_NAME, controller.getDescription());

                pc.getRootNode().remove(0);


            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testNodeListenerOnRename() {
        // test to fix the bug that on File Rename the file location is not updated
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final ThumbnailDescriptionController controller = new ThumbnailDescriptionController();
                final PictureInfo pictureInfo = new PictureInfo();
                final String ORIGINAL_FILENAME = "gaga.jpg";
                pictureInfo.setImageLocation(new File(ORIGINAL_FILENAME));
                final SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode(pictureInfo);
                controller.setNode(node);
                assertEquals(ORIGINAL_FILENAME,pictureInfo.getImageFile().getName());
                assertEquals(ORIGINAL_FILENAME, Paths.get(controller.getFileLocation()).getFileName().toString());

                final String NEW_FILENAME = "newfile.jpg";
                pictureInfo.setImageLocation(new File(NEW_FILENAME));
                assertEquals(NEW_FILENAME, pictureInfo.getImageFile().getName());
                assertEquals(NEW_FILENAME, Paths.get(controller.getFileLocation()).getFileName().toString());
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testCorrectTextPopupMenuNoSpecialChars() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final JTextArea area = new JTextArea();
                final String STRING_WITHOUT_SPECIAL_CHARS = "No special chars in this string";
                Optional<JPopupMenu> optional = ThumbnailDescriptionController.correctTextPopupMenu(STRING_WITHOUT_SPECIAL_CHARS, area);
                assertTrue(!optional.isPresent());
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testCorrectTextPopupMenuUnderscore() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final JTextArea area = new JTextArea();
                final String STRING_WITH_UNDERSCORE = "Text_with_underscores";
                final String EXPECTED_RESULT = "Text with underscores";
                final Optional<JPopupMenu> optional = ThumbnailDescriptionController.correctTextPopupMenu(STRING_WITH_UNDERSCORE, area);
                assertTrue(optional.isPresent());

                final JPopupMenu menu = optional.get();
                assertEquals(1,menu.getComponentCount());

                final JMenuItem item = (JMenuItem) menu.getComponent(0);
                item.doClick();
                assertEquals(area.getText(), EXPECTED_RESULT);
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testCorrectTextPopupMenuUnicodeSpace() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final JTextArea area = new JTextArea();
                final String STRING_WITH_UNICODE_SPACE = "Text%20with%20unicode%20spaces";
                final String EXPECTED_RESULT = "Text with unicode spaces";
                final Optional<JPopupMenu> optional = ThumbnailDescriptionController.correctTextPopupMenu(STRING_WITH_UNICODE_SPACE, area);
                assertTrue(optional.isPresent());

                final JPopupMenu menu = optional.get();
                assertEquals(1,menu.getComponentCount());

                final JMenuItem item = (JMenuItem) menu.getComponent(0);
                item.doClick();
                assertEquals(area.getText(), EXPECTED_RESULT);
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testCorrectTextPopupMenuUnicodeSpaceAndUnderscores() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final JTextArea area = new JTextArea();
                final String STRING_WITH_UNICODE_SPACE_AND_UNDERSCORES = "Text%20with%20unicode%20spaces_and_underscores";
                final String EXPECTED_RESULT = "Text with unicode spaces and underscores";
                final Optional<JPopupMenu> optional = ThumbnailDescriptionController.correctTextPopupMenu(STRING_WITH_UNICODE_SPACE_AND_UNDERSCORES, area);
                assertTrue(optional.isPresent());

                final JPopupMenu menu = optional.get();
                assertEquals(3,menu.getComponentCount());

                final JMenuItem item = (JMenuItem) menu.getComponent(2);
                item.doClick();
                assertEquals(area.getText(), EXPECTED_RESULT);
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }



}