package org.jpo.eventbus;

import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import java.io.File;
import java.util.Collection;

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
 * This request holds the details about pictures to be added
 * @param receivingNode          The node on which to add the pictures. It must be a node with a GroupInfo object
 * @param chosenFiles        The array of Files to add
 * @param newOnly            indicates whether to check if the picture is already in the collection
 * @param recurseDirectories indicates whether to scan down into directories for more pictures.
 * @param retainDirectories  indicates whether to preserve the directory structure.
 * @param selectedCategories The categories to give the pictures
 */
public record PictureAdderRequest(
        @NotNull SortableDefaultMutableTreeNode receivingNode,
        @NotNull File[] chosenFiles,
        boolean newOnly,
        boolean recurseDirectories,
        boolean retainDirectories,
        @NotNull Collection<Integer> selectedCategories) {

    public PictureAdderRequest {
        if (!(receivingNode.getUserObject() instanceof GroupInfo))
            throw new IllegalArgumentException("The receivingNode must have a GroupInfo userObject");
    }
}
