/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpo.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import jpo.dataModel.DuplicatesQuery;
import jpo.dataModel.QueryNavigator;
import jpo.dataModel.RandomNavigator;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;
import static jpo.gui.Jpo.collectionJTreeController;
import static jpo.gui.Jpo.positionToNode;
import static jpo.gui.Jpo.searchesJTree;
import static jpo.gui.Jpo.showThumbnails;
import jpo.gui.swing.MainWindow;
import jpo.gui.swing.QueryJFrame;
import jpo.gui.swing.ResizableJFrame;

/**
 *
 * @author Richard Eigenmann
 */
/**
 * This class handles all the Application Menu Events
 */
public class ApplicationEventHandler
        implements ApplicationMenuInterface {

    
    public void setMainWindow( MainWindow mainWindow ) {
        this.mainWindow = mainWindow;
    }
    
    private MainWindow mainWindow;
    
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ApplicationEventHandler.class.getName() );

    /**
     * The user wants to find duplicates
     */
    @Override
    public void requestFindDuplicates() {
        DuplicatesQuery duplicatesQuery = new DuplicatesQuery();
        DefaultMutableTreeNode newNode = Settings.pictureCollection.addQueryToTreeModel( duplicatesQuery );
        showQuery( newNode );
        QueryNavigator queryBrowser = new QueryNavigator( duplicatesQuery );
        showThumbnails( queryBrowser );
    }

    /**
     * Opens up a Year Browser
     */
    @Override
    public void requestYearlyAnalyis() {
        new YearlyAnalysisGuiController( Settings.pictureCollection.getRootNode() );
    }

    /**
     * Opens up a Year Browser
     */
    @Override
    public void requestYearBrowser() {
        new YearsBrowserController( Settings.pictureCollection.getRootNode() );
    }

    /**
     * Creates an IntegrityChecker that does it's magic on the collection.
     */
    @Override
    public void requestCheckIntegrity() {
        new IntegrityCheckerJFrame( Settings.pictureCollection.getRootNode() );
    }

    /**
     * Creates a {@link SettingsDialog} where the user can edit Application wide
     * settings.
     */
    @Override
    public void requestEditSettings() {
        new SettingsDialog( true );
    }

    /**
     * opens up the Camera Editor GUI. See {@link CamerasEditor}
     */
    @Override
    public void requestEditCameras() {
        new CamerasEditor();
    }

    /**
     * method that is invoked when the Jpo application is to be closed. Checks
     * if the main application window size should be saved and saves if
     * necessary. also checks for unsaved changes before closing the
     * application.
     */
    @Override
    public void requestExit() {
        if ( checkUnsavedUpdates() ) {
            return;
        }

        if ( Settings.unsavedSettingChanges ) {
            Settings.writeSettings();
        }

        LOGGER.info( "Exiting JPO\n------------------------------------------------------------" );

        System.exit( 0 );
    }

    /**
     * Calls {@link #find} to bring up a find dialog box.
     */
    @Override
    public void openFindDialog() {
        find( Settings.pictureCollection.getRootNode() );
    }

    /**
     * Brings up a QueryJFrame GUI.
     *
     * @param startSearchNode
     */
    public void find( SortableDefaultMutableTreeNode startSearchNode ) {
        new QueryJFrame( startSearchNode, this );
    }

    /**
     * Creates a {@link ReconcileJFrame} which lets the user specify a directory
     * whose pictures are then compared against the current collection.
     */
    @Override
    public void requestCheckDirectories() {
        new ReconcileJFrame( Settings.pictureCollection.getRootNode() );
    }

    /**
     * calls up the Picture Viewer
     */
    @Override
    public void performSlideshow() {
        PictureViewer p1 = new PictureViewer();
        p1.pictureFrame.myJFrame.switchWindowMode( ResizableJFrame.WINDOW_LEFT );
        //p1.switchDecorations( true );
        PictureViewer p2 = new PictureViewer();
        p2.pictureFrame.myJFrame.switchWindowMode( ResizableJFrame.WINDOW_RIGHT );
        //p2.switchDecorations( true );
        RandomNavigator rb1 = new RandomNavigator( Settings.pictureCollection.getRootNode().getChildPictureNodes( true ), String.format( "Randomised pictures from %s", Settings.pictureCollection.getRootNode().toString() ) );
        RandomNavigator rb2 = new RandomNavigator( Settings.pictureCollection.getRootNode().getChildPictureNodes( true ), String.format( "Randomised pictures from %s", Settings.pictureCollection.getRootNode().toString() ) );
        p1.show( rb1, 0 );
        p1.startAdvanceTimer( 10 );
        p2.show( rb2, 0 );
        p2.startAdvanceTimer( 10 );
    }

    /**
     * The {@link ApplicationMenuInterface} calls here when the user wants to
     * add pictures to the root node of the collection.
     */
    @Override
    public void requestAddPictures() {
        chooseAndAddPicturesToGroup( Settings.pictureCollection.getRootNode() );
    }

    /**
     * Brings up a dialog where the user can select the collection to be loaded.
     * Calls {@link SortableDefaultMutableTreeNode#fileLoad}
     */
    @Override
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
                    LOGGER.log( Level.INFO, "FileNotFoundExecption: {0}", ex.getMessage() );
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
     * Call to do the File|New function
     */
    @Override
    public void requestFileNew() {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                if ( checkUnsavedUpdates() ) {
                    return;
                }
                Settings.pictureCollection.clearCollection();
                positionToNode( Settings.pictureCollection.getRootNode() );
            }
        };
        SwingUtilities.invokeLater( r );
    }

    /**
     * Calls the {@link jpo.dataModel.PictureCollection#fileSave} method that
     * saves the current collection under it's present name and if it was never
     * saved before brings up a popup window.
     */
    @Override
    public void requestFileSave() {
        if ( Settings.pictureCollection.getXmlFile() == null ) {
            requestFileSaveAs();
        } else {
            LOGGER.info( String.format( "Saving under the name: %s", Settings.pictureCollection.getXmlFile() ) );
            Settings.pictureCollection.fileSave();
            afterFileSaveDialog();
        }
    }

    /**
     * method that saves the entire index in XML format. It prompts for the
     * filename first.
     */
    @Override
    public void requestFileSaveAs() {
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
     * Ask whether the file should be opened by default.
     */
    public void afterFileSaveDialog() {
        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.add( new JLabel( Settings.jpoResources.getString( "collectionSaveBody" ) + Settings.pictureCollection.getXmlFile().toString() ) );
        JCheckBox setAutoload = new JCheckBox( Settings.jpoResources.getString( "setAutoload" ) );
        if ( ( !( Settings.autoLoad == null ) ) && ( ( new File( Settings.autoLoad ) ).compareTo( Settings.pictureCollection.getXmlFile() ) == 0 ) ) {
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
     * Requests that a collection be added at this point in the tree
     *
     * @param popupNode The node at which to add
     * @param fileToLoad The collection file to load
     * @see GroupPopupInterface
     */
    public void requestAddCollection(
            SortableDefaultMutableTreeNode popupNode,
            File fileToLoad ) {
        SortableDefaultMutableTreeNode newNode = popupNode.addGroupNode( "New Group" );
        try {
            newNode.fileLoad( fileToLoad );
        } catch ( FileNotFoundException x ) {
            LOGGER.log( Level.INFO, "{0}.fileToLoad: FileNotFoundExecption: {1}", new Object[]{ this.getClass().toString(), x.getMessage() } );
            JOptionPane.showMessageDialog( Settings.anchorFrame,
                    "File not found:\n" + fileToLoad.getPath(),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
        }
        newNode.getPictureCollection().setUnsavedUpdates( true );
        positionToNode( newNode );
        collectionJTreeController.expandPath( new TreePath( newNode.getPath() ) );
    }

    /**
     * Requests a recently loaded collection to be loaded. The index of which
     * recently opened file to load is supplied from the
     * {@link ApplicationJMenuBar} through the interface method
     * {@link ApplicationMenuInterface#requestOpenRecent}.
     */
    @Override
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
                    LOGGER.log( Level.INFO, "FileNotFoundExecption: {0}", ex.getMessage() );
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
     * Switches the left panel to the queries, expands to the right place and
     * shows the thumbnails of the query.
     *
     * @param node The query node to be displayed
     */
    public void showQuery( DefaultMutableTreeNode node ) {
        mainWindow.tabToSearches();
        searchesJTree.setSelectedNode( node );
    }

    /**
     * Calling this method brings up a filechooser which allows pictures and
     * directories to be selected that are then added to the supplied node.
     *
     * @param groupNode The group node to which to add the pictures or
     * subdirectories
     */
    public void chooseAndAddPicturesToGroup(
            SortableDefaultMutableTreeNode groupNode ) {
        PictureFileChooser pa = new PictureFileChooser( groupNode );
    }

    /**
     * Checks for unsaved changes in the data model, pops up a dialog and does
     * the save if so indicated by the user.
     *
     * @return Returns true if the user want to cancel the close.
     */
    public boolean checkUnsavedUpdates() {
        Tools.checkEDT();
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
                    requestFileSave();
                    return Settings.pictureCollection.getUnsavedUpdates();
                case 2:
                    requestFileSaveAs();
                    return Settings.pictureCollection.getUnsavedUpdates();
                case 3:
                    return true;
            }
        }
        return false;
    }

}
