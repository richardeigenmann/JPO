package org.jpo.gui;


import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.JpoEventBus;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


/*
 Copyright (C) 2017-2021  Richard Eigenmann.
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
public class ApplicationEventHandlerTest {

    private static final String TEST_IMAGE = "exif-test-canon-eos-350d.jpg";

    /**
     * Test Constructor
     */
    @Test
    public void testConstructor() {
        final ApplicationEventHandler aeh = new ApplicationEventHandler();
        assertNotNull(aeh);
        assertNotNull(JpoEventBus.getInstance());
    }


    @Test
    public void testDeleteNodeAndFile() {
        try {
            // Create a collection
            final Path tempDir = Files.createTempDirectory("testDeleteNodeAndFile");

            final SortableDefaultMutableTreeNode rootNode = new SortableDefaultMutableTreeNode();
            rootNode.setUserObject(new GroupInfo("Root Node"));

            final File picture1 = new File(tempDir.toFile(), "picture1.jpg");
            try (final BufferedInputStream bin1 = new BufferedInputStream(Objects.requireNonNull(ApplicationEventHandlerTest.class.getClassLoader().getResource(TEST_IMAGE)).openStream());
                 final BufferedOutputStream bout1 = new BufferedOutputStream(new FileOutputStream(picture1));) {
                bin1.transferTo(bout1);
            }
            final File picture2 = new File(tempDir.toFile(), "picture2.jpg");
            try (final BufferedInputStream bin2 = new BufferedInputStream(Objects.requireNonNull(ApplicationEventHandlerTest.class.getClassLoader().getResource(TEST_IMAGE)).openStream());
                 final BufferedOutputStream bout2 = new BufferedOutputStream(new FileOutputStream(picture2));) {
                bin2.transferTo(bout2);
            }
            final File picture3 = new File(tempDir.toFile(), "picture3.jpg");
            try (final BufferedInputStream bin3 = new BufferedInputStream(Objects.requireNonNull(ApplicationEventHandlerTest.class.getClassLoader().getResource(TEST_IMAGE)).openStream());
                 final BufferedOutputStream bout3 = new BufferedOutputStream(new FileOutputStream(picture3));) {
                bin3.transferTo(bout3);
            }

            final SortableDefaultMutableTreeNode pi1 = new SortableDefaultMutableTreeNode();
            final PictureInfo pictureInfo1 = new PictureInfo(picture1, "Image 1");
            pi1.setUserObject(pictureInfo1);
            rootNode.add(pi1);
            final SortableDefaultMutableTreeNode pi2 = new SortableDefaultMutableTreeNode();
            final PictureInfo pictureInfo2 = new PictureInfo(picture2, "Image 2");
            pi2.setUserObject(pictureInfo2);
            rootNode.add(pi2);
            final SortableDefaultMutableTreeNode pi3 = new SortableDefaultMutableTreeNode();
            final PictureInfo pictureInfo3 = new PictureInfo(picture3, "Image 3");
            pi3.setUserObject(pictureInfo3);
            rootNode.add(pi3);

            // Delete node 2
            //    First assert it exists and has a file
            assertEquals(pi2, rootNode.getChildAt(1));
            assert (picture2.exists());

            ApplicationEventHandler.deleteNodeAndFileTest(pi2);

            //    Now the node and the file must be gone
            assertEquals(pi1, rootNode.getChildAt(0));
            assertEquals(pi3, rootNode.getChildAt(1));
            assertFalse(picture2.exists());


            // cleanup
            Files.delete(picture1.toPath());
            Files.delete(picture3.toPath());
            Files.delete(tempDir);

        } catch (final IOException e) {
            fail(e.getMessage());
        }


    }
}
