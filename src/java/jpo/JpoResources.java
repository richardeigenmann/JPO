package jpo;

import java.util.*;

/*
JpoResources.java:  class that holds the generic labels for the JPO application

Copyright (C) 2002  Richard Eigenmann.
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
 *  class that holds the generic labels for the JPO application.
 *  Use the following command to access your Strings:
 *  Settings.jpoResources.getString("key")
 */
public class JpoResources extends ListResourceBundle {
	public Object[][] getContents() {
		return contents;

	}
	static final Object[][] contents = {
		// Jpo
		{"ApplicationTitle", "JPO - Java Picture Organizer"},  
		
	
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
		{"internalError", "or"},
		{"genericWarning", "Warning"},
		{"genericExit", "Exit"},
		{"outOfMemoryError", "An out of Memory error has occured"},
		{"areYouSure", "Are you sure?"},
		
		
		
		// Help About Dialog
		{"HelpAboutText", "JPO Version 0.8.5 is a Java/Swing program\n" 
			+ "written by Richard Eigenmann, Zrich, Switzerland\n" 
			+ "Copyright 2000 - 2004\n"
			+ "richard_eigenmann@compuserve.com\n"
			+ "http://j-po.sourceforge.net\n"
			+ "\nThe Exif extration is courtesy of Drew Noakes\n"
			+  "The Table sorter is courtesy of Philip Milne\n\n"},
		{"HelpAboutUser", "User: " },
		{"HelpAboutOs", "Operating System: " },
		{"HelpAboutJvm", "JVM: " },
		{"HelpAboutJvmMemory", "JVM Max Memory: " },
		{"HelpAboutJvmFreeMemory", "JVM Free Memory: " },


		// QueryJFrame
		{"searchDialogTitle", "Find Pictures"},
		{"searchDialogLabel", "Search for:"},
		{"searchDialogSaveResultsLabel", "save results"},
		{"advancedFindJButtonOpen", "Advanced Criteria"},
		{"advancedFindJButtonClose", "Simple search"},
		{"noSearchResults", "There were no pictures matching the criteria."},
		{"lowerDateJLabel", "Between:"},
		{"dateRangeError", "Sorry, the date range makes no sense."},
		


		
		// PictureViewer Texts
		{"PictureViewerTitle", "JPO Picture Browser"},
		{"PictureViewerKeycodes", "The following keyboard shortcuts can be used:\n" 
			+ "N: Next Image\n"
			+ "P: Previous Image\n"
			+ "I: Show Information on | off\n"
			+ "<space>,<home>: Resize to fit\n"
			+ "<left>,<right>,<up>,<down>: scroll image\n"
			+ "<PgUp>: Zoom In\n"
			+ "<PgDown>: Zoom Out\n"
			+ "1: Zoom to 100%\n"
			+ "F: Window size menu\n"
			+ "M: Popup menu"},
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

		// SettingsDialog Texts
		{"settingsDialogTitle", "Edit Settings"},
		
		{"browserWindowSettingsJPanel", "General"},
		{"languageJLabel", "Language:"},
		{"autoLoadJLabelLabel", "Automatically load:"},
		{"logfileJCheckBoxLabel", "Write logfile"},
		{"logfileJLabelLabel", "Logfile path and filename:"},
		{"saveSizeJCheckBoxLabel", "Save Window Position on Exit"},
		{"MainCoordinates", "Coordinates of main window (x/y):"},
		{"MainSize", "Size of main window (w/h):"},
		
		{"pictureViewerJPanel", "Picture Viewer"},
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
		{"thumbnailDirError", "Something is seriousely wrong with your thumbnail directory"},

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
		{"emailUserJLabel", "Username:"},
		{"emailPasswordJLabel", "Password:"},
		
		
		
		// Settings
		{"thumbNoExistError", "The directory for the thumbnails doesn't exist.\nPlease select Edit | Settings to set this correctly.\nThumbnail caching has been turned off."},
		{"thumbNoWriteError", "The directory for the thumbnails is not writeable.\nPlease select Edit | Settings to set this correctly.\nThumbnail caching has been turned off."},
		{"thumbNoDirError", "The location for the thumbnails is not a directory.\nPlease select Edit | Settings to set this correctly.\nThumbnail caching has been turned off."},
		{"logFileCanWriteError", "The logfile in the Settings is not writeable.\nPlease select Edit | Settings to set this correctly.\nLogging has been disabled"},
		{"logFileIsFileError", "The location for the logfile is not a File.\nPlease select Edit | Settings to set this to a file.\nLogging has been disabled"},
		{"generalLogFileError", "There is a problem with the logfile. Logging was disabled."},
		{"cantWriteIniFile", "Error while writing ini file:\n"},
		{"cantReadIniFile", "Could not read JPO.ini. Using defaults.\n"},
		



		// HtmlDistillerJFrame
		{"HtmlDistillerJFrameHeading", "Export to HTML"},
		{"HtmlDistillerThreadTitle", "Extracting to HTML"}, 
		{"HtmlDistillerChooserTitle", "Select Target Directory for HTML"},
		{"exportHighresJCheckBox", "Export high resolution pictures"},
		{"linkToHighresJCheckBox", "Link to high resoloution pictures at current locations"},
		{"generateDHTMLJCheckBox", "Generate DHTML mouseover effects"},
		{"picsPerRowText", "Columns"},
		{"thubnailSizeJLabel", "Thumbnail size"},
		{"htmlDistCrtDirError", "Could not create Export directory!"},
		{"htmlDistIsDirError", "This is not a directory!"},
		{"htmlDistCanWriteError", "This is not a writable directory!"},
		{"htmlDistIsNotEmptyWarning", "The target Directory is not empty.\nClick OK to continue and possibly overwrite files."},
		{"midresSizeJLabel", "Midres size"},
		{"jpgQualitySlider", "Jpg Quality"},
		{"jpgQualityBad", "Bad"},
		{"jpgQualityGood", "Good"},
		{"jpgQualityBest", "Best"},
		// HtmlDistillerThread
		{"LinkToJpo", "Made with <A HREF=\"http://j-po.sourceforge.net\">JPO</A>"},
		{"htmlDistillerInterrupt", "interrupting gracefully"},
		{"CssCopyError", "Could not copy Stylesheet jpo.css\n"},

				

		// ReconcileJFrame
		{"ReconcileJFrameTitle", "Reconcile Directory vs Collection"},
		{"ReconcileBlaBlaLabel", "<HTML>This function will check whether the files in the indicated directory are present <br>in the current collection</htm>"},
		{"directoryJLabelLabel", "Directory to reconcile:"},
		{"directoryCheckerChooserTitle", "Directory to reconcile"},
		{"ReconcileFound", " found in collection as "},
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
		{"ConsolidateDirChooserTitle", "Select Consolidation Directory"},
		{"RecurseSubgroupsLabel", "Recurse Sub-Groups"},
		{"ConsolidateGroupBlaBlaLabel", "<HTML>This function will move all pictures of the selected group to the target directory. It will fix any ref-<br>erences to the images elsewhere in the collection. The files will be physically moved on the disk.<br><p> <font color=red>Are sure you want to do this?<br></font></htm>"},
		{"ConsolidateGroupJFrameHeading", "Consolidate / Move Pictures"},
		{"ConsolidateButton", "Consolidate"},
		{"ConsolidateFailure", "Consolidation generated a failure and has aborted."},
		{"ConsolitdateProgBarTitle", "Running Consolidation"},
		{"ConsolitdateProgBarDone", " pictures Consolidated"},
		{"lowresJCheckBox", "consolidate Lowres too"},
		

		
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
		{"commentLabel", "Comment:"},
		{"copyrightHolderLabel", "Copyright statment:"},
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
		{"groupEditJMenuItem", "Rename"},
		{"groupRefreshJMenuItem", "Refresh Icon"},
		{"groupTableJMenuItemLabel", "Edit as Table"},
		{"addGroupJMenuLabel", "Add"},
		{"addNewGroupJMenuItemLabel", "New Group"},
		{"addPicturesJMenuItemLabel", "Pictures"},
		{"addCollectionJMenuItemLabel", "Collection"},
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
		{"groupExportHtmlMenuText", "Export to HTML"},
		{"groupExportFlatFileMenuText", "Export to Flat File"},
		{"groupExportJarMenuText", "Export to Jar Archive"},
		
		
		// PicturePopupMenu
		{"pictureShowJMenuItemLabel", "Show Picture"},
		{"pictureEditJMenuItemLabel", "Properties"},
		{"copyImageJMenuLabel", "Copy Image"},
		{"copyToNewLocationJMenuItem", "choose target directory"},
		{"FileOperations", "File operations"},
		{"fileRenameJMenuItem", "Rename"},
		{"FileRenameLabel1", "Rename \n"},
		{"FileRenameLabel2", "\nto: "},
		{"fileDeleteJMenuItem", "Delete"},
		{"pictureRefreshJMenuItem", "Refresh Thumbnail"},
		{"pictureMailSelectJMenuItem", "Select for email"},
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


		// ThumbnailJScrollPane
		{"ThumbnailSearchResults", "Search Results for "},
		{"ThumbnailSearchResults2", " in "},

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


		// CleverJTree
		{"DefaultRootNodeText", "New Collection"},
		{"CopyImageDialogButton", "Copy"},
		{"CopyImageDialogTitle", "Specify target for: "},
		{"CopyImageNullError", "validateAndCopyPicture invoked with null arguments! Copy aborted."},
		{"CopyImageDirError", "Target directory can't be created. Copy aborted.\n"},
		{"fileOpenButtonText", "Open"},
		{"fileOpenHeading", "Open Collection"},
		{"fileSaveAsTitle", "Save Collection As"},
		{"collectionSaveTitle", "Collection Saved"},
		{"collectionSaveBody", "Collection Saved as:\n"},
		{"addSinglePictureTitle", "Select picture to add"},
		{"addSinglePictureButtonLabel", "Select"},
		{"addFlatFileTitle", "Choose Flat File Picture List"},
		{"saveFlatFileTitle", "Save List of Pictures as Flat File"},
		{"saveFlatFileButtonLabel", "Save"},
		{"moveNodeError", "Target is decendant of source. Move aborted."},
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
		{"FileCameraJMenuItem", "Add from Camera"},
		{"FileSaveMenuItemText", "Save Collection"},
		{"FileSaveAsMenuItemText", "Save As"},
		{"FileExitMenuItemText", "Exit"},
		{"EditJMenuText", "Edit"},
		{"EditFindJMenuItemText", "Find"},
		{"EditCheckDirectoriesJMenuItemText", "Reconcile"},
		{"EditCollectionPropertiesJMenuItem", "Collection Properties"},
		{"EditCheckIntegrityJMenuItem", "Check Integrity"},
		{"EditCamerasJMenuItem", "Cameras"},
		{"EditCategoriesJMenuItem", "Categories"},
		{"EditSettingsMenuItemText", "Settings"},
		{"actionJMenu", "Action"},
		{"emailJMenuItem", "Send email"},
		{"HelpJMenuText", "Help"},
		{"HelpAboutMenuItemText", "About"},
		{"HelpLicenseMenuItemText", "License"},
		
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
		{"recurseSubdirectoriesTitle", "Recurse Sbdirectories"},
		{"recurseSubdirectoriesMessage", "There are nested subdirectories in your selection.\nDo you want to add these too?"},
		{"recurseSubdirectoriesOk", "Add"},
		{"recurseSubdirectoriesNo", "No"},
		{"picturesAdded", " pictures Added"},

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
		
		// CameraEditor
		{"CameraEditor", "Edit Camera Settings"},
		{"cameraNewNameJLabel", "New Name"},
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

		// PictureAdder
		{"recurseJCheckBox", "Recurse subdirectories"},
		{"newOnlyJCheckBox", "Add new pictures only"},
		{"showThumbnailJCheckBox", "Show Thumbnail"},

		// IntegrityChecker
		{"IntegrityCheckerTitle", "Checking Collection Integrity"},
		{"integrityCheckerLabel", "Checking the integrity:"},
		{"check1", "Check Date Parsing"},
		{"check1done", "Dates that could not be parsed: "},
		{"check2", "Verify Checksums"},
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


		// CategoryEditorJFrame
		{"CategoryEditorJFrameTitle", "Cateogry Editor"},
		{"categoryJLabel", "Category"},
		{"categoriesJLabel", "Categories"},
		{"addCateogryJButton", "Add Category"},
		{"deleteCateogryJButton", "Remove Category"},
		{"renameCateogryJButton", "Rename Category"},
		{"doneJButton", "Done"},
		{"countCategoryUsageWarning1", "There are "},
		{"countCategoryUsageWarning2", " nodes using this category.\nAre you sure you want to remove it?"},

		
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

				
		{"Template", "Template"},
		{"Template", "Template"}
		
		
		};
	}

