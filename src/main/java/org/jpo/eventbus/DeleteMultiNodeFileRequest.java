package org.jpo.eventbus;

import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import java.util.List;

/*
 Copyright (C) 2017  Richard Eigenmann.
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
 * This request indicates that the user wants to delete the files of a specific 
 * nodes and remove the nodes
 *
 * @author Richard Eigenmann
 */
public class DeleteMultiNodeFileRequest implements Request {

    private final List<SortableDefaultMutableTreeNode> nodes;

    /**
     * This request indicates that the user wants to delete the file of a specific node
     *
     * @param nodes The node for which the file and the nodes are to be deleted
     */
    public DeleteMultiNodeFileRequest( List<SortableDefaultMutableTreeNode> nodes ) {
        this.nodes = nodes;
    }

    /**
     * The nodes to delete
     *
     * @return the nodes
     */
    public List<SortableDefaultMutableTreeNode> getNodes() {
        return nodes;
    }

}
