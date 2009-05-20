package jpo;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TreeModelEvent;
import jpo.gui.ThumbnailCreationFactory;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.gui.RandomBrowser;
import jpo.gui.GroupBrowser;
import jpo.gui.SettingsDialog;
import jpo.gui.QueriesJTree;
import jpo.gui.ResizableJFrame;
import jpo.gui.QueryJFrame;
import jpo.gui.ThumbnailJScrollPane;
import jpo.gui.CollectionPropertiesJFrame;
import jpo.gui.CollectionJTreeController;
import jpo.gui.PictureViewer;
import jpo.gui.ReconcileJFrame;
import jpo.gui.InfoPanel;
import jpo.gui.ApplicationMenuInterface;
import jpo.gui.ApplicationJMenuBar;
import jpo.gui.AddFromCamera;
import jpo.gui.CameraEditor;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureCollection;
import jpo.dataModel.PictureInfo;
import jpo.gui.swing.CollectionJTree;
import jpo.gui.PictureAdder;
import jpo.gui.XmlFilter;


/*
Jpo.java:  main class of the JPO application

Copyright (C) 2002-2009  Richard Eigenmann.
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
 * Jpo is the the main class of a browser application that lets
 * a user view a collection of pictures in as thumbnails, in a separate window
 * or in a full sized mode.<p>
 *
 * The Jpo class creates the following main objects:
 *
 * <p><img src=../Overview.png border=0><p>
 *
 * It uses a list of pictures (PictureList file) to create a hierarchical model of
 * <code>SortableDefaultMutableTreeNode</code>s that represent the structure of the collection.
 * Each node has an associated object of {@link GroupInfo} or {@link PictureInfo} type.
 *
 * The {@link CollectionJTreeController} visualises the model and allows the user to
 * expand and collapse branches of the tree with the mouse. If a node is clicked this generates
 * a <code>valueChanged</code> event from the model which is sent to all listening objects.<p>
 *
 * Listening objects are the thumbnail pane which displays the group if a node of type
 * <code>GroupInfo</code> has been selected.<p>
 *
 * This listener architecture allows fairly easy expansion of the application
 * since all that is required is that any additional objects that need to be change the picture
 * or need to be informed of a change can connect to the model in this manner and
 * need no other controls.
 *
 * @author Richard Eigenmann, richard.eigenmann@gmail.com
 * @version 0.9
 * @see CollectionJTree
 * @see ThumbnailJScrollPane
 * @see PictureViewer
 * @since JDK1.6.0
 */
public class Jpo extends JFrame
        implements ApplicationMenuInterface {

    /**
     *   the main method is the entry point for this application (or any)
     *   Java application. No parameter passing is used in the Jpo application. <p>
     *
     *   The method verifies that the user has the correct Java Virtual Machine (> 1.4.0)
     *   and then created a new {@link Jpo} object.
     *
     *
     * @param args
     */
    public static void main( String[] args ) {
        // Verify that we have to correct version of the jvm
        String jvmVersion = System.getProperty( "java.version" );
        String jvmMainVersion = jvmVersion.substring( 0, jvmVersion.lastIndexOf( "." ) );
        float jvmVersionFloat = Float.parseFloat( jvmMainVersion );
        if ( jvmVersionFloat < 1.6f ) {
            String message = "The JPO application uses new graphics features\n" + "that were added to the Java language in version\n" + "1.6.0. You are using version " + jvmVersion + " and must upgrade.\n";
            System.out.println( message );
            JOptionPane.showMessageDialog( Settings.anchorFrame, message, "Old Version Error", JOptionPane.ERROR_MESSAGE );
            System.exit( 1 );
        }

        // somewhat rabid way of allowing the application access to the local filesystem. RE 13. Nov 2007
        System.setSecurityManager( null );

        new Jpo();

    }


    /**
     *  Constructor for the Jpo application that creates the main JFrame, attaches an
     *  {@link ApplicationJMenuBar}, adds a JSplitPane to which it adds the {@link CollectionJTreeController}
     *  on the left side and a {@link ThumbnailJScrollPane} on the right side.
     */
    public Jpo() {
        System.out.println( "\nJPO version 0.9\n" + "Copyright (C) 2000-2009 Richard Eigenmann\n" + "JPO comes with ABSOLUTELY NO WARRANTY;\n" + "for details Look at the Help | License menu item.\n" + "This is free software, and you are welcome\n" + "to redistribute it under certain conditions;\n" + "see Help | License for details.\n\n" );
        Settings.loadSettings();

        Tools.log( "------------------------------------------------------------" );
        Tools.log( "Starting JPO" );

        // does this give us any performance gains?? RE 7.6.2004
        javax.imageio.ImageIO.setUseCache( false );
        try {
            // Activate OpenGL performance improvements
            //System.setProperty("sun.java2d.opengl", "true");
            SwingUtilities.invokeAndWait( new Runnable() {

                public void run() {
                    initComponents();
                }
            } );
        } catch ( InterruptedException ex ) {
            Logger.getLogger( Jpo.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( InvocationTargetException ex ) {
            Logger.getLogger( Jpo.class.getName() ).log( Level.SEVERE, null, ex );
        }
        Settings.pictureCollection.getTreeModel().addTreeModelListener( new MainAppModelListener() );
        loadCollectionOnStartup();
        new CameraWatchDaemon();

    }

    /**
     *  This object does all the tree work. It can load and save the nodes of the tree, listens to
     *  events happening on the tree and calls back with any actions that should be performed.
     *
     * @see CollectionJTreeController
     */
    public static CollectionJTreeController collectionJTreeController;

    /**
     *  This object holds all the thumbnails and deals with all the thumbnail events.
     * ToDo: Make this private again when the wordlist browser is properly integrated
     **/
    public static ThumbnailJScrollPane thumbnailJScrollPane;

    /**
     *  This Vector allows us to keep track of the number of ThumbnailCreationThreads
     *  we have fired off. Could be enhanced to dynamically start more or less.
     */
    private static Vector<ThumbnailCreationFactory> thumbnailFactories = new Vector<ThumbnailCreationFactory>();


    /**
     *  static initializer for the ThumbnailCreationThreads
     */
    static {
        for ( int i = 1; i <= Settings.numberOfThumbnailCreationThreads; i++ ) {
            thumbnailFactories.add( new ThumbnailCreationFactory() );
        }
    }


    /**
     *  This method initialises the GUI components of the main window.
     */
    private void initComponents() {
        try {
            //final String GTK = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            final String Windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
            //final String Metal = "javax.swing.plaf.metal.MetalLookAndFeel";
            //final String CDE = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

            UIManager.setLookAndFeel( Windows );
        } catch ( Exception e ) {
            // System.out.println( "Jpo.main: Could not set Look and Feel");
        }
        //ScreenHelper.explainGraphicsEnvironment();
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
                closeJpo();
            }
        } );
        Settings.anchorFrame = this;
        setTitle( Settings.jpoResources.getString( "ApplicationTitle" ) );


        setMinimumSize( Settings.jpoJFrameMinimumSize );
        setPreferredSize( Settings.mainFrameDimensions );

        //Create the menu bar.
        ApplicationJMenuBar menuBar = new ApplicationJMenuBar( this );
        setJMenuBar( menuBar );

        // Set Tooltipps to snappy mode
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setDismissDelay( 600 );
        ttm.setInitialDelay( 100 );



        // Set up the Info Panel
        InfoPanel infoPanel = new InfoPanel();

        /**
         *  The pane that holds the main window. On the left will go the tree, on the
         *  right will go the thumbnails
         **/
        final JSplitPane leftSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        leftSplitPane.setDividerSize( Settings.dividerWidth );
        leftSplitPane.setOneTouchExpandable( true );

        final JTabbedPane jpoNavigatorJTabbedPane = new JTabbedPane();
        jpoNavigatorJTabbedPane.setMinimumSize( Settings.jpoNavigatorJTabbedPaneMinimumSize );
        jpoNavigatorJTabbedPane.setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );

        //leftSplitPane.setTopComponent( jpoNavigatorJTabbedPane );
        leftSplitPane.setBottomComponent( infoPanel );
        leftSplitPane.setDividerLocation( Settings.preferredLeftDividerSpot );
        /**
         *  The pane that holds the main window. On the left will go the tree, on the
         *  right will go the thumbnails
         **/
        final JSplitPane masterSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
        masterSplitPane.setDividerSize( Settings.dividerWidth );
        masterSplitPane.setOneTouchExpandable( true );
        masterSplitPane.setDividerLocation( Settings.preferredMasterDividerSpot );


        //Add the split pane to this frame.
        getContentPane().add( masterSplitPane, BorderLayout.CENTER );
        pack();

        if ( Settings.maximiseJpoOnStartup ) {
            setExtendedState( MAXIMIZED_BOTH );
        }


        collectionJTreeController = new CollectionJTreeController();
        //JScrollPane collectionJScrollPane = new JScrollPane( collectionJTreeController.getComponent() );
        //collectionJScrollPane.setMinimumSize( Settings.jpoNavigatorJTabbedPaneMinimumSize );
        //collectionJScrollPane.setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );
        JScrollPane collectionJScrollPane = collectionJTreeController.getJScrollPane();
        collectionJScrollPane.setMinimumSize( Settings.jpoNavigatorJTabbedPaneMinimumSize );
        collectionJScrollPane.setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );

        QueriesJTree searchesJTree = new QueriesJTree();
        JScrollPane searchesJScrollPane = new JScrollPane( searchesJTree );
        searchesJScrollPane.setMinimumSize( Settings.jpoNavigatorJTabbedPaneMinimumSize );
        searchesJScrollPane.setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );

        jpoNavigatorJTabbedPane.add( Settings.jpoResources.getString( "jpoTabbedPaneCollection" ), collectionJScrollPane );
        jpoNavigatorJTabbedPane.add( Settings.jpoResources.getString( "jpoTabbedPaneSearches" ), searchesJScrollPane );
        leftSplitPane.setTopComponent( jpoNavigatorJTabbedPane );

        // Set up the Thumbnail Pane
        thumbnailJScrollPane = new ThumbnailJScrollPane();
        thumbnailJScrollPane.setFocusable( true );
        masterSplitPane.setLeftComponent( leftSplitPane );
        masterSplitPane.setRightComponent( thumbnailJScrollPane );


        // Set up the communication between the JTree and the Thumbnail Pane
        collectionJTreeController.setAssociatedThumbnailJScrollpane( thumbnailJScrollPane );
        collectionJTreeController.setAssociatedInfoPanel( infoPanel );
        searchesJTree.setAssociatedThumbnailJScrollpane( thumbnailJScrollPane );
        searchesJTree.setAssociatedInfoPanel( infoPanel );
        thumbnailJScrollPane.setAssociatedCollectionJTree( collectionJTreeController );
        thumbnailJScrollPane.setAssociatedInfoPanel( infoPanel );
        Settings.mainCollectionJTreeController = collectionJTreeController;

        infoPanel.addComponentListener( new ComponentAdapter() {

            @Override
            public void componentResized( ComponentEvent event ) {
                //Tools.log( "Jpo:InfoPanelcomponentResized invoked" );
                //Tools.log( "collectionJTree.preferredSize: " + collectionJTree.getPreferredSize().toString() );
                //Tools.log( "jpoNavigatorJTabbedPane.preferredSize: " + jpoNavigatorJTabbedPane.getPreferredSize().toString() );
                int leftDividerSpot = leftSplitPane.getDividerLocation();
                if ( leftDividerSpot != Settings.preferredLeftDividerSpot ) {
                    Settings.preferredLeftDividerSpot = leftDividerSpot;
                    Settings.unsavedSettingChanges = true;
                }
            }
        } );

        jpoNavigatorJTabbedPane.addComponentListener( new ComponentAdapter() {

            @Override
            public void componentResized( ComponentEvent event ) {
                //Tools.log( "Jpo.collectionJTree.componentResized invoked" );
                int dividerSpot = masterSplitPane.getDividerLocation();
                if ( dividerSpot != Settings.preferredMasterDividerSpot ) {
                    Settings.preferredMasterDividerSpot = dividerSpot;
                    Settings.unsavedSettingChanges = true;
                }
            }
        } );
        setVisible( true );
    }


    /**
     *  Brings up a QueryJFrame GUI.
     * @param startSearchNode
     */
    public void find( SortableDefaultMutableTreeNode startSearchNode ) {
        new QueryJFrame( startSearchNode, thumbnailJScrollPane );
    }


    /**
     *  method that is invoked when the Jpo application is to be closed. Checks if
     *  the main application window size should be saved and saves if necessary.
     *  also checks for unsaved changes before closing the application.
     */
    public void closeJpo() {
        if ( checkUnsavedUpdates() ) {
            return;
        }

        if ( Settings.unsavedSettingChanges ) {
            Settings.writeSettings();
        }

        Tools.log( "Exiting JPO" );
        Tools.log( "------------------------------------------------------------" );

        System.exit( 0 );
    }


    /**
     *  This method should be called after the application has started up. It tries to load a
     *  collection indicated in the ini file or collection jar.
     */
    public void loadCollectionOnStartup() {
        // load from jar or load from autoload instruction
        Settings.jarAutostartList = ClassLoader.getSystemResource( "autostartJarPicturelist.xml" );
        if ( Settings.jarAutostartList != null ) {
            Settings.jarRoot = Settings.jarAutostartList.toString().substring( 0, Settings.jarAutostartList.toString().indexOf( "!" ) + 1 );
            Tools.log( "Trying to load picturelist from jar: " + Settings.jarAutostartList.toString() );
            try {
                Settings.pictureCollection.getRootNode().streamLoad( Settings.jarAutostartList.openStream() );
                thumbnailJScrollPane.show( new GroupBrowser( Settings.pictureCollection.getRootNode() ) );
            } catch ( IOException x ) {
                Tools.log( Settings.jarAutostartList.toString() + " could not be loaded\nReason: " + x.getMessage() );
            }
        } else if ( ( Settings.autoLoad != null ) && ( Settings.autoLoad.length() > 0 ) ) {
            File xmlFile = new File( Settings.autoLoad );
            Tools.log( "Jpo.constructor: Trying to load collection from location in stored settings: " + Settings.autoLoad );
            if ( xmlFile.exists() ) {
                try {
                    Settings.pictureCollection.fileLoad( xmlFile );
                } catch ( FileNotFoundException ex ) {
                    Logger.getLogger( Jpo.class.getName() ).log( Level.SEVERE, null, ex );
                    requestFileNew();
                }
                positionToNode( Settings.pictureCollection.getRootNode() );
            }
        } else {
            requestFileNew();
        }
    }


    /**
     *   Call to do the File|New function
     */
    public void requestFileNew() {
        if ( checkUnsavedUpdates() ) {
            return;
        }
        Settings.pictureCollection.clearCollection();
        positionToNode( Settings.pictureCollection.getRootNode() );
    }


    /**
     *   Creates a {@link PictureAdder} object and tells it to
     *   add the selected pictures to the root node of the
     *   {@link CollectionJTreeController}.
     */
    public void requestFileAdd() {
        collectionJTreeController.setPopupNode( Settings.pictureCollection.getRootNode() );
        collectionJTreeController.requestAdd();
    }


    /**
     *   Creates a {@link PictureAdder} object and tells it to
     *   add the selected pictures to the root node of the
     *   {@link CollectionJTreeController}.
     */
    public void requestFileAddFromCamera() {
        new AddFromCamera( Settings.pictureCollection.getRootNode() );
        positionToNode( Settings.pictureCollection.getRootNode() );
    }


    /**
     *   Brings up a dialog where the user can select the collection
     *   to be loaded. Calls {@link SortableDefaultMutableTreeNode#fileLoad}
     */
    public void requestFileLoad() {
        if ( checkUnsavedUpdates() ) {
            return;
        }
        final File fileToLoad = Tools.chooseXmlFile();
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    Settings.pictureCollection.fileLoad( fileToLoad );
                } catch ( FileNotFoundException ex ) {
                    Tools.log( this.getClass().toString() + ".requestFileLoad: FileNotFoundExecption: " + ex.getMessage() );
                    JOptionPane.showMessageDialog( Settings.anchorFrame,
                            ex.getMessage(),
                            Settings.jpoResources.getString( "genericError" ),
                            JOptionPane.ERROR_MESSAGE );

                    return;
                }
                positionToNode( Settings.pictureCollection.getRootNode() );
            }
        };
        t.start();
    }


    /**
     *  A convenience method that tells the Tree and the Thumbnail pane to position themselves
     *  on the supplied node.
     * @param displayNode
     */
    public static void positionToNode( SortableDefaultMutableTreeNode displayNode ) {
        collectionJTreeController.setSelectedNode( displayNode );
        thumbnailJScrollPane.show( new GroupBrowser( displayNode ) );
    }


    /**
     *   Requests a recently loaded collection to be loaded. The index
     *   of which recently opened file to load is supplied from the
     *   {@link ApplicationJMenuBar} through the interface method
     *   {@link ApplicationMenuInterface#requestOpenRecent}.
     */
    public void requestOpenRecent( final int i ) {
        if ( checkUnsavedUpdates() ) {
            return;
        }
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    Settings.pictureCollection.fileLoad( new File( Settings.recentCollections[i] ) );
                } catch ( FileNotFoundException ex ) {
                    Logger.getLogger( Jpo.class.getName() ).log( Level.SEVERE, null, ex );
                    Tools.log( this.getClass().toString() + ".requestFileLoad: FileNotFoundExecption: " + ex.getMessage() );
                    JOptionPane.showMessageDialog( Settings.anchorFrame,
                            ex.getMessage(),
                            Settings.jpoResources.getString( "genericError" ),
                            JOptionPane.ERROR_MESSAGE );
                    return;
                }
                positionToNode( Settings.pictureCollection.getRootNode() );
            }
        };
        t.start();
    }


    /**
     *  method that checks for unsaved changes in the data model and asks if you really want to discard them.
     *  It returns true if the user want to cancel the close.
     * @return
     */
    public boolean checkUnsavedUpdates() {
        if ( Settings.pictureCollection.getUnsavedUpdates() ) {
            Object[] options = {
                Settings.jpoResources.getString( "discardChanges" ),
                Settings.jpoResources.getString( "genericSaveButtonLabel" ),
                Settings.jpoResources.getString( "FileSaveAsMenuItemText" ),
                Settings.jpoResources.getString( "genericCancelText" ) };
            int option = JOptionPane.showOptionDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "unsavedChanges" ),
                    Settings.jpoResources.getString( "genericWarning" ),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0] );
            switch ( option ) {
                case 0:
                    return false;
                case 1:
                    Settings.pictureCollection.fileSave();
                    return Settings.pictureCollection.getUnsavedUpdates();
                case 2:
                    fileSaveAs();
                    return Settings.pictureCollection.getUnsavedUpdates();
                case 3:
                    return true;
            }
        }
        return false;
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
        if ( Settings.pictureCollection.getXmlFile() != null ) {
            jFileChooser.setCurrentDirectory( Settings.pictureCollection.getXmlFile() );
        } else {
            jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );
        }

        int returnVal = jFileChooser.showSaveDialog( Settings.anchorFrame );
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File chosenFile = jFileChooser.getSelectedFile();
            chosenFile = Tools.correctFilenameExtension( "xml", chosenFile );
            if ( chosenFile.exists() ) {
                int answer = JOptionPane.showConfirmDialog( Settings.anchorFrame,
                        Settings.jpoResources.getString( "confirmSaveAs" ),
                        Settings.jpoResources.getString( "genericWarning" ),
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE );
                if ( answer == JOptionPane.CANCEL_OPTION ) {
                    return;
                }
            }

            Settings.pictureCollection.setXmlFile( chosenFile );
            Settings.pictureCollection.fileSave();

            Settings.memorizeCopyLocation( chosenFile.getParent() );
            Settings.pushRecentCollection( chosenFile.toString() );
            afterFileSaveDialog();
        }
    }


    /**
     *   Calls the {@link PictureCollection#fileSave} method that saves the
     *   current collection under it's present name and if it was never
     *   saved before brings up a popup window.
     */
    public void requestFileSave() {
        Settings.pictureCollection.fileSave();
    }


    /**
     *   saves the file and asks whether the file should be opened by default.
     */
    public void fileSave() {
        Settings.pictureCollection.fileSave();
        afterFileSaveDialog();
    }


    /**
     *   Ask whether the file should be opened by default.
     */
    public void afterFileSaveDialog() {
        /*JOptionPane.showMessageDialog( Settings.anchorFrame,
        Settings.jpoResources.getString("collectionSaveBody") + xmlFile.toString(),
        Settings.jpoResources.getString("collectionSaveTitle"),
        JOptionPane.INFORMATION_MESSAGE);*/
        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.add( new JLabel( Settings.jpoResources.getString( "collectionSaveBody" ) + Settings.pictureCollection.getXmlFile().toString() ) );
        JCheckBox setAutoload = new JCheckBox( Settings.jpoResources.getString( "setAutoload" ) );
        if ( ( new File( Settings.autoLoad ) ).compareTo( Settings.pictureCollection.getXmlFile() ) == 0 ) {
            setAutoload.setSelected( true );
        }
        p.add( setAutoload );
        JOptionPane.showMessageDialog( Settings.anchorFrame,
                p,
                Settings.jpoResources.getString( "collectionSaveTitle" ),
                JOptionPane.INFORMATION_MESSAGE );
        if ( setAutoload.isSelected() ) {
            Settings.autoLoad = Settings.pictureCollection.getXmlFile().toString();
            Settings.writeSettings();
        }
    }


    /**
     *   Calls the {@link PictureCollection#fileSaveAs} method to bring up
     *   a filechooser where the user can select the filename to
     *   save under.
     */
    public void requestFileSaveAs() {
        fileSaveAs();
    }


    /**
     *   Calls {@link #closeJpo} to shut down the application.
     */
    public void requestExit() {
        closeJpo();
    }


    /**
     *   Calls {@link #find} to bring up a find dialog box.
     */
    public void requestEditFind() {
        find( Settings.pictureCollection.getRootNode() );
    }


    /**
     *   Creates a {@link ReconcileJFrame} which lets the user
     *   specify a directory whose pictures are then compared
     *   against the current collection.
     */
    public void requestCheckDirectories() {
        new ReconcileJFrame( Settings.pictureCollection.getRootNode() );
    }


    /**
     *   Creates a {@link CollectionPropertiesJFrame} that displays
     *   statistics about the collection and allows the user to
     *   protect it from edits.
     */
    public void requestCollectionProperties() {
        new CollectionPropertiesJFrame( Settings.pictureCollection.getRootNode() );
    }


    /**
     *  Creates an IntegrityChecker that does it's magic on the collection.
     */
    public void requestCheckIntegrity() {
        new IntegrityChecker( Settings.pictureCollection.getRootNode() );
    }


    /**
     *   Creates a {@link SettingsDialog} where the user can edit
     *   Application wide settings.
     */
    public void requestEditSettings() {
        new SettingsDialog( this, true );
    }


    /**
     *   opens up the Camera Editor GUI. See {@link CameraEditor}
     */
    public void requestEditCameras() {
        new CameraEditor();
    }


    /**
     *   calls up the Pictureviewer
     */
    public void performSlideshow() {
        PictureViewer p1 = new PictureViewer();
        p1.switchWindowMode( ResizableJFrame.WINDOW_LEFT );
        p1.switchDecorations( true );
        PictureViewer p2 = new PictureViewer();
        p2.switchWindowMode( ResizableJFrame.WINDOW_RIGHT );
        p2.switchDecorations( true );
        RandomBrowser rb1 = new RandomBrowser( Settings.pictureCollection.getRootNode() );
        RandomBrowser rb2 = new RandomBrowser( Settings.pictureCollection.getRootNode() );
        p1.changePicture( rb1, 0 );
        p1.startAdvanceTimer( 10 );
        p2.changePicture( rb2, 0 );
        p2.startAdvanceTimer( 10 );
    }

    private class MainAppModelListener implements TreeModelListener {

        public void treeNodesChanged( TreeModelEvent e ) {
            TreePath tp = e.getTreePath();
            if ( tp.getPathCount() == 1 ) { //if the root node sent the event
                updateApplicationTitle();
            }
        }


        public void treeNodesInserted( TreeModelEvent e ) {
            // ignore
        }


        public void treeNodesRemoved( TreeModelEvent e ) {
            // ignore, the root can't be removed ... Really?
        }


        public void treeStructureChanged( TreeModelEvent e ) {
            TreePath tp = e.getTreePath();
            if ( tp.getPathCount() == 1 ) { //if the root node sent the event
                updateApplicationTitle();
            }
        }
    }


    /**
     * Sets the application title to the default tile based on the Resourcebundle string
     * ApplicationTitle and the file name of the loaded xml file if any.
     */
    private void updateApplicationTitle() {
        final File xmlFile = Settings.pictureCollection.getXmlFile();
        if ( xmlFile != null ) {
            updateApplicationTitle( Settings.jpoResources.getString( "ApplicationTitle" ) + ":  " + xmlFile.toString() );
        } else {
            updateApplicationTitle( Settings.jpoResources.getString( "ApplicationTitle" ) );
        }
    }


    /**
     * Swing EDT invoking method that sets the title of the Frame to the new name
     * @param newTitle The new title of the Frame
     */
    private void updateApplicationTitle( final String newTitle ) {
        Runnable r = new Runnable() {

            public void run() {
                Settings.anchorFrame.setTitle( newTitle );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            new Thread( r ).start();
        }
    }
}
