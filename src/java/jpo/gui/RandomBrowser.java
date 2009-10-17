package jpo.gui;

import jpo.dataModel.*;
import jpo.*;
import java.util.*;

/*
RandomBrower.java:  an implementation of the ThumbnailBrowserInterface for browsing random pictures.

Copyright (C) 2006 - 2009  Richard Eigenmann, Zürich, Switzerland
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
 *  This class returns a browser for all the pictures under a node in a random order
 */
public class RandomBrowser
        extends ThumbnailBrowser {

    /**
     *  This ArrayList holds all the picture nodes that the browser will serve
     */
    private ArrayList<SortableDefaultMutableTreeNode> allPictures = new ArrayList<SortableDefaultMutableTreeNode>();

    /**
     * The root node for this browser
     */
    private SortableDefaultMutableTreeNode rootNode;


    /**
     *  Constructor for a RandomBrowser.
     *
     *  @param rootNode    The rootNode under which the randomisation should happen.
     */
    public RandomBrowser( SortableDefaultMutableTreeNode rootNode ) {
        logger.fine( "Constructor called on node: " + rootNode.toString() );
        this.rootNode = rootNode;
        rootNode.getChildPictureNodes( allPictures, true );
        Collections.shuffle( allPictures );
    }


    /**
     *  returns a title for this browser
     */
    public String getTitle() {
        return String.format("Randomised pictures from %s",rootNode.toString() ) ;
    }


    /**
     * The Random Browser returns the number of nodes in the shuffled list
     * @return the number of nodes in the browser
     */
    public int getNumberOfNodes() {
        return allPictures.size();
    }


    /**
     * Returns the node for the specific index
     * @param index The index 0 to getNumberOfNodes to retrieve
     * @return the node for the index number
     */
    public SortableDefaultMutableTreeNode getNode( int index ) {
        logger.fine( String.format( "requested for node: %d", index ) );
        return allPictures.get( index );
    }
}
