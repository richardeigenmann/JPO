package jpo.dataModel;

import java.util.*;
import java.util.logging.Logger;

/*
NodeNavigator.java:  an implementation of the NodeNavigatorInterface for browsing pictures sequentially.

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
 *  This class implements the NodeNavigatorListener functionality required by the  NodeNavigatorInterface
 *  but the other methods need to be implements by the extending class.
 */
public abstract class NodeNavigator
        implements NodeNavigatorInterface {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( NodeNavigator.class.getName() );


    /**
     *  The implementing class must return the title for the images being shown.
     */
    @Override
    public abstract String getTitle();


    /**
     *  The implementing class must return the number of nodes it contains.
     */
    @Override
    public abstract int getNumberOfNodes();


    /**
     *  This method returns the node for the indicated position in the group.
     *
     *  @param index   The component index that is to be returned. The number is from 0 to
     *                 {@link #getNumberOfNodes}. If there are 3 nodes request getNode(0),
     *                 getNode(1) and getNode(2).
     */
    @Override
    public abstract SortableDefaultMutableTreeNode getNode( int index );


    /**
     *  This method unregisters the TreeModelListener and sets the variables to null;
     *  a super must be called to ensure that the NodeNavigator cleans up the relayoutListeners
     */
    @Override
    public void getRid() {
        relayoutListeners.clear();
    }

    /**
     *  This ArrayList holds the reference to the listeners that need to be notified if there is a structural change.
     *  Observer pattern.
     */
    private final ArrayList<NodeNavigatorListener> relayoutListeners = new ArrayList<NodeNavigatorListener>();


    /**
     *  method to register a NodeNavigatorListener as a listener
     */
    @Override
    public void addNodeNavigatorListener( NodeNavigatorListener listener ) {
        LOGGER.fine( String.format( "adding listener: %s", listener.toString() ) );
        relayoutListeners.add( listener );
        LOGGER.fine( String.format( "We now have %d relayout listeners.", relayoutListeners.size() ) );
    }


    /**
     *  method to remove a NodeNavigatorListener as a listener
     */
    @Override
    public void removeNodeNavigatorListener( NodeNavigatorListener listener ) {
        LOGGER.fine( String.format( "removing listener: %s", listener.toString() ) );
        relayoutListeners.remove( listener );
        LOGGER.fine( String.format( "We now have %d relayout listeners.", relayoutListeners.size() ) );
    }


    /**
     * Method that notifies the NodeNavigatorListener of a structural change that they need to
     * respond to.
     */
    public void notifyNodeNavigatorListeners() {
        LOGGER.fine( String.format( "notifying %d NodeNavigatorListeners.", relayoutListeners.size() ) );
        @SuppressWarnings( "unchecked" )
        ArrayList<NodeNavigatorListener> stableRelayoutListeners = (ArrayList<NodeNavigatorListener>) relayoutListeners.clone();
        for ( NodeNavigatorListener relayoutListener : stableRelayoutListeners ) {
            LOGGER.fine( String.format( "   now notifying relayout listener: %s", relayoutListener.toString() ) );
            relayoutListener.nodeLayoutChanged();
        }
    }

 
}
