package org.jpo.gui;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SingleNodeNavigator;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.ShowPictureRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

/*
 Copyright (C) 2017 - 2025 Richard Eigenmann.
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
 * Tests for the PictureViewer
 *
 * @author Richard Eigenmann
 */
class PictureViewerTest {

    @BeforeAll
    static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Test
    void testConstructor() {
        final var pictureInfo = new PictureInfo();
        try {
            final var imageFile = new File(ClassLoader.getSystemResources("exif-test-nikon-d100-1.jpg").nextElement().toURI());
            pictureInfo.setImageLocation(imageFile);

            pictureInfo.setImageLocation(imageFile);
        } catch (URISyntaxException | IOException e) {
            fail("Could not load image file: " + e.getMessage());
        }
        final var node = new SortableDefaultMutableTreeNode(pictureInfo);
        final var navigator = new SingleNodeNavigator(node);
        final var request = new ShowPictureRequest(navigator,0 );
        final var pictureViewer = GuiActionRunner.execute(() ->new PictureViewer(request));
        assertNotNull(pictureViewer);
        GuiActionRunner.execute(pictureViewer::closeViewerTest);
    }


    @Test
    void testShowPicture() {
        final var pictureInfo = new PictureInfo();
        try {
            final var image = new File(PictureViewerTest.class.getClassLoader().getResource("exif-test-nikon-d100-1.jpg").toURI());
            pictureInfo.setImageLocation(image);
        } catch (URISyntaxException e) {
            fail("Could not load image file: " + e.getMessage());
        }
        final var DESCRIPTION = "A test image";
        pictureInfo.setDescription(DESCRIPTION);
        final var node = new SortableDefaultMutableTreeNode(pictureInfo);
        final var navigator = new SingleNodeNavigator(node);
        final var request = new ShowPictureRequest(navigator, 0);
        final var pictureViewer = GuiActionRunner.execute(() -> new PictureViewer(request));
        assertEquals(node, pictureViewer.getCurrentNodeTest());
        GuiActionRunner.execute(pictureViewer::closeViewerTest);
    }

}
