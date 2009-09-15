package jpo.gui;

import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.*;
import java.io.File;

/*
GroupPopupInterface.java:  interface for the group popum menu
Copyright (C) 2002-2009 Richard Eigenmann.
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
     *
     * @param newNode
     */
    public void requestShowGroup( SortableDefaultMutableTreeNode newNode );


    /**
     * the implementing class must trap and do something when the user wants to
     * show the Pictures.
     */
    public void requestSlideshow( SortableDefaultMutableTreeNode groupNode );


    /**
     * the implementing class must trap and do something when the user wants to
     * Find pictures
     */
    public void requestFind( SortableDefaultMutableTreeNode groupNode );


    /**
     * the implementing class must trap and do something when the user wants to
     * edit the Group.
     */
    public void requestEditGroupNode( SortableDefaultMutableTreeNode groupNode );


    /**
     * the implementing class must trap and do something when the user wants to
     * edit the Group as a table.
     */
    public void requestEditGroupTable( SortableDefaultMutableTreeNode groupNode );


    /**
     * the implementing class must trap and do something when the user wants to
     * add an empty group.
     */
    public void requestAddGroup( SortableDefaultMutableTreeNode groupNode );


    /**
     * The implementing class should bring up a chooser dialog and should
     * add the selected pictures to the indicated group.
     *
     * @param groupNode The node to which the pictures should be added.
     */
    public void chooseAndAddPicturesToGroup(
            SortableDefaultMutableTreeNode groupNode );


    /**
     * the implementing class must trap and do something when the user wants to
     * add a collection.
     */
    public void requestAddCollection( SortableDefaultMutableTreeNode groupNode );


    /**
     * the implementing class must load the collection indicated
     *
     * @param fileToLoad
     */
    public void requestAddCollection( SortableDefaultMutableTreeNode groupNode,
            File fileToLoad );


    /**
     * the implementing class must trap and do something when the user wants to
     * export to HTML.
     */
    public void requestGroupExportHtml( SortableDefaultMutableTreeNode groupNode );


    /**
     * the implementing class must trap and do something when the user wants to
     * export to a JAR archive.
     */
    public void requestGroupExportFlatFile(
            SortableDefaultMutableTreeNode groupNode );


    /**
     * the implementing class must trap and do something when the user wants to
     * export to a JAR archive.
     *
    public void requestGroupExportJar();
     * /


    /**
     * the implementing class must trap and do something when the user wants to
     * export to a new collection.
     */
    public void requestGroupExportNewCollection(
            SortableDefaultMutableTreeNode groupNode );


    /**
     * the implementing class must trap and do something when the user wants to
     * remove a group.
     */
    public void requestGroupRemove( SortableDefaultMutableTreeNode groupNode );


    /**
     * the implementing class must trap and do something when the user wants to
     * move a group to the top.
     */
    public void requestMoveGroupToTop( SortableDefaultMutableTreeNode groupNode );


    /**
     * the implementing class must trap and do something when the user wants to
     * move a group up
     */
    public void requestMoveGroupUp( SortableDefaultMutableTreeNode groupNode );


    /**
     * the implementing class must trap and do something when the user wants to
     * move a group down
     */
    public void requestMoveGroupDown( SortableDefaultMutableTreeNode groupNode );


    /**
     * the implementing class must trap and do something when the user wants to
     * move a group down
     */
    public void requestMoveGroupToBottom(
            SortableDefaultMutableTreeNode groupNode );


    /**
     *  the implementing class must trap and do something whne the user wants to
     *  move the current group to the specified node.
     *  @param  targetGroup
     */
    public void requestMoveToNode( SortableDefaultMutableTreeNode groupNode,
            SortableDefaultMutableTreeNode targetGroup );


    /**
     * the implementing class must trap and do something when the user wants to
     * consolidate a group.
     */
    public void requestConsolidateGroup(
            SortableDefaultMutableTreeNode groupNode );


    /**
     * the implementing class must trap and do something when the user wants to
     * sort a group.
     *
     * @param groupNode  The node to sort
     * @param	sortCriteria 	the constant Settings.DESCRIPTION, Settings.FILM_REFERENCE,
     *				Settings.CREATION_TIME, Settings.COMMENT, Settings.PHOTOGRAPHER,
     *				Settings.COPYRIGHT_HOLDER.
     */
    public void requestSort( SortableDefaultMutableTreeNode groupNode,
            int sortCriteria );


    /**
     * the implementing class must trap and do something when the user wants to
     * edit the categories
     */
    public void showCategoryUsageGUI( SortableDefaultMutableTreeNode groupNode );
}

