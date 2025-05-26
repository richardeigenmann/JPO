package org.jpo.eventbus;

/*
 Copyright (C) 2017-2025 Richard Eigenmann.
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

import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.Collection;

/**
 * Request to indicate that the user would like to copy the pictures in the
 * selected nodes to a target directory
 *
 * @param nodes           The nodes for which the user would like copy the pictures
 * @param targetDirectory The target directory. It must exist and it must be writable.
 * @author Richard Eigenmann
 */
public record CopyToDirRequest(@NonNull Collection<SortableDefaultMutableTreeNode> nodes, @NonNull File targetDirectory) {
    /**
     * Constructor to validate the that target Directory is writable and that it is a directory
     *
     * @param nodes           The nodes to copy
     * @param targetDirectory the target directory to copy to
     */
    public CopyToDirRequest {
        if (!targetDirectory.canWrite()) {
            throw new IllegalArgumentException(String.format("Target location {%s} must be writable!", targetDirectory));
        }
        if (!targetDirectory.isDirectory()) {
            throw new IllegalArgumentException(String.format("Target location {%s} must be a directory!", targetDirectory));
        }
    }
}
