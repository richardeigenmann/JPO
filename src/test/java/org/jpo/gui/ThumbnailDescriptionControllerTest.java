package org.jpo.gui;

import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;

public class ThumbnailDescriptionControllerTest {

    @Test
    public void testConstructor() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final ThumbnailDescriptionController panel = new ThumbnailDescriptionController();
                assertNotNull(panel);
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testSetNodeEmpty() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final ThumbnailDescriptionController panel = new ThumbnailDescriptionController();
                SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
                panel.setNode(node);
                assertEquals("Error", panel.getDescription());
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testSetNodePictureInfo() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final ThumbnailDescriptionController panel = new ThumbnailDescriptionController();
                PictureInfo pictureInfo = new PictureInfo();
                final String pictureInfoDescription = "A PictureInfo description";
                pictureInfo.setDescription(pictureInfoDescription);
                SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode(pictureInfo);
                panel.setNode(node);
                assertEquals(pictureInfoDescription, panel.getDescription());
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testSetNodeGroupInfo() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final ThumbnailDescriptionController panel = new ThumbnailDescriptionController();
                final String groupDescription = "A GroupInfo description";
                GroupInfo groupInfo = new GroupInfo(groupDescription);
                SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode(groupInfo);
                panel.setNode(node);
                assertEquals(groupDescription, panel.getDescription());
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testSetNodeNull() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final ThumbnailDescriptionController panel = new ThumbnailDescriptionController();
                SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
                panel.setNode(node);
                panel.setNode(null);
                assertEquals("No node for this position.", panel.getDescription());
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testSetNodeNull2() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final ThumbnailDescriptionController panel = new ThumbnailDescriptionController();
                panel.setNode(null);
                assertEquals("", panel.getDescription());
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }


    @Test
    public void testSetNodeChangeListener() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
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
                GroupInfo groupInfo = new GroupInfo(groupInfoDescription);
                SortableDefaultMutableTreeNode ginode = new SortableDefaultMutableTreeNode(groupInfo);
                panel.setNode(ginode);
                assertEquals(groupInfoDescription, panel.getDescription());

            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testNodeListenerWorks() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final ThumbnailDescriptionController panel = new ThumbnailDescriptionController();
                PictureInfo pictureInfo = new PictureInfo();
                final String pictureInfoDescription ="A PictureInfo description";
                pictureInfo.setDescription(pictureInfoDescription);
                SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode(pictureInfo);
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
    public void testCorrectTextPopupMenuNoSpecialChars() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final JTextArea area = new JTextArea();
                final String STRING_WITHOUT_SPECIAL_CHARS = "No special chars in this string";
                Optional<JPopupMenu> optional = ThumbnailDescriptionController.correctTextPopupMenu(STRING_WITHOUT_SPECIAL_CHARS, area);
                assertTrue(!optional.isPresent());
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testCorrectTextPopupMenuUnderscore() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final JTextArea area = new JTextArea();
                final String STRING_WITH_UNDERSCORE = "Text_with_underscores";
                final String EXPECTED_RESULT = "Text with underscores";
                Optional<JPopupMenu> optional = ThumbnailDescriptionController.correctTextPopupMenu(STRING_WITH_UNDERSCORE, area);
                assertTrue(optional.isPresent());

                JPopupMenu menu = optional.get();
                assertEquals(1,menu.getComponentCount());

                JMenuItem item = (JMenuItem) menu.getComponent(0);
                item.doClick();
                assertEquals(area.getText(), EXPECTED_RESULT);
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testCorrectTextPopupMenuUnicodeSpace() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final JTextArea area = new JTextArea();
                final String STRING_WITH_UNICODE_SPACE = "Text%20with%20unicode%20spaces";
                final String EXPECTED_RESULT = "Text with unicode spaces";
                Optional<JPopupMenu> optional = ThumbnailDescriptionController.correctTextPopupMenu(STRING_WITH_UNICODE_SPACE, area);
                assertTrue(optional.isPresent());

                JPopupMenu menu = optional.get();
                assertEquals(1,menu.getComponentCount());

                JMenuItem item = (JMenuItem) menu.getComponent(0);
                item.doClick();
                assertEquals(area.getText(), EXPECTED_RESULT);
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testCorrectTextPopupMenuUnicodeSpaceAndUnderscores() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final JTextArea area = new JTextArea();
                final String STRING_WITH_UNICODE_SPACE_AND_UNDERSCORES = "Text%20with%20unicode%20spaces_and_underscores";
                final String EXPECTED_RESULT = "Text with unicode spaces and underscores";
                Optional<JPopupMenu> optional = ThumbnailDescriptionController.correctTextPopupMenu(STRING_WITH_UNICODE_SPACE_AND_UNDERSCORES, area);
                assertTrue(optional.isPresent());

                JPopupMenu menu = optional.get();
                assertEquals(3,menu.getComponentCount());

                JMenuItem item = (JMenuItem) menu.getComponent(2);
                item.doClick();
                assertEquals(area.getText(), EXPECTED_RESULT);
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }



}