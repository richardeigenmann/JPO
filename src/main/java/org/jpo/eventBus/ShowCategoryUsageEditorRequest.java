package org.jpo.eventBus;

import org.jpo.dataModel.SortableDefaultMutableTreeNode;

import java.util.Set;

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
 * The receiver of this request is supposed to spawn the CategoryUsageEditor
 * for the supplied node.
 * 
 * @author Richard Eigenmann
 */
public class ShowCategoryUsageEditorRequest implements Request {

    private final Set<SortableDefaultMutableTreeNode> nodes;

    /**
     * A request to bring up the CategoryUsageEditor for the supplied nodes
     * @param nodes The nodes
     */
    public ShowCategoryUsageEditorRequest( Set<SortableDefaultMutableTreeNode> nodes ) {
        this.nodes = nodes;
    }

    /**
     * Returns the node for which the CategoryUsageEditor is to be shown.
     * @return the Node with the picture
     */
    public Set<SortableDefaultMutableTreeNode> getNodes() {
        return nodes;
    }

}
