package jpo.gui;

import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import jpo.gui.swing.CollectionJTree;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.FlatFileDistiller;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.*;
import jpo.dataModel.PictureInfo;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.*;
import jpo.export.GenerateWebsiteWizard;

/*
CollectionJTreeController.java:  class that manages a JTree for the collection
 * 
Copyright (C) 2002 - 2009 Richard Eigenmann, Zurich, Switzerland
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
        implements DropTargetListener,
        DragSourceListener,
        DragGestureListener,
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

        // set up drag & drop
        dropTarget = new DropTarget( collectionJTree, this );
        dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer( collectionJTree, DnDConstants.ACTION_COPY_OR_MOVE, this );

        //Add listener to components that can bring up groupPopupJPopupMenu menus.
        CollectionMouseAdapter mouseAdapter = new CollectionMouseAdapter( this );
        collectionJTree.addMouseListener( mouseAdapter );

    }

    /**
     * enables this component to be a dropTarget
     */
    DropTarget dropTarget = null;

    /**
     * enables this component to be a Drag Source
     */
    DragSource dragSource = null;

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
     * @return
     */
    public JScrollPane getJScrollPane() {
        return collectionJScrollPane;
    }


    /**
     *  callback method that is called when a drag gesture has been initiated on a node of the JTree.
     *  @param  event    The Drag and Drop Framework gives us details about the detected event in this parameter.
     *
     */
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
    }


    /**
     *   this callback method is invoked every time something is
     *   dragged onto the JTree. We check if the desired DataFlavor is
     *   supported and then reject the drag if it is not.
     * @param event
     */
    public void dragEnter( DropTargetDragEvent event ) {
        if ( !event.isDataFlavorSupported( JpoTransferable.dmtnFlavor ) ) {
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
     */
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
     */
    public void dragOver( DropTargetDragEvent event ) {
        //logger.info("CollectionJTreeController.dragOver (DropTargetDragEvent) triggered");
        if ( !event.isDataFlavorSupported( JpoTransferable.dmtnFlavor ) ) {
            //logger.info("CollectionJTree.dragOver (DropTargetDragEvent): The dmtn DataFlavor is not supported. Rejecting drag.");
            event.rejectDrag();
        } else {
            // figure out where the cursor is and highlight the node by setting the selection path
            TreePath popupPath = collectionJTree.getPathForLocation( event.getLocation().x, event.getLocation().y );
            if ( popupPath != null ) {
                event.acceptDrag( DnDConstants.ACTION_COPY_OR_MOVE );
                //popupNode = (SortableDefaultMutableTreeNode) popupPath.getLastPathComponent();
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
     */
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
     */
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
     */
    public void dropActionChanged( DragSourceDragEvent event ) {
        //logger.info( "CollectionJTreeController.dropActionChanged( DragSourceDragEvent ): invoked");
        Tools.setDragCursor( event );
    }


    /**
     *   this callback method is invoked to tell the dropTarget that the drag has moved on
     *   to something else. We do nothing here.
     * @param event
     */
    public void dragExit( DropTargetEvent event ) {
        //logger.info("CollectionJTreeController.dragExit( DropTargetEvent ): invoked");
    }


    /**
     *   this callback method is invoked to tell the dragSource that the drag has moved on
     *   to something else. We do nothing here.
     * @param event
     */
    public void dragExit( DragSourceEvent event ) {
    }


    /**
     *  Entry point for the drop event. Figures out which node the drop occured on and
     *  sorts out the drop action in the data model.
     * @param event
     */
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
     */
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
     *  @see  GroupPopupInterface
     */
    public void requestSlideshow( SortableDefaultMutableTreeNode popupNode ) {
        Jpo.browsePictures( popupNode );
    }


    /**
     *  This method can be invoked by the GroupPopupMenu.
     *
     *  @see  GroupPopupInterface
     */
    public void requestFind( SortableDefaultMutableTreeNode popupNode ) {
        new QueryJFrame( popupNode, collectionController );
    }


    /**
     *  this method invokes an editor for the GroupInfo data
     *  @see  GroupPopupInterface
     */
    public void requestEditGroupNode( SortableDefaultMutableTreeNode popupNode ) {
        TreeNodeController.showEditGUI( popupNode );
    }


    /**
     *  this method invokes the Category editor and allows the user to set the categories for all the pictures in the Group.
     */
    public void showCategoryUsageGUI( SortableDefaultMutableTreeNode popupNode ) {
        TreeNodeController.showCategoryUsageGUI( popupNode );
    }


    /**
     *  requests a new empty group to be added.
     *  @see  GroupPopupInterface
     */
    public void requestAddGroup( SortableDefaultMutableTreeNode popupNode ) {
        SortableDefaultMutableTreeNode newNode = popupNode.addGroupNode( "New Group" );
        Settings.memorizeGroupOfDropLocation( newNode );
        setSelectedNode( newNode );
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
     *  requests that a collection be added at this point in the tree
     *  @param popupNode
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
     *  method that will bring up a dialog box that allows the user to select how he wants
     *  to export the pictures of the current Group.
     **/
    public void requestGroupExportHtml(SortableDefaultMutableTreeNode popupNode) {
        // new HtmlDistillerJFrame( myPopupNode );
        new GenerateWebsiteWizard( popupNode );
    }


    /**
     *  requests that the pictures indicated in a flat file be added at this point in the tree
     *  @see GroupPopupInterface
     */
    public void requestGroupExportFlatFile(SortableDefaultMutableTreeNode popupNode) {
        javax.swing.JFileChooser jFileChooser = new javax.swing.JFileChooser();
        jFileChooser.setFileSelectionMode( javax.swing.JFileChooser.FILES_ONLY );
        jFileChooser.setDialogTitle( Settings.jpoResources.getString( "saveFlatFileTitle" ) );
        jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "saveFlatFileButtonLabel" ) );
        jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );

        int returnVal = jFileChooser.showSaveDialog( Settings.anchorFrame );
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File chosenFile = jFileChooser.getSelectedFile();
            new FlatFileDistiller( chosenFile, popupNode );
        }

    }


    /**
     *  requests that a group be exported to a jar archive
     *  @see  GroupPopupInterface
     *
    public void requestGroupExportJar(SortableDefaultMutableTreeNode popupNode) {
//		new JarDistillerJFrame( myPopupNode );
    }*/


    /**
     *  requests that a group be exported to a new collectionjar archive
     *  @see  GroupPopupInterface
     */
    public void requestGroupExportNewCollection(SortableDefaultMutableTreeNode popupNode) {
        new CollectionDistillerJFrame( popupNode );
    }


    /**
     *  requests that a group be removed
     *  @see  GroupPopupInterface
     */
    public void requestGroupRemove(SortableDefaultMutableTreeNode popupNode) {
        logger.fine( "CollectionJTree.requestGroupRemove: invoked on group: " + popupNode.getUserObject().toString() );
        SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) popupNode.getParent();
        if ( popupNode.deleteNode() ) {
            setSelectedNode( parentNode );
        }
    }


    /**
     *  requests that a group's picture files be consolidated
     *  @see  GroupPopupInterface
     */
    public void requestConsolidateGroup(SortableDefaultMutableTreeNode popupNode) {
        new ConsolidateGroupJFrame( popupNode );
    }


    /**
     *  requests that a group be moved to the top
     *  @see  GroupPopupInterface
     */
    public void requestMoveGroupToTop(SortableDefaultMutableTreeNode popupNode) {
        popupNode.moveNodeToTop();
    }


    /**
     *  requests that a group be moved up
     *  @see  GroupPopupInterface
     */
    public void requestMoveGroupUp(SortableDefaultMutableTreeNode popupNode) {
        popupNode.moveNodeUp();
    }


    /**
     *  requests that a group be moved down
     *  @see  GroupPopupInterface
     */
    public void requestMoveGroupDown(SortableDefaultMutableTreeNode popupNode) {
        popupNode.moveNodeDown();
    }


    /**
     *  requests that a group be moved down
     *  @see  GroupPopupInterface
     */
    public void requestMoveGroupToBottom(SortableDefaultMutableTreeNode popupNode) {
        popupNode.moveNodeToBottom();
    }


    /**
     *  requests that a picture be moved to the target Group node
     *  @see  GroupPopupInterface
     */
    public void requestMoveToNode( SortableDefaultMutableTreeNode popupNode, SortableDefaultMutableTreeNode targetGroup ) {
        popupNode.moveToNode( targetGroup );
    }


    /**
     *  request that a group be edited as a table
     */
    public void requestEditGroupTable(SortableDefaultMutableTreeNode popupNode) {
        TableJFrame tableJFrame = new TableJFrame( popupNode );
        tableJFrame.pack();
        tableJFrame.setVisible( true );
    }


    /**
     *  gets called by the GroupPopupInterface and implements the sort request.
     */
    public void requestSort(SortableDefaultMutableTreeNode popupNode, int sortCriteria ) {
        //logger.info( "Sort requested on " + myPopupNode.toString() + " for Criteria: " + Integer.toString( sortCriteria ) );
        popupNode.sortChildren( sortCriteria );
        //( (DefaultTreeModel) collectionJTree.getModel() ).nodeStructureChanged( myPopupNode );
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
            if ( clickPath == null ) {
                return;
            } // happens
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
                    FlatGroupBrowser sb = new FlatGroupBrowser( (SortableDefaultMutableTreeNode) popupNode.getParent() );
                    int index = 0;
                    for ( int i = 0; i < sb.getNumberOfNodes(); i++ ) {
                        if ( sb.getNode( i ).equals( popupNode ) ) {
                            index = i;
                            i = sb.getNumberOfNodes();
                        }
                    }
                    PicturePopupMenu picturePopupMenu = new PicturePopupMenu( sb, index, null, collectionController );
                    picturePopupMenu.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        }
    }
}
