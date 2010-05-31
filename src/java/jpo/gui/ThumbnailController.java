package jpo.gui;

import jpo.dataModel.NodeNavigatorInterface;
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
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.logging.Logger;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.GroupInfoChangeEvent;
import jpo.dataModel.GroupInfoChangeListener;
import jpo.dataModel.Settings;
import jpo.dataModel.Tools;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.PictureInfoChangeEvent;
import jpo.dataModel.PictureInfoChangeListener;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.gui.swing.Thumbnail;

/*
ThumbnailController.java:  class that displays a visual respresentation of the specified node

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
 *   ThumbnailController displays a visual representation of the specified node. On a Picture this
 *   is a ThumbnailController thereof, on a Group it is a folder icon.
 *
 * TODO: move the methods to make the ThumbnailController back into this class from ThumbnailCreationFactory
 * TODO: split this class into a GUI component that deals with the GUI stuff and one which deals with the
 * creation stuff and all the model notifcations. I.e. MVC..
 */
public class ThumbnailController
        implements DropTargetListener,
        PictureInfoChangeListener,
        GroupInfoChangeListener,
        TreeModelListener {

    /**
     *   Creates a new ThumbnailController object with a reference to the ThumbnailPanelController which
     *   must receive notifications that a new node should be selected.
     *
     **/
    public ThumbnailController() {
        this( Settings.thumbnailSize );
    }


    /**
     * Creates a new ThumbnailController object.
     * This must happen on the EDT because it creates a Thumbnail SWING component
     *
     *   @param	thumbnailSize	The size in which the thumbnail is to be created
     **/
    public ThumbnailController( final int thumbnailSize ) {
        Tools.checkEDT();
        theThumbnail = new Thumbnail();
        theThumbnail.thumbnailSize = thumbnailSize;
        theThumbnail.addMouseListener( new ThumbnailMouseAdapter() );
        // set up drag & drop
        dropTarget = new DropTarget( theThumbnail, ThumbnailController.this );
        myDragGestureListener = new ThumbnailDragGestureListener();
        dragSource.createDefaultDragGestureRecognizer(
                theThumbnail, DnDConstants.ACTION_COPY_OR_MOVE, myDragGestureListener );

        // attach the ThumbnailController to the Tree Model to get notifications.
        Settings.pictureCollection.getTreeModel().addTreeModelListener( this );
    }

    /**
     *  a link to the SortableDefaultMutableTreeNode in the data model.
     *  This allows thumbnails to be selected by sending a
     *  nodeSelected event to the data model.
     **/
    public SortableDefaultMutableTreeNode referringNode;

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( ThumbnailController.class.getName() );

    /**
     *  A set of picture nodes of which one indicated by {@link #myIndex} is to be shown
     */
    private NodeNavigatorInterface myThumbnailBrowser = null;

    /**
     *  the Index position in the {@link #myThumbnailBrowser} which is being shown by this
     *  component.
     */
    public int myIndex = 0;

    /**
     *   enables this component to be a Drag Source
     */
    public DragSource dragSource = DragSource.getDefaultDragSource();

    /**
     *   enables this component to be a dropTarget
     */
    public DropTarget dropTarget;

    /**
     *   The DragGestureListener for a thumbnail.
     */
    private DragGestureListener myDragGestureListener;

    /**
     *  The DragSourceListener for a thumbnail.
     */
    private DragSourceListener myDragSourceListener = new ThumbnailDragSourceListener();

    /**
     * The priority this ThumbnailController should have on the ThumbnailCreationQueue
     */
    private int priority = ThumbnailQueueRequest.MEDIUM_PRIORITY;

    private Thumbnail theThumbnail;


    /**
     * returns the Thumbnail that is being controlled by this Controller.
     * @return the Thumbnail
     */
    public Thumbnail getThumbnail() {
        return theThumbnail;
    }

    /**
     * Handle for operations that affect the collection.
     */
    private Jpo collectionController;

    /**
     * remember where we registered as a PictureInfoListener
     */
    private PictureInfo registeredPictureInfoChangeListener;

    /**
     * remember where we registered as a GroupInfoListener
     */
    private GroupInfo registeredGroupInfoChangeListener;


    /**
     * Returns to the caller whether the thumbnail is already showing the node.
     * @param newBrowser The NodeNavigatorInterface from which the node is coming
     * @param newIndex The index position that should be checked.
     * @return true if the indicated node is already showing, false if not
     */
    public boolean isSameNode( NodeNavigatorInterface newBrowser,
            int newIndex ) {
        if ( !newBrowser.equals( myThumbnailBrowser ) ) {
            if ( myThumbnailBrowser == null ) {
                logger.fine( String.format( "Not the same Browser %s  vs.  null", newBrowser.toString() ) );
            } else {
                logger.fine( String.format( "Not the same Browser %s  vs.  %s ", newBrowser.toString(), myThumbnailBrowser.toString() ) );
            }
            return false;
        } else if ( newIndex == myIndex ) {
            logger.fine( String.format( "Same index: %d on same Browser %s. But is it actually the same node?", newIndex, newBrowser.toString() ) );
            //return true;
            SortableDefaultMutableTreeNode testNode = newBrowser.getNode( newIndex );
            logger.fine( String.format( "The refferingNode is the same as the newNode: %b", testNode == referringNode ) );
            return testNode == referringNode;
        } else {
            logger.fine( String.format( "Same Browser but Different index: new: %d old: %d", newIndex, myIndex ) );
            return false;
        }
    }


    /**
     *  Sets the node being visualised by this ThumbnailController object.
     *
     *  @param mySetOfNodes  The {@link NodeNavigatorInterface} being tracked
     *  @param index	The position of this object to be displayed.
     */
    public void setNode( NodeNavigatorInterface mySetOfNodes, int index ) {
        logger.fine( String.format( "Setting Thubnail %d to index %d in Browser %s ", this.hashCode(), index, mySetOfNodes.toString() ) );
        this.myThumbnailBrowser = mySetOfNodes;
        this.myIndex = index;
        SortableDefaultMutableTreeNode node = mySetOfNodes.getNode( index );

        unqueue();

        this.referringNode = node;

        attachChangeListeners();

        if ( node == null ) {
            theThumbnail.setVisible( false );
        } else {
            requestThumbnailCreation( priority, false );
        }

        showSlectionStatus();
        determineMailSlectionStatus();
        determineImageStatus( referringNode );
    }


    /**
     * Unattaches the ThumbnailController from the previously linked
     * PictureInfoChangeListener or GroupInfoChangeListener (if any)
     * and attaches it to the new PictureInfoChangeListener or GroupInfoChangeListerner.
     */
    private void attachChangeListeners() {
        // unattach from the change Listener
        if ( registeredPictureInfoChangeListener != null ) {
            logger.fine( String.format( "unattaching ThumbnailController %d from Picturinfo %d", this.hashCode(), registeredPictureInfoChangeListener.hashCode() ) );
            registeredPictureInfoChangeListener.removePictureInfoChangeListener( this );
            registeredPictureInfoChangeListener = null;
        }
        // unattach the change Listener from the GroupInfo
        if ( registeredGroupInfoChangeListener != null ) {
            registeredGroupInfoChangeListener.removeGroupInfoChangeListener( this );
            registeredGroupInfoChangeListener = null;
        }

        // attach the change Listener
        if ( referringNode != null ) {
            if ( referringNode.getUserObject() instanceof PictureInfo ) {
                PictureInfo pi = (PictureInfo) referringNode.getUserObject();
                logger.fine( String.format( "attaching ThumbnailController %d to Picturinfo %d", this.hashCode(), pi.hashCode() ) );
                pi.addPictureInfoChangeListener( this );
                registeredPictureInfoChangeListener = pi; //remember so we can poll
            } else if ( referringNode.getUserObject() instanceof GroupInfo ) {
                GroupInfo pi = (GroupInfo) referringNode.getUserObject();
                pi.addGroupInfoChangeListener( this );
                registeredGroupInfoChangeListener = pi; //remember so we can poll

            }
        }
    }


    /**
     *  This method forwards the request to create the thumbnail to the ThumbnailCreationQueue
     *  @param	priority	The priority with which the request is to be treated on the queue
     *  @param	force		Set to true if the thumbnail needs to be rebuilt from source, false
     *				if using a cached version is OK.
     */
    public void requestThumbnailCreation( int priority, boolean force ) {
        boolean newRequest = ThumbnailCreationQueue.requestThumbnailCreation(
                this, priority, force );
        if ( newRequest ) {
            setPendingIcon();
        } else {
            logger.fine( String.format( "Why have we just sent in a request for Thumbnail creation for %s when it's already on the queue?", toString() ) );
        }
    }


    /**
     * Sets an icon for a pending state before a final icon is put in place by a ThumbnailCreation
     */
    public void setPendingIcon() {
        //logger.info( "Setting pending icon");
        //Thread.dumpStack();
        if ( referringNode == null ) {
            logger.info( "Referring node is null! How did this happen?" );
            Thread.dumpStack();
            return;
        }
        if ( referringNode.getUserObject() instanceof PictureInfo ) {
            theThumbnail.setQueueIcon();
        } else {
            theThumbnail.setLargeFolderIcon();
        }
    }


    /**
     *   Returns the maximum unscaled size for the ThumbnailController as a Dimension using the thumbnailSize
     *   as width and height.
     * @return
     */
    public Dimension getMaximumUnscaledSize() {
        return new Dimension( theThumbnail.thumbnailSize, theThumbnail.thumbnailSize );
    }


    /**
     *  Removes any request for this thumbnail from the ThumbnailCreationQueue. No problem if
     *  it was not on the queue.
     */
    public void unqueue() {
        ThumbnailCreationQueue.removeThumbnailRequest( this );
    }


    /**
     *  This method determines whether the source image is available online and sets the {@link #drawOfflineIcon}
     *  indicator accordingly.
     * @param n
     */
    public void determineImageStatus( DefaultMutableTreeNode n ) {
        if ( n == null ) {
            theThumbnail.drawOfflineIcon( false );
            return;
        }

        Object userObject = n.getUserObject();
        if ( userObject instanceof PictureInfo ) {
            try {
                ( (PictureInfo) userObject ).getHighresURL().openStream().close();
                theThumbnail.drawOfflineIcon( false );
            } catch ( MalformedURLException x ) {
                theThumbnail.drawOfflineIcon( true );
            } catch ( IOException x ) {
                theThumbnail.drawOfflineIcon( true );
            }
        } else {
            theThumbnail.drawOfflineIcon( false );
        }
    }

    /**
     *  Inner class to handle the mouse events on the ThumbnailController
     */
    private class ThumbnailMouseAdapter
            extends MouseAdapter {

        /**
         *   overridden to analyse the mouse event and decide whether
         *   to display the picture right away (doubleclick) or show
         *   the popupMenu.
         */
        @Override
        public void mouseClicked( MouseEvent e ) {
            if ( referringNode == null ) {
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
    }


    /**
     * Logic for processing a leftclick on the thumbnail
     */
    private void leftClickResponse( MouseEvent e ) {
        if ( e.isControlDown() ) {
            if ( Settings.pictureCollection.isSelected( referringNode ) ) {
                Settings.pictureCollection.removeFromSelection( referringNode );
            } else {
                logger.fine( String.format( "Adding; Now Selected: %d", Settings.pictureCollection.getSelectedNodesAsVector().size() ) );
                Settings.pictureCollection.addToSelectedNodes( referringNode );
            }
        } else {
            if ( Settings.pictureCollection.isSelected( referringNode ) ) {
                Settings.pictureCollection.clearSelection();
            } else {
                Settings.pictureCollection.clearSelection();
                Settings.pictureCollection.addToSelectedNodes( referringNode );
                logger.fine( String.format( "1 selection added; Now Selected: %d", Settings.pictureCollection.getSelectedNodesAsVector().size() ) );
            }
        }
    }


    /**
     * Logic for processing a right click on the thumbnail
     */
    private void rightClickResponse( MouseEvent e ) {
        if ( referringNode.getUserObject() instanceof PictureInfo ) {
            PicturePopupMenu picturePopupMenu = new PicturePopupMenu( myThumbnailBrowser, myIndex );
            picturePopupMenu.show( e.getComponent(), e.getX(), e.getY() );
        } else if ( referringNode.getUserObject() instanceof GroupInfo ) {
            GroupPopupMenu groupPopupMenu = new GroupPopupMenu( Jpo.collectionJTreeController, referringNode );
            groupPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
        } else {
            logger.severe( "Processing a right click response on an unknown node." );
            Thread.dumpStack();
        }
    }


    /**
     * Logic for processing a doubleclick on the thumbnail
     */
    private void doubleClickResponse() {
        if ( referringNode.getUserObject() instanceof PictureInfo ) {
            Jpo.browsePictures( referringNode );
        } else if ( referringNode.getUserObject() instanceof GroupInfo ) {
            Jpo.positionToNode( referringNode );
        }
    }


    /**
     *  here we get notified by the PictureInfo object that something has
     *  changed.
     */
    public void pictureInfoChangeEvent( PictureInfoChangeEvent e ) {
        if ( e.getHighresLocationChanged() || e.getChecksumChanged() || e.getLowresLocationChanged() || e.getThumbnailChanged() || e.getRotationChanged() ) {
            requestThumbnailCreation( ThumbnailQueueRequest.HIGH_PRIORITY, false );
        } else if ( e.getWasSelected() ) {
            theThumbnail.showAsSelected();
        } else if ( e.getWasUnselected() ) {
            theThumbnail.showAsUnselected();
        } else if ( ( e.getWasMailSelected() ) || ( e.getWasMailUnselected() ) ) {
            determineMailSlectionStatus();
        }
    }


    /**
     *  here we get notified by the GroupInfo object that something has
     *  changed.
     */
    public void groupInfoChangeEvent( GroupInfoChangeEvent e ) {
        logger.fine( String.format( "Got a Group Change event: %s", e.toString() ) );
        if ( e.getLowresLocationChanged() ) {
            requestThumbnailCreation( ThumbnailQueueRequest.HIGH_PRIORITY, false );
        } else if ( e.getWasSelected() ) {
            theThumbnail.showAsSelected();
        } else if ( e.getWasUnselected() ) {
            theThumbnail.showAsUnselected();
        }
    }


    /**
     *  changes the color so that the user sees whether the thumbnail is part of the selection
     */
    public void showSlectionStatus() {
        if ( Settings.pictureCollection.isSelected( referringNode ) ) {
            theThumbnail.showAsSelected();
        } else {
            theThumbnail.showAsUnselected();
        }

    }


    /**
     *  This method sets the scaling factor for the display of a thumbnail.
     *  0 .. 1
     * @param thumbnailSizeFactor
     */
    public void setFactor( float thumbnailSizeFactor ) {
        logger.fine( String.format( "Scaling factor is being set to %f", thumbnailSizeFactor ) );
        theThumbnail.setFactor( thumbnailSizeFactor );
    }


    /**
     * tells the Thumbnail to show a broken icon
     */
    public void setBrokenIcon() {
        theThumbnail.setBrokenIcon();
    }

    /**
     *  This flag indicates where decorations should be drawn at all
     */
    private boolean decorateThumbnails = true;


    /**
     * Determines whether decorations should be drawn or not
     *
     * TODO: Whatever effect does this have?
     * @param b
     */
    public void setDecorateThumbnails( boolean b ) {
        if ( decorateThumbnails != b ) {
            decorateThumbnails = b;
        }
    }


    /**
     *  determines if the thumbnail is part of the mail selection and changes the drawMailIcon
     *  flag to ensure that the mail icon will be place over the image.
     */
    public void determineMailSlectionStatus() {
        if ( ( referringNode != null ) && decorateThumbnails && Settings.pictureCollection.isMailSelected( referringNode ) ) {
            theThumbnail.drawMailIcon( true );
        } else {
            theThumbnail.drawMailIcon( false );
        }

    }

    // Here we are not that interested in TreeModel change events other than to find out if our
    // current node was removed in which case we close the Window.

    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     */
    public void treeNodesChanged( TreeModelEvent e ) {
        logger.fine( String.format( "ThumbnailController %d detected a treeNodesChanged event: %s", hashCode(), e ) );

        // find out whether our node was changed
        Object[] children = e.getChildren();
        if ( children == null ) {
            // the root path does not have children as it doesn't have a parent
            logger.fine( "Supposedly we got the root node?" );
            return;
        }

        for ( int i = 0; i <
                children.length; i++ ) {
            if ( children[i] == referringNode ) {
                // we are displaying a changed node. What changed?
                Object userObject = referringNode.getUserObject();
                if ( userObject instanceof GroupInfo ) {
                    // determine if the icon changed
                    // logger.info( "ThumbnailController should be reloading the icon..." );
                    requestThumbnailCreation( ThumbnailQueueRequest.HIGH_PRIORITY, false );
                } else {
                    logger.fine( String.format( "ThumbnailController %d detected a treeNodesChanged event: %s on a PictureInfo node", hashCode(), e ) );

                }
                // what do we do here when a PictureInfor has updated?

            }
        }
    }


    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     */
    public void treeNodesInserted( TreeModelEvent e ) {
        logger.fine( String.format( "ThumbnailController %d detected a treeNodesInserted event: %s", hashCode(), e ) );
    }


    /**
     *  The TreeModelListener interface tells us of tree node removal events.
     * @param e
     */
    public void treeNodesRemoved( TreeModelEvent e ) {
        logger.fine( String.format( "ThumbnailController %d detected a treeNodesRemoved event: %s", hashCode(), e ) );
    }


    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     */
    public void treeStructureChanged( TreeModelEvent e ) {
        logger.fine( String.format( "ThumbnailController %d detected a treeStructureChanged event: %s", hashCode(), e ) );

        attachChangeListeners();

    }


    /**
     *   this callback method is invoked every time something is
     *   dragged onto the ThumbnailController. We check if the desired DataFlavor is
     *   supported and then reject the drag if it is not.
     * @param event
     */
    public void dragEnter( DropTargetDragEvent event ) {
        if ( !event.isDataFlavorSupported( JpoTransferable.jpoNodeFlavor ) ) {
            event.rejectDrag();
        }

    }


    /**
     *   this callback method is invoked every time something is 
     *   dragged over the ThumbnailController. We could do some highlighting if
     *   we so desired.
     * @param event
     */
    public void dragOver( DropTargetDragEvent event ) {
        if ( !event.isDataFlavorSupported( JpoTransferable.jpoNodeFlavor ) ) {
            event.rejectDrag();
        }

    }


    /**
     *   this callback method is invoked when the user presses or releases shift when
     *   doing a drag. He can signal that he wants to change the copy / move of the 
     *   operation. This method could intercept this change and could modify the event
     *   if it needs to.  On Thumbnails this does nothing.
     * @param event
     */
    public void dropActionChanged( DropTargetDragEvent event ) {
    }


    /**
     *   this callback method is invoked to tell the dropTarget that the drag has moved on
     *   to something else. We do nothing here.
     * @param event
     */
    public void dragExit( DropTargetEvent event ) {
        logger.fine( "Thumbnail.dragExit( DropTargetEvent ): invoked" );
    }


    /**
     *  This method is called when the drop occurs. It gives the hard work to the
     *  SortableDefaultMutableTreeNode.
     * @param event
     */
    public void drop( DropTargetDropEvent event ) {
        referringNode.executeDrop( event );
    }

    /**
     *   This class extends a DragGestureListener and allows DnD on Thumbnails.
     */
    private class ThumbnailDragGestureListener
            implements DragGestureListener, Serializable {

        /**
         *   This method is invoked by the drag and drop framework. It signifies
         *   the start of a drag and drop operation. If the event is a copy or move we
         *   start the drag and create a Transferable.
         */
        public void dragGestureRecognized( DragGestureEvent event ) {
            if ( ( event.getDragAction() & DnDConstants.ACTION_COPY_OR_MOVE ) == 0 ) {
                return;
            }

            JpoTransferable t;

            if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
                Object[] nodes = { referringNode };
                t = new JpoTransferable( nodes );
            } else {
                t = new JpoTransferable( Settings.pictureCollection.getSelectedNodes() );
            }

            try {
                event.startDrag( DragSource.DefaultMoveNoDrop, t, myDragSourceListener );
                logger.fine( "Drag started on node: " + referringNode.getUserObject().toString() );
            } catch ( InvalidDnDOperationException x ) {
                logger.fine( "Threw a InvalidDnDOperationException: reason: " + x.getMessage() );
            }
        }
    }

    /**
     *  This class extends a DragSourceListener for tracking the drag operation originating
     *  from this thumbnail.
     */
    private class ThumbnailDragSourceListener
            implements DragSourceListener, Serializable {

        /**
         *  this callback method is invoked after the dropTaget had a chance
         *  to evaluate the drag event and was given the option of rejecting or
         *  modifying the event. This method sets the cursor to reflect
         *  whether a copy, move or no drop is possible.
         */
        public void dragEnter( DragSourceDragEvent event ) {
            Tools.setDragCursor( event );
        }


        /**
         *  this callback method is invoked after the dropTaget had a chance
         *  to evaluate the dragOver event and was given the option of rejecting or
         *  modifying the event. This method sets the cursor to reflect
         *  whether a copy, move or no drop is possible.
         */
        public void dragOver( DragSourceDragEvent event ) {
            Tools.setDragCursor( event );
        }


        /**
         *   this callback method is invoked to tell the dragSource that the drag has moved on
         *   to something else.
         */
        public void dragExit( DragSourceEvent event ) {
        }


        /**
         *   this callback method is invoked when the user presses or releases shift when
         *   doing a drag. He can signal that he wants to change the copy / move of the
         *   operation.
         */
        public void dropActionChanged( DragSourceDragEvent event ) {
            Tools.setDragCursor( event );
        }


        /**
         *   this callback message goes to DragSourceListener, informing it that the dragging
         *   has ended.
         */
        public void dragDropEnd( DragSourceDropEvent event ) {
            Settings.pictureCollection.clearSelection();
        }
    }


    /**
     * Give some info about the ThumbnailController.
     * @return some info about the ThumbnailController
     */
    @Override
    public String toString() {
        String myNode = "none";
        if ( referringNode != null ) {
            myNode = referringNode.toString();
        }
        return String.format( "Thumbnail: HashCode: %d, referringNode: %s", hashCode(), myNode );
    }
}
