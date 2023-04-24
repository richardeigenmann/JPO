package org.jpo.datamodel;

/*
 GroupOrPicture.java: The userObjects in the TreeNodes

 Copyright (C) 2023 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */


/**
 * Objects conforming to this interface are the objects on each tree node in JPO.
 * They will be PictureInfo or GroupInfo objects
 */
public interface GroupOrPicture {

    /**
     * A GroupOrPicture must be able to remember the node that owns it to facilitate traversal
     * @param sortableDefaultMutableTreeNode The owning node
     */
    void setOwningNode(final SortableDefaultMutableTreeNode sortableDefaultMutableTreeNode);

    /**
     * Returns the node that owns this object
     * @return the node that owns this object
     */
    SortableDefaultMutableTreeNode getOwningNode();
}
