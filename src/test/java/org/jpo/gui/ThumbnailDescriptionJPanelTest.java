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
                final ThumbnailDescriptionJPanel panel = new ThumbnailDescriptionJPanel();
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
                final ThumbnailDescriptionJPanel panel = new ThumbnailDescriptionJPanel();
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
                final ThumbnailDescriptionJPanel panel = new ThumbnailDescriptionJPanel();
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


}