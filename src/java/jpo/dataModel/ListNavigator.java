package jpo.dataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*
 ListNavigator.java:  an implementation of the NodeNavigator for browsing pictures.

 Copyright (C) 2006-2014 Richard Eigenmann, Zürich, Switzerland
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
 * This class implements the NodeNavigator Interface so that all the pictures of
 * a specified List can be browsed sequentially.
 */
public class ListNavigator
        extends NodeNavigator {

    /**
     * Optional Constructor for a ListNavigator.
     *
     * @param title
     * @param nodes
     */
    public ListNavigator( String title,
            List<SortableDefaultMutableTreeNode> nodes ) {
        setTitle( title );
        setList( nodes );
    }

    /**
     * Optional Constructor for a ListNavigator.
     */
    public ListNavigator() {
        setTitle( "" );
        setList( new ArrayList<SortableDefaultMutableTreeNode>() );
    }
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ListNavigator.class.getName() );
    /**
     * title for this List of images.
     */
    private String title;

    /**
     * Sets the title to be returned if any component tried to display the title
     * of the list
     *
     * @param newTitle
     */
    public void setTitle( String newTitle ) {
        title = newTitle;
    }

    /**
     * Returns the title of the node set
     *
     * @return the Title
     */
    @Override
    public String getTitle() {
        try {
            return title;
        } catch ( NullPointerException ex ) {
            return "";
        }
    }

    /**
     * Returns the number of pictures in this group. Starts at 1 like all
     * arrays.
     *
     * @return The number of pictures
     */
    @Override
    public int getNumberOfNodes() {
        try {
            return allPictures.size();
        } catch ( NullPointerException ex ) {
            LOGGER.severe( String.format( "Why did we get a NullPointerExecption for the ListNavigator %s?", getTitle() ) );
            return 0;
        }
    }

    /**
     * This method returns the node for the indicated position in the group.
     *
     * @param index The component index that is to be returned. The number is
     * from 0 to {@link #getNumberOfNodes}.
     * @return The node for the specified index.
     */
    @Override
    public SortableDefaultMutableTreeNode getNode( int index ) {
        try {
            return allPictures.get( index );
        } catch ( IndexOutOfBoundsException x ) {
            return null;
        }
    }
    /**
     * This List holds a reference to the nodes that make up the navigator
     */
    protected List<SortableDefaultMutableTreeNode> allPictures;

    /**
     * Sets the List of nodes for this navigator
     *
     * @param newList the new List with the nodes to display.
     */
    public void setList(
            List<SortableDefaultMutableTreeNode> newList ) {
        allPictures = newList;
    }

    /**
     * adds a node to the List
     *
     * @param addNode a node to add to the allPictures List
     */
    public void addNode( SortableDefaultMutableTreeNode addNode ) {
        allPictures.add( addNode );
    }

    /**
     * Removes a node from the List
     *
     * @param removeNode a node to add to the allPictures List
     */
    public void removeNode( SortableDefaultMutableTreeNode removeNode ) {
        allPictures.remove( removeNode );
    }

    /**
     * Release the variables used
     */
    @Override
    public void getRid() {
        super.getRid();
        allPictures = null;
    }

    /**
     * Returns a description of the navigator
     *
     * @return A helpful description
     */
    @Override
    public String toString() {
        return String.format( "ListNavigator %d Title: %s  with %d nodes",
                hashCode(), getTitle(), getNumberOfNodes() );
    }
}
