package jpo; 

import java.util.*;
 
/*
SequentialBrower.java:  an implementation of the ThumbnailBrowserInterface for browsing pictures sequentially.

Copyright (C) 2006-2007  Richard Eigenmann, Zürich, Switzerland
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

public class SequentialBrowser extends ArrayListBrowser {

	/**
	 *  Constructor for a SequentialBrowser. 
	 *
	 *  @param groupNode    The groupNode under which the pictures should be displayed.
	 */
	SequentialBrowser( SortableDefaultMutableTreeNode groupNode ) {
		Tools.log("SequentialBrowser: constructor called on node: " + groupNode.toString() );
		this.groupNode = groupNode;
		enumerateAndAddToList( allPictures, groupNode );
	}
	
	/**
	 *  A reference to the group for which this SequentialBrowser was created.
	 */
	private SortableDefaultMutableTreeNode groupNode = null;


	/**
	 *  returns the string Sequential
	 */
	public String getTitle() {
		if ( ( groupNode != null )
		  && ( groupNode.getUserObject() instanceof GroupInfo ) ) {
		  	return ( (GroupInfo) groupNode.getUserObject() ).getGroupName();
		} else {
			return "Sequential";
		}
	}




	/**
	 *  This method collects all pictures under the startNode into the supplied ArrayList. This method
	 *  calls itself recursively.
	 *
	 *  @param  myList   The ArrayList to which to add the pictures.
	 *  @param  startNode   The group node under which to collect the pictures.
	 */
	public void enumerateAndAddToList( ArrayList myList, SortableDefaultMutableTreeNode startNode ) {
		Tools.log("RandomBrowser.enumerateAndAddToList: invoked on group " + startNode.toString() );
		Enumeration kids = startNode.children();
		SortableDefaultMutableTreeNode n;
		
		while (kids.hasMoreElements()) {
			n = (SortableDefaultMutableTreeNode) kids.nextElement();
			if (n.getUserObject() instanceof GroupInfo)
				enumerateAndAddToList (myList, n);
			else if (n.getUserObject() instanceof PictureInfo) 
				myList.add(n);
		}
	}



	
	
}
