package jpo.dataModel;

import java.util.logging.Logger;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

/*
 FlatGroupNavigator.java:  an implementation of the NodeNavigator for 
browsing all the pictures of a group sequentially.

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
 * Converts a tree of notes into a flat list of pictures
 */
public class FlatGroupNavigator
        extends ListNavigator {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( FlatGroupNavigator.class.getName() );

    /**
     * Listener for tree model events
     */
    private final MyTreeModelListener myTreeModelListener = new MyTreeModelListener();
    
    /**
     * Constructor for a FlatGroupNavigator.
     *
     * @param groupNode The groupNode under which the pictures should be
     * displayed.
     */
    public FlatGroupNavigator( SortableDefaultMutableTreeNode groupNode ) {
        this.groupNode = groupNode;
        Settings.getPictureCollection().getTreeModel().addTreeModelListener( myTreeModelListener );
        buildFromScratch();
    }

    /**
     * Builds the list of nodes from the group.
     */
    private void buildFromScratch() {
        allPictures.clear();
        allPictures = groupNode.getChildPictureNodes( true );
    }

    /**
     * This method shuts down the object and makes it available for garbage
     * collection
     */
    @Override
    public void getRid() {
        //LOGGER.info( "Deregistering the Navigator from the data model notifications." );
        Settings.getPictureCollection().getTreeModel().removeTreeModelListener( myTreeModelListener );
        super.getRid();
    }
    /**
     * A reference to the group for which this FlatGroupNavigator was created.
     */
    private final SortableDefaultMutableTreeNode groupNode;

    /**
     * returns the title of the node
     *
     * @return the title of the node
     */
    @Override
    public String getTitle() {
        if ( ( groupNode != null ) && ( groupNode.getUserObject() instanceof GroupInfo ) ) {
            return ( (GroupInfo) groupNode.getUserObject() ).getGroupName();
        } else {
            return "No title available";
        }
    }

    private class MyTreeModelListener implements TreeModelListener {

        /**
         * We are notified here that a node changed
         *
         * @param e The notification event details
         */
        @Override
        public void treeNodesChanged( TreeModelEvent e ) {
        }

        /**
         * We are notified here that a node was inserted
         *
         * @param e The event that we will be notified on
         */
        @Override
        public void treeNodesInserted( TreeModelEvent e ) {
        }

        /**
         * The TreeModelListener interface tells us of tree node removal events.
         * If we receive a removal event we need to find out if one of our nodes
         * was removed
         *
         * @param e The Notification event
         */
        @Override
        public void treeNodesRemoved( TreeModelEvent e ) {
            LOGGER.info( String.format( "Investigating a remove event: %s", e.toString() ) );

            // Problem here is that if the current node was removed we are no longer on the node that was removed
            TreePath currentNodeTreePath = new TreePath( groupNode.getPath() );
            LOGGER.fine( String.format( "The current group node has this path: %s", currentNodeTreePath.toString() ) );

            // step through the array of removed nodes
            Object[] children = e.getChildren();
            TreePath removedChild;
            for ( int i = 0; i < children.length; i++ ) {
                removedChild = new TreePath( children[i] );
                LOGGER.fine( String.format( "Deleted child[%d] has path: %s", i, removedChild.toString() ) );
                if ( removedChild.isDescendant( currentNodeTreePath ) ) {
                    LOGGER.info( String.format( "Oh dear, our group has just disappeared." ) );
                    allPictures.clear();
                    notifyNodeNavigatorListeners();
                    return; // no point in continuing the loop; the group is gone.
                }

                TreePath parentOfRemoved = e.getTreePath();
                if ( currentNodeTreePath.equals( parentOfRemoved ) ) {
                    int[] childIndices = e.getChildIndices();
                    int myNodeCount = getNumberOfNodes();
                    LOGGER.info( String.format( "The removed %d node(s) are children of the current group (which has %d nodes)", childIndices.length, myNodeCount ) );
                    buildFromScratch();
                    notifyNodeNavigatorListeners();
                }
            }
        }

        /**
         * We are notified here if the structure changed
         *
         * @param e The event
         */
        @Override
        public void treeStructureChanged( TreeModelEvent e ) {
        }
    }
}
