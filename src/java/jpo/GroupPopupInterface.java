package jpo;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

/*
GroupPopupInterface.java:  interface for the group popum menu
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
 *  wants to create a GroupPopupMenu
 *  
 **/
 
public interface GroupPopupInterface {


	/**
	 * the implementing class must trap and do something when the user wants to
	 * show the Group.
	 */
	public void requestShowGroup();

	/**
	 * the implementing class must trap and do something when the user wants to
	 * show the Pictures.
	 */
	public void requestSlideshow();


	/**
	 * the implementing class must trap and do something when the user wants to
	 * Find pictures
	 */
	public void requestFind();


	/**
	 * the implementing class must trap and do something when the user wants to
	 * edit the Group.
	 */
	public void requestEditGroupNode();


	/**
	 * the implementing class must trap and do something when the user wants to
	 * edit the Group as a table.
	 */
	public void requestEditGroupTable();


	/**
	 * the implementing class must trap and do something when the user wants to
	 * add an empty group.
	 */
	public void requestAddGroup();


	/**
	 * the implementing class must trap and do something when the user wants to
	 * add pictures
	 */
	public void requestAdd();



	/**
	 * the implementing class must trap and do something when the user wants to
	 * add a collection.
	 */
	public void requestAddCollection();


	
	/**
	 * the implementing class must trap and do something when the user wants to
	 * export to HTML.
	 */
	public void requestGroupExportHtml();

	/**
	 * the implementing class must trap and do something when the user wants to
	 * export to a JAR archive.
	 */
	public void requestGroupExportFlatFile();


	/**
	 * the implementing class must trap and do something when the user wants to
	 * export to a JAR archive.
	 */
	public void requestGroupExportJar();


	/**
	 * the implementing class must trap and do something when the user wants to
	 * export to a new collection.
	 */
	public void requestGroupExportNewCollection();


	/**
	 * the implementing class must trap and do something when the user wants to
	 * remove a group.
	 */
	public void requestGroupRemove();


	/**
	 * the implementing class must trap and do something when the user wants to
	 * move a group to the top.
	 */
	public void requestMoveGroupToTop();


	/**
	 * the implementing class must trap and do something when the user wants to
	 * move a group up
	 */
	public void requestMoveGroupUp();


	/**
	 * the implementing class must trap and do something when the user wants to
	 * move a group down
	 */
	public void requestMoveGroupDown();


	/**
	 * the implementing class must trap and do something when the user wants to
	 * move a group down
	 */
	public void requestMoveGroupToBottom();


	/**
	 *  the implementing class must trap and do something whne the user wants to
	 *  move the current group to the specified node.
	 *  @param  targetGroup  
	 */
	public void requestMoveToNode( SortableDefaultMutableTreeNode targetGroup );



	/**
	 * the implementing class must trap and do something when the user wants to
	 * consolidate a group.
	 */
	public void requestConsolidateGroup();


	/**
	 * the implementing class must trap and do something when the user wants to
	 * sort a group.
	 *
	 * @param	sortCriteria 	the constant Settings.DESCRIPTION, Settings.FILM_REFERENCE,
	 *				Settings.CREATION_TIME, Settings.COMMENT, Settings.PHOTOGRAPHER,
	 *				Settings.COPYRIGHT_HOLDER.
	 */
	public void requestSort( int sortCriteria );

}

