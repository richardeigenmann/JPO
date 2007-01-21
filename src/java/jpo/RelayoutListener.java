package jpo;


/*
RelayoutListener.java:  interface for notification

Copyright (C) 2007 Richard Eigenmann, Zürich, Switzerland
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
 * This interface allows the ThumbnailBrowser to inform a component that is displaying
 * the Thumbnails that the list of nodes has changed and that the view should be updated.
 */

public interface RelayoutListener {

	/**
	 *  This method is invoked on the implementing target object when the nodes of the 
         *  ThumbnailBrowser have changed.
	 */
	public void assignThumbnails ();

}

