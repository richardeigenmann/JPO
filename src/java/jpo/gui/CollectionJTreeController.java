package jpo.gui;

import java.awt.datatransfer.Transferable;
import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import jpo.gui.swing.CollectionJTree;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.FlatFileDistiller;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.PictureInfo;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Enumeration;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.*;
import jpo.dataModel.SingleNodeNavigator;
import jpo.export.GenerateWebsiteWizard;

/*
CollectionJTreeController.java:  class that manages a JTree for the collection
 * 
Copyright (C) 2002 - 2010 Richard Eigenmann, Zurich, Switzerland
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
 *   The is one of the main classes in the JPO application as it is an extended JTree that
 *   deals with most of the logic surrounding the collection and the user interactions with it.
 */
public class CollectionJTreeController
        implements
        GroupPopupInterface {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( CollectionJTreeController.class.getName() );

    /**
     * reference to the main collection controller so that we can delegate stuff to 
     */
    private Jpo collectionController;


    /**
     * The Controller class for the JTree. This class no longer extends the JTree. Instead it
     * is a plain simple old class that does all the things to respond to or instruct the JTree to
     * do and show.
     *
     * @param collectionController the reference to the collection controller
     */
    public CollectionJTreeController( Jpo collectionController ) {
        this.collectionController = collectionController;
        collectionJTree = new CollectionJTree();

        Runnable r = new Runnable() {

            public void run() {
                collectionJTree.setModel( Settings.pictureCollection.getTreeModel() );
                collectionJTree.setEditable( true ); // doing this in the controller as it might not always be desired (like in the CameraDownloadWizard)
                collectionJTree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
                collectionJTree.setTransferHandler( new MyTransferHandler() );
                collectionJTree.setDragEnabled( true );
                collectionJTree.setDropMode( DropMode.ON_OR_INSERT );
                // embed the JTree in a JScrollPane
                collectionJScrollPane = new JScrollPane( collectionJTree );
                collectionJScrollPane.setMinimumSize( Settings.jpoNavigatorJTabbedPaneMinimumSize );
                collectionJScrollPane.setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }

        //Add listener to components that can bring up groupPopupJPopupMenu menus.
        CollectionMouseAdapter mouseAdapter = new CollectionMouseAdapter( this );
        collectionJTree.addMouseListener( mouseAdapter );

    }

    private class MyTransferHandler
            extends TransferHandler {

        /**
         * This method is used to query what actions are supported by the source component
         * @param c
         * @return
         */
        @Override
        public int getSourceActions( JComponent c ) {
            return COPY_OR_MOVE;
        }


        /**
         * This method bundles up the data to be exported into a Transferable object in preparation for the transfer.
         * @param c
         * @return a Treansferable
         */
        @Override
        protected Transferable createTransferable( JComponent c ) {
            TreePath selected = collectionJTree.getSelectionPath();
            SortableDefaultMutableTreeNode dmtn = (SortableDefaultMutableTreeNode) selected.getLastPathComponent();
            if ( dmtn.isRoot() ) {
                logger.info( "The Root node must not be dragged. Dragging disabled." );
                return null;
            }
            final Object t[] = { dmtn };
            JpoTransferable draggedNode = new JpoTransferable( t );
            return draggedNode;
        }


        /**
         * This method is called repeatedly during a drag gesture and returns true if the
         * area below the cursor can accept the transfer, or false if the transfer will be rejected.
         * @param support
         * @return
         */
        @Override
        public boolean canImport( TransferSupport support ) {
            return support.isDataFlavorSupported( JpoTransferable.jpoNodeFlavor );
        }


        /**
         * This method is called on a successful drop (or paste) and initiates
         * the transfer of data to the target component. This method returns
         * true if the import was successful and false otherwise.
         * @param support
         * @return
         */
        @Override
        public boolean importData( TransferSupport support ) {
            JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
            SortableDefaultMutableTreeNode targetNode = (SortableDefaultMutableTreeNode) dropLocation.getPath().getLastPathComponent();
            logger.info( String.format( "Choosing node %s as target for path %s, ChildIndex: %d", targetNode.toString(), dropLocation.getPath(), dropLocation.getChildIndex() ) );

            int actionType = support.getDropAction();
            if ( !( ( actionType == TransferHandler.COPY ) || ( actionType == TransferHandler.MOVE ) ) ) {
                logger.info( String.format( "The event has an odd Action Type: %d. Drop rejected. Copy is %d; Move is %d", actionType, TransferHandler.COPY, TransferHandler.MOVE ) );
                return false;
            }

            SortableDefaultMutableTreeNode sourceNode;
            Object[] arrayOfNodes;

            try {
                Transferable t = support.getTransferable();
                Object o = t.getTransferData( JpoTransferable.jpoNodeFlavor );
                arrayOfNodes = (Object[]) o;
            } catch ( java.awt.datatransfer.UnsupportedFlavorException x ) {
                logger.info( "Caught an UnsupportedFlavorException: message: " + x.getMessage() );
                return false;
            } catch ( java.io.IOException x ) {
                logger.info( "Caught an IOException: message: " + x.getMessage() );
                return false;
            } catch ( ClassCastException x ) {
                logger.info( "Caught an ClassCastException: message: " + x.getMessage() );
                return false;
            }

            /* We must ensure that if the action is a move it does not drop into
            itself or into a child of itself. */
            for ( int i = 0; i < arrayOfNodes.length; i++ ) {
                sourceNode = (SortableDefaultMutableTreeNode) arrayOfNodes[i];
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
                logger.info( "Failed to find the group of the drop location. Not memorizing." );
            }


            for ( int i = 0; i < arrayOfNodes.length; i++ ) {
                sourceNode = (SortableDefaultMutableTreeNode) arrayOfNodes[i];
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
    private JTree collectionJTree;

    /**
     * The private reference to the JScrollPane that holds the JTree.
     */
    private JScrollPane collectionJScrollPane;


    /**
     * Returns the JScrollPane that holds the JTree.
     * @return the JSCrollPane that holds the tree
     */
    public JScrollPane getJScrollPane() {
        return collectionJScrollPane;


    }

    /**
     *  callback method that is called when a drag gesture has been initiated on a node of the JTree.
     *  @param  event    The Drag and Drop Framework gives us details about the detected event in this parameter.
     *
     *
    public void dragGestureRecognized( DragGestureEvent event ) {
    if ( ( event.getDragAction() & DnDConstants.ACTION_COPY_OR_MOVE ) == 0 ) {
    return;
    }

    TreePath selected = collectionJTree.getSelectionPath();
    SortableDefaultMutableTreeNode dmtn = (SortableDefaultMutableTreeNode) selected.getLastPathComponent();
    // logger.info("CollectionJTreeController.dragGestureRecognized: Drag started on node: " + dmtn.getUserObject().toString() );
    if ( dmtn.isRoot() ) {
    logger.info( "The Root node must not be dragged. Dragging disabled." );
    return;
    }
    final Object t[] = { dmtn };
    JpoTransferable draggedNode = new JpoTransferable( t );
    event.startDrag( DragSource.DefaultMoveDrop, draggedNode, this );
    }*/
    /**
     *   this callback method is invoked every time something is
     *   dragged onto the JTree. We check if the desired DataFlavor is
     *   supported and then reject the drag if it is not.
     * @param event
     *
    public void dragEnter( DropTargetDragEvent event ) {
    if ( !event.isDataFlavorSupported( JpoTransferable.jpoNodeFlavor ) ) {
    event.rejectDrag();


    }
    }


    /**
     *  this callback method is invoked after the dropTaget had a chance
     *  to evaluate the drag event and was given the option of rejecting or
     *  modifying the event. This method sets the cursor to reflect
     *  whether a copy, move or no drop is possible.
     *
     * @param event
     *
    public void dragEnter( DragSourceDragEvent event ) {
    //logger.info( "CollectionJTreeController.dragEnter(DragSourceDragEvent): invoked");
    Tools.setDragCursor( event );


    }


    /**
     *   this callback method is invoked every time something is
     *   dragged over the JTree. We check if the desired DataFlavor is
     *   supported and then reject the drag if it is not.
     *
     * @param event
     *
    public void dragOver( DropTargetDragEvent event ) {
    //logger.info("CollectionJTreeController.dragOver (DropTargetDragEvent) triggered");
    if ( !event.isDataFlavorSupported( JpoTransferable.jpoNodeFlavor ) ) {
    //logger.info("CollectionJTree.dragOver (DropTargetDragEvent): The dmtn DataFlavor is not supported. Rejecting drag.");
    event.rejectDrag();


    } else {
    // figure out where the cursor is and highlight the node by setting the selection path
    TreePath popupPath = collectionJTree.getPathForLocation( event.getLocation().x, event.getLocation().y );


    if ( popupPath != null ) {
    event.acceptDrag( DnDConstants.ACTION_COPY_OR_MOVE );
    //nodeToRemove = (SortableDefaultMutableTreeNode) popupPath.getLastPathComponent();
    collectionJTree.setSelectionPath( popupPath );


    } else {
    //logger.info("CollectionJTreeController.dragOver( DropTargetDragEvent ): the coordinates returned by the event do not match a selectable path." );
    event.rejectDrag();


    }
    }
    autoscroll( (JTree) event.getDropTargetContext().getComponent(), event.getLocation() );


    }

    /** insets for autoscroll */
    private Insets autoscrollInsets = new Insets( 20, 20, 20, 20 );


    private Insets getAutoscrollInsets() {
        return autoscrollInsets;


    }


    /**
     * Autoscroll implemented following the article here:
     * http://articles.lightdev.com/tree/tree_article.pdf
     * By Ulrich Hilger from Light Development
     */
    private void autoscroll( JTree tree, Point cursorLocation ) {
        Insets insets = getAutoscrollInsets();
        Rectangle outer = tree.getVisibleRect();
        Rectangle inner = new Rectangle(
                outer.x + insets.left,
                outer.y + insets.top,
                outer.width - ( insets.left + insets.right ),
                outer.height - ( insets.top + insets.bottom ) );


        if ( !inner.contains( cursorLocation ) ) {
            Rectangle scrollRect = new Rectangle(
                    cursorLocation.x - insets.left,
                    cursorLocation.y - insets.top,
                    insets.left + insets.right,
                    insets.top + insets.bottom );
            tree.scrollRectToVisible( scrollRect );


        }
    }


    /**
     *  this callback method is invoked after the dropTaget had a chance
     *  to evaluate the dragOver event and was given the option of rejecting or
     *  modifying the event. This method sets the cursor to reflect
     *  whether a copy, move or no drop is possible.
     *
     * @param event
     *
    public void dragOver( DragSourceDragEvent event ) {
    //logger.info("CollectionJTreeController.dragOver(DragSourceDragEvent) invoked");
    Tools.setDragCursor( event );


    }


    /**
     *  this callback method is invoked when the user presses or releases Ctrl when
     *  doing a drag. He can signal that he wants to change the copy / move of the
     *  operation. This method could intercept this change and could modify the event
     *  if it needs to.
     *  Here we use this as a convenient handle to expand and collapse the Tree.
     *
     * @param event
     *
    public void dropActionChanged( DropTargetDragEvent event ) {
    // figure out where the cursor is and highlight the node
    TreePath popupPath = collectionJTree.getPathForLocation( event.getLocation().x, event.getLocation().y );


    if ( popupPath != null ) {
    SortableDefaultMutableTreeNode myPopupNode = (SortableDefaultMutableTreeNode) popupPath.getLastPathComponent();
    logger.info( "CollectionJTree.dropActionChanged( DropTargetDragEvent ): hovering over: " + myPopupNode.getUserObject().toString() );


    if ( collectionJTree.isExpanded( popupPath ) ) {
    collectionJTree.collapsePath( popupPath );


    } else {
    collectionJTree.expandPath( popupPath );


    }
    } else {
    logger.info( "CollectionJTree.dropActionChanged( DropTargetDragEvent ): the coordinates returned by the event do not match a selectable path." );


    }
    }


    /**
     *  this callback method is invoked when the user presses or releases shift when
     *  doing a drag. He can signal that he wants to change the copy / move of the
     *  operation. This method changes the cursor to reflect the mode of the
     *  operation.
     * @param event
     *
    public void dropActionChanged( DragSourceDragEvent event ) {
    //logger.info( "CollectionJTreeController.dropActionChanged( DragSourceDragEvent ): invoked");
    Tools.setDragCursor( event );


    }


    /**
     *   this callback method is invoked to tell the dropTarget that the drag has moved on
     *   to something else. We do nothing here.
     * @param event
     *
    public void dragExit( DropTargetEvent event ) {
    //logger.info("CollectionJTreeController.dragExit( DropTargetEvent ): invoked");
    }


    /**
     *   this callback method is invoked to tell the dragSource that the drag has moved on
     *   to something else. We do nothing here.
     * @param event
     *
    public void dragExit( DragSourceEvent event ) {
    }


    /**
     *  Entry point for the drop event. Figures out which node the drop occured on and
     *  sorts out the drop action in the data model.
     * @param event
     *
    public void drop( DropTargetDropEvent event ) {
    Point p = event.getLocation();
    TreePath targetPath = collectionJTree.getPathForLocation( p.x, p.y );


    if ( targetPath == null ) {
    logger.info( "CollectionJTree.drop(DropTargetDropEvent): The drop coordinates do not specify a node. Drop aborted." );
    event.dropComplete( false );


    return;


    } else {
    SortableDefaultMutableTreeNode targetNode = (SortableDefaultMutableTreeNode) targetPath.getLastPathComponent();
    targetNode.executeDrop( event );


    }
    }


    /**
     * this callback message goes to DragSourceListener, informing it that the dragging
     * has ended.
     *
     * @param event
     *
    public void dragDropEnd( DragSourceDropEvent event ) {
    }


    /**
     *  Requests the group to be shown using the {@link #setSelectedNode} method. Additionally requests
     *  the Group to be shown in the ThumbnailPanelController.
     *  @param newNode  The Node to which to jump
     *  @see  GroupPopupInterface
     */
    public void requestShowGroup( SortableDefaultMutableTreeNode newNode ) {
        logger.fine( "requesting node: " + newNode.toString() );
        Jpo.positionToNode( newNode );


    }


    /**
     *  Moves the highlighted row to the indicated one and expands the tree if necessary.
     *  Also ensures that the associatedInfoPanel is updated
     *  @param newNode  The node which should be highlighted
     */
    public void setSelectedNode( final SortableDefaultMutableTreeNode newNode ) {
        Runnable r = new Runnable() {

            public void run() {
                TreePath tp = new TreePath( newNode.getPath() );
                collectionJTree.setSelectionPath( tp );
                collectionJTree.scrollPathToVisible( tp );


            }
        };


        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();


        } else {
            SwingUtilities.invokeLater( r );


        }
    }


    /**
     *  requests the pictures to be shown.
     *  @param nodeToRemove The node that sent the request
     *  @see  GroupPopupInterface
     */
    public void requestSlideshow( SortableDefaultMutableTreeNode popupNode ) {
        Jpo.browsePictures( popupNode );


    }


    /**
     *  This method can be invoked by the GroupPopupMenu.
     *
     *  @param nodeToRemove  The node on which the popup Menu was done
     * @see  GroupPopupInterface
     */
    public void requestFind( SortableDefaultMutableTreeNode popupNode ) {
        new QueryJFrame( popupNode, collectionController );


    }


    /**
     *  this method invokes an editor for the GroupInfo data
     *  @param nodeToRemove
     * @see  GroupPopupInterface
     */
    public void requestEditGroupNode( SortableDefaultMutableTreeNode popupNode ) {
        TreeNodeController.showEditGUI( popupNode );


    }


    /**
     *  this method invokes the Category editor and allows the user to set the categories for all the pictures in the Group.
     * @param nodeToRemove
     */
    public void showCategoryUsageGUI( SortableDefaultMutableTreeNode popupNode ) {
        TreeNodeController.showCategoryUsageGUI( popupNode );


    }


    /**
     *  requests a new empty group to be added.
     *  @param nodeToRemove
     * @see  GroupPopupInterface
     */
    public void requestAddGroup( SortableDefaultMutableTreeNode popupNode ) {
        SortableDefaultMutableTreeNode newNode = popupNode.addGroupNode( "New Group" );
        Settings.memorizeGroupOfDropLocation( newNode );
        setSelectedNode(
                newNode );


    }


    /**
     * Bring up a chooser and add pictures to the group.
     * @see  GroupPopupInterface
     * @param groupNode  The group nodde to which to add the pictures
     */
    public void chooseAndAddPicturesToGroup(
            SortableDefaultMutableTreeNode groupNode ) {
        collectionController.chooseAndAddPicturesToGroup( groupNode );


    }


    /**
     * Requests that a collection be added at this point in the tree
     * @param nodeToExport
     * @see GroupPopupInterface
     */
    public void requestAddCollection( SortableDefaultMutableTreeNode popupNode ) {
        File fileToLoad = Tools.chooseXmlFile();
        if ( fileToLoad != null ) {
            requestAddCollection( popupNode, fileToLoad );
        }
    }


    public void requestAddCollection( SortableDefaultMutableTreeNode popupNode,
            File fileToLoad ) {
        collectionController.requestAddCollection( popupNode, fileToLoad );
    }


    public void expandPath( TreePath tp ) {
        collectionJTree.expandPath( tp );
    }


    /**
     * Method that will bring up a dialog box that allows the user to select how he wants
     * to export the pictures of the current Group.
     *
     * @param nodeToExport  The node to export to a website
     */
    public void requestGroupExportHtml(
            SortableDefaultMutableTreeNode nodeToExport ) {
        new GenerateWebsiteWizard( nodeToExport );
    }


    /**
     *  Selects all the pictures under the group for emailing.
     *
     * @param groupNode
     */
    public void requestEmailSelection( SortableDefaultMutableTreeNode groupNode ) {
        SortableDefaultMutableTreeNode n;
        for ( Enumeration e = groupNode.breadthFirstEnumeration(); e.hasMoreElements(); ) {
            n = (SortableDefaultMutableTreeNode) e.nextElement();
            if ( n.getUserObject() instanceof PictureInfo ) {
                Settings.pictureCollection.addToMailSelected( n );
            }
        }
    }


    /**
     * Requests that the pictures indicated in a flat file be added at this point in the tree
     * @param nodeToExport The node on which the request was made
     * @see GroupPopupInterface
     */
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
     *  requests that a group be exported to a new collectionjar archive
     *  @see  GroupPopupInterface
     *
     *  @param nodeToExport The node on which the request was made
     */
    public void requestGroupExportNewCollection(
            SortableDefaultMutableTreeNode nodeToExport ) {
        new CollectionDistillerJFrame( nodeToExport );
    }


    /**
     * Requests that a group be removed
     * @param nodeToRemove The Node you want removed
     * @see  GroupPopupInterface
     */
    public void requestGroupRemove( SortableDefaultMutableTreeNode nodeToRemove ) {
        //logger.fine( "CollectionJTree.requestGroupRemove: invoked on group: " + nodeToRemove.getUserObject().toString() );
        SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) nodeToRemove.getParent();
        if ( nodeToRemove.deleteNode() ) {
            setSelectedNode( parentNode );
        }
    }


    /**
     * requests that a group's picture files be consolidated
     * @param node
     * @see  GroupPopupInterface
     */
    public void requestConsolidateGroup(
            SortableDefaultMutableTreeNode node ) {
        new ConsolidateGroupJFrame( node );
    }


    /**
     * Requests that a group be moved to the top
     * @param nodeToExport The node on which the request was made
     * @see  GroupPopupInterface
     */
    public void requestMoveGroupToTop( SortableDefaultMutableTreeNode popupNode ) {
        popupNode.moveNodeToTop();
    }


    /**
     * Requests that a group be moved up
     * @param nodeToExport The node on which the request was made
     * @see  GroupPopupInterface
     */
    public void requestMoveGroupUp( SortableDefaultMutableTreeNode popupNode ) {
        popupNode.moveNodeUp();
    }


    /**
     * Requests that a group be moved down
     * @param nodeToExport The node on which the request was made
     * @see  GroupPopupInterface
     */
    public void requestMoveGroupDown( SortableDefaultMutableTreeNode popupNode ) {
        popupNode.moveNodeDown();
    }


    /**
     * Requests that a group be moved down
     * @param nodeToExport The node on which the request was made
     * @see  GroupPopupInterface
     */
    public void requestMoveGroupToBottom(
            SortableDefaultMutableTreeNode popupNode ) {
        popupNode.moveNodeToBottom();


    }


    /**
     * Requests that a picture be moved to the target Group node
     * @param nodeToExport The node on which the request was made
     * @see  GroupPopupInterface
     */
    public void requestMoveToNode( SortableDefaultMutableTreeNode popupNode,
            SortableDefaultMutableTreeNode targetGroup ) {
        popupNode.moveToLastChild( targetGroup );


    }


    /**
     * Request that a group be edited as a table
     * @param nodeToExport The node on which the request was made
     */
    public void requestEditGroupTable( SortableDefaultMutableTreeNode popupNode ) {
        TableJFrame tableJFrame = new TableJFrame( popupNode );
        tableJFrame.pack();
        tableJFrame.setVisible( true );
    }


    /**
     * Gets called by the GroupPopupInterface and implements the sort request.
     * @param nodeToExport The node on which the request was made
     */
    public void requestSort( SortableDefaultMutableTreeNode popupNode,
            int sortCriteria ) {
        //logger.info( "Sort requested on " + myPopupNode.toString() + " for Criteria: " + Integer.toString( sortCriteria ) );
        popupNode.sortChildren( sortCriteria );
    }

    /**
     *  This class decides what to do with mouse events on the JTree.
     *  Since there is so much logic tied to what we are trying to do in the context
     *  of the JTree being the top left component here which might not be desirable in a different context
     *  this is kept as an inner class of the CollectionJTreeController.
     *  the groupPopupJPopupMenu menu must exist.
     **/
    private class CollectionMouseAdapter
            extends MouseAdapter {

        /**
         *  A reference back to the CollectionJTreeController for which this is a listener.
         */
        private CollectionJTreeController collectionJTreeController;


        private CollectionMouseAdapter(
                CollectionJTreeController collectionJTreeController ) {
            this.collectionJTreeController = collectionJTreeController;
        }


        /**
         *    If the mouse was clicked more than once using the left mouse button over a valid picture
         *    node then the picture editor is opened.
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
         *   Override the mousePressed event.
         */
        @Override
        public void mousePressed( MouseEvent e ) {
            maybeShowPopup( e );
        }


        /**
         *  Override the mouseReleased event.
         */
        @Override
        public void mouseReleased( MouseEvent e ) {
            maybeShowPopup( e );
        }


        /**
         *  This method figures out whether a popup window should be displayed and displays
         *  it.
         *  @param   e	The MouseEvent that was trapped.
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
