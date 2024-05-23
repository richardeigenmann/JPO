package org.jpo.gui.swing;

import com.google.common.eventbus.Subscribe;
import org.jpo.cache.QUEUE_PRIORITY;
import org.jpo.datamodel.*;
import org.jpo.eventbus.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 Copyright (C) 2002-2024 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * Generates a popup menu on a group node
 */
public class GroupPopupMenu extends JPopupMenu {

    /**
     * An array of recently opened collections.
     */
    private final JMenuItem[] recentOpenedfileJMenuItem = new JMenuItem[Settings.MAX_MEMORISE];

    /**
     * a separator for the Move menu
     */
    private final JSeparator movePictureNodeSeparator = new JSeparator();

    /**
     * menu items for the recently dropped group nodes
     */
    private final JMenuItem[] recentDropNodes = new JMenuItem[Settings.getMaxDropnodes()];

    /**
     * the node we are doing the popup menu for
     */
    private final SortableDefaultMutableTreeNode popupNode;

    /**
     * Creates a popup menu for a group.
     *
     * @param node the node for which the popup menu is being created.
     */
    public GroupPopupMenu(final SortableDefaultMutableTreeNode node) {
        this.popupNode = node;
        JpoEventBus.getInstance().register(new RecentCollectionsChangedEventHandler());
        JpoEventBus.getInstance().register(new RecentDropNodeChangedEventHandler());

        final Runnable runnable = this::initComponents;
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    /**
     * Create the menu items
     */
    private void initComponents() {
        final var title = getPopupNode().toString();
        setLabel(title);
        add(getTitleJMenuItem(title));
        addSeparator();
        add(getGroupShowJMenuItem());
        add(getGroupSlideshowJMenuItem());
        addSeparator();
        if (popupNode.getPictureCollection().getAllowEdits()) {
            add(getGroupRefreshJMenuItem());
            addSeparator();
            add(getGroupTableJMenuItem());
            addSeparator();
            add(getAddGroupJMenu());
            add(getMoveGroupNodeJMenu());
            add(getGroupRemove());
            addSeparator();
            add(getConsolidateMoveJMenuItem());
            addSeparator();
            add(getSortJMenu());
            addSeparator();
        }
        add(getGroupSelectForEmail());
        add(getGroupExportHtml());
        add(getGroupExportNewCollection());
        add(getGroupExportFlatFile());
        add(getGroupExportPicasa());
        addSeparator();
        add(getGroupEditJMenuItem());
    }

    private JMenuItem getTitleJMenuItem(final String title) {
        final var titleJMenuItem = new JMenuItem(title);
        titleJMenuItem.setEnabled(false);
        return titleJMenuItem;
    }


    private JMenuItem getGroupShowJMenuItem() {
        final var groupShowJMenuItem = new JMenuItem(Settings.getJpoResources().getString("groupShowJMenuItem"));
        groupShowJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowGroupRequest(popupNode)));
        return groupShowJMenuItem;
    }

    private JMenuItem getGroupSlideshowJMenuItem() {
        final var groupSlideshowJMenuItem = new JMenuItem(Settings.getJpoResources().getString("groupSlideshowJMenuItem"));
        groupSlideshowJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowPictureRequest(new FlatGroupNavigator(popupNode), 0)));
        if (!popupNode.hasChildPictureNodes()) {
            groupSlideshowJMenuItem.setEnabled(false);
        }
        return groupSlideshowJMenuItem;
    }

    private JMenuItem getGroupRefreshJMenuItem() {
        final var groupRefreshJMenuItem = new JMenuItem(Settings.getJpoResources().getString("groupRefreshJMenuItem"));
        groupRefreshJMenuItem.addActionListener((ActionEvent e) -> {
            final List<SortableDefaultMutableTreeNode> actionNodes = new ArrayList<>();
            if ((Settings.getPictureCollection().countSelectedNodes() > 0) && (Settings.getPictureCollection().isSelected(popupNode))) {
                actionNodes.addAll(Settings.getPictureCollection().getSelection());
            } else {
                actionNodes.add(popupNode);
            }

            JpoEventBus.getInstance().post(new RefreshThumbnailRequest(actionNodes, false, QUEUE_PRIORITY.HIGH_PRIORITY));
        });

        return groupRefreshJMenuItem;
    }

    private JMenuItem getGroupTableJMenuItem() {
        final var groupTableJMenuItem = new JMenuItem(Settings.getJpoResources().getString("groupTableJMenuItemLabel"));
        groupTableJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowGroupAsTableRequest(popupNode)));
        return groupTableJMenuItem;
    }

    private JMenu getAddGroupJMenu() {
        //submenu which has several navigation options
        final var addGroupJMenu = new JMenu(Settings.getJpoResources().getString("addGroupJMenuLabel"));
        // menu item that allows adding a new blank group
        final var addNewGroupJMenuItem = new JMenuItem(Settings.getJpoResources().getString("addNewGroupJMenuItemLabel"));
        addNewGroupJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new AddEmptyGroupRequest(popupNode)));
        addGroupJMenu.add(addNewGroupJMenuItem);

        final var addPicturesJMenuItem = new JMenuItem(Settings.getJpoResources().getString("addPicturesJMenuItemLabel"));
        addPicturesJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ChooseAndAddPicturesToGroupRequest(popupNode)));
        addGroupJMenu.add(addPicturesJMenuItem);

        // Add Collections
        // submenu which offers the choice of either loading from a file or from one of the recent collections
        final var addCollectionJMenu = new JMenu(Settings.getJpoResources().getString("addCollectionJMenuItemLabel"));
        addGroupJMenu.add(addCollectionJMenu);

        // menu item that allows adding a collection of pictures
        final var addCollectionFromFile = new JMenuItem(Settings.getJpoResources().getString("addCollectionFormFile"));
        addCollectionFromFile.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ChooseAndAddCollectionRequest(popupNode)));
        addCollectionJMenu.add(addCollectionFromFile);

        // add the recently opened files to the menu
        for (var i = 0; i < Settings.MAX_MEMORISE; i++) {
            recentOpenedfileJMenuItem[i] = new JMenuItem();
            final int index = i;  // the anonymous inner class needs a final variable
            recentOpenedfileJMenuItem[i].addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new AddCollectionToGroupRequest(popupNode, new File(Settings.getRecentCollections()[index]))));
            recentOpenedfileJMenuItem[i].setVisible(false);
            addCollectionJMenu.add(recentOpenedfileJMenuItem[i]);
        }
        populateRecentFilesMenuItems();

        // menu item that allows adding from a list of filenames
        final var addFlatFileJMenuItem = new JMenuItem(Settings.getJpoResources().getString("addFlatFileJMenuItemLabel"));
        addFlatFileJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ChooseAndAddFlatfileRequest(popupNode)));
        addGroupJMenu.add(addFlatFileJMenuItem);

        return addGroupJMenu;
    }


    private JMenu getMoveGroupNodeJMenu() {
        // submenu which has several navigation options
        final var moveGroupNodeJMenu = new JMenu(Settings.getJpoResources().getString("moveNodeJMenuLabel"));

        final SortableDefaultMutableTreeNode[] nodes = Settings.getRecentDropNodes().toArray(new SortableDefaultMutableTreeNode[0]);
        for (var i = 0; i < Settings.getMaxDropnodes(); i++) {
            final var dropnode = i;
            recentDropNodes[i] = new JMenuItem();
            recentDropNodes[i].addActionListener((ActionEvent e) -> {
                popupNode.moveToLastChild(nodes[dropnode]);
                Settings.memorizeGroupOfDropLocation(nodes[dropnode]);
                JpoEventBus.getInstance().post(new RecentDropNodesChangedEvent());
            });
            moveGroupNodeJMenu.add(recentDropNodes[i]);
        }
        moveGroupNodeJMenu.add(movePictureNodeSeparator);
        populateRecentDropNodeMenuItems();

        //menu item that allows move to top op list
        final var moveGroupToTopJMenuItem = new JMenuItem(Settings.getJpoResources().getString("moveGroupToTopJMenuItem"));
        moveGroupToTopJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new MoveNodeToTopRequest(List.of(popupNode))));
        moveGroupNodeJMenu.add(moveGroupToTopJMenuItem);

        // menu item that allows move up in the list
        final var moveGroupUpJMenuItem = new JMenuItem(Settings.getJpoResources().getString("moveGroupUpJMenuItem"));
        moveGroupUpJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new MoveNodeUpRequest(List.of(popupNode))));
        moveGroupNodeJMenu.add(moveGroupUpJMenuItem);

        //menu item that allows move up in the list
        final var moveGroupDownJMenuItem = new JMenuItem(Settings.getJpoResources().getString("moveGroupDownJMenuItem"));
        moveGroupDownJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new MoveNodeDownRequest(List.of(popupNode))));
        moveGroupNodeJMenu.add(moveGroupDownJMenuItem);

        // menu item that allows move to top op list
        final var moveGroupToBottomJMenuItem = new JMenuItem(Settings.getJpoResources().getString("moveGroupToBottomJMenuItem"));
        moveGroupToBottomJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new MoveNodeToBottomRequest(List.of(popupNode))));
        moveGroupNodeJMenu.add(moveGroupToBottomJMenuItem);
        //menu item that allows indenting the group
        final var indentJMenuItem = new JMenuItem(Settings.getJpoResources().getString("indentJMenuItem"));
        indentJMenuItem.addActionListener((ActionEvent e) -> popupNode.indentNode());
        moveGroupNodeJMenu.add(indentJMenuItem);

        // menu item that allows out-denting the group
        final var outdentJMenuItem = new JMenuItem(Settings.getJpoResources().getString("outdentJMenuItem"));
        outdentJMenuItem.addActionListener((ActionEvent e) -> popupNode.outdentNode());
        moveGroupNodeJMenu.add(outdentJMenuItem);
        return moveGroupNodeJMenu;
    }

    private JMenuItem getGroupRemove() {
        final var groupRemove = new JMenuItem(Settings.getJpoResources().getString("groupRemoveLabel"));
        groupRemove.addActionListener((ActionEvent event) -> {
            if ((Settings.getPictureCollection().countSelectedNodes() > 0) && (Settings.getPictureCollection().isSelected(popupNode))) {
                JpoEventBus.getInstance().post(new RemoveNodeRequest(Settings.getPictureCollection().getSelection()));
            } else {
                JpoEventBus.getInstance().post(new RemoveNodeRequest(List.of(popupNode)));
            }
        });
        return groupRemove;
    }

    private JMenu getSortJMenu() {
        //submenu which has several sort options
        final var sortJMenu = new JMenu(Settings.getJpoResources().getString("sortJMenu"));
        //requests a sort by Description
        final List<SortOption> sortOptions = Settings.getSortOptions();
        final var sortByDescription = sortOptions.get(1);
        final var sortByDescriptionJMenuItem = new JMenuItem(sortByDescription.getDescription());
        sortByDescriptionJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new SortGroupRequest(popupNode, sortByDescription.getSortCode())));
        sortJMenu.add(sortByDescriptionJMenuItem);

        //requests a sort by Film Reference
        final var sortByFilmReference = sortOptions.get(2);
        final var sortByFilmReferenceJMenuItem = new JMenuItem(sortByFilmReference.getDescription());
        sortByFilmReferenceJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new SortGroupRequest(popupNode, sortByFilmReference.getSortCode())));
        sortJMenu.add(sortByFilmReferenceJMenuItem);

        //requests a sort by Creation Time
        final var sortByCreationTime = sortOptions.get(3);
        final var sortByCreationTimeJMenuItem = new JMenuItem(sortByCreationTime.getDescription());
        sortByCreationTimeJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new SortGroupRequest(popupNode, sortByCreationTime.getSortCode())));
        sortJMenu.add(sortByCreationTimeJMenuItem);

        // requests a sort by Comment
        final var sortByComment = sortOptions.get(4);
        final var sortByCommentJMenuItem = new JMenuItem(sortByComment.getDescription());
        sortByCommentJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new SortGroupRequest(popupNode, sortByComment.getSortCode())));
        sortJMenu.add(sortByCommentJMenuItem);

        // requests a sort by Photographer
        final var sortByPhotographer = sortOptions.get(5);
        final var sortByPhotographerJMenuItem = new JMenuItem(sortByPhotographer.getDescription());
        sortByPhotographerJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new SortGroupRequest(popupNode, sortByPhotographer.getSortCode())));
        sortJMenu.add(sortByPhotographerJMenuItem);

        // requests a sort by Copyright Holder
        final var sortByCopyrightHolder = sortOptions.get(6);
        final var sortByCopyrightHolderTimeJMenuItem = new JMenuItem(sortByCopyrightHolder.getDescription());
        sortByCopyrightHolderTimeJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new SortGroupRequest(popupNode, sortByCopyrightHolder.getSortCode())));
        sortJMenu.add(sortByCopyrightHolderTimeJMenuItem);
        return sortJMenu;
    }

    private JMenuItem getGroupSelectForEmail() {
        final var groupSelectForEmail = new JMenuItem(Settings.getJpoResources().getString("groupSelectForEmail"));
        groupSelectForEmail.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new AddGroupToEmailSelectionRequest(popupNode)));
        return groupSelectForEmail;
    }

    /**
     * Here we receive notification that the drop nodes have been updated. We
     * then populate the Move submenu with the current drop nodes.
     */
    private JMenuItem getGroupEditJMenuItem() {
        final var groupEditJMenuItem = new JMenuItem(Settings.getJpoResources().getString("groupEditJMenuItem"));
        groupEditJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowGroupInfoEditorRequest(popupNode)));
        return groupEditJMenuItem;
    }

    private JMenuItem getGroupExportHtml() {
        final var groupExportHtml = new JMenuItem(Settings.getJpoResources().getString("groupExportHtmlMenuText"));
        groupExportHtml.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ExportGroupToHtmlRequest(popupNode)));
        return groupExportHtml;
    }

    private JMenuItem getConsolidateMoveJMenuItem() {
        // menu item that brings a dialog to ask where to consolidate the files to
        final var consolidateMoveJMenuItem = new JMenuItem(Settings.getJpoResources().getString("consolidateMoveLabel"));
        consolidateMoveJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ConsolidateGroupDialogRequest(popupNode, null)));
        return consolidateMoveJMenuItem;
    }

    private JMenuItem getGroupExportNewCollection() {
        //  menu item that allows the user to export the group to several formats
        final var groupExportNewCollection = new JMenuItem(Settings.getJpoResources().getString("groupExportNewCollectionMenuText"));
        groupExportNewCollection.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ExportGroupToNewCollectionRequest(popupNode)));
        return groupExportNewCollection;
    }

    private JMenuItem getGroupExportFlatFile() {
        // menu item that allows the user to export the group to a flat list of filenames
        final var groupExportFlatFile = new JMenuItem(Settings.getJpoResources().getString("groupExportFlatFileMenuText"));
        groupExportFlatFile.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ExportGroupToFlatFileRequest(popupNode)));
        return groupExportFlatFile;
    }

    private JMenuItem getGroupExportPicasa() {
        // menu item that allows the user to upload the group to Picasa
        final var groupExportPicasa = new JMenuItem("Export to Picasa");
        groupExportPicasa.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ExportGroupToPicasaRequest(popupNode)));
        return groupExportPicasa;
    }


    private class RecentDropNodeChangedEventHandler {


        /**
         * Handle the event by updating the submenu items
         *
         * @param event event
         */
        @Subscribe
        public void handleRecentDropNodeChangedEventHandler(final RecentDropNodesChangedEvent event) {
            SwingUtilities.invokeLater(GroupPopupMenu.this::populateRecentDropNodeMenuItems);

        }
    }

    /**
     * Populates the Move menu with the recent drop nodes of the application. If
     * there are no recent drop nodes the list is empty.
     */
    private void populateRecentDropNodeMenuItems() {
        var dropNodesVisible = false;
        final SortableDefaultMutableTreeNode[] nodes = Settings.getRecentDropNodes().toArray(new SortableDefaultMutableTreeNode[0]);
        for (var i = 0; i < Settings.getMaxDropnodes(); i++) {
            if (i < nodes.length && nodes[i] != null) {
                recentDropNodes[i].setText("To Group: " + nodes[i].toString());
                recentDropNodes[i].setVisible(true);
                dropNodesVisible = true;
            } else {
                recentDropNodes[i].setVisible(false);
            }
        }
        movePictureNodeSeparator.setVisible(dropNodesVisible);
    }

    /**
     * Receives an Event when the RecentCollections array has been modified.
     * Ensures that the menu entries are updated in line with the new recent
     * collections.
     */
    private class RecentCollectionsChangedEventHandler {

        /**
         * Handle the event by updating the submenu items
         *
         * @param event event
         */
        @Subscribe
        public void handleRecentCollectionsChangedEvent(final RecentCollectionsChangedEvent event) {
            SwingUtilities.invokeLater(GroupPopupMenu.this::populateRecentFilesMenuItems);

        }
    }

    /**
     * Sets up the menu entries in the Add &gt; Collection sub menu from the
     * recentCollections in the application. Has to be run on the EDT.
     */
    private void populateRecentFilesMenuItems() {
        Tools.checkEDT();
        for (var i = 0; i < Settings.getRecentCollections().length; i++) {
            if (Settings.getRecentCollections()[i] != null) {
                recentOpenedfileJMenuItem[i].setText((i + 1) + ": " + Settings.getRecentCollections()[i]);
                recentOpenedfileJMenuItem[i].setVisible(true);
            } else {
                recentOpenedfileJMenuItem[i].setVisible(false);
            }
        }
    }

    public SortableDefaultMutableTreeNode getPopupNode() {
        return popupNode;
    }
}
