package jpo;

import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.awt.image.*;
import java.awt.geom.*;
import javax.imageio.*;
/*
Thumbnail.java:  class that displays a visual respresentation of the specified node

Copyright (C) 2002  Richard Eigenmann.
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
 */
public class Thumbnail extends JPanel
	implements DropTargetListener,
		PictureInfoChangeListener, 
		TreeModelListener {




	/**
	 *  a link to the SortableDefaultMutableTreeNode in the data model.
	 *  This allows thumbnails to be selected by sending a 
	 *  nodeSelected event to the data model.
	 **/
	public SortableDefaultMutableTreeNode referringNode;

	/**
	 *  the desired size for the thumbnail
	 **/
	public int thumbnailSize;

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
	private  DragGestureListener myDragGestureListener;


	/**
	 *  The DragSourceListener for a thumbnail.
	 */
	private DragSourceListener myDragSourceListener = new ThumbnailDragSourceListener();

	/**
	 *  The icon to superimpose on the picture if the highres picture is not available
	 */
	protected static final ImageIcon offlineIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_offline.gif" ) ); 
	

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
	 *  a reference to the ThumbnailJScrollPane where group Info objects can refer their mouseclicks back to.
	 */
	private ThumbnailJScrollPane associcatedPanel;

	/**
	 *    The image that should be displayed
	 */
	private Image img = null;

	/**
	 *    The Image Observer of the image that should be displayed
	 */
	private ImageObserver imgOb;


	/**
	 *  The color to use when the thumbnail has been selected
	 */
	private static final Color  HIGHLIGHT_COLOR = Color.DARK_GRAY;
	

	/**
	 *  The color to use when the thumbnail has been selected
	 */
	private static final Color  SHADOW_COLOR = Color.LIGHT_GRAY;

	
	/**
	 *  The color to use when the thumbnail has been selected
	 */
	private static final Color  UNSELECTED_COLOR = Color.WHITE;


	/** 
	 * The priority this Thumbnail should have on the ThumbnailCreationQueue
	 */
	private int priority = ThumbnailCreationQueue.MEDIUM_PRIORITY;

	/**
	 *   Creates a new Thumbnail object.
	 *
	 *   @param	thumbnailSize	The size in which the thumbnail is to be created
	 **/
	public Thumbnail ( int thumbnailSize ) {
		this.thumbnailSize = thumbnailSize;

		setVisible( false );
		setOpaque( false );
		setBackground( UNSELECTED_COLOR );
		setBorder( BorderFactory.createEmptyBorder(0,0,0,0) );
		addMouseListener( new ThumbnailMouseAdapter() );

		// attach the Thumbnail to the Tree Model to get notifications.
		Settings.top.getTreeModel().addTreeModelListener( this );


		// set up drag & drop
		dropTarget = new DropTarget (this, this);
		myDragGestureListener = new ThumbnailDragGestureListener();
		dragSource.createDefaultDragGestureRecognizer( 
			this, DnDConstants.ACTION_COPY_OR_MOVE, myDragGestureListener );
	}


	/**
	 *   Creates a new Thumbnail object and sets it to the supplied node.
	 *
	 *   @param 	referringNode	The SortableDefaultMutableTreeNode for which this Thumbnail is
	 *				being created.
	 *
	 *   @param	thumbnailSize	The size in which the thumbnail is to be created
	 *
	 *   @param     priority	One of ThumbnailCreationQueue.MEDIUM_PRIORITY,ThumbnailCreationQueue.HIGH_PRORITY, ThumbnailCreationQueue.LOW_PRIORITY
	 *
	 **/
	public Thumbnail ( SortableDefaultMutableTreeNode referringNode, int thumbnailSize, int priority ) {
		this( thumbnailSize );
		this.priority = priority;
		setNode( referringNode );
	}


	/**
	 *   Creates a new Thumbnail object and sets it to the supplied node. Has been deprecated as 
	 *   the priority should be passed as a third argument.
	 *
	 *   @param 	referringNode	The SortableDefaultMutableTreeNode for which this Thumbnail is
	 *				being created.
	 *
	 *   @param	thumbnailSize	The size in which the thumbnail is to be created
	 *
	 *   @deprecated
	 *
	 *
	 **/
	public Thumbnail ( SortableDefaultMutableTreeNode referringNode, int thumbnailSize ) {
		this( referringNode, thumbnailSize, ThumbnailCreationQueue.MEDIUM_PRIORITY );
	}


	/**
	 *   Creates a new Thumbnail object with a reference to the ThumbnailJScrollPane which
	 *   must receive notifications that a new node should be selected.
	 *
	 *   @param	associcatedPanel	The ThumbnailJScrollpane that will be notified when the 
	 *					user want a different selection shown
	 **/
	public Thumbnail ( ThumbnailJScrollPane associcatedPanel ) {
		this ( Settings.thumbnailSize );
		setAssocicatedPanel( associcatedPanel );
	}




	/**
	 *  changes the node that is being deiplayed. This does several things: 
	 *  It disconnects the {@link PictureInfoChangeListener} from the old
	 *  node and reconnects such a listener to the new node (to receive notification).
	 *  That allows the Thumbnail to be informed if anything changes such as the 
	 *  file location of the node which would cause the image to have to be redisplayed.
	 *  The setNode method fires off a {@link ThumbnailCreationQueue#requestThumbnailCreation}.
	 *  This puts the request on a quee and when the ThumbnailCreationThread picks 
	 *  the request up it will update the image by calling the {@link #setThumbnail} method 
	 *  on this object.
	 *
	 *  @param  node   The node which should be displayed. Can be null if the Thumbnail is to be muted.
	 */
	public void setNode( SortableDefaultMutableTreeNode node ) {
		if ( this.referringNode == node ) {
			// Don't refresh the node if it hasn't changed
			return;
		}

		unqueue();
		
		// unattach the change Listener
		if ( ( this.referringNode != null ) 
		  && ( this.referringNode.getUserObject() instanceof  PictureInfo ) ) {
			PictureInfo pi = (PictureInfo) this.referringNode.getUserObject();
			pi.removePictureInfoChangeListener( this );
		}

		this.referringNode = node;

		// attach the change Listener
		if ( ( referringNode != null ) 
		  && ( referringNode.getUserObject() instanceof  PictureInfo ) ) {
			PictureInfo pi = (PictureInfo) referringNode.getUserObject();
			pi.addPictureInfoChangeListener( this );
		}


		if ( node == null ) {
			img = null;
			imgOb = null;
			setVisible( false );
		} else { //if ( node.getUserObject() instanceof PictureInfo ) {
			ThumbnailCreationQueue.requestThumbnailCreation( 
				this, priority );
		} // else {
			// setThumbnail( folderIcon );
		//}
		
		showSlectionStatus();
		determineMailSlectionStatus();
		determineImageStatus( referringNode );
	}



	/**
	 *  sets the Thumbnail to the specified icon  
	 *  This is called from the ThumbnailCreationThread.
	 *  The call to setVisible adjusts the size and forces the
	 *  LayoutManager to reconsider layouts by calling revalidate().
	 *
	 *  @param  icon  The imageicon that should be displayed
	 */
	public void setThumbnail( ImageIcon icon ) {
		img = icon.getImage();
		imgOb = icon.getImageObserver();
		setVisible( true );
	}

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
	public void setVisible( boolean visibility ) {
		super.setVisible( visibility );
		if ( visibility ) {
			setPreferredSize( new Dimension( thumbnailSize, img.getHeight(imgOb) ) );
			setMaximumSize( new Dimension( thumbnailSize, img.getHeight(imgOb) ) ); 
			setMinimumSize( new Dimension( thumbnailSize, img.getHeight(imgOb) ) ); 
			setSize( new Dimension( thumbnailSize, img.getHeight(imgOb) ) ); 
			revalidate();
			repaint();
		} else {
			setPreferredSize( new Dimension( thumbnailSize, 0 ));
			setMaximumSize( new Dimension( thumbnailSize, 0 )); 
			setMinimumSize( new Dimension( thumbnailSize, 0 )); 
			setSize( new Dimension( thumbnailSize, 0 ));
			revalidate();
		}
	}



	/**
	 *  Removes any request for this thumbnail from the ThumbnailCreationQueue. No problem if
	 *  it was not on the queue.
	 */
	public void unqueue() {
		ThumbnailCreationQueue.remove( this );
	}


	/**
	 *   sets the associated ThumbnailJScrollPane that will be told to display another group
	 *   if the user clicks on a group node.
	 */
	public void setAssocicatedPanel( ThumbnailJScrollPane associcatedPanel ) {
		this.associcatedPanel = associcatedPanel;
	}


	/**
	 *   we are overriding the default paintComponent method, grabbing the Graphics 
	 *   handle and doing our own drawing here. Esentially this method draws a large
	 *   black rectangle. A drawImage is then painted doing an affine transformation
	 *   on the image to position it so the the desired point is in the middle of the 
	 *   Graphics object. 
	 */
	public void paintComponent( Graphics g ) {
		int WindowWidth = getSize().width;
		int WindowHeight = getSize().height;

		if ( img != null ) {
			Graphics2D g2d = (Graphics2D)g;
			
			int focusPointx = (int) ( img.getWidth(imgOb) / 2 );
			int focusPointy = (int) ( img.getHeight(imgOb) / 2 );

			int X_Offset = (int) ((double) ( WindowWidth / 2 ) - ( focusPointx ));
			int Y_Offset = (int) ((double) ( WindowHeight / 2 ) - ( focusPointy ));

			// clear damaged component area
		 	Rectangle clipBounds = g2d.getClipBounds();
			g2d.setColor( getBackground()); 
			g2d.fillRect( clipBounds.x, 
				      clipBounds.y,
			              clipBounds.width, 
			      	      clipBounds.height);

			g2d.drawImage( img, AffineTransform.getTranslateInstance((int) X_Offset, (int) Y_Offset), imgOb);
			if ( drawOfflineIcon ) {
				g2d.drawImage( offlineIcon.getImage(), (int) X_Offset + 10, (int) Y_Offset + 10, offlineIcon.getImageObserver() );
			}
			if ( drawMailIcon ) {
				int additionalOffset = drawOfflineIcon ? 40 : 0;
				g2d.drawImage( mailIcon.getImage(), (int) X_Offset + 10 + additionalOffset, (int) Y_Offset + 10, mailIcon.getImageObserver() );
			}
		} else {
			// paint a black square
			g.setClip(0, 0, WindowWidth, WindowHeight);
			g.setColor(Color.black);
			g.fillRect(0,0,WindowWidth,WindowHeight);
		}
	}


	/**
	 *  This method determines whether the source image is available online and sets the\
	 *  indicator accordingly.
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
	private class ThumbnailMouseAdapter extends MouseAdapter {
		/**
		 *   overridden to analyse the mouse event and decide whether
		 *   to display the picture right away (doubleclick) or show
		 *   the popupMenu.
		 */
		public void mouseClicked( MouseEvent e ) {
			if ( referringNode == null ){
				return;
			}
			if ( referringNode.getUserObject() instanceof PictureInfo ) {
				if  ( e.getClickCount() > 1 ) {
					referringNode.showLargePicture();
				}  else if ( e.getButton() == 3 ) { // popup menu only on 3rd mouse button.
					PicturePopupMenu picturePopupMenu = new PicturePopupMenu( referringNode );
					picturePopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}  else if ( e.getButton() == 1) { // first button
					// I.e. selection
					if ( e.isControlDown() ) {
						if ( referringNode.isSelected() ) {
							referringNode.removeFromSelection();
						} else {
							referringNode.setSelected();
						}
					} else {
						if ( referringNode.isSelected() ) {
							referringNode.clearSelection();
						} else {
							referringNode.clearSelection();
							referringNode.setSelected();
						}
					}
				}
			} else {
				if ( associcatedPanel != null ) {		
					if  ( e.getClickCount() > 1 ) {
						associcatedPanel.requestShowGroup( referringNode );
					}  else if ( e.getButton() == 3 ) { // popup menu only on 3rd mouse button.
						CleverJTree cleverJTree = associcatedPanel.getAssociatedCleverJTree();
						if ( cleverJTree != null ) {
							cleverJTree.popupNode = referringNode;
							GroupPopupMenu groupPopupMenu = new GroupPopupMenu( cleverJTree, referringNode );
							groupPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
						}
					}
				}
			}
		}
	}



	/**
	 *  here we get notified by the PictureInfo object that something has
	 *  changed.
	 */
	public void pictureInfoChangeEvent( PictureInfoChangeEvent e ) {
		if ( e.getHighresLocationChanged()
		  || e.getChecksumChanged() 
		  || e.getLowresLocationChanged()
		  || e.getThumbnailChanged()
		  || e.getRotationChanged() ) {
			ThumbnailCreationQueue.requestThumbnailCreation( 
				this, ThumbnailCreationQueue.HIGH_PRIORITY );
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
	 *  changes the color so that the user sees that the thumbnail is part of the selection
	 */
	public void showAsSelected() {
		//setBackground( HIGHLIGHT_COLOR );
		setBorder( BorderFactory.createCompoundBorder( 
			BorderFactory.createBevelBorder( BevelBorder.LOWERED, HIGHLIGHT_COLOR, SHADOW_COLOR ),
			BorderFactory.createBevelBorder( BevelBorder.RAISED, HIGHLIGHT_COLOR, SHADOW_COLOR ) ) );
	}

	/**
	 *  changes the color so that the user sees that the thumbnail is not part of the selection
	 */
	public void showAsUnselected() {
		//setBackground( UNSELECTED_COLOR );
		setBorder( BorderFactory.createEmptyBorder() );
	}


	/**
	 *  changes the color so that the user sees whether the thumbnail is part of the selection
	 */
	public void showSlectionStatus() {
		if ( ( referringNode != null )
		  && ( referringNode.isSelected() ) ) {
			showAsSelected();
		} else {
			showAsUnselected();
		}
	}


	/**
	 *  determines if the thumbnail is part of the mail selection and changes the drawMailIcon
	 *  flag to ensure that the mail icon will be place over the image.
	 */
	public void determineMailSlectionStatus() {
		if ( ( referringNode != null )
		  && ( referringNode.isMailSelected() ) ) {
			drawMailIcon = true;
		} else {
			drawMailIcon = false;
		}
	}



	// Here we are not that interested in TreeModel change events other than to find out if our
	// current node was removed in which case we close the Window.
	
	/**
	 *   implemented here to satisfy the TreeModelListener interface; not used.
	 */
	public void treeNodesChanged ( TreeModelEvent e ) {
		// find out whether our node was changed
		Object[] children = e.getChildren();
		if ( children == null ) {
			// the root node does not have children as it doesn't have a parent
			return;
		}
		
		for ( int i = 0; i < children.length; i++ ) {
			if ( children[i] == referringNode ) {
				// Tools.log( "Thumbnail detected a treeNodesChanged event" );
				// we are displaying a changed node. What changed?
				Object userObject = referringNode.getUserObject();
				if ( userObject instanceof GroupInfo ) {
					// determine if the icon changed
					// Tools.log( "Thumbnail should be reloading the icon..." );
					ThumbnailCreationQueue.requestThumbnailCreation( 
						this, ThumbnailCreationQueue.HIGH_PRIORITY );
				}
			}
		}
	}


	/**
	 *   implemented here to satisfy the TreeModelListener interface; not used.
	 */
	public void treeNodesInserted ( TreeModelEvent e ) {
	}

	/**
	 *  The TreeModelListener interface tells us of tree node removal events. 
	 */
	public void treeNodesRemoved ( TreeModelEvent e ) {
	}

	/**
	 *   implemented here to satisfy the TreeModelListener interface; not used.
	 */
	public void treeStructureChanged ( TreeModelEvent e ) {
	}




	/**
	 *   this callback method is invoked every time something is 
	 *   dragged onto the Thumbnail. We check if the desired DataFlavor is 
	 *   supported and then reject the drag if it is not.
	 */
	public void dragEnter ( DropTargetDragEvent event ) {
		if ( ! event.isDataFlavorSupported( JpoTransferable.dmtnFlavor ) ) {
			event.rejectDrag();
		}
	}



	/**
	 *   this callback method is invoked every time something is 
	 *   dragged over the Thumbnail. We could do some highlighting if
	 *   we so desired.
	 *
	 *   @see	CleverJTree#dragOver(DropTargetDragEvent)
	 */
	public void dragOver ( DropTargetDragEvent event ) {
		if ( ! event.isDataFlavorSupported ( JpoTransferable.dmtnFlavor)) {
			event.rejectDrag();
		}
	}




	/**
	 *   this callback method is invoked when the user presses or releases shift when
	 *   doing a drag. He can signal that he wants to change the copy / move of the 
	 *   operation. This method could intercept this change and could modify the event
	 *   if it needs to.  On Thumbnails this does nothing.
	 *
	 *   @see	CleverJTree#dropActionChanged(DropTargetDragEvent)
	 */
	public void dropActionChanged ( DropTargetDragEvent event ) {
	}



	/**
	 *   this callback method is invoked to tell the dropTarget that the drag has moved on
	 *   to something else. We do nothing here.
	 */
	public void dragExit ( DropTargetEvent event ) {
		Tools.log("Thumbnail.dragExit( DropTargetEvent ): invoked");
	}



	/**
	 *  This method is called when the drop occurs. It gives the hard work to the
	 *  SortableDefaultMutableTreeNode.
	 */
	public void drop ( DropTargetDropEvent event ) {
		referringNode.executeDrop ( event ); 
	}



	/**
	 *  this method rotates the Thumbnail by the angle indicated. If the rotateAngle is 0 
	 *  then the Image is not rotated but reloaded from the source.
	 *  @param  rotateAngle  The angle by which the thumbnail is to be rotated.
	 *
	public void requestRotation( int rotateAngle ) {
		if ( Settings.logFunctions ) {
			Tools.log ( "Thumbnail.requestRotation invoked with angle: " + Integer.toString( rotateAngle ) );
		}
		
		double newAngle;
		if ( rotateAngle != 0 ) {
			double oldAngle = ( (PictureInfo) referringNode.getUserObject()).getRotation();
			newAngle = ( oldAngle + rotateAngle ) % 360;
		} else {
			newAngle = 0;
		}

		Tools.log ("Setting new angle to: " + Double.toString( newAngle	) );
		( (PictureInfo) referringNode.getUserObject()).setRotation( newAngle );
		referringNode.setUnsavedUpdates();


		if ( newAngle == 0 ) {
			// don't rotate but rebuild the thumbnail from source
			ThumbnailCreationQueue.requestThumbnailCreation( this, ThumbnailCreationQueue.HIGH_PRIORITY, true );
			return;
		}


		PictureInfo pi = (PictureInfo) referringNode.getUserObject();
		URL lowresUrl;
		
		try {
			lowresUrl = pi.getLowresURL();
		} catch ( MalformedURLException x ) {
			Tools.log("Lowres URL was Malformed: " + pi.getLowresLocation() );
			ThumbnailCreationQueue.requestThumbnailCreation( this, ThumbnailCreationQueue.HIGH_PRIORITY, true );
			return;
		}

		// Rotate the Thumbnail
		//Image currentThumbnail = ( (ImageIcon) getIcon() ).getImage();
		BufferedImage currentThumbnail;
		try {
			currentThumbnail = ImageIO.read( lowresUrl );
		} catch ( IOException x ) {
			Tools.log("Error: Thumbnail.requestRotation: IOException while loading: " + lowresUrl.toString() );
			ThumbnailCreationQueue.requestThumbnailCreation( this, ThumbnailCreationQueue.HIGH_PRIORITY, true );
			return;
		}
		

		try {
		int xRot = currentThumbnail.getWidth() / 2;
		int yRot = currentThumbnail.getHeight() / 2;
//		AffineTransform rotateAf = AffineTransform.getRotateInstance( Math.toRadians( newAngle ), xRot, yRot );
		AffineTransform rotateAf = AffineTransform.getRotateInstance( Math.toRadians( rotateAngle ), xRot, yRot );
		AffineTransformOp op = new AffineTransformOp( rotateAf, AffineTransformOp.TYPE_BILINEAR );
		Rectangle2D newBounds = op.getBounds2D( currentThumbnail );
		// a simple AffineTransform would give negative top left coordinates -->
		// do another transform to get 0,0 as top coordinates again.
		double minX = newBounds.getMinX();
		double minY = newBounds.getMinY();
		AffineTransform translateAf = AffineTransform.getTranslateInstance( minX * (-1), minY * (-1) );
		rotateAf.preConcatenate( translateAf );
		op = new AffineTransformOp( rotateAf, AffineTransformOp.TYPE_BILINEAR );
		newBounds = op.getBounds2D( currentThumbnail );
		// this piece of code is so essential!!! Otherwise the internal image format
		// is totally altered and either the AffineTransformOp decides it doesn't
		// want to rotate the image or web browsers can't read the resulting image.
		BufferedImage targetImage = new BufferedImage(
			(int) newBounds.getWidth(),
			(int) newBounds.getHeight(),
			BufferedImage.TYPE_USHORT_565_RGB );

		targetImage = op.filter( currentThumbnail, targetImage );
		setIcon( new ImageIcon( targetImage ) );

		setPreferredSize( new Dimension( thumbnailSize, getIcon().getIconHeight() ) );
		setMaximumSize( new Dimension( thumbnailSize, getIcon().getIconHeight() ) ); 
		setMinimumSize( new Dimension( thumbnailSize, getIcon().getIconHeight() ) ); 

		ScalablePicture.writeJpg( pi.getLowresFile(), targetImage, Settings.defaultJpgQuality );
		} catch ( ImagingOpException x ) {
			Tools.log ("Error: Thumbnail.requestRotation caught an ImaginOpException. requesting a rebuild.\nThe reason for the error was: " + x.toString() );
			ThumbnailCreationQueue.requestThumbnailCreation( this, ThumbnailCreationQueue.HIGH_PRIORITY, true );
		}				
	}*/



	/**
	 *   This class extends a DragGestureListener and allows DnD on Thumbnails.
	 */
	private class ThumbnailDragGestureListener implements DragGestureListener, Serializable {
		/**
		 *   This method is invoked by the drag and drop framework. It signifies
		 *   the start of a drag and drop operation. If the event is a copy or move we
		 *   start the drag and create a Transferable.
		 */
 		public void dragGestureRecognized( DragGestureEvent event ) {
			if( ( event.getDragAction() & DnDConstants.ACTION_COPY_OR_MOVE ) == 0) {
				return;
			}

			referringNode.setSelected();
	
			//JpoTransferable t = new JpoTransferable( referringNode );
			JpoTransferable t = new JpoTransferable( referringNode.getSelectedNodes() );
			try {
				//event.startDrag( DragSource.DefaultMoveDrop, t, myDragSourceListener );
				event.startDrag( DragSource.DefaultMoveNoDrop, t, myDragSourceListener );
				Tools.log("Thumbnail.dragGestureRecognized: Drag started on node: " + referringNode.getUserObject().toString() );
			} catch ( InvalidDnDOperationException x ) {
				Tools.log("Thumbnail.dragGestureRecognized threw a InvalidDnDOperationException: reason: " + x.getMessage() );
			}			
		}
	}


	/**
	 *  This class extends a DragSourceListener for tracking the drag operation originating
	 *  from this thumbnail.
	 */
	private class ThumbnailDragSourceListener implements DragSourceListener, Serializable {
	
		/**
		 *  this callback method is invoked after the dropTaget had a chance
		 *  to evaluate the drag event and was given the option of rejecting or
		 *  modifying the event. This method sets the cursor to reflect
		 *  whether a copy, move or no drop is possible.
		 */
		public void dragEnter ( DragSourceDragEvent event ) {
			Tools.setDragCursor( event );
		}

		/**
		 *  this callback method is invoked after the dropTaget had a chance
		 *  to evaluate the dragOver event and was given the option of rejecting or
		 *  modifying the event. This method sets the cursor to reflect
		 *  whether a copy, move or no drop is possible.
		 */
		public void dragOver ( DragSourceDragEvent event ) {
			Tools.setDragCursor( event );
		}

		/**
		 *   this callback method is invoked to tell the dragSource that the drag has moved on
		 *   to something else. 
		 */
		public void dragExit ( DragSourceEvent event ) {
		}
		
		/**
		 *   this callback method is invoked when the user presses or releases shift when
		 *   doing a drag. He can signal that he wants to change the copy / move of the 
		 *   operation. 
		 */
		public void dropActionChanged ( DragSourceDragEvent event ) {
			Tools.setDragCursor( event );
		}

		/**
		 *   this callback message goes to DragSourceListener, informing it that the dragging 
		 *   has ended. 
		 */
		public void dragDropEnd ( DragSourceDropEvent event ) {   
			referringNode.clearSelection();
		}
	}

}
