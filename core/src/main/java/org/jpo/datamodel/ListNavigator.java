package org.jpo.datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 ListNavigator.java:  an implementation of the NodeNavigator for browsing pictures.

 Copyright (C) 2006-2025 Richard Eigenmann, Zürich, Switzerland
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY,
 without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * This class implements the NodeNavigator Interface so that all the pictures of
 * a specified List can be browsed sequentially.
 */
public class ListNavigator
        extends NodeNavigator {

    public ListNavigator(final Collection<SortableDefaultMutableTreeNode> nodes) {
        add(nodes);
    }

    /**
     * Returns the title of the node set
     *
     * @return the Title
     */
    @Override
    public String getTitle() {
        return "";
    }

    /**
     * Returns the number of pictures in this navigator. Starts at 1 like all
     * arrays.
     *
     * @return The number of pictures
     */
    @Override
    public int getNumberOfNodes() {
        return allPictures.size();
    }

    /**
     * This method returns the node for the indicated position in the group.
     *
     * @param index The component index that is to be returned. The number is
     *              from 0 to {@link #getNumberOfNodes}.
     * @return The node for the specified index.
     */
    @Override
    public SortableDefaultMutableTreeNode getNode(int index) {
        return allPictures.get(index);
    }

    /**
     * This List holds a reference to the nodes that make up the navigator
     */
    private final List<SortableDefaultMutableTreeNode> allPictures = new ArrayList<>();


    /**
     * adds a node to the List
     *
     * @param addNode a node to add to the allPictures List
     */
    public void add(SortableDefaultMutableTreeNode addNode) {
        allPictures.add(addNode);
    }

    /**
     * adds a node to the List
     *
     * @param nodes a node to add to the allPictures List
     */
    public void add(final Collection<SortableDefaultMutableTreeNode> nodes) {
        allPictures.addAll(nodes);
    }

    /**
     * Removes a node from the List
     *
     * @param removeNode a node to add to the allPictures List
     */
    public void removeNode(final SortableDefaultMutableTreeNode removeNode) {
        allPictures.remove(removeNode);
    }

    /**
     * Clears all nodes from the List
     */
    public void clear() {
        allPictures.clear();
    }

    /**
     * Returns a description of the navigator
     *
     * @return A helpful description
     */
    @Override
    public String toString() {
        return String.format("ListNavigator %d Title: %s with %d nodes",
                hashCode(), getTitle(), getNumberOfNodes());
    }
}
