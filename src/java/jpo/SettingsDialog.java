package jpo;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

/*
SettingsDialog.java:  the class that provides a GUI for the settings

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
 *  GUI that allos the settings to be changed.
 *
 * @author  Richard Eigenmann
 */
 public class SettingsDialog extends JDialog 
 	implements ActionListener,
		ChangeListener {


	/**
	 *   field that allows the user to capture the file that should be automatically loaded
	 */
	private JTextField autoLoadJTextField = new JTextField();


	/**
	 *  button that brings up a file chooser and puts the value back into the jar file field
	 **/
	private JButton autoLoadJButton = new JButton();


	/**
	 *  tickbox that indicates where status information should be written to the log
	 */
	private JCheckBox logfileJCheckBox = new JCheckBox();
	

	/**
	 *   field that allows the user to specify where the logs should be written to
	 */
	private JTextField logfileJTextField = new JTextField();


	/**
	 *  button that brings up a file chooser for the logfile
	 **/
	private JButton logfileJButton = new JButton();

	
	/**
	 *  checkbox to indicate that the screen position should be saved upon exit.
	 */
	private JCheckBox saveSizeJCheckBox = new JCheckBox();




	/**
	 *  The save button
	 */
	private JButton saveButton = new JButton();


	/**
	 *  The cancel button
	 */
	private JButton cancelButton = new JButton();




	/**
	 *   maximum number of pictures to cache
	 */
	private WholeNumberField maxCacheJTextField = new WholeNumberField( 0, 4 );



	/**
	 *   x coordinates of top left corner of main window
	 */
	private WholeNumberField mainX = new WholeNumberField( 0, 6 );


	/**
	 *   y coordinates of top left corner of main window
	 */
	private WholeNumberField mainY = new WholeNumberField( 0, 6 );


	/**
	 *   width of specific size window
	 */
	private WholeNumberField mainWidth = new WholeNumberField( 0, 6 );


	/**
	 *   height of specific size window
	 */
	private WholeNumberField mainHeight = new WholeNumberField( 0, 6 );



	/**
	 *   x coordinates of top left corner of main window
	 */
	private WholeNumberField pictureX = new WholeNumberField( 0, 6 );


	/**
	 *   y coordinates of top left corner of main window
	 */
	private WholeNumberField pictureY = new WholeNumberField( 0, 6 );


	/**
	 *   width of specific size window
	 */
	private WholeNumberField pictureWidth = new WholeNumberField( 0, 6 );


	/**
	 *   height of specific size window
	 */
	private WholeNumberField pictureHeight = new WholeNumberField( 0, 6 );

	
	/**
	 *   maximum size of picture
	 */
	private WholeNumberField maximumPictureSizeJTextField = new WholeNumberField( 0, 6 );




	/**
	 *  checkbox that indicates whether small images should be enlarged
	 */
	private JCheckBox dontEnlargeJCheckBox = new JCheckBox( Settings.jpoResources.getString("dontEnlargeJCheckBoxLabel") );





	/**
	 *    textfield that says how much space to leave from bottom of screen for taskbars
	 */
	private WholeNumberField taskbarSpaceJTextField = new WholeNumberField( 0, 4 );


	/**
	 *  tickbox that indicates whether to scale the thumbnails quickly
	 */
	private JCheckBox pictureViewerFastScaleJCheckBox = new JCheckBox( Settings.jpoResources.getString("pictureViewerFastScale") );



	/**
	 *   fields that allows the user to capture the path for the thumbnails
	 */
	private DirectoryChooser thumbnailPathField = new DirectoryChooser( Settings.jpoResources.getString("genericSelectText"), 
									    DirectoryChooser.DIR_MUST_BE_WRITABLE );



	/**
	 *  tickbox that indicates whether thumbnails should be written to disk
	 */
	private JCheckBox keepThumbnailsJCheckBox = new JCheckBox( Settings.jpoResources.getString("keepThumbnailsJCheckBoxLabel") );



	/**
	 *  button to delete all thumbnails
	 */
	private JButton zapThumbnailsJButton = new JButton( Settings.jpoResources.getString("zapThumbnails") );
	
	/**
	 *     field that allows the user to capture the maximum number of thumbnails to be displayed
	 */
	private WholeNumberField maxThumbnails = new WholeNumberField( 0, 4 );


	
	
	/**
	 *   fields that allows the user to capture the desired size of thumbnails
	 */
	private WholeNumberField thumbnailSize = new WholeNumberField( 0, 6 );


	/**
	 *  slider that allows the quality of the jpg's to be specified
	 */
	private JSlider jpgQualityJSlider = new JSlider(JSlider.HORIZONTAL,
                	0, 100, (int) (Settings.defaultJpgQuality * 100));

	/**
	 *  tickbox that indicates whether to scale the thumbnails quickly
	 */
	private JCheckBox thumbnailFastScaleJCheckBox = new JCheckBox( Settings.jpoResources.getString("thumbnailFastScale") );



	/**
	 *   Text Filed that holds the first user Function
	 */
	private JTextField userFunction1NameJTextField = new JTextField();

	/**
	 *   Text Filed that holds the second user Function
	 */
	private JTextField userFunction2NameJTextField = new JTextField();

	/**
	 *   Text Filed that holds the third user Function
	 */
	private JTextField userFunction3NameJTextField = new JTextField();


	/**
	 *   Text Filed that holds the first user Function
	 */
	private JTextField userFunction1CmdJTextField = new JTextField();

	/**
	 *   Text Filed that holds the second user Function
	 */
	private JTextField userFunction2CmdJTextField = new JTextField();

	/**
	 *   Text Filed that holds the third user Function
	 */
	private JTextField userFunction3CmdJTextField = new JTextField();


	/**
	 *  Supported Languages
	 */
	private String[] supportedLanguages = { "English", "Deutsch" };

	/**
	 *  Drop down box that shows the languages
	 */
	private	JComboBox languageJComboBox = new JComboBox( supportedLanguages );


	/**
	 *   Text Field that holds the address of the email server
	 */
	private JTextField emailServerJTextField = new JTextField();

	/**
	 *   Text Field that holds the port of the email server
	 */
	private JTextField emailPortJTextField = new JTextField();


	/**
	 *   Text Field that holds the user for the email server
	 */
	private JTextField emailUserJTextField = new JTextField();

	/**
	 *   Text Field that holds the password for the email server
	 */
	private JTextField emailPasswordJTextField = new JTextField();

	/** 
    	 *   Constructor to create the GUI
	 */
	public SettingsDialog( JFrame parent, boolean modal) {
		super (parent, modal);

		initComponents ();
		initValues ();
		
		
		pack ();
		setLocationRelativeTo ( Settings.anchorFrame );
		show ();
	}



	/** 
     	 *   method to create the GUI elements
      	 */
	private void initComponents () {
		setTitle( Settings.jpoResources.getString("settingsDialogTitle") );
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(4,4,4,4);


		// General Settings
		JPanel browserWindowSettingsJPanel = new JPanel();
		browserWindowSettingsJPanel.setLayout(new GridBagLayout());
		browserWindowSettingsJPanel.setBorder( BorderFactory.createEmptyBorder() );

		c.gridx = 0; c.gridy = 0;
		c.gridwidth = 2;
		JLabel languageJLabel = new JLabel( Settings.jpoResources.getString("languageJLabel") );
		browserWindowSettingsJPanel.add( languageJLabel, c );

		c.gridy ++;
		browserWindowSettingsJPanel.add( languageJComboBox, c );


		c.gridy ++;
		JLabel autoLoadJLabel = new JLabel( Settings.jpoResources.getString("autoLoadJLabelLabel") );
		browserWindowSettingsJPanel.add( autoLoadJLabel, c );

		c.gridy ++;
		c.weightx = 0.7f;
		c.fill = GridBagConstraints.HORIZONTAL;
		autoLoadJTextField.setPreferredSize( Settings.filenameFieldPreferredSize );
		autoLoadJTextField.setMinimumSize( Settings.filenameFieldMinimumSize );
		autoLoadJTextField.setMaximumSize( Settings.filenameFieldMaximumSize );
		autoLoadJTextField.setInputVerifier ( new FileTextFieldVerifier() );
		browserWindowSettingsJPanel.add( autoLoadJTextField, c );

		c.gridx = 2; 
		c.gridwidth = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		autoLoadJButton.setText( Settings.jpoResources.getString("threeDotText") );
		autoLoadJButton.setPreferredSize( Settings.threeDotButtonSize );
		autoLoadJButton.setMinimumSize( Settings.threeDotButtonSize ) ;
		autoLoadJButton.setMaximumSize( Settings.threeDotButtonSize );
		autoLoadJButton.addActionListener( this );
		browserWindowSettingsJPanel.add( autoLoadJButton, c );
		

		// logfile stuff
		c.gridx = 0; c.gridy ++;
		c.gridwidth = 2;
		logfileJCheckBox.setText( Settings.jpoResources.getString("logfileJCheckBoxLabel") );
		browserWindowSettingsJPanel.add( logfileJCheckBox, c );

		c.gridy ++;
		JLabel logfileJLabel = new JLabel( Settings.jpoResources.getString("logfileJLabelLabel") );
		browserWindowSettingsJPanel.add( logfileJLabel, c );


		c.gridy ++;
		c.weightx = 0.7f;
		c.fill = GridBagConstraints.HORIZONTAL;
		logfileJTextField.setPreferredSize( Settings.filenameFieldPreferredSize );
		logfileJTextField.setMinimumSize( Settings.filenameFieldMinimumSize );
		logfileJTextField.setMaximumSize( Settings.filenameFieldMaximumSize );
		logfileJTextField.setInputVerifier ( new FileTextFieldVerifier() );
		browserWindowSettingsJPanel.add( logfileJTextField, c );
		
		c.gridx = 2;
		c.gridwidth = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		logfileJButton.setText( Settings.jpoResources.getString("threeDotText") );
		logfileJButton.setPreferredSize( Settings.threeDotButtonSize );
		logfileJButton.setMinimumSize( Settings.threeDotButtonSize ) ;
		logfileJButton.setMaximumSize( Settings.threeDotButtonSize );
		logfileJButton.addActionListener(this);
		browserWindowSettingsJPanel.add( logfileJButton, c );

		c.gridx = 0; c.gridy ++;
		c.gridwidth = 2;
		saveSizeJCheckBox.setText( Settings.jpoResources.getString("saveSizeJCheckBoxLabel") );
		browserWindowSettingsJPanel.add( saveSizeJCheckBox, c );

		c.gridx = 0; c.gridy ++;
		JLabel mainCoordsJLabel = new JLabel( Settings.jpoResources.getString("MainCoordinates") );
		browserWindowSettingsJPanel.add( mainCoordsJLabel, c );
		
		c.gridx = 0; c.gridy ++;
		c.gridwidth = 1;
	        mainX.setPreferredSize( Settings.shortNumberPreferredSize );
	        mainX.setMinimumSize( Settings.shortNumberMinimumSize );
	        mainX.setMaximumSize( Settings.shortNumberMaximumSize );
	        browserWindowSettingsJPanel.add ( mainX, c );

		c.gridx++;
	        mainY.setPreferredSize( Settings.shortNumberPreferredSize );
	        mainY.setMinimumSize( Settings.shortNumberMinimumSize );
	        mainY.setMaximumSize( Settings.shortNumberMaximumSize );
	        browserWindowSettingsJPanel.add ( mainY, c );

		c.gridx = 0; c.gridy ++;
		c.gridwidth = 2;
		JLabel mainSizeJLabel = new JLabel( Settings.jpoResources.getString("MainSize") );
		browserWindowSettingsJPanel.add( mainSizeJLabel, c );

		c.gridx = 0; c.gridy ++;
		c.gridwidth = 1;
	        mainWidth.setPreferredSize( Settings.shortNumberPreferredSize );
	        mainWidth.setMinimumSize( Settings.shortNumberMinimumSize );
	        mainWidth.setMaximumSize( Settings.shortNumberMaximumSize );
	        browserWindowSettingsJPanel.add ( mainWidth, c );

		c.gridx++;
		c.gridwidth = 1;
	        mainHeight.setPreferredSize( Settings.shortNumberPreferredSize );
	        mainHeight.setMinimumSize( Settings.shortNumberMinimumSize );
	        mainHeight.setMaximumSize( Settings.shortNumberMaximumSize );
	        browserWindowSettingsJPanel.add ( mainHeight, c );




		// set up the pictureViewerJPanel 
		JPanel pictureViewerJPanel = new JPanel();
		pictureViewerJPanel.setLayout( new GridBagLayout() );
		pictureViewerJPanel.setBorder( BorderFactory.createEmptyBorder() );
		
		c.gridx = 0; c.gridy = 0;
		c.gridwidth = 2;
		JLabel mximumPictureSizeLabel = new JLabel( Settings.jpoResources.getString("maximumPictureSizeLabel") );
	        pictureViewerJPanel.add ( mximumPictureSizeLabel, c );

		c.gridx = 2;		
		c.gridwidth = 1;
		maximumPictureSizeJTextField.setPreferredSize(  Settings.shortNumberPreferredSize  );
		maximumPictureSizeJTextField.setMinimumSize( Settings.shortNumberMinimumSize  );
		maximumPictureSizeJTextField.setMaximumSize(  Settings.shortNumberMaximumSize  );
	        pictureViewerJPanel.add ( maximumPictureSizeJTextField, c );

		c.gridx = 0; c.gridy++;
		c.gridwidth = 2;
		JLabel maxCacheJLabel = new JLabel( Settings.jpoResources.getString("maxCacheLabel") );
	        pictureViewerJPanel.add ( maxCacheJLabel, c );

		c.gridx = 2;		
		c.gridwidth = 1;
		maxCacheJTextField.setPreferredSize(  Settings.shortNumberPreferredSize  );
		maxCacheJTextField.setMinimumSize( Settings.shortNumberMinimumSize );
		maxCacheJTextField.setMaximumSize( Settings.shortNumberMaximumSize );
	        pictureViewerJPanel.add ( maxCacheJTextField, c );

		c.gridx = 0; c.gridy++;
		c.gridwidth = 2;
		JLabel taskbarJLabel = new JLabel( Settings.jpoResources.getString("leaveSpaceLabel")  );
	        taskbarJLabel.setForeground ( Color.black );
	        pictureViewerJPanel.add ( taskbarJLabel, c );

		c.gridx = 2;
		c.gridwidth = 1;
	        taskbarSpaceJTextField.setPreferredSize( Settings.shortNumberPreferredSize );
	        taskbarSpaceJTextField.setMinimumSize( Settings.shortNumberMinimumSize );
		taskbarSpaceJTextField.setMaximumSize(  Settings.shortNumberMaximumSize  );
	        pictureViewerJPanel.add( taskbarSpaceJTextField, c);


		c.gridx = 0; c.gridy++;
		c.gridwidth = 3;
		pictureViewerJPanel.add( dontEnlargeJCheckBox, c );


		c.gridx = 0; c.gridy ++;
		JLabel pictureCoordsJLabel = new JLabel( Settings.jpoResources.getString("pictureCoordinates") );
		pictureViewerJPanel.add( pictureCoordsJLabel, c );
		
		c.gridx = 0; c.gridy ++;
		c.gridwidth = 1;
	        pictureX.setPreferredSize( Settings.shortNumberPreferredSize );
	        pictureX.setMinimumSize( Settings.shortNumberMinimumSize );
		pictureX.setMaximumSize(  Settings.shortNumberMaximumSize  );
	        pictureViewerJPanel.add( pictureX, c );

		c.gridx++;
	        pictureY.setPreferredSize( Settings.shortNumberPreferredSize );
	        pictureY.setMinimumSize( Settings.shortNumberMinimumSize );
		pictureY.setMaximumSize(  Settings.shortNumberMaximumSize  );
	        pictureViewerJPanel.add ( pictureY, c );

		c.gridx = 0; c.gridy ++;
		c.gridwidth = 3;
		JLabel pictureSizeJLabel = new JLabel( Settings.jpoResources.getString("pictureSize") );
		pictureViewerJPanel.add( pictureSizeJLabel, c );

		c.gridx = 0; c.gridy ++;
		c.gridwidth = 1;
	        pictureWidth.setPreferredSize( Settings.shortNumberPreferredSize );
	        pictureWidth.setMinimumSize( Settings.shortNumberMinimumSize );
		pictureWidth.setMaximumSize(  Settings.shortNumberMaximumSize  );
	        pictureViewerJPanel.add ( pictureWidth, c );

		c.gridx++;
		c.gridwidth = 1;
	        pictureHeight.setPreferredSize( Settings.shortNumberPreferredSize );
	        pictureHeight.setMinimumSize( Settings.shortNumberMinimumSize );
		pictureHeight.setMaximumSize(  Settings.shortNumberMaximumSize  );
	        pictureViewerJPanel.add ( pictureHeight, c );

		c.gridx = 0; c.gridy++;
		c.gridwidth = 3;
	        pictureViewerJPanel.add( pictureViewerFastScaleJCheckBox, c);
		
		



		// set up the thumbnailSettingsJPanel
		JPanel thumbnailSettingsJPanel = new JPanel();
		thumbnailSettingsJPanel.setLayout( new GridBagLayout() );

		c.gridx = 0; c.gridy = 0;
		c.gridwidth = 3;
		JLabel thumbnailPathJLabel = new JLabel( Settings.jpoResources.getString("thumbnailDirLabel") );
	        thumbnailPathJLabel.setForeground ( Color.black );
	        thumbnailSettingsJPanel.add ( thumbnailPathJLabel, c );

		c.gridx = 0; c.gridy++; 
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
	        thumbnailSettingsJPanel.add ( thumbnailPathField, c );



		c.gridx = 0; c.gridy++; 
		c.gridwidth = 3;
	        thumbnailSettingsJPanel.add ( keepThumbnailsJCheckBox, c );
		

		c.gridx = 0; c.gridy++; 
		c.gridwidth = 3;
		c.fill = GridBagConstraints.NONE;
		zapThumbnailsJButton.addActionListener( this );
		zapThumbnailsJButton.setPreferredSize( Settings.defaultButtonDimension );
		zapThumbnailsJButton.setMinimumSize( Settings.defaultButtonDimension ) ;
		zapThumbnailsJButton.setMaximumSize( Settings.defaultButtonDimension );
	        thumbnailSettingsJPanel.add ( zapThumbnailsJButton, c );


		
		c.gridx = 0; c.gridy++; 
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
	        JLabel maxThumbnailsLabel = new JLabel( Settings.jpoResources.getString("maxThumbnailsLabelText") );
	        maxThumbnailsLabel.setForeground ( Color.black );
        	thumbnailSettingsJPanel.add ( maxThumbnailsLabel, c );

		c.gridx++; 
	        maxThumbnails.setPreferredSize( Settings.shortNumberPreferredSize );
	        maxThumbnails.setMinimumSize( Settings.shortNumberMinimumSize );
		maxThumbnails.setMaximumSize(  Settings.shortNumberMaximumSize  );
	        thumbnailSettingsJPanel.add( maxThumbnails, c );

		c.gridx = 0; c.gridy++;
		JLabel thumbnailSizeLabel = new JLabel( Settings.jpoResources.getString("thumbnailSizeLabel") );
	        thumbnailSizeLabel.setForeground ( Color.black );
	        thumbnailSettingsJPanel.add( thumbnailSizeLabel, c );

		c.gridx++; 
	        thumbnailSize.setPreferredSize( Settings.shortNumberPreferredSize );
	        thumbnailSize.setMinimumSize( Settings.shortNumberMinimumSize );
		thumbnailSize.setMaximumSize(  Settings.shortNumberMaximumSize  );
	        thumbnailSettingsJPanel.add( thumbnailSize, c);


		JLabel jpgQualitySlider =  
			new JLabel( Settings.jpoResources.getString("jpgQualitySlider") );
		c.gridx = 0; c.gridy ++;
		c.gridwidth = 3;
	        thumbnailSettingsJPanel.add( jpgQualitySlider, c);
		
		//Create the label table
		Hashtable labelTable = new Hashtable();
		labelTable.put( new Integer( 0 ), new JLabel( Settings.jpoResources.getString("jpgQualityBad") ) );
		labelTable.put( new Integer( 80 ), new JLabel( Settings.jpoResources.getString("jpgQualityGood") ) );
		labelTable.put( new Integer( 100 ), new JLabel( Settings.jpoResources.getString("jpgQualityBest") ) );
		jpgQualityJSlider.setLabelTable( labelTable );
		
		jpgQualityJSlider.setMajorTickSpacing( 10 );
		jpgQualityJSlider.setMinorTickSpacing( 5) ;
		jpgQualityJSlider.setPaintTicks( true );
		jpgQualityJSlider.setPaintLabels( true );
		jpgQualityJSlider.setBorder( BorderFactory.createEmptyBorder(0,0,10,20) );
		c.gridx = 0; c.gridy++;
		c.gridwidth = 3;
	        thumbnailSettingsJPanel.add( jpgQualityJSlider, c);

		
		c.gridx = 0; c.gridy++;
		c.gridwidth = 3;
	        thumbnailSettingsJPanel.add( thumbnailFastScaleJCheckBox, c);



		// User Functions
		JPanel userFunctionJPanel = new JPanel();
		userFunctionJPanel.setLayout(new GridBagLayout());
		userFunctionJPanel.setBorder( BorderFactory.createEmptyBorder() );

		c.gridx = 0; c.gridy = 0;
		c.gridwidth = 2;
		JLabel userFunction1JLabel = new JLabel( Settings.jpoResources.getString("userFunction1JLabel") );
		userFunctionJPanel.add( userFunction1JLabel, c );

		c.gridx = 0; c.gridy++;
		c.gridwidth = 1;
		userFunctionJPanel.add( new JLabel( Settings.jpoResources.getString("userFunctionNameJLabel") ), c );
		c.gridx = 1;
		userFunction1NameJTextField.setPreferredSize( Settings.textfieldPreferredSize );
		userFunction1NameJTextField.setMinimumSize( Settings.textfieldMinimumSize );
		userFunction1NameJTextField.setMaximumSize( Settings.textfieldMaximumSize );
		userFunctionJPanel.add( userFunction1NameJTextField, c );
		c.gridx = 0; c.gridy++;
		userFunctionJPanel.add( new JLabel( Settings.jpoResources.getString("userFunctionCmdJLabel") ), c );
		c.gridx = 1;
		userFunction1CmdJTextField.setPreferredSize( Settings.textfieldPreferredSize );
		userFunction1CmdJTextField.setMinimumSize( Settings.textfieldMinimumSize );
		userFunction1CmdJTextField.setMaximumSize( Settings.textfieldMaximumSize );
		userFunctionJPanel.add( userFunction1CmdJTextField, c );

		c.gridx = 0; c.gridy++;
		c.gridwidth = 2;
		JLabel userFunction2JLabel = new JLabel( Settings.jpoResources.getString("userFunction2JLabel") );
		userFunctionJPanel.add( userFunction2JLabel, c );

		c.gridx = 0; c.gridy++;
		c.gridwidth = 1;
		userFunctionJPanel.add( new JLabel( Settings.jpoResources.getString("userFunctionNameJLabel") ), c );
		c.gridx = 1;
		userFunction2NameJTextField.setPreferredSize( Settings.textfieldPreferredSize );
		userFunction2NameJTextField.setMinimumSize( Settings.textfieldMinimumSize );
		userFunction2NameJTextField.setMaximumSize( Settings.textfieldMaximumSize );
		userFunctionJPanel.add( userFunction2NameJTextField, c );
		c.gridx = 0; c.gridy++;
		userFunctionJPanel.add( new JLabel( Settings.jpoResources.getString("userFunctionCmdJLabel") ), c );
		c.gridx = 1;
		userFunction2CmdJTextField.setPreferredSize( Settings.textfieldPreferredSize );
		userFunction2CmdJTextField.setMinimumSize( Settings.textfieldMinimumSize );
		userFunction2CmdJTextField.setMaximumSize( Settings.textfieldMaximumSize );
		userFunctionJPanel.add( userFunction2CmdJTextField, c );

		c.gridx = 0; c.gridy++;
		c.gridwidth = 2;
		JLabel userFunction3JLabel = new JLabel( Settings.jpoResources.getString("userFunction3JLabel") );
		userFunctionJPanel.add( userFunction3JLabel, c );

		c.gridx = 0; c.gridy++;
		c.gridwidth = 1;
		userFunctionJPanel.add( new JLabel( Settings.jpoResources.getString("userFunctionNameJLabel") ), c );
		c.gridx = 1;
		userFunction3NameJTextField.setPreferredSize( Settings.textfieldPreferredSize );
		userFunction3NameJTextField.setMinimumSize( Settings.textfieldMinimumSize );
		userFunction3NameJTextField.setMaximumSize( Settings.textfieldMaximumSize );
		userFunctionJPanel.add( userFunction3NameJTextField, c );
		c.gridx = 0; c.gridy++;
		userFunctionJPanel.add( new JLabel( Settings.jpoResources.getString("userFunctionCmdJLabel") ), c );
		c.gridx = 1;
		userFunction3CmdJTextField.setPreferredSize( Settings.textfieldPreferredSize );
		userFunction3CmdJTextField.setMinimumSize( Settings.textfieldMinimumSize );
		userFunction3CmdJTextField.setMaximumSize( Settings.textfieldMaximumSize );
		userFunctionJPanel.add( userFunction3CmdJTextField, c );

		c.gridx = 0; c.gridy++;
		c.gridwidth = 2;
		JTextArea userFunctionHelpJTextArea = new JTextArea( Settings.jpoResources.getString("userFunctionHelpJTextArea") );
		userFunctionHelpJTextArea.setEditable( false );
		userFunctionHelpJTextArea.setWrapStyleWord( true );
		userFunctionHelpJTextArea.setLineWrap( true );
		userFunctionJPanel.add( userFunctionHelpJTextArea, c );



		// Email Server
		JPanel emailJPanel = new JPanel();
		emailJPanel.setLayout( new GridBagLayout() );
		emailJPanel.setBorder( BorderFactory.createEmptyBorder() );

		c.gridx = 0; c.gridy = 0;
		c.gridwidth = 2;
		JLabel emailJLabel = new JLabel( Settings.jpoResources.getString("emailJLabel") );
		emailJPanel.add( emailJLabel, c );

		JComboBox predefinedEmailJComboBox = new JComboBox();
		predefinedEmailJComboBox.addItem( (String) "Localhost" );
		predefinedEmailJComboBox.addItem( (String) "Hotmail" );
		predefinedEmailJComboBox.addItem( (String) "Compuserve" );
		predefinedEmailJComboBox.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				JComboBox cb = (JComboBox) e.getSource();
			        String cbSelection = (String) cb.getSelectedItem();
				if ( cbSelection.equals( "Localhost" ) ) {
					emailServerJTextField.setText("localhost");
					emailPortJTextField.setText("25");
					emailUserJTextField.setText( "" );
					emailPasswordJTextField.setText( "" );
				} else if ( cbSelection.equals( "Compuserve" ) ) {
					emailServerJTextField.setText("smtp.compuserve.com");
					emailPortJTextField.setText("25");
					emailUserJTextField.setText( "set your username" );
					emailPasswordJTextField.setText( "set your password" );
				}
			}
		});

		c.gridy++;
		c.gridwidth = 1;
		emailJPanel.add( new JLabel( Settings.jpoResources.getString("predefinedEmailJLabel") ), c );
		c.gridx++;
		emailJPanel.add( predefinedEmailJComboBox,c );


		c.gridx = 0; c.gridy++;
		emailJPanel.add( new JLabel( Settings.jpoResources.getString("emailServerJLabel") ), c );
		c.gridx++;
		emailServerJTextField.setPreferredSize( Settings.textfieldPreferredSize );
		emailServerJTextField.setMinimumSize( Settings.textfieldMinimumSize );
		emailServerJTextField.setMaximumSize( Settings.textfieldMaximumSize );
		emailJPanel.add( emailServerJTextField, c );

		c.gridx = 0; c.gridy++;
		emailJPanel.add( new JLabel( Settings.jpoResources.getString("emailPortJLabel") ), c );
		c.gridx++;
		emailPortJTextField.setPreferredSize( Settings.shortNumberPreferredSize );
		emailPortJTextField.setMinimumSize( Settings.shortNumberMinimumSize );
		emailPortJTextField.setMaximumSize( Settings.shortNumberMaximumSize );
		emailJPanel.add( emailPortJTextField, c );

		c.gridx = 0; c.gridy++;
		emailJPanel.add( new JLabel( Settings.jpoResources.getString("emailUserJLabel") ), c );
		c.gridx++;
		emailUserJTextField.setPreferredSize( Settings.textfieldPreferredSize );
		emailUserJTextField.setMinimumSize( Settings.textfieldMinimumSize );
		emailUserJTextField.setMaximumSize( Settings.textfieldMaximumSize );
		emailJPanel.add( emailUserJTextField, c );

		c.gridx = 0; c.gridy++;
		emailJPanel.add( new JLabel( Settings.jpoResources.getString("emailPasswordJLabel") ), c );
		c.gridx++;
		emailPasswordJTextField.setPreferredSize( Settings.textfieldPreferredSize );
		emailPasswordJTextField.setMinimumSize( Settings.textfieldMinimumSize );
		emailPasswordJTextField.setMaximumSize( Settings.textfieldMaximumSize );
		emailJPanel.add( emailPasswordJTextField, c );
		



		// set up the main part of the dialog				
		getContentPane().setLayout( new BorderLayout());
		
		JTabbedPane tp = new JTabbedPane();
		tp.setTabPlacement( JTabbedPane.TOP );
	        tp.setPreferredSize( new Dimension(500, 400) );
		tp.setBorder( BorderFactory.createEmptyBorder(4,4,4,4) );

		tp.add( Settings.jpoResources.getString("browserWindowSettingsJPanel"), browserWindowSettingsJPanel );
		tp.add( Settings.jpoResources.getString("pictureViewerJPanel"), pictureViewerJPanel );
		tp.add( Settings.jpoResources.getString("thumbnailSettingsJPanel"), thumbnailSettingsJPanel );
		tp.add( Settings.jpoResources.getString("userFunctionJPanel"), userFunctionJPanel );
		tp.add( Settings.jpoResources.getString("emailJPanel"), emailJPanel );
		
		getContentPane().add(tp, BorderLayout.NORTH);

		/**
		 *   contrainer to neatly group the 2 buttons
		 */
		Container buttonContainer = new Container();

		buttonContainer.setLayout( new FlowLayout() );

	        saveButton.setText( Settings.jpoResources.getString("genericSaveButtonLabel") );
	        saveButton.setPreferredSize( Settings.defaultButtonDimension );
	        saveButton.setMinimumSize( Settings.defaultButtonDimension );
	        saveButton.setMaximumSize( Settings.defaultButtonDimension );
		saveButton.setBorder( BorderFactory.createRaisedBevelBorder() );
	        saveButton.addActionListener( this );
	        buttonContainer.add( saveButton );

	        cancelButton = new JButton ();
	        cancelButton.setText( Settings.jpoResources.getString("genericCancelText") );
	        cancelButton.setPreferredSize( Settings.defaultButtonDimension );
	        cancelButton.setMinimumSize( Settings.defaultButtonDimension );
	        cancelButton.setMaximumSize( Settings.defaultButtonDimension );
		cancelButton.setBorder( BorderFactory.createRaisedBevelBorder() );
	        cancelButton.addActionListener( this );
	        buttonContainer.add( cancelButton );
		

		getContentPane ().add( buttonContainer,BorderLayout.SOUTH );
		

	        setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		addWindowListener (new WindowAdapter () {
			public void windowClosing ( WindowEvent evt) {
				getRid ();
			}
		}
        	);
	}





	/** 
	 *   This method sets up the GUI fields according to what the
	 *   Settings object's values are
	 */
	private void initValues () {
		if ( Settings.currentLanguage.equals("Deutsch") ) {
			languageJComboBox.setSelectedIndex( 1 );
		} else {
			languageJComboBox.setSelectedIndex( 0 );
		}
		
		autoLoadJTextField.setText(Settings.autoLoad);
		logfileJCheckBox.setSelected( Settings.writeLog );
		logfileJTextField.setText( Settings.logfile.getPath() );
		saveSizeJCheckBox.setSelected( Settings.saveSizeOnExit );
		mainX.setValue( Settings.mainFrameDimensions.x );
		mainY.setValue( Settings.mainFrameDimensions.y );
		mainWidth.setValue( Settings.mainFrameDimensions.width ); 
		mainHeight.setValue( Settings.mainFrameDimensions.height );


		maximumPictureSizeJTextField.setValue( Settings.maximumPictureSize );
		maxCacheJTextField.setValue( Settings.maxCache );
	        dontEnlargeJCheckBox.setSelected ( Settings.dontEnlargeSmallImages );
		pictureX.setValue( Settings.pictureViewerDefaultDimensions.x );
		pictureY.setValue( Settings.pictureViewerDefaultDimensions.y );
		pictureWidth.setValue( Settings.pictureViewerDefaultDimensions.width ); 
		pictureHeight.setValue( Settings.pictureViewerDefaultDimensions.height );
		pictureViewerFastScaleJCheckBox.setSelected( Settings.pictureViewerFastScale );



		thumbnailPathField.setText( Settings.thumbnailPath.getPath() );
		maxThumbnails.setValue( Settings.maxThumbnails );
		thumbnailSize.setValue( Settings.thumbnailSize );
		taskbarSpaceJTextField.setValue( Settings.leaveForPanel );
		keepThumbnailsJCheckBox.setSelected( Settings.keepThumbnails );
		jpgQualityJSlider.setValue( (int) (Settings.defaultJpgQuality * 100) );
		thumbnailFastScaleJCheckBox.setSelected( Settings.thumbnailFastScale );

		userFunction1NameJTextField.setText( Settings.userFunctionNames[0] );
		userFunction2NameJTextField.setText( Settings.userFunctionNames[1] );
		userFunction3NameJTextField.setText( Settings.userFunctionNames[2] );
	
		userFunction1CmdJTextField.setText( Settings.userFunctionCmd[0] );
		userFunction2CmdJTextField.setText( Settings.userFunctionCmd[1] );
		userFunction3CmdJTextField.setText( Settings.userFunctionCmd[2] );

		emailServerJTextField.setText( Settings.emailServer );
		emailPortJTextField.setText( Settings.emailPort );
		emailUserJTextField.setText( Settings.emailUser );
		emailPasswordJTextField.setText( Settings.emailPassword );


		// deliberately placed here to stop change events being triggered while the fields are
		// being initialised.
		checkLogfile( logfileJTextField.getText() );
		checkAutoLoad( autoLoadJTextField.getText() );
		logfileJCheckBox.addChangeListener( this );
		//logfileJTextField.addActionListener( this );
	}
        


	/**
	 *   This method checks that the values all make sense and adjusts them if not.
	 */
	private void consistencyCheck () {
		if ( (! checkLogfile( logfileJTextField.getText() )) &&  logfileJCheckBox.isSelected() ) {
			// disable logging if logfile is not in order
			JOptionPane.showMessageDialog(Settings.anchorFrame, 
				Settings.jpoResources.getString("generalLogFileError"), 
				Settings.jpoResources.getString("settingsError"), 
				JOptionPane.ERROR_MESSAGE);
			logfileJCheckBox.setSelected( false );
		}

		
		if ( (! thumbnailPathField.checkDirectory() ) ) {
			JOptionPane.showMessageDialog(Settings.anchorFrame, 
				Settings.jpoResources.getString("thumbnailDirError"), 
				Settings.jpoResources.getString("settingsError"), 
				JOptionPane.ERROR_MESSAGE);
			logfileJCheckBox.setSelected( false );
		}
	}



	/** 
	 *   after the save button is clicked this method is invoked which reads the values off
	 *   the GUI fields and writes them to the Settings object.
	 */
	private void writeValues () {
		if ( languageJComboBox.getSelectedIndex() == 1 ) {
			Settings.setLanguage( "Deutsch" );
		} else {
			Settings.setLanguage( "English" );
		}

		Settings.autoLoad = autoLoadJTextField.getText();
		if ( ! logfileJTextField.getText().equals( Settings.logfile.getPath() ) 
		  || ( logfileJCheckBox.isSelected() ^ Settings.writeLog ))
			Tools.closeLogfile();  // logging either stopped or file changed
			
		Settings.logfile = new File ( logfileJTextField.getText() );
		Settings.writeLog = logfileJCheckBox.isSelected();

		Settings.saveSizeOnExit = saveSizeJCheckBox.isSelected ();

		Settings.mainFrameDimensions.x = mainX.getValue();
		Settings.mainFrameDimensions.y = mainY.getValue();
		Settings.mainFrameDimensions.width = mainWidth.getValue();
		Settings.mainFrameDimensions.height = mainHeight.getValue();

		Settings.maximumPictureSize = maximumPictureSizeJTextField.getValue();
		Settings.maxCache = maxCacheJTextField.getValue();
		Settings.leaveForPanel = taskbarSpaceJTextField.getValue();
	        Settings.dontEnlargeSmallImages = dontEnlargeJCheckBox.isSelected ();
		Settings.pictureViewerDefaultDimensions.x = pictureX.getValue();
		Settings.pictureViewerDefaultDimensions.y = pictureY.getValue();
		Settings.pictureViewerDefaultDimensions.width = pictureWidth.getValue();
		Settings.pictureViewerDefaultDimensions.height = pictureHeight.getValue();
		Settings.pictureViewerFastScale = pictureViewerFastScaleJCheckBox.isSelected();



		//Settings.thumbnailPath = new File( thumbnailPathJTextField.getText() );
		Settings.thumbnailPath = new File( thumbnailPathField.getText() );
		Settings.keepThumbnails = keepThumbnailsJCheckBox.isSelected ();

		if ( (! Settings.thumbnailPath.exists()) && Settings.keepThumbnails )
			Settings.thumbnailPath.mkdirs();

		Settings.maxThumbnails = maxThumbnails.getValue();
		Settings.thumbnailSize = thumbnailSize.getValue();
		Settings.defaultJpgQuality = ((float) jpgQualityJSlider.getValue()) / 100 ;
		Settings.thumbnailFastScale = thumbnailFastScaleJCheckBox.isSelected();

		Settings.userFunctionNames[0] = userFunction1NameJTextField.getText();
		Settings.userFunctionNames[1] = userFunction2NameJTextField.getText();
		Settings.userFunctionNames[2] = userFunction3NameJTextField.getText();

		Settings.userFunctionCmd[0] = userFunction1CmdJTextField.getText();
		Settings.userFunctionCmd[1] = userFunction2CmdJTextField.getText();
		Settings.userFunctionCmd[2] = userFunction3CmdJTextField.getText();


		if ( ! emailServerJTextField.equals( "" ) ) {
			Settings.emailServer = emailServerJTextField.getText();
		}
		Settings.emailPort = emailPortJTextField.getText();
		if ( ! emailUserJTextField.equals( "" ) ) {
			Settings.emailUser = emailUserJTextField.getText();
		}
		if ( ! emailPasswordJTextField.equals( "" ) ) {
			Settings.emailPassword = emailPasswordJTextField.getText();
		}
		
		Settings.validateSettings();
		Settings.notifyUserFunctionsChanged();
	}

    
    
	/**
	 *   this method verifies that the file specified in the logfileJTextField
	 *   is valid. It sets the color of the font to red if this is not ok and
	 *   returns false to the caller. If all is fine it returns true;
	 */
	public boolean checkLogfile( String validationFile ) {
		File testFile = new File ( validationFile );

		if ( testFile.exists() ) {
			if  ( ! testFile.canWrite() ) {
				logfileJTextField.setForeground( Color.red );
				//System.out.println("logfile exists but can't be written: " + testFile );
				return false;
			}
			if ( ! testFile.isFile() ) {
				logfileJTextField.setForeground( Color.red );
				//System.out.println("isFile failed: " + testFile );
				return false;
			}
		} else {
			File testFileParent = testFile.getParentFile();
			if (testFileParent == null) {
				logfileJTextField.setForeground( Color.red );
				//System.out.println("Logfile can't be the root directory!" );
				return false;
			}
			if ( ! testFileParent.canWrite() ){
				logfileJTextField.setForeground( Color.red );
				//System.out.println("Parent Directory is read only!");
				return false;
			}
		} 
		
		logfileJTextField.setForeground( Color.black );
		return true;
	}	


	/**
	 *   this method verifies that the file specified in the logfileJTextField
	 *   is valid. It sets the color of the font to red if this is not ok and
	 *   returns false to the caller. If all is fine it returns true;
	 */
	public boolean checkAutoLoad( String validationFile ) {
		//Tools.log("SettingsDialog.checkAutoLoad: called on: "+ validationFile);
		File testFile = new File ( validationFile );

		if ( validationFile.equals("") ) {
			autoLoadJTextField.setForeground( Color.black );
			return false;
		}			
			
		if ( ! testFile.exists() ) {
			//Tools.log("SettingsDialog.checkAutoLoad: " + testFile.toString() + " doesn't exist.");
			autoLoadJTextField.setForeground( Color.red );
			return false;
		} else {
			if  ( ! testFile.canRead() ) {
				//Tools.log("SettingsDialog.checkAutoLoad: " + testFile.toString() + " can't read.");
				autoLoadJTextField.setForeground( Color.red );
				return false;
			}
		}
		autoLoadJTextField.setForeground( Color.black );
		return true;
	}	





	/** 
	 *  method that gets rid of the SettingsDialog 
	 */
	private void getRid() {
		setVisible( false );
		dispose ();      
	}

	
	
	
	/** 
	 *  method that brings up a JFileChooser and places the path of the file selected into the 
	 *  JTextField of the autoFileJTextField.
	 */
	private void autoLoadChooser() {
		JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.setFileFilter( new XmlFilter() );
    
		jFileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY);
		jFileChooser.setDialogTitle( Settings.jpoResources.getString("autoLoadChooserTitle") );
		jFileChooser.setCurrentDirectory(new File( autoLoadJTextField.getText() ) );
		
		int returnVal = jFileChooser.showDialog( this, Settings.jpoResources.getString("genericSelectText") );
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			autoLoadJTextField.setText(jFileChooser.getSelectedFile().getPath());
			checkAutoLoad( autoLoadJTextField.getText() );
		}
	}


	/** 
	 *  method that brings up a JFileChooser and places the path of the file selected into the 
	 *  JTextField of the logfileJTextField.
	 */
	private void logfileChooser() {
		JFileChooser jFileChooser = new JFileChooser();
		//jFileChooser.setFileFilter( new XmlFilter() );
    
		jFileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		jFileChooser.setDialogTitle( Settings.jpoResources.getString("logfileChooserTitle") );
		jFileChooser.setCurrentDirectory(new File(autoLoadJTextField.getText()));
		
		int returnVal = jFileChooser.showDialog( this, Settings.jpoResources.getString("genericSelectText") );
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			logfileJTextField.setText(jFileChooser.getSelectedFile().getPath());
			checkLogfile( logfileJTextField.getText() );
		}
	}




	/** 
	 *  method that analyses the user initiated action and performs what the user requested
	 **/
	public void actionPerformed( ActionEvent e) {
		if (e.getSource() == saveButton) {
			consistencyCheck();
			writeValues();
			Settings.writeSettings();
			getRid();
		} else if (e.getSource() == cancelButton) {
			getRid();
		} else if (e.getSource() == autoLoadJButton) {
			autoLoadChooser();
		} else if (e.getSource() == logfileJButton) {
			logfileChooser();
		} else if (e.getSource() == zapThumbnailsJButton) {
			zapThumbnails();
		}
	}



	/**
	 *   method that catches change events on tickboxes
	 */
	public void stateChanged( ChangeEvent e ) {
		if ( e.getSource() == logfileJCheckBox ) {
			checkLogfile( logfileJTextField.getText() );
		}
	}



	/**
	 *  special inner class that verifies whether the path indicated by the component is 
	 *  valid
	 */
	class FileTextFieldVerifier extends InputVerifier {
		public boolean shouldYieldFocus ( JComponent input ) {
			String validationFile = ((JTextField) input).getText();
			Tools.log("SettingsDialog.FileTextFieldVerifyer.shouldYieldFocus: called with: " + validationFile );
			Tools.log("JComponent = " + Integer.toString( input.hashCode() ) );
			Tools.log("logfileJTextField = " + Integer.toString( logfileJTextField.hashCode() ) );
			Tools.log("autoLoadJTextField = " + Integer.toString( autoLoadJTextField.hashCode() ) );
			if ( input.hashCode() == logfileJTextField.hashCode() ) 
				checkLogfile( validationFile );
			else if ( input.hashCode() == autoLoadJTextField.hashCode() )
				checkAutoLoad( validationFile );

			return true;
		}

		public boolean verify ( JComponent input ) {
			Tools.log("SettingsDialog.FileTextFieldVerifyer.verify: called with: " + ((JTextField) input).getText());
			return true;
		}
	}


	/**
	 *  brings up an are you sure dialog and then zaps all the
	 *  thumbnauil images
	 */
	public void zapThumbnails () {
		if ( (! thumbnailPathField.checkDirectory() ) ) {
			JOptionPane.showMessageDialog(Settings.anchorFrame, 
				Settings.jpoResources.getString("thumbnailDirError"), 
				Settings.jpoResources.getString("settingsError"), 
				JOptionPane.ERROR_MESSAGE);
			return;
		}

		String thumbnailDir = thumbnailPathField.getText();
		
		int option = JOptionPane.showConfirmDialog(
			Settings.anchorFrame,
			Settings.jpoResources.getString("zapThumbnails") 
				+ "\n"
				+ thumbnailDir
				+ "\n"
				+ Settings.jpoResources.getString("areYouSure"), 
			Settings.jpoResources.getString("FileDeleteTitle"), 
			JOptionPane.OK_CANCEL_OPTION);
			
		if ( option == 0 ) {
			File thumbnailDirFile = new File( thumbnailDir );
			File[] thumbnailFiles = thumbnailDirFile.listFiles( new java.io.FileFilter(){
				public boolean accept(File file) {
					return file.getName().startsWith( Settings.thumbnailPrefix ) ;
				}
			});
			for ( int i=0; i < thumbnailFiles.length; i++) {
				thumbnailFiles[i].delete();
			}
			// it is not a good idea to reset the counter since
			// this can lead to some thumbnails getting the same 
			// id. Especially in different collections.
			//Settings.thumbnailCounter = 0;
			JOptionPane.showMessageDialog(Settings.anchorFrame, 
				Integer.toString( thumbnailFiles.length )
				+ Settings.jpoResources.getString( "thumbnailsDeleted" ), 
				Settings.jpoResources.getString( "zapThumbnails" ), 
				JOptionPane.INFORMATION_MESSAGE);

		}

	}

    
}
