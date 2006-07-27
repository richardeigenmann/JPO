package jpo; 

import java.util.*;
import javax.swing.event.*; 
 
/*
ArrayListBrower.java:  an implementation of the ThumbnailBrowserInterface for browsing pictures sequentially.

Copyright (C) 2006  Richard Eigenmann.
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
 *  This class implements the ThumbnailBrowserInterface so that all the pictures of a specified group
 *  are browsed sequentially.
 */

public class ArrayListBrowser implements ThumbnailBrowserInterface, TreeModelListener {

	/**
	 *   title for this ArrayList of images.
	 */
	private String title;


	/**
	 *  sets the title to be returned if any component tried to display the title of the list
	 */
	public void setTitle( String newTitle) {
		title = newTitle;
	}


	/**
	 *  returns the title
	 */
	public String getTitle() {
		if ( title != null ) {
			return title;
		} else {
			return "ArrayList";
		}
	}


	/**
	 *  Returns the number of pictures in this group. The number is 0 to the number of pictures minus 1 
	 *  because counting starts at 0. So 3 nodes in the group returns 2 meaning node0, node1, node2
	 */
	public int getNumberOfNodes() {
		Tools.log("ArrayListBrowser.getNumberOfNodes: returning: " + Integer.toString( allPictures.size() - 1) );
		return allPictures.size() - 1;
	}



	/**
	 *  This method returns the node for the indicated position in the group.
	 *
	 *  @param index   The component index that is to be returned. The number is from 0 to
	 *                 {@link #getNumberOfNodes}. If there are 3 nodes request getNode(0),
	 *                 getNode(1) and getNode(2).
	 */
 	public SortableDefaultMutableTreeNode getNode( int index ) {
		Tools.log("ArrayListBrowser.getNode: requested for node: " + Integer.toString( index ) );
		if ( index > getNumberOfNodes() ) {
			Tools.log("ArrayListBrowser.getNode: index requested beyond number of pictures. Returning null");
			return null;
		} else {
			return (SortableDefaultMutableTreeNode) allPictures.get( index );
		}
	}



	/**
	 *  This ArrayList holds a reference to each picture under the start group.
	 */
	protected ArrayList allPictures = new ArrayList();


	/**
	 *   sets the ArrayList
	 *
	 *  @param newArrayList   the new ArrayList with the nodes to display.
	 */
	public void setArrayList( ArrayList newArrayList ) {
		allPictures = newArrayList;
	}


	/**
	 *  adds a node to the ArrayList
	 *
	 *  @param  addNode   a node to add to the allPictures ArrayList
	 */
	public void addNode( SortableDefaultMutableTreeNode addNode ) {
		allPictures.add( addNode );
	}
	

	/**
	 *  This method unregisters the TreeModelListener and sets the variables to null;
	 */
	public void cleanup () {
		allPictures = null;
		relayoutListeners.clear();
	}

	



	/**
	 *  A vector that holds all the listeners that need to be notified if there is a structural change.
	 */
	private Vector relayoutListeners = new Vector();


	/**
	 *  method to register a ThumbnailJScrollPane as a listener
	 */
	public void addRelayoutListener ( ThumbnailJScrollPane listener ) {
		relayoutListeners.add( listener );
	}


	/**
	 *  method to remove a ThumbnailJScrollPane as a listener
	 */
	public void removeRelayoutListener ( ThumbnailJScrollPane listener ) {
		relayoutListeners.remove( listener );
	}


	/**
	 * Method that notifies the relayoutListeners of a structural change that they need to 
	 * respond to.
	 */
	private void notifyRelayoutListeners() {
		Vector nonmodifiedVector = (Vector) relayoutListeners.clone();

		Enumeration e = nonmodifiedVector.elements();
		while ( e.hasMoreElements() ) {
			((ThumbnailJScrollPane) e.nextElement()).layoutThumbnailsInThread();
		}
	}


	/** 
	 *   This method is defined by the TreeModelListener interface and gives the 
	 *   JThumnailScrollPane a notification that some nodes changed in a non dramatic way.
	 *   The nodes that were changed have their Constraints reevaluated and a revalidate
	 *   is called to update the screen.
	 */
	public void treeNodesChanged (TreeModelEvent e) {
		/*Tools.log("GroupBrowser.treeNodesChanged: " + e.toString() );
		if ( myNode == null ) {
			Tools.log("GroupBrowser.treeNodesChanged: ERROR! This should not have been called as there is not group showing and therefore there should be no tree listener firing off. Ignoring notification.");
			return;
		}

		// don't get excited and force a relayout unless the inserted node is part 
		// of the current group
		TreePath myPath = new TreePath( myNode.getPath() );
		if ( myPath.equals( e.getTreePath() ) ) {
			Tools.log("GroupBrowser.treeNodesChanged: The changed node's parent is the group being shown. We must therefore relayout the children; myNode: " + myPath.toString() + " comparison:" +  ((SortableDefaultMutableTreeNode) e.getTreePath().getLastPathComponent() ).toString() );
			notifyRelayoutListeners(); 
		}*/
	}
	
	/** 
	 *   This method is defined by the TreeModelListener interface and gives the 
	 *   JThumnailScrollPane a notification if additional nodes were inserted.
	 *   The additional nodes are added and the existing nodes are reevaluated
	 *   as to whether they are at the right place. Revalidate is called to update
	 *   the screen.
	 */
	public void treeNodesInserted (TreeModelEvent e) {
		/*Tools.log("GroupBrowser.treeNodesInserted: " + e.toString() );
		if ( myNode == null ) {
			Tools.log("GroupBrowser.treeNodesInserted: ERROR! This should not have been called as there is not group showing and therefore there should be no tree listener firing off. Ignoring notification.");
			return;
		}

		// don't get excited and force a relayout unless the inserted node is part 
		// of the current group
		TreePath myPath = new TreePath( myNode.getPath() );
		if ( myPath.equals( e.getTreePath() ) ) {
			Tools.log("GroupBrowser.treeNodesInserted: The inserted node's parent is the group being shown. We must therefore relayout the children; myNode: " + myPath.toString() + " comparison:" +  ((SortableDefaultMutableTreeNode) e.getTreePath().getLastPathComponent() ).toString() );
			notifyRelayoutListeners(); 
		}*/
	}

	/** 
	 *   This method is defined by the TreeModelListener interface and gives the 
	 *   JThumnailScrollPane a notification that some nodes were removed. It steps
	 *   through all the Thumbnail Components and makes sure they all are at the correct
	 *   location. The dead ones are removed.
	 */
	public void treeNodesRemoved ( TreeModelEvent e ) {
		/*Tools.log("GroupBrowser.treeNodesRemoved: " + e.toString() );
		if ( myNode == null ) {
			Tools.log("GroupBrowser.treeNodesRemoved: ERROR! This should not have been called as there is not group showing and therefore there should be no tree listener firing off. Ignoring notification.");
			return;
		}
		
		// if the current node is part of the tree that was deleted then we need to 
		//  reposition the group at the parent node that remains.
		if ( SortableDefaultMutableTreeNode.wasNodeDeleted( myNode, e ) ) {
			Tools.log("GroupBrowser.treeNodesRemoved: determined that a child node of the currently displaying node was deleted and therefore executing a setNode on the parent that remains.");
			setNode( (SortableDefaultMutableTreeNode) e.getTreePath().getLastPathComponent() );
		} else {
			// don't get excited and force a relayout unless the partent of the deleted 
			// node is the current group
			TreePath myPath = new TreePath( myNode.getPath() );
			if ( myPath.equals( e.getTreePath() ) ) {
				Tools.log("GroupBrowser.treeNodesRemoved: The removed node's parent is the group being shown. We must therefore relayout the children; myNode: " + myPath.toString() + " comparison:" +  ((SortableDefaultMutableTreeNode) e.getTreePath().getLastPathComponent() ).toString() );
				notifyRelayoutListeners(); 
			}
		}*/
	}


	/** 
	 *   This method is defined by the TreeModelListener interface and gives the 
	 *   JThumnailScrollPane a notification if there was a massive structure change in the 
	 *   tree. In this event all laying out shall stop and the group should be laid out from 
	 *   scratch.
	 */
	public void treeStructureChanged (TreeModelEvent e) {
		/*Tools.log("GroupBrowser.treeStructureChanged: " + e.toString() );
		if ( myNode == null ) {
			Tools.log("GroupBrowser.treeStructureChanged: ERROR! This should not have been called as there is not group showing and therefore there should be no tree listener firing off. Ignoring notification.");
			return;
		}
		if ( myNode.isNodeDescendant( (SortableDefaultMutableTreeNode) e.getTreePath().getLastPathComponent() ) ){
			notifyRelayoutListeners(); 
		}*/
	}

	
	
}
