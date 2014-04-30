package jpo.gui;

import com.google.common.eventbus.Subscribe;
import jpo.gui.swing.GroupPopupMenu;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import static javax.swing.TransferHandler.COPY_OR_MOVE;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import jpo.EventBus.GroupSelectionEvent;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.RecentDropNodesChangedEvent;
import jpo.EventBus.ShowGroupRequest;
import jpo.EventBus.ShowPictureRequest;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.SingleNodeNavigator;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;
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
public class CollectionJTreeController {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( CollectionJTreeController.class.getName() );

    /**
     * The Controller class for the JTree. This class no longer extends the
     * JTree. Instead it is a plain simple old class that does all the things to
     * respond to or instruct the JTree to do and show.
     *
     */
    public CollectionJTreeController() {
        Tools.checkEDT();

        collectionJTree.setModel( Settings.getPictureCollection().getTreeModel() );
        collectionJTree.setEditable( true ); // doing this in the controller as it might not always be desired (like in the CameraDownloadWizard)
        collectionJTree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        collectionJTree.setTransferHandler( new MyTransferHandler() );
        collectionJTree.setDragEnabled( true );
        collectionJTree.setDropMode( DropMode.ON_OR_INSERT );
        ToolTipManager.sharedInstance().registerComponent( collectionJTree );

        collectionJScrollPane.setMinimumSize( Settings.JPO_NAVIGATOR_JTABBEDPANE_MINIMUM_SIZE );
        collectionJScrollPane.setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );

        CollectionMouseAdapter mouseAdapter = new CollectionMouseAdapter( this );
        collectionJTree.addMouseListener( mouseAdapter );
        registerOnEventBus();
    }

    private void registerOnEventBus() {
        JpoEventBus.getInstance().register( this );
    }

    /**
     * When the tree receives a GroupSelectionEvent it will expand the treepath
     * to show the node that was selected.
     *
     * @param event The GroupSelectionEvent
     */
    @Subscribe
    public void handleGroupSlectionEvent( GroupSelectionEvent event ) {
        expandAndScroll( event.getNode() );
    }

    /**
     * When the tree receives a ShowGroupRequest it will expand the treepath to
     * show the node that was selected.
     *
     * @param request The ShowGroupRequest
     */
    @Subscribe
    public void handleShowGroupRequest( ShowGroupRequest request ) {
        expandAndScroll( request.getNode() );
    }

    /**
     * Expands the nodes and scroll the tree so that the indicated node is
     * visible.
     *
     * @param node
     */
    private void expandAndScroll( SortableDefaultMutableTreeNode node ) {
        final TreePath tp = new TreePath( node.getPath() );
        Runnable r = new Runnable() {

            @Override
            public void run() {
                collectionJTree.expandPath( tp );
                collectionJTree.scrollPathToVisible( tp );
                collectionJTree.setSelectionPath( tp );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }
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
                JpoEventBus.getInstance().post( new RecentDropNodesChangedEvent() );
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
            if ( collectionJTree.getRowForLocation( mouseEvent.getX(), mouseEvent.getY() ) == -1 ) {
                return null;
            }
            TreePath curPath = collectionJTree.getPathForLocation( mouseEvent.getX(), mouseEvent.getY() );
            SortableDefaultMutableTreeNode node = (SortableDefaultMutableTreeNode) curPath.getLastPathComponent();
            Object userObject = node.getUserObject();
            String toolTip = "";
            if ( userObject instanceof GroupInfo ) {
                GroupInfo groupInfo = (GroupInfo) userObject;
                toolTip = String.format( "<html>Group: %s</html>", groupInfo.getGroupName() );
            } else if ( userObject instanceof PictureInfo ) {
                final PictureInfo pictureInfo = (PictureInfo) userObject;
                File highresFile = pictureInfo.getHighresFile();
                String fileSize = highresFile == null ? "no file" : Tools.fileSizeToString( highresFile.length() );
                toolTip = String.format( "<html>Picture: %s<br>%s %s</html>", pictureInfo.getDescription(), Settings.jpoResources.getString( "CollectionSizeJLabel" ), fileSize );
            }
            return toolTip;
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

        private CollectionMouseAdapter(
                CollectionJTreeController collectionJTreeController ) {
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
                    JpoEventBus.getInstance().post( new ShowGroupRequest( clickNode ) );
                }
            } else if ( e.getClickCount() > 1 && ( !e.isPopupTrigger() ) ) {
                //Jpo.browsePictures( clickNode );
                JpoEventBus.getInstance().post( new ShowPictureRequest( clickNode ) );
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
                            GroupPopupMenu groupPopupMenu = new GroupPopupMenu( popupNode );
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
