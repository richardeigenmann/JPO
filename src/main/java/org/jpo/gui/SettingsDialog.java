package org.jpo.gui;

/*
 Copyright (C) 2002-2019  Richard Eigenmann, Zürich, Switzerland
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

import net.miginfocom.swing.MigLayout;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.LocaleChangedEvent;
import org.jpo.eventbus.UserFunctionsChangedEvent;
import org.jpo.cache.JpoCache;
import org.jpo.datamodel.Settings;
import org.jpo.gui.swing.WholeNumberField;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GUI that allows the settings to be changed.
 *
 * @author Richard Eigenmann
 */
public class SettingsDialog extends JDialog {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( SettingsDialog.class.getName() );
    /**
     * field that allows the user to capture the file that should be
     * automatically loaded
     */
    private final JTextField autoLoadJTextField = new JTextField();

    private final SpinnerModel model
            = new SpinnerNumberModel( Settings.tagCloudWords, //initial value
                    0, //min
                    2000, //max
                    1 );                //step
    private final JSpinner tagCloudWordsJSpinner = new JSpinner( model );

    /**
     * Dropdown to indicate what preference the user has for JPO startup
     */
    private final JComboBox<String> startupSizeDropdown = new JComboBox<>();
    /**
     * Dropdown to indicate the preferred size of the viewer window
     */
    private final JComboBox<String> viewerSizeDropdown = new JComboBox<>();

    /**
     * maximum size of picture
     */
    private final WholeNumberField maximumPictureSizeJTextField = new WholeNumberField( 0, 6 );
    /**
     * checkbox that indicates whether small images should be enlarged
     */
    private final JCheckBox dontEnlargeJCheckBox = new JCheckBox( Settings.jpoResources.getString( "dontEnlargeJCheckBoxLabel" ) );
    /**
     * tickbox that indicates whether to scale the thumbnails quickly
     */
    private final JCheckBox pictureViewerFastScaleJCheckBox = new JCheckBox( Settings.jpoResources.getString( "pictureViewerFastScale" ) );

    /**
     * User picks the thumbnail cache directory here
     */
    private final DirectoryChooser thumbnailCacheDirPathChooser
            = new DirectoryChooser( Settings.jpoResources.getString( "genericSelectText" ),
                    DirectoryChooser.DIR_MUST_BE_WRITABLE );

    /**
     * field that allows the user to capture the maximum number of thumbnails to
     * be displayed
     */
    private final WholeNumberField maxThumbnails = new WholeNumberField( 0, 4 );
    /**
     * fields that allows the user to capture the desired size of thumbnails
     */
    private final WholeNumberField thumbnailSize = new WholeNumberField( 0, 6 );
    /**
     * slider that allows the quality of the jpg's to be specified Should this
     * really be the same as the HTLM Quality Field?
     */
    private final JSlider jpgQualityJSlider = new JSlider( JSlider.HORIZONTAL,
            0, 100, (int) ( Settings.defaultHtmlLowresQuality * 100 ) );
    /**
     * tickbox that indicates whether to scale the thumbnails quickly
     */
    private final JCheckBox thumbnailFastScaleJCheckBox = new JCheckBox( Settings.jpoResources.getString( "thumbnailFastScale" ) );
    /**
     * Text Filed that holds the first user Function
     */
    private final JTextField userFunction1NameJTextField = new JTextField();
    /**
     * Text Filed that holds the second user Function
     */
    private final JTextField userFunction2NameJTextField = new JTextField();
    /**
     * Text Filed that holds the third user Function
     */
    private final JTextField userFunction3NameJTextField = new JTextField();
    /**
     * Text Filed that holds the first user Function
     */
    private final JTextField userFunction1CmdJTextField = new JTextField();
    /**
     * Text Filed that holds the second user Function
     */
    private final JTextField userFunction2CmdJTextField = new JTextField();
    /**
     * Text Filed that holds the third user Function
     */
    private final JTextField userFunction3CmdJTextField = new JTextField();
    /**
     * Drop down box that shows the languages
     */
    private final JComboBox<String> languageJComboBox = new JComboBox<>( Settings.supportedLanguages );
    /**
     * Text Field that holds the address of the email server
     */
    private final JTextField emailServerJTextField = new JTextField();
    /**
     * Text Field that holds the port of the email server
     */
    private final JTextField emailPortJTextField = new JTextField();
    /**
     * ComboBox that holds the type of authentication.
     */
    private final JComboBox<String> authenticationJComboBox = new JComboBox<>();
    /**
     * Text Field that holds the user for the email server
     */
    private final JTextField emailUserJTextField = new JTextField();
    /**
     * Text Field that holds the password for the email server
     */
    //private JTextField emailPasswordJTextField = new JTextField();
    private final JPasswordField emailPasswordJTextField = new JPasswordField();
    /**
     * Defines the size of this dialog box
     */
    private static final Dimension SETTINGS_DIALOG_SIZE = new Dimension( 700, 330 );

    private final JTextArea highresStatsJTA = new JTextArea();
    private final JTextArea lowresStatsJTA = new JTextArea();

    /**
     * Constructor to create the GUI that allows modification of the settings
     *
     * @param modal flag to say if modal or not
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
     * Create the GUI elements
     */
    private void initComponents() {
        setTitle( Settings.jpoResources.getString( "settingsDialogTitle" ) );

        // General tab
        final JPanel generalJPanel = new JPanel( new MigLayout() );

        final JLabel languageJLabel = new JLabel( Settings.jpoResources.getString( "languageJLabel" ) );
        generalJPanel.add( languageJLabel );
        generalJPanel.add( languageJComboBox, "wrap" );

        // Initial Windowsize stuff
        generalJPanel.add( new JLabel( Settings.jpoResources.getString( "windowSizeChoicesJlabel" ) ) );
        final String[] windowSizeChoices = new String[Settings.windowSizes.length];
        windowSizeChoices[0] = Settings.jpoResources.getString( "windowSizeChoicesMaximum" );
        for ( int i = 1; i < Settings.windowSizes.length; i++ ) {
            windowSizeChoices[i] = Settings.windowSizes[i].width + " x " + Settings.windowSizes[i].height;
        }
        final DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<>( windowSizeChoices );
        startupSizeDropdown.setModel( dcbm );
        generalJPanel.add( startupSizeDropdown, "wrap" );
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
        generalJPanel.add( autoLoadJLabel );

        autoLoadJTextField.setPreferredSize( Settings.filenameFieldPreferredSize );
        autoLoadJTextField.setMinimumSize( Settings.filenameFieldMinimumSize );
        autoLoadJTextField.setMaximumSize( Settings.filenameFieldMaximumSize );
        autoLoadJTextField.setInputVerifier( new FileTextFieldVerifier() );
        generalJPanel.add( autoLoadJTextField );

        final JButton autoLoadJButton = new JButton( Settings.jpoResources.getString( "threeDotText" ) );
        autoLoadJButton.setPreferredSize( Settings.threeDotButtonSize );
        autoLoadJButton.setMinimumSize( Settings.threeDotButtonSize );
        autoLoadJButton.setMaximumSize( Settings.threeDotButtonSize );
        autoLoadJButton.addActionListener(( ActionEvent e ) -> autoLoadChooser());
        generalJPanel.add( autoLoadJButton, "wrap" );

        final JLabel wordCloudWordJLabel = new JLabel( "Max Word Cloud Words" );
        generalJPanel.add( wordCloudWordJLabel );

        generalJPanel.add( tagCloudWordsJSpinner, "wrap" );

        // set up the pictureViewerJPanel
        final JPanel pictureViewerJPanel = new JPanel( new MigLayout() );

        // PictureViewer size stuff
        pictureViewerJPanel.add( new JLabel( Settings.jpoResources.getString( "pictureViewerSizeChoicesJlabel" ) ) );
        final DefaultComboBoxModel<String> viewerSizeModel = new DefaultComboBoxModel<>( windowSizeChoices );
        viewerSizeDropdown.setModel( viewerSizeModel );
        pictureViewerJPanel.add( viewerSizeDropdown, "wrap" );
        // End of PictureViewer size stuff

        final JLabel mximumPictureSizeLabel = new JLabel( Settings.jpoResources.getString( "maximumPictureSizeLabel" ) );
        pictureViewerJPanel.add( mximumPictureSizeLabel );
        maximumPictureSizeJTextField.setPreferredSize( Settings.shortNumberPreferredSize );
        maximumPictureSizeJTextField.setMinimumSize( Settings.shortNumberMinimumSize );
        maximumPictureSizeJTextField.setMaximumSize( Settings.shortNumberMaximumSize );
        pictureViewerJPanel.add( maximumPictureSizeJTextField, "wrap" );

        final JLabel maxCacheJLabel = new JLabel( Settings.jpoResources.getString( "maxCacheLabel" ) );
        pictureViewerJPanel.add( maxCacheJLabel );

        pictureViewerJPanel.add( dontEnlargeJCheckBox, "wrap" );
        pictureViewerJPanel.add( pictureViewerFastScaleJCheckBox );

        // set up the thumbnailSettingsJPanel
        final JPanel thumbnailsJPanel = new JPanel( new MigLayout() );

        final JLabel maxThumbnailsLabel = new JLabel( Settings.jpoResources.getString( "maxThumbnailsLabelText" ) );
        thumbnailsJPanel.add( maxThumbnailsLabel );

        maxThumbnails.setPreferredSize( Settings.shortNumberPreferredSize );
        maxThumbnails.setMinimumSize( Settings.shortNumberMinimumSize );
        maxThumbnails.setMaximumSize( Settings.shortNumberMaximumSize );
        thumbnailsJPanel.add( maxThumbnails, "wrap" );

        final JLabel thumbnailSizeLabel = new JLabel( Settings.jpoResources.getString( "thumbnailSizeLabel" ) );
        thumbnailsJPanel.add( thumbnailSizeLabel );

        thumbnailSize.setPreferredSize( Settings.shortNumberPreferredSize );
        thumbnailSize.setMinimumSize( Settings.shortNumberMinimumSize );
        thumbnailSize.setMaximumSize( Settings.shortNumberMaximumSize );
        thumbnailsJPanel.add( thumbnailSize, "wrap" );

        final JLabel jpgQualitySlider
                = new JLabel( Settings.jpoResources.getString( "lowresJpgQualitySlider" ) );
        thumbnailsJPanel.add( jpgQualitySlider, "wrap" );

        //Create the label table
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put( 0, new JLabel( Settings.jpoResources.getString( "jpgQualityBad" ) ) );
        labelTable.put( 80, new JLabel( Settings.jpoResources.getString( "jpgQualityGood" ) ) );
        labelTable.put( 100, new JLabel( Settings.jpoResources.getString( "jpgQualityBest" ) ) );
        jpgQualityJSlider.setLabelTable( labelTable );

        jpgQualityJSlider.setMajorTickSpacing( 10 );
        jpgQualityJSlider.setMinorTickSpacing( 5 );
        jpgQualityJSlider.setPaintTicks( true );
        jpgQualityJSlider.setPaintLabels( true );
        jpgQualityJSlider.setBorder( BorderFactory.createEmptyBorder( 0, 0, 10, 20 ) );
        thumbnailsJPanel.add( jpgQualityJSlider, "span, grow, wrap" );

        thumbnailsJPanel.add( thumbnailFastScaleJCheckBox );

        // User Functions
        final JPanel userFunctionsJPanel = new JPanel( new MigLayout() );
        final JLabel userFunction1JLabel = new JLabel( Settings.jpoResources.getString( "userFunction1JLabel" ) );
        userFunctionsJPanel.add( userFunction1JLabel, "span, wrap" );

        userFunctionsJPanel.add( new JLabel( Settings.jpoResources.getString( "userFunctionNameJLabel" ) ) );
        userFunction1NameJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        userFunction1NameJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        userFunction1NameJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        userFunctionsJPanel.add( userFunction1NameJTextField, "wrap" );

        userFunctionsJPanel.add( new JLabel( Settings.jpoResources.getString( "userFunctionCmdJLabel" ) ) );
        userFunction1CmdJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        userFunction1CmdJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        userFunction1CmdJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        userFunctionsJPanel.add( userFunction1CmdJTextField, "wrap" );

        final JLabel userFunction2JLabel = new JLabel( Settings.jpoResources.getString( "userFunction2JLabel" ) );
        userFunctionsJPanel.add( userFunction2JLabel, "span, wrap" );

        userFunctionsJPanel.add( new JLabel( Settings.jpoResources.getString( "userFunctionNameJLabel" ) ) );
        userFunction2NameJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        userFunction2NameJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        userFunction2NameJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        userFunctionsJPanel.add( userFunction2NameJTextField, "wrap" );

        userFunctionsJPanel.add( new JLabel( Settings.jpoResources.getString( "userFunctionCmdJLabel" ) ) );

        userFunction2CmdJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        userFunction2CmdJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        userFunction2CmdJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        userFunctionsJPanel.add( userFunction2CmdJTextField, "wrap" );

        final JLabel userFunction3JLabel = new JLabel( Settings.jpoResources.getString( "userFunction3JLabel" ) );
        userFunctionsJPanel.add( userFunction3JLabel, "span, wrap" );

        userFunctionsJPanel.add( new JLabel( Settings.jpoResources.getString( "userFunctionNameJLabel" ) ) );
        userFunction3NameJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        userFunction3NameJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        userFunction3NameJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        userFunctionsJPanel.add( userFunction3NameJTextField, "wrap" );

        userFunctionsJPanel.add( new JLabel( Settings.jpoResources.getString( "userFunctionCmdJLabel" ) ) );
        userFunction3CmdJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        userFunction3CmdJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        userFunction3CmdJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        userFunctionsJPanel.add( userFunction3CmdJTextField, "wrap" );

        final JTextArea userFunctionHelpJTextArea = new JTextArea( Settings.jpoResources.getString( "userFunctionHelpJTextArea" ) );
        userFunctionHelpJTextArea.setEditable( false );
        userFunctionHelpJTextArea.setWrapStyleWord( true );
        userFunctionHelpJTextArea.setLineWrap( true );
        userFunctionsJPanel.add( userFunctionHelpJTextArea, "span, grow" );

        // Email Server
        final JPanel emailServerJPanel = new JPanel( new MigLayout() );
        final JLabel emailJLabel = new JLabel( Settings.jpoResources.getString( "emailJLabel" ) );
        emailServerJPanel.add( emailJLabel, "span, wrap" );

        emailServerJPanel.add( new JLabel( Settings.jpoResources.getString( "predefinedEmailJLabel" ) ) );

        JComboBox<Object> predefinedEmailJComboBox = new JComboBox<>();
        predefinedEmailJComboBox.addItem( "Localhost" );
        predefinedEmailJComboBox.addItem( "Gmail" );
        predefinedEmailJComboBox.addItem( "Compuserve" );
        predefinedEmailJComboBox.addItem( "Hotmail" );
        predefinedEmailJComboBox.addItem( "Other" );
        predefinedEmailJComboBox.addActionListener(( ActionEvent e ) -> {
            JComboBox cb = (JComboBox) e.getSource();
            String cbSelection = (String) cb.getSelectedItem();
            if ( "Localhost".equals( cbSelection ) ) {
                emailServerJTextField.setText( "localhost" );
                emailPortJTextField.setText( "25" );
                authenticationJComboBox.setSelectedIndex( 1 ); //Password
            } else if ( "Compuserve".equals( cbSelection ) ) {
                emailServerJTextField.setText( "smtp.compuserve.com" );
                emailPortJTextField.setText( "25" );
                //emailUserJTextField.setText( "set your username" );
                //emailPasswordJTextField.setText( "set your password" );
                authenticationJComboBox.setSelectedIndex( 1 ); //Password
            } else if ( "Gmail".equals( cbSelection ) ) {
                emailServerJTextField.setText( "smtp.gmail.com" );
                emailPortJTextField.setText( "465" );
                //emailUserJTextField.setText( "set your username" );
                //emailPasswordJTextField.setText( "set your password" );
                authenticationJComboBox.setSelectedIndex( 2 ); //SSL
            } else if ( "Hotmail".equals( cbSelection ) ) {
                emailServerJTextField.setText( "smtp.live.com" );
                emailPortJTextField.setText( "25" );
                //emailUserJTextField.setText( "set your username" );
                //emailPasswordJTextField.setText( "set your password" );
                authenticationJComboBox.setSelectedIndex( 1 ); //Password
            } else if ( "Other".equals( cbSelection ) ) {
                emailServerJTextField.setText( "" );
                emailPortJTextField.setText( "25" );
            }
        });
        emailServerJPanel.add( predefinedEmailJComboBox, "wrap" );

        emailServerJPanel.add( new JLabel( Settings.jpoResources.getString( "emailServerJLabel" ) ) );
        emailServerJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        emailServerJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        emailServerJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        emailServerJPanel.add( emailServerJTextField, "wrap" );

        emailServerJPanel.add( new JLabel( Settings.jpoResources.getString( "emailPortJLabel" ) ) );
        emailPortJTextField.setPreferredSize( Settings.shortNumberPreferredSize );
        emailPortJTextField.setMinimumSize( Settings.shortNumberMinimumSize );
        emailPortJTextField.setMaximumSize( Settings.shortNumberMaximumSize );
        emailServerJPanel.add( emailPortJTextField, "wrap" );

        final JLabel userNameJLabel = new JLabel( Settings.jpoResources.getString( "emailUserJLabel" ) );
        final JLabel passwordJLabel = new JLabel( Settings.jpoResources.getString( "emailPasswordJLabel" ) );
        final JLabel showPasswordLabel = new JLabel();
        final JButton showPasswordButton = new JButton( Settings.jpoResources.getString( "emailShowPasswordButton" ) );

        emailServerJPanel.add( new JLabel( Settings.jpoResources.getString( "emailAuthentication" ) ) );
        authenticationJComboBox.removeAllItems();
        authenticationJComboBox.addItem( "None" );
        authenticationJComboBox.addItem( "Password" );
        authenticationJComboBox.addItem( "SSL" );
        authenticationJComboBox.addActionListener(( ActionEvent e ) -> {
            JComboBox cb = (JComboBox) e.getSource();
            String cbSelection = (String) cb.getSelectedItem();
            switch (Objects.requireNonNull(cbSelection)) {
                case "Password":
                    userNameJLabel.setVisible( true );
                    emailUserJTextField.setVisible( true );
                    passwordJLabel.setVisible( true );
                    emailPasswordJTextField.setVisible( true );
                    showPasswordButton.setVisible( true );
                    showPasswordLabel.setVisible( true );
                    break;
                case "SSL":
                    userNameJLabel.setVisible( true );
                    emailUserJTextField.setVisible( true );
                    passwordJLabel.setVisible( true );
                    emailPasswordJTextField.setVisible( true );
                    showPasswordButton.setVisible( true );
                    showPasswordLabel.setVisible( true );
                    break;
                default: //case "None":
                    emailUserJTextField.setText( "" );
                    userNameJLabel.setVisible( false );
                    emailUserJTextField.setVisible( false );
                    emailPasswordJTextField.setText( "" );
                    passwordJLabel.setVisible( false );
                    emailPasswordJTextField.setVisible( false );
                    showPasswordButton.setVisible( false );
                    showPasswordLabel.setVisible( false );
                    break;
            }
        });
        emailServerJPanel.add( authenticationJComboBox, "wrap" );

        emailServerJPanel.add( userNameJLabel );
        emailUserJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        emailUserJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        emailUserJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        emailServerJPanel.add( emailUserJTextField, "wrap" );

        emailServerJPanel.add( passwordJLabel );
        emailPasswordJTextField.setPreferredSize( Settings.textfieldPreferredSize );
        emailPasswordJTextField.setMinimumSize( Settings.textfieldMinimumSize );
        emailPasswordJTextField.setMaximumSize( Settings.textfieldMaximumSize );
        emailServerJPanel.add( emailPasswordJTextField, "wrap" );

        showPasswordButton.addActionListener(( ActionEvent ae ) -> showPasswordLabel.setText( new String( emailPasswordJTextField.getPassword() ) ));

        emailServerJPanel.add( showPasswordButton );
        emailServerJPanel.add( showPasswordLabel, "wrap" );

        // Cache Panel
        final JPanel cacheJPanel = new JPanel( new MigLayout() );

        cacheJPanel.add( new JLabel( Settings.jpoResources.getString( "thumbnailDirLabel" ) ) );
        cacheJPanel.add( thumbnailCacheDirPathChooser );
        cacheJPanel.add( new JLabel( "(Needs restart)" ), "wrap" );

        cacheJPanel.add( new JLabel( "Highres Stats:" ) );
        cacheJPanel.add( new JLabel( "Lowres Stats:" ), "wrap" );

        cacheJPanel.add( new JScrollPane( highresStatsJTA ) );
        cacheJPanel.add( new JScrollPane( lowresStatsJTA ), "wrap" );

        JButton clearHighresCacheJButton = new JButton( "Clear" );
        clearHighresCacheJButton.addActionListener(( ActionEvent e ) -> clearHighresCache());

        JButton clearThumbnailCacheJButton = new JButton( "Clear" );
        clearThumbnailCacheJButton.addActionListener(( ActionEvent e ) -> clearThumbnailCache());

        cacheJPanel.add( clearHighresCacheJButton );
        cacheJPanel.add( clearThumbnailCacheJButton );

        JButton updateCacheStatsJButton = new JButton( "Update" );
        updateCacheStatsJButton.addActionListener(( ActionEvent e ) -> updateCacheStats());
        cacheJPanel.add( updateCacheStatsJButton );

        // set up the main part of the dialog
        getContentPane().setLayout( new BorderLayout() );

        JTabbedPane tabbedPanel = new JTabbedPane();
        tabbedPanel.setTabPlacement( JTabbedPane.TOP );
        tabbedPanel.setPreferredSize( SETTINGS_DIALOG_SIZE );
        tabbedPanel.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );

        tabbedPanel.add( Settings.jpoResources.getString( "browserWindowSettingsJPanel" ), generalJPanel );
        tabbedPanel.add( Settings.jpoResources.getString( "pictureViewerJPanel" ), pictureViewerJPanel );
        tabbedPanel.add( Settings.jpoResources.getString( "thumbnailSettingsJPanel" ), thumbnailsJPanel );
        tabbedPanel.add( Settings.jpoResources.getString( "userFunctionJPanel" ), userFunctionsJPanel );
        tabbedPanel.add( Settings.jpoResources.getString( "emailJPanel" ), emailServerJPanel );
        tabbedPanel.add( "Cache", cacheJPanel );

        getContentPane().add( tabbedPanel, BorderLayout.NORTH );

        Container buttonContainer = new Container();
        buttonContainer.setLayout( new FlowLayout() );

        JButton saveButton = new JButton( Settings.jpoResources.getString( "genericSaveButtonLabel" ) );
        saveButton.setPreferredSize( Settings.defaultButtonDimension );
        saveButton.setMinimumSize( Settings.defaultButtonDimension );
        saveButton.setMaximumSize( Settings.defaultButtonDimension );
        saveButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        saveButton.addActionListener(( ActionEvent e ) -> {
            writeValues();
            Settings.writeSettings();
            getRid();
        });
        buttonContainer.add( saveButton );

        JButton cancelButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );
        cancelButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelButton.setMinimumSize( Settings.defaultButtonDimension );
        cancelButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        cancelButton.addActionListener(( ActionEvent e ) -> getRid());
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
     * This method sets up the GUI fields according to what the Settings
     * object's values are
     */
    private void initValues() {
        for ( int i = 0; i < Settings.supportedLanguages.length; i++ ) {
            if ( Settings.getCurrentLocale().equals( Settings.supportedLocale[i] ) ) {
                languageJComboBox.setSelectedIndex( i );
                break;
            }
        }

        autoLoadJTextField.setText( Settings.autoLoad );

        startupSizeDropdown.setSelectedIndex( findSizeIndex( Settings.maximiseJpoOnStartup, Settings.mainFrameDimensions ) );
        viewerSizeDropdown.setSelectedIndex( findSizeIndex( Settings.maximisePictureViewerWindow, Settings.pictureViewerDefaultDimensions ) );

        maximumPictureSizeJTextField.setValue( Settings.maximumPictureSize );
        dontEnlargeJCheckBox.setSelected( Settings.dontEnlargeSmallImages );

        //thumbnailPathChooser.setText( Settings.thumbnailPath.getPath() );
        thumbnailCacheDirPathChooser.setText( Settings.thumbnailCacheDirectory );
        maxThumbnails.setValue( Settings.maxThumbnails );
        thumbnailSize.setValue( Settings.thumbnailSize );
        //keepThumbnailsJCheckBox.setSelected( Settings.keepThumbnails );
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
        checkAutoLoad( autoLoadJTextField.getText() );
        updateCacheStats();

    }

    /**
     * returns the index for the size dropdowns based on the supplied
     * parameters.
     *
     * @param maximise whether the index should be maximised
     * @param targetDimension the target size of the window
     * @return the index
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
     * This method writes the values in the GUI widgets into the Settings
     * object.
     */
    private void writeValues() {
        Locale comboBoxLocale = Settings.supportedLocale[languageJComboBox.getSelectedIndex()];
        boolean localeChanged = Settings.setLocale( comboBoxLocale );
        if ( localeChanged ) {
            LOGGER.info( "Locale changed!" );
            JpoEventBus.getInstance().post( new LocaleChangedEvent() );
        }

        Settings.autoLoad = autoLoadJTextField.getText();

        Settings.tagCloudWords = (Integer) tagCloudWordsJSpinner.getValue();

        if ( startupSizeDropdown.getSelectedIndex() == 0 ) {
            Settings.maximiseJpoOnStartup = true;
            Settings.mainFrameDimensions = new Dimension( 0, 0 );
        } else {
            Settings.maximiseJpoOnStartup = false;
            Settings.mainFrameDimensions = new Dimension( Settings.windowSizes[startupSizeDropdown.getSelectedIndex()] );
        }

        Settings.maximumPictureSize = maximumPictureSizeJTextField.getValue();
        Settings.dontEnlargeSmallImages = dontEnlargeJCheckBox.isSelected();

        if ( viewerSizeDropdown.getSelectedIndex() == 0 ) {
            Settings.maximisePictureViewerWindow = true;
            Settings.pictureViewerDefaultDimensions = new Dimension( 0, 0 );
        } else {
            Settings.maximisePictureViewerWindow = false;
            Settings.pictureViewerDefaultDimensions = new Dimension( Settings.windowSizes[viewerSizeDropdown.getSelectedIndex()] );
        }

        Settings.pictureViewerFastScale = pictureViewerFastScaleJCheckBox.isSelected();

        //Settings.thumbnailPath = thumbnailPathChooser.getDirectory();
        //Settings.thumbnailCacheDirectory = thumbnailCacheDirPathChooser.getDirectory().toString();
        //Settings.keepThumbnails = keepThumbnailsJCheckBox.isSelected();

        /*if ( ( !Settings.thumbnailPath.exists() ) && Settings.keepThumbnails ) {
            if ( !Settings.thumbnailPath.mkdirs() ) {
                LOGGER.severe( String.format( "Could not create directory: %s", Settings.thumbnailPath.toString() ) );
            }
        }*/

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
        JpoEventBus.getInstance().post( new UserFunctionsChangedEvent() );
    }

    /**
     * this method verifies that the file specified in the AutoLoadJTextField is
     * valid. It sets the color of the font to red if this is not OK.
     *
     * @param validationFile the file to validate
     */
    public void checkAutoLoad(String validationFile ) {
        LOGGER.log( Level.FINE, "SettingsDialog.checkAutoLoad: called on: {0}", validationFile );
        File testFile = new File( validationFile );

        if ( "".equals( validationFile ) ) {
            autoLoadJTextField.setForeground( Color.black );
            return;
        }

        if ( !testFile.exists() ) {
            LOGGER.log( Level.WARNING, "SettingsDialog.checkAutoLoad: {0} doesn''t exist.", testFile.toString() );
            autoLoadJTextField.setForeground( Color.red );
            return;
        } else {
            if ( !testFile.canRead() ) {
                LOGGER.log( Level.WARNING, "SettingsDialog.checkAutoLoad: {0} can''t read.", testFile.toString() );
                autoLoadJTextField.setForeground( Color.red );
                return;
            }
        }
        autoLoadJTextField.setForeground( Color.black );
    }

    /**
     * method that gets rid of the SettingsDialog
     */
    private void getRid() {
        setVisible( false );
        dispose();
    }

    /**
     * method that brings up a JFileChooser and places the path of the file
     * selected into the JTextField of the autoFileJTextField.
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
     * special inner class that verifies whether the path indicated by the
     * component is valid
     */
    class FileTextFieldVerifier
            extends InputVerifier {

        @Override
        public boolean shouldYieldFocus( JComponent source, JComponent target ) {
            String validationFile = ( (JTextComponent) source ).getText();
            LOGGER.log( Level.INFO, "SettingsDialog.FileTextFieldVerifyer.shouldYieldFocus: called with: {0}", validationFile );
            LOGGER.log( Level.INFO, "JComponent = {0}", Integer.toString( source.hashCode() ) );
            LOGGER.log( Level.INFO, "autoLoadJTextField = {0}", Integer.toString( autoLoadJTextField.hashCode() ) );

            if ( source.hashCode() == autoLoadJTextField.hashCode() ) {
                checkAutoLoad( validationFile );
            }

            return true;
        }

        @Override
        public boolean verify( JComponent input ) {
            LOGGER.log( Level.INFO, "SettingsDialog.FileTextFieldVerifyer.verify: called with: {0}", ( (JTextComponent) input ).getText() );
            return true;
        }
    }

    /**
     * Updates the text areas with the JCS cache statistics
     */
    private void updateCacheStats() {
        highresStatsJTA.setText( JpoCache.getInstance().getHighresCacheStats() );
        lowresStatsJTA.setText( JpoCache.getInstance().getThumbnailCacheStats() );
    }

    /**
     * Clears the JCS highres picture cache
     */
    private void clearHighresCache() {
        JpoCache.getInstance().clearHighresCache();
        updateCacheStats();
    }

    /**
     * Clears the JCS thumbnail cache.
     */
    private void clearThumbnailCache() {
        JpoCache.getInstance().clearThumbnailCache();
        updateCacheStats();
    }

}
