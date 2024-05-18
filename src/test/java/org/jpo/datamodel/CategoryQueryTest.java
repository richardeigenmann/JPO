package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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


class CategoryQueryTest {
    @Test
    void testGetTitle() {
        final var pictureCollection = new PictureCollection();
        final var key = pictureCollection.addCategory("Mountains");
        final var categoryQuery = new CategoryQuery(pictureCollection, key);
        assertEquals("Category: Mountains", categoryQuery.getTitle());
    }

    @Test
    void testQuery() {
        final var pictureCollection = new PictureCollection();
        final var key = pictureCollection.addCategory("Mountains");
        final var categoryQuery = new CategoryQuery(pictureCollection, key);
        assertEquals(0, categoryQuery.getNumberOfResults());

        final var pictureInfo = new PictureInfo();
        final var pictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
        pictureCollection.getRootNode().add(pictureNode);
        categoryQuery.refresh();
        assertEquals(0, categoryQuery.getNumberOfResults());

        pictureInfo.addCategoryAssignment(key);
        categoryQuery.refresh();
        assertEquals(1, categoryQuery.getNumberOfResults());
    }

    @Test
    void testGetIndex() {
        final var pictureCollection = new PictureCollection();
        final var pictureInfo = new PictureInfo();
        final var key = pictureCollection.addCategory("Mountains");
        pictureInfo.addCategoryAssignment(key);
        final var pictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
        pictureCollection.getRootNode().add(pictureNode);

        final var categoryQuery = new CategoryQuery(pictureCollection, key);

        assertEquals(1, categoryQuery.getNumberOfResults());
        assertEquals(pictureNode, categoryQuery.getIndex(0));
        assertNull(categoryQuery.getIndex(-1));
        assertNull(categoryQuery.getIndex(1));
    }

}