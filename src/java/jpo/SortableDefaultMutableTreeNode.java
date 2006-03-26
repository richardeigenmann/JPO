package jpo;

import javax.swing.tree.*;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream; 
import javax.swing.event.TreeModelEvent;
import java.net.*;
import java.io.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import com.drew.metadata.*;
import com.drew.metadata.exif.*;
import com.drew.metadata.iptc.*;
import com.drew.imaging.jpeg.*;
import com.drew.imaging.jpeg.JpegSegmentReader.*;
import java.awt.event.*;
import javax.swing.*;


/*
SortableDefaultMutableTreeNode.java:  A DefaultMutableTreeNode that knows how to compare my objects

Copyright (C) 2003  Richard Eigenmann.
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
 *   This class extends the DefaultMutableTreeNode with the Comparable
 *   Interface that allows our nodes to be compared.
 */

public class SortableDefaultMutableTreeNode extends DefaultMutableTreeNode
	implements Comparable, Serializable {


	/**
	 *   Constructor for a new node.
	 */
	public SortableDefaultMutableTreeNode () {
		super ();
	}

	/**
	 *   Constructor for a new node including a user object.
	 */
	public SortableDefaultMutableTreeNode ( Object userObject ) {
		super ( userObject );
	}


	/**
	 *   returns the root node. This is a convenience method.
	 *   @return 	The root node of the JTree.
	 */
	public SortableDefaultMutableTreeNode getRootNode() {
		return (SortableDefaultMutableTreeNode) this.getRoot();
	}


	/**
	 *   returns the collection associated with this node
	 */
	public PictureCollection getPictureCollection() {
		return Settings.pictureCollection;
	}


	/**
	 *  Call this method to sort the Children of a node by a field. The field codes are 
	 *  the ones in the Settings object.
	 */
	public void sortChildren( int sortCriteria ) {
	        int childCount = getChildCount();
		SortableDefaultMutableTreeNode[] childNodes = new SortableDefaultMutableTreeNode[ childCount ];
		for ( int i = 0; i < childCount; i++ ){
			childNodes[i] = (SortableDefaultMutableTreeNode) getChildAt(i);		
		}
		
		// sort the array
		sortfield = sortCriteria;
		Arrays.sort( childNodes );
		
	        //Remove all children from the node
		getPictureCollection().setSendModelUpdates( false );
	        removeAllChildren();
	        //Add the new array of nodes to top
        	for ( int i = 0; i < childNodes.length; i++ ){
			add( childNodes[i] );
			Tools.log("Adding back to group: " + childNodes[i].toString() );
		}
		getPictureCollection().setSendModelUpdates( true );
		getPictureCollection().getTreeModel().nodeStructureChanged( this );
	}


	/**
	 *  This field records the field by which the group is to be sorted. This is not very
	 *  elegant as a second sort could run at the same time and clobber this global variable.
	 *  But that's not very likely on a single user app like this.
	 */
	private static int sortfield;


	/**
	 *   Overriden method to allow sorting of nodes. It uses the static global variable
	 *   sortfield to figure out what to compare on.
	 */
	public int compareTo( Object o ) {
		Object myObject = getUserObject();
		Object otherObject = ((DefaultMutableTreeNode) o).getUserObject();
		//Tools.log( "Comparing " + myObject.toString() + " against " + otherObject.toString() );
		
		if ( ( myObject instanceof GroupInfo ) 
		  && ( otherObject instanceof GroupInfo )
		  && ( sortfield == Settings.DESCRIPTION )
		   )
		 	return ((GroupInfo) myObject).getGroupName().compareTo( ((GroupInfo) otherObject).getGroupName() );
			
		if ( ( myObject instanceof GroupInfo ) 
		  && ( otherObject instanceof PictureInfo )
		  && ( sortfield == Settings.DESCRIPTION )
		   )
		 	return ((GroupInfo) myObject).getGroupName().compareTo( ((PictureInfo) otherObject).getDescription() );

		if ( ( myObject instanceof PictureInfo ) 
		  && ( otherObject instanceof GroupInfo )
		  && ( sortfield == Settings.DESCRIPTION )
		   )
		 	return ((PictureInfo) myObject).getDescription().compareTo( ((GroupInfo) otherObject).getGroupName() );
		
		if ( (myObject instanceof GroupInfo) || (otherObject instanceof GroupInfo ) ) {
			// we can't compare Groups against the other types of field other than the description.
			return 0;
		}
		
		// at this point there can only two PictureInfo Objects
		PictureInfo myPi = (PictureInfo) myObject;
		PictureInfo otherPi = (PictureInfo) otherObject;
		switch ( sortfield ) {
			case Settings.DESCRIPTION:
				return myPi.getDescription().compareTo( otherPi.getDescription() );
			case Settings.FILM_REFERENCE:
				return myPi.getFilmReference().compareTo( otherPi.getFilmReference() );
			case Settings.CREATION_TIME:
				return myPi.getCreationTime().compareTo( otherPi.getCreationTime() );
			case Settings.COMMENT:
				return myPi.getComment().compareTo( otherPi.getComment() );
			case Settings.PHOTOGRAPHER:
				return myPi.getPhotographer().compareTo( otherPi.getPhotographer() );
			case Settings.COPYRIGHT_HOLDER:
				return myPi.getCopyrightHolder().compareTo( otherPi.getCopyrightHolder() );
			default:
				return myPi.getDescription().compareTo( otherPi.getDescription() );
		}
	}




	/**
	 *  Returns the first node with a picture before the current one in the tree.
	 *  It uses the getPreviousNode method of DefaultMutableTreeNode.
	 *
	 *  @return	the first node with a picture in preorder traversal or null if none found.
	 */
	public SortableDefaultMutableTreeNode getPreviousPicture() {
		DefaultMutableTreeNode prevNode = getPreviousNode();
		while ( (prevNode != null) && (! (prevNode.getUserObject() instanceof PictureInfo))) {
			prevNode = prevNode.getPreviousNode();
		}
		return (SortableDefaultMutableTreeNode) prevNode;		
	}

	

	/**
	 *   Returns the next node with a picture found after current one in the tree. This can 
	 *   be in another Group.
	 *   It uses the getNextNode method of the DefaultMutableTreeNode.
	 *
	 *   @return			The SortableDefaultMutableTreeNode that represents the next 
	 *				picture. If no picture can be found it returns null.
	 *
	 */
	public SortableDefaultMutableTreeNode getNextPicture () {
		DefaultMutableTreeNode nextNode = getNextNode();
		while ( ( nextNode != null ) && (! ( nextNode.getUserObject() instanceof PictureInfo ) ) )
			nextNode = nextNode.getNextNode();
		return ( SortableDefaultMutableTreeNode ) nextNode;
	}
	

	/**
	 *   Returns the next node with a picture found after current one in the current Group
	 *   It uses the getNextSibling method of the DefaultMutableTreeNode.
	 *
	 *   @return			The SortableDefaultMutableTreeNode that represents the next 
	 *				picture. If no picture can be found it returns null.
	 *
	 */
	public SortableDefaultMutableTreeNode getNextGroupPicture () {
		DefaultMutableTreeNode nextNode = getNextSibling();
		while ( ( nextNode != null ) && (! ( nextNode.getUserObject() instanceof PictureInfo ) ) )
			nextNode = nextNode.getNextNode();
		return ( SortableDefaultMutableTreeNode ) nextNode;
	}


	/**
	 *  Returns the first child node under the current node which holds a PictureInfo object.
	 *  
	 *  @return  The first child node holding a picture or null if none can be found.
	 */
	public SortableDefaultMutableTreeNode findFirstPicture() {
		SortableDefaultMutableTreeNode testNode;
		Enumeration e = children();
		while ( e.hasMoreElements() ) {
			testNode = (SortableDefaultMutableTreeNode) e.nextElement();
			if ( testNode.getUserObject() instanceof PictureInfo ) {
				return testNode;
			} else if ( testNode.getUserObject() instanceof GroupInfo ) {
				testNode = testNode.findFirstPicture();
				if ( testNode != null ) {
					return testNode;
				}
			}
		}
		return null;
	}







	/**
	 * @deprecated
	 */
	public void setUnsavedUpdates() {
		Settings.pictureCollection.setUnsavedUpdates() ;
	}	




 


	/**
	 *  This method fires up a user function if it can. User functions are only valid on 
	 *  PictureInfo nodes.
	 *
	 *  @param  userFunction	The user function to be executed
	 */
	public void runUserFunction( int userFunction ) {
		Object myObject = getUserObject();
		if ( ! ( myObject instanceof PictureInfo ) ){
			Tools.log("SortableDefaultMutableTreeNode.runUserFunction: was called on an Object that wasn't a PictureInfo. Aborting.");
			return;
		}
		if ( ( userFunction < 0 ) || ( userFunction >= Settings.maxUserFunctions ) ) {
			Tools.log("SortableDefaultMutableTreeNode.runUserFunction: was called with an out of bounds index");
			return;
		}
		String command = Settings.userFunctionCmd[ userFunction ];
		if ( ( command == null ) || ( command.length() == 0 ) ) {
			Tools.log("SortableDefaultMutableTreeNode.runUserFunction: command " + Integer.toString(userFunction) + " is not properly defined");
			return;
		}

		String filename = ((PictureInfo) myObject).getHighresFile().toString();
		command = command.replaceAll("%f", filename );

		String escapedFilename = filename.replaceAll( "\\s", "\\\\\\\\ " );
		command = command.replaceAll("%e", escapedFilename );


		URL pictureURL = ((PictureInfo) myObject).getHighresURLOrNull();
		if ( pictureURL == null ) {
			Tools.log("SortableDefaultMutableTreeNode.runUserFunction: The picture doesn't have a valid URL. This is bad. Aborted.");
			return;
		}
		command = command.replaceAll("%u", pictureURL.toString());
		
		Tools.log("SortableDefaultMutableTreeNode.runUserFunction: Command to run is: " + command);
		try {
			// Had big issues here because the simple exec (String) calls a StringTokenizer
			// which messes up the filename parameters
			int blank = command.indexOf( " " );
			if ( blank > -1 ) {
				String[] cmdarray = new String[2];
				cmdarray[0] = command.substring( 0, blank );
				cmdarray[1] = command.substring( blank+1 );
				Runtime.getRuntime().exec( cmdarray );
			} else {
				String[] cmdarray = new String[1];
				cmdarray[0] = command;
				Runtime.getRuntime().exec( cmdarray );
			}
				
		} catch ( IOException x ) {
			Tools.log("SortableDefaultMutableTreeNode.runUserFunction: Runtime.exec collapsed with and IOException: " + x.getMessage() );
		}

	}




	

	/**
	 *   This method is called by the drop method of the DragTarget to do the 
	 *   move. It deals with the intricacies of the drop event and handles all
	 *   the moving, cloning and positioning that is required.
	 *
	 *   @param event The event the listening object received.
	 */
	public void executeDrop ( DropTargetDropEvent event ) {
		//Tools.log( "SDMTN.executeDrop: invoked");
		
		if ( ! event.isLocalTransfer() ) {
			Tools.log( "SDMTN.executeDrop: detected that the drop is not a local Transfer. These are not supported. Aborting drop.");
			event.rejectDrop();
			event.dropComplete( false );
			return;
		}					
		
		if ( ! event.isDataFlavorSupported ( JpoTransferable.dmtnFlavor )) {
			Tools.log("SDMTN.executeDrop: The local drop does not support the dmtnFlavor DataFlavor. Drop rejected." );
			event.rejectDrop();
			event.dropComplete( false );
			return;
		}
		
		
		int actionType= event.getDropAction();
		if ( ( actionType == DnDConstants.ACTION_MOVE )
		   || ( actionType == DnDConstants.ACTION_COPY ) ) {
			event.acceptDrop( actionType );   // crucial Step!
		} else {
			Tools.log("SDMTN.executeDrop: The event has an odd Action Type. Drop rejected." );
			event.rejectDrop();
			event.dropComplete( false );
			return;
		}
		
				
		SortableDefaultMutableTreeNode sourceNode;
		int originalHashCode;
		Object[] arrayOfNodes;
		
		try {
			Transferable t = event.getTransferable();
			Object o =  t.getTransferData( JpoTransferable.dmtnFlavor );
			arrayOfNodes = (Object[]) o;
		} catch ( java.awt.datatransfer.UnsupportedFlavorException x ) {
			Tools.log( "SDMTN.executeDrop caught an UnsupportedFlavorException: message: " + x.getMessage() );
			event.dropComplete( false );
			return;
		} catch ( java.io.IOException x ) {
			Tools.log( "SDMTN.executeDrop caught an IOException: message: " + x.getMessage() );
			event.dropComplete( false );
			return;
		} catch ( ClassCastException x ) {
			Tools.log( "SDMTN.executeDrop caught an ClassCastException: message: " + x.getMessage() );
			event.dropComplete( false );
			return;
		}
		
		
		/* We must ensure that if the action is a move it does not drop into 
		itself or into a child of itself. */
		for ( int i=0; i<arrayOfNodes.length; i++ ) {
			sourceNode = (SortableDefaultMutableTreeNode) arrayOfNodes[ i ];
			if ( this.isNodeAncestor( sourceNode ) ) {
				JOptionPane.showMessageDialog( Settings.anchorFrame, 
					Settings.jpoResources.getString("moveNodeError"), 
					Settings.jpoResources.getString("genericError"), 
					JOptionPane.ERROR_MESSAGE);
				event.dropComplete( false );
				return;
			}			
		}
		
		

		// The drop is a valid one.
		
		//  memorise the group of the drop location.
		SortableDefaultMutableTreeNode groupOfDropLocation;
		if ( this.getUserObject() instanceof GroupInfo ) {
			groupOfDropLocation = this;
		} else { 
			// the parent must be a group node
			groupOfDropLocation = (SortableDefaultMutableTreeNode) this.getParent();
		}
		if ( ( groupOfDropLocation != null ) && ( groupOfDropLocation.getUserObject() instanceof GroupInfo ) ) {
			Settings.memorizeGroupOfDropLocation( groupOfDropLocation );
		} else {
			Tools.log( "SDMTN.executeDrop failed to find the group of the drop location. Not memorizing.");
		}
	
	
		boolean dropcomplete = false;
		for ( int i=0; i<arrayOfNodes.length; i++ ) {
			sourceNode = (SortableDefaultMutableTreeNode) arrayOfNodes[ i ];
		
			if ( ( sourceNode.getUserObject() instanceof PictureInfo ) && ( this.getUserObject() instanceof GroupInfo ) ) {
				// a picture is being dropped onto a group; add it at the end
				if ( actionType == DnDConstants.ACTION_MOVE ) {
					Tools.log ("Moving Picture onto Group --> add picture to bottom of group");
					sourceNode.removeFromParent();  //SDTMN removeFromParents fire the model notification
					add( sourceNode );  //SDTMN adds fire the model notifications
				} else {
					// it was a copy event
					SortableDefaultMutableTreeNode newNode = new SortableDefaultMutableTreeNode( ( (PictureInfo) sourceNode.getUserObject() ).getClone() );
					add( newNode );
				}
				dropcomplete = true;	
				setUnsavedUpdates();
			} else if ( ( sourceNode.getUserObject() instanceof PictureInfo ) && ( this.getUserObject() instanceof PictureInfo )) {
				// a picture is being dropped onto a picture
						
				// insert the new Node in front of the current node.
				SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
				int currentIndex = parentNode.getIndex( this );

				// position is one less if the source is further up the list than the target 
				// and at the same level
				int offset = 0;
				if ( isNodeSibling( sourceNode ) ) {
					//Tools.log ("The target is a sibling of the sourceNode");
					if ( parentNode.getIndex( sourceNode ) < parentNode.getIndex( this ))
						offset = -1;
				}
			
				if ( actionType == DnDConstants.ACTION_MOVE ) {
					Tools.log ("Moving Picture onto Picture --> move to current spot");
					sourceNode.removeFromParent();  //SDTMN removeFromParents fire the model notification
					parentNode.insert( sourceNode, currentIndex  + offset );
				} else {
					// it was a copy event
					SortableDefaultMutableTreeNode newNode = new SortableDefaultMutableTreeNode( ( (PictureInfo) sourceNode.getUserObject() ).getClone() );
					parentNode.insert( newNode, currentIndex  + offset );
				}
				dropcomplete = true;	
				setUnsavedUpdates();
			} else 	{
				//Tools.log("SDMTN.executeDrop: we are dropping a GroupInfo object");
				// we are dropping a GroupInfo object; all others move down one step.
				// find out at which index to insert the group
				if ( ! this.isRoot() ) {
			
					GroupDropPopupMenu groupDropPopupMenu = new GroupDropPopupMenu( event, sourceNode, this);
					groupDropPopupMenu.show( event.getDropTargetContext().getDropTarget().getComponent(), event.getLocation().x, event.getLocation().y );
				} else {
					// Group was dropped on the root node --> add at first place.
					sourceNode.removeFromParent();
					this.insert( sourceNode, 0 );
					dropcomplete = true;	
					setUnsavedUpdates();
				}
			}
		}
		event.dropComplete( dropcomplete );	

			
	}


	/**
	 *   This innter class creates a popup menu for group drop events to find out whether to drop
	 *   into before or after the drop node.
	 */
	private class GroupDropPopupMenu extends JPopupMenu {
	
		/** 
		 *  menu item that allows the user to edit the group description
		 **/
		private JMenuItem dropBefore = new JMenuItem( Settings.jpoResources.getString("GDPMdropBefore") ); 

		/** 
		 *  menu item that allows the user to edit the group description
		 **/
		private JMenuItem dropAfter = new JMenuItem( Settings.jpoResources.getString("GDPMdropAfter") );
		
		/** 
		 *  menu item that allows the user to edit the group description
		 **/
		private JMenuItem dropIntoFirst = new JMenuItem( Settings.jpoResources.getString("GDPMdropIntoFirst") );; 

		/** 
		 *  menu item that allows the user to edit the group description
		 **/
		private JMenuItem dropIntoLast = new JMenuItem( Settings.jpoResources.getString("GDPMdropIntoLast") );; 
 
		/** 
		 *  menu item that allows the user to edit the group description
		 **/
		private JMenuItem dropCancel = new JMenuItem( Settings.jpoResources.getString("GDPMdropCancel") );; 



		/**
		 *   This innter class creates a popup menu for group drop events to find out whether to drop
		 *   into before or after the drop node.
		 */
		public GroupDropPopupMenu( final DropTargetDropEvent event, final SortableDefaultMutableTreeNode sourceNode, final SortableDefaultMutableTreeNode targetNode ) {
			dropBefore.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) targetNode.getParent();
					int currentIndex = parentNode.getIndex( targetNode );
	
					// position is one less if the source is further up the list than the target 
					// and at the same level
					int offset = 0;
					if ( targetNode.isNodeSibling( sourceNode ) ) {
						//Tools.log ("The target is a sibling of the sourceNode");
						if ( parentNode.getIndex( sourceNode ) < parentNode.getIndex( targetNode ))
							offset = -1;
					}
					sourceNode.removeFromParent();
					parentNode.insert( sourceNode, currentIndex  + offset );

					event.dropComplete( true );	
					setUnsavedUpdates();
				}
			} );
			add( dropBefore );
			
			dropAfter.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) targetNode.getParent();
					int currentIndex = parentNode.getIndex( targetNode );
	
					// position is one less if the source is further up the list than the target 
					// and at the same level
					int offset = 0;
					if ( targetNode.isNodeSibling( sourceNode ) ) {
						//Tools.log ("The target is a sibling of the sourceNode");
						if ( parentNode.getIndex( sourceNode ) < parentNode.getIndex( targetNode ))
							offset = -1;
					}
					sourceNode.removeFromParent();
					parentNode.insert( sourceNode, currentIndex  + offset + 1 );

					event.dropComplete( true );	
					setUnsavedUpdates();
				}
			} );
			add( dropAfter );
			
			dropIntoFirst.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sourceNode.removeFromParent();
					targetNode.insert( sourceNode, 0 );
					
					event.dropComplete( true );	
					setUnsavedUpdates();
				}
			} );
			add( dropIntoFirst );

			dropIntoLast.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int childCount = targetNode.getChildCount();
					int offset = 0;
					if ( childCount > 0 ) {
						// position is one less if the source is further up the list than the target 
						// and at the same level
						if ( targetNode.isNodeSibling( sourceNode.getFirstChild() ) ) {
							offset = -1;
						}
					}

					sourceNode.removeFromParent();
					targetNode.insert( sourceNode, childCount + offset );

					event.dropComplete( true );	
					setUnsavedUpdates();
				}
			} );
			add( dropIntoLast );

			
			dropCancel.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Tools.log("cancel drop");
					event.dropComplete( false );	
				}
			} );
			add( dropCancel );
		}

	}






	/**
	 *  This method brings up a Filechooser and then loads the images off the specified flat file.
	 */
	public void addFlatFile() {
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "fileOpenButtonText" ) );
		jFileChooser.setDialogTitle( Settings.jpoResources.getString( "addFlatFileTitle" ) );
		jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );
		
		int returnVal = jFileChooser.showOpenDialog( Settings.anchorFrame );
		if( returnVal == JFileChooser.APPROVE_OPTION ) {
			File chosenFile = jFileChooser.getSelectedFile();
			addFlatFile( chosenFile );
		}
	}
			
			
	/** 
	 *  This method adds the speicifed flat file of images at the current node.
	 */		
	public void addFlatFile( File chosenFile ) {
		SortableDefaultMutableTreeNode newNode = 
			new SortableDefaultMutableTreeNode(
				new GroupInfo( chosenFile.getName() ) );
		this.add( newNode );
		try {
			BufferedReader in = new BufferedReader( new FileReader( chosenFile ) );
			String sb = new String();
			while ( in.ready () ) {
				sb = in.readLine();
				File testFile = null;
				try {
					testFile = new File( new URI(sb) );
				} catch ( URISyntaxException x ) {
					Tools.log ( "Conversion of " + sb+ " to URI failed: " + x.getMessage() );
				} catch ( IllegalArgumentException x ) {
					Tools.log ( "Conversion of " + sb+ " to URI failed: " + x.getMessage() );
				}
					
				// dangerous but it doesn't continue if the first condition is true

				if ( (testFile != null) && (testFile.canRead()) ) {
					//Tools.log ( "Adding picture: " + sb );
					SortableDefaultMutableTreeNode newPictureNode = new SortableDefaultMutableTreeNode( 
						new PictureInfo(
							sb , 
							Tools.lowresFilename() ,
							Tools.stripOutFilenameRoot( testFile ) ,
							""
						)
					);
					newNode.add ( newPictureNode );
				} else {
					Tools.log ( "Not adding picture: " + sb + " because it can't be read" );
				}
			}
			in.close();
			getPictureCollection().getTreeModel().nodeStructureChanged( this );
			getPictureCollection().setUnsavedUpdates( false );
		} catch (IOException e) {
			Tools.log( "IOException " + e.getMessage() );
			JOptionPane.showMessageDialog(Settings.anchorFrame, 
				"Could not read " + chosenFile.getPath(), 
				Settings.jpoResources.getString("genericError"), 					JOptionPane.ERROR_MESSAGE);
		}		
	}






	/**
	 *  This method removes the designated SortableDefaultMutableTreeNode from the tree.
	 *  The parent node is made the currently selected node.
	 *
	 *  @return	true if successful, false if not
	 *
 	 */
	public boolean deleteNode() {
		Tools.log( "SDMTN.deleteNode: invoked on:" + this.toString() );
		if ( this.isRoot() ) {
			Tools.log( "SDMTN.deleteNode: attempted on Root node. Can't do this! Aborted." );
			JOptionPane.showMessageDialog( null, //very annoying if the main window is used as it forces itself into focus.
				Settings.jpoResources.getString("deleteRootNodeError"), 
				Settings.jpoResources.getString("genericError"), 
				JOptionPane.ERROR_MESSAGE);
			return false;
		}
		SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
		parentNode.setUnsavedUpdates();
		
		int[] childIndices = { parentNode.getIndex( this ) };
		Object[] removedChildren = { this };

		//this.removeFromSelection();

		super.removeFromParent();

		if ( getPictureCollection().getSendModelUpdates() ) {
			getPictureCollection().getTreeModel().nodesWereRemoved( parentNode, childIndices, removedChildren );
		}
		
		/**  remove the move targets here **/
		Enumeration e = this.breadthFirstEnumeration();
		SortableDefaultMutableTreeNode testNode;
		while ( e.hasMoreElements() ) {
			Settings.removeRecentDropNode( (SortableDefaultMutableTreeNode) e.nextElement() );
		}
		
		return true;
	}



	/**
	 *   Overriden method which will do the default befaviour and then sends a notification to 
	 *   the Tree Model.
	 */
	public void removeFromParent() {
		Tools.log( "SDMTN.removeFromParent was called for node: " + this.toString() );
		SortableDefaultMutableTreeNode oldParentNode = (SortableDefaultMutableTreeNode) this.getParent();
		int oldParentIndex = oldParentNode.getIndex( this );
		Tools.log("SDMTN.removeFromParent: Currentnode: " + this.toString() + " Parent Node:" + oldParentNode.toString() );
		super.removeFromParent();
		if ( getPictureCollection().getSendModelUpdates() ) {
			getPictureCollection().getTreeModel().nodesWereRemoved( oldParentNode, 
						new int[]  { oldParentIndex },
						new Object[] { this } );
		}
	}



	/**
	 *   This method adds a new node to the data model of the tree. It is the overriden add 
	 *   method which will first do the default befaviour and then send a notification to 
	 *   the Tree Model if model updates are being requested. Likewise the unsaved changes
	 *   of the collection are only being updated when model updates are not being reported.
	 *   This allows the loading of collections (which of course massively change the collection
	 *   in memory) to report nothing changed.
	 */
	public void add ( SortableDefaultMutableTreeNode newNode ) {
		//Tools.log( "SDMTN.add was called for node: " + newNode.toString() );
		super.add( newNode );
		if ( getPictureCollection().getSendModelUpdates() ) {
			int index = this.getIndex( newNode );
			getPictureCollection().getTreeModel().nodesWereInserted( this, new int[] { index } );
			newNode.setUnsavedUpdates();
		}
	}


	/**
	 *   Overriden method which will do the default befaviour and then sends a notification to 
	 *   the Tree Model.
	 */
	public void insert ( SortableDefaultMutableTreeNode node, int index ) {
		Tools.log( "SDMTN.insert was called for node: " + node.toString() );
		super.insert( node, index );
		if ( getPictureCollection().getSendModelUpdates() ) {
			getPictureCollection().getTreeModel().nodesWereInserted( this, new int[]  { index } );
		}
	}



	/**
	 *   brings up a file chooser 
	 *   and allows the user to specify where the image should to be copied to and then
	 *   copies it.
	 *
	 *   
	 */
	public void copyToNewLocation () {
		URL originalUrl;
		if ( ! ( this.getUserObject() instanceof PictureInfo ) ) {
			Tools.log( "SDMTN.copyToNewLocation: inkoked on a non PictureInfo type node! Aborted." );
			return;
		}
		try {
			originalUrl = ((PictureInfo) this.getUserObject()).getHighresURL();
		} catch ( MalformedURLException x ) {
			Tools.log("MarformedURLException trapped on: " + ((PictureInfo) this.getUserObject()).getHighresLocation() + "\nReason: " + x.getMessage());
			JOptionPane.showMessageDialog(
				Settings.anchorFrame, 
				"MarformedURLException trapped on:\n" + ((PictureInfo) this.getUserObject()).getHighresLocation() + "\nReason: " + x.getMessage(), 
				Settings.jpoResources.getString("genericError"), 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		JFileChooser jFileChooser = new JFileChooser();
    
		jFileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "CopyImageDialogButton" ) );
		jFileChooser.setDialogTitle( Settings.jpoResources.getString( "CopyImageDialogTitle" ) + originalUrl );
		jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );
		
		int returnVal = jFileChooser.showSaveDialog( Settings.anchorFrame );
		if( returnVal == JFileChooser.APPROVE_OPTION ) {
			this.validateAndCopyPicture ( jFileChooser.getSelectedFile() );
		}
	}




	/**
	 *   Validates the target of the picture copy instruction and tries to find the
	 *   appropriate thing to do. It does the following steps:<br>
	 *   1. If any input is null the copy is aborted with an error dialog.<br>
	 *   2: If the target is a directory the filename of the original is used.<br>
	 *   3: If the target is an existing file the copy is aborted<br>
	 *   4: If the target directory doesn't exist then the directories are created.<br>
	 *   5: The file extension is made to be that of the original if it isn't already that.<br>
	 *   When all preconditions are met the image is copied
	 *
	 *   @param targetFile  The target location for the new Picture.
	 */
	public void validateAndCopyPicture ( File targetFile ) {
		if ( ( this == null ) || ( targetFile == null ) ) {
			JOptionPane.showMessageDialog( Settings.anchorFrame, 
				Settings.jpoResources.getString("CopyImageNullError"), 
				Settings.jpoResources.getString("genericError"), 
				JOptionPane.ERROR_MESSAGE);
			return;
		}

		if ( ! ( this.getUserObject() instanceof PictureInfo ) ) {
			Tools.log( "SDMTN.copyToNewLocation: inkoked on a non PictureInfo type node! Aborted." );
			return;
		}


		URL originalUrl;
		try {		
			originalUrl = ((PictureInfo) this.getUserObject()).getHighresURL();
		} catch ( MalformedURLException x ) {
			Tools.log("MarformedURLException trapped on: " + ((PictureInfo) this.getUserObject()).getHighresLocation() + "\nReason: " + x.getMessage());
			JOptionPane.showMessageDialog(
				Settings.anchorFrame, 
				"MarformedURLException trapped on: " + ((PictureInfo) this.getUserObject()).getHighresLocation() + "\nReason: " + x.getMessage(), 
				Settings.jpoResources.getString("genericError"), 
				JOptionPane.ERROR_MESSAGE);
			return;
		}

		if ( targetFile.exists() ) {
			if ( ! targetFile.isDirectory() ) {
				try {
					String sourceFilename = new File( new URI( originalUrl.toString() ) ).getName();
					targetFile = Tools.inventPicFilename( targetFile.getParentFile(), sourceFilename);
					//Tools.log("JTree:validateAndCopyPicture: originalUrl: " + originalUrl.toString() + "\nsourceFilename: " + sourceFilename + "  targetFile: " + targetFile.toString());
				} catch ( URISyntaxException x) {
					JOptionPane.showMessageDialog( Settings.anchorFrame , 
						"URISyntaxException: " + x, 
						Settings.jpoResources.getString("genericError"), 
						JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		} else {
			// it doesn't exist
			if ( ! targetFile.mkdirs() ) {
				JOptionPane.showMessageDialog( Settings.anchorFrame, 
					Settings.jpoResources.getString("CopyImageDirError")
						+ targetFile.toString() ,
					Settings.jpoResources.getString("genericError"), 
					JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
			


		if ( targetFile.isDirectory() ) {
			try {
				String sourceFilename = new File( new URI( originalUrl.toString() ) ).getName();
				targetFile = Tools.inventPicFilename( targetFile, sourceFilename);
			} catch ( URISyntaxException x) {
				JOptionPane.showMessageDialog( Settings.anchorFrame , 
					"URISyntaxException: " + x, 
					Settings.jpoResources.getString("genericError"), 
					JOptionPane.ERROR_MESSAGE);
				return;
			}
		}





		targetFile = Tools.correctFilenameExtension ( Tools.getExtension( originalUrl ), targetFile );	
		
		if ( ! targetFile.getParentFile().exists() )
			targetFile.getParentFile().mkdirs();
			
		Tools.copyPicture( originalUrl, targetFile );
		Settings.memorizeCopyLocation( targetFile.getParent() );
	}




	/**
	 *  Moves a node to the top of it's branch.
	 */
	public void moveNodeToTop () {
		if ( this.isRoot() )
			return;  // don't do anything with a root node.
		
		
		SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
		// abort if this action was attempted on the top node
		if ( parentNode.getIndex( this ) < 1 )
			return;
		this.removeFromParent();
		parentNode.insert( this, 0 );
		setUnsavedUpdates();
	}
	


	/**
	 *  Moves a node up one position in the current branch.
	 */
	public void moveNodeUp () {
		if ( this.isRoot() )
			return;  // don't do anything with a root node.
		
		SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
		int currentIndex = parentNode.getIndex( this );
		// abort if this action was attempted on the top node or not a child
		if ( currentIndex < 1 )
			return;
		this.removeFromParent();
		parentNode.insert( this, currentIndex - 1 );
		setUnsavedUpdates();
	}


	/**
	 *  Method that moves a node down one position
	 */
	public void moveNodeDown() {
		if ( this.isRoot() )
			return;  // don't do anything with a root node.
		
		SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
		int childCount = parentNode.getChildCount();
		int currentIndex = parentNode.getIndex( this );
		// abort if this action was attempted on the bootom node
		if ( (currentIndex == -1) || 
		     (currentIndex == childCount - 1) )
			return;
		this.removeFromParent();
		parentNode.insert( this, currentIndex + 1 );
		setUnsavedUpdates();
	}


	/**
	 *  Method that moves a node to the bottom of the current branch
	 */
	public void moveNodeToBottom () {
		if ( this.isRoot() )
			return;  // don't do anything with a root node.


		SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
		int childCount = parentNode.getChildCount();
		// abort if this action was attempted on the bootom node
		if ( (parentNode.getIndex( this ) == -1) || 
		     (parentNode.getIndex( this ) == childCount - 1) )
			return;
		this.removeFromParent();
		parentNode.insert( this, childCount - 1 );
		setUnsavedUpdates();
	}


	/**
	 *  Method that indents a node.
	 */
	public void indentNode () {
		if ( this.isRoot() )
			return;  // don't do anything with a root node.

		SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
		SortableDefaultMutableTreeNode childBefore = this;
		do {
			childBefore = (SortableDefaultMutableTreeNode) parentNode.getChildBefore( childBefore );
		} while ( ( childBefore != null ) 
		       && ( ! ( childBefore.getUserObject() instanceof GroupInfo ) ) );

		if ( childBefore == null ) {
			SortableDefaultMutableTreeNode newGroup = 
				new SortableDefaultMutableTreeNode(
					new GroupInfo( Settings.jpoResources.getString("newGroup") ) );
			parentNode.insert( newGroup, 0 );
			this.removeFromParent();
			newGroup.add( this );
		} else {
			this.removeFromParent();
			childBefore.add( this );
		}
		setUnsavedUpdates();
	}

	/**
	 *  Method that outdents a node. This means the node will be placed just after it's parent's node
	 *  as a child of it's grandparent.
	 */
	public void outdentNode () {
		if ( this.isRoot() )
			return;  // don't do anything with a root node.

		SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
		if ( parentNode.isRoot() )
			return;  // don't do anything with a root parent node.
			
		SortableDefaultMutableTreeNode grandParentNode = (SortableDefaultMutableTreeNode) parentNode.getParent();
		int index = grandParentNode.getIndex( parentNode );
			
		this.removeFromParent();
		grandParentNode.insert( this, index + 1 );
		setUnsavedUpdates();
	}




	/**
	 *  Method that moves a node to bottom of the specified group node
	 */
	public void moveToNode( SortableDefaultMutableTreeNode groupNode ) {
		if ( this.isRoot() )
			return;  // don't do anything with a root node.

		this.removeFromParent();
		groupNode.add( this );
		
		setUnsavedUpdates();
	}








	/**
	 *    Opens up a PictureViewer and positions it at the indicated node.
	 */
	public void showLargePicture() {
		PictureViewer pictureViewer = new PictureViewer();
		pictureViewer.changePicture( this );
	}



	/**
	 *   Renames the file of the indicated node.
	 */
	public void fileRename() {
		Object userObject = this.getUserObject();
		if ( ! ( userObject instanceof PictureInfo ) )
			return;
			
		PictureInfo pi = (PictureInfo) userObject;			
		File highresFile = pi.getHighresFile();
		if ( highresFile == null ) 
			return;
		
		Object object =  Settings.jpoResources.getString("FileRenameLabel1")
			+ highresFile.toString()
			+ Settings.jpoResources.getString("FileRenameLabel2") ;
/*		String selectedValue = JOptionPane.showInputDialog (
	 			Settings.anchorFrame, 					// parent component
				object, 						// message
				Settings.jpoResources.getString("fileRenameTitle"),	// title
       				JOptionPane.QUESTION_MESSAGE,				// message type
				new Icon(),							// icon
				null,							// selectionValues
				highresFile.toString() );				// initialSelectionValue
				*/
		String selectedValue = JOptionPane.showInputDialog (
	 			Settings.anchorFrame, 					// parent component
				object, 						// message
				highresFile.toString() );				// initialSelectionValue
		if ( selectedValue != null ) {
			File newName = new File( selectedValue );
			if ( highresFile.renameTo( newName ) ) {
				Tools.log("Sucessufully renamed: " + highresFile.toString() + " to: " + selectedValue);
				try {
					pi.setHighresLocation( newName.toURI().toURL() );
				} catch ( MalformedURLException x ) {
					Tools.log("Caught a MalformedURLException because of: " + x.getMessage() );
				}
			} else {
				Tools.log("Rename failed from : " + highresFile.toString() + " to: " + selectedValue);
			}
		}
	}


	/**
	 *  Brings up an are you sure dialog and then deletes the file.
	 */
	public void fileDelete() {
		Object userObject = this.getUserObject();
		if ( ! ( userObject instanceof PictureInfo ) )
			return;
			
		PictureInfo pi = (PictureInfo) userObject;			
		File highresFile = pi.getHighresFile();
		if ( highresFile == null ) 
			return;

		int option = JOptionPane.showConfirmDialog(
			null, //very annoying if the main window is used as it forces itself into focus.
			Settings.jpoResources.getString("FileDeleteLabel") 
				+ highresFile.toString()
				+ "\n"
				+ Settings.jpoResources.getString("areYouSure"), 
			Settings.jpoResources.getString("FileDeleteTitle"), 
			JOptionPane.OK_CANCEL_OPTION);
			
		if ( option == 0 ) {
			boolean ok = false;
			File lowresFile = pi.getLowresFile();
			if ( ( lowresFile != null )
			  && ( lowresFile.exists() ) ) {
			  	ok = lowresFile.delete();
				if ( ! ok )
					//Tools.log("File deleted: " + lowresFile.toString() );
				// else
					Tools.log("File deleted failed on: " + lowresFile.toString() );
			}
			

			if ( highresFile.exists() ) {
			  	ok = highresFile.delete();
				if ( ! ok )
					//Tools.log("File deleted: " + highresFile.toString() );
				//else
					Tools.log("File deleted failed on: " + highresFile.toString() );
			}

			deleteNode();
			
			if ( ! ok ) {
				JOptionPane.showMessageDialog( Settings.anchorFrame, 
					Settings.jpoResources.getString("fileDeleteError") + highresFile.toString(), 
					Settings.jpoResources.getString("genericError"), 
					JOptionPane.ERROR_MESSAGE);
			}
		}
	}



	/**
	 *  method that searches for pictures and stores them at the end of the current node 
	 *  @param q	 The Query object which specifies the search criteria
	 *  @return  The new node in the tree or null if nothing was found
	 */
	/*public SortableDefaultMutableTreeNode findAndSave( Query q ) {
		SortableDefaultMutableTreeNode resultsGroupNode = 
			new SortableDefaultMutableTreeNode ( new GroupInfo ( q.getTitle() ) );
		SortableDefaultMutableTreeNode TestNode; 
		Object nodeInfo; 

		ArrayList searchResults =  q.getSearchResults();
		if ( ! searchResults.isEmpty() ) {
			setSendModelUpdates( false );
			this.add( resultsGroupNode );
			for ( int i = 0 ;  i < searchResults.size(); i++) { 
				resultsGroupNode.add( (SortableDefaultMutableTreeNode) searchResults.get( i ) );
			}
			setSendModelUpdates( true );
			getTreeModel().nodeStructureChanged( this );
			return resultsGroupNode;
		} else {
			return null;
		}
	}*/




	/**
	 *  This function brings up a picture editor gui.
	 */
	public void showEditGUI() {
		if ( this.getUserObject() instanceof PictureInfo ) {
			new PictureInfoEditor( this );
		} else if ( this.getUserObject() instanceof GroupInfo ) {
			new GroupInfoEditor( this );
		} else {
			Tools.log( "SortableDefaultMutableTreeNode.showEditGUI: doesn't know what kind of editor to use. Irngoring request.");
		}
	}


	/**
	 *  This function opens the CateGoryUsageEditor.
	 */
	public void showCategoryUsageGUI() {
		Tools.log("SDMTN.showCategoryUsageGUI invoked");
		if ( this.getUserObject() instanceof PictureInfo ) {
			CategoryUsageJFrame cujf = new CategoryUsageJFrame();
			Vector nodes = new Vector();
			nodes.add( this );
			cujf.setSelection( nodes );
		} else  if ( this.getUserObject() instanceof GroupInfo ) {
			CategoryUsageJFrame cujf = new CategoryUsageJFrame();
			cujf.setGroupSelection( this, false );
		} else{
			Tools.log( "SortableDefaultMutableTreeNode.showCategoryUsageGUI: doesn't know what kind of editor to use. Irngoring request.");
		}
	}



	/**
	 *  Adds a new Group with the indicated description.
	 *  @return  The new node is returned for convenience.
	 */
	public SortableDefaultMutableTreeNode addGroupNode( String description ) {		
		SortableDefaultMutableTreeNode newNode = 
			new SortableDefaultMutableTreeNode(
				new GroupInfo( description ));
		add( newNode );
		return newNode;
	}
	



	/**
	 *  Adds the indicated files to the current node if they are valid pictures. If the newOnly
	 *  Flag is on then the collection is checked to see if the picture is already present. It 
	 *  also opens a progress Gui to provide feedback to the user.
	 * 
	 *  @param newOnly indicates whether to check if the picture is already in the collection
	 *  @param recurseDirectories  indicates whether to scan down into directories for more pictures. 
	 *  @param retainDirectories  indicates whether to preserve the directory structure.
	 *  @return In case this is of interest to the caller we return here the node to be displayed.
	 */
	public SortableDefaultMutableTreeNode addPictures( File[] chosenFiles, boolean newOnly, boolean recurseDirectories, boolean retainDirectories, HashSet selectedCategories ) {
		ProgressGui progGui = new ProgressGui( Tools.countfiles( chosenFiles ),
			Settings.jpoResources.getString("PictureAdderProgressDialogTitle"),
			Settings.jpoResources.getString("picturesAdded") );
		getPictureCollection().setSendModelUpdates( false );
		
		SortableDefaultMutableTreeNode displayNode = null;
		SortableDefaultMutableTreeNode addedNode = null;
	
		// add all the files from the array as nodes to the start node.
		for ( int i = 0; (i < chosenFiles.length) && ( ! progGui.interrupt ); i++ ) {
			File addFile = chosenFiles[i];
			if ( ! addFile.isDirectory() ) {
				if ( addSinglePicture( addFile, newOnly, selectedCategories ) ) {
					progGui.progressIncrement();
				} else {
					progGui.decrementTotal();
				}
			} else {
				if ( Tools.hasPictures( addFile ) ) {
					addedNode = addDirectory( addFile, newOnly, recurseDirectories, retainDirectories, progGui, selectedCategories );
					if ( displayNode == null ) {
						displayNode = addedNode;
					}
				} else {
					Tools.log ("PictureAdder.run: no pictures in directory " + addFile.toString() );
				}
			}
		}
		getPictureCollection().setSendModelUpdates( true );
		
		// Force an update in the tree
		getPictureCollection().getTreeModel().nodeStructureChanged( this );
		
		progGui.switchToDoneMode();
		if ( displayNode == null ) {
			displayNode = this;
		}
		return displayNode;
	}




	/**
	 *  Copies the pictures from the source tree to the target directory and adds them to the collection.
	 *  @param  newOnly  If true only pictures not yet in the collection will be added.
	 *  @param  retainDirectories  indicates that the directory structure should be preserved.
	 *  @param  selectedCategories  the categories to be applied to the newly loaded pictures.
	 * 
	 */
	public SortableDefaultMutableTreeNode copyAddPictures( File sourceDir, File targetDir, String groupName, boolean newOnly, boolean retainDirectories, HashSet selectedCategories ) {
		File[] files = sourceDir.listFiles();
		ProgressGui progGui = new ProgressGui( Tools.countfiles( files ),
			Settings.jpoResources.getString("PictureAdderProgressDialogTitle"),
			Settings.jpoResources.getString("picturesAdded") );
//		SortableDefaultMutableTreeNode newGroup = addGroupNode( groupName );
	
		SortableDefaultMutableTreeNode newGroup = 
			new SortableDefaultMutableTreeNode(
				new GroupInfo( groupName ));

		getPictureCollection().setSendModelUpdates( false );


		boolean picturesAdded = copyAddPictures1( files, targetDir, newGroup, progGui, newOnly, retainDirectories, selectedCategories );
		progGui.switchToDoneMode();
		getPictureCollection().setSendModelUpdates( true );

		if ( picturesAdded) {
			add ( newGroup );
			// Force an update in the tree
			// getTreeModel().nodeStructureChanged( this );
		} else {
			newGroup = null;
		}
		return newGroup;
	}


	/**
	 *  Copies the pictures from the source tree to the target directory and adds them to the collection. 
	 *  This method does the actual loop.
	 *  @return  true if pictures were added, false if not.
	 * 
	 */
	protected static boolean copyAddPictures1( File[] files, 
		File targetDir, 
		SortableDefaultMutableTreeNode receivingNode, 
		ProgressGui progGui, 
		boolean newOnly,
		boolean retainDirectories,
		HashSet selectedCategories ) {
		
		boolean picturesAdded = false;
		// add all the files from the array as nodes to the start node.
		for ( int i = 0; (i < files.length) && ( ! progGui.interrupt ); i++ ) {
			File addFile = files[i];
			if ( ! addFile.isDirectory() ) {
				File targetFile = Tools.inventPicFilename( targetDir, addFile.getName() );
				long crc = Tools.copyPicture( addFile, targetFile );
				if ( newOnly && Settings.pictureCollection.isInCollection( crc ) ) {
					targetFile.delete();
					progGui.decrementTotal();
				} else {
					receivingNode.addPicture( targetFile, selectedCategories );
					progGui.progressIncrement();
					picturesAdded = true;
				}
			} else {
				if ( Tools.hasPictures( addFile ) ) {
					SortableDefaultMutableTreeNode subNode;
					if ( retainDirectories ) {
						subNode = receivingNode.addGroupNode( addFile.getName() );
					} else {
						subNode = receivingNode;
					}
					boolean a = copyAddPictures1( addFile.listFiles(), targetDir, subNode, progGui, newOnly, retainDirectories, selectedCategories );
					picturesAdded = a || picturesAdded;
				} else {
					Tools.log ("SDMTN.copyAddPictures: no pictures in directory " + addFile.toString() );
				}
			}
		}
		return picturesAdded;
	}


	/**
	 *  Copies the pictures from the source tree to the target directory and adds them to the 
	 *  collection only if they have not been seen by the camera before.
	 *
	 *  @param  cam  The camera object with knows the checksums of the pictures seen before.
	 * 
	 */
	public SortableDefaultMutableTreeNode copyAddPictures( File sourceDir, File targetDir, String groupName, Camera cam, boolean retainDirectories, HashSet selectedCategories ) {
		File[] files = sourceDir.listFiles();
		ProgressGui progGui = new ProgressGui( Tools.countfiles( files ),
			Settings.jpoResources.getString("PictureAdderProgressDialogTitle"),
			Settings.jpoResources.getString("picturesAdded") );
		SortableDefaultMutableTreeNode newGroup = 
			new SortableDefaultMutableTreeNode(
				new GroupInfo( groupName ));

		getPictureCollection().setSendModelUpdates( false );

		cam.zapNewImage();
		boolean picturesAdded = copyAddPictures1( files, targetDir, newGroup, progGui, cam, retainDirectories, selectedCategories );

		cam.storeNewImage();
		Settings.writeCameraSettings();
		
		getPictureCollection().setSendModelUpdates( true );
		progGui.switchToDoneMode();
		
		if ( picturesAdded ) {
			add( newGroup );
		} else {
			newGroup = null;
		}
		return newGroup;
	}


	/**
	 *   Creates a JFileChooser GUI and allows the user to select an XML file
	 *   which is then loaded current node of the collection
	 */
	public void fileLoad() {
		File fileToLoad = Tools.chooseXmlFile();
		fileLoad( fileToLoad );
	}



	/**
	 *   Loads the collection indicated by the File at the 
	 *   specified node.
	 *
	 *   @param  fileToLoad		The File object that is to be loaded.
	 */
	public void fileLoad( File fileToLoad ) {
		if ( fileToLoad != null ) {
			try {
				InputStream is = new FileInputStream( fileToLoad );
				if ( this.isRoot() ) {
					getPictureCollection().clearCollection();
					getPictureCollection().setXmlFile( fileToLoad );
				}
				streamLoad( is );
				Settings.pushRecentCollection( fileToLoad.toString() );
			} catch ( FileNotFoundException x ) {
				JOptionPane.showMessageDialog( Settings.anchorFrame, 
					"File not found:\n" + fileToLoad.getPath(), 
					Settings.jpoResources.getString("genericError"), 
					JOptionPane.ERROR_MESSAGE);
			}
		}
	}


	/**
	 *   Loads the collection indicated by the File at the 
	 *   specified node.
	 *
	 *   @param  is	The inputstream that is to be loaded.
	 */
	public void streamLoad( InputStream is ) {
		getPictureCollection().setSendModelUpdates( false ); // turn off model notification of each add for performance
		new XmlReader( is, this );
		getPictureCollection().getTreeModel().nodeStructureChanged( this );
		getPictureCollection().setSendModelUpdates( true );
	}


	/**
	 *  Copies the pictures from the source tree to the target directory and adds them to the collection. 
	 *  This method does the actual loop.
	 * 
	 */
	protected static boolean copyAddPictures1( File[] files, 
		File targetDir, 
		SortableDefaultMutableTreeNode receivingNode, 
		ProgressGui progGui, 
		Camera cam,
		boolean retainDirectories,
		HashSet selectedCategories ) {
		
		
		boolean picturesAdded = false;
		// add all the files from the array as nodes to the start node.
		for ( int i = 0; (i < files.length) && ( ! progGui.interrupt ); i++ ) {
			File addFile = files[i];
			if ( ! addFile.isDirectory() ) {
				if ( cam.useFilename && cam.inOldImage ( addFile ) ) {
					// ignore image if the filename is known
					cam.copyToNewImage( addFile ); // put it in the known pictures Hash
					progGui.decrementTotal();
				} else {
					File targetFile = Tools.inventPicFilename( targetDir, addFile.getName() );
					long crc = Tools.copyPicture( addFile, targetFile );
					cam.storePictureNewImage( addFile, crc ); // remember it next time
					if ( cam.inOldImage( crc ) ) {
						targetFile.delete();
						progGui.decrementTotal();
					} else {
						receivingNode.addPicture( targetFile, selectedCategories );
						progGui.progressIncrement();
						picturesAdded = true;
					}
				}
			} else {
				if ( Tools.hasPictures( addFile ) ) {
					SortableDefaultMutableTreeNode subNode;
					if ( retainDirectories ) {
						subNode = receivingNode.addGroupNode( addFile.getName() );
					} else {
						subNode = receivingNode;
					}
					
					boolean a = copyAddPictures1( addFile.listFiles(), targetDir, subNode, progGui, cam, retainDirectories, selectedCategories );
					picturesAdded = a || picturesAdded;
				} else {
					Tools.log ("SDMTN.copyAddPictures: no pictures in directory " + addFile.toString() );
				}
			}
		}
		return picturesAdded;
	}




	/**
	 *  method that is invoked resursively on each directory encoundered. It adds
	 *  a new group to the tree and then adds all the pictures found therein to that
	 *  group. The ImageIO.getImageReaders method is queried to see whether a reader
	 *  exists for the image that is attempted to be loaded.
	 *  @param retainDirectories  indicates whether to preserve the directory structure
	 *  @return returns the node that was added.
	 */
	private SortableDefaultMutableTreeNode addDirectory( File dir, boolean newOnly, boolean recurseDirectories, boolean retainDirectories, ProgressGui progGui, HashSet selectedCategories ) {
		SortableDefaultMutableTreeNode newNode;
		if ( retainDirectories) {
			newNode = new SortableDefaultMutableTreeNode( new GroupInfo( dir.getName() ) );
			add( newNode );
			setUnsavedUpdates();
		} else {
			newNode = this;
		}

		File[] fileArray = dir.listFiles();
		for (int i = 0; (i < fileArray.length) && ( ! progGui.interrupt ); i++) {
			if ( fileArray[i].isDirectory() && recurseDirectories ) {
				if ( Tools.hasPictures( fileArray[i] ) ) {
					newNode.addDirectory( fileArray[i], newOnly, recurseDirectories, retainDirectories, progGui, selectedCategories );
				}
			} else {
				if ( newNode.addSinglePicture( fileArray[i], newOnly, selectedCategories ) ) {
					progGui.progressIncrement();
				} else {
					progGui.decrementTotal();
				}
			}
		}
		return newNode;
	}


	/**
	 *  this method adds a new Picture if it is not yet in the collection.
	 *
	 *  @param  addFile  the file that should be added
	 *  @param  newOnly whether to check if the picture is in the collection already
	 *  @return  true if the picture was valid, false if not.
	 */
	public boolean addSinglePicture ( File addFile, boolean newOnly, HashSet selectedCategories ) {
		Tools.log("SDMTN.addSinglePicture: invoked on: " + addFile.toString() + " for " + this.toString() );
		if ( newOnly && getPictureCollection().isInCollection( addFile ) ) {
		    	return false; // only add pics not in the collection already
		} else {
			return addPicture( addFile, selectedCategories );
		}
	}
				

	
	/**
	 *  this method adds a new Picture to the current node if the JVM has a reader for it.
	 *
	 *  @param  addFile  the file that should be added
	 *  @return  true if the picture was valid, false if not.
	 *
	public boolean addPicture ( File addFile, HashSet selectedCategories ) {
		return ( addPicture (addFile, selectedCategories) );
	}*/


	
	/**
	 *  this method adds a new Picture to the current node if the JVM has a reader for it.
	 *
	 *  @param  addFile  the file that should be added
	 *  @return  true if the picture was valid, false if not.
	 */
	public boolean addPicture ( File addFile, HashSet categoryAssignment ) {
		Tools.log("SDMTN.addPicture: invoked on: " + addFile.toString() + " for " + this.toString() );
		PictureInfo newPictureInfo = new PictureInfo();
		try {
			if ( ! Tools.jvmHasReader( addFile ) ) {
			    	return false; // don't add if there is no reader.
			}
			newPictureInfo.setHighresLocation( addFile.toURI().toURL() );
			newPictureInfo.setLowresLocation ( Tools.lowresFilename() );
			newPictureInfo.setDescription( Tools.stripOutFilenameRoot( addFile ) );
			newPictureInfo.setChecksum( Tools.calculateChecksum( addFile ) );
			if ( categoryAssignment != null ) {
				newPictureInfo.setCategoryAssignment( categoryAssignment );
			}
		} catch ( MalformedURLException x ) {
			Tools.log( "PictureAdder.addSinglePicture: MalformedURLException: " + addFile.getPath() + "\nError: " + x.getMessage());
			return false;
		}
		
		
		SortableDefaultMutableTreeNode newNode = new SortableDefaultMutableTreeNode( newPictureInfo );	
		this.add( newNode );
		// This is not elegant but for now forces the creation of the Thumbnail image
		// It is 
		// unfortunate that the queue will not recognize duplicates because it is working 
		//  off Thumbnail objects instead of Picturefiles. This also makes urgent requests come too late
		Thumbnail t = new Thumbnail( newNode, Settings.thumbnailSize, ThumbnailCreationQueue.LOW_PRIORITY );
		this.setUnsavedUpdates();
				
		try {
			// try to read EXIF data and get the date/time if possible
			// if this fails the is crashes into the catch statements and 
			// the date is not added

			InputStream highresStream = newPictureInfo.getHighresURL().openStream();
			JpegSegmentReader reader = new JpegSegmentReader( new BufferedInputStream( highresStream ) );
			byte[] exifSegment = reader.readSegment( JpegSegmentReader.SEGMENT_APP1 );
			byte[] iptcSegment = reader.readSegment( JpegSegmentReader.SEGMENT_APPD );
			highresStream.close();

			Metadata metadata = new Metadata();
			new ExifReader( exifSegment ).extract( metadata );
			new IptcReader( iptcSegment ).extract( metadata );
				
			Directory exifDirectory = metadata.getDirectory( ExifDirectory.class );
			String creationTime = exifDirectory.getString( ExifDirectory.TAG_DATETIME );
			if ( creationTime == null ) creationTime = "";
				newPictureInfo.setCreationTime( creationTime );
		} catch ( MalformedURLException x ) {
			Tools.log( "SDMTN.addSinglePicture: MalformedURLException: " + addFile.getPath() + "\nError: " + x.getMessage());
		} catch ( IOException x ) {
			Tools.log( "SDMTN.addSinglePicture: IOException: " + x.getMessage() );
		} catch ( JpegProcessingException x ) { 
			Tools.log( "SDMTN.addSinglePicture: No EXIF header found\n" + x.getMessage() );
		}
		
		return true;
	}



	/**
	 *  this method can be called to refresh the thumbnail of the node. It places a request 
	 *  on the Queue. When the ThumbnailCreationThread picks it up it will send a 
	 *  {@link PictureInfo#sendThumbnailChangedEvent()}
	 *  event.
	 */
	public void refreshThumbnail() {
		/*if ( ! ( this.getUserObject() instanceof PictureInfo ) ) {
			Tools.log("SDMTN.refresh Thumbnail called on a node that doesn't contain a picture! Ignoring request.");
		}*/
		Thumbnail t = new Thumbnail ( this, Settings.thumbnailSize, ThumbnailCreationQueue.HIGH_PRIORITY );
		ThumbnailCreationQueue.forceThumbnailCreation( t, ThumbnailCreationQueue.HIGH_PRIORITY );
	}
	

	/**
	 *  this method can be called to rotate the image of the node.
	 */
	public void rotatePicture( int angle ) {
		if ( ! ( this.getUserObject() instanceof PictureInfo ) ) {
			Tools.log("SDMTN.rotateImage called on a node that doesn't contain a picture! Ignoring request.");
			return;
		}
		PictureInfo pi = (PictureInfo) this.getUserObject();
		setPictureRotation( (int) ( pi.getRotation() + angle ) % 360 );
	}

	/**
	 *  this method can be called to rotate the image of the node.
	 */
	public void setPictureRotation( int angle ) {
		if ( ! ( this.getUserObject() instanceof PictureInfo ) ) {
			Tools.log("SDMTN.setPictureRotation called on a node that doesn't contain a picture! Ignoring request.");
			return;
		}
		PictureInfo pi = (PictureInfo) this.getUserObject();
		pi.setRotation( angle );
		setUnsavedUpdates();
		this.refreshThumbnail();
	}


	/**
	 *  This method returns whether the supplied node is a descendent of the deletions that 
	 *  have been detected in the TreeModelListener delivered TreeModelEvent.
	 *  @param  affectedNode  The node to check whether it is or is a descendent of the deleted node.
	 *  @param  e the TreenModelEvent that was detected
	 */
	public static boolean wasNodeDeleted( SortableDefaultMutableTreeNode affectedNode, TreeModelEvent e ) {
		Tools.log( "SDMTN.wasNodeDeleted invoked for: " + affectedNode.toString() + " / " + e.toString() );
		TreePath removedChild;
		TreePath currentNodeTreePath = new TreePath( affectedNode.getPath() );
		Object [] children = e.getChildren();
		for ( int i = 0; i<children.length; i++ ) {
			removedChild = new TreePath( children[ i ] );
			if ( removedChild.isDescendant( currentNodeTreePath ) ) {
				return true;
			}
		}
		return false;
	}



	
}
