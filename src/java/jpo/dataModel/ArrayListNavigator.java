package jpo.dataModel;

import jpo.*;
import java.util.*;

/*
ArrayListNavigator.java:  an implementation of the NodeNavigator for browsing pictures.

Copyright (C) 2006-2010  Richard Eigenmann, Zürich, Switzerland
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
 *  This class implements the NodeNavigator Interface so that all the pictures of a specified
 *  ArrayList can be browsed sequentially.
 */
public class ArrayListNavigator
        extends NodeNavigator {

    /**
     * Optional Constructor for an ArrayListNavigator.
     * @param title
     * @param nodes 
     */
    public ArrayListNavigator( String title,
            ArrayList<SortableDefaultMutableTreeNode> nodes ) {
        setTitle( title );
        setArrayList( nodes );
    }


    /**
     * Optional Constructor for an ArrayListNavigator.
     */
    public ArrayListNavigator() {
        setTitle( "" );
        setArrayList( new ArrayList<SortableDefaultMutableTreeNode>() );
    }

    /**
     *   title for this ArrayList of images.
     */
    private String title;


    /**
     * Sets the title to be returned if any component tried to display the title of the list
     * @param newTitle
     */
    public void setTitle( String newTitle ) {
        title = newTitle;
    }


    /**
     * Returns the title of the node set
     * @return the Title
     */
    public String getTitle() {
        if ( title != null ) {
            return title;
        } else {
            return "";
        }
    }


    /**
     *  Returns the number of pictures in this group. Starts at 1 like all arrays.
     */
    public int getNumberOfNodes() {
        return allPictures.size();
    }


    /**
     *  This method returns the node for the indicated position in the group.
     *
     *  @param index   The component index that is to be returned. The number is from 0 to
     *                 {@link #getNumberOfNodes}. If there are 3 nodes request getNode(0),
     *                 getNode(1) and getNode(2).
     */
    public SortableDefaultMutableTreeNode getNode( int index ) {
        try {
            return allPictures.get( index );
        } catch ( ArrayIndexOutOfBoundsException x ) {
            logger.warning( String.format( "Requested node %d on NodeNavigator %s is out of bounds!", index, getTitle() ) );
            return null;
        }
    }

    /**
     *  This ArrayList holds a reference to each picture under the start group.
     */
    protected ArrayList<SortableDefaultMutableTreeNode> allPictures;


    /**
     *   sets the ArrayList
     *
     *  @param newArrayList   the new ArrayList with the nodes to display.
     */
    public void setArrayList(
            ArrayList<SortableDefaultMutableTreeNode> newArrayList ) {
        allPictures = newArrayList;
    }


    /**
     *  adds a node to the ArrayList
     *
     *  @param  addNode   a node to add to the allPictures ArrayList
     */
    public void addNode( SortableDefaultMutableTreeNode addNode ) {
        allPictures.add( addNode );
    }


    /**
     *  Removes a node from the ArrayList
     *
     *  @param  removeNode   a node to add to the allPictures ArrayList
     */
    public void removeNode( SortableDefaultMutableTreeNode removeNode ) {
        //logger.info( String.format( "Removing node %s", removeNode.toString() ) );
        allPictures.remove( removeNode );
        //logger.info( toString() );
    }


    /**
     *  This method unregisters the TreeModelListener and sets the variables to null;
     */
    @Override
    public void getRid() {
        super.getRid();
        allPictures = null;
    }


    /**
     * Overriden to print some useful info about the nodes
     * @return A helpful description
     */
    @Override
    public String toString() {
        return String.format( "ArrayListBrowser %d Title: %s  with %d nodes",
                hashCode(), getTitle(), getNumberOfNodes() );
    }
}
