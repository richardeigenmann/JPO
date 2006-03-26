package jpo;

import java.io.*;
import java.util.*;
import java.text.*;


/*
CategoryQuery.java:  A type of query for Categories

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
 *  This class implements the Query interface to show all the nodes attached to a category.
 */

public class CategoryQuery implements Query {

	/**
	 * the category key for the Query
	 */
	private Object key = null;


	/**
	 * An ArrayList of the nodes that represent these images
	 */
	private ArrayList resultList = null;
	

	/**
	 * Constructor for a Category Query
	 */
	public CategoryQuery( Object key ) {
		this.key = key;
		this.refresh();
	}

	
	/**
	 *  The query must be able to say how many results it will return.
	 */
	public int getNumberOfResults() {
		if ( resultList == null) {
			Tools.log("CategoryQuery.getNumberOfResults: called on a null result set.");
			return 0;
		}
		return resultList.size();
	}
	

	/**
	 *  This method returns the SDMTN node for the indicated position in the query. If the 
	 *  index is out of bounds it returns null.
	 *
	 *  @param index   The component index that is to be returned.
	 */
 	public SortableDefaultMutableTreeNode getIndex( int index ) {
		if ( resultList == null) {
			Tools.log("CategoryQuery.getIndex: called on a null result set.");
			return null;
		}
		if ( ( index < 0 ) || ( index >= resultList.size() ) ) {
			return null;
		}
		return (SortableDefaultMutableTreeNode) resultList.get( index );
	}


	/**
	 *  returns a title for the search that can be used to display the search results under.
	 */
	public String getTitle() {
		return toString();
	}

	/**
	 *  returns a the title for the search that can be used to display the search results under.
	 */
	public String toString() {
		return Settings.jpoResources.getString("CategoryQuery") + Settings.pictureCollection.getCategory( key ).toString();
	}


	/**
	 *  This method retrieves a new ArrayList of nodes that match the category.
	 */
	public void refresh() {
		resultList = null;
		resultList = Settings.pictureCollection.getCategoryUsageNodes( key, Settings.pictureCollection.getRootNode() );
	}

}
