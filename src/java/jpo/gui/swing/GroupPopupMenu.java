package jpo.gui.swing;

import com.google.common.eventbus.Subscribe;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import jpo.EventBus.AddCollectionToGroupRequest;
import jpo.EventBus.AddEmptyGroupRequest;
import jpo.EventBus.AddGroupToEmailSelectionRequest;
import jpo.EventBus.ChooseAndAddCollectionRequest;
import jpo.EventBus.ChooseAndAddFlatfileRequest;
import jpo.EventBus.ChooseAndAddPicturesToGroupRequest;
import jpo.EventBus.ConsolidateGroupRequest;
import jpo.EventBus.ExportGroupToFlatFileRequest;
import jpo.EventBus.ExportGroupToHtmlRequest;
import jpo.EventBus.ExportGroupToNewCollectionRequest;
import jpo.EventBus.ExportGroupToPicasaRequest;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.MoveNodeDownRequest;
import jpo.EventBus.MoveNodeToBottomRequest;
import jpo.EventBus.MoveNodeToTopRequest;
import jpo.EventBus.MoveNodeUpRequest;
import jpo.EventBus.OpenSearchDialogRequest;
import jpo.EventBus.RecentCollectionsChangedEvent;
import jpo.EventBus.RecentDropNodesChangedEvent;
import jpo.EventBus.RefreshThumbnailRequest;
import jpo.EventBus.RemoveNodeRequest;
import jpo.EventBus.ShowCategoryUsageEditorRequest;
import jpo.EventBus.ShowGroupAsTableRequest;
import jpo.EventBus.ShowGroupInfoEditorRequest;
import jpo.EventBus.ShowGroupRequest;
import jpo.EventBus.ShowPictureRequest;
import jpo.EventBus.SortGroupRequest;
import jpo.dataModel.Settings;
import jpo.dataModel.SortOption;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;
import jpo.cache.ThumbnailQueueRequest.QUEUE_PRIORITY;

/*
 GroupPopupMenu.java: popup menu for groups

 Copyright (C) 2002 - 2015  Richard Eigenmann.
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
 * Generates a popup menu on a group node
 *
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
    private final JMenuItem[] recentDropNodes = new JMenuItem[Settings.MAX_DROPNODES];

    /**
     * the node we are doing the popup menu for
     */
    private final SortableDefaultMutableTreeNode popupNode;

    /**
     * Creates a popup menu for a group.
     *
     * @param node the node for which the popup menu is being created.
     */
    public GroupPopupMenu( final SortableDefaultMutableTreeNode node ) {
        this.popupNode = node;
        JpoEventBus.getInstance().register( new RecentCollectionsChangedEventHandler() );
        JpoEventBus.getInstance().register( new RecentDropNodeChangedEventHandler() );

        Runnable r = new Runnable() {

            @Override
            public void run() {
                initComponents();
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }

    }

    /**
     * Create the menu items
     */
    private void initComponents() {
        JMenuItem groupShowJMenuItem = new JMenuItem( Settings.jpoResources.getString( "groupShowJMenuItem" ) );
        groupShowJMenuItem.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JpoEventBus.getInstance().post( new ShowGroupRequest( popupNode ) );
            }
        } );
        add( groupShowJMenuItem );

        JMenuItem groupSlideshowJMenuItem = new JMenuItem( Settings.jpoResources.getString( "groupSlideshowJMenuItem" ) );
        groupSlideshowJMenuItem.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JpoEventBus.getInstance().post( new ShowPictureRequest( popupNode ) );
            }
        } );
        add( groupSlideshowJMenuItem );
        if ( ! popupNode.hasChildPictureNodes() ) {
            groupSlideshowJMenuItem.setEnabled( false);
        }

        JMenuItem groupFindJMenuItem = new JMenuItem( Settings.jpoResources.getString( "groupFindJMenuItemLabel" ) );
        groupFindJMenuItem.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JpoEventBus.getInstance().post( new OpenSearchDialogRequest( popupNode ) );
            }
        } );
        add( groupFindJMenuItem );

        addSeparator();

        if ( popupNode.getPictureCollection().getAllowEdits() ) {

            JMenuItem categoryUsagetJMenuItem = new JMenuItem( Settings.jpoResources.getString( "categoryUsagetJMenuItem" ) );
            categoryUsagetJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    HashSet<SortableDefaultMutableTreeNode> hs = new HashSet<>();
                    hs.add( popupNode );
                    JpoEventBus.getInstance().post( new ShowCategoryUsageEditorRequest( hs ) );
                    //caller.showCategoryUsageGUI( popupNode );
                }
            } );
            add( categoryUsagetJMenuItem );

            JMenuItem groupRefreshJMenuItem = new JMenuItem( Settings.jpoResources.getString( "groupRefreshJMenuItem" ) );
            groupRefreshJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    List<SortableDefaultMutableTreeNode> actionNodes = new ArrayList<>();
                    if ( ( Settings.getPictureCollection().countSelectedNodes() > 0 ) && ( Settings.getPictureCollection().isSelected( popupNode ) ) ) {
                        actionNodes.addAll( Settings.getPictureCollection().getSelectedNodesAsList() );
                    } else {
                        actionNodes.add( popupNode );
                    }

                    JpoEventBus.getInstance().post( new RefreshThumbnailRequest( popupNode, QUEUE_PRIORITY.HIGH_PRIORITY ) );

                }
            } );
            add( groupRefreshJMenuItem );

            addSeparator();

            JMenuItem groupTableJMenuItem = new JMenuItem( Settings.jpoResources.getString( "groupTableJMenuItemLabel" ) );
            groupTableJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new ShowGroupAsTableRequest( popupNode ) );
                }
            } );
            add( groupTableJMenuItem );

            addSeparator();

            //submenu which has several navigation options
            JMenu addGroupJMenu = new JMenu( Settings.jpoResources.getString( "addGroupJMenuLabel" ) );
            add( addGroupJMenu );

            // menu item that allows adding a new blank group
            JMenuItem addNewGroupJMenuItem = new JMenuItem( Settings.jpoResources.getString( "addNewGroupJMenuItemLabel" ) );
            addNewGroupJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new AddEmptyGroupRequest( popupNode ) );
                }
            } );
            addGroupJMenu.add( addNewGroupJMenuItem );

            JMenuItem addPicturesJMenuItem = new JMenuItem( Settings.jpoResources.getString( "addPicturesJMenuItemLabel" ) );
            addPicturesJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new ChooseAndAddPicturesToGroupRequest( popupNode ) );
                }
            } );
            addGroupJMenu.add( addPicturesJMenuItem );

            // Add Collections
            // submenu which offers the choice of either loading from a file or from one of the recent collections
            JMenu addCollectionJMenu = new JMenu( Settings.jpoResources.getString( "addCollectionJMenuItemLabel" ) );
            addGroupJMenu.add( addCollectionJMenu );

            // menu item that allows adding a collection of pictures
            JMenuItem addCollectionFromFile = new JMenuItem( Settings.jpoResources.getString( "addCollectionFormFile" ) );
            addCollectionFromFile.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new ChooseAndAddCollectionRequest( popupNode ) );
                }
            } );
            addCollectionJMenu.add( addCollectionFromFile );

            // add the recently opened files to the menu
            for ( int i = 0; i < Settings.MAX_MEMORISE; i++ ) {
                recentOpenedfileJMenuItem[i] = new JMenuItem();
                final int index = i;  // the anonymous innter class needs a final variable
                recentOpenedfileJMenuItem[i].addActionListener( new ActionListener() {

                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        JpoEventBus.getInstance().post( new AddCollectionToGroupRequest( popupNode, new File( Settings.recentCollections[index] ) ) );
                    }
                } );
                recentOpenedfileJMenuItem[i].setVisible( false );
                addCollectionJMenu.add( recentOpenedfileJMenuItem[i] );
            }
            populateRecentFilesMenuItems();

            // menu item that allows adding from a list of filenames
            JMenuItem addFlatFileJMenuItem = new JMenuItem( Settings.jpoResources.getString( "addFlatFileJMenuItemLabel" ) );
            addFlatFileJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new ChooseAndAddFlatfileRequest( popupNode ) );
                }
            } );
            addGroupJMenu.add( addFlatFileJMenuItem );

            // submenu which has several navigation options
            JMenu moveGroupNodeJMenu = new JMenu( Settings.jpoResources.getString( "moveNodeJMenuLabel" ) );
            add( moveGroupNodeJMenu );

            for ( int i = 0; i < Settings.MAX_DROPNODES; i++ ) {
                final int dropnode = i;
                recentDropNodes[i] = new JMenuItem();
                recentDropNodes[i].addActionListener( new ActionListener() {

                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        popupNode.moveToLastChild( Settings.recentDropNodes[dropnode] );
                        Settings.memorizeGroupOfDropLocation( Settings.recentDropNodes[dropnode] );
                        JpoEventBus.getInstance().post( new RecentDropNodesChangedEvent() );
                    }
                } );
                moveGroupNodeJMenu.add( recentDropNodes[i] );
            }
            moveGroupNodeJMenu.add( movePictureNodeSeparator );
            populateRecentDropNodeMenuItems();

            //menu item that allows move to top op list
            JMenuItem moveGroupToTopJMenuItem = new JMenuItem( Settings.jpoResources.getString( "moveGroupToTopJMenuItem" ) );
            moveGroupToTopJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new MoveNodeToTopRequest( popupNode ) );
                }
            } );
            moveGroupNodeJMenu.add( moveGroupToTopJMenuItem );

            // menu item that allows move up in the list
            JMenuItem moveGroupUpJMenuItem = new JMenuItem( Settings.jpoResources.getString( "moveGroupUpJMenuItem" ) );
            moveGroupUpJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new MoveNodeUpRequest( popupNode ) );
                }
            } );
            moveGroupNodeJMenu.add( moveGroupUpJMenuItem );

            //menu item that allows move up in the list
            JMenuItem moveGroupDownJMenuItem = new JMenuItem( Settings.jpoResources.getString( "moveGroupDownJMenuItem" ) );
            moveGroupDownJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new MoveNodeDownRequest( popupNode ) );
                }
            } );
            moveGroupNodeJMenu.add( moveGroupDownJMenuItem );

            // menu item that allows move to top op list
            JMenuItem moveGroupToBottomJMenuItem = new JMenuItem( Settings.jpoResources.getString( "moveGroupToBottomJMenuItem" ) );
            moveGroupToBottomJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new MoveNodeToBottomRequest( popupNode ) );
                }
            } );
            moveGroupNodeJMenu.add( moveGroupToBottomJMenuItem );
            //menu item that allows indenting the group
            JMenuItem indentJMenuItem = new JMenuItem( Settings.jpoResources.getString( "indentJMenuItem" ) );
            indentJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    popupNode.indentNode();
                }
            } );
            moveGroupNodeJMenu.add( indentJMenuItem );

            // menu item that allows outdenting the group
            JMenuItem outdentJMenuItem = new JMenuItem( Settings.jpoResources.getString( "outdentJMenuItem" ) );
            outdentJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    popupNode.outdentNode();
                }
            } );
            moveGroupNodeJMenu.add( outdentJMenuItem );

            JMenuItem groupRemove = new JMenuItem( Settings.jpoResources.getString( "groupRemoveLabel" ) );
            groupRemove.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent event ) {
                    List<SortableDefaultMutableTreeNode> actionNodes = new ArrayList<>();
                    if ( ( Settings.getPictureCollection().countSelectedNodes() > 0 ) && ( Settings.getPictureCollection().isSelected( popupNode ) ) ) {
                        actionNodes.addAll( Settings.getPictureCollection().getSelectedNodesAsList() );
                    } else {
                        actionNodes.add( popupNode );
                    }

                    JpoEventBus.getInstance().post( new RemoveNodeRequest( actionNodes ) );
                }
            } );
            add( groupRemove );

            addSeparator();

            // menu item that brings a dialog to ask where to consolidate the files to
            JMenuItem consolidateMoveJMenuItem = new JMenuItem( Settings.jpoResources.getString( "consolidateMoveLabel" ) );
            consolidateMoveJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new ConsolidateGroupRequest( popupNode, null ) );
                }
            } );
            add( consolidateMoveJMenuItem );

            addSeparator();
            //submenu which has several sort options
            JMenu sortJMenu = new JMenu( Settings.jpoResources.getString( "sortJMenu" ) );
            add( sortJMenu );
            //requests a sort by Description
            List<SortOption> sortOptions = Settings.getSortOptions();
            final SortOption sortByDescription = sortOptions.get( 1 );
            JMenuItem sortByDescriptionJMenuItem = new JMenuItem( sortByDescription.getDescription() );
            sortByDescriptionJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new SortGroupRequest( popupNode, sortByDescription.getSortCode() ) );
                }
            } );
            sortJMenu.add( sortByDescriptionJMenuItem );

            //requests a sort by Film Reference
            final SortOption sortByFilmReference = sortOptions.get( 2 );
            JMenuItem sortByFilmReferenceJMenuItem = new JMenuItem( sortByFilmReference.getDescription() );
            sortByFilmReferenceJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new SortGroupRequest( popupNode, sortByFilmReference.getSortCode() ) );
                }
            } );
            sortJMenu.add( sortByFilmReferenceJMenuItem );

            //requests a sort by Creation Time
            final SortOption sortByCreationTime = sortOptions.get( 3 );
            JMenuItem sortByCreationTimeJMenuItem = new JMenuItem( sortByCreationTime.getDescription() );
            sortByCreationTimeJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new SortGroupRequest( popupNode, sortByCreationTime.getSortCode() ) );
                }
            } );
            sortJMenu.add( sortByCreationTimeJMenuItem );

            // requests a sort by Comment
            final SortOption sortByComment = sortOptions.get( 4 );
            JMenuItem sortByCommentJMenuItem = new JMenuItem( sortByComment.getDescription() );
            sortByCommentJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new SortGroupRequest( popupNode, sortByComment.getSortCode() ) );
                }
            } );
            sortJMenu.add( sortByCommentJMenuItem );

            // requests a sort by Photographer
            final SortOption sortByPhotographer = sortOptions.get( 5 );
            JMenuItem sortByPhotographerJMenuItem = new JMenuItem( sortByPhotographer.getDescription() );
            sortByPhotographerJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new SortGroupRequest( popupNode, sortByPhotographer.getSortCode() ) );
                }
            } );
            sortJMenu.add( sortByPhotographerJMenuItem );

            // requests a sort by Copyright Holder
            final SortOption sortByCopyrightHolder = sortOptions.get( 6 );
            JMenuItem sortByCopyrightHolderTimeJMenuItem = new JMenuItem( sortByCopyrightHolder.getDescription() );
            sortByCopyrightHolderTimeJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new SortGroupRequest( popupNode, sortByCopyrightHolder.getSortCode() ) );
                }
            } );
            sortJMenu.add( sortByCopyrightHolderTimeJMenuItem );

            addSeparator();
        }

        JMenuItem groupSelectForEmail = new JMenuItem( Settings.jpoResources.getString( "groupSelectForEmail" ) );
        groupSelectForEmail.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JpoEventBus.getInstance().post( new AddGroupToEmailSelectionRequest( popupNode ) );
            }
        } );
        add( groupSelectForEmail );

        JMenuItem groupExportHtml = new JMenuItem( Settings.jpoResources.getString( "groupExportHtmlMenuText" ) );
        groupExportHtml.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JpoEventBus.getInstance().post( new ExportGroupToHtmlRequest( popupNode ) );
            }
        } );
        add( groupExportHtml );

        //  menu item that allows the user to export the group to several different formats
        JMenuItem groupExportNewCollection = new JMenuItem( Settings.jpoResources.getString( "groupExportNewCollectionMenuText" ) );
        groupExportNewCollection.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JpoEventBus.getInstance().post( new ExportGroupToNewCollectionRequest( popupNode ) );
            }
        } );
        add( groupExportNewCollection );

        // menu item that allows the user to export the group to a flat list of filenames
        JMenuItem groupExportFlatFile = new JMenuItem( Settings.jpoResources.getString( "groupExportFlatFileMenuText" ) );
        groupExportFlatFile.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JpoEventBus.getInstance().post( new ExportGroupToFlatFileRequest( popupNode ) );
            }
        } );
        add( groupExportFlatFile );

        // menu item that allows the user to upload the group to Picasa
        JMenuItem groupExportPicasa = new JMenuItem( "Export to Picasa" );
        groupExportPicasa.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JpoEventBus.getInstance().post( new ExportGroupToPicasaRequest( popupNode ) );
            }
        } );
        add( groupExportPicasa );

        addSeparator();

        JMenuItem groupEditJMenuItem = new JMenuItem( Settings.jpoResources.getString( "groupEditJMenuItem" ) );
        groupEditJMenuItem.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JpoEventBus.getInstance().post( new ShowGroupInfoEditorRequest( popupNode ) );
            }
        } );
        add( groupEditJMenuItem );

    }

    /**
     * Here we receive notification that the drop nodes have been updated. We 
     * then populate the Move submenu with the current drop nodes.
     */
    private class RecentDropNodeChangedEventHandler {

        /**
         * Handle the event by updating the submenu items
         *
         * @param event event
         */
        @Subscribe
        public void handleRecentDropNodeChangedEventHandler( RecentDropNodesChangedEvent event ) {
            SwingUtilities.invokeLater( new Runnable() {

                @Override
                public void run() {
                    populateRecentDropNodeMenuItems();
                }
            } );

        }
    }

    /**
     * Populates the Move menu with the recent drop nodes of the application. 
     * If there are no recent drop nodes the list is empty.
     */
    public void populateRecentDropNodeMenuItems() {
        boolean dropNodesVisible = false;
        for ( int i = 0; i < Settings.MAX_DROPNODES; i++ ) {
            if ( ( Settings.recentDropNodes[i] != null ) ) {
                recentDropNodes[i].setText( "To Group: " + Settings.recentDropNodes[i].toString() );
                recentDropNodes[i].setVisible( true );
                dropNodesVisible = true;
            } else {
                recentDropNodes[i].setVisible( false );
            }
        }
        if ( dropNodesVisible ) {
            movePictureNodeSeparator.setVisible( true );
        } else {
            movePictureNodeSeparator.setVisible( false );
        }
    }

    /**
     * Receives an Event when the RecentCollections array has been modified. 
     * Ensures that the menu entries are updated in line with the new recent collections.
     */
    private class RecentCollectionsChangedEventHandler {

        /**
         * Handle the event by updating the submenu items
         *
         * @param event event
         */
        @Subscribe
        public void handleRecentCollectionsChangedEvent( RecentCollectionsChangedEvent event ) {
            SwingUtilities.invokeLater( new Runnable() {

                @Override
                public void run() {
                    populateRecentFilesMenuItems();
                }
            } );

        }
    }

    /**
     * Sets up the menu entries in the Add &gt; Collection sub menu from the
     * recentCollections in the application. Has to be run on the EDT.
     */
    public void populateRecentFilesMenuItems() {
        Tools.checkEDT();
        for ( int i = 0; i < Settings.recentCollections.length; i++ ) {
            if ( Settings.recentCollections[i] != null ) {
                recentOpenedfileJMenuItem[i].setText( Integer.toString( i + 1 ) + ": " + Settings.recentCollections[i] );
                recentOpenedfileJMenuItem[i].setVisible( true );
            } else {
                recentOpenedfileJMenuItem[i].setVisible( false );
            }
        }
    }
}
