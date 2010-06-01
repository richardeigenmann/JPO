package jpo.dataModel;

import javax.swing.event.*;
import javax.swing.tree.*;

/*
GroupBrower.java:  an implementation of the NodeNavigator for browsing groups.

Copyright (C) 2002 - 2010  Richard Eigenmann.
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
 *  This class implements the NodeNavigator in the specific manner that is required for
 *  displaying the child nodes of a Group in the Thumbnail JScrollPane.
 */
public class GroupNavigator
        extends NodeNavigator
        implements TreeModelListener {

    /**
     *  A reference to the current group that shall be browsed
     */
    private SortableDefaultMutableTreeNode myNode;


    /**
     *  Constructs a new Group Browser object for a specific group node
     *
     * @param node
     */
    public GroupNavigator( SortableDefaultMutableTreeNode node ) {
        //logger.info( String.format( "Creating a new GroupNavigator for group %s", node.toString() ) );
        setNode( node );
    }


    /**
     *  call this method to specify the node that this GroupNavigator should refer to. The node is validated
     *  that it's payload is of type GroupInfo.
     *
     *  @param  node   The SortableDefaultMutableTreeNode that refers to the Group that should be displayed.
     */
    public void setNode( SortableDefaultMutableTreeNode node ) {
        // deregister from prior TreeModelListener (could be that the new group is from a different tree)
        if ( myNode != null ) {
            myNode.getPictureCollection().getTreeModel().removeTreeModelListener( this );
        }

        // validate that we are dealing with a GroupInfo node
        if ( !( node.getUserObject() instanceof GroupInfo ) ) {
            myNode = null;
        } else {
            myNode = node;
            // register this component so that it receives notifications from the Model
            myNode.getPictureCollection().getTreeModel().addTreeModelListener( this );
        }
    }


    /**
     *  returns the name of the Group being displayed
     */
    public String getTitle() {
        if ( myNode != null ) {
            return myNode.toString();
        } else {
            return "<no group>";
        }
    }


    /**
     *  On a group we return the number of children in the group.
     */
    public int getNumberOfNodes() {
        if ( myNode != null ) {
            return myNode.getChildCount();
        } else {
            return 0;
        }
    }


    /**
     *  This method returns the SDMTN node for the indicated position in the group
     *  If there are more Thumbnails than nodes in the group it returns null.
     *
     *  @param index   The component index that is to be returned.
     */
    public SortableDefaultMutableTreeNode getNode( int index ) {
        if ( myNode == null ) {
            return null;
        }
        if ( index >= getNumberOfNodes() ) {
            return null;
        } else {
            return (SortableDefaultMutableTreeNode) myNode.getChildAt( index );
        }
    }


    /**
     *  This method unregisters the TreeModelListener and sets the variables to null;
     */
    @Override
    public void getRid() {
        super.getRid();
        if ( myNode != null ) {
            myNode.getPictureCollection().getTreeModel().removeTreeModelListener( this );
            myNode = null;
        }
        //relayoutListeners.clear();
    }


    /**
     *   This method is defined by the TreeModelListener interface and gives the
     *   JThumnailScrollPane a notification that some nodes changed in a non dramatic way.
     *   The nodes that were changed have their Constraints reevaluated and a revalidate
     *   is called to update the screen.
     *
     * @param e
     */
    @Override
    public void treeNodesChanged( TreeModelEvent e ) {
        logger.fine( "treeNodesChanged: " + e.toString() );
        if ( myNode == null ) {
            //logger.info("GroupNavigator.treeNodesChanged: ERROR! This should not have been called as there is not group showing and therefore there should be no tree listener firing off. Ignoring notification.");
            return;
        }

        // don't get excited and force a relayout unless the inserted node is part
        // of the current group
        TreePath myPath = new TreePath( myNode.getPath() );
        if ( myPath.equals( e.getTreePath() ) ) {
            logger.fine( String.format( "A Node was changed. No need to get excited at the group level. myNode: %s, notification node %s", myPath.toString(), ( (SortableDefaultMutableTreeNode) e.getTreePath().getLastPathComponent() ).toString() ) );
            //notifyNodeNavigatorListeners();
        }
    }


    /**
     *   This method is defined by the TreeModelListener interface and gives the
     *   JThumnailScrollPane a notification if additional nodes were inserted.
     *   The additional nodes are added and the existing nodes are reevaluated
     *   as to whether they are at the right place. Revalidate is called to update
     *   the screen.
     *
     * @param e
     */
    @Override
    public void treeNodesInserted( TreeModelEvent e ) {
        //logger.info("GroupNavigator.treeNodesInserted: " + e.toString() );
        if ( myNode == null ) {
            //logger.info("GroupNavigator.treeNodesInserted: ERROR! This should not have been called as there is not group showing and therefore there should be no tree listener firing off. Ignoring notification.");
            return;
        }

        // don't get excited and force a relayout unless the inserted node is part
        // of the current group
        TreePath myPath = new TreePath( myNode.getPath() );
        if ( myPath.equals( e.getTreePath() ) ) {
            //logger.info( "Nodes were inserted under my node. We must therefore relayout the children; myNode: " + myPath.toString() + " comparison:" + ( (SortableDefaultMutableTreeNode) e.getTreePath().getLastPathComponent() ).toString() );
            notifyNodeNavigatorListeners();
        }
    }


    /**
     *   This method is defined by the TreeModelListener interface and gives the
     *   JThumnailScrollPane a notification that some nodes were removed. It steps
     *   through all the Thumbnail Components and makes sure they all are at the correct
     *   location. The dead ones are removed.
     *
     * @param e
     */
    @Override
    public void treeNodesRemoved( TreeModelEvent e ) {
        //logger.info("GroupNavigator.treeNodesRemoved: " + e.toString() );
        if ( myNode == null ) {
            logger.severe( "ERROR! This should not have been called as there is not group showing and therefore there should be no tree listener firing off. Ignoring notification." );
            return;
        }

        // if the current node is part of the tree that was deleted then we need to
        //  reposition the group at the parent node that remains.
        if ( SortableDefaultMutableTreeNode.wasNodeDeleted( myNode, e ) ) {
            //logger.info("GroupNavigator.treeNodesRemoved: determined that a child node of the currently displaying node was deleted and therefore executing a setNode on the parent that remains.");
            setNode( (SortableDefaultMutableTreeNode) e.getTreePath().getLastPathComponent() );
        } else {
            // don't get excited and force a relayout unless the partent of the deleted
            // node is the current group
            TreePath myPath = new TreePath( myNode.getPath() );
            if ( myPath.equals( e.getTreePath() ) ) {
                logger.fine( String.format( "Nodes were removed from my node. We must therefore relayout the children; myPath: %s, lastPathComponent: [%s]", myPath.toString(), ( (SortableDefaultMutableTreeNode) e.getTreePath().getLastPathComponent() ).toString() ) );
                notifyNodeNavigatorListeners();
            }
        }
    }


    /**
     *   This method is defined by the TreeModelListener interface and gives the
     *   JThumnailScrollPane a notification if there was a massive structure change in the
     *   tree. In this event all laying out shall stop and the group should be laid out from
     *   scratch.
     *
     * @param e
     */
    @Override
    public void treeStructureChanged( TreeModelEvent e ) {
        logger.fine( String.format( "We've teen told that the Tree structure changed Event: %s", e.toString() ) );
        if ( myNode == null ) {
            //logger.info("GroupNavigator.treeStructureChanged: ERROR! This should not have been called as there is not group showing and therefore there should be no tree listener firing off. Ignoring notification.");
            return;
        }
        if ( myNode.isNodeDescendant( (SortableDefaultMutableTreeNode) e.getTreePath().getLastPathComponent() ) ) {
            notifyNodeNavigatorListeners();
        }
    }
}
