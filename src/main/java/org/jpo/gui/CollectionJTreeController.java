package org.jpo.gui;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.jpo.datamodel.*;
import org.jpo.eventbus.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright (C) 2002-2024 Richard Eigenmann, Zurich, Switzerland This
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

        collectionJTree.putClientProperty("JTree.lineStyle", "Angled");
        collectionJTree.setShowsRootHandles(true);
        collectionJTree.setOpaque(true);
        collectionJTree.setBackground(Settings.getJpoBackgroundColor());
        collectionJTree.setMinimumSize(Settings.JPO_NAVIGATOR_JTABBEDPANE_MINIMUM_SIZE);
        collectionJTree.setEditable(true);
        collectionJTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        collectionJTree.setCellRenderer(getRenderer());
        collectionJTree.setCellEditor(getTreeCellEditor());

        collectionJScrollPane.setMinimumSize(Settings.JPO_NAVIGATOR_JTABBEDPANE_MINIMUM_SIZE);
        collectionJScrollPane.setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );

        collectionJTree.addMouseListener(new CollectionJTreeMouseAdapter());
        JpoEventBus.getInstance().register(this);
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


    /**
     * A helper method that returns us the selected node on the tree if we have a selection, and it is
     * pointing to a GroupInfo or a PictureInfo object.
     * @param component The component that we want to check
     * @return the node if we have one.
     */
    public static Optional<SortableDefaultMutableTreeNode> getSelectedNode(final JComponent component) {
        if ( component instanceof JTree collectionJTree) {
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
                final var node = selectedNode.get();
                if ( node.isRoot() ) {
                    return NONE;
                }
                final var userObject = node.getUserObject();
                if ( userObject instanceof  GroupInfo ) {
                    return MOVE;
                } else if ( userObject instanceof PictureInfo ) {
                    return COPY_OR_MOVE;
                } else {
                    LOGGER.log(Level.INFO, "Odd userobject - can''t support drag from object: {0}", userObject);
                    return NONE;
                }
            }
            LOGGER.log(Level.FINE, "Can''t support drag from component {0}", component);
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
            LOGGER.log(Level.FINE, "Created a Transferable from node {0}", selectedNode);
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
            if (! support.isDataFlavorSupported(JpoTransferable.jpoNodeFlavor)) {
                LOGGER.log(Level.INFO, "Can''t drop as we only accept drops of JpoTransferables here");
                return false;
            }

            final var closestNode = getClosestTargetNode(support);
            if ( closestNode.containsAnAncestor(getTransferableNodes(support.getTransferable())) ) {
                LOGGER.log(Level.FINE, "Cant allow drop on node {0} because of ancestor violation", closestNode);
                return false;
            }

            return true;
        }

        private SortableDefaultMutableTreeNode getClosestTargetNode(TransferSupport support) {
            final var dropLocation = (JTree.DropLocation) support.getDropLocation();
            return (SortableDefaultMutableTreeNode) dropLocation.getPath().getLastPathComponent();
        }


        @NonNull
        private Collection<SortableDefaultMutableTreeNode> getTransferableNodes(final Transferable transferable) {
            try {
                return (Collection<SortableDefaultMutableTreeNode>) transferable.getTransferData(JpoTransferable.jpoNodeFlavor);
            } catch (final UnsupportedFlavorException | ClassCastException | IOException x) {
                LOGGER.log(Level.SEVERE, x.getMessage());
            }
            return new ArrayList<>();
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
         * This method is called on a successful drop and initiates
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

            // see the documentation for JTree.DropLocation; if the childIndex is -1 the drop occurred on the node.
            // if it has a value, this is the index position between the nodes
            final var dropLocation = (JTree.DropLocation) support.getDropLocation();
            final var targetNode = getClosestTargetNode(support);
            LOGGER.log(Level.FINE, "Choosing node {0} as target based on path {1}, with ChildIndex: {2}", new Object[]{targetNode, dropLocation.getPath(), dropLocation.getChildIndex()});

            final var transferableNodes = getTransferableNodes(support.getTransferable());
            if (targetNode. containsAnAncestor(transferableNodes)) {
                LOGGER.log(Level.SEVERE, "The drop operation on node {0} could not be completed because one of the dropped nodes was an ancestor", targetNode);
                return false;
            }

            memorizeGroupOfDropLocation(targetNode);
            transferableNodes.forEach(sourceNode -> {
                if (actionType == TransferHandler.MOVE) {
                    dndMoveNode(dropLocation, sourceNode);
                } else {
                    importNodeCopy(dropLocation, sourceNode);
                }
            });
            return true;
        }

        private void importNodeCopy(final JTree.DropLocation dropLocation, final SortableDefaultMutableTreeNode sourceNode) {
            final var targetNode = (SortableDefaultMutableTreeNode) dropLocation.getPath().getLastPathComponent();
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

        private void dndMoveNode(final JTree.DropLocation dropLocation, final SortableDefaultMutableTreeNode sourceNode) {
            final var targetNode = (SortableDefaultMutableTreeNode) dropLocation.getPath().getLastPathComponent();
            LOGGER.log(Level.FINE, "DND Moving source node {0} to target Node {1}, dropLocation path: {2}, dropLocation.childIndex is {3}", new Object[] {sourceNode, targetNode, dropLocation.getPath(), dropLocation.getChildIndex()});
            if (dropLocation.getChildIndex() == -1) {
                LOGGER.log(Level.FINE, "This is a drop onto the target node {0}", targetNode);
                // i.e. drop ON the node
                if (targetNode.getUserObject() instanceof GroupInfo) {
                    // append to end of group if dropping on a group node
                    sourceNode.moveToLastChild(targetNode);
                } else {
                    // dropping on a PictureInfo
                    sourceNode.moveBefore(targetNode);
                }
            } else {
                LOGGER.log(Level.FINE, "This is a next to a node. Parent: {0} Index: {1}", new Object[]{targetNode, dropLocation.getChildIndex()});
                //index was supplied by the JTree notification
                sourceNode.moveToIndex(targetNode, dropLocation.getChildIndex());
            }
        }
    }


    @TestOnly
    public JTree getCollectionJTree() {
        return collectionJTree;
    }


    /**
     * The private reference to the JTree representing the collection
     */
    private final JTree collectionJTree = new JTree() {

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
                toolTip = String.format("<html>Group: %s<br>%d nodes</html>", groupInfo.getGroupName(), node.getChildCount());
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


    @NotNull
    private static DefaultTreeCellRenderer getRenderer() {
        return new DefaultTreeCellRenderer() {

            /**
             *  Overridden method that sets the icon in the JTree to either a
             *  {@link CollectionJTreeController#CLOSED_FOLDER_ICON} or a {@link CollectionJTreeController#OPEN_FOLDER_ICON} or a
             *  {@link CollectionJTreeController#PICTURE_ICON} depending on what sort of userObject
             *  the SortableDefaultMutableTreeNode is carrying and the expansion state
             *  of the node.
             *  First we let the super implementation give us the component.
             *  Then we look at the userObject and its class and figure
             *  out what sort of icon to give it.
             */
            @Override
            public Component getTreeCellRendererComponent(
                    JTree tree,
                    Object value,
                    boolean sel,
                    boolean expanded,
                    boolean leaf,
                    int row,
                    boolean hasFocus) {
                final var userObject = ((DefaultMutableTreeNode) value).getUserObject();
                super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);
                if (userObject instanceof PictureInfo) {
                    setIcon(PICTURE_ICON);
                } else if (userObject instanceof GroupInfo) {
                    if (expanded) {
                        setIcon(OPEN_FOLDER_ICON);
                    } else {
                        setIcon(CLOSED_FOLDER_ICON);
                    }
                }
                //else let the look and feel take over

                return this;
            }
        };
    }

    private TreeCellEditor getTreeCellEditor() {
        return new DefaultTreeCellEditor(collectionJTree, getRenderer(), new DefaultCellEditor(new JTextField())) {

            /**
             * This solution to the bug 4745084 found on
             * <a href="http://forum.java.sun.com/thread.jspa?threadID=196868&start=15&tstart=0">...</a>
             * The problem is that when you hit F2 to edit the field without this override you
             * fall back on the default icon set.
             */
            @Override
            protected void determineOffset(JTree tree, Object value,
                                           boolean isSelected, boolean isExpanded, boolean isLeaf,
                                           int row) {
                super.determineOffset(tree, value, isSelected, isExpanded, isLeaf, row);
                final Component rendererComponent = super.renderer.getTreeCellRendererComponent(tree, value, isSelected, isExpanded, isLeaf, row, true);
                if (rendererComponent instanceof JLabel jLabel) {
                    super.editingIcon = (jLabel.getIcon());
                }
            }
        };
    }

    /**
     * Icon of a closed folder to be used on groups that are not expanded in the JTree.
     */
    private static final ImageIcon CLOSED_FOLDER_ICON;

    private static final String CLASSLOADER_COULD_NOT_FIND_THE_FILE_0 = "Classloader could not find the file: {0}";

    static {
        final var CLOSED_FOLDER_ICON_FILE = "icon_folder_closed.gif";
        final var resource = CollectionJTreeController.class.getClassLoader().getResource(CLOSED_FOLDER_ICON_FILE);
        if (resource == null) {
            LOGGER.log(Level.SEVERE, CLASSLOADER_COULD_NOT_FIND_THE_FILE_0, CLOSED_FOLDER_ICON_FILE);
            CLOSED_FOLDER_ICON = null;
        } else {
            CLOSED_FOLDER_ICON = new ImageIcon(resource);
        }
    }

    @TestOnly
    ImageIcon getClosedFolderIcon() {
        return CLOSED_FOLDER_ICON;
    }



    /**
     * Icon of an open folder to be used on groups that are expanded in the JTree.
     */
    private static final ImageIcon OPEN_FOLDER_ICON;

    static {
        final var OPEN_FOLDER_ICON_FILE = "icon_folder_open.gif";
        final var resource = CollectionJTreeController.class.getClassLoader().getResource(OPEN_FOLDER_ICON_FILE);
        if (resource == null) {
            LOGGER.log(Level.SEVERE, CLASSLOADER_COULD_NOT_FIND_THE_FILE_0, OPEN_FOLDER_ICON_FILE);
            OPEN_FOLDER_ICON = null;
        } else {
            OPEN_FOLDER_ICON = new ImageIcon(resource);
        }
    }

    @TestOnly
    ImageIcon getOpenFolderIcon() {
        return OPEN_FOLDER_ICON;
    }

    /**
     * Icon of a picture for use on picture bearing nodes in the JTree.
     */
    private static final ImageIcon PICTURE_ICON;
    static {
        final var PICTURE_ICON_FILE = "icon_picture.gif";
        final var resource = CollectionJTreeController.class.getClassLoader().getResource(PICTURE_ICON_FILE);
        if (resource == null) {
            LOGGER.log(Level.SEVERE, CLASSLOADER_COULD_NOT_FIND_THE_FILE_0, PICTURE_ICON_FILE);
            PICTURE_ICON = null;
        } else {
            PICTURE_ICON = new ImageIcon(resource);
        }
    }

    @TestOnly
    ImageIcon getPictureIcon() {
        return PICTURE_ICON;
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
         * Override the mousePressed event and print up the appropriate popup menu if the right mouse button was used.
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
