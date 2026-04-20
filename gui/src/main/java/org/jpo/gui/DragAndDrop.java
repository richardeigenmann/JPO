package org.jpo.gui;

import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
 Copyright (C) 2025 Richard Eigenmann.
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
 * Handle the DragAndDrop of Nodes in the GUI
 */
public class DragAndDrop {

    private static final Logger LOGGER = Logger.getLogger(DragAndDrop.class.getName());

    /**
     * This method is called by the drop method of the DragTarget to do the
     * move. It deals with the intricacies of the drop event and handles all the
     * moving, cloning and positioning that is required.
     *
     * #param node The node onto which the drop occurred
     * @param dropEvent The event the listening object received.
     */
    public static void executeDrop(final SortableDefaultMutableTreeNode node, final DropTargetDropEvent dropEvent) {
        LOGGER.log(Level.INFO, "Data Flavours: [{0}]", dropEvent.getCurrentDataFlavorsAsList()
                .stream()
                .map(n -> n.getClass().toString())
                .collect(Collectors.joining(", ")));
        if (!isExecuteDropOk(dropEvent)) {
            LOGGER.info("Can't accept drop, rejecting drop event");
            dropEvent.rejectDrop();
            dropEvent.dropComplete(false);
            return;
        }

        final List<SortableDefaultMutableTreeNode> transferableNodes;
        try {
            transferableNodes = extractTransferableNodes(dropEvent);
        } catch (final UnsupportedFlavorException | IOException | ClassCastException ex) {
            LOGGER.log(Level.INFO, "Error while collecting the transferables. Exception: {0}", ex.getMessage());
            dropEvent.dropComplete(false);
            return;
        }

        if (node.containsAnAncestor(transferableNodes)) {
            LOGGER.log(Level.INFO, """
                    One of the transferring nodes is an ancestor of the current node which would orphan the tree.
                    """);
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    JpoResources.getResource("moveNodeError"),
                    JpoResources.getResource("genericError"),
                    JOptionPane.ERROR_MESSAGE);
            dropEvent.dropComplete(false);

            return;
        }

        memorizeGroupOfDropLocation(node);

        for (final SortableDefaultMutableTreeNode sourceNode : transferableNodes) {
            if ((sourceNode.getUserObject() instanceof PictureInfo) && (node.getUserObject() instanceof GroupInfo)) {
                dropPictureOnGroup(dropEvent, sourceNode, node);
            } else if ((sourceNode.getUserObject() instanceof PictureInfo) && (node.getUserObject() instanceof PictureInfo)) {
                dropPictureOnPicture(dropEvent, sourceNode, node);
            } else {
                dropGroupOnGroup(dropEvent, sourceNode, node);
            }
            node.getPictureCollection().setUnsavedUpdates();
        }

    }

    /**
     * This method memorizes the group associated with the supplied node in the
     * Settings object. If the supplied node is not a group its parent which
     * must be a group is memorized.
     *
     * @param node the node to memorize
     */
    public static void memorizeGroupOfDropLocation(final SortableDefaultMutableTreeNode node) {
        if (node.getUserObject() instanceof GroupInfo) {
            Settings.memorizeGroupOfDropLocation(node);
        } else {
            final SortableDefaultMutableTreeNode parent = node.getParent();
            if ((parent != null) && (parent.getUserObject() instanceof GroupInfo)) {
                Settings.memorizeGroupOfDropLocation(parent);
            } else {
                LOGGER.info("Failed to find the group of the drop location. Not memorizing in settings.");
            }
        }
    }

    /**
     * Checks if the DropTargetDropEvent is suitable. Presently we can only deal
     * with local transfers of JpoTransferables
     *
     * @param event The drop event
     * @return true if acceptable, false if not
     */
    public static boolean isExecuteDropOk(final DropTargetDropEvent event) {
        if (!event.isLocalTransfer()) {
            LOGGER.info("The drop is not a local Transfer. These are not supported. Aborting drop.");
            event.rejectDrop();
            event.dropComplete(false);
            return false;
        } else {
            LOGGER.info("The drop is a local Transfer.");
        }

        if (!event.isDataFlavorSupported(JpoTransferable.jpoNodeFlavor)) {
            LOGGER.info("The drop doesn't have a JpoTransferable.jpoNodeFlavor. Drop rejected.");
            event.rejectDrop();
            event.dropComplete(false);
            return false;
        } else {
            LOGGER.info("The drop is for a JpoTransferable.jpoNodeFlavor");
        }

        int actionType = event.getDropAction();
        if ((actionType == DnDConstants.ACTION_MOVE) || (actionType == DnDConstants.ACTION_COPY)) {
            event.acceptDrop(actionType);   // crucial Step!
        } else {
            LOGGER.info("The event has an odd Action Type. Drop rejected.");
            event.rejectDrop();
            event.dropComplete(false);
            return false;
        }
        return true;
    }


    /**
     * Extract the transferable nodes from the drop event
     *
     * @param event the drop event
     * @return a list of the transferable nodes
     * @throws UnsupportedFlavorException when a bad transferable is received
     * @throws IOException                when an IO error occurs
     */
    @NotNull
    public static List<SortableDefaultMutableTreeNode> extractTransferableNodes(final DropTargetDropEvent event)
            throws UnsupportedFlavorException, IOException {
        final Transferable t = event.getTransferable();
        final Object o = t.getTransferData(JpoTransferable.jpoNodeFlavor);
        return ((List<?>) o).stream()
                .filter(SortableDefaultMutableTreeNode.class::isInstance)
                .map(SortableDefaultMutableTreeNode.class::cast)
                .toList();
    }



    private static void dropGroupOnGroup(final DropTargetDropEvent dropEvent, final SortableDefaultMutableTreeNode sourceNode, final SortableDefaultMutableTreeNode targetNode) {
        LOGGER.log(Level.INFO, "Dropping Group node {0} onto Group node {1}", new Object[]{sourceNode, targetNode});
        if (!targetNode.isRoot()) {
            final GroupDropPopupMenu groupDropPopupMenu = new GroupDropPopupMenu(dropEvent, sourceNode, targetNode);
            groupDropPopupMenu.show(dropEvent.getDropTargetContext().getDropTarget().getComponent(), dropEvent.getLocation().x, dropEvent.getLocation().y);
        } else {
            // Group was dropped on the root node --> add at first place.
            sourceNode.removeFromParent();
            targetNode.insert(sourceNode, 0);
            dropEvent.dropComplete(true);

        }
    }

    private static void dropPictureOnPicture(final DropTargetDropEvent event, final SortableDefaultMutableTreeNode sourceNode, final SortableDefaultMutableTreeNode targetNode) {
        // a picture is being dropped onto a picture and should be inserted before the target node
        final SortableDefaultMutableTreeNode parentNode = targetNode.getParent();
        if (event.getDropAction() == DnDConstants.ACTION_MOVE) {
            LOGGER.log(Level.INFO, "Moving Picture node {0} before Picture node {1}", new Object[]{sourceNode, targetNode});
            sourceNode.removeFromParent();
            int indexPosition = parentNode.getIndex(targetNode);
            parentNode.insert(sourceNode, indexPosition);
        } else {
            LOGGER.log(Level.INFO, "Copying Picture node {0} before Picture node {1}", new Object[]{sourceNode, targetNode});
            final SortableDefaultMutableTreeNode newNode = new SortableDefaultMutableTreeNode(((PictureInfo) sourceNode.getUserObject()).getClone());
            int indexPosition = parentNode.getIndex(targetNode);
            parentNode.insert(newNode, indexPosition);
        }
        event.dropComplete(true);
    }

    private static void dropPictureOnGroup(final DropTargetDropEvent event, final SortableDefaultMutableTreeNode sourceNode, final SortableDefaultMutableTreeNode targetNode) {
        // a picture is being dropped onto a group; add it at the end
        if (event.getDropAction() == DnDConstants.ACTION_MOVE) {
            LOGGER.log(Level.INFO, "Moving Picture node {0} onto last position in Group node {1}", new Object[]{sourceNode, targetNode});
            sourceNode.moveToLastChild(targetNode);
        } else {
            LOGGER.log(Level.INFO, "Cloning Picture node {0} onto last position in Group node {1}", new Object[]{sourceNode, targetNode});
            SortableDefaultMutableTreeNode newNode = new SortableDefaultMutableTreeNode(((PictureInfo) sourceNode.getUserObject()).getClone());
            targetNode.add(newNode);
        }
        event.dropComplete(true);
    }

    /**
     * This inner class creates a popup menu for group drop events to find out
     * whether to drop into before or after the drop node.
     */
    static class GroupDropPopupMenu
            extends JPopupMenu {

        /**
         * This inner class creates a popup menu for group drop events to find
         * out whether to drop into before or after the drop node.
         *
         * @param event      The event
         * @param sourceNode the source node
         * @param targetNode the target node
         */
        private GroupDropPopupMenu(final DropTargetDropEvent event,
                                   final SortableDefaultMutableTreeNode sourceNode,
                                   final SortableDefaultMutableTreeNode targetNode) {

            // menu item that allows the user to edit the group description
            final JMenuItem dropBefore = new JMenuItem(JpoResources.getResource("GDPMdropBefore"));
            dropBefore.addActionListener((ActionEvent e) -> {
                final SortableDefaultMutableTreeNode parentNode = targetNode.getParent();
                sourceNode.removeFromParent();
                int currentIndex = parentNode.getIndex(targetNode);
                parentNode.insert(sourceNode, currentIndex);
                event.dropComplete(true);
                targetNode.getPictureCollection().setUnsavedUpdates();
            });
            super.add(dropBefore);

            // menu item that allows the user to edit the group description
            final JMenuItem dropAfter = new JMenuItem(JpoResources.getResource("GDPMdropAfter"));
            dropAfter.addActionListener((ActionEvent e) -> {
                final SortableDefaultMutableTreeNode parentNode = targetNode.getParent();
                sourceNode.removeFromParent();
                int currentIndex = parentNode.getIndex(targetNode);
                parentNode.insert(sourceNode, currentIndex + 1);
                event.dropComplete(true);
                targetNode.getPictureCollection().setUnsavedUpdates();
            });
            super.add(dropAfter);

            // menu item that allows the user to edit the group description
            final JMenuItem dropIntoFirst = new JMenuItem(JpoResources.getResource("GDPMdropIntoFirst"));
            dropIntoFirst.addActionListener((ActionEvent e) -> {
                synchronized (targetNode.getRoot()) {
                    sourceNode.removeFromParent();
                    targetNode.insert(sourceNode, 0);
                }
                event.dropComplete(true);
                targetNode.getPictureCollection().setUnsavedUpdates();
            });
            super.add(dropIntoFirst);

            // menu item that allows the user to edit the group description
            final JMenuItem dropIntoLast = new JMenuItem(JpoResources.getResource("GDPMdropIntoLast"));
            dropIntoLast.addActionListener((ActionEvent e) -> {
                synchronized (targetNode.getRoot()) {
                    sourceNode.removeFromParent();
                    int childCount = targetNode.getChildCount();
                    targetNode.insert(sourceNode, childCount);
                }
                event.dropComplete(true);
                targetNode.getPictureCollection().setUnsavedUpdates();
            });
            super.add(dropIntoLast);

            // menu item that allows the user to edit the group description
            final JMenuItem dropCancel = new JMenuItem(JpoResources.getResource("GDPMdropCancel"));
            dropCancel.addActionListener((ActionEvent e) -> {
                LOGGER.info("cancel drop");
                event.dropComplete(false);
            });
            super.add(dropCancel);
        }
    }


}
