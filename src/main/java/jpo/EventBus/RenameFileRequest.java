package jpo.EventBus;

import jpo.dataModel.PictureInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

/*
 Copyright (C) 2018  Richard Eigenmann.
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
 * The receiver of this request is supposed to bring up the file rename dialog for the selected node
 *
 * @author Richard Eigenmann
 */
public class RenameFileRequest implements Request {

    private final SortableDefaultMutableTreeNode node;
    private final String newFileName;

    /**
     * A request to rename the supplied node
     *
     * @param node The node to rename
     */
    public RenameFileRequest(@NonNull SortableDefaultMutableTreeNode node, String newFileName) {
        Objects.requireNonNull(node);
        Objects.requireNonNull(newFileName);
        if (!(node.getUserObject() instanceof PictureInfo)) {
            throw new RuntimeException("The node must be of type PictureInfo");
        }
        this.node = node;
        this.newFileName = newFileName;
    }

    /**
     * Returns the node to be renamed
     *
     * @return the Node with the picture
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }


    /**
     * Returns the new file name
     * @return the new file name
     */
    public String getNewFileName() {
        return newFileName;
    }

}
