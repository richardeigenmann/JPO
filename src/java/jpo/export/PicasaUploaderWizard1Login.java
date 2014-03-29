package jpo.export;

import com.google.gdata.util.AuthenticationException;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import jpo.dataModel.Settings;
import net.javaprog.ui.wizard.AbstractStep;
import net.miginfocom.swing.MigLayout;

/*
 * PicasaUploaderWizard1Login.java: Login stuff for Picasa
 *
 * Copyright (C) 2012 Richard Eigenmann. Zürich
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
     * Asks all the questions we need to know in regards to the thumbnails on
     * the final website.
     *
     * @param myRequest
     */
    public PicasaUploaderWizard1Login( PicasaUploadRequest myRequest ) {
        super( "Login to Picasa", "Login to Picasa" );
        this.myRequest = myRequest;

        userNameJTextField.setText( myRequest.getUsername() );
        passwordJPasswordField.setText( myRequest.getPassword() );
    }
    private final JTextField userNameJTextField = new JTextField();
    private final JPasswordField passwordJPasswordField = new JPasswordField();
    private final JCheckBox rememberCredentialsJCheckkBox = new JCheckBox( "Remember credentials" );
    private final JButton loginJButton = new JButton();
    private final JTextArea errorJTextArea = new JTextArea();

    /**
     * Creates the GUI widgets
     *
     * @return The component to be shown
     */
    @Override
    protected JComponent createComponent() {
        JPanel wizardPanel = new JPanel();
        MigLayout layout = new MigLayout( "wrap 2" );
        wizardPanel.setLayout( layout );

        final Dimension minimumSize = new Dimension( 250, 28 );
        final Dimension maximumSize = new Dimension( 550, 35 );
        userNameJTextField.setMinimumSize( minimumSize );
        userNameJTextField.setMaximumSize( maximumSize );
        wizardPanel.add( new JLabel( "Username:" ) );
        wizardPanel.add( userNameJTextField );

        passwordJPasswordField.setMinimumSize( minimumSize );
        passwordJPasswordField.setMaximumSize( maximumSize );
        wizardPanel.add( new JLabel( "Password:" ) );
        wizardPanel.add( passwordJPasswordField );
        wizardPanel.add( rememberCredentialsJCheckkBox, "wrap" );
        loginJButton.setText( "Log in" );
        wizardPanel.add( loginJButton, "span 2" );
        loginJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent ae ) {
                try {
                    errorJTextArea.setText( "Logging in..." );
                    if ( rememberCredentialsJCheckkBox.isSelected() ) {
                        LOGGER.info( "saving" );
                        Settings.rememberGoogleCredentials = true;
                        Settings.googleUsername = userNameJTextField.getText();
                        Settings.googlePassword = new String( passwordJPasswordField.getPassword() );
                        Settings.unsavedSettingChanges = true;
                    } else {
                        LOGGER.info( "wiping" );
                        Settings.rememberGoogleCredentials = false;
                        Settings.googleUsername = "";
                        Settings.googlePassword = "";
                    }
                    myRequest.setUsername( userNameJTextField.getText() );
                    myRequest.setPassword( new String( passwordJPasswordField.getPassword() ) );
                    myRequest.picasaWebService.setUserCredentials( myRequest.getUsername(), myRequest.getPassword() );
                    setCanGoNext( true );
                    errorJTextArea.setText( "Successfully logged into Picasa." );
                } catch ( AuthenticationException ex ) {
                    LOGGER.severe( ex.getMessage() );
                    errorJTextArea.setText( ex.getMessage() );
                    setCanGoNext( false );
                }
            }
        } );

        errorJTextArea.setMinimumSize( new Dimension( 450, 150 ) );
        errorJTextArea.setMaximumSize( new Dimension( 550, 300 ) );
        wizardPanel.add( errorJTextArea, "span2" );
        setCanGoNext( false );

        rememberCredentialsJCheckkBox.setSelected( Settings.rememberGoogleCredentials );
        if ( Settings.rememberGoogleCredentials ) {
            LOGGER.info( "remembering" );
            userNameJTextField.setText( Settings.googleUsername );
            passwordJPasswordField.setText( Settings.googlePassword );
        } else {
            LOGGER.info( "not remembering" );
        }

        return wizardPanel;
    }

    @Override
    public void prepareRendering() {
    }
}
