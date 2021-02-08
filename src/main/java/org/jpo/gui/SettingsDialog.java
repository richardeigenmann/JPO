package org.jpo.gui;

/*
 Copyright (C) 2002-2021  Richard Eigenmann, Zürich, Switzerland
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
import org.jetbrains.annotations.NotNull;
import org.jpo.cache.JpoCache;
import org.jpo.datamodel.Settings;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.LocaleChangedEvent;
import org.jpo.eventbus.UserFunctionsChangedEvent;
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
 * A window to modify the settings of the JPO application
 *
 * @author Richard Eigenmann
 */
public class SettingsDialog extends JDialog {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(SettingsDialog.class.getName());

    /**
     * Defines the size of this dialog box
     */
    private static final Dimension SETTINGS_DIALOG_SIZE = new Dimension(700, 330);

    /**
     * field that allows the user to capture the file that should be
     * automatically loaded
     */
    private final JTextField autoLoadJTextField = new JTextField();

    private final SpinnerModel model
            = new SpinnerNumberModel(Settings.getTagCloudWords(),
            0,
            2000,
            1);

    private final JSpinner tagCloudWordsJSpinner = new JSpinner(model);

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
    private final WholeNumberField maximumPictureSizeJTextField = new WholeNumberField(0, 6);
    /**
     * checkbox that indicates whether small images should be enlarged
     */
    private final JCheckBox dontEnlargeJCheckBox = new JCheckBox(Settings.getJpoResources().getString("dontEnlargeJCheckBoxLabel"));
    /**
     * tickbox that indicates whether to scale the thumbnails quickly
     */
    private final JCheckBox pictureViewerFastScaleJCheckBox = new JCheckBox(Settings.getJpoResources().getString("pictureViewerFastScale"));
    /**
     * User picks the thumbnail cache directory here
     */
    private final DirectoryChooser thumbnailCacheDirPathChooser
            = new DirectoryChooser(Settings.getJpoResources().getString("genericSelectText"),
            DirectoryChooser.DIR_MUST_BE_WRITABLE);
    /**
     * field that allows the user to capture the maximum number of thumbnails to
     * be displayed
     */
    private final WholeNumberField maxThumbnails = new WholeNumberField(0, 4);
    /**
     * fields that allows the user to capture the desired size of thumbnails
     */
    private final WholeNumberField thumbnailSize = new WholeNumberField(0, 6);
    /**
     * slider that allows the quality of the jpg's to be specified Should this
     * really be the same as the HTLM Quality Field?
     */
    private final JSlider jpgQualityJSlider = new JSlider(SwingConstants.HORIZONTAL,
            0, 100, (int) (Settings.getDefaultHtmlLowresQuality() * 100));
    /**
     * tickbox that indicates whether to scale the thumbnails quickly
     */
    private final JCheckBox thumbnailFastScaleJCheckBox = new JCheckBox(Settings.getJpoResources().getString("thumbnailFastScale"));
    /**
     * tickbox that indicates whether to show the filenames or not
     */
    private final JCheckBox thumbnailShowFilenamesJCheckBox = new JCheckBox(Settings.getJpoResources().getString("thumbnailShowFilenamesJCheckBox"));
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
    private final JComboBox<String> languageJComboBox = new JComboBox<>(Settings.getSupportedLanguages());
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
    private final JPasswordField emailPasswordJTextField = new JPasswordField();
    private final JTextArea highresStatsJTA = new JTextArea();
    private final JTextArea lowresStatsJTA = new JTextArea();

    /**
     * Constructor to create the GUI that allows modification of the settings
     *
     * @param modal flag to say if modal or not
     */
    public SettingsDialog(final boolean modal) {
        super(Settings.getAnchorFrame(), modal);
        initComponents();
        initValues();
        pack();
        setLocationRelativeTo(Settings.getAnchorFrame());
        setVisible(true);
    }

    /**
     * returns the index for the size dropdowns based on the supplied
     * parameters.
     *
     * @param maximise        whether the index should be maximised
     * @param targetDimension the target size of the window
     * @return the index
     */
    private static int findSizeIndex(boolean maximise,
                                     Dimension targetDimension) {
        if (maximise) {
            return 0;
        } else {
            int settingsArea = targetDimension.width * targetDimension.height;
            int index = 1;
            for (int i = 1; i < Settings.getWindowSizes().length; i++) {
                if (Settings.getWindowSizes()[i].width * Settings.getWindowSizes()[i].height <= settingsArea) {
                    index = i;
                } else {
                    break;
                }
            }
            return index;
        }
    }

    /**
     * Create the GUI elements
     */
    private void initComponents() {
        setTitle(Settings.getJpoResources().getString("settingsDialogTitle"));
        getContentPane().setLayout(new BorderLayout());
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(SwingConstants.TOP);
        tabbedPane.setPreferredSize(SETTINGS_DIALOG_SIZE);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tabbedPane.add(Settings.getJpoResources().getString("browserWindowSettingsJPanel"), getGeneralJPanel());
        tabbedPane.add(Settings.getJpoResources().getString("pictureViewerJPanel"), getPictureViewerJPanel());
        tabbedPane.add(Settings.getJpoResources().getString("thumbnailSettingsJPanel"), getThumbnailsJPanel());
        tabbedPane.add(Settings.getJpoResources().getString("userFunctionJPanel"), getUserFunctionsJPanel());
        tabbedPane.add(Settings.getJpoResources().getString("emailJPanel"), getEmailServerJPanel());
        tabbedPane.add(Settings.getJpoResources().getString("cacheJPanel"), getCacheJPanel());
        getContentPane().add(tabbedPane, BorderLayout.NORTH);

        final Container buttonContainer = new Container();
        buttonContainer.setLayout(new FlowLayout());
        buttonContainer.add(getSaveButton());

        final JButton cancelButton = new JButton(Settings.getJpoResources().getString("genericCancelText"));
        cancelButton.setPreferredSize(Settings.getDefaultButtonDimension());
        cancelButton.setMinimumSize(Settings.getDefaultButtonDimension());
        cancelButton.setMaximumSize(Settings.getDefaultButtonDimension());
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
        cancelButton.addActionListener((ActionEvent e) -> getRid());
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

    @NotNull
    private JPanel getGeneralJPanel() {
        final JPanel generalJPanel = new JPanel(new MigLayout());

        final JLabel languageJLabel = new JLabel(Settings.getJpoResources().getString("languageJLabel"));
        generalJPanel.add(languageJLabel);
        generalJPanel.add(languageJComboBox, "wrap");

        // Initial Windowsize stuff
        generalJPanel.add(new JLabel(Settings.getJpoResources().getString("windowSizeChoicesJlabel")));

        final String[] windowSizeChoices = getWindowSizeChoices();
        final DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<>(windowSizeChoices);
        startupSizeDropdown.setModel(dcbm);
        generalJPanel.add(startupSizeDropdown, "wrap");
        startupSizeDropdown.addActionListener(new ActionListener() {

            boolean firstrun = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (firstrun) {
                    // don't change the window size when setting up the gui
                    firstrun = false;
                } else {
                    if (startupSizeDropdown.getSelectedIndex() == 0) {
                        Settings.getAnchorFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
                    } else {
                        Settings.getAnchorFrame().setExtendedState(Frame.NORMAL);
                        Settings.getAnchorFrame().setSize(Settings.getWindowSizes()[startupSizeDropdown.getSelectedIndex()]);
                    }
                }
            }
        });
        // End of Initial Windowsize stuff

        //Autoload stuff
        final JLabel autoLoadJLabel = new JLabel(Settings.getJpoResources().getString("autoLoadJLabelLabel"));
        generalJPanel.add(autoLoadJLabel);

        autoLoadJTextField.setPreferredSize(Settings.getFilenameFieldPreferredSize());
        autoLoadJTextField.setMinimumSize(Settings.getFilenameFieldMinimumSize());
        autoLoadJTextField.setMaximumSize(Settings.getFilenameFieldMaximumSize());
        autoLoadJTextField.setInputVerifier(new FileTextFieldVerifier());
        generalJPanel.add(autoLoadJTextField);

        generalJPanel.add(getAutoLoadJButton(), "wrap");

        final JLabel wordCloudWordJLabel = new JLabel("Max Word Cloud Words");
        generalJPanel.add(wordCloudWordJLabel);

        generalJPanel.add(tagCloudWordsJSpinner, "wrap");
        return generalJPanel;
    }

    @NotNull
    private JPanel getPictureViewerJPanel() {
        final JPanel pictureViewerJPanel = new JPanel(new MigLayout());
        // PictureViewer size stuff
        pictureViewerJPanel.add(new JLabel(Settings.getJpoResources().getString("pictureViewerSizeChoicesJlabel")));
        final String[] windowSizeChoices = getWindowSizeChoices();
        final DefaultComboBoxModel<String> viewerSizeModel = new DefaultComboBoxModel<>(windowSizeChoices);
        viewerSizeDropdown.setModel(viewerSizeModel);
        pictureViewerJPanel.add(viewerSizeDropdown, "wrap");
        // End of PictureViewer size stuff

        final JLabel minimumPictureSizeLabel = new JLabel(Settings.getJpoResources().getString("maximumPictureSizeLabel"));
        pictureViewerJPanel.add(minimumPictureSizeLabel);
        maximumPictureSizeJTextField.setPreferredSize(Settings.getShortNumberPreferredSize());
        maximumPictureSizeJTextField.setMinimumSize(Settings.getShortNumberMinimumSize());
        maximumPictureSizeJTextField.setMaximumSize(Settings.getShortNumberMaximumSize());
        pictureViewerJPanel.add(maximumPictureSizeJTextField, "wrap");

        final JLabel maxCacheJLabel = new JLabel(Settings.getJpoResources().getString("maxCacheLabel"));
        pictureViewerJPanel.add(maxCacheJLabel);

        pictureViewerJPanel.add(dontEnlargeJCheckBox, "wrap");
        pictureViewerJPanel.add(pictureViewerFastScaleJCheckBox);
        return pictureViewerJPanel;
    }

    @NotNull
    private JPanel getThumbnailsJPanel() {
        final JPanel thumbnailsJPanel = new JPanel(new MigLayout());

        final JLabel maxThumbnailsLabel = new JLabel(Settings.getJpoResources().getString("maxThumbnailsLabelText"));
        thumbnailsJPanel.add(maxThumbnailsLabel);

        maxThumbnails.setPreferredSize(Settings.getShortNumberPreferredSize());
        maxThumbnails.setMinimumSize(Settings.getShortNumberMinimumSize());
        maxThumbnails.setMaximumSize(Settings.getShortNumberMaximumSize());
        thumbnailsJPanel.add(maxThumbnails, "wrap");

        final JLabel thumbnailSizeLabel = new JLabel(Settings.getJpoResources().getString("thumbnailSizeLabel"));
        thumbnailsJPanel.add(thumbnailSizeLabel);

        thumbnailSize.setPreferredSize(Settings.getShortNumberPreferredSize());
        thumbnailSize.setMinimumSize(Settings.getShortNumberMinimumSize());
        thumbnailSize.setMaximumSize(Settings.getShortNumberMaximumSize());
        thumbnailsJPanel.add(thumbnailSize, "wrap");

        final JLabel jpgQualitySlider
                = new JLabel(Settings.getJpoResources().getString("lowresJpgQualitySlider"));
        thumbnailsJPanel.add(jpgQualitySlider, "wrap");

        //Create the label table
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, new JLabel(Settings.getJpoResources().getString("jpgQualityBad")));
        labelTable.put(80, new JLabel(Settings.getJpoResources().getString("jpgQualityGood")));
        labelTable.put(100, new JLabel(Settings.getJpoResources().getString("jpgQualityBest")));
        jpgQualityJSlider.setLabelTable(labelTable);

        jpgQualityJSlider.setMajorTickSpacing(10);
        jpgQualityJSlider.setMinorTickSpacing(5);
        jpgQualityJSlider.setPaintTicks(true);
        jpgQualityJSlider.setPaintLabels(true);
        jpgQualityJSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 20));
        thumbnailsJPanel.add(jpgQualityJSlider, "span, grow, wrap");

        thumbnailsJPanel.add(thumbnailFastScaleJCheckBox, "wrap");
        thumbnailsJPanel.add(thumbnailShowFilenamesJCheckBox);
        return thumbnailsJPanel;
    }

    @NotNull
    private JPanel getUserFunctionsJPanel() {
        // User Functions
        final JPanel userFunctionsJPanel = new JPanel(new MigLayout());
        final JLabel userFunction1JLabel = new JLabel(Settings.getJpoResources().getString("userFunction1JLabel"));
        userFunctionsJPanel.add(userFunction1JLabel, "span, wrap");

        userFunctionsJPanel.add(new JLabel(Settings.getJpoResources().getString("userFunctionNameJLabel")));
        userFunction1NameJTextField.setPreferredSize(Settings.getTextfieldPreferredSize());
        userFunction1NameJTextField.setMinimumSize(Settings.getTextfieldMinimumSize());
        userFunction1NameJTextField.setMaximumSize(Settings.getTextfieldMaximumSize());
        userFunctionsJPanel.add(userFunction1NameJTextField, "wrap");

        userFunctionsJPanel.add(new JLabel(Settings.getJpoResources().getString("userFunctionCmdJLabel")));
        userFunction1CmdJTextField.setPreferredSize(Settings.getTextfieldPreferredSize());
        userFunction1CmdJTextField.setMinimumSize(Settings.getTextfieldMinimumSize());
        userFunction1CmdJTextField.setMaximumSize(Settings.getTextfieldMaximumSize());
        userFunctionsJPanel.add(userFunction1CmdJTextField, "wrap");

        final JLabel userFunction2JLabel = new JLabel(Settings.getJpoResources().getString("userFunction2JLabel"));
        userFunctionsJPanel.add(userFunction2JLabel, "span, wrap");

        userFunctionsJPanel.add(new JLabel(Settings.getJpoResources().getString("userFunctionNameJLabel")));
        userFunction2NameJTextField.setPreferredSize(Settings.getTextfieldPreferredSize());
        userFunction2NameJTextField.setMinimumSize(Settings.getTextfieldMinimumSize());
        userFunction2NameJTextField.setMaximumSize(Settings.getTextfieldMaximumSize());
        userFunctionsJPanel.add(userFunction2NameJTextField, "wrap");

        userFunctionsJPanel.add(new JLabel(Settings.getJpoResources().getString("userFunctionCmdJLabel")));

        userFunction2CmdJTextField.setPreferredSize(Settings.getTextfieldPreferredSize());
        userFunction2CmdJTextField.setMinimumSize(Settings.getTextfieldMinimumSize());
        userFunction2CmdJTextField.setMaximumSize(Settings.getTextfieldMaximumSize());
        userFunctionsJPanel.add(userFunction2CmdJTextField, "wrap");

        final JLabel userFunction3JLabel = new JLabel(Settings.getJpoResources().getString("userFunction3JLabel"));
        userFunctionsJPanel.add(userFunction3JLabel, "span, wrap");

        userFunctionsJPanel.add(new JLabel(Settings.getJpoResources().getString("userFunctionNameJLabel")));
        userFunction3NameJTextField.setPreferredSize(Settings.getTextfieldPreferredSize());
        userFunction3NameJTextField.setMinimumSize(Settings.getTextfieldMinimumSize());
        userFunction3NameJTextField.setMaximumSize(Settings.getTextfieldMaximumSize());
        userFunctionsJPanel.add(userFunction3NameJTextField, "wrap");

        userFunctionsJPanel.add(new JLabel(Settings.getJpoResources().getString("userFunctionCmdJLabel")));
        userFunction3CmdJTextField.setPreferredSize(Settings.getTextfieldPreferredSize());
        userFunction3CmdJTextField.setMinimumSize(Settings.getTextfieldMinimumSize());
        userFunction3CmdJTextField.setMaximumSize(Settings.getTextfieldMaximumSize());
        userFunctionsJPanel.add(userFunction3CmdJTextField, "wrap");

        final JTextArea userFunctionHelpJTextArea = new JTextArea(Settings.getJpoResources().getString("userFunctionHelpJTextArea"));
        userFunctionHelpJTextArea.setEditable(false);
        userFunctionHelpJTextArea.setWrapStyleWord(true);
        userFunctionHelpJTextArea.setLineWrap(true);
        userFunctionsJPanel.add(userFunctionHelpJTextArea, "span, grow");
        return userFunctionsJPanel;
    }

    @NotNull
    private JPanel getEmailServerJPanel() {
        // Email Server
        final JPanel emailServerJPanel = new JPanel(new MigLayout());
        final JLabel emailJLabel = new JLabel(Settings.getJpoResources().getString("emailJLabel"));
        emailServerJPanel.add(emailJLabel, "span, wrap");

        emailServerJPanel.add(new JLabel(Settings.getJpoResources().getString("predefinedEmailJLabel")));


        emailServerJPanel.add(getPredefinedEmailJComboBox(), "wrap");

        emailServerJPanel.add(new JLabel(Settings.getJpoResources().getString("emailServerJLabel")));
        emailServerJTextField.setPreferredSize(Settings.getTextfieldPreferredSize());
        emailServerJTextField.setMinimumSize(Settings.getTextfieldMinimumSize());
        emailServerJTextField.setMaximumSize(Settings.getTextfieldMaximumSize());
        emailServerJPanel.add(emailServerJTextField, "wrap");

        emailServerJPanel.add(new JLabel(Settings.getJpoResources().getString("emailPortJLabel")));
        emailPortJTextField.setPreferredSize(Settings.getShortNumberPreferredSize());
        emailPortJTextField.setMinimumSize(Settings.getShortNumberMinimumSize());
        emailPortJTextField.setMaximumSize(Settings.getShortNumberMaximumSize());
        emailServerJPanel.add(emailPortJTextField, "wrap");

        final JLabel userNameJLabel = new JLabel(Settings.getJpoResources().getString("emailUserJLabel"));
        final JLabel passwordJLabel = new JLabel(Settings.getJpoResources().getString("emailPasswordJLabel"));
        final JLabel showPasswordLabel = new JLabel();
        final JButton showPasswordButton = new JButton(Settings.getJpoResources().getString("emailShowPasswordButton"));

        emailServerJPanel.add(new JLabel(Settings.getJpoResources().getString("emailAuthentication")));
        authenticationJComboBox.removeAllItems();
        authenticationJComboBox.addItem("None");
        authenticationJComboBox.addItem("Password");
        authenticationJComboBox.addItem("SSL");
        authenticationJComboBox.addActionListener((ActionEvent e) -> {
            final String cbSelection = (String) authenticationJComboBox.getSelectedItem();
            switch (Objects.requireNonNull(cbSelection)) {
                case "Password" -> {
                    userNameJLabel.setVisible(true);
                    emailUserJTextField.setVisible(true);
                    passwordJLabel.setVisible(true);
                    emailPasswordJTextField.setVisible(true);
                    showPasswordButton.setVisible(true);
                    showPasswordLabel.setVisible(true);
                }
                case "SSL" -> {
                    userNameJLabel.setVisible(true);
                    emailUserJTextField.setVisible(true);
                    passwordJLabel.setVisible(true);
                    emailPasswordJTextField.setVisible(true);
                    showPasswordButton.setVisible(true);
                    showPasswordLabel.setVisible(true);
                }
                default -> {
                    emailUserJTextField.setText("");
                    userNameJLabel.setVisible(false);
                    emailUserJTextField.setVisible(false);
                    emailPasswordJTextField.setText("");
                    passwordJLabel.setVisible(false);
                    emailPasswordJTextField.setVisible(false);
                    showPasswordButton.setVisible(false);
                    showPasswordLabel.setVisible(false);
                }
            }
        });
        emailServerJPanel.add(authenticationJComboBox, "wrap");

        emailServerJPanel.add(userNameJLabel);
        emailUserJTextField.setPreferredSize(Settings.getTextfieldPreferredSize());
        emailUserJTextField.setMinimumSize(Settings.getTextfieldMinimumSize());
        emailUserJTextField.setMaximumSize(Settings.getTextfieldMaximumSize());
        emailServerJPanel.add(emailUserJTextField, "wrap");

        emailServerJPanel.add(passwordJLabel);
        emailPasswordJTextField.setPreferredSize(Settings.getTextfieldPreferredSize());
        emailPasswordJTextField.setMinimumSize(Settings.getTextfieldMinimumSize());
        emailPasswordJTextField.setMaximumSize(Settings.getTextfieldMaximumSize());
        emailServerJPanel.add(emailPasswordJTextField, "wrap");

        showPasswordButton.addActionListener((ActionEvent ae) -> showPasswordLabel.setText(new String(emailPasswordJTextField.getPassword())));

        emailServerJPanel.add(showPasswordButton);
        emailServerJPanel.add(showPasswordLabel, "wrap");
        return emailServerJPanel;
    }

    @NotNull
    private JPanel getCacheJPanel() {
        // Cache Panel
        final JPanel cacheJPanel = new JPanel(new MigLayout());

        cacheJPanel.add(new JLabel(Settings.getJpoResources().getString("thumbnailDirLabel")));
        cacheJPanel.add(thumbnailCacheDirPathChooser);
        cacheJPanel.add(new JLabel("(Needs restart)"), "wrap");

        cacheJPanel.add(new JLabel("Highres Stats:"));
        cacheJPanel.add(new JLabel("Lowres Stats:"), "wrap");

        cacheJPanel.add(new JScrollPane(highresStatsJTA));
        cacheJPanel.add(new JScrollPane(lowresStatsJTA), "wrap");

        final JButton clearHighresCacheJButton = new JButton("Clear");
        clearHighresCacheJButton.addActionListener((ActionEvent e) -> clearHighresCache());

        final JButton clearThumbnailCacheJButton = new JButton("Clear");
        clearThumbnailCacheJButton.addActionListener((ActionEvent e) -> clearThumbnailCache());

        cacheJPanel.add(clearHighresCacheJButton);
        cacheJPanel.add(clearThumbnailCacheJButton);

        final JButton updateCacheStatsJButton = new JButton("Update");
        updateCacheStatsJButton.addActionListener((ActionEvent e) -> updateCacheStats());
        cacheJPanel.add(updateCacheStatsJButton);
        return cacheJPanel;
    }

    private String[] getWindowSizeChoices() {
        final String[] windowSizeChoices = new String[Settings.getWindowSizes().length];
        windowSizeChoices[0] = Settings.getJpoResources().getString("windowSizeChoicesMaximum");
        for (int i = 1; i < Settings.getWindowSizes().length; i++) {
            windowSizeChoices[i] = Settings.getWindowSizes()[i].width + " x " + Settings.getWindowSizes()[i].height;
        }
        return windowSizeChoices;
    }

    private JButton getAutoLoadJButton() {
        final JButton autoLoadJButton = new JButton(Settings.getJpoResources().getString("threeDotText"));
        autoLoadJButton.setPreferredSize(Settings.getThreeDotButtonSize());
        autoLoadJButton.setMinimumSize(Settings.getThreeDotButtonSize());
        autoLoadJButton.setMaximumSize(Settings.getThreeDotButtonSize());
        autoLoadJButton.addActionListener((ActionEvent e) -> autoLoadChooser());
        return autoLoadJButton;
    }

    private JComboBox<String> getPredefinedEmailJComboBox() {
        final JComboBox<String> predefinedEmailJComboBox = new JComboBox<>();
        predefinedEmailJComboBox.addItem("Localhost");
        predefinedEmailJComboBox.addItem("Gmail");
        predefinedEmailJComboBox.addItem("Compuserve");
        predefinedEmailJComboBox.addItem("Hotmail");
        predefinedEmailJComboBox.addItem("Other");
        predefinedEmailJComboBox.addActionListener((ActionEvent e) -> {
            final String cbSelection = (String) predefinedEmailJComboBox.getSelectedItem();
            if ("Localhost".equals(cbSelection)) {
                emailServerJTextField.setText("localhost");
                emailPortJTextField.setText("25");
                authenticationJComboBox.setSelectedIndex(1); //Password
            } else if ("Compuserve".equals(cbSelection)) {
                emailServerJTextField.setText("smtp.compuserve.com");
                emailPortJTextField.setText("25");
                authenticationJComboBox.setSelectedIndex(1); //Password
            } else if ("Gmail".equals(cbSelection)) {
                emailServerJTextField.setText("smtp.gmail.com");
                emailPortJTextField.setText("465");
                authenticationJComboBox.setSelectedIndex(2); //SSL
            } else if ("Hotmail".equals(cbSelection)) {
                emailServerJTextField.setText("smtp.live.com");
                emailPortJTextField.setText("25");
                authenticationJComboBox.setSelectedIndex(1); //Password
            } else if ("Other".equals(cbSelection)) {
                emailServerJTextField.setText("");
                emailPortJTextField.setText("25");
            }
        });
        return predefinedEmailJComboBox;
    }

    /**
     * This method sets up the GUI fields according to what the Settings
     * object's values are
     */
    private void initValues() {
        for (int i = 0; i < Settings.getSupportedLanguages().length; i++) {
            if (Settings.getCurrentLocale().equals(Settings.getSupportedLocale()[i])) {
                languageJComboBox.setSelectedIndex(i);
                break;
            }
        }

        autoLoadJTextField.setText(Settings.getAutoLoad());

        startupSizeDropdown.setSelectedIndex(findSizeIndex(Settings.isMaximiseJpoOnStartup(), Settings.getMainFrameDimensions()));
        viewerSizeDropdown.setSelectedIndex(findSizeIndex(Settings.isMaximisePictureViewerWindow(), Settings.getPictureViewerDefaultDimensions()));

        maximumPictureSizeJTextField.setValue(Settings.getMaximumPictureSize());
        dontEnlargeJCheckBox.setSelected(Settings.isDontEnlargeSmallImages());

        thumbnailCacheDirPathChooser.setText(Settings.getThumbnailCacheDirectory());
        maxThumbnails.setValue(Settings.getMaxThumbnails());
        thumbnailSize.setValue(Settings.getThumbnailSize());
        jpgQualityJSlider.setValue((int) (Settings.getDefaultHtmlLowresQuality() * 100));
        thumbnailFastScaleJCheckBox.setSelected(Settings.isThumbnailFastScale());
        thumbnailShowFilenamesJCheckBox.setSelected(Settings.isShowFilenamesOnThumbnailPanel());

        userFunction1NameJTextField.setText(Settings.getUserFunctionNames()[0]);
        userFunction2NameJTextField.setText(Settings.getUserFunctionNames()[1]);
        userFunction3NameJTextField.setText(Settings.getUserFunctionNames()[2]);

        userFunction1CmdJTextField.setText(Settings.getUserFunctionCmd()[0]);
        userFunction2CmdJTextField.setText(Settings.getUserFunctionCmd()[1]);
        userFunction3CmdJTextField.setText(Settings.getUserFunctionCmd()[2]);

        emailServerJTextField.setText(Settings.getEmailServer());
        emailPortJTextField.setText(Settings.getEmailPort());
        authenticationJComboBox.setSelectedIndex(Settings.getEmailAuthentication());
        emailUserJTextField.setText(Settings.getEmailUser());
        emailPasswordJTextField.setText(Settings.getEmailPassword());

        // deliberately placed here to stop change events being triggered while the fields are
        // being initialised.
        checkAutoLoad(autoLoadJTextField.getText());
        updateCacheStats();

    }

    private JButton getSaveButton() {
        final JButton saveButton = new JButton(Settings.getJpoResources().getString("genericSaveButtonLabel"));
        saveButton.setPreferredSize(Settings.getDefaultButtonDimension());
        saveButton.setMinimumSize(Settings.getDefaultButtonDimension());
        saveButton.setMaximumSize(Settings.getDefaultButtonDimension());
        saveButton.setBorder(BorderFactory.createRaisedBevelBorder());
        saveButton.addActionListener((
                ActionEvent e) ->

        {
            writeValues();
            Settings.writeSettings();
            getRid();
        });
        return saveButton;
    }

    /**
     * This method writes the values in the GUI widgets into the Settings
     * object.
     */
    private void writeValues() {
        Locale comboBoxLocale = Settings.getSupportedLocale()[languageJComboBox.getSelectedIndex()];
        boolean localeChanged = Settings.setLocale(comboBoxLocale);
        if (localeChanged) {
            LOGGER.info("Locale changed!");
            JpoEventBus.getInstance().post(new LocaleChangedEvent());
        }

        Settings.setAutoLoad(autoLoadJTextField.getText());

        Settings.setTagCloudWords((Integer) tagCloudWordsJSpinner.getValue());

        if (startupSizeDropdown.getSelectedIndex() == 0) {
            Settings.setMaximiseJpoOnStartup(true);
            Settings.setMainFrameDimensions(new Dimension(0, 0));
        } else {
            Settings.setMaximiseJpoOnStartup(false);
            Settings.setMainFrameDimensions(new Dimension(Settings.getWindowSizes()[startupSizeDropdown.getSelectedIndex()]));
        }

        Settings.setMaximumPictureSize(maximumPictureSizeJTextField.getValue());
        Settings.setDontEnlargeSmallImages(dontEnlargeJCheckBox.isSelected());

        if (viewerSizeDropdown.getSelectedIndex() == 0) {
            Settings.setMaximisePictureViewerWindow(true);
            Settings.setPictureViewerDefaultDimensions(new Dimension(0, 0));
        } else {
            Settings.setMaximisePictureViewerWindow(false);
            Settings.setPictureViewerDefaultDimensions(new Dimension(Settings.getWindowSizes()[viewerSizeDropdown.getSelectedIndex()]));
        }

        Settings.setPictureViewerFastScale(pictureViewerFastScaleJCheckBox.isSelected());

        Settings.setMaxThumbnails(maxThumbnails.getValue());
        Settings.setThumbnailSize(thumbnailSize.getValue());
        Settings.setDefaultHtmlLowresQuality(((float) jpgQualityJSlider.getValue()) / 100);
        Settings.setThumbnailFastScale(thumbnailFastScaleJCheckBox.isSelected());
        Settings.setShowFilenamesOnThumbnailPanel(thumbnailShowFilenamesJCheckBox.isSelected());

        Settings.getUserFunctionNames()[0] = userFunction1NameJTextField.getText();
        Settings.getUserFunctionNames()[1] = userFunction2NameJTextField.getText();
        Settings.getUserFunctionNames()[2] = userFunction3NameJTextField.getText();

        Settings.getUserFunctionCmd()[0] = userFunction1CmdJTextField.getText();
        Settings.getUserFunctionCmd()[1] = userFunction2CmdJTextField.getText();
        Settings.getUserFunctionCmd()[2] = userFunction3CmdJTextField.getText();

        Settings.setEmailServer(emailServerJTextField.getText());
        Settings.setEmailPort(emailPortJTextField.getText());
        Settings.setEmailAuthentication(authenticationJComboBox.getSelectedIndex());
        Settings.setEmailUser(emailUserJTextField.getText());
        Settings.setEmailPassword(new String(emailPasswordJTextField.getPassword()));

        Settings.validateSettings();
        JpoEventBus.getInstance().post(new UserFunctionsChangedEvent());
    }

    /**
     * this method verifies that the file specified in the AutoLoadJTextField is
     * valid. It sets the color of the font to red if this is not OK.
     *
     * @param validationFile the file to validate
     */
    public void checkAutoLoad(String validationFile) {
        LOGGER.log(Level.FINE, "SettingsDialog.checkAutoLoad: called on: {0}", validationFile);
        File testFile = new File(validationFile);

        if ("".equals(validationFile)) {
            autoLoadJTextField.setForeground(Color.black);
            return;
        }

        if (!testFile.exists()) {
            LOGGER.log(Level.WARNING, "SettingsDialog.checkAutoLoad: {0} doesn''t exist.", testFile);
            autoLoadJTextField.setForeground(Color.red);
            return;
        } else {
            if (!testFile.canRead()) {
                LOGGER.log(Level.WARNING, "SettingsDialog.checkAutoLoad: {0} can''t read.", testFile);
                autoLoadJTextField.setForeground(Color.red);
                return;
            }
        }
        autoLoadJTextField.setForeground(Color.black);
    }

    /**
     * method that gets rid of the SettingsDialog
     */
    private void getRid() {
        setVisible(false);
        dispose();
    }

    /**
     * method that brings up a JFileChooser and places the path of the file
     * selected into the JTextField of the autoFileJTextField.
     */
    private void autoLoadChooser() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(new XmlFilter());

        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setDialogTitle(Settings.getJpoResources().getString("autoLoadChooserTitle"));
        jFileChooser.setCurrentDirectory(new File(autoLoadJTextField.getText()));

        int returnVal = jFileChooser.showDialog(this, Settings.getJpoResources().getString("genericSelectText"));
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            autoLoadJTextField.setText(jFileChooser.getSelectedFile().getPath());
            checkAutoLoad(autoLoadJTextField.getText());
        }
    }

    /**
     * Updates the text areas with the JCS cache statistics
     */
    private void updateCacheStats() {
        highresStatsJTA.setText(JpoCache.getHighresCacheStats());
        lowresStatsJTA.setText(JpoCache.getThumbnailCacheStats());
    }

    /**
     * Clears the JCS highres picture cache
     */
    private void clearHighresCache() {
        JpoCache.clearHighresCache();
        updateCacheStats();
    }

    /**
     * Clears the JCS thumbnail cache.
     */
    private void clearThumbnailCache() {
        JpoCache.clearThumbnailCache();
        updateCacheStats();
    }

    /**
     * special inner class that verifies whether the path indicated by the
     * component is valid
     */
    class FileTextFieldVerifier
            extends InputVerifier {

        @Override
        public boolean shouldYieldFocus(final JComponent source, final JComponent target) {
            final String validationFile = ((JTextComponent) source).getText();
            LOGGER.log(Level.INFO, "SettingsDialog.FileTextFieldVerifyer.shouldYieldFocus: called with: {0}", validationFile);
            LOGGER.log(Level.INFO, "JComponent = {0}", Integer.toString(source.hashCode()));
            LOGGER.log(Level.INFO, "autoLoadJTextField = {0}", Integer.toString(autoLoadJTextField.hashCode()));

            if (source.hashCode() == autoLoadJTextField.hashCode()) {
                checkAutoLoad(validationFile);
            }

            return true;
        }

        @Override
        public boolean verify(final JComponent input) {
            LOGGER.log(Level.INFO, "SettingsDialog.FileTextFieldVerifyer.verify: called with: {0}", ((JTextComponent) input).getText());
            return true;
        }
    }

}
