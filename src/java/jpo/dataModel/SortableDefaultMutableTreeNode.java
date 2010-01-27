package jpo.dataModel;

import jpo.gui.ThumbnailController;
import jpo.gui.ThumbnailCreationQueue;
import jpo.gui.ThumbnailQueueRequest;
import jpo.*;
import jpo.gui.JpoTransferable;
import jpo.gui.ProgressGui;
import javax.swing.tree.*;
import java.util.*;
import javax.swing.event.TreeModelEvent;
import java.net.*;
import java.io.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import com.drew.metadata.*;
import com.drew.metadata.exif.*;
import com.drew.metadata.iptc.*;
import com.drew.imaging.jpeg.*;
import java.awt.event.*;
import java.util.logging.Logger;
import javax.swing.*;


/*
SortableDefaultMutableTreeNode.java:  A DefaultMutableTreeNode that knows how to compare my objects

Copyright (C) 2003 - 2009  Richard Eigenmann, Zurich, Switzerland
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
 *  This class makes up the nodes of the JPO Collection.
 *  It extends the DefaultMutableTreeNode with the Comparable
 *  Interface that allows our nodes to be compared.
 */
public class SortableDefaultMutableTreeNode
        extends DefaultMutableTreeNode
        implements Comparable, Serializable, PictureInfoChangeListener {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( SortableDefaultMutableTreeNode.class.getName() );


    /**
     *   Constructor for a new node.
     */
    public SortableDefaultMutableTreeNode() {
        super();
    }


    /**
     * Constructor for a new node including a user object.
     * @param userObject
     */
    public SortableDefaultMutableTreeNode( Object userObject ) {
        super();
        setUserObject( userObject );
    }


    /**
     *   returns the collection associated with this node
     * @return
     */
    public PictureCollection getPictureCollection() {
        return Settings.pictureCollection;
    }


    /**
     *  Call this method to sort the Children of a node by a field. The value of sortCriteria can be one of
     *   {@link Settings#DESCRIPTION}, {@link Settings#FILM_REFERENCE}, {@link Settings#CREATION_TIME},
     *   {@link Settings#COMMENT}, {@link Settings#PHOTOGRAPHER}, {@link Settings#COPYRIGHT_HOLDER}.
     *  @param  sortCriteria The criteria by which the pictures should be sorted.
     */
    public void sortChildren( int sortCriteria ) {
        int childCount = getChildCount();
        SortableDefaultMutableTreeNode[] childNodes = new SortableDefaultMutableTreeNode[childCount];
        for ( int i = 0; i < childCount; i++ ) {
            childNodes[i] = (SortableDefaultMutableTreeNode) getChildAt( i );
        }

        // sort the array
        sortfield = sortCriteria;
        Arrays.sort( childNodes );

        //Remove all children from the node
        getPictureCollection().setSendModelUpdates( false );
        removeAllChildren();
        //Add the new array of nodes to top
        for ( int i = 0; i < childNodes.length; i++ ) {
            add( childNodes[i] );
        }
        getPictureCollection().setUnsavedUpdates();
        getPictureCollection().setSendModelUpdates( true );
        refreshThumbnail();

        // tell the collection that the structure changed
        final SortableDefaultMutableTreeNode nodeStructureChangedNode = this;
        Runnable r = new Runnable() {

            public void run() {
                logger.fine( String.format( "Sending node structure changed event on node %s after sort", nodeStructureChangedNode.toString() ) );
                getPictureCollection().getTreeModel().nodeStructureChanged( nodeStructureChangedNode );
            }
        };
        SwingUtilities.invokeLater( r );


    }

    /**
     *  This field records the field by which the group is to be sorted. This is not very
     *  elegant as a second sort could run at the same time and clobber this global variable.
     *  But that's not very likely on a single user app like this.
     */
    private static int sortfield;


    /**
     *   Overridden method to allow sorting of nodes. It uses the static global variable
     *   sortfield to figure out what to compare on. The value of sortfield can be one of
     *   {@link Settings#DESCRIPTION}, {@link Settings#FILM_REFERENCE}, {@link Settings#CREATION_TIME},
     *   {@link Settings#COMMENT}, {@link Settings#PHOTOGRAPHER}, {@link Settings#COPYRIGHT_HOLDER}.
     *
     * @param o
     * @return the usual compareTo value used for sorting.
     */
    public int compareTo( Object o ) {
        Object myObject = getUserObject();
        Object otherObject = ( (DefaultMutableTreeNode) o ).getUserObject();
        //logger.info( "Comparing " + myObject.toString() + " against " + otherObject.toString() );

        if ( ( myObject instanceof GroupInfo ) && ( otherObject instanceof GroupInfo ) && ( sortfield == Settings.DESCRIPTION ) ) {
            return ( (GroupInfo) myObject ).getGroupName().compareTo( ( (GroupInfo) otherObject ).getGroupName() );
        }

        if ( ( myObject instanceof GroupInfo ) && ( otherObject instanceof PictureInfo ) && ( sortfield == Settings.DESCRIPTION ) ) {
            return ( (GroupInfo) myObject ).getGroupName().compareTo( ( (PictureInfo) otherObject ).getDescription() );
        }

        if ( ( myObject instanceof PictureInfo ) && ( otherObject instanceof GroupInfo ) && ( sortfield == Settings.DESCRIPTION ) ) {
            return ( (PictureInfo) myObject ).getDescription().compareTo( ( (GroupInfo) otherObject ).getGroupName() );
        }

        if ( ( myObject instanceof GroupInfo ) || ( otherObject instanceof GroupInfo ) ) {
            // we can't compare Groups against the other types of field other than the description.
            return 0;
        }

        // at this point there can only two PictureInfo Objects
        PictureInfo myPi = (PictureInfo) myObject;
        PictureInfo otherPi = (PictureInfo) otherObject;
        switch ( sortfield ) {
            case Settings.DESCRIPTION:
                return myPi.getDescription().compareTo( otherPi.getDescription() );
            case Settings.FILM_REFERENCE:
                return myPi.getFilmReference().compareTo( otherPi.getFilmReference() );
            case Settings.CREATION_TIME:
                return myPi.getCreationTime().compareTo( otherPi.getCreationTime() );
            case Settings.COMMENT:
                return myPi.getComment().compareTo( otherPi.getComment() );
            case Settings.PHOTOGRAPHER:
                return myPi.getPhotographer().compareTo( otherPi.getPhotographer() );
            case Settings.COPYRIGHT_HOLDER:
                return myPi.getCopyrightHolder().compareTo( otherPi.getCopyrightHolder() );
            default:
                return myPi.getDescription().compareTo( otherPi.getDescription() );
        }
    }


    /**
     *  Returns the first node with a picture before the current one in the tree.
     *  It uses the getPreviousNode method of DefaultMutableTreeNode.
     *
     *  @return	the first node with a picture in preorder traversal or null if none found.
     */
    public SortableDefaultMutableTreeNode getPreviousPicture() {
        DefaultMutableTreeNode prevNode = getPreviousNode();
        while ( ( prevNode != null ) && ( !( prevNode.getUserObject() instanceof PictureInfo ) ) ) {
            prevNode = prevNode.getPreviousNode();
        }
        return (SortableDefaultMutableTreeNode) prevNode;
    }


    /**
     *   Returns the next node with a picture found after current one in the tree. This can
     *   be in another Group.
     *   It uses the getNextNode method of the DefaultMutableTreeNode.
     *
     *   @return The SortableDefaultMutableTreeNode that represents the next
     *		 picture. If no picture can be found it returns null.
     *
     */
    public SortableDefaultMutableTreeNode getNextPicture() {
        DefaultMutableTreeNode nextNode = getNextNode();
        while ( ( nextNode != null ) && ( !( nextNode.getUserObject() instanceof PictureInfo ) ) ) {
            nextNode = nextNode.getNextNode();
        }
        return (SortableDefaultMutableTreeNode) nextNode;
    }


    /**
     *   Returns the next node with a picture found after current one in the current Group
     *   It uses the getNextSibling method of the DefaultMutableTreeNode.
     *
     *   @return The SortableDefaultMutableTreeNode that represents the next
     *		 picture. If no picture can be found it returns null.
     *
     */
    public SortableDefaultMutableTreeNode getNextGroupPicture() {
        DefaultMutableTreeNode nextNode = getNextSibling();
        while ( ( nextNode != null ) && ( !( nextNode.getUserObject() instanceof PictureInfo ) ) ) {
            nextNode = nextNode.getNextNode();
        }
        return (SortableDefaultMutableTreeNode) nextNode;
    }


    /**
     *  Returns the first child node under the current node which holds a PictureInfo object.
     *
     *  @return  The first child node holding a picture or null if none can be found.
     */
    public SortableDefaultMutableTreeNode findFirstPicture() {
        SortableDefaultMutableTreeNode testNode;
        Enumeration e = children();
        while ( e.hasMoreElements() ) {
            testNode = (SortableDefaultMutableTreeNode) e.nextElement();
            if ( testNode.getUserObject() instanceof PictureInfo ) {
                return testNode;
            } else if ( testNode.getUserObject() instanceof GroupInfo ) {
                testNode = testNode.findFirstPicture();
                if ( testNode != null ) {
                    return testNode;
                }
            }
        }
        return null;
    }


    /**
     *  This method collects all pictures under the current node and returns them as an Array List..
     *
     *  @param  pictureNodes   The ArrayList to which to add the picture nodes.
     *  @param recursive Whether to add the pictures of any groups nodes or not
     */
    public void getChildPictureNodes(
            ArrayList<SortableDefaultMutableTreeNode> pictureNodes,
            boolean recursive ) {
        Enumeration kids = this.children();
        SortableDefaultMutableTreeNode n;

        while ( kids.hasMoreElements() ) {
            n = (SortableDefaultMutableTreeNode) kids.nextElement();
            if ( recursive && n.getUserObject() instanceof GroupInfo ) {
                n.getChildPictureNodes( pictureNodes, recursive );
            } else if ( n.getUserObject() instanceof PictureInfo ) {
                pictureNodes.add( n );
            }
        }
    }


    /**
     *  This method is being overridden to allow us to capture editing events on the JTree that is rendering this node.
     *  The TreeCellEditor will send the changed label as a String type object to the setUserObject method of this class.
     *  My overriding this we can intercept this and update the PictureInfo or GroupInfo accordingly.
     * @param o
     */
    @Override
    public void setUserObject( Object o ) {
        //logger.info( "setUserObject fired with o: " + o.toString() + " of class: " + o.getClass().toString() );
        if ( o instanceof String ) {
            logger.severe( "Why is ever being called?" );
            Object obj = getUserObject();
            if ( obj instanceof GroupInfo ) {
                ( (GroupInfo) obj ).setGroupName( (String) o );
            } else if ( obj instanceof PictureInfo ) {
                ( (PictureInfo) obj ).setDescription( (String) o );
            }
        } else if ( o instanceof PictureInfo ) {
            PictureInfo pi = (PictureInfo) o;
            Object oldUserObject = getUserObject();
            if ( oldUserObject != null ) {
                if ( oldUserObject instanceof PictureInfo ) {
                    PictureInfo oldPi = (PictureInfo) oldUserObject;
                    oldPi.removePictureInfoChangeListener( this );
                }
            }
            pi.addPictureInfoChangeListener( this );
            super.setUserObject( o );
        } else {
            // fall back on the default behaviour
            super.setUserObject( o );
        }
        if ( getPictureCollection() != null ) {
            if ( getPictureCollection().getSendModelUpdates() ) {
                getPictureCollection().getTreeModel().nodeStructureChanged( this );
            }
        }
    }


    /**
     *   This method is called by the drop method of the DragTarget to do the
     *   move. It deals with the intricacies of the drop event and handles all
     *   the moving, cloning and positioning that is required.
     *
     *   @param event The event the listening object received.
     */
    public void executeDrop( DropTargetDropEvent event ) {
        //logger.info( "SDMTN.executeDrop: invoked");

        if ( !event.isLocalTransfer() ) {
            logger.info( "SDMTN.executeDrop: detected that the drop is not a local Transfer. These are not supported. Aborting drop." );
            event.rejectDrop();
            event.dropComplete( false );
            return;
        }

        if ( !event.isDataFlavorSupported( JpoTransferable.dmtnFlavor ) ) {
            logger.info( "SDMTN.executeDrop: The local drop does not support the dmtnFlavor DataFlavor. Drop rejected." );
            event.rejectDrop();
            event.dropComplete( false );
            return;
        }


        int actionType = event.getDropAction();
        if ( ( actionType == DnDConstants.ACTION_MOVE ) || ( actionType == DnDConstants.ACTION_COPY ) ) {
            event.acceptDrop( actionType );   // crucial Step!
        } else {
            logger.info( "SDMTN.executeDrop: The event has an odd Action Type. Drop rejected." );
            event.rejectDrop();
            event.dropComplete( false );
            return;
        }


        SortableDefaultMutableTreeNode sourceNode;
        int originalHashCode;
        Object[] arrayOfNodes;

        try {
            Transferable t = event.getTransferable();
            Object o = t.getTransferData( JpoTransferable.dmtnFlavor );
            arrayOfNodes = (Object[]) o;
        } catch ( java.awt.datatransfer.UnsupportedFlavorException x ) {
            logger.info( "SDMTN.executeDrop caught an UnsupportedFlavorException: message: " + x.getMessage() );
            event.dropComplete( false );
            return;
        } catch ( java.io.IOException x ) {
            logger.info( "SDMTN.executeDrop caught an IOException: message: " + x.getMessage() );
            event.dropComplete( false );
            return;
        } catch ( ClassCastException x ) {
            logger.info( "SDMTN.executeDrop caught an ClassCastException: message: " + x.getMessage() );
            event.dropComplete( false );
            return;
        }


        /* We must ensure that if the action is a move it does not drop into
        itself or into a child of itself. */
        for ( int i = 0; i < arrayOfNodes.length; i++ ) {
            sourceNode = (SortableDefaultMutableTreeNode) arrayOfNodes[i];
            if ( this.isNodeAncestor( sourceNode ) ) {
                JOptionPane.showMessageDialog( Settings.anchorFrame,
                        Settings.jpoResources.getString( "moveNodeError" ),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
                event.dropComplete( false );
                return;
            }
        }



        // The drop is a valid one.

        //  memorise the group of the drop location.
        SortableDefaultMutableTreeNode groupOfDropLocation;
        if ( this.getUserObject() instanceof GroupInfo ) {
            groupOfDropLocation = this;
        } else {
            // the parent must be a group node
            groupOfDropLocation = (SortableDefaultMutableTreeNode) this.getParent();
        }
        if ( ( groupOfDropLocation != null ) && ( groupOfDropLocation.getUserObject() instanceof GroupInfo ) ) {
            Settings.memorizeGroupOfDropLocation( groupOfDropLocation );
        } else {
            logger.info( "SDMTN.executeDrop failed to find the group of the drop location. Not memorizing." );
        }


        boolean dropcomplete = false;
        for ( int i = 0; i < arrayOfNodes.length; i++ ) {
            sourceNode = (SortableDefaultMutableTreeNode) arrayOfNodes[i];

            if ( ( sourceNode.getUserObject() instanceof PictureInfo ) && ( this.getUserObject() instanceof GroupInfo ) ) {
                // a picture is being dropped onto a group; add it at the end
                if ( actionType == DnDConstants.ACTION_MOVE ) {
                    logger.info( "Moving Picture onto Group --> add picture to bottom of group" );
                    sourceNode.removeFromParent();  //SDTMN removeFromParents fire the model notification
                    add( sourceNode );  //SDTMN adds fire the model notifications
                } else {
                    // it was a copy event
                    SortableDefaultMutableTreeNode newNode = new SortableDefaultMutableTreeNode( ( (PictureInfo) sourceNode.getUserObject() ).getClone() );
                    add( newNode );
                }
                dropcomplete = true;
                getPictureCollection().setUnsavedUpdates();
            } else if ( ( sourceNode.getUserObject() instanceof PictureInfo ) && ( this.getUserObject() instanceof PictureInfo ) ) {
                // a picture is being dropped onto a picture

                // insert the new Node in front of the current node.
                SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
                int currentIndex = parentNode.getIndex( this );

                // position is one less if the source is further up the list than the target
                // and at the same level
                int offset = 0;
                if ( isNodeSibling( sourceNode ) ) {
                    //logger.info ("The target is a sibling of the sourceNode");
                    if ( parentNode.getIndex( sourceNode ) < parentNode.getIndex( this ) ) {
                        offset = -1;
                    }
                }

                if ( actionType == DnDConstants.ACTION_MOVE ) {
                    logger.info( "Moving Picture onto Picture --> move to current spot" );
                    sourceNode.removeFromParent();  //SDTMN removeFromParents fire the model notification
                    parentNode.insert( sourceNode, currentIndex + offset );
                } else {
                    // it was a copy event
                    SortableDefaultMutableTreeNode newNode = new SortableDefaultMutableTreeNode( ( (PictureInfo) sourceNode.getUserObject() ).getClone() );
                    parentNode.insert( newNode, currentIndex + offset );
                }
                dropcomplete = true;
                getPictureCollection().setUnsavedUpdates();
            } else {
                //logger.info("SDMTN.executeDrop: we are dropping a GroupInfo object");
                // we are dropping a GroupInfo object; all others move down one step.
                // find out at which index to insert the group
                if ( !this.isRoot() ) {

                    GroupDropPopupMenu groupDropPopupMenu = new GroupDropPopupMenu( event, sourceNode, this );
                    groupDropPopupMenu.show( event.getDropTargetContext().getDropTarget().getComponent(), event.getLocation().x, event.getLocation().y );
                } else {
                    // Group was dropped on the root node --> add at first place.
                    sourceNode.removeFromParent();
                    this.insert( sourceNode, 0 );
                    dropcomplete = true;
                    getPictureCollection().setUnsavedUpdates();
                }
            }
        }
        event.dropComplete( dropcomplete );


    }


    /**
     * This is where the Nodes in the tree find out about changes in the PictureInfo object
     * @param e
     */
    public void pictureInfoChangeEvent( PictureInfoChangeEvent e ) {
        logger.fine( String.format( "The SDMTN %s received a PictureInfoChangeEvent %s", this.toString(), e.toString() ) );
        if ( SwingUtilities.isEventDispatchThread() ) {
            getPictureCollection().getTreeModel().nodeChanged( this );
        } else {
            final SortableDefaultMutableTreeNode finalNode = this;
            Runnable r = new Runnable() {

                public void run() {
                    getPictureCollection().getTreeModel().nodeChanged( finalNode );
                }
            };
            SwingUtilities.invokeLater( r );
        }
    }

    /**
     *   This inner class creates a popup menu for group drop events to find out whether to drop
     *   into before or after the drop node.
     */
    class GroupDropPopupMenu
            extends JPopupMenu {

        /**
         *  menu item that allows the user to edit the group description
         **/
        private JMenuItem dropBefore = new JMenuItem( Settings.jpoResources.getString( "GDPMdropBefore" ) );

        /**
         *  menu item that allows the user to edit the group description
         **/
        private JMenuItem dropAfter = new JMenuItem( Settings.jpoResources.getString( "GDPMdropAfter" ) );

        /**
         *  menu item that allows the user to edit the group description
         **/
        private JMenuItem dropIntoFirst = new JMenuItem( Settings.jpoResources.getString( "GDPMdropIntoFirst" ) );


        ;

        /**
         *  menu item that allows the user to edit the group description
         **/
        private JMenuItem dropIntoLast = new JMenuItem( Settings.jpoResources.getString( "GDPMdropIntoLast" ) );


        ;

        /**
         *  menu item that allows the user to edit the group description
         **/
        private JMenuItem dropCancel = new JMenuItem( Settings.jpoResources.getString( "GDPMdropCancel" ) );


        ;


        /**
         *   This inner class creates a popup menu for group drop events to find out whether to drop
         *   into before or after the drop node.
         * TODO: Doesn't really belong here from a MVC perspective...
         */
        public GroupDropPopupMenu( final DropTargetDropEvent event,
                final SortableDefaultMutableTreeNode sourceNode,
                final SortableDefaultMutableTreeNode targetNode ) {
            dropBefore.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) targetNode.getParent();
                    int currentIndex = parentNode.getIndex( targetNode );

                    // position is one less if the source is further up the list than the target
                    // and at the same level
                    int offset = 0;
                    if ( targetNode.isNodeSibling( sourceNode ) ) {
                        //logger.info ("The target is a sibling of the sourceNode");
                        if ( parentNode.getIndex( sourceNode ) < parentNode.getIndex( targetNode ) ) {
                            offset = -1;
                        }
                    }
                    sourceNode.removeFromParent();
                    parentNode.insert( sourceNode, currentIndex + offset );

                    event.dropComplete( true );
                    getPictureCollection().setUnsavedUpdates();
                }
            } );
            add( dropBefore );

            dropAfter.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) targetNode.getParent();
                    int currentIndex = parentNode.getIndex( targetNode );

                    // position is one less if the source is further up the list than the target
                    // and at the same level
                    int offset = 0;
                    if ( targetNode.isNodeSibling( sourceNode ) ) {
                        //logger.info ("The target is a sibling of the sourceNode");
                        if ( parentNode.getIndex( sourceNode ) < parentNode.getIndex( targetNode ) ) {
                            offset = -1;
                        }
                    }
                    sourceNode.removeFromParent();
                    parentNode.insert( sourceNode, currentIndex + offset + 1 );

                    event.dropComplete( true );
                    getPictureCollection().setUnsavedUpdates();
                }
            } );
            add( dropAfter );

            dropIntoFirst.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    sourceNode.removeFromParent();
                    targetNode.insert( sourceNode, 0 );

                    event.dropComplete( true );
                    getPictureCollection().setUnsavedUpdates();
                }
            } );
            add( dropIntoFirst );

            dropIntoLast.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    int childCount = targetNode.getChildCount();
                    int offset = 0;
                    if ( childCount > 0 ) {
                        // position is one less if the source is further up the list than the target
                        // and at the same level
                        if ( targetNode.isNodeSibling( sourceNode.getFirstChild() ) ) {
                            offset = -1;
                        }
                    }

                    sourceNode.removeFromParent();
                    targetNode.insert( sourceNode, childCount + offset );

                    event.dropComplete( true );
                    getPictureCollection().setUnsavedUpdates();
                }
            } );
            add( dropIntoLast );


            dropCancel.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    logger.info( "cancel drop" );
                    event.dropComplete( false );
                }
            } );
            add( dropCancel );
        }
    }


    /**
     *  This method adds the specified flat file of images at the current node.
     * @param chosenFile
     * @throws IOException
     */
    public void addFlatFile( File chosenFile ) throws IOException {
        SortableDefaultMutableTreeNode newNode =
                new SortableDefaultMutableTreeNode(
                new GroupInfo( chosenFile.getName() ) );
        this.add( newNode );
        BufferedReader in = new BufferedReader( new FileReader( chosenFile ) );
        String sb = new String();
        while ( in.ready() ) {
            sb = in.readLine();
            File testFile = null;
            try {
                testFile = new File( new URI( sb ) );
            } catch ( URISyntaxException x ) {
                logger.info( "Conversion of " + sb + " to URI failed: " + x.getMessage() );
            } catch ( IllegalArgumentException x ) {
                logger.info( "Conversion of " + sb + " to URI failed: " + x.getMessage() );
            }

            // dangerous but it doesn't continue if the first condition is true

            if ( ( testFile != null ) && ( testFile.canRead() ) ) {
                //logger.info ( "Adding picture: " + sb );
                SortableDefaultMutableTreeNode newPictureNode = new SortableDefaultMutableTreeNode(
                        new PictureInfo(
                        sb,
                        Tools.lowresFilename(),
                        Tools.stripOutFilenameRoot( testFile ),
                        "" ) );
                newNode.add( newPictureNode );
            } else {
                logger.info( "Not adding picture: " + sb + " because it can't be read" );
            }
        }
        in.close();
        getPictureCollection().getTreeModel().nodeStructureChanged( this );
        getPictureCollection().setUnsavedUpdates( false );
    }


    /**
     *  This method removes the designated SortableDefaultMutableTreeNode from the tree.
     *  The parent node is made the currently selected node.
     *
     *  @return	true if successful, false if not
     *
     */
    public boolean deleteNode() {
        logger.fine( "SDMTN.deleteNode: invoked on: " + this.toString() );
        if ( this.isRoot() ) {
            logger.info( "SDMTN.deleteNode: attempted on Root node. Can't do this! Aborted." );
            JOptionPane.showMessageDialog( null, //very annoying if the main window is used as it forces itself into focus.
                    Settings.jpoResources.getString( "deleteRootNodeError" ),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return false;
        }
        SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
        getPictureCollection().setUnsavedUpdates();

        int[] childIndices = { parentNode.getIndex( this ) };
        Object[] removedChildren = { this };

        //this.removeFromSelection();

        super.removeFromParent();

        if ( getPictureCollection().getSendModelUpdates() ) {
            getPictureCollection().getTreeModel().nodesWereRemoved( parentNode, childIndices, removedChildren );
        }

        /**  removeThumbnailRequest the move targets here **/
        Enumeration e = this.breadthFirstEnumeration();
        SortableDefaultMutableTreeNode testNode;
        while ( e.hasMoreElements() ) {
            Settings.removeRecentDropNode( (SortableDefaultMutableTreeNode) e.nextElement() );
        }

        return true;
    }


    /**
     *   Overridden method which will do the default behaviour and then sends a notification to
     *   the Tree Model.
     */
    @Override
    public void removeFromParent() {
        //logger.info( "SDMTN.removeFromParent was called for node: " + this.toString() );
        SortableDefaultMutableTreeNode oldParentNode = (SortableDefaultMutableTreeNode) this.getParent();
        int oldParentIndex = oldParentNode.getIndex( this );
        //logger.info( "SDMTN.removeFromParent: Currentnode: " + this.toString() + " Parent Node:" + oldParentNode.toString() );
        super.removeFromParent();
        if ( getPictureCollection().getSendModelUpdates() ) {
            getPictureCollection().getTreeModel().nodesWereRemoved( oldParentNode,
                    new int[] { oldParentIndex },
                    new Object[] { this } );
        }
    }


    /**
     *   This method adds a new node to the data model of the tree. It is the overridden add
     *   method which will first do the default behaviour and then send a notification to
     *   the Tree Model if model updates are being requested. Likewise the unsaved changes
     *   of the collection are only being updated when model updates are not being reported.
     *   This allows the loading of collections (which of course massively change the collection
     *   in memory) to report nothing changed.
     * @param newNode
     */
    public void add( SortableDefaultMutableTreeNode newNode ) {
        logger.fine( String.format( "Adding a new node %s to the node %s", newNode.toString(), this.toString() ) );
        super.add( newNode );
        if ( getPictureCollection().getSendModelUpdates() ) {
            int index = this.getIndex( newNode );
            getPictureCollection().getTreeModel().nodesWereInserted( this, new int[] { index } );
            getPictureCollection().setUnsavedUpdates();
        }
    }


    /**
     *   Overriden method which will do the default behaviour and then sends a notification to
     *   the Tree Model.
     * @param node
     * @param index
     */
    public void insert( SortableDefaultMutableTreeNode node, int index ) {
        logger.fine( "insert was called for node: " + node.toString() );
        super.insert( node, index );
        if ( getPictureCollection().getSendModelUpdates() ) {
            getPictureCollection().getTreeModel().nodesWereInserted( this, new int[] { index } );
        }
    }


    /**
     *   Validates the target of the picture copy instruction and tries to find the
     *   appropriate thing to do. It does the following steps:<br>
     *   1. If any input is null the copy is aborted with an error dialog.<br>
     *   2: If the target is a directory the filename of the original is used.<br>
     *   3: If the target is an existing file the copy is aborted<br>
     *   4: If the target directory doesn't exist then the directories are created.<br>
     *   5: The file extension is made to be that of the original if it isn't already that.<br>
     *   When all preconditions are met the image is copied
     *
     *  //TODO should throw exceptions instead of doing dialogs
     *
     *   @param targetFile  The target location for the new Picture.
     */
    public void validateAndCopyPicture( File targetFile ) {
        if ( ( this == null ) || ( targetFile == null ) ) {
            JOptionPane.showMessageDialog( Settings.anchorFrame,
                    Settings.jpoResources.getString( "CopyImageNullError" ),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        if ( !( this.getUserObject() instanceof PictureInfo ) ) {
            logger.info( "SDMTN.copyToNewLocation: inkoked on a non PictureInfo type node! Aborted." );
            return;
        }


        URL originalUrl;
        try {
            originalUrl = ( (PictureInfo) this.getUserObject() ).getHighresURL();
        } catch ( MalformedURLException x ) {
            logger.info( "MarformedURLException trapped on: " + ( (PictureInfo) this.getUserObject() ).getHighresLocation() + "\nReason: " + x.getMessage() );
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    "MarformedURLException trapped on: " + ( (PictureInfo) this.getUserObject() ).getHighresLocation() + "\nReason: " + x.getMessage(),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        if ( targetFile.exists() ) {
            if ( !targetFile.isDirectory() ) {
                try {
                    String sourceFilename = new File( new URI( originalUrl.toString() ) ).getName();
                    targetFile = Tools.inventPicFilename( targetFile.getParentFile(), sourceFilename );
                    //logger.info("JTree:validateAndCopyPicture: originalUrl: " + originalUrl.toString() + "\nsourceFilename: " + sourceFilename + "  targetFile: " + targetFile.toString());
                } catch ( URISyntaxException x ) {
                    JOptionPane.showMessageDialog( Settings.anchorFrame,
                            "URISyntaxException: " + x,
                            Settings.jpoResources.getString( "genericError" ),
                            JOptionPane.ERROR_MESSAGE );
                    return;
                }
            }
        } else {
            // it doesn't exist
            if ( !targetFile.mkdirs() ) {
                JOptionPane.showMessageDialog( Settings.anchorFrame,
                        Settings.jpoResources.getString( "CopyImageDirError" ) + targetFile.toString(),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
                return;
            }
        }


        if ( targetFile.isDirectory() ) {
            try {
                String sourceFilename = new File( new URI( originalUrl.toString() ) ).getName();
                targetFile = Tools.inventPicFilename( targetFile, sourceFilename );
            } catch ( URISyntaxException x ) {
                JOptionPane.showMessageDialog( Settings.anchorFrame,
                        "URISyntaxException: " + x,
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
                return;
            }
        }

        targetFile = Tools.correctFilenameExtension( Tools.getExtension( originalUrl ), targetFile );

        if ( !targetFile.getParentFile().exists() ) {
            targetFile.getParentFile().mkdirs();
        }

        Tools.copyPicture( originalUrl, targetFile );
        Settings.memorizeCopyLocation( targetFile.getParent() );
    }


    /**
     *  When this method is invoked on a node it is moved to the first child position of it's parent node.
     */
    public void moveNodeToTop() {
        if ( this.isRoot() ) {
            return;  // don't do anything with a root node.
        }
        SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
        // abort if this action was attempted on the top node
        if ( parentNode.getIndex( this ) < 1 ) {
            return;
        }
        this.removeFromParent();
        parentNode.insert( this, 0 );
        getPictureCollection().setUnsavedUpdates();
    }


    /**
     *  When this method is invoked on a node it moves itself one position up towards the first
     *  child position of it's parent node.
     */
    public void moveNodeUp() {
        if ( this.isRoot() ) {
            return;  // don't do anything with a root node.
        }
        SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
        int currentIndex = parentNode.getIndex( this );
        // abort if this action was attempted on the top node or not a child
        if ( currentIndex < 1 ) {
            return;
        }
        this.removeFromParent();
        parentNode.insert( this, currentIndex - 1 );
        getPictureCollection().setUnsavedUpdates();
    }


    /**
     *  Method that moves a node down one position
     */
    public void moveNodeDown() {
        if ( this.isRoot() ) {
            return;  // don't do anything with a root node.
        }
        SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
        int childCount = parentNode.getChildCount();
        int currentIndex = parentNode.getIndex( this );
        // abort if this action was attempted on the bootom node
        if ( ( currentIndex == -1 ) ||
                ( currentIndex == childCount - 1 ) ) {
            return;
        }
        this.removeFromParent();
        parentNode.insert( this, currentIndex + 1 );
        getPictureCollection().setUnsavedUpdates();
    }


    /**
     *  Method that moves a node to the bottom of the current branch
     */
    public void moveNodeToBottom() {
        if ( this.isRoot() ) {
            return;  // don't do anything with a root node.
        }

        SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
        int childCount = parentNode.getChildCount();
        // abort if this action was attempted on the bootom node
        if ( ( parentNode.getIndex( this ) == -1 ) ||
                ( parentNode.getIndex( this ) == childCount - 1 ) ) {
            return;
        }
        this.removeFromParent();
        parentNode.insert( this, childCount - 1 );
        getPictureCollection().setUnsavedUpdates();
    }


    /**
     *  When this method is invoked on a node it becomes a sub-node of it's preceeding group.
     */
    public void indentNode() {
        if ( this.isRoot() ) {
            return;  // don't do anything with a root node.
        }
        SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
        SortableDefaultMutableTreeNode childBefore = this;
        do {
            childBefore = (SortableDefaultMutableTreeNode) parentNode.getChildBefore( childBefore );
        } while ( ( childBefore != null ) && ( !( childBefore.getUserObject() instanceof GroupInfo ) ) );

        if ( childBefore == null ) {
            SortableDefaultMutableTreeNode newGroup =
                    new SortableDefaultMutableTreeNode(
                    new GroupInfo( Settings.jpoResources.getString( "newGroup" ) ) );
            parentNode.insert( newGroup, 0 );
            this.removeFromParent();
            newGroup.add( this );
        } else {
            this.removeFromParent();
            childBefore.add( this );
        }
        getPictureCollection().setUnsavedUpdates();
    }


    /**
     *  Method that outdents a node. This means the node will be placed just after it's parent's node
     *  as a child of it's grandparent.
     */
    public void outdentNode() {
        if ( this.isRoot() ) {
            return;  // don't do anything with a root node.
        }
        SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) this.getParent();
        if ( parentNode.isRoot() ) {
            return;  // don't do anything with a root parent node.
        }
        SortableDefaultMutableTreeNode grandParentNode = (SortableDefaultMutableTreeNode) parentNode.getParent();
        int index = grandParentNode.getIndex( parentNode );

        this.removeFromParent();
        grandParentNode.insert( this, index + 1 );
        getPictureCollection().setUnsavedUpdates();
    }


    /**
     *  Method that moves a node to bottom of the specified group node
     * @param groupNode
     */
    public void moveToNode( SortableDefaultMutableTreeNode groupNode ) {
        if ( this.isRoot() ) {
            return;  // don't do anything with a root node.
        }
        this.removeFromParent();
        groupNode.add( this );

        getPictureCollection().setUnsavedUpdates();
    }


 

    /**
     *  Adds a new Group to the current node with the indicated description.
     *  @param description
     * @return  The new node is returned for convenience.
     */
    public SortableDefaultMutableTreeNode addGroupNode( String description ) {
        SortableDefaultMutableTreeNode newNode =
                new SortableDefaultMutableTreeNode(
                new GroupInfo( description ) );
        add( newNode );
        return newNode;
    }


    /**
     *  Copies the pictures from the source tree to the target directory and adds them to the collection creating a progress GUI.
     *  @param sourceDir The source directory for the pictures
     *  @param targetDir  The target directory for the pictures
     *  @param groupName the new name for the group
     *  @param newOnly  If true only pictures not yet in the collection will be added.
     *  @param retainDirectories  indicates that the directory structure should be preserved.
     *  @param selectedCategories  the categories to be applied to the newly loaded pictures.
     * @return
     *
     */
    public SortableDefaultMutableTreeNode copyAddPictures( File sourceDir,
            File targetDir, String groupName, boolean newOnly,
            boolean retainDirectories, HashSet<Object> selectedCategories ) {
        File[] files = sourceDir.listFiles();
        ProgressGui progGui = new ProgressGui( Tools.countfiles( files ),
                Settings.jpoResources.getString( "PictureAdderProgressDialogTitle" ),
                Settings.jpoResources.getString( "picturesAdded" ) );

        SortableDefaultMutableTreeNode newGroup =
                new SortableDefaultMutableTreeNode(
                new GroupInfo( groupName ) );

        getPictureCollection().setSendModelUpdates( false );


        boolean picturesAdded = copyAddPictures1( files, targetDir, newGroup, progGui, newOnly, retainDirectories, selectedCategories );
        progGui.switchToDoneMode();
        getPictureCollection().setSendModelUpdates( true );

        if ( picturesAdded ) {
            add( newGroup );
        } else {
            newGroup = null;
        }
        return newGroup;
    }


    /**
     *  Copies the pictures from the source tree to the target directory and adds them to the collection.
     *  This method does the actual loop.
     * @param files
     * @param targetDir
     * @param receivingNode
     * @param progGui
     * @param newOnly
     * @param retainDirectories
     * @param selectedCategories
     * @return  true if pictures were added, false if not.
     *
     */
    protected static boolean copyAddPictures1( File[] files,
            File targetDir,
            SortableDefaultMutableTreeNode receivingNode,
            ProgressGui progGui,
            boolean newOnly,
            boolean retainDirectories,
            HashSet<Object> selectedCategories ) {

        logger.info( String.format( "Copying %d files from directory %s to node %s", files.length + 1, targetDir.toString(), receivingNode.toString() ) );
        boolean picturesAdded = false;
        // add all the files from the array as nodes to the start node.
        for ( int i = 0; ( i < files.length ) && ( !progGui.getInterruptor().getShouldInterrupt() ); i++ ) {
            File addFile = files[i];
            if ( !addFile.isDirectory() ) {
                File targetFile = Tools.inventPicFilename( targetDir, addFile.getName() );
                long crc = Tools.copyPicture( addFile, targetFile );
                if ( newOnly && Settings.pictureCollection.isInCollection( crc ) ) {
                    targetFile.delete();
                    progGui.decrementTotal();
                } else {
                    receivingNode.addPicture( targetFile, selectedCategories );
                    progGui.progressIncrement();
                    picturesAdded = true;
                }
            } else {
                if ( Tools.hasPictures( addFile ) ) {
                    SortableDefaultMutableTreeNode subNode;
                    if ( retainDirectories ) {
                        subNode = receivingNode.addGroupNode( addFile.getName() );
                    } else {
                        subNode = receivingNode;
                    }
                    boolean a = copyAddPictures1( addFile.listFiles(), targetDir, subNode, progGui, newOnly, retainDirectories, selectedCategories );
                    picturesAdded = a || picturesAdded;
                } else {
                    logger.info( "SDMTN.copyAddPictures: no pictures in directory " + addFile.toString() );
                }
            }
        }
        return picturesAdded;
    }


    /**
     *  Copies the pictures from the source tree to the target directory and adds them to the
     *  collection only if they have not been seen by the camera before.
     *
     * @param sourceDir 
     * @param targetDir 
     * @param  cam  The camera object with knows the checksums of the pictures seen before.
     * @param groupName
     * @param retainDirectories
     * @param selectedCategories
     * @return
     *
     */
    public SortableDefaultMutableTreeNode copyAddPictures( File sourceDir,
            File targetDir, String groupName, Camera cam,
            boolean retainDirectories, HashSet<Object> selectedCategories ) {
        File[] files = sourceDir.listFiles();
        ProgressGui progGui = new ProgressGui( Tools.countfiles( files ),
                Settings.jpoResources.getString( "PictureAdderProgressDialogTitle" ),
                Settings.jpoResources.getString( "picturesAdded" ) );
        SortableDefaultMutableTreeNode newGroup =
                new SortableDefaultMutableTreeNode(
                new GroupInfo( groupName ) );

        getPictureCollection().setSendModelUpdates( false );

        cam.zapNewImage();
        boolean picturesAdded = copyAddPictures1( files, targetDir, newGroup, progGui, cam, retainDirectories, selectedCategories );

        cam.storeNewImage();
        Settings.writeCameraSettings();

        getPictureCollection().setSendModelUpdates( true );
        progGui.switchToDoneMode();

        if ( picturesAdded ) {
            add( newGroup );
        } else {
            newGroup = null;
        }
        return newGroup;
    }


    /**
     *  Copies the pictures from the source File collection into the target node
     *  @param newPictures  A Collection framework of the new picture Files
     *  @param targetDir    The target directory for the copy operation
     *  @param copyMode     Set to true if you want to copy, false if you want to move the pictures.
     *  @param progressBar   The optional progressBar that should be incremented.
     *
     */
    public void copyAddPictures( Collection<File> newPictures, File targetDir,
            boolean copyMode, final JProgressBar progressBar ) {
        logger.fine( String.format( "Copy/Moving %d pictures to target directory %s", newPictures.size(), targetDir.toString() ) );
        getPictureCollection().setSendModelUpdates( false );
        for ( File f : newPictures ) {
            logger.fine( String.format( "Processing file %s", f.toString() ) );
            if ( progressBar != null ) {
                Runnable r = new Runnable() {

                    public void run() {
                        progressBar.setValue( progressBar.getValue() + 1 );
                    }
                };
                SwingUtilities.invokeLater( r );

            }
            File targetFile = Tools.inventPicFilename( targetDir, f.getName() );
            logger.fine( String.format( "Target file name chosen as: ", targetFile.toString() ) );
            Tools.copyPicture( f, targetFile );
            if ( !copyMode ) {
                f.delete();
            }
            addPicture( targetFile, null );
        }
        getPictureCollection().setSendModelUpdates( true );
    }


    /**
     *   Loads the collection indicated by the File at the "this" node
     *
     *   @param  fileToLoad		The File object that is to be loaded.
     *   @throws FileNotFoundException
     */
    public void fileLoad( File fileToLoad ) throws FileNotFoundException {
        if ( fileToLoad != null ) {
            InputStream is = new FileInputStream( fileToLoad );
            streamLoad( is );
            Settings.pushRecentCollection( fileToLoad.toString() );
        }
    }


    /**
     *   Loads the collection indicated by the Input stream at the "this" node.
     *
     *   @param  is	The inputstream that is to be loaded.
     */
    public void streamLoad( InputStream is ) {
        getPictureCollection().setSendModelUpdates( false ); // turn off model notification of each add for performance
        new XmlReader( is, this );
        getPictureCollection().setSendModelUpdates( true );
        getPictureCollection().sendNodeStructureChanged( this );
    }


    /**
     *  Copies the pictures from the source tree to the target directory and adds them to the collection.
     *  This method does the actual loop.
     *
     * @param files
     * @param targetDir
     * @param receivingNode
     * @param progGui
     * @param cam
     * @param retainDirectories
     * @param selectedCategories
     * @return
     */
    protected static boolean copyAddPictures1( File[] files,
            File targetDir,
            SortableDefaultMutableTreeNode receivingNode,
            ProgressGui progGui,
            Camera cam,
            boolean retainDirectories,
            HashSet<Object> selectedCategories ) {


        boolean picturesAdded = false;
        // add all the files from the array as nodes to the start node.
        for ( int i = 0; ( i < files.length ) && ( !progGui.getInterruptor().getShouldInterrupt() ); i++ ) {
            File addFile = files[i];
            if ( !addFile.isDirectory() ) {
                if ( cam.getUseFilename() && cam.inOldImage( addFile ) ) {
                    // ignore image if the filename is known
                    cam.copyToNewImage( addFile ); // put it in the known pictures Hash
                    progGui.decrementTotal();
                } else {
                    File targetFile = Tools.inventPicFilename( targetDir, addFile.getName() );
                    long crc = Tools.copyPicture( addFile, targetFile );
                    cam.storePictureNewImage( addFile, crc ); // remember it next time
                    if ( cam.inOldImage( crc ) ) {
                        targetFile.delete();
                        progGui.decrementTotal();
                    } else {
                        receivingNode.addPicture( targetFile, selectedCategories );
                        progGui.progressIncrement();
                        picturesAdded = true;
                    }
                }
            } else {
                if ( Tools.hasPictures( addFile ) ) {
                    SortableDefaultMutableTreeNode subNode;
                    if ( retainDirectories ) {
                        subNode = receivingNode.addGroupNode( addFile.getName() );
                    } else {
                        subNode = receivingNode;
                    }

                    boolean a = copyAddPictures1( addFile.listFiles(), targetDir, subNode, progGui, cam, retainDirectories, selectedCategories );
                    picturesAdded = a || picturesAdded;
                } else {
                    logger.info( "SDMTN.copyAddPictures: no pictures in directory " + addFile.toString() );
                }
            }
        }
        return picturesAdded;
    }


    /**
     *  Creates and add a new picture node to the current node from an image file.
     *
     *  @param  addFile  the file of the pircute that should be added
     *  @param  newOnly flag whether to check if the picture is in the collection already; if true will only add the picture if its not yet included
     *  @param selectedCategories
     * @return  true if the node was added, false if not.
     */
    public boolean addSinglePicture( File addFile, boolean newOnly,
            HashSet<Object> selectedCategories ) {
        logger.fine( String.format( "Adding File: %s, NewOnly: %b to node %s", addFile.toString(), newOnly, toString() ) );
        if ( newOnly && getPictureCollection().isInCollection( addFile ) ) {
            return false; // only add pics not in the collection already
        } else {
            return addPicture( addFile, selectedCategories );
        }
    }


    /**
     *  this method adds a new Picture to the current node if the JVM has a reader for it.
     *
     *  @param  addFile  the file that should be added
     *  @param categoryAssignment  Can be null
     *  @return  true if the picture was valid, false if not.
     */
    public boolean addPicture( File addFile, HashSet<Object> categoryAssignment ) {
        logger.fine( String.format( "Adding file %s to the node %s", addFile.toString(), toString() ) );
        PictureInfo newPictureInfo = new PictureInfo();
        try {
            if ( !Tools.jvmHasReader( addFile ) ) {
                logger.severe( String.format( "The Java Virtual Machine has not got a reader for the file %s", addFile.toString() ) );
                return false; // don't add if there is no reader.
            }
            newPictureInfo.setHighresLocation( addFile.toURI().toURL() );
            newPictureInfo.setLowresLocation( Tools.lowresFilename() );
            newPictureInfo.setDescription( Tools.stripOutFilenameRoot( addFile ) );
            newPictureInfo.setChecksum( Tools.calculateChecksum( addFile ) );
            if ( categoryAssignment != null ) {
                newPictureInfo.setCategoryAssignment( categoryAssignment );
            }
        } catch ( MalformedURLException x ) {
            logger.severe( String.format( "Caught a MalformedURLException: %s\nError: %s", addFile.getPath(), x.getMessage() ) );
            return false;
        }


        SortableDefaultMutableTreeNode newNode = new SortableDefaultMutableTreeNode( newPictureInfo );
        this.add( newNode );
        // This is not elegant but for now forces the creation of the ThumbnailController image
        // It is unfortunate that the queue will not recognize duplicates because it is working
        //  off ThumbnailController objects instead of Picturefiles. This also makes urgent requests come too late
        // TODO: Improve it. This is totally broken!
        //ThumbnailController t = new ThumbnailController( new SingleNodeBrowser( newNode ), 0, Settings.thumbnailSize, ThumbnailQueueRequest.LOW_PRIORITY, null );
        getPictureCollection().setUnsavedUpdates();

        String creationTime = null;
        try {
            // try to read EXIF data and get the date/time if possible
            // if this fails the is crashes into the catch statements and
            // the date is not added

            InputStream highresStream = newPictureInfo.getHighresURL().openStream();
            JpegSegmentReader reader = new JpegSegmentReader( new BufferedInputStream( highresStream ) );
            byte[] exifSegment = reader.readSegment( JpegSegmentReader.SEGMENT_APP1 );
            byte[] iptcSegment = reader.readSegment( JpegSegmentReader.SEGMENT_APPD );
            highresStream.close();

            Metadata metadata = new Metadata();
            new ExifReader( exifSegment ).extract( metadata );
            new IptcReader( iptcSegment ).extract( metadata );

            Directory exifDirectory = metadata.getDirectory( ExifDirectory.class );
            creationTime = exifDirectory.getString( ExifDirectory.TAG_DATETIME );
        } catch ( MalformedURLException x ) {
            logger.severe( String.format( "Caught a MalformedURLException: %s\nError: %s", addFile.getPath(), x.getMessage() ) );
        } catch ( IOException x ) {
            logger.severe( String.format( "Caught an IOException: %s\nError: %s", addFile.getPath(), x.getMessage() ) );
        } catch ( JpegProcessingException x ) {
            logger.fine( String.format( "Could not extract an EXIF header for file %s\nJpegProcessingException: ", addFile.getPath(), x.getMessage() ) );
        }
        if ( creationTime == null ) {
            creationTime = "";
        }
        newPictureInfo.setCreationTime( creationTime );

        return true;
    }


    /**
     * This method places a thumbnail creation request on the ThumbnailCreationQueue.
     * It is here for convenience as it should be a controller doing this sort of stuff.
     * @link PictureInfo#sendThumbnailChangedEvent()}
     * event.
     */
    public void refreshThumbnail() {
        if ( isRoot() ) {
            logger.fine( "Ingnoring the request for a thumbnail on the Root Node as the query for it's parent's children will fail" );
            return;
        }
        logger.fine( String.format( "refreshing the thumbnail on the node %s\nAbout to create the thubnail", this.toString() ) );
        ThumbnailController t = new ThumbnailController( new SingleNodeBrowser( this ), 0, Settings.thumbnailSize, ThumbnailQueueRequest.HIGH_PRIORITY, null );
        logger.fine( String.format( "Thumbnail %s created. Now chucking it on the creation queue", t.toString() ) );
        ThumbnailCreationQueue.requestThumbnailCreation( t, ThumbnailQueueRequest.HIGH_PRIORITY, true );
    }


    /**
     * This method returns whether the supplied node is a descendent of the deletions that
     * have been detected in the TreeModelListener delivered TreeModelEvent.
     * @param  affectedNode  The node to check whether it is or is a descendent of the deleted node.
     * @param  e the TreenModelEvent that was detected
     * @return tue if successful, false if not
     */
    public static boolean wasNodeDeleted(
            SortableDefaultMutableTreeNode affectedNode, TreeModelEvent e ) {
        //logger.info( "SDMTN.wasNodeDeleted invoked for: " + affectedNode.toString() + " / " + e.toString() );
        TreePath removedChild;
        TreePath currentNodeTreePath = new TreePath( affectedNode.getPath() );
        Object[] children = e.getChildren();
        for ( int i = 0; i < children.length; i++ ) {
            removedChild = new TreePath( children[i] );
            if ( removedChild.isDescendant( currentNodeTreePath ) ) {
                return true;
            }
        }
        return false;
    }
}
