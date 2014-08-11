package jpo.gui;

import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.ShowGroupRequest;
import jpo.EventBus.ShowPictureRequest;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.GroupInfoChangeEvent;
import jpo.dataModel.GroupInfoChangeListener;
import jpo.dataModel.NodeNavigatorInterface;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.PictureInfoChangeEvent;
import jpo.dataModel.PictureInfoChangeListener;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;
import jpo.gui.swing.GroupPopupMenu;
import jpo.gui.swing.Thumbnail;

/*
 ThumbnailController.java:  class that displays a visual respresentation of the specified node

 Copyright (C) 2002 - 2014  Richard Eigenmann.
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
public class ThumbnailController implements JpoDropTargetDropEventHandler {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ThumbnailController.class.getName() );

    /**
     * Creates a new ThumbnailController object with a reference to the
     * ThumbnailPanelController which must receive notifications that a new node
     * should be selected.
     *
     *
     */
    public ThumbnailController() {
        this( Settings.thumbnailSize );
    }


    /**
     * Creates a new ThumbnailController object. This must happen on the EDT
     * because it creates a Thumbnail SWING component
     *
     * @param	thumbnailSize	The size in which the thumbnail is to be created
     *
     */
    public ThumbnailController( final int thumbnailSize ) {
        myThumbnail = new Thumbnail();
        myThumbnail.setThumbnailSize( thumbnailSize );
        myThumbnail.addMouseListener( new ThumbnailMouseAdapter() );

        // set up drag & drop
        new DropTarget( myThumbnail, new JpoTransferrableDropTargetListener( this ) );
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(
                myThumbnail, DnDConstants.ACTION_COPY_OR_MOVE, new ThumbnailDragGestureListener() );
    }

    /**
     * Refers to the thumbnail which is being controlled
     */
    private Thumbnail myThumbnail;

    /**
     * a link to the SortableDefaultMutableTreeNode in the data model. This
     * allows thumbnails to be selected by sending a nodeSelected event to the
     * data model.
     *
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
    public int myIndex;  // default is 0

    /**
     * The priority this ThumbnailController should have on the
     * ThumbnailCreationQueue
     */
    private final int DEFAULT_QUEUE_PRIORITY = ThumbnailQueueRequest.MEDIUM_PRIORITY;

    /**
     * returns the Thumbnail that is being controlled by this Controller.
     *
     * @return the Thumbnail
     */
    public Thumbnail getThumbnail() {
        return myThumbnail;
    }

    /**
     * Returns to the caller whether the thumbnail is already showing the node.
     *
     * @param newNavigator The NodeNavigatorInterface from which the node is
     * coming
     * @param newIndex The index position that should be checked.
     * @return true if the indicated node is already showing, false if not
     */
    public boolean isSameNode( NodeNavigatorInterface newNavigator,
            int newIndex ) {
        if ( !newNavigator.equals( myNodeNavigator ) ) {
            return false;
        } else if ( newIndex == myIndex ) {
            LOGGER.fine( String.format( "Same index: %d on same Browser %s. But is it actually the same node?", newIndex, newNavigator.toString() ) );
            //return true;
            SortableDefaultMutableTreeNode testNode = newNavigator.getNode( newIndex );
            LOGGER.fine( String.format( "The refferingNode is the same as the newNode: %b", testNode == myNode ) );
            return testNode == myNode;
        } else {
            LOGGER.fine( String.format( "Same Browser but Different index: new: %d old: %d", newIndex, myIndex ) );
            return false;
        }
    }

    /**
     * Sets the node being visualised by this ThumbnailController object.
     *
     * @param mySetOfNodes The {@link NodeNavigatorInterface} being tracked
     * @param index	The position of this object to be displayed.
     */
    public void setNode( NodeNavigatorInterface mySetOfNodes, int index ) {
        LOGGER.fine( String.format( "Setting Thubnail %d to index %d in Browser %s ", this.hashCode(), index, mySetOfNodes.toString() ) );
        unqueue();

        this.myNodeNavigator = mySetOfNodes;
        this.myIndex = index;
        SortableDefaultMutableTreeNode node = mySetOfNodes.getNode( index );

        this.myNode = node;

        attachChangeListeners();

        if ( node == null ) {
            myThumbnail.setVisible( false );
        } else {
            requestThumbnailCreation( DEFAULT_QUEUE_PRIORITY, false );
        }

        showSlectionStatus();
        determineMailSlectionStatus();
        drawOfflineIcon( myNode );
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
     * GroupInfoChangeListerner.
     */
    private void attachChangeListeners() {
        // unattach from the change Listener
        if ( registeredPictureInfoChangeListener != null ) {
            LOGGER.fine( String.format( "unattaching MyPictureInfoChangeEventHandler %d from Picturinfo %d", myPictureInfoChangeEventHandler.hashCode(), registeredPictureInfoChangeListener.hashCode() ) );
            registeredPictureInfoChangeListener.removePictureInfoChangeListener( myPictureInfoChangeEventHandler );
            registeredPictureInfoChangeListener = null;
        }
        // unattach the change Listener from the GroupInfo
        if ( registeredGroupInfoChangeListener != null ) {
            registeredGroupInfoChangeListener.removeGroupInfoChangeListener( myGroupInfoChangeEventHandler );
            registeredGroupInfoChangeListener = null;
        }

        // attach the change Listener
        if ( myNode != null ) {
            if ( myNode.getUserObject() instanceof PictureInfo ) {
                PictureInfo pictureInfo = (PictureInfo) myNode.getUserObject();
                LOGGER.fine( String.format( "attaching ThumbnailController %d to Picturinfo %d", this.hashCode(), pictureInfo.hashCode() ) );
                pictureInfo.addPictureInfoChangeListener( myPictureInfoChangeEventHandler );
                registeredPictureInfoChangeListener = pictureInfo; //remember so we can poll
            } else if ( myNode.getUserObject() instanceof GroupInfo ) {
                GroupInfo groupInfo = (GroupInfo) myNode.getUserObject();
                groupInfo.addGroupInfoChangeListener( myGroupInfoChangeEventHandler );
                registeredGroupInfoChangeListener = groupInfo; //remember so we can poll
            }
        }
    }

    /**
     * This method forwards the request to create the thumbnail to the
     * ThumbnailCreationQueue
     *
     * @param	priority	The priority with which the request is to be treated on
     * the queue
     * @param	force	Set to true if the thumbnail needs to be rebuilt from
     * source, false if using a cached version is OK.
     */
    public void requestThumbnailCreation( int priority, boolean force ) {
        boolean newRequest = ThumbnailCreationQueue.requestThumbnailCreation(
                this, priority, force );
        if ( newRequest ) {
            setPendingIcon();
        } else {
            LOGGER.fine( String.format( "Why have we just sent in a request for Thumbnail creation for %s when it's already on the queue?", toString() ) );
        }
    }

    /**
     * Sets an icon for a pending state before a final icon is put in place by a
     * ThumbnailCreation
     */
    public void setPendingIcon() {
        if ( myNode == null ) {
            LOGGER.severe( "Referring node is null! How did this happen?" );
            return;
        }
        if ( myNode.getUserObject() instanceof PictureInfo ) {
            myThumbnail.setQueueIcon();
        } else {
            myThumbnail.setLargeFolderIcon();
        }
    }

    /**
     * Returns the maximum unscaled size for the ThumbnailController as a
     * Dimension using the thumbnailSize as width and height.
     *
     * @return The maximum unscaled size of the ThumbnailController
     */
    public Dimension getMaximumUnscaledSize() {
        return new Dimension( myThumbnail.getThumbnailSize(), myThumbnail.getThumbnailSize() );
    }

    /**
     * Removes any request for this thumbnail from the ThumbnailCreationQueue.
     * No problem if it was not on the queue.
     */
    public void unqueue() {
        ThumbnailCreationQueue.removeThumbnailQueueRequest( this );
    }

    /**
     * This method checks if the node is set and whether the highres image is
     * available. If there is a problem the offline icon is drawn over the
     * thumbnail. sets the {@link Thumbnail#drawOfflineIcon} indicator
     * accordingly.
     *
     * @param nodeToCheck The Node to check
     */
    public void drawOfflineIcon( DefaultMutableTreeNode nodeToCheck ) {
        if ( nodeToCheck == null ) {
            myThumbnail.drawOfflineIcon( false );
            return;
        }

        Object userObject = nodeToCheck.getUserObject();
        if ( userObject instanceof PictureInfo ) {
            try {
                ( (PictureInfo) userObject ).getHighresURL().openStream().close();
                myThumbnail.drawOfflineIcon( false );
            } catch ( MalformedURLException x ) {
                myThumbnail.drawOfflineIcon( true );
            } catch ( IOException x ) {
                myThumbnail.drawOfflineIcon( true );
            }
        } else {
            myThumbnail.drawOfflineIcon( false );
        }
    }

    /**
     * Inner class to handle the mouse events on the ThumbnailController
     */
    private class ThumbnailMouseAdapter
            extends MouseAdapter {

        /**
         * overridden to analyse the mouse event and decide whether to display
         * the picture right away (doubleclick) or show the popupMenu.
         */
        @Override
        public void mouseClicked( MouseEvent e ) {
            if ( myNode == null ) {
                return;
            }
            if ( e.getClickCount() > 1 ) {
                doubleClickResponse();
            } else if ( e.getButton() == 3 ) { // popup menu only on 3rd mouse button.
                rightClickResponse( e );
            } else if ( e.getButton() == 1 ) { // first button
                leftClickResponse( e );
            }
        }

        /**
         * Logic for processing a left click on the thumbnail
         */
        private void leftClickResponse( MouseEvent e ) {
            if ( e.isControlDown() ) {
                if ( Settings.getPictureCollection().isSelected( myNode ) ) {
                    Settings.getPictureCollection().removeFromSelection( myNode );
                } else {
                    LOGGER.fine( String.format( "Adding; Now Selected: %d", Settings.getPictureCollection().getSelectedNodes().length ) );
                    Settings.getPictureCollection().addToSelectedNodes( myNode );
                }
            } else {
                if ( Settings.getPictureCollection().isSelected( myNode ) ) {
                    Settings.getPictureCollection().clearSelection();
                } else {
                    Settings.getPictureCollection().clearSelection();
                    Settings.getPictureCollection().addToSelectedNodes( myNode );
                    LOGGER.fine( String.format( "1 selection added; Now Selected: %d", Settings.getPictureCollection().getSelectedNodes().length ) );
                }
            }
        }

        /**
         * Logic for processing a right click on the thumbnail
         */
        private void rightClickResponse( MouseEvent e ) {
            if ( myNode.getUserObject() instanceof PictureInfo ) {
                PicturePopupMenu picturePopupMenu = new PicturePopupMenu( myNodeNavigator, myIndex );
                picturePopupMenu.show( e.getComponent(), e.getX(), e.getY() );
            } else if ( myNode.getUserObject() instanceof GroupInfo ) {
                GroupPopupMenu groupPopupMenu = new GroupPopupMenu( myNode );
                groupPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
            } else {
                LOGGER.severe( String.format( "Processing a right click response on an unknown node type: %s", myNode.getUserObject().getClass().toString() ) );
            }
        }

        /**
         * Logic for processing a doubleclick on the thumbnail
         */
        private void doubleClickResponse() {
            if ( myNode.getUserObject() instanceof PictureInfo ) {
                JpoEventBus.getInstance().post( new ShowPictureRequest( myNode ) );
            } else if ( myNode.getUserObject() instanceof GroupInfo ) {
                JpoEventBus.getInstance().post( new ShowGroupRequest( myNode ) );
            }
        }

    }

    private class MyPictureInfoChangeEventHandler implements PictureInfoChangeListener {

        /**
         * here we get notified by the PictureInfo object that something has
         * changed.
         *
         * @param pictureInfoChangeEvent
         */
        @Override
        public void pictureInfoChangeEvent( PictureInfoChangeEvent pictureInfoChangeEvent ) {
            if ( pictureInfoChangeEvent.getHighresLocationChanged() || pictureInfoChangeEvent.getChecksumChanged() || pictureInfoChangeEvent.getThumbnailChanged() ) {
                requestThumbnailCreation( ThumbnailQueueRequest.HIGH_PRIORITY, false );
            } else if ( pictureInfoChangeEvent.getWasSelected() ) {
                myThumbnail.showAsSelected();
            } else if ( pictureInfoChangeEvent.getWasUnselected() ) {
                myThumbnail.showAsUnselected();
            } else if ( ( pictureInfoChangeEvent.getWasMailSelected() ) || ( pictureInfoChangeEvent.getWasMailUnselected() ) ) {
                determineMailSlectionStatus();
            } else if ( pictureInfoChangeEvent.getRotationChanged() ) {
                requestThumbnailCreation( ThumbnailQueueRequest.HIGH_PRIORITY, true );
            }
        }
    }

    private class MyGroupInfoChangeEventHandler implements GroupInfoChangeListener {

        /**
         * here we get notified by the GroupInfo object that something has
         * changed.
         *
         * @param groupInfoChangeEvent
         */
        @Override
        public void groupInfoChangeEvent( GroupInfoChangeEvent groupInfoChangeEvent ) {
            LOGGER.fine( String.format( "Got a Group Change event: %s", groupInfoChangeEvent.toString() ) );
            if ( groupInfoChangeEvent.getWasSelected() ) {
                myThumbnail.showAsSelected();
            } else if ( groupInfoChangeEvent.getWasUnselected() ) {
                myThumbnail.showAsUnselected();
            }
        }
    }

    /**
     * changes the colour so that the user sees whether the thumbnail is part of
     * the selection
     */
    public void showSlectionStatus() {
        if ( Settings.getPictureCollection().isSelected( myNode ) ) {
            myThumbnail.showAsSelected();
        } else {
            myThumbnail.showAsUnselected();
        }

    }

    /**
     * This method sets the scaling factor for the display of a thumbnail. 0 ..
     * 1
     *
     * @param thumbnailSizeFactor
     */
    public void setFactor( float thumbnailSizeFactor ) {
        LOGGER.fine( String.format( "Scaling factor is being set to %f", thumbnailSizeFactor ) );
        myThumbnail.setFactor( thumbnailSizeFactor );
    }

    /**
     * tells the Thumbnail to show a broken icon
     */
    public void setBrokenIcon() {
        myThumbnail.setBrokenIcon();
    }

    /**
     * This flag indicates where decorations should be drawn at all
     */
    private boolean decorateThumbnails = true;

    /**
     * Determines whether decorations should be drawn or not
     *
     * @param decorateThumbnails
     */
    public void setDecorateThumbnails( boolean decorateThumbnails ) {
        this.decorateThumbnails = decorateThumbnails;
    }

    /**
     * determines if the thumbnail is part of the mail selection and changes the
     * drawMailIcon flag to ensure that the mail icon will be place over the
     * image.
     */
    public void determineMailSlectionStatus() {
        if ( ( myNode != null ) && decorateThumbnails && Settings.getPictureCollection().isMailSelected( myNode ) ) {
            myThumbnail.drawMailIcon( true );
        } else {
            myThumbnail.drawMailIcon( false );
        }

    }



    /**
     * This class extends a DragGestureListener and allows DnD on Thumbnails.
     */
    private class ThumbnailDragGestureListener
            implements DragGestureListener, Serializable {

        /**
         * This method is invoked by the drag and drop framework. It signifies
         * the start of a drag and drop operation. If the event is a copy or
         * move we start the drag and create a Transferable.
         */
        @Override
        public void dragGestureRecognized( DragGestureEvent event ) {
            if ( ( event.getDragAction() & DnDConstants.ACTION_COPY_OR_MOVE ) == 0 ) {
                return;
            }

            JpoTransferable transferable;

            if ( Settings.getPictureCollection().countSelectedNodes() < 1 ) {
                Object[] nodes = { myNode };
                transferable = new JpoTransferable( nodes );
            } else {
                transferable = new JpoTransferable( Settings.getPictureCollection().getSelectedNodes() );
            }

            try {
                event.startDrag( DragSource.DefaultMoveNoDrop, transferable, new ThumbnailDragSourceListener() );
                LOGGER.log( Level.FINE, "Drag started on node: {0}", myNode.getUserObject().toString() );
            } catch ( InvalidDnDOperationException x ) {
                LOGGER.log( Level.FINE, "Threw a InvalidDnDOperationException: reason: {0}", x.getMessage() );
            }
        }
    }

    /**
     * This class extends a DragSourceListener for tracking the drag operation
     * originating from this thumbnail.
     */
    private class ThumbnailDragSourceListener
            implements DragSourceListener, Serializable {

        /**
         * this callback method is invoked after the dropTaget had a chance to
         * evaluate the drag event and was given the option of rejecting or
         * modifying the event. This method sets the cursor to reflect whether a
         * copy, move or no drop is possible.
         */
        @Override
        public void dragEnter( DragSourceDragEvent event ) {
            Tools.setDragCursor( event );
        }

        /**
         * this callback method is invoked after the dropTaget had a chance to
         * evaluate the dragOver event and was given the option of rejecting or
         * modifying the event. This method sets the cursor to reflect whether a
         * copy, move or no drop is possible.
         */
        @Override
        public void dragOver( DragSourceDragEvent event ) {
            Tools.setDragCursor( event );
        }

        /**
         * this callback method is invoked to tell the dragSource that the drag
         * has moved on to something else.
         */
        @Override
        public void dragExit( DragSourceEvent event ) {
        }

        /**
         * this callback method is invoked when the user presses or releases
         * shift when doing a drag. He can signal that he wants to change the
         * copy / move of the operation.
         */
        @Override
        public void dropActionChanged( DragSourceDragEvent event ) {
            Tools.setDragCursor( event );
        }

        /**
         * this callback message goes to DragSourceListener, informing it that
         * the dragging has ended.
         */
        @Override
        public void dragDropEnd( DragSourceDropEvent event ) {
            Settings.getPictureCollection().clearSelection();
        }
    }

    /**
     * Give some info about the ThumbnailController.
     *
     * @return some info about the ThumbnailController
     */
    @Override
    public String toString() {
        String description = "none";
        if ( myNode != null ) {
            description = myNode.toString();
        }
        return String.format( "Thumbnail: HashCode: %d, referringNode: %s", hashCode(), description );
    }

    @Override
    public void handleJpoDropTargetDropEvent( DropTargetDropEvent event ) {
        myNode.executeDrop( event );
    }
}
