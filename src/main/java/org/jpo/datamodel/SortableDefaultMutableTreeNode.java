package org.jpo.datamodel;

import org.apache.commons.io.FilenameUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.Settings.FieldCodes;
import org.jpo.gui.JpoTransferable;
import org.jpo.gui.SourcePicture;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.jpo.datamodel.Tools.copyBufferedStream;


/*
 Copyright (C) 2003 - 2020  Richard Eigenmann, Zurich, Switzerland
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
 * This is the main data structure object for the JPO Collection. Holds a
 * reference to either a PictureInfo or GroupInfo object in its getUserObject.
 * <p>
 * It extends the DefaultMutableTreeNode with the Comparable Interface that
 * allows our nodes to be compared.
 */
public class SortableDefaultMutableTreeNode
        extends DefaultMutableTreeNode
        implements Comparable<SortableDefaultMutableTreeNode>, PictureInfoChangeListener {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(SortableDefaultMutableTreeNode.class.getName());

    /**
     * Constructor for a new node.
     */
    public SortableDefaultMutableTreeNode() {
        super();
    }

    /**
     * Constructor for a new node.
     *
     * @param userObject User Object
     */
    public SortableDefaultMutableTreeNode(GroupInfo userObject) {
        this((Object) userObject);
    }

    /**
     * Constructor for a new node.
     *
     * @param userObject User Object
     */
    public SortableDefaultMutableTreeNode(PictureInfo userObject) {
        this((Object) userObject);
    }

    /**
     * Constructor for a new node including a user object. The user object must
     * be a PictureInfo or GroupInfo object. Set to private so that this constructor
     */
    private SortableDefaultMutableTreeNode(Object userObject) {
        super();
        setUserObject(userObject);
    }

    /**
     * returns the collection associated with this node
     *
     * @return the picture collection.
     */
    public PictureCollection getPictureCollection() {
        return Settings.getPictureCollection();
    }

    /**
     * Call this method to sort the Children of a node by a field.
     *
     * @param sortCriteria The criteria by which the pictures should be sorted.
     */
    public void sortChildren(final FieldCodes sortCriteria) {
        Tools.checkEDT();  // because of removeAllChildren
        synchronized (this.getRoot()) {
            int childCount = getChildCount();
            final SortableDefaultMutableTreeNode[] childNodes = new SortableDefaultMutableTreeNode[childCount];
            for (int i = 0; i < childCount; i++) {
                childNodes[i] = (SortableDefaultMutableTreeNode) getChildAt(i);
            }

            // sort the array
            sortField = sortCriteria;
            Arrays.sort(childNodes);

            //Remove all children from the node
            getPictureCollection().setSendModelUpdates(false);
            removeAllChildren();
            for (SortableDefaultMutableTreeNode childNode : childNodes) {
                add(childNode);
            }
        }
        getPictureCollection().setUnsavedUpdates();
        getPictureCollection().setSendModelUpdates(true);

        // tell the collection that the structure changed
        LOGGER.log(Level.FINE, "Sending node structure changed event on node {0} after sort", this);
        getPictureCollection().sendNodeStructureChanged(this);
    }

    /**
     * This field records the field by which the group is to be sorted. This is
     * not very elegant as a second sort could run at the same time and clobber
     * this global variable. But that's not very likely on a single user app
     * like this.
     */
    private static FieldCodes sortField;

    /**
     * Overridden method to allow sorting of nodes. It uses the static global
     * variable sortfield to figure out what to compare on.
     *
     * @param o the object to compare to
     * @return the usual compareTo value used for sorting.
     */
    @Override
    public int compareTo(@NonNull SortableDefaultMutableTreeNode o) {
        final Object myObject = getUserObject();
        final Object otherObject = o.getUserObject();

        if ((myObject instanceof GroupInfo myGi) && (otherObject instanceof GroupInfo otherGi) && (sortField == FieldCodes.DESCRIPTION)) {
            return (myGi.getGroupName().compareTo(otherGi.getGroupName()));
        }

        if ((myObject instanceof GroupInfo myGi) && (otherObject instanceof PictureInfo pi) && (sortField == FieldCodes.DESCRIPTION)) {
            return (myGi.getGroupName().compareTo(pi.getDescription()));
        }

        if ((myObject instanceof PictureInfo pi) && (otherObject instanceof GroupInfo gi) && (sortField == FieldCodes.DESCRIPTION)) {
            return (pi.getDescription().compareTo(gi.getGroupName()));
        }

        if ((myObject instanceof GroupInfo) || (otherObject instanceof GroupInfo)) {
            // we can't compare Groups against the other types of field other than the description.
            return 0;
        }

        // at this point there can only two PictureInfo Objects
        if ((myObject instanceof PictureInfo myPi) && (otherObject instanceof PictureInfo otherPi)) {
            return switch (sortField) {
                case FILM_REFERENCE -> myPi.getFilmReference().compareTo(otherPi.getFilmReference());
                case CREATION_TIME -> myPi.getCreationTime().compareTo(otherPi.getCreationTime());
                case COMMENT -> myPi.getComment().compareTo(otherPi.getComment());
                case PHOTOGRAPHER -> myPi.getPhotographer().compareTo(otherPi.getPhotographer());
                case COPYRIGHT_HOLDER -> myPi.getCopyrightHolder().compareTo(otherPi.getCopyrightHolder());
                default -> myPi.getDescription().compareTo(otherPi.getDescription());
            };
        } else {
            LOGGER.severe("We are not supposed to hit this else branch!");
            Thread.dumpStack();
            return 0;
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof SortableDefaultMutableTreeNode)) {
            return false;
        }
        return hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }


    /**
     * Returns the first node with a picture before the current one in the tree.
     * It uses the getPreviousNode method of DefaultMutableTreeNode.
     *
     * @return the first node with a picture in preorder traversal or null if
     * none found.
     */
    public SortableDefaultMutableTreeNode getPreviousPicture() {
        synchronized (this.getRoot()) {
            DefaultMutableTreeNode prevNode = getPreviousNode();
            while ((prevNode != null) && (!(prevNode.getUserObject() instanceof PictureInfo))) {
                prevNode = prevNode.getPreviousNode();
            }
            return (SortableDefaultMutableTreeNode) prevNode;
        }
    }

    /**
     * Returns the next node with a picture found after current one in the tree.
     * This can be in another Group. It uses the getNextNode method of the
     * DefaultMutableTreeNode.
     *
     * @return The SortableDefaultMutableTreeNode that represents the next
     * picture. If no picture can be found it returns null.
     */
    public SortableDefaultMutableTreeNode getNextPicture() {
        synchronized (this.getRoot()) {
            DefaultMutableTreeNode nextNode = getNextNode();
            while ((nextNode != null) && (!(nextNode.getUserObject() instanceof PictureInfo))) {
                nextNode = nextNode.getNextNode();
            }
            return (SortableDefaultMutableTreeNode) nextNode;
        }
    }

    /**
     * Returns the next node with a picture found after current one in the
     * current Group It uses the getNextSibling method of the
     * DefaultMutableTreeNode.
     *
     * @return The SortableDefaultMutableTreeNode that represents the next
     * picture. If no picture can be found it returns null.
     */
    public SortableDefaultMutableTreeNode getNextGroupPicture() {
        synchronized (this.getRoot()) {
            DefaultMutableTreeNode nextNode = getNextSibling();
            while ((nextNode != null) && (!(nextNode.getUserObject() instanceof PictureInfo))) {
                nextNode = nextNode.getNextNode();
            }
            return (SortableDefaultMutableTreeNode) nextNode;
        }
    }

    /**
     * Returns the first child node under the current node which holds a
     * PictureInfo object.
     *
     * @return The first child node holding a picture or null if none can be
     * found.
     */
    public SortableDefaultMutableTreeNode findFirstPicture() {
        SortableDefaultMutableTreeNode testNode;
        final Enumeration<TreeNode> e = children();
        while (e.hasMoreElements()) {
            testNode = (SortableDefaultMutableTreeNode) e.nextElement();
            if (testNode.getUserObject() instanceof PictureInfo) {
                return testNode;
            } else if (testNode.getUserObject() instanceof GroupInfo) {
                testNode = testNode.findFirstPicture();
                if (testNode != null) {
                    return testNode;
                }
            }
        }
        return null;
    }

    /**
     * This method collects all pictures under the current node and returns them
     * as an Array List..
     *
     * @param recursive Pass true if the method is supposed to recursively
     *                  search the subgroups, false if not
     * @return A List of child nodes that hold a picture
     */
    public List<SortableDefaultMutableTreeNode> getChildPictureNodes(
            boolean recursive) {
        final List<SortableDefaultMutableTreeNode> pictureNodes = new ArrayList<>();
        final Enumeration<TreeNode> kids = this.children();
        SortableDefaultMutableTreeNode node;

        while (kids.hasMoreElements()) {
            node = (SortableDefaultMutableTreeNode) kids.nextElement();
            if (recursive && node.getUserObject() instanceof GroupInfo) {
                pictureNodes.addAll(node.getChildPictureNodes(true));
            } else if (node.getUserObject() instanceof PictureInfo) {
                pictureNodes.add(node);
            }
        }
        return pictureNodes;
    }

    /**
     * A convenience method to tell if the current node has at least one picture
     * node in the tree of children. (Could be one)
     *
     * @return true if at least one picture is found, false if not
     */
    public boolean hasChildPictureNodes() {
        final Enumeration<TreeNode> kids = this.breadthFirstEnumeration();
        SortableDefaultMutableTreeNode node;
        while (kids.hasMoreElements()) {
            node = (SortableDefaultMutableTreeNode) kids.nextElement();
            if (node.getUserObject() instanceof PictureInfo) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method is being overridden to allow us to capture editing events on
     * the JTree that is rendering this node. The TreeCellEditor will send the
     * changed label as a String type object to the setUserObject method of this
     * class. My overriding this we can intercept this and update the
     * PictureInfo or GroupInfo accordingly.
     *
     * @param userObject The object to attach to the node
     */
    @Override
    public void setUserObject(final Object userObject) {
        if (userObject instanceof String s) {
            // this gets called when the JTree is being edited with F2
            final Object obj = getUserObject();
            if (obj instanceof GroupInfo gi) {
                gi.setGroupName(s);
            } else if (obj instanceof PictureInfo pi) {
                pi.setDescription(s);
            }
        } else if (userObject instanceof PictureInfo pictureInfo) {
            Object oldUserObject = getUserObject();
            if (oldUserObject instanceof PictureInfo oldPi) {
                oldPi.removePictureInfoChangeListener(this);
            }
            pictureInfo.addPictureInfoChangeListener(this);
            super.setUserObject(userObject);
        } else {
            // fall back on the default behaviour
            super.setUserObject(userObject);
        }
        if (getPictureCollection() != null && getPictureCollection().getSendModelUpdates()) {
            getPictureCollection().sendNodeChanged(this);
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
                .filter(element -> element instanceof SortableDefaultMutableTreeNode)
                .map(element -> (SortableDefaultMutableTreeNode) element)
                .collect(Collectors.toList());
    }

    /**
     * This method memorizes the group associated with the supplied node in the
     * Settings object. If the supplied node is not a group it's parent which
     * must be a group is memorised.
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
     * This method is called by the drop method of the DragTarget to do the
     * move. It deals with the intricacies of the drop event and handles all the
     * moving, cloning and positioning that is required.
     *
     * @param dropEvent The event the listening object received.
     */
    public void executeDrop(final DropTargetDropEvent dropEvent) {
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

        for (final SortableDefaultMutableTreeNode sourceNode : transferableNodes) {
            if (this.isNodeAncestor(sourceNode)) {
                LOGGER.log(Level.INFO, """
                        One of the transferring nodes is an ancestor of the current node which would orphan the tree.
                        Ancestor node: {0}
                        Current node: {1}""", new Object[]{sourceNode, this});
                JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                        Settings.getJpoResources().getString("moveNodeError"),
                        Settings.getJpoResources().getString("genericError"),
                        JOptionPane.ERROR_MESSAGE);
                dropEvent.dropComplete(false);
                return;
            }
        }

        memorizeGroupOfDropLocation(this);

        for (final SortableDefaultMutableTreeNode sourceNode : transferableNodes) {
            if ((sourceNode.getUserObject() instanceof PictureInfo) && (this.getUserObject() instanceof GroupInfo)) {
                dropPictureOnGroup(dropEvent, sourceNode, this);
            } else if ((sourceNode.getUserObject() instanceof PictureInfo) && (this.getUserObject() instanceof PictureInfo)) {
                dropPictureOnPicture(dropEvent, sourceNode, this);
            } else {
                dropGroupOnGroup(dropEvent, sourceNode, this);
            }
            getPictureCollection().setUnsavedUpdates();
        }

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
     * This is where the Nodes in the tree find out about changes in the
     * PictureInfo object
     *
     * @param e The event
     */
    @Override
    public void pictureInfoChangeEvent(final PictureInfoChangeEvent e) {
        LOGGER.log(Level.FINE, "The SDMTN {0} received a PictureInfoChangeEvent {1}", new Object[]{this, e});
        getPictureCollection().sendNodeChanged(this);
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
            final JMenuItem dropBefore = new JMenuItem(Settings.getJpoResources().getString("GDPMdropBefore"));
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
            final JMenuItem dropAfter = new JMenuItem(Settings.getJpoResources().getString("GDPMdropAfter"));
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
            final JMenuItem dropIntoFirst = new JMenuItem(Settings.getJpoResources().getString("GDPMdropIntoFirst"));
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
            final JMenuItem dropIntoLast = new JMenuItem(Settings.getJpoResources().getString("GDPMdropIntoLast"));
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
            final JMenuItem dropCancel = new JMenuItem(Settings.getJpoResources().getString("GDPMdropCancel"));
            dropCancel.addActionListener((ActionEvent e) -> {
                LOGGER.info("cancel drop");
                event.dropComplete(false);
            });
            super.add(dropCancel);
        }
    }

    /**
     * This method removes the designated SortableDefaultMutableTreeNode from
     * the tree. The parent node is made the currently selected node.
     */
    public void deleteNode() {
        LOGGER.log(Level.FINE, "Delete requested for node: {0}", this);
        if (this.isRoot()) {
            LOGGER.info("Delete attempted on Root node. Can't do this! Aborted.");
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("deleteRootNodeError"),
                    Settings.getJpoResources().getString("genericError"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        getPictureCollection().setUnsavedUpdates();
        synchronized (this.getRoot()) {

            final SortableDefaultMutableTreeNode parentNode = this.getParent();

            int[] childIndices = {parentNode.getIndex(this)};
            final Object[] removedChildren = {this};

            super.removeFromParent();

            if (getPictureCollection().getSendModelUpdates()) {
                LOGGER.log(Level.FINE, "Sending delete message. Model: {0} Parent: {1}, ChildIndex {2}, removedChild: {3}",
                        new Object[]{getPictureCollection().getTreeModel(), parentNode, childIndices[0], removedChildren[0]});
                getPictureCollection().sendNodesWereRemoved(parentNode, childIndices, removedChildren);
            }
        }

        final Enumeration<TreeNode> e = this.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            Settings.getRecentDropNodes().remove(e.nextElement());
        }

    }

    /**
     * Removes the node from the parent and sends a model update.
     */
    @Override
    public void removeFromParent() {
        synchronized (this.getRoot()) {

            final SortableDefaultMutableTreeNode oldParentNode = this.getParent();
            if (oldParentNode == null) {
                return;
            }
            int oldParentIndex = oldParentNode.getIndex(this);
            super.removeFromParent();

            if (getPictureCollection().getSendModelUpdates()) {
                getPictureCollection().sendNodesWereRemoved(oldParentNode,
                        new int[]{oldParentIndex},
                        new Object[]{this});
            }
        }
    }

    /**
     * Returns a new SortableDefaultTreeMode which has the same content as the
     * source node
     *
     * @return a new node which is a clone of the old one
     */
    public SortableDefaultMutableTreeNode getClone() {
        final SortableDefaultMutableTreeNode newNode = new SortableDefaultMutableTreeNode();
        if (this.getUserObject() instanceof PictureInfo pi) {
            newNode.setUserObject(pi.getClone());
        } else if (this.getUserObject() instanceof GroupInfo gi) {
            newNode.setUserObject(gi.getClone());
            final Enumeration<TreeNode> e = children();
            while (e.hasMoreElements()) {
                newNode.add(((SortableDefaultMutableTreeNode) e.nextElement()).getClone());
            }
        }
        return newNode;
    }

    /**
     * This method adds a new node to the data model of the tree. It is the
     * overridden add method which will first do the default behavior and then
     * send a notification to the Tree Model if model updates are being
     * requested. Likewise the unsaved changes of the collection are only being
     * updated when model updates are not being reported. This allows the
     * loading of collections (which of course massively change the collection
     * in memory) to report nothing changed.
     *
     * @param newNode the new node
     */
    public void add(SortableDefaultMutableTreeNode newNode) {
        synchronized (this.getRoot()) {
            super.add(newNode);
        }
        if (getPictureCollection().getSendModelUpdates()) {
            int index = this.getIndex(newNode);
            LOGGER.log(Level.FINE, "The new node {0} has index {1}", new Object[]{newNode, index});
            getPictureCollection().sendNodesWereInserted(this, new int[]{index});
            getPictureCollection().setUnsavedUpdates();
        }
    }

    /**
     * Adds a new Group to the current node with the indicated description.
     *
     * @param description Description for the group
     * @return The new node is returned for convenience.
     */
    public SortableDefaultMutableTreeNode addGroupNode(final String description) {
        synchronized (this.getRoot()) {
            final SortableDefaultMutableTreeNode newNode
                    = new SortableDefaultMutableTreeNode(
                    new GroupInfo(description));
            add(newNode);
            return newNode;
        }
    }

    /**
     * Inserts the node and notifies the tree model of changes if we are sending
     * Model updates
     *
     * @param node  The node
     * @param index the index position
     */
    public void insert(final SortableDefaultMutableTreeNode node, final int index) {
        LOGGER.log(Level.FINE, "insert was called for node: {0}", node);
        synchronized (this.getRoot()) {
            super.insert(node, index);
        }
        getPictureCollection().setUnsavedUpdates();
        if (getPictureCollection().getSendModelUpdates()) {
            getPictureCollection().sendNodesWereInserted(this, new int[]{index});
        }
    }


    /**
     * When this method is invoked on a node it is moved to the first child
     * position of it's parent node.
     */
    public void moveNodeToTop() {
        if (this.isRoot()) {
            return;  // don't do anything with a root node.
        }
        synchronized (this.getRoot()) {
            final SortableDefaultMutableTreeNode parentNode = this.getParent();
            // abort if this action was attempted on the top node
            if (parentNode.getIndex(this) < 1) {
                return;
            }
            this.removeFromParent();
            parentNode.insert(this, 0);
        }
        getPictureCollection().setUnsavedUpdates();
    }

    /**
     * When this method is invoked on a node it moves itself one position up
     * towards the first child position of it's parent node.
     */
    public void moveNodeUp() {
        if (this.isRoot()) {
            return;  // don't do anything with a root node.
        }
        synchronized (this.getRoot()) {
            SortableDefaultMutableTreeNode parentNode = this.getParent();
            int currentIndex = parentNode.getIndex(this);
            // abort if this action was attempted on the top node or not a child
            if (currentIndex < 1) {
                return;
            }
            this.removeFromParent();
            parentNode.insert(this, currentIndex - 1);
        }
        getPictureCollection().setUnsavedUpdates();
    }

    /**
     * Method that moves a node down one position
     */
    public void moveNodeDown() {
        if (this.isRoot()) {
            return;  // don't do anything with a root node.
        }
        synchronized (this.getRoot()) {
            final SortableDefaultMutableTreeNode parentNode = this.getParent();
            int childCount = parentNode.getChildCount();
            int currentIndex = parentNode.getIndex(this);
            // abort if this action was attempted on the bottom node
            if ((currentIndex == -1)
                    || (currentIndex == childCount - 1)) {
                return;
            }
            this.removeFromParent();
            parentNode.insert(this, currentIndex + 1);
        }
        getPictureCollection().setUnsavedUpdates();
    }

    /**
     * Method that moves a node to the bottom of the current branch
     */
    public void moveNodeToBottom() {
        if (this.isRoot()) {
            return;  // don't do anything with a root node.
        }

        synchronized (this.getRoot()) {
            final SortableDefaultMutableTreeNode parentNode = this.getParent();
            int childCount = parentNode.getChildCount();
            // abort if this action was attempted on the bottom node
            if ((parentNode.getIndex(this) == -1)
                    || (parentNode.getIndex(this) == childCount - 1)) {
                return;
            }
            this.removeFromParent();
            parentNode.insert(this, childCount - 1);
        }
        getPictureCollection().setUnsavedUpdates();
    }

    /**
     * When this method is invoked on a node it becomes a sub-node of it's
     * preceding group.
     */
    public void indentNode() {
        if (this.isRoot()) {
            return;  // don't do anything with a root node.
        }

        synchronized (this.getRoot()) {
            final SortableDefaultMutableTreeNode parentNode = this.getParent();
            SortableDefaultMutableTreeNode childBefore = this;
            do {
                childBefore = (SortableDefaultMutableTreeNode) parentNode.getChildBefore(childBefore);
            } while ((childBefore != null) && (!(childBefore.getUserObject() instanceof GroupInfo)));

            if (childBefore == null) {
                final SortableDefaultMutableTreeNode newGroup
                        = new SortableDefaultMutableTreeNode(
                        new GroupInfo(Settings.getJpoResources().getString("newGroup")));
                parentNode.insert(newGroup, 0);
                this.removeFromParent();
                newGroup.add(this);
            } else {
                this.removeFromParent();
                childBefore.add(this);
            }
        }
        getPictureCollection().setUnsavedUpdates();
    }

    /**
     * Method that outdents a node. This means the node will be placed just
     * after it's parent's node as a child of it's grandparent.
     */
    public void outdentNode() {
        if (this.isRoot()) {
            return;  // don't do anything with a root node.
        }
        final SortableDefaultMutableTreeNode parentNode = this.getParent();
        if (parentNode.isRoot()) {
            return;  // don't do anything with a root parent node.
        }

        synchronized (this.getRoot()) {
            final SortableDefaultMutableTreeNode grandParentNode = parentNode.getParent();
            int index = grandParentNode.getIndex(parentNode);

            this.removeFromParent();
            grandParentNode.insert(this, index + 1);
        }
        getPictureCollection().setUnsavedUpdates();
    }

    /**
     * Method that moves a node to bottom of the specified target group node. It
     * sets the collection's unsaved updates to true.
     *
     * @param targetNode The target node you wish to attach the node to
     * @return true if the move was successful, false if not
     */
    public boolean moveToLastChild(final SortableDefaultMutableTreeNode targetNode) {
        if (this.isRoot()) {
            LOGGER.log(Level.SEVERE, "You can''t move the root node to be a child of another node! Aborting move.");
            return false;
        }
        if (!targetNode.getAllowsChildren()) {
            LOGGER.log(Level.SEVERE, "You can''t move a node onto a node that doesn''t allow child nodes.");
            return false;
        }

        synchronized (targetNode.getRoot()) {
            LOGGER.log(Level.INFO, "Removing node {0} from its parent", this);
            this.removeFromParent();
            LOGGER.log(Level.INFO, "Adding node {0} to target node {1}", new Object[]{this, targetNode});
            targetNode.add(this);
        }

        getPictureCollection().setUnsavedUpdates();
        return true;
    }

    /**
     * Method that moves the node to the spot before the indicated node
     *
     * @param targetNode The before which you wish to insert the node to
     * @return true if the move was successful, false if not
     */
    public boolean moveBefore(final SortableDefaultMutableTreeNode targetNode) {
        if (isNodeDescendant(targetNode)) {
            LOGGER.fine("Can't move to a descendant node. Aborting move.");
            return false;
        }

        if (targetNode.isRoot()) {
            LOGGER.fine("You can't move anything in front of the the root node! Aborting move.");
            return false;
        }

        synchronized (targetNode.getRoot()) {
            final SortableDefaultMutableTreeNode targetParentNode = targetNode.getParent();
            int targetIndex = targetParentNode.getIndex(targetNode);
            return moveToIndex(targetParentNode, targetIndex);
        }
    }

    /**
     * Method that moves the node to the specified index
     *
     * @param parentNode The parent node that will get the child
     * @param index      the position at which to insert
     * @return true if the move was successful, false if not
     */
    public boolean moveToIndex(final SortableDefaultMutableTreeNode parentNode,
                               final int index) {
        if (isNodeDescendant(parentNode)) {
            LOGGER.log(Level.SEVERE, "Can''t move to a descendant node. Aborting move.");
            return false;
        }

        int offset = 0;
        if (this.getParent() != null) {
            if (this.getParent().equals(parentNode) && (this.getParent().getIndex(this) < index)) {
                // correct the index if the node goes to the same parent but further down the list
                offset = -1;
            }
            this.removeFromParent();
        }
        LOGGER.log(Level.INFO, "Inserting node {0} at index {1} on parent node {2}", new Object[]{this, index + offset, parentNode});
        parentNode.insert(this, index + offset);
        getPictureCollection().setUnsavedUpdates();
        return true;
    }

    /**
     * Informs whether this node allows children. If the node holds a
     * PictureInfo it does not allow child nodes, if it holds a GroupInfo, it
     * does.
     *
     * @return true if child nodes are allowed, false if not
     */
    @Override
    public boolean getAllowsChildren() {
        if (userObject != null) {
            if (userObject instanceof PictureInfo) {
                return false;
            } else if (userObject instanceof GroupInfo) {
                return true;
            }
        }
        return super.getAllowsChildren();
    }


    /**
     * Copy any file from sourceFile source File to sourceFile target File
     * location.
     *
     * @param sourceFile the source file location
     * @param targetFile the target file location
     * @return The crc of the copied picture.
     */
    public static long copyPicture(final File sourceFile, final File targetFile) {
        LOGGER.log(Level.FINE, "Copying file {0} to file {1}", new Object[]{sourceFile, targetFile});
        try (
                final InputStream in = new FileInputStream(sourceFile);
                final OutputStream out = new FileOutputStream(targetFile)) {

            final BufferedInputStream bin = new BufferedInputStream(in);
            final BufferedOutputStream bout = new BufferedOutputStream(out);

            return copyBufferedStream(bin, bout);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("copyPictureError1")
                            + sourceFile.toString()
                            + Settings.getJpoResources().getString("copyPictureError2")
                            + targetFile.toString()
                            + Settings.getJpoResources().getString("copyPictureError3")
                            + e.getMessage(),
                    Settings.getJpoResources().getString("genericError"),
                    JOptionPane.ERROR_MESSAGE);
            return Long.MIN_VALUE;
        }
    }

    /**
     * Copies the pictures from the source File collection into the target node while updating a supplied progress bar
     *
     * @param fileCollection A Collection framework of the new picture Files
     * @param targetDir      The target directory for the copy operation
     * @param copyMode       Set to true if you want to copy, false if you want to
     *                       move the pictures.
     * @param progressBar    The optional progressBar that should be incremented.
     */
    public void copyAddPictures(final Collection<File> fileCollection, final File targetDir,
                                boolean copyMode, final JProgressBar progressBar) {
        LOGGER.log(Level.FINE, "Copy/Moving {0} pictures to target directory {1}", new Object[]{fileCollection.size(), targetDir});
        getPictureCollection().setSendModelUpdates(false);
        for (final File file : fileCollection) {
            LOGGER.log(Level.FINE, "Processing file {}", file);
            if (progressBar != null) {
                SwingUtilities.invokeLater(
                        () -> progressBar.setValue(progressBar.getValue() + 1)
                );
            }
            final File targetFile = Tools.inventFilename(targetDir, file.getName());
            LOGGER.log(Level.FINE, "Target file name chosen as: {0}", new Object[]{targetFile});
            copyPicture(file, targetFile);

            if (!copyMode) {
                try {
                    Files.delete(file.toPath());
                } catch (final IOException e) {
                    LOGGER.log(Level.SEVERE, "File {} could not be deleted!", file);
                }
            }
            addPicture(targetFile, null);
        }
        getPictureCollection().setSendModelUpdates(true);
    }

    /**
     * Creates and add a new picture node to the current node from an image
     * file.
     *
     * @param addFile            the file of the picture that should be added
     * @param newOnly            flag whether to check if the picture is in the collection
     *                           already; if true will only add the picture if its not yet included
     * @param selectedCategories selected categories
     * @return true if the node was added, false if not.
     */
    public boolean addSinglePicture(final File addFile, final boolean newOnly,
                                    final Collection<Integer> selectedCategories) {
        LOGGER.log(Level.FINE, "Adding File: {0}, NewOnly: {1} to node {2}", new Object[]{addFile, newOnly, this});
        if (newOnly && getPictureCollection().isInCollection(addFile)) {
            return false; // only add pics not in the collection already
        } else {
            return addPicture(addFile, selectedCategories);
        }
    }

    /**
     * this method adds a new Picture to the current node if the JVM has a
     * reader for it.
     *
     * @param addFile            the file that should be added
     * @param categoryAssignment Can be null
     * @return true if the picture was valid, false if not.
     */
    public boolean addPicture(final File addFile, final Collection<Integer> categoryAssignment) {
        LOGGER.log(Level.FINE, "Adding file {0} to the node {1}", new Object[]{addFile, this});
        final PictureInfo newPictureInfo = new PictureInfo();

        if (!SourcePicture.jvmHasReader(addFile)) {
            LOGGER.log(Level.FINE, "Not adding file {0} because the Java Virtual Machine has not got a reader for the file.", addFile);
            return false; // don't add if there is no reader.
        }
        newPictureInfo.setImageLocation(addFile);
        newPictureInfo.setDescription(FilenameUtils.getBaseName(addFile.getName()));
        newPictureInfo.setChecksum(Tools.calculateChecksum(addFile));
        if (categoryAssignment != null) {
            newPictureInfo.setCategoryAssignment(categoryAssignment);
        }
        final SortableDefaultMutableTreeNode newNode = new SortableDefaultMutableTreeNode(newPictureInfo);

        this.add(newNode);
        getPictureCollection().setUnsavedUpdates();

        ExifInfo exifInfo = new ExifInfo(newPictureInfo.getImageFile());
        exifInfo.decodeExifTags();
        newPictureInfo.setCreationTime(exifInfo.getCreateDateTime());
        newPictureInfo.setLatLng(exifInfo.getLatLng());
        newPictureInfo.setRotation(exifInfo.getRotation());

        return true;
    }

    /**
     * This method returns whether the supplied node is a descendant of the
     * deletions that have been detected in the TreeModelListener delivered
     * TreeModelEvent.
     *
     * @param affectedNode The node to check whether it is or is a descendant of
     *                     the deleted node.
     * @param e            the TreeModelEvent that was detected
     * @return true if successful, false if not
     */
    public static boolean wasNodeDeleted(
            final SortableDefaultMutableTreeNode affectedNode, final TreeModelEvent e) {
        TreePath removedChild;
        final TreePath currentNodeTreePath = new TreePath(affectedNode.getPath());
        final Object[] children = e.getChildren();
        for (final Object child : children) {
            removedChild = new TreePath(child);
            if (removedChild.isDescendant(currentNodeTreePath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public SortableDefaultMutableTreeNode getParent() {
        return (SortableDefaultMutableTreeNode) super.getParent();
    }

    @Override
    public SortableDefaultMutableTreeNode getRoot() {
        return (SortableDefaultMutableTreeNode) super.getRoot();
    }

}
