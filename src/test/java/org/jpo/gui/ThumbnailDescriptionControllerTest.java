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
 Copyright (C) 2023-2024 Richard Eigenmann.
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
                final var pictureInfo = new PictureInfo();
                final var pictureInfoDescription = "A PictureInfo description";
                pictureInfo.setDescription(pictureInfoDescription);
                final var node = new SortableDefaultMutableTreeNode(pictureInfo);
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(node);

                final var controller = new ThumbnailDescriptionController();
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
                final var groupDescription = "A GroupInfo description";
                final var groupInfo = new GroupInfo(groupDescription);
                final var node = new SortableDefaultMutableTreeNode(groupInfo);
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(node);

                final var controller = new ThumbnailDescriptionController();
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
                final var node = new SortableDefaultMutableTreeNode();
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(node);

                final var panel = new ThumbnailDescriptionController();
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
                final var panel = new ThumbnailDescriptionController();
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
                final var panel = new ThumbnailDescriptionController();
                // First attach a pictureInfo
                final var pictureInfo = new PictureInfo();
                final String pictureInfoDescription = "A PictureInfo description";
                pictureInfo.setDescription(pictureInfoDescription);
                final var piNode = new SortableDefaultMutableTreeNode(pictureInfo);
                panel.setNode(piNode);
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(piNode);

                assertEquals(pictureInfoDescription, panel.getDescription());

                // then replace the node with a GroupInfo node
                final var groupInfoDescription = "A GroupInfo description";
                final var groupInfo = new GroupInfo(groupInfoDescription);
                final var giNode = new SortableDefaultMutableTreeNode(groupInfo);
                panel.setNode(giNode);
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
                final var pictureInfo = new PictureInfo();
                final var pictureInfoDescription = "A PictureInfo description";
                pictureInfo.setDescription(pictureInfoDescription);
                final var node = new SortableDefaultMutableTreeNode(pictureInfo);
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(node);

                final var panel = new ThumbnailDescriptionController();
                panel.setNode(node);
                assertEquals(pictureInfoDescription, panel.getDescription());

                final var newDescription = "A changed description";
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
                final var pictureCollection = new PictureCollection();

                final var childNode1 = new SortableDefaultMutableTreeNode(new GroupInfo("First Child is a GroupInfo node"));
                pictureCollection.getRootNode().add(childNode1);
                final var CHILD_GROUP_INFO_TEXT = "Second Child is a GroupInfo node";
                final var childNode2= new SortableDefaultMutableTreeNode(new GroupInfo(CHILD_GROUP_INFO_TEXT));
                pictureCollection.getRootNode().add(childNode2);
                final var pictureInfo = new PictureInfo();
                final var pictureInfoDescription ="A PictureInfo description";
                pictureInfo.setDescription(pictureInfoDescription);
                final var pictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
                childNode2.add(pictureNode);

                final var controller = new ThumbnailDescriptionController();
                controller.setNode(childNode2);
                assertEquals(CHILD_GROUP_INFO_TEXT, controller.getDescription());

                final String CHANGED_GROUP_NAME = "A new group name";
                ((GroupInfo) childNode2.getUserObject()).setGroupName(CHANGED_GROUP_NAME);
                assertEquals(CHANGED_GROUP_NAME, controller.getDescription());

                pictureCollection.getRootNode().remove(0);
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
                final var pictureInfo = new PictureInfo();
                final var ORIGINAL_FILENAME = "gaga.jpg";
                pictureInfo.setImageLocation(new File(ORIGINAL_FILENAME));
                final var node = new SortableDefaultMutableTreeNode(pictureInfo);
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(node);

                final var controller = new ThumbnailDescriptionController();
                controller.setNode(node);
                assertEquals(ORIGINAL_FILENAME,pictureInfo.getImageFile().getName());
                assertEquals(ORIGINAL_FILENAME, Paths.get(controller.getFileLocation()).getFileName().toString());

                final var NEW_FILENAME = "newfile.jpg";
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
                final var area = new JTextArea();
                final var STRING_WITHOUT_SPECIAL_CHARS = "No special chars in this string";
                Optional<JPopupMenu> optional = ThumbnailDescriptionController.correctTextPopupMenu(STRING_WITHOUT_SPECIAL_CHARS, area);
                assertFalse(optional.isPresent());
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
                final var area = new JTextArea();
                final var STRING_WITH_UNDERSCORE = "Text_with_underscores";
                final var EXPECTED_RESULT = "Text with underscores";
                final Optional<JPopupMenu> optional = ThumbnailDescriptionController.correctTextPopupMenu(STRING_WITH_UNDERSCORE, area);
                assertTrue(optional.isPresent());

                final var menu = optional.get();
                assertEquals(1,menu.getComponentCount());

                final var item = (JMenuItem) menu.getComponent(0);
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
                final var area = new JTextArea();
                final var STRING_WITH_UNICODE_SPACE = "Text%20with%20unicode%20spaces";
                final var EXPECTED_RESULT = "Text with unicode spaces";
                final Optional<JPopupMenu> optional = ThumbnailDescriptionController.correctTextPopupMenu(STRING_WITH_UNICODE_SPACE, area);
                assertTrue(optional.isPresent());

                final var menu = optional.get();
                assertEquals(1,menu.getComponentCount());

                final var item = (JMenuItem) menu.getComponent(0);
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
                final var area = new JTextArea();
                final var STRING_WITH_UNICODE_SPACE_AND_UNDERSCORES = "Text%20with%20unicode%20spaces_and_underscores";
                final var EXPECTED_RESULT = "Text with unicode spaces and underscores";
                final Optional<JPopupMenu> optional = ThumbnailDescriptionController.correctTextPopupMenu(STRING_WITH_UNICODE_SPACE_AND_UNDERSCORES, area);
                assertTrue(optional.isPresent());

                final var menu = optional.get();
                assertEquals(3,menu.getComponentCount());

                final var item = (JMenuItem) menu.getComponent(2);
                item.doClick();
                assertEquals(area.getText(), EXPECTED_RESULT);
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }



}