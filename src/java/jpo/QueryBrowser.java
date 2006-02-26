package jpo; 

import java.util.*;
import javax.swing.event.*; 
import javax.swing.tree.*; 
 
/*
QueryBrower.java:  an implementation of the ThumbnailBrowserInterface for browsing groups.

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
 *  This class implements the ThumbnailBrowserInterface in the specific manner that is required for 
 *  displaying Queries in the Thumbnail JScrollPane.
 */

public class QueryBrowser implements ThumbnailBrowserInterface {

	/**
	 *  A reference to the query group that shall be browsed
	 */
	private DefaultMutableTreeNode myNode;

	/**
	 *  A reference to the query group that shall be browsed
	 */
	private Query myQuery;

	
	/**
	 *  Constructs a new Group Browser object
	 */
	public QueryBrowser () {
	}

	
	/**
	 *  call this method to specify the node that this QueryBrowser should refer to.
	 *
	 *  @param  node   The DefaultMutableTreeNode that refers to the Group that should be displayed.
	 */
	public void setNode ( DefaultMutableTreeNode node ) {
		if ( ( node == null ) 
		  || ( node.getUserObject() == null )
		  || ( ! ( node.getUserObject() instanceof Query ) ) ) {
		  	return;
		}
		
		myNode = node;
		myQuery = (Query) myNode.getUserObject();
	}


	/**
	 *  returns the name of the Group being displayed
	 */
	public String getTitle() {
		if ( myNode != null ) {
			return myNode.toString();
		} else {
			return "<no query>";
		}
	}


	/**
	 *  On a group we return the number of children in the group.
	 */
	public int getNumberOfNodes() {
		if ( myQuery == null ) {
			return 0;
		} else {
			return myQuery.getNumberOfResults();
		}
	}



	/**
	 *  This method returns the SDMTN node for the indicated position in the group
	 *  If there are more Thumbnails than nodes in the group it returns null.
	 *
	 *  @param index   The component index that is to be returned.
	 */
 	public SortableDefaultMutableTreeNode getNode( int index ) {
		if ( myQuery == null ) {
			return null;
		} else {
			return myQuery.getIndex( index );
		}
	}


	/**
	 *  This method unregisters the TreeModelListener and sets the variables to null;
	 */
	public void cleanup () {
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


	
	
}
