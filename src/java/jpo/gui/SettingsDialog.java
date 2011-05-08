package jpo.gui;

import jpotestground.ResizableJFrameTest;
import jpo.dataModel.Settings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.File;
import java.util.Hashtable;
import java.util.logging.Logger;
import javax.swing.*;

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
    private static final Logger LOGGER = Logger.getLogger(SettingsDialog.class.getName());
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
    private final WholeNumberField maxCacheJTextField = new WholeNumberField(0, 4);
    /**
     *   x coordinates of top left corner of main window
     */
    private final WholeNumberField mainX = new WholeNumberField(0, 6);
    /**
     *   y coordinates of top left corner of main window
     */
    private final WholeNumberField mainY = new WholeNumberField(0, 6);
    /**
     *   width of specific size window
     */
    private final WholeNumberField mainWidth = new WholeNumberField(0, 6);
    /**
     *   height of specific size window
     */
    private final WholeNumberField mainHeight = new WholeNumberField(0, 6);
    /**
     *   x coordinates of top left corner of main window
     */
    private final WholeNumberField pictureX = new WholeNumberField(0, 6);
    /**
     *   y coordinates of top left corner of main window
     */
    private final WholeNumberField pictureY = new WholeNumberField(0, 6);
    /**
     *   width of specific size window
     */
    private final WholeNumberField pictureWidth = new WholeNumberField(0, 6);
    /**
     *   height of specific size window
     */
    private final WholeNumberField pictureHeight = new WholeNumberField(0, 6);
    /**
     *   maximum size of picture
     */
    private final WholeNumberField maximumPictureSizeJTextField = new WholeNumberField(0, 6);
    /**
     *  checkbox that indicates whether small images should be enlarged
     */
    private final JCheckBox dontEnlargeJCheckBox = new JCheckBox(Settings.jpoResources.getString("dontEnlargeJCheckBoxLabel"));
    /**
     *  tickbox that indicates whether to scale the thumbnails quickly
     */
    private final JCheckBox pictureViewerFastScaleJCheckBox = new JCheckBox(Settings.jpoResources.getString("pictureViewerFastScale"));
    /**
     *   fields that allows the user to capture the path for the thumbnails
     */
    private final DirectoryChooser thumbnailPathChooser = new DirectoryChooser(Settings.jpoResources.getString("genericSelectText"),
            DirectoryChooser.DIR_MUST_BE_WRITABLE);
    /**
     *  tickbox that indicates whether thumbnails should be written to disk
     */
    private final JCheckBox keepThumbnailsJCheckBox = new JCheckBox(Settings.jpoResources.getString("keepThumbnailsJCheckBoxLabel"));
    /**
     *     field that allows the user to capture the maximum number of thumbnails to be displayed
     */
    private final WholeNumberField maxThumbnails = new WholeNumberField(0, 4);
    /**
     *   fields that allows the user to capture the desired size of thumbnails
     */
    private final WholeNumberField thumbnailSize = new WholeNumberField(0, 6);
    /**
     *  slider that allows the quality of the jpg's to be specified
     * Should this really be the same as the HTLM Quality Field?
     */
    private final JSlider jpgQualityJSlider = new JSlider(JSlider.HORIZONTAL,
            0, 100, (int) (Settings.defaultHtmlLowresQuality * 100));
    /**
     *  tickbox that indicates whether to scale the thumbnails quickly
     */
    private final JCheckBox thumbnailFastScaleJCheckBox = new JCheckBox(Settings.jpoResources.getString("thumbnailFastScale"));
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
    private final JComboBox languageJComboBox = new JComboBox(Settings.supportedLanguages);
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
     *   Constructor to create the GUI that allows modification of the settings
     * @param modal
     */
    public SettingsDialog(boolean modal) {
        super(Settings.anchorFrame, modal);
        initComponents();
        initValues();
        pack();
        setLocationRelativeTo(Settings.anchorFrame);
        setVisible(true);
    }

    /**
     *   Create the GUI elements
     */
    private void initComponents() {
        setTitle(Settings.jpoResources.getString("settingsDialogTitle"));
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(4, 4, 4, 4);


        // General Settings
        JPanel browserWindowSettingsJPanel = new JPanel();
        browserWindowSettingsJPanel.setLayout(new GridBagLayout());
        browserWindowSettingsJPanel.setBorder(BorderFactory.createEmptyBorder());

        // Language stuff
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        JLabel languageJLabel = new JLabel(Settings.jpoResources.getString("languageJLabel"));
        browserWindowSettingsJPanel.add(languageJLabel, c);

        c.gridx++;
        browserWindowSettingsJPanel.add(languageJComboBox, c);
        // End of Language stuff

        // Initial Windowsize stuff
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        browserWindowSettingsJPanel.add(new JLabel(Settings.jpoResources.getString("windowSizeChoicesJlabel")), c);

        c.gridx++;
        final String[] windowSizeChoices = new String[Settings.windowSizes.length];
        windowSizeChoices[0] = Settings.jpoResources.getString("windowSizeChoicesMaximum");
        for (int i = 1; i < Settings.windowSizes.length; i++) {
            windowSizeChoices[i] = Integer.toString(Settings.windowSizes[i].width) + " x " + Integer.toString(Settings.windowSizes[i].height);
        }
        final DefaultComboBoxModel dcbm = new DefaultComboBoxModel(windowSizeChoices);
        startupSizeDropdown.setModel(dcbm);
        browserWindowSettingsJPanel.add(startupSizeDropdown, c);
        startupSizeDropdown.addActionListener(new ActionListener() {

            boolean firstrun = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (firstrun) {
                    // dont change the window size when setting up the gui
                    firstrun = false;
                } else {
                    if (startupSizeDropdown.getSelectedIndex() == 0) {
                        Settings.anchorFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
                    } else {
                        Settings.anchorFrame.setExtendedState(Frame.NORMAL);
                        Settings.anchorFrame.setSize(Settings.windowSizes[startupSizeDropdown.getSelectedIndex()]);
                    }
                }
            }
        });
        // End of Initial Windowsize stuff

        // PictureViewer size stuff
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        browserWindowSettingsJPanel.add(new JLabel(Settings.jpoResources.getString("pictureViewerSizeChoicesJlabel")), c);

        c.gridx++;
        final DefaultComboBoxModel viewerSizeModel = new DefaultComboBoxModel(windowSizeChoices);
        viewerSizeDropdown.setModel(viewerSizeModel);
        browserWindowSettingsJPanel.add(viewerSizeDropdown, c);
        // End of PictureViewer size stuff



        //Autoload stuff
        c.gridx = 0;
        c.gridy++;
        JLabel autoLoadJLabel = new JLabel(Settings.jpoResources.getString("autoLoadJLabelLabel"));
        browserWindowSettingsJPanel.add(autoLoadJLabel, c);

        c.gridy++;
        c.gridwidth = 2;
        c.weightx = 0.7f;
        c.fill = GridBagConstraints.HORIZONTAL;
        autoLoadJTextField.setPreferredSize(Settings.filenameFieldPreferredSize);
        autoLoadJTextField.setMinimumSize(Settings.filenameFieldMinimumSize);
        autoLoadJTextField.setMaximumSize(Settings.filenameFieldMaximumSize);
        autoLoadJTextField.setInputVerifier(new FileTextFieldVerifier());
        browserWindowSettingsJPanel.add(autoLoadJTextField, c);

        c.gridx = 2;
        c.gridwidth = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        JButton autoLoadJButton = new JButton(Settings.jpoResources.getString("threeDotText"));
        autoLoadJButton.setPreferredSize(Settings.threeDotButtonSize);
        autoLoadJButton.setMinimumSize(Settings.threeDotButtonSize);
        autoLoadJButton.setMaximumSize(Settings.threeDotButtonSize);
        autoLoadJButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                autoLoadChooser();
            }
        });
        browserWindowSettingsJPanel.add(autoLoadJButton, c);
        // End of Autoload stuff


        // set up the pictureViewerJPanel
        JPanel pictureViewerJPanel = new JPanel();
        pictureViewerJPanel.setLayout(new GridBagLayout());
        pictureViewerJPanel.setBorder(BorderFactory.createEmptyBorder());



        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        JLabel mximumPictureSizeLabel = new JLabel(Settings.jpoResources.getString("maximumPictureSizeLabel"));
        pictureViewerJPanel.add(mximumPictureSizeLabel, c);

        c.gridx = 2;
        c.gridwidth = 1;
        maximumPictureSizeJTextField.setPreferredSize(Settings.shortNumberPreferredSize);
        maximumPictureSizeJTextField.setMinimumSize(Settings.shortNumberMinimumSize);
        maximumPictureSizeJTextField.setMaximumSize(Settings.shortNumberMaximumSize);
        pictureViewerJPanel.add(maximumPictureSizeJTextField, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        JLabel maxCacheJLabel = new JLabel(Settings.jpoResources.getString("maxCacheLabel"));
        pictureViewerJPanel.add(maxCacheJLabel, c);

        c.gridx = 2;
        c.gridwidth = 1;
        maxCacheJTextField.setPreferredSize(Settings.shortNumberPreferredSize);
        maxCacheJTextField.setMinimumSize(Settings.shortNumberMinimumSize);
        maxCacheJTextField.setMaximumSize(Settings.shortNumberMaximumSize);
        pictureViewerJPanel.add(maxCacheJTextField, c);


        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 3;
        pictureViewerJPanel.add(dontEnlargeJCheckBox, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 3;
        pictureViewerJPanel.add(pictureViewerFastScaleJCheckBox, c);


        // set up the thumbnailSettingsJPanel
        JPanel thumbnailSettingsJPanel = new JPanel();
        thumbnailSettingsJPanel.setLayout(new GridBagLayout());

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        JLabel thumbnailPathJLabel = new JLabel(Settings.jpoResources.getString("thumbnailDirLabel"));
        thumbnailPathJLabel.setForeground(Color.black);
        thumbnailSettingsJPanel.add(thumbnailPathJLabel, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        thumbnailSettingsJPanel.add(thumbnailPathChooser, c);



        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 3;
        thumbnailSettingsJPanel.add(keepThumbnailsJCheckBox, c);


        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.NONE;
        JButton zapThumbnailsJButton = new JButton(Settings.jpoResources.getString("zapThumbnails"));
        zapThumbnailsJButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                zapThumbnails();
            }
        });
        Dimension zapButtonSize = new Dimension(180, Settings.defaultButtonDimension.height);
        zapThumbnailsJButton.setPreferredSize(zapButtonSize);
        zapThumbnailsJButton.setMinimumSize(zapButtonSize);
        zapThumbnailsJButton.setMaximumSize(zapButtonSize);
        thumbnailSettingsJPanel.add(zapThumbnailsJButton, c);



        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        JLabel maxThumbnailsLabel = new JLabel(Settings.jpoResources.getString("maxThumbnailsLabelText"));
        maxThumbnailsLabel.setForeground(Color.black);
        thumbnailSettingsJPanel.add(maxThumbnailsLabel, c);

        c.gridx++;
        maxThumbnails.setPreferredSize(Settings.shortNumberPreferredSize);
        maxThumbnails.setMinimumSize(Settings.shortNumberMinimumSize);
        maxThumbnails.setMaximumSize(Settings.shortNumberMaximumSize);
        thumbnailSettingsJPanel.add(maxThumbnails, c);

        c.gridx = 0;
        c.gridy++;
        JLabel thumbnailSizeLabel = new JLabel(Settings.jpoResources.getString("thumbnailSizeLabel"));
        thumbnailSizeLabel.setForeground(Color.black);
        thumbnailSettingsJPanel.add(thumbnailSizeLabel, c);

        c.gridx++;
        thumbnailSize.setPreferredSize(Settings.shortNumberPreferredSize);
        thumbnailSize.setMinimumSize(Settings.shortNumberMinimumSize);
        thumbnailSize.setMaximumSize(Settings.shortNumberMaximumSize);
        thumbnailSettingsJPanel.add(thumbnailSize, c);


        JLabel jpgQualitySlider =
                new JLabel(Settings.jpoResources.getString("lowresJpgQualitySlider"));
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 3;
        thumbnailSettingsJPanel.add(jpgQualitySlider, c);

        //Create the label table
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(new Integer(0), new JLabel(Settings.jpoResources.getString("jpgQualityBad")));
        labelTable.put(new Integer(80), new JLabel(Settings.jpoResources.getString("jpgQualityGood")));
        labelTable.put(new Integer(100), new JLabel(Settings.jpoResources.getString("jpgQualityBest")));
        jpgQualityJSlider.setLabelTable(labelTable);

        jpgQualityJSlider.setMajorTickSpacing(10);
        jpgQualityJSlider.setMinorTickSpacing(5);
        jpgQualityJSlider.setPaintTicks(true);
        jpgQualityJSlider.setPaintLabels(true);
        jpgQualityJSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 20));
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 3;
        thumbnailSettingsJPanel.add(jpgQualityJSlider, c);


        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 3;
        thumbnailSettingsJPanel.add(thumbnailFastScaleJCheckBox, c);



        // User Functions
        JPanel userFunctionJPanel = new JPanel();
        userFunctionJPanel.setLayout(new GridBagLayout());
        userFunctionJPanel.setBorder(BorderFactory.createEmptyBorder());

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        JLabel userFunction1JLabel = new JLabel(Settings.jpoResources.getString("userFunction1JLabel"));
        userFunctionJPanel.add(userFunction1JLabel, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        userFunctionJPanel.add(new JLabel(Settings.jpoResources.getString("userFunctionNameJLabel")), c);
        c.gridx = 1;
        userFunction1NameJTextField.setPreferredSize(Settings.textfieldPreferredSize);
        userFunction1NameJTextField.setMinimumSize(Settings.textfieldMinimumSize);
        userFunction1NameJTextField.setMaximumSize(Settings.textfieldMaximumSize);
        userFunctionJPanel.add(userFunction1NameJTextField, c);
        c.gridx = 0;
        c.gridy++;
        userFunctionJPanel.add(new JLabel(Settings.jpoResources.getString("userFunctionCmdJLabel")), c);
        c.gridx = 1;
        userFunction1CmdJTextField.setPreferredSize(Settings.textfieldPreferredSize);
        userFunction1CmdJTextField.setMinimumSize(Settings.textfieldMinimumSize);
        userFunction1CmdJTextField.setMaximumSize(Settings.textfieldMaximumSize);
        userFunctionJPanel.add(userFunction1CmdJTextField, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        JLabel userFunction2JLabel = new JLabel(Settings.jpoResources.getString("userFunction2JLabel"));
        userFunctionJPanel.add(userFunction2JLabel, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        userFunctionJPanel.add(new JLabel(Settings.jpoResources.getString("userFunctionNameJLabel")), c);
        c.gridx = 1;
        userFunction2NameJTextField.setPreferredSize(Settings.textfieldPreferredSize);
        userFunction2NameJTextField.setMinimumSize(Settings.textfieldMinimumSize);
        userFunction2NameJTextField.setMaximumSize(Settings.textfieldMaximumSize);
        userFunctionJPanel.add(userFunction2NameJTextField, c);
        c.gridx = 0;
        c.gridy++;
        userFunctionJPanel.add(new JLabel(Settings.jpoResources.getString("userFunctionCmdJLabel")), c);
        c.gridx = 1;
        userFunction2CmdJTextField.setPreferredSize(Settings.textfieldPreferredSize);
        userFunction2CmdJTextField.setMinimumSize(Settings.textfieldMinimumSize);
        userFunction2CmdJTextField.setMaximumSize(Settings.textfieldMaximumSize);
        userFunctionJPanel.add(userFunction2CmdJTextField, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        JLabel userFunction3JLabel = new JLabel(Settings.jpoResources.getString("userFunction3JLabel"));
        userFunctionJPanel.add(userFunction3JLabel, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        userFunctionJPanel.add(new JLabel(Settings.jpoResources.getString("userFunctionNameJLabel")), c);
        c.gridx = 1;
        userFunction3NameJTextField.setPreferredSize(Settings.textfieldPreferredSize);
        userFunction3NameJTextField.setMinimumSize(Settings.textfieldMinimumSize);
        userFunction3NameJTextField.setMaximumSize(Settings.textfieldMaximumSize);
        userFunctionJPanel.add(userFunction3NameJTextField, c);
        c.gridx = 0;
        c.gridy++;
        userFunctionJPanel.add(new JLabel(Settings.jpoResources.getString("userFunctionCmdJLabel")), c);
        c.gridx = 1;
        userFunction3CmdJTextField.setPreferredSize(Settings.textfieldPreferredSize);
        userFunction3CmdJTextField.setMinimumSize(Settings.textfieldMinimumSize);
        userFunction3CmdJTextField.setMaximumSize(Settings.textfieldMaximumSize);
        userFunctionJPanel.add(userFunction3CmdJTextField, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        JTextArea userFunctionHelpJTextArea = new JTextArea(Settings.jpoResources.getString("userFunctionHelpJTextArea"));
        userFunctionHelpJTextArea.setEditable(false);
        userFunctionHelpJTextArea.setWrapStyleWord(true);
        userFunctionHelpJTextArea.setLineWrap(true);
        userFunctionJPanel.add(userFunctionHelpJTextArea, c);



        // Email Server
        JPanel emailJPanel = new JPanel();
        emailJPanel.setLayout(new GridBagLayout());
        emailJPanel.setBorder(BorderFactory.createEmptyBorder());

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        JLabel emailJLabel = new JLabel(Settings.jpoResources.getString("emailJLabel"));
        emailJPanel.add(emailJLabel, c);

        c.gridy++;
        c.gridwidth = 1;
        emailJPanel.add(new JLabel(Settings.jpoResources.getString("predefinedEmailJLabel")), c);

        JComboBox predefinedEmailJComboBox = new JComboBox();
        predefinedEmailJComboBox.addItem("Localhost");
        predefinedEmailJComboBox.addItem("Gmail");
        predefinedEmailJComboBox.addItem("Compuserve");
        predefinedEmailJComboBox.addItem("Hotmail");
        predefinedEmailJComboBox.addItem("Other");
        predefinedEmailJComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                String cbSelection = (String) cb.getSelectedItem();
                if (cbSelection.equals("Localhost")) {
                    emailServerJTextField.setText("localhost");
                    emailPortJTextField.setText("25");
                    authenticationJComboBox.setSelectedIndex(1); //Password
                } else if (cbSelection.equals("Compuserve")) {
                    emailServerJTextField.setText("smtp.compuserve.com");
                    emailPortJTextField.setText("25");
                    //emailUserJTextField.setText( "set your username" );
                    //emailPasswordJTextField.setText( "set your password" );
                    authenticationJComboBox.setSelectedIndex(1); //Password
                } else if (cbSelection.equals("Gmail")) {
                    emailServerJTextField.setText("smtp.gmail.com");
                    emailPortJTextField.setText("465");
                    //emailUserJTextField.setText( "set your username" );
                    //emailPasswordJTextField.setText( "set your password" );
                    authenticationJComboBox.setSelectedIndex(2); //SSL
                } else if (cbSelection.equals("Hotmail")) {
                    emailServerJTextField.setText("smtp.live.com");
                    emailPortJTextField.setText("25");
                    //emailUserJTextField.setText( "set your username" );
                    //emailPasswordJTextField.setText( "set your password" );
                    authenticationJComboBox.setSelectedIndex(1); //Password
                } else if (cbSelection.equals("Other")) {
                    emailServerJTextField.setText("");
                    emailPortJTextField.setText("25");
                }

            }
        });
        c.gridx++;
        emailJPanel.add(predefinedEmailJComboBox, c);


        c.gridx = 0;
        c.gridy++;
        emailJPanel.add(new JLabel(Settings.jpoResources.getString("emailServerJLabel")), c);
        c.gridx++;
        emailServerJTextField.setPreferredSize(Settings.textfieldPreferredSize);
        emailServerJTextField.setMinimumSize(Settings.textfieldMinimumSize);
        emailServerJTextField.setMaximumSize(Settings.textfieldMaximumSize);
        emailJPanel.add(emailServerJTextField, c);

        c.gridx = 0;
        c.gridy++;
        emailJPanel.add(new JLabel(Settings.jpoResources.getString("emailPortJLabel")), c);
        c.gridx++;
        emailPortJTextField.setPreferredSize(Settings.shortNumberPreferredSize);
        emailPortJTextField.setMinimumSize(Settings.shortNumberMinimumSize);
        emailPortJTextField.setMaximumSize(Settings.shortNumberMaximumSize);
        emailJPanel.add(emailPortJTextField, c);

        final JLabel userNameJLabel = new JLabel(Settings.jpoResources.getString("emailUserJLabel"));
        final JLabel passwordJLabel = new JLabel(Settings.jpoResources.getString("emailPasswordJLabel"));
        final JLabel showPasswordLabel = new JLabel();
        final JButton showPasswordButton = new JButton(Settings.jpoResources.getString("emailShowPasswordButton"));

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        emailJPanel.add(new JLabel(Settings.jpoResources.getString("emailAuthentication")), c);
        //authenticationJComboBox = new JComboBox();
        authenticationJComboBox.removeAllItems();
        authenticationJComboBox.addItem("None");
        authenticationJComboBox.addItem("Password");
        authenticationJComboBox.addItem("SSL");
        authenticationJComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                String cbSelection = (String) cb.getSelectedItem();
                if (cbSelection.equals("None")) {
                    emailUserJTextField.setText("");
                    userNameJLabel.setVisible(false);
                    emailUserJTextField.setVisible(false);
                    emailPasswordJTextField.setText("");
                    passwordJLabel.setVisible(false);
                    emailPasswordJTextField.setVisible(false);
                    showPasswordButton.setVisible(false);
                    showPasswordLabel.setVisible(false);
                } else if (cbSelection.equals("Password")) {
                    userNameJLabel.setVisible(true);
                    emailUserJTextField.setVisible(true);
                    passwordJLabel.setVisible(true);
                    emailPasswordJTextField.setVisible(true);
                    showPasswordButton.setVisible(true);
                    showPasswordLabel.setVisible(true);
                } else if (cbSelection.equals("SSL")) {
                    userNameJLabel.setVisible(true);
                    emailUserJTextField.setVisible(true);
                    passwordJLabel.setVisible(true);
                    emailPasswordJTextField.setVisible(true);
                    showPasswordButton.setVisible(true);
                    showPasswordLabel.setVisible(true);
                }

            }
        });
        c.gridx++;
        emailJPanel.add(authenticationJComboBox, c);


        c.gridx = 0;
        c.gridy++;
        emailJPanel.add(userNameJLabel, c);
        c.gridx++;
        emailUserJTextField.setPreferredSize(Settings.textfieldPreferredSize);
        emailUserJTextField.setMinimumSize(Settings.textfieldMinimumSize);
        emailUserJTextField.setMaximumSize(Settings.textfieldMaximumSize);
        emailJPanel.add(emailUserJTextField, c);

        c.gridx = 0;
        c.gridy++;
        emailJPanel.add(passwordJLabel, c);
        c.gridx++;
        emailPasswordJTextField.setPreferredSize(Settings.textfieldPreferredSize);
        emailPasswordJTextField.setMinimumSize(Settings.textfieldMinimumSize);
        emailPasswordJTextField.setMaximumSize(Settings.textfieldMaximumSize);
        emailJPanel.add(emailPasswordJTextField, c);

        showPasswordButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                showPasswordLabel.setText(new String(emailPasswordJTextField.getPassword()));
            }
        });

        c.gridx = 0;
        c.gridy++;
        emailJPanel.add(showPasswordButton, c);
        c.gridx++;
        emailJPanel.add(showPasswordLabel, c);



        // Debug Panel
        JPanel debugJPanel = new JPanel();
        debugJPanel.setLayout(new GridBagLayout());
        debugJPanel.setBorder(BorderFactory.createEmptyBorder());

        // Logfile stuff
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        logfileJCheckBox.setText(Settings.jpoResources.getString("logfileJCheckBoxLabel"));
        final JLabel logfileJLabel = new JLabel(Settings.jpoResources.getString("logfileJLabelLabel"));
        final JButton logfileJButton = new JButton(Settings.jpoResources.getString("threeDotText"));
        logfileJCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                logfileJLabel.setVisible(logfileJCheckBox.isSelected());
                logfileJTextField.setVisible(logfileJCheckBox.isSelected());
                logfileJButton.setVisible(logfileJCheckBox.isSelected());
                checkLogfile(logfileJTextField.getText());
            }
        });
        debugJPanel.add(logfileJCheckBox, c);

        c.gridy++;
        debugJPanel.add(logfileJLabel, c);

        c.gridy++;
        c.weightx = 0.7f;
        c.fill = GridBagConstraints.HORIZONTAL;
        logfileJTextField.setPreferredSize(Settings.filenameFieldPreferredSize);
        logfileJTextField.setMinimumSize(Settings.filenameFieldMinimumSize);
        logfileJTextField.setMaximumSize(Settings.filenameFieldMaximumSize);
        logfileJTextField.setInputVerifier(new FileTextFieldVerifier());
        debugJPanel.add(logfileJTextField, c);

        c.gridx = 2;
        c.gridwidth = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        logfileJButton.setPreferredSize(Settings.threeDotButtonSize);
        logfileJButton.setMinimumSize(Settings.threeDotButtonSize);
        logfileJButton.setMaximumSize(Settings.threeDotButtonSize);
        logfileJButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                logfileChooser();
            }
        });
        debugJPanel.add(logfileJButton, c);
        // end of Logfile Stuff

        c.gridx = 0;
        c.gridwidth = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridy++;
        JButton screenSizeTestButton = new JButton("Window Resize Test");
        screenSizeTestButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                getRid(); // the dialog is modal and would prevent us using the frame
                new ResizableJFrameTest();
            }
        });
        debugJPanel.add(screenSizeTestButton, c);





        // set up the main part of the dialog
        getContentPane().setLayout(new BorderLayout());

        JTabbedPane tp = new JTabbedPane();
        tp.setTabPlacement(JTabbedPane.TOP);
        tp.setPreferredSize(new Dimension(500, 400));
        tp.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        tp.add(Settings.jpoResources.getString("browserWindowSettingsJPanel"), browserWindowSettingsJPanel);
        tp.add(Settings.jpoResources.getString("pictureViewerJPanel"), pictureViewerJPanel);
        tp.add(Settings.jpoResources.getString("thumbnailSettingsJPanel"), thumbnailSettingsJPanel);
        tp.add(Settings.jpoResources.getString("userFunctionJPanel"), userFunctionJPanel);
        tp.add(Settings.jpoResources.getString("emailJPanel"), emailJPanel);
        tp.add("Debug", debugJPanel);

        getContentPane().add(tp, BorderLayout.NORTH);

        /**
         *   container to neatly group the 2 buttons
         */
        Container buttonContainer = new Container();

        buttonContainer.setLayout(new FlowLayout());

        JButton saveButton = new JButton(Settings.jpoResources.getString("genericSaveButtonLabel"));
        saveButton.setPreferredSize(Settings.defaultButtonDimension);
        saveButton.setMinimumSize(Settings.defaultButtonDimension);
        saveButton.setMaximumSize(Settings.defaultButtonDimension);
        saveButton.setBorder(BorderFactory.createRaisedBevelBorder());
        saveButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                consistencyCheck();
                writeValues();
                Settings.writeSettings();
                getRid();
            }
        });
        buttonContainer.add(saveButton);

        JButton cancelButton = new JButton(Settings.jpoResources.getString("genericCancelText"));
        cancelButton.setPreferredSize(Settings.defaultButtonDimension);
        cancelButton.setMinimumSize(Settings.defaultButtonDimension);
        cancelButton.setMaximumSize(Settings.defaultButtonDimension);
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                getRid();
            }
        });
        buttonContainer.add(cancelButton);


        getContentPane().add(buttonContainer, BorderLayout.SOUTH);


        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent evt) {
                getRid();
            }
        });
    }

    /**
     *   This method sets up the GUI fields according to what the
     *   Settings object's values are
     */
    private void initValues() {
        for (int i = 0; i < Settings.supportedLanguages.length; i++) {
            if (Settings.getCurrentLocale().equals(Settings.supportedLocale[i])) {
                languageJComboBox.setSelectedIndex(i);
                break;
            }
        }

        autoLoadJTextField.setText(Settings.autoLoad);
        logfileJCheckBox.setSelected(Settings.writeLog);
        logfileJTextField.setText(Settings.logfile.getPath());

        startupSizeDropdown.setSelectedIndex(findSizeIndex(Settings.maximiseJpoOnStartup, Settings.mainFrameDimensions));
        viewerSizeDropdown.setSelectedIndex(findSizeIndex(Settings.maximisePictureViewerWindow, Settings.pictureViewerDefaultDimensions));

        maximumPictureSizeJTextField.setValue(Settings.maximumPictureSize);
        maxCacheJTextField.setValue(Settings.maxCache);
        dontEnlargeJCheckBox.setSelected(Settings.dontEnlargeSmallImages);

        thumbnailPathChooser.setText(Settings.thumbnailPath.getPath());
        maxThumbnails.setValue(Settings.maxThumbnails);
        thumbnailSize.setValue(Settings.thumbnailSize);
        keepThumbnailsJCheckBox.setSelected(Settings.keepThumbnails);
        jpgQualityJSlider.setValue((int) (Settings.defaultHtmlLowresQuality * 100));
        thumbnailFastScaleJCheckBox.setSelected(Settings.thumbnailFastScale);

        userFunction1NameJTextField.setText(Settings.userFunctionNames[0]);
        userFunction2NameJTextField.setText(Settings.userFunctionNames[1]);
        userFunction3NameJTextField.setText(Settings.userFunctionNames[2]);

        userFunction1CmdJTextField.setText(Settings.userFunctionCmd[0]);
        userFunction2CmdJTextField.setText(Settings.userFunctionCmd[1]);
        userFunction3CmdJTextField.setText(Settings.userFunctionCmd[2]);

        emailServerJTextField.setText(Settings.emailServer);
        emailPortJTextField.setText(Settings.emailPort);
        authenticationJComboBox.setSelectedIndex(Settings.emailAuthentication);
        emailUserJTextField.setText(Settings.emailUser);
        emailPasswordJTextField.setText(Settings.emailPassword);

        // deliberately placed here to stop change events being triggered while the fields are
        // being initialised.
        checkLogfile(logfileJTextField.getText());
        checkAutoLoad(autoLoadJTextField.getText());
    }

    /**
     * returns the index for the size dropdowns based on the supplied parameters.
     * @param maximise  whether the index should be maximised
     * @param targetDimension  the target size of the window
     */
    private static int findSizeIndex(boolean maximise,
            Dimension targetDimension) {
        if (maximise) {
            return 0;
        } else {
            int settingsArea = targetDimension.width * targetDimension.height;
            int index = 1;
            for (int i = 1; i < Settings.windowSizes.length; i++) {
                if (Settings.windowSizes[i].width * Settings.windowSizes[i].height <= settingsArea) {
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
        if ((!checkLogfile(logfileJTextField.getText())) && logfileJCheckBox.isSelected()) {
            // disable logging if logfile is not in order
            JOptionPane.showMessageDialog(Settings.anchorFrame,
                    Settings.jpoResources.getString("generalLogFileError"),
                    Settings.jpoResources.getString("settingsError"),
                    JOptionPane.ERROR_MESSAGE);
            logfileJCheckBox.setSelected(false);
        }


        if ((!thumbnailPathChooser.setColor())) { // TODO: This seems very odd
            JOptionPane.showMessageDialog(Settings.anchorFrame,
                    Settings.jpoResources.getString("thumbnailDirError"),
                    Settings.jpoResources.getString("settingsError"),
                    JOptionPane.ERROR_MESSAGE);
            logfileJCheckBox.setSelected(false);
        }
    }

    /**
     *   This method writes the values in the GUI widgets into the Settings object.
     */
    private void writeValues() {
        Settings.setLocale(Settings.supportedLocale[languageJComboBox.getSelectedIndex()]);

        Settings.autoLoad = autoLoadJTextField.getText();

        Settings.logfile = new File(logfileJTextField.getText());
        Settings.writeLog = logfileJCheckBox.isSelected();

        if (startupSizeDropdown.getSelectedIndex() == 0) {
            Settings.maximiseJpoOnStartup = true;
            Settings.mainFrameDimensions = new Dimension(0, 0);
        } else {
            Settings.maximiseJpoOnStartup = false;
            Settings.mainFrameDimensions = new Dimension(Settings.windowSizes[startupSizeDropdown.getSelectedIndex()]);
        }

        Settings.maximumPictureSize = maximumPictureSizeJTextField.getValue();
        Settings.maxCache = maxCacheJTextField.getValue();
        Settings.dontEnlargeSmallImages = dontEnlargeJCheckBox.isSelected();

        if (viewerSizeDropdown.getSelectedIndex() == 0) {
            Settings.maximisePictureViewerWindow = true;
            Settings.pictureViewerDefaultDimensions = new Dimension(0, 0);
        } else {
            Settings.maximisePictureViewerWindow = false;
            Settings.pictureViewerDefaultDimensions = new Dimension(Settings.windowSizes[viewerSizeDropdown.getSelectedIndex()]);
        }

        Settings.pictureViewerFastScale = pictureViewerFastScaleJCheckBox.isSelected();

        Settings.thumbnailPath = thumbnailPathChooser.getDirectory();
        Settings.keepThumbnails = keepThumbnailsJCheckBox.isSelected();

        if ((!Settings.thumbnailPath.exists()) && Settings.keepThumbnails) {
            if (!Settings.thumbnailPath.mkdirs()) {
                LOGGER.severe(String.format("Could not create directory: %s", Settings.thumbnailPath.toString()));
            }
        }

        Settings.maxThumbnails = maxThumbnails.getValue();
        Settings.thumbnailSize = thumbnailSize.getValue();
        Settings.defaultHtmlLowresQuality = ((float) jpgQualityJSlider.getValue()) / 100;
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
        Settings.emailPassword = new String(emailPasswordJTextField.getPassword());

        Settings.validateSettings();
        Settings.notifyUserFunctionsChanged();
    }

    /**
     *   this method verifies that the file specified in the logfileJTextField
     *   is valid. It sets the color of the font to red if this is not ok and
     *   returns false to the caller. If all is fine it returns true;
     * @param validationFile
     * @return
     */
    public boolean checkLogfile(String validationFile) {
        File testFile = new File(validationFile);

        if (testFile.exists()) {
            if (!testFile.canWrite()) {
                logfileJTextField.setForeground(Color.red);
                LOGGER.warning("logfile exists but can't be written: " + testFile);
                return false;
            }
            if (!testFile.isFile()) {
                logfileJTextField.setForeground(Color.red);
                LOGGER.warning("isFile failed: " + testFile);
                return false;
            }
        } else {
            File testFileParent = testFile.getParentFile();
            if (testFileParent == null) {
                logfileJTextField.setForeground(Color.red);
                LOGGER.warning("Logfile can't be the root directory!");
                return false;
            }
            if (!testFileParent.canWrite()) {
                logfileJTextField.setForeground(Color.red);
                LOGGER.warning("Parent Directory is read only!");
                return false;
            }
        }

        logfileJTextField.setForeground(Color.black);
        return true;
    }

    /**
     *   this method verifies that the file specified in the logfileJTextField
     *   is valid. It sets the color of the font to red if this is not ok and
     *   returns false to the caller. If all is fine it returns true;
     * @param validationFile
     * @return
     */
    public boolean checkAutoLoad(String validationFile) {
        LOGGER.fine("SettingsDialog.checkAutoLoad: called on: " + validationFile);
        File testFile = new File(validationFile);

        if (validationFile.equals("")) {
            autoLoadJTextField.setForeground(Color.black);
            return false;
        }

        if (!testFile.exists()) {
            LOGGER.warning("SettingsDialog.checkAutoLoad: " + testFile.toString() + " doesn't exist.");
            autoLoadJTextField.setForeground(Color.red);
            return false;
        } else {
            if (!testFile.canRead()) {
                LOGGER.warning("SettingsDialog.checkAutoLoad: " + testFile.toString() + " can't read.");
                autoLoadJTextField.setForeground(Color.red);
                return false;
            }
        }
        autoLoadJTextField.setForeground(Color.black);
        return true;
    }

    /**
     *  method that gets rid of the SettingsDialog
     */
    private void getRid() {
        setVisible(false);
        dispose();
    }

    /**
     *  method that brings up a JFileChooser and places the path of the file selected into the
     *  JTextField of the autoFileJTextField.
     */
    private void autoLoadChooser() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(new XmlFilter());

        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setDialogTitle(Settings.jpoResources.getString("autoLoadChooserTitle"));
        jFileChooser.setCurrentDirectory(new File(autoLoadJTextField.getText()));

        int returnVal = jFileChooser.showDialog(this, Settings.jpoResources.getString("genericSelectText"));
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            autoLoadJTextField.setText(jFileChooser.getSelectedFile().getPath());
            checkAutoLoad(autoLoadJTextField.getText());
        }
    }

    /**
     *  method that brings up a JFileChooser and places the path of the file selected into the
     *  JTextField of the logfileJTextField.
     */
    private void logfileChooser() {
        JFileChooser jFileChooser = new JFileChooser();
        //jFileChooser.setFileFilter( new XmlFilter() );

        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setDialogTitle(Settings.jpoResources.getString("logfileChooserTitle"));
        jFileChooser.setCurrentDirectory(new File(autoLoadJTextField.getText()));

        int returnVal = jFileChooser.showDialog(this, Settings.jpoResources.getString("genericSelectText"));
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            logfileJTextField.setText(jFileChooser.getSelectedFile().getPath());
            checkLogfile(logfileJTextField.getText());
        }
    }

    /**
     *  special inner class that verifies whether the path indicated by the component is
     *  valid
     */
    class FileTextFieldVerifier
            extends InputVerifier {

        @Override
        public boolean shouldYieldFocus(JComponent input) {
            String validationFile = ((JTextField) input).getText();
            LOGGER.info("SettingsDialog.FileTextFieldVerifyer.shouldYieldFocus: called with: " + validationFile);
            LOGGER.info("JComponent = " + Integer.toString(input.hashCode()));
            LOGGER.info("logfileJTextField = " + Integer.toString(logfileJTextField.hashCode()));
            LOGGER.info("autoLoadJTextField = " + Integer.toString(autoLoadJTextField.hashCode()));
            if (input.hashCode() == logfileJTextField.hashCode()) {
                checkLogfile(validationFile);
            } else if (input.hashCode() == autoLoadJTextField.hashCode()) {
                checkAutoLoad(validationFile);
            }

            return true;
        }

        public boolean verify(JComponent input) {
            LOGGER.info("SettingsDialog.FileTextFieldVerifyer.verify: called with: " + ((JTextField) input).getText());
            return true;
        }
    }

    /**
     *  brings up an are you sure dialog and then zaps all the
     *  thumbnauil images
     */
    public void zapThumbnails() {
        if ((!thumbnailPathChooser.setColor())) {//TODO: Seems odd to use a GUI component to validate a path
            JOptionPane.showMessageDialog(Settings.anchorFrame,
                    Settings.jpoResources.getString("thumbnailDirError"),
                    Settings.jpoResources.getString("settingsError"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        File thumbnailDirFile = thumbnailPathChooser.getDirectory();

        int option = JOptionPane.showConfirmDialog(
                Settings.anchorFrame,
                Settings.jpoResources.getString("zapThumbnails") + "\n" + thumbnailDirFile.toString() + "\n" + Settings.jpoResources.getString("areYouSure"),
                Settings.jpoResources.getString("FileDeleteTitle"),
                JOptionPane.OK_CANCEL_OPTION);

        if (option == 0) {
            File[] thumbnailFiles = thumbnailDirFile.listFiles(new java.io.FileFilter() {

                public boolean accept(File file) {
                    return file.getName().startsWith(Settings.thumbnailPrefix);
                }
            });
            for (int i = 0; i < thumbnailFiles.length; i++) {
                boolean success = thumbnailFiles[i].delete();
            }
            // it is not a good idea to reset the counter since
            // this can lead to some thumbnails getting the same
            // id. Especially in different collections.
            //Settings.thumbnailCounter = 0;
            JOptionPane.showMessageDialog(Settings.anchorFrame,
                    Integer.toString(thumbnailFiles.length) + Settings.jpoResources.getString("thumbnailsDeleted"),
                    Settings.jpoResources.getString("zapThumbnails"),
                    JOptionPane.INFORMATION_MESSAGE);

        }

    }
}
