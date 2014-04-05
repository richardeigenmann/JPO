package jpo.gui;

import com.google.common.eventbus.Subscribe;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import jpo.dataModel.NodeNavigatorInterface;
import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.PictureInfo;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import jpo.EventBus.ConsolidateGroupRequest;
import jpo.EventBus.CopyLocationsChangedEvent;
import jpo.EventBus.GroupSelectionEvent;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.MoveNodeDownRequest;
import jpo.EventBus.MoveNodeToBottomRequest;
import jpo.EventBus.MoveNodeToNodeRequest;
import jpo.EventBus.MoveNodeToTopRequest;
import jpo.EventBus.MoveNodeUpRequest;
import jpo.EventBus.RecentDropNodesChangedEvent;
import jpo.EventBus.RefreshThumbnailRequest;
import jpo.EventBus.RenamePictureRequest;
import jpo.EventBus.ResetPictureRotationRequest;
import jpo.EventBus.RotatePictureRequest;
import jpo.EventBus.ShowCategoryUsageEditorRequest;
import jpo.EventBus.ShowGroupRequest;
import jpo.EventBus.ShowPictureInfoEditorRequest;
import jpo.EventBus.ShowPictureOnMapRequest;
import jpo.EventBus.ShowPictureRequest;
import jpo.EventBus.UserFunctionsChangedEvent;

/*
 PicturePopupMenu.java:  a popup menu for pictures

 Copyright (C) 2002 - 2014  Richard Eigenmann.
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
 * This class generates a popup menu on a picture node.
 *
 */
public class PicturePopupMenu extends JPopupMenu {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( PicturePopupMenu.class.getName() );

    /**
     * array of menu items that allows the user to call up a user function
     *
     *
     */
    private final JMenuItem[] userFunctionsJMenuItems = new JMenuItem[Settings.maxUserFunctions];

    /**
     * array of menu items that allows the user to copy the picture to a
     * memorised file location
     *
     *
     */
    private final JMenuItem[] copyLocationsJMenuItems = new JMenuItem[Settings.MAX_MEMORISE];
    /**
     * array of menu items that allows the user to copy pictures to a recently
     * used zip file
     *
     *
     */
    private final JMenuItem[] memorizedZipFileJMenuItems = new JMenuItem[Settings.MAX_MEMORISE];

    /**
     * a separator for the Move menu. Declared here because other class methods
     * want to turn on and off visible.
     */
    private final JSeparator movePictureNodeSeparator = new JSeparator();

    /**
     * This array of JMenuItems memorises the most recent drop locations and
     * allows The user to quickly select a recently used drop location for the
     * next drop.
     */
    private final JMenuItem[] recentDropNodes = new JMenuItem[Settings.MAX_DROPNODES];

    /**
     * Constructor for the PicturePopupMenu where we do have a
     * {@link PictureViewer} that should receive the picture. TODO: Fix the way
     * this is being called because the whole business of figuring out whether
     * this is a single node or multi node is silly TODO: Decouple the
     * presentation from the functionality
     *
     * @param setOfNodes The set of nodes from which the popup picture is coming
     * @param idx	The picture of the set for which the popup is being shown.
     */
    public PicturePopupMenu( NodeNavigatorInterface setOfNodes, int idx ) {
        this.mySetOfNodes = setOfNodes;
        this.index = idx;
        this.popupNode = mySetOfNodes.getNode( index );
        JpoEventBus.getInstance().register( new RecentDropNodeChangedEventHandler() );
        JpoEventBus.getInstance().register( new CopyLocationsChangedEventHandler() );
        JpoEventBus.getInstance().register( new UserFunctionsChangedEventHandler() );

        initComponents();
    }

    private void initComponents() {
        String title = getTitle();
        setLabel( title );
        JMenuItem menuTitle = new JMenuItem( title );
        menuTitle.setEnabled( false );
        add( menuTitle );
        addSeparator();

        add( new ShowPictureMenuItem() );
        add( new ShowMapMenuItem() );
        add( new NavigateToMenu() );
        add( new ShowCategoryUsageJMenuItemMenuItem() );

        JMenuItem pictureMailSelectJMenuItem = new JMenuItem();
        JMenuItem pictureMailUnselectAllJMenuItem = new JMenuItem( Settings.jpoResources.getString( "pictureMailUnselectAllJMenuItem" ) );
        if ( popupNode.getPictureCollection().isMailSelected( popupNode ) ) {
            pictureMailSelectJMenuItem.setText( Settings.jpoResources.getString( "pictureMailUnselectJMenuItem" ) );
            pictureMailSelectJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    requestSelectForEmail();

                }
            } );
            add( pictureMailSelectJMenuItem );

            pictureMailUnselectAllJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    doClearMailSelection();
                }
            } );
            add( pictureMailUnselectAllJMenuItem );
        } else {
            pictureMailSelectJMenuItem.setText( Settings.jpoResources.getString( "pictureMailSelectJMenuItem" ) );
            pictureMailSelectJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    addToMailSelection();
                }
            } );
            add( pictureMailSelectJMenuItem );
        }

        JMenu userFunctionsJMenu = new JMenu( Settings.jpoResources.getString( "userFunctionsJMenu" ) );
        for ( int i = 0; i < Settings.maxUserFunctions; i++ ) {
            final int userfunction = i;
            userFunctionsJMenuItems[i] = new JMenuItem();
            userFunctionsJMenuItems[i].addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    try {
                        Tools.runUserFunction( userfunction, (PictureInfo) popupNode.getUserObject() );
                    } catch ( ClassCastException | NullPointerException x ) {
                        LOGGER.severe( x.getMessage() );
                    }
                }
            } );
            userFunctionsJMenu.add( userFunctionsJMenuItems[i] );
        }
        add( userFunctionsJMenu );
        userFunctionsChanged();

        if ( popupNode.getPictureCollection().getAllowEdits() ) {
            add( new RotationSubmenu() );
        }

        JMenuItem pictureRefreshJMenuItem = new JMenuItem( Settings.jpoResources.getString( "pictureRefreshJMenuItem" ) );
        pictureRefreshJMenuItem.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                refreshSelectedPictures();
            }
        } );
        add( pictureRefreshJMenuItem );

        if ( popupNode.getPictureCollection().getAllowEdits() ) {
            add( new MoveSubmenu() );
        }

        add( new CopySubmenu() );

        // remove node
        if ( popupNode.getPictureCollection().getAllowEdits() ) {
            JMenuItem pictureNodeRemove = new JMenuItem( Settings.jpoResources.getString( "pictureNodeRemove" ) );
            pictureNodeRemove.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    requestRemoveNode();

                }
            } );
            add( pictureNodeRemove );

            JMenu fileOperationsJMenu = new JMenu( Settings.jpoResources.getString( "FileOperations" ) );
            add( fileOperationsJMenu );

            JMenuItem fileRenameJMenuItem = new JMenuItem( Settings.jpoResources.getString( "fileRenameJMenuItem" ) );
            fileRenameJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
                        JpoEventBus.getInstance().post( new RenamePictureRequest( popupNode ) );
                        //TreeNodeController.fileRename( popupNode );
                    } else {
                        for ( SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes() ) {
                            if ( selectedNode.getUserObject() instanceof PictureInfo ) {
                                JpoEventBus.getInstance().post( new RenamePictureRequest( selectedNode ) );
                                //TreeNodeController.fileRename( selectedNode );
                            }
                        }
                    }
                }
            } );
            fileOperationsJMenu.add( fileRenameJMenuItem );

            // Delete
            JMenuItem fileDeleteJMenuItem = new JMenuItem( Settings.jpoResources.getString( "fileDeleteJMenuItem" ) );
            fileDeleteJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    requestDelete();
                }
            } );
            fileOperationsJMenu.add( fileDeleteJMenuItem );
        }

        add( new ShowPictureInfoEditorMenuItem() );

        JMenuItem consolidateHereMenuItem = new JMenuItem( "Consolidate Here" );
        consolidateHereMenuItem.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JpoEventBus.getInstance().post(
                        new ConsolidateGroupRequest(
                                (SortableDefaultMutableTreeNode) popupNode.getParent(),
                                ( (PictureInfo) popupNode.getUserObject() ).getHighresFile().getParentFile() )
                );
            }
        } );
        add( consolidateHereMenuItem );

    }

    private String getTitle() {
        String title = "Picture Popup Menu";
        if ( ( Settings.pictureCollection.countSelectedNodes() > 1 ) && ( Settings.pictureCollection.isSelected( popupNode ) ) ) {
            title = String.format( "%d nodes", Settings.pictureCollection.countSelectedNodes() );
        } else {
            Object uo = popupNode.getUserObject();
            if ( uo instanceof PictureInfo ) {
                title = ( (PictureInfo) uo ).getDescription();
            }
            // trim title length to 25 characters if longer
            if ( title.length() > 25 ) {
                title = title.substring( 0, 25 ) + "...";
            }
        }
        return title;
    }

    /**
     * Request to remove a node. What happens depends on the selection: If the
     * node on which the popup was performed is one of a selection of nodes, the
     * multi-delete dialog is opened. If, however it is not part of a
     * potentially existing selection then only the specified node will be
     * deleted.
     */
    private void requestRemoveNode() {
        SortableDefaultMutableTreeNode actionNode = mySetOfNodes.getNode( index );
        if ( ( Settings.pictureCollection.countSelectedNodes() > 1 ) && ( Settings.pictureCollection.isSelected( actionNode ) ) ) {
            for ( SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes() ) {
                if ( selectedNode.getUserObject() instanceof PictureInfo ) {
                    selectedNode.deleteNode();
                }
            }
        } else {
            popupNode.deleteNode();
        }
    }

    /**
     * the Delete menu was clicked. What happens depends on the selection: If
     * the node on which the popup was performed is one of a selection of nodes,
     * the multi-delete dialog is opened. If, however it is not part of a
     * potentially existing selection then only the specified node will be
     * deleted.
     */
    private void requestDelete() {
        SortableDefaultMutableTreeNode actionNode = mySetOfNodes.getNode( index );
        if ( ( Settings.pictureCollection.countSelectedNodes() > 1 ) && ( Settings.pictureCollection.isSelected( actionNode ) ) ) {
            multiDeleteDialog();
        } else {
            fileDelete( actionNode );
        }
    }

    /**
     * Brings up an are you sure dialog and then deletes the file.
     *
     * @param nodeToDelete The node to be deleted
     */
    public void fileDelete( SortableDefaultMutableTreeNode nodeToDelete ) {
        Object userObj = nodeToDelete.getUserObject();
        if ( !( userObj instanceof PictureInfo ) ) {
            return;
        }

        PictureInfo pi = (PictureInfo) userObj;
        File highresFile = pi.getHighresFile();
        if ( highresFile == null ) {
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                Settings.anchorFrame,
                Settings.jpoResources.getString( "FileDeleteLabel" ) + highresFile.toString() + "\n" + Settings.jpoResources.getString( "areYouSure" ),
                Settings.jpoResources.getString( "FileDeleteTitle" ),
                JOptionPane.OK_CANCEL_OPTION );

        if ( option == 0 ) {
            boolean ok = false;
            File lowresFile = pi.getLowresFile();
            if ( ( lowresFile != null ) && ( lowresFile.exists() ) ) {
                ok = lowresFile.delete();
                if ( !ok ) //logger.info("File deleted: " + lowresFile.toString() );
                // else
                {
                    LOGGER.log( Level.INFO, "File deleted failed on: {0}", lowresFile.toString() );
                }
            }

            if ( highresFile.exists() ) {
                ok = highresFile.delete();
                if ( !ok ) //logger.info("File deleted: " + highresFile.toString() );
                //else
                {
                    LOGGER.log( Level.INFO, "File deleted failed on: {0}", highresFile.toString() );
                }
            }

            nodeToDelete.deleteNode();

            if ( !ok ) {
                JOptionPane.showMessageDialog( Settings.anchorFrame,
                        Settings.jpoResources.getString( "fileDeleteError" ) + highresFile.toString(),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    /**
     * Multi delete dialog
     */
    private void multiDeleteDialog() {
        JTextArea textArea = new JTextArea();
        textArea.setText( "" );
        for ( SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes() ) {
            if ( selectedNode.getUserObject() instanceof PictureInfo ) {
                textArea.append( ( (PictureInfo) selectedNode.getUserObject() ).getHighresLocation() + "\n" );
            }
        }
        textArea.append( Settings.jpoResources.getString( "areYouSure" ) );

        int option = JOptionPane.showConfirmDialog(
                Settings.anchorFrame, //very annoying if the main window is used as it forces itself into focus.
                textArea,
                Settings.jpoResources.getString( "FileDeleteLabel" ),
                JOptionPane.OK_CANCEL_OPTION );

        if ( option == 0 ) {
            for ( SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes() ) {
                PictureInfo pi;
                if ( selectedNode.getUserObject() instanceof PictureInfo ) {
                    pi = (PictureInfo) selectedNode.getUserObject();
                    boolean ok = false;
                    File lowresFile = pi.getLowresFile();
                    if ( ( lowresFile != null ) && ( lowresFile.exists() ) ) {
                        ok = lowresFile.delete();
                        if ( !ok ) {
                            LOGGER.info( "File deleted failed on: " + lowresFile.toString() );
                        }
                    }

                    File highresFile = pi.getHighresFile();
                    if ( highresFile.exists() ) {
                        ok = highresFile.delete();
                        if ( !ok ) {
                            LOGGER.info( "File deleted failed on: " + highresFile.toString() );
                        }
                    }

                    selectedNode.deleteNode();

                    if ( !ok ) {
                        JOptionPane.showMessageDialog( Settings.anchorFrame,
                                Settings.jpoResources.getString( "fileDeleteError" ) + highresFile.toString(),
                                Settings.jpoResources.getString( "genericError" ),
                                JOptionPane.ERROR_MESSAGE );
                    }
                }
            }
            Settings.pictureCollection.clearSelection();
        }

    }
    //  Controller type Stuff
    /**
     * The node the popup menu was created for
     */
    private final SortableDefaultMutableTreeNode popupNode;
    /**
     * Reference to the {@link NodeNavigatorInterface} which indicates the nodes
     * being displayed.
     */
    private NodeNavigatorInterface mySetOfNodes = null;
    /**
     * Index of the {@link #mySetOfNodes} being popped up.
     */
    private int index = 0;

    /**
     * handle the Categories click
     */
    private void requestCategories() {
        if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
            HashSet<SortableDefaultMutableTreeNode> hs = new HashSet<>();
            hs.add( popupNode );
            JpoEventBus.getInstance().post( new ShowCategoryUsageEditorRequest( hs ) );
        } else {
            if ( !Settings.pictureCollection.isSelected( popupNode ) ) {
                Settings.pictureCollection.clearSelection();
                HashSet<SortableDefaultMutableTreeNode> hs = new HashSet<>();
                hs.add( popupNode );
                JpoEventBus.getInstance().post( new ShowCategoryUsageEditorRequest( hs ) );
            } else {
                HashSet<SortableDefaultMutableTreeNode> hs = new HashSet<>( Arrays.asList( Settings.pictureCollection.getSelectedNodes() ) );
                JpoEventBus.getInstance().post( new ShowCategoryUsageEditorRequest( hs ) );
            }
        }
    }

    /**
     * request to select for Email
     */
    private void requestSelectForEmail() {
        if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
            popupNode.getPictureCollection().removeFromMailSelection( popupNode );
        } else {
            for ( SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes() ) {
                if ( selectedNode.getUserObject() instanceof PictureInfo ) {
                    selectedNode.getPictureCollection().removeFromMailSelection( selectedNode );
                }
            }
        }
    }

    /**
     * Handler for the RecentDropNodeChangedEvent
     */
    private class RecentDropNodeChangedEventHandler {

        /**
         * Handle the event by updating the submenu items
         *
         * @param event
         */
        @Subscribe
        public void handleRecentDropNodeChangedEventHandler( RecentDropNodesChangedEvent event ) {
            SwingUtilities.invokeLater( new Runnable() {

                @Override
                public void run() {
                    recentDropNodesChanged();
                }
            } );

        }
    }

    /**
     * Here we receive notification that the nodes have been updated. The method
     * then updates the menu items for the drop targets. Those that are null are
     * not shown. If no drop targets are shown at all the separator in the
     * submenu is not shown either.
     */
    public void recentDropNodesChanged() {
        boolean dropNodesVisible = false;
        for ( int i = 0; i < Settings.MAX_DROPNODES; i++ ) {
            if ( Settings.recentDropNodes[i] != null ) {
                recentDropNodes[i].setText(
                        Settings.jpoResources.getString( "recentDropNodePrefix" ) + Settings.recentDropNodes[i].toString() );
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
     * Handler for the CopyLocationsChangedEvent
     */
    private class CopyLocationsChangedEventHandler {

        /**
         * Handle the event by updating the submenu items
         *
         * @param event
         */
        @Subscribe
        public void handleCopyLocationsChangedEvent( CopyLocationsChangedEvent event ) {
            SwingUtilities.invokeLater( new Runnable() {

                @Override
                public void run() {
                    copyLocationsChanged();
                }
            } );

        }
    }

    /**
     * Here we receive notification that the copy locations were updated and
     * then go and update the targets on the menu.
     */
    public void copyLocationsChanged() {
        for ( int i = 0; i < Settings.copyLocations.length; i++ ) {
            if ( Settings.copyLocations[i] != null ) {
                copyLocationsJMenuItems[i].setText( Settings.copyLocations[i] );
                copyLocationsJMenuItems[i].setVisible( true );
            } else {
                copyLocationsJMenuItems[i].setVisible( false );
            }
        }
    }

    /**
     * Handler for the UserFunctionsChangedEvent
     */
    private class UserFunctionsChangedEventHandler {

        /**
         * Handle the event by updating the submenu items
         *
         * @param event
         */
        @Subscribe
        public void handleUserFunctionsChangedEvent( UserFunctionsChangedEvent event ) {
            SwingUtilities.invokeLater( new Runnable() {

                @Override
                public void run() {
                    userFunctionsChanged();
                }
            } );

        }
    }

    /**
     * This method populates the user functions sub entries on the menu. It is
     * called by a listener when user functions are added.
     *
     */
    public void userFunctionsChanged() {
        for ( int i = 0; i < Settings.maxUserFunctions; i++ ) {
            if ( ( Settings.userFunctionNames[i] != null ) && ( Settings.userFunctionNames[i].length() > 0 ) && ( Settings.userFunctionCmd[i] != null ) && ( Settings.userFunctionCmd[i].length() > 0 ) ) {
                userFunctionsJMenuItems[i].setText( Settings.userFunctionNames[i] );
                userFunctionsJMenuItems[i].setVisible( true );
            } else {
                userFunctionsJMenuItems[i].setVisible( false );
            }
        }
    }

    /**
     * Clears the set of images selected for emailing
     */
    private void doClearMailSelection() {
        popupNode.getPictureCollection().clearMailSelection();
    }

    /**
     * Adds a picture to the selection of pictures to be mailed.
     */
    private void addToMailSelection() {
        if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
            popupNode.getPictureCollection().addToMailSelection( popupNode );
        } else {
            for ( SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes() ) {
                if ( selectedNode.getUserObject() instanceof PictureInfo ) {
                    selectedNode.getPictureCollection().addToMailSelection( selectedNode );
                }
            }
        }
    }

    /**
     * Refresh the selected pictures
     */
    private void refreshSelectedPictures() {
        if ( ! Settings.pictureCollection.isSelected( popupNode ) ) {
            JpoEventBus.getInstance().post( new RefreshThumbnailRequest( popupNode, ThumbnailQueueRequest.HIGH_PRIORITY ) );
        } else {
            JpoEventBus.getInstance().post( new RefreshThumbnailRequest( Settings.pictureCollection.getSelectedNodesAsArrayList(), ThumbnailQueueRequest.HIGH_PRIORITY ) );
        }

    }

    /**
     * This method moves the popup node or the selection (if the popup node is
     * part of the selection) to the end of the picked node:
     *
     * TODO: This is "Controller Stuff" and should be handled in a different
     * class
     *
     * @param targetNode To selected node where the node should go
     */
    private void moveToLastChild( SortableDefaultMutableTreeNode targetNode ) {

        if ( ( Settings.pictureCollection.countSelectedNodes() > 0 ) && ( Settings.pictureCollection.isSelected( popupNode ) ) ) {
            // move the selected nodes and then unselect them
            LOGGER.info( "Moving the selection." );
            for ( SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes() ) {
                if ( selectedNode.getUserObject() instanceof PictureInfo ) {
                    selectedNode.moveToLastChild( targetNode );
                }
            }

            Settings.pictureCollection.clearSelection();
        } else {
            // move only the popup node
            popupNode.moveToLastChild( targetNode );
        }

    }

    private class ShowPictureMenuItem extends JMenuItem {

        public ShowPictureMenuItem() {
            setText( Settings.jpoResources.getString( "pictureShowJMenuItemLabel" ) );
            addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new ShowPictureRequest( popupNode ) );
                }
            } );
        }
    }

    private class ShowMapMenuItem extends JMenuItem {

        public ShowMapMenuItem() {

            setText( Settings.jpoResources.getString( "mapShowJMenuItemLabel" ) );
            addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new ShowPictureOnMapRequest( popupNode ) );

                }
            } );
        }
    }

    private class ShowPictureInfoEditorMenuItem extends JMenuItem {

        public ShowPictureInfoEditorMenuItem() {
            setText( Settings.jpoResources.getString( "pictureEditJMenuItemLabel" ) );
            addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new ShowPictureInfoEditorRequest( popupNode ) );
                }
            } );

        }
    }

    /**
     * Builds a submenu with the parent nodes of the popupNode and allows them
     * to be clicked and navigated to.
     */
    private class NavigateToMenu extends JMenu {

        public NavigateToMenu() {
            SortableDefaultMutableTreeNode[] parentNodes = Settings.pictureCollection.findParentGroups( popupNode );
            setText( Settings.jpoResources.getString( "navigationJMenu" ) );
            for ( SortableDefaultMutableTreeNode parentNode : parentNodes ) {
                JMenuItem navigateToRootNode = new JMenuItem( parentNode.getUserObject().toString() );
                final SortableDefaultMutableTreeNode targetNode = parentNode;
                navigateToRootNode.addActionListener( new ActionListener() {

                    final SortableDefaultMutableTreeNode node = targetNode;

                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        JpoEventBus.getInstance().post( new ShowGroupRequest( node ) );

                    }
                } );
                add( navigateToRootNode );
            }
        }
    }

    private class ShowCategoryUsageJMenuItemMenuItem extends JMenuItem {

        public ShowCategoryUsageJMenuItemMenuItem() {

            setText( Settings.jpoResources.getString( "categoryUsagetJMenuItem" ) );
            addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    requestCategories();
                }
            } );
        }
    }

    /**
     * Builds a submenu with the options and actions to rotate the picture node
     */
    private class RotationSubmenu extends JMenu {

        public RotationSubmenu() {
            setText( Settings.jpoResources.getString( "rotation" ) );
            JMenuItem rotate90JMenuItem = new JMenuItem( Settings.jpoResources.getString( "rotate90" ) );
            rotate90JMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new RotatePictureRequest( popupNode, 90, ThumbnailQueueRequest.HIGH_PRIORITY ) );
                }
            } );
            JMenuItem rotate180JMenuItem = new JMenuItem( Settings.jpoResources.getString( "rotate180" ) );
            rotate180JMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new RotatePictureRequest( popupNode, 180, ThumbnailQueueRequest.HIGH_PRIORITY ) );
                }
            } );
            JMenuItem rotate270JMenuItem = new JMenuItem( Settings.jpoResources.getString( "rotate270" ) );
            rotate270JMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new RotatePictureRequest( popupNode, 270, ThumbnailQueueRequest.HIGH_PRIORITY ) );
                }
            } );
            JMenuItem rotate0JMenuItem = new JMenuItem( Settings.jpoResources.getString( "rotate0" ) );
            rotate0JMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JpoEventBus.getInstance().post( new ResetPictureRotationRequest( popupNode, ThumbnailQueueRequest.HIGH_PRIORITY ) );
                }
            } );
            add( rotate90JMenuItem );
            add( rotate180JMenuItem );
            add( rotate270JMenuItem );
            add( rotate0JMenuItem );
        }
    }

    private class MoveSubmenu extends JMenu {

        public MoveSubmenu() {
            setText( Settings.jpoResources.getString( "moveNodeJMenuLabel" ) );

            // ToDo: We could try to suppress the current group in the list of drop nodes as it doesn't make sense to move to itself
            for ( int i = 0; i < Settings.MAX_DROPNODES; i++ ) {
                final int dropnode = i;
                recentDropNodes[i] = new JMenuItem();
                recentDropNodes[i].addActionListener( new ActionListener() {

                    /**
                     * Moves the selected nodes to the picked destination. If no
                     * nodes are in the selection then the popup node is moved.
                     * If the node for the popup is not in the selection then
                     * this node only is moved. After the move of the selected
                     * nodes they are cleared.
                     *
                     * @param event
                     */
                    @Override
                    public void actionPerformed( ActionEvent event ) {
                        SortableDefaultMutableTreeNode targetNode = Settings.recentDropNodes[dropnode];
                        ArrayList<SortableDefaultMutableTreeNode> movingNodes = new ArrayList<>();
                        if ( ( Settings.pictureCollection.countSelectedNodes() > 0 ) && ( Settings.pictureCollection.isSelected( popupNode ) ) ) {
                            movingNodes.addAll( Settings.pictureCollection.getSelectedNodesAsArrayList() );
                            Settings.pictureCollection.clearSelection();
                        } else {
                            movingNodes.add( popupNode );
                        }
                        JpoEventBus.getInstance().post( new MoveNodeToNodeRequest( movingNodes, targetNode ) );

                        Settings.memorizeGroupOfDropLocation( Settings.recentDropNodes[dropnode] );
                        JpoEventBus.getInstance().post( new RecentDropNodesChangedEvent() );
                    }
                } );
                add( recentDropNodes[i] );
            }
            add( movePictureNodeSeparator );
            recentDropNodesChanged();

            JMenuItem movePictureToTopJMenuItem = new JMenuItem( Settings.jpoResources.getString( "movePictureToTopJMenuItem" ) );
            movePictureToTopJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent event ) {
                    if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
                        JpoEventBus.getInstance().post( new MoveNodeToTopRequest( popupNode ) );
                    } else {
                        for ( SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes() ) {
                            if ( selectedNode.getUserObject() instanceof PictureInfo ) {
                                JpoEventBus.getInstance().post( new MoveNodeToTopRequest( selectedNode ) );
                            }
                        }
                    }
                }
            } );
            add( movePictureToTopJMenuItem );

            JMenuItem movePictureUpJMenuItem = new JMenuItem( Settings.jpoResources.getString( "movePictureUpJMenuItem" ) );
            movePictureUpJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
                        JpoEventBus.getInstance().post( new MoveNodeUpRequest( popupNode ) );
                    } else {
                        for ( SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes() ) {
                            if ( selectedNode.getUserObject() instanceof PictureInfo ) {
                                JpoEventBus.getInstance().post( new MoveNodeUpRequest( selectedNode ) );
                            }
                        }
                    }
                }
            } );
            add( movePictureUpJMenuItem );

            JMenuItem movePictureDownJMenuItem = new JMenuItem( Settings.jpoResources.getString( "movePictureDownJMenuItem" ) );
            movePictureDownJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
                        JpoEventBus.getInstance().post( new MoveNodeDownRequest( popupNode ) );
                    } else {
                        for ( SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes() ) {
                            if ( selectedNode.getUserObject() instanceof PictureInfo ) {
                                JpoEventBus.getInstance().post( new MoveNodeDownRequest( selectedNode ) );
                                selectedNode.moveNodeDown();
                            }
                        }
                    }
                }
            } );
            add( movePictureDownJMenuItem );

            JMenuItem movePictureToBottomJMenuItem = new JMenuItem( Settings.jpoResources.getString( "movePictureToBottomJMenuItem" ) );
            movePictureToBottomJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
                        JpoEventBus.getInstance().post( new MoveNodeToBottomRequest( popupNode ) );
                    } else {
                        for ( SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes() ) {
                            if ( selectedNode.getUserObject() instanceof PictureInfo ) {
                                JpoEventBus.getInstance().post( new MoveNodeToBottomRequest( selectedNode ) );
                            }
                        }
                    }
                }
            } );
            add( movePictureToBottomJMenuItem );

            JMenuItem indentJMenuItem = new JMenuItem( Settings.jpoResources.getString( "indentJMenuItem" ) );
            indentJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
                        popupNode.indentNode();
                    } else {
                        for ( SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes() ) {
                            if ( selectedNode.getUserObject() instanceof PictureInfo ) {
                                selectedNode.indentNode();
                            }
                        }
                    }
                }
            } );
            add( indentJMenuItem );

            JMenuItem outdentJMenuItem = new JMenuItem( Settings.jpoResources.getString( "outdentJMenuItem" ) );
            outdentJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
                        popupNode.outdentNode();
                    } else {
                        for ( SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes() ) {
                            if ( selectedNode.getUserObject() instanceof PictureInfo ) {
                                selectedNode.outdentNode();
                            }
                        }
                    }
                }
            } );
            add( outdentJMenuItem );
        }

    }

    private class CopySubmenu extends JMenu {

        public CopySubmenu() {
            setText( Settings.jpoResources.getString( "copyImageJMenuLabel" ) );

            final JMenuItem copyToNewLocationJMenuItem = new JMenuItem( Settings.jpoResources.getString( "copyToNewLocationJMenuItem" ) );
            copyToNewLocationJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
                        SortableDefaultMutableTreeNode[] nodes = new SortableDefaultMutableTreeNode[1];
                        nodes[0] = popupNode;
                        TreeNodeController.copyToNewLocation( nodes );
                    } else {
                        TreeNodeController.copyToNewLocation( Settings.pictureCollection.getSelectedNodes() );
                    }
                }
            } );
            add( copyToNewLocationJMenuItem );

            addSeparator();

            for ( int i = 0; i < Settings.MAX_MEMORISE; i++ ) {
                final int item = i;
                copyLocationsJMenuItems[i] = new JMenuItem();
                copyLocationsJMenuItems[i].addActionListener( new ActionListener() {

                    @Override
                    public void actionPerformed( ActionEvent ae ) {
                        if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
                            SortableDefaultMutableTreeNode[] nodes = new SortableDefaultMutableTreeNode[1];
                            nodes[0] = popupNode;
                            TreeNodeController.copyToLocation( nodes, new File( Settings.copyLocations[item] ) );
                        } else {
                            TreeNodeController.copyToLocation( Settings.pictureCollection.getSelectedNodes(), new File( Settings.copyLocations[item] ) );
                        }
                    }
                } );
                add( copyLocationsJMenuItems[i] );
            }
            copyLocationsChanged();

            addSeparator();

            final JMenuItem copyToNewZipfileJMenuItem = new JMenuItem( Settings.jpoResources.getString( "copyToNewZipfileJMenuItem" ) );
            copyToNewZipfileJMenuItem.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
                        SortableDefaultMutableTreeNode[] nodes = new SortableDefaultMutableTreeNode[1];
                        nodes[0] = popupNode;
                        TreeNodeController.copyToNewZipfile( nodes );
                    } else {
                        TreeNodeController.copyToNewZipfile( Settings.pictureCollection.getSelectedNodes() );
                    }
                }
            } );
            add( copyToNewZipfileJMenuItem );

            for ( int i = 0; i < Settings.MAX_MEMORISE; i++ ) {
                final int item = i;
                memorizedZipFileJMenuItems[i] = new JMenuItem();
                memorizedZipFileJMenuItems[i].addActionListener( new ActionListener() {

                    @Override
                    public void actionPerformed( ActionEvent ae ) {
                        if ( Settings.pictureCollection.countSelectedNodes() < 1 ) {
                            SortableDefaultMutableTreeNode[] nodes = new SortableDefaultMutableTreeNode[1];
                            nodes[0] = popupNode;
                            TreeNodeController.copyToZipfile( nodes, new File( Settings.memorizedZipFiles[item] ) );
                        } else {
                            TreeNodeController.copyToZipfile( Settings.pictureCollection.getSelectedNodes(), new File( Settings.memorizedZipFiles[item] ) );
                        }
                    }
                } );
                add( memorizedZipFileJMenuItems[i] );
                if ( Settings.memorizedZipFiles[i] != null ) {
                    memorizedZipFileJMenuItems[i].setText( Settings.memorizedZipFiles[i] );
                    memorizedZipFileJMenuItems[i].setVisible( true );
                } else {
                    memorizedZipFileJMenuItems[i].setVisible( false );
                }
            }
        }
    }

}
