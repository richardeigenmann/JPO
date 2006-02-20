package jpo;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/*
AddFromCamera.java:  
a class that creates a GUI and then adds the pictures from the camera to your collection.

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
public class AddFromCamera 
	extends 	JFrame 
	implements 	ActionListener, CategoryGuiListenerInterface {



	/**
	 *  The name of the camera
	 */
	JComboBox cameraNameJComboBox = new JComboBox();

			      
				      
	/** 
	 *   holds the target directory where the images are to be copied to
	 */
	private DirectoryChooser targetDirJTextField =  
		new DirectoryChooser( Settings.jpoResources.getString("targetDirJLabel"),
				      DirectoryChooser.DIR_MUST_EXIST );
	


	/**
	 *  Ok Button
	**/
	private JButton okJButton = new JButton ( Settings.jpoResources.getString("AddFromCameraOkJButton") );


	/**
	 *  Cancel Button
	 **/
	private JButton cancelJButton = new JButton ( Settings.jpoResources.getString("genericCancelText") );


	/**
	 *  Category Button
	 **/
	private JButton categoriesJButton = new JButton ( Settings.jpoResources.getString("categoriesJButton") );


	/**
	 *  a reference to the root node with which shall be added to.
	 */
	private SortableDefaultMutableTreeNode rootNode;	 		

	/**
	 *   Radio Button that indicates that all the pictures in the camera should be loaded.
	 */
	private JRadioButton allPicturesJRadioButton = new JRadioButton( Settings.jpoResources.getString("allPicturesJRadioButton") );
	
	/**
	 *  Radio Button that indicates that only the new pictures in the camera should be loaded.
	 */
	private JRadioButton newPicturesJRadioButton = new JRadioButton( Settings.jpoResources.getString("newPicturesJRadioButton") );
	
	/**
	 *  Radio Button that indicates that only those pictures missing in the collection should be loaded
	 */
	private JRadioButton missingPicturesJRadioButton = new JRadioButton( Settings.jpoResources.getString("missingPicturesJRadioButton") );

	/**
	 *  Checkbox that allows the user to specifiy whether directory structures should be retained
	 */
	private JCheckBox retainDirectoriesJCheckBox = new JCheckBox( Settings.jpoResources.getString("retainDirectoriesJCheckBox") );


	/**
	 *  this vector holds the list of categories to be applied to newly loaded pictures.
	 */
	private HashSet selectedCategories = null;
	 


	/** 
    	 *   Creates a JFrame with the GUI elements and buttons that can
	 *   start and stop the reconciliation. The reconciliation itself
	 *   runs in it's own Thread.
	 *
	 *   @param	rootNode	The node which should be used as
	 *				a starting point for the reconciliation.
	 *				Will probably always be the root node of
	 *				the tree.
	 */
	public AddFromCamera( SortableDefaultMutableTreeNode rootNode ) {
		this.rootNode = rootNode;

		setSize( 500, 300 );
		setLocationRelativeTo( Settings.anchorFrame );
		setTitle( Settings.jpoResources.getString( "AddFromCamera" ) );
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
		constraints.gridx = 0; constraints.gridy = 0;
		constraints.gridwidth = 3;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(4, 4, 4, 4);

		
		// The camera panel

		JPanel cameraJPanel = new JPanel();
		cameraJPanel.setLayout( new GridBagLayout() );
		cameraJPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Camera") );
		constraints.gridy++; constraints.gridx = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		controlJPanel.add( cameraJPanel, constraints );

		JLabel cameraNameJLabel = new JLabel ( Settings.jpoResources.getString("cameraNameJLabel") );
		constraints.gridy =0; constraints.gridx = 0;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(4, 4, 4, 4);
		cameraJPanel.add( cameraNameJLabel, constraints );
		
		constraints.gridy++; constraints.gridx = 0;
		constraints.gridwidth = 1;
		cameraJPanel.add( cameraNameJComboBox, constraints );
		cameraNameJComboBox.setEditable( false );



		// end of Camera Panel


		constraints.gridx = 0; constraints.gridy++;
		constraints.fill = GridBagConstraints.NONE;
		categoriesJButton.setPreferredSize( Settings.defaultButtonDimension );
	        categoriesJButton.setMinimumSize( Settings.defaultButtonDimension );
	        categoriesJButton.setMaximumSize( Settings.defaultButtonDimension );
		categoriesJButton.setBorder(BorderFactory.createRaisedBevelBorder());
	        categoriesJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				CategoryUsageJFrame cujf = new CategoryUsageJFrame();
				cujf.updateCategories();
				cujf.addCategoryGuiListener( AddFromCamera.this );
			}
		} );
		controlJPanel.add( categoriesJButton, constraints );



		//Create the radio buttons.
		constraints.gridx = 0; constraints.gridy++;
		constraints.insets = new Insets(0, 4, 0, 0);
		controlJPanel.add( allPicturesJRadioButton, constraints );

		constraints.gridy++;
		controlJPanel.add( newPicturesJRadioButton, constraints );

		constraints.gridy++;
		controlJPanel.add( missingPicturesJRadioButton, constraints );


		//Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add( allPicturesJRadioButton );
		group.add( newPicturesJRadioButton );
		group.add( missingPicturesJRadioButton );
		newPicturesJRadioButton.setSelected( true );


		retainDirectoriesJCheckBox.setSelected( false );
		constraints.gridy++;
		controlJPanel.add( retainDirectoriesJCheckBox, constraints );


		JLabel targetDirJLabel = new JLabel ( Settings.jpoResources.getString("targetDirJLabel") );
		constraints.gridy++; constraints.gridx = 0;
		constraints.gridwidth = 2;
		controlJPanel.add( targetDirJLabel, constraints );

		constraints.gridy++;
		controlJPanel.add( targetDirJTextField, constraints );


		JPanel buttonJPanel = new JPanel();
		
		okJButton.setPreferredSize( Settings.defaultButtonDimension );
	        okJButton.setMinimumSize( Settings.defaultButtonDimension );
	        okJButton.setMaximumSize( Settings.defaultButtonDimension );
		okJButton.setBorder(BorderFactory.createRaisedBevelBorder());
		okJButton.setDefaultCapable( true );
		getRootPane().setDefaultButton ( okJButton );
	        okJButton.addActionListener( this );
		buttonJPanel.add( okJButton );


		cancelJButton.setPreferredSize( Settings.defaultButtonDimension );
	        cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
	        cancelJButton.setMaximumSize( Settings.defaultButtonDimension );
		cancelJButton.setBorder(BorderFactory.createRaisedBevelBorder());
	        cancelJButton.addActionListener( this );
		buttonJPanel.add( cancelJButton );

		constraints.gridwidth = 2;
		constraints.gridy++; constraints.gridx=0;
		constraints.fill = GridBagConstraints.NONE;
		controlJPanel.add( buttonJPanel, constraints );
		
		getContentPane().add( controlJPanel );
		
	 	// As per http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
		Runnable runner = new FrameShower( this );
        	EventQueue.invokeLater(runner);

		cameraNameJComboBox.setModel( new DefaultComboBoxModel( Settings.Cameras ) );
		cameraNameJComboBox.setSelectedIndex( 0 );
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
		if ( e.getSource() == cancelJButton ) {
			File targetDir = new File( targetDirJTextField.getText() );
			if ( targetDir.exists() ) {
				Settings.memorizeCopyLocation( targetDir.toString() );
			}
			getRid(); 
		} else if ( e.getSource() == okJButton ) {
			//new Thread(this).start();
			Thread t = new Thread() {
        			public void run() {
					Camera cam = (Camera) cameraNameJComboBox.getSelectedItem();
					cam.runConnectScript();
					Tools.log("AddFromCamera.actionPerformed: running");
					File sourceDir = new File( cam.rootDir );
					// give the OS time to mount properly:
					try { sleep (1000); } catch ( InterruptedException x) {}
					if ( ! Tools.hasPictures( sourceDir ) ) {
						JOptionPane.showMessageDialog(
							Settings.anchorFrame, 
							Settings.jpoResources.getString("copyAddPicturesNoPicturesError"),
							Settings.jpoResources.getString("genericError"), 
							JOptionPane.ERROR_MESSAGE);
						return;
					}

					File targetDir = new File( targetDirJTextField.getText() );
					targetDir.mkdirs();
					Settings.memorizeCopyLocation( targetDir.toString() );
					
					String groupName = cam.description 
						+ " " 
						+ Tools.currentDate( Settings.addFromCameraDateFormat );
		
		
					SortableDefaultMutableTreeNode newNode = null;
					if ( allPicturesJRadioButton.isSelected() ) {
						Tools.log ("AddFromCamera.run: AllPictures should be loaded from camera");
						newNode = rootNode.copyAddPictures( sourceDir, targetDir, groupName, false, retainDirectoriesJCheckBox.isSelected(), selectedCategories  );
					} else if ( newPicturesJRadioButton.isSelected() ) {
						Tools.log ("AddFromCamera.run: only new pictures should be loaded from camera");
						newNode = rootNode.copyAddPictures( sourceDir, targetDir, groupName, cam, retainDirectoriesJCheckBox.isSelected(), selectedCategories );
					} else if ( missingPicturesJRadioButton.isSelected() ) {
						Tools.log ("AddFromCamera.run: only missing pictures should be loaded from camera");
						newNode = rootNode.copyAddPictures( sourceDir, targetDir, groupName, true, retainDirectoriesJCheckBox.isSelected(), selectedCategories );
					}
					
					if ( newNode != null ) {
						Jpo.positionToNode( newNode );
					}

					cam.runDisconnectScript();
        			}
    			};
			t.start();
			getRid(); 
		} 
	}


	/**
	 *  This method gets invoked from the CategoryUsageJFrame object when a selection has been made.
	 */
	public void categoriesChosen(  HashSet selectedCategories  ) {
		this.selectedCategories = selectedCategories;
	}

}
