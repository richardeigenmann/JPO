package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.util.Objects;

/*
 Copyright (C) 2017-2018  Richard Eigenmann.
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
 * Request to copy the pictures of the supplied nodes to the supplied directory
 *
 * @author Richard Eigenmann
 */
public class CopyToDirRequest implements Request {

    private final SortableDefaultMutableTreeNode[] nodes;
    private final File targetLocation;

    /**
     * Request to indicate that the user would like to copy the pictures in the
     * selected nodes to a target directory
     *
     * @param nodes The nodes for which the user would like copy the pictures
     * @param targetLocation The target directory
     */
    public CopyToDirRequest(@NonNull SortableDefaultMutableTreeNode[] nodes, @NonNull File targetLocation ) {
        Objects.requireNonNull(nodes);
        Objects.requireNonNull(targetLocation);
        this.nodes = nodes;
        this.targetLocation = targetLocation;
    }

    /**
     * The nodes for which the dialog should be executed
     *
     * @return the node
     */
    @NonNull
    public SortableDefaultMutableTreeNode[] getNodes() {
        return nodes;
    }

    /**
     * Returns the target directory
     * @return the target directory
     */
    @NonNull
    public File getTargetLocation() {
        return targetLocation;
    }

}
