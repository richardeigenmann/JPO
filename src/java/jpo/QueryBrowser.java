package jpo; 

import java.util.*;
import javax.swing.event.*; 
import javax.swing.tree.*; 
 
/*
QueryBrower.java:  an implementation of the ThumbnailBrowserInterface for browsing groups.

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
 *  This class implements the {@link ThumbnailBrowserInterface} in the specific manner that is required for 
 *  displaying {@link Query} in the {@link ThumbnailJScrollPane}.
 */

public class QueryBrowser implements ThumbnailBrowserInterface {

	/**
	 *  A reference to the query group that shall be browsed
	 */
	protected Query myQuery;

	
	/**
	 *  Constructs a new Group Browser object
	 */
	public QueryBrowser () {
	}


	/**
	 *  call this method to specify the query that should be browsed.
	 *
	 *  @param  queryToBrowse   The {@link Query} which should be browsed.
	 */
	public void setQuery ( Query queryToBrowse ) {
		myQuery = queryToBrowse;
		myQuery.refresh();
	}


	/**
	 *  returns the {@link Query} for this QueryBrowser
	 */
	public Query getQuery() {
		return myQuery;
	}


	/**
	 *  returns the name of the Group being displayed
	 */
	public String getTitle() {
		return getQuery().getTitle();
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