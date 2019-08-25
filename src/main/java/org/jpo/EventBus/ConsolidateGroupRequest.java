package org.jpo.EventBus;

import org.jpo.dataModel.SortableDefaultMutableTreeNode;

import java.io.File;

/*
 Copyright (C) 2017 - 2019  Richard Eigenmann.
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
 * Request to consolidate a group
 *
 * @author Richard Eigenmann
 */
public class ConsolidateGroupRequest implements Request {

    private final SortableDefaultMutableTreeNode node;
    private final File targetDir;
    private final boolean recurseSubgroups;

    /**
     * Request to consolidate a group
     *
     * @param node The node to consolidate
     * @param targetDir the target directory.
     * @param  recurseSubgroups whether to recurse into sub groups
     */
    public ConsolidateGroupRequest(SortableDefaultMutableTreeNode node, File targetDir, boolean recurseSubgroups ) {
        this.node = node;
        this.targetDir = targetDir;
        this.recurseSubgroups = recurseSubgroups;
    }

    /**
     * The node to consolidate
     *
     * @return the node
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

    /**
     * The target dir for the operation
     *
     * @return the target directory
     */
    public File getTargetDir() {
        return targetDir;
    }

    /**
     * Whether to recurse into sub groups
     * @return true if it should recurse, false if not
     */
    public boolean getRecurseSubgroups() { return recurseSubgroups; }
}
