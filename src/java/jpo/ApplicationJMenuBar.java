package jpo;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

/*
ApplicationJMenuBar.java:  main menu for the application

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
 *   This class builds the main menu of the Jpo application. It creates all the menu
 *   items and listens for the events. The events are processed and desired actions
 *   are communicated back to the object which is requesting the menu to be built through
 *   the {@link ApplicationMenuInterface} interface methods.
 *
 */

public class ApplicationJMenuBar extends JMenuBar
	    		         implements ActionListener,
				 	    RecentOpenFileListener  {


	/** 
	 *  The File menu which is part of the JMenuBar for the Jpo application.
	 **/	 	 
	private JMenu FileJMenu;


	/** 
	 *  The Edit menu which is part of the JMenuBar for the Jpo application.
	 *
	 **/	 	 
	private JMenu EditJMenu;
	

	/** 
	 *  The Tools menu which is part of the JMenuBar for the Jpo application.
	 *
	 **/	 	 
	private JMenu ToolsMenu;

	
	/** 
	 *  The g menu which is part of the JMenuBar for the Jpo application.
	 **/	 	 
	private JMenu HelpJMenu;
	

	/** 
	 *  Menu item that will request a File|New operation.
	 **/	 	 
	private JMenuItem FileNewJMenuItem = new JMenuItem( Settings.jpoResources.getString("FileNewJMenuItem") ); 
	

	/** 
	 *  Menu item that will request a File|Add operation.
	 **/	 	 
	private JMenuItem FileAddJMenuItem;

	/** 
	 *  Menu item that will request a File|Add from Camera operation.
	 **/	 	 
	private JMenuItem FileCameraJMenuItem;


	/** 
	 *  Menu item that allows the user to load a collection.
	 **/	 	 
	private JMenuItem FileLoadJMenuItem;


	/** 
	 *  Menu item that allows the user to load a collection recently used.
	 **/	 	 
	private JMenu FileOpenRecentJMenu;

	
	/**
	 *   An array of recently opened collections.
	 */
	private JMenuItem []  recentOpenedfileJMenuItem = new JMenuItem[ Settings.recentFiles ];

	
	/** 
	 *  Menu item that allows the user to save the picture list.
	 **/	 	 
	private JMenuItem FileSaveJMenuItem;
	


	/** 
	 *  Menu item that allows the user to save the picture list to a new file.
	 **/	 	 
	private JMenuItem FileSaveAsJMenuItem;



	/** 
	 *  Menu item that allows the user to close the application.
	 **/	 	 
	private JMenuItem FileExitJMenuItem;
	

	
	/** 
	 *  Menu item that allows the user to search for pictures.
	 **/	 	 
	private JMenuItem EditFindJMenuItem;
	

	/** 
	 *  Menu item that allows the user to change the application settings.
	 **/	 	 
	private JMenuItem EditCheckDirectoriesJMenuItem;


	/** 
	 *  Menu item that allows the user to change the application settings.
	 **/	 	 
	private JMenuItem EditCollectionPropertiesJMenuItem = 
		new JMenuItem( Settings.jpoResources.getString("EditCollectionPropertiesJMenuItem") ); 

	/** 
	 *  Menu item that allows the user to have the collection integrity checked.
	 **/	 	 
	private JMenuItem EditCheckIntegrityJMenuItem = 
		new JMenuItem( Settings.jpoResources.getString("EditCheckIntegrityJMenuItem") ); 


	/** 
	 *  Menu item that allows the user to set up his cameras.
	 **/	 	 
	private JMenuItem EditCamerasJMenuItem;

	
	/** 
	 *  Menu item that allows the user to change the application settings.
	 **/	 	 
	private JMenuItem EditSettingsJMenuItem;
	
	/** 
	 *  Menu item that allows the user to change the categories.
	 **/	 	 
	private JMenuItem EditCategoriesJMenuItem;


	/**
	 *  Object that must implement the functions dealing with the user
	 *  request.
	 */
	private ApplicationMenuInterface caller;


	/**
	 *  Creates a menu object for use in the main frame of the application.
	 *
	 *  @param caller  The object that is going to get the requests.
	 */
	public ApplicationJMenuBar ( final ApplicationMenuInterface caller ) {
		this.caller = caller;

		//Build the file menu.
		FileJMenu = new JMenu( Settings.jpoResources.getString("FileMenuText") ); 
		FileJMenu.setMnemonic(KeyEvent.VK_F);
		add( FileJMenu );


		FileNewJMenuItem.setMnemonic( KeyEvent.VK_N );
		FileNewJMenuItem.setAccelerator( KeyStroke.getKeyStroke( 'N', java.awt.event.InputEvent.CTRL_MASK ) );
		FileNewJMenuItem.addActionListener(this);
		FileJMenu.add( FileNewJMenuItem );


		FileAddJMenuItem = new JMenuItem( Settings.jpoResources.getString("FileAddMenuItemText") ); 
		FileAddJMenuItem.setMnemonic( KeyEvent.VK_A );
		FileAddJMenuItem.addActionListener( this );
		FileJMenu.add( FileAddJMenuItem );
		
		FileCameraJMenuItem = new JMenuItem( Settings.jpoResources.getString("FileCameraJMenuItem") ); 
		FileCameraJMenuItem.setMnemonic( KeyEvent.VK_C );
		FileCameraJMenuItem.addActionListener( this );
		FileJMenu.add( FileCameraJMenuItem );


		FileLoadJMenuItem = new JMenuItem( Settings.jpoResources.getString("FileLoadMenuItemText") ); 
		FileLoadJMenuItem.setMnemonic(KeyEvent.VK_O);
		FileLoadJMenuItem.setAccelerator( KeyStroke.getKeyStroke( 'O', java.awt.event.InputEvent.CTRL_MASK ) );
		FileLoadJMenuItem.addActionListener(this);
		FileJMenu.add( FileLoadJMenuItem );


		FileOpenRecentJMenu = new JMenu( Settings.jpoResources.getString("FileOpenRecentItemText") ); 
		FileOpenRecentJMenu.setMnemonic(KeyEvent.VK_R);
		//FileOpenRecentJMenu.setAccelerator( KeyStroke.getKeyStroke( 'R', java.awt.event.InputEvent.CTRL_MASK ) );
		//FileOpenRecentJMenu.addActionListener(this);
		FileJMenu.add( FileOpenRecentJMenu );
		
		for ( int i = 0; i < Settings.recentFiles; i++ ) {
			recentOpenedfileJMenuItem[ i ] = new JMenuItem();
			recentOpenedfileJMenuItem[ i ].addActionListener(this);
			recentOpenedfileJMenuItem[ i ].setVisible( false );
			recentOpenedfileJMenuItem[ i ].setAccelerator( KeyStroke.getKeyStroke( "control " + Integer.toString(i).substring(1,1) ) );
			FileOpenRecentJMenu.add( recentOpenedfileJMenuItem[ i ] );
		}
		Settings.addRecentOpenFileListener( this );

		FileSaveJMenuItem = new JMenuItem( Settings.jpoResources.getString("FileSaveMenuItemText") ); 
		FileSaveJMenuItem.setMnemonic(KeyEvent.VK_S);
		FileSaveJMenuItem.setAccelerator( KeyStroke.getKeyStroke( 'S', java.awt.event.InputEvent.CTRL_MASK ) );
		FileSaveJMenuItem.addActionListener(this);
		FileJMenu.add( FileSaveJMenuItem );


		FileSaveAsJMenuItem = new JMenuItem( Settings.jpoResources.getString("FileSaveAsMenuItemText") ); 
		FileSaveAsJMenuItem.setMnemonic( KeyEvent.VK_A );
		FileSaveAsJMenuItem.setAccelerator( KeyStroke.getKeyStroke( 'A', java.awt.event.InputEvent.CTRL_MASK ) );
		FileSaveAsJMenuItem.addActionListener( this );
		FileJMenu.add( FileSaveAsJMenuItem );



		FileExitJMenuItem = new JMenuItem( Settings.jpoResources.getString("FileExitMenuItemText") ); 
		FileExitJMenuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK ) );
		FileExitJMenuItem.addActionListener( this );
		FileJMenu.add( FileExitJMenuItem );


		//Build the Edit menu.
		EditJMenu = new JMenu( Settings.jpoResources.getString("EditJMenuText") ); 
		EditJMenu.setMnemonic( KeyEvent.VK_E );
		add( EditJMenu );


		EditFindJMenuItem = new JMenuItem( Settings.jpoResources.getString("EditFindJMenuItemText") ); 
		EditFindJMenuItem.setMnemonic( KeyEvent.VK_F );
		EditFindJMenuItem.setAccelerator( KeyStroke.getKeyStroke( 'F', java.awt.event.InputEvent.CTRL_MASK ) );
		EditFindJMenuItem.addActionListener( this );
		EditJMenu.add( EditFindJMenuItem );


		EditCheckDirectoriesJMenuItem = new JMenuItem( Settings.jpoResources.getString("EditCheckDirectoriesJMenuItemText") ); 
		EditCheckDirectoriesJMenuItem.setMnemonic( KeyEvent.VK_D );
		EditCheckDirectoriesJMenuItem.addActionListener( this );
		EditJMenu.add( EditCheckDirectoriesJMenuItem );


		EditCollectionPropertiesJMenuItem.setMnemonic( KeyEvent.VK_D );
		EditCollectionPropertiesJMenuItem.addActionListener( this );
		EditJMenu.add( EditCollectionPropertiesJMenuItem );

		EditCheckIntegrityJMenuItem.setMnemonic( KeyEvent.VK_C );
		EditCheckIntegrityJMenuItem.addActionListener( this );
		EditJMenu.add( EditCheckIntegrityJMenuItem );


		EditCamerasJMenuItem = new JMenuItem( Settings.jpoResources.getString("EditCamerasJMenuItem") ); 
		EditCamerasJMenuItem.setMnemonic( KeyEvent.VK_D );
		EditCamerasJMenuItem.addActionListener( this );
		EditJMenu.add( EditCamerasJMenuItem );


		EditCategoriesJMenuItem = new JMenuItem( Settings.jpoResources.getString("EditCategoriesJMenuItem") ); 
		EditCategoriesJMenuItem.setMnemonic( KeyEvent.VK_D );
		EditCategoriesJMenuItem.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				new CategoryEditorJFrame();
			}
		} );
		EditJMenu.add( EditCategoriesJMenuItem );



		EditSettingsJMenuItem = new JMenuItem( Settings.jpoResources.getString("EditSettingsMenuItemText") ); 
		EditSettingsJMenuItem.setMnemonic( KeyEvent.VK_S );
		EditSettingsJMenuItem.addActionListener( this );
		EditJMenu.add( EditSettingsJMenuItem );




		//Build the Help menu.
		HelpJMenu = new JMenu( Settings.jpoResources.getString("HelpJMenuText") ); 
		HelpJMenu.setMnemonic( KeyEvent.VK_H );
		add( HelpJMenu );


		JMenuItem HelpAboutJMenuItem = new JMenuItem( Settings.jpoResources.getString("HelpAboutMenuItemText") ); 
		HelpAboutJMenuItem.setMnemonic(KeyEvent.VK_A);
		HelpAboutJMenuItem.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				showHelpAboutDialog();
			}
		} );
		HelpJMenu.add( HelpAboutJMenuItem );

		JMenuItem HelpLicenseJMenuItem = new JMenuItem( Settings.jpoResources.getString("HelpLicenseMenuItemText") ); 
		HelpLicenseJMenuItem.setMnemonic(KeyEvent.VK_L);
		HelpLicenseJMenuItem.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				showLicense();
			}
		} );
		HelpJMenu.add( HelpLicenseJMenuItem );
		
		recentFilesChanged();
	}


	/**
	 *  Sets up the menu entries in the File|OpenRecent submenu.
	 */
	public void recentFilesChanged() {
		for ( int i = 0; i < Settings.recentCollections.length; i++ ) {
			if ( Settings.recentCollections[ i ] != null ) {
				recentOpenedfileJMenuItem[ i ].setText( Integer.toString( i+1 ) + ": " + Settings.recentCollections[ i ] );
				recentOpenedfileJMenuItem[ i ].setVisible( true );
			} else {
				recentOpenedfileJMenuItem[ i ].setVisible( false );
			}
		}
	}


	/** 
	 *  Method that analyses the user initiated action and performs what the user reuqested.
	 **/
	public void actionPerformed(ActionEvent e) {
		//  File Menu
		if ( e.getSource() == FileNewJMenuItem )
			caller.requestFileNew();
		else 	if ( e.getSource() == FileLoadJMenuItem ) 
			caller.requestFileLoad();
		else 	if ( e.getSource() == FileAddJMenuItem ) 
			caller.requestFileAdd();
		else 	if ( e.getSource() == FileCameraJMenuItem ) 
			caller.requestFileAddFromCamera();
		else 	if ( e.getSource()  == FileSaveJMenuItem )
			caller.requestFileSave();
		else 	if ( e.getSource()  == FileSaveAsJMenuItem )
			caller.requestFileSaveAs();
		else	if ( e.getSource() == FileExitJMenuItem ) 
			caller.requestExit();
	
		// Edit Menu
		else 	if (e.getSource() == EditFindJMenuItem) 
			caller.requestEditFind();
		else 	if (e.getSource() == EditCheckDirectoriesJMenuItem)
			caller.requestCheckDirectories();
		else 	if (e.getSource() == EditCollectionPropertiesJMenuItem)
			caller.requestCollectionProperties();
		else 	if (e.getSource() == EditCheckIntegrityJMenuItem)
			caller.requestCheckIntegrity();
		else 	if (e.getSource() == EditCamerasJMenuItem)
			caller.requestEditCameras();
		else 	if (e.getSource() == EditSettingsJMenuItem) 
			caller.requestEditSettings();
		else {
			boolean notFound = true;
			for ( int i = 0; (i < Settings.recentFiles) && notFound ; i++ ) {
				if ( e.getSource() == recentOpenedfileJMenuItem [ i ] ) {
					caller.requestOpenRecent( i );
					notFound = false;;
				}
			}
			
			if ( notFound ) {
				JOptionPane.showMessageDialog(null, 
					"Unknown event", 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
			}
		}
	}



	/**
	 *   Displays a <code>JFrame</code> that shows the GPL licence under
	 *   which the Jpo application is licenced.
	 */
	public void showLicense() {
		JTextArea licenseJTextArea  = new JTextArea("read the file gpl.txt");
	        licenseJTextArea.setWrapStyleWord(true); 
	        licenseJTextArea.setLineWrap(true); 
	        licenseJTextArea.setEditable(false); 
		// licenseJTextArea.setPreferredSize( new Dimension (500, 400) );
		JScrollPane jsp = new JScrollPane( licenseJTextArea, 
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		jsp.setPreferredSize( new Dimension (500, 400) );
			
		//ClassLoader cl = this.getClass().getClassLoader();
		//BufferedReader bin = new BufferedReader( new InputStreamReader( cl.getResourceAsStream("jpo/gpl.txt") ) ); 
		//BufferedReader bin = new BufferedInputStream( ApplicationJMenuBar.class.getResourceAsStream( "jpo/gpl.txt" ) );
			
		String sb = new String("");
		String textLine;
		try {
			InputStream in = ApplicationJMenuBar.class.getResourceAsStream( "gpl.txt" );
			BufferedReader bin = new BufferedReader( new InputStreamReader( in ) ); 
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

		JDialog dialog = pane.createDialog( Settings.anchorFrame, "GNU General Public License");
		dialog.show();
	}

	/**
	 *   displays the Help|About window
	 */
	public void showHelpAboutDialog() {
		JOptionPane.showMessageDialog( Settings.anchorFrame , 
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


	

}

