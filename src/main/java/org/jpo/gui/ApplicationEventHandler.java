package org.jpo.gui;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.TestOnly;
import org.jpo.cache.JpoCache;
import org.jpo.cache.QUEUE_PRIORITY;
import org.jpo.cache.ThumbnailCreationFactory;
import org.jpo.datamodel.*;
import org.jpo.datamodel.Settings.FieldCodes;
import org.jpo.eventbus.*;
import org.jpo.export.GenerateWebsiteWizard;
import org.jpo.export.PicasaUploadRequest;
import org.jpo.export.PicasaUploaderWizard;
import org.jpo.export.WebsiteGenerator;
import org.jpo.gui.swing.*;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_UNDECORATED_LEFT;
import static org.jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_UNDECORATED_RIGHT;

/*
 Copyright (C) 2014-2021  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * This class handles all the Application Events. It uses the Google Guava
 * EventBus
 *
 * @author Richard Eigenmann
 */
@SuppressWarnings("UnstableApiUsage")
public class ApplicationEventHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ApplicationEventHandler.class.getName());
    private static final String GENERIC_ERROR = Settings.getJpoResources().getString("genericError");
    public static final String GENERIC_INFO = Settings.getJpoResources().getString("genericInfo");

    /**
     * This class handles most of the events flying around the JPO application
     */
    public ApplicationEventHandler() {
        JpoEventBus.getInstance().register(this);
    }

    /**
     * @param request the request
     */
    @Subscribe
    public static void handleEvent(final MoveToNewLocationRequest request) {
        final var jFileChooser = new JFileChooser();

        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.setDialogTitle(Settings.getJpoResources().getString("MoveImageDialogTitle"));
        jFileChooser.setCurrentDirectory(Settings.getMostRecentCopyLocation());

        final int returnVal = jFileChooser.showDialog(Settings.getAnchorFrame(), Settings.getJpoResources().getString("MoveImageDialogButton"));
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        JpoEventBus.getInstance().post(new MoveToDirRequest(request.nodes(), jFileChooser.getSelectedFile()));
    }

    /**
     * Bring up a Dialog where the user can input a new name for a file and
     * rename it. If the target file already exists and would overwrite the existing file
     * A new name is suggested that the user can accept or abort the rename.
     *
     * @param request the request
     */
    @Subscribe
    public static void handleEvent(@NonNull final RenamePictureRequest request) {
        for (final SortableDefaultMutableTreeNode node : request.nodes()) {
            renameOnePictureRequest(node);
        }
    }

    public static void renameOnePictureRequest(@NonNull final SortableDefaultMutableTreeNode node) {
        final PictureInfo pi = (PictureInfo) node.getUserObject();

        final var imageFile = pi.getImageFile();
        if (imageFile == null) {
            return;
        }

        final Object object = Settings.getJpoResources().getString("FileRenameLabel1")
                + imageFile.toString()
                + Settings.getJpoResources().getString("FileRenameLabel2");
        final String selectedValue = JOptionPane.showInputDialog(Settings.getAnchorFrame(),
                object,
                imageFile.toString());
        if (selectedValue != null) {
            var newName = new File(selectedValue);

            if (newName.exists()) {
                final var alternativeNewName = Tools.inventFilename(newName.getParentFile(), newName.getName());
                int alternativeAnswer = JOptionPane.showConfirmDialog(Settings.getAnchorFrame(),
                        String.format(Settings.getJpoResources().getString("FileRenameTargetExistsText"), newName.toString(), alternativeNewName.toString()),
                        Settings.getJpoResources().getString("FileRenameTargetExistsTitle"),
                        JOptionPane.OK_CANCEL_OPTION);
                if (alternativeAnswer == JOptionPane.OK_OPTION) {
                    newName = alternativeNewName;
                } else {
                    LOGGER.log(Level.INFO, "File exists and new name was not accepted by user");
                    return;
                }
            }
            JpoEventBus.getInstance().post(new RenameFileRequest(node, newName.getName()));
        }
    }


    /**
     * Bring up a Dialog where the user can input a new name for a file and
     * rename it. If the target file already exists and would overwrite the existing file
     * A new name is suggested that the user can accept or abort the rename.
     *
     * @param request the request
     */
    @Subscribe
    public static void handleEvent(@NonNull final RenameFileRequest request) {
        final PictureInfo pi = (PictureInfo) request.node().getUserObject();
        LOGGER.log(Level.INFO, "Renaming node {0} ({1} to new filename: {2}", new Object[]{request.node(), pi.getImageFile().getPath(), request.newFileName()});
        final var imageFile = pi.getImageFile();
        final String newName = request.newFileName();
        final var newFile = new File(imageFile.getParentFile(), newName);
        if (imageFile.renameTo(newFile)) {
            LOGGER.log(Level.INFO, "Successfully renamed: {0} to: {1}", new Object[]{imageFile, newName});
            pi.setImageLocation(newFile);
            request.node().getPictureCollection().setUnsavedUpdates();
        } else {
            LOGGER.log(Level.INFO, "Rename failed from : {0} to: {1}", new Object[]{imageFile, newName});
        }

    }

    /**
     * Method that chooses an xml file or returns null
     *
     * @return the xml file or null
     */
    private static File chooseXmlFile() {
        final var jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setApproveButtonText(Settings.getJpoResources().getString("fileOpenButtonText"));
        jFileChooser.setDialogTitle(Settings.getJpoResources().getString("fileOpenHeading"));
        jFileChooser.setFileFilter(new XmlFilter());
        jFileChooser.setCurrentDirectory(Settings.getMostRecentCopyLocation());

        int returnVal = jFileChooser.showOpenDialog(Settings.getAnchorFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return jFileChooser.getSelectedFile();
        } else {
            return null;
        }
    }


    /**
     * Moves the pictures of the supplied nodes to the target directory
     *
     * @param request The request
     */
    @Subscribe
    public static void handleEvent(final MoveToDirRequest request) {
        if (!request.targetLocation().isDirectory()) {
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("htmlDistIsDirError"),
                    GENERIC_ERROR,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!request.targetLocation().canWrite()) {
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("htmlDistCanWriteError"),
                    GENERIC_ERROR,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        var picsMoved = 0;
        for (SortableDefaultMutableTreeNode node : request.nodes()) {
            if (node.getUserObject() instanceof PictureInfo pi) {
                if (ConsolidateGroupWorker.movePicture(pi, request.targetLocation())) {
                    picsMoved++;
                }
            } else {
                LOGGER.log(Level.INFO, "Node {0} is not a picture. Skipping the move for this node.", node);
            }
        }
        JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                String.format(Settings.getJpoResources().getString("moveToNewLocationSuccess"), picsMoved, request.nodes().size()),
                GENERIC_INFO,
                JOptionPane.INFORMATION_MESSAGE);
    }


    /**
     * This method fires up a user function if it can. User functions are only
     * valid on PictureInfo nodes.
     *
     * @param userFunction The user function to be executed in the array
     *                     Settings.userFunctionCmd
     * @param myObject     The PictureInfo upon which the user function should be
     *                     executed.
     */
    private static void runUserFunction(final int userFunction, final PictureInfo myObject) {
        if ((userFunction < 0) || (userFunction >= Settings.MAX_USER_FUNCTIONS)) {
            LOGGER.info("Error: called with an out of bounds index");
            return;
        }
        String command = Settings.getUserFunctionCmd()[userFunction];
        if ((command == null) || (command.length() == 0)) {
            LOGGER.log(Level.INFO, "Command {0} is not properly defined", Integer.toString(userFunction));
            return;
        }

        final var filename = (myObject).getImageFile().toString();
        command = command.replace("%f", filename);

        final String escapedFilename = filename.replaceAll("\\s", "\\\\\\\\ ");
        command = command.replace("%e", escapedFilename);

        try {
            final var pictureURL = myObject.getImageFile().toURI().toURL();
            command = command.replace("%u", pictureURL.toString());
        } catch (final MalformedURLException x) {
            LOGGER.log(Level.SEVERE, "Could not substitute %u with the URL: {0}", x.getMessage());
            return;
        }


        LOGGER.log(Level.INFO, "Command to run is: {0}", command);
        try {
            // Had big issues here because the simple exec (String) calls a StringTokenizer
            // which messes up the filename parameters
            int blank = command.indexOf(' ');
            if (blank > -1) {
                final var cmdarray = new String[2];
                cmdarray[0] = command.substring(0, blank);
                cmdarray[1] = command.substring(blank + 1);
                Runtime.getRuntime().exec(cmdarray);
            } else {
                final var cmdarray = new String[1];
                cmdarray[0] = command;
                Runtime.getRuntime().exec(cmdarray);
            }
        } catch (final IOException x) {
            LOGGER.log(Level.INFO, "Runtime.exec collapsed with and IOException: {0}", x.getMessage());
        }
    }

    /**
     * Handles the application startup by posting an {@link OpenMainWindowRequest},
     * starting the {@link StartCameraWatchDaemonRequest}, starting the
     * {@link StartThumbnailCreationFactoryRequest}. If an autoLoad is defined in the Settings it
     * will load that or start a new collection with {@link StartNewCollectionRequest}.
     *
     * @param request the startup request
     * @see OpenMainWindowRequest
     * @see ApplicationEventHandler#handleEvent(OpenMainWindowRequest)
     * @see StartCameraWatchDaemonRequest
     * @see ApplicationEventHandler#handleEvent(StartCameraWatchDaemonRequest)
     * @see StartThumbnailCreationFactoryRequest
     * @see ApplicationEventHandler#handleEvent(StartThumbnailCreationFactoryRequest)
     * @see FileLoadRequest
     * @see ApplicationEventHandler#handleEvent(FileLoadRequest)
     * @see StartNewCollectionRequest
     * @see ApplicationEventHandler#handleEvent(StartNewCollectionRequest)
     */
    @Subscribe
    public void handleEvent(final ApplicationStartupRequest request) {
        LOGGER.info("------------------------------------------------------------\n      Starting JPO");

        Settings.loadSettings();

        JpoEventBus.getInstance().post(new OpenMainWindowRequest());
        JpoEventBus.getInstance().post(new StartCameraWatchDaemonRequest());

        for (var i = 1; i <= Settings.NUMBER_OF_THUMBNAIL_CREATION_THREADS; i++) {
            JpoEventBus.getInstance().post(new StartThumbnailCreationFactoryRequest());
        }

        if ((Settings.getAutoLoad() != null) && (Settings.getAutoLoad().length() > 0)) {
            final var xmlFile = new File(Settings.getAutoLoad());
            JpoEventBus.getInstance().post(new FileLoadRequest(xmlFile));
        } else {
            JpoEventBus.getInstance().post(new StartNewCollectionRequest());
        }
        JpoEventBus.getInstance().post(new CheckForUpdatesRequest(false));
    }

    /**
     * Start a ThumbnailCreationFactory
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final StartThumbnailCreationFactoryRequest request) {
        new ThumbnailCreationFactory(Settings.THUMBNAIL_CREATION_THREAD_POLLING_TIME);
    }

    /**
     * Opens the MainWindow on the EDT thread by constructing a {@link MainWindow}. We then fire a
     * {@link LoadDockablesPositionsRequest}. We connect the picture collection with the {@link MainAppModelListener}
     *
     * @param request The request
     * @see MainWindow
     * @see LoadDockablesPositionsRequest
     * @see MainAppModelListener
     */
    @Subscribe
    public void handleEvent(final OpenMainWindowRequest request) {
        try {
            SwingUtilities.invokeAndWait(
                    () -> {
                        new MainWindow();
                        JpoEventBus.getInstance().post(new LoadDockablesPositionsRequest());
                        Settings.getPictureCollection().getTreeModel().addTreeModelListener(new MainAppModelListener());
                    }
            );
        } catch (final InterruptedException | InvocationTargetException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * The user wants to find duplicates
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final FindDuplicatesRequest request) {
        final var duplicatesQuery = new DuplicatesQuery();
        Settings.getPictureCollection().addQueryToTreeModel(duplicatesQuery);
        JpoEventBus.getInstance().post(new ShowQueryRequest(duplicatesQuery));
    }

    /**
     * Creates an IntegrityChecker that does it's magic on the collection.
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final CheckIntegrityRequest request) {
        new IntegrityCheckerJFrame(Settings.getPictureCollection().getRootNode());
    }

    /**
     * Creates a {@link SettingsDialog} where the user can edit Application wide
     * settings.
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final EditSettingsRequest request) {
        new SettingsDialog(true);
    }

    /**
     * Opens up the Camera Editor GUI. See {@link CamerasEditor}
     *
     * @param request the request object
     */
    @Subscribe
    public void handleEvent(final EditCamerasRequest request) {
        new CamerasEditor();
    }

    /**
     * Opens up the Camera Editor GUI. See {@link CamerasEditor}
     *
     * @param request the request object
     */
    @Subscribe
    public void handleEvent(final SendEmailRequest request) {
        new EmailerGui();
    }

    /**
     * Shuts down JPO no questions asked. Wrap it as a next request with a
     * UnsavedUpdatesDialogRequest
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final ShutdownApplicationRequest request) {
        if (Settings.isUnsavedSettingChanges()) {
            Settings.writeSettings();
        }

        JpoCache.shutdown();

        LOGGER.info("Exiting JPO\n------------------------------------------------------------");

        System.exit(0);
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
    public void handleEvent(final CheckDirectoriesRequest request) {
        new ReconcileJFrame(Settings.getPictureCollection().getRootNode());
    }

    /**
     * Starts a double panel slide show
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final StartDoublePanelSlideshowRequest request) {
        Tools.checkEDT();
        final SortableDefaultMutableTreeNode rootNode = request.node();
        final var p1 = new PictureViewer();
        p1.switchWindowMode(WINDOW_UNDECORATED_LEFT);
        final var p2 = new PictureViewer();
        p2.switchWindowMode(WINDOW_UNDECORATED_RIGHT);
        final var rb1 = new RandomNavigator(rootNode.getChildPictureNodes(true), String.format("Randomised pictures from %s", rootNode.toString()));
        final var rb2 = new RandomNavigator(rootNode.getChildPictureNodes(true), String.format("Randomised pictures from %s", rootNode.toString()));
        p1.showNode(rb1, 0);
        p1.startAdvanceTimer(10);
        p2.showNode(rb2, 0);
        p2.startAdvanceTimer(10);
    }

    /**
     * When we see a ShowPictureRequest this method will open a {@link PictureViewer}
     * and will tell it to show the {@link FlatGroupNavigator} based on the pictures
     * parent node starting at the current position
     *
     * @param request the {@link ShowPictureRequest}
     */
    @Subscribe
    public void handleEvent(final ShowPictureRequest request) {
        SwingUtilities.invokeLater(() -> {
            final var pictureViewer = new PictureViewer();
            pictureViewer.showNode(request.nodeNavigator(), request.currentIndex());
        });
    }


    /**
     * When the app sees a ShowPictureInfoEditorRequest it will open the
     * PictureInfoEditor for the supplied node
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final ShowPictureInfoEditorRequest request) {
        new PictureInfoEditor(request.node());
    }

    /**
     * When the app sees a ShowGroupInfoEditorRequest it will open the
     * PictureInfoEditor for the supplied node
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final ShowGroupInfoEditorRequest request) {
        new GroupInfoEditor(request.node());
    }

    /**
     * When the app sees a ShowCategoryUsageEditorRequest it will open the
     * CategoryUsageEditor for the supplied node
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final ShowCategoryUsageEditorRequest request) {
        JOptionPane.showMessageDialog(Settings.getAnchorFrame(), "This dialog was removed. Use AssignCategories instead");
    }

    /**
     * When the app sees a CategoryAssignmentWindowRequest it will open the
     * CategoryAssignmentWindowRequest for the supplied nodes
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final CategoryAssignmentWindowRequest request) {
        new CategroyAssignmentWindow(request);
    }

    /**
     * When the app sees a ShowAutoAdvanceDialog it needs to show the Auto Advance dialog
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final ShowAutoAdvanceDialogRequest request) {
        new AutoAdvanceDialog(request);
    }

    /**
     * When the app sees a ChooseAndAddCollectionRequest it will open the a
     * chooser dialog and will add the collection to the supplied node
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final ChooseAndAddCollectionRequest request) {
        final var fileToLoad = chooseXmlFile();
        if (fileToLoad != null) {
            JpoEventBus.getInstance().post(new AddCollectionToGroupRequest(request.node(), fileToLoad));
        }

    }

    /**
     * When the app sees a ShowGroupAsTableRequest it will open the the group in
     * a table.
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final ShowGroupAsTableRequest request) {
        final var tableJFrame = new TableJFrame(request.node());
        tableJFrame.pack();
        tableJFrame.setVisible(true);
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
    public void handleEvent(final FileLoadDialogRequest request) {
        final var fileToLoad = chooseXmlFile();
        if (fileToLoad != null) {
            JpoEventBus.getInstance().post(new FileLoadRequest(fileToLoad));
        }
    }

    /**
     * Loads the file by calling
     * {@link PictureCollection#fileLoad}. If there is a problem
     * creates a new collection.
     * <p>
     * Remember to wrap this request in an UnsavedUpdatesDialogRequest if you
     * care about unsaved changes as this request will not check for unsaved
     * changes
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final FileLoadRequest request) {
        final var fileToLoad = request.fileToLoad();
        new Thread("FileLoadRequest") {

            @Override
            public void run() {
                try {
                    Settings.getPictureCollection().fileLoad(fileToLoad);
                    Settings.pushRecentCollection(fileToLoad.toString());
                    JpoEventBus.getInstance().post(new RecentCollectionsChangedEvent());
                    JpoEventBus.getInstance().post(new ShowGroupRequest(Settings.getPictureCollection().getRootNode()));
                } catch (final FileNotFoundException ex) {

                    SwingUtilities.invokeLater(() -> {
                                LOGGER.log(Level.INFO, "FileNotFoundException: {0}", ex.getMessage());
                        JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                                ex.getMessage(),
                                GENERIC_ERROR,
                                JOptionPane.ERROR_MESSAGE);
                                JpoEventBus.getInstance().post(new StartNewCollectionRequest());
                            }
                    );
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
    public void handleEvent(final StartNewCollectionRequest event) {
        SwingUtilities.invokeLater(
                () -> {
                    Settings.getPictureCollection().clearCollection();
                    JpoEventBus.getInstance().post(new ShowGroupRequest(Settings.getPictureCollection().getRootNode()));
                }
        );
    }

    /**
     * Calls the {@link org.jpo.datamodel.PictureCollection#fileSave} method that
     * saves the current collection under it's present name and if it was never
     * saved before brings up a popup window.
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final FileSaveRequest request) {
        if (Settings.getPictureCollection().getXmlFile() == null) {
            final var fileSaveAsRequest = new FileSaveAsRequest(request.onSuccessNextRequest());
            JpoEventBus.getInstance().post(fileSaveAsRequest);
        } else {
            LOGGER.log(Level.INFO, "Saving under the name: {0}", Settings.getPictureCollection().getXmlFile());
            Settings.getPictureCollection().fileSave();
            JpoEventBus.getInstance().post(new AfterFileSaveRequest(Settings.getPictureCollection().getXmlFile().toString()));
            if (request.onSuccessNextRequest() != null) {
                JpoEventBus.getInstance().post(request.onSuccessNextRequest());
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
    public void handleEvent(final FileSaveAsRequest request) {
        final var jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        jFileChooser.setDialogTitle(Settings.getJpoResources().getString("fileSaveAsTitle"));
        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.setFileFilter(new XmlFilter());
        if (Settings.getPictureCollection().getXmlFile() != null) {
            jFileChooser.setCurrentDirectory(Settings.getPictureCollection().getXmlFile());
        } else {
            jFileChooser.setCurrentDirectory(Settings.getMostRecentCopyLocation());
        }

        final int returnVal = jFileChooser.showSaveDialog(Settings.getAnchorFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            var chosenFile = jFileChooser.getSelectedFile();
            chosenFile = Tools.correctFilenameExtension("xml", chosenFile);
            if (chosenFile.exists()) {
                int answer = JOptionPane.showConfirmDialog(Settings.getAnchorFrame(),
                        Settings.getJpoResources().getString("confirmSaveAs"),
                        Settings.getJpoResources().getString("genericWarning"),
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (answer == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }

            Settings.getPictureCollection().setXmlFile(chosenFile);
            Settings.getPictureCollection().fileSave();

            Settings.memorizeCopyLocation(chosenFile.getParent());
            JpoEventBus.getInstance().post(new CopyLocationsChangedEvent());
            Settings.pushRecentCollection(chosenFile.toString());
            JpoEventBus.getInstance().post(new RecentCollectionsChangedEvent());
            JpoEventBus.getInstance().post(new AfterFileSaveRequest(Settings.getPictureCollection().getXmlFile().toString()));
            if (request.onSuccessNextRequest() != null) {
                JpoEventBus.getInstance().post(request.onSuccessNextRequest());
            }
        }
    }

    /**
     * Brings up the dialog after a file save and allows the saved collection to
     * be set as the default start up collection.
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final AfterFileSaveRequest request) {
        final var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(Settings.getJpoResources().getString("collectionSaveBody") + Settings.getPictureCollection().getXmlFile().toString()));
        final var setAutoload = new JCheckBox(Settings.getJpoResources().getString("setAutoload"));
        if (Settings.getAutoLoad() != null && ((new File(Settings.getAutoLoad())).compareTo(Settings.getPictureCollection().getXmlFile()) == 0)) {
            setAutoload.setSelected(true);
        }
        panel.add(setAutoload);
        JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                panel,
                Settings.getJpoResources().getString("collectionSaveTitle"),
                JOptionPane.INFORMATION_MESSAGE);

        if (setAutoload.isSelected()) {
            Settings.setAutoLoad(request.autoLoadCollectionFile());
            Settings.writeSettings();
        }
    }

    /**
     * Handles the request to add a collection supplied as a file to the
     * supplied group node
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final AddCollectionToGroupRequest request) {
        LOGGER.info("Starting");
        Tools.checkEDT();
        final SortableDefaultMutableTreeNode popupNode = request.node();
        final var fileToLoad = request.collectionFile();

        final SortableDefaultMutableTreeNode newNode = popupNode.addGroupNode("New Group");
        try {
            PictureCollection.fileLoad(fileToLoad, newNode);
        } catch (final FileNotFoundException x) {
            LOGGER.severe(x.getMessage());
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    "File not found:\n" + fileToLoad.getPath(),
                    GENERIC_ERROR,
                    JOptionPane.ERROR_MESSAGE);
        }
        newNode.getPictureCollection().setUnsavedUpdates(true);
        JpoEventBus.getInstance().post(new ShowGroupRequest(newNode));
    }

    /**
     * when the App sees this request it will sort the group by the criteria
     *
     * @param request The node on which the request was made
     */
    @Subscribe
    public void handleEvent(final SortGroupRequest request) {
        final SortableDefaultMutableTreeNode popupNode = request.node();
        final FieldCodes sortCriteria = request.sortCriteria();
        popupNode.sortChildren(sortCriteria);
        final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
        nodes.add(popupNode);
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }

    /**
     * when the App sees an AddEmptyGroup request it will sort the group by the
     * criteria
     *
     * @param request The node on which the request was made
     */
    @Subscribe
    public void handleEvent(final AddEmptyGroupRequest request) {
        final SortableDefaultMutableTreeNode node = request.node();
        if (!(node.getUserObject() instanceof GroupInfo)) {
            LOGGER.log(Level.WARNING, "node {0} is of type {1} instead of GroupInfo. Proceeding anyway.", new Object[]{node.getUserObject(), node.getUserObject().getClass()});
        }
        final SortableDefaultMutableTreeNode newNode = node.addGroupNode("New Group");
        Settings.memorizeGroupOfDropLocation(newNode);
        JpoEventBus.getInstance().post(new RecentDropNodesChangedEvent());
        JpoEventBus.getInstance().post(new ShowGroupRequest(newNode));
    }

    /**
     * The App will respond to this request by opening the Export to HTML wizard
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final ExportGroupToHtmlRequest request) {
        new GenerateWebsiteWizard(request.node());
    }

    /**
     * The Creates the Website
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final GenerateWebsiteRequest request) {
        WebsiteGenerator.generateWebsite(request);
    }

    /**
     * The App will respond to this request by creating a FlatFileDistiller
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final ExportGroupToFlatFileRequest request) {
        new FlatFileDistiller(request);
    }

    /**
     * Opens a dialog asking for the name of the new collection
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final ExportGroupToNewCollectionRequest request) {
        new CollectionDistillerJFrame(request);
    }

    /**
     * Fulfill the export to new collection request
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final ExportGroupToCollectionRequest request) {
        JpoWriter.writeInThread(request);
    }

    /**
     * When the app receives the ExportGroupToPicasaRequest the dialog will be
     * opened to export the pictures to Picasa
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final ExportGroupToPicasaRequest request) {
        final var myRequest = new PicasaUploadRequest();
        myRequest.setNode(request.node());
        new PicasaUploaderWizard(myRequest);
    }

    /**
     * Adds the pictures in the supplied group to the email selection
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final AddGroupToEmailSelectionRequest request) {
        final SortableDefaultMutableTreeNode groupNode = request.node();
        SortableDefaultMutableTreeNode n;
        for (final Enumeration<TreeNode> e = groupNode.breadthFirstEnumeration(); e.hasMoreElements(); ) {
            n = (SortableDefaultMutableTreeNode) e.nextElement();
            if (n.getUserObject() instanceof PictureInfo) {
                Settings.getPictureCollection().addToMailSelection(n);
            }
        }
    }

    /**
     * Adds the picture nodes in the supplied request to the email selection
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final AddPictureNodesToEmailSelectionRequest request) {
        final List<SortableDefaultMutableTreeNode> nodesList = request.nodesList();
        for (final SortableDefaultMutableTreeNode n : nodesList) {
            if (n.getUserObject() instanceof PictureInfo) {
                Settings.getPictureCollection().addToMailSelection(n);
            }
        }
    }

    /**
     * Removes the picture nodes in the supplied request from the email
     * selection
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final RemovePictureNodesFromEmailSelectionRequest request) {
        final List<SortableDefaultMutableTreeNode> nodesList = request.nodesList();
        for (final SortableDefaultMutableTreeNode n : nodesList) {
            if (n.getUserObject() instanceof PictureInfo) {
                Settings.getPictureCollection().removeFromMailSelection(n);
            }
        }
    }

    /**
     * Clears the the email selection
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final ClearEmailSelectionRequest request) {
        Settings.getPictureCollection().clearMailSelection();
    }

    /**
     * Opens the consolidate group dialog
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final ConsolidateGroupDialogRequest request) {
        new ConsolidateGroupController(request);
    }

    /**
     * Consolidates the files
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final ConsolidateGroupRequest request) {
        new ConsolidateGroupWorker(
                request.targetDir(),
                request.node(),
                request.recurseSubgroups(),
                new ProgressGui(NodeStatistics.countPictures(request.node(), request.recurseSubgroups()),
                        Settings.getJpoResources().getString("ConsolidateProgBarTitle"),
                        ""));
    }

    /**
     * Brings up a JFileChooser to select the target location and then copies
     * the images to the target location
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final CopyToNewLocationRequest request) {
        final var jFileChooser = new JFileChooser();

        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.setApproveButtonText(Settings.getJpoResources().getString("CopyImageDialogButton"));
        jFileChooser.setDialogTitle(Settings.getJpoResources().getString("CopyImageDialogTitle"));
        jFileChooser.setCurrentDirectory(Settings.getMostRecentCopyLocation());

        final int returnVal = jFileChooser.showSaveDialog(Settings.getAnchorFrame());
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        final var targetDirectory = jFileChooser.getSelectedFile();
        Settings.memorizeCopyLocation(targetDirectory.toString());
        JpoEventBus.getInstance().post(new CopyLocationsChangedEvent());

        if (!targetDirectory.canWrite()) {
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("htmlDistCanWriteError"),
                    GENERIC_ERROR,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JpoEventBus.getInstance().post(new CopyToDirRequest(request.nodes(), targetDirectory));
    }

    /**
     * Copies the pictures of the supplied nodes to the target directory
     *
     * @param request The request
     */
    @Subscribe
    public static void handleEvent(final CopyToDirRequest request) {

        var picsCopied = 0;
        for (final SortableDefaultMutableTreeNode node : request.nodes()) {
            if (node.getUserObject() instanceof PictureInfo pictureInfo) {
                if (copyPicture(pictureInfo, request.targetDirectory())) {
                    picsCopied++;
                }
            } else {
                LOGGER.log(Level.INFO, "Node {0} is not a picture. Can''t copy other nodes to a directroy.", node);
            }
        }
        JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                String.format(Settings.getJpoResources().getString("copyToNewLocationSuccess"), picsCopied, request.nodes().size()),
                GENERIC_INFO,
                JOptionPane.INFORMATION_MESSAGE);

    }

    /**
     * Validates the target of the picture copy instruction and tries to find
     * the appropriate thing to do. It does the following steps:<br>
     * 1: If the target is an existing file then it invents a new filename<br>
     * 2: If the target is a directory the filename of the original is used.<br>
     * 3: If the target directory doesn't exist then the directories are
     * created.<br>
     * 4: The file extension is made to be that of the original if it isn't
     * already that.<br>
     * When all preconditions are met the image is copied
     *
     * @param targetDirectory The target location for the new Picture.
     * @return true if successful, false if not
     */
    private static boolean copyPicture(@NonNull final PictureInfo pictureInfo, @NonNull File targetDirectory) {
        Objects.requireNonNull(targetDirectory, "targetDirectory must not be null");

        final var originalFile = pictureInfo.getImageFile();
        final var targetFile = Tools.inventFilename(targetDirectory, originalFile.getName());
        try {
            FileUtils.copyFile(originalFile, targetFile);
        } catch (final IOException e) {
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    "IOException: " + e.getMessage(),
                    Settings.getJpoResources().getString("genericError"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }


    /**
     * Brings up a JFileChooser to select the target zip file and then copies
     * the images there
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final CopyToNewZipfileRequest request) {
        final var jFileChooser = new JFileChooser();

        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setApproveButtonText(Settings.getJpoResources().getString("genericSelectText"));
        jFileChooser.setDialogTitle("Pick the zipfile to which the pictures should be added");
        jFileChooser.setCurrentDirectory(Settings.getMostRecentCopyLocation());

        final int returnVal = jFileChooser.showDialog(Settings.getAnchorFrame(), "Select");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        final var chosenFile = jFileChooser.getSelectedFile();
        Settings.memorizeZipFile(chosenFile.getPath());

        JpoEventBus.getInstance().post(new CopyToZipfileRequest(request.nodes(), chosenFile));
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
    public void handleEvent(final CopyToZipfileRequest request) {
        final var tempFile = new File(request.targetZipfile().getAbsolutePath() + ".org.jpo.temp");
        var picsCopied = 0;
        try (final var zipArchiveOutputStream = new ZipArchiveOutputStream(tempFile)) {
            zipArchiveOutputStream.setLevel(9);
            picsCopied += addPicturesToZip(zipArchiveOutputStream, request.nodes());

            if (request.targetZipfile().exists()) {
                // copy the old entries over
                try (
                        final var oldZipFile = new ZipFile(request.targetZipfile())) {
                    final Enumeration<ZipArchiveEntry> entries = oldZipFile.getEntries();
                    while (entries.hasMoreElements()) {
                        final ZipArchiveEntry e = entries.nextElement();
                        LOGGER.log(Level.INFO, "streamCopy: {0}", e.getName());
                        zipArchiveOutputStream.putArchiveEntry(e);
                        if (!e.isDirectory()) {
                            oldZipFile.getInputStream(e).transferTo(zipArchiveOutputStream);
                        }
                        zipArchiveOutputStream.closeArchiveEntry();
                    }
                }
            }
            zipArchiveOutputStream.finish();
        } catch (final IOException ex) {
            try {
                Files.delete(tempFile.toPath());
            } catch (final IOException e) {
                LOGGER.log(Level.SEVERE, "Could not delete tempFile: {0} Exception: {1}", new Object[]{tempFile, e.getMessage()});
            }
        }

        if (request.targetZipfile().exists()) {
            LOGGER.log(Level.INFO, "Deleting old file {0}", request.targetZipfile().getAbsolutePath());
            try {
                Files.delete(request.targetZipfile().toPath());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to delete file {0}, Exception: {1}", new Object[]{request.targetZipfile().getAbsolutePath(), e.getMessage()});
            }
        }
        LOGGER.log(Level.INFO, "Renaming temp file {0} to {1}", new Object[]{tempFile.getAbsolutePath(), request.targetZipfile().getAbsolutePath()});
        boolean ok = tempFile.renameTo(request.targetZipfile());
        if (!ok) {
            LOGGER.log(Level.SEVERE, "Failed to rename temp file {0} to {1}", new Object[]{tempFile.getAbsolutePath(), request.targetZipfile().getAbsolutePath()});
        }

        JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                String.format("Copied %d files of %d to zipfile %s", picsCopied, request.nodes().size(), request.targetZipfile()),
                GENERIC_INFO,
                JOptionPane.INFORMATION_MESSAGE);

    }


    private int addPicturesToZip(
            final ZipArchiveOutputStream zipArchiveOutputStream,
            final Collection<SortableDefaultMutableTreeNode> nodes)
            throws IOException {
        var picsCopied = 0;
        for (final SortableDefaultMutableTreeNode node : nodes) {
            if (node.getUserObject() instanceof PictureInfo pi) {
                final var sourceFile = pi.getImageFile();
                LOGGER.log(Level.INFO, "Processing file {0}", sourceFile);

                final var entry = new ZipArchiveEntry(sourceFile, sourceFile.getName());
                zipArchiveOutputStream.putArchiveEntry(entry);

                try (final var fis = new FileInputStream(sourceFile)) {
                    fis.transferTo(zipArchiveOutputStream);
                }
                zipArchiveOutputStream.closeArchiveEntry();

                picsCopied++;

            } else {
                LOGGER.log(Level.INFO, "Skipping non PictureInfo node {0}", node);
            }
        }
        return picsCopied;
    }

    /**
     * Copies the supplied picture nodes to the system clipboard
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final CopyImageToClipboardRequest request) {
        final var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final var transferable = new JpoTransferable(request.nodes());
        clipboard.setContents(transferable, (Clipboard clipboard1, Transferable contents) -> LOGGER.info("Lost Ownership of clipboard - not an issue"));
    }

    /**
     * Copies the path(s) of the supplied picture node(s) to the system clipboard
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final CopyPathToClipboardRequest request) {
        final var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final var sb = new StringBuilder();
        for (final SortableDefaultMutableTreeNode s : request.nodes()) {
            if (s.getUserObject() instanceof PictureInfo pi) {
                sb.append(pi.getImageFile().getAbsoluteFile().toString());
                sb.append(System.lineSeparator());
            }
        }
        final var stringSelection = new StringSelection(sb.toString());
        clipboard.setContents(stringSelection, (Clipboard clipboard1, Transferable contents) -> LOGGER.info("Lost Ownership of clipboard - not an issue"));
    }

    /**
     * Moves the node to the first position in the group
     *
     * @param request The node on which the request was made
     */
    @Subscribe
    public void handleEvent(final MoveNodeToTopRequest request) {
        final SortableDefaultMutableTreeNode popupNode = request.node();
        popupNode.moveNodeToTop();
        final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
        nodes.add(popupNode.getParent());
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }

    /**
     * Moves the node up one position
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveNodeUpRequest request) {
        final SortableDefaultMutableTreeNode popupNode = request.node();
        popupNode.moveNodeUp();
        final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
        nodes.add(popupNode.getParent());
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }

    /**
     * Moves the node down one position
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveNodeDownRequest request) {
        final SortableDefaultMutableTreeNode popupNode = request.node();
        popupNode.moveNodeDown();
        final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
        nodes.add(popupNode.getParent());
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }

    /**
     * Moves the node to the last position
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveNodeToBottomRequest request) {
        final SortableDefaultMutableTreeNode popupNode = request.node();
        popupNode.moveNodeToBottom();
        final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
        nodes.add(popupNode.getParent());
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }

    /**
     * Indents the nodes
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveIndentRequest request) {
        final List<SortableDefaultMutableTreeNode> nodes = request.nodes();
        for (SortableDefaultMutableTreeNode node : nodes) {
            node.indentNode();
        }
    }

    /**
     * Outdents the nodes
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveOutdentRequest request) {
        final List<SortableDefaultMutableTreeNode> nodes = request.nodes();
        for (SortableDefaultMutableTreeNode node : nodes) {
            node.outdentNode();
        }
    }

    /**
     * Removes the supplied node from it's parent
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final RemoveNodeRequest request) {
        final List<SortableDefaultMutableTreeNode> nodesToRemove = request.nodes();
        final SortableDefaultMutableTreeNode firstParentNode = nodesToRemove.get(0).getParent();
        for (SortableDefaultMutableTreeNode deleteNode : nodesToRemove) {
            deleteNode.deleteNode();
        }
        JpoEventBus.getInstance().post(new ShowGroupRequest(firstParentNode));
    }

    /**
     * Deletes the file and the node
     *
     * @param request the request the request
     */
    @Subscribe
    public void handleEvent(final DeleteNodeFileRequest request) {
        try {
            if (!(request.node().getUserObject() instanceof PictureInfo pi)) {
                return;
            } else {
                if (pi.getImageFile() == null) {
                    return;
                }
            }
        } catch (final NullPointerException ex) {
            return;
        }

        final var highresFile = ((PictureInfo) request.node().getUserObject()).getImageFile();
        final int option = JOptionPane.showConfirmDialog(
                Settings.getAnchorFrame(),
                Settings.getJpoResources().getString("FileDeleteLabel") + highresFile + "\n" + Settings.getJpoResources().getString("areYouSure"),
                Settings.getJpoResources().getString("FileDeleteTitle"),
                JOptionPane.OK_CANCEL_OPTION);

        if (option == 0 && highresFile.exists()) {
            try {
                deleteNodeAndFile(request.node());
            } catch (IOException e) {
                LOGGER.log(Level.INFO, "File deleted failed on file {0}: {1}", new Object[]{highresFile, e.getMessage()});
                JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                        Settings.getJpoResources().getString("fileDeleteError") + highresFile + e.getMessage(),
                        GENERIC_ERROR,
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Deletes the file and the node
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final DeleteMultiNodeFileRequest request) {
        final var textArea = new JTextArea();
        textArea.setText(getFilenames(request.nodes()));
        textArea.append(Settings.getJpoResources().getString("areYouSure"));

        final int option = JOptionPane.showConfirmDialog(
                Settings.getAnchorFrame(), //very annoying if the main window is used as it forces itself into focus.
                textArea,
                Settings.getJpoResources().getString("FileDeleteLabel"),
                JOptionPane.OK_CANCEL_OPTION);

        if (option == 0) {
            for (final SortableDefaultMutableTreeNode selectedNode : request.nodes()) {
                        try {
                            deleteNodeAndFile(selectedNode);
                        } catch (final IOException e) {
                            LOGGER.log(Level.INFO, "File deleted failed on: {0} Exception: {1}", new Object[]{selectedNode, e.getMessage()});
                            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                                    Settings.getJpoResources().getString("fileDeleteError") + selectedNode.toString(),
                                    GENERIC_ERROR,
                                    JOptionPane.ERROR_MESSAGE);
                        }
            }
            Settings.getPictureCollection().clearSelection();
        }
    }

    @TestOnly
    public static void deleteNodeAndFileTest(final SortableDefaultMutableTreeNode node) throws IOException {
        deleteNodeAndFile(node);
    }

    private static void deleteNodeAndFile(final SortableDefaultMutableTreeNode node) throws IOException {
        if (node.getUserObject() instanceof PictureInfo pi) {
            final var highresFile = pi.getImageFile();
            if (highresFile.exists()) {
                Files.delete(highresFile.toPath());
            }
        }
        node.deleteNode();
    }

    private String getFilenames(final Collection<SortableDefaultMutableTreeNode> nodes) {
        final var sb = new StringBuilder();
        for (final SortableDefaultMutableTreeNode selectedNode : nodes) {
            if (selectedNode.getUserObject() instanceof PictureInfo) {
                sb.append(((PictureInfo) selectedNode.getUserObject()).getImageLocation() + "\n");
            }
        }
        return sb.toString();
    }

    /**
     * Handles the request to open a recent collection
     * <p>
     * Remember to wrap this request in an UnsavedUpdatesDialogRequest if you
     * care about unsaved changes as this request will not check for unsaved
     * changes
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final OpenRecentCollectionRequest request) {
        final int i = request.index();

        new Thread("OpenRecentCollectionRequest") {

            @Override
            public void run() {
                final var fileToLoad = new File(Settings.getRecentCollections()[i]);
                try {
                    Settings.getPictureCollection().fileLoad(fileToLoad);
                } catch (final FileNotFoundException ex) {
                    Logger.getLogger(ApplicationEventHandler.class.getName()).log(Level.SEVERE, null, ex);
                    LOGGER.log(Level.INFO, "FileNotFoundException: {0}", ex.getMessage());
                    JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                            ex.getMessage(),
                            GENERIC_ERROR,
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JpoEventBus.getInstance().post(new ShowGroupRequest(Settings.getPictureCollection().getRootNode()));

                Settings.pushRecentCollection(fileToLoad.toString());
                JpoEventBus.getInstance().post(new RecentCollectionsChangedEvent());
            }
        }.start();
    }

    /**
     * Brings up a chooser to pick files and add them to the group.
     *
     * @param request the Request
     */
    @Subscribe
    public void handleEvent(final ChooseAndAddPicturesToGroupRequest request) {
        new PictureFileChooser(request);
    }

    /**
     * Handes the request to zoom in on the PictureController
     *
     * @param request the Request
     */
    @Subscribe
    public void handleEvent(final PictureControllerZoomRequest request) {
        request.pictureController().handleZoomRequest(request);
    }

    /**
     * Brings up a chooser to pick a flat file and add them to the group.
     *
     * @param request the Request
     */
    @Subscribe
    public void handleEvent(final ChooseAndAddFlatfileRequest request) {
        final var jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        jFileChooser.setApproveButtonText(Settings.getJpoResources().getString("fileOpenButtonText"));
        jFileChooser.setDialogTitle(Settings.getJpoResources().getString("addFlatFileTitle"));
        jFileChooser.setCurrentDirectory(Settings.getMostRecentCopyLocation());

        final int returnVal = jFileChooser.showOpenDialog(Settings.getAnchorFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final var chosenFile = jFileChooser.getSelectedFile();
            JpoEventBus.getInstance().post(new AddFlatFileRequest(request.node(), chosenFile));

        }
    }

    /**
     * Handles the request to add a flat file to a node
     *
     * @param request the Request
     */
    @Subscribe
    public void handleEvent(final AddFlatFileRequest request) {
        FlatFileReader.handleRequest(request);
    }

    /**
     * Moves the movingNode into the last child position of the target node
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveNodeToNodeRequest request) {
        final List<SortableDefaultMutableTreeNode> movingNodes = request.movingNodes();
        final SortableDefaultMutableTreeNode targetGroup = request.targetNode();
        for (final SortableDefaultMutableTreeNode movingNode : movingNodes) {
            movingNode.moveToLastChild(targetGroup);
        }
    }

    /**
     * Opens the License window
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final OpenLicenceFrameRequest request) {
        new LicenseWindow();
    }

    /**
     * Opens the Help About window
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final OpenHelpAboutFrameRequest request) {
        JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                Settings.getJpoResources().getString("HelpAboutText") + Settings.getJpoResources().getString("HelpAboutUser") + System.getProperty("user.name") + "\n" + Settings.getJpoResources().getString("HelpAboutOs") + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch") + "\n" + Settings.getJpoResources().getString("HelpAboutJvm") + System.getProperty("java.vendor") + " " + System.getProperty("java.version") + "\n" + Settings.getJpoResources().getString("HelpAboutJvmMemory") + Long.toString(Runtime.getRuntime().maxMemory() / 1024 / 1024, 0) + " MB\n" + Settings.getJpoResources().getString("HelpAboutJvmFreeMemory") + Long.toString(Runtime.getRuntime().freeMemory() / 1024 / 1024, 0) + " MB\n");

        // while we're at it dump the stuff to the log
        LOGGER.info("HelpAboutWindow: Help About showed the following information");
        LOGGER.log(Level.INFO, "User: {0}", System.getProperty("user.name"));
        LOGGER.log(Level.INFO, "Operating System: {0}  {1}", new Object[]{System.getProperty("os.name"), System.getProperty("os.version")});
        LOGGER.log(Level.INFO, "Java: {0}", System.getProperty("java.version"));
        LOGGER.log(Level.INFO, "Max Memory: {0} MB", Long.toString(Runtime.getRuntime().maxMemory() / 1024 / 1024, 0));
        LOGGER.log(Level.INFO, "Free Memory: {0} MB", Long.toString(Runtime.getRuntime().freeMemory() / 1024 / 1024, 0));
    }

    /**
     * Opens the Privacy window
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final OpenPrivacyFrameRequest request) {
        new PrivacyJFrame();
    }

    /**
     * Starts the Camera Watch Daemon
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final StartCameraWatchDaemonRequest request) {
        new CameraWatchDaemon();
    }

    /**
     * Brings the unsaved updates dialog if there are unsaved updates and then
     * fires the next request. Logic is: if unsavedChanges then show dialog
     * submit next request
     * <p>
     * The dialog has choices: 0 : discard unsaved changes and go to next
     * request 1 : fire save request then send next request 2 : fire save-as
     * request then send next request 3 : cancel - don't proceed with next
     * request
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final UnsavedUpdatesDialogRequest request) {
        Tools.checkEDT();

        // a good time to save the window coordinates
        LOGGER.log(Level.INFO, "Info requesting positions to be saved.");
        JpoEventBus.getInstance().post(new SaveDockablesPositionsRequest());


        if (Settings.getPictureCollection().getUnsavedUpdates()) {
            final Object[] options = {
                    Settings.getJpoResources().getString("discardChanges"),
                    Settings.getJpoResources().getString("genericSaveButtonLabel"),
                    Settings.getJpoResources().getString("FileSaveAsMenuItemText"),
                    Settings.getJpoResources().getString("genericCancelText")};
            int option = JOptionPane.showOptionDialog(
                    Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("unsavedChanges"),
                    Settings.getJpoResources().getString("genericWarning"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (option) {
                case 0:
                    JpoEventBus.getInstance().post(request.nextRequest());
                    break;
                case 1:
                    final var fileSaveRequest = new FileSaveRequest(request.nextRequest());
                    JpoEventBus.getInstance().post(fileSaveRequest);
                    break;
                case 2:
                    final var fileSaveAsRequest = new FileSaveAsRequest(request.nextRequest());
                    JpoEventBus.getInstance().post(fileSaveAsRequest);
                    break;
                default:
                    // do a cancel if no other option was chosen
                    break;
            }
        } else {
            JpoEventBus.getInstance().post(request.nextRequest());
        }

    }

    /**
     * Handles the RefreshThumbnailRequest
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final RefreshThumbnailRequest request) {
        for (final SortableDefaultMutableTreeNode node : request.nodes()) {
            if (node.isRoot()) {
                LOGGER.fine("Ignoring the request for a thumbnail refresh on the Root Node as the query for it's parent's children will fail");
                return;
            }
            LOGGER.log(Level.FINE, "refreshing the thumbnail on the node {0}%nAbout to create the thumbnail", this);
            final var t = new ThumbnailController(new Thumbnail(), Settings.getThumbnailSize());
            t.setNode(new SingleNodeNavigator(node), 0);
        }
    }

    /**
     * Handles the RotatePictureRequest request
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final RotatePictureRequest request) {
        final var pictureInfo = (PictureInfo) request.node().getUserObject();
        pictureInfo.rotate(request.angle());
        LOGGER.info("Changed the rotation");
        final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
        nodes.add(request.node());
        nodes.add(request.node().getParent());
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, request.priority()));
    }

    /**
     * Handles the SetPictureRotationRequest request by setting the rotation and
     * calling the refresh thumbnails methods
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final SetPictureRotationRequest request) {
        final var pictureInfo = (PictureInfo) request.node().getUserObject();
        pictureInfo.setRotation(request.angle());
        final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
        nodes.add(request.node());
        nodes.add(request.node().getParent());
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, request.priority()));
    }

    /**
     * Handles the OpenCategoryEditorRequest request
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final OpenCategoryEditorRequest request) {
        new CategoryEditorJFrame();
    }

    /**
     * Handles the ShowGroupPopUpMenuRequest request
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final ShowGroupPopUpMenuRequest request) {
        final Runnable r = () -> {
            final var groupPopupMenu = new GroupPopupMenu(request.node());
            groupPopupMenu.show(request.invoker(), request.x(), request.y());
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    /**
     * Handles the ShowPicturePopUpMenuRequest request
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final ShowPicturePopUpMenuRequest request) {
        final Runnable r = () -> {
            final var picturePopupMenu = new PicturePopupMenu(request.nodes(), request.index());
            picturePopupMenu.show(request.invoker(), request.x(), request.y());
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }

    }

    /**
     * Handles the RunUserFunctionRequest request
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final RunUserFunctionRequest request) {
        try {
            runUserFunction(request.userFunctionIndex(), request.pictureInfo());
        } catch (ClassCastException | NullPointerException x) {
            LOGGER.severe(x.getMessage());
        }

    }

    /**
     * Handles the RemoveOldLowresThumbnailsRequest request
     *
     * @param request The request with the lowres urls to remove
     */
    @Subscribe
    public void handleEvent(final RemoveOldLowresThumbnailsRequest request) {
        SwingUtilities.invokeLater(
                () -> new ClearThumbnailsJFrame(request.lowresUrls())
        );
    }

    /**
     * Handles the OpenFileExplorerRequest request
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final OpenFileExplorerRequest request) {
        try {
            Desktop.getDesktop().open(request.directory());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "handleOpenFileExplorerRequest Exception: {0}", e.getMessage());
        }
    }


    /**
     * Handles the RemoveCategoryFromPictureInfoRequest request
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final RemoveCategoryFromPictureInfoRequest request) {
        request.pictureInfo().removeCategory(request.category());
    }


    /**
     * Handles the AddCategoriesToPictureNodesRequest request
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final AddCategoriesToPictureNodesRequest request) {
        for (final SortableDefaultMutableTreeNode node : request.nodes()) {
            if (node.getUserObject() instanceof PictureInfo pi) {
                pi.addCategoryAssignment(request.category());
            }
        }
    }


    /**
     * Listens to the CheckForUpdatesRequest and fulfills it if the conditions are met
     *
     * @param request the request object which contains the forceCheck flag
     */
    @Subscribe
    public void handleEvent(final CheckForUpdatesRequest request) {
        LOGGER.log(Level.INFO, "Caught the request to check for Updates");
        if (request.forceCheck() || VersionUpdate.mayCheckForUpdates()) {
            new VersionUpdate();
        }
    }

    /**
     * Inner class that monitors the collection for changes and figures out
     * whether the root node changed and asks the application to change the
     * title of the Window accordingly
     */
    private static class MainAppModelListener
            implements TreeModelListener {

        @Override
        public void treeNodesChanged(final TreeModelEvent e) {
            final var tp = e.getTreePath();
            LOGGER.log(Level.FINE, "The main app model listener trapped a tree node change event on the tree path: {0}", tp);
            if (tp.getPathCount() == 1) { //if the root node sent the event
                LOGGER.fine("Since this is the root node we will update the ApplicationTitle");

                updateApplicationTitle();
            }
        }

        @Override
        public void treeNodesInserted(final TreeModelEvent e) {
            // ignore
        }

        @Override
        public void treeNodesRemoved(final TreeModelEvent e) {
            // ignore, the root can't be removed ... Really?
        }

        @Override
        public void treeStructureChanged(final TreeModelEvent e) {
            final var tp = e.getTreePath();
            if (tp.getPathCount() == 1) { //if the root node sent the event
                updateApplicationTitle();
            }
        }

        /**
         * Sets the application title to the default title based on the
         * resource bundle string ApplicationTitle and the file name of the
         * loaded xml file if any.
         */
        private void updateApplicationTitle() {
            final var xmlFile = Settings.getPictureCollection().getXmlFile();
            if (xmlFile != null) {
                JpoEventBus.getInstance().post(new UpdateApplicationTitleRequest(Settings.getJpoResources().getString("ApplicationTitle") + ":  " + xmlFile.toString()));
            } else {
                JpoEventBus.getInstance().post(new UpdateApplicationTitleRequest(Settings.getJpoResources().getString("ApplicationTitle")));
            }
        }
    }

}

