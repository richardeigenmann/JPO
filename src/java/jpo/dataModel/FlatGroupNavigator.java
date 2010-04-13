package jpo.dataModel;

import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

/*
FlatGroupNavigator.java:  an implementation of the ThumbnailBrowserInterface for browsing all the pictures of a group sequentially.

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
 *  This class implements the ThumbnailBrowserInterface so that all the potentially nested 
 *  child pictures of the specified group are browsed sequentially.
 */
public class FlatGroupNavigator
        extends ArrayListNavigator
        implements TreeModelListener {

    /**
     *  Constructor for a FlatGroupNavigator.
     *
     *  @param groupNode    The groupNode under which the pictures should be displayed.
     */
    public FlatGroupNavigator( SortableDefaultMutableTreeNode groupNode ) {
        this.groupNode = groupNode;
        Settings.pictureCollection.getTreeModel().addTreeModelListener( this );
        buildFromScratch();
    }


    /**
     * Builds the list of nodes from the group.
     */
    private void buildFromScratch() {
        allPictures.clear();
        enumerateAndAddToList( allPictures, groupNode );
    }


    /**
     * This method shuts down the object and makes it available for garbage collection
     */
    public void getRid() {
        logger.info( "Deregistering the Navigator from the data model notifications." );
        Settings.pictureCollection.getTreeModel().removeTreeModelListener( this );
        groupNode = null;
        super.getRid();
    }

    /**
     *  A reference to the group for which this FlatGroupNavigator was created.
     */
    private SortableDefaultMutableTreeNode groupNode = null;


    /**
     *  returns the title of the groupstring Sequential
     */
    @Override
    public String getTitle() {
        if ( ( groupNode != null ) && ( groupNode.getUserObject() instanceof GroupInfo ) ) {
            return ( (GroupInfo) groupNode.getUserObject() ).getGroupName();
        } else {
            return "No title available";
        }
    }


    /**
     *  This method collects all pictures under the startNode into the supplied ArrayList. This method
     *  calls itself recursively.
     *
     *  @param  myList   The ArrayList to which to add the pictures.
     *  @param  startNode   The group node under which to collect the pictures.
     */
    public void enumerateAndAddToList(
            ArrayList<SortableDefaultMutableTreeNode> myList,
            SortableDefaultMutableTreeNode startNode ) {
        Enumeration kids = startNode.children();
        SortableDefaultMutableTreeNode n;

        while ( kids.hasMoreElements() ) {
            n = (SortableDefaultMutableTreeNode) kids.nextElement();
            if ( n.getUserObject() instanceof GroupInfo ) {
                enumerateAndAddToList( myList, n );
            } else if ( n.getUserObject() instanceof PictureInfo ) {
                myList.add( n );
            }
        }
    }


    /**
     * We are notified here that a node changed
     * @param e The notification event details
     */
    public void treeNodesChanged( TreeModelEvent e ) {
    }


    /**
     * We are notified here that a node was inserted
     * @param e
     */
    public void treeNodesInserted( TreeModelEvent e ) {
    }


    /**
     *  The TreeModelListener interface tells us of tree node removal events.
     *  If we receive a removal event we need to find out if one of our nodes was removed
     * @param e The Notification eevent
     */
    public void treeNodesRemoved( TreeModelEvent e ) {
        logger.info( String.format( "Investigating a remove event: %s", e.toString() ) );

        // Problem here is that if the current node was removed we are no longer on the node that was removed
        TreePath currentNodeTreePath = new TreePath( groupNode.getPath() );
        logger.fine( String.format( "The current group node has this path: %s", currentNodeTreePath.toString() ) );

        // step through the array of removed nodes
        Object[] children = e.getChildren();
        TreePath removedChild;
        for ( int i = 0; i < children.length; i++ ) {
            removedChild = new TreePath( children[i] );
            logger.fine( String.format( "Deleted child[%d] has path: %s", i, removedChild.toString() ) );
            if ( removedChild.isDescendant( currentNodeTreePath ) ) {
                logger.info( String.format( "Oh dear, our group has just disappeared." ) );
                allPictures.clear();
                //notifyNodeChangeListeners( indexMapping );
                notifyRelayoutListeners();
                return; // no point in continuing the loop; the group is gone.
            }

            TreePath parentOfRemoved = e.getTreePath();
            if ( currentNodeTreePath.equals( parentOfRemoved ) ) {
                int[] childIndices = e.getChildIndices();
                int myNodeCount = getNumberOfNodes();
                logger.info( String.format( "The removed %d node(s) are children of the current group (which has %d nodes)", childIndices.length, myNodeCount ) );
                buildFromScratch();
                notifyRelayoutListeners();
            }
        }
    }


    /**
     * We are notified here if the structure changed
     * @param e
     */
    public void treeStructureChanged( TreeModelEvent e ) {
    }
}
