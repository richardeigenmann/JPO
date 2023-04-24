package org.jpo.datamodel;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2002-2022 Richard Eigenmann.
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
 * This class implements the NodeNavigator in the specific manner that is
 * required for displaying the child nodes of a Group in the Thumbnail
 * JScrollPane.
 */
public class GroupNavigator extends NodeNavigator {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( GroupNavigator.class.getName() );

    /**
     * A reference to the current group that shall be browsed
     */
    private SortableDefaultMutableTreeNode myNode;

    public GroupNavigator(final SortableDefaultMutableTreeNode groupNode) {
        setNode(groupNode);
    }

    /**
     * call this method to specify the node that this GroupNavigator should
     * refer to. The node is validated that it's payload is of type GroupInfo.
     *
     * @param node The SortableDefaultMutableTreeNode that refers to the Group
     *             that should be displayed.
     */
    private void setNode(final SortableDefaultMutableTreeNode node) {
        // deregister from prior TreeModelListener (could be that the new group is from a different tree)
        final var pictureCollection = node.getPictureCollection();
        if (myNode != null && pictureCollection != null) {
            myNode.getPictureCollection().removeTreeModelListener(myTreeModelListener);
        }

        // validate that we are dealing with a GroupInfo node
        if (!(node.getUserObject() instanceof GroupInfo)) {
            myNode = null;
        } else {
            myNode = node;
            if ( pictureCollection != null ) {
                // register this component so that it receives notifications from the Model
                myNode.getPictureCollection().addTreeModelListener(myTreeModelListener);
            }
        }
    }

    /**
     * returns the name of the Group being displayed
     *
     * @return the description of the group
     */
    @Override
    public String getTitle() {
        if ( myNode != null ) {
            return myNode.toString();
        } else {
            return "<no group>";
        }
    }

    /**
     * Returns the group node of the Navigator
     *
     * @return the group node
     */
    public SortableDefaultMutableTreeNode getGroupNode() {
        return myNode;
    }

    /**
     * On a group we return the number of children in the group.
     *
     * @return the number of child nodes
     */
    @Override
    public int getNumberOfNodes() {
        if ( myNode != null ) {
            return myNode.getChildCount();
        } else {
            return 0;
        }
    }

    /**
     * This method returns the SortableDefaultMutableTreeNode node for the
     * indicated position in the group. If the request is for an index larger
     * than the number of nodes null is returned so that clients can show
     * something appropriate.
     *
     * @param index The component index that is to be returned.
     * @return the child node at the index
     */
    @Override
    public SortableDefaultMutableTreeNode getNode(final int index) {
        if (myNode == null) {
            return null;
        }
        if (index >= getNumberOfNodes()) {
            return null;
        } else {
            return (SortableDefaultMutableTreeNode) myNode.getChildAt(index);
        }
    }


    private final MyTreeModelListener myTreeModelListener = new MyTreeModelListener();

    /**
     * Listens for Model changes and adjusts the Navigator accordingly
     */
    private class MyTreeModelListener implements TreeModelListener {

        /**
         * This method is defined by the TreeModelListener interface and gives
         * the GroupNavigator a notification that some nodes changed in a non-dramatic
         * way. The nodes that were changed have their Constraints
         * reevaluated and a revalidate is called to update the screen.
         *
         * @param treeModelEvent The event
         */
        @Override
        public void treeNodesChanged(final TreeModelEvent treeModelEvent) {
            LOGGER.log(Level.FINE, "treeNodesChanged: {0}", treeModelEvent);
            if (myNode == null) {
                return;
            }

            // don't get excited and force a relayout unless the inserted node is part
            // of the current group
            final var myPath = new TreePath(myNode.getPath());
            if (myPath.equals(treeModelEvent.getTreePath())) {
                LOGGER.log(Level.FINE, "A Node was changed. No need to get excited at the group level. myNode: {0}, notification node {1}", new Object[]{myPath, treeModelEvent.getTreePath().getLastPathComponent()} );
            }
        }

        /**
         * This method is defined by the TreeModelListener interface and gives
         * the GroupNavigator a notification if additional nodes were inserted.
         * The additional nodes are added and the existing nodes are reevaluated
         * whether they are at the right place. Revalidate is called to
         * update the screen.
         *
         * @param treeModelEvent The event
         */
        @Override
        public void treeNodesInserted(final TreeModelEvent treeModelEvent) {
            if (myNode == null) {
                return;
            }

            // don't get excited and force a relayout unless the inserted node is part
            // of the current group
            final var myPath = new TreePath(myNode.getPath());
            if (myPath.equals(treeModelEvent.getTreePath())) {
                notifyNodeNavigatorListeners();
            }
        }

        /**
         * This method is defined by the TreeModelListener interface and gives
         * the GroupNavigator a notification that some nodes were removed.
         * Case 1: the removal affected some other part of the tree. Result: we
         * don't care Case 2: the Group being shown was wiped off the tree.
         * Result: We reposition to the last node still in existence (could be
         * the root node) Case 3: a child of our current Group was removed.
         * Result: we relayout the nodes.
         *
         * @param treeModelEvent The event
         */
        @Override
        public void treeNodesRemoved(final TreeModelEvent treeModelEvent) {
            if (myNode == null) {
                LOGGER.severe("ERROR! This should not have been called as there is not group showing and therefore there should be no tree listener firing off. Ignoring notification.");
                return;
            }

            // if the current node is part of the tree that was deleted then we need to
            //  reposition the group at the parent node that remains.
            if (SortableDefaultMutableTreeNode.wasNodeDeleted(myNode, treeModelEvent)) {
                LOGGER.log(Level.INFO, "Determined that our current node has died. Moving to the last node still present: {0}", treeModelEvent.getTreePath().getLastPathComponent());
                setNode((SortableDefaultMutableTreeNode) treeModelEvent.getTreePath().getLastPathComponent() );
                notifyNodeNavigatorListeners();
            } else {
                // don't get excited and force a relayout unless the parent of the deleted
                // node is the current group
                final var myPath = new TreePath(myNode.getPath() );
                if ( myPath.equals( treeModelEvent.getTreePath() ) ) {
                    LOGGER.log( Level.FINE, "Children were removed from the current node. We must therefore relayout the children; myPath: {0}, lastPathComponent: [{1}]", new Object[]{myPath, treeModelEvent.getTreePath().getLastPathComponent()} );
                    notifyNodeNavigatorListeners();
                }
            }
        }

        /**
         * This method is defined by the TreeModelListener interface and gives
         * the GroupNavigator a notification if there was a massive structure
         * change in the tree. In this event all laying out shall stop and the
         * group should be laid out from scratch.
         *
         * @param treeModelEvent The event
         */
        @Override
        public void treeStructureChanged(final TreeModelEvent treeModelEvent) {
            LOGGER.log(Level.FINE, "We''ve teen told that the Tree structure changed Event: {0}", treeModelEvent);
            if (myNode == null) {
                return;
            }
            if (myNode.isNodeDescendant((DefaultMutableTreeNode) treeModelEvent.getTreePath().getLastPathComponent())) {
                notifyNodeNavigatorListeners();
            }
        }
    }
}
