package org.jpo.gui;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.TestOnly;
import org.jpo.datamodel.*;
import org.jpo.eventbus.*;
import org.jpo.gui.swing.CollectionJTree;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright (C) 2002 - 2023 Richard Eigenmann, Zurich, Switzerland This
 * program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA. The license is in gpl.txt. See
 * http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 * This class deals with the tree representation of the picture collection
 */
public class CollectionJTreeController {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( CollectionJTreeController.class.getName() );

    /**
     * The Controller class for the tree representation of the picture collection.
     *
     * @param pictureCollection the PictureCollection to control
     */
    public CollectionJTreeController(final PictureCollection pictureCollection) {
        if (pictureCollection != null) {
            collectionJTree.setModel(pictureCollection.getTreeModel());
        }
        collectionJTree.setTransferHandler(new MyTransferHandler());
        ToolTipManager.sharedInstance().registerComponent(collectionJTree);
        collectionJTree.setDragEnabled(true);
        collectionJTree.setDropMode(DropMode.ON_OR_INSERT);

        collectionJScrollPane.setMinimumSize(Settings.JPO_NAVIGATOR_JTABBEDPANE_MINIMUM_SIZE);
        collectionJScrollPane.setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );

        final var mouseAdapter = new CollectionJTreeMouseAdapter();
        collectionJTree.addMouseListener( mouseAdapter );
        registerOnEventBus();
    }

    private void registerOnEventBus() {
        JpoEventBus.getInstance().register( this );
    }

    /**
     * When the tree receives a ShowGroupRequest it position the selection on the node.
     *
     * @param request The ShowGroupRequest
     */
    @Subscribe
    public void handleShowGroupRequest(final ShowGroupRequest request) {
        setSelection(request.node());
    }

    /**
     * Sets the tree selection on the supplied node. Will also scroll the tree so that the selected node is
     * visible. It used to also expand the node but that was wierd. If the user wants to expand she can
     * click on the + sign next to the node.
     *
     * @param node The node to select in the tree
     */
    private void setSelection(final SortableDefaultMutableTreeNode node) {
        final var treePath = new TreePath(node.getPath());
        final Runnable r = () -> {
            collectionJTree.scrollPathToVisible(treePath);
            collectionJTree.setSelectionPath(treePath);
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    private static boolean ancestorViolationCheckWithAlert(final SortableDefaultMutableTreeNode targetNode, final Collection<SortableDefaultMutableTreeNode> transferableNodes) {
        if ( targetNode.containsAnAncestor(transferableNodes) ) {
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("moveNodeError"),
                    Settings.getJpoResources().getString("genericError"),
                    JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * A helper method that returns us the selected node on the tree if we have a selection and it is
     * pointing to a GroupInfo or a PictureInfo object.
     * @param component The component that we want to check
     * @return the node if we have one.
     */
    public static Optional<SortableDefaultMutableTreeNode> getSelectedNode(final JComponent component) {
        if ( component instanceof CollectionJTree collectionJTree) {
            final var selectionPath = collectionJTree.getSelectionPath();
            if ( selectionPath == null ) {
                return Optional.empty();
            }
            final var lastPathComponent = selectionPath.getLastPathComponent();
            if ( lastPathComponent instanceof SortableDefaultMutableTreeNode node
                && (node.getUserObject() instanceof GroupInfo || node.getUserObject() instanceof PictureInfo)) {
                    return Optional.of(node);
            }
        }
        return Optional.empty();
    }


    private class MyTransferHandler
            extends TransferHandler {

        /**
         * This method tells the DND system what kind of actions the source of the DND can support.
         * GroupInfos can only be moved. PictureInfos can be moved or copied.
         *
         * @param component the Object to query
         * @return COPY_OR_MOVE for this TransferHandler
         */
        @Override
        public int getSourceActions( final JComponent component ) {
            final var selectedNode = CollectionJTreeController.getSelectedNode(component);
            if ( selectedNode.isPresent() ) {
                LOGGER.log(Level.INFO, "Allowing drag from node {0}", selectedNode.get());
                final var userObject = selectedNode.get().getUserObject();
                if ( userObject instanceof  GroupInfo ) {
                    return MOVE;
                } else if ( userObject instanceof PictureInfo ) {
                    return COPY_OR_MOVE;
                } else {
                    LOGGER.log(Level.INFO, "Odd userobject - can''t support drag from if: {0}", userObject);
                    return NONE;
                }
            }
            LOGGER.log(Level.INFO, "Can''t support drag from component {0}", component);
            return NONE;
        }

        /**
         * This method bundles up the data to be exported into a Transferable
         * object in preparation for the transfer.
         *
         * @param component The component
         * @return a transferable
         */
        @Override
        protected Transferable createTransferable(final JComponent component) {
            final var selectedNode = CollectionJTreeController.getSelectedNode(component);
            if (selectedNode.isEmpty()) {
                return null;
            }
            return new JpoTransferable(List.of(selectedNode.get()));
        }

        /**
         * This method is called repeatedly during a drag gesture and returns
         * true if the area below the cursor can accept the transfer, or false
         * if the transfer will be rejected.
         *
         * @param support The transfer support object
         * @return true if the import is OK
         */
        @Override
        public boolean canImport(final TransferSupport support) {
            LOGGER.log(Level.INFO, "canImport fired on {0}", support);
            if (! support.isDataFlavorSupported(JpoTransferable.jpoNodeFlavor)) {
                LOGGER.log(Level.INFO, "Only drops of JpoTransferables are supported");
                return false;
            }

            final var dropLocation = (JTree.DropLocation) support.getDropLocation();
            final var treePath = dropLocation.getPath();
            final var closestNode = (SortableDefaultMutableTreeNode) treePath.getLastPathComponent();

            if ( closestNode.containsAnAncestor(getTransferableNodes(support.getTransferable())) ) {
                LOGGER.log(Level.INFO, "Cant allow drop on node {0} because of ancestor violation", closestNode);
                return false;
            }

            return true;
        }


        @NonNull
        private Collection<SortableDefaultMutableTreeNode> getTransferableNodes(final Transferable transferable) {
            Collection<SortableDefaultMutableTreeNode> transferableNodes = new ArrayList<>();
            try {
                final var transferData = transferable.getTransferData(JpoTransferable.jpoNodeFlavor);
                transferableNodes = (List<SortableDefaultMutableTreeNode>) transferData;
                LOGGER.log(Level.INFO, "processing a list with {0} transferable nodes", transferableNodes.size());
            } catch (final UnsupportedFlavorException | ClassCastException | IOException x) {
                LOGGER.log(Level.SEVERE, x.getMessage());
            }
            return transferableNodes;
        }


        private void memorizeGroupOfDropLocation(final SortableDefaultMutableTreeNode targetNode) {
            SortableDefaultMutableTreeNode groupOfDropLocation;
            if (targetNode.getUserObject() instanceof GroupInfo) {
                groupOfDropLocation = targetNode;
            } else {
                // the parent must be a group node
                groupOfDropLocation = targetNode.getParent();
            }
            if ((groupOfDropLocation != null) && (groupOfDropLocation.getUserObject() instanceof GroupInfo)) {
                Settings.memorizeGroupOfDropLocation(groupOfDropLocation);
                JpoEventBus.getInstance().post(new RecentDropNodesChangedEvent());
            } else {
                LOGGER.info("Failed to find the group of the drop location. Not memorizing.");
            }
        }

        /**
         * This method is called on a successful drop (or paste) and initiates
         * the transfer of data to the target component. This method returns
         * true if the import was successful and false otherwise.
         *
         * @param support the TransferSupport
         * @return true if successful
         */
        @Override
        public boolean importData(final TransferSupport support) {
            final var actionType = support.getDropAction();
            if (!((actionType == TransferHandler.COPY) || (actionType == TransferHandler.MOVE))) {
                LOGGER.log(Level.INFO, "The event has an odd Action Type: {0}. Drop rejected. Copy is {1}, Move is {2}", new Object[]{actionType, TransferHandler.COPY, TransferHandler.MOVE});
                return false;
            }

            final var dropLocation = (JTree.DropLocation) support.getDropLocation();
            final var targetNode = (SortableDefaultMutableTreeNode) dropLocation.getPath().getLastPathComponent();
            LOGGER.log(Level.INFO, "Choosing node {0} as target for path {1}, ChildIndex: {2}", new Object[]{targetNode, dropLocation.getPath(), dropLocation.getChildIndex()});


            final var transferableNodes = getTransferableNodes(support.getTransferable());
            if (ancestorViolationCheckWithAlert(targetNode, transferableNodes)) return false;

            memorizeGroupOfDropLocation(targetNode);
            transferableNodes.forEach(sourceNode -> importNode(actionType, dropLocation, targetNode, sourceNode));
            return true;
        }

        private void importNode(int actionType, JTree.DropLocation dropLocation, SortableDefaultMutableTreeNode targetNode, SortableDefaultMutableTreeNode sourceNode) {
            if (actionType == TransferHandler.MOVE) {
                importNodeMove(dropLocation, targetNode, sourceNode);
            } else {
                importNodeCopy(dropLocation, targetNode, sourceNode);
            }
        }

        private void importNodeCopy(JTree.DropLocation dropLocation, SortableDefaultMutableTreeNode targetNode, SortableDefaultMutableTreeNode sourceNode) {
            SortableDefaultMutableTreeNode cloneNode = sourceNode.getClone();
            if (dropLocation.getChildIndex() == -1) {
                if (targetNode.getUserObject() instanceof GroupInfo) {
                    targetNode.add(cloneNode);
                } else {
                    // dropping onto a picture
                    cloneNode.moveBefore(targetNode);
                }
            } else {
                cloneNode.moveToIndex(targetNode, dropLocation.getChildIndex());
            }
        }

        private void importNodeMove(JTree.DropLocation dropLocation, SortableDefaultMutableTreeNode targetNode, SortableDefaultMutableTreeNode sourceNode) {
            LOGGER.log(Level.INFO, "Processing a MOVE on node: {0}", sourceNode);
            if (dropLocation.getChildIndex() == -1) {
                if (targetNode.getUserObject() instanceof GroupInfo) {
                    // append to end of group if dropping on a group node
                    sourceNode.moveToLastChild(targetNode);
                } else {
                    // dropping on a PictureInfo
                    sourceNode.moveBefore(targetNode);
                }
            } else {
                //index was supplied by the JTree notification
                sourceNode.moveToIndex(targetNode, dropLocation.getChildIndex());
            }
        }
    }


    @TestOnly
    public CollectionJTree getCollectionJTree() {
        return (CollectionJTree) collectionJTree;
    }


    /**
     * The private reference to the JTree representing the collection
     */
    private final JTree collectionJTree = new CollectionJTree() {

        @Override
        public String getToolTipText(MouseEvent mouseEvent) {
            if (collectionJTree.getRowForLocation(mouseEvent.getX(), mouseEvent.getY()) == -1) {
                return null;
            }
            final var curPath = collectionJTree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
            final var node = (SortableDefaultMutableTreeNode) Objects.requireNonNull(curPath).getLastPathComponent();
            final var userObject = node.getUserObject();
            var toolTip = "";
            if (userObject instanceof GroupInfo groupInfo) {
                toolTip = String.format("<html>Group: %s</html>", groupInfo.getGroupName());
            } else if (userObject instanceof PictureInfo pictureInfo) {
                final var highresFile = pictureInfo.getImageFile();
                final var fileSize = highresFile == null ? "no file" : FileUtils.byteCountToDisplaySize(highresFile.length());
                toolTip = String.format("<html>Picture: %s<br>%s %s</html>", pictureInfo.getDescription(), Settings.getJpoResources().getString("CollectionSizeJLabel"), fileSize);
            }
            return toolTip;
        }
    };


    /**
     * The private reference to the JScrollPane that holds the JTree.
     */
    private final JScrollPane collectionJScrollPane = new JScrollPane( collectionJTree );

    /**
     * Returns the JScrollPane that holds the JTree.
     *
     * @return the JScrollPane that holds the tree
     */
    public JScrollPane getJScrollPane() {
        return collectionJScrollPane;
    }

    /**
     * This class decides what to do with mouse events on the JTree.
     */
    private static class CollectionJTreeMouseAdapter
            extends MouseAdapter {

        /**
         * Handle click events on the tree. Find out what node was clicked.
         * If it was a single click on a group show the group.
         * If it was multi-click open the (first) picture.
         */
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            final var clickNode = getTreeNodeFromMouseEvent(mouseEvent);
            clickNode.ifPresent( node ->  {
                if (mouseEvent.getClickCount() == 1 && (!mouseEvent.isPopupTrigger()) && node.getUserObject() instanceof GroupInfo) {
                    JpoEventBus.getInstance().post(new ShowGroupRequest(node));
                } else if (mouseEvent.getClickCount() > 1 && (!mouseEvent.isPopupTrigger())) {
                    final var groupNavigator = new FlatGroupNavigator(node.getParent());
                    JpoEventBus.getInstance().post(new ShowPictureRequest(groupNavigator, node.getParent().getIndex(node)));
                }
            });
        }

        private static Optional<SortableDefaultMutableTreeNode> getTreeNodeFromMouseEvent(final MouseEvent mouseEvent) {
            final var clickPath = ((JTree) mouseEvent.getSource()).getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
            if (clickPath == null) {
                return Optional.empty();
            }
            final var clickNode = (SortableDefaultMutableTreeNode) clickPath.getLastPathComponent();
            return Optional.of(clickNode);
        }

        /**
         * Override the mousePressed event.
         */
        @Override
        public void mousePressed( MouseEvent mouseEvent ) {
            if (mouseEvent.isPopupTrigger()) {
                final var clickNode = getTreeNodeFromMouseEvent(mouseEvent);
                clickNode.ifPresent( node -> {
                    final var nodeInfo = node.getUserObject();

                    if (nodeInfo instanceof GroupInfo) {
                        JpoEventBus.getInstance().post(new ShowGroupPopUpMenuRequest(node, mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY()));
                    } else if (nodeInfo instanceof PictureInfo) {
                        final var groupNavigator = new GroupNavigator( node.getParent() );
                        final var index = node.getParent().getIndex(node);
                        JpoEventBus.getInstance().post(new ShowPicturePopUpMenuRequest(groupNavigator, index, mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY()));
                    }
                });
            }
        }


    }
}
