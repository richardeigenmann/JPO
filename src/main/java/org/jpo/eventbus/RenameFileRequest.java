package org.jpo.eventbus;

import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/*
 Copyright (C) 2018-2025 Richard Eigenmann.
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
 * The receiver of this request is supposed to bring up the file rename dialog for the selected node
 *
 * @param node        The node to rename
 * @param newFileName the new file name
 * @author Richard Eigenmann
 */
public record RenameFileRequest(@NonNull SortableDefaultMutableTreeNode node, @NotNull String newFileName) {

    /**
     * Constructor validates that the node holds a picture and that a newFilename was supplied
     *
     * @param node        The node to rename
     * @param newFileName the new filename
     */
    public RenameFileRequest {
        Objects.requireNonNull(node);
        Objects.requireNonNull(newFileName);
        if (!(node.getUserObject() instanceof PictureInfo)) {
            throw new NotPictureInfoException("The node must be of type PictureInfo");
        }
    }

}

