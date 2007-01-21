package jpo; 
 

/*
ThumbnailBrowserInterface.java:  an interface that defines the methods that a class 
must implement so that the ThumbnailJScrollPane can identify the Thumbnails to be displayed.

Copyright (C) 2002-2007  Richard Eigenmann, Zürich, Switzerland
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
 *   ThumbnailBrowserInterface.java:  an interface that defines the methods that a class 
 *   must implement so that the ThumbnailJScrollPane can identify the Thumbnails to be displayed.
 */
public interface ThumbnailBrowserInterface {

	/**
	 *  The implementing class must return the title for the images being shown. This 
	 *  is displayed in the top part of the ThumbnailJScrollPane.
	 */
	public String getTitle();


	/**
	 *  The implementing class must return the number of nodes it contains.
	 */
	public int getNumberOfNodes();


	/**
	 *  This method must return the SortableDefaultMutableTreeNode indicated
	 *  by the position number passed as parameter. If the index is out of bounds 
	 *  null is returned.
	 *
	 *  @param  componentNumber     The number between 0 and #getNumberOfNodes()
	 */
	public SortableDefaultMutableTreeNode getNode( int componentNumber );



	/**
	 *  method to register a ThumbnailJScrollPane as a listener
	 */
	public void addRelayoutListener ( RelayoutListener listener ); 


	/**
	 *  method to remove a ThumbnailJScrollPane as a listener
	 */
	public void removeRelayoutListener ( RelayoutListener listener );


	/**
	 *  method that must be implemented which gives the implementing object the
	 *  chance to free variables and deallocate itself from listeners and things.
	 */
	public void cleanup ();


}
