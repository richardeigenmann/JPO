package jpo.dataModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import jpo.gui.ThumbnailCreationQueue;


/*
PictureCollection.java:  Information about the collection and owns the tree model

Copyright (C) 2006 - 2009  Richard Eigenmann, Zurich, Switzerland
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
 *  Information about the collection and owner of the treemodel
 */
public class PictureCollection {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( PictureCollection.class.getName() );


    /**
     *  Constructs a new PictureCollection object with a root object
     */
    public PictureCollection() {
        setRootNode( new SortableDefaultMutableTreeNode( new Object() ) );
        treeModel = new DefaultTreeModel( getRootNode() );
        categories = new HashMap<Integer, String>();
        mailSelection = new Vector<SortableDefaultMutableTreeNode>();
        setAllowEdits( true );
        setUnsavedUpdates( false );
    }


    /**
     * This method wipes out the data in the picture collection.
     * As it updates the TreeModel it has been made synchronous on the EDT.
     */
    public void clearCollection() {
        Runnable r = new Runnable() {

            public void run() {
                getRootNode().removeAllChildren();
                getRootNode().setUserObject( new GroupInfo( Settings.jpoResources.getString( "DefaultRootNodeText" ) ) );
                clearQueriesTreeModel();
                categories.clear();
                clearMailSelection();
                setAllowEdits( true );
                setUnsavedUpdates( false );
                setXmlFile( null );
                getTreeModel().reload();
                Settings.clearRecentDropNodes();
                ThumbnailCreationQueue.clear();
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait( r );
            } catch ( InterruptedException ex ) {
                logger.log( Level.SEVERE, null, ex );
                logger.log( Level.SEVERE, null, "no idea what to do here" );
                Thread.dumpStack();
            } catch ( InvocationTargetException ex ) {
                logger.log( Level.SEVERE, null, ex );
                logger.log( Level.SEVERE, null, "no idea what to do here" );
                Thread.dumpStack();
            }
        }
    }

    /**
     *   This variable refers to the tree model.
     */
    private DefaultTreeModel treeModel;


    /**
     *   The DefaultTreeModel allows notification of tree change events to listening
     *   objects.
     * @return
     */
    public DefaultTreeModel getTreeModel() {
        return ( treeModel );
    }

    /**
     *  controls whether updates should be fired from add, delete, insert methods
     */
    public static boolean sendModelUpdates = true;


    /**
     *  returns true if edits are allowed on this collection
     * @return
     */
    public boolean getSendModelUpdates() {
        return sendModelUpdates;
    }


    /**
     * This method sends all listeners of the TreeModel a nodeStructureChanged notification for the supplied node.
     * It is asynchroneous on the EDT
     * @param node  The node that was changed.
     */
    public void sendNodeStructureChanged( final DefaultMutableTreeNode node ) {
        Runnable r = new Runnable() {

            public void run() {
                getTreeModel().nodeStructureChanged( node );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }
    }


    /**
     * This method sends all listeners of the TreeModel a nodeChanged notification for the supplied node.
     * It is asynchroneous on the EDT
     * @param node  The node that was changed.
     */
    public void sendNodeChanged( final DefaultMutableTreeNode node ) {
        Runnable r = new Runnable() {

            public void run() {
                getTreeModel().nodeChanged( node );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }
    }


    /**
     * Sets the flag whether to send model updates or not
     * @param status
     */
    public void setSendModelUpdates( boolean status ) {
        sendModelUpdates = status;
    }

    /**
     *  The root node of the tree data model. It holds all the branches
     *  to the groups and pictures
     **/
    private SortableDefaultMutableTreeNode rootNode;


    /**
     *  This method returns the root node of the collection
     * @return the root node
     */
    public SortableDefaultMutableTreeNode getRootNode() {
        return rootNode;
    }


    /**
     * This method sets the root node of the collection
     */
    private void setRootNode( SortableDefaultMutableTreeNode rootNode ) {
        if ( rootNode != null ) {
            logger.fine( "setting root node to " + rootNode.toString() );
        } else {
            logger.info( "setting root node to null. Why ?" );
        }
        this.rootNode = rootNode;
    }

    /**
     *   This variable indicates whether uncommited changes exist for this collection.
     *   Care should be taken when adding removing or changing nodes to update this flag.
     *   It should be queried before exiting the application. Also when a new collection is
     *   loaded this flag should be checked so as not to loose modifications.
     *   This flag should be set only on the root node.
     *
     *   @see  #setUnsavedUpdates()
     *   @see  #setUnsavedUpdates(boolean)
     *   @see  #getUnsavedUpdates()
     */
    private boolean unsavedUpdates = false;


    /**
     *   This method marks the root node of the tree as having unsaved updates.
     *
     *   @see #unsavedUpdates
     *
     */
    public void setUnsavedUpdates() {
        setUnsavedUpdates( true );
    }


    /**
     *   This method allows the programmer to set whether the tree has unsaved updates or not.
     *
     *   @param b  Set to true if there are unsaved updates, false if there are none
     *   @see #unsavedUpdates
     */
    public void setUnsavedUpdates( boolean b ) {
        unsavedUpdates = b;
    }


    /**
     *   This method returns true is the tree has unsaved updates, false if it has none
     *
     *   @return
     * @see #unsavedUpdates
     *
     */
    public boolean getUnsavedUpdates() {
        return unsavedUpdates;
    }

    /**
     *  This flag controls whether this collection can be edited. This is queried by several
     *  menus and will restrict the options a use has if it returns true.
     */
    private boolean allowEdits;


    /**
     *  Returns true if edits are allowed on this collection
     * @return
     */
    public boolean getAllowEdits() {
        return allowEdits;
    }


    /**
     *  sets the allow edit status of this collection
     * @param status
     */
    public void setAllowEdits( boolean status ) {
        allowEdits = status;
    }

    /**
     *   This variable holds the reference to the queries executed against the collection.
     */
    private DefaultTreeModel queriesTreeModel = null;


    /**
     * Call this method when you need the TreeModel for the queries
     * @return  The treemodel of the queries
     */
    public DefaultTreeModel getQueriesTreeModel() {
        if ( queriesTreeModel == null ) {
            createQueriesTreeModel();
        }
        return ( queriesTreeModel );
    }


    /**
     * Call this method when you need the toot Node for the queries
     * @return
     */
    public DefaultMutableTreeNode getQueriesRootNode() {
        return ( (DefaultMutableTreeNode) getQueriesTreeModel().getRoot() );
    }


    /**
     *   Call this method when you need to set the TreeModel for the queries
     * @param tm
     */
    public void setQueriesTreeModel( DefaultTreeModel tm ) {
        queriesTreeModel = tm;
    }


    /**
     *   Call this method when you need to create a new TreeModel for the queries.
     */
    public void createQueriesTreeModel() {
        setQueriesTreeModel( new DefaultTreeModel( new DefaultMutableTreeNode( Settings.jpoResources.getString( "queriesTreeModelRootNode" ) ) ) );
    }


    /**
     *   Clear out the nodes in the exisitng queries Tree Model
     */
    public void clearQueriesTreeModel() {
        getQueriesRootNode().removeAllChildren();
    }


    /**
     * Adds a query to the Query Tree Model. It has been made synchroneous on the EDT
     * @param q The new Query to add
     * @return The node that was added.
     */
    public DefaultMutableTreeNode addQueryToTreeModel( final Query q ) {
        final DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( q );
        getQueriesRootNode().add( newNode );
        Runnable r = new Runnable() {

            public void run() {
                queriesTreeModel.nodesWereInserted( getQueriesRootNode(), new int[] { getQueriesRootNode().getIndex( newNode ) } );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            logger.fine( "We're not normally on the EDT but OK" );
            r.run();
        } else {
            logger.fine( "In order for the JTree to update on the EDT we are sending the node change event on a Swing invoke Later thread" );
            SwingUtilities.invokeLater( r );
        }
        return newNode;
    }

    /**
     *  This HashMap holds the categories that will be available for this collection.
     *  It is only populated on the root node.
     */
    private HashMap<Integer, String> categories;


    /**
     *  Acessor for the categories object
     * @return
     */
    public HashMap<Integer, String> getCategories() {
        return categories;
    }


    /**
     *  This adds a category to the HashMap
     * @param index
     * @param category
     */
    public void addCategory( Integer index, String category ) {
        categories.put( index, category );

        // add a new CategoryQuery to the Searches tree
        CategoryQuery q = new CategoryQuery( index );
        addQueryToTreeModel( q );
    }


    /**
     *  This adds a category to the HashMap
     * @param category
     * @return the number at which the category was added
     */
    public Integer addCategory( String category ) {
        Integer key = null;
        for ( int i = 0; i < Integer.MAX_VALUE; i++ ) {
            key = new Integer( i );
            if ( !categories.containsKey( key ) ) {
                break;
            }
        }
        addCategory( key, category );
        return key;
    }


    /**
     *  Renames a category in the HashMap
     * @param key
     * @param category
     */
    public void renameCategory( Integer key, String category ) {
        removeCategory( key );
        addCategory( key, category );
    }


    /**
     *  returns an iterator through the categories keys
     * @return
     */
    public Iterator getCategoryIterator() {
        return categories.keySet().iterator();
    }


    /**
     *  returns an iterator through the categories
     * @param key
     * @return
     */
    public String getCategory( Integer key ) {
        return categories.get( key );
    }


    /**
     *  returns an iterator through the categories
     * @param key
     * @return
     */
    public String removeCategory( Integer key ) {
        return categories.remove( key );
    }


    /**
     *  counts the number of nodes using the category
     * @param key
     * @param startNode
     * @return
     */
    public static int countCategoryUsage( Object key,
            SortableDefaultMutableTreeNode startNode ) {
        Enumeration nodes = startNode.children();
        int count = 0;
        SortableDefaultMutableTreeNode n;
        while ( nodes.hasMoreElements() ) {
            n = (SortableDefaultMutableTreeNode) nodes.nextElement();
            if ( n.getUserObject() instanceof PictureInfo ) {
                if ( ( (PictureInfo) n.getUserObject() ).containsCategory( key ) ) {
                    count++;
                }
            }
            if ( n.getChildCount() > 0 ) {
                count += countCategoryUsage( key, n );
            }
        }
        return count;
    }


    /**
     *  returns an ArrayList of the nodes that match this category
     * @param key
     * @param startNode
     * @return
     */
    public static ArrayList<SortableDefaultMutableTreeNode> getCategoryUsageNodes(
            Object key, SortableDefaultMutableTreeNode startNode ) {
        ArrayList<SortableDefaultMutableTreeNode> resultList = new ArrayList<SortableDefaultMutableTreeNode>();
        Enumeration nodes = startNode.children();
        SortableDefaultMutableTreeNode n;
        while ( nodes.hasMoreElements() ) {
            n = (SortableDefaultMutableTreeNode) nodes.nextElement();
            if ( n.getUserObject() instanceof PictureInfo ) {
                if ( ( (PictureInfo) n.getUserObject() ).containsCategory( key ) ) {
                    resultList.add( n );
                }
            }
            if ( n.getChildCount() > 0 ) {
                resultList.addAll( getCategoryUsageNodes( key, n ) );
            }
        }
        return resultList;
    }


    /**
     * removes the category from the nodes using it
     * @param key
     * @param startNode
     */
    public void removeCategoryUsage( Object key,
            SortableDefaultMutableTreeNode startNode ) {
        Enumeration nodes = startNode.children();
        while ( nodes.hasMoreElements() ) {
            SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) nodes.nextElement();
            if ( n.getUserObject() instanceof PictureInfo ) {
                ( (PictureInfo) n.getUserObject() ).removeCategory( key );
            }
            if ( n.getChildCount() > 0 ) {
                removeCategoryUsage( key, n );
            }
        }
    }


    /**
     *  returns the number of categories available.
     * @return
     */
    public int countCategories() {
        return categories.size();
    }

    /**
     *   This Hash Set hold references to the selected nodes for mailing. It works just like the selection
     *   HashSet only that the purpose is a different one. As such it has different behaviour.
     */
    private Vector<SortableDefaultMutableTreeNode> mailSelection;


    /**
     *  This method places the current SDMTN into the mailSelection HashSet.
     * @param node
     */
    public void addToMailSelected( SortableDefaultMutableTreeNode node ) {
        if ( isMailSelected( node ) ) {
            logger.fine( String.format( "The node %s is already selected. Leaving it selected.", node.toString() ) );
            return;
        }
        mailSelection.add( node );
        Object userObject = node.getUserObject();
        if ( userObject instanceof PictureInfo ) {
            ( (PictureInfo) userObject ).sendWasMailSelectedEvent();
        }
    }


    /**
     *  This method inverts the status of the node on the mail selection HashSet
     * @param node
     */
    public void toggleMailSelected( SortableDefaultMutableTreeNode node ) {
        if ( isMailSelected( node ) ) {
            removeFromMailSelection( node );
        } else {
            addToMailSelected( node );
        }
    }


    /**
     *  This method clears the mailSelection HashSet.
     */
    public void clearMailSelection() {
        //can't use iterator or there is a concurrent modification exception
        Object[] array = new Object[mailSelection.size()];
        mailSelection.copyInto( array );
        for ( int i = 0; i < array.length; i++ ) {
            removeFromMailSelection( (SortableDefaultMutableTreeNode) array[i] );
        }
    }


    /**
     *  This method removes the current SDMTN from the mailSelection HashSet.
     * @param node the node to remove from the mail selection
     */
    public void removeFromMailSelection( SortableDefaultMutableTreeNode node ) {
        mailSelection.remove( node );
        Object userObject = node.getUserObject();
        if ( userObject instanceof PictureInfo ) {
            ( (PictureInfo) userObject ).sendWasMailUnselectedEvent();
        }
    }


    /**
     *  This returns whether the SDMTN is part of the mailSelection HashSet.
     * @param node
     * @return
     */
    public boolean isMailSelected( SortableDefaultMutableTreeNode node ) {
        try {
            return mailSelection.contains( node );
        } catch ( NullPointerException x ) {
            return false;
        }
    }


    /**
     *  returns an array of the mailSelected nodes.
     * @return
     */
    public Object[] getMailSelectedNodes() {
        return mailSelection.toArray();
    }


    /**
     *   This method returns true if the indicated picture file is already a member
     *   of the collection. Otherwise it returns false. Enhanced on 9.6.2004 to
     *   check against Lowres pictures too as we might be adding in pictures that have a
     *   Lowres Subdirectory and we don't wan't to add the Lowres of the collection
     *   back in.
     *
     *   @param	f	The File object of the file to check for
     * @return
     */
    public boolean isInCollection( File f ) {
        SortableDefaultMutableTreeNode node;
        Object nodeObject;
        File highresFile;
        File lowresFile;
        File groupThumbnail;
        Enumeration e = getRootNode().preorderEnumeration();
        while ( e.hasMoreElements() ) {
            node = (SortableDefaultMutableTreeNode) e.nextElement();
            nodeObject = node.getUserObject();
            if ( nodeObject instanceof PictureInfo ) {
                highresFile = ( (PictureInfo) nodeObject ).getHighresFile();
                lowresFile = ( (PictureInfo) nodeObject ).getLowresFile();
                logger.fine( "Checking: " + ( (PictureInfo) nodeObject ).getHighresLocation() );
                if ( ( highresFile != null ) && ( highresFile.compareTo( f ) == 0 ) ) {
                    logger.fine( "CollectionJTree.isInCollection found a match on: " + ( (PictureInfo) nodeObject ).getDescription() );
                    return true;
                } else if ( ( lowresFile != null ) && ( lowresFile.compareTo( f ) == 0 ) ) {
                    return true;
                }
            } else if ( nodeObject instanceof GroupInfo ) {
                groupThumbnail = ( (GroupInfo) nodeObject ).getLowresFile();
                if ( ( groupThumbnail != null ) && ( groupThumbnail.compareTo( f ) == 0 ) ) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     *   This method returns true if the indicated checksum is already a member
     *   of the collection. Otherwise it returns false.
     *
     *   @param	checksum	The checksum of the picture to check for
     * @return
     */
    public boolean isInCollection( long checksum ) {
        SortableDefaultMutableTreeNode node;
        Object nodeObject;
        Enumeration e = getRootNode().preorderEnumeration();
        while ( e.hasMoreElements() ) {
            node = (SortableDefaultMutableTreeNode) e.nextElement();
            nodeObject = node.getUserObject();
            if ( nodeObject instanceof PictureInfo ) {
                logger.fine( "Checking: " + ( (PictureInfo) nodeObject ).getHighresLocation() );
                if ( ( (PictureInfo) nodeObject ).getChecksum() == checksum ) {
                    logger.fine( "CollectionJTree.isInCollection found a match on: " + ( (PictureInfo) nodeObject ).getDescription() );
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *  status variable to find out if a thread is loading a file
     */
    public boolean fileLoading = false;

    /**
     *  A file reference to the file that was loaded. It will come in handy when
     *  a save instruction comes along.
     */
    private File xmlFile;


    /**
     *  This method sets the file which represents the current collection.
     *  It updates the title of the main application window too.
     * @param f
     */
    public void setXmlFile( File f ) {
        xmlFile = f;
    }


    /**
     *  This method returns the xml file for the collection
     * @return
     */
    public File getXmlFile() {
        return xmlFile;
    }


    /**
     * Loads the specified file into the root node of the collection
     * @param f
     * @throws FileNotFoundException
     */
    public void fileLoad( File f ) throws FileNotFoundException {
        if ( fileLoading ) {
            logger.info( this.getClass().toString() + ".fileLoad: already busy loading another file. Aborting" );
            return;
        }
        fileLoading = true;
        clearCollection();
        setXmlFile( f );
        try {
            getRootNode().fileLoad( getXmlFile() );
            fileLoading = false;
        } catch ( FileNotFoundException ex ) {
            fileLoading = false;
            throw ex;
        }
    }


    /**
     * method that saves the entire index in XML format.
     * TODO: simply returning is a bit carefree if no save has taken place
     */
    public void fileSave() {
        if ( xmlFile == null ) {
            logger.severe( "xmlFile is null. Not saving!" );
            return;
        } else {
            File temporaryFile = new File( xmlFile.getPath() + ".!!!" );
            new XmlDistiller( temporaryFile, getRootNode(), false, false );
            File originalFile = new File( xmlFile.getPath() + ".orig" );
            xmlFile.renameTo( originalFile );
            temporaryFile.renameTo( xmlFile );
            setUnsavedUpdates( false );
            originalFile.delete();
            Settings.pushRecentCollection( xmlFile.toString() );
        }
    }


    /**
     *  This method returns an array of the groups that hold a reference to the picture of the specified node
     * @param orphanNode
     * @return
     */
    public SortableDefaultMutableTreeNode[] findParentGroups(
            SortableDefaultMutableTreeNode orphanNode ) {
        if ( !( orphanNode.getUserObject() instanceof PictureInfo ) ) {
            return null;
        }

        Vector<SortableDefaultMutableTreeNode> parentGroups = new Vector<SortableDefaultMutableTreeNode>();
        if ( ( (DefaultMutableTreeNode) orphanNode.getParent() ).getUserObject() instanceof GroupInfo ) {
            parentGroups.add( (SortableDefaultMutableTreeNode) orphanNode.getParent() );
        }

        String comparingFilename = ( (PictureInfo) orphanNode.getUserObject() ).getHighresFilename();
        SortableDefaultMutableTreeNode testNode, testNodeParent;
        Object nodeObject;
        PictureInfo pi;
        for ( Enumeration e = getRootNode().preorderEnumeration(); e.hasMoreElements(); ) {
            testNode = (SortableDefaultMutableTreeNode) e.nextElement();
            nodeObject = testNode.getUserObject();
            if ( ( nodeObject instanceof PictureInfo ) ) {
                pi = (PictureInfo) nodeObject;
                if ( pi.getHighresFilename().equals( comparingFilename ) ) {
                    testNodeParent = (SortableDefaultMutableTreeNode) testNode.getParent();
                    if ( !parentGroups.contains( testNodeParent ) ) {
                        logger.fine( "adding node: " + testNodeParent.toString() );
                        parentGroups.add( testNodeParent );
                    }
                }
            }
        }

        return parentGroups.toArray( new SortableDefaultMutableTreeNode[0] );
    }

    /**
     *   This Hash Set holds references to the selected nodes.
     */
    public final Vector<SortableDefaultMutableTreeNode> selection = new Vector<SortableDefaultMutableTreeNode>();


    /**
     * This method places the current {@link SortableDefaultMutableTreeNode} into the selection HashSet.
     * @param node
     */
    public void addToSelectedNodes( SortableDefaultMutableTreeNode node ) {
        if ( isSelected( node ) ) {
            logger.fine( String.format( "The node %s is already selected. Leaving it selected.", node.toString() ) );
            return;
        }
        selection.add( node );
        Object userObject = node.getUserObject();
        if ( userObject instanceof PictureInfo ) {
            ( (PictureInfo) userObject ).sendWasSelectedEvent();
        } else if ( userObject instanceof GroupInfo ) {
            ( (GroupInfo) userObject ).sendWasSelectedEvent();
        }
    }


    /**
     * This method removes the current SDMTN from the selection
     * @param node the node to remove
     */
    public void removeFromSelection( SortableDefaultMutableTreeNode node ) {
        selection.remove( node );
        Object userObject = node.getUserObject();
        if ( userObject instanceof PictureInfo ) {
            ( (PictureInfo) userObject ).sendWasUnselectedEvent();
        }else if ( userObject instanceof GroupInfo ) {
            ( (GroupInfo) userObject ).sendWasUnselectedEvent();
        }
    }


    /**
     * This method clears selection HashSet that refers to the selected
     * highlighted thumbnails and fires unselectedEvents
     */
    public void clearSelection() {
        //can't use iterator or there is a concurrent modification exception
        Object[] array = new Object[selection.size()];
        selection.copyInto( array );
        for ( int i = 0; i < array.length; i++ ) {
            removeFromSelection( (SortableDefaultMutableTreeNode) array[i] );
        }
    }


    /**
     * This returns whether the SDMTN is part of the selection HashSet.
     * @param node
     * @return true if the node is selected
     */
    public boolean isSelected( SortableDefaultMutableTreeNode node ) {
        try {
            return selection.contains( node );
        } catch ( NullPointerException x ) {
            return false;
        }
    }


    /**
     * returns an array of the selected nodes.
     * @return an array of the selected nodes
     */
    public Object[] getSelectedNodes() {
        return selection.toArray();
    }


    /**
     * The selected nodes
     * @return a vector of the selected nodes
     */
    public Vector<SortableDefaultMutableTreeNode> getSelectedNodesAsVector() {
        return selection;
    }


    /**
     *  returns the amount of selected nodes
     * @return
     */
    public int countSelectedNodes() {
        return selection.size();
    }
}
