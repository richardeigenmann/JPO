package org.jpo.eventBus;

import org.jpo.dataModel.SortableDefaultMutableTreeNode;

import java.util.List;

/*
 Copyright (C) 2017-2019 Richard Eigenmann.
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
 * Request to copy the pictures of the supplied nodes to a Zipfile to be picked 
 * from a Filechooser
 *
 * @author Richard Eigenmann
 */
public class CopyToNewZipfileRequest implements Request {

    private final List<SortableDefaultMutableTreeNode> nodes;

    /**
     * Request to indicate that the user would like to copy the pictures in the selected nodes to a target zipfile
     *
     * @param nodes The nodes for which the user would like copy the pictures
     */
    public CopyToNewZipfileRequest( List<SortableDefaultMutableTreeNode> nodes ) {
        this.nodes = nodes;
    }

    /**
     * The nodes for which the operation should be done
     *
     * @return the node
     */
    public List<SortableDefaultMutableTreeNode> getNodes() {
        return nodes;
    }

    
    
}
