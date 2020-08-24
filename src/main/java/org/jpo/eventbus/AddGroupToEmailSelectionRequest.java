package org.jpo.eventbus;

import org.jpo.datamodel.SortableDefaultMutableTreeNode;

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
 * This request indicates that the group's child nodes should be added to the email selection
 * 
 * @author Richard Eigenmann
 */
public class AddGroupToEmailSelectionRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * A request to add the child nodes of the group to the email selection
     * @param node The node to which the empty group should be added
     */
    public AddGroupToEmailSelectionRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * Returns the group node whose pictures are to be added
     * @return the Node with the group
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

    
}
