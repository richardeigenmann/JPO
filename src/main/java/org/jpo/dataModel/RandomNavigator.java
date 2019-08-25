package org.jpo.dataModel;

import java.util.Collections;
import java.util.List;


/*
RandomNavigator.java:  an implementation of the NodeNavigator for navigating nodes in a random order.

Copyright (C) 2006 - 2014  Richard Eigenmann, Zürich, Switzerland
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
 *  This class returns a NodeNavigator which shuffles the supplied nodes
 */
public class RandomNavigator
        extends NodeNavigator {

    /**
     *  This List holds all the nodes that the NodeNavigator will serve
     */
    private final List<SortableDefaultMutableTreeNode> nodes;

    /**
     * The title of this set of nodes
     */
    private final String title;

    /**
     *  Constructor for a RandomNavigator.
     *
     *  @param nodes A List of nodes to randomly navigate
     *  @param  title The title of the nodes
     */
    public RandomNavigator( List<SortableDefaultMutableTreeNode> nodes, String title ) {
        this.nodes = nodes;
        this.title = title;
        Collections.shuffle( this.nodes );
    }

    /**
     *  returns a title for this NodeNavigator
     * @return the Title for the NodeNavigator
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Returns the number of nodes in the shuffled list
     * @return The number of nodes in the list
     */
    @Override
    public int getNumberOfNodes() {
        return nodes.size();
    }

    /**
     * Returns the node for the specific index
     * @param index The index of the node to retrieve
     * @return the node for the index number
     */
    @Override
    public SortableDefaultMutableTreeNode getNode( int index ) {
        return nodes.get( index );
    }
}
