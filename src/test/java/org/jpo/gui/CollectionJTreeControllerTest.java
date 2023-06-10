package org.jpo.gui;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;


/*
 Copyright (C) 2017-2023 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 without even the implied warranty of MERCHANTABILITY or FITNESS 
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * @author Richard Eigenmann
 */
public class CollectionJTreeControllerTest {

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    /**
     * Test Constructor
     */
    @Test
    void testConstructor() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final var pictureCollection = new PictureCollection();
                assertNotNull(pictureCollection.getRootNode());
                final var collectionJTreeController = new CollectionJTreeController(pictureCollection);
                assertNotNull( collectionJTreeController );
            } );
        } catch ( final InterruptedException | InvocationTargetException ex ) {
            fail( ex.getCause().getMessage() );
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testSelection() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final var pictureCollection = new PictureCollection();
                final var groupNode = new SortableDefaultMutableTreeNode(new GroupInfo("Group Node"));
                pictureCollection.getRootNode().add(groupNode);
                final var collectionJTreeController = new CollectionJTreeController(pictureCollection);

                final var collectionJTree = collectionJTreeController.getCollectionJTree();

                final var selectedNode = CollectionJTreeController.getSelectedNode(collectionJTree);
                assert(selectedNode.isEmpty());

                final var selectionPath = new TreePath(new Object[]{groupNode});
                collectionJTree.setSelectionPath(selectionPath);
                final var selectedNodeAfterSetting = CollectionJTreeController.getSelectedNode(collectionJTree);
                assert(selectedNodeAfterSetting.isPresent());
                assertEquals(groupNode, selectedNodeAfterSetting.get());
            } );
        } catch ( final InterruptedException | InvocationTargetException ex ) {
            fail( ex.getCause().getMessage() );
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testSelectionOfDifferentJTree() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final var anyJTree = new JTree();

                final var selectedNode = CollectionJTreeController.getSelectedNode(anyJTree);
                assert(selectedNode.isEmpty());
            } );
        } catch ( final InterruptedException | InvocationTargetException ex ) {
            fail( ex.getCause().getMessage() );
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testImageInitialisation() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var collectionJTreeController = new CollectionJTreeController(new PictureCollection());
                assertNotNull(collectionJTreeController);

                assertNotNull(collectionJTreeController.getPictureIcon());
                assertNotNull(collectionJTreeController.getClosedFolderIcon());
                assertNotNull(collectionJTreeController.getOpenFolderIcon());

            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getCause().getMessage());
            Thread.currentThread().interrupt();
        }
    }

}
