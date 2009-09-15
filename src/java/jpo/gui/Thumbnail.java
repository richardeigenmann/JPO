package jpo.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
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

/*
Thumbnail.java:  class that displays a visual respresentation of the specified node

Copyright (C) 2002 - 20089  Richard Eigenmann.
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
 *   Thumbnail displays a visual representation of the specified node. On a Picture this 
 *   is a Thumbnail thereof, on a Group it is a folder icon.
 *
 * TODO: move the methods to make the Thumbnail back into this class from ThumbnailCreationFactory
 * TODO: split this class into a GUI component that deals with the GUI stuff and one which deals with the
 * creation stuff and all the model notifcations. I.e. MVC..
 */
public class Thumbnail
        extends JComponent
        implements DropTargetListener,
        PictureInfoChangeListener,
        GroupInfoChangeListener,
        TreeModelListener {

    /**
     *  a link to the SortableDefaultMutableTreeNode in the data model.
     *  This allows thumbnails to be selected by sending a
     *  nodeSelected event to the data model.
     **/
    public SortableDefaultMutableTreeNode referringNode;

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( Thumbnail.class.getName() );

    /**
     *  A set of picture nodes of which one indicated by {@link #myIndex} is to be shown
     */
    private ThumbnailBrowserInterface myThumbnailBrowser = null;

    /**
     *  the Index position in the {@link #myThumbnailBrowser} which is being shown by this
     *  component.
     */
    public int myIndex = 0;

    /**
     *  the desired size for the thumbnail
     **/
    public int thumbnailSize;

    /**
     *  I've put in this variable because I have having real trouble with the getPreferredSize method
     *  not being able to access the ImageObserver to query the height of the thumbnail.
     */
    private int thumbnailHeight = 0;

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
     *   This icon indicates that the thumbnail creation is sitting on the queue.
     */
    static final ImageIcon queueIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/queued_thumbnail.gif" ) );

    /**
     *   This icon shows a large yellow folder.
     */
    private static final ImageIcon largeFolderIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_folder_large.jpg" ) );

    /**
     *  The icon to superimpose on the picture if the highres picture is not available
     */
    protected static final ImageIcon offlineIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_offline.gif" ) );

    /**
     *   An icon that indicates that the image is being loaded
     */
    protected static final ImageIcon loadingIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/loading_thumbnail.gif" ) );

    /**
     *   An icon that indicates a broken image used when there is a
     *   problem rendering the correct thumbnail.
     */
    protected static final ImageIcon brokenThumbnailPicture = new ImageIcon( Settings.cl.getResource( "jpo/images/broken_thumbnail.gif" ) );

    /**
     *  The icon to superimpose on the picture if the highres picture is not available
     */
    protected static final ImageIcon mailIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_mail.gif" ) );

    /**
     *  This flag indicates whether the offline icon should be drawn or not.
     */
    public boolean drawOfflineIcon = false;

    /**
     *  This flag indicates whether the mail icon should be drawn or not.
     */
    public boolean drawMailIcon = false;

    /**
     *  This flag indicates where decorations should be drawn at all
     */
    private boolean decorateThumbnails = true;

    /**
     *    The image that should be displayed
     */
    private Image img = null;

    /**
     *    The Image Observer of the image that should be displayed
     */
    private ImageObserver imgOb;

    /**
     *   This variable will hold the darkend or otherwise processed Thumbnail that will be
     *   painted when the Thumbnail is on a selected node.
     */
    private BufferedImage selectedThumbnail = null;

    /**
     *  The color to use when the thumbnail has been selected
     */
    private static final Color HIGHLIGHT_COLOR = Color.DARK_GRAY;

    /**
     *  The color to use when the thumbnail has been selected
     */
    private static final Color SHADOW_COLOR = Color.LIGHT_GRAY;

    /**
     *  The color to use when the thumbnail has been selected
     */
    private static final Color UNSELECTED_COLOR = Color.WHITE;
    //private static final Color  UNSELECTED_COLOR = Color.BLUE;

    /**
     * The priority this Thumbnail should have on the ThumbnailCreationQueue
     */
    private int priority = ThumbnailQueueRequest.MEDIUM_PRIORITY;

    /**
     *   The factor which is multiplied with the Thumbnail to determine how large it is shown.
     */
    private float thumbnailSizeFactor = 1;


    /**
     *   Creates a new Thumbnail object.
     *
     *   @param	thumbnailSize	The size in which the thumbnail is to be created
     **/
    public Thumbnail( int thumbnailSize ) {
        this.thumbnailSize = thumbnailSize;

        addMouseListener( new ThumbnailMouseAdapter() );

        // attach the Thumbnail to the Tree Model to get notifications.
        Settings.pictureCollection.getTreeModel().addTreeModelListener( this );


        // set up drag & drop
        dropTarget = new DropTarget( this, this );
        myDragGestureListener = new ThumbnailDragGestureListener();
        dragSource.createDefaultDragGestureRecognizer(
                this, DnDConstants.ACTION_COPY_OR_MOVE, myDragGestureListener );

        initComponents();
    }

    /**
     * Handle for operations that affect the collection.
     */
    private Jpo collectionController;


    /**
     *   Creates a new Thumbnail object and sets it to the supplied node.
     *
     *   @param 	mySetOfNodes	The set for which this Thumbnail is
     *				being created.
     *
     *   @param	index		the position of the image in the set
     *
     *   @param	thumbnailSize	The size in which the thumbnail is to be created
     *
     *   @param     priority	One of ThumbnailCreationQueue.MEDIUM_PRIORITY,ThumbnailCreationQueue.HIGH_PRORITY, ThumbnailCreationQueue.LOW_PRIORITY
     * @param collectionController  The controller for the collection
     *
     **/
    public Thumbnail( ThumbnailBrowserInterface mySetOfNodes, int index,
            int thumbnailSize, int priority, Jpo collectionController ) {
        this( thumbnailSize );
        this.priority = priority;
        this.myThumbnailBrowser = mySetOfNodes;
        this.myIndex = index;
        this.collectionController = collectionController;
        setNode( mySetOfNodes, index );
    }


    /**
     *   Creates a new Thumbnail object with a reference to the ThumbnailPanelController which
     *   must receive notifications that a new node should be selected.
     *
     **/
    public Thumbnail() {
        this( Settings.thumbnailSize );
    }

    /**
     * remember where we registered as a PictureInfoListener
     */
    private PictureInfo registeredPictureInfoChangeListener;

    /**
     * remember where we registered as a GroupInfoListener
     */
    private GroupInfo registeredGroupInfoChangeListener;


    private void initComponents() {
        Runnable r = new Runnable() {

            public void run() {
                setVisible( false );
                setOpaque( false );
                setBackground( UNSELECTED_COLOR );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }
    }


    /**
     *  Sets the node being visualised by this Thumbnail object.
     *
     *  @param mySetOfNodes  The {@link ThumbnailBrowserInterface} being tracked
     *  @param index	The position of this object to be displayed.
     */
    public void setNode( ThumbnailBrowserInterface mySetOfNodes, int index ) {
        this.myThumbnailBrowser = mySetOfNodes;
        this.myIndex = index;
        SortableDefaultMutableTreeNode node = mySetOfNodes.getNode( index );
        /*if ( this.referringNode == node ) {
        // Don't refresh the node if it hasn't changed
        return;
        }*/

        unqueue();

        // unattach the change Listener
        if ( registeredPictureInfoChangeListener != null ) {
            registeredPictureInfoChangeListener.removePictureInfoChangeListener( this );
            registeredPictureInfoChangeListener = null;
        }
        if ( registeredGroupInfoChangeListener != null ) {
            registeredGroupInfoChangeListener.removeGroupInfoChangeListener( this );
            registeredGroupInfoChangeListener = null;
        }
        /*if ( ( this.referringNode != null ) && ( this.referringNode.getUserObject() instanceof PictureInfo ) ) {
        PictureInfo pi = (PictureInfo) this.referringNode.getUserObject();
        pi.removePictureInfoChangeListener( this );
        }*/


        this.referringNode = node;

        // attach the change Listener
        if ( referringNode != null ) {
            if ( referringNode.getUserObject() instanceof PictureInfo ) {
                PictureInfo pi = (PictureInfo) referringNode.getUserObject();
                pi.addPictureInfoChangeListener( this );
                registeredPictureInfoChangeListener = pi; //remember so we can remove
            } else if ( referringNode.getUserObject() instanceof GroupInfo ) {
                GroupInfo pi = (GroupInfo) referringNode.getUserObject();
                pi.addGroupInfoChangeListener( this );
                registeredGroupInfoChangeListener = pi; //remember so we can remove

            }
        }


        if ( node == null ) {
            img = null;
            imgOb = null;
            setVisible( false );
        } else { //if ( node.getUserObject() instanceof PictureInfo ) {
            requestThumbnailCreation( priority, false );
        }

        showSlectionStatus();
        determineMailSlectionStatus();
        determineImageStatus( referringNode );
    }


    /**
     *  This method forwards the request to create the thumbnail to the ThumbnailCreationQueue
     *  @param	priority	The priority with which the request is to be treated on the queue
     *  @param	force		Set to true if the thumbnail needs to be rebuilt from source, false
     *				if using a cached version is OK.
     */
    public void requestThumbnailCreation( int priority, boolean force ) {
        ThumbnailCreationQueue.requestThumbnailCreation(
                this, priority, force );
    }


    /**
     *  sets the Thumbnail to the specified icon
     *  This is called from the ThumbnailCreationThread.
     *
     *  @param  icon  The imageicon that should be displayed
     */
    public void setThumbnail( final ImageIcon icon ) {
        Runnable r = new Runnable() {

            public void run() {
                if ( icon == null ) {
                    return;
                }
                img = icon.getImage();
                if ( img == null ) {
                    return;
                }
                imgOb = icon.getImageObserver();
                thumbnailHeight = img.getHeight( imgOb );

                RescaleOp darkenOp = new RescaleOp( .6f, 0, null );
                BufferedImage source = new BufferedImage( img.getWidth( imgOb ), thumbnailHeight, BufferedImage.TYPE_INT_BGR );
                source.createGraphics().drawImage( img, 0, 0, null );
                selectedThumbnail = darkenOp.filter( source, null );

                // force update of layout
                setVisible( false );
                setVisible( true );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }
    }


    /**
     * Sets an icon for a pending state before a final icon is put in place by a ThumbnailCreation
     */
    public void setPendingIcon() {
        if ( referringNode == null ) {
            logger.info( "Referring node is null! How did this happen?" );
            Thread.dumpStack();
            return;
        }
        if ( referringNode.getUserObject() instanceof PictureInfo ) {
            setThumbnail( Thumbnail.queueIcon );
        } else {
            setThumbnail( Thumbnail.largeFolderIcon );
        }
    }


    /**
     * Sets an icon to mark that the thumbnail is in loading state before a final icon is put in place by a ThumbnailCreation
     */
    public void setLoadingIcon() {
        setThumbnail( loadingIcon );
    }


    /**
     * Sets an icon to mark that the thumbnail is in loading state before a final icon is put in place by a ThumbnailCreation
     */
    public void setBrokenIcon() {
        setThumbnail( brokenThumbnailPicture );
    }


    /**
     *  Returns the Image of the Thumbnail.
     *  @return The image of the Thumbnail.
     */
    public Image getThumbnail() {
        return img;
    }


    /**
     *   Overridden method to allow the setting of the size when not visible. This
     *   was a bit problematic as the Component which is showing the Thumbnails was
     *   not adjusting to the new image size. The revalidate() cured this.
     *
     *   @param  visibility   true for visible, false for non visible.
     */
    @Override
    public void setVisible( final boolean visibility ) {
        super.setVisible( visibility );
        Runnable r = new Runnable() {

            public void run() {
                if ( visibility ) {
                    if ( getSize().height != thumbnailHeight ) {
                        //logger.info("Thumbnail.setVisible: The Size is not right!");
                        // finally I found the solution to the size issue! Unless it's set to
                        //  non visible the whole rendering engine sees no point in fixing the size.
                        Thumbnail.super.setVisible( false );
                        Thumbnail.super.setVisible( true );
                    }
                }
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }

    }


    /**
     *   Returns the preferred size for the Thumbnail as a Dimension using the thumbnailSize
     *   as width and height.
     * @return
     */
    @Override
    public Dimension getPreferredSize() {
        int height = 0;
        if ( isVisible() ) {
            height = (int) ( thumbnailHeight * thumbnailSizeFactor );
        }
        return new Dimension( (int) ( thumbnailSize * thumbnailSizeFactor ), height );
    }


    /**
     *   Returns the maximum (scaled) size for the Thumbnail as a Dimension using the thumbnailSize
     *   as width and height.
     * @return
     */
    @Override
    public Dimension getMaximumSize() {
        return new Dimension( (int) ( thumbnailSize * thumbnailSizeFactor ), (int) ( thumbnailSize * thumbnailSizeFactor ) );
    }


    /**
     *   Returns the maximum unscaled size for the Thumbnail as a Dimension using the thumbnailSize
     *   as width and height.
     * @return
     */
    public Dimension getMaximumUnscaledSize() {
        return new Dimension( thumbnailSize, thumbnailSize );
    }


    /**
     *  This method sets the scaling factor for the display of a thumbnail.
     *  0 .. 1
     * @param thumbnailSizeFactor
     */
    public void setFactor( float thumbnailSizeFactor ) {
        //logger.info("Thumbnail.setFactor: " + Float.toString( thumbnailSizeFactor ) );
        this.thumbnailSizeFactor = thumbnailSizeFactor;
        setVisible( isVisible() );
    }


    /**
     *  Removes any request for this thumbnail from the ThumbnailCreationQueue. No problem if
     *  it was not on the queue.
     */
    public void unqueue() {
        ThumbnailCreationQueue.removeThumbnailRequest( this );
    }


    /**
     *   we are overriding the default paintComponent method, grabbing the Graphics
     *   handle and doing our own drawing here. Essentially this method draws a large
     *   black rectangle. A drawImage is then painted doing an affine transformation
     *   on the image to position it so the the desired point is in the middle of the
     *   Graphics object.
     * @param g
     */
    @Override
    public void paintComponent( Graphics g ) {
        if ( !SwingUtilities.isEventDispatchThread() ) {
            logger.severe( "Not running on EDT!" );
            Thread.dumpStack();
        }

        int WindowWidth = getSize().width;
        int WindowHeight = getSize().height;

        if ( img != null ) {
            Graphics2D g2d = (Graphics2D) g;

            int focusPointx = (int) ( img.getWidth( imgOb ) * thumbnailSizeFactor / 2 );
            int focusPointy = (int) ( img.getHeight( imgOb ) * thumbnailSizeFactor / 2 );

            int X_Offset = (int) ( (double) ( WindowWidth / 2 ) - ( focusPointx ) );
            int Y_Offset = (int) ( (double) ( WindowHeight / 2 ) - ( focusPointy ) );

            // clear damaged component area
            Rectangle clipBounds = g2d.getClipBounds();
            g2d.setColor( getBackground() );
            g2d.fillRect( clipBounds.x,
                    clipBounds.y,
                    clipBounds.width,
                    clipBounds.height );

            AffineTransform af1 = AffineTransform.getTranslateInstance( X_Offset, Y_Offset );
            AffineTransform af2 = AffineTransform.getScaleInstance( (double) thumbnailSizeFactor, (double) thumbnailSizeFactor );
            af2.concatenate( af1 );
            //op = new AffineTransformOp( af2, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );


            if ( Settings.pictureCollection.isSelected( referringNode ) ) {
                g2d.drawImage( selectedThumbnail, af2, imgOb );
            } else {
                g2d.drawImage( img, af2, imgOb );
            }

            if ( drawOfflineIcon ) {
                g2d.drawImage( offlineIcon.getImage(), X_Offset + 10, Y_Offset + 10, offlineIcon.getImageObserver() );
            }
            if ( drawMailIcon ) {
                int additionalOffset = drawOfflineIcon ? 40 : 0;
                g2d.drawImage( mailIcon.getImage(), X_Offset + 10 + additionalOffset, Y_Offset + 10, mailIcon.getImageObserver() );
            }
        } else {
            // paint a black square
            g.setClip( 0, 0, WindowWidth, WindowHeight );
            g.setColor( Color.black );
            g.fillRect( 0, 0, WindowWidth, WindowHeight );
        }
    }


    /**
     *  This method determines whether the source image is available online and sets the {@link #drawOfflineIcon}
     *  indicator accordingly.
     * @param n
     */
    public void determineImageStatus( DefaultMutableTreeNode n ) {
        if ( n == null ) {
            drawOfflineIcon = false;
            return;
        }

        Object userObject = n.getUserObject();
        if ( userObject instanceof PictureInfo ) {
            try {
                ( (PictureInfo) userObject ).getHighresURL().openStream().close();
                drawOfflineIcon = false;
            } catch ( MalformedURLException x ) {
                drawOfflineIcon = true;
            } catch ( IOException x ) {
                drawOfflineIcon = true;
            }
        } else {
            drawOfflineIcon = false;
        }
    }

    /**
     *  Inner class to handle the mouse events on the Thumbnail
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
            PicturePopupMenu picturePopupMenu = new PicturePopupMenu( myThumbnailBrowser, myIndex, null, collectionController );
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
            PictureViewer pictureViewer = new PictureViewer();
            pictureViewer.changePicture( myThumbnailBrowser, myIndex );
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
            showAsSelected();
        } else if ( e.getWasUnselected() ) {
            showAsUnselected();
        } else if ( ( e.getWasMailSelected() ) || ( e.getWasMailUnselected() ) ) {
            determineMailSlectionStatus();
            repaint();
        }
    }


    /**
     *  here we get notified by the GroupInfo object that something has
     *  changed.
     */
    public void groupInfoChangeEvent( GroupInfoChangeEvent e ) {
        logger.fine( String.format( "Got a Group Change event", e.toString() ) );
        if ( e.getLowresLocationChanged() ) {
            requestThumbnailCreation( ThumbnailQueueRequest.HIGH_PRIORITY, false );
        } else if ( e.getWasSelected() ) {
            showAsSelected();
        } else if ( e.getWasUnselected() ) {
            showAsUnselected();
        }
    }


    /**
     *  changes the color so that the user sees that the thumbnail is part of the selection
     */
    public void showAsSelected() {
        Runnable r = new Runnable() {

            public void run() {
                setBorder( BorderFactory.createCompoundBorder(
                        BorderFactory.createBevelBorder( BevelBorder.LOWERED, HIGHLIGHT_COLOR, SHADOW_COLOR ),
                        BorderFactory.createBevelBorder( BevelBorder.RAISED, HIGHLIGHT_COLOR, SHADOW_COLOR ) ) );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }

    }


    /**
     *  changes the color so that the user sees that the thumbnail is not part of the selection
     */
    public void showAsUnselected() {
        logger.fine( "running show unselected" );
        Runnable r = new Runnable() {

            public void run() {
                setBorder( BorderFactory.createEmptyBorder() );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }

    }


    /**
     *  changes the color so that the user sees whether the thumbnail is part of the selection
     */
    public void showSlectionStatus() {
        if ( Settings.pictureCollection.isSelected( referringNode ) ) {
            showAsSelected();
        } else {
            showAsUnselected();
        }

    }


    /**
     *  Determines whether decorations should be drawn or not
     * @param b
     */
    public void setDecorateThumbnails( boolean b ) {
        decorateThumbnails = b;
    }


    /**
     *  determines if the thumbnail is part of the mail selection and changes the drawMailIcon
     *  flag to ensure that the mail icon will be place over the image.
     */
    public void determineMailSlectionStatus() {
        if ( ( referringNode != null ) && decorateThumbnails && Settings.pictureCollection.isMailSelected( referringNode ) ) {
            drawMailIcon = true;
        } else {
            drawMailIcon = false;
        }

    }

    // Here we are not that interested in TreeModel change events other than to find out if our
    // current node was removed in which case we close the Window.

    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     */
    public void treeNodesChanged( TreeModelEvent e ) {
        // find out whether our node was changed
        Object[] children = e.getChildren();
        if ( children == null ) {
            // the root node does not have children as it doesn't have a parent
            return;
        }

        for ( int i = 0; i <
                children.length; i++ ) {
            if ( children[i] == referringNode ) {
                // logger.info( "Thumbnail detected a treeNodesChanged event" );
                // we are displaying a changed node. What changed?
                Object userObject = referringNode.getUserObject();
                if ( userObject instanceof GroupInfo ) {
                    // determine if the icon changed
                    // logger.info( "Thumbnail should be reloading the icon..." );
                    requestThumbnailCreation( ThumbnailQueueRequest.HIGH_PRIORITY, false );
                }

            }
        }
    }


    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     */
    public void treeNodesInserted( TreeModelEvent e ) {
    }


    /**
     *  The TreeModelListener interface tells us of tree node removal events.
     * @param e
     */
    public void treeNodesRemoved( TreeModelEvent e ) {
    }


    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     */
    public void treeStructureChanged( TreeModelEvent e ) {
    }


    /**
     *   this callback method is invoked every time something is
     *   dragged onto the Thumbnail. We check if the desired DataFlavor is
     *   supported and then reject the drag if it is not.
     * @param event
     */
    public void dragEnter( DropTargetDragEvent event ) {
        if ( !event.isDataFlavorSupported( JpoTransferable.dmtnFlavor ) ) {
            event.rejectDrag();
        }

    }


    /**
     *   this callback method is invoked every time something is 
     *   dragged over the Thumbnail. We could do some highlighting if
     *   we so desired.
     * @param event
     */
    public void dragOver( DropTargetDragEvent event ) {
        if ( !event.isDataFlavorSupported( JpoTransferable.dmtnFlavor ) ) {
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
     * Give some info about the Thumbnail.
     * @return some info about the Thumbnail
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
