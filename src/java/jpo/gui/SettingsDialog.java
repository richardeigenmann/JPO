package jpo.gui;

import java.util.logging.Level;
import jpotestground.ResizableJFrameTest;
import jpo.dataModel.Settings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.*;
import java.io.File;
import java.util.Hashtable;
import java.util.logging.Logger;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

/*
SettingsDialog.java:  the class that provides a GUI for the settings

Copyright (C) 2002-2011  Richard Eigenmann, Zürich, Switzerland
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
 *  GUI that allows the settings to be changed.
 *
 * @author  Richard Eigenmann
 */
public class SettingsDialog
        extends JDialog {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( SettingsDialog.class.getName() );
    /**
     *   field that allows the user to capture the file that should be automatically loaded
     */
    private final JTextField autoLoadJTextField = new JTextField();
    /**
     *  tickbox that indicates where status information should be written to the log
     */
    private final JCheckBox logfileJCheckBox = new JCheckBox();
    /**
     *   field that allows the user to specify where the logs should be written to
     */
    private final JTextField logfileJTextField = new JTextField();
    /**
     *  checkbox to indicate that JPO should be maximised on startup
     */
    private final JCheckBox maximiseJpoOnStartupJCheckBox = new JCheckBox();
    /**
     *  Dropdown to indicate what preference the user has for JPO startup
     */
    private final JComboBox startupSizeDropdown = new JComboBox();
    /**
     *  Dropdown to indicate the preferred size of the viewer window
     */
    private final JComboBox viewerSizeDropdown = new JComboBox();
    /**
     *  checkbox to indicate that the screen position should be saved upon exit.
     */
    private final JCheckBox saveSizeJCheckBox = new JCheckBox();
    /**
     *   maximum number of pictures to cache
     */
    private final WholeNumberField maxCacheJTextField = new WholeNumberField( 0, 4 );
    /**
     *   x coordinates of top left corner of main window
     */
    private final WholeNumberField mainX = new WholeNumberField( 0, 6 );
    /**
     *   y coordinates of top left corner of main window
     */
    private final WholeNumberField mainY = new WholeNumberField( 0, 6 );
    /**
     *   width of specific size window
     */
    private final WholeNumberField mainWidth = new WholeNumberField( 0, 6 );
    /**
     *   height of specific size window
     */
    private final WholeNumberField mainHeight = new WholeNumberField( 0, 6 );
    /**
     *   x coordinates of top left corner of main window
     */
    private final WholeNumberField pictureX = new WholeNumberField( 0, 6 );
    /**
     *   y coordinates of top left corner of main window
     */
    private final WholeNumberField pictureY = new WholeNumberField( 0, 6 );
    /**
     *   width of specific size window
     */
    private final WholeNumberField pictureWidth = new WholeNumberField( 0, 6 );
    /**
     *   height of specific size window
     */
    private final WholeNumberField pictureHeight = new WholeNumberField( 0, 6 );
    /**
     *   maximum size of picture
     */
    private final WholeNumberField maximumPictureSizeJTextField = new WholeNumberField( 0, 6 );
    /**
     *  checkbox that indicates whether small images should be enlarged
     */
    private final JCheckBox dontEnlargeJCheckBox = new JCheckBox( Settings.jpoResources.getString( "dontEnlargeJCheckBoxLabel" ) );
    /**
     *  tickbox that indicates whether to scale the thumbnails quickly
     */
    private final JCheckBox pictureViewerFastScaleJCheckBox = new JCheckBox( Settings.jpoResources.getString( "pictureViewerFastScale" ) );
    /**
     *   fields that allows the user to capture the path for the thumbnails
     */
    private final DirectoryChooser thumbnailPathChooser = new DirectoryChooser( Settings.jpoResources.getString( "genericSelectText" ),
            DirectoryChooser.DIR_MUST_BE_WRITABLE );
    /**
     *  tickbox that indicates whether thumbnails should be written to disk
     */
    private final JCheckBox keepThumbnailsJCheckBox = new JCheckBox( Settings.jpoResources.getString( "keepThumbnailsJCheckBoxLabel" ) );
    /**
     *     field that allows the user to capture the maximum number of thumbnails to be displayed
     */
    private final WholeNumberField maxThumbnails = new WholeNumberField( 0, 4 );
    /**
     *   fields that allows the user to capture the desired size of thumbnails
     */
    private final WholeNumberField thumbnailSize = new WholeNumberField( 0, 6 );
    /**
     *  slider that allows the quality of the jpg's to be specified
     * Should this really be the same as the HTLM Quality Field?
     */
    private final JSlider jpgQualityJSlider = new JSlider( JSlider.HORIZONTAL,
            0, 100, (int) ( Settings.defaultHtmlLowresQuality * 100 ) );
    /**
     *  tickbox that indicates whether to scale the thumbnails quickly
     */
    private final JCheckBox thumbnailFastScaleJCheckBox = new JCheckBox( Settings.jpoResources.getString( "thumbnailFastScale" ) );
    /**
     *   Text Filed that holds the first user Function
     */
    private final JTextField userFunction1NameJTextField = new JTextField();
    /**
     *   Text Filed that holds the second user Function
     */
    private final JTextField userFunction2NameJTextField = new JTextField();
    /**
     *   Text Filed that holds the third user Function
     */
    private final JTextField userFunction3NameJTextField = new JTextField();
    /**
     *   Text Filed that holds the first user Function
     */
    private final JTextField userFunction1CmdJTextField = new JTextField();
    /**
     *   Text Filed that holds the second user Function
     */
    private final JTextField userFunction2CmdJTextField = new JTextField();
    /**
     *   Text Filed that holds the third user Function
     */
    private final JTextField userFunction3CmdJTextField = new JTextField();
    /**
     *  Drop down box that shows the languages
     */
    private final JComboBox languageJComboBox = new JComboBox( Settings.supportedLanguages );
    /**
     *   Text Field that holds the address of the email server
     */
    private final JTextField emailServerJTextField = new JTextField();
    /**
     *   Text Field that holds the port of the email server
     */
    private final JTextField emailPortJTextField = new JTextField();
    /**
     * Combobox that holds the type of authentication.
     */
    private final JComboBox authenticationJComboBox = new JComboBox();
    /**
     *   Text Field that holds the user for the email server
     */
    private final JTextField emailUserJTextField = new JTextField();
    /**
     *   Text Field that holds the password for the email server
     */
    //private JTextField emailPasswordJTextField = new JTextField();
    private final JPasswordField emailPasswordJTextField = new JPasswordField();
    /**
     * Defines the size of this dialog box
     */
    private static final Dimension SETTINGS_DIALOG_SIZE = new Dimension( 700, 330 );

    /**
     *   Constructor to create the GUI that allows modification of the settings
     * @param modal
     */
    public SettingsDialog( boolean modal ) {
        super( Settings.anchorFrame, modal );
        initComponents();
        initValues();
        pack();
        setLocationRelativeTo( Settings.anchorFrame );
        setVisible( true );
    }

    /**
     *   Create the GUI elements
     */
    private void initComponents() {
        setTitle( Settings.jpoResources.getString( "settingsDialogTitle" ) );

        // General tab
        final JPanel generalTab = new JPanel( new MigLayout() );

        final JLabel languageJLabel = new JLabel( Settings.jpoResources.getString( "languageJLabel" ) );
        generalTab.add( languageJLabel );
        generalTab.add( languageJComboBox, "wrap" );

        // Initial Windowsize stuff
        generalTab.add( new JLabel( Settings.jpoResources.getString( "windowSizeChoicesJlabel" ) ) );
        final String[] windowSizeChoices = new String[Settings.windowSizes.length];
        windowSizeChoices[0] = Settings.jpoResources.getString( "windowSizeChoicesMaximum" );
        for ( int i = 1; i < Settings.windowSizes.length; i++ ) {
            windowSizeChoices[i] = Integer.toString( Settings.windowSizes[i].width ) + " x " + Integer.toString( Settings.windowSizes[i].height );
        }
        final DefaultComboBoxModel dcbm = new DefaultComboBoxModel( windowSizeChoices );
        startupSizeDropdown.setModel( dcbm );
        generalTab.add( startupSizeDropdown, "wrap" );
        startupSizeDropdown.addActionListener( new ActionListener() {

            boolean firstrun = true;

            @Override
            public void actionPerformed( ActionEvent e ) {
                if ( firstrun ) {
                    // dont change the window size when setting up the gui
                    firstrun = false;
                } else {
                    if ( startupSizeDropdown.getSelectedIndex() == 0 ) {
                        Settings.anchorFrame.setExtendedState( Frame.MAXIMIZED_BOTH );
                    } else {
                        Settings.anchorFrame.setExtendedState( Frame.NORMAL );
                        Settings.anchorFrame.setSize( Settings.windowSizes[startupSizeDropdown.getSelectedIndex()] );
                    }
                }
            }
        } );
        // End of Initial Windowsize stuff

        //Autoload stuff
        final JLabel autoLoadJLabel = new JLabel( Settings.jpoResources.getString( "autoLoadJLabelLabel" ) );
        generalTab.add( autoLoadJLabel );

        autoLoadJTextField.setPreferredSize( Settings.filenameFieldPreferredSize );
        autoLoadJTextField.setMinimumSize( Settings.filenameFieldMinimumSize );
        autoLoadJTextField.setMaximumSize( Settings.filenameFieldMaximumSize );
        autoLoadJTextField.setInputVerifier( new FileTextFieldVerifier() );
        generalTab.add( autoLoadJTextField );

        final JButton autoLoadJButton = new JButton( Settings.jpoResources.getString( "threeDotText" ) );
        autoLoadJButton.setPreferredSize( Settings.threeDotButtonSize );
        autoLoadJButton.setMinimumSize( Settings.threeDotButtonSize );
        autoLoadJButton.setMaximumSize( Settings.threeDotButtonSize );
        autoLoadJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                autoLoadChooser();
            }
        } );
        generalTab.add( autoLoadJButton );
        // End of Autoload stuff


        // set up the pictureViewerJPanel
        final JPanel pictureViewerTab = new JPanel( new MigLayout() );

        // PictureViewer size stuff
        pictureViewerTab.add( new JLabel( Settings.jpoResources.getString( "pictureViewerSizeChoicesJlabel" ) ) );
        final DefaultComboBoxModel viewerSizeModel = new DefaultComboBoxModel( windowSizeChoices );
        viewerSizeDropdown.setModel( viewerSizeModel );
        pictureViewerTab.add( viewerSizeDropdown, "wrap" );
        // End of PictureViewer size stuff

        final JLabel mximumPictureSizeLabel = new JLabel( Settings.jpoResources.getString( "maximumPictureSizeLabel" ) );
        pictureViewerTab.add( mximumPictureSizeLabel );
        maximumPictureSizeJTextField.setPreferredSize( Settings.shortNumberPreferredSize );
        maximumPictureSizeJTextField.setMinimumSize( Settings.shortNumberMinimumSize );
        maximumPictureSizeJTextField.setMaximumSize( Settings.shortNumberMaximumSize );
        pictureViewerTab.add( maximumPictureSizeJTextField, "wrap" );

        final JLabel maxCacheJLabel = new JLabel( Settings.jpoResources.getString( "maxCacheLabel" ) );
        pictureViewerTab.add( maxCacheJLabel );
        maxCacheJTextField.setPreferredSize( Settings.shortNumberPreferredSize );
        maxCacheJTextField.setMinimumSize( Settings.shortNumberMinimumSize );
        maxCacheJTextField.setMaximumSize( Settings.shortNumberMaximumSize );
        pictureViewerTab.add( maxCacheJTextField, "wrap" );

        pictureViewerTab.add( dontEnlargeJCheckBox, "wrap" );
        pictureViewerTab.add( pictureViewerFastScaleJCheckBox );




        // set up the thumbnailSettingsJPanel
        final JPanel thumbnailsTab = new JPanel( new MigLayout() );

        final JLabel thumbnailPathJLabel = new JLabel( Settings.jpoResources.getString( "thumbnailDirLabel" ) );
        thumbnailsTab.add( thumbnailPathJLabel );
        thumbnailsTab.add( thumbnailPathChooser, "wrap" );

        thumbnailsTab.add( keepThumbnailsJCheckBox );

        final JButton zapThumbnailsJButton = new JButton( Settings.jpoResources.getString( "zapThumbnails" ) );
        zapThumbnailsJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                zapThumbnails();
            }
        } );
        final Dimension zapButtonSize = new Dimension( 250, Settings.defaultButtonDimension.height );
        zapThumbnailsJButton.setPreferredSize( zapButtonSize );
        zapThumbnailsJButton.setMinimumSize( zapButtonSize );
        zapThumbnailsJButton.setMaximumSize( zapButtonSize );
        thumbnailsTab.add( zapThumbnailsJButton, "wrap" );

        final JLabel maxThumbnailsLabel = new JLabel( Settings.jpoResources.getString( "maxThumbnailsLabelText" ) );
        thumbnailsTab.add( maxThumbnailsLabel );

        maxThumbnails.setPreferredSize( Settings.shortNumberPreferredSize );
        maxThumbnails.setMinimumSize( Settings.shortNumberMinimumSize );
        maxThumbnails.setMaximumSize( Settings.shortNumberMaximumSize );
        thumbnailsTab.add( maxThumbnails, "wrap" );

        final JLabel thumbnailSizeLabel = new JLabel( Settings.jpoResources.getString( "thumbnailSizeLabel" ) );
        thumbnailsTab.add( thumbnailSizeLabel );

        thumbnailSize.setPreferredSize( Settings.shortNumberPreferredSize );
        thumbnailSize.setMinimumSize( Settings.shortNumberMinimumSize );
        thumbnailSize.setMaximumSize( Settings.shortNumberMaximumSize );
        thumbnailsTab.add( thumbnailSize, "wrap" );


        final JLabel jpgQualitySlider =
                new JLabel( Settings.jpoResources.getString( "lowresJpgQualitySlider" ) );
        thumbnailsTab.add( jpgQualitySlider, "wrap" );

        //Create the label table
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put( new Integer( 0 ), new JLabel( Settings.jpoResources.getString( "jpgQualityBad" ) ) );
        labelTable.put( new Integer( 80 ), new JLabel( Settings.jpoResources.getString( "jpgQualityGood" ) ) );
        labelTable.put( new Integer( 100 ), new JLabel( Settings.jpoResources.getString( "jpgQualityBest" ) ) );
        jpgQualityJSlider.setLabelTable( labelTable );

        jpgQualityJSlider.setMajorTickSpacing( 10 );
        jpgQualityJSlider.setMinorTickSpacing( 5 );
        jpgQualityJSlider.setPaintTicks( true );
        jpgQualityJSlider.setPaintLabels( true );
        jpgQualityJSlider.setBorder( BorderFactory.createEmptyBorder( 0, 0, 10, 20 ) );
        thumbnailsTab.add( jpgQualityJSlider, "span, grow, wrap" );


        thumbnailsTab.add( thumbnailFastScaleJCheckBox );



        // User Functions
        final JPanel userFunctionJPanel = new JPanel( new MigLayout() );
        final JLabel userFunction1JLabel = new JLabel( Settings.jpoResources.getString( "userFunction1JLabel" ) );
        userFunctionJPanel.add( userFunction1JLabel, "span, wrap" );

        userFunctionJPanel.add( new JLabel( Settings.jpoResources.getString( "userFunctionNameJLabel" ) ) );
        userFunction1NameJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        userFunction1NameJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        userFunction1NameJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        userFunctionJPanel.add( userFunction1NameJTextField, "wrap" );

        userFunctionJPanel.add( new JLabel( Settings.jpoResources.getString( "userFunctionCmdJLabel" ) ) );
        userFunction1CmdJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        userFunction1CmdJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        userFunction1CmdJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        userFunctionJPanel.add( userFunction1CmdJTextField, "wrap" );

        final JLabel userFunction2JLabel = new JLabel( Settings.jpoResources.getString( "userFunction2JLabel" ) );
        userFunctionJPanel.add( userFunction2JLabel, "span, wrap" );

        userFunctionJPanel.add( new JLabel( Settings.jpoResources.getString( "userFunctionNameJLabel" ) ) );
        userFunction2NameJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        userFunction2NameJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        userFunction2NameJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        userFunctionJPanel.add( userFunction2NameJTextField, "wrap" );

        userFunctionJPanel.add( new JLabel( Settings.jpoResources.getString( "userFunctionCmdJLabel" ) ) );

        userFunction2CmdJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        userFunction2CmdJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        userFunction2CmdJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        userFunctionJPanel.add( userFunction2CmdJTextField, "wrap" );

        final JLabel userFunction3JLabel = new JLabel( Settings.jpoResources.getString( "userFunction3JLabel" ) );
        userFunctionJPanel.add( userFunction3JLabel, "span, wrap" );

        userFunctionJPanel.add( new JLabel( Settings.jpoResources.getString( "userFunctionNameJLabel" ) ) );
        userFunction3NameJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        userFunction3NameJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        userFunction3NameJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        userFunctionJPanel.add( userFunction3NameJTextField, "wrap" );

        userFunctionJPanel.add( new JLabel( Settings.jpoResources.getString( "userFunctionCmdJLabel" ) ) );
        userFunction3CmdJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        userFunction3CmdJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        userFunction3CmdJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        userFunctionJPanel.add( userFunction3CmdJTextField, "wrap" );

        final JTextArea userFunctionHelpJTextArea = new JTextArea( Settings.jpoResources.getString( "userFunctionHelpJTextArea" ) );
        userFunctionHelpJTextArea.setEditable( false );
        userFunctionHelpJTextArea.setWrapStyleWord( true );
        userFunctionHelpJTextArea.setLineWrap( true );
        userFunctionJPanel.add( userFunctionHelpJTextArea, "span, grow" );





        // Email Server
        final JPanel emailJPanel = new JPanel( new MigLayout() );
        final JLabel emailJLabel = new JLabel( Settings.jpoResources.getString( "emailJLabel" ) );
        emailJPanel.add( emailJLabel, "span, wrap" );

        emailJPanel.add( new JLabel( Settings.jpoResources.getString( "predefinedEmailJLabel" ) ) );

        JComboBox predefinedEmailJComboBox = new JComboBox();
        predefinedEmailJComboBox.addItem( "Localhost" );
        predefinedEmailJComboBox.addItem( "Gmail" );
        predefinedEmailJComboBox.addItem( "Compuserve" );
        predefinedEmailJComboBox.addItem( "Hotmail" );
        predefinedEmailJComboBox.addItem( "Other" );
        predefinedEmailJComboBox.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JComboBox cb = (JComboBox) e.getSource();
                String cbSelection = (String) cb.getSelectedItem();
                if ( cbSelection.equals( "Localhost" ) ) {
                    emailServerJTextField.setText( "localhost" );
                    emailPortJTextField.setText( "25" );
                    authenticationJComboBox.setSelectedIndex( 1 ); //Password
                } else if ( cbSelection.equals( "Compuserve" ) ) {
                    emailServerJTextField.setText( "smtp.compuserve.com" );
                    emailPortJTextField.setText( "25" );
                    //emailUserJTextField.setText( "set your username" );
                    //emailPasswordJTextField.setText( "set your password" );
                    authenticationJComboBox.setSelectedIndex( 1 ); //Password
                } else if ( cbSelection.equals( "Gmail" ) ) {
                    emailServerJTextField.setText( "smtp.gmail.com" );
                    emailPortJTextField.setText( "465" );
                    //emailUserJTextField.setText( "set your username" );
                    //emailPasswordJTextField.setText( "set your password" );
                    authenticationJComboBox.setSelectedIndex( 2 ); //SSL
                } else if ( cbSelection.equals( "Hotmail" ) ) {
                    emailServerJTextField.setText( "smtp.live.com" );
                    emailPortJTextField.setText( "25" );
                    //emailUserJTextField.setText( "set your username" );
                    //emailPasswordJTextField.setText( "set your password" );
                    authenticationJComboBox.setSelectedIndex( 1 ); //Password
                } else if ( cbSelection.equals( "Other" ) ) {
                    emailServerJTextField.setText( "" );
                    emailPortJTextField.setText( "25" );
                }

            }
        } );
        emailJPanel.add( predefinedEmailJComboBox, "wrap" );


        emailJPanel.add( new JLabel( Settings.jpoResources.getString( "emailServerJLabel" ) ) );
        emailServerJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        emailServerJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        emailServerJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        emailJPanel.add( emailServerJTextField, "wrap" );

        emailJPanel.add( new JLabel( Settings.jpoResources.getString( "emailPortJLabel" ) ) );
        emailPortJTextField.setPreferredSize( Settings.shortNumberPreferredSize );
        emailPortJTextField.setMinimumSize( Settings.shortNumberMinimumSize );
        emailPortJTextField.setMaximumSize( Settings.shortNumberMaximumSize );
        emailJPanel.add( emailPortJTextField, "wrap" );

        final JLabel userNameJLabel = new JLabel( Settings.jpoResources.getString( "emailUserJLabel" ) );
        final JLabel passwordJLabel = new JLabel( Settings.jpoResources.getString( "emailPasswordJLabel" ) );
        final JLabel showPasswordLabel = new JLabel();
        final JButton showPasswordButton = new JButton( Settings.jpoResources.getString( "emailShowPasswordButton" ) );

        emailJPanel.add( new JLabel( Settings.jpoResources.getString( "emailAuthentication" ) ) );
        authenticationJComboBox.removeAllItems();
        authenticationJComboBox.addItem( "None" );
        authenticationJComboBox.addItem( "Password" );
        authenticationJComboBox.addItem( "SSL" );
        authenticationJComboBox.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JComboBox cb = (JComboBox) e.getSource();
                String cbSelection = (String) cb.getSelectedItem();
                if ( cbSelection.equals( "None" ) ) {
                    emailUserJTextField.setText( "" );
                    userNameJLabel.setVisible( false );
                    emailUserJTextField.setVisible( false );
                    emailPasswordJTextField.setText( "" );
                    passwordJLabel.setVisible( false );
                    emailPasswordJTextField.setVisible( false );
                    showPasswordButton.setVisible( false );
                    showPasswordLabel.setVisible( false );
                } else if ( cbSelection.equals( "Password" ) ) {
                    userNameJLabel.setVisible( true );
                    emailUserJTextField.setVisible( true );
                    passwordJLabel.setVisible( true );
                    emailPasswordJTextField.setVisible( true );
                    showPasswordButton.setVisible( true );
                    showPasswordLabel.setVisible( true );
                } else if ( cbSelection.equals( "SSL" ) ) {
                    userNameJLabel.setVisible( true );
                    emailUserJTextField.setVisible( true );
                    passwordJLabel.setVisible( true );
                    emailPasswordJTextField.setVisible( true );
                    showPasswordButton.setVisible( true );
                    showPasswordLabel.setVisible( true );
                }

            }
        } );
        emailJPanel.add( authenticationJComboBox, "wrap" );

        emailJPanel.add( userNameJLabel );
        emailUserJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        emailUserJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        emailUserJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        emailJPanel.add( emailUserJTextField, "wrap" );

        emailJPanel.add( passwordJLabel );
        emailPasswordJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        emailPasswordJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        emailPasswordJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        emailJPanel.add( emailPasswordJTextField, "wrap" );

        showPasswordButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent ae ) {
                showPasswordLabel.setText( new String( emailPasswordJTextField.getPassword() ) );
            }
        } );

        emailJPanel.add( showPasswordButton );
        emailJPanel.add( showPasswordLabel, "wrap" );


        // Debug Panel
        final JPanel debugTab = new JPanel( new MigLayout() );

        // Logfile stuff
        logfileJCheckBox.setText( Settings.jpoResources.getString( "logfileJCheckBoxLabel" ) );
        final JLabel logfileJLabel = new JLabel( Settings.jpoResources.getString( "logfileJLabelLabel" ) );
        final JButton logfileJButton = new JButton( Settings.jpoResources.getString( "threeDotText" ) );
        logfileJCheckBox.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                logfileJLabel.setVisible( logfileJCheckBox.isSelected() );
                logfileJTextField.setVisible( logfileJCheckBox.isSelected() );
                logfileJButton.setVisible( logfileJCheckBox.isSelected() );
                checkLogfile( logfileJTextField.getText() );
            }
        } );
        debugTab.add( logfileJCheckBox, "wrap" );

        debugTab.add( logfileJLabel );

        logfileJTextField.setPreferredSize( Settings.filenameFieldPreferredSize );
        logfileJTextField.setMinimumSize( Settings.filenameFieldMinimumSize );
        logfileJTextField.setMaximumSize( Settings.filenameFieldMaximumSize );
        logfileJTextField.setInputVerifier( new FileTextFieldVerifier() );
        debugTab.add( logfileJTextField );

        logfileJButton.setPreferredSize( Settings.threeDotButtonSize );
        logfileJButton.setMinimumSize( Settings.threeDotButtonSize );
        logfileJButton.setMaximumSize( Settings.threeDotButtonSize );
        logfileJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                logfileChooser();
            }
        } );
        debugTab.add( logfileJButton, "wrap" );
        // end of Logfile Stuff

        JButton screenSizeTestButton = new JButton( "Window Resize Test" );
        screenSizeTestButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                getRid(); // the dialog is modal and would prevent us using the frame
                new ResizableJFrameTest();
            }
        } );
        debugTab.add( screenSizeTestButton );





        // set up the main part of the dialog
        getContentPane().setLayout( new BorderLayout() );

        JTabbedPane tp = new JTabbedPane();
        tp.setTabPlacement( JTabbedPane.TOP );
        tp.setPreferredSize( SETTINGS_DIALOG_SIZE );
        tp.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );

        tp.add( Settings.jpoResources.getString( "browserWindowSettingsJPanel" ), generalTab );
        tp.add( Settings.jpoResources.getString( "pictureViewerJPanel" ), pictureViewerTab );
        tp.add( Settings.jpoResources.getString( "thumbnailSettingsJPanel" ), thumbnailsTab );
        tp.add( Settings.jpoResources.getString( "userFunctionJPanel" ), userFunctionJPanel );
        tp.add( Settings.jpoResources.getString( "emailJPanel" ), emailJPanel );
        tp.add( "Debug", debugTab );

        getContentPane().add( tp, BorderLayout.NORTH );

        /**
         *   container to neatly group the 2 buttons
         */
        Container buttonContainer = new Container();

        buttonContainer.setLayout( new FlowLayout() );

        JButton saveButton = new JButton( Settings.jpoResources.getString( "genericSaveButtonLabel" ) );
        saveButton.setPreferredSize( Settings.defaultButtonDimension );
        saveButton.setMinimumSize( Settings.defaultButtonDimension );
        saveButton.setMaximumSize( Settings.defaultButtonDimension );
        saveButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        saveButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                consistencyCheck();
                writeValues();
                Settings.writeSettings();
                getRid();
            }
        } );
        buttonContainer.add( saveButton );

        JButton cancelButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );
        cancelButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelButton.setMinimumSize( Settings.defaultButtonDimension );
        cancelButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        cancelButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                getRid();
            }
        } );
        buttonContainer.add( cancelButton );


        getContentPane().add( buttonContainer, BorderLayout.SOUTH );


        setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent evt ) {
                getRid();
            }
        } );
    }

    /**
     *   This method sets up the GUI fields according to what the
     *   Settings object's values are
     */
    private void initValues() {
        for ( int i = 0; i < Settings.supportedLanguages.length; i++ ) {
            if ( Settings.getCurrentLocale().equals( Settings.supportedLocale[i] ) ) {
                languageJComboBox.setSelectedIndex( i );
                break;
            }
        }

        autoLoadJTextField.setText( Settings.autoLoad );
        logfileJCheckBox.setSelected( Settings.writeLog );
        logfileJTextField.setText( Settings.logfile.getPath() );

        startupSizeDropdown.setSelectedIndex( findSizeIndex( Settings.maximiseJpoOnStartup, Settings.mainFrameDimensions ) );
        viewerSizeDropdown.setSelectedIndex( findSizeIndex( Settings.maximisePictureViewerWindow, Settings.pictureViewerDefaultDimensions ) );

        maximumPictureSizeJTextField.setValue( Settings.maximumPictureSize );
        maxCacheJTextField.setValue( Settings.maxCache );
        dontEnlargeJCheckBox.setSelected( Settings.dontEnlargeSmallImages );

        thumbnailPathChooser.setText( Settings.thumbnailPath.getPath() );
        maxThumbnails.setValue( Settings.maxThumbnails );
        thumbnailSize.setValue( Settings.thumbnailSize );
        keepThumbnailsJCheckBox.setSelected( Settings.keepThumbnails );
        jpgQualityJSlider.setValue( (int) ( Settings.defaultHtmlLowresQuality * 100 ) );
        thumbnailFastScaleJCheckBox.setSelected( Settings.thumbnailFastScale );

        userFunction1NameJTextField.setText( Settings.userFunctionNames[0] );
        userFunction2NameJTextField.setText( Settings.userFunctionNames[1] );
        userFunction3NameJTextField.setText( Settings.userFunctionNames[2] );

        userFunction1CmdJTextField.setText( Settings.userFunctionCmd[0] );
        userFunction2CmdJTextField.setText( Settings.userFunctionCmd[1] );
        userFunction3CmdJTextField.setText( Settings.userFunctionCmd[2] );

        emailServerJTextField.setText( Settings.emailServer );
        emailPortJTextField.setText( Settings.emailPort );
        authenticationJComboBox.setSelectedIndex( Settings.emailAuthentication );
        emailUserJTextField.setText( Settings.emailUser );
        emailPasswordJTextField.setText( Settings.emailPassword );

        // deliberately placed here to stop change events being triggered while the fields are
        // being initialised.
        checkLogfile( logfileJTextField.getText() );
        checkAutoLoad( autoLoadJTextField.getText() );
    }

    /**
     * returns the index for the size dropdowns based on the supplied parameters.
     * @param maximise  whether the index should be maximised
     * @param targetDimension  the target size of the window
     */
    private static int findSizeIndex( boolean maximise,
            Dimension targetDimension ) {
        if ( maximise ) {
            return 0;
        } else {
            int settingsArea = targetDimension.width * targetDimension.height;
            int index = 1;
            for ( int i = 1; i < Settings.windowSizes.length; i++ ) {
                if ( Settings.windowSizes[i].width * Settings.windowSizes[i].height <= settingsArea ) {
                    index = i;
                } else {
                    break;
                }
            }
            return index;
        }
    }

    /**
     *   This method checks that the values all make sense and adjusts them if not.
     */
    private void consistencyCheck() {
        if ( ( !checkLogfile( logfileJTextField.getText() ) ) && logfileJCheckBox.isSelected() ) {
            // disable logging if logfile is not in order
            JOptionPane.showMessageDialog( Settings.anchorFrame,
                    Settings.jpoResources.getString( "generalLogFileError" ),
                    Settings.jpoResources.getString( "settingsError" ),
                    JOptionPane.ERROR_MESSAGE );
            logfileJCheckBox.setSelected( false );
        }


        if ( ( !thumbnailPathChooser.setColor() ) ) { // TODO: This seems very odd
            JOptionPane.showMessageDialog( Settings.anchorFrame,
                    Settings.jpoResources.getString( "thumbnailDirError" ),
                    Settings.jpoResources.getString( "settingsError" ),
                    JOptionPane.ERROR_MESSAGE );
            logfileJCheckBox.setSelected( false );
        }
    }

    /**
     *   This method writes the values in the GUI widgets into the Settings object.
     */
    private void writeValues() {
        Settings.setLocale( Settings.supportedLocale[languageJComboBox.getSelectedIndex()] );

        Settings.autoLoad = autoLoadJTextField.getText();

        Settings.logfile = new File( logfileJTextField.getText() );
        Settings.writeLog = logfileJCheckBox.isSelected();

        if ( startupSizeDropdown.getSelectedIndex() == 0 ) {
            Settings.maximiseJpoOnStartup = true;
            Settings.mainFrameDimensions = new Dimension( 0, 0 );
        } else {
            Settings.maximiseJpoOnStartup = false;
            Settings.mainFrameDimensions = new Dimension( Settings.windowSizes[startupSizeDropdown.getSelectedIndex()] );
        }

        Settings.maximumPictureSize = maximumPictureSizeJTextField.getValue();
        Settings.maxCache = maxCacheJTextField.getValue();
        Settings.dontEnlargeSmallImages = dontEnlargeJCheckBox.isSelected();

        if ( viewerSizeDropdown.getSelectedIndex() == 0 ) {
            Settings.maximisePictureViewerWindow = true;
            Settings.pictureViewerDefaultDimensions = new Dimension( 0, 0 );
        } else {
            Settings.maximisePictureViewerWindow = false;
            Settings.pictureViewerDefaultDimensions = new Dimension( Settings.windowSizes[viewerSizeDropdown.getSelectedIndex()] );
        }

        Settings.pictureViewerFastScale = pictureViewerFastScaleJCheckBox.isSelected();

        Settings.thumbnailPath = thumbnailPathChooser.getDirectory();
        Settings.keepThumbnails = keepThumbnailsJCheckBox.isSelected();

        if ( ( !Settings.thumbnailPath.exists() ) && Settings.keepThumbnails ) {
            if ( !Settings.thumbnailPath.mkdirs() ) {
                LOGGER.severe( String.format( "Could not create directory: %s", Settings.thumbnailPath.toString() ) );
            }
        }

        Settings.maxThumbnails = maxThumbnails.getValue();
        Settings.thumbnailSize = thumbnailSize.getValue();
        Settings.defaultHtmlLowresQuality = ( (float) jpgQualityJSlider.getValue() ) / 100;
        Settings.thumbnailFastScale = thumbnailFastScaleJCheckBox.isSelected();

        Settings.userFunctionNames[0] = userFunction1NameJTextField.getText();
        Settings.userFunctionNames[1] = userFunction2NameJTextField.getText();
        Settings.userFunctionNames[2] = userFunction3NameJTextField.getText();

        Settings.userFunctionCmd[0] = userFunction1CmdJTextField.getText();
        Settings.userFunctionCmd[1] = userFunction2CmdJTextField.getText();
        Settings.userFunctionCmd[2] = userFunction3CmdJTextField.getText();


        Settings.emailServer = emailServerJTextField.getText();
        Settings.emailPort = emailPortJTextField.getText();
        Settings.emailAuthentication = authenticationJComboBox.getSelectedIndex();
        Settings.emailUser = emailUserJTextField.getText();
        //Settings.emailPassword = emailPasswordJTextField.getText();
        Settings.emailPassword = new String( emailPasswordJTextField.getPassword() );

        Settings.validateSettings();
        Settings.notifyUserFunctionsChanged();
    }

    /**
     *   this method verifies that the file specified in the logfileJTextField
     *   is valid. It sets the color of the font to red if this is not ok and
     *   returns false to the caller. If all is fine it returns true;
     * @param validationFile The file to test
     * @return true if good, false if bad
     */
    public boolean checkLogfile( String validationFile ) {
        File testFile = new File( validationFile );

        if ( testFile.exists() ) {
            if ( !testFile.canWrite() ) {
                logfileJTextField.setForeground( Color.red );
                LOGGER.log( Level.WARNING, "logfile exists but can''t be written: {0}", testFile );
                return false;
            }
            if ( !testFile.isFile() ) {
                logfileJTextField.setForeground( Color.red );
                LOGGER.log( Level.WARNING, "isFile failed: {0}", testFile );
                return false;
            }
        } else {
            File testFileParent = testFile.getParentFile();
            if ( testFileParent == null ) {
                logfileJTextField.setForeground( Color.red );
                LOGGER.warning( "Logfile can't be the root directory!" );
                return false;
            }
            if ( !testFileParent.canWrite() ) {
                logfileJTextField.setForeground( Color.red );
                LOGGER.warning( "Parent Directory is read only!" );
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
     * @param validationFile the file to validate
     * @return true if good, false if not
     */
    public boolean checkAutoLoad( String validationFile ) {
        LOGGER.log( Level.FINE, "SettingsDialog.checkAutoLoad: called on: {0}", validationFile );
        File testFile = new File( validationFile );

        if ( validationFile.equals( "" ) ) {
            autoLoadJTextField.setForeground( Color.black );
            return false;
        }

        if ( !testFile.exists() ) {
            LOGGER.log( Level.WARNING, "SettingsDialog.checkAutoLoad: {0} doesn''t exist.", testFile.toString() );
            autoLoadJTextField.setForeground( Color.red );
            return false;
        } else {
            if ( !testFile.canRead() ) {
                LOGGER.log( Level.WARNING, "SettingsDialog.checkAutoLoad: {0} can''t read.", testFile.toString() );
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
        dispose();
    }

    /**
     *  method that brings up a JFileChooser and places the path of the file selected into the
     *  JTextField of the autoFileJTextField.
     */
    private void autoLoadChooser() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter( new XmlFilter() );

        jFileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        jFileChooser.setDialogTitle( Settings.jpoResources.getString( "autoLoadChooserTitle" ) );
        jFileChooser.setCurrentDirectory( new File( autoLoadJTextField.getText() ) );

        int returnVal = jFileChooser.showDialog( this, Settings.jpoResources.getString( "genericSelectText" ) );
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            autoLoadJTextField.setText( jFileChooser.getSelectedFile().getPath() );
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
        jFileChooser.setDialogTitle( Settings.jpoResources.getString( "logfileChooserTitle" ) );
        jFileChooser.setCurrentDirectory( new File( autoLoadJTextField.getText() ) );

        int returnVal = jFileChooser.showDialog( this, Settings.jpoResources.getString( "genericSelectText" ) );
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            logfileJTextField.setText( jFileChooser.getSelectedFile().getPath() );
            checkLogfile( logfileJTextField.getText() );
        }
    }

    /**
     *  special inner class that verifies whether the path indicated by the component is
     *  valid
     */
    class FileTextFieldVerifier
            extends InputVerifier {

        @Override
        public boolean shouldYieldFocus( JComponent input ) {
            String validationFile = ( (JTextField) input ).getText();
            LOGGER.log( Level.INFO, "SettingsDialog.FileTextFieldVerifyer.shouldYieldFocus: called with: {0}", validationFile );
            LOGGER.log( Level.INFO, "JComponent = {0}", Integer.toString( input.hashCode() ) );
            LOGGER.log( Level.INFO, "logfileJTextField = {0}", Integer.toString( logfileJTextField.hashCode() ) );
            LOGGER.log( Level.INFO, "autoLoadJTextField = {0}", Integer.toString( autoLoadJTextField.hashCode() ) );
            if ( input.hashCode() == logfileJTextField.hashCode() ) {
                checkLogfile( validationFile );
            } else if ( input.hashCode() == autoLoadJTextField.hashCode() ) {
                checkAutoLoad( validationFile );
            }

            return true;
        }

        @Override
        public boolean verify( JComponent input ) {
            LOGGER.log( Level.INFO, "SettingsDialog.FileTextFieldVerifyer.verify: called with: {0}", ( (JTextField) input ).getText() );
            return true;
        }
    }

    /**
     *  brings up an are you sure dialog and then zaps all the
     *  thumbnail images
     */
    public void zapThumbnails() {
        if ( ( !thumbnailPathChooser.setColor() ) ) {//TODO: Seems odd to use a GUI component to validate a path
            JOptionPane.showMessageDialog( Settings.anchorFrame,
                    Settings.jpoResources.getString( "thumbnailDirError" ),
                    Settings.jpoResources.getString( "settingsError" ),
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        File thumbnailDirFile = thumbnailPathChooser.getDirectory();

        int option = JOptionPane.showConfirmDialog(
                Settings.anchorFrame,
                Settings.jpoResources.getString( "zapThumbnails" ) + "\n" + thumbnailDirFile.toString() + "\n" + Settings.jpoResources.getString( "areYouSure" ),
                Settings.jpoResources.getString( "FileDeleteTitle" ),
                JOptionPane.OK_CANCEL_OPTION );

        if ( option == 0 ) {
            File[] thumbnailFiles = thumbnailDirFile.listFiles( new java.io.FileFilter() {

                @Override
                public boolean accept( File file ) {
                    return file.getName().startsWith( Settings.thumbnailPrefix );
                }
            } );
            for ( int i = 0; i < thumbnailFiles.length; i++ ) {
                boolean success = thumbnailFiles[i].delete();
            }
            // it is not a good idea to reset the counter since
            // this can lead to some thumbnails getting the same
            // id. Especially in different collections.
            //Settings.thumbnailCounter = 0;
            JOptionPane.showMessageDialog( Settings.anchorFrame,
                    Integer.toString( thumbnailFiles.length ) + Settings.jpoResources.getString( "thumbnailsDeleted" ),
                    Settings.jpoResources.getString( "zapThumbnails" ),
                    JOptionPane.INFORMATION_MESSAGE );

        }

    }
}
