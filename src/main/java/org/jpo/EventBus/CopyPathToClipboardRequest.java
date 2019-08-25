package org.jpo.EventBus;

import org.jpo.dataModel.SortableDefaultMutableTreeNode;

import java.util.List;

/*
 Copyright (C) 2019  Richard Eigenmann.
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
 * Request to copy the file path(s) of picture nodes to the system clipboard
 *
 * @author Richard Eigenmann
 */
public class CopyPathToClipboardRequest implements Request {

    private final List<SortableDefaultMutableTreeNode> nodes;

    /**
     * Request to copy the picture modes to the clipboard
     *
     * @param nodes The nodes
     */
    public CopyPathToClipboardRequest(List<SortableDefaultMutableTreeNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * The nodes for which the operation should be done
     *
     * @return the nodes
     */
    public List<SortableDefaultMutableTreeNode> getNodes() {
        return nodes;
    }


}
