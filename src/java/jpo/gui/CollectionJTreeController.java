package jpo.gui;

import jpo.gui.swing.QueryJFrame;
import jpo.gui.swing.GroupPopupMenu;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import jpo.dataModel.*;
import jpo.export.GenerateWebsiteWizard;
import jpo.export.PicasaUploadRequest;
import jpo.export.PicasaUploaderWizard;
import jpo.gui.swing.CollectionJTree;

/*
 * CollectionJTreeController.java: class that manages a JTree for the collection
 *
 * Copyright (C) 2002 - 2014 Richard Eigenmann, Zurich, Switzerland This
 * program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA. The license is in gpl.txt. See
 * http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 * The is one of the main classes in the JPO application as it manages the JTree
 * that deals with most of the logic surrounding the collection and the user
 * interactions with it.
 */
public class CollectionJTreeController
        implements
        GroupPopupInterface {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( CollectionJTreeController.class.getName() );

    /**
     * reference to the main collection controller so that we can delegate stuff
     * to
     */
    private final ApplicationEventHandler applicationEventHandler;

    /**
     * The Controller class for the JTree. This class no longer extends the
     * JTree. Instead it is a plain simple old class that does all the things to
     * respond to or instruct the JTree to do and show.
     *
     * @param applicationEventHandler the reference to the collection controller
     */
    public CollectionJTreeController(
            ApplicationEventHandler applicationEventHandler ) {
        Tools.checkEDT();
        this.applicationEventHandler = applicationEventHandler;

        collectionJTree.setModel( Settings.pictureCollection.getTreeModel() );
        collectionJTree.setEditable( true ); // doing this in the controller as it might not always be desired (like in the CameraDownloadWizard)
        collectionJTree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        collectionJTree.setTransferHandler( new MyTransferHandler() );
        collectionJTree.setDragEnabled( true );
        collectionJTree.setDropMode( DropMode.ON_OR_INSERT );
        ToolTipManager.sharedInstance().registerComponent( collectionJTree );

        // embed the JTree in a JScrollPane
        collectionJScrollPane.setMinimumSize( Settings.JPO_NAVIGATOR_JTABBEDPANE_MINIMUM_SIZE );
        collectionJScrollPane.setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );

        //Add listener to components that can bring up groupPopupJPopupMenu menus.
        CollectionMouseAdapter mouseAdapter = new CollectionMouseAdapter( this );
        collectionJTree.addMouseListener( mouseAdapter );

    }

    @Override
    public void requestGroupExportPicasa( SortableDefaultMutableTreeNode groupNode ) {
        PicasaUploadRequest myRequest = new PicasaUploadRequest();
        myRequest.setNode( groupNode );
        new PicasaUploaderWizard( myRequest );
    }

    private class MyTransferHandler
            extends TransferHandler {

        /**
         * This method is used to query what actions are supported by the source
         * component
         *
         * @param c the Object to query
         * @return COPY_OR_MOVE for this TransferHandler
         */
        @Override
        public int getSourceActions( JComponent c ) {
            return COPY_OR_MOVE;
        }

        /**
         * This method bundles up the data to be exported into a Transferable
         * object in preparation for the transfer.
         *
         * @param c
         * @return a transferable
         */
        @Override
        protected Transferable createTransferable( JComponent c ) {
            TreePath selected = collectionJTree.getSelectionPath();
            SortableDefaultMutableTreeNode dmtn = (SortableDefaultMutableTreeNode) selected.getLastPathComponent();
            if ( dmtn.isRoot() ) {
                LOGGER.info( "The Root node must not be dragged. Dragging disabled." );
                return null;
            }
            final Object t[] = { dmtn };
            JpoTransferable draggedNode = new JpoTransferable( t );
            return draggedNode;
        }

        /**
         * This method is called repeatedly during a drag gesture and returns
         * true if the area below the cursor can accept the transfer, or false
         * if the transfer will be rejected.
         *
         * @param support The transfer support object
         * @return true if the import is OK
         */
        @Override
        public boolean canImport( TransferSupport support ) {
            return support.isDataFlavorSupported( JpoTransferable.jpoNodeFlavor );
        }

        /**
         * This method is called on a successful drop (or paste) and initiates
         * the transfer of data to the target component. This method returns
         * true if the import was successful and false otherwise.
         *
         * @param support
         * @return true if successful
         */
        @Override
        public boolean importData( TransferSupport support ) {
            JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
            SortableDefaultMutableTreeNode targetNode = (SortableDefaultMutableTreeNode) dropLocation.getPath().getLastPathComponent();
            LOGGER.info( String.format( "Choosing node %s as target for path %s, ChildIndex: %d", targetNode.toString(), dropLocation.getPath(), dropLocation.getChildIndex() ) );

            int actionType = support.getDropAction();
            if ( !( ( actionType == TransferHandler.COPY ) || ( actionType == TransferHandler.MOVE ) ) ) {
                LOGGER.info( String.format( "The event has an odd Action Type: %d. Drop rejected. Copy is %d; Move is %d", actionType, TransferHandler.COPY, TransferHandler.MOVE ) );
                return false;
            }

            SortableDefaultMutableTreeNode sourceNode;
            Object[] arrayOfNodes;

            try {
                Transferable t = support.getTransferable();
                Object o = t.getTransferData( JpoTransferable.jpoNodeFlavor );
                arrayOfNodes = (Object[]) o;
            } catch ( java.awt.datatransfer.UnsupportedFlavorException x ) {
                LOGGER.log( Level.INFO, "Caught an UnsupportedFlavorException: message: {0}", x.getMessage() );
                return false;
            } catch ( java.io.IOException x ) {
                LOGGER.log( Level.INFO, "Caught an IOException: message: {0}", x.getMessage() );
                return false;
            } catch ( ClassCastException x ) {
                LOGGER.log( Level.INFO, "Caught an ClassCastException: message: {0}", x.getMessage() );
                return false;
            }

            for ( Object arrayOfNode : arrayOfNodes ) {
                sourceNode = (SortableDefaultMutableTreeNode) arrayOfNode;
                if ( targetNode.isNodeAncestor( sourceNode ) ) {
                    JOptionPane.showMessageDialog( Settings.anchorFrame,
                            Settings.jpoResources.getString( "moveNodeError" ),
                            Settings.jpoResources.getString( "genericError" ),
                            JOptionPane.ERROR_MESSAGE );
                    return false;
                }
            }

            // The drop is a valid one.
            //  memorise the group of the drop location.
            SortableDefaultMutableTreeNode groupOfDropLocation;
            if ( targetNode.getUserObject() instanceof GroupInfo ) {
                groupOfDropLocation = targetNode;
            } else {
                // the parent must be a group node
                groupOfDropLocation = (SortableDefaultMutableTreeNode) targetNode.getParent();
            }
            if ( ( groupOfDropLocation != null ) && ( groupOfDropLocation.getUserObject() instanceof GroupInfo ) ) {
                Settings.memorizeGroupOfDropLocation( groupOfDropLocation );
            } else {
                LOGGER.info( "Failed to find the group of the drop location. Not memorizing." );
            }

            for ( Object arrayOfNode : arrayOfNodes ) {
                sourceNode = (SortableDefaultMutableTreeNode) arrayOfNode;
                if ( actionType == TransferHandler.MOVE ) {
                    if ( dropLocation.getChildIndex() == -1 ) {
                        if ( targetNode.getUserObject() instanceof GroupInfo ) {
                            // append to end of group if dropping on a group node
                            sourceNode.moveToLastChild( targetNode );
                        } else {
                            // dropping on a PictureInfo
                            sourceNode.moveBefore( targetNode );
                        }
                    } else {
                        //index was supplied by the JTree notification
                        sourceNode.moveToIndex( targetNode, dropLocation.getChildIndex() );
                    }
                } else {
                    // Copy
                    SortableDefaultMutableTreeNode cloneNode = sourceNode.getClone();
                    if ( dropLocation.getChildIndex() == -1 ) {
                        if ( targetNode.getUserObject() instanceof GroupInfo ) {
                            targetNode.add( cloneNode );
                        } else {
                            // dropping onto a picture
                            cloneNode.moveBefore( targetNode );
                        }
                    } else {
                        cloneNode.moveToIndex( targetNode, dropLocation.getChildIndex() );
                    }
                }
            }
            return true;
        }
    }
    /**
     * The private reference to the JTree representing the collection
     */
    private final JTree collectionJTree = new CollectionJTree() {

        @Override
        public String getToolTipText( MouseEvent mouseEvent ) {
            return overriddenGetToolTipText( mouseEvent );
        }
    };
    /**
     * The private reference to the JScrollPane that holds the JTree.
     */
    private final JScrollPane collectionJScrollPane = new JScrollPane( collectionJTree );

    /**
     * Returns the JScrollPane that holds the JTree.
     *
     * @return the JSCrollPane that holds the tree
     */
    public JScrollPane getJScrollPane() {
        return collectionJScrollPane;
    }

    public String overriddenGetToolTipText( MouseEvent mouseEvent ) {
        if ( collectionJTree.getRowForLocation( mouseEvent.getX(), mouseEvent.getY() ) == -1 ) {
            return null;
        }
        TreePath curPath = collectionJTree.getPathForLocation( mouseEvent.getX(), mouseEvent.getY() );
        SortableDefaultMutableTreeNode node = (SortableDefaultMutableTreeNode) curPath.getLastPathComponent();
        Object userObject = node.getUserObject();
        String toolTip = "";
        if ( userObject instanceof GroupInfo ) {
            //NodeStatistics ns = new NodeStatistics( node ); // slow; don't want on the EDT
            GroupInfo groupInfo = (GroupInfo) userObject;
            //toolTip = String.format( "<html>Group: %s<br>%d Nodes: %d Groups, %d Pictures<br>%s</html>", groupInfo.getGroupName(), ns.getNumberOfNodes(), ns.getNumberOfGroups(), ns.getNumberOfPictures(), ns.getSizeOfPicturesString() );
            toolTip = String.format( "<html>Group: %s</html>", groupInfo.getGroupName() );
        } else if ( userObject instanceof PictureInfo ) {
            final PictureInfo pictureInfo = (PictureInfo) userObject;
            File highresFile = pictureInfo.getHighresFile();
            String fileSize = highresFile == null ? "no file" : Tools.fileSizeToString( highresFile.length() );
            toolTip = String.format( "<html><img src=\"%s\"><br>Picture: %s<br>%s %s</html>", pictureInfo.getLowresLocation(), pictureInfo.getDescription(), Settings.jpoResources.getString( "CollectionSizeJLabel" ), fileSize );
        }
        return toolTip;
    }

    /**
     * Requests the group to be shown using the {@link #setSelectedNode} method.
     * Additionally requests the Group to be shown in the
     * ThumbnailPanelController.
     *
     * @param newNode The Node to which to jump
     * @see GroupPopupInterface
     */
    @Override
    public void requestShowGroup( SortableDefaultMutableTreeNode newNode ) {
        LOGGER.log( Level.FINE, "requesting node: {0}", newNode.toString() );
        Jpo.positionToNode( newNode );

    }

    /**
     * Moves the highlighted row to the indicated one and expands the tree if
     * necessary. Also ensures that the associatedInfoPanel is updated
     *
     * @param newNode The node which should be highlighted
     */
    public void setSelectedNode( final SortableDefaultMutableTreeNode newNode ) {
        Tools.checkEDT();
        TreePath tp = new TreePath( newNode.getPath() );
        collectionJTree.setSelectionPath( tp );
        collectionJTree.scrollPathToVisible( tp );
    }

    /**
     * requests the pictures to be shown.
     *
     * @param popupNode The node that sent the request
     * @see GroupPopupInterface
     */
    @Override
    public void requestSlideshow( SortableDefaultMutableTreeNode popupNode ) {
        Jpo.browsePictures( popupNode );

    }

    /**
     * This method can be invoked by the GroupPopupMenu.
     *
     * @param popupNode The node on which the popup Menu was done
     * @see GroupPopupInterface
     */
    @Override
    public void requestFind( SortableDefaultMutableTreeNode popupNode ) {
        new QueryJFrame( popupNode, applicationEventHandler );
    }

    /**
     * this method invokes an editor for the GroupInfo data
     *
     * @param popupNode
     * @see GroupPopupInterface
     */
    @Override
    public void requestEditGroupNode( SortableDefaultMutableTreeNode popupNode ) {
        TreeNodeController.showEditGUI( popupNode );

    }

    /**
     * this method invokes the Category editor and allows the user to set the
     * categories for all the pictures in the Group.
     *
     * @param popupNode
     */
    @Override
    public void showCategoryUsageGUI( SortableDefaultMutableTreeNode popupNode ) {
        TreeNodeController.showCategoryUsageGUI( popupNode );

    }

    /**
     * Adds a new empty group to the node that was supplied. The group is called
     * "New Group". It is added to the recent copy group targets.
     *
     * @param popupNode The node to which the group is being attached
     * @see GroupPopupInterface
     */
    @Override
    public void requestAddGroup( SortableDefaultMutableTreeNode popupNode ) {
        if ( !( popupNode.getUserObject() instanceof GroupInfo ) ) {
            LOGGER.warning( String.format( "node %s is of type %s instead of GroupInfo. Proceeding anyway.", popupNode.getUserObject().toString(), popupNode.getUserObject().getClass().toString() ) );
        }
        SortableDefaultMutableTreeNode newNode = popupNode.addGroupNode( "New Group" );
        Settings.memorizeGroupOfDropLocation( newNode );
        setSelectedNode( newNode );
    }

    /**
     * Bring up a chooser and add pictures to the group.
     *
     * @see GroupPopupInterface
     * @param groupNode The group node to which to add the pictures
     */
    @Override
    public void chooseAndAddPicturesToGroup(
            SortableDefaultMutableTreeNode groupNode ) {
        applicationEventHandler.chooseAndAddPicturesToGroup( groupNode );

    }

    /**
     * Requests that a collection be added at this point in the tree
     *
     * @param popupNode
     * @see GroupPopupInterface
     */
    @Override
    public void requestAddCollection( SortableDefaultMutableTreeNode popupNode ) {
        File fileToLoad = Tools.chooseXmlFile();
        if ( fileToLoad != null ) {
            requestAddCollection( popupNode, fileToLoad );
        }
    }

    @Override
    public void requestAddCollection( SortableDefaultMutableTreeNode popupNode,
            File fileToLoad ) {
        applicationEventHandler.requestAddCollection( popupNode, fileToLoad );
    }

    public void expandPath( TreePath tp ) {
        collectionJTree.expandPath( tp );
    }

    /**
     * Method that will bring up a dialog box that allows the user to select how
     * he wants to export the pictures of the current Group.
     *
     * @param nodeToExport The node to export to a website
     */
    @Override
    public void requestGroupExportHtml(
            SortableDefaultMutableTreeNode nodeToExport ) {
        new GenerateWebsiteWizard( nodeToExport );
    }

    /**
     * Selects all the pictures under the group for emailing.
     *
     * @param groupNode
     */
    @Override
    public void requestEmailSelection( SortableDefaultMutableTreeNode groupNode ) {
        SortableDefaultMutableTreeNode n;
        for ( Enumeration e = groupNode.breadthFirstEnumeration(); e.hasMoreElements(); ) {
            n = (SortableDefaultMutableTreeNode) e.nextElement();
            if ( n.getUserObject() instanceof PictureInfo ) {
                Settings.pictureCollection.addToMailSelection( n );
            }
        }
    }

    /**
     * Requests that the pictures indicated in a flat file be added at this
     * point in the tree
     *
     * @param nodeToExport The node on which the request was made
     * @see GroupPopupInterface
     */
    @Override
    public void requestGroupExportFlatFile(
            SortableDefaultMutableTreeNode nodeToExport ) {
        javax.swing.JFileChooser jFileChooser = new javax.swing.JFileChooser();
        jFileChooser.setFileSelectionMode( javax.swing.JFileChooser.FILES_ONLY );
        jFileChooser.setDialogTitle( Settings.jpoResources.getString( "saveFlatFileTitle" ) );
        jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "saveFlatFileButtonLabel" ) );
        jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );
        int returnVal = jFileChooser.showSaveDialog( Settings.anchorFrame );
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File chosenFile = jFileChooser.getSelectedFile();
            new FlatFileDistiller( chosenFile, nodeToExport );
        }
    }

    /**
     * requests that a group be exported to a new collectionjar archive
     *
     * @see GroupPopupInterface
     *
     * @param nodeToExport The node on which the request was made
     */
    @Override
    public void requestGroupExportNewCollection(
            SortableDefaultMutableTreeNode nodeToExport ) {
        new CollectionDistillerJFrame( nodeToExport );
    }

    /**
     * Requests that a group be removed
     *
     * @param nodeToRemove The Node you want removed
     * @see GroupPopupInterface
     */
    @Override
    public void requestGroupRemove( SortableDefaultMutableTreeNode nodeToRemove ) {
        //logger.fine( "CollectionJTree.requestGroupRemove: invoked on group: " + nodeToRemove.getUserObject().toString() );
        SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) nodeToRemove.getParent();
        if ( nodeToRemove.deleteNode() ) {
            setSelectedNode( parentNode );
        }
    }

    /**
     * requests that a group's picture files be consolidated
     *
     * @param node
     * @see GroupPopupInterface
     */
    @Override
    public void requestConsolidateGroup(
            SortableDefaultMutableTreeNode node ) {
        new ConsolidateGroupJFrame( node );
    }

    /**
     * Requests that a group be moved to the top
     *
     * @param popupNode The node on which the request was made
     * @see GroupPopupInterface
     */
    @Override
    public void requestMoveGroupToTop( SortableDefaultMutableTreeNode popupNode ) {
        popupNode.moveNodeToTop();
    }

    /**
     * Requests that a group be moved up
     *
     * @param popupNode The node on which the request was made
     * @see GroupPopupInterface
     */
    @Override
    public void requestMoveGroupUp( SortableDefaultMutableTreeNode popupNode ) {
        popupNode.moveNodeUp();
    }

    /**
     * Requests that a group be moved down
     *
     * @param popupNode The node on which the request was made
     * @see GroupPopupInterface
     */
    @Override
    public void requestMoveGroupDown( SortableDefaultMutableTreeNode popupNode ) {
        popupNode.moveNodeDown();
    }

    /**
     * Requests that a group be moved down
     *
     * @param popupNode The node on which the request was made
     * @see GroupPopupInterface
     */
    @Override
    public void requestMoveGroupToBottom(
            SortableDefaultMutableTreeNode popupNode ) {
        popupNode.moveNodeToBottom();

    }

    /**
     * Requests that a picture be moved to the target Group node
     *
     * @param popupNode The node on which the request was made
     * @param targetGroup the target group node
     * @see GroupPopupInterface
     */
    @Override
    public void requestMoveToNode( SortableDefaultMutableTreeNode popupNode,
            SortableDefaultMutableTreeNode targetGroup ) {
        popupNode.moveToLastChild( targetGroup );

    }

    /**
     * Request that a group be edited as a table
     *
     * @param popupNode The node on which the request was made
     */
    @Override
    public void requestEditGroupTable( SortableDefaultMutableTreeNode popupNode ) {
        TableJFrame tableJFrame = new TableJFrame( popupNode );
        tableJFrame.pack();
        tableJFrame.setVisible( true );
    }

    /**
     * Gets called by the GroupPopupInterface and implements the sort request.
     *
     * @param popupNode The node on which the request was made
     */
    @Override
    public void requestSort( SortableDefaultMutableTreeNode popupNode,
            int sortCriteria ) {
        //logger.info( "Sort requested on " + myPopupNode.toString() + " for Criteria: " + Integer.toString( sortCriteria ) );
        popupNode.sortChildren( sortCriteria );
    }

    /**
     * This class decides what to do with mouse events on the JTree. Since there
     * is so much logic tied to what we are trying to do in the context of the
     * JTree being the top left component here which might not be desirable in a
     * different context this is kept as an inner class of the
     * CollectionJTreeController. the groupPopupJPopupMenu menu must exist.
     *
     */
    private class CollectionMouseAdapter
            extends MouseAdapter {

        /**
         * A reference back to the CollectionJTreeController for which this is a
         * listener.
         */
        private CollectionJTreeController collectionJTreeController;

        private CollectionMouseAdapter(
                CollectionJTreeController collectionJTreeController ) {
            this.collectionJTreeController = collectionJTreeController;
        }

        /**
         * If the mouse was clicked more than once using the left mouse button
         * over a valid picture node then the picture editor is opened.
         */
        @Override
        public void mouseClicked( MouseEvent e ) {
            TreePath clickPath = ( (JTree) e.getSource() ).getPathForLocation( e.getX(), e.getY() );
            if ( clickPath == null ) { // this happens
                return;
            }
            SortableDefaultMutableTreeNode clickNode = (SortableDefaultMutableTreeNode) clickPath.getLastPathComponent();

            if ( e.getClickCount() == 1 && ( !e.isPopupTrigger() ) ) {
                if ( clickNode.getUserObject() instanceof GroupInfo ) {
                    Jpo.positionToNode( clickNode );
                }
            } else if ( e.getClickCount() > 1 && ( !e.isPopupTrigger() ) ) {
                Jpo.browsePictures( clickNode );
            }
        }

        /**
         * Override the mousePressed event.
         */
        @Override
        public void mousePressed( MouseEvent e ) {
            maybeShowPopup( e );
        }

        /**
         * Override the mouseReleased event.
         */
        @Override
        public void mouseReleased( MouseEvent e ) {
            maybeShowPopup( e );
        }

        /**
         * This method figures out whether a popup window should be displayed
         * and displays it.
         *
         * @param e	The MouseEvent that was trapped.
         */
        private void maybeShowPopup( MouseEvent e ) {
            if ( e.isPopupTrigger() ) {
                TreePath popupPath = ( (JTree) e.getSource() ).getPathForLocation( e.getX(), e.getY() );
                if ( popupPath == null ) {
                    return;
                } // happens
                final SortableDefaultMutableTreeNode popupNode = (SortableDefaultMutableTreeNode) popupPath.getLastPathComponent();
                ( (JTree) e.getSource() ).setSelectionPath( popupPath );
                Object nodeInfo = popupNode.getUserObject();

                if ( nodeInfo instanceof GroupInfo ) {
                    final MouseEvent fe = e;
                    Runnable r = new Runnable() {

                        @Override
                        public void run() {
                            GroupPopupMenu groupPopupMenu = new GroupPopupMenu( collectionJTreeController, popupNode );
                            groupPopupMenu.show( fe.getComponent(), fe.getX(), fe.getY() );
                        }
                    };
                    if ( SwingUtilities.isEventDispatchThread() ) {
                        r.run();
                    } else {
                        SwingUtilities.invokeLater( r );
                    }
                } else if ( nodeInfo instanceof PictureInfo ) {
                    SingleNodeNavigator sb = new SingleNodeNavigator( popupNode );
                    PicturePopupMenu picturePopupMenu = new PicturePopupMenu( sb, 0 );
                    picturePopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        }
    }
}
