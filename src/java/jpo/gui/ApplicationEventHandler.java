/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpo.gui;

import com.google.common.eventbus.Subscribe;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
import jpo.EventBus.AddCollectionToGroupRequest;
import jpo.EventBus.AddEmptyGroupRequest;
import jpo.EventBus.AddFlatFileRequest;
import jpo.EventBus.AddGroupToEmailSelectionRequest;
import jpo.EventBus.CheckDirectoriesRequest;
import jpo.EventBus.CheckIntegrityRequest;
import jpo.EventBus.ChooseAndAddCollectionRequest;
import jpo.EventBus.ChooseAndAddFlatfileRequest;
import jpo.EventBus.ChooseAndAddPicturesToGroupRequest;
import jpo.EventBus.CloseApplicationRequest;
import jpo.EventBus.ConsolidateGroupRequest;
import jpo.EventBus.CopyLocationsChangedEvent;
import jpo.EventBus.EditCamerasRequest;
import jpo.EventBus.EditSettingsRequest;
import jpo.EventBus.ExportGroupToFlatFileRequest;
import jpo.EventBus.ExportGroupToHtmlRequest;
import jpo.EventBus.ExportGroupToNewCollectionRequest;
import jpo.EventBus.ExportGroupToPicasaRequest;
import jpo.EventBus.FileLoadRequest;
import jpo.EventBus.FileSaveAsRequest;
import jpo.EventBus.FileSaveRequest;
import jpo.EventBus.FindDuplicatesRequest;
import jpo.EventBus.OpenHelpAboutFrameRequest;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.MoveNodeDownRequest;
import jpo.EventBus.MoveNodeToBottomRequest;
import jpo.EventBus.MoveNodeToNodeRequest;
import jpo.EventBus.MoveNodeToTopRequest;
import jpo.EventBus.MoveNodeUpRequest;
import jpo.EventBus.OpenCategoryEditorRequest;
import jpo.EventBus.OpenLicenceFrameRequest;
import jpo.EventBus.OpenMainWindowRequest;
import jpo.EventBus.OpenPrivacyFrameRequest;
import jpo.EventBus.OpenRecentCollectionRequest;
import jpo.EventBus.OpenSearchDialogRequest;
import jpo.EventBus.RecentCollectionsChangedEvent;
import jpo.EventBus.RecentDropNodesChangedEvent;
import jpo.EventBus.RefreshThumbnailRequest;
import jpo.EventBus.RemoveNodeRequest;
import jpo.EventBus.RenamePictureRequest;
import jpo.EventBus.SetPictureRotationRequest;
import jpo.EventBus.RotatePictureRequest;
import jpo.EventBus.SendEmailRequest;
import jpo.EventBus.ShowCategoryUsageEditorRequest;
import jpo.EventBus.ShowGroupAsTableRequest;
import jpo.EventBus.ShowGroupInfoEditorRequest;
import jpo.EventBus.ShowGroupRequest;
import jpo.EventBus.ShowPictureInfoEditorRequest;
import jpo.EventBus.ShowPictureOnMapRequest;
import jpo.EventBus.ShowPictureRequest;
import jpo.EventBus.ShowQueryRequest;
import jpo.EventBus.SortGroupRequest;
import jpo.EventBus.StartCameraWatchDaemonRequest;
import jpo.EventBus.StartDoublePanelSlideshowRequest;
import jpo.EventBus.StartNewCollectionRequest;
import jpo.EventBus.UnsavedUpdatesDialogRequest;
import jpo.EventBus.YearBrowserRequest;
import jpo.EventBus.YearlyAnalysisRequest;
import jpo.cache.JpoCache;
import jpo.dataModel.DuplicatesQuery;
import jpo.dataModel.FlatFileReader;
import jpo.gui.swing.FlatFileDistiller;
import jpo.dataModel.FlatGroupNavigator;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.NodeNavigatorInterface;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.QueryNavigator;
import jpo.dataModel.RandomNavigator;
import jpo.dataModel.Settings;
import jpo.dataModel.Settings.FieldCodes;
import jpo.dataModel.SingleNodeNavigator;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;
import jpo.export.GenerateWebsiteWizard;
import jpo.export.PicasaUploadRequest;
import jpo.export.PicasaUploaderWizard;
import jpo.gui.swing.HelpAboutWindow;
import jpo.gui.swing.MainWindow;
import jpo.gui.swing.PrivacyJFrame;
import jpo.gui.swing.QueryJFrame;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_LEFT;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_RIGHT;
import webserver.Webserver;

/**
 *
 * @author Richard Eigenmann
 */
/**
 * This class handles all the Application Menu Events
 */
public class ApplicationEventHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ApplicationEventHandler.class.getName() );

    /**
     * This class handles most of the events flying around the JPO application
     */
    public ApplicationEventHandler() {
        registerOnEventBus();
    }

    private void registerOnEventBus() {
        JpoEventBus.getInstance().register( this );
    }

    /**
     * Opens the MainWindow
     *
     * @param request
     */
    @Subscribe
    public void handleOpenMainWindowRequest( OpenMainWindowRequest request ) {
        try {
            // Activate OpenGL performance improvements
            System.setProperty( "sun.java2d.opengl", "true" );
            SwingUtilities.invokeAndWait( new Runnable() {

                @Override
                public void run() {
                    new MainWindow();
                }
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            LOGGER.log( Level.SEVERE, null, ex );
        }
    }

    /**
     * The user wants to find duplicates
     *
     * @param request The request
     */
    @Subscribe
    public void handleFindDuplicatesRequest( FindDuplicatesRequest request ) {
        DuplicatesQuery duplicatesQuery = new DuplicatesQuery();
        DefaultMutableTreeNode newNode = Settings.pictureCollection.addQueryToTreeModel( duplicatesQuery );
        QueryNavigator queryBrowser = new QueryNavigator( duplicatesQuery );
        JpoEventBus.getInstance().post( new ShowQueryRequest( duplicatesQuery ) );
    }

    /**
     * Opens up a Year Browser
     *
     * @param request The request
     */
    @Subscribe
    public void handleYearlyAnalysisRequest( YearlyAnalysisRequest request ) {
        new YearlyAnalysisGuiController( Settings.pictureCollection.getRootNode() );
    }

    /**
     * Opens up a Year Browser
     *
     * @param request The request
     */
    @Subscribe
    public void handlerYearBrowserRequest( YearBrowserRequest request ) {
        new YearsBrowserController( Settings.pictureCollection.getRootNode() );
    }

    /**
     * Creates an IntegrityChecker that does it's magic on the collection.
     *
     * @param request The request
     */
    @Subscribe
    public void handleCheckIntegrityRequest( CheckIntegrityRequest request ) {
        new IntegrityCheckerJFrame( Settings.pictureCollection.getRootNode() );
    }

    /**
     * Creates a {@link SettingsDialog} where the user can edit Application wide
     * settings.
     *
     * @param request The request
     */
    @Subscribe
    public void handleEditSettingsRequest( EditSettingsRequest request ) {
        new SettingsDialog( true );
    }

    /**
     * Opens up the Camera Editor GUI. See {@link CamerasEditor}
     *
     * @param request the request object
     */
    @Subscribe
    public void handleEditCamerasRequest( EditCamerasRequest request ) {
        new CamerasEditor();
    }

    /**
     * Opens up the Camera Editor GUI. See {@link CamerasEditor}
     *
     * @param request the request object
     */
    @Subscribe
    public void handleSendEmailRequest( SendEmailRequest request ) {
        new EmailerGui();
    }

    /**
     * Shuts down JPO no questions asked. Wrap it as a next request with a
     * UnsavedUpdatesDialogRequest
     * @param request
     */
    @Subscribe
    public void handleCloseApplicationRequest( CloseApplicationRequest request ) {
        if ( Settings.unsavedSettingChanges ) {
            Settings.writeSettings();
        }

        JpoCache.getInstance().shutdown();

        LOGGER.info( "Exiting JPO\n------------------------------------------------------------" );

        System.exit( 0 );
    }

    /**
     * Creates a {@link ReconcileJFrame} which lets the user specify a directory
     * whose pictures are then compared against the current collection. Allows
     * the user to reconcile pictures in a directory with those in his
     * collection.
     *
     * @param request The request
     */
    @Subscribe
    public void handleCheckDirectoriesRequest( CheckDirectoriesRequest request ) {
        new ReconcileJFrame( Settings.pictureCollection.getRootNode() );
    }

    /**
     * Starts a double panel slideshow
     *
     * @param request The request
     */
    @Subscribe
    public void handleStartDoublePanelSlideshowRequest( StartDoublePanelSlideshowRequest request ) {
        SortableDefaultMutableTreeNode rootNode = request.getNode();
        PictureViewer p1 = new PictureViewer();
        p1.pictureFrame.myJFrame.switchWindowMode( WINDOW_LEFT );
        PictureViewer p2 = new PictureViewer();
        p2.pictureFrame.myJFrame.switchWindowMode( WINDOW_RIGHT );
        RandomNavigator rb1 = new RandomNavigator( rootNode.getChildPictureNodes( true ), String.format( "Randomised pictures from %s", rootNode.toString() ) );
        RandomNavigator rb2 = new RandomNavigator( rootNode.getChildPictureNodes( true ), String.format( "Randomised pictures from %s", rootNode.toString() ) );
        p1.show( rb1, 0 );
        p1.startAdvanceTimer( 10 );
        p2.show( rb2, 0 );
        p2.startAdvanceTimer( 10 );
    }

    /**
     * When we see a ShowPictureRequest this method will open a PictureViewer
     * and will tell it to show the FlatGroupNavigator based on the pictures
     * parent node starting at the current position
     *
     * @param request the ShowPictureRequest
     */
    @Subscribe
    public void handleShowPictureRequest( ShowPictureRequest request ) {
        SortableDefaultMutableTreeNode node = request.getNode();
        Object userObject = node.getUserObject();

        final NodeNavigatorInterface navigator;
        int index = 0;

        if ( userObject instanceof PictureInfo ) {
            navigator = new FlatGroupNavigator( (SortableDefaultMutableTreeNode) node.getParent() );
            for ( int i = 0; i < navigator.getNumberOfNodes(); i++ ) {
                if ( navigator.getNode( i ).equals( node ) ) {
                    index = i;
                    i = navigator.getNumberOfNodes();
                }
            }
        } else if ( userObject instanceof GroupInfo && node.hasChildPictureNodes() ) {
            navigator = new FlatGroupNavigator( node );
        } else {
            return; // should only be receiving PictureInfo or GroupInfo with child pictures
        }
        PictureViewer pictureViewer = new PictureViewer();
        pictureViewer.show( navigator, index );

    }

    /**
     * When the app sees a ShowPictureOnMapRequest it will start a webserver and
     * will spawn the Google maps with the teardrop on the indicated location.
     *
     * @param request
     */
    @Subscribe
    public void handleShowPictureOnMapRequest( ShowPictureOnMapRequest request ) {
        Webserver.getInstance().browse( request.getNode() );
    }

    /**
     * When the app sees a ShowPictureInfoEditorRequest it will open the
     * PictureInfoEditor for the supplied node
     *
     * @param request
     */
    @Subscribe
    public void handleShowPictureInfoEditorRequest( ShowPictureInfoEditorRequest request ) {
        new PictureInfoEditor( request.getNode() );
    }

    /**
     * When the app sees a ShowGroupInfoEditorRequest it will open the
     * PictureInfoEditor for the supplied node
     *
     * @param request
     */
    @Subscribe
    public void handleShowGroupInfoEditorRequest( ShowGroupInfoEditorRequest request ) {
        new GroupInfoEditor( request.getNode() );
    }

    /**
     * When the app sees a OpenSearchDialog it will open the QueryJFrame
     *
     * @param request
     */
    @Subscribe
    public void handleOpenSearchDialogRequest( OpenSearchDialogRequest request ) {
        new QueryJFrame( request.getStartNode() );
    }

    /**
     * This function opens the CategoryUsageEditor.
     *
     * @param node
     */
    /**
     * When the app sees a ShowCategoryUsageEditorRequest it will open the
     * CategoryUsageEditor for the supplied node
     *
     * @param request
     */
    @Subscribe
    public void handleShowGroupInfoEditorRequest( ShowCategoryUsageEditorRequest request ) {
        new CategoryUsageJFrame( request );
    }

    /**
     * Bring up a Dialog where the user can input a new name for a file and
     * rename it.
     *
     * @param request
     */
    @Subscribe
    public static void handleRenamePictureRequest( RenamePictureRequest request ) {
        SortableDefaultMutableTreeNode node = request.getNode();
        Object userObject = node.getUserObject();
        if ( !( userObject instanceof PictureInfo ) ) {
            return;
        }

        PictureInfo pi = (PictureInfo) userObject;
        File highresFile = pi.getHighresFile();
        if ( highresFile == null ) {
            return;
        }

        Object object = Settings.jpoResources.getString( "FileRenameLabel1" ) + highresFile.toString() + Settings.jpoResources.getString( "FileRenameLabel2" );
        String selectedValue = JOptionPane.showInputDialog(
                Settings.anchorFrame, // parent component
                object, // message
                highresFile.toString() );				// initialSelectionValue
        if ( selectedValue != null ) {
            File newName = new File( selectedValue );
            if ( highresFile.renameTo( newName ) ) {
                LOGGER.log( Level.INFO, "Sucessufully renamed: {0} to: {1}", new Object[]{ highresFile.toString(), selectedValue } );
                try {
                    pi.setHighresLocation( newName.toURI().toURL() );
                } catch ( MalformedURLException x ) {
                    LOGGER.log( Level.INFO, "Caught a MalformedURLException because of: {0}", x.getMessage() );
                }
            } else {
                LOGGER.log( Level.INFO, "Rename failed from : {0} to: {1}", new Object[]{ highresFile.toString(), selectedValue } );
            }
        }
    }

    /**
     * When the app sees a ChooseAndAddCollectionRequest it will open the a
     * chooser dialog and will add the collection to the supplied node
     *
     * @param request
     */
    @Subscribe
    public void handleChooseAndAddCollectionRequest( ChooseAndAddCollectionRequest request ) {
        File fileToLoad = Tools.chooseXmlFile();
        if ( fileToLoad != null ) {
            JpoEventBus.getInstance().post( new AddCollectionToGroupRequest( request.getNode(), fileToLoad ) );
        }

    }

    /**
     * When the app sees a ShowGroupAsTableRequest it will open the the group in
     * a table.
     *
     * @param request
     */
    @Subscribe
    public void handleShowGroupAsTableRequest( ShowGroupAsTableRequest request ) {
        TableJFrame tableJFrame = new TableJFrame( request.getNode() );
        tableJFrame.pack();
        tableJFrame.setVisible( true );
    }

    /**
     * Brings up a dialog where the user can select the collection to be loaded.
     * Calls {@link SortableDefaultMutableTreeNode#fileLoad} Remember to wrap
     * this request in an UnsavedUpdatesDialogRequest if you care about unsaved
     * changes as this request will not check for unsaved changes
     *
     * @param request the request
     */
    @Subscribe
    public void handleFileLoadRequest( FileLoadRequest request ) {
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
                JpoEventBus.getInstance().post( new ShowGroupRequest( Settings.pictureCollection.getRootNode() ) );
            }
        };
        t.start();
    }

    /**
     * Clears the collection and starts a new one. Remember to wrap this request
     * in an UnsavedUpdatesDialogRequest if you care about unsaved changes as
     * this request will not check for unsaved changes
     *
     * @param event the event
     */
    @Subscribe
    public void handleStartNewCollectionRequest( StartNewCollectionRequest event ) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                Settings.pictureCollection.clearCollection();
                JpoEventBus.getInstance().post( new ShowGroupRequest( Settings.pictureCollection.getRootNode() ) );
            }
        };
        SwingUtilities.invokeLater( r );
    }

    /**
     * Calls the {@link jpo.dataModel.PictureCollection#fileSave} method that
     * saves the current collection under it's present name and if it was never
     * saved before brings up a popup window.
     *
     * @param request The request
     */
    @Subscribe
    public void handleFileSaveRequest( FileSaveRequest request ) {
        if ( Settings.pictureCollection.getXmlFile() == null ) {
            FileSaveAsRequest fileSaveAsRequest = new FileSaveAsRequest();
            fileSaveAsRequest.setOnSuccessNextRequest( request.getOnSuccessNextRequest() );
            JpoEventBus.getInstance().post( fileSaveAsRequest );
        } else {
            LOGGER.info( String.format( "Saving under the name: %s", Settings.pictureCollection.getXmlFile() ) );
            Settings.pictureCollection.fileSave();
            afterFileSaveDialog();
            if ( request.getOnSuccessNextRequest() != null ) {
                JpoEventBus.getInstance().post( request.getOnSuccessNextRequest() );
            }
        }
    }

    /**
     * method that saves the entire index in XML format. It prompts for the
     * filename first.
     *
     * @param request the request
     */
    @Subscribe
    public void handleFileSaveAsRequest( FileSaveAsRequest request ) {
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
            boolean success = Settings.pictureCollection.fileSave();
            if ( !success ) {
                JOptionPane.showMessageDialog( Settings.anchorFrame,
                        "There was a problem saving the file.",
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
            }

            Settings.memorizeCopyLocation( chosenFile.getParent() );
            JpoEventBus.getInstance().post( new CopyLocationsChangedEvent() );
            Settings.pushRecentCollection( chosenFile.toString() );
            JpoEventBus.getInstance().post( new RecentCollectionsChangedEvent() );
            afterFileSaveDialog();
            if ( request.getOnSuccessNextRequest() != null ) {
                JpoEventBus.getInstance().post( request.getOnSuccessNextRequest() );
            }
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
     * Handles the request to add a collection supplied as a file to the
     * supplied group node
     *
     * @param request
     */
    @Subscribe
    public void handleAddCollectionToGroupRequest( AddCollectionToGroupRequest request ) {
        LOGGER.info( "Starting" );
        Tools.checkEDT();
        SortableDefaultMutableTreeNode popupNode = request.getNode();
        File fileToLoad = request.getCollectionFile();

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
        JpoEventBus.getInstance().post( new ShowGroupRequest( newNode ) );
    }

    /**
     * when the App sees this request it will sort the group by the criteria
     *
     * @param request The node on which the request was made
     */
    @Subscribe
    public void handleSortGroupRequest( SortGroupRequest request ) {
        SortableDefaultMutableTreeNode popupNode = request.getNode();
        FieldCodes sortCriteria = request.getSortCriteria();
        //logger.info( "Sort requested on " + myPopupNode.toString() + " for Criteria: " + Integer.toString( sortCriteria ) );
        popupNode.sortChildren( sortCriteria );
        JpoEventBus.getInstance().post( new RefreshThumbnailRequest( popupNode, ThumbnailQueueRequest.MEDIUM_PRIORITY ) );
    }

    /**
     * when the App sees an AddEmptyGroup request it will sort the group by the
     * criteria
     *
     * @param request The node on which the request was made
     */
    @Subscribe
    public void handleAddEmptyGroupRequest( AddEmptyGroupRequest request ) {
        SortableDefaultMutableTreeNode node = request.getNode();
        if ( !( node.getUserObject() instanceof GroupInfo ) ) {
            LOGGER.warning( String.format( "node %s is of type %s instead of GroupInfo. Proceeding anyway.", node.getUserObject().toString(), node.getUserObject().getClass().toString() ) );
        }
        SortableDefaultMutableTreeNode newNode = node.addGroupNode( "New Group" );
        Settings.memorizeGroupOfDropLocation( newNode );
        JpoEventBus.getInstance().post( new RecentDropNodesChangedEvent() );
        JpoEventBus.getInstance().post( new ShowGroupRequest( newNode ) );
    }

    /**
     * The App will respond to this request by opening the Export to HTML wizard
     *
     * @param request
     */
    @Subscribe
    public void handleExportGroupToHtmlRequest( ExportGroupToHtmlRequest request ) {
        SortableDefaultMutableTreeNode nodeToExport = request.getNode();
        new GenerateWebsiteWizard( nodeToExport );
    }

    /**
     * The App will respond to this request by creating a FlatFileDistiller
     *
     * @param request
     */
    @Subscribe
    public void handleExportGroupFlatFileRequest( ExportGroupToFlatFileRequest request ) {
        new FlatFileDistiller( request );
    }

    /**
     * Opens a dialog asking for the name of the new collection
     *
     *
     * @param request
     */
    @Subscribe
    public void handleExportGroupToNewCollectionRequest( ExportGroupToNewCollectionRequest request ) {
        SortableDefaultMutableTreeNode nodeToExport = request.getNode();
        new CollectionDistillerJFrame( nodeToExport );
    }

    /**
     * When the app receives the ExportGroupToPicasaRequest the dialog will be
     * opened to export the pictures to Picasa
     *
     * @param request
     */
    @Subscribe
    public void handleExportGroupToPicasaRequest( ExportGroupToPicasaRequest request ) {
        SortableDefaultMutableTreeNode groupNode = request.getNode();
        PicasaUploadRequest myRequest = new PicasaUploadRequest();
        myRequest.setNode( groupNode );
        new PicasaUploaderWizard( myRequest );
    }

    /**
     * Adds the pictures in the supplied group to the email selection
     *
     * @param request
     */
    @Subscribe
    public void handleAddGroupToEmailSelectionRequest( AddGroupToEmailSelectionRequest request ) {
        SortableDefaultMutableTreeNode groupNode = request.getNode();
        SortableDefaultMutableTreeNode n;
        for ( Enumeration e = groupNode.breadthFirstEnumeration(); e.hasMoreElements(); ) {
            n = (SortableDefaultMutableTreeNode) e.nextElement();
            if ( n.getUserObject() instanceof PictureInfo ) {
                Settings.pictureCollection.addToMailSelection( n );
            }
        }
    }

    /**
     * Opens the consolidate group dialog
     *
     * @param request The request
     */
    @Subscribe
    public void handleConsolidateGroupRequest( ConsolidateGroupRequest request ) {
        SortableDefaultMutableTreeNode node = request.getNode();
        if ( request.getTargetDir() == null ) {
            new ConsolidateGroupJFrame( node );
        } else {
            new ConsolidateGroupJFrame( node, request.getTargetDir() );
        }
    }

    /**
     * Moves the node to the first position in the group
     *
     * @param request The node on which the request was made
     */
    @Subscribe
    public void handleMoveNodeToTopRequest( MoveNodeToTopRequest request ) {
        SortableDefaultMutableTreeNode popupNode = request.getNode();
        popupNode.moveNodeToTop();
        JpoEventBus.getInstance().post( new RefreshThumbnailRequest( (SortableDefaultMutableTreeNode) popupNode.getParent(), ThumbnailQueueRequest.MEDIUM_PRIORITY ) );
    }

    /**
     * Moves the node up one position
     *
     * @param request
     */
    @Subscribe
    public void handleMoveNodeUpRequest( MoveNodeUpRequest request ) {
        SortableDefaultMutableTreeNode popupNode = request.getNode();
        popupNode.moveNodeUp();
        JpoEventBus.getInstance().post( new RefreshThumbnailRequest( (SortableDefaultMutableTreeNode) popupNode.getParent(), ThumbnailQueueRequest.MEDIUM_PRIORITY ) );
    }

    /**
     * Moves the node down one position
     *
     * @param request
     */
    @Subscribe
    public void handleMoveNodeDownRequest( MoveNodeDownRequest request ) {
        SortableDefaultMutableTreeNode popupNode = request.getNode();
        popupNode.moveNodeDown();
        JpoEventBus.getInstance().post( new RefreshThumbnailRequest( (SortableDefaultMutableTreeNode) popupNode.getParent(), ThumbnailQueueRequest.MEDIUM_PRIORITY ) );
    }

    /**
     * Moves the node to the last position
     *
     * @param request
     */
    @Subscribe
    public void handleMoveNodeToBottomRequest( MoveNodeToBottomRequest request ) {
        SortableDefaultMutableTreeNode popupNode = request.getNode();
        popupNode.moveNodeToBottom();
        JpoEventBus.getInstance().post( new RefreshThumbnailRequest( (SortableDefaultMutableTreeNode) popupNode.getParent(), ThumbnailQueueRequest.MEDIUM_PRIORITY ) );
    }

    /**
     * Removes the supplied node from it's parent
     *
     * @param request
     */
    @Subscribe
    public void handleRemoveNodeRequest( RemoveNodeRequest request ) {
        ArrayList<SortableDefaultMutableTreeNode> nodesToRemove = request.getNodes();
        SortableDefaultMutableTreeNode firstParentNode = (SortableDefaultMutableTreeNode) nodesToRemove.get( 0 ).getParent();
        for ( SortableDefaultMutableTreeNode deleteNode : nodesToRemove ) {
            deleteNode.deleteNode();
        }
        JpoEventBus.getInstance().post( new ShowGroupRequest( firstParentNode ) );
    }

    /**
     * Handles the request to open a recent collection
     *
     * Remember to wrap this request in an UnsavedUpdatesDialogRequest if you
     * care about unsaved changes as this request will not check for unsaved
     * changes
     *
     * @param request the request
     */
    @Subscribe
    public void handleOpenRecentCollectionRequest( OpenRecentCollectionRequest request ) {
        final int i = request.getIndex();

        Thread t = new Thread() {

            @Override
            public void run() {
                final File fileToLoad = new File( Settings.recentCollections[i] );
                try {
                    Settings.pictureCollection.fileLoad( fileToLoad );
                } catch ( FileNotFoundException ex ) {
                    Logger.getLogger( Jpo.class.getName() ).log( Level.SEVERE, null, ex );
                    LOGGER.log( Level.INFO, "FileNotFoundExecption: {0}", ex.getMessage() );
                    JOptionPane.showMessageDialog( Settings.anchorFrame,
                            ex.getMessage(),
                            Settings.jpoResources.getString( "genericError" ),
                            JOptionPane.ERROR_MESSAGE );
                    return;
                }
                JpoEventBus.getInstance().post( new ShowGroupRequest( Settings.pictureCollection.getRootNode() ) );

                Settings.pushRecentCollection( fileToLoad.toString() );
                JpoEventBus.getInstance().post( new RecentCollectionsChangedEvent() );
            }
        };
        t.start();
    }

    /**
     * Brings up a chooser to pick files and add them to the group.
     *
     * @param request the Request
     */
    @Subscribe
    public void handleChooseAndAddPicturesToGroupRequest( ChooseAndAddPicturesToGroupRequest request ) {
        new PictureFileChooser( request.getNode() );
    }

    /**
     * Brings up a chooser to pick a flat file and add them to the group.
     *
     * @param request the Request
     */
    @Subscribe
    public void handleChooseAndAddFlatfileRequest( ChooseAndAddFlatfileRequest request ) {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode( javax.swing.JFileChooser.FILES_ONLY );
        jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "fileOpenButtonText" ) );
        jFileChooser.setDialogTitle( Settings.jpoResources.getString( "addFlatFileTitle" ) );
        jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );

        int returnVal = jFileChooser.showOpenDialog( Settings.anchorFrame );
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File chosenFile = jFileChooser.getSelectedFile();
            JpoEventBus.getInstance().post( new AddFlatFileRequest( request.getNode(), chosenFile ) );

        }
    }

    /**
     * Handles the request to add a flat file to a node
     *
     * @param request the Request
     */
    @Subscribe
    public void handleAddFlatFileRequest( AddFlatFileRequest request ) {
        new FlatFileReader( request );
    }

    /**
     * Moves the movingNode into the last child position of the target node
     *
     * @param request
     */
    @Subscribe
    public void handleMoveNodeToNodeRequest( MoveNodeToNodeRequest request ) {
        List<SortableDefaultMutableTreeNode> movingNodes = request.getMovingNodes();
        SortableDefaultMutableTreeNode targetGroup = request.getTargetNode();
        for ( SortableDefaultMutableTreeNode movingNode : movingNodes ) {
            movingNode.moveToLastChild( targetGroup );
        }
    }

    /**
     * Opens the License window
     *
     * @param request
     */
    @Subscribe
    public void handleOpenLicenceFrameRequest( OpenLicenceFrameRequest request ) {
        new LicenseWindow();

    }

    /**
     * Opens the Help About window
     *
     * @param request
     */
    @Subscribe
    public void handleHelpAboutFrameRequest( OpenHelpAboutFrameRequest request ) {
        new HelpAboutWindow();
    }

    /**
     * Opens the Privacy window
     *
     * @param request
     */
    @Subscribe
    public void handleOpenPrivacyFrameRequest( OpenPrivacyFrameRequest request ) {
        new PrivacyJFrame();
    }

    /**
     * Starts the Camera Watch Daemon
     *
     * @param request
     */
    @Subscribe
    public void handleStartCameraWatchDaemonRequest( StartCameraWatchDaemonRequest request ) {
        new CameraWatchDaemon();
    }

    /**
     * Brings the unsaved updates dialog if there are unsaved updates and then
     * fires the next request. Logic is: if unsavedChanges then show dialog
     * submit next request
     *
     * The dialog has choices: 0 : discard unsaved changes and go to next
     * request 1 : fire save request then send next request 2 : fire save-as
     * request then send next request 3 : cancel - don't proceed with next
     * request
     *
     *
     * @param request
     */
    @Subscribe
    public void handleUnsavedUpdatesDialogRequest( UnsavedUpdatesDialogRequest request ) {
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
                    JpoEventBus.getInstance().post( request.getNextRequest() );
                    return;
                case 1:
                    FileSaveRequest fileSaveRequest = new FileSaveRequest();
                    fileSaveRequest.setOnSuccessNextRequest( request.getNextRequest() );
                    JpoEventBus.getInstance().post( fileSaveRequest );
                    return;
                case 2:
                    FileSaveAsRequest fileSaveAsRequest = new FileSaveAsRequest();
                    fileSaveAsRequest.setOnSuccessNextRequest( request.getNextRequest() );
                    JpoEventBus.getInstance().post( fileSaveAsRequest );
                    return;
                case 3:
                    return;
            }
        } else {
            JpoEventBus.getInstance().post( request.getNextRequest() );
        }

    }

    /**
     * Handles the RefreshThumbnailRequest
     *
     * @param request
     */
    @Subscribe
    public void handleRefreshThumbnailRequest( RefreshThumbnailRequest request ) {
        for ( SortableDefaultMutableTreeNode node : request.getNodes() ) {
            if ( node.isRoot() ) {
                LOGGER.fine( "Ingnoring the request for a thumbnail refresh on the Root Node as the query for it's parent's children will fail" );
                return;
            }
            LOGGER.fine( String.format( "refreshing the thumbnail on the node %s%nAbout to create the thubnail", this.toString() ) );
            ThumbnailController t = new ThumbnailController( Settings.thumbnailSize );
            t.setNode( new SingleNodeNavigator( node ), 0 );
            ThumbnailCreationQueue.requestThumbnailCreation( t,
                    request.getPriority(), true );
        }
    }

    /**
     * Handles the RotatePictureRequest request
     *
     * @param request
     */
    @Subscribe
    public void handleRotatePictureRequestRequest( RotatePictureRequest request ) {
        PictureInfo pictureInfo = (PictureInfo) request.getNode().getUserObject();
        pictureInfo.rotate( request.getAngle() );
        LOGGER.info( "Changed the rotation" );
        JpoEventBus.getInstance().post( new RefreshThumbnailRequest( request.getNode(), request.getPriority() ) );
        JpoEventBus.getInstance().post( new RefreshThumbnailRequest( (SortableDefaultMutableTreeNode) request.getNode().getParent(), request.getPriority() ) );
    }

    /**
     * Handles the ResetPictureRotationRequest request
     *
     * @param request
     */
    @Subscribe
    public void handleSetPictureRotationRequest( SetPictureRotationRequest request ) {
        PictureInfo pi = (PictureInfo) request.getNode().getUserObject();
        pi.setRotation( request.getAngle() );
        JpoEventBus.getInstance().post( new RefreshThumbnailRequest( request.getNode(), request.getPriority() ) );
        JpoEventBus.getInstance().post( new RefreshThumbnailRequest( (SortableDefaultMutableTreeNode) request.getNode().getParent(), request.getPriority() ) );
    }

    /**
     * Handles the OpenCategoryEditorRequest request
     *
     * @param request
     */
    @Subscribe
    public void handleOpenCategoryEditorRequest( OpenCategoryEditorRequest request ) {
        new CategoryEditorJFrame();
    }

}
