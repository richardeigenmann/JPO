package org.jpo.gui.swing;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2023 Richard Eigenmann.
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

class CategoryPopupMenuTest {

    @BeforeAll
    static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    /**
     * Tests constructing a CategoryEditorJFrame
     */
    @Test
    void testConstructor() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var node = new SortableDefaultMutableTreeNode();
                final var picture1 = new File("picture1.jpg");
                final var pictureInfo1 = new PictureInfo(picture1, "Image 1");
                node.setUserObject(pictureInfo1);
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(node);

                final var nodes = new ArrayList<>(List.of(node));

                final var jPopupMenu = new CategoryPopupMenu(nodes);
                assertNotNull(jPopupMenu);
                assertEquals("Add a Category to the Picture", jPopupMenu.getLabel());
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}