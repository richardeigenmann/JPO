package jpo.gui;

import java.util.ListResourceBundle;


/*
Copyright (C) 2002-2018  Richard Eigenmann, Zürich, Switzerland
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
 * class that holds the generic labels for the JPO application.
 * Use the following command to access your Strings:
 * Settings.jpoResources.getString("key")
 */
public class JpoResources_en extends ListResourceBundle {

    @Override
    protected Object[][] getContents() {
        return contents;

    }

    /**
     * the resource bundle
     */
    protected static final Object[][] contents = {
            // Jpo
            {"ApplicationTitle", "JPO - Java Picture Organizer"},
            {"jpoTabbedPaneCollection", "Collection"},
            {"jpoTabbedPaneSearches", "Searches"},

            // Generic texts
            {"genericTargetDirText", "Target Directory:"},
            {"genericCancelText", "Cancel"},
            {"genericSaveButtonLabel", "Save"},
            {"genericOKText", "OK"},
            {"genericSelectText", "Select"},
            {"threeDotText", "..."},
            {"genericExportButtonText", "Export"},
            {"genericSecurityException", "Security Exception"},
            {"genericError", "Error"},
            {"genericInfo", "Information"},
            {"internalError", "or"},
            {"genericWarning", "Warning"},
            {"genericExit", "Exit"},
            {"outOfMemoryError", "An out of Memory error has occurred"},
            {"areYouSure", "Are you sure?"},

            // Help About Dialog
            {"HelpAboutText", "JPO Version 0.14 is a Java/Swing program\n"
                    + "written by Richard Eigenmann, Zürich, Switzerland\n"
                    + "Copyright 2000 - 2018\n"
                    + "richard.eigenmann@gmail.com\n"
                    + "http://j-po.sourceforge.net\n"
                    + "\nThe Exif extraction is courtesy of Drew Noakes\n"
                    + "The Table sorter is courtesy of Philip Milne\n"
                    + "Mikael Grev develops MiG Layout\n"
                    + "Michael Rudolf develops JWizz\n"
                    + "Franklin He translated to Chinese\n\n"},
            {"HelpAboutUser", "User: "},
            {"HelpAboutOs", "Operating System: "},
            {"HelpAboutJvm", "JVM: "},
            {"HelpAboutJvmMemory", "JVM Max Memory: "},
            {"HelpAboutJvmFreeMemory", "JVM Free Memory: "},

            // QueryJFrame
            {"searchDialogTitle", "Find Pictures"},
            {"searchDialogLabel", "Search for:"},
            {"searchDialogSaveResultsLabel", "save results"},
            {"advancedFindJButtonOpen", "Advanced Criteria"},
            {"advancedFindJButtonClose", "Simple search"},
            {"noSearchResults", "There were no pictures matching the criteria."},
            {"lowerDateJLabel", "Between:"},
            {"dateRangeError", "Sorry, the date range makes no sense."},

            // PictureViewer
            {"PictureViewerTitle", "JPO Picture Browser"},
            {"PictureViewerKeycodes", "The following keyboard shortcuts can be used:\n" + "N: Next Image\n" + "P: Previous Image\n" + "I: Show Information on | off\n" + "<space>,<home>: Resize to fit\n" + "<left>,<right>,<up>,<down>: scroll image\n" + "<PgUp>: Zoom In\n" + "<PgDown>: Zoom Out\n" + "1: Zoom to 100%\n" + "F: Window size menu\n" + "M: Popup menu"},
            {"PictureViewerKeycodesTitle", "Keyboard Shortcuts"},
            {"NavigationPanel", "Navigation Tools"},
            {"fullScreenJButton.ToolTipText", "Full Screen"},
            {"popupMenuJButton.ToolTipText", "Popup Menu"},
            {"nextJButton.ToolTipText", "Next Picture"},
            {"previousJButton.ToolTipText", "Prior Picture"},
            {"infoJButton.ToolTipText", "Info"},
            {"resetJButton.ToolTipText", "Reset"},
            {"clockJButton.ToolTipText", "Automatic Advance"},
            {"closeJButton.ToolTipText", "close window"},
            {"rotateLeftJButton.ToolTipText", "Rotate Left"},
            {"rotateRightJButton.ToolTipText", "Rotate Right"},
            {"zoomInJButton.ToolTipText", "Zoom In"},
            {"zoomOutJButton.ToolTipText", "Zoom Out"},
            {"PictureViewerDescriptionFont", "Arial-BOLD-12"},

            // Settings
            {"SettingsTitleFont", "Arial-BOLD-20"},
            {"SettingsCaptionFont", "Arial-PLAIN-16"},
            // SettingsDialog Texts
            {"settingsDialogTitle", "Edit Settings"},
            {"browserWindowSettingsJPanel", "General"},
            {"languageJLabel", "Language:"},
            {"autoLoadJLabelLabel", "Automatically load:"},
            {"windowSizeChoicesJlabel", "When JPO starts size Window to:"},
            {"windowSizeChoicesMaximum", "Maximum"},
            {"pictureViewerJPanel", "Picture Viewer"},
            {"pictureViewerSizeChoicesJlabel", "Size of viewer:"},
            {"maximumPictureSizeLabel", "Maximum Picture Scale size:"},
            {"maxCacheLabel", "Maximum Cache Images:"},
            {"leaveSpaceLabel", "Leave from bottom:"},
            {"dontEnlargeJCheckBoxLabel", "Don't enlarge small images"},
            {"pictureCoordinates", "Default Coordinates of picture window (x/y):"},
            {"pictureSize", "Default size of picture window (w/h):"},
            {"pictureViewerFastScale", "Tick for Speed over Quality when scaling"},
            {"thumbnailSettingsJPanel", "Thumbnails"},
            {"thumbnailDirLabel", "Thumbnail Directory:"},
            {"keepThumbnailsJCheckBoxLabel", "Write thumbnails to disk"},
            {"maxThumbnailsLabelText", "Maximum thumbnails per page:"},
            {"thumbnailSizeLabel", "Thumbnail Size:"},
            {"thumbnailFastScale", "Tick for Speed over Quality when scaling"},
            {"zapThumbnails", "Delete all Thumbnails"},
            {"thumbnailsDeleted", " Thumbnails deleted"},
            {"autoLoadChooserTitle", "Choose File to automatically load"},
            {"logfileChooserTitle", "Choose the file to write the log"},
            {"thumbDirChooserTitle", "Choose Directory for Thumbnails"},
            {"settingsError", "Settings Error"},
            {"generalLogFileError", "There is a problem with the logfile. Logging was disabled."},
            {"thumbnailDirError", "Something is seriously wrong with your thumbnail directory"},
            {"userFunctionJPanel", "User Functions"},
            {"userFunction1JLabel", "User Function 1"},
            {"userFunction2JLabel", "User Function 2"},
            {"userFunction3JLabel", "User Function 3"},
            {"userFunctionNameJLabel", "Name:"},
            {"userFunctionCmdJLabel", "Cmd:"},
            {"userFunctionHelpJTextArea", "%f will be substituted by the filename\n%u will be substituted by the URL of the picture"},
            {"emailJPanel", "Email Server"},
            {"emailJLabel", "Email Server Details"},
            {"predefinedEmailJLabel", "Predefined Server:"},
            {"emailServerJLabel", "Email Server:"},
            {"emailPortJLabel", "Port:"},
            {"emailAuthentication", "Authentication:"},
            {"emailUserJLabel", "Username:"},
            {"emailPasswordJLabel", "Password:"},
            {"emailShowPasswordButton", "Show Password"},


            // Settings
            {"thumbNoExistError", "The directory for the thumbnails doesn't exist.\nPlease select Edit | Settings to set this correctly.\nThumbnail caching has been turned off."},
            {"thumbNoWriteError", "The directory for the thumbnails is not writable.\nPlease select Edit | Settings to set this correctly.\nThumbnail caching has been turned off."},
            {"thumbNoDirError", "The location for the thumbnails is not a directory.\nPlease select Edit | Settings to set this correctly.\nThumbnail caching has been turned off."},
            {"logFileCanWriteError", "The logfile in the Settings is not writable.\nPlease select Edit | Settings to set this correctly.\nLogging has been disabled"},
            {"logFileIsFileError", "The location for the logfile is not a File.\nPlease select Edit | Settings to set this to a file.\nLogging has been disabled"},
            {"generalLogFileError", "There is a problem with the logfile. Logging was disabled."},
            {"cantWriteIniFile", "Error while writing ini file:\n"},
            {"cantReadIniFile", "Could not read JPO.ini. Using defaults.\n"},


            // HtmlDistillerJFrame
            {"HtmlDistillerJFrameHeading", "Generate Website"},
            {"HtmlDistillerThreadTitle", "Extracting to HTML"},
            {"HtmlDistillerChooserTitle", "Select Target Directory for HTML"},
            {"HtmlDistTarget", "Target"},
            {"HtmlDistThumbnails", "Thumbnails"},
            {"exportHighresJCheckBox", "Export high resolution pictures"},
            {"rotateHighresJCheckBox", "Rotate high resolution pictures"},
            {"linkToHighresJCheckBox", "Link to high resolution pictures at current locations"},
            {"jpo.export.GenerateWebsiteWizard3Midres.generateMouseoverJCheckBox", "Generate mouseover effects"},
            {"generateZipfileJCheckBox", "Generate Zipfile for download of Highres Pictures"},
            {"picsPerRowText", "Columns: "},
            {"thumbnailSizeJLabel", "Thumbnail size:"},
            {"scalingSteps", "Scaling steps: "},
            {"htmlDistCrtDirError", "Could not create Export directory!"},
            {"htmlDistIsDirError", "This is not a directory!"},
            {"htmlDistCanWriteError", "This is not a writable directory!"},
            {"htmlDistIsNotEmptyWarning", "The target Directory is not empty.\nClick OK to continue and possibly overwrite files."},
            {"HtmlDistMidres", "Medium size images"},
            {"GenerateMap", "Generate Map"},
            {"HtmlDistMidresHtml", "Generate navigation pages"},
            {"midresSizeJLabel", "Midres size"},
            {"midresJpgQualitySlider", "Midres Jpg Quality"},
            {"lowresJpgQualitySlider", "Jpg Quality:"},
            {"jpgQualityBad", "Poor"},
            {"jpgQualityGood", "Good"},
            {"jpgQualityBest", "Best"},
            {"HtmlDistHighres", "High resolution originals"},
            {"HtmlDistOptions", "Options"},
            {"HtmlDistillerNumbering", "Create Image Filenames"},
            {"hashcodeRadioButton", "using Java Hash Code"},
            {"originalNameRadioButton", "using original image name"},
            {"sequentialRadioButton", "using sequential number"},
            {"sequentialRadioButtonStart", "Starting at"},
            {"generateRobotsJCheckBox", "Prevent search engine indexing (write robots.txt)"},
            {"welcomeTitle", "Welcome"}, // new
            {"welcomeMsg", "Generate a Web Page showing %d pictures"}, // new
            {"generateFrom", "From: "}, // new
            {"summary", "Summary"}, // new
            {"check", "Check"}, // new


            // HtmlDistillerThread
            {"LinkToJpo", "Made with <a href=\"http://j-po.sourceforge.net\">JPO</a>"},
            {"htmlDistillerInterrupt", "interrupting gracefully"},
            {"CssCopyError", "Error writing file: "},
            {"HtmlDistillerPreviewFont", "SansSerif-BOLD-18"},
            {"HtmlDistDone", "Wrote website with %d pictures"},

            // ReconcileJFrame
            {"ReconcileJFrameTitle", "Reconcile Directory vs Collection"},
            {"ReconcileBlaBlaLabel", "<HTML>This function will check whether the files in the indicated directory are present <br>in the current collection</HTML>"},
            {"directoryJLabelLabel", "Directory to reconcile:"},
            {"directoryCheckerChooserTitle", "Directory to reconcile"},
            {"jpo.gui.ReconcileFound", "%s found in collection.\n"},
            {"ReconcileNotFound", "Not in collection: "},
            {"ReconcileDone", "Done.\n"},
            {"ReconcileInterrupted", "Interrupted.\n"},
            {"ReconcileListPositives", "List Positive matches"},
            {"ReconcileOkButtonLabel", "Reconcile"},
            {"ReconcileSubdirectories", "Reconcile Subdirectories"},
            {"ReconcileCantReadError", "Can't read: "},
            {"ReconcileNullFileError", "Bad directory"},
            {"ReconcileStart", "Reconciling directory: "},
            {"ReconcileNoFiles", "No Files Found.\n"},

            // CollectionDistillerJFrame
            {"CollectionDistillerJFrameFrameHeading", "Export to new Collection"},
            {"collectionExportPicturesText", "Export Pictures"},
            {"xmlFileNameLabel", "Name for XML file:"},
            {"collectionExportChooserTitle", "Target Directory for Collection"},

            // ConsolidateGroupJFrame
            {"highresTargetDirJTextField", "Select Highres Consolidation Directory"},
            {"lowresTargetDirJTextField", "Select Lowres Consolidation Directory"},
            {"RecurseSubgroupsLabel", "Recurse Sub-Groups"},
            {"ConsolidateGroupBlaBlaLabel", "<HTML>This function will move all pictures of the selected group to the target directory. It will fix any ref-<br>erences to the images elsewhere in the collection. The files will be physically moved on the disk.<br><p> <font color=red>Are sure you want to do this?<br></font></htm>"},
            {"ConsolidateGroupJFrameHeading", "Consolidate / Move Pictures"},
            {"ConsolidateButton", "Consolidate"},
            {"ConsolidateFailure", "Consolidation generated a failure and has aborted."},
            {"ConsolidateProgBarTitle", "Running Consolidation"},
            {"ConsolidateProgBarDone", "%d pictures Consolidated"},
            {"lowresJCheckBox", "consolidate Lowres too"},
            {"ConsolidateCreateDirFailure", "Aborted because directory %s can't be created."},
            {"ConsolidateCantWrite", "Aborted because directory %s is not writable."},

            // JarDistillerJFrame
            {"groupExportJarTitleText", "Export to Jar"},
            {"JarDistillerLabel", "Jar (Java Archive) to create:"},
            {"SelectJarFileTitle", "Select directory to export pictures to"},

            // PictureInfoEditor
            {"PictureInfoEditorHeading", "Picture Properties"},
            {"highresChooserTitle", "Choose Highres Image"},
            {"pictureDescriptionLabel", "Picture description:"},
            {"creationTimeLabel", "Creation Date & Time:"},
            {"highresLocationLabel", "Highres location:"},
            {"lowresLocationLabel", "Lowres location:"},
            {"filmReferenceLabel", "Film reference:"},
            {"rotationLabel", "Rotate on load:"},
            {"latitudeLabel", "Latitude:"},
            {"longitudeLabel", "Longitude:"},
            {"commentLabel", "Comment:"},
            {"copyrightHolderLabel", "Copyright statement:"},
            {"photographerLabel", "Photographer:"},
            {"resetLabel", "Reset"},
            {"checksumJButton", "refresh"},
            {"checksumJLabel", "Adler32 Checksum: "},
            {"parsedAs", "Parses as: "},
            {"failedToParse", "Fails to parse into Date object"},
            {"categoriesJLabel-2", "Categories:"},
            {"setupCategories", ">> Set up categories <<"},
            {"noCategories", ">> None <<"},

            //GroupInfoEditor
            {"GroupInfoEditorHeading", "Edit Group Description"},
            {"groupDescriptionLabel", "Group description:"},

            // GroupPopupMenu
            {"groupShowJMenuItem", "Show Group"},
            {"groupSlideshowJMenuItem", "Show Pictures"},
            {"groupFindJMenuItemLabel", "Find"},
            {"groupEditJMenuItem", "Properties"},
            {"groupRefreshJMenuItem", "Refresh Icon"},
            {"groupTableJMenuItemLabel", "Edit as Table"},
            {"addGroupJMenuLabel", "Add"},
            {"addNewGroupJMenuItemLabel", "New Group"},
            {"addPicturesJMenuItemLabel", "Pictures"},
            {"addCollectionJMenuItemLabel", "Collection"},
            {"addCollectionFormFile", "Choose File"},
            {"groupExportNewCollectionMenuText", "Export to Collection"},
            {"addFlatFileJMenuItemLabel", "Flat File"},
            {"moveNodeJMenuLabel", "Move"},
            {"moveGroupToTopJMenuItem", "to Top"},
            {"moveGroupUpJMenuItem", "Up"},
            {"moveGroupDownJMenuItem", "Down"},
            {"moveGroupToBottomJMenuItem", "to Bottom"},
            {"indentJMenuItem", "indent"},
            {"outdentJMenuItem", "outdent"},
            {"groupRemoveLabel", "Remove Node"},
            {"consolidateMoveLabel", "Consolidate/Move"},
            {"sortJMenu", "Sort by"},
            {"sortByDescriptionJMenuItem", "Description"},
            {"sortByFilmReferenceJMenuItem", "Film Reference"},
            {"sortByCreationTimeJMenuItem", "Creation Time"},
            {"sortByCommentJMenuItem", "Comment"},
            {"sortByPhotographerJMenuItem", "Photographer"},
            {"sortByCopyrightHolderTimeJMenuItem", "Copyright Holder"},
            {"groupSelectForEmail", "Select all for Emailing"},
            {"groupExportHtmlMenuText", "Generate Website"},
            {"groupExportFlatFileMenuText", "Export to Flat File"},
            {"groupExportJarMenuText", "Export to Jar Archive"},

            // PicturePopupMenu
            {"pictureShowJMenuItemLabel", "Show Picture"},
            {"mapShowJMenuItemLabel", "Show Map"},
            {"pictureEditJMenuItemLabel", "Properties"},
            {"copyImageJMenuLabel", "Copy Image"},
            {"copyToNewLocationJMenuItem", "choose target directory"},
            {"copyToNewZipfileJMenuItem", "to zip file"},
            {"copyToNewLocationSuccess", "%d of %d pictures copied"},
            {"moveToNewLocationSuccess", "%d of %d pictures moved"},
            {"FileOperations", "File operations"},
            {"fileMoveJMenu", "Move File"},
            {"moveToNewLocationJMenuItem", "choose target directory"},
            {"renameJMenu", "Rename"},
            {"fileRenameJMenuItem", "Rename"},
            {"FileRenameLabel1", "Rename \n"},
            {"FileRenameLabel2", "\nto: "},
            {"FileRenameTargetExistsTitle", "Target File Exists"},
            {"FileRenameTargetExistsText", "The file %s exists and would be overwritten\nOK to change to %s ?"},
            {"fileDeleteJMenuItem", "Delete"},
            {"pictureRefreshJMenuItem", "Refresh Thumbnail"},
            {"pictureMailSelectJMenuItem", "Select for email"},
            {"pictureMailUnselectJMenuItem", "Unselect for email"},
            {"pictureMailUnselectAllJMenuItem", "Clear email selection"},
            {"rotation", "Rotation"},
            {"rotate90", "Rotate Right 90"},
            {"rotate180", "Rotate 180"},
            {"rotate270", "Rotate Left 270"},
            {"rotate0", "No Rotation"},
            {"userFunctionsJMenu", "User Function"},
            {"pictureNodeRemove", "Remove Node"},
            {"movePictureToTopJMenuItem", "to Top"},
            {"movePictureUpJMenuItem", "Up"},
            {"movePictureDownJMenuItem", "Down"},
            {"movePictureToBottomJMenuItem", "to Bottom"},
            {"recentDropNodePrefix", "To Group: "},
            {"categoryUsageJMenuItem", "Categories"},
            {"navigationJMenu", "Navigate to"},
            // ThumbnailJScrollPane
            {"ThumbnailSearchResults", "Search Results for "},
            {"ThumbnailSearchResults2", " in "},
            {"ThumbnailToolTipPrevious", "Previous Page"},
            {"ThumbnailToolTipNext", "Next Page"},
            {"ThumbnailJScrollPanePage", "Page "},

            //ChangeWindowPopupMenu
            {"fullScreenLabel", "Fullscreen"},
            {"leftWindowLabel", "Left"},
            {"rightWindowLabel", "Right"},
            {"topLeftWindowLabel", "Top Left"},
            {"topRightWindowLabel", "Top Right"},
            {"bottomLeftWindowLabel", "Bottom Left"},
            {"bottomRightWindowLabel", "Bottom Right"},
            {"defaultWindowLabel", "Default"},
            {"windowDecorationsLabel", "Window Frame"},
            {"windowNoDecorationsLabel", "No Frame"},

            // CollectionJTree
            {"DefaultRootNodeText", "New Collection"},
            {"CopyImageDialogButton", "Copy"},
            {"CopyImageDialogTitle", "Specify target for: "},
            {"MoveImageDialogButton", "Move"},
            {"MoveImageDialogTitle", "Specify move target directory"},
            {"CopyImageDirError", "Target directory can't be created. Copy aborted.\n"},
            {"fileOpenButtonText", "Open"},
            {"fileOpenHeading", "Open Collection"},
            {"fileSaveAsTitle", "Save Collection As"},
            {"collectionSaveTitle", "Collection Saved"},
            {"collectionSaveBody", "Collection Saved as: \n"},
            {"setAutoload", "Automatically load this collection when JPO starts"},
            {"addSinglePictureTitle", "Select picture to add"},
            {"addSinglePictureButtonLabel", "Select"},
            {"addFlatFileTitle", "Choose Flat File Picture List"},
            {"saveFlatFileTitle", "Save List of Pictures as Flat File"},
            {"saveFlatFileButtonLabel", "Save"},
            {"moveNodeError", "Target is descendant of source. Move aborted."},
            {"unsavedChanges", "Unsaved changes exist."},
            {"confirmSaveAs", "Target file exists!\nContinue and overwrite it?"},
            {"discardChanges", "Discard"},
            {"noPicsForSlideshow", "This group has no pictures to display."},
            {"fileRenameTitle", "Rename File"},
            {"fileDeleteTitle", "Delete File"},
            {"fileDeleteError", "The file could not be deleted:\n"},
            {"deleteRootNodeError", "You can't remove the root node of the collection."},

            // ApplicationJMenuBar
            {"FileMenuText", "File"},
            {"FileNewJMenuItem", "New Collection"},
            {"FileLoadMenuItemText", "Open Collection"},
            {"FileOpenRecentItemText", "Open Recent"},
            {"FileAddMenuItemText", "Add Pictures"},
            {"FileSaveMenuItemText", "Save Collection"},
            {"FileSaveAsMenuItemText", "Save As"},
            {"FileExitMenuItemText", "Exit"},
            {"EditJMenuText", "Edit"},
            {"EditFindJMenuItemText", "Find"},
            {"ExtrasJMenu", "Extras"},
                        {"EditCheckDirectoriesJMenuItemText", "Reconcile"},
            {"EditCheckIntegrityJMenuItem", "Check Integrity"},
            {"EditCamerasJMenuItem", "Cameras"},
            {"FindDuplicatesJMenuItem", "Find Duplicates"},
            {"EditCategoriesJMenuItem", "Categories"},
            {"EditSettingsMenuItemText", "Settings"},
            {"actionJMenu", "Action"},
            {"emailJMenuItem", "Send email"},
            {"RandomSlideshowJMenuItem", "Random Slideshow"},
            {"StartThumbnailCreationThreadJMenuItem", "Start additional thumbnail creator thread"},
            {"HelpJMenuText", "Help"},
            {"HelpAboutMenuItemText", "About"},
            {"HelpLicenseMenuItemText", "License"},
            {"HelpPrivacyMenuItemText", "Privacy"},
            {"HelpResetWindowsJMenuItem", "Reset Windows"},

            // PictureViewer
            {"autoAdvanceDialogTitle", "Start Automatic Advance Timer"},
            {"randomAdvanceJRadioButtonLabel", "Random Advance"},
            {"sequentialAdvanceJRadioButtonLabel", "Sequential Advance"},
            {"restrictToGroupJRadioButtonLabel", "Restrict to current Group"},
            {"useAllPicturesJRadioButtonLabel", "Cycle through all Pictures"},
            {"timerSecondsJLabelLabel", "Advance Delay (seconds)"},

            // ExifViewerJFrame
            {"ExifTitle", "EXIF Headers\n"},
            {"noExifTags", "No EXIF tags found"},

            // PictureAdder
            {"PictureAdderDialogTitle", "Add Pictures and Directories"},
            {"PictureAdderProgressDialogTitle", "Adding Pictures"},
            {"notADir", "Not a directory:\n"},
            {"notGroupInfo", "Node is not a group node."},
            {"fileChooserAddButtonLabel", "Add"},
            {"recurseSubdirectoriesTitle", "Recurse Subdirectories"},
            {"recurseSubdirectoriesMessage", "There are nested subdirectories in your selection.\nDo you want to add these too?"},
            {"recurseSubdirectoriesOk", "Add"},
            {"recurseSubdirectoriesNo", "No"},
            {"picturesAdded", "%d pictures Added"},
            {"pictureAdderOptionsTab", "Options"},
            {"pictureAdderThumbnailTab", "Thumbnail"},
            {"pictureAdderCategoryTab", "Categories"},
            // AddFromCamera
            {"AddFromCamera", "Add pictures from camera"},
            {"cameraNameJLabel", "Name of Camera:"},
            {"cameraDirJLabel", "Root directory of camera on computer's file system:"},
            {"cameraConnectJLabel", "Command to connect camera to computer:"},
            {"cameraDisconnectJLabel", "Command to disconnect camera from computer:"},
            {"allPicturesJRadioButton", "Add all pictures in camera to Collection"},
            {"newPicturesJRadioButton", "Add only the new pictures in the camera to Collection"},
            {"missingPicturesJRadioButton", "Add the pictures that are not in the Collection"},
            {"targetDirJLabel", "Target directory for the pictures:"},
            {"AddFromCameraOkJButton", "Go"},
            {"editCameraJButton", "Edit Cameras"},
            {"categoriesJButton", "Categories"},
            // CameraEditor
            {"CameraEditor", "Edit Camera Settings"},
            {"runConnectJButton", "Run"},
            {"saveJButton", "Save"},
            {"memorisedPicsJLabel", "Number of pictures remembered from last import:"},
            {"refreshJButton", "Refresh"},
            {"zeroJButton", "Cancel"},
            {"addJButton", "Add"},
            {"deleteJButton", "Delete"},
            {"closeJButton", "Close"},
            {"filenameJCheckBox", "rely on filenames to remember pictures (faster)"},
            {"refreshJButtonError", "Save your changes first!"},
            {"monitorJCheckBox", "automatically monitor for new pictures"},

            // Camera
            {"countingChecksum", "Building Checksums"},
            {"countingChecksumComplete", " checksums calculated"},
            {"newCamera", "New Camera"},

            // XmlDistiller
            {"DtdCopyError", "Could not copy collection.dtd\n"},

            // CollectionProperties
            {"CollectionPropertiesJFrameTitle", "Collection Properties"},
            {"CollectionNodeCountLabel", "Number of Nodes: "},
            {"CollectionGroupCountLabel", "Number of Groups: "},
            {"CollectionPictureCountLabel", "Number of Pictures: "},
            {"CollectionSizeJLabel", "Disk space used: "},
            {"queCountJLabel", "Thumbnails on queue: "},
            {"editProtectJCheckBoxLabel", "Protect Collection from Edits"},

            // Tools
            {"copyPictureError1", "Could not copy\n"},
            {"copyPictureError2", "\nto: "},
            {"copyPictureError3", "\nbecause: "},
            {"freeMemory", "Memory: "},

            // PictureAdder
            {"recurseJCheckBox", "Recurse subdirectories"},
            {"retainDirectoriesJCheckBox", "Retain directory structure"},
            {"newOnlyJCheckBox", "Add new pictures only"},
            {"showThumbnailJCheckBox", "Show Thumbnail"},

            // IntegrityChecker
            {"IntegrityCheckerTitle", "Checking Collection Integrity"},
            {"integrityCheckerLabel", "Checking the integrity:"},
            {"checkDateParsing", "Check Date Parsing"},
            {"check1done", "Dates that could not be parsed: "},
            {"checkChecksums", "Verify Checksums"},
            {"check2progress", "Corrected Checksums (running): "},
            {"check2done", "Corrected Checksums: "},
            {"check3", "Check 3"},
            // SortableDefaultMutableTreeNode
            {"GDPMdropBefore", "drop before target"},
            {"GDPMdropAfter", "drop after target"},
            {"GDPMdropIntoFirst", "drop into target at top"},
            {"GDPMdropIntoLast", "drop into target at end"},
            {"GDPMdropCancel", "cancel drop"},
            {"copyAddPicturesNoPicturesError", "No pictures found. Operation Aborted."},
            {"FileDeleteTitle", "Delete"},
            {"FileDeleteLabel", "Delete File\n"},
            {"newGroup", "New Group"},
            {"queriesTreeModelRootNode", "Searches"},
            // CategoryEditorJFrame
            {"CategoryEditorJFrameTitle", "Category Editor"},
            {"categoryJLabel", "Category"},
            {"categoriesJLabel", "Categories"},
            {"addCategoryJButton", "Add Category"},
            {"deleteCategoryJButton", "Remove Category"},
            {"renameCategoryJButton", "Rename Category"},
            {"doneJButton", "Done"},
            {"countCategoryUsageWarning1", "There are "},
            {"countCategoryUsageWarning2", " nodes using this category.\nAre you sure you want to remove it?"},
            // CategoryUsageJFrame
            {"CategoryUsageJFrameTitle", "Category Usage"},
            {"numberOfPicturesJLabel", "%d pictures selected"},
            {"updateJButton", "Update"},
            {"refreshJButtonCUJF", "Refresh"},
            {"modifyCategoryJButton", "Categories"},
            {"cancelJButton", "Cancel"},

            // EmailerJFrame
            {"EmailerJFrame", "Send email"},
            {"imagesCountJLabel", "Number of Pictures Selected: "},
            {"emailJButton", "Send"},
            {"noNodesSelected", "There are no selected nodes. Please select them with the popup menu on the picture nodes."},
            {"fromJLabel", "From:"},
            {"toJLabel", "To:"},
            {"messageJLabel", "Message:"},
            {"subjectJLabel", "Subject:"},
            {"emailSendError", "The following error occured:\n"},
            {"emailOK", "The mail was sent successfully."},
            {"emailSizesJLabel", "Sizes:"},
            {"emailResizeJLabel", "Resize to:"},
            {"emailSize1", "Small (350 x 300)"},
            {"emailSize2", "Medium (700 x 550)"},
            {"emailSize3", "Medium plus Original"},
            {"emailSize4", "Large (1000 x 800)"},
            {"emailSize5", "Originals only"},
            {"emailOriginals", "Attach Original Images"},
            {"emailNoNodes", "No pictures to send. Select images with the right-click popup menu."},
            {"emailNoServer", "No Email Server configured. Configure the server under Edit > Settings > Email Server."},
            //Emailer Thread
            {"EmailerLoading", "Loading:  "},
            {"EmailerScaling", "Scaling:  "},
            {"EmailerWriting", "Writing:  "},
            {"EmailerAdding", "Adding:  "},
            {"EmailerSending", "Sending Mail"},
            {"EmailerSent", "Mail delivered to Server"},
            //CategoryQuery
            {"CategoryQuery", "Category: "},
            //PicturePanel
            {"PicturePaneInfoFont", "Arial-PLAIN-10"},
            {"PicturePaneSize", "Size: "},
            {"PicturePaneMidpoint", " Midpoint: "},
            {"PicturePaneLoadTime", "Loaded in: "},
            {"PicturePaneSeconds", " seconds"},
            {"PicturePaneFreeMemory", "Free Memory: "},
            {"PicturePaneReadyStatus", "Ready"},
            //ExifInfo
            {"ExifInfoCamera", "Camera:"},
            {"ExifInfoLens", "Lens:"},
            {"ExifInfoShutterSpeed", "Shutter Speed:"},
            {"ExifInfoAperture", "Aperture:"},
            {"ExifInfoFocalLength", "Focal Length:"},
            {"ExifInfoISO", "ISO:"},
            {"ExifInfoTimeStamp", "Time stamp:"},
            {"ExifInfoLongitude", "Longitude:"},
            {"ExifInfoLatitude", "Latitude:"},

            //ThumbnailDescriptionJPanel
            {"ThumbnailDescriptionJPanelLargeFont", "Arial-PLAIN-12"},
            {"ThumbnailDescriptionJPanelSmallFont", "Arial-PLAIN-9"},
            {"ThumbnailDescriptionNoNodeError", "No node for this position."},
            // ScalablePicture
            {"ScalablePictureUninitialisedStatus", "Uninitialised"},
            {"ScalablePictureLoadingStatus", "Loading"},
            {"ScalablePictureRotatingStatus", "Rotating"},
            {"ScalablePictureScalingStatus", "Scaling"},
            {"ScalablePictureErrorStatus", "Error"},
            //CameraDownloadWizard
            {"CameraDownloadWizard", "Camera Download Wizard"},
            {"DownloadCameraWizardStep1Title", "Camera detected"},
            {"DownloadCameraWizardStep1Description", "Camera detected"},
            {"DownloadCameraWizardStep1Text1", "Camera "},
            {"DownloadCameraWizardStep1Text2", " detected."},
            {"DownloadCameraWizardStep1Text3", " new pictures found."},
            {"DownloadCameraWizardStep1Text4", "Analysing pictures...."},
            {"DownloadCameraWizardStep2Title", "Move or Copy"},
            {"DownloadCameraWizardStep2Description", "Move or Copy to your Computer"},
            {"DownloadCameraWizardStep2Text1", "<html>There are "},
            {"DownloadCameraWizardStep2Text2", " new pictures on your camera.<br><br>Would you like to<br>"},
            {"DownloadCameraWizardStep2Text3", "<html><b>move</b> the pictures to your computer or"},
            {"DownloadCameraWizardStep2Text4", "<html><b>copy</b> them to your computer?"},
            {"DownloadCameraWizardStep3Title", "Where to add"},
            {"DownloadCameraWizardStep3Description", "Where to add in the collection"},
            {"DownloadCameraWizardStep3Text0", "Tick to create a sub-folder"},
            {"DownloadCameraWizardStep3Text1", "Title for the new sub-folder:"},
            {"DownloadCameraWizardStep3Text2a", "Add the new folder to:"},
            {"DownloadCameraWizardStep3Text2b", "Add the pictures to:"},
            {"DownloadCameraWizardStep4Title", "Where to store"},
            {"DownloadCameraWizardStep4Description", "Select directory to save pictures on your computer"},
            {"DownloadCameraWizardStep4Text1", "Target directory on Computer:"},
            {"DownloadCameraWizardStep5Title", "Summary"},
            {"DownloadCameraWizardStep5Description", "Summary"},
            {"DownloadCameraWizardStep5Text1", "Copy "},
            {"DownloadCameraWizardStep5Text2", "Move "},
            {"DownloadCameraWizardStep5Text3", " pictures from"},
            {"DownloadCameraWizardStep5Text4", "Camera: "},
            {"DownloadCameraWizardStep5Text5", "Adding to new folder: "},
            {"DownloadCameraWizardStep5Text6", "Adding to folder: "},
            {"DownloadCameraWizardStep5Text7", "Storing in: "},
            {"DownloadCameraWizardStep6Title", "Download"},
            {"DownloadCameraWizardStep6Description", "Downloading the pictures"},

            //Privacy Dialog
            {"PrivacyTitle", "Privacy Settings"},
            {"PrivacyClearRecentFiles", "Clear Recent Files"},
            {"PrivacyClearThumbnails", "Clear Thumbnails"},
            {"PrivacyClearAutoload", "Clear Autoload"},
            {"PrivacyClearMemorisedDirs", "Clear Memorised Directories"},
            {"PrivacySelected", "Do Selected"},
            {"PrivacyClose", "Close Window"},
            {"PrivacyAll", "All"},
            {"PrivacyClear", "clear"},
            {"PrivacyTumbProgBarTitle", "Deleting Thumbnails"},
            {"PrivacyTumbProgBarDone", "%d Thumbnails Deleted"},


            {"jpo.dataModel.XmlReader.loadProgressGuiTitle", "Loading File"},
            {"jpo.dataModel.XmlReader.progressUpdate", "Loaded: %d Groups, %d Pictures"}


    };
}

