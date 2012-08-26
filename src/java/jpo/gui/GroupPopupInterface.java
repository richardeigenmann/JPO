package jpo.gui;

import java.io.File;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/*
 * GroupPopupInterface.java: interface for the group popum menu Copyright (C)
 * 2002-2012 Richard Eigenmann. This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details. You should have received a copy of
 * the GNU General Public License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA. The license is in gpl.txt. See
 * http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 * This interface defines the method that an object must implement if it wants
 * to create a GroupPopupMenu
 *
 *
 */
public interface GroupPopupInterface {

    /**
     * the implementing class must trap and do something when the user wants to
     * show the Group.
     *
     * @param newNode
     */
    public void requestShowGroup(SortableDefaultMutableTreeNode newNode);

    /**
     * the implementing class must trap and do something when the user wants to
     * show the Pictures.
     *
     * @param groupNode
     */
    public void requestSlideshow(SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must trap and do something when the user wants to
     * Find pictures
     *
     * @param groupNode
     */
    public void requestFind(SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must trap and do something when the user wants to
     * edit the Group.
     *
     * @param groupNode
     */
    public void requestEditGroupNode(SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must trap and do something when the user wants to
     * edit the Group as a table.
     *
     * @param groupNode
     */
    public void requestEditGroupTable(SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must trap and do something when the user wants to
     * add an empty group.
     *
     * @param groupNode
     */
    public void requestAddGroup(SortableDefaultMutableTreeNode groupNode);

    /**
     * The implementing class should bring up a chooser dialog and should add
     * the selected pictures to the indicated group.
     *
     * @param groupNode The node to which the pictures should be added.
     */
    public void chooseAndAddPicturesToGroup(
            SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must trap and do something when the user wants to
     * add a collection.
     *
     * @param groupNode
     */
    public void requestAddCollection(SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must load the collection indicated
     *
     * @param groupNode
     * @param fileToLoad
     */
    public void requestAddCollection(SortableDefaultMutableTreeNode groupNode,
            File fileToLoad);

    /**
     * the implementing class must trap this request and must select all the
     * child nodes for email.
     *
     * @param groupNode The group node whose children should be emailed
     */
    public void requestEmailSelection(SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must trap and do something when the user wants to
     * export to HTML.
     *
     * @param groupNode
     */
    public void requestGroupExportHtml(SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must trap and do something when the user wants to
     * export to a JAR archive.
     *
     * @param groupNode
     */
    public void requestGroupExportFlatFile(
            SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must export the group of pictures to Picasa
     *
     * @param groupNode
     */
    public void requestGroupExportPicasa(
            SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must trap and do something when the user wants to
     * export to a JAR archive.
     *
     * public void requestGroupExportJar(); /
     *
     *
     * /**
     * the implementing class must trap and do something when the user wants to
     * export to a new collection.
     *
     * @param groupNode
     */
    public void requestGroupExportNewCollection(
            SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must trap and do something when the user wants to
     * remove a group.
     *
     * @param groupNode
     */
    public void requestGroupRemove(SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must trap and do something when the user wants to
     * move a group to the top.
     *
     * @param groupNode
     */
    public void requestMoveGroupToTop(SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must trap and do something when the user wants to
     * move a group up
     *
     * @param groupNode
     */
    public void requestMoveGroupUp(SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must trap and do something when the user wants to
     * move a group down
     *
     * @param groupNode
     */
    public void requestMoveGroupDown(SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must trap and do something when the user wants to
     * move a group down
     *
     * @param groupNode
     */
    public void requestMoveGroupToBottom(
            SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must trap and do something whne the user wants to
     * move the current group to the specified node.
     *
     * @param groupNode
     * @param targetGroup
     */
    public void requestMoveToNode(SortableDefaultMutableTreeNode groupNode,
            SortableDefaultMutableTreeNode targetGroup);

    /**
     * the implementing class must trap and do something when the user wants to
     * consolidate a group.
     *
     * @param groupNode
     */
    public void requestConsolidateGroup(
            SortableDefaultMutableTreeNode groupNode);

    /**
     * the implementing class must trap and do something when the user wants to
     * sort a group.
     *
     * @param groupNode The node to sort
     * @param	sortCriteria the constant Settings.DESCRIPTION,
     * Settings.FILM_REFERENCE, Settings.CREATION_TIME, Settings.COMMENT,
     * Settings.PHOTOGRAPHER, Settings.COPYRIGHT_HOLDER.
     */
    public void requestSort(SortableDefaultMutableTreeNode groupNode,
            int sortCriteria);

    /**
     * the implementing class must trap and do something when the user wants to
     * edit the categories
     *
     * @param groupNode
     */
    public void showCategoryUsageGUI(SortableDefaultMutableTreeNode groupNode);
}
