package org.jpo.gui.swing;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.jpo.cache.ThumbnailCreationQueue;
import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

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

/**
 * Tests for the PictureInfoEditor
 *
 * @author Richard Eigenmann
 */
public class PictureInfoEditorTest {

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Test
    void testConstructor() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var pictureInfo = new PictureInfo();
                final var JPG_IMAGE_FILE = "exif-test-nikon-d100-1.jpg";
                final var imageUrl = Objects.requireNonNull(PictureInfoEditorTest.class.getClassLoader().getResource(JPG_IMAGE_FILE));
                pictureInfo.setImageLocation(new File(imageUrl.getFile()));

                final var node = new SortableDefaultMutableTreeNode(pictureInfo);
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(node);

                final var pictureInfoEditor = new PictureInfoEditor(node);
                assertNotNull( pictureInfoEditor );
                ThumbnailCreationQueue.clear(); // the created request confuses other tests
                pictureInfoEditor.getRid();
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail( "This test didn't work. Exception: " + ex.getMessage() );
            Thread.currentThread().interrupt();
        }
    }


    @Test
    void testFileNameCorruption() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var pictureInfo = new PictureInfo();
                final var JPG_IMAGE_FILE = "exif-test-nikon-d100-1.jpg";
                final var imageUrl = Objects.requireNonNull(PictureInfoEditorTest.class.getClassLoader().getResource(JPG_IMAGE_FILE));

                final var imageFile = new File(imageUrl.getFile());
                pictureInfo.setImageLocation(imageFile);
                assertEquals(pictureInfo.getImageFile(), imageFile);

                final var node = new SortableDefaultMutableTreeNode( pictureInfo );
                final var pictureCollection = new PictureCollection();
                pictureCollection.getRootNode().add(node);

                final var pictureInfoEditor = new PictureInfoEditor(node);
                assertNotNull( pictureInfoEditor );

                pictureInfoEditor.callSaveFieldData();
                // Expectation is that after saving without changing file stays the same
                assertEquals( imageFile, pictureInfo.getImageFile());
                ThumbnailCreationQueue.clear();  // the created request confuses other tests
                pictureInfoEditor.getRid();
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail( ex.getMessage() );
            Thread.currentThread().interrupt();
        }
    }


}
