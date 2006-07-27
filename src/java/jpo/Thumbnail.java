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
import java.awt.Dimension;

/*
Thumbnail.java:  class that displays a visual respresentation of the specified node

Copyright (C) 2002-2006  Richard Eigenmann.
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

	
	private ThumbnailBrowserInterface myThumbnailBrowser = null;
	
	
	private int myIndex = 0;


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
	 *  This flag indicates where decorations should be drawn at all
	 */
	private boolean decorateThumbnails = true;

	/**
	 *  a reference to the ThumbnailJScrollPane where group Info objects can refer their mouseclicks back to.
	 */
	private ThumbnailJScrollPane associatedPanel;

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
	 *   The factor which is multiplied with the Thumbnail to determine how large it is shown.
	 */
	private float thumbnailSizeFactor = 1;

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
		Settings.pictureCollection.getTreeModel().addTreeModelListener( this );


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
	public Thumbnail ( ThumbnailBrowserInterface mySetOfNodes, int index, int thumbnailSize, int priority ) {
		this( thumbnailSize );
		this.priority = priority;
		this.myThumbnailBrowser = mySetOfNodes;
		this.myIndex = index;
		setNode( mySetOfNodes, index );
	}



	/**
	 *   Creates a new Thumbnail object with a reference to the ThumbnailJScrollPane which
	 *   must receive notifications that a new node should be selected.
	 *
	 *   @param	associatedPanel	The ThumbnailJScrollpane that will be notified when the 
	 *					user want a different selection shown
	 **/
	public Thumbnail ( ThumbnailJScrollPane associatedPanel ) {
		this ( Settings.thumbnailSize );
		setassociatedPanel( associatedPanel );
	}



	/**
	 *  This version of setNode is context aware and knows what sort of {@link ThumbnailBrowserInterface}
	 *  is being 
	 *  tracked and what position it occupies.
	 *
	 *  @param mySetOfNodes  The {@link ThumbnailBrowserInterface} being tracked
	 *  @param index	The position of this object to be displayed.
	 */
	 public void setNode( ThumbnailBrowserInterface mySetOfNodes, int index ) {
	 	//setNode( mySetOfNodes.getNode( index ) );
		this.myThumbnailBrowser = mySetOfNodes;
		this.myIndex = index;
		SortableDefaultMutableTreeNode node = mySetOfNodes.getNode( index );
		if ( this.referringNode == node ) {
			// Don't refresh the node if it hasn't changed
			//Tools.log("Thumbnail.setNode: determined that this node is being set to the same.");
			return;
		}

		unqueue();
		
		// unattach the change Listener
		if ( ( this.referringNode != null ) 
		  && ( this.referringNode.getUserObject() instanceof  PictureInfo ) ) {
			PictureInfo pi = (PictureInfo) this.referringNode.getUserObject();
			pi.removePictureInfoChangeListener( this );
		}

		// don't change while another thread might be using the Thumbnail
		// in particular the ThumbnailCreationThread.createThumbnail doesn't like it
		// if this node is changed while it is rendering a thumbnail.
		synchronized( this ) {
			this.referringNode = node;
		}

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
			//Tools.log("Thumbnail.setNode: called on node: " + node.getUserObject().toString());
			ThumbnailCreationQueue.requestThumbnailCreation( 
				this, priority, false );
		} 
	
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
		if ( icon == null ) { return;}
		img = icon.getImage();
		if ( img == null ) { return; }
		imgOb = icon.getImageObserver();
		
		/*short[] threshold = new short[256];
		for (int i = 0; i < 256; i++) threshold[i] = (i < 128) ? (short)0 : (short)255;
		BufferedImageOp thresholdOp = new LookupOp(new ShortLookupTable(0, threshold), null);
		BufferedImage destination = thresholdOp.filter(source, null);

		short[] invert = new short[256];
		for (int i = 0; i < 256; i++) invert[i] = (short)(255 - i);
		BufferedImageOp invertOp = new LookupOp(new ShortLookupTable(0, invert), null);
		BufferedImage destination = invertOp.filter(source, null); */

		RescaleOp darkenOp = new RescaleOp( .6f, 0, null );
		BufferedImage source = new BufferedImage(img.getWidth(imgOb),img.getHeight(imgOb),BufferedImage.TYPE_INT_BGR);
		source.createGraphics().drawImage( img, 0,0, null );
		selectedThumbnail = darkenOp.filter( source, null );

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
		//Tools.log("Thumbnail.setVisible: thumbnailSize: " + Integer.toString( thumbnailSize ) + " (int) ( thumbnailSize * thumbnailSizeFactor ): " 
		//	+ Integer.toString( (int) ( thumbnailSize * thumbnailSizeFactor ) ) );
		if ( visibility ) {
			setPreferredSize( new Dimension( (int) ( thumbnailSize * thumbnailSizeFactor ), (int) ( img.getHeight(imgOb) * thumbnailSizeFactor ) ) );
			setMaximumSize( new Dimension( (int) ( thumbnailSize * thumbnailSizeFactor ), (int) ( img.getHeight(imgOb) * thumbnailSizeFactor ) ) ); 
			setMinimumSize( new Dimension( (int) ( thumbnailSize * thumbnailSizeFactor ), (int) ( img.getHeight(imgOb) * thumbnailSizeFactor ) ) ); 
			setSize( new Dimension( (int) ( thumbnailSize * thumbnailSizeFactor ), (int) ( img.getHeight(imgOb) * thumbnailSizeFactor ) ) ); 
			revalidate();
			repaint();
		} else {
			setPreferredSize( new Dimension( (int) ( thumbnailSize * thumbnailSizeFactor ), 0 ));
			setMaximumSize( new Dimension( (int) ( thumbnailSize * thumbnailSizeFactor ), 0 )); 
			setMinimumSize( new Dimension( (int) ( thumbnailSize * thumbnailSizeFactor ), 0 )); 
			setSize( new Dimension( (int) ( thumbnailSize * thumbnailSizeFactor ), 0 ));
			revalidate();
		}
	}


	/**
	 *  This method sets the scaling factor for the display of a thumbnail.
	 */
	public void setFactor( float thumbnailSizeFactor ) {
		//Tools.log("Thumbnail.setFactor: " + Float.toString( thumbnailSizeFactor ) );
		this.thumbnailSizeFactor = thumbnailSizeFactor;
		setVisible( isVisible() );
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
	public void setassociatedPanel( ThumbnailJScrollPane associatedPanel ) {
		this.associatedPanel = associatedPanel;
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
			
			int focusPointx = (int) ( img.getWidth(imgOb) * thumbnailSizeFactor / 2 );
			int focusPointy = (int) ( img.getHeight(imgOb) * thumbnailSizeFactor / 2 );

			int X_Offset = (int) ((double) ( WindowWidth / 2 ) - ( focusPointx ));
			int Y_Offset = (int) ((double) ( WindowHeight / 2 ) - ( focusPointy ));

			// clear damaged component area
		 	Rectangle clipBounds = g2d.getClipBounds();
			g2d.setColor( getBackground()); 
			g2d.fillRect( clipBounds.x, 
				      clipBounds.y,
			              clipBounds.width, 
			      	      clipBounds.height);

			AffineTransform af1 = AffineTransform.getTranslateInstance((int) X_Offset, (int) Y_Offset);
			AffineTransform af2 = AffineTransform.getScaleInstance((double) thumbnailSizeFactor, (double) thumbnailSizeFactor);
			af2.concatenate( af1 );
			//op = new AffineTransformOp( af2, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
			
			
			if ( associatedPanel != null ) { 
				if ( associatedPanel.isSelected( referringNode ) ) {
					g2d.drawImage( selectedThumbnail, af2, imgOb);
				} else {
					g2d.drawImage( img, af2, imgOb);
				}
			} else {
				g2d.drawImage( img, af2, imgOb);
			} 

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
					PictureViewer pictureViewer = new PictureViewer();
					if ( myThumbnailBrowser == null ) {
						Tools.log("Thumbnail.mouseClicked: why does this Thumbnail not know the context it is showing pictures in?");
						myThumbnailBrowser = new SequentialBrowser( (SortableDefaultMutableTreeNode) referringNode.getParent() );
						int myIndex = 0;
						for ( int i=0; i <= myThumbnailBrowser.getNumberOfNodes(); i++ ) {
							if ( myThumbnailBrowser.getNode( i ).equals( referringNode ) ) {
								myIndex = i;
								i = myThumbnailBrowser.getNumberOfNodes() + 1;
							}
						}
					}
					pictureViewer.changePicture( myThumbnailBrowser, myIndex );
				} else if ( e.getButton() == 3 ) { // popup menu only on 3rd mouse button.
					//PicturePopupMenu picturePopupMenu = new PicturePopupMenu( referringNode );
					PicturePopupMenu picturePopupMenu = new PicturePopupMenu( myThumbnailBrowser, myIndex, null );
					picturePopupMenu.setSelection( associatedPanel );
					picturePopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}  else if ( ( e.getButton() == 1) && ( associatedPanel != null ) ) { // first button
					// I.e. selection
					if ( e.isControlDown() ) {
						if ( associatedPanel.isSelected( referringNode ) ) {
							associatedPanel.removeFromSelection( referringNode );
						} else {
							associatedPanel.setSelected( referringNode );
						}
					} else {
						if ( associatedPanel.isSelected( referringNode ) ) {
							associatedPanel.clearSelection();
						} else {
							associatedPanel.clearSelection();
							associatedPanel.setSelected( referringNode );
						}
					}
				}
			} else {
				if ( associatedPanel != null ) {		
					if  ( e.getClickCount() > 1 ) {
						associatedPanel.requestShowGroup( referringNode );
					}  else if ( e.getButton() == 3 ) { // popup menu only on 3rd mouse button.
						CollectionJTree CollectionJTree = associatedPanel.getAssociatedCollectionJTree();
						if ( CollectionJTree != null ) {
							CollectionJTree.popupNode = referringNode;
							GroupPopupMenu groupPopupMenu = new GroupPopupMenu( CollectionJTree, referringNode );
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
				this, ThumbnailCreationQueue.HIGH_PRIORITY, false );
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
		if ( ( associatedPanel != null )
		  && ( associatedPanel.isSelected( referringNode ) ) ) {
			showAsSelected();
		} else {
			showAsUnselected();
		}
	}


	/**
	 *  Determines whether decorations should be drawn or not
	 */
	public void setDecorateThumbnails( boolean b ) {
		decorateThumbnails = b;
	}


	/**
	 *  determines if the thumbnail is part of the mail selection and changes the drawMailIcon
	 *  flag to ensure that the mail icon will be place over the image.
	 */
	public void determineMailSlectionStatus() {
		if ( ( referringNode != null )
		  && decorateThumbnails 
		  && Settings.pictureCollection.isMailSelected( referringNode ) ) {
			drawMailIcon = true;
		} else {
			drawMailIcon = false;
		}
	}



	/**
	 *   Returns the preferred size for the Thumbnail as a Dimension using the thumbnailSize 
	 *   as widht and height.
	 */
	public Dimension getPreferredSize() {
		return new Dimension( thumbnailSize, thumbnailSize );
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
						this, ThumbnailCreationQueue.HIGH_PRIORITY, false );
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
		/*if ( associatedPanel != null ) {
			associatedPanel.removeFromSelection( referringNode );
		}*/
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
	 *   @see	CollectionJTree#dragOver(DropTargetDragEvent)
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
	 *   @see	CollectionJTree#dropActionChanged(DropTargetDragEvent)
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

			JpoTransferable t;
			if ( associatedPanel == null ) {
				Object[] nodes = { referringNode };
				t = new JpoTransferable( nodes );
			} else {
				if ( associatedPanel.countSelectedNodes() < 1 ) {
					Object[] nodes = { referringNode };
					t = new JpoTransferable( nodes );
				} else {
					t = new JpoTransferable( associatedPanel.getSelectedNodes() );
				}
			}
			
			try {
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
			if ( associatedPanel != null ) {
				associatedPanel.clearSelection();
			}
		}
	}

}
