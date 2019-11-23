package org.jpo.eventBus;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jpo.dataModel.SortableDefaultMutableTreeNode;

import java.util.List;
import java.util.Objects;

/*
 Copyright (C) 2018-2019 Richard Eigenmann.
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
 * Request to move the pictures of the supplied nodes to the supplied directory
 *
 * @author Richard Eigenmann
 */
public class MoveToNewLocationRequest implements Request {

    private final List<SortableDefaultMutableTreeNode> nodes;

    /**
     * Request to indicate that the user would like to move the pictures in the
     * selected nodes to a target directory
     *
     * @param nodes The nodes for which the user would like move the pictures
     */
    public MoveToNewLocationRequest(@NonNull List<SortableDefaultMutableTreeNode> nodes ) {
        Objects.requireNonNull(nodes);
        this.nodes = nodes;
    }

    /**
     * The nodes for which the dialog should be executed
     *
     * @return the node
     */
    public List<SortableDefaultMutableTreeNode> getNodes() {
        return nodes;
    }


}
