package jpo;

import java.util.*;
import java.net.*;
import java.io.*;
import java.awt.*;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.jnlp.*;


/*
Settings.java:  class that holds the settings of the JPO application

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
	 *  The top node of the tree data model. It holds all the branches 
	 *  to the groups and	pictures
	 **/
	public static SortableDefaultMutableTreeNode top;


	/**
	 *  the name of the ini file
	 **/
	public static File iniFile = new File( new File( System.getProperty("user.dir")), "JPO.ini");
		

	/**
	 *  the name of the cameras file
	 **/
	public static File camerasFile = new File( new File( System.getProperty("user.dir")), "JPO_cameras.dta");


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
	 *  the dimensions of the main frame
	 */
	 
	public static Rectangle mainFrameDimensions;


	/**
	 *  the dimensions of the "Default" picture viewer
	 */
	 
	public static Rectangle pictureViewerDefaultDimensions;


	/**
	 *  variable to indicate that the window size should be stored 
	 *  when the application closes.
	 */
	public static boolean saveSizeOnExit;
	

	/**
	 *  the default place for the divider. 
	 **/
	public static int preferredMasterDividerSpot;
	
	/**
	 *  the default place for the left side divider. 
	 **/
	public static int preferredLeftDividerSpot;

	
	/**
	 *  the default width of the divider
	 **/
	public static int dividerWidth;


	/**
	 *  a variable that sets the maximum number of thumbnails that shall be displayed at one time.
	 **/
	public static int maxThumbnails;



	/**
	 * the width of the thumbnails
	 */
	public static int thumbnailSize;

	
	/**
	 *  the dimension of minithumbnails in the group folders
	 */
	public static final Dimension miniThumbnailSize = new Dimension( 100, 75 );
	

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
	public static String []  recentCollections = new String[ recentFiles ];





	/** 
	 *  the path where thumbnails are to be kept if at all
	 */
	public static File thumbnailPath;



	/**
	 *  the prefix for thumbnail files in the thumbnail directory
	 */
	public static final String thumbnailPrefix = "JPO_Thumbnail_" ;



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
	 *  the Java engine creating enormous temporaty images which
	 *  lock the computer up copletely.
	 */
	public static int maximumPictureSize;





	/** 
	 *  the resourceBundle is a Java thing that sorts out language customisation
	 */
	public static ResourceBundle jpoResources;



	/**
	 *  standard size for all JTextFields that need to record a filename.
	 */
	public static final Dimension filenameFieldPreferredSize = new Dimension(550,20);

	/**
	 *  standard size for all JTextFields that need to record a filename
	 */
	public static final Dimension filenameFieldMinimumSize = new Dimension(450,20);

	/**
	 *  standard size for all JTextFields that need to record a filename
	 */
	public static final Dimension filenameFieldMaximumSize = new Dimension(1000,20);



	/**
	 *  standard size for all JTextFields that need to record a short text.
	 */
	public static final Dimension shortFieldPreferredSize = new Dimension(350,20);

	/**
	 *  standard size for all JTextFields that need to record a short text
	 */
	public static final Dimension shortFieldMinimumSize = new Dimension(150,20);

	/**
	 *  standard size for all JTextFields that need to record a short text
	 */
	public static final Dimension shortFieldMaximumSize = new Dimension(1000,20);



	/**
	 *  standard size for all JTextFields that need to record a normal length text
	 */
	public static final Dimension textfieldPreferredSize = new Dimension(350,20);

	/**
	 *  standard size for all JTextFields that need to record a normal length text
	 */
	public static final Dimension textfieldMinimumSize = new Dimension(150,20);

	/**
	 *  standard size for all JTextFields that need to record a normal length text
	 */
	public static final Dimension textfieldMaximumSize = new Dimension(1000,20);



	/**
	 *  standard size for all JTextFields that need to record a normal length text
	 */
	public static final Dimension shortNumberPreferredSize = new Dimension(60,20);

	/**
	 *  standard size for all JTextFields that need to record a normal length text
	 */
	public static final Dimension shortNumberMinimumSize = new Dimension(60,20);

	/**
	 *  standard size for all JTextFields that need to record a normal length text
	 */
	public static final Dimension shortNumberMaximumSize = new Dimension(100,20);



	/**
	 *   fixed size for the threeDotButton which opens the JFileChooser dialog
	 */
	public static final Dimension threeDotButtonSize = new Dimension(25,20);
	

	/**
	 *  the font used to display the title. Currently Arial Bold 20.
	 */
	public static final Font titleFont = new Font("Arial", Font.BOLD, 20);
 
	/**
	 *  the font used to display the captions. Currently Arial Plain 16
	 */
	public static final Font captionFont = new Font("Arial", Font.PLAIN, 16) ;


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
	 *  The locale to be used for the application
	 */
	public static Locale currentLocale = Locale.getDefault();

	/**
	 *  The langauge to be used for the application
	 */
	public static String currentLanguage = "English";


	/**
	 *  The default number of pictures per row for the Html export
	 */
	public static int defaultHtmlPicsPerRow = 3;

	/**
	 *  The default width for pictures for the Html export overview
	 */
	public static int defaultHtmlThumbnailWidth = 150;

	/**
	 *  The default height for pictures for the Html export overview
	 */
	public static int defaultHtmlThumbnailHeight = 100;

	/**
	 *  The default midres width for pictures for the Html export
	 */
	public static int defaultHtmlMidresWidth = 450;

	/**
	 *  The default midres height for pictures for the Html export
	 */
	public static int defaultHtmlMidresHeight = 450;


	/**
	 *   The default color for the background on the web page is white.
	 */	
	public static Color htmlBackgroundColor = Color.WHITE;
	
	/**
	 *  This constant defines the text color on the web page.
	 */
	public static Color htmlFontColor = Color.BLACK;



	/**
	 *  The default size for pictures for the Html export
	 */
	public static float defaultJpgQuality = 0.8f;

	/**
	 *  true when thumbnails are supposed to scale fast
	 */
	public static boolean thumbnailFastScale = true;
	

	/**
	 *  true when the pictureViewer is supposed to scale fast
	 */
	public static boolean pictureViewerFastScale = true;


	/**
	 *   a Vector referring to the objects that want to find out about changes to the recently opened files
	 */
	private static Vector recentOpenFileListeners = new Vector();


	/**
	 *  Default size for buttons such as OK, cancel etc.
	 */
	public static Dimension defaultButtonDimension = new Dimension(80, 25);


	/**
	 *  Default size for buttons such as OK, cancel etc.
	 */
	public static Dimension threeDotButtonDimension = new Dimension(25, 25);



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
	 *	list of cameras
	 */
	public static Vector Cameras = new Vector(); 


	/**
	 *	list of email senders
	 */
	public static TreeSet emailSenders = new TreeSet() {
		public boolean add( Object o ) {
			boolean b = super.add( o );
			if ( b ) unsavedSettingChanges = true;
			return b;
		}
	
	}; 


	/**
	 *	list of email senders
	 */
	public static TreeSet emailRecipients = new TreeSet(){
		public boolean add( Object o ) {
			boolean b = super.add( o );
			if ( b ) unsavedSettingChanges = true;
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
	public static Dimension emailDimensions = new Dimension( 350, 300);

	/**
	 *	Should emails contain the original images
	 */
	public static boolean emailSendOriginal = false;
	
	


	/**
	 *  constructor
	 */
	public Settings() {
		setDefaults();
	}


	/**
	 *  method that set the default parameters
	 */
	public static void setDefaults() {
		Rectangle screenDimensions = Tools.getScreenDimensions();

		setLanguage( currentLocale );

		autoLoad = "";
		logfile = new File( new File( System.getProperty("java.io.tmpdir")), "JPO.log");
		saveSizeOnExit = false;
		mainFrameDimensions = new Rectangle( screenDimensions );
		preferredMasterDividerSpot = 350;
		preferredLeftDividerSpot = screenDimensions.height - 200;
		if ( preferredLeftDividerSpot < 0 ) { preferredLeftDividerSpot = 100; };
		dividerWidth = 12;
	

		maximumPictureSize = 6000;
		maxCache = 4;
		//leaveForPanel = 45; //KDE
		leaveForPanel = 27; // Windows
		pictureViewerDefaultDimensions = new Rectangle( screenDimensions );

		maxThumbnails = 50;
		thumbnailSize = 350;
		keepThumbnails = true;
		thumbnailPath = new File( new File( System.getProperty("java.io.tmpdir")) , "JPO_thumbnails" + File.separator);
		
		dontEnlargeSmallImages = true;			
	}


	public static void setLanguage ( Locale currentLocale ) {
		// overriden for testing
		// currentLocale = Locale.GERMANY;
		// currentLocale = Locale.JAPAN;
		try {
			jpoResources = ResourceBundle.getBundle("jpo.JpoResources", currentLocale);
		} catch ( MissingResourceException mre ) {
			Tools.log("Settings.setDefaults: MissingResourceException: " + mre.getMessage());
			jpoResources = ResourceBundle.getBundle("jpo.JpoResources", Locale.ENGLISH);
		}
	}
	
	public static void setLanguage ( String language ) {
		if ( language.equals("Deutsch") ) {
			setLanguage( Locale.GERMANY );
			currentLanguage = language;
		} else {
			setLanguage( Locale.ENGLISH );
			currentLanguage = "English";
		}
	}
	





	/**
	 *  method that reads the Jpo.ini file
	 */
	public static void loadSettings() {
		setDefaults();
		
		loadCameraSettings();
		createFirstCameraIfEmpty();	
		
		BufferedReader in = null;

		try {
			PersistenceService ps = (PersistenceService) ServiceManager.lookup( "javax.jnlp.PersistenceService" );
			BasicService bs = (BasicService) ServiceManager.lookup( "javax.jnlp.BasicService" );
			try {
				URL baseURL = bs.getCodeBase();
				URL settingsURL = new URL( baseURL, "Settings" );
				ps.get( settingsURL );
				FileContents fc = ps.get( settingsURL );
				in = new BufferedReader( new InputStreamReader( fc.getInputStream() ) );
				Tools.log("Setting.loadSettings: Running in Java Web Start Mode and found PersistenceService for Settings." );
			} catch ( MalformedURLException x ) {
				Tools.log( "We had a MalformedURLException: " + x.getMessage() );
				return;
			} catch ( IOException x ) {
				Tools.log( "Settings.loadSettings: There are no settings that could be read." );
				return;
			}
		} catch ( UnavailableServiceException x ) {
			Tools.log( "Settings.loadSettings: Running in local file mode. Trying to locate ini file " + iniFile.getPath() );
			if ( ! iniFile.exists() ) {
				Tools.log("Settings.loadSettings: Can't find ini File. Using defaults." );
				return;
			}
			try {
				in = new BufferedReader(new FileReader( iniFile ));
			} catch ( FileNotFoundException y ) {
				Tools.log("Settings.loadSettings: Can't find ini File. Using defaults." );
				return;
			}
		}

		
		try {

			String sb = new String();

			int recentCollectionsIndex = 0;
			int copyLocationsIndex = 0;
			int userFunctionNameIndex = 0;
			int userFunctionCmdIndex = 0;

			while (in.ready()) {
				sb = in.readLine();


				
				if (sb.startsWith("leaveForPanel")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					leaveForPanel = Integer.parseInt(Value);
				} else if (sb.startsWith("maxThumbnails")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					maxThumbnails = Integer.parseInt(Value);
				} else if (sb.startsWith("currentLanguage")) {
					setLanguage ( sb.substring(sb.indexOf("=") + 2) );
				} else if (sb.startsWith("thumbnailSize")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					thumbnailSize = Integer.parseInt(Value);
				} else if (sb.startsWith("mainFrameDimensions-X")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					mainFrameDimensions.x = Integer.parseInt(Value);
				} else if (sb.startsWith("mainFrameDimensions-Y")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					mainFrameDimensions.y = Integer.parseInt(Value);
				} else if (sb.startsWith("mainFrameDimensions-Width")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					mainFrameDimensions.width = Integer.parseInt(Value);
				} else if (sb.startsWith("mainFrameDimensions-Height")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					mainFrameDimensions.height = Integer.parseInt(Value);
				} else if (sb.startsWith("preferredMasterDividerSpot")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					preferredMasterDividerSpot = Integer.parseInt(Value);
				} else if (sb.startsWith("preferredLeftDividerSpot")) {
					Tools.log(" --> loading " + sb);
					String Value = sb.substring(sb.indexOf("=") + 2);
					preferredLeftDividerSpot = Integer.parseInt(Value);
				} else if (sb.startsWith("dividerWidth")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					dividerWidth = Integer.parseInt(Value);
				} else if (sb.startsWith("pictureViewerDefaultDimensions-X")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					pictureViewerDefaultDimensions.x = Integer.parseInt(Value);
				} else if (sb.startsWith("pictureViewerDefaultDimensions-Y")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					pictureViewerDefaultDimensions.y = Integer.parseInt(Value);
				} else if (sb.startsWith("pictureViewerDefaultDimensions-Width")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					pictureViewerDefaultDimensions.width = Integer.parseInt(Value);
				} else if (sb.startsWith("pictureViewerDefaultDimensions-Height")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					pictureViewerDefaultDimensions.height = Integer.parseInt(Value);
				} else if (sb.startsWith("autoload")) {
					autoLoad = sb.substring(sb.indexOf("=") + 2);
				} else if (sb.startsWith("copyLocations") 
					&& ( copyLocationsIndex < maxCopyLocations ) ) {
					copyLocations[ copyLocationsIndex ] = sb.substring(sb.indexOf("=") + 2);
					Tools.log( "copyLocations[" + Integer.toString( copyLocationsIndex ) + "] is set to " + copyLocations[ copyLocationsIndex ] );
					copyLocationsIndex++;
				} else if (sb.startsWith("recentCollections") 
					&& ( recentCollectionsIndex < recentFiles ) ) {
					recentCollections[ recentCollectionsIndex ] = sb.substring(sb.indexOf("=") + 2);
					Tools.log( "recentCollections[" + Integer.toString( recentCollectionsIndex ) + "] is set to " + recentCollections[ recentCollectionsIndex ] );
					recentCollectionsIndex++;
				} else if (sb.startsWith("userFunctionName") 
					&& ( userFunctionNameIndex < maxUserFunctions ) ) {
					userFunctionNames[ userFunctionNameIndex ] = sb.substring(sb.indexOf("=") + 2);
					Tools.log( "userFunctionNames[" + Integer.toString( userFunctionNameIndex ) + "] is set to " + userFunctionNames[ userFunctionNameIndex ] );
					userFunctionNameIndex++;
				} else if (sb.startsWith("userFunctionCmd") 
					&& ( userFunctionCmdIndex < maxUserFunctions ) ) {
					userFunctionCmd[ userFunctionCmdIndex ] = sb.substring(sb.indexOf("=") + 2);
					Tools.log( "userFunctionCmd[" + Integer.toString( userFunctionCmdIndex ) + "] is set to " + userFunctionCmd[ userFunctionCmdIndex ] );
					userFunctionCmdIndex++;
				} else if (sb.startsWith("keepThumbnails")) {
					if ( (sb.substring(sb.indexOf("=") + 2)).startsWith( "False" ) )
						keepThumbnails = false;
				} else if (sb.startsWith("thumbnailPath")) {
					thumbnailPath = new File( sb.substring(sb.indexOf("=") + 2));
				} else if (sb.startsWith("dontEnlargeSmallImages")) {
					if ( (sb.substring(sb.indexOf("=") + 2)).startsWith( "False" ) )
						dontEnlargeSmallImages = false;
				} else if (sb.startsWith("thumbnailCounter")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					thumbnailCounter = Integer.parseInt(Value);
				} else if (sb.startsWith("logfile")) {
					logfile = new File( sb.substring(sb.indexOf("=") + 2));
				} else if (sb.startsWith("writeLog")) {
					if ( (sb.substring(sb.indexOf("=") + 2)).startsWith( "True" ) )
						writeLog = true;
					else 
						writeLog = false;
				} else if (sb.startsWith("saveSizeOnExit")) {
					if ( (sb.substring(sb.indexOf("=") + 2)).startsWith( "True" ) )
						saveSizeOnExit = true;
					else 
						saveSizeOnExit = false;
				} else if (sb.startsWith("maxCache")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					//Tools.log("maxCache: >" + Value + "<");
					maxCache = Integer.parseInt(Value);
				} else if (sb.startsWith("maximumPictureSize")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					//Tools.log("maxCache: >" + Value + "<");
					maximumPictureSize = Integer.parseInt(Value);
				} else if (sb.startsWith("defaultHtmlPicsPerRow")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					//Tools.log("maxCache: >" + Value + "<");
					defaultHtmlPicsPerRow = Integer.parseInt(Value);
				} else if (sb.startsWith("defaultHtmlThumbnailWidth")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					//Tools.log("maxCache: >" + Value + "<");
					defaultHtmlThumbnailWidth = Integer.parseInt(Value);
				} else if (sb.startsWith("defaultHtmlThumbnailHeight")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					//Tools.log("maxCache: >" + Value + "<");
					defaultHtmlThumbnailHeight = Integer.parseInt(Value);
				} else if (sb.startsWith("defaultHtmlMidresWidth")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					//Tools.log("maxCache: >" + Value + "<");
					defaultHtmlMidresWidth = Integer.parseInt(Value);
				} else if (sb.startsWith("defaultHtmlMidresHeight")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					//Tools.log("maxCache: >" + Value + "<");
					defaultHtmlMidresHeight = Integer.parseInt(Value);
				} else if (sb.startsWith("defaultJpgQuality")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					//Tools.log("maxCache: >" + Value + "<");
					defaultJpgQuality = Float.parseFloat(Value);
				} else if (sb.startsWith("thumbnailFastScale")) {
					if ( (sb.substring(sb.indexOf("=") + 2)).startsWith( "True" ) )
						thumbnailFastScale = true;
					else 
						thumbnailFastScale = false;
				} else if (sb.startsWith("pictureViewerFastScale")) {
					if ( (sb.substring(sb.indexOf("=") + 2)).startsWith( "True" ) )
						pictureViewerFastScale = true;
					else 
						pictureViewerFastScale = false;
				} else if (sb.startsWith("emailSender")) {
					emailSenders.add(sb.substring(sb.indexOf("=") + 2) );
				} else if (sb.startsWith("emailRecipient")) {
					emailRecipients.add(sb.substring(sb.indexOf("=") + 2) );
				} else if (sb.startsWith("emailServer")) {
					emailServer = sb.substring( sb.indexOf("=") + 2 ); 
				} else if (sb.startsWith("emailPort")) {
					emailPort = sb.substring( sb.indexOf("=") + 2 ); 
				} else if (sb.startsWith("emailUser")) {
					emailUser = sb.substring( sb.indexOf("=") + 2 ); 
				} else if (sb.startsWith("emailPassword")) {
					emailPassword = sb.substring( sb.indexOf("=") + 2 ); 
				} else if (sb.startsWith("emailScaleImages")) {
					if ( (sb.substring(sb.indexOf("=") + 2)).startsWith( "True" ) )
						emailScaleImages = true;
					else 
						emailScaleImages = false;
				} else if (sb.startsWith("emailSendOriginal")) {
					if ( (sb.substring(sb.indexOf("=") + 2)).startsWith( "True" ) )
						emailSendOriginal = true;
					else 
						emailSendOriginal = false;
				} else if (sb.startsWith("emailDimensions.width")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					emailDimensions.width = Integer.parseInt(Value);
				} else if (sb.startsWith("emailDimensions.height")) {
					String Value = sb.substring(sb.indexOf("=") + 2);
					emailDimensions.height = Integer.parseInt(Value);
				} else if ( sb.startsWith("#") || sb.equals("") ) {
				} else {
					System.out.println("Can't decode ini line: " + sb);
				}


			}
			validateCopyLocations();
			//validateSettings();
		} catch (IOException e) {
			Tools.log( "IOException " + e.getMessage() );
			JOptionPane.showMessageDialog(
				Settings.anchorFrame, 
				Settings.jpoResources.getString("cantReadIniFile") + e.getMessage(), 
				Settings.jpoResources.getString("settingsError"), 
				JOptionPane.ERROR_MESSAGE);
		}

	}



	/**
	 *  method that validates the settings & brings up the Settings dialog if not ok
	 */
	public static void validateSettings() {
		if ( keepThumbnails ) {
			if (! thumbnailPath.exists()) {
				try {
					thumbnailPath.mkdirs();
				} catch ( SecurityException x ) {
					// do nothing here because
					// the error situation will 
					// be flagged in the next steps.
				}
			}
			if ( ! thumbnailPath.exists() ) {
				JOptionPane.showMessageDialog(Settings.anchorFrame, 
					Settings.jpoResources.getString("thumbNoExistError"), 
					Settings.jpoResources.getString("settingsError"), 
					JOptionPane.ERROR_MESSAGE);
				keepThumbnails = false;
			} else if ( ! thumbnailPath.canWrite() ) {
				JOptionPane.showMessageDialog(Settings.anchorFrame, 
					Settings.jpoResources.getString("thumbNoWriteError"), 
					Settings.jpoResources.getString("settingsError"), 
					JOptionPane.ERROR_MESSAGE);
				keepThumbnails = false;
			} else if ( ! thumbnailPath.isDirectory() ) {
				JOptionPane.showMessageDialog(Settings.anchorFrame, 
					Settings.jpoResources.getString("thumbNoDirError"), 
					Settings.jpoResources.getString("settingsError"), 
					JOptionPane.ERROR_MESSAGE);
				keepThumbnails = false;
			}
		}


		if ( writeLog ) {
			if ( logfile.exists() ) {
				if  ( ! logfile.canWrite() ) {
					JOptionPane.showMessageDialog(Settings.anchorFrame, 
						Settings.jpoResources.getString("logFileCanWriteError"), 
						Settings.jpoResources.getString("settingsError"), 
						JOptionPane.ERROR_MESSAGE);
					writeLog = false;
				}
				if ( ! logfile.isFile() ) {
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
				}
				if ( ! testFileParent.canWrite() ){
					JOptionPane.showMessageDialog(Settings.anchorFrame, 
						Settings.jpoResources.getString("logFileCanWriteError"), 
						Settings.jpoResources.getString("settingsError"), 
						JOptionPane.ERROR_MESSAGE);
					writeLog = false;
				}
			} 

		}
		
		notifyRecentFilesChanged();

	}



	/**
	 *  method that writes the Jpo.ini file
	 */
	public static void writeSettings() {
		//Tools.log( "Settings.writeSettings" );
		BufferedWriter out = null;
		
		try {
			PersistenceService ps = (PersistenceService) ServiceManager.lookup( "javax.jnlp.PersistenceService" );
			BasicService bs = (BasicService) ServiceManager.lookup( "javax.jnlp.BasicService" );
			try {
				URL baseURL = bs.getCodeBase();
				//Tools.log( "CodeBase was " + baseURL.toString() );
				URL settingsURL = new URL( baseURL, "Settings" );
				try {
					ps.delete( settingsURL );
				} catch ( IOException x ) {
					// it doesn't matter if we can't delete the file.
					Tools.log( "Settings.writeSettings: Caught an IOException when trying to delete the file. Perhaps it didn't exist?. Continuing. Error message: " + x.getMessage() );
				}
				ps.create( settingsURL, 4096 );
				FileContents fc = ps.get( settingsURL );
				Tools.log ( "Settings.writeSettings: Running in Java Web Start setting and writing settings to PersistenceService: " + baseURL.toString() + "/" + fc.getName() );
				out = new BufferedWriter( new OutputStreamWriter( fc.getOutputStream( true ) ) );
			} catch ( MalformedURLException x ) {
				Tools.log( "We had a MalformedURLException: " + x.getMessage() );
				return;
			} catch ( IOException x ) {
				Tools.log( "We had an IOException: " + x.getMessage() );
				return;
			}
		} catch ( UnavailableServiceException x ) {
			Tools.log( "Settings.writeSettings: no PersistenceService available: writing to local file: " + iniFile.getPath() );
			try {
				out = new BufferedWriter(new FileWriter( iniFile));
			} catch ( IOException y ) {
				Tools.log ("Settings.writeSettings: can't create ini File. Aborting. Error: " + y.getMessage() );
				return;
			}
		}
			
		
		try {
		
			
			out.write("# This is the settings file for the JPO application.");
			out.newLine();
			out.write("# You can delete it and the application will revert to the defaults.");
			out.newLine();
			out.write("# This file gets created when you click Save in the settlings dialog.");
			out.newLine();
			out.newLine();


			out.write("currentLanguage = " + String.valueOf( currentLanguage ) );
			out.newLine();

			out.write("maximumPictureSize = " + String.valueOf( maximumPictureSize ) );
			out.newLine();

			out.write("leaveForPanel = " + String.valueOf( leaveForPanel ) );
			out.newLine();

			out.write("maxThumbnails = " + String.valueOf( maxThumbnails ) );
			out.newLine();
			
			out.write("thumbnailSize = " + String.valueOf( thumbnailSize ) );
			out.newLine();

			if ( saveSizeOnExit )
				out.write( "saveSizeOnExit = True" );
			else
				out.write( "saveSizeOnExit = False" );
			out.newLine();

			out.write("mainFrameDimensions-X = " + String.valueOf( (int) mainFrameDimensions.getX() ) );
			out.newLine();

			out.write("mainFrameDimensions-Y = " + String.valueOf( (int) mainFrameDimensions.getY() ) );
			out.newLine();

			out.write("mainFrameDimensions-Width = " + String.valueOf( (int) mainFrameDimensions.getWidth() ) );
			out.newLine();

			out.write("mainFrameDimensions-Height = " + String.valueOf( (int) mainFrameDimensions.getHeight() ) );
			out.newLine();

			out.write( "preferredMasterDividerSpot = " + String.valueOf( preferredMasterDividerSpot ) );
			out.newLine();

			out.write( "preferredLeftDividerSpot = " + String.valueOf( preferredLeftDividerSpot ) );
			out.newLine();

			out.write("dividerWidth = " + String.valueOf(dividerWidth));
			out.newLine();

			out.write("autoload = " + autoLoad);
			out.newLine();


			out.write("pictureViewerDefaultDimensions-X = " + String.valueOf((int) pictureViewerDefaultDimensions.getX()) );
			out.newLine();

			out.write("pictureViewerDefaultDimensions-Y = " + String.valueOf((int) pictureViewerDefaultDimensions.getY()) );
			out.newLine();

			out.write("pictureViewerDefaultDimensions-Width = " + String.valueOf((int)pictureViewerDefaultDimensions.getWidth()));
			out.newLine();

			out.write("pictureViewerDefaultDimensions-Height = " + String.valueOf((int)pictureViewerDefaultDimensions.getHeight()));
			out.newLine();


			for ( int i = 0; i < Settings.maxCopyLocations; i++ ) {
				if ( copyLocations[ i ] != null ) {
					out.write( "copyLocations = " + copyLocations[ i ] );
					out.newLine();
				}
			}
			
			
			for ( int i = 0; i < Settings.recentFiles; i++ ) {
				if ( recentCollections[ i ] != null ) {
					out.write( "recentCollections = " + recentCollections[ i ] );
					out.newLine();
				}
			}
			

			for ( int i = 0; i < Settings.maxUserFunctions; i++ ) {
				if (( userFunctionNames[ i ] != null ) 
				 && ( userFunctionNames[ i ].length() > 0 ) 
				 && ( userFunctionCmd[ i ] != null )
				 && ( userFunctionCmd[ i ].length() > 0 ) ) {
					out.write( "userFunctionName = " + userFunctionNames[ i ] );
					out.newLine();
					out.write( "userFunctionCmd = " + userFunctionCmd[ i ] );
					out.newLine();
				}
			}
			

			if ( keepThumbnails ) 
				out.write( "keepThumbnails = True" );
			else
				out.write( "keepThumbnails = False" );
			out.newLine();

			out.write("thumbnailPath = " + thumbnailPath.getPath() );
			out.newLine();

			if ( dontEnlargeSmallImages ) 
				out.write( "dontEnlargeSmallImages = True" );
			else
				out.write( "dontEnlargeSmallImages = False" );
			out.newLine();


			out.write("thumbnailCounter = " + String.valueOf(thumbnailCounter));
			out.newLine();


			out.write( "logfile = " + logfile.getPath() );
			out.newLine();
			
			if ( writeLog )
				out.write( "writeLog = True" );
			else
				out.write( "writeLog = False" );
			out.newLine();

			out.write("maxCache = " + String.valueOf( maxCache ));
			out.newLine();

			out.write("defaultHtmlPicsPerRow = " + String.valueOf( defaultHtmlPicsPerRow ));
			out.newLine();

			out.write("defaultHtmlThumbnailWidth = " + String.valueOf( defaultHtmlThumbnailWidth ));
			out.newLine();
			out.write("defaultHtmlThumbnailHeight = " + String.valueOf( defaultHtmlThumbnailHeight ));
			out.newLine();

			out.write("defaultHtmlMidresWidth = " + String.valueOf( defaultHtmlMidresWidth ));
			out.newLine();
			out.write("defaultHtmlMidresHeight = " + String.valueOf( defaultHtmlMidresHeight ));
			out.newLine();

			out.write("defaultJpgQuality = " + String.valueOf( defaultJpgQuality ));
			out.newLine();


			if ( thumbnailFastScale ) 
				out.write( "thumbnailFastScale = True" );
			else
				out.write( "thumbnailFastScale = False" );
			out.newLine();

			if ( pictureViewerFastScale ) 
				out.write( "pictureViewerFastScale = True" );
			else
				out.write( "pictureViewerFastScale = False" );
			out.newLine();


			Iterator i = emailSenders.iterator();
			while ( i.hasNext() ) {
				out.write( "emailSender = " + (String) i.next() );
				out.newLine();
			}

			i = emailRecipients.iterator();
			while ( i.hasNext() ) {
				out.write( "emailRecipient = " + (String) i.next() );
				out.newLine();
			}


			out.write( "emailServer = " + emailServer );
			out.newLine();

			out.write( "emailPort = " + emailPort );
			out.newLine();

			out.write( "emailUser = " + emailUser );
			out.newLine();

			out.write( "emailPassword = " + emailPassword );
			out.newLine();


			if ( emailScaleImages ) 
				out.write( "emailScaleImages = True" );
			else
				out.write( "emailScaleImages = False" );
			out.newLine();

			if ( emailSendOriginal ) 
				out.write( "emailSendOriginal = True" );
			else
				out.write( "emailSendOriginal = False" );
			out.newLine();

			out.write("emailDimensions.width = " + String.valueOf( emailDimensions.width ) );
			out.newLine();

			out.write("emailDimensions.height = " + String.valueOf( Settings.emailDimensions.height ) );
			out.newLine();


			out.close();
			unsavedSettingChanges = false;
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
				Settings.anchorFrame, 
				Settings.jpoResources.getString("cantWriteIniFile" + e.getMessage() ), 
				Settings.jpoResources.getString("settingsError"), 
				JOptionPane.ERROR_MESSAGE);
		}
	}



	public static void writeCameraSettings () {		
		Tools.log( "Writing Camera Settings" );
		OutputStream out;
		
		try {
			PersistenceService ps = (PersistenceService) ServiceManager.lookup( "javax.jnlp.PersistenceService" );
			BasicService bs = (BasicService) ServiceManager.lookup( "javax.jnlp.BasicService" );
			try {
				URL baseURL = bs.getCodeBase();
				//Tools.log( "CodeBase was " + baseURL.toString() );
				URL camerasURL = new URL( baseURL, "Cameras" );
				try {
					ps.delete( camerasURL );
				} catch ( IOException x ) {
					// it doesn't matter if we can't delete the file.
					Tools.log( "Settings.writeSettings: Caught an IOException when trying to delete the file. Perhaps it didn't exist?. Continuing. Error message: " + x.getMessage() );
				}
				ps.create( camerasURL, 4096 );
				FileContents fc = ps.get( camerasURL );
				Tools.log ( "Settings.writeSettings: Running in Java Web Start setting and writing settings to PersistenceService: " + baseURL.toString() + "/" + fc.getName() );
				out = fc.getOutputStream( true );
			} catch ( MalformedURLException x ) {
				Tools.log( "We had a MalformedURLException: " + x.getMessage() );
				return;
			} catch ( IOException x ) {
				Tools.log( "We had an IOException: " + x.getMessage() );
				return;
			}
		} catch ( UnavailableServiceException x ) {
			Tools.log( "Settings.writeSettings: no PersistenceService available: writing to local file: " + camerasFile.getPath() );
			try {
				out = new FileOutputStream( camerasFile );
			} catch ( IOException y ) {
				Tools.log ("Settings.writeSettings: can't create cameras File. Aborting. Error: " + y.getMessage() );
				return;
			}
		}
			
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream( out );

			oos.writeObject( Cameras );
			oos.close();
		} catch ( IOException x ) {
			Tools.log("Settings.writeCameraSettings failed on an IOException: " + x.getMessage());
		}
	}


	public static void loadCameraSettings () {		
		Tools.log( "Settings.loadCameraSettings: Loading Camera Settings" );
		InputStream in;
		try {
			PersistenceService ps = (PersistenceService) ServiceManager.lookup( "javax.jnlp.PersistenceService" );
			BasicService bs = (BasicService) ServiceManager.lookup( "javax.jnlp.BasicService" );
			try {
				URL baseURL = bs.getCodeBase();
				URL camerasURL = new URL( baseURL, "Cameras" );
				ps.get( camerasURL );
				FileContents fc = ps.get( camerasURL );
				in = fc.getInputStream();
				Tools.log("Setting.loadSettings: Running in Java Web Start Mode and found PersistenceService for Settings." );
			} catch ( MalformedURLException x ) {
				Tools.log( "We had a MalformedURLException: " + x.getMessage() );
				return;
			} catch ( IOException x ) {
				Tools.log( "Settings.loadSettings: There are no settings that could be read." );
				return;
			}
		} catch ( UnavailableServiceException x ) {
			Tools.log( "Settings.loadSettings: Running in local file mode. Trying to locate ini file " + camerasFile.getPath() );
			if ( ! iniFile.exists() ) {
				Tools.log("Settings.loadSettings: Can't find ini File. Using defaults." );
				return;
			}
			try {
				in = new FileInputStream( camerasFile );
			} catch ( FileNotFoundException y ) {
				Tools.log("Settings.loadSettings: Can't find ini File. Using defaults." );
				return;
			}
		}

		try{
			ObjectInputStream ois = new ObjectInputStream( in );

			Cameras = (Vector) ois.readObject();
			ois.close();
		} catch ( IOException x ) {
			Tools.log ("Settings.loadCameraSettings failed with an IOException: " + x.getMessage());
		} catch ( ClassNotFoundException x ) {
			Tools.log ("Settings.loadCameraSettings failed with an ClassNotFoundException: " + x.getMessage());
		}
	}


	/**
	 *   Creates a first camera in the table if we have no cameras yet.
	 */
	private static void createFirstCameraIfEmpty() {
		Tools.log( "Settings.createFirstCameraIfEmpty: invoked" );
		if ( Cameras.isEmpty() ) {
			Camera cam = new Camera();
			Cameras.add( cam );
		}
	}


	

	/**
	 *  every time a collection is opened this function is called for storing
	 *  the collection name in the Open Recent menu
	 */
	public static void pushRecentCollection( String recentFile ) {
		for ( int i = 0; i < Settings.recentFiles; i++ ) {
			if ( ( recentCollections[ i ] != null ) &&
				( recentCollections[ i ].equals( recentFile ) ) ) {
				// it was already in the list make it the first one
				for ( int j = i; j > 0; j-- ) {
					recentCollections[ j ] = recentCollections[ j - 1 ];
				}
				recentCollections[ 0 ] = recentFile;
				notifyRecentFilesChanged();
				return;
			}
		}
		
		// move all the elements down by one
		for ( int i = Settings.recentFiles - 1; i > 0; i-- ) {
			recentCollections[ i ] = recentCollections[ i - 1 ];
		}
		recentCollections[ 0 ] = recentFile;
		notifyRecentFilesChanged();
		writeSettings();
	}



	private static void notifyRecentFilesChanged() {
 		Enumeration e = recentOpenFileListeners.elements();
		while ( e.hasMoreElements() ) {
			((RecentOpenFileListener) e.nextElement()).recentFilesChanged();
		}
	}
	

	/**
	 *  method to register the listening object of the status events
	 */
	public static void addRecentOpenFileListener ( RecentOpenFileListener listener) {
		recentOpenFileListeners.add( listener );
	}


	/**
	 *  method to register the listening object of the status events
	 */
	public static void removeRecentOpenFileListener ( RecentOpenFileListener listener ) {
		recentOpenFileListeners.remove( listener );
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
	public static SortableDefaultMutableTreeNode []  recentDropNodes = new SortableDefaultMutableTreeNode[ maxDropNodes ];
	    

	/**
	 *  This method memorizes the recent drop targets so that they can be accessed 
	 *  more quickly next time round.
	 */
	public static void memorizeGroupOfDropLocation( SortableDefaultMutableTreeNode recentNode ) {
		for ( int i = 0; i < Settings.maxDropNodes; i++ ) {
			if ( ( recentDropNodes[ i ] != null ) &&
				( recentDropNodes[ i ].hashCode() == recentNode.hashCode() ) ) {
				//Tools.log( "Settings.memorizeGroupOfDropLocation: node was already in the list make it the first one.");
				for ( int j = i; j > 0; j-- ) {
					recentDropNodes[ j ] = recentDropNodes[ j - 1 ];
				}
				recentDropNodes[ 0 ] = recentNode;
				return;
			}
		}
		
		// move all the elements down by one
		for ( int i = Settings.maxDropNodes - 1; i > 0; i-- ) {
			recentDropNodes[ i ] = recentDropNodes[ i - 1 ];
		}
		recentDropNodes[ 0 ] = recentNode;
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
	public static void addRecentDropNodeListener ( RecentDropNodeListener listener) {
		recentDropNodeListeners.add( listener );
	}

	/**
	 *  method to register the listening object of the status events
	 */
	public static void removeRecentDropNodeListener ( RecentDropNodeListener listener ) {
		recentDropNodeListeners.remove( listener );
	}


	/**
	 *  notifies the listeners that the target drop nodes have changed.
	 */
	private static void notifyRecentDropNodesChanged() {
 		Enumeration e = recentDropNodeListeners.elements();
		while ( e.hasMoreElements() ) {
			((RecentDropNodeListener) e.nextElement()).recentDropNodesChanged();
		}
	} 

	/**
	 *  method to remove one of the recent Drop Nodes. It is important to check each time a node 
	 *  is deleted whether it or one of it's descendents is a drop target as this would no longer 
	 *  be a valid target.
	 */
	public static void removeRecentDropNode ( SortableDefaultMutableTreeNode deathNode ) {
	      for ( int i=0; i < recentDropNodes.length; i++ ) {
		      if ( deathNode == recentDropNodes[ i ] ) {
			      recentDropNodes[ i ] = null;
			      Settings.notifyRecentDropNodesChanged();
		      }
	      }
	}

	/**
	 *  clears the list of recent drop nodes
	 */
	public static void clearRecentDropNodes() {
		recentDropNodes = new SortableDefaultMutableTreeNode[ maxDropNodes ];
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
	public static String []  copyLocations = new String[ maxCopyLocations ];


	/**
	 *  This method memorizes the copy targets so that they can be accessed 
	 *  more quickly next time round.
	 */
	public static void memorizeCopyLocation( String location ) {
		for ( int i = 0; i < Settings.maxCopyLocations; i++ ) {
			if ( ( copyLocations[ i ] != null ) 
			  && ( copyLocations[ i ].equals( location ) ) ) {
				for ( int j = i; j > 0; j-- ) {
					copyLocations[ j ] = copyLocations[ j - 1 ];
				}
				copyLocations[ 0 ] = location;
				return;
			}
		}
		
		// move all the elements down by one
		for ( int i = Settings.maxCopyLocations - 1; i > 0; i-- ) {
			copyLocations[ i ] = copyLocations[ i - 1 ];
		}
		copyLocations[ 0 ] = location;
		
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
				f = new File(  copyLocations[i] );
				if ( ! f.exists() ) {
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
		return new File( System.getProperty("user.dir") );
	}


	/**
	 *   a Vector referring to the objects that want to find out about changes to the 
	 *   recently drop target nodes.
	 */
	private static Vector copyLocationChangeListeners = new Vector();
	

	/**
	 *  method to register the listening object of the status events
	 */
	public static void addCopyLocationsChangeListener ( CopyLocationsChangeListener listener) {
		copyLocationChangeListeners.add( listener );
	}

	/**
	 *  method to register the listening object of the status events
	 */
	public static void removeCopyLocationsChangeListener ( CopyLocationsChangeListener listener ) {
		copyLocationChangeListeners.remove( listener );
	}


	/**
	 *  notifies the listeners that the target drop nodes have changed.
	 */
	private static void notifyCopyLocationsChanged() {
 		Enumeration e = copyLocationChangeListeners.elements();
		while ( e.hasMoreElements() ) {
			((CopyLocationsChangeListener) e.nextElement()).copyLocationsChanged();
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
	public static String []  userFunctionNames = new String[ maxUserFunctions ];

	/**
	 *  Array of user fucntion commands
	 */
	public static String []  userFunctionCmd = new String[ maxUserFunctions ];



	/**
	 *   a Vector referring to the objects that want to find out about changes to the 
	 *   recently drop target nodes.
	 */
	private static Vector userFunctionsChangeListeners = new Vector();
	

	/**
	 *  method to register the listening object of the status events
	 */
	public static void addUserFunctionsChangeListener ( UserFunctionsChangeListener listener) {
		userFunctionsChangeListeners.add( listener );
	}

	/**
	 *  method to register the listening object of the status events
	 */
	public static void removeUserFunctionsChangeListener ( UserFunctionsChangeListener listener ) {
		userFunctionsChangeListeners.remove( listener );
	}


	/**
	 *  notifies the listeners that the target drop nodes have changed.
	 */
	public static void notifyUserFunctionsChanged() {
 		Enumeration e = userFunctionsChangeListeners.elements();
		while ( e.hasMoreElements() ) {
			((UserFunctionsChangeListener) e.nextElement()).userFunctionsChanged();
		}
	} 





}





