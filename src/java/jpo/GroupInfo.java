package jpo;

import java.io.*;

/*
GroupInfo.java:  definitions for the group objects

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
 * A class which holds information about the group and has been 
 * given the intelligence of how to write itself and it's pictures to an html
 * document.
 *  <p> This class must implement the Serializable interface or Drag and Drop will not work.
 *
 * @see PictureInfo
 */

public class GroupInfo implements Serializable {
	/**
	 *  The description of the GroupInfo.
	 **/
       	public String GroupDescription;
	

	/**
	 *   Constructor to create a new GroupInfo object. 
	 *
	 *   @param	description	The description of the Group
	 **/			      
	public GroupInfo( String description ) {
		setGroupName( description );
	}

	/**
	 *   toString method that returns the descrition of the group
	 **/
        public String toString() {
		return GroupDescription;
	}

	
	/**
	 *   Returns the description of the group.
	 *
	 *   @return	The description of the Group.
	 *   @see #setGroupName
	 **/			      
	public String getGroupName() {
		return GroupDescription;
	}


	/**
	 *   Set name of the GroupIno. The synchronized keyword is really important
	 *   because it prevents the JTree from displaying inconsistent
	 *   Group Nodes which have nodes with very tall textareas. - I hope it does
	 *
	 *   @param name 	The new description of the GroupInfo
	 *   @see #getGroupName
	 **/			      
	public synchronized void setGroupName( String name ) {
		GroupDescription = name;
	}


	

}
