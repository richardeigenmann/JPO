package jpo.EventBus;

import java.io.File;
import jpo.dataModel.SortableDefaultMutableTreeNode;

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
 * This request indicates that the user wants to add the supplied collection to the supplied group
 * 
 * @author Richard Eigenmann
 */
public class AddCollectionToGroupRequest implements Request {

    private final SortableDefaultMutableTreeNode node;
    private final File collectionFile;

    /**
     * A request to add the supplied collection file to the node
     * @param node The node to which the collection should be added
     * @param collectionFile The file with the collection
     */
    public AddCollectionToGroupRequest( SortableDefaultMutableTreeNode node, File collectionFile ) {
        this.node = node;
        this.collectionFile = collectionFile;
    }

    /**
     * Returns the node to which the collection should be added
     * @return the Node with the group
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

    /**
     * Returns the collectionFile that should be added.
     * @return the collection file
     */
    public File getCollectionFile() {
        return collectionFile;
    }
    
}
