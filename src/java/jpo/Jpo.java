package jpo;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.lang.*;
import javax.jnlp.*;


/*
Jpo.java:  main class of the JPO application

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
 * Jpo is the the main class of a browser application that lets
 * a user view a collection of pictures in as thumbnails, in a seperate window
 * or in a full sized mode.<p>
 *
 * The Jpo class creates the following main objects:
 *
 * <p><img src=../Overview.png border=0><p>
 * 
 * It uses a list of pictures (PictureList file) to create a hirarchical model of 
 * <code>SortableDefaultMutableTreeNode</code>s that represent the structure of the collection. 
 * Each node has an associated object of {@link GroupInfo} or {@link PictureInfo} type.
 *
 * The {@link CleverJTree} visualises the model and allows the user to 
 * expand and collapse branches of the tree with the mouse. If a node is clicked this generates
 * a <code>valueChanged</code> event from the model which is sent to all listening objects.<p>
 * 
 * Listening objects are the thumbnail pane which displays the group if a node of type 
 * <code>GroupInfo</code> has been selected.<p>
 *
 * This listener architecture allows fairly easy expansion of the application
 * since all that is required is that any additional objects that need to be change the picture
 * or need to be informed of a change can connect to the model in this manner and 
 * need no other contorls.
 * 
 * @see CleverJTree
 * @see ThumbnailJScrollPane
 * @see PictureViewer
 *
 *
 * @author 	Richard Eigenmann, richard_eigenmann@compuserve.com
 * @version 	0.8.4
 * @since       JDK1.4.0
 *
 **/
public class Jpo extends JFrame 
		 implements CleverJTreeInterface,
			    ApplicationMenuInterface {
			    

	/**
	 *  This object does all the tree work. It can load and save the nodes of the tree, listens to 
	 *  events happening on the tree and calls back with any actions that should be performed.
	 * 
	 *  @see CleverJTree
	 */
	public static CleverJTree cleverJTree;



	/**
	 *  This oject holds all the thumbnails and deals with all the thumbnail events.
	 **/
	private static ThumbnailJScrollPane thumbnailJScrollPane;




	/**
	 *   the main method is the entry point for this application (or any) 
	 *   Java application. No parameter passing is used in the Jpo application. <p>
	 *
	 *   The method verifies that the user has the correct Java Virtual Machine (> 1.4.0)
	 *   and then created a new {@link Jpo} object.
	 *
	 **/
	public static void main(String[] args) {
		try {
			final String GTK = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
			final String Windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
			final String Metal = "javax.swing.plaf.metal.MetalLookAndFeel";
			final String CDE = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
			
			UIManager.setLookAndFeel( Windows );
		} catch ( Exception e ) { 
			// System.out.println( "Jpo.main: Could not set Look and Feel");
		}


		// Verify that we have to correct version of the jvm
		String jvmVersion = System.getProperty( "java.version");
		String jvmMainVersion = jvmVersion.substring(0, jvmVersion.lastIndexOf("."));
		float jvmVersionFloat = Float.parseFloat( jvmMainVersion );
		if ( jvmVersionFloat < 1.4f ) {
			JOptionPane.showMessageDialog(Settings.anchorFrame, 
				"The JPO application uses new graphics features\n"
				+  "that were added to the Java language in version\n"
				+  "1.4.0. You are using version "
				+  jvmVersion
				+  " and must upgrade.\n"
				+  "Visit http://java.sun.com", 
				"Old Version Error", 
				JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		new Jpo();

		
	}  




	/**
	 *  Constructor for the Jpo application that creates the main JFrame, attaches an 
	 *  {@link ApplicationJMenuBar}, adds a JSplitPane to which it adds the {@link CleverJTree} 
	 *  on the left side and a {@link ThumbnailJScrollPane} on the right side.
	 *
	 **/
	public Jpo() {
		System.out.println ("\nJPO version 0.8.4\n"
			+ "Copyright (C) 2000-2003 Richard Eigenmann\n" 
			+ "JPO comes with ABSOLUTELY NO WARRANTY;\n"
			+ "for details Look at the Help | License menu item.\n"
			+ "This is free software, and you are welcome\n"
			+ "to redistribute it under certain conditions;\n"
			+ "see Help | License for details.\n\n");


		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
				closeJpo();
			}
	        });  


		Settings.loadSettings();
		Settings.validateSettings();
		
		this.setTitle ( Settings.jpoResources.getString("ApplicationTitle"));
			

		Tools.log ("------------------------------------------------------------");
		Tools.log ("Starting JPO on " + DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime() ) );

		
		Settings.anchorFrame = (JFrame) this;

		this.setSize( Settings.mainFrameDimensions.getSize() );
		this.setLocation( Settings.mainFrameDimensions.getLocation() );


		//Create the menu bar.
		JMenuBar menuBar = new ApplicationJMenuBar( this ); 
		setJMenuBar( menuBar );


		
		// Create a first node for the tree 
		Settings.top = new SortableDefaultMutableTreeNode( true ) ;
		
		// Create and attach the JTree object
		JScrollPane TreePane = new JScrollPane();
		cleverJTree = new CleverJTree( this, Settings.top );		
       		TreePane.setViewportView( cleverJTree );


		// Set up the Thumbnail Pane
		thumbnailJScrollPane = new ThumbnailJScrollPane( Settings.top );

		// Set up the Info Panel
		InfoPanel infoPanel = new InfoPanel();
		
		// Set up the communication between the JTree and the Thumbnail Pane
		
		cleverJTree.setAssociatedThumbnailJScrollpane( thumbnailJScrollPane ) ;
		cleverJTree.setAssociatedInfoPanel( infoPanel ) ;
		thumbnailJScrollPane.setAssociatedCleverJTree( cleverJTree );
		thumbnailJScrollPane.setAssociatedInfoPanel( infoPanel ) ;



		// load from jar or load from autoload instruction
		Settings.jarAutostartList = ClassLoader.getSystemResource("autostartJarPicturelist.xml");
		if ( Settings.jarAutostartList != null ) {
			Settings.jarRoot = Settings.jarAutostartList.toString().substring(0, Settings.jarAutostartList.toString().indexOf("!") + 1);
			Tools.log( "Trying to load picturelist from jar: " + Settings.jarAutostartList.toString() );
			try {
				Settings.top.streamLoad( Settings.jarAutostartList.openStream() );
				cleverJTree.setSelectedNode ( Settings.top );
				thumbnailJScrollPane.showGroup( Settings.top );
			} catch ( IOException x ) {
				Tools.log( Settings.jarAutostartList.toString() + " could not be loaded\nReason: " + x.getMessage() );
			}
		} else if ( Settings.autoLoad != "" ) {
			File xmlFile =  new File( Settings.autoLoad );
			Tools.log("Trying to load picturelist from ini: " + Settings.autoLoad );
			if ( xmlFile.exists() ) {
				Settings.top.fileLoad( xmlFile );
				cleverJTree.setSelectedNode ( Settings.top );
				thumbnailJScrollPane.showGroup( Settings.top );
			}
		}
		Settings.top.setUnsavedUpdates( false );



		/**
		 *  The pane that holds the main window. On the left will go the tree, on the
		 *  right will go the thumbnails
		 **/
		final JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		leftSplitPane.setDividerSize( Settings.dividerWidth );
		leftSplitPane.setOneTouchExpandable( true );
		leftSplitPane.setTopComponent( TreePane );
		leftSplitPane.setBottomComponent( infoPanel );
		leftSplitPane.setDividerLocation( Settings.preferredLeftDividerSpot );
		//masterSplitPane.setPreferredSize( Settings.mainFrameDimensions.getSize() );
		infoPanel.addComponentListener(new ComponentAdapter() {
		        public void componentResized( ComponentEvent event ) {
				Tools.log( "Jpo:InfoPanelcomponentResized invoked" );
				int leftDividerSpot = leftSplitPane.getDividerLocation();
				if ( leftDividerSpot != Settings.preferredLeftDividerSpot ) {
					Settings.preferredLeftDividerSpot = leftDividerSpot;
					Settings.unsavedSettingChanges = true;
				}
		        }
		});

		



		/**
		 *  The pane that holds the main window. On the left will go the tree, on the
		 *  right will go the thumbnails
		 **/
		final JSplitPane masterSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		masterSplitPane.setDividerSize( Settings.dividerWidth );
		masterSplitPane.setOneTouchExpandable( true );
		masterSplitPane.setLeftComponent( leftSplitPane );
		masterSplitPane.setRightComponent( thumbnailJScrollPane );
		masterSplitPane.setDividerLocation( Settings.preferredMasterDividerSpot );
		masterSplitPane.setPreferredSize( Settings.mainFrameDimensions.getSize() );
		cleverJTree.addComponentListener(new ComponentAdapter() {
		        public void componentResized( ComponentEvent event ) {
				Tools.log( "Jpo.ThumbnailJScrollPane.componentResized invoked" );
				int dividerSpot = masterSplitPane.getDividerLocation();
				if ( dividerSpot != Settings.preferredMasterDividerSpot ) {
					Settings.preferredMasterDividerSpot = dividerSpot;
					Settings.unsavedSettingChanges = true;
				}
		        }
		});



		//Add the split pane to this frame.
		getContentPane().add( masterSplitPane, BorderLayout.CENTER );

	 	//  As per http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
		Runnable runner = new FrameShower( this );
        	EventQueue.invokeLater(runner);
	}



	/**
	 *  Brings up a QueryJFrame GUI.
  	*/
	public void find( SortableDefaultMutableTreeNode startSearchNode ) {
		new QueryJFrame( startSearchNode, cleverJTree, thumbnailJScrollPane );
	}


	/**
	 *  method that is invoked when the Jpo application is to be closed. Checks if
	 *  the main application window size should be saved and saves if nescessary.
	 *  also checks for unsaved changes before closing the application.
	 */
	public void closeJpo() {
		if ( Settings.top.checkUnsavedChanges() ) return;
		
		if ( Settings.saveSizeOnExit &&
			( ! 
				( Settings.mainFrameDimensions.getSize().equals( this.getSize() ) 
				&& Settings.mainFrameDimensions.getLocation().equals( this.getLocationOnScreen() ) )
			)
		) {
			Tools.log ("A settings save is required because the window has a different shape.");
			Settings.mainFrameDimensions.setLocation( this.getLocationOnScreen() );
			Settings.mainFrameDimensions.setSize( this.getContentPane().getSize() );
			Settings.unsavedSettingChanges = true;
		}
		
		
		if ( Settings.unsavedSettingChanges )
			Settings.writeSettings();
			
		Tools.log ("Exiting JPO: " + DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime() ) );
		Tools.log ("------------------------------------------------------------");
		Tools.closeLogfile();
		
		System.exit(0);
	}



	/**
	 *   displays the Help|About window
	 */
	public void showHelpAboutDialog() {
		JOptionPane.showMessageDialog(this, 
			Settings.jpoResources.getString("HelpAboutText")
			+ Settings.jpoResources.getString("HelpAboutUser")
				+ System.getProperty( "user.name" )
				+ "\n"
			+ Settings.jpoResources.getString("HelpAboutOs")
				+ System.getProperty("os.name")
				+ " "
				+ System.getProperty("os.version")
				+ " "
				+ System.getProperty("os.arch")
				+ "\n"
			+ Settings.jpoResources.getString("HelpAboutJvm")
				+ System.getProperty("java.vendor")
				+ " "
				+ System.getProperty("java.version")
				+ "\n"
			+ Settings.jpoResources.getString("HelpAboutJvmMemory")
				+ Long.toString( Runtime.getRuntime().maxMemory() /1024/1024, 0)
				+ " MB\n"
			+ Settings.jpoResources.getString("HelpAboutJvmFreeMemory")
				+ Long.toString( Runtime.getRuntime().freeMemory() /1024/1024, 0)
				+ " MB\n"
			);

		// while we're at it dump the stuff to the log
		Tools.log ("Help About showed the following information" );
		Tools.log ("User: " + System.getProperty( "user.name" ) );
		Tools.log ("Operating System: " + System.getProperty( "os.name" ) + "  " + System.getProperty( "os.version" ));
		Tools.log ("Java: " + System.getProperty( "java.version" ) );
		Tools.log ("Max Memory: " + Long.toString( Runtime.getRuntime().maxMemory( )/1024/1024, 0 ) + " MB" );
		Tools.log ("Free Memory: " + Long.toString( Runtime.getRuntime().freeMemory( )/1024/1024, 0 ) + " MB" );
	}




	/**
	 *   Call to do the File|New function
	 */
	public void requestFileNew() {
		Settings.top.initialiseNewCollection();				
		positionToNode( Settings.top );
	}


	/**
	 *   Creates a {@link PictureAdder} object and tells it to
	 *   add the selected pictures to the root node of the 
	 *   {@link CleverJTree}.
	 */
	public void requestFileAdd() {
		new PictureAdder( Settings.top );
		positionToNode( Settings.top );
	}
	
	
	/**
	 *   Creates a {@link PictureAdder} object and tells it to
	 *   add the selected pictures to the root node of the 
	 *   {@link CleverJTree}.
	 */
	public void requestFileAddFromCamera() {
		new AddFromCamera( Settings.top );
		positionToNode( Settings.top );
	}


	/**
	 *   Brings up a dialog where the user can select the collection
	 *   to be loaded. Calls {@link SortableDefaultMutableTreeNode#fileLoad}
	 */
	public void requestFileLoad() {
		if ( Settings.top.checkUnsavedChanges() ) return;
		Settings.top.fileLoad();
		Settings.top.setUnsavedUpdates( false );
		positionToNode( Settings.top );
	}


	/**
	 *  A convenience method that tells the Tree and the Thumbnail pane to position themselves
	 *  on the supplied node.
	 */
	public static void positionToNode( SortableDefaultMutableTreeNode displayNode ) {
		cleverJTree.setSelectedNode( displayNode );
		thumbnailJScrollPane.showGroup( displayNode );
	}

	/**
	 *   Requests a recently loaded collection to be loaded. The index
	 *   of which recently opened file to load is supplied from the 
	 *   {@link ApplicationJMenuBar} through the interface method
	 *   {@link ApplicationMenuInterface#requestOpenRecent}.
	 */
	public void requestOpenRecent( int i ) {
		Settings.top.fileLoad( new File( Settings.recentCollections[ i ] ) );
		positionToNode( Settings.top );
	}
	


	/**
	 *   Calls the {@link SortableDefaultMutableTreeNode#fileSave} method that saves the 
	 *   current collection under it's present name and if it was never
	 *   saved before brings up a popup window.
	 */
	public void requestFileSave() {
		Settings.top.fileSave();				
	}


	/**
	 *   Calls the {@link SortableDefaultMutableTreeNode#fileSaveAs} method to bring up 
	 *   a filechooser where the user can select the filename to
	 *   save under.
	 */
	public void requestFileSaveAs() {
		Settings.top.fileSaveAs();				
	}



	/**
	 *   Calls {@link #closeJpo} to shut down the application.
	 */
	public void requestExit() {
		closeJpo();
	}



	/**
	 *   Calls {@link #find} to bring up a find dialog box.
	 */
	public void requestEditFind() {
		find( Settings.top.getRootNode() );
	}


	/**
	 *   Creates a {@link ReconcileJFrame} which lets the user
	 *   specify a directory whose pictures are then compared
	 *   against the current collection.
	 */
	public void requestCheckDirectories() {
		new ReconcileJFrame( Settings.top );
	}


	/**
	 *   Creates a {@link CollectionPropertiesJFrame} that displays
	 *   statistics about the collection and allows the user to 
	 *   protect it from edits.
	 */
	public void requestCollectionProperties() {
		new CollectionPropertiesJFrame( Settings.top );
	}

	/**
	 *  Creates an IntegrityChecker that does it's magic on the collection.
	 */
	public void requestCheckIntegrity() {
		new IntegrityChecker( Settings.top );
	}


	/**
	 *   Creates a {@link SettingsDialog} where the user can edit 
	 *   Application wide settings.
	 */
	public void requestEditSettings() {
		new SettingsDialog(this, true);
	}


	/**
	 *   Camera Editor GUI.
	 */
	public void requestEditCameras() {
		new CameraEditor();
	}


	/**
	 *   Calls {@link #showHelpAboutDialog} to display general information
	 *   about the application and the current system environment.
	 */
	public void requestHelpAbout() {
		showHelpAboutDialog();
	}


	/**
	 *   Displays a <code>JFrame</code> that shows the GPL licence under
	 *   which the Jpo application is licenced.
	 */
	public void requestHelpLicense() {
		JTextArea licenseJTextArea  = new JTextArea("read the file gpl.txt");
	        licenseJTextArea.setWrapStyleWord(true); 
	        licenseJTextArea.setLineWrap(true); 
	        licenseJTextArea.setEditable(false); 
		// licenseJTextArea.setPreferredSize( new Dimension (500, 400) );
		JScrollPane jsp = new JScrollPane( licenseJTextArea, 
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		jsp.setPreferredSize( new Dimension (500, 400) );
			
		ClassLoader cl = this.getClass().getClassLoader();
		BufferedReader bin = new BufferedReader( new InputStreamReader( cl.getResourceAsStream("jpo/gpl.txt") ) ); 
			
		String sb = new String("");
		String textLine;
		try {
			while ( ( textLine = bin.readLine() ) != null ) {
				sb += textLine + "\n";
			}
		} catch (IOException e) {
			Tools.log( "Jpo.java: Error while reading gpl.txt: " + e.getMessage() );
		}
		licenseJTextArea.setText( sb );
		licenseJTextArea.setCaretPosition( 0 );

		Object[] License = {jsp};

		final String btnString1 = "OK";
		Object[] options = {btnString1};

		JOptionPane pane = new JOptionPane(License, 
               	           JOptionPane.INFORMATION_MESSAGE,
                       	   JOptionPane.OK_OPTION,
                           null,
                           options,
       	                   options[0]);

		JDialog dialog = pane.createDialog(this, "GNU General Public License");
		dialog.show();
	}






}
