package jpo.gui;

import jpo.EventBus.*;
import jpo.cache.ThumbnailCreationQueue;
import jpo.cache.ThumbnailQueueRequest;
import jpo.cache.ThumbnailQueueRequest.QUEUE_PRIORITY;
import jpo.cache.ThumbnailQueueRequestCallbackHandler;
import jpo.dataModel.*;
import jpo.gui.swing.Thumbnail;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2002 - 2019  Richard Eigenmann.
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
 * ThumbnailController controls a visual representation of the specified node.
 */
public class ThumbnailController
        implements JpoDropTargetDropEventHandler, ThumbnailQueueRequestCallbackHandler {

    /**
     * Defines a LOGGER for this classComponents
     */
    private static final Logger LOGGER = Logger.getLogger(ThumbnailController.class.getName());

    /**
     * Creates a new ThumbnailController object.
     *
     * @param thumbnail     The thumbnail to manage
     * @param thumbnailSize The size in which the thumbnail is to be created
     */
    public ThumbnailController(Thumbnail thumbnail, final int thumbnailSize) {
        myThumbnail = thumbnail;
        myThumbnail.setThumbnailSize(thumbnailSize);
        myThumbnail.addMouseListener(new ThumbnailMouseAdapter());

        new DropTarget(myThumbnail, new JpoTransferrableDropTargetListener(this));
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(
                myThumbnail, DnDConstants.ACTION_COPY_OR_MOVE, new ThumbnailDragGestureListener());
    }

    /**
     * Refers to the thumbnail which is being controlled
     */
    private final Thumbnail myThumbnail;

    /**
     * a link to the SortableDefaultMutableTreeNode in the data model. This
     * allows thumbnails to be selected by sending a nodeSelected event to the
     * data model.
     */
    private SortableDefaultMutableTreeNode myNode;

    /**
     * Returns the node for which the controller is acting
     *
     * @return the node
     */
    public SortableDefaultMutableTreeNode getNode() {
        return myNode;
    }

    /**
     * A set of picture nodes of which one indicated by {@link #myIndex} is to
     * be shown
     */
    private NodeNavigatorInterface myNodeNavigator;

    /**
     * the Index position in the {@link #myNodeNavigator} which is being shown
     * by this component.
     */
    private int myIndex;  // default is 0


    /**
     * returns the Thumbnail that is being controlled by this Controller.
     *
     * @return the Thumbnail
     */
    public Thumbnail getThumbnail() {
        return myThumbnail;
    }

    /**
     * Returns to the caller whether the ThumbnailController is already showing
     * the node.
     *
     * @param nodeNavigator The NodeNavigatorInterface from which the node is
     *                      coming
     * @param index         The index position that should be checked.
     * @return true if the indicated node is already showing, false if not
     */
    public boolean isSameNode(NodeNavigatorInterface nodeNavigator,
                              int index) {
        if (!nodeNavigator.equals(myNodeNavigator)) {
            return false;
        } else {
            if (index != myIndex) {
                return false;
            } else {
                SortableDefaultMutableTreeNode testNode = nodeNavigator.getNode(index);
                if (testNode == null) {
                    return false;
                } else {
                    return testNode.equals(myNode);
                }
            }
        }
    }

    private ThumbnailQueueRequest myThumbnailQueueRequest = null;

    /**
     * Sets the node being visualised by this ThumbnailController object.
     *
     * @param mySetOfNodes The {@link NodeNavigatorInterface} being tracked
     * @param index        The position of this object to be displayed.
     */
    public void setNode(NodeNavigatorInterface mySetOfNodes, int index) {
        this.myNodeNavigator = Objects.requireNonNull(mySetOfNodes);
        this.myIndex = index;

        SortableDefaultMutableTreeNode node = mySetOfNodes.getNode(index);
        this.myNode = node;

        // remove and silence the old request if it is still alive
        if (myThumbnailQueueRequest != null) {
            myThumbnailQueueRequest.cancel();
            ThumbnailCreationQueue.remove(myThumbnailQueueRequest);
        }

        attachChangeListeners();
        if (node == null) {
            myThumbnail.setVisible(false);
            myThumbnailQueueRequest = null;
        } else {
            QUEUE_PRIORITY priority;
            if ( thumbnailIsInVisibleArea() ) {
                priority = QUEUE_PRIORITY.HIGH_PRIORITY;
            } else {
                 priority = QUEUE_PRIORITY.MEDIUM_PRIORITY;
            }
            myThumbnailQueueRequest = requestThumbnailCreation(priority);
        }

        showSelectionStatus();
        determineMailSelectionStatus();
        drawOfflineIcon(myNode);
    }

    /**
     * This code tries to find out if the thumbnail is being shown in a JViewport where it is visible
     */
    private boolean thumbnailIsInVisibleArea() {
        try {
            if (getThumbnail().getParent().getParent().getParent() instanceof JViewport) {
                JViewport viewport = (JViewport) getThumbnail().getParent().getParent().getParent();
                Thumbnail thumbnail = getThumbnail();
                if (thumbnail != null) {
                    Point point = thumbnail.getLocation();
                    if (viewport.getViewRect().contains(point)) {
                        return true;
                    }
                }
            }
        } catch (NullPointerException npe) {
            // the thumbnail is not in the JViewport hierarchy so we can't say
        }
        return false;
    }

    /**
     * remember where we registered as a PictureInfoListener
     */
    private PictureInfo registeredPictureInfoChangeListener;

    private final MyPictureInfoChangeEventHandler myPictureInfoChangeEventHandler = new MyPictureInfoChangeEventHandler();

    /**
     * remember where we registered as a GroupInfoListener
     */
    private GroupInfo registeredGroupInfoChangeListener;

    private final MyGroupInfoChangeEventHandler myGroupInfoChangeEventHandler = new MyGroupInfoChangeEventHandler();

    /**
     * Unattaches the ThumbnailController from the previously linked
     * PictureInfoChangeListener or GroupInfoChangeListener (if any) and
     * attaches it to the new PictureInfoChangeListener or
     * GroupInfoChangeListener.
     */
    private void attachChangeListeners() {
        // unattach from the change Listener
        if (registeredPictureInfoChangeListener != null) {
            LOGGER.fine(String.format("unattaching MyPictureInfoChangeEventHandler %d from PictureInfo %d", myPictureInfoChangeEventHandler.hashCode(), registeredPictureInfoChangeListener.hashCode()));
            registeredPictureInfoChangeListener.removePictureInfoChangeListener(myPictureInfoChangeEventHandler);
            registeredPictureInfoChangeListener = null;
        }
        // unattach the change Listener from the GroupInfo
        if (registeredGroupInfoChangeListener != null) {
            registeredGroupInfoChangeListener.removeGroupInfoChangeListener(myGroupInfoChangeEventHandler);
            registeredGroupInfoChangeListener = null;
        }

        // attach the change Listener
        if (myNode != null) {
            if (myNode.getUserObject() instanceof PictureInfo) {
                PictureInfo pictureInfo = (PictureInfo) myNode.getUserObject();
                LOGGER.fine(String.format("attaching ThumbnailController %d to PictureInfo %d", this.hashCode(), pictureInfo.hashCode()));
                pictureInfo.addPictureInfoChangeListener(myPictureInfoChangeEventHandler);
                registeredPictureInfoChangeListener = pictureInfo; //remember so we can poll
            } else if (myNode.getUserObject() instanceof GroupInfo) {
                GroupInfo groupInfo = (GroupInfo) myNode.getUserObject();
                groupInfo.addGroupInfoChangeListener(myGroupInfoChangeEventHandler);
                registeredGroupInfoChangeListener = groupInfo; //remember so we can poll
            }
        }
    }

    /**
     * This method forwards the request to create the thumbnail to the
     * ThumbnailCreationQueue
     *
     * @param priority The priority with which the request is to be treated on
     *                 the queue
     * @return the request
     */
    private ThumbnailQueueRequest requestThumbnailCreation(QUEUE_PRIORITY priority) {
        myThumbnail.setQueueIcon();
        return ThumbnailCreationQueue.requestThumbnailCreation(
                this, myNode, priority, getMaximumUnscaledSize());
    }


    /**
     * Returns the maximum unscaled size for the ThumbnailController as a
     * Dimension using the thumbnailSize as width and height.
     *
     * @return The maximum unscaled size of the ThumbnailController
     */
    private Dimension getMaximumUnscaledSize() {
        return new Dimension(myThumbnail.getThumbnailSize(), myThumbnail.getThumbnailSize());
    }

    /**
     * This method checks if the node is set and whether the highres image is
     * available. If there is a problem the offline icon is drawn over the
     * thumbnail.
     *
     * @param nodeToCheck The Node to check
     */
    private void drawOfflineIcon(DefaultMutableTreeNode nodeToCheck) {
        if (nodeToCheck == null) {
            myThumbnail.drawOfflineIcon(false);
            return;
        }

        Object userObject = nodeToCheck.getUserObject();
        if (userObject instanceof PictureInfo) {
            myThumbnail.drawOfflineIcon(!Files.isReadable(((PictureInfo) userObject).getImageFile().toPath()));
        } else {
            myThumbnail.drawOfflineIcon(false);
        }
    }

    /**
     * Entry point for the callback handler
     *
     * @param thumbnailQueueRequest The request from the queue
     */
    @Override
    public void callbackThumbnailCreated(ThumbnailQueueRequest thumbnailQueueRequest) {
        getThumbnail().setImageIcon(thumbnailQueueRequest.getIcon());
        myThumbnailQueueRequest = null;
    }

    /**
     * Inner class to handle the mouse events on the ThumbnailController
     */
    private class ThumbnailMouseAdapter
            extends MouseAdapter {

        /**
         * overridden to analyse the mouse event and decide whether to display
         * the picture right away (double click) or show the popupMenu.
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (myNode == null) {
                return;
            }
            if (e.getClickCount() > 1) {
                doubleClickResponse();
            } else if (e.getButton() == 3) { // popup menu only on 3rd mouse button.
                rightClickResponse(e);
            } else if (e.getButton() == 1) { // first button
                leftClickResponse(e);
            }
        }

        /**
         * Logic for processing a left click on the thumbnail
         *
         * @param e the mouse event
         */
        private void leftClickResponse(MouseEvent e) {
            if (e.isControlDown()) {
                if (Settings.getPictureCollection().isSelected(myNode)) {
                    Settings.getPictureCollection().removeFromSelection(myNode);
                } else {
                    LOGGER.fine(String.format("Adding; Now Selected: %d", Settings.getPictureCollection().getSelection().size()));
                    Settings.getPictureCollection().addToSelectedNodes(myNode);
                }
            } else {
                if (Settings.getPictureCollection().isSelected(myNode)) {
                    Settings.getPictureCollection().clearSelection();
                } else {
                    Settings.getPictureCollection().clearSelection();
                    Settings.getPictureCollection().addToSelectedNodes(myNode);
                    LOGGER.fine(String.format("1 selection added; Now Selected: %d", Settings.getPictureCollection().getSelection().size()));
                }
            }
        }

        /**
         * Logic for processing a right click on the thumbnail
         *
         * @param e Mouse event
         */
        private void rightClickResponse(MouseEvent e) {
            if (myNode.getUserObject() instanceof PictureInfo) {
                JpoEventBus.getInstance().post(new ShowPicturePopUpMenuRequest( myNodeNavigator, myIndex, e.getComponent(), e.getX(), e.getY() ) );
            } else if (myNode.getUserObject() instanceof GroupInfo) {
                JpoEventBus.getInstance().post(new ShowGroupPopUpMenuRequest( myNode, e.getComponent(), e.getX(), e.getY() ) );
            } else {
                LOGGER.severe(String.format("Processing a right click response on an unknown node type: %s", myNode.getUserObject().getClass().toString()));
            }
        }

        /**
         * Logic for processing a double click on the thumbnail
         */
        private void doubleClickResponse() {
            if (myNode.getUserObject() instanceof PictureInfo) {
                JpoEventBus.getInstance().post(new ShowPictureRequest(myNode));
            } else if (myNode.getUserObject() instanceof GroupInfo) {
                JpoEventBus.getInstance().post(new ShowGroupRequest(myNode));
            }
        }

    }

    private class MyPictureInfoChangeEventHandler implements PictureInfoChangeListener {

        /**
         * here we get notified by the PictureInfo object that something has
         * changed.
         *
         * @param pictureInfoChangeEvent event
         */
        @Override
        public void pictureInfoChangeEvent(PictureInfoChangeEvent pictureInfoChangeEvent) {
            if (pictureInfoChangeEvent.getHighresLocationChanged() || pictureInfoChangeEvent.getChecksumChanged() || pictureInfoChangeEvent.getThumbnailChanged()) {
                requestThumbnailCreation(QUEUE_PRIORITY.HIGH_PRIORITY);
            } else if (pictureInfoChangeEvent.getWasSelected()) {
                myThumbnail.setSelected();
            } else if (pictureInfoChangeEvent.getWasUnselected()) {
                myThumbnail.setUnSelected();
            } else if ((pictureInfoChangeEvent.getWasMailSelected()) || (pictureInfoChangeEvent.getWasMailUnselected())) {
                determineMailSelectionStatus();
            } else if (pictureInfoChangeEvent.getRotationChanged()) {
                requestThumbnailCreation(QUEUE_PRIORITY.HIGH_PRIORITY);
            }
        }
    }

    private class MyGroupInfoChangeEventHandler implements GroupInfoChangeListener {

        /**
         * here we get notified by the GroupInfo object that something has
         * changed.
         *
         * @param groupInfoChangeEvent event
         */
        @Override
        public void groupInfoChangeEvent(GroupInfoChangeEvent groupInfoChangeEvent) {
            LOGGER.fine(String.format("Got a Group Change event: %s", groupInfoChangeEvent.toString()));
            if (groupInfoChangeEvent.getWasSelected()) {
                myThumbnail.setSelected();
            } else if (groupInfoChangeEvent.getWasUnselected()) {
                myThumbnail.setUnSelected();
            }
        }
    }

    /**
     * changes the colour so that the user sees whether the thumbnail is part of
     * the selection
     */
    private void showSelectionStatus() {
        if (Settings.getPictureCollection().isSelected(myNode)) {
            myThumbnail.setSelected();
        } else {
            myThumbnail.setUnSelected();
        }

    }

    /**
     * This method sets the scaling factor for the display of a thumbnail. 0 ..
     * 1
     *
     * @param thumbnailSizeFactor Factor
     */
    public void setFactor(float thumbnailSizeFactor) {
        LOGGER.fine(String.format("Scaling factor is being set to %f", thumbnailSizeFactor));
        myThumbnail.setFactor(thumbnailSizeFactor);
    }

    /**
     * This flag indicates where decorations should be drawn at all
     */
    private boolean decorateThumbnails = true;

    /**
     * Determines whether decorations should be drawn or not
     *
     * @param decorateThumbnails Whether to decorate
     */
    public void setDecorateThumbnails(boolean decorateThumbnails) {
        this.decorateThumbnails = decorateThumbnails;
    }

    /**
     * determines if the thumbnail is part of the mail selection and changes the
     * drawMailIcon flag to ensure that the mail icon will be place over the
     * image.
     */
    public void determineMailSelectionStatus() {
        if ((myNode != null) && decorateThumbnails && Settings.getPictureCollection().isMailSelected(myNode)) {
            myThumbnail.drawMailIcon(true);
        } else {
            myThumbnail.drawMailIcon(false);
        }

    }

    /**
     * This class extends a DragGestureListener and allows DnD on Thumbnails.
     */
    private class ThumbnailDragGestureListener
            implements DragGestureListener {

        /**
         * This method is invoked by the drag and drop framework. It signifies
         * the start of a drag and drop operation. If the event is a copy or
         * move we start the drag and create a Transferable.
         */
        @Override
        public void dragGestureRecognized(DragGestureEvent event) {
            if ((event.getDragAction() & DnDConstants.ACTION_COPY_OR_MOVE) == 0) {
                return;
            }

            JpoTransferable transferable;

            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                List<SortableDefaultMutableTreeNode> transferableNodes = new ArrayList<>();
                transferableNodes.add(myNode);
                transferable = new JpoTransferable(transferableNodes);
            } else {
                transferable = new JpoTransferable(Settings.getPictureCollection().getSelection());
            }

            try {
                event.startDrag(DragSource.DefaultMoveNoDrop, transferable, new ThumbnailDragSourceListener());
                LOGGER.log(Level.FINE, "Drag started on node: {0}", myNode.getUserObject().toString());
            } catch (InvalidDnDOperationException x) {
                LOGGER.log(Level.FINE, "Threw a InvalidDnDOperationException: reason: {0}", x.getMessage());
            }
        }
    }

    /**
     * This class extends a DragSourceListener for tracking the drag operation
     * originating from this thumbnail.
     */
    private class ThumbnailDragSourceListener
            implements DragSourceListener {

        /**
         * this callback method is invoked after the dropTarget had a chance to
         * evaluate the drag event and was given the option of rejecting or
         * modifying the event. This method sets the cursor to reflect whether a
         * copy, move or no drop is possible.
         */
        @Override
        public void dragEnter(DragSourceDragEvent event) {
            setDragCursor(event);
        }

        /**
         * this callback method is invoked after the dropTarget had a chance to
         * evaluate the dragOver event and was given the option of rejecting or
         * modifying the event. This method sets the cursor to reflect whether a
         * copy, move or no drop is possible.
         */
        @Override
        public void dragOver(DragSourceDragEvent event) {
            setDragCursor(event);
        }

        /**
         * this callback method is invoked to tell the dragSource that the drag
         * has moved on to something else.
         */
        @Override
        public void dragExit(DragSourceEvent event) {
        }

        /**
         * this callback method is invoked when the user presses or releases
         * shift when doing a drag. He can signal that he wants to change the
         * copy / move of the operation.
         */
        @Override
        public void dropActionChanged(DragSourceDragEvent event) {
            setDragCursor(event);
        }

        /**
         * this callback message goes to DragSourceListener, informing it that
         * the dragging has ended.
         */
        @Override
        public void dragDropEnd(DragSourceDropEvent event) {
            Settings.getPictureCollection().clearSelection();
        }

        /**
         * Analyses the drag event and sets the cursor to the appropriate style.
         *
         * @param event the DragSourceDragEvent for which the cursor is to be
         *              adjusted
         */
        void setDragCursor(DragSourceDragEvent event) {
            DragSourceContext context = event.getDragSourceContext();
            int dndCode = event.getDropAction();
            if ((dndCode & DnDConstants.ACTION_COPY) != 0) {
                context.setCursor(DragSource.DefaultCopyDrop);
            } else if ((dndCode & DnDConstants.ACTION_MOVE) != 0) {
                context.setCursor(DragSource.DefaultMoveDrop);
            } else {
                context.setCursor(DragSource.DefaultMoveNoDrop);
            }
        }

    }

    @Override
    public void handleJpoDropTargetDropEvent(DropTargetDropEvent event) {
        myNode.executeDrop(event);
    }
}
