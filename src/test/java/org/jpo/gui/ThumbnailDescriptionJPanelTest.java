package org.jpo.gui;

import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;

public class ThumbnailDescriptionJPanelTest {

    @Test
    public void testConstructor() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final ThumbnailDescriptionJPanel panel = new ThumbnailDescriptionJPanel();
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
                final ThumbnailDescriptionJPanel panel = new ThumbnailDescriptionJPanel();
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
                final ThumbnailDescriptionJPanel panel = new ThumbnailDescriptionJPanel();
                PictureInfo pictureInfo = new PictureInfo();
                pictureInfo.setDescription("A PictureInfo description");
                SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode(pictureInfo);
                panel.setNode(node);
                assertEquals("A PictureInfo description", panel.getDescription());
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
                final ThumbnailDescriptionJPanel panel = new ThumbnailDescriptionJPanel();
                GroupInfo groupInfo = new GroupInfo("A GroupInfo description");
                SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode(groupInfo);
                panel.setNode(node);
                assertEquals("A GroupInfo description", panel.getDescription());
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
                final ThumbnailDescriptionJPanel panel = new ThumbnailDescriptionJPanel();
                SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
                panel.setNode(node);
                panel.setNode(null);
                System.out.println(panel.getDescription());
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
                final ThumbnailDescriptionJPanel panel = new ThumbnailDescriptionJPanel();
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
                final ThumbnailDescriptionJPanel panel = new ThumbnailDescriptionJPanel();
                // First attach a pictureInfo
                PictureInfo pictureInfo = new PictureInfo();
                pictureInfo.setDescription("A PictureInfo description");
                SortableDefaultMutableTreeNode pinode = new SortableDefaultMutableTreeNode(pictureInfo);
                panel.setNode(pinode);
                assertEquals("A PictureInfo description", panel.getDescription());

                // then replace the node with a GroupInfo node
                GroupInfo groupInfo = new GroupInfo("A GroupInfo description");
                SortableDefaultMutableTreeNode ginode = new SortableDefaultMutableTreeNode(groupInfo);
                panel.setNode(ginode);
                assertEquals("A GroupInfo description", panel.getDescription());

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
                final ThumbnailDescriptionJPanel panel = new ThumbnailDescriptionJPanel();
                PictureInfo pictureInfo = new PictureInfo();
                pictureInfo.setDescription("A PictureInfo description");
                SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode(pictureInfo);
                panel.setNode(node);
                assertEquals("A PictureInfo description", panel.getDescription());

                pictureInfo.setDescription("A changed description");
                assertEquals("A changed description", panel.getDescription());
            } );
        } catch (final InterruptedException | InvocationTargetException ex  ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }


}