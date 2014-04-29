package jpo.gui;

import jpo.dataModel.Settings;
import jpo.dataModel.Camera;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
/*
 CameraEditor.java: a class that creates a JPanel and allows camera attributes to be edited

 Copyright (C) 2002 - 2014  Richard Eigenmann.
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
 * This class creates a JPanel and allows attributes of a single camera to be
 * edited on it.
 *
 * @author Richard Eigenmann richard.eigenmann@gmail.com
 */
public class CameraEditor
        extends JPanel {

    /**
     * Constructor for the editor gui
     */
    public CameraEditor() {
        setLayout( new MigLayout( "nogrid" ) );
        JLabel cameraNameJLabel = new JLabel( Settings.jpoResources.getString( "cameraNameJLabel" ) );
        add( cameraNameJLabel, "wrap" );
        add( cameraNameJTextField, "grow, wrap unrel" );

        add( cameraDirJLabel, "wrap" );
        add( cameraDirJTextField, "wrap unrel" );

        add( monitorJCheckBox, "wrap unrel" );

        add( memorisedPicsText );

        add( memorisedPicturesJLabel );

        refreshJButton.setPreferredSize( Settings.defaultButtonDimension );
        refreshJButton.setMinimumSize( Settings.defaultButtonDimension );
        refreshJButton.setMaximumSize( Settings.defaultButtonDimension );
        refreshJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        refreshJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                getCamera().buildOldImage();
                updateMemorisedPicturesJLabel();
            }
        } );
        add( refreshJButton );

        zeroJButton.setPreferredSize( Settings.defaultButtonDimension );
        zeroJButton.setMinimumSize( Settings.defaultButtonDimension );
        zeroJButton.setMaximumSize( Settings.defaultButtonDimension );
        zeroJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        zeroJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                getCamera().zapOldImage();
                updateMemorisedPicturesJLabel();
            }
        } );
        add( zeroJButton, "wrap unrel" );

        saveJButton.setPreferredSize( Settings.defaultButtonDimension );
        saveJButton.setMinimumSize( Settings.defaultButtonDimension );
        saveJButton.setMaximumSize( Settings.defaultButtonDimension );
        saveJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        saveJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                saveCamera();
            }
        } );
        add( saveJButton, "align right" );
    }

    /**
     * A Handle to the camera being edited
     */
    private Camera camera;

    /**
     * Return the camera object
     *
     * @return the Camera object
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Call this method to set the camera this panel is supposed to edit
     *
     * @param camera the camera object
     */
    public void setCamera( Camera camera ) {
        this.camera = camera;
        loadFields();
    }

    /**
     * This method loads the values from the camera to the GUI fields.
     */
    private void loadFields() {
        if ( getCamera() == null ) {
            cameraNameJTextField.setEnabled( false );
            cameraDirJTextField.setEnabled( false );
            filenameJCheckBox.setEnabled( false );
            monitorJCheckBox.setEnabled( false );
            cameraDirJLabel.setEnabled( false );
            memorisedPicsText.setEnabled( false );
            refreshJButton.setEnabled( false );
            zeroJButton.setEnabled( false );
            cameraNameJTextField.setText( "" );
            cameraDirJTextField.setText( "" );
        } else {
            cameraNameJTextField.setText( getCamera().getDescription() );
            cameraDirJTextField.setText( getCamera().getCameraMountPoint() );
            filenameJCheckBox.setSelected( getCamera().getUseFilename() );
            monitorJCheckBox.setSelected( getCamera().getMonitorForNewPictures() );
            updateMemorisedPicturesJLabel();
            cameraNameJTextField.setEnabled( true );
            cameraDirJTextField.setEnabled( true );
            filenameJCheckBox.setEnabled( true );
            monitorJCheckBox.setEnabled( true );
            cameraDirJLabel.setEnabled( true );
            memorisedPicsText.setEnabled( true );
            refreshJButton.setEnabled( true );
            zeroJButton.setEnabled( true );
        }
    }

    /**
     * save the currently edited camera details into the set of cameras
     */
    public void saveCamera() {
        if ( getCamera() != null ) {
            getCamera().setDescription( cameraNameJTextField.getText() );
            getCamera().setCameraMountPoint( cameraDirJTextField.getDirectory().toString() );
            getCamera().setUseFilename( filenameJCheckBox.isSelected() );
            getCamera().setMonitorForNewPictures( monitorJCheckBox.isSelected() );

            Settings.writeCameraSettings();
            notifyActionListeners();
        }
    }

    /**
     * Updates the memorised pictures label
     */
    public void updateMemorisedPicturesJLabel() {
        memorisedPicturesJLabel.setText( getCamera().getOldIndexCountAsString() );
    }

    /**
     * "Root directory of camera on computer's file system:
     */
    private final JLabel cameraDirJLabel = new JLabel( Settings.jpoResources.getString( "cameraDirJLabel" ) );

    /**
     * The new name of the camera
     */
    private final JTextField cameraNameJTextField = new JTextField();

    /**
     * holds the root directory of the camera relative to the host computer's
     * file system
     */
    private final DirectoryChooser cameraDirJTextField
            = new DirectoryChooser( Settings.jpoResources.getString( "cameraDirJLabel" ),
                    DirectoryChooser.DIR_MUST_EXIST );

    /**
     * "Number of pictures remembered from last import:"
     */
    private final JLabel memorisedPicsText = new JLabel( Settings.jpoResources.getString( "memorisedPicsJLabel" ) );

    /**
     * label that informs how many pictures have been memorised for this camera
     */
    private final JLabel memorisedPicturesJLabel = new JLabel();

    /**
     * Refresh Button to memorise the files on the camera
     */
    private final JButton refreshJButton = new JButton( Settings.jpoResources.getString( "refreshJButton" ) );

    /**
     * Button to zero out the memorised pictures on the camera
     */
    private final JButton zeroJButton = new JButton( Settings.jpoResources.getString( "zeroJButton" ) );

    /**
     * Button to save the camera information
     */
    private final JButton saveJButton = new JButton( "Save" );

    /**
     * checkbox to indicate that filenames should be used
     */
    private final JCheckBox filenameJCheckBox = new JCheckBox( Settings.jpoResources.getString( "filenameJCheckBox" ) );

    /**
     * checkbox to indicate whether to monitor for new pictures
     */
    private final JCheckBox monitorJCheckBox = new JCheckBox( Settings.jpoResources.getString( "monitorJCheckBox" ) );

    /**
     * A collection that holds all the listeners that want to be notified about
     * changes to this Camera
     */
    private final Set<ActionListener> listeners = Collections.synchronizedSet( new HashSet<ActionListener>() );

    /**
     * Adds a listener
     * @param actionListener 
     */
    public void addActionListener( ActionListener actionListener ) {
        listeners.add( actionListener );
    }

    /**
     * Removes a listener
     * @param actionListener 
     */
    public void removeActionListener( ActionListener actionListener ) {
        listeners.remove( actionListener );
    }

    /**
     * Notifies the listeners
     */
    public void notifyActionListeners() {
        ActionEvent event = new ActionEvent( this, 0, "save" );
        synchronized ( listeners ) {
            for ( ActionListener actionListener : listeners ) {
                actionListener.actionPerformed( event );
            }
        }
    }
}
