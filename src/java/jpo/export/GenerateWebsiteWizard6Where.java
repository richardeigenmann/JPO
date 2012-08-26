package jpo.export;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.*;
import jpo.dataModel.Settings;
import jpo.gui.DirectoryChooser;
import net.javaprog.ui.wizard.AbstractStep;

/*
GenerateWebsiteWizard6Where: Ask where to generate the website

Copyright (C) 2008-2012  Richard Eigenmann.
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
 * Asks where we should create the website
 *
 * @author Richard Eigenmann
 */
public class GenerateWebsiteWizard6Where extends AbstractStep {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( GenerateWebsiteWizard6Where.class.getName() );

    /**
     * The link to the values that this panel should change
     */
    private final HtmlDistillerOptions options;


    /**
     * This Wizard prompts for the otpions regarding Highres
     * @param options The data object with all the settings
     */
    public GenerateWebsiteWizard6Where( HtmlDistillerOptions options ) {
        super( Settings.jpoResources.getString( "HtmlDistTarget" ), Settings.jpoResources.getString( "HtmlDistTarget" ) );
        this.options = options;
    }

    /**
     *  Text field that holds the directory that the html is to be exported to.
     **/
    private DirectoryChooser targetDirJTextField =
            new DirectoryChooser( Settings.jpoResources.getString( "HtmlDistillerChooserTitle" ),
            DirectoryChooser.DIR_MUST_BE_WRITABLE );


    /**
     * Creates the GUI widgets
     * @return The component to be shown
     */
    @Override
    protected JComponent createComponent() {
        JPanel wizardPanel = new JPanel();
        wizardPanel.setLayout( new BoxLayout( wizardPanel, BoxLayout.PAGE_AXIS ) );
        wizardPanel.setAlignmentX( Component.LEFT_ALIGNMENT );

        /* ToDo: Support FTP and SCP
        String[] finalTargetOptions = { "Local Directory", "FTP Location", "SCP Location" };
        final JComboBox finalTarget = new JComboBox( finalTargetOptions );
        finalTarget.setSelectedIndex( 0 );
        finalTarget.addActionListener( new ActionListener() {

        public void actionPerformed( ActionEvent arg0 ) {
        logger.info( "Other delivery types are not yet supported" );
        finalTarget.setSelectedIndex( 0 );
        }
        } );
        finalTarget.setAlignmentX( Component.LEFT_ALIGNMENT );
        finalTarget.setMaximumSize( GenerateWebsiteWizard.normalComponentSize );
        wizardPanel.add( finalTarget );
        wizardPanel.add( Box.createRigidArea( new Dimension( 0, 8 ) ) ); */

        wizardPanel.add( new JLabel( Settings.jpoResources.getString( "genericTargetDirText" ) ) );
        wizardPanel.add( Box.createRigidArea( new Dimension( 0, 8 ) ) );

        targetDirJTextField.setAlignmentX( Component.LEFT_ALIGNMENT );
        targetDirJTextField.setMaximumSize( GenerateWebsiteWizard.normalComponentSize );
        wizardPanel.add( targetDirJTextField );

        JButton checkButton = new JButton( Settings.jpoResources.getString( "check" ) );
        checkButton.setAlignmentX( Component.LEFT_ALIGNMENT );
        checkButton.setMaximumSize( Settings.defaultButtonDimension );
        checkButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent arg0 ) {
                options.setTargetDirectory( targetDirJTextField.getDirectory() );
                setCanGoNext( check( options.getTargetDirectory() ) );
            }
        } );
        wizardPanel.add( Box.createRigidArea( new Dimension( 0, 8 ) ) );
        wizardPanel.add( checkButton );


        return wizardPanel;
    }


    /**
     * Enforces that the user must check the directory before he can go next
     */
    @Override
    public void prepareRendering() {
        setCanGoNext( false );
    }


    /**
     * Checks whether the supplied file is good for webpage generation and spams popups if not
     * @param targetDirectory
     * @return true if ok, false if not
     */
    public static boolean check( File targetDirectory ) {
        if ( !targetDirectory.exists() ) {
            try {
                targetDirectory.mkdirs();
            } catch ( SecurityException e ) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        Settings.anchorFrame,
                        Settings.jpoResources.getString( "htmlDistCrtDirError" ) + "\n" + e.getMessage(),
                        Settings.jpoResources.getString( "genericSecurityException" ),
                        JOptionPane.ERROR_MESSAGE );
                return false;
            }
        } else {
            if ( !targetDirectory.isDirectory() ) {
                JOptionPane.showMessageDialog(
                        Settings.anchorFrame,
                        Settings.jpoResources.getString( "htmlDistIsDirError" ),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
                return false;
            }
            if ( !targetDirectory.canWrite() ) {
                JOptionPane.showMessageDialog(
                        Settings.anchorFrame,
                        Settings.jpoResources.getString( "htmlDistCanWriteError" ),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
                return false;
            }
            if ( targetDirectory.listFiles().length > 0 ) {
                int option = JOptionPane.showConfirmDialog(
                        Settings.anchorFrame,
                        Settings.jpoResources.getString( "htmlDistIsNotEmptyWarning" ),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE );
                if ( option == JOptionPane.CANCEL_OPTION ) {
                    return false;
                }
            }
        }

        return true;
    }
}
