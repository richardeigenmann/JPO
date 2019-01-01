package jpo.EventBus;

import java.io.File;
import java.util.List;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/*
 Copyright (C) 2017-2019  Richard Eigenmann.
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
 * Request to copy the pictures of the supplied nodes to the supplied zipfile
 *
 * @author Richard Eigenmann
 */
public class CopyToZipfileRequest implements Request {

    //private final SortableDefaultMutableTreeNode[] nodes;
    private final List<SortableDefaultMutableTreeNode> nodes;
    private final File targetZipfile;

    /**
     * Request to indicate that the user would like to copy the pictures in the
     * selected nodes to a target zipfile
     *
     * @param nodes         The nodes for which the user would like copy the pictures
     * @param targetZipfile the target zipfile
     */
    public CopyToZipfileRequest(List<SortableDefaultMutableTreeNode> nodes, File targetZipfile) {
        this.nodes = nodes;
        this.targetZipfile = targetZipfile;
    }

    /**
     * The nodes for which the dialog should be executed
     *
     * @return the node
     */
    public List<SortableDefaultMutableTreeNode> getNodes() {
        return nodes;
    }

    /**
     * Returns the target directory
     *
     * @return the target zipfile
     */
    public File getTargetZipfile() {
        return targetZipfile;
    }

}
