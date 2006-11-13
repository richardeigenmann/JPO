package jpo;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

import java.awt.event.*;

import java.util.*;
import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;

import java.io.*;
import java.io.IOException;
import java.net.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.DefaultListModel;


/*
CollectionJTree.java:  class that creates a JTree for the collection

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
 *   The is one of the main classes in the JPO application as it is an extended JTree that
 *   deals with most of the logic surrounding the collection and the user interactions with it.
 */
public class CollectionJTree extends JTree 
	    implements 	DropTargetListener,
	    		DragSourceListener, 
			DragGestureListener,
			GroupPopupInterface {



	/**
	 * enables this component to be a dropTarget
	 */
	DropTarget dropTarget = null;

	/**
	 * enables this component to be a Drag Source
	 */
	DragSource dragSource = null;



	/** 
	 *  a temporaty object that serves as a reference to the node that was selected when the popup menu was activated.
	 *  When the user selects the action on the popup menu this node is then used as the starting point.
	 *
	 **/	 	 
	public SortableDefaultMutableTreeNode popupNode = null;

	/** 
	 *  a temporaty object that serves as a reference to the node that was selected when the popup menu was activated.
	 *  When the user selects the action on the popup menu this node is then used as the starting point.
	 *
	 **/	 	 
	private TreePath popupPath ;




	/**
	 *   a reference to the Thumbnail Pane that is displaying pictures. This
	 *   allows the CollectionJTree to tell the Thumbnail Pane to display groups 
	 *   of pictures via it's showGroup method.
	 */
	private ThumbnailJScrollPane associatedThumbnailJScrollPane;

	/**
	 *   a reference to the Info Panel that is displaying information. This
	 *   allows the CollectionJTree to tell the Info Panel about selections
	 *   via it's showInfo method.
	 */
	private InfoPanel associatedInfoPanel;


			
	/**
	 * Constructs a JTree.
	 *
	 */
	public CollectionJTree() {
		getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
		putClientProperty( "JTree.lineStyle", "Angled" );
		setOpaque( true );
		setEditable(false);
		setShowsRootHandles( true );
		setCellRenderer( new JpoTreeCellRenderer() );
		setBackground( Settings.JPO_BACKGROUND_COLOR );
		setMinimumSize( Settings.jpoNavigatorJTabbedPaneMinimumSize );
		//setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );
		

		// set up drag & drop
		dropTarget = new DropTarget (this, this);
		dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer( this, DnDConstants.ACTION_COPY_OR_MOVE, this );

		//Add listener to components that can bring up groupPopupJPopupMenu menus.
		CollectionMouseAdapter mouseAdapter = new CollectionMouseAdapter( this );
		addMouseListener( mouseAdapter );

		setModel( Settings.pictureCollection.getTreeModel() );
	}

	

	/**  
	 *   This method assigns the supplied ThumbnailJScrollpane with this JTree. This association is
	 *   used to allow the JTree to order the ThumbnailJScrollpane to display a different group.
	 */
	public void setAssociatedThumbnailJScrollpane( ThumbnailJScrollPane associatedThumbnailJScrollPane ) {
		this.associatedThumbnailJScrollPane = associatedThumbnailJScrollPane;
	}


	/**  
	 *   This method assigns the supplied InfoPanel with this JTree. This association is
	 *   used to allow the JTree to tell the InfoPanel to show information about selections.
	 */
	public void setAssociatedInfoPanel( InfoPanel associatedInfoPanel ) {
		this.associatedInfoPanel = associatedInfoPanel;
	}



	/**
	 *   Moves the highlighted row to the indicated one and makes sure it is on the screen.
	 */
	public void setSelectedNode( SortableDefaultMutableTreeNode selectedNode ) {
		//Tools.log("CollectionJTree.setSelectedNode: called for node: " + selectedNode.toString() );
		TreePath tp = new TreePath( selectedNode.getPath() );
		setSelectionPath( tp );
		scrollPathToVisible( tp );
		if ( associatedInfoPanel != null ) {
			associatedInfoPanel.showInfo( selectedNode );
		}
	}



	/**
	 *  callback method that is called when a drag gesture has been initiated on a node of the JTree.
	 *  @param  event    The Drag and Drop Framework gives us details about the detected event in this parameter.
	 * 
	 */
	public void dragGestureRecognized( DragGestureEvent event) {
		if( ( event.getDragAction() & DnDConstants.ACTION_COPY_OR_MOVE ) == 0) {
			return;
		}
	
		TreePath selected = getSelectionPath();
		SortableDefaultMutableTreeNode dmtn = (SortableDefaultMutableTreeNode) selected.getLastPathComponent();
		// Tools.log("CollectionJTree.dragGestureRecognized: Drag started on node: " + dmtn.getUserObject().toString() );
		if ( dmtn.isRoot() ) {
			Tools.log( "The Root node must not be dragged. Dragging disabled." );
			return;
		}
		final Object t[] = { dmtn };
		JpoTransferable draggedNode = new JpoTransferable ( t );
		event.startDrag( DragSource.DefaultMoveDrop, draggedNode, this);
	}



	/**
	 *   this callback method is invoked every time something is 
	 *   dragged onto the JTree. We check if the desired DataFlavor is 
	 *   supported and then reject the drag if it is not.
	 */
	public void dragEnter ( DropTargetDragEvent event ) {
		if ( ! event.isDataFlavorSupported( JpoTransferable.dmtnFlavor ) ) {
			event.rejectDrag();
		}
	}



	/**
	 *  this callback method is invoked after the dropTaget had a chance
	 *  to evaluate the drag event and was given the option of rejecting or
	 *  modifying the event. This method sets the cursor to reflect
	 *  whether a copy, move or no drop is possible.
	 * 
	 */
	public void dragEnter ( DragSourceDragEvent event ) {
		//Tools.log( "CollectionJTree.dragEnter(DragSourceDragEvent): invoked");
		Tools.setDragCursor( event );
	}



	
	/**
	 *   this callback method is invoked every time something is 
	 *   dragged over the JTree. We check if the desired DataFlavor is 
	 *   supported and then reject the drag if it is not.
	 * 
	 */
	public void dragOver ( DropTargetDragEvent event ) {
		//Tools.log("CollectionJTree.dragOver (DropTargetDragEvent) triggered");
		if ( ! event.isDataFlavorSupported( JpoTransferable.dmtnFlavor )) {
			Tools.log ("CollectionJTree.dragOver (DropTargetDragEvent): The dmtn DataFlavor is not supported. Rejecting drag.");
			event.rejectDrag();
		} else {
			// figure out where the cursor is and highlight the node
			//  Highlighting turned off as it is very annoying, RE 18.12.2003
			popupPath = getPathForLocation( event.getLocation().x, event.getLocation().y );
			if ( popupPath != null ) {
				event.acceptDrag( DnDConstants.ACTION_COPY_OR_MOVE );
				popupNode = (SortableDefaultMutableTreeNode) popupPath.getLastPathComponent();
				setSelectionPath( popupPath );
			} else  {
				//Tools.log("CollectionJTree.dragOver( DropTargetDragEvent ): the coordinates returned by the event do not match a selectable path." );
				event.rejectDrag();
			}
		}
	}
	

	/**
	 *  this callback method is invoked after the dropTaget had a chance
	 *  to evaluate the dragOver event and was given the option of rejecting or
	 *  modifying the event. This method sets the cursor to reflect
	 *  whether a copy, move or no drop is possible.
	 * 
	 */
	public void dragOver ( DragSourceDragEvent event ) {
		//Tools.log("CollectionJTree.dragOver(DragSourceDragEvent) invoked");
		Tools.setDragCursor( event );
	}


	/**
	 *  this callback method is invoked when the user presses or releases Ctrl when
	 *  doing a drag. He can signal that he wants to change the copy / move of the 
	 *  operation. This method could intercept this change and could modify the event
	 *  if it needs to. 
	 *  Here we use this as a convenient handle to expand and collapse the Tree.
	 * 
	 */
	public void dropActionChanged ( DropTargetDragEvent event ) {
		// figure out where the cursor is and highlight the node
		TreePath popupPath = getPathForLocation( event.getLocation().x, event.getLocation().y );
		if ( popupPath != null ) {
			SortableDefaultMutableTreeNode popupNode = (SortableDefaultMutableTreeNode) popupPath.getLastPathComponent();
			Tools.log("CollectionJTree.dropActionChanged( DropTargetDragEvent ): hovering over: " + popupNode.getUserObject().toString() );
			if ( this.isExpanded( popupPath ) )
				this.collapsePath( popupPath );
			else
				this.expandPath( popupPath );
		} else  {
			Tools.log("CollectionJTree.dropActionChanged( DropTargetDragEvent ): the coordinates returned by the event do not match a selectable path." );
		}
	}


	/**
	 *  this callback method is invoked when the user presses or releases shift when
	 *  doing a drag. He can signal that he wants to change the copy / move of the 
	 *  operation. This method changes the cursor to reflect the mode of the 
	 *  operation.
	 */
	public void dropActionChanged ( DragSourceDragEvent event ) {
		//Tools.log( "CollectionJTree.dropActionChanged( DragSourceDragEvent ): invoked"); 
		Tools.setDragCursor( event );
	}



	/**
	 *   this callback method is invoked to tell the dropTarget that the drag has moved on
	 *   to something else. We do nothing here.
	 */
	public void dragExit ( DropTargetEvent event ) {
		//Tools.log("CollectionJTree.dragExit( DropTargetEvent ): invoked");
	}
	




	/**
	 *   this callback method is invoked to tell the dragSource that the drag has moved on
	 *   to something else. We do nothing here.
	 */
	public void dragExit ( DragSourceEvent event ) {
	}



	/**
	 *  Entry point for the drop event. Figures out which node the drop occured on and 
	 *  sorts out the drop action in the data model.
	 */
	public void drop ( DropTargetDropEvent event ) {
		Point p = event.getLocation();
		TreePath targetPath = this.getPathForLocation (p.x, p.y);
		if ( targetPath == null ) {
			Tools.log( "CollectionJTree.drop(DropTargetDropEvent): The drop coordinates do not specify a node. Drop aborted.");
			event.dropComplete( false );
			return;
		} else {
			SortableDefaultMutableTreeNode targetNode = (SortableDefaultMutableTreeNode) targetPath.getLastPathComponent();
			targetNode.executeDrop ( event ); 
		}
	}





	/**
	 * this callback message goes to DragSourceListener, informing it that the dragging 
	 * has ended. 
	 * 
	 */
	public void dragDropEnd ( DragSourceDropEvent event ) {   
	}



	/**
	 *  requests the group to be shown.
	 *  @see  GroupPopupInterface
	 */
	public void requestShowGroup() {
		requestShowGroup( popupNode );
	}

	/**
	 *  requests the group to be shown.
	 *  @see  GroupPopupInterface
	 */
	public void requestShowGroup( SortableDefaultMutableTreeNode newNode ) {
		setSelectedNode( newNode );
		if ( associatedThumbnailJScrollPane != null ) {
			associatedThumbnailJScrollPane.show( new GroupBrowser( newNode ) );
		}
	}



	/**
	 *  requests the pictures to be shown.
	 *  @see  GroupPopupInterface
	 */
	public void requestSlideshow() {
		SortableDefaultMutableTreeNode firstPicNode = popupNode.findFirstPicture();
		if ( firstPicNode != null ) {
			//firstPicNode.showLargePicture();
			SequentialBrowser sb = new SequentialBrowser( popupNode );
			PictureViewer pictureViewer = new PictureViewer();
			pictureViewer.changePicture( sb, 0 );
		} else {
			JOptionPane.showMessageDialog( Settings.anchorFrame, 
				Settings.jpoResources.getString( "noPicsForSlideshow" ), 
				Settings.jpoResources.getString( "genericError" ), 
				JOptionPane.ERROR_MESSAGE);
		}
	}





	/**
	 *  This method can be invoked by the GroupPopupMenu. 
	 *
	 *  @see  GroupPopupInterface
	 */
	public void requestFind() {
		new QueryJFrame( popupNode, associatedThumbnailJScrollPane );
	}


	/**
	 *  this method invokes an editor for the GroupInfo data 
	 *  @see  GroupPopupInterface
	 */
	public void requestEditGroupNode() {
		popupNode.showEditGUI();
	}


	/**
	 *  this method ivokes the Category editor and allows the user to set the categories for all the pictures in the Group.
	 */
	public void showCategoryUsageGUI() {
		popupNode.showCategoryUsageGUI();
	}


	/**
	 *  requests a new empty group to be added.
	 *  @see  GroupPopupInterface
	 */
	public void requestAddGroup() {
		SortableDefaultMutableTreeNode newNode = popupNode.addGroupNode( "New Group" );
		setSelectedNode ( newNode );
	}




	/**
	 *  requests pictures to be added at the popup node.
	 *  @see  GroupPopupInterface
	 */
	public void requestAdd() {
		PictureAdder pa = new PictureAdder( popupNode );
		pa.setNotificationTarget( this );
	}


	/**
	 *  requests that a collection be added at this point in the tree
	 *  @see GroupPopupInterface
	 */
	public void requestAddCollection() {
		SortableDefaultMutableTreeNode newNode = popupNode.addGroupNode( "New Group" );
		newNode.fileLoad();
		newNode.getPictureCollection().setUnsavedUpdates( true );
		setSelectedNode ( newNode );
		expandPath( new TreePath ( newNode.getPath()) );
		if ( associatedThumbnailJScrollPane != null ) {
			associatedThumbnailJScrollPane.show( new GroupBrowser( popupNode ) );
		}
		if ( associatedInfoPanel != null ) {
			associatedInfoPanel.showInfo( popupNode );
		}
	}





	/**
	 *  method that will bring up a dialog box that allows the user to select how he wants 
	 *  to export the pictures of the current Group.
	 **/
	public void requestGroupExportHtml() {
		new HtmlDistillerJFrame( popupNode );

	}


	/**
	 *  requests that the pictures indicated in a flat file be added at this point in the tree
	 *  @see GroupPopupInterface
	 */
	public void requestGroupExportFlatFile() {
		javax.swing.JFileChooser jFileChooser = new javax.swing.JFileChooser();
		jFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		jFileChooser.setDialogTitle( Settings.jpoResources.getString("saveFlatFileTitle") );
		jFileChooser.setApproveButtonText( Settings.jpoResources.getString("saveFlatFileButtonLabel") );
		jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );
		
		int returnVal = jFileChooser.showSaveDialog( Settings.anchorFrame );
		if ( returnVal == JFileChooser.APPROVE_OPTION ) {
			File chosenFile = jFileChooser.getSelectedFile();
			new FlatFileDistillerThread( chosenFile, popupNode );
		}

	}


	/**
	 *  requests that a group be exported to a jar archive
	 *  @see  GroupPopupInterface
	 */
	public void requestGroupExportJar() {
//		new JarDistillerJFrame( popupNode );
	}


	/**
	 *  requests that a group be exported to a new collectionjar archive
	 *  @see  GroupPopupInterface
	 */
	public void requestGroupExportNewCollection() {
		new CollectionDistillerJFrame( popupNode );
	}



	/**
	 *  requests that a group be removed
	 *  @see  GroupPopupInterface
	 */
	public void requestGroupRemove() {
		Tools.log( "CollectionJTree.requestGroupRemove: invoked on group: " + popupNode.getUserObject().toString() );
		SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) popupNode.getParent();
		if ( popupNode.deleteNode() ) {
			setSelectedNode ( parentNode );
		}
	}



	/**
	 *  requests that a group's picture files be consolidated
	 *  @see  GroupPopupInterface
	 */
	public void requestConsolidateGroup() {
		new ConsolidateGroupJFrame( popupNode );
	}


	/**
	 *  requests that a group be moved to the top
	 *  @see  GroupPopupInterface
	 */
	public void requestMoveGroupToTop() {
		popupNode.moveNodeToTop();
	}


	/**
	 *  requests that a group be moved up
	 *  @see  GroupPopupInterface
	 */
	public void requestMoveGroupUp() {
		popupNode.moveNodeUp();
	}




	/**
	 *  requests that a group be moved down
	 *  @see  GroupPopupInterface
	 */
	public void requestMoveGroupDown() {
		popupNode.moveNodeDown();
	}



	/**
	 *  requests that a group be moved down
	 *  @see  GroupPopupInterface
	 */
	public void requestMoveGroupToBottom() {
		popupNode.moveNodeToBottom();
	}


	/**
	 *  requests that a picture be moved to the target Group node
	 *  @see  GroupPopupInterface
	 */
	public void requestMoveToNode( SortableDefaultMutableTreeNode targetGroup ) {
		popupNode.moveToNode( targetGroup );
	}



	/** 
	 *  request that a group be edited as a table
	 */
	public void requestEditGroupTable() {
		TableJFrame tableJFrame = new TableJFrame( popupNode );
		tableJFrame.pack();
		tableJFrame.setVisible( true );
	}





	/**
	 *  gets called by the GroupPopupInterface and implements the sort request.
	 */
	public void requestSort( int sortCriteria ) {
		Tools.log ( "Sort requested on " + popupNode.toString() + " for Criteria: " + Integer.toString( sortCriteria ) );
		popupNode.sortChildren( sortCriteria );
		((DefaultTreeModel) getModel()).nodeStructureChanged( popupNode );
	}
	

	


	/**  
	 *  subclass to determine if a popup Menu should be shown.
	 *  the groupPopupJPopupMenu menu must exist.
	 **/
	private class CollectionMouseAdapter extends MouseAdapter {
		/**
		  *  A reference back to the CollectionJTree for which this is a listener.
		  */
		private CollectionJTree collectionJTree;

		private CollectionMouseAdapter( CollectionJTree collectionJTree ) {
			this.collectionJTree = collectionJTree;
		}
		
		
		/**
		 *    If the mouse was clicked more than once using the left mouse button over a valid picture
		 *    node then the picture editor is opened.
		 */
		public void mouseClicked( MouseEvent e ) {
			TreePath clickPath = getPathForLocation( e.getX(), e.getY() );
			if ( clickPath == null ) return; // happens
			SortableDefaultMutableTreeNode clickNode = (SortableDefaultMutableTreeNode) clickPath.getLastPathComponent();

			if ( associatedInfoPanel != null ) {
				associatedInfoPanel.showInfo( clickNode );
			}

			if ( e.getClickCount() == 1 && (! e.isPopupTrigger() ) ) {
				if ( clickNode.getUserObject() instanceof GroupInfo ) {
					if ( associatedThumbnailJScrollPane != null ) {
						associatedThumbnailJScrollPane.show( new GroupBrowser( clickNode ) );
					}
				}
			} else 	if ( e.getClickCount() > 1 && (! e.isPopupTrigger() ) ) {
				if ( clickNode.getUserObject() instanceof PictureInfo ) {
					//clickNode.showLargePicture();
					SequentialBrowser sb = new SequentialBrowser( (SortableDefaultMutableTreeNode) clickNode.getParent() );
					int index = 0;
					for ( int i=0; i <= sb.getNumberOfNodes(); i++ ) {
						if ( sb.getNode( i ).equals( clickNode ) ) {
							index = i;
							i = sb.getNumberOfNodes() + 1;
						}
					}
					PictureViewer pictureViewer = new PictureViewer();
					pictureViewer.changePicture( sb, index );
				}  
			}
		}
		
		/**
		 *   Override thge mousePressed event.
		 */
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}
		
		/**
		 *  Override the mouseReleased event.
		 */
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}
		
		/**
		 *  This method figures out whether a popup window should be displayed and displays 
		 *  it.
		 *  @param   e	The MouseEvent that was trapped.
		 */
		private void maybeShowPopup( MouseEvent e ) {
			if ( e.isPopupTrigger() ) {
				popupPath = getPathForLocation(e.getX(), e.getY());
				if ( popupPath == null ) return; // happens
				popupNode = (SortableDefaultMutableTreeNode) popupPath.getLastPathComponent();
				setSelectionPath ( popupPath );
				Object nodeInfo = popupNode.getUserObject();
				
				if (nodeInfo instanceof GroupInfo) {
					GroupPopupMenu groupPopupMenu = new GroupPopupMenu( collectionJTree, popupNode );				
					groupPopupMenu.show(e.getComponent(), e.getX(), e.getY());
				} else if (nodeInfo instanceof PictureInfo) {
					SequentialBrowser sb = new SequentialBrowser( (SortableDefaultMutableTreeNode) popupNode.getParent() );
					int index = 0;
					for ( int i=0; i <= sb.getNumberOfNodes(); i++ ) {
						if ( sb.getNode( i ).equals( popupNode ) ) {
							index = i;
							i = sb.getNumberOfNodes() + 1;
						}
					}
					PicturePopupMenu picturePopupMenu = new PicturePopupMenu( sb, index, null );
					picturePopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}
	}




	/**
	 *  Icon of a closed folder to be used on groups that are not expanded in the JTree.
	 */
	private static final ImageIcon closedFolderIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_folder_closed.gif" ) );
		
	/**
	 *  Icon of an open folder to be used on groups that are expanded in the JTree.
	 */
	private static final ImageIcon openFolderIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_folder_open.gif" ) );
		
	/**
	 *  Icon of a picture for use on picture bearing nodes in the JTree.
	 */
	private static final ImageIcon pictureIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_picture.gif" ) );


	/**
	 *   Inner class that extends the DefaulTreeCellRenderer to provide 
	 *   customised icons to the tree nodes
	 */
	private class JpoTreeCellRenderer extends DefaultTreeCellRenderer {

		/**
		 *   Constructs a new JpoTreeCellRenderer.
		 */		
		public JpoTreeCellRenderer() {
		}


		/**
		 *  Overriden method that sets the icon in the JTree to either a
		 *  {@link #closedFolderIcon} or a {@link #openFolderIcon} or a
		 *  {@link #pictureIcon} depending on what dort of userObject
		 *  the SortableDefaultMutableTreeNode is carrying and the expansion state
		 *  of the node.
		 */
		public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {
			
		        super.getTreeCellRendererComponent(
                        	tree, value, sel,
	                        expanded, leaf, row,
        	                hasFocus);
				
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object userObject = node.getUserObject();
			if ( userObject instanceof GroupInfo ) {
				if ( expanded ) {
					setIcon( openFolderIcon );
				} else {
					setIcon( closedFolderIcon );
				}
			} else {
				setIcon( pictureIcon );
			}
			return this;
		}
	}




}
