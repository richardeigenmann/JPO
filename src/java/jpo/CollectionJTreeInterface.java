package jpo;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;


/*
CategoryJTreeInterface.java:  interface definitions for the tree

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
 *  This interface defines the method that an object must implement if it 
 *  wants to create a {@link CollectionJTree}.
 *  
 **/
 
public interface CollectionJTreeInterface {


	/**
	 *   The GUI for the find command is not implemented in the {@link CollectionJTree} because
	 *   the user might want to display the search results only in the Thumbnail pane.
	 *   In order to facilitate this the find requests are passed back to the creator
	 *   (the {@link Jpo} object) that then passes the requested action down to
	 *   the {@link CollectionJTree} object or the {@link ThumbnailJScrollPane} object.
	 *
	 *   @param startSearchNode	The node from which the search will run.
	 */
	public void find ( SortableDefaultMutableTreeNode startSearchNode);


}

