package jpo;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
CameraEditor.java:  
a class that creates a GUI that allows the user to edit the definitions of his cameras.

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
 *   This class creates a JFrame and then presents the user with the dialog to add pictures directly
 *   from the camera.
 *
 */
public class CameraEditor 
	extends 	JFrame 
	implements 	ActionListener {



	/**
	 *  The name of the camera
	 */
	private JComboBox cameraNameJComboBox = new JComboBox();

	/**
	 *  The new name of the camera
	 */
	private JTextField cameraNameJTextField = new JTextField();


	/**
	 *  an icon that displays a camera to beautify the screen.
	 */
	private JLabel cameraIcon = new JLabel( new ImageIcon( Settings.cl.getResource( "jpo/images/camera.jpg" ) ) );

	/** 
	 *   holds the root directory of the camera relative to the host computer's file system
	 */
	private DirectoryChooser cameraDirJTextField =  
		new DirectoryChooser( Settings.jpoResources.getString("cameraDirJLabel"),
				      DirectoryChooser.DIR_MUST_EXIST );


	/**
	 *  the script that will connect the camera to the filesystem
	 */
	private JTextField cameraConnectJTextField = new JTextField();


	/**
	 *  the script that will disconnect the camera to the filesystem
	 */
	private JTextField cameraDisconnectJTextField = new JTextField();


	/**
	 *  label that informs how many pictures have been memorised for this camera
	 */
	private JLabel memorisedPicturesJLabel = new JLabel();

	/**
	 *  button to run the connect script
	 **/
	private JButton runConnectJButton = new JButton ( Settings.jpoResources.getString("runConnectJButton") );


	/**
	 *  button to run the disconnect script
	 **/
	private JButton runDisconnectJButton = new JButton ( Settings.jpoResources.getString("runConnectJButton") );


	
				      
	/**
	 *  checkbox to indicate that filenames should be used
	 */
	private JCheckBox filenameJCheckBox = new JCheckBox( Settings.jpoResources.getString("filenameJCheckBox") );



	/**
	 *  button to zero the old image of the camera
	 **/
	private JButton zeroJButton = new JButton ( Settings.jpoResources.getString("zeroJButton") );



	/**
	 *  button to save settings
	 **/
	private JButton saveJButton = new JButton ( Settings.jpoResources.getString("saveJButton") );

	/**
	 *  button to save settings
	 **/
	private JButton addJButton = new JButton ( Settings.jpoResources.getString("addJButton") );

	/**
	 *  button to save settings
	 **/
	private JButton deleteJButton = new JButton ( Settings.jpoResources.getString("deleteJButton") );


				      
	/** 
	 *   holds the target directory where the images are to be copied to
	 */
	private DirectoryChooser targetDirJTextField =  
		new DirectoryChooser( Settings.jpoResources.getString("targetDirJLabel"),
				      DirectoryChooser.DIR_MUST_EXIST );
	


	/**
	 *  button to start the export
	 **/
	private JButton closeJButton = new JButton ( Settings.jpoResources.getString("closeJButton") );


	/**
	 *  button to cancel the dialog
	 **/
	private JButton cancelJButton = new JButton ( Settings.jpoResources.getString("genericCancelText") );



	/** 
    	 *   Creates a JFrame with the GUI elements and buttons that can
	 *   start and stop the reconciliation. The reconciliation itself
	 *   runs in it's own Thread.
	 *
	 */
	public CameraEditor() {
		setSize( 750, 550 );
		setLocationRelativeTo( Settings.anchorFrame );
		setTitle( Settings.jpoResources.getString( "CameraEditor" ) );
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e ) {
				getRid();
			}
	        });  


		JPanel controlJPanel = new JPanel();
		controlJPanel.setLayout( new GridBagLayout() );
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridwidth = 3;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(4, 4, 4, 4);

		
		// The camera panel

		JPanel cameraJPanel = new JPanel();
		cameraJPanel.setLayout( new GridBagLayout() );
		cameraJPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Camera") );
		constraints.gridy = 0; constraints.gridx = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		controlJPanel.add( cameraJPanel, constraints );
		

		constraints.gridy = 0; constraints.gridx = 0;
		constraints.gridwidth = 1; constraints.gridheight = 10;
		constraints.insets = new Insets(4, 4, 4, 4);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		cameraJPanel.add( cameraIcon, constraints );


		JLabel cameraNameJLabel = new JLabel ( Settings.jpoResources.getString("cameraNameJLabel") );
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridy =0; constraints.gridx = 1;
		constraints.gridwidth = 1; constraints.gridheight = 1;
		constraints.insets = new Insets(4, 4, 4, 4);
		cameraJPanel.add( cameraNameJLabel, constraints );
		
		
		constraints.gridy++; 
		cameraJPanel.add( cameraNameJComboBox, constraints );
		cameraNameJComboBox.setEditable( false );
		cameraNameJComboBox.addActionListener( new ActionListener() {
			//  here we load the camera details
			public void actionPerformed( ActionEvent e ) {
        			JComboBox cb = (JComboBox) e.getSource();
        			Camera cam = (Camera) cb.getSelectedItem();
				cameraNameJTextField.setText( cam.description );
				cameraDirJTextField.setText( cam.rootDir );
				cameraConnectJTextField.setText( cam.connectScript );
				cameraDisconnectJTextField.setText( cam.disconnectScript );
		 		filenameJCheckBox.setSelected( cam.useFilename );
			}
		});


		constraints.gridy++; 
		JLabel cameraNewNameJLabel = new JLabel ( Settings.jpoResources.getString("cameraNewNameJLabel") );
		cameraJPanel.add( cameraNewNameJLabel, constraints );

		constraints.gridy++; 
		cameraJPanel.add( cameraNameJTextField, constraints );


		JLabel cameraConnectJLabel = new JLabel ( Settings.jpoResources.getString("cameraConnectJLabel") );
		constraints.gridy++;
		cameraJPanel.add( cameraConnectJLabel, constraints );

		constraints.gridy++; 
		cameraJPanel.add( cameraConnectJTextField, constraints );

		constraints.gridx++; 
		runConnectJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				saveCamera();
        			Camera cam = (Camera) cameraNameJComboBox.getSelectedItem();
				cam.runConnectScript();
			}
		});
		cameraJPanel.add( runConnectJButton, constraints );



		JLabel cameraDirJLabel = new JLabel ( Settings.jpoResources.getString("cameraDirJLabel") );
		constraints.gridy++; constraints.gridx = 1;
		cameraJPanel.add( cameraDirJLabel, constraints );

		constraints.gridy++;
		cameraJPanel.add( cameraDirJTextField, constraints );


		JLabel cameraDisconnectJLabel = new JLabel ( Settings.jpoResources.getString("cameraDisconnectJLabel") );
		constraints.gridy++;
		cameraJPanel.add( cameraDisconnectJLabel, constraints );

		constraints.gridy++;
		cameraJPanel.add( cameraDisconnectJTextField, constraints );

		constraints.gridx++; 
		runDisconnectJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				saveCamera();
        			Camera cam = (Camera) cameraNameJComboBox.getSelectedItem();
				cam.runDisconnectScript();
			}
		});
		cameraJPanel.add( runDisconnectJButton, constraints );

		constraints.gridy++; constraints.gridx = 1;
		cameraJPanel.add( filenameJCheckBox, constraints );

		constraints.gridy++; constraints.gridx = 1;
		
		
		JLabel memorisedPicsJLabel = new JLabel ( Settings.jpoResources.getString("memorisedPicsJLabel") );
		cameraJPanel.add( memorisedPicsJLabel, constraints );

		JPanel checksumJPanel = new JPanel();
		constraints.gridy++; constraints.gridx = 1;
		checksumJPanel.add( memorisedPicturesJLabel );
		
		JButton refreshJButton = new JButton ( Settings.jpoResources.getString("refreshJButton") );
		refreshJButton.setPreferredSize( Settings.defaultButtonDimension );
	        refreshJButton.setMinimumSize( Settings.defaultButtonDimension );
	        refreshJButton.setMaximumSize( Settings.defaultButtonDimension );
		refreshJButton.setBorder(BorderFactory.createRaisedBevelBorder());
		refreshJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
        			Camera cam = (Camera) cameraNameJComboBox.getSelectedItem();
				if ( ! cam.getRootDir().equals( cameraDirJTextField.getText() ) ) {
					JOptionPane.showMessageDialog( CameraEditor.this, 
						Settings.jpoResources.getString("refreshJButtonError"), 
						Settings.jpoResources.getString("genericError"), 
						JOptionPane.ERROR_MESSAGE );
					return;
				}
				cam.buildOldImage();
				updateMemorisedPicturesJLabel();
			}
		});
		checksumJPanel.add( refreshJButton );

		zeroJButton.setPreferredSize( Settings.defaultButtonDimension );
	        zeroJButton.setMinimumSize( Settings.defaultButtonDimension );
	        zeroJButton.setMaximumSize( Settings.defaultButtonDimension );
		zeroJButton.setBorder(BorderFactory.createRaisedBevelBorder());
		zeroJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
        			Camera cam = (Camera) cameraNameJComboBox.getSelectedItem();
				cam.zapOldImage();
				updateMemorisedPicturesJLabel();
			}
		});
		checksumJPanel.add( zeroJButton );

		constraints.gridy++; constraints.gridx = 1;
		cameraJPanel.add( checksumJPanel, constraints );

		// end of Camera Panel

		//  Button Panel
		JPanel buttonJPanel = new JPanel();


		saveJButton.setPreferredSize( Settings.defaultButtonDimension );
	        saveJButton.setMinimumSize( Settings.defaultButtonDimension );
	        saveJButton.setMaximumSize( Settings.defaultButtonDimension );
		saveJButton.setBorder(BorderFactory.createRaisedBevelBorder());
	        saveJButton.addActionListener( this );
		buttonJPanel.add( saveJButton );
		
		addJButton.setPreferredSize( Settings.defaultButtonDimension );
	        addJButton.setMinimumSize( Settings.defaultButtonDimension );
	        addJButton.setMaximumSize( Settings.defaultButtonDimension );
		addJButton.setBorder(BorderFactory.createRaisedBevelBorder());
	        addJButton.addActionListener( this );
		buttonJPanel.add( addJButton );

		deleteJButton.setPreferredSize( Settings.defaultButtonDimension );
	        deleteJButton.setMinimumSize( Settings.defaultButtonDimension );
	        deleteJButton.setMaximumSize( Settings.defaultButtonDimension );
		deleteJButton.setBorder(BorderFactory.createRaisedBevelBorder());
		deleteJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				Camera cam = (Camera) cameraNameJComboBox.getSelectedItem();
				Settings.Cameras.remove( cam );
				cameraNameJComboBox.updateUI();
				if ( Settings.Cameras.isEmpty() ) {
					cam = new Camera();
					Settings.Cameras.add( cam );
					saveCamera();
				}
				cameraNameJComboBox.setSelectedItem( Settings.Cameras.elementAt(0) );
			}
		});
		buttonJPanel.add( deleteJButton );

		cancelJButton.setPreferredSize( Settings.defaultButtonDimension );
	        cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
	        cancelJButton.setMaximumSize( Settings.defaultButtonDimension );
		cancelJButton.setBorder(BorderFactory.createRaisedBevelBorder());
	        cancelJButton.addActionListener( this );
		buttonJPanel.add( cancelJButton );

		closeJButton.setPreferredSize( Settings.defaultButtonDimension );
	        closeJButton.setMinimumSize( Settings.defaultButtonDimension );
	        closeJButton.setMaximumSize( Settings.defaultButtonDimension );
		closeJButton.setBorder(BorderFactory.createRaisedBevelBorder());
		closeJButton.setDefaultCapable( true );
		getRootPane().setDefaultButton ( closeJButton );
	        closeJButton.addActionListener( this );
		buttonJPanel.add( closeJButton );


		constraints.gridy = 1; constraints.gridx = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		controlJPanel.add( buttonJPanel, constraints );

		getContentPane().add( controlJPanel );
		
	 	//  As per http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
		Runnable runner = new FrameShower( this );
        	EventQueue.invokeLater(runner);

		cameraNameJComboBox.setModel( new DefaultComboBoxModel( Settings.Cameras ) );
		cameraNameJComboBox.setSelectedIndex( 0 );
		updateMemorisedPicturesJLabel();
		
	}





	/**
	 *  method that closes te frame and gets rid of it
	 */
	private void getRid() {
		setVisible ( false );
		dispose ();
	}



	/** 
	 *  method that analyses the user initiated action and performs what the user requested
	 **/
	public void actionPerformed( ActionEvent e ) {
		//Tools.log ("actionperformed!!");
		if (e.getSource() == cancelJButton) {
			getRid(); 
		} else 	if (e.getSource() == closeJButton) {
			saveCamera();
			getRid(); 
		} else 	if (e.getSource() == saveJButton) {
			saveCamera();
		} else 	if (e.getSource() == addJButton) {
			saveCamera();
			Camera cam = new Camera();
			Settings.Cameras.add( cam );
			cameraNameJComboBox.setSelectedItem( cam );
			saveCamera();
		}
	}


	/**
	 *  save the currently edited camera details into the set of cameras
	 */
	public void saveCamera() {
        	Camera cam = (Camera) cameraNameJComboBox.getSelectedItem();
		cam.description = cameraNameJTextField.getText();
		cam.rootDir = cameraDirJTextField.getText();
		cam.connectScript = cameraConnectJTextField.getText();
		cam.disconnectScript = cameraDisconnectJTextField.getText();
		cam.useFilename = filenameJCheckBox.isSelected();

		Settings.writeCameraSettings();
		cameraNameJComboBox.updateUI();
	}


	public void updateMemorisedPicturesJLabel () {
        	Camera cam = (Camera) cameraNameJComboBox.getSelectedItem();
		memorisedPicturesJLabel.setText( cam.getOldIndexCountAsString() );
	}
		

}
