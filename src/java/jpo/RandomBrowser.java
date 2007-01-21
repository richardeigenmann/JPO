package jpo; 

import java.util.*;
import javax.swing.event.*; 
import javax.swing.tree.*; 
 
/*
RandomBrower.java:  an implementation of the ThumbnailBrowserInterface for browsing random pictures.

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
 *  Thuis class implements the ThumbnailBrowserInterface in the specific manner that is required for 
 *  displaying Groups in the Thumbnail JScrollPane.
 */

public class RandomBrowser extends ThumbnailBrowser {

	/**
	 *  Constructor for a RandomBrowser. 
	 *
	 *  @param groupNode    The groupNode under which the randomisation should happen.
	 */
	RandomBrowser( SortableDefaultMutableTreeNode groupNode ) {
		Tools.log("RandomBrowser: constructor called on node: " + groupNode.toString() );
		enumerateAndAddToList( allPictures, groupNode );
	}
	

	/**
	 *   This ArrayList holds the nodes we have identified
	 */
	private ArrayList randomNodes = new ArrayList();
	

	/**
	 *  returns the string Random
	 */
	public String getTitle() {
		return "Random";
	}


	/**
	 *  The Random Browser returns the size as one more than the randomNodes ArrayList contains.
	 *  If the last element is requested the Random Generator picks a new one.
	 */
	public int getNumberOfNodes() {
		Tools.log("RandomBrowser.getNumberOfNodes: returning: " + Integer.toString( randomNodes.size() ) );
		return randomNodes.size();
	}



	/**
	 *  This method returns the node for the indicated position in the group.
	 *
	 *  @param index   The component index that is to be returned.
	 */
 	public SortableDefaultMutableTreeNode getNode( int index ) {
		Tools.log("RandomBrowser.getNode: requested for node: " + Integer.toString( index ) );
		if ( index >= randomNodes.size() ) {
			int randomIndex = (int) ( Math.random() * allPictures.size() );
			randomNodes.add( allPictures.get( randomIndex ) );
		}
		return (SortableDefaultMutableTreeNode) randomNodes.get( index );
	}


	/**
	 *  This ArrayList holds a reference to each picture under the start group.
	 */
	private ArrayList allPictures = new ArrayList();


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




	/**
	 *  This method unregisters the TreeModelListener and sets the variables to null;
	 */
	public void cleanup () {
            super.cleanup();
		randomNodes = null;
		allPictures = null;
	}

	


	
	
}
