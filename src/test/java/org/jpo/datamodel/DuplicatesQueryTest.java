package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/*
 Copyright (C) 2024 Richard Eigenmann.
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


class DuplicatesQueryTest {

    @Test
    void testGetTitle() {
        final var pictureCollection = new PictureCollection();
        final var duplicatesQuery = new DuplicatesQuery(pictureCollection);
        assertEquals("Duplicates", duplicatesQuery.getTitle());
    }

    @Test
    void testQuery() {
        final var pictureCollection = new PictureCollection();
        final var pictureInfo = new PictureInfo();
        final var NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        File imageFile = null;
        try {
            imageFile = new File(Objects.requireNonNull(ExifInfoTest.class.getClassLoader().getResource(NIKON_D100_IMAGE)).toURI());
        } catch (URISyntaxException e) {
            fail ("Could not create file for image: " + NIKON_D100_IMAGE + " : " + e.getMessage());
        }
        pictureInfo.setImageLocation(imageFile);
        final var pictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
        pictureCollection.getRootNode().add(pictureNode);
        final var duplicatesQuery = new DuplicatesQuery(pictureCollection);
        assertEquals(0, duplicatesQuery.getNumberOfResults());

        final var anotherPictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
        pictureCollection.getRootNode().add(anotherPictureNode);
        duplicatesQuery.refresh();
        assertEquals(2, duplicatesQuery.getNumberOfResults());
    }

    @Test
    void testGetIndex() {
        final var pictureCollection = new PictureCollection();
        final var pictureInfo = new PictureInfo();
        final var NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        File imageFile = null;
        try {
            imageFile = new File(Objects.requireNonNull(ExifInfoTest.class.getClassLoader().getResource(NIKON_D100_IMAGE)).toURI());
        } catch (URISyntaxException e) {
            fail ("Could not create file for image: " + NIKON_D100_IMAGE + " : " + e.getMessage());
        }
        pictureInfo.setImageLocation(imageFile);
        final var pictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
        pictureCollection.getRootNode().add(pictureNode);
        final var anotherPictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
        pictureCollection.getRootNode().add(anotherPictureNode);

        final var duplicatesQuery = new DuplicatesQuery(pictureCollection);
        assertEquals(2, duplicatesQuery.getNumberOfResults());

        assertEquals(pictureNode, duplicatesQuery.getIndex(0));
        assertEquals(anotherPictureNode, duplicatesQuery.getIndex(1));
        assertNull(duplicatesQuery.getIndex(-1));
        assertNull(duplicatesQuery.getIndex(2));
    }

}