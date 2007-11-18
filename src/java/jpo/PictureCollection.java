package jpo;

import java.awt.GridBagLayout;
import javax.swing.tree.*;
import java.util.*;
import javax.swing.*;
import java.io.*;

/*
PictureCollection.java:  An object that holds all the references to a collection of pictures
 
Copyright (C) 2006-2007  Richard Eigenmann, Zurich, Switzerland
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
 *  An object that holds all the references to a collection of pictures
 */

public class PictureCollection {
    
    /**
     *  Constructs a new PictureCollection object.
     */
    public PictureCollection() {
        rootNode = new SortableDefaultMutableTreeNode( new Object() );
        treeModel = new DefaultTreeModel( getRootNode() );
        //createQueriesTreeModel();
        categories = new HashMap();
        mailSelection = new HashSet();
        setAllowEdits( true );
        setUnsavedUpdates( false );
    }
    
    
    
    /**
     *  This method wipes out the data in the picture collection
     */
    public void clearCollection() {
        if ( checkUnsavedUpdates() ) return;
        
        getRootNode().removeAllChildren();
        getRootNode().setUserObject( new GroupInfo( Settings.jpoResources.getString("DefaultRootNodeText") ) );
        clearQueriesTreeModel();
        categories.clear();
        clearMailSelection();
        setAllowEdits( true );
        setUnsavedUpdates( false );
        setXmlFile( null );
        getTreeModel().reload();
        Settings.clearRecentDropNodes();
        ThumbnailCreationQueue.removeAll();
    }
    
    
    
    
    /**
     *   This variable refers to the tree model.
     */
    private DefaultTreeModel treeModel;
    
    
    /**
     *   The
     *   DefaultTreeModel allows notification of tree change events to listening
     *   objects.
     */
    public DefaultTreeModel getTreeModel() {
        return( treeModel );
    }
    
    
    /**
     *  controls whether updates should be fired from add, delete, insert methods
     */
    public static boolean sendModelUpdates = true;
    
    
    /**
     *  returns true if edits are allowed on this collection
     */
    public boolean getSendModelUpdates() {
        return sendModelUpdates;
    }
    
    
    /**
     *  sets the allow edit status of this collection
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
     */
    public SortableDefaultMutableTreeNode getRootNode() {
        return rootNode;
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
     *   @see #unsavedUpdates
     *
     */
    public boolean getUnsavedUpdates() {
        return unsavedUpdates;
    }
    
    
    
    /**
     *  method that checks for unsaved changes in the data model and asks if you really want to discard them.
     *  It returns true if the user want to cancel the close.
     */
    public boolean checkUnsavedUpdates() {
        if ( getUnsavedUpdates() ) {
            Object[] options = {
                Settings.jpoResources.getString("discardChanges"),
                Settings.jpoResources.getString("genericSaveButtonLabel"),
                Settings.jpoResources.getString("FileSaveAsMenuItemText"),
                Settings.jpoResources.getString("genericCancelText")};
            int option = JOptionPane.showOptionDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString("unsavedChanges"),
                    Settings.jpoResources.getString("genericWarning"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]);
            switch ( option ) {
                case 0:
                    return false;
                case 1:
                    fileSave();
                    return getUnsavedUpdates();
                case 2:
                    fileSaveAs();
                    return getUnsavedUpdates();
                case 3:
                    return true;
            }
        }
        return false;
    }
    
    
    
    
    
    /**
     *  This flag controls whether this collection can be edited. This is queried by several
     *  menus and will restrict the options a use has if it returns true.
     */
    private boolean allowEdits;
    
    
    /**
     *  Returns true if edits are allowed on this collection
     */
    public boolean getAllowEdits() {
        return allowEdits;
    }
    
    
    /**
     *  sets the allow edit status of this collection
     */
    public void setAllowEdits( boolean status ) {
        allowEdits = status;
    }
    
    
    
    
    
    
    
    
    
    /**
     *   This variable holds the reference to the queries executed against the collection.
     */
    private TreeModel queriesTreeModel = null;
    
    
    /**
     *   Call this method when you need the TreeModel for the queries
     */
    public TreeModel getQueriesTreeModel() {
        if ( queriesTreeModel == null ) createQueriesTreeModel();
        return( queriesTreeModel );
    }
    
    
    /**
     *   Call this method when you need the toot Node for the queries
     */
    public DefaultMutableTreeNode getQueriesRootNode() {
        return( (DefaultMutableTreeNode) queriesTreeModel.getRoot() );
    }
    
    
    /**
     *   Call this method when you need to set the TreeModel for the queries
     */
    public void setQueriesTreeModel( TreeModel tm ) {
        queriesTreeModel = tm;
    }
    
    /**
     *   Call this method when you need to create a new TreeModel for the queries.
     */
    public void createQueriesTreeModel() {
        setQueriesTreeModel( new DefaultTreeModel( new DefaultMutableTreeNode( Settings.jpoResources.getString("queriesTreeModelRootNode") ) ) );
    }
    
    
    /**
     *   Clear out the nodes in the exisitng queries Tree Model
     */
    public void clearQueriesTreeModel() {
        getQueriesRootNode().removeAllChildren();
    }
    
    /**
     *   Call this method when you need to add a query to the tree model.
     */
    public void addQueryToTreeModel( Query q ) {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( q );
        getQueriesRootNode().add( newNode );
        ( (DefaultTreeModel) queriesTreeModel ).nodesWereInserted( getQueriesRootNode(), new int[] { getQueriesRootNode().getIndex( newNode ) } );
    }
    
    
    
    
    
    
    
    
    
    
    
    /**
     *  This HashMap holds the categories that will be available for this collection.
     *  It is only populated on the root node.
     */
    private HashMap categories;
    
    /**
     *  Acessor for the categories object
     */
    public HashMap getCategories() {
        return categories;
    }
    
    /**
     *  This adds a category to the HashMap
     */
    public void addCategory( Integer index, String category ) {
        categories.put( index, category );
        
        // add a new CategoryQuery to the Searches tree
        CategoryQuery q = new CategoryQuery( index );
        addQueryToTreeModel( q );
    }
    
    /**
     *  This adds a category to the HashMap
     */
    public Object addCategory( String category ) {
        Integer key = null;
        for ( int i = 0; i < Integer.MAX_VALUE; i ++ ) {
            key = new Integer( i );
            if ( ! categories.containsKey( key ) ) {
                break;
            }
        }
        addCategory( key, category );
        return key;
    }
    
    
    /**
     *  Renames a category in the HashMap
     */
    public void renameCategory( Object key, String category ) {
        removeCategory( key );
        addCategory( (Integer) key, category );
    }
    
    
    /**
     *  returns an iterator through the categories keys
     */
    public Iterator getCategoryIterator() {
        return categories.keySet().iterator();
    }
    
    
    /**
     *  returns an iterator through the categories
     */
    public Object getCategory( Object key ) {
        return categories.get( key );
    }
    
    
    /**
     *  returns an iterator through the categories
     */
    public Object removeCategory( Object key ) {
        return categories.remove( key );
    }
    
    
    /**
     *  counts the number of nodes using the category
     */
    public static int countCategoryUsage( Object key, SortableDefaultMutableTreeNode startNode ) {
        Enumeration nodes = startNode.children();
        int count = 0;
        SortableDefaultMutableTreeNode n;
        while ( nodes.hasMoreElements() ) {
            n = (SortableDefaultMutableTreeNode) nodes.nextElement();
            if ( n.getUserObject() instanceof PictureInfo)
                if ( ( (PictureInfo) n.getUserObject() ).containsCategory( key ) )
                    count++;
            if ( n.getChildCount() > 0 )
                count += countCategoryUsage( key, n );
        }
        return count;
    }
    
    /**
     *  returns an ArrayList of the nodes that match this category
     */
    public static ArrayList getCategoryUsageNodes( Object key, SortableDefaultMutableTreeNode startNode ) {
        ArrayList resultList = new ArrayList();
        Enumeration nodes = startNode.children();
        SortableDefaultMutableTreeNode n;
        while ( nodes.hasMoreElements() ) {
            n = (SortableDefaultMutableTreeNode) nodes.nextElement();
            if ( n.getUserObject() instanceof PictureInfo)
                if ( ( (PictureInfo) n.getUserObject() ).containsCategory( key ) )
                    resultList.add( n );
            if ( n.getChildCount() > 0 )
                resultList.addAll( getCategoryUsageNodes( key, n ) );
        }
        return resultList;
    }
    
    
    /**
     *  removes the category from the nodes using it
     */
    public void removeCategoryUsage( Object key, SortableDefaultMutableTreeNode startNode  ) {
        Enumeration nodes = startNode.children();
        while ( nodes.hasMoreElements() ) {
            SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) nodes.nextElement();
            if ( n.getUserObject() instanceof PictureInfo)
                ( (PictureInfo) n.getUserObject() ).removeCategory( key );
            if ( n.getChildCount() > 0 )
                removeCategoryUsage( key, n );
        }
    }
    
    
    /**
     *  returns the number of categories available.
     */
    public int countCategories() {
        return categories.size();
    }
    
    
    
    
    
    
    
    
    /**
     *   This Hash Set hold references to the selected nodes for mailing. It works just like the selection
     *   HashSet only that the purpose is a different one. As such it has different behaviour.
     */
    private HashSet mailSelection;
    
    /**
     *  This method places the current SDMTN into the mailSelection HashSet.
     */
    public void setMailSelected( SortableDefaultMutableTreeNode node ) {
        mailSelection.add( node );
        Object userObject = node.getUserObject();
        if ( userObject instanceof PictureInfo ) {
            ((PictureInfo) userObject).sendWasMailSelectedEvent();
        }
    }
    
    /**
     *  This method inverts the status of the node on the mail selection HashSet
     */
    public void toggleMailSelected( SortableDefaultMutableTreeNode node ) {
        if ( isMailSelected( node ) ) {
            removeFromMailSelection( node );
        } else {
            setMailSelected( node );
        }
    }
    
    /**
     *  This method clears the mailSelection HashSet.
     */
    public void clearMailSelection() {
        Iterator i = mailSelection.iterator();
        Object o;
        Object userObject;
        while ( i.hasNext() ) {
            o = i.next();
            i.remove();
            userObject = ((SortableDefaultMutableTreeNode) o).getUserObject();
            if ( userObject instanceof PictureInfo ) {
                ((PictureInfo) userObject).sendWasMailUnselectedEvent();
                
            }
        }
    }
    
    
    /**
     *  This method removes the current SDMTN from the mailSelection HashSet.
     */
    public void removeFromMailSelection( SortableDefaultMutableTreeNode node ) {
        mailSelection.remove( node );
        Object userObject = node.getUserObject();
        if ( userObject instanceof PictureInfo ) {
            ((PictureInfo) userObject).sendWasMailUnselectedEvent();
        }
    }
    
    
    /**
     *  This returns whether the SDMTN is part of the mailSelection HashSet.
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
     */
    public Object [] getMailSelectedNodes() {
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
     */
    public boolean isInCollection( File f ) {
        SortableDefaultMutableTreeNode node;
        Object nodeObject;
        Enumeration e = getRootNode().preorderEnumeration();
        while ( e.hasMoreElements() ) {
            node = (SortableDefaultMutableTreeNode) e.nextElement();
            nodeObject = node.getUserObject();
            if  (nodeObject instanceof PictureInfo) {
                //Tools.log( "Checking: " + ( (PictureInfo) nodeObject ).getHighresLocation() );
                if ( ((PictureInfo) nodeObject ).getHighresFile().compareTo( f ) == 0 )  {
                    //Tools.log ( "CollectionJTree.isInCollection found a match on: " + ( (PictureInfo) nodeObject ).getDescription() );
                    return true;
                } else if ( ((PictureInfo) nodeObject ).getLowresFile().compareTo( f ) == 0 )  {
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
     */
    public boolean isInCollection( long checksum ) {
        SortableDefaultMutableTreeNode node;
        Object nodeObject;
        Enumeration e = getRootNode().preorderEnumeration();
        while ( e.hasMoreElements() ) {
            node = (SortableDefaultMutableTreeNode) e.nextElement();
            nodeObject = node.getUserObject();
            if  (nodeObject instanceof PictureInfo) {
                //Tools.log( "Checking: " + ( (PictureInfo) nodeObject ).getHighresLocation() );
                if ( ((PictureInfo) nodeObject ).getChecksum() == checksum )  {
                    //Tools.log ( "CollectionJTree.isInCollection found a match on: " + ( (PictureInfo) nodeObject ).getDescription() );
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
    private File xmlFile = null;
    
    /**
     *  This method sets the file which represents the current collection.
     *  It updates the title of the main application window too.
     */
    public void setXmlFile( File f ) {
        xmlFile = f;
        if ( f != null ) {
            Settings.anchorFrame.setTitle( Settings.jpoResources.getString("ApplicationTitle") + ":  " + xmlFile.toString() );
        } else {
            Settings.anchorFrame.setTitle( Settings.jpoResources.getString("ApplicationTitle") );
        }
        
    }
    
    
    /**
     *  This method returns the xml file for the collection
     */
    public File getXmlFile() {
        return xmlFile;
    }
    
    
    /**
     *   Creates a JFileChooser GUI and allows the user to select an XML file
     *   which is then loaded into the root node of the collection
     */
    public void fileLoad() {
        File fileToLoad = Tools.chooseXmlFile();
        fileLoad( fileToLoad );
    }
    
    
    /**
     *   Loads the specified file into the root node of the collection
     */
    public void fileLoad( File f ) {
        if ( fileLoading ) {
            Tools.log( this.getClass().toString() + ".fileLoad: already busy loading another file. Aborting");
            return;
        }
        setXmlFile( f );
        fileLoading = true;
        try {
            getRootNode().fileLoad( getXmlFile() );
        } catch ( FileNotFoundException x) {
            Tools.log( this.getClass().toString() + ".fileToLoad: FileNotFoundExecption: "+ x.getMessage() );
            JOptionPane.showMessageDialog( Settings.anchorFrame,
                    x.getMessage(),
                    Settings.jpoResources.getString("genericError"),
                    JOptionPane.ERROR_MESSAGE);
        }
        fileLoading = false;
    }
    
    
    /**
     *   method that saves the entire index in XML format. It prompts for the
     *   filename first.
     */
    public void fileSave() {
        if ( xmlFile == null )
            fileSaveAs();
        else {
            
            File temporaryFile = new File( xmlFile.getPath() + ".!!!" );
            new XmlDistiller( temporaryFile, getRootNode(), false, false );
            File originalFile = new File( xmlFile.getPath() + ".orig" );
            xmlFile.renameTo( originalFile );
            temporaryFile.renameTo( xmlFile );
            setUnsavedUpdates( false );
            originalFile.delete();
            Settings.pushRecentCollection( xmlFile.toString() );
            /*JOptionPane.showMessageDialog( Settings.anchorFrame,
                    Settings.jpoResources.getString("collectionSaveBody") + xmlFile.toString(),
                    Settings.jpoResources.getString("collectionSaveTitle"),
                    JOptionPane.INFORMATION_MESSAGE);*/
            JPanel p = new JPanel();
            p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
            p.add( new JLabel( Settings.jpoResources.getString("collectionSaveBody") + xmlFile.toString() ) );
            JCheckBox setAutoload = new JCheckBox(Settings.jpoResources.getString("setAutoload") );
            if ( ( new File( Settings.autoLoad ) ).compareTo( xmlFile ) == 0 ) {
                setAutoload.setSelected( true );
            }
            p.add( setAutoload );
            JOptionPane.showMessageDialog( Settings.anchorFrame,
                    p,
                    Settings.jpoResources.getString("collectionSaveTitle"),
                    JOptionPane.INFORMATION_MESSAGE );
            if ( setAutoload.isSelected() ) {
                Settings.autoLoad = xmlFile.toString();
                Settings.writeSettings();
            }
        }
    }
    
    
    
    
    /**
     *   method that saves the entire index in XML format. It prompts for the
     *   filename first.
     */
    public void fileSaveAs() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        jFileChooser.setDialogType( JFileChooser.SAVE_DIALOG );
        jFileChooser.setDialogTitle( Settings.jpoResources.getString( "fileSaveAsTitle" ) );
        jFileChooser.setMultiSelectionEnabled( false );
        jFileChooser.setFileFilter( new XmlFilter() );
        if (xmlFile != null )
            jFileChooser.setCurrentDirectory( xmlFile );
        else
            jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );
        
        int returnVal = jFileChooser.showSaveDialog( Settings.anchorFrame );
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File chosenFile = jFileChooser.getSelectedFile();
            chosenFile = Tools.correctFilenameExtension( "xml", chosenFile );
            if ( chosenFile.exists() ) {
                int answer = JOptionPane.showConfirmDialog( Settings.anchorFrame,
                        Settings.jpoResources.getString("confirmSaveAs"),
                        Settings.jpoResources.getString("genericWarning"),
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE );
                if ( answer == JOptionPane.CANCEL_OPTION )
                    return;
            }
            
            setXmlFile( chosenFile );
            fileSave();
            
            Settings.memorizeCopyLocation( chosenFile.getParent() );
            Settings.pushRecentCollection( chosenFile.toString() );
        }
        
    }
    
    
    
    
    /**
     *  This method returns an array of the groups that hold a reference to the picture of the specified node
     */
    public SortableDefaultMutableTreeNode[] findParentGroups( SortableDefaultMutableTreeNode orphanNode ) {
        if ( ! ( orphanNode.getUserObject() instanceof PictureInfo ) ) {
            return null;
        }
        
        Vector parentGroups = new Vector();
        if ( ( (DefaultMutableTreeNode) orphanNode.getParent() ).getUserObject() instanceof GroupInfo ) {
            parentGroups.add( (SortableDefaultMutableTreeNode) orphanNode.getParent() );
        }
        
        String comparingFilename = ( (PictureInfo) orphanNode.getUserObject()).getHighresFilename();
        SortableDefaultMutableTreeNode testNode, testNodeParent;
        Object nodeObject;
        PictureInfo pi;
        for ( Enumeration e = rootNode.preorderEnumeration() ; e.hasMoreElements(); ) {
            testNode =  (SortableDefaultMutableTreeNode) e.nextElement();
            nodeObject = testNode.getUserObject();
            if  ( ( nodeObject instanceof PictureInfo ) )  {
                pi = (PictureInfo) nodeObject;
                if ( pi.getHighresFilename().equals( comparingFilename ) ) {
                    testNodeParent = (SortableDefaultMutableTreeNode) testNode.getParent();
                    if (! parentGroups.contains( testNodeParent ) ) {
                        System.out.println("adding node: " + testNodeParent.toString());
                        parentGroups.add( testNodeParent );
                    }
                }
            }
        }
        
        return (SortableDefaultMutableTreeNode []) parentGroups.toArray( new SortableDefaultMutableTreeNode [0] );
    }
}
