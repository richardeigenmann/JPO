package org.jpo.datamodel;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.Settings.FieldCodes;
import org.jpo.gui.JpoTransferable;

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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jpo.datamodel.Tools.copyBufferedStream;

/*
 Copyright (C) 2003-2024 Richard Eigenmann, Zurich, Switzerland
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
 * This is the main data structure object for the JPO Collection. Holds a
 * reference to either a PictureInfo or GroupInfo object in its getUserObject.
 * <p>
 * It extends the DefaultMutableTreeNode with the Comparable Interface that
 * allows our nodes to be compared.
 */
public class SortableDefaultMutableTreeNode
        extends DefaultMutableTreeNode
        implements PictureInfoChangeListener, GroupInfoChangeListener {

    public static final String GENERIC_ERROR = "genericError";
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
    public SortableDefaultMutableTreeNode(final GroupOrPicture userObject) {
        super();
        setUserObject(userObject);
        userObject.setOwningNode(this);
    }

    private transient PictureCollection myPictureCollection;

    /**
     * Should only be called on the root node
     * @param pictureCollection the picture collection that owns this node
     */
    public void setPictureCollection (final PictureCollection pictureCollection) {
        myPictureCollection = pictureCollection;
    }


    /**
     * This method adds a new node to the data model of the tree.
     * The add method which will first do the default behavior and then
     * send a notification to the Tree Model if model updates are being
     * requested. Likewise, the unsaved changes of the collection are only being
     * updated when model updates are not being reported. This allows the
     * loading of collections (which of course massively change the collection
     * in memory) to report nothing changed.
     *
     * @param nodeToAdd the new node
     */
    public void add(final SortableDefaultMutableTreeNode nodeToAdd) {
        final var priorParent = nodeToAdd.getParent();
        var priorChildIndex = -1;
        if (priorParent != null ) {
            priorChildIndex = priorParent.getIndex(nodeToAdd);
        }

        synchronized (this.getRoot()) {
            super.add(nodeToAdd);
        }

        final var pictureCollection = getPictureCollection();
        if (pictureCollection != null && pictureCollection.getSendModelUpdates()) {
            if (priorParent !=null && priorChildIndex != -1) {
                pictureCollection.sendNodesWereRemoved(priorParent, new int[] {priorChildIndex}, new Object[] {nodeToAdd});
            }

            int index = this.getIndex(nodeToAdd);
            pictureCollection.sendNodesWereInserted(this, new int[]{index});

            pictureCollection.setUnsavedUpdates();
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
     * Creates and add a new picture node to the current node from an image
     * file.
     *
     * @param addFile            the file of the picture that should be added
     * @param newOnly            flag whether to check if the picture is in the collection
     *                           already; if true will only add the picture if it is not yet included
     * @param selectedCategories selected categories
     * @return true if the node was added, false if not.
     */
    public boolean addSinglePicture(final File addFile, final boolean newOnly,
                                    final Collection<Integer> selectedCategories) {
        LOGGER.log(Level.INFO, "Adding File: {0}, NewOnly: {1} to node {2}", new Object[]{addFile, newOnly, this});
        if (newOnly && getPictureCollection().isInCollection(addFile)) {
            LOGGER.log(Level.INFO, "Rejecting file {0} because it already exists in the collection", addFile);
            return false; // only add pics not in the collection already
        } else {
            return addPicture(addFile, selectedCategories);
        }
    }

    /**
     * this method adds a new Picture to the current node if the JVM has a
     * reader for it.
     *
     * @param file                the file that should be added
     * @param categoryAssignments Can be null
     * @return true if the picture was valid, false if not.
     */
    public boolean addPicture(final File file, final Collection<Integer> categoryAssignments) {
        if (JpoImageIO.jvmHasReader(file)
                || MimeTypes.isAPicture(file)
                || MimeTypes.isADocument(file)
                || MimeTypes.isAMovie(file)) {
            LOGGER.log(Level.INFO, "File {0} has a reader or a picture mime type or is a document or is a movie so it will be added", file);
            final var newPictureInfo = new PictureInfo();
            newPictureInfo.setImageLocation(file);
            newPictureInfo.setDescription(FilenameUtils.getBaseName(file.getName()));
            newPictureInfo.setSha256();
            newPictureInfo.setCategoryAssignment(categoryAssignments);
            ExifInfo exifInfo = new ExifInfo(newPictureInfo.getImageFile());
            exifInfo.decodeExifTags();
            newPictureInfo.setCreationTime(exifInfo.getCreateDateTime());
            newPictureInfo.setLatLng(exifInfo.getLatLng());
            newPictureInfo.setRotation(exifInfo.getRotation());
            final SortableDefaultMutableTreeNode newNode = new SortableDefaultMutableTreeNode(newPictureInfo);

            this.add(newNode);
            getPictureCollection().setUnsavedUpdates();

            return true;
        } else {
            LOGGER.log(Level.INFO, "Not adding file {0} because the Java Virtual Machine has not got a reader for the file.", file);
            return false;
        }
    }


    /**
     * This method reports if one of the supplied nodes is an ancestor of the current node.
     * @param potentialAncestorNodes The collection of potential ancestor nodes to check
     * @return true if an ancestor is found
     */
    public boolean containsAnAncestor(final Collection<SortableDefaultMutableTreeNode> potentialAncestorNodes) {
        for (final var potentialAncestorNode : potentialAncestorNodes) {
            if (this.isNodeAncestor(potentialAncestorNode)) {
                return true;
            }
        }
        return false;
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
     * Copy any file from sourceFile source File to sourceFile target File
     * location.
     *
     * @param sourceFile the source file location
     * @param targetFile the target file location

     */
    public static void copyPicture(final File sourceFile, final File targetFile) {
        LOGGER.log(Level.FINE, "Copying file {0} to file {1}", new Object[]{sourceFile, targetFile});
        try (
                final var bufferedInputStream = new BufferedInputStream(new FileInputStream(sourceFile));
                final var bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(targetFile))) {
            copyBufferedStream(bufferedInputStream, bufferedOutputStream);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("copyPictureError1")
                            + sourceFile
                            + Settings.getJpoResources().getString("copyPictureError2")
                            + targetFile.toString()
                            + Settings.getJpoResources().getString("copyPictureError3")
                            + e.getMessage(),
                    Settings.getJpoResources().getString(GENERIC_ERROR),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method returns whether the supplied node is a descendant of the
     * deletions that have been detected in the TreeModelListener delivered
     * TreeModelEvent.
     *
     * @param potentiallyAffectedNode The node to check whether it is or is a descendant of
     *                     the deleted node.
     * @param treeModelEvent The TreeModelEvent that was detected
     * @return true if successful, false if not
     */
    public static boolean wasNodeDeleted(
            final SortableDefaultMutableTreeNode potentiallyAffectedNode, final TreeModelEvent treeModelEvent) {
        TreePath removedChild;
        final TreePath currentNodeTreePath = new TreePath(potentiallyAffectedNode.getPath());
        final Object[] children = treeModelEvent.getChildren();
        for (final Object child : children) {
            removedChild = new TreePath(child);
            if (removedChild.isDescendant(currentNodeTreePath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns the collection associated with this node
     *
     * @return the picture collection.
     */
    public PictureCollection getPictureCollection() {
        return getRoot().myPictureCollection;
    }

    /**
     * Call this method to sort the Children of a node by a field.
     *
     * @param sortCriteria The criteria by which the pictures should be sorted.
     */
    public void sortChildren(final FieldCodes sortCriteria) {
        Tools.checkEDT();  // because of removeAllChildren
        getPictureCollection().setSendModelUpdates(false);
        this.children.sort(new SortableDefaultMutableTreeNodeComparator(sortCriteria));
        getPictureCollection().setUnsavedUpdates();
        getPictureCollection().setSendModelUpdates(true);
        getPictureCollection().sendNodeStructureChanged(this);
    }


    private static class SortableDefaultMutableTreeNodeComparator implements Comparator<TreeNode> {
        SortableDefaultMutableTreeNodeComparator(final FieldCodes sortField) {
            mySortField = sortField;
        }

        final FieldCodes mySortField;

        @Override
        public int compare(final TreeNode treeNode1, final TreeNode treeNode2) {
            if (treeNode1 instanceof SortableDefaultMutableTreeNode node1 && treeNode2 instanceof SortableDefaultMutableTreeNode node2) {
                final Object userObject1 = node1.getUserObject();
                final Object userObject2 = node2.getUserObject();

                if ((userObject1 instanceof GroupInfo myGroupInfo) && (userObject2 instanceof GroupInfo otherGroupInfo)) {
                    return myGroupInfo.compareTo(otherGroupInfo);
                }
                if ((userObject1 instanceof PictureInfo pictureInfo1) && (userObject2 instanceof PictureInfo pictureInfo2)) {
                    return pictureInfo1.compareTo(pictureInfo2, mySortField);
                }

                if ((userObject1 instanceof GroupInfo myGi) && (userObject2 instanceof PictureInfo pi) && (mySortField == FieldCodes.DESCRIPTION)) {
                    return (myGi.getGroupName().compareTo(pi.getDescription()));
                }

                if ((userObject1 instanceof PictureInfo pi) && (userObject2 instanceof GroupInfo gi) && (mySortField == FieldCodes.DESCRIPTION)) {
                    return (pi.getDescription().compareTo(gi.getGroupName()));
                }
            }
            LOGGER.severe("Comparing something strange.");
            return 0;
        }


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
            var prevNode = getPreviousNode();
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
        final Enumeration<TreeNode> e = children();
        while (e.hasMoreElements()) {
            SortableDefaultMutableTreeNode testNode = (SortableDefaultMutableTreeNode) e.nextElement();
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
     * as an Array List.
     *
     * @param recursive Pass true if the method is supposed to recursively
     *                  search the subgroups, false if not
     * @return A List of child nodes that hold a picture
     */
    public List<SortableDefaultMutableTreeNode> getChildPictureNodes(
            final boolean recursive) {
        final List<SortableDefaultMutableTreeNode> pictureNodes = new ArrayList<>();
        final Enumeration<TreeNode> kids = this.children();
        while (kids.hasMoreElements()) {
            final SortableDefaultMutableTreeNode node = (SortableDefaultMutableTreeNode) kids.nextElement();
            if (recursive && node.getUserObject() instanceof GroupInfo) {
                pictureNodes.addAll(node.getChildPictureNodes(true));
            } else if (node.getUserObject() instanceof PictureInfo) {
                pictureNodes.add(node);
            }
        }
        return pictureNodes;
    }

    /**
     * This method collects all pictures under the current node and returns them
     * as an Array List.
     *
     * @return A List of child nodes that hold a picture
     */
    public Stream<SortableDefaultMutableTreeNode> getChildPictureNodesDFS() {
        return Collections.list(this.depthFirstEnumeration()).stream()
                .filter(node -> node.isLeaf() && (((DefaultMutableTreeNode) node).getUserObject() instanceof PictureInfo))
                .map(SortableDefaultMutableTreeNode.class::cast);
    }


    /**
     * A convenience method to tell if the current node has at least one picture
     * node in the tree of children. (Could be one)
     *
     * @return true if at least one picture is found, false if not
     */
    public boolean hasChildPictureNodes() {
        final Enumeration<TreeNode> kids = this.breadthFirstEnumeration();
        while (kids.hasMoreElements()) {
            final SortableDefaultMutableTreeNode node = (SortableDefaultMutableTreeNode) kids.nextElement();
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
        removeOldListener(getUserObject());
        switch (userObject) {
            case String s -> {
                // this gets called when the JTree is being edited with F2 keystroke
                final Object obj = getUserObject();
                if (obj instanceof GroupInfo groupInfo) {
                    groupInfo.setGroupName(s);
                } else if (obj instanceof PictureInfo pictureInfo) {
                    pictureInfo.setDescription(s);
                }
            }
            case PictureInfo pictureInfo -> {
                pictureInfo.addPictureInfoChangeListener(this);
                super.setUserObject(userObject);
            }
            case GroupInfo groupInfo -> {
                groupInfo.addGroupInfoChangeListener(this);
                super.setUserObject(userObject);
            }
            case null, default ->
                // fall back on the default behaviour
                    super.setUserObject(userObject);
        }
        if (getPictureCollection() != null && getPictureCollection().getSendModelUpdates()) {
            getPictureCollection().sendNodeChanged(this);
        }
    }

    private void removeOldListener(final Object userObject) {
        if (userObject instanceof PictureInfo pictureInfo) {
            pictureInfo.removePictureInfoChangeListener(this);
        } else if (userObject instanceof GroupInfo groupInfo) {
            groupInfo.removeGroupInfoChangeListener(this);
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

        if (this.containsAnAncestor(transferableNodes)) {
            LOGGER.log(Level.INFO, """
                    One of the transferring nodes is an ancestor of the current node which would orphan the tree.
                    """);
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("moveNodeError"),
                    Settings.getJpoResources().getString(GENERIC_ERROR),
                    JOptionPane.ERROR_MESSAGE);
            dropEvent.dropComplete(false);

            return;
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

    /**
     * This is where the Nodes in the tree find out about changes in the
     * PictureInfo object
     *
     * @param pictureInfoChangeEvent The event
     */
    @Override
    public void pictureInfoChangeEvent(final PictureInfoChangeEvent pictureInfoChangeEvent) {
        if ( this.getParent() != null ) {
            getPictureCollection().sendNodeChanged(this);
        }
    }

    @Override
    public void groupInfoChangeEvent(final GroupInfoChangeEvent groupInfoChangeEvent) {
        if ( this.getParent() != null ) {
            getPictureCollection().sendNodeChanged(this);
        }
    }


    /**
     * Removes the node from the parent and sends a nodesWereRemoved notification.
     * Essentially this is the equivalent of deleting the node from the tree.
     */
    @Override
    public void removeFromParent() {
        final var oldParentNode = this.getParent();
        if (oldParentNode == null) {
            return;
        }
        final var pictureCollection = oldParentNode.getPictureCollection();
        final var oldParentIndex = oldParentNode.getIndex(this);

        synchronized (this.getRoot()) {
            super.removeFromParent();
        }
        pictureCollection.setUnsavedUpdates();

        if (pictureCollection.getSendModelUpdates() && oldParentIndex != -1) {
            pictureCollection.sendNodesWereRemoved(oldParentNode,
                    new int[]{oldParentIndex},
                    new Object[]{this});
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
     * position of its parent node.
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
     * towards the first child position of its parent node.
     */
    public void moveNodeUp() {
        if (this.isRoot()) {
            return;  // don't do anything with a root node.
        }
        synchronized (this.getRoot()) {
            final SortableDefaultMutableTreeNode parentNode = this.getParent();
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
     * after it's parent's node as a child of its grandparent.
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
        LOGGER.log(Level.FINE, "moving node {0} to the last child of node {1}", new Object[]{this, targetNode});
        if (targetNode.isNodeAncestor(this)) {
            LOGGER.log(Level.SEVERE, "You can''t move a node to be a child of who it is an ancestor! Aborting move.");
            return false;
        }
        if (!targetNode.getAllowsChildren()) {
            LOGGER.log(Level.SEVERE, "You can''t move a node onto a node that doesn''t allow child nodes.");
            return false;
        }

        LOGGER.log(Level.FINE, "Adding node {0} to target node {1}", new Object[]{this, targetNode});
        targetNode.add(this);

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
        LOGGER.log(Level.INFO, "moving node {0} to the position before node {1}", new Object[]{this, targetNode});
        if (isNodeDescendant(targetNode)) {
            LOGGER.fine("Can't move to a descendant node. Aborting move.");
            return false;
        }

        if (targetNode.isRoot()) {
            LOGGER.fine("You can't move anything in front of the the root node! Aborting move.");
            return false;
        }

        synchronized (targetNode.getRoot()) {
            final var targetParentNode = targetNode.getParent();
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
        LOGGER.log(Level.FINE, "Moving node {0} to child index {1} of node {2}", new Object[]{this, index, parentNode});
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
        LOGGER.log(Level.FINE, "Inserting node {0} at index {1} on parent node {2}", new Object[]{this, index + offset, parentNode});
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


    @Override
    public SortableDefaultMutableTreeNode getParent() {
        return (SortableDefaultMutableTreeNode) super.getParent();
    }

    @Override
    public SortableDefaultMutableTreeNode getRoot() {
        return (SortableDefaultMutableTreeNode) super.getRoot();
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

    public Path getCommonPath() {
        final var firstNode = getChildPictureNodesDFS().findFirst();
        if (firstNode.isEmpty()) return null;
        final var firstPath = ((PictureInfo) firstNode.get().getUserObject()).getImageFile().toPath();
        return getChildPictureNodesDFS()
                .map(node -> ((PictureInfo) node.getUserObject()).getImageFile().toPath())
                .reduce(firstPath, SortableDefaultMutableTreeNode::commonPath);
    }

    /**
     * Finds the common path between two input paths
     *
     * @param path0 The first path
     * @param path1 The second path
     * @return The common path
     * See <a href="https://stackoverflow.com/questions/54595752/find-the-longest-path-common-to-two-paths-in-java">find-the-longest-path-common-to-two-paths-in-java</a>
     */
    public static Path commonPath(final Path path0, Path path1) {
        if (path0.equals(path1)) {
            return path0;
        }

        final var normalizedPath0 = path0.normalize();
        final var normalizedPath1 = path1.normalize();
        int minCount = Math.min(normalizedPath0.getNameCount(), normalizedPath1.getNameCount());
        for (int i = minCount; i > 0; i--) {
            Path sp0 = normalizedPath0.subpath(0, i);
            if (sp0.equals(normalizedPath1.subpath(0, i))) {
                String root = Objects.toString(normalizedPath0.getRoot(), "");
                return Paths.get(root, sp0.toString());
            }
        }

        return normalizedPath0.getRoot();
    }

}
