package org.jpo.export;

import net.javaprog.ui.wizard.AbstractStep;
import net.miginfocom.swing.MigLayout;
import org.jpo.gui.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

/*
 * Copyright (C) 2012-2024 Richard Eigenmann. Zürich
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA. The license is in gpl.txt. See
 * http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 * Performs the login to Google
 *
 * @author Richard Eigenmann
 */
public class PicasaUploaderWizard1Login extends AbstractStep {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( PicasaUploaderWizard1Login.class.getName() );
    /**
     * The link to the values that this panel should change
     */
    private final PicasaUploadRequest myRequest;

    /**
     * Asks all the questions we need to know in regard to the thumbnails on
     * the final website.
     *
     * @param myRequest my request
     */
    public PicasaUploaderWizard1Login(final PicasaUploadRequest myRequest) {
        super("Login to Picasa", "Login to Picasa");
        this.myRequest = myRequest;

        userNameJTextField.setText(myRequest.getUsername());
        passwordJPasswordField.setText(myRequest.getPassword());
    }
    private final JTextField userNameJTextField = new JTextField();
    private final JPasswordField passwordJPasswordField = new JPasswordField();
    private final JCheckBox rememberCredentialsJCheckBox = new JCheckBox( "Remember credentials" );
    private final JButton loginJButton = new JButton();
    private final JTextArea errorJTextArea = new JTextArea();

    /**
     * Creates the GUI widgets
     *
     * @return The component to be shown
     */
    @Override
    protected JComponent createComponent() {
        final var wizardPanel = new JPanel();
        final var layout = new MigLayout( "wrap 2" );
        wizardPanel.setLayout( layout );

        final var minimumSize = new Dimension( 250, 28 );
        final var maximumSize = new Dimension( 550, 35 );
        userNameJTextField.setMinimumSize( minimumSize );
        userNameJTextField.setMaximumSize( maximumSize );
        wizardPanel.add( new JLabel( "Username:" ) );
        wizardPanel.add( userNameJTextField );

        passwordJPasswordField.setMinimumSize( minimumSize );
        passwordJPasswordField.setMaximumSize( maximumSize );
        wizardPanel.add( new JLabel( "Password:" ) );
        wizardPanel.add( passwordJPasswordField );
        wizardPanel.add(rememberCredentialsJCheckBox, "wrap" );
        loginJButton.setText( "Log in" );
        wizardPanel.add( loginJButton, "span 2" );
        loginJButton.addActionListener(( ActionEvent ae ) -> {
            try {
                errorJTextArea.setText( "Logging in..." );
                if ( rememberCredentialsJCheckBox.isSelected() ) {
                    LOGGER.info("saving");
                    Settings.setRememberGoogleCredentials(true);
                    Settings.setGoogleUsername(userNameJTextField.getText());
                    Settings.setGooglePassword(new String(passwordJPasswordField.getPassword()));
                    Settings.setUnsavedSettingChanges(true);
                } else {
                    LOGGER.info("wiping");
                    Settings.setRememberGoogleCredentials(false);
                    Settings.setGoogleUsername("");
                    Settings.setGooglePassword("");
                }
                myRequest.setUsername(userNameJTextField.getText());
                myRequest.setPassword(new String(passwordJPasswordField.getPassword()));
                //myRequest.picasaWebService.setUserCredentials(myRequest.getUsername(), myRequest.getPassword());
                setCanGoNext(true);
                errorJTextArea.setText("Successfully logged into Picasa.");
            } //catch (final AuthenticationException ex) {
            catch (Exception ex) {
                LOGGER.severe(ex.getMessage());
                errorJTextArea.setText(ex.getMessage());
                setCanGoNext(false);
            }
        });

        errorJTextArea.setMinimumSize(new Dimension(450, 150));
        errorJTextArea.setMaximumSize(new Dimension(550, 300));
        wizardPanel.add(errorJTextArea, "span2");
        setCanGoNext(false);

        rememberCredentialsJCheckBox.setSelected(Settings.isRememberGoogleCredentials());
        if (Settings.isRememberGoogleCredentials()) {
            LOGGER.info("remembering");
            userNameJTextField.setText(Settings.getGoogleUsername());
            passwordJPasswordField.setText(Settings.getGooglePassword());
        } else {
            LOGGER.info("not remembering");
        }

        return wizardPanel;
    }

    /**
     * Required but not needed
     */
    @Override
    public void prepareRendering() {
        // noop
    }
}
