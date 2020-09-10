package org.jpo.datamodel;

import com.google.common.collect.EvictingQueue;
import org.jpo.eventbus.GenerateWebsiteRequest;
import org.jpo.gui.swing.MainWindow;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/*
 * Copyright (C) 2002 - 2019 Richard Eigenmann, Zürich, Switzerland This program
 * is free software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place
 * - Suite 330, Boston, MA 02111-1307, USA. The license is in gpl.txt. See
 * http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * This class defines all the settings for the JPO application. In order for it
 * to be valid for all objects it's components are all static as is the Settings
 * object itself when created.
 * <p>
 * The SettingsDialog is the editor for these settings.
 */
public class Settings {


    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(Settings.class.getName());

    /**
     * A static reference to the Collection being displayed. In future perhaps
     * we will allow multiple collections to be loaded.
     */
    private static PictureCollection pictureCollection = new PictureCollection();

    /**
     * @return the main pictureCollection
     */
    public static PictureCollection getPictureCollection() {
        return pictureCollection;
    }

    /**
     * @param pictureCollection the pictureCollection to set
     */
    public static void setPictureCollection(PictureCollection pictureCollection) {
        Settings.pictureCollection = pictureCollection;
    }

    public static boolean isWriteLog() {
        return writeLog;
    }

    /**
     * flag to indicate that debug information should be logged
     */
    private static boolean writeLog = false;

    /**
     * the filename of the logfile
     */
    private static File logfile = new File(new File(System.getProperty("java.io.tmpdir")), "JPO.log");

    public static boolean isMaximiseJpoOnStartup() {
        return maximiseJpoOnStartup;
    }

    public static void setMaximiseJpoOnStartup(boolean maximiseJpoOnStartup) {
        Settings.maximiseJpoOnStartup = maximiseJpoOnStartup;
    }

    /**
     * Flag to indicate whether the JPO window should be maximised on startup or
     * left for the OS to decide on the size together with the JVM
     */
    private static boolean maximiseJpoOnStartup = true;

    public static Dimension getMainFrameDimensions() {
        return mainFrameDimensions;
    }

    public static void setMainFrameDimensions(Dimension mainFrameDimensions) {
        Settings.mainFrameDimensions = mainFrameDimensions;
    }


    /**
     * A set of window sizes that the user can choose his preferred size from.
     * The first option will be to maximise the window
     */
    private static final Dimension[] windowSizes = {new Dimension(0, 0), new Dimension(1050, 760), new Dimension(1250, 900), new Dimension(1450, 1190), new Dimension(2150, 1300)};

    /**
     * the dimensions of the main JPO frame
     */
    private static Dimension mainFrameDimensions = new Dimension(windowSizes[4]);


    public static Dimension[] getWindowSizes() {
        return windowSizes;
    }

    /**
     * Flag to indicate whether the JPO window should be maximised on startup or
     * left for the OS to decide on the size together with the JVM
     */
    public static boolean maximisePictureViewerWindow = true;

    /**
     * the dimensions of the "Default" picture viewer
     */
    public static Dimension pictureViewerDefaultDimensions = new Dimension(windowSizes[1]);

    /**
     * variable to indicate that the window size should be stored when the
     * application closes.
     */
    public static boolean saveSizeOnExit;

    /**
     * the default place for the divider.
     */
    public static int preferredMasterDividerSpot = 350;

    /**
     * the default place for the left side divider.
     */
    public static int preferredLeftDividerSpot = mainFrameDimensions.height - 200;
    static {
        if (preferredLeftDividerSpot < 0) {
            preferredLeftDividerSpot = 150;
        }
    }

    /**
     * the default width of the divider
     */
    public static int dividerWidth = 12;

    /**
     * the default value for maxThumbnails
     */
    public final static int DEFAULT_MAX_THUMBNAILS = 50;

    /**
     * a variable that sets the maximum number of thumbnails that shall be
     * displayed at one time.
     */
    public static int maxThumbnails = DEFAULT_MAX_THUMBNAILS;

    /**
     * Setting for the width of the thumbnails. Set by default to 350 pixels.
     */
    public static int thumbnailSize = 350;

    /**
     * the dimension of mini thumbnails in the group folders
     */
    public static final Dimension miniThumbnailSize = new Dimension(100, 75);

    /**
     * The minimum width for the left panels
     */
    public static final int LEFT_PANEL_MINIMUM_WIDTH = 300;
    /**
     * the minimum Dimension for the InfoPanel
     */
    public static final Dimension INFO_PANEL_MINIMUM_DIMENSION = new Dimension(LEFT_PANEL_MINIMUM_WIDTH, 100);
    /**
     * the preferred Dimension for the InfoPanel
     */
    public static final Dimension INFO_PANEL_PREFERRED_SIZE = new Dimension(LEFT_PANEL_MINIMUM_WIDTH, 100);
    /**
     * the minimum Dimension for the Navigator Panel
     */
    public static final Dimension JPO_NAVIGATOR_JTABBEDPANE_MINIMUM_SIZE = new Dimension(LEFT_PANEL_MINIMUM_WIDTH, 450);
    /**
     * the preferred Dimension for the Navigator Panel
     */
    public static final Dimension jpoNavigatorJTabbedPanePreferredSize = new Dimension(preferredMasterDividerSpot, 800);
    /**
     * the minimum Dimension for the Thumbnail Panel
     */
    public static final Dimension THUMBNAIL_JSCROLLPANE_MINIMUM_SIZE = new Dimension((int) (thumbnailSize * 1.4f), (int) (thumbnailSize * 1.8f));
    /**
     * the preferred Dimension for the Thumbnail Panel
     */
    public static final Dimension thumbnailJScrollPanePreferredSize = new Dimension((int) (thumbnailSize * 2.2f), 800);
    /**
     * the minimum Dimension for the JPO Window
     */
    public static final Dimension jpoJFrameMinimumSize
            = new Dimension(JPO_NAVIGATOR_JTABBEDPANE_MINIMUM_SIZE.width + dividerWidth + THUMBNAIL_JSCROLLPANE_MINIMUM_SIZE.width,
            Math.max(JPO_NAVIGATOR_JTABBEDPANE_MINIMUM_SIZE.height + dividerWidth + INFO_PANEL_MINIMUM_DIMENSION.height,
                    THUMBNAIL_JSCROLLPANE_MINIMUM_SIZE.height));
    /**
     * the preferred Dimension for the JPO Window
     */
    public static final Dimension jpoJFramePreferredSize
            = new Dimension(jpoNavigatorJTabbedPanePreferredSize.width + dividerWidth + thumbnailJScrollPanePreferredSize.width,
            Math.max(jpoNavigatorJTabbedPanePreferredSize.height + dividerWidth + INFO_PANEL_PREFERRED_SIZE.height,
                    thumbnailJScrollPanePreferredSize.height));
    /**
     * The polling interval in milliseconds for the ThumbnailCreationThreads to
     * check Whether there is something new to render.
     */
    public static final int THUMBNAIL_CREATION_THREAD_POLLING_TIME = 200;
    /**
     * The number of thumbnail creation threads to spawn.
     */
    public static final int NUMBER_OF_THUMBNAIL_CREATION_THREADS = 5;
    /**
     * The KDE Panel has the unfortunate habit of insisting on being on top so
     * this parameter allows you to specify how much space should be left from
     * the bottom of the screen for Full screen windows.
     */
    public static int leaveForPanel;
    /**
     * The collection that should be loaded automatically
     */
    public static String autoLoad;

    /**
     * Method to clear the autoload collection.
     */
    public static void clearAutoLoad() {
        autoLoad = "";
    }

    /**
     * number of recent files shown in the file menu
     */
    public static final int MAX_MEMORISE = 9;

    public static String[] getRecentCollections() {
        return recentCollections;
    }

    /**
     * Array of recently used files
     */
    private static final String[] recentCollections = new String[MAX_MEMORISE];
    /**
     * A counter that keeps track of the number of thumbnails created
     */
    public static int thumbnailCounter = 0;
    /**
     * a flag that indicates that small images should not be enlarged
     */
    public static boolean dontEnlargeSmallImages = true;

    /**
     * the path to the jar file; derived from jarAutostartList
     */
    public static String jarRoot = null;

    public static boolean isUnsavedSettingChanges() {
        return unsavedSettingChanges;
    }

    public static void setUnsavedSettingChanges(boolean unsavedSettingChanges) {
        Settings.unsavedSettingChanges = unsavedSettingChanges;
    }

    /**
     * variable that tracks if there are unsaved changes in these settings.
     */
    private static boolean unsavedSettingChanges = false;
    /**
     * URL of the document type definition in the xml file.
     */
    public static final String COLLECTION_DTD = "file:./collection.dtd";
    /**
     * handle to the main frame of the application. It's purpose it to have a
     * handy reference for dialog boxes and the like to have a reference object.
     */
    public static JFrame anchorFrame = null;
    /**
     * The maximum number of pictures to keep in memory
     * <p>
     * public static int maxCache;
     */

    public static String thumbnailCacheDirectory = System.getProperty("java.io.tmpdir")
            + System.getProperty("file.separator")
            + "Jpo-Thumbnail-Cache";


    /**
     * The maximum size a picture is zoomed to. This is to stop the Java engine
     * creating enormous temporary images which lock the computer up completely.
     */
    public static int maximumPictureSize = 6000;

    /**
     * standard size for all JTextFields that need to record a filename.
     */
    public static final Dimension filenameFieldPreferredSize = new Dimension(300, 20);
    /**
     * standard size for all JTextFields that need to record a filename
     */
    public static final Dimension filenameFieldMinimumSize = new Dimension(150, 20);
    /**
     * standard size for all JTextFields that need to record a filename
     */
    public static final Dimension filenameFieldMaximumSize = new Dimension(600, 20);
    /**
     * standard size for all JTextFields that need to record a short text.
     */
    public static final Dimension shortFieldPreferredSize = new Dimension(350, 20);
    /**
     * standard size for all JTextFields that need to record a short text
     */
    public static final Dimension shortFieldMinimumSize = new Dimension(150, 20);
    /**
     * standard size for all JTextFields that need to record a short text
     */
    public static final Dimension shortFieldMaximumSize = new Dimension(1000, 20);
    /**
     * standard size for all JTextFields that need to record a normal length
     * text
     */
    public static final Dimension textfieldPreferredSize = new Dimension(350, 20);
    /**
     * standard size for all JTextFields that need to record a normal length
     * text
     */
    public static final Dimension textfieldMinimumSize = new Dimension(150, 20);
    /**
     * standard size for all JTextFields that need to record a normal length
     * text
     */
    public static final Dimension textfieldMaximumSize = new Dimension(1000, 20);
    /**
     * standard size for all JTextFields that need to record a normal length
     * text
     */
    public static final Dimension shortNumberPreferredSize = new Dimension(60, 20);
    /**
     * standard size for all JTextFields that need to record a normal length
     * text
     */
    public static final Dimension shortNumberMinimumSize = new Dimension(60, 20);
    /**
     * standard size for all JTextFields that need to record a normal length
     * text
     */
    public static final Dimension shortNumberMaximumSize = new Dimension(100, 20);
    /**
     * fixed size for the threeDotButton which opens the JFileChooser dialog
     */
    public static final Dimension threeDotButtonSize = new Dimension(25, 20);
    /**
     * the font used to display the title. Currently Arial Bold 20.
     */
    public static Font titleFont;
    /**
     * the font used to display the captions. Currently Arial Plain 16
     */
    public static Font captionFont;
    /**
     * the height of the Thumbnail descriptions
     */
    public static final int THUMBNAIL_DESCRIPTION_HEIGHT = 200;
    /**
     * The interval between the timer checking to see if the picture is ready
     * before the main delay loop should be waited. You want to give the user
     * the specified seconds to look at the picture and not subtract from that
     * the time it took the program to load the image.
     */
    public static final int ADVANCE_TIMER_POLLING_INTERVAL = 500;
    /**
     * The default number of pictures per row for the Html export
     */
    public static int defaultHtmlPicsPerRow = 3;
    /**
     * The default width for pictures for the Html export overview
     */
    public static int defaultHtmlThumbnailWidth = 300;
    /**
     * The default height for pictures for the Html export overview
     */
    public static int defaultHtmlThumbnailHeight = 300;
    /**
     * Whether to generate the midres html pages or not
     */
    public static boolean defaultGenerateMidresHtml = true;
    /**
     * Whether to generate a map or not
     */
    public static boolean defaultGenerateMap = true;
    /**
     * Whether to generate DHTML effects or not
     */
    public static boolean defaultGenerateDHTML = true;
    /**
     * Whether to generate a zip file with the highres pictures
     */
    public static boolean defaultGenerateZipfile = false;
    /**
     * Whether to generate a link to highres pictures at the current location or
     * not
     */
    public static boolean defaultLinkToHighres = false;
    /**
     * Whether to export the Highres pictures or not
     */
    public static boolean defaultExportHighres = false;
    /**
     * Whether to rotate the Highres pictures or not
     */
    public static boolean defaultRotateHighres = false;
    /**
     * The default midres width for pictures for the Html export
     */
    public static int defaultHtmlMidresWidth = 700;
    /**
     * The default midres height for pictures for the Html export
     */
    public static int defaultHtmlMidresHeight = 700;
    /**
     * Picture naming convention on HTML output
     */
    public static GenerateWebsiteRequest.PictureNamingType defaultHtmlPictureNaming = GenerateWebsiteRequest.PictureNamingType.PICTURE_NAMING_BY_HASH_CODE;
    /**
     * OutputTarget convention for HTML output
     */
    public static GenerateWebsiteRequest.OutputTarget defaultHtmlOutputTarget = GenerateWebsiteRequest.OutputTarget.OUTPUT_LOCAL_DIRECTORY;
    /**
     * The default color for the background on the web page is white.
     */
    public static Color htmlBackgroundColor = Color.WHITE;
    /**
     * This constant defines the text color on the web page.
     */
    public static Color htmlFontColor = Color.BLACK;
    /**
     * The default quality for Thumbnail pictures for the Html export
     */
    public static float defaultHtmlLowresQuality = 0.8f;
    /**
     * The default quality for Midres pictures for the Html export
     */
    public static float defaultHtmlMidresQuality = 0.8f;
    /**
     * Whether to write the robots.txt on the generate webpage
     */
    public static boolean writeRobotsTxt = false;
    /**
     * The default ftp server for Html export
     */
    public static String defaultHtmlFtpServer = "";
    /**
     * The default ftp port for Html export
     */
    public static int defaultHtmlFtpPort = 21;
    /**
     * The default ftp user for Html export
     */
    public static String defaultHtmlFtpUser = "";
    /**
     * The default ftp password for Html export
     */
    public static String defaultHtmlFtpPassword = "";
    /**
     * The default ftp target directory for Html export
     */
    public static String defaultHtmlFtpTargetDir = "";
    /**
     * The default ssh server for Html export
     */
    public static String defaultHtmlSshServer = "";
    /**
     * The default ssh port for Html export
     */
    public static int defaultHtmlSshPort = 22;
    /**
     * The default ssh user for Html export
     */
    public static String defaultHtmlSshUser = "";
    /**
     * OutputTarget convention for HTML output
     */
    public static GenerateWebsiteRequest.SshAuthType defaultHtmlSshAuthType = GenerateWebsiteRequest.SshAuthType.SSH_AUTH_PASSWORD;
    /**
     * The default ssh password for Html export
     */
    public static String defaultHtmlSshPassword = "";
    /**
     * The default ssh target directory for Html export
     */
    public static String defaultHtmlSshTargetDir = "";
    /**
     * The default ssh key file for Html export
     */
    public static String defaultHtmlSshKeyFile = "";
    /**
     * true when thumbnails are supposed to scale fast
     */
    public static boolean thumbnailFastScale = true;
    /**
     * true when the pictureViewer is supposed to scale fast
     */
    public static boolean pictureViewerFastScale = true;
    /**
     * Informs the PictureAdder whether to show a thumbnail or not
     */
    public static boolean showThumbOnFileChooser = true;
    /**
     * Default size for buttons such as OK, cancel etc.
     */
    public static final Dimension defaultButtonDimension = new Dimension(80, 25);
    /**
     * Default size for buttons such as OK, cancel etc.
     */
    public static final Dimension threeDotButtonDimension = new Dimension(25, 25);

    /**
     * Codes to indicate the field
     */
    public enum FieldCodes {

        NO_SORTING,
        DESCRIPTION,
        FILE_URL,
        FILE_LOWRES_URL,
        FILM_REFERENCE,
        CREATION_TIME,
        COMMENT,
        PHOTOGRAPHER,
        COPYRIGHT_HOLDER,
        ROTATION,
        LATLNG,
        CHECKSUM,
        CATEGORIES,
        CATEGORY,
        CATEGORY_DESCRIPTION,
    }

    /**
     * returns an List of SortOptions
     *
     * @return the List of sort options
     */
    public static List<SortOption> getSortOptions() {
        List<SortOption> sortOptions = new ArrayList<>();
        sortOptions.add(new SortOption("No Sorting", FieldCodes.NO_SORTING));
        sortOptions.add(new SortOption(Settings.jpoResources.getString("sortByDescriptionJMenuItem"), FieldCodes.DESCRIPTION));
        sortOptions.add(new SortOption(Settings.jpoResources.getString("sortByFilmReferenceJMenuItem"), FieldCodes.FILM_REFERENCE));
        sortOptions.add(new SortOption(Settings.jpoResources.getString("sortByCreationTimeJMenuItem"), FieldCodes.CREATION_TIME));
        sortOptions.add(new SortOption(Settings.jpoResources.getString("sortByCommentJMenuItem"), FieldCodes.COMMENT));
        sortOptions.add(new SortOption(Settings.jpoResources.getString("sortByPhotographerJMenuItem"), FieldCodes.PHOTOGRAPHER));
        sortOptions.add(new SortOption(Settings.jpoResources.getString("sortByCopyrightHolderTimeJMenuItem"), FieldCodes.COPYRIGHT_HOLDER));
        return sortOptions;
    }

    /**
     * The color to use when the thumbnail has been selected
     */
    public static final Color SELECTED_COLOR = new Color(45, 47, 84);

    /**
     * The color to use for text background when the thumbnail has been selected
     */
    public static final Color SELECTED_COLOR_TEXT = new Color(145, 149, 153);

    /**
     * The color to use when the thumbnail has been selected
     */
    public static final Color UNSELECTED_COLOR = Color.WHITE;

    /**
     * date format for adding new pictures from the camera
     */
    public static final String ADD_FROM_CAMERA_DATE_FORMAT = "dd.MM.yyyy  HH:mm";

    /**
     * Collection of cameras
     */
    public static List<Camera> cameras = new ArrayList<>();

    public static SortedSet<Object> getEmailSenders() {
        return emailSenders;
    }

    /**
     * list of email senders
     */
    private static final TreeSet<Object> emailSenders = new TreeSet<>() {
        @Override
        public boolean add(Object o) {
            boolean b = super.add(o);
            if (b) {
                unsavedSettingChanges = true;
            }
            return b;
        }
    };

    public static SortedSet<Object> getEmailRecipients() {
        return emailRecipients;
    }

    /**
     * list of email senders
     */
    private static final TreeSet<Object> emailRecipients = new TreeSet<>() {
        @Override
        public boolean add(Object o) {
            boolean b = super.add(o);
            if (b) {
                unsavedSettingChanges = true;
            }
            return b;
        }
    };
    /**
     * Email Server
     */
    public static String emailServer = "";
    /**
     * Email Server port
     */
    public static String emailPort = "25";
    /**
     * Email authentication 0 = None 1 = Password 2 = SSL
     */
    public static int emailAuthentication = 0;
    /**
     * Email User
     */
    public static String emailUser = "";
    /**
     * Email Password
     */
    public static String emailPassword = "";
    /**
     * Should emails have scaled images
     */
    public static boolean emailScaleImages = true;
    /**
     * The last size we scaled images to in the email dialog
     */
    public static Dimension emailDimensions = new Dimension(350, 300);
    /**
     * Should emails contain the original images
     */
    public static boolean emailSendOriginal = false;
    /**
     * The default application background color.
     */
    public static final Color JPO_BACKGROUND_COLOR = Color.WHITE;
    /**
     * The background color for the picture Viewer
     */
    public static final Color PICTUREVIEWER_BACKGROUND_COLOR = Color.BLACK;
    /**
     * The text color for the picture Viewer
     */
    public static final Color PICTUREVIEWER_TEXT_COLOR = Color.WHITE;
    /**
     * The PictureViewer minimum size
     */
    public static final Dimension PICTUREVIEWER_MINIMUM_SIZE = new Dimension(300, 300);

    /**
     * The number of Words that the TagCloud should show.
     */
    public static int tagCloudWords = 200;
    /**
     * The last sort choice of the user
     */
    public static FieldCodes lastSortChoice = FieldCodes.CREATION_TIME;
    /**
     * The last choice in the Camera Download Wizard whether to copy or move
     */
    public static boolean lastCameraWizardCopyMode = true;
    /**
     * Whether to remember the Google login credentials for the Picasa upload.
     */
    public static boolean rememberGoogleCredentials = false;
    /**
     * Google user name if rememberGoogleCredentials is true
     */
    public static String googleUsername;
    /**
     * Google password if rememberGoogleCredentials is true;
     */
    public static String googlePassword;

    /**
     * method that set the default parameters
     */
    public static void setDefaults() {
        setLocale(currentLocale);

        clearAutoLoad();
    }

    /**
     * handle to the user Preferences
     */
    public static final Preferences prefs = Preferences.userNodeForPackage(Settings.class);

    /**
     * This method reads the settings from the preferences.
     */
    public static void loadSettings() {
        setDefaults();

        setLocale(new Locale(prefs.get("currentLocale", getCurrentLocale().toString())));
        maximumPictureSize = prefs.getInt("maximumPictureSize", maximumPictureSize);
        maxThumbnails = prefs.getInt("maxThumbnails", maxThumbnails);
        thumbnailSize = prefs.getInt("thumbnailSize", thumbnailSize);
        maximiseJpoOnStartup = prefs.getBoolean("maximiseJpoOnStartup", maximiseJpoOnStartup);
        mainFrameDimensions.width = prefs.getInt("mainFrameDimensions.width", mainFrameDimensions.width);
        mainFrameDimensions.height = prefs.getInt("mainFrameDimensions.height", mainFrameDimensions.height);
        preferredMasterDividerSpot = prefs.getInt("preferredMasterDividerSpot", preferredMasterDividerSpot);
        preferredLeftDividerSpot = prefs.getInt("preferredLeftDividerSpot", preferredLeftDividerSpot);

        dividerWidth = prefs.getInt("dividerWidth", dividerWidth);
        autoLoad = prefs.get("autoload", autoLoad);

        maximisePictureViewerWindow = prefs.getBoolean("maximisePictureViewerWindow", maximisePictureViewerWindow);
        pictureViewerDefaultDimensions.width = prefs.getInt("pictureViewerDefaultDimensions.width", pictureViewerDefaultDimensions.width);
        pictureViewerDefaultDimensions.height = prefs.getInt("pictureViewerDefaultDimensions.height", pictureViewerDefaultDimensions.height);

        for (int i = 0; i < MAX_MEMORISE; i++) {
            String key = "copyLocations-" + i;
            String loc = prefs.get(key, null);
            if (loc != null) {
                copyLocations.add(loc);
            }
        }
        for (int i = 0; i < Settings.MAX_MEMORISE; i++) {
            recentCollections[i] = prefs.get("recentCollections-" + i, null);
        }
        for (int i = 0; i < Settings.MAX_USER_FUNCTIONS; i++) {
            userFunctionNames[i] = prefs.get("userFunctionName-" + i, null);
            userFunctionCmd[i] = prefs.get("userFunctionCmd-" + i, null);
        }
        dontEnlargeSmallImages = prefs.getBoolean("dontEnlargeSmallImages", dontEnlargeSmallImages);
        thumbnailCounter = prefs.getInt("thumbnailCounter", thumbnailCounter);
        writeLog = prefs.getBoolean("writeLog", writeLog);
        logfile = new File(prefs.get("logfile", logfile.getPath())); // inefficient, RE, 11.11.2006
        thumbnailCacheDirectory = prefs.get("thumbnailCacheDirectory", thumbnailCacheDirectory);
        defaultHtmlPicsPerRow = prefs.getInt("defaultHtmlPicsPerRow", defaultHtmlPicsPerRow);
        defaultHtmlThumbnailWidth = prefs.getInt("defaultHtmlThumbnailWidth", defaultHtmlThumbnailWidth);
        defaultHtmlThumbnailHeight = prefs.getInt("defaultHtmlThumbnailHeight", defaultHtmlThumbnailHeight);
        defaultGenerateMidresHtml = prefs.getBoolean("defaultGenerateMidresHtml", defaultGenerateMidresHtml);
        String defaultHtmlPictureNamingString = prefs.get("defaultHtmlPictureNamingString", defaultHtmlPictureNaming.name());
        defaultHtmlPictureNaming = GenerateWebsiteRequest.PictureNamingType.valueOf(defaultHtmlPictureNamingString);
        String defaultHtmlOutputTargetString = prefs.get("defaultHtmlOutputTarget", defaultHtmlOutputTarget.name());
        defaultHtmlOutputTarget = GenerateWebsiteRequest.OutputTarget.valueOf(defaultHtmlOutputTargetString);
        String defaultHtmlSshAuthTypeString = prefs.get("defaultHtmlSshAuthType", defaultHtmlSshAuthType.name());
        defaultHtmlSshAuthType = GenerateWebsiteRequest.SshAuthType.valueOf(defaultHtmlSshAuthTypeString);

        defaultGenerateMap = prefs.getBoolean("defaultGenerateMap", defaultGenerateMap);
        defaultGenerateDHTML = prefs.getBoolean("defaultGenerateDHTML", defaultGenerateDHTML);
        defaultGenerateZipfile = prefs.getBoolean("defaultGenerateZipfile", defaultGenerateZipfile);
        defaultLinkToHighres = prefs.getBoolean("defaultLinkToHighres", defaultLinkToHighres);
        defaultExportHighres = prefs.getBoolean("defaultExportHighres", defaultExportHighres);
        defaultRotateHighres = prefs.getBoolean("defaultRotateHighres", defaultRotateHighres);
        defaultHtmlMidresWidth = prefs.getInt("defaultHtmlMidresWidth", defaultHtmlMidresWidth);
        defaultHtmlMidresHeight = prefs.getInt("defaultHtmlMidresHeight", defaultHtmlMidresHeight);
        defaultHtmlLowresQuality = prefs.getFloat("defaultHtmlLowresQuality", defaultHtmlLowresQuality);
        defaultHtmlMidresQuality = prefs.getFloat("defaultHtmlMidresQuality", defaultHtmlMidresQuality);
        writeRobotsTxt = prefs.getBoolean("writeRobotsTxt", writeRobotsTxt);
        defaultHtmlFtpServer = prefs.get("defaultHtmlFtpServer", defaultHtmlFtpServer);
        defaultHtmlFtpPort = prefs.getInt("defaultHtmlFtpPort", defaultHtmlFtpPort);
        defaultHtmlFtpUser = prefs.get("defaultHtmlFtpUser", defaultHtmlFtpUser);
        defaultHtmlFtpPassword = prefs.get("defaultHtmlFtpPassword", defaultHtmlFtpPassword);
        defaultHtmlFtpTargetDir = prefs.get("defaultHtmlFtpTargetDir", defaultHtmlFtpTargetDir);
        defaultHtmlSshServer = prefs.get("defaultHtmlSshServer", defaultHtmlSshServer);
        defaultHtmlSshPort = prefs.getInt("defaultHtmlSshPort", defaultHtmlSshPort);
        defaultHtmlSshUser = prefs.get("defaultHtmlSshUser", defaultHtmlSshUser);
        defaultHtmlSshPassword = prefs.get("defaultHtmlSshPassword", defaultHtmlSshPassword);
        defaultHtmlSshTargetDir = prefs.get("defaultHtmlSshTargetDir", defaultHtmlSshTargetDir);
        defaultHtmlSshKeyFile = prefs.get("defaultHtmlSshKeyFile", defaultHtmlSshKeyFile);
        thumbnailFastScale = prefs.getBoolean("thumbnailFastScale", thumbnailFastScale);
        pictureViewerFastScale = prefs.getBoolean("pictureViewerFastScale", pictureViewerFastScale);
        defaultLinkToHighres = prefs.getBoolean("showThumbOnFileChooser", showThumbOnFileChooser);
        int n = prefs.getInt("emailSenders", 0);
        for (int i = 0; i < n; i++) {
            emailSenders.add(prefs.get("emailSender-" + i, ""));
        }
        n = prefs.getInt("emailRecipients", 0);
        for (int i = 0; i < n; i++) {
            emailRecipients.add(prefs.get("emailRecipient-" + i, ""));
        }
        emailServer = prefs.get("emailServer", emailServer);
        emailPort = prefs.get("emailPort", emailPort);
        emailAuthentication = prefs.getInt("emailAuthentication", emailAuthentication);
        emailUser = prefs.get("emailUser", emailUser);
        emailPassword = prefs.get("emailPassword", emailPassword);
        emailScaleImages = prefs.getBoolean("emailScaleImages", emailScaleImages);
        emailSendOriginal = prefs.getBoolean("emailSendOriginal", emailSendOriginal);
        emailDimensions.width = prefs.getInt("emailDimensions.width", emailDimensions.width);
        emailDimensions.height = prefs.getInt("emailDimensions.height", emailDimensions.height);
        tagCloudWords = prefs.getInt("tagCloudWords", tagCloudWords);
        lastSortChoice = FieldCodes.valueOf(prefs.get("lastSortChoiceString", lastSortChoice.toString()));
        lastCameraWizardCopyMode = prefs.getBoolean("lastCameraWizardCopyMode", lastCameraWizardCopyMode);

        rememberGoogleCredentials = prefs.getBoolean("rememberGoogleCredentials", rememberGoogleCredentials);
        googleUsername = prefs.get("googleUsername", "");
        googlePassword = prefs.get("googlePassword", "");

        validateCopyLocations();
        validateSettings();

        loadCameraSettings();
    }

    /**
     * method that validates the settings &amp; brings up the Settings dialog if
     * not ok
     */
    public static void validateSettings() {
        if (maxThumbnails < 1) { //how can this happen?
            maxThumbnails = DEFAULT_MAX_THUMBNAILS;
        }

        if (writeLog) {
            if (logfile.exists()) {
                if (!logfile.canWrite()) {
                    JOptionPane.showMessageDialog(Settings.anchorFrame,
                            Settings.jpoResources.getString("logFileCanWriteError"),
                            Settings.jpoResources.getString("settingsError"),
                            JOptionPane.ERROR_MESSAGE);
                    writeLog = false;
                }
                if (!logfile.isFile()) {
                    JOptionPane.showMessageDialog(Settings.anchorFrame,
                            Settings.jpoResources.getString("logFileIsFileError"),
                            Settings.jpoResources.getString("settingsError"),
                            JOptionPane.ERROR_MESSAGE);
                    writeLog = false;
                }
            } else {
                File testFileParent = logfile.getParentFile();
                if (testFileParent == null) {
                    // the parent of root dir is null
                    JOptionPane.showMessageDialog(Settings.anchorFrame,
                            Settings.jpoResources.getString("logFileIsFileError"),
                            Settings.jpoResources.getString("settingsError"),
                            JOptionPane.ERROR_MESSAGE);
                    writeLog = false;
                } else if (!testFileParent.canWrite()) {
                    JOptionPane.showMessageDialog(Settings.anchorFrame,
                            Settings.jpoResources.getString("logFileCanWriteError"),
                            Settings.jpoResources.getString("settingsError"),
                            JOptionPane.ERROR_MESSAGE);
                    writeLog = false;
                }
            }

        }
    }

    /**
     * This method writes the settings to the Preferences object which was added
     * to Java with 1.4
     */
    public static void writeSettings() {
        prefs.put("currentLocale", getCurrentLocale().toString());
        prefs.putInt("maximumPictureSize", maximumPictureSize);
        prefs.putInt("maxThumbnails", maxThumbnails);
        prefs.putInt("thumbnailSize", thumbnailSize);
        prefs.putBoolean("maximiseJpoOnStartup", maximiseJpoOnStartup);
        prefs.putInt("mainFrameDimensions.width", mainFrameDimensions.width);
        prefs.putInt("mainFrameDimensions.height", mainFrameDimensions.height);
        prefs.putInt("preferredMasterDividerSpot", preferredMasterDividerSpot);
        prefs.putInt("preferredLeftDividerSpot", preferredLeftDividerSpot);
        prefs.putInt("dividerWidth", dividerWidth);
        if (autoLoad != null) {
            prefs.put("autoload", autoLoad);
        }
        prefs.putBoolean("maximisePictureViewerWindow", maximisePictureViewerWindow);
        prefs.putInt("pictureViewerDefaultDimensions.width", pictureViewerDefaultDimensions.width);
        prefs.putInt("pictureViewerDefaultDimensions.height", pictureViewerDefaultDimensions.height);
        final Iterator<String> iterator = copyLocations.iterator();
        for (int ordinal = 0; iterator.hasNext(); ordinal++) {
            prefs.put(String.format("copyLocations-%d", ordinal), iterator.next());
        }

        // recent collections
        int n = 0;
        for (int i = 0; i < Settings.MAX_MEMORISE; i++) {
            if (recentCollections[i] != null) {
                prefs.put(String.format("recentCollections-%d", n), recentCollections[i]);
                n++;
            }
        }
        for (int x = n; x < MAX_MEMORISE; x++) {
            prefs.remove(String.format("recentCollections-%d", x));
        }

        int i;
        n = 0;
        for (i = 0; i < Settings.MAX_USER_FUNCTIONS; i++) {
            if ((userFunctionNames[i] != null) && (userFunctionNames[i].length() > 0) && (userFunctionCmd[i] != null) && (userFunctionCmd[i].length() > 0)) {
                prefs.put("userFunctionName-" + n, userFunctionNames[i]);
                prefs.put("userFunctionCmd-" + n, userFunctionCmd[i]);
                n++;
            }
        }
        prefs.putBoolean("dontEnlargeSmallImages", dontEnlargeSmallImages);
        prefs.putInt("thumbnailCounter", thumbnailCounter);
        prefs.putBoolean("writeLog", writeLog);
        prefs.put("logfile", logfile.getPath());
        prefs.put("thumbnailCacheDirectory", thumbnailCacheDirectory);
        prefs.putInt("defaultHtmlPicsPerRow", defaultHtmlPicsPerRow);
        prefs.putInt("defaultHtmlThumbnailWidth", defaultHtmlThumbnailWidth);
        prefs.putInt("defaultHtmlThumbnailHeight", defaultHtmlThumbnailHeight);
        prefs.putBoolean("defaultGenerateMidresHtml", defaultGenerateMidresHtml);
        prefs.put("defaultHtmlPictureNamingString", defaultHtmlPictureNaming.name());
        prefs.put("defaultHtmlOutputTarget", defaultHtmlOutputTarget.name());
        prefs.putBoolean("defaultGenerateMap", defaultGenerateMap);
        prefs.putBoolean("defaultGenerateDHTML", defaultGenerateDHTML);
        prefs.putBoolean("defaultGenerateZipfile", defaultGenerateZipfile);
        prefs.putBoolean("defaultLinkToHighres", defaultLinkToHighres);
        prefs.putBoolean("defaultExportHighres", defaultExportHighres);
        prefs.putBoolean("defaultRotateHighres", defaultRotateHighres);
        prefs.putInt("defaultHtmlMidresWidth", defaultHtmlMidresWidth);
        prefs.putInt("defaultHtmlMidresHeight", defaultHtmlMidresHeight);
        prefs.putFloat("defaultHtmlLowresQuality", defaultHtmlLowresQuality);
        prefs.putFloat("defaultHtmlMidresQuality", defaultHtmlMidresQuality);
        prefs.putBoolean("writeRobotsTxt", writeRobotsTxt);
        prefs.put("defaultHtmlFtpServer", defaultHtmlFtpServer);
        prefs.putInt("defaultHtmlFtpPort", defaultHtmlFtpPort);
        prefs.put("defaultHtmlFtpUser", defaultHtmlFtpUser);
        prefs.put("defaultHtmlFtpPassword", defaultHtmlFtpPassword);
        prefs.put("defaultHtmlFtpTargetDir", defaultHtmlFtpTargetDir);
        prefs.put("defaultHtmlSshServer", defaultHtmlSshServer);
        prefs.putInt("defaultHtmlSshPort", defaultHtmlSshPort);
        prefs.put("defaultHtmlSshUser", defaultHtmlSshUser);
        prefs.put("defaultHtmlSshAuthType", defaultHtmlSshAuthType.name());
        prefs.put("defaultHtmlSshPassword", defaultHtmlSshPassword);
        prefs.put("defaultHtmlSshTargetDir", defaultHtmlSshTargetDir);
        prefs.put("defaultHtmlSshKeyFile", defaultHtmlSshKeyFile);

        prefs.putBoolean("thumbnailFastScale", thumbnailFastScale);
        prefs.putBoolean("pictureViewerFastScale", pictureViewerFastScale);
        prefs.putBoolean("showThumbOnFileChooser", showThumbOnFileChooser);
        n = 0;
        final Iterator<Object> senders = emailSenders.iterator();
        while (senders.hasNext()) {
            prefs.put("emailSender-" + n, (String) senders.next());
            n++;
        }
        prefs.putInt("emailSenders", n);
        n = 0;
        final Iterator<Object> recipients = emailRecipients.iterator();
        while (recipients.hasNext()) {
            prefs.put("emailRecipient-" + n, (String) recipients.next());
            n++;
        }
        prefs.putInt("emailRecipients", n);
        prefs.put("emailServer", emailServer);
        prefs.put("emailPort", emailPort);
        prefs.putInt("emailAuthentication", emailAuthentication);
        prefs.put("emailUser", emailUser);
        prefs.put("emailPassword", emailPassword);
        prefs.putBoolean("emailScaleImages", emailScaleImages);
        prefs.putBoolean("emailSendOriginal", emailSendOriginal);
        prefs.putInt("emailDimensions.width", emailDimensions.width);
        prefs.putInt("emailDimensions.height", emailDimensions.height);
        prefs.putInt("tagCloudWords", tagCloudWords);
        prefs.put("lastSortChoiceString", lastSortChoice.toString());
        prefs.putBoolean("lastCameraWizardCopyMode", lastCameraWizardCopyMode);

        prefs.putBoolean("rememberGoogleCredentials", rememberGoogleCredentials);
        if (rememberGoogleCredentials) {
            prefs.put("googleUsername", googleUsername);
            prefs.put("googlePassword", googlePassword);
        } else {
            prefs.put("googleUsername", "");
            prefs.put("googlePassword", "");
        }

        unsavedSettingChanges = false;
    }

    /**
     * Writes the cameras collection to the preferences. Uses an idea presented
     * by Greg Travis on this IBM website:
     * http://www-128.ibm.com/developerworks/java/library/j-prefapi.html
     */
    public static void writeCameraSettings() {
        prefs.putInt("NumberOfCameras", cameras.size());
        int i = 0;
        for (final Camera c : cameras) {
            final String camera = "Camera[" + i + "]";
            prefs.put(camera + ".description", c.getDescription());
            prefs.put(camera + ".cameraMountPoint", c.getCameraMountPoint());
            prefs.putBoolean(camera + ".useFilename", c.getUseFilename());
            prefs.putBoolean(camera + ".monitor", c.getMonitorForNewPictures());
            try {
                PrefObj.putObject(prefs, camera + ".oldImage", c.getOldImage());
            } catch (IOException | BackingStoreException ex) {
                LOGGER.severe(ex.getLocalizedMessage());
            }
            i++;
        }
    }

    /**
     * this method attempts to load the cameras
     */
    @SuppressWarnings("unchecked")
    public static void loadCameraSettings() {
        int numberOfCameras = prefs.getInt("NumberOfCameras", 0);
        for (int i = 0; i < numberOfCameras; i++) {
            final Camera camera = new Camera();
            final String CAMERA = "Camera[" + i + "]";
            camera.setDescription(prefs.get(CAMERA + ".description", "unknown"));
            camera.setCameraMountPoint(prefs.get(CAMERA + ".cameraMountPoint", FileSystemView.getFileSystemView().getHomeDirectory().toString()));
            camera.setUseFilename(prefs.getBoolean(CAMERA + ".useFilename", true));
            camera.setMonitorForNewPictures(prefs.getBoolean(CAMERA + ".monitor", true));

            camera.setOldImage(new HashMap<>());
            try {
                camera.setOldImage((HashMap) PrefObj.getObject(prefs, CAMERA + ".oldImage"));
            } catch (IOException | BackingStoreException | ClassNotFoundException ex) {
                LOGGER.severe(ex.getLocalizedMessage());
            }
            cameras.add(camera);
        }
    }

    /**
     * This method memorises a collection file name for the Open &gt; Recent
     * menu.
     * <p>
     * The caller should notify any listeners that the recentCollections changed
     * by sending a
     * {@code JpoEventBus.getInstance().post( new RecentCollectionsChangedEvent() ); }
     *
     * @param recentFile The collection file name to be memorised
     */
    public static void pushRecentCollection(final String recentFile) {
        for (int i = 0; i < Settings.MAX_MEMORISE; i++) {
            if ((recentCollections[i] != null)
                    && (recentCollections[i].equals(recentFile))) {
                // it was already in the list make it the first one
                System.arraycopy(recentCollections, 0, recentCollections, 1, i);
                recentCollections[0] = recentFile;
                return;
            }
        }

        // move all the elements down by one
        System.arraycopy(recentCollections, 0, recentCollections, 1, Settings.MAX_MEMORISE - 1);
        recentCollections[0] = recentFile;
        writeSettings();
    }

    /**
     * This method clears all the recent collection file names.
     * <p>
     * The caller should notify any listeners that the recentCollections changed
     * by sending a
     * {@code JpoEventBus.getInstance().post( new RecentCollectionsChangedEvent() ); }
     */
    public static void clearRecentCollection() {
        for (int i = 0; i < Settings.MAX_MEMORISE; i++) {
            recentCollections[i] = null;
            writeSettings();
        }
    }

    /*
     * ------------------------------------------------------------------------------
     *
     *
     *
     *
     * /**
     * Default locale if all else fails use this one.
     */
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    /**
     * The locale to be used for the application
     */
    private static Locale currentLocale = Locale.getDefault();

    /**
     * returns the Locale the application is running in.
     *
     * @return The locale set by environment, default, user or persistent
     * settings.
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * returns the Language the application is running in.
     *
     * @return The Language
     */
    public static String getCurrentLanguage() {
        return currentLocale.getDisplayLanguage();
    }

    public static String[] getSupportedLanguages() {
        return supportedLanguages;
    }

    public static Locale[] getSupportedLocale() {
        return supportedLocale;
    }

    /**
     * Supported Languages
     */
    private static final String[] supportedLanguages = {"English", "Deutsch", "Simplified Chinese", "Traditional Chinese"};
    /**
     * Locales for the languages in supportedLanguages
     */
    private static final Locale[] supportedLocale = {Locale.ENGLISH, Locale.GERMAN, Locale.SIMPLIFIED_CHINESE, Locale.TRADITIONAL_CHINESE};

    /**
     * Sets the new locale. As of 3 Apr 2014 this doesn't send a
     * LocaleChangeEvent any more. Instead the widget changing the locale is
     * expected to send a LocaledChangedEvent
     *
     * @param newLocale the new locale
     * @return true if the locale was changed, false if not.
     */
    public static boolean setLocale(final Locale newLocale) {
        Locale oldLocale = currentLocale;
        try {
            jpoResources = ResourceBundle.getBundle("org.jpo.gui.JpoResources", newLocale);
            currentLocale = newLocale;
        } catch (MissingResourceException mre) {
            LOGGER.info(mre.getMessage());
            jpoResources = ResourceBundle.getBundle("org.jpo.gui.JpoResources", DEFAULT_LOCALE);
            currentLocale = DEFAULT_LOCALE;
        }
        titleFont = Font.decode(Settings.jpoResources.getString("SettingsTitleFont"));
        captionFont = Font.decode(Settings.jpoResources.getString("SettingsCaptionFont"));

        return (!currentLocale.equals(oldLocale));
    }

    /**
     * the resourceBundle is a Java thing that sorts out language customisation
     */
    public static ResourceBundle jpoResources;

    /*
     * I'm using a class block initializer here so that we don't ever end up
     * without a ResourceBundle. This proves highly annoying to the Unit Tests
     * and caused me frustration and headaches. RE, 20.1.2007
     */
    static {
        setLocale(currentLocale);
    }

    /**
     * MAX number of recent Drop Nodes
     */
    public static final int MAX_DROPNODES = 12;

    /**
     * Recently used Drop Nodes to make it simple to re-use
     */
    public static final Queue<SortableDefaultMutableTreeNode> recentDropNodes = EvictingQueue.create(MAX_DROPNODES);

    /**
     * This method memorizes the recent drop targets so that they can be
     * accessed more quickly in subsequent move operations. After calling this method send a
     * {@link org.jpo.eventbus.RecentDropNodesChangedEvent RecentDropNodesChangedEvent}
     * onto the EventBus so that GUI widgets can update themselves. Nodes are only added once.
     * <p>
     * {@code JpoEventBus.getInstance().post( new RecentDropNodesChangedEvent() );}
     *
     * @param recentNode The recent drop target to add
     */
    public static void memorizeGroupOfDropLocation(final SortableDefaultMutableTreeNode recentNode) {
        if (!recentDropNodes.contains(recentNode)) {
            recentDropNodes.add(recentNode);
        }
    }

    /*
     * ------------------------------------------------------------------------------
     * Stuff for memorizing the copy target locations
     */
    /**
     * Queue of recently used directories in copy operations and other file
     * selections.
     */
    public static final Queue<String> copyLocations = EvictingQueue.create(MAX_MEMORISE);
    /**
     * Array of recently used zip files operations and other file selections.
     */
    public static final Queue<String> memorizedZipFiles = EvictingQueue.create(MAX_MEMORISE);

    /**
     * This method memorises the directories used in copy operations so that
     * they can be offered as options in drop down lists.
     * <p>
     * The callers of this method need to make sure they notify interested
     * listeners of a change by calling:
     * <p>
     * {@code JpoEventBus.getInstance().post( new CopyLocationsChangedEvent() );}
     *
     * @param location The new location to memorise
     */
    public static void memorizeCopyLocation(final String location) {
        if (!copyLocations.contains(location)) {
            copyLocations.add(location);
        }
        validateCopyLocations();
        writeSettings();
    }

    /**
     * This method memorises the zip files used in copy operations so that they
     * can be offered as options in drop down lists.
     *
     * @param location The new zip file to memorise
     */
    public static void memorizeZipFile(final String location) {
        if (!memorizedZipFiles.contains(location)) {
            memorizedZipFiles.add(location);
        }
    }

    /**
     * This method validates that the copy locations are valid directories and
     * removes those that don't exist
     */
    public static void validateCopyLocations() {
        copyLocations.removeIf(location -> !new File(location).exists());
    }

    /**
     * This method clears the copy locations and saves the settings
     */
    public static void clearCopyLocations() {
        copyLocations.clear();
        writeSettings();
    }

    /**
     * This method returns the most recently used copy location. If there is no
     * most recent copyLocation then the user's home directory is returned.
     *
     * @return Returns the most recent copy location directory or the user's
     * home directory
     */
    public static File getMostRecentCopyLocation() {
        for (String copyLocation : copyLocations) {
            if (copyLocation != null) {
                return new File(copyLocation);
            }
        }
        return new File(System.getProperty("user.dir"));
    }
    /*
     * ------------------------------------------------------------------------------
     * Stuff for user Functions
     */
    /**
     * number of user Functions
     */
    public static final int MAX_USER_FUNCTIONS = 3;

    public static String[] getUserFunctionNames() {
        return userFunctionNames;
    }

    public static String[] getUserFunctionCmd() {
        return userFunctionCmd;
    }

    /**
     * Array of user function names
     */
    private static final String[] userFunctionNames = new String[MAX_USER_FUNCTIONS];
    /**
     * Array of user function commands
     */
    private static final String[] userFunctionCmd = new String[MAX_USER_FUNCTIONS];

    private static MainWindow mainWindow;

    public static void setMainWindow(MainWindow newMainWindow) {
        mainWindow = newMainWindow;
    }

    public static MainWindow getMainWindow() {
        return mainWindow;
    }

}
