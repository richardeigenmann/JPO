package jpo;

import java.util.*;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.util.prefs.BackingStoreException;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.util.prefs.Preferences;
import javax.swing.filechooser.FileSystemView;
import jpo.export.HtmlDistillerOptions;


/*
Settings.java:  class that holds the settings of the JPO application

Copyright (C) 2002-2008 Richard Eigenmann, Zürich, Switzerland
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
 *  This class defines all the settings for the JPO application. In order for it to be valid
 *  for all objects it's components are all static as is the Settings object itself when
 *  created.<p>
 *
 *  The SettingsDialog is the editor for these settings.
 *
 **/
public class Settings {

    /**
     *  this ClassLoader helps most other objects find the resources they require
     */
    public static final ClassLoader cl = Settings.class.getClassLoader();
    /**
     *  A static reference to the Collection being displayed. In future perhaps we will
     *  allow multiple collections to be loaded.
     **/
    public static PictureCollection pictureCollection = new PictureCollection();
    /**
     *  Indicates whether functions should log that they were called.
     */
    public static final boolean logFunctions = true;
    /**
     *  Indicates whether user actions should be logged
     */
    public static final boolean logUserActions = true;
    /**
     *  Indicates whether errors should be logged
     */
    public static final boolean logErrors = true;
    /**
     *  flag to indicate that debug information should be logged
     */
    public static boolean writeLog = false;
    /**
     *  the filename of the logfile
     */
    public static File logfile;
    /**
     *  Flag to indicate whether the JPO window should be maximised on startup or left for the
     *  OS to decide on the size together with the JVM
     */
    public static boolean maximiseJpoOnStartup = true;
    /**
     *  the dimensions of the main JPO frame
     */
    public static Dimension mainFrameDimensions;
    /**
     *  A set of window sizes that the user can choose his preferred size from. The first option will be to maximise the window
     */
    public static final Dimension[] windowSizes = { new Dimension( 0, 0 ), new Dimension( 1050, 760 ), new Dimension( 1250, 900 ), new Dimension( 1450, 1190 ), new Dimension( 2150, 1300 ) };
    /**
     *  Flag to indicate whether the JPO window should be maximised on startup or left for the
     *  OS to decide on the size together with the JVM
     */
    public static boolean maximisePictureViewerWindow = true;
    /**
     *  the dimensions of the "Default" picture viewer
     */
    public static Dimension pictureViewerDefaultDimensions;
    /**
     *  variable to indicate that the window size should be stored
     *  when the application closes.
     */
    public static boolean saveSizeOnExit;
    /**
     *  the default place for the divider.
     **/
    public static int preferredMasterDividerSpot = 350;
    /**
     *  the default place for the left side divider.
     **/
    public static int preferredLeftDividerSpot;
    /**
     *  the default width of the divider
     **/
    public static int dividerWidth = 12;
    /**
     *  the default value for maxThumbnails
     **/
    public final static int defaultMaxThumbnails = 50;
    /**
     *  a variable that sets the maximum number of thumbnails that shall be displayed at one time.
     **/
    public static int maxThumbnails = defaultMaxThumbnails;
    /**
     * Setting for the width of the thumbnails. Set by default to 350 pixels.
     */
    public static int thumbnailSize = 350;
    /**
     *  the dimension of minithumbnails in the group folders
     */
    public static final Dimension miniThumbnailSize = new Dimension( 100, 75 );
    /**
     *   The minimum width for the left panels
     */
    public static final int leftPanelMinimumWidth = 200;
    /**
     *  the minimum Dimension for the InfoPanel
     */
    public static final Dimension infoPanelMinimumSize = new Dimension( leftPanelMinimumWidth, 100 );
    /**
     *  the preferred Dimension for the InfoPanel
     */
    public static final Dimension infoPanelPreferredSize = new Dimension( leftPanelMinimumWidth, 100 );
    /**
     *  the minimum Dimension for the Navigator Panel
     */
    public static final Dimension jpoNavigatorJTabbedPaneMinimumSize = new Dimension( leftPanelMinimumWidth, 300 );
    /**
     *  the preferred Dimension for the Navigator Panel
     */
    public static final Dimension jpoNavigatorJTabbedPanePreferredSize = new Dimension( preferredMasterDividerSpot, 500 );
    /**
     *  the minimum Dimension for the Thumbnail Panel
     */
    public static final Dimension thumbnailJScrollPaneMinimumSize = new Dimension( (int) ( thumbnailSize * 1.4f ), (int) ( thumbnailSize * 1.8f ) );
    /**
     *  the preferred Dimension for the Thumbnail Panel
     */
    public static final Dimension thumbnailJScrollPanePreferredSize = new Dimension( (int) ( thumbnailSize * 2.2f ), 800 );
    /**
     *  the minimum Dimension for the JPO Window
     */
    public static final Dimension jpoJFrameMinimumSize =
            new Dimension( jpoNavigatorJTabbedPaneMinimumSize.width + dividerWidth + thumbnailJScrollPaneMinimumSize.width,
            Math.max( jpoNavigatorJTabbedPaneMinimumSize.height + dividerWidth + infoPanelMinimumSize.height,
            thumbnailJScrollPaneMinimumSize.height ) );
    /**
     *  the preferred Dimension for the JPO Window
     */
    public static final Dimension jpoJFramePreferredSize =
            new Dimension( jpoNavigatorJTabbedPanePreferredSize.width + dividerWidth + thumbnailJScrollPanePreferredSize.width,
            Math.max( jpoNavigatorJTabbedPanePreferredSize.height + dividerWidth + infoPanelPreferredSize.height,
            thumbnailJScrollPanePreferredSize.height ) );
    /**
     *   The polling interval in milliseconds for the ThumbnailCreationThreads to check
     *   Whether there is something new to render.
     */
    public static final int ThumbnailCreationThreadPollingTime = 500;
    /**
     *  The number of thumbnail creation threads to spawn.
     *  @see ThumbnailCreationThread
     */
    public static final int numberOfThumbnailCreationThreads = 2;
    /**
     *  The KDE Panel has the unfortunate habit of insisting on being on top so this
     *  parameter allows you to specify how much space should be left from the bottom of
     *  the screen for Full screen windows.
     **/
    public static int leaveForPanel;
    /**
     *   The picturelist that should be loaded automatically
     **/
    public static String autoLoad;
    /**
     *  number of recent files shown in the file menu
     */
    public static final int recentFiles = 9;
    /**
     *  Array of recently used files
     */
    public static String[] recentCollections = new String[recentFiles];
    /**
     *  the path where thumbnails are to be kept if at all
     */
    public static File thumbnailPath;
    /**
     *  the prefix for thumbnail files in the thumbnail directory
     */
    public static final String thumbnailPrefix = "JPO_Thumbnail_";
    /**
     *  a flag that indicates whether thumbnails are to be kept as files at all
     */
    public static boolean keepThumbnails;
    /**
     *   A counter that keeps track of the number of thumbnails created
     */
    public static int thumbnailCounter = 0;
    /**
     *  a flag that indicates that small images should not be enlarged
     */
    public static boolean dontEnlargeSmallImages;
    /**
     *  Object that gets populated by the main Jpo object if it can
     *  discover a file called autostartJarPicturelist in the Classpath.
     *  This is only supposed to be the case for jar bundled archives of
     *  pictures and code. If the file is discovered this URL object is
     *  populated. Otherwise it is null. You can test for null on it.
     */
    public static URL jarAutostartList = null;
    /**
     *  the path to the jar file; derived from jarAutostartList
     */
    public static String jarRoot = null;
    /**
     *   variable that tracks if there are unsaved changes in these
     *   settings.
     */
    public static boolean unsavedSettingChanges = false;
    /**
     *    URL of the document type definition in the xml file.
     */
    public static final String COLLECTION_DTD = "file:./collection.dtd";
    /**
     *    handle to the main frame of the application. It's purpose it
     *    to have a handy reference for dialog boxes and the like to have
     *    a reference object.
     */
    public static JFrame anchorFrame = null;
    /**
     *   The maximum number of pictures to keep in memory
     */
    public static int maxCache;
    /**
     *  The maximum size a picture is zoomed to. This is to stop
     *  the Java engine creating enormous temporary images which
     *  lock the computer up completely.
     */
    public static int maximumPictureSize;
    /**
     *  standard size for all JTextFields that need to record a filename.
     */
    public static final Dimension filenameFieldPreferredSize = new Dimension( 550, 20 );
    /**
     *  standard size for all JTextFields that need to record a filename
     */
    public static final Dimension filenameFieldMinimumSize = new Dimension( 300, 20 );
    /**
     *  standard size for all JTextFields that need to record a filename
     */
    public static final Dimension filenameFieldMaximumSize = new Dimension( 1000, 20 );
    /**
     *  standard size for all JTextFields that need to record a short text.
     */
    public static final Dimension shortFieldPreferredSize = new Dimension( 350, 20 );
    /**
     *  standard size for all JTextFields that need to record a short text
     */
    public static final Dimension shortFieldMinimumSize = new Dimension( 150, 20 );
    /**
     *  standard size for all JTextFields that need to record a short text
     */
    public static final Dimension shortFieldMaximumSize = new Dimension( 1000, 20 );
    /**
     *  standard size for all JTextFields that need to record a normal length text
     */
    public static final Dimension textfieldPreferredSize = new Dimension( 350, 20 );
    /**
     *  standard size for all JTextFields that need to record a normal length text
     */
    public static final Dimension textfieldMinimumSize = new Dimension( 150, 20 );
    /**
     *  standard size for all JTextFields that need to record a normal length text
     */
    public static final Dimension textfieldMaximumSize = new Dimension( 1000, 20 );
    /**
     *  standard size for all JTextFields that need to record a normal length text
     */
    public static final Dimension shortNumberPreferredSize = new Dimension( 60, 20 );
    /**
     *  standard size for all JTextFields that need to record a normal length text
     */
    public static final Dimension shortNumberMinimumSize = new Dimension( 60, 20 );
    /**
     *  standard size for all JTextFields that need to record a normal length text
     */
    public static final Dimension shortNumberMaximumSize = new Dimension( 100, 20 );
    /**
     *   fixed size for the threeDotButton which opens the JFileChooser dialog
     */
    public static final Dimension threeDotButtonSize = new Dimension( 25, 20 );
    /**
     *  the font used to display the title. Currently Arial Bold 20.
     */
    public static Font titleFont;
    /**
     *  the font used to display the captions. Currently Arial Plain 16
     */
    public static Font captionFont;
    /**
     *  the height of the Thumbnail descriptions
     */
    public static final int thumbnailDescriptionHeight = 200;
    /**
     *  The interval between the timer checking to see if the picture is ready
     *  before the main delay loop should be waited. You want to give the user
     *  the specified seconds to look at the picture and not subtract from that
     *  the time it took the program to load the image.
     */
    public static final int advanceTimerPollingInterval = 500;
    /**
     *  The default number of pictures per row for the Html export
     */
    public static int defaultHtmlPicsPerRow = 3;
    /**
     *  The default width for pictures for the Html export overview
     */
    public static int defaultHtmlThumbnailWidth = 300;
    /**
     *  The default height for pictures for the Html export overview
     */
    public static int defaultHtmlThumbnailHeight = 300;
    /**
     *  Whether to generate the midres html pages or not
     */
    public static boolean defaultGenerateMidresHtml = true;
    /**
     *  Whether to generate DHTML effects or not
     */
    public static boolean defaultGenerateDHTML = true;
    /**
     *  Whether to generate a zip file with the highres pictures
     */
    public static boolean defaultGenerateZipfile = false;
    /**
     *  Whether to generate a link to highre pictures at the current location or not
     */
    public static boolean defaultLinkToHighres = false;
    /**
     *  Whether to export the Highres pictures or not
     */
    public static boolean defaultExportHighres = false;
    /**
     *  The default midres width for pictures for the Html export
     */
    public static int defaultHtmlMidresWidth = 700;
    /**
     *  The default midres height for pictures for the Html export
     */
    public static int defaultHtmlMidresHeight = 700;
    /**
     * Picture nameing convention on HTML output
     */
    public static int defaultHtmlPictureNaming = HtmlDistillerOptions.PICTURE_NAMING_BY_HASH_CODE;
    /**
     *   The default color for the background on the web page is white.
     */
    public static Color htmlBackgroundColor = Color.WHITE;
    /**
     *  This constant defines the text color on the web page.
     */
    public static Color htmlFontColor = Color.BLACK;
    /**
     *  The default quality for Thumbnail pictures for the Html export
     */
    public static float defaultHtmlLowresQuality = 0.8f;
    /**
     *  The default quality for Midres pictures for the Html export
     */
    public static float defaultHtmlMidresQuality = 0.8f;
    /**
     * Whether to write the robots.txt on the generate webpage
     */
    public static boolean writeRobotsTxt = false;
    /**
     *  true when thumbnails are supposed to scale fast
     */
    public static boolean thumbnailFastScale = true;
    /**
     *  true when the pictureViewer is supposed to scale fast
     */
    public static boolean pictureViewerFastScale = true;
    /**
     *  Default size for buttons such as OK, cancel etc.
     */
    public static Dimension defaultButtonDimension = new Dimension( 80, 25 );
    /**
     *  Default size for buttons such as OK, cancel etc.
     */
    public static Dimension threeDotButtonDimension = new Dimension( 25, 25 );
    /**
     *  This object is a handy reference for any component that wants to tell the
     *  main JTree for the collection to reposition itself.
     *  ToDo: make this an interface.
     */
    public static CollectionJTreeController mainCollectionJTreeController = null;
    /**
     *	constant to indicate the Description to some routines
     */
    public final static int DESCRIPTION = 1;
    /**
     *	constant to indicate the Picture URL to some routines
     */
    public final static int FILE_URL = DESCRIPTION + 1;
    /**
     *	constant to indicate the Lowres URL to some routines
     */
    public final static int FILE_LOWRES_URL = FILE_URL + 1;
    /**
     *	constant to indicate the Film Reference to some routines
     */
    public final static int FILM_REFERENCE = FILE_LOWRES_URL + 1;
    /**
     *	constant to indicate the Creation Time to some routines
     */
    public final static int CREATION_TIME = FILM_REFERENCE + 1;
    /**
     *	constant to indicate the Comment to some routines
     */
    public final static int COMMENT = CREATION_TIME + 1;
    /**
     *	constant to indicate the Photographer to some routines
     */
    public final static int PHOTOGRAPHER = COMMENT + 1;
    /**
     *	constant to indicate the Copyright Holder to some routines
     */
    public final static int COPYRIGHT_HOLDER = PHOTOGRAPHER + 1;
    /**
     *	constant to indicate the Rotation to some routines
     */
    public final static int ROTATION = COPYRIGHT_HOLDER + 1;
    /**
     *	constant to indicate the Rotation to some routines
     */
    public final static int CHECKSUM = ROTATION + 1;
    /**
     *	constant to indicate the Categories are being parsed
     */
    public final static int CATEGORIES = CHECKSUM + 1;
    /**
     *	constant to indicate that a Category is being parsed
     */
    public final static int CATEGORY = CATEGORIES + 1;
    /**
     *	constant to indicate that a Category is being parsed
     */
    public final static int CATEGORY_DESCRIPTION = CATEGORY + 1;
    /**
     *      date format for adding ew pictures from the camera
     */
    public static String addFromCameraDateFormat = "dd.MM.yyyy  HH:mm";
    /**
     *	Collection of Cameras
     */
    public static Vector<Camera> Cameras = new Vector();
    /**
     *	list of email senders
     */
    public static TreeSet emailSenders = new TreeSet() {

        public boolean add( Object o ) {
            boolean b = super.add( o );
            if ( b ) {
                unsavedSettingChanges = true;
            }
            return b;
        }
    };
    /**
     *	list of email senders
     */
    public static TreeSet emailRecipients = new TreeSet() {

        public boolean add( Object o ) {
            boolean b = super.add( o );
            if ( b ) {
                unsavedSettingChanges = true;
            }
            return b;
        }
    };
    /**
     *	Email Server
     */
    public static String emailServer = "";
    /**
     *	Email Server port
     */
    public static String emailPort = "25";
    /**
     *	Email User
     */
    public static String emailUser = "";
    /**
     *	Email Password
     */
    public static String emailPassword = "";
    /**
     *	Should emails have scaled images
     */
    public static boolean emailScaleImages = true;
    /**
     *	The last size we scaled images to in the email dialog
     */
    public static Dimension emailDimensions = new Dimension( 350, 300 );
    /**
     *	Should emails contain the original images
     */
    public static boolean emailSendOriginal = false;
    /**
     *   The default application background color.
     */
    public static final Color JPO_BACKGROUND_COLOR = Color.WHITE;
    /**
     *  The background color for the picture Viewer
     */
    public static final Color PICTUREVIEWER_BACKGROUND_COLOR = Color.BLACK;
    /**
     *  The text color for the picture Viewer
     */
    public static final Color PICTUREVIEWER_TEXT_COLOR = Color.WHITE;

    /**
     *  method that set the default parameters
     */
    public static void setDefaults() {
        setLocale( currentLocale );

        autoLoad = "";
        logfile = new File( new File( System.getProperty( "java.io.tmpdir" ) ), "JPO.log" );

        mainFrameDimensions = new Dimension( windowSizes[1] );
        preferredLeftDividerSpot = mainFrameDimensions.height - 200;
        if ( preferredLeftDividerSpot < 0 ) {
            preferredLeftDividerSpot = 100;
        }
        ;

        maximumPictureSize = 6000;
        maxCache = 4;

        pictureViewerDefaultDimensions = new Dimension( windowSizes[1] );

        keepThumbnails = true;
        thumbnailPath = new File( new File( System.getProperty( "java.io.tmpdir" ) ), "JPO_thumbnails" + File.separator );

        dontEnlargeSmallImages = true;
    }
    /**
     *  handle to the user Preferences
     */
    public static Preferences prefs = Preferences.userNodeForPackage( Jpo.class );

    /**
     *  This method reads the settings from the preferences.
     */
    public static void loadSettings() {
        setDefaults();

        setLocale( new Locale( prefs.get( "currentLocale", getCurrentLocale().toString() ) ) );
        maximumPictureSize = prefs.getInt( "maximumPictureSize", maximumPictureSize );
        maxThumbnails = prefs.getInt( "maxThumbnails", maxThumbnails );
        thumbnailSize = prefs.getInt( "thumbnailSize", thumbnailSize );
        maximiseJpoOnStartup = prefs.getBoolean( "maximiseJpoOnStartup", maximiseJpoOnStartup );
        mainFrameDimensions.width = prefs.getInt( "mainFrameDimensions.width", mainFrameDimensions.width );
        mainFrameDimensions.height = prefs.getInt( "mainFrameDimensions.height", mainFrameDimensions.height );
        preferredMasterDividerSpot = prefs.getInt( "preferredMasterDividerSpot", preferredMasterDividerSpot );
        preferredLeftDividerSpot = prefs.getInt( "preferredLeftDividerSpot", preferredLeftDividerSpot );
        dividerWidth = prefs.getInt( "dividerWidth", dividerWidth );
        autoLoad = prefs.get( "autoload", autoLoad );

        maximisePictureViewerWindow = prefs.getBoolean( "maximisePictureViewerWindow", maximisePictureViewerWindow );
        pictureViewerDefaultDimensions.width = prefs.getInt( "pictureViewerDefaultDimensions.width", pictureViewerDefaultDimensions.width );
        pictureViewerDefaultDimensions.height = prefs.getInt( "pictureViewerDefaultDimensions.height", pictureViewerDefaultDimensions.height );

        int i;
        for ( i = 0; i < Settings.maxCopyLocations; i++ ) {
            copyLocations[i] = prefs.get( "copyLocations-" + Integer.toString( i ), null );
        }
        for ( i = 0; i < Settings.recentFiles; i++ ) {
            recentCollections[i] = prefs.get( "recentCollections-" + Integer.toString( i ), null );
        }
        for ( i = 0; i < Settings.maxUserFunctions; i++ ) {
            userFunctionNames[i] = prefs.get( "userFunctionName-" + Integer.toString( i ), null );
            userFunctionCmd[i] = prefs.get( "userFunctionCmd-" + Integer.toString( i ), null );
        }
        keepThumbnails = prefs.getBoolean( "keepThumbnails", keepThumbnails );
        thumbnailPath = new File( prefs.get( "thumbnailPath", thumbnailPath.getPath() ) ); // inefficient RE, 11.11.2006
        dontEnlargeSmallImages = prefs.getBoolean( "dontEnlargeSmallImages", dontEnlargeSmallImages );
        thumbnailCounter = prefs.getInt( "thumbnailCounter", thumbnailCounter );
        writeLog = prefs.getBoolean( "writeLog", writeLog );
        logfile = new File( prefs.get( "logfile", logfile.getPath() ) ); // inefficient, RE, 11.11.2006
        maxCache = prefs.getInt( "maxCache", maxCache );
        defaultHtmlPicsPerRow = prefs.getInt( "defaultHtmlPicsPerRow", defaultHtmlPicsPerRow );
        defaultHtmlThumbnailWidth = prefs.getInt( "defaultHtmlThumbnailWidth", defaultHtmlThumbnailWidth );
        defaultHtmlThumbnailHeight = prefs.getInt( "defaultHtmlThumbnailHeight", defaultHtmlThumbnailHeight );
        defaultGenerateMidresHtml = prefs.getBoolean( "defaultGenerateMidresHtml", defaultGenerateMidresHtml );
        defaultHtmlPictureNaming = prefs.getInt( "defaultHtmlPictureNaming", defaultHtmlPictureNaming );
        defaultGenerateDHTML = prefs.getBoolean( "defaultGenerateDHTML", defaultGenerateDHTML );
        defaultGenerateZipfile = prefs.getBoolean( "defaultGenerateZipfile", defaultGenerateZipfile );
        defaultLinkToHighres = prefs.getBoolean( "defaultLinkToHighres", defaultLinkToHighres );
        defaultExportHighres = prefs.getBoolean( "defaultExportHighres", defaultExportHighres );
        defaultHtmlMidresWidth = prefs.getInt( "defaultHtmlMidresWidth", defaultHtmlMidresWidth );
        defaultHtmlMidresHeight = prefs.getInt( "defaultHtmlMidresHeight", defaultHtmlMidresHeight );
        defaultHtmlLowresQuality = prefs.getFloat( "defaultHtmlLowresQuality", defaultHtmlLowresQuality );
        defaultHtmlMidresQuality = prefs.getFloat( "defaultHtmlMidresQuality", defaultHtmlMidresQuality );
        writeRobotsTxt = prefs.getBoolean( "writeRobotsTxt", writeRobotsTxt );
        thumbnailFastScale = prefs.getBoolean( "thumbnailFastScale", thumbnailFastScale );
        pictureViewerFastScale = prefs.getBoolean( "pictureViewerFastScale", pictureViewerFastScale );
        int n = prefs.getInt( "emailSenders", 0 );
        for ( i = 0; i < n; i++ ) {
            emailSenders.add( prefs.get( "emailSender-" + Integer.toString( i ), "" ) );
        }
        n = prefs.getInt( "emailRecipients", 0 );
        for ( i = 0; i < n; i++ ) {
            emailRecipients.add( prefs.get( "emailRecipient-" + Integer.toString( i ), "" ) );
        }
        emailServer = prefs.get( "emailServer", emailServer );
        emailPort = prefs.get( "emailPort", emailPort );
        emailUser = prefs.get( "emailUser", emailUser );
        emailPassword = prefs.get( "emailPassword", emailPassword );
        emailScaleImages = prefs.getBoolean( "emailScaleImages", emailScaleImages );
        emailSendOriginal = prefs.getBoolean( "emailSendOriginal", emailSendOriginal );
        emailDimensions.width = prefs.getInt( "emailDimensions.width", emailDimensions.width );
        emailDimensions.height = prefs.getInt( "emailDimensions.height", emailDimensions.height );

        validateCopyLocations();
        validateSettings();

        loadCameraSettings();
    }

    /**
     *  method that validates the settings & brings up the Settings dialog if not ok
     */
    public static void validateSettings() {
        if ( maxThumbnails < 1 ) { //how can this happen?
            maxThumbnails = defaultMaxThumbnails;
        }
        if ( keepThumbnails ) {
            if ( !thumbnailPath.exists() ) {
                try {
                    thumbnailPath.mkdirs();
                } catch ( SecurityException x ) {
                    // do nothing here because
                    // the error situation will
                    // be flagged in the next steps.
                }
            }
            if ( !thumbnailPath.exists() ) {
                JOptionPane.showMessageDialog( Settings.anchorFrame,
                        Settings.jpoResources.getString( "thumbNoExistError" ),
                        Settings.jpoResources.getString( "settingsError" ),
                        JOptionPane.ERROR_MESSAGE );
                keepThumbnails = false;
            } else if ( !thumbnailPath.canWrite() ) {
                JOptionPane.showMessageDialog( Settings.anchorFrame,
                        Settings.jpoResources.getString( "thumbNoWriteError" ),
                        Settings.jpoResources.getString( "settingsError" ),
                        JOptionPane.ERROR_MESSAGE );
                keepThumbnails = false;
            } else if ( !thumbnailPath.isDirectory() ) {
                JOptionPane.showMessageDialog( Settings.anchorFrame,
                        Settings.jpoResources.getString( "thumbNoDirError" ),
                        Settings.jpoResources.getString( "settingsError" ),
                        JOptionPane.ERROR_MESSAGE );
                keepThumbnails = false;
            }
        }


        if ( writeLog ) {
            if ( logfile.exists() ) {
                if ( !logfile.canWrite() ) {
                    JOptionPane.showMessageDialog( Settings.anchorFrame,
                            Settings.jpoResources.getString( "logFileCanWriteError" ),
                            Settings.jpoResources.getString( "settingsError" ),
                            JOptionPane.ERROR_MESSAGE );
                    writeLog = false;
                }
                if ( !logfile.isFile() ) {
                    JOptionPane.showMessageDialog( Settings.anchorFrame,
                            Settings.jpoResources.getString( "logFileIsFileError" ),
                            Settings.jpoResources.getString( "settingsError" ),
                            JOptionPane.ERROR_MESSAGE );
                    writeLog = false;
                }
            } else {
                File testFileParent = logfile.getParentFile();
                if ( testFileParent == null ) {
                    // the parent of root dir is null
                    JOptionPane.showMessageDialog( Settings.anchorFrame,
                            Settings.jpoResources.getString( "logFileIsFileError" ),
                            Settings.jpoResources.getString( "settingsError" ),
                            JOptionPane.ERROR_MESSAGE );
                    writeLog = false;
                }
                if ( !testFileParent.canWrite() ) {
                    JOptionPane.showMessageDialog( Settings.anchorFrame,
                            Settings.jpoResources.getString( "logFileCanWriteError" ),
                            Settings.jpoResources.getString( "settingsError" ),
                            JOptionPane.ERROR_MESSAGE );
                    writeLog = false;
                }
            }

        }

        notifyRecentFilesChanged();  // why? RE, 20.1.2007
    }

    /**
     *  This method writes the settings to the Preferences object which was added to Java with 1.4
     */
    public static void writeSettings() {
        prefs.put( "currentLocale", getCurrentLocale().toString() );
        prefs.putInt( "maximumPictureSize", maximumPictureSize );
        prefs.putInt( "maxThumbnails", maxThumbnails );
        prefs.putInt( "thumbnailSize", thumbnailSize );
        prefs.putBoolean( "maximiseJpoOnStartup", maximiseJpoOnStartup );
        prefs.putInt( "mainFrameDimensions.width", mainFrameDimensions.width );
        prefs.putInt( "mainFrameDimensions.height", mainFrameDimensions.height );
        prefs.putInt( "preferredMasterDividerSpot", preferredMasterDividerSpot );
        prefs.putInt( "preferredLeftDividerSpot", preferredLeftDividerSpot );
        prefs.putInt( "dividerWidth", dividerWidth );
        prefs.put( "autoload", autoLoad );
        prefs.putBoolean( "maximisePictureViewerWindow", maximisePictureViewerWindow );
        prefs.putInt( "pictureViewerDefaultDimensions.width", pictureViewerDefaultDimensions.width );
        prefs.putInt( "pictureViewerDefaultDimensions.height", pictureViewerDefaultDimensions.height );
        int i;
        int n = 0;
        for ( i = 0; i < Settings.maxCopyLocations; i++ ) {
            if ( copyLocations[i] != null ) {
                prefs.put( "copyLocations-" + Integer.toString( n ), copyLocations[i] );
                n++;
            }
        }
        n = 0;
        for ( i = 0; i < Settings.recentFiles; i++ ) {
            if ( recentCollections[i] != null ) {
                prefs.put( "recentCollections-" + Integer.toString( n ), recentCollections[i] );
                n++;
            }
        }
        n = 0;
        for ( i = 0; i < Settings.maxUserFunctions; i++ ) {
            if ( ( userFunctionNames[i] != null ) && ( userFunctionNames[i].length() > 0 ) && ( userFunctionCmd[i] != null ) && ( userFunctionCmd[i].length() > 0 ) ) {
                prefs.put( "userFunctionName-" + Integer.toString( n ), userFunctionNames[i] );
                prefs.put( "userFunctionCmd-" + Integer.toString( n ), userFunctionCmd[i] );
                n++;
            }
        }
        prefs.putBoolean( "keepThumbnails", keepThumbnails );
        prefs.put( "thumbnailPath", thumbnailPath.getPath() );
        prefs.putBoolean( "dontEnlargeSmallImages", dontEnlargeSmallImages );
        prefs.putInt( "thumbnailCounter", thumbnailCounter );
        prefs.putBoolean( "writeLog", writeLog );
        prefs.put( "logfile", logfile.getPath() );
        prefs.putInt( "maxCache", maxCache );
        prefs.putInt( "defaultHtmlPicsPerRow", defaultHtmlPicsPerRow );
        prefs.putInt( "defaultHtmlThumbnailWidth", defaultHtmlThumbnailWidth );
        prefs.putInt( "defaultHtmlThumbnailHeight", defaultHtmlThumbnailHeight );
        prefs.putBoolean( "defaultGenerateMidresHtml", defaultGenerateMidresHtml );
        prefs.putInt( "defaultHtmlPictureNaming", defaultHtmlPictureNaming );
        prefs.putBoolean( "defaultGenerateDHTML", defaultGenerateDHTML );
        prefs.putBoolean( "defaultGenerateZipfile", defaultGenerateZipfile );
        prefs.putBoolean( "defaultLinkToHighres", defaultLinkToHighres );
        prefs.putBoolean( "defaultExportHighres", defaultExportHighres );
        prefs.putInt( "defaultHtmlMidresWidth", defaultHtmlMidresWidth );
        prefs.putInt( "defaultHtmlMidresHeight", defaultHtmlMidresHeight );
        prefs.putFloat( "defaultHtmlLowresQuality", defaultHtmlLowresQuality );
        prefs.putFloat( "defaultHtmlMidresQuality", defaultHtmlMidresQuality );
        prefs.putBoolean( "writeRobotsTxt", writeRobotsTxt );
        prefs.putBoolean( "thumbnailFastScale", thumbnailFastScale );
        prefs.putBoolean( "pictureViewerFastScale", pictureViewerFastScale );
        n = 0;
        Iterator itr = emailSenders.iterator();
        while ( itr.hasNext() ) {
            prefs.put( "emailSender-" + Integer.toString( n ), (String) itr.next() );
            n++;
        }
        prefs.putInt( "emailSenders", n );
        n = 0;
        itr = emailRecipients.iterator();
        while ( itr.hasNext() ) {
            prefs.put( "emailRecipient-" + Integer.toString( n ), (String) itr.next() );
            n++;
        }
        prefs.putInt( "emailRecipients", n );
        prefs.put( "emailServer", emailServer );
        prefs.put( "emailPort", emailPort );
        prefs.put( "emailUser", emailUser );
        prefs.put( "emailPassword", emailPassword );
        prefs.putBoolean( "emailScaleImages", emailScaleImages );
        prefs.putBoolean( "emailSendOriginal", emailSendOriginal );
        prefs.putInt( "emailDimensions.width", emailDimensions.width );
        prefs.putInt( "emailDimensions.height", emailDimensions.height );

        unsavedSettingChanges = false;
    }

    /**
     *  Writes the Cameras collection to the preferences. Uses an idea presented by Greg Travis
     *  on this IBM website: http://www-128.ibm.com/developerworks/java/library/j-prefapi.html
     */
    public static void writeCameraSettings() {
        //Tools.log( "Writing Cameras" );
        prefs.putInt( "NumberOfCameras", Cameras.size() );
        int i = 0;
        for ( Camera c : Cameras ) {
            prefs.put( "Camera[" + Integer.toString( i ) + "].description", c.getDescription() );
            prefs.put( "Camera[" + Integer.toString( i ) + "].cameraMountPoint", c.getCameraMountPoint() );
            prefs.putBoolean( "Camera[" + Integer.toString( i ) + "].useFilename", c.getUseFilename() );
            prefs.putBoolean( "Camera[" + Integer.toString( i ) + "].monitor", c.getMonitorForNewPictures() );
            try {
                PrefObj.putObject( prefs, "Camera[" + Integer.toString( i ) + "].oldImage", c.getOldImage() );
            } catch ( IOException ex ) {
                ex.printStackTrace();
            } catch ( BackingStoreException ex ) {
                ex.printStackTrace();
            } catch ( ClassNotFoundException ex ) {
                ex.printStackTrace();
            }
            i++;
        }
    }

    /**
     *  Writes the Camera settings
     *
     * public static void writeCameraSettingsOld() {
     * //Tools.log( "Writing Camera Settings" );
     * OutputStream out;
     *
     * try {
     * PersistenceService ps = (PersistenceService) ServiceManager.lookup( "javax.jnlp.PersistenceService" );
     * BasicService bs = (BasicService) ServiceManager.lookup( "javax.jnlp.BasicService" );
     * try {
     * URL baseURL = bs.getCodeBase();
     * //Tools.log( "CodeBase was " + baseURL.toString() );
     * URL camerasURL = new URL( baseURL, "Cameras" );
     * try {
     * ps.delete( camerasURL );
     * } catch ( IOException x ) {
     * // it doesn't matter if we can't delete the file.
     * Tools.log( "Settings.writeSettings: Caught an IOException when trying to delete the file. Perhaps it didn't exist?. Continuing. Error message: " + x.getMessage() );
     * }
     * ps.create( camerasURL, 4096 );
     * FileContents fc = ps.get( camerasURL );
     * Tools.log( "Settings.writeSettings: Running in Java Web Start setting and writing settings to PersistenceService: " + baseURL.toString() + "/" + fc.getName() );
     * out = fc.getOutputStream( true );
     * } catch ( MalformedURLException x ) {
     * Tools.log( "We had a MalformedURLException: " + x.getMessage() );
     * return;
     * } catch ( IOException x ) {
     * Tools.log( "We had an IOException: " + x.getMessage() );
     * return;
     * }
     * } catch ( UnavailableServiceException x ) {
     * Tools.log( "Settings.writeSettings: no PersistenceService available: writing to local file: " + camerasFile.getPath() );
     * try {
     * out = new FileOutputStream( camerasFile );
     * } catch ( IOException y ) {
     * Tools.log("Settings.writeSettings: can't create cameras File. Aborting. Error: " + y.getMessage() );
     * return;
     * }
     * }
     *
     *
     * try {
     * ObjectOutputStream oos = new ObjectOutputStream( out );
     *
     * oos.writeObject( Cameras );
     * oos.close();
     * } catch ( IOException x ) {
     * Tools.log("Settings.writeCameraSettings failed on an IOException: " + x.getMessage());
     * }
     * }*/
    /**
     *  this method attempts to load the cameras
     */
    public static void loadCameraSettings() {
        int numberOfCameras = prefs.getInt( "NumberOfCameras", 0 );
        for ( int i = 0; i < numberOfCameras; i++ ) {
            Camera c = new Camera();
            c.setDescription( prefs.get( "Camera[" + Integer.toString( i ) + "].description", "unknown" ) );
            c.setCameraMountPoint( prefs.get( "Camera[" + Integer.toString( i ) + "].cameraMountPoint", FileSystemView.getFileSystemView().getHomeDirectory().toString() ) );
            c.setUseFilename( prefs.getBoolean( "Camera[" + Integer.toString( i ) + "].useFilename", true ) );
            c.setMonitorForNewPictures( prefs.getBoolean( "Camera[" + Integer.toString( i ) + "].monitor", true ) );

            c.setOldImage( new HashMap() );
            try {
                c.setOldImage( (HashMap) PrefObj.getObject( prefs, "Camera[" + Integer.toString( i ) + "].oldImage" ) );
            } catch ( IOException ex ) {
                ex.printStackTrace();
            } catch ( BackingStoreException ex ) {
                ex.printStackTrace();
            } catch ( ClassNotFoundException ex ) {
                ex.printStackTrace();
            }
            Cameras.add( c );
        }
    }

    /**
     *  this method attempts to load the cameras
     *
     * public static void loadCameraSettingsOld() {
     * //Tools.log( "Settings.loadCameraSettings: Loading Camera Settings" );
     * InputStream in;
     * try {
     * PersistenceService ps = (PersistenceService) ServiceManager.lookup( "javax.jnlp.PersistenceService" );
     * BasicService bs = (BasicService) ServiceManager.lookup( "javax.jnlp.BasicService" );
     * try {
     * URL baseURL = bs.getCodeBase();
     * URL camerasURL = new URL( baseURL, "Cameras" );
     * ps.get( camerasURL );
     * FileContents fc = ps.get( camerasURL );
     * in = fc.getInputStream();
     * //Tools.log("Setting.loadCameraSettings: Running in Java Web Start Mode and found PersistenceService for Settings." );
     * } catch ( MalformedURLException x ) {
     * Tools.log( "Setting.loadCameraSettings: We had a MalformedURLException: " + x.getMessage() );
     * return;
     * } catch ( IOException x ) {
     * Tools.log( "Settings.loadCameraSettings: There are no settings that could be read." );
     * return;
     * }
     * } catch ( UnavailableServiceException x ) {
     * //Tools.log( "Settings.loadCameraSettings: Running in local file mode. Trying to locate file " + camerasFile.getPath() );
     * if ( ! camerasFile.exists() ) {
     * Tools.log("Settings.loadCameraSettings: Can't find file. Using defaults." );
     * return;
     * }
     * try {
     * in = new FileInputStream( camerasFile );
     * } catch ( FileNotFoundException y ) {
     * Tools.log("Settings.loadCameraSettings: Can't find ini File. Using defaults." );
     * return;
     * }
     * }
     *
     * try{
     * ObjectInputStream ois = new ObjectInputStream( in );
     * Cameras = (Vector) ois.readObject();
     * ois.close();
     * } catch ( IOException x ) {
     * Tools.log("Settings.loadCameraSettings failed with an IOException: " + x.getMessage());
     * } catch ( ClassNotFoundException x ) {
     * Tools.log("Settings.loadCameraSettings failed with an ClassNotFoundException: " + x.getMessage());
     * }
     * createFirstCameraIfEmpty();
     * }*/
    /*------------------------------------------------------------------------------


    /**
     *  every time a collection is opened this function is called for storing
     *  the collection name in the Open Recent menu
     */
    public static void pushRecentCollection( String recentFile ) {
        for ( int i = 0; i < Settings.recentFiles; i++ ) {
            if ( ( recentCollections[i] != null ) &&
                    ( recentCollections[i].equals( recentFile ) ) ) {
                // it was already in the list make it the first one
                for ( int j = i; j > 0; j-- ) {
                    recentCollections[j] = recentCollections[j - 1];
                }
                recentCollections[ 0] = recentFile;
                notifyRecentFilesChanged();
                return;
            }
        }

        // move all the elements down by one
        for ( int i = Settings.recentFiles - 1; i > 0; i-- ) {
            recentCollections[i] = recentCollections[i - 1];
        }
        recentCollections[ 0] = recentFile;
        notifyRecentFilesChanged();
        writeSettings();
    }

    /**
     *  This method needs to be called when the recentCollections Array is updated so that
     *  the listeners for this change are informed about the change.
     */
    private static void notifyRecentFilesChanged() {
        Enumeration e = recentFilesChangeListeners.elements();
        while ( e.hasMoreElements() ) {
            ( (RecentFilesChangeListener) e.nextElement() ).recentFilesChanged();
        }
    }
    /**
     *   a Vector referring to the objects that want to find out about changes to the recently opened files
     */
    private static final Vector recentFilesChangeListeners = new Vector();

    /**
     *  method to register the listening object of the status events
     */
    public static void addRecentFilesChangeListener( RecentFilesChangeListener listener ) {
        recentFilesChangeListeners.add( listener );
    }

    /**
     *  method to register the listening object of the status events
     */
    public static void removeRecentFilesChangeListener( RecentFilesChangeListener listener ) {
        recentFilesChangeListeners.remove( listener );
    }
    /*------------------------------------------------------------------------------




    /**
     *  Default locale if all else fails use this one.
     */
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    /**
     *  The locale to be used for the application
     */
    private static Locale currentLocale = Locale.getDefault();

    /**
     *  returns the Locale the application is running in.
     *  @return   The locale set by environment, default, user or persistent settings.
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     *  The language to be used for the application
     */
    //private static String currentLanguage = "English";
    /**
     *  returns the Language the application is running in.
     *  @return   The Language
     */
    public static String getCurrentLanguage() {
        return currentLocale.getDisplayLanguage();
    }
    /**
     *  Supported Languages
     */
    public static final String[] supportedLanguages = { "English", "Deutsch", "Simplified Chinese", "Traditional Chinese" };
    /**
     *  Locales for the languages in supportedLanguages
     */
    public static final Locale[] supportedLocale = { Locale.ENGLISH, Locale.GERMAN, Locale.SIMPLIFIED_CHINESE, Locale.TRADITIONAL_CHINESE };

    public static void setLocale( Locale newLocale ) {
        Locale oldLocale = currentLocale;
        try {
            jpoResources = ResourceBundle.getBundle( "jpo.JpoResources", newLocale );
            currentLocale = newLocale;
        } catch ( MissingResourceException mre ) {
            Tools.log( "Settings.setDefaults: MissingResourceException: " + mre.getMessage() );
            jpoResources = ResourceBundle.getBundle( "jpo.JpoResources", DEFAULT_LOCALE );
            currentLocale = DEFAULT_LOCALE;
        }
        titleFont = Font.decode( Settings.jpoResources.getString( "SettingsTitleFont" ) );
        captionFont = Font.decode( Settings.jpoResources.getString( "SettingsCaptionFont" ) );

        if ( currentLocale != oldLocale ) {
            notifyLocaleChangeListeners();
        }
    }
    /**
     *   a Vector referring to the objects that want to find out about changes to the locale
     */
    private static ArrayList localeChangeListeners = new ArrayList();

    /**
     *  when the locale is changed this method must be called to inform the
     *  {@link LocaleChangeListener}s that the locale has changed.
     */
    private static void notifyLocaleChangeListeners() {
        Iterator i = localeChangeListeners.iterator();
        while ( i.hasNext() ) {
            ( (LocaleChangeListener) i.next() ).localeChanged();
        }
    }

    /**
     *  method to register the listening object of the status events
     */
    public static void addLocaleChangeListener( LocaleChangeListener listener ) {
        localeChangeListeners.add( listener );
    }

    /**
     *  method to register the listening object of the status events
     */
    public static void removeLocaleChangeListener( LocaleChangeListener listener ) {
        localeChangeListeners.remove( listener );
    }
    /**
     *  the resourceBundle is a Java thing that sorts out language customisation
     */
    public static ResourceBundle jpoResources;
    /**
     *  I'm using a class block initializer here so that we don't ever end up without a
     *  ResourceBundle. This proves highly annoying to the Unit Tests and caused me
     *  frustration and headaches. RE, 20.1.2007
     */


    static {
        setLocale( currentLocale );
    }
    /*------------------------------------------------------------------------------
    Stuff for memorizing the drop locations    */
    /**
     *  MAX number of recent Drop Nodes
     */
    public static final int maxDropNodes = 6;
    /**
     *  Array of recently used Drop Nodes
     */
    public static SortableDefaultMutableTreeNode[] recentDropNodes = new SortableDefaultMutableTreeNode[maxDropNodes];

    /**
     *  This method memorizes the recent drop targets so that they can be accessed
     *  more quickly next time round.
     */
    public static void memorizeGroupOfDropLocation( SortableDefaultMutableTreeNode recentNode ) {
        for ( int i = 0; i < Settings.maxDropNodes; i++ ) {
            if ( ( recentDropNodes[i] != null ) &&
                    ( recentDropNodes[i].hashCode() == recentNode.hashCode() ) ) {
                //Tools.log( "Settings.memorizeGroupOfDropLocation: node was already in the list make it the first one.");
                for ( int j = i; j > 0; j-- ) {
                    recentDropNodes[j] = recentDropNodes[j - 1];
                }
                recentDropNodes[ 0] = recentNode;
                return;
            }
        }

        // move all the elements down by one
        for ( int i = Settings.maxDropNodes - 1; i > 0; i-- ) {
            recentDropNodes[i] = recentDropNodes[i - 1];
        }
        recentDropNodes[ 0] = recentNode;
        notifyRecentDropNodesChanged();
    }
    /**
     *   a Vector referring to the objects that want to find out about changes to the
     *   recently drop target nodes.
     */
    private static Vector recentDropNodeListeners = new Vector();

    /**
     *  method to register the listening object of the status events
     */
    public static void addRecentDropNodeListener( RecentDropNodeListener listener ) {
        recentDropNodeListeners.add( listener );
    }

    /**
     *  method to register the listening object of the status events
     */
    public static void removeRecentDropNodeListener( RecentDropNodeListener listener ) {
        recentDropNodeListeners.remove( listener );
    }

    /**
     *  notifies the listeners that the target drop nodes have changed.
     */
    private static void notifyRecentDropNodesChanged() {
        Enumeration e = recentDropNodeListeners.elements();
        while ( e.hasMoreElements() ) {
            ( (RecentDropNodeListener) e.nextElement() ).recentDropNodesChanged();
        }
    }

    /**
     *  method to remove one of the recent Drop Nodes. It is important to check each time a node
     *  is deleted whether it or one of it's descendents is a drop target as this would no longer
     *  be a valid target.
     */
    public static void removeRecentDropNode( SortableDefaultMutableTreeNode deathNode ) {
        for ( int i = 0; i < recentDropNodes.length; i++ ) {
            if ( deathNode == recentDropNodes[i] ) {
                recentDropNodes[i] = null;
                Settings.notifyRecentDropNodesChanged();
            }
        }
    }

    /**
     *  clears the list of recent drop nodes
     */
    public static void clearRecentDropNodes() {
        recentDropNodes = new SortableDefaultMutableTreeNode[maxDropNodes];
        notifyRecentDropNodesChanged();
    }
    /*------------------------------------------------------------------------------
    Stuff for memorizing the copy target locations    */
    /**
     *  MAX number of recent Drop Nodes
     */
    public static final int maxCopyLocations = 6;
    /**
     *  Array of recently used directories in copy operations and other
     *  File selections.
     */
    public static String[] copyLocations = new String[maxCopyLocations];

    /**
     *  This method memorizes the copy targets so that they can be accessed
     *  more quickly next time round.
     */
    public static void memorizeCopyLocation( String location ) {
        for ( int i = 0; i < Settings.maxCopyLocations; i++ ) {
            if ( ( copyLocations[i] != null ) && ( copyLocations[i].equals( location ) ) ) {
                for ( int j = i; j > 0; j-- ) {
                    copyLocations[j] = copyLocations[j - 1];
                }
                copyLocations[ 0] = location;
                return;
            }
        }

        // move all the elements down by one
        for ( int i = Settings.maxCopyLocations - 1; i > 0; i-- ) {
            copyLocations[i] = copyLocations[i - 1];
        }
        copyLocations[ 0] = location;

        validateCopyLocations();
        unsavedSettingChanges = true;
        notifyCopyLocationsChanged();
    }

    /**
     *  This method validates that the copy locations are valid directories and if not sets the
     *  entry in the copyLocations array to null.
     *
     *  @return   Returns true if the array was changed, false if not.
     */
    public static boolean validateCopyLocations() {
        // validate the locations
        File f;
        boolean arrayChanged = false;
        for ( int i = 0; i < Settings.maxCopyLocations; i++ ) {
            if ( copyLocations[i] != null ) {
                f = new File( copyLocations[i] );
                if ( !f.exists() ) {
                    copyLocations[i] = null;
                    arrayChanged = true;
                }
            }
        }
        return arrayChanged;
    }

    /**
     *  This method returns the most recently used copy location. If there is no most recent
     *  copyLocation then the user's home directory is returned.
     *
     *  @return   Returns the most recent copy location directory or the user's home directory
     */
    public static File getMostRecentCopyLocation() {
        for ( int i = 0; i < Settings.maxCopyLocations; i++ ) {
            if ( Settings.copyLocations[i] != null ) {
                return new File( Settings.copyLocations[i] );
            }
        }
        return new File( System.getProperty( "user.dir" ) );
    }
    /**
     *   a Vector referring to the objects that want to find out about changes to the
     *   recently drop target nodes.
     */
    private static Vector copyLocationChangeListeners = new Vector();

    /**
     *  method to register the listening object of the status events
     */
    public static void addCopyLocationsChangeListener( CopyLocationsChangeListener listener ) {
        copyLocationChangeListeners.add( listener );
    }

    /**
     *  method to register the listening object of the status events
     */
    public static void removeCopyLocationsChangeListener( CopyLocationsChangeListener listener ) {
        copyLocationChangeListeners.remove( listener );
    }

    /**
     *  notifies the listeners that the target drop nodes have changed.
     */
    private static void notifyCopyLocationsChanged() {
        Enumeration e = copyLocationChangeListeners.elements();
        while ( e.hasMoreElements() ) {
            ( (CopyLocationsChangeListener) e.nextElement() ).copyLocationsChanged();
        }
    }
    /*------------------------------------------------------------------------------
    Stuff for user Functions    */
    /**
     *  number of user Functions
     */
    public static final int maxUserFunctions = 3;
    /**
     *  Array of user fucntion names
     */
    public static String[] userFunctionNames = new String[maxUserFunctions];
    /**
     *  Array of user fucntion commands
     */
    public static String[] userFunctionCmd = new String[maxUserFunctions];
    /**
     *   a Vector referring to the objects that want to find out about changes to the
     *   recently drop target nodes.
     */
    private static Vector userFunctionsChangeListeners = new Vector();

    /**
     *  method to register the listening object of the status events
     */
    public static void addUserFunctionsChangeListener( UserFunctionsChangeListener listener ) {
        userFunctionsChangeListeners.add( listener );
    }

    /**
     *  method to register the listening object of the status events
     */
    public static void removeUserFunctionsChangeListener( UserFunctionsChangeListener listener ) {
        userFunctionsChangeListeners.remove( listener );
    }

    /**
     *  notifies the listeners that the target drop nodes have changed.
     */
    public static void notifyUserFunctionsChanged() {
        Enumeration e = userFunctionsChangeListeners.elements();
        while ( e.hasMoreElements() ) {
            ( (UserFunctionsChangeListener) e.nextElement() ).userFunctionsChanged();
        }
    }
}