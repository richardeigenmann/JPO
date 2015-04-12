/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpo.gui;

import com.google.common.eventbus.Subscribe;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
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
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import jpo.EventBus.AddCollectionToGroupRequest;
import jpo.EventBus.AddEmptyGroupRequest;
import jpo.EventBus.AddFlatFileRequest;
import jpo.EventBus.AddGroupToEmailSelectionRequest;
import jpo.EventBus.AddPictureNodesToEmailSelectionRequest;
import jpo.EventBus.ApplicationStartupRequest;
import jpo.EventBus.CheckDirectoriesRequest;
import jpo.EventBus.CheckIntegrityRequest;
import jpo.EventBus.ChooseAndAddCollectionRequest;
import jpo.EventBus.ChooseAndAddFlatfileRequest;
import jpo.EventBus.ChooseAndAddPicturesToGroupRequest;
import jpo.EventBus.ClearEmailSelectionRequest;
import jpo.EventBus.CloseApplicationRequest;
import jpo.EventBus.ConsolidateGroupRequest;
import jpo.EventBus.CopyLocationsChangedEvent;
import jpo.EventBus.CopyToClipboardRequest;
import jpo.EventBus.CopyToDirRequest;
import jpo.EventBus.CopyToNewLocationRequest;
import jpo.EventBus.CopyToNewZipfileRequest;
import jpo.EventBus.CopyToZipfileRequest;
import jpo.EventBus.DeleteMultiNodeFileRequest;
import jpo.EventBus.DeleteNodeFileRequest;
import jpo.EventBus.EditCamerasRequest;
import jpo.EventBus.EditSettingsRequest;
import jpo.EventBus.ExportGroupToFlatFileRequest;
import jpo.EventBus.ExportGroupToHtmlRequest;
import jpo.EventBus.ExportGroupToNewCollectionRequest;
import jpo.EventBus.ExportGroupToPicasaRequest;
import jpo.EventBus.FileLoadDialogRequest;
import jpo.EventBus.FileLoadRequest;
import jpo.EventBus.FileSaveAsRequest;
import jpo.EventBus.FileSaveRequest;
import jpo.EventBus.FindDuplicatesRequest;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.MoveIndentRequest;
import jpo.EventBus.MoveNodeDownRequest;
import jpo.EventBus.MoveNodeToBottomRequest;
import jpo.EventBus.MoveNodeToNodeRequest;
import jpo.EventBus.MoveNodeToTopRequest;
import jpo.EventBus.MoveNodeUpRequest;
import jpo.EventBus.MoveOutdentRequest;
import jpo.EventBus.OpenCategoryEditorRequest;
import jpo.EventBus.OpenHelpAboutFrameRequest;
import jpo.EventBus.OpenLicenceFrameRequest;
import jpo.EventBus.OpenMainWindowRequest;
import jpo.EventBus.OpenPrivacyFrameRequest;
import jpo.EventBus.OpenRecentCollectionRequest;
import jpo.EventBus.OpenSearchDialogRequest;
import jpo.EventBus.RecentCollectionsChangedEvent;
import jpo.EventBus.RecentDropNodesChangedEvent;
import jpo.EventBus.RefreshThumbnailRequest;
import jpo.EventBus.RemoveNodeRequest;
import jpo.EventBus.RemovePictureNodesFromEmailSelectionRequest;
import jpo.EventBus.RenamePictureRequest;
import jpo.EventBus.RotatePictureRequest;
import jpo.EventBus.RunUserFunctionRequest;
import jpo.EventBus.SendEmailRequest;
import jpo.EventBus.SetPictureRotationRequest;
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
import jpo.cache.JpoCache;
import jpo.dataModel.DuplicatesQuery;
import jpo.dataModel.FlatFileReader;
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
import static jpo.dataModel.Tools.streamcopy;
import jpo.gui.swing.FlatFileDistiller;
import jpo.gui.swing.HelpAboutWindow;
import jpo.gui.swing.MainWindow;
import jpo.gui.swing.PrivacyJFrame;
import jpo.gui.swing.QueryJFrame;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_LEFT;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_RIGHT;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
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
     * Handles the application startup.
     *
     * @param request the startup request
     */
    @Subscribe
    public void handleApplicationStartupRequest( ApplicationStartupRequest request ) {
        LOGGER.info( "------------------------------------------------------------\n      Starting JPO" );

        // Check for EDT violations
        // RepaintManager.setCurrentManager( new CheckThreadViolationRepaintManager() );
        Settings.loadSettings();

        // JpoEventBus.getInstance().register( new DebugEventListener() );
        JpoEventBus.getInstance().post( new OpenMainWindowRequest() );

        JpoEventBus.getInstance().post( new StartCameraWatchDaemonRequest() );

        //final List<ThumbnailCreationFactory> THUMBNAIL_FACTORIES = new ArrayList<>();
        for ( int i = 1; i <= Settings.numberOfThumbnailCreationThreads; i++ ) {
            //THUMBNAIL_FACTORIES.add( new ThumbnailCreationFactory() );
            new ThumbnailCreationFactory();
        }

        if ( ( Settings.autoLoad != null ) && ( Settings.autoLoad.length() > 0 ) ) {
            File xmlFile = new File( Settings.autoLoad );
            JpoEventBus.getInstance().post( new FileLoadRequest( xmlFile ) );
        } else {
            JpoEventBus.getInstance().post( new StartNewCollectionRequest() );
        }

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
        Settings.getPictureCollection().addQueryToTreeModel( duplicatesQuery );
        new QueryNavigator( duplicatesQuery );
        JpoEventBus.getInstance().post( new ShowQueryRequest( duplicatesQuery ) );
    }

    /**
     * Creates an IntegrityChecker that does it's magic on the collection.
     *
     * @param request The request
     */
    @Subscribe
    public void handleCheckIntegrityRequest( CheckIntegrityRequest request ) {
        new IntegrityCheckerJFrame( Settings.getPictureCollection().getRootNode() );
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
     *
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
        new ReconcileJFrame( Settings.getPictureCollection().getRootNode() );
    }

    /**
     * Starts a double panel slideshow
     *
     * @param request The request
     */
    @Subscribe
    public void handleStartDoublePanelSlideshowRequest( StartDoublePanelSlideshowRequest request ) {
        Tools.checkEDT();
        SortableDefaultMutableTreeNode rootNode = request.getNode();
        PictureViewer p1 = new PictureViewer();
        p1.switchWindowMode( WINDOW_LEFT );
        PictureViewer p2 = new PictureViewer();
        p2.switchWindowMode( WINDOW_RIGHT );
        RandomNavigator rb1 = new RandomNavigator( rootNode.getChildPictureNodes( true ), String.format( "Randomised pictures from %s", rootNode.toString() ) );
        RandomNavigator rb2 = new RandomNavigator( rootNode.getChildPictureNodes( true ), String.format( "Randomised pictures from %s", rootNode.toString() ) );
        p1.showNode( rb1, 0 );
        p1.startAdvanceTimer( 10 );
        p2.showNode( rb2, 0 );
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
        pictureViewer.showNode( navigator, index );

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
     * When the app sees a ShowCategoryUsageEditorRequest it will open the
     * CategoryUsageEditor for the supplied node
     *
     * @param request
     */
    @Subscribe
    public void handleShowCategoryUsageEditorRequest( ShowCategoryUsageEditorRequest request ) {
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
                pi.setHighresLocation( newName );
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
     * Then fires a {@link FileLoadRequest}.
     * <p>
     * Enclose this request in an {@link UnsavedUpdatesDialogRequest} if you
     * care about unsaved changes as this request will not check for unsaved
     * changes
     *
     * @param request the request
     */
    @Subscribe
    public void handleFileLoadDialogRequest( FileLoadDialogRequest request ) {
        final File fileToLoad = Tools.chooseXmlFile();
        JpoEventBus.getInstance().post( new FileLoadRequest( fileToLoad ) );
    }

    /**
     * Loads the file by calling
     * {@link SortableDefaultMutableTreeNode#fileLoad}. If there is a problem
     * creates a new collection.
     * <p>
     * Remember to wrap this request in an UnsavedUpdatesDialogRequest if you
     * care about unsaved changes as this request will not check for unsaved
     * changes
     *
     * @param request the request
     */
    @Subscribe
    public void handleFileLoadRequest( FileLoadRequest request ) {
        final File fileToLoad = request.getFileToLoad();
        new Thread( "FileLoadRequest" ) {

            @Override
            public void run() {
                try {
                    Settings.getPictureCollection().fileLoad( fileToLoad );
                    JpoEventBus.getInstance().post( new ShowGroupRequest( Settings.getPictureCollection().getRootNode() ) );
                } catch ( final FileNotFoundException ex ) {
                    Runnable r = new Runnable() {

                        @Override
                        public void run() {
                            LOGGER.log( Level.INFO, "FileNotFoundExecption: {0}", ex.getMessage() );
                            JOptionPane.showMessageDialog( Settings.anchorFrame,
                                    ex.getMessage(),
                                    Settings.jpoResources.getString( "genericError" ),
                                    JOptionPane.ERROR_MESSAGE );
                            JpoEventBus.getInstance().post( new StartNewCollectionRequest() );
                        }
                    };
                    SwingUtilities.invokeLater( r );
                }
            }
        }.start();
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
        SwingUtilities.invokeLater( new Runnable() {

            @Override
            public void run() {
                Settings.getPictureCollection().clearCollection();
                JpoEventBus.getInstance().post( new ShowGroupRequest( Settings.getPictureCollection().getRootNode() ) );
            }
        } );
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
        if ( Settings.getPictureCollection().getXmlFile() == null ) {
            FileSaveAsRequest fileSaveAsRequest = new FileSaveAsRequest();
            fileSaveAsRequest.setOnSuccessNextRequest( request.getOnSuccessNextRequest() );
            JpoEventBus.getInstance().post( fileSaveAsRequest );
        } else {
            LOGGER.info( String.format( "Saving under the name: %s", Settings.getPictureCollection().getXmlFile() ) );
            Settings.getPictureCollection().fileSave();
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
        if ( Settings.getPictureCollection().getXmlFile() != null ) {
            jFileChooser.setCurrentDirectory( Settings.getPictureCollection().getXmlFile() );
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

            Settings.getPictureCollection().setXmlFile( chosenFile );
            Settings.getPictureCollection().fileSave();

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
        p.add( new JLabel( Settings.jpoResources.getString( "collectionSaveBody" ) + Settings.getPictureCollection().getXmlFile().toString() ) );
        JCheckBox setAutoload = new JCheckBox( Settings.jpoResources.getString( "setAutoload" ) );
        if ( ( !( Settings.autoLoad == null ) ) && ( ( new File( Settings.autoLoad ) ).compareTo( Settings.getPictureCollection().getXmlFile() ) == 0 ) ) {
            setAutoload.setSelected( true );
        }
        p.add( setAutoload );
        JOptionPane.showMessageDialog( Settings.anchorFrame,
                p,
                Settings.jpoResources.getString( "collectionSaveTitle" ),
                JOptionPane.INFORMATION_MESSAGE );
        if ( setAutoload.isSelected() ) {
            Settings.autoLoad = Settings.getPictureCollection().getXmlFile().toString();
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
                Settings.getPictureCollection().addToMailSelection( n );
            }
        }
    }

    /**
     * Adds the picture nodes in the supplied request to the email selection
     *
     * @param request
     */
    @Subscribe
    public void handleAddPictureModesToEmailSelectionRequest( AddPictureNodesToEmailSelectionRequest request ) {
        List<SortableDefaultMutableTreeNode> nodesList = request.getNodesList();
        for ( SortableDefaultMutableTreeNode n : nodesList ) {
            if ( n.getUserObject() instanceof PictureInfo ) {
                Settings.getPictureCollection().addToMailSelection( n );
            }
        }
    }

    /**
     * Removes the picture nodes in the supplied request from the email
     * selection
     *
     * @param request
     */
    @Subscribe
    public void handleRemovePictureModesFromEmailSelectionRequest( RemovePictureNodesFromEmailSelectionRequest request ) {
        List<SortableDefaultMutableTreeNode> nodesList = request.getNodesList();
        for ( SortableDefaultMutableTreeNode n : nodesList ) {
            if ( n.getUserObject() instanceof PictureInfo ) {
                Settings.getPictureCollection().removeFromMailSelection( n );
            }
        }
    }

    /**
     * Clears the the email selection
     *
     * @param request
     */
    @Subscribe
    public void handleClearEmailSelectionRequest( ClearEmailSelectionRequest request ) {
        Settings.getPictureCollection().clearMailSelection();
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
     * Brings up a JFileChooser to select the target location and then copies
     * the images to the target location
     *
     * @param request The request
     */
    @Subscribe
    public void handleCopyToNewLocationRequest( CopyToNewLocationRequest request ) {
        JFileChooser jFileChooser = new JFileChooser();

        jFileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "CopyImageDialogButton" ) );
        jFileChooser.setDialogTitle( Settings.jpoResources.getString( "CopyImageDialogTitle" ) );
        jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );

        int returnVal = jFileChooser.showSaveDialog( Settings.anchorFrame );
        if ( returnVal != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        JpoEventBus.getInstance().post( new CopyToDirRequest( request.getNodes(), jFileChooser.getSelectedFile() ) );
    }

    /**
     * Copies the pictures of the supplied nodes to the target directory
     *
     * @param request The request
     */
    @Subscribe
    public static void handleCopyToDirRequest( CopyToDirRequest request ) {
        if ( !request.getTargetLocation().canWrite() ) {
            JOptionPane.showMessageDialog( Settings.anchorFrame,
                    Settings.jpoResources.getString( "htmlDistCanWriteError" ),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        int picsCopied = 0;
        for ( SortableDefaultMutableTreeNode node : request.getNodes() ) {
            if ( node.getUserObject() instanceof PictureInfo ) {
                if ( node.validateAndCopyPicture( request.getTargetLocation() ) ) {
                    picsCopied++;
                }
            } else {
                LOGGER.info( String.format( "Skipping non PictureInfo node %s", node.toString() ) );
            }
        }
        JOptionPane.showMessageDialog( Settings.anchorFrame,
                String.format( Settings.jpoResources.getString( "copyToNewLocationSuccess" ), picsCopied, request.getNodes().length ),
                Settings.jpoResources.getString( "genericInfo" ),
                JOptionPane.INFORMATION_MESSAGE );

    }

    /**
     * Brings up a JFileChooser to select the target zip file and then copies
     * the images there
     *
     * TODO: Refactor to use a list
     *
     * @param request The request
     */
    @Subscribe
    public void handleCopyToNewZipfileRequest( CopyToNewZipfileRequest request ) {
        final JFileChooser jFileChooser = new JFileChooser();

        jFileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        // TODO: internationalise this Settings.jpoResources.getString( "CopyImageDialogButton" )
        jFileChooser.setApproveButtonText( "Select" );
        jFileChooser.setDialogTitle( "Pick the zipfile to which the pictures should be added" );
        jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );

        int returnVal = jFileChooser.showDialog( Settings.anchorFrame, "Select" );
        if ( returnVal != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        final File chosenFile = jFileChooser.getSelectedFile();
        Settings.memorizeZipFile( chosenFile.getPath() );
        //copyToZipfile( request.getNodes(), chosenFile );

        JpoEventBus.getInstance().post( new CopyToZipfileRequest( request.getNodes(), chosenFile ) );
    }

    /**
     * Copies the pictures of the supplied nodes to the target zipfile, creating
     * it if need be. This method does append to the zipfile by writing to a
     * temporary file and then copying the old zip file over to this one as the
     * API doesn't support directly appending to a zip file.
     *
     * @param request The request
     */
    @Subscribe
    public void handleCopyToZipfileRequest( CopyToZipfileRequest request ) {

        File tempfile = new File( request.getTargetZipfile().getAbsolutePath() + ".jpo.temp" );
        int picsCopied = 0;
        PictureInfo pictureInfo;
        File sourceFile;
        try ( ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream( tempfile ); ) {
            zipArchiveOutputStream.setLevel( 9 );
            for ( SortableDefaultMutableTreeNode node : request.getNodes() ) {
                if ( node.getUserObject() instanceof PictureInfo ) {
                    pictureInfo = (PictureInfo) node.getUserObject();
                    sourceFile = pictureInfo.getHighresFile();
                    LOGGER.info( String.format( "Processing file %s", sourceFile.toString() ) );

                    ZipArchiveEntry entry = new ZipArchiveEntry( sourceFile, sourceFile.getName() );
                    zipArchiveOutputStream.putArchiveEntry( entry );

                    try ( FileInputStream fis = new FileInputStream( sourceFile ) ) {
                        streamcopy( fis, zipArchiveOutputStream );
                    }
                    zipArchiveOutputStream.closeArchiveEntry();

                    picsCopied++;

                } else {
                    LOGGER.info( String.format( "Skipping non PictureInfo node %s", node.toString() ) );
                }
            }

            if ( request.getTargetZipfile().exists() ) {
                // copy the old entries over
                org.apache.commons.compress.archivers.zip.ZipFile oldzip = new org.apache.commons.compress.archivers.zip.ZipFile( request.getTargetZipfile() );
                Enumeration entries = oldzip.getEntries();
                while ( entries.hasMoreElements() ) {
                    ZipArchiveEntry e = (ZipArchiveEntry) entries.nextElement();
                    LOGGER.info( String.format( "streamcopy: %s", e.getName() ) );
                    zipArchiveOutputStream.putArchiveEntry( e );
                    if ( !e.isDirectory() ) {
                        streamcopy( oldzip.getInputStream( e ), zipArchiveOutputStream );
                    }
                    zipArchiveOutputStream.closeArchiveEntry();
                }
            }
            zipArchiveOutputStream.finish();
            zipArchiveOutputStream.close();
        } catch ( IOException ex ) {
            LOGGER.severe( ex.getMessage() );
            tempfile.delete();
        }

        if ( request.getTargetZipfile().exists() ) {
            LOGGER.info( String.format( "Deleting old file %s", request.getTargetZipfile().getAbsolutePath() ) );
            request.getTargetZipfile().delete();
        }
        LOGGER.info( String.format( "Renaming temp file %s to %s", tempfile.getAbsolutePath(), request.getTargetZipfile().getAbsolutePath() ) );
        tempfile.renameTo( request.getTargetZipfile() );

        JOptionPane.showMessageDialog( Settings.anchorFrame,
                String.format( "Copied %d files of %d to zipfile %s", picsCopied, request.getNodes().length, request.getTargetZipfile().toString() ),
                Settings.jpoResources.getString( "genericInfo" ),
                JOptionPane.INFORMATION_MESSAGE );

    }

    /**
     * Copies the supplied picture nodes to the system clipboard
     *
     * @param request The request
     */
    @Subscribe
    public void handleCopyToClipboardRequest( CopyToClipboardRequest request ) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        JpoTransferable transferable = new JpoTransferable(request.getNodes() );
        clipboard.setContents( transferable, new ClipboardOwner() {

            @Override
            public void lostOwnership( Clipboard clipboard, Transferable contents ) {
                LOGGER.info( "Lost Ownership of clipboard - not an issue");
            }
        } );
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
     * Indents the nodes
     *
     * @param request
     */
    @Subscribe
    public void handleMoveIndentRequest( MoveIndentRequest request ) {
        List<SortableDefaultMutableTreeNode> nodes = request.getNodes();
        for ( SortableDefaultMutableTreeNode node : nodes ) {
            node.indentNode();
        }
        // ToDo: Figure out what to refresh. New Group node for instance
        //JpoEventBus.getInstance().post( new RefreshThumbnailRequest( (SortableDefaultMutableTreeNode) popupNode.getParent(), ThumbnailQueueRequest.MEDIUM_PRIORITY ) );
    }

    /**
     * Outdents the nodes
     *
     * @param request
     */
    @Subscribe
    public void handleMoveOutdentRequest( MoveOutdentRequest request ) {
        List<SortableDefaultMutableTreeNode> nodes = request.getNodes();
        for ( SortableDefaultMutableTreeNode node : nodes ) {
            node.outdentNode();
        }
        // ToDo: Figure out what to refresh. New Group node for instance
        // ToDo: Could also delete the left over group node if it is empty
        //JpoEventBus.getInstance().post( new RefreshThumbnailRequest( (SortableDefaultMutableTreeNode) popupNode.getParent(), ThumbnailQueueRequest.MEDIUM_PRIORITY ) );
    }

    /**
     * Removes the supplied node from it's parent
     *
     * @param request
     */
    @Subscribe
    public void handleRemoveNodeRequest( RemoveNodeRequest request ) {
        List<SortableDefaultMutableTreeNode> nodesToRemove = request.getNodes();
        SortableDefaultMutableTreeNode firstParentNode = (SortableDefaultMutableTreeNode) nodesToRemove.get( 0 ).getParent();
        for ( SortableDefaultMutableTreeNode deleteNode : nodesToRemove ) {
            deleteNode.deleteNode();
        }
        JpoEventBus.getInstance().post( new ShowGroupRequest( firstParentNode ) );
    }

    /**
     * Deletes the file and the node
     *
     * @param request
     */
    @Subscribe
    public void handleDeleteNodeFileRequest( DeleteNodeFileRequest request ) {
        SortableDefaultMutableTreeNode node = request.getNode();
        Object userObj = node.getUserObject();
        if ( !( userObj instanceof PictureInfo ) ) {
            return;
        }

        PictureInfo pi = (PictureInfo) userObj;
        File highresFile = pi.getHighresFile();
        if ( highresFile == null ) {
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                Settings.anchorFrame,
                Settings.jpoResources.getString( "FileDeleteLabel" ) + highresFile.toString() + "\n" + Settings.jpoResources.getString( "areYouSure" ),
                Settings.jpoResources.getString( "FileDeleteTitle" ),
                JOptionPane.OK_CANCEL_OPTION );

        if ( option == 0 ) {
            boolean ok = false;

            if ( highresFile.exists() ) {
                ok = highresFile.delete();
                if ( !ok ) {
                    LOGGER.log( Level.INFO, "File deleted failed on: {0}", highresFile.toString() );
                }
            }

            node.deleteNode();

            if ( !ok ) {
                JOptionPane.showMessageDialog( Settings.anchorFrame,
                        Settings.jpoResources.getString( "fileDeleteError" ) + highresFile.toString(),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    /**
     * Deletes the file and the node
     *
     * @param request
     */
    @Subscribe
    public void handleDeleteMultiNodeFileRequest( DeleteMultiNodeFileRequest request ) {
        List<SortableDefaultMutableTreeNode> nodes = request.getNodes();
        JTextArea textArea = new JTextArea();
        textArea.setText( "" );
        for ( SortableDefaultMutableTreeNode selectedNode : nodes ) {
            if ( selectedNode.getUserObject() instanceof PictureInfo ) {
                textArea.append( ( (PictureInfo) selectedNode.getUserObject() ).getHighresLocation() + "\n" );
            }
        }
        textArea.append( Settings.jpoResources.getString( "areYouSure" ) );

        int option = JOptionPane.showConfirmDialog(
                Settings.anchorFrame, //very annoying if the main window is used as it forces itself into focus.
                textArea,
                Settings.jpoResources.getString( "FileDeleteLabel" ),
                JOptionPane.OK_CANCEL_OPTION );

        if ( option == 0 ) {
            for ( SortableDefaultMutableTreeNode selectedNode : nodes ) {
                PictureInfo pi;
                if ( selectedNode.getUserObject() instanceof PictureInfo ) {
                    pi = (PictureInfo) selectedNode.getUserObject();
                    boolean ok = false;

                    File highresFile = pi.getHighresFile();
                    if ( highresFile.exists() ) {
                        ok = highresFile.delete();
                        if ( !ok ) {
                            LOGGER.log( Level.INFO, "File deleted failed on: {0}", highresFile.toString() );
                        }
                    }

                    selectedNode.deleteNode();

                    if ( !ok ) {
                        JOptionPane.showMessageDialog( Settings.anchorFrame,
                                Settings.jpoResources.getString( "fileDeleteError" ) + highresFile.toString(),
                                Settings.jpoResources.getString( "genericError" ),
                                JOptionPane.ERROR_MESSAGE );
                    }
                }
            }
            Settings.getPictureCollection().clearSelection();
        }
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

        new Thread( "OpenRecentCollectionRequest" ) {

            @Override
            public void run() {
                final File fileToLoad = new File( Settings.recentCollections[i] );
                try {
                    Settings.getPictureCollection().fileLoad( fileToLoad );
                } catch ( FileNotFoundException ex ) {
                    Logger.getLogger( ApplicationEventHandler.class.getName() ).log( Level.SEVERE, null, ex );
                    LOGGER.log( Level.INFO, "FileNotFoundExecption: {0}", ex.getMessage() );
                    JOptionPane.showMessageDialog( Settings.anchorFrame,
                            ex.getMessage(),
                            Settings.jpoResources.getString( "genericError" ),
                            JOptionPane.ERROR_MESSAGE );
                    return;
                }
                JpoEventBus.getInstance().post( new ShowGroupRequest( Settings.getPictureCollection().getRootNode() ) );

                Settings.pushRecentCollection( fileToLoad.toString() );
                JpoEventBus.getInstance().post( new RecentCollectionsChangedEvent() );
            }
        }.start();
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
        if ( Settings.getPictureCollection().getUnsavedUpdates() ) {
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
     * Handles the SetPictureRotationRequest request by setting the rotation and
     * calling the refresh thumbnails methods
     *
     * @param request
     */
    @Subscribe
    public void handleSetPictureRotationRequest( SetPictureRotationRequest request ) {
        PictureInfo pictureInfo = (PictureInfo) request.getNode().getUserObject();
        pictureInfo.setRotation( request.getAngle() );
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

    /**
     * Handles the RunUserFunctionRequest request
     *
     * @param request
     */
    @Subscribe
    public void handleRunUserFunctionRequest( RunUserFunctionRequest request ) {
        try {
            Tools.runUserFunction( request.getUserFunctionIndex(), request.getPictureInfo() );
        } catch ( ClassCastException | NullPointerException x ) {
            LOGGER.severe( x.getMessage() );
        }

    }

}
