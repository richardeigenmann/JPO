package org.jpo.gui;


import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.DeleteNodeFileHandler;
import org.jpo.eventbus.JpoEventBus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


/*
 Copyright (C) 2017-2024 Richard Eigenmann.
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
 * @author Richard Eigenmann
 */
class ApplicationEventHandlerTest {

    private static final String TEST_IMAGE = "exif-test-canon-eos-350d.jpg";

    /**
     * Test Constructor
     */
    @Test
    void testConstructor() {
        final var applicationStartupHandler = new ApplicationStartupHandler();
        assertNotNull(applicationStartupHandler);
        assertNotNull(JpoEventBus.getInstance());
    }


    @Test
    void testDeleteNodeAndFile(@TempDir Path tempDir) {
        try {
            // Create a collection
            final var rootNode = new SortableDefaultMutableTreeNode();
            rootNode.setUserObject(new GroupInfo("Root Node"));

            var pictureCollection = new PictureCollection();
            pictureCollection.getRootNode().add(rootNode);

            final var picture1File = new File(tempDir.toFile(), "picture1.jpg");
            try (final var bin1 = new BufferedInputStream(Objects.requireNonNull(ApplicationEventHandlerTest.class.getClassLoader().getResource(TEST_IMAGE)).openStream());
                 final var bout1 = new BufferedOutputStream(new FileOutputStream(picture1File))) {
                bin1.transferTo(bout1);
            }
            final var picture2File = new File(tempDir.toFile(), "picture2.jpg");
            try (final var bin2 = new BufferedInputStream(Objects.requireNonNull(ApplicationEventHandlerTest.class.getClassLoader().getResource(TEST_IMAGE)).openStream());
                 final var bout2 = new BufferedOutputStream(new FileOutputStream(picture2File))) {
                bin2.transferTo(bout2);
            }
            final var picture3File = new File(tempDir.toFile(), "picture3.jpg");
            try (final var bin3 = new BufferedInputStream(Objects.requireNonNull(ApplicationEventHandlerTest.class.getClassLoader().getResource(TEST_IMAGE)).openStream());
                 final var bout3 = new BufferedOutputStream(new FileOutputStream(picture3File))) {
                bin3.transferTo(bout3);
            }

            final var pi1 = new SortableDefaultMutableTreeNode();
            final var pictureInfo1 = new PictureInfo(picture1File, "Image 1");
            pi1.setUserObject(pictureInfo1);
            rootNode.add(pi1);
            final var pi2 = new SortableDefaultMutableTreeNode();
            final var pictureInfo2 = new PictureInfo(picture2File, "Image 2");
            pi2.setUserObject(pictureInfo2);
            rootNode.add(pi2);
            final var pi3 = new SortableDefaultMutableTreeNode();
            final var pictureInfo3 = new PictureInfo(picture3File, "Image 3");
            pi3.setUserObject(pictureInfo3);
            rootNode.add(pi3);

            // Delete node 2
            //    First assert it exists and has a file
            assertEquals(pi2, rootNode.getChildAt(1));
            assertThat (picture2File).exists();

            DeleteNodeFileHandler.deleteNodeAndFileTest(pi2);

            //    Now the node and the file must be gone
            assertEquals(pi1, rootNode.getChildAt(0));
            assertEquals(pi3, rootNode.getChildAt(1));
            assertThat(picture2File).doesNotExist();
        } catch (final IOException e) {
            fail(e.getMessage());
        }


    }
}
