package org.jpo.datamodel;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2006-2024 Richard Eigenmann, Zürich, Switzerland
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
 * Converts a tree of notes into a flat list of pictures
 */
public class FlatGroupNavigator
        extends ListNavigator {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(FlatGroupNavigator.class.getName());

    /**
     * Constructor for a FlatGroupNavigator.
     *
     * @param groupNode The groupNode under which the pictures should be
     *                  displayed.
     */
    public FlatGroupNavigator(final SortableDefaultMutableTreeNode groupNode) {
        super( groupNode.getChildPictureNodes(true) );
        this.groupNode = groupNode;
        final var pictureCollection = groupNode.getPictureCollection();
        if (pictureCollection != null) {
            pictureCollection.addTreeModelListener(new MyTreeModelListener());
        }
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
        if ((groupNode != null) && (groupNode.getUserObject() instanceof GroupInfo groupInfo)) {
            return groupInfo.getGroupName();
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
        public void treeNodesChanged(final TreeModelEvent e) {
            // Not interested in this event
        }

        /**
         * We are notified here that a node was inserted
         *
         * @param e The event that we will be notified on
         */
        @Override
        public void treeNodesInserted(final TreeModelEvent e) {
            // Not interested in this event
        }

        /**
         * The TreeModelListener interface tells us of tree node removal events.
         * If we receive a removal event we need to find out if one of our nodes
         * was removed
         *
         * @param e The Notification event
         */
        @Override
        public void treeNodesRemoved(final TreeModelEvent e) {
            LOGGER.log(Level.INFO, "Investigating a remove event: {0}", e);

            // Problem here is that if the current node was removed we are no longer on the node that was removed
            final var currentNodeTreePath = new TreePath(groupNode.getPath());
            LOGGER.log(Level.INFO, "The current group node has this path: {0}", currentNodeTreePath);

            for (final var child : e.getChildren()) {
                final TreePath removedChild = new TreePath(child);
                LOGGER.log(Level.INFO, "Deleted child [{0}] has path: {1}", new Object[]{child, removedChild});

                if (removedChild.isDescendant(currentNodeTreePath)) {
                    LOGGER.info("Oh dear, our group has just disappeared.");
                    clear();
                    notifyNodeNavigatorListeners();
                    return; // no point in continuing the loop; the group is gone.
                }

                final var parentOfRemoved = e.getTreePath();
                if (currentNodeTreePath.equals(parentOfRemoved)) {
                    int[] childIndices = e.getChildIndices();
                    int myNodeCount = getNumberOfNodes();
                    LOGGER.log(Level.INFO, "The removed {0} node(s) are children of the current group (which has {1} nodes)", new Object[]{childIndices.length, myNodeCount});
                    buildFromScratch();
                    notifyNodeNavigatorListeners();
                    // TODO: How to tell a Picture Viewer that the picture it is showing has moved to up an index position?
                }
            }
        }

        /**
         * Builds the list of nodes from the group.
         */
        private void buildFromScratch() {
            clear();
            add(groupNode.getChildPictureNodes(true));
        }


        /**
         * We are notified here if the structure changed
         *
         * @param e The event
         */
        @Override
        public void treeStructureChanged(final TreeModelEvent e) {
            // Not interested in this event
        }
    }
}
