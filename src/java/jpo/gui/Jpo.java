package jpo.gui;

import jpo.gui.swing.QueriesJTree;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import jpo.dataModel.FlatGroupNavigator;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.GroupNavigator;
import jpo.dataModel.NodeNavigatorInterface;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.gui.swing.CollectionJTree;
import jpo.gui.swing.MainWindow;
import jpotestground.CheckThreadViolationRepaintManager;


/*
Jpo.java:  The collection controller object of the JPO application

Copyright (C) 2002 - 2013  Richard Eigenmann.
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
 * This is the collection controller the Java Picture Organizer application that lets
 * a user view a collection of pictures in as thumbnails, in a separate window
 * or in a full sized mode.<p>
 *
 *
 * <p><img src=../Overview.png border=0><p>
 *
 * It uses a list of pictures (PictureList file) to create a hierarchical model of
 * <code>SortableDefaultMutableTreeNode</code>s that represent the structure of the collection.
 * Each node has an associated object of {@link GroupInfo} or {@link PictureInfo} type.
 *
 * The Jpo class creates the following main objects:
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
 * @version 0.11
 * @see CollectionJTree
 * @see ThumbnailPanelController
 * @see PictureViewer
 * @since JDK1.6.0
 */
public class Jpo {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( Jpo.class.getName() );

    private final ApplicationEventHandler applicationEventHandler = new ApplicationEventHandler();
    
    /**
     *  Constructor for the Jpo application that creates the main JFrame, attaches an
     *  {@link ApplicationJMenuBar}, adds a JSplitPane to which it adds the {@link CollectionJTreeController}
     *  on the left side and a {@link ThumbnailPanelController} on the right side.
     */
    public Jpo() {

        // set up logging level
        Handler[] handlers =
                Logger.getLogger( "" ).getHandlers();
        for ( Handler handler : handlers ) {
            handler.setLevel( Level.FINEST );
        }
        Settings.loadSettings();

        LOGGER.info( "------------------------------------------------------------\n      Starting JPO" );


        // Check for EDT violations
        RepaintManager.setCurrentManager( new CheckThreadViolationRepaintManager() );


        // does this give us any performance gains?? RE 7.6.2004
        javax.imageio.ImageIO.setUseCache( false );
        try {
            // Activate OpenGL performance improvements
            System.setProperty( "sun.java2d.opengl", "true" );
            SwingUtilities.invokeAndWait( new Runnable() {

                @Override
                public void run() {
                    ApplicationJMenuBar menuBar = new ApplicationJMenuBar( applicationEventHandler );
                    collectionJTreeController = new CollectionJTreeController( applicationEventHandler );
                    searchesJTree = new QueriesJTree();
                    thumbnailPanelController = new ThumbnailPanelController( new JScrollPane() );
                    infoPanelController = new InfoPanelController();
                    mainWindow = new MainWindow( menuBar, collectionJTreeController.getJScrollPane(), searchesJTree.getJComponent(), thumbnailPanelController.getView(), infoPanelController.getInfoPanel(), infoPanelController.getTagCloud() );
                    applicationEventHandler.setMainWindow( mainWindow);
                    mainWindow.addWindowListener( new WindowAdapter() {

                        @Override
                        public void windowClosing( WindowEvent e ) {
                            mainWindow.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
                            applicationEventHandler.requestExit();
                        }
                    } );
                     Settings.anchorFrame = mainWindow;
                }
            } );
        } catch ( InterruptedException ex ) {
            LOGGER.log( Level.SEVERE, null, ex );
        } catch ( InvocationTargetException ex ) {
            LOGGER.log( Level.SEVERE, null, ex );
        }

        Settings.pictureCollection.getTreeModel().addTreeModelListener( new MainAppModelListener() );
        if ( !loadAutoloadCollection() ) {
            applicationEventHandler.requestFileNew();
        }
        new CameraWatchDaemon( this );

    }


    /**
     * This class handles the the main window
     */
    private static MainWindow mainWindow;
    
    /**
     *  This object does all the tree work. It can load and save the nodes of the tree, listens to
     *  events happening on the tree and calls back with any actions that should be performed.
     *
     * @see CollectionJTreeController
     */
    public static CollectionJTreeController collectionJTreeController;
    /**
     * This object does the controller work for the Queries.
     */
    public static QueriesJTree searchesJTree;
    
    /**
     *  The controller for the thumbnail panel
     *  ToDo: Make this private again when the wordlist browser is properly integrated
     **/
    public static ThumbnailPanelController thumbnailPanelController;
    /**
     * The InfoPanelController
     */
    private static InfoPanelController infoPanelController;
    /**
     *  This Vector allows us to keep track of the number of ThumbnailCreationThreads
     *  we have fired off. Could be enhanced to dynamically start more or less.
     */
    public final static ArrayList<ThumbnailCreationFactory> THUMBNAIL_FACTORIES = new ArrayList<ThumbnailCreationFactory>();

    /**
     *  static initializer for the ThumbnailCreationThreads
     */
    static {
        for ( int i = 1; i <= Settings.numberOfThumbnailCreationThreads; i++ ) {
            THUMBNAIL_FACTORIES.add( new ThumbnailCreationFactory() );
        }
    }

    /**
     *  This method looks if it can find a file called autostartJarPicturelist.xml in the classpath;
     *  failing that it loads the file indicated in Settings.autoLoad.
     *  @return returns whether this was successful or not.
     */
    public boolean loadAutoloadCollection() {
        if ( ( Settings.autoLoad != null ) && ( Settings.autoLoad.length() > 0 ) ) {
            File xmlFile = new File( Settings.autoLoad );
            LOGGER.log( Level.FINE, "File to Autoload: {0}", Settings.autoLoad );
            if ( xmlFile.exists() ) {
                try {
                    Settings.pictureCollection.fileLoad( xmlFile );
                } catch ( FileNotFoundException ex ) {
                    Logger.getLogger( Jpo.class.getName() ).log( Level.SEVERE, null, ex );
                    return false;
                }


                positionToNode( Settings.pictureCollection.getRootNode() );

                return true;
            } else {
                LOGGER.fine( String.format( "File %s doesn't exist. not loading", xmlFile.toString() ) );
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * A call to this method with a GroupInfo node will position the JTree to the
     * node and will open the node in the ThumbnailPanel. This method has been made
     * EDT safe so it can be called from any Thread.
     * @param displayNode must be of a node of type GroupInfo
     */
    public static void positionToNode(
            final SortableDefaultMutableTreeNode displayNode ) {

        if ( displayNode == null ) {
            LOGGER.severe( "I've been told to position to a null node!" );
            return;
        }
        if ( !( displayNode.getUserObject() instanceof GroupInfo ) ) {
            LOGGER.severe( "We can only position to GroupInfo nodes!" );
            return;
        }

        // Make it EDT safe
        Runnable r = new Runnable() {
            @Override
            public void run() {
                collectionJTreeController.setSelectedNode( displayNode );
                mainWindow.tabToCollection();
                showThumbnails( new GroupNavigator( displayNode ) );
                showInfo( displayNode );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }
    }

    /**
     * A call to this method with a ThumbnailBrowser will show the
     * nodes of in the Thumbnail pane.
     * @param nodeSet must be of a node of type GroupInfo
     */
    public static void showThumbnails( NodeNavigatorInterface nodeSet ) {
        if ( nodeSet == null ) {
            LOGGER.severe( "I've been told to showThumbnails on a null set!" );
            return;
        }
        thumbnailPanelController.show( nodeSet );
    }

    /**
     * A call to this method will result in the InfoPanel updating it's display
     * @param node the Node for which to show the info
     */
    public static void showInfo( SortableDefaultMutableTreeNode node ) {
        if ( node == null ) {
            LOGGER.severe( "I've been told to show Info on a null node!" );
            return;
        }
        infoPanelController.showInfo( node );
    }

    /**
     * Opens a Picture Browser for the specified node. A PictureInfo node
     * will open directly and a GroupInfo node will open at it's first child node.
     * @param node
     */
    public static void browsePictures( SortableDefaultMutableTreeNode node ) {
        Object o = node.getUserObject();
        if ( o instanceof PictureInfo ) {
            FlatGroupNavigator sb = new FlatGroupNavigator( (SortableDefaultMutableTreeNode) node.getParent() );
            int index = 0;
            for ( int i = 0; i < sb.getNumberOfNodes(); i++ ) {
                if ( sb.getNode( i ).equals( node ) ) {
                    index = i;
                    i = sb.getNumberOfNodes();
                }
            }
            PictureViewer pictureViewer = new PictureViewer();
            pictureViewer.show( sb, index );
        } else if ( o instanceof GroupInfo ) {
            SortableDefaultMutableTreeNode firstPicNode = node.findFirstPicture();
            if ( firstPicNode != null ) {
                FlatGroupNavigator sb = new FlatGroupNavigator( node );
                PictureViewer pictureViewer = new PictureViewer();
                pictureViewer.show( sb, 0 );
            } else {
                JOptionPane.showMessageDialog( Settings.anchorFrame,
                        Settings.jpoResources.getString( "noPicsForSlideshow" ),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
            }
        }

    }


    private class MainAppModelListener
            implements TreeModelListener {

        @Override
        public void treeNodesChanged( TreeModelEvent e ) {
            TreePath tp = e.getTreePath();
            LOGGER.fine( String.format( "The main app model listener trapped a tree node change event on the tree path: %s", tp.toString() ) );
            if ( tp.getPathCount() == 1 ) { //if the root node sent the event
                LOGGER.fine( "Since this is the root node we will update the ApplicationTitle" );

                updateApplicationTitle();
            }
        }

        @Override
        public void treeNodesInserted( TreeModelEvent e ) {
            // ignore
        }

        @Override
        public void treeNodesRemoved( TreeModelEvent e ) {
            // ignore, the root can't be removed ... Really?
        }

        @Override
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
            mainWindow.updateApplicationTitleEDT( Settings.jpoResources.getString( "ApplicationTitle" ) + ":  " + xmlFile.toString() );
        } else {
            mainWindow.updateApplicationTitleEDT( Settings.jpoResources.getString( "ApplicationTitle" ) );
        }
    }

}
