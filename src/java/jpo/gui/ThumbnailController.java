package jpo.gui;

import jpo.gui.swing.GroupPopupMenu;
import java.util.logging.Level;
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
 *   ThumbnailController displays a visual representation of the specified node. On a Picture this
 *   is a ThumbnailController thereof, on a Group it is a folder icon.
 *
 * TODO: move the methods to make the ThumbnailController back into this class from ThumbnailCreationFactory
 * TODO: split this class into a GUI component that deals with the GUI stuff and one which deals with the
 * creation stuff and all the model notifications. I.e. MVC..
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
    public SortableDefaultMutableTreeNode myNode;

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ThumbnailController.class.getName() );
    //{ LOGGER.setLevel( Level.ALL ); }

    /**
     *  A set of picture nodes of which one indicated by {@link #myIndex} is to be shown
     */
    private NodeNavigatorInterface myNodeNavigator = null;

    /**
     *  the Index position in the {@link #myNodeNavigator} which is being shown by this
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
    private final DragSourceListener myDragSourceListener = new ThumbnailDragSourceListener();

    /**
     * The priority this ThumbnailController should have on the ThumbnailCreationQueue
     */
    private final int priority = ThumbnailQueueRequest.MEDIUM_PRIORITY;

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
     * @param newNavigator The NodeNavigatorInterface from which the node is coming
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
     *  Sets the node being visualised by this ThumbnailController object.
     *
     *  @param mySetOfNodes  The {@link NodeNavigatorInterface} being tracked
     *  @param index	The position of this object to be displayed.
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
            theThumbnail.setVisible( false );
        } else {
            requestThumbnailCreation( priority, false );
        }

        showSlectionStatus();
        determineMailSlectionStatus();
        determineImageStatus( myNode );
    }


    /**
     * Unattaches the ThumbnailController from the previously linked
     * PictureInfoChangeListener or GroupInfoChangeListener (if any)
     * and attaches it to the new PictureInfoChangeListener or GroupInfoChangeListerner.
     */
    private void attachChangeListeners() {
        // unattach from the change Listener
        if ( registeredPictureInfoChangeListener != null ) {
            LOGGER.fine( String.format( "unattaching ThumbnailController %d from Picturinfo %d", this.hashCode(), registeredPictureInfoChangeListener.hashCode() ) );
            registeredPictureInfoChangeListener.removePictureInfoChangeListener( this );
            registeredPictureInfoChangeListener = null;
        }
        // unattach the change Listener from the GroupInfo
        if ( registeredGroupInfoChangeListener != null ) {
            registeredGroupInfoChangeListener.removeGroupInfoChangeListener( this );
            registeredGroupInfoChangeListener = null;
        }

        // attach the change Listener
        if ( myNode != null ) {
            if ( myNode.getUserObject() instanceof PictureInfo ) {
                PictureInfo pi = (PictureInfo) myNode.getUserObject();
                LOGGER.fine( String.format( "attaching ThumbnailController %d to Picturinfo %d", this.hashCode(), pi.hashCode() ) );
                pi.addPictureInfoChangeListener( this );
                registeredPictureInfoChangeListener = pi; //remember so we can poll
            } else if ( myNode.getUserObject() instanceof GroupInfo ) {
                GroupInfo pi = (GroupInfo) myNode.getUserObject();
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
            LOGGER.fine( String.format( "Why have we just sent in a request for Thumbnail creation for %s when it's already on the queue?", toString() ) );
        }
    }


    /**
     * Sets an icon for a pending state before a final icon is put in place by a ThumbnailCreation
     */
    public void setPendingIcon() {
        if ( myNode == null ) {
            LOGGER.severe( "Referring node is null! How did this happen?" );
            Thread.dumpStack();
            return;
        }
        if ( myNode.getUserObject() instanceof PictureInfo ) {
            theThumbnail.setQueueIcon();
        } else {
            theThumbnail.setLargeFolderIcon();
        }
    }


    /**
     *   Returns the maximum unscaled size for the ThumbnailController as a Dimension using the thumbnailSize
     *   as width and height.
     * @return The maximum unscaled size of the ThumbnailController
     */
    public Dimension getMaximumUnscaledSize() {
        return new Dimension( theThumbnail.thumbnailSize, theThumbnail.thumbnailSize );
    }


    /**
     *  Removes any request for this thumbnail from the ThumbnailCreationQueue. No problem if
     *  it was not on the queue.
     */
    public void unqueue() {
        ThumbnailCreationQueue.removeThumbnailQueueRequest( this );
    }


    /**
     *  This method determines whether the source image is available online and sets the {@link Thumbnail#drawOfflineIcon}
     *  indicator accordingly.
     * @param nodeToCheck The Node to check
     */
    public void determineImageStatus( DefaultMutableTreeNode nodeToCheck ) {
        if ( nodeToCheck == null ) {
            theThumbnail.drawOfflineIcon( false );
            return;
        }

        Object userObject = nodeToCheck.getUserObject();
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
    }


    /**
     * Logic for processing a left click on the thumbnail
     */
    private void leftClickResponse( MouseEvent e ) {
        if ( e.isControlDown() ) {
            if ( Settings.pictureCollection.isSelected( myNode ) ) {
                Settings.pictureCollection.removeFromSelection( myNode );
            } else {
                LOGGER.fine( String.format( "Adding; Now Selected: %d", Settings.pictureCollection.getSelectedNodes().length ) );
                Settings.pictureCollection.addToSelectedNodes( myNode );
            }
        } else {
            if ( Settings.pictureCollection.isSelected( myNode ) ) {
                Settings.pictureCollection.clearSelection();
            } else {
                Settings.pictureCollection.clearSelection();
                Settings.pictureCollection.addToSelectedNodes( myNode );
                LOGGER.fine( String.format( "1 selection added; Now Selected: %d", Settings.pictureCollection.getSelectedNodes().length ) );
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
            GroupPopupMenu groupPopupMenu = new GroupPopupMenu( Jpo.collectionJTreeController, myNode );
            groupPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
        } else {
            LOGGER.severe( String.format( "Processing a right click response on an unknown node type: %s", myNode.getUserObject().getClass().toString() ) );
            Thread.dumpStack();
        }
    }


    /**
     * Logic for processing a doubleclick on the thumbnail
     */
    private void doubleClickResponse() {
        if ( myNode.getUserObject() instanceof PictureInfo ) {
            Jpo.browsePictures( myNode );
        } else if ( myNode.getUserObject() instanceof GroupInfo ) {
            Jpo.positionToNode( myNode );
        }
    }


    /**
     *  here we get notified by the PictureInfo object that something has
     *  changed.
     */
    @Override
    public void pictureInfoChangeEvent( PictureInfoChangeEvent e ) {
        if ( e.getHighresLocationChanged() || e.getChecksumChanged() || e.getLowresLocationChanged() || e.getThumbnailChanged()  ) {
            requestThumbnailCreation( ThumbnailQueueRequest.HIGH_PRIORITY, false );
        } else if ( e.getWasSelected() ) {
            theThumbnail.showAsSelected();
        } else if ( e.getWasUnselected() ) {
            theThumbnail.showAsUnselected();
        } else if ( ( e.getWasMailSelected() ) || ( e.getWasMailUnselected() ) ) {
            determineMailSlectionStatus();
        } else if ( e.getRotationChanged() ) {
            requestThumbnailCreation( ThumbnailQueueRequest.HIGH_PRIORITY, true );
        }
    }


    /**
     *  here we get notified by the GroupInfo object that something has
     *  changed.
     */
    @Override
    public void groupInfoChangeEvent( GroupInfoChangeEvent e ) {
        LOGGER.fine( String.format( "Got a Group Change event: %s", e.toString() ) );
        if ( e.getLowresLocationChanged() ) {
            requestThumbnailCreation( ThumbnailQueueRequest.HIGH_PRIORITY, false );
        } else if ( e.getWasSelected() ) {
            theThumbnail.showAsSelected();
        } else if ( e.getWasUnselected() ) {
            theThumbnail.showAsUnselected();
        }
    }


    /**
     *  changes the colour so that the user sees whether the thumbnail is part of the selection
     */
    public void showSlectionStatus() {
        if ( Settings.pictureCollection.isSelected( myNode ) ) {
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
        LOGGER.fine( String.format( "Scaling factor is being set to %f", thumbnailSizeFactor ) );
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
        if ( ( myNode != null ) && decorateThumbnails && Settings.pictureCollection.isMailSelected( myNode ) ) {
            theThumbnail.drawMailIcon( true );
        } else {
            theThumbnail.drawMailIcon( false );
        }

    }


    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     */
    @Override
    public void treeNodesChanged( TreeModelEvent e ) {
        LOGGER.fine( String.format( "ThumbnailController %d detected a treeNodesChanged event: %s", hashCode(), e ) );

        // find out whether our node was changed
        Object[] children = e.getChildren();
        if ( children == null ) {
            // the root path does not have children as it doesn'transferable have a parent
            LOGGER.fine( "Supposedly we got the root node?" );
            return;
        }

        for ( Object children1 : children ) {
            if ( children1 == myNode ) {
                // we are displaying a changed node. What changed?
                Object userObject = myNode.getUserObject();
                if ( userObject instanceof GroupInfo ) {
                    // determine if the icon changed
                    // LOGGER.info( "ThumbnailController should be reloading the icon..." );
                    requestThumbnailCreation( ThumbnailQueueRequest.HIGH_PRIORITY, false );
                } else {
                    LOGGER.fine( String.format( "ThumbnailController %d detected a treeNodesChanged event: %s on a PictureInfo node", hashCode(), e ) );

                }
            }
        }
    }


    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     */
    @Override
    public void treeNodesInserted( TreeModelEvent e ) {
        LOGGER.fine( String.format( "ThumbnailController %d detected a treeNodesInserted event: %s", hashCode(), e ) );
    }


    /**
     *  The TreeModelListener interface tells us of tree node removal events.
     * @param e
     */
    @Override
    public void treeNodesRemoved( TreeModelEvent e ) {
        LOGGER.fine( String.format( "ThumbnailController %d detected a treeNodesRemoved event: %s", hashCode(), e ) );
    }


    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     */
    @Override
    public void treeStructureChanged( TreeModelEvent e ) {
        LOGGER.fine( String.format( "ThumbnailController %d detected a treeStructureChanged event: %s", hashCode(), e ) );

        attachChangeListeners();

    }


    /**
     *   this callback method is invoked every time something is
     *   dragged onto the ThumbnailController. We check if the desired DataFlavor is
     *   supported and then reject the drag if it is not.
     * @param event
     */
    @Override
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
    @Override
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
    @Override
    public void dropActionChanged( DropTargetDragEvent event ) {
    }


    /**
     *   this callback method is invoked to tell the dropTarget that the drag has moved on
     *   to something else. We do nothing here.
     * @param event
     */
    @Override
    public void dragExit( DropTargetEvent event ) {
        LOGGER.fine( "Thumbnail.dragExit( DropTargetEvent ): invoked" );
    }


    /**
     *  This method is called when the drop occurs. It gives the hard work to the
     *  SortableDefaultMutableTreeNode.
     * @param event
     */
    @Override
    public void drop( DropTargetDropEvent event ) {
        myNode.executeDrop( event );
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
        @Override
        public void dragGestureRecognized( DragGestureEvent event ) {
            if ( ( event.getDragAction() & DnDConstants.ACTION_COPY_OR_MOVE ) == 0 ) {
                return;
            }

            JpoTransferable transferable;

            if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
                Object[] nodes = { myNode };
                transferable = new JpoTransferable( nodes );
            } else {
                transferable = new JpoTransferable( Settings.pictureCollection.getSelectedNodes() );
            }

            try {
                event.startDrag( DragSource.DefaultMoveNoDrop, transferable, myDragSourceListener );
                LOGGER.log( Level.FINE, "Drag started on node: {0}", myNode.getUserObject().toString());
            } catch ( InvalidDnDOperationException x ) {
                LOGGER.log( Level.FINE, "Threw a InvalidDnDOperationException: reason: {0}", x.getMessage());
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
        @Override
        public void dragEnter( DragSourceDragEvent event ) {
            Tools.setDragCursor( event );
        }


        /**
         *  this callback method is invoked after the dropTaget had a chance
         *  to evaluate the dragOver event and was given the option of rejecting or
         *  modifying the event. This method sets the cursor to reflect
         *  whether a copy, move or no drop is possible.
         */
        @Override
        public void dragOver( DragSourceDragEvent event ) {
            Tools.setDragCursor( event );
        }


        /**
         *   this callback method is invoked to tell the dragSource that the drag has moved on
         *   to something else.
         */
        @Override
        public void dragExit( DragSourceEvent event ) {
        }


        /**
         *   this callback method is invoked when the user presses or releases shift when
         *   doing a drag. He can signal that he wants to change the copy / move of the
         *   operation.
         */
        @Override
        public void dropActionChanged( DragSourceDragEvent event ) {
            Tools.setDragCursor( event );
        }


        /**
         *   this callback message goes to DragSourceListener, informing it that the dragging
         *   has ended.
         */
        @Override
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
        String description = "none";
        if ( myNode != null ) {
            description = myNode.toString();
        }
        return String.format( "Thumbnail: HashCode: %d, referringNode: %s", hashCode(), description );
    }
}
