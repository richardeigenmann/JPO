package org.jpo;

import org.jpo.datamodel.Settings;
import org.jpo.eventbus.*;
import org.jpo.gui.ApplicationStartupHandler;


/*
 org.jpo.Main.java:  starting point for the JPO application

 Copyright (C) 2002 - 2022  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY-
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 * The first class to be started to get the JPO application going.
 *
 * @author Richard Eigenmann, richard.eigenmann@gmail.com
 * @version 0.18
 * @since JDK1.18.0
 */
public class Main {

    /**
     * The main method is the entry point for this application (or any) Java
     * application. No parameter passing is used in the Jpo application. It checks if
     * the most important classes can be loaded and then posts the
     * ApplicationStartupRequest.
     *
     * @see ApplicationStartupRequest
     * @param args The command line arguments
     */
    public static void main( String[] args ) {
        System.out.println("\nJPO version " + Settings.JPO_VERSION + "\n"
                + "Copyright (C) 2000-2022 Richard Eigenmann,\nZurich, Switzerland\n"
                + "JPO comes with ABSOLUTELY NO WARRANTY;\n"
                + "for details Look at the Help | License menu item.\n"
                + "This is free software, and you are welcome\n"
                + "to redistribute it under certain conditions;\n"
                + "see Help | License for details.\n\n");

        registerEventHandlers();
        JpoEventBus.getInstance().post(new ApplicationStartupRequest() );
    }

    private static void registerEventHandlers() {
        JpoEventBus.getInstance().register(new ApplicationStartupHandler());
        JpoEventBus.getInstance().register(new RunUserFunctionHandler());
        JpoEventBus.getInstance().register(new StartThumbnailCreationDaemonHandler());
        JpoEventBus.getInstance().register(new MoveToDirHandler());
        JpoEventBus.getInstance().register(new RenameFileHandler());
        JpoEventBus.getInstance().register(new RenamePictureHandler());
        JpoEventBus.getInstance().register(new MoveToNewLocationHandler());
        JpoEventBus.getInstance().register(new PictureAdderHandler());
        JpoEventBus.getInstance().register(new OpenMainWindowHandler());
        JpoEventBus.getInstance().register(new FindDuplicatesHandler());
        JpoEventBus.getInstance().register(new CheckIntegrityHandler());
        JpoEventBus.getInstance().register(new EditSettingsHandler());
        JpoEventBus.getInstance().register(new EditCamerasHandler());
        JpoEventBus.getInstance().register(new SendEmailHandler());
        JpoEventBus.getInstance().register(new ShutdownApplicationHandler());
        JpoEventBus.getInstance().register(new CheckDirectoriesHandler());
        JpoEventBus.getInstance().register(new StartDoublePanelSlideshowHandler());
        JpoEventBus.getInstance().register(new ShowPictureHandler());
        JpoEventBus.getInstance().register(new ShowPictureInfoEditorHandler());
        JpoEventBus.getInstance().register(new ShowGroupInfoEditorHandler());
        JpoEventBus.getInstance().register(new CategoryAssignmentWindowHandler());
        JpoEventBus.getInstance().register(new ShowAutoAdvanceDialogHandler());
        JpoEventBus.getInstance().register(new ChooseAndAddCollectionHandler());
        JpoEventBus.getInstance().register(new ShowGroupAsTableHandler());
        JpoEventBus.getInstance().register(new FileLoadDialogHandler());
        JpoEventBus.getInstance().register(new FileLoadHandler());
        JpoEventBus.getInstance().register(new StartNewCollectionHandler());
        JpoEventBus.getInstance().register(new FileSaveHandler());
        JpoEventBus.getInstance().register(new FileSaveAsHandler());
        JpoEventBus.getInstance().register(new AfterFileSaveHandler());
        JpoEventBus.getInstance().register(new AddCollectionToGroupHandler());
        JpoEventBus.getInstance().register(new SortGroupHandler());
        JpoEventBus.getInstance().register(new AddEmptyGroupHandler());
        JpoEventBus.getInstance().register(new ExportGroupToHtmlHandler());
        JpoEventBus.getInstance().register(new GenerateWebsiteHandler());
        JpoEventBus.getInstance().register(new ExportGroupToFlatFileHandler());
        JpoEventBus.getInstance().register(new ExportGroupToNewCollectionHandler());
        JpoEventBus.getInstance().register(new ExportGroupToCollectionHandler());
        JpoEventBus.getInstance().register(new ExportGroupToPicasaHandler());
        JpoEventBus.getInstance().register(new AddGroupToEmailSelectionHandler());
        JpoEventBus.getInstance().register(new EmailSelectionHandler());
        JpoEventBus.getInstance().register(new ConsolidateGroupDialogHandler());
        JpoEventBus.getInstance().register(new ConsolidateGroupHandler());
        JpoEventBus.getInstance().register(new CopyToNewLocationHandler());
        JpoEventBus.getInstance().register(new CopyToDirHandler());
        JpoEventBus.getInstance().register(new CopyToNewZipfileHandler());
        JpoEventBus.getInstance().register(new CopyToZipfileHandler());
        JpoEventBus.getInstance().register(new CopyImageToClipboardHandler());
        JpoEventBus.getInstance().register(new CopyPathToClipboardHandler());
        JpoEventBus.getInstance().register(new MoveNodeHandler());
        JpoEventBus.getInstance().register(new RemoveNodeHandler());
        JpoEventBus.getInstance().register(new DeleteNodeFileHandler());
        JpoEventBus.getInstance().register(new ChooseAndAddPicturesToGroupHandler());
        JpoEventBus.getInstance().register(new PictureControllerZoomHandler());
        JpoEventBus.getInstance().register(new ChooseAndAddFlatfileHandler());
        JpoEventBus.getInstance().register(new AddFlatFileRequestHandler());
        JpoEventBus.getInstance().register(new OpenLicenceFrameHandler());
        JpoEventBus.getInstance().register(new OpenHelpAboutFrameHandler());
        JpoEventBus.getInstance().register(new OpenPrivacyFrameHandler());
        JpoEventBus.getInstance().register(new StartCameraWatchDaemonHandler());
        JpoEventBus.getInstance().register(new UnsavedUpdatesDialogHandler());
        JpoEventBus.getInstance().register(new RefreshThumbnailHandler());
        JpoEventBus.getInstance().register(new RotatePictureHandler());
        JpoEventBus.getInstance().register(new SetPictureRotationHandler());
        JpoEventBus.getInstance().register(new OpenCategoryEditorHandler());
        JpoEventBus.getInstance().register(new ShowGroupPopUpMenuHandler());
        JpoEventBus.getInstance().register(new ShowPicturePopUpMenuHandler());
        JpoEventBus.getInstance().register(new RemoveOldLowresThumbnailsHandler());
        JpoEventBus.getInstance().register(new OpenFileExplorerHandler());
        JpoEventBus.getInstance().register(new PictureCategoryHandler());
        JpoEventBus.getInstance().register(new CheckForUpdatesHandler());
        JpoEventBus.getInstance().register(new FindBasedirHandler());
        JpoEventBus.getInstance().register(new StartThumbnailCreationDaemonWatchDogHandler());
        JpoEventBus.getInstance().register(new StartHashCodeScannerHandler());
        JpoEventBus.getInstance().register(new CheckForCollectionProblemsHandler());
    }


}
