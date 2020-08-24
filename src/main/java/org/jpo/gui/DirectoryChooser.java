package org.jpo.gui;

import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/*
 DirectoryChooserTest.java:  a object that displays a JTextFiled and has a button
 next to it which allows you to bring up a filechooser

 Copyright (C) 2002 - 2019  Richard Eigenmann.
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
 * An object of this class displays a directory field with a dropdown button to
 * select the previously used directories and has a button next to it which
 * allows you to bring up a filechooser. The selected directory is validated as
 * to whether it exists and is writable. If this is not the case the characters
 * of the textfield are displayed in red.
 *
 * @author Richard Eigenmann
 */
public class DirectoryChooser
        extends JPanel {

    /**
     * Creates the directory chooser component
     *
     * @param chooserTitle The title for a JFileChooser window if the user
     * clicks the button
     * @param validationType	The type of validation that must be performed on
     * the directory. Can either be DIR_MUST_EXIST or DIR_MUST_BE_WRITABLE
     */
    public DirectoryChooser( final String chooserTitle, int validationType ) {
        this.validationType = validationType;
        initComponents();
        addBehaviour();
    }
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( DirectoryChooser.class.getName() );
    /**
     * The title that will be used in the JFileChooser if the user clicks the
     * button
     */
    private final String chooserTitle = "";
    /**
     * Variable that records what type of validation this object must perform.
     * Valid Types are DIR_MUST_EXIST and DIR_MUST_BE_WRITABLE.
     */
    private final int validationType;
    /**
     * The drop down list of the previously used directories
     */
    private final JComboBox<Object> directoryJComboBox = new JComboBox<>();
    /**
     * Field that allows the user to capture the directory which is a sub object
     * of the JComboBox. This code relies that the ComboBoxEditors are
     * implemented as a JTextField. The Java 1.4.0 manual says this is the case.
     */
    private final JTextField directoryJTextField = (JTextField) directoryJComboBox.getEditor().getEditorComponent();

    /**
     * Button that brings up a file chooser for the directory
     *
     */
    private final JButton directoryChooserJButton = new JButton( Settings.jpoResources.getString( "threeDotText" ) );

    /**
     * creates the GUI widgets
     */
    private void initComponents() {
        Tools.checkEDT();
        directoryJComboBox.setEditable( true );
        for ( String copyLocation : Settings.copyLocations ) {
            addDirToDropdown( copyLocation );
        }
        directoryJComboBox.setPreferredSize( Settings.filenameFieldPreferredSize );
        directoryJComboBox.setMinimumSize( Settings.filenameFieldMinimumSize );
        directoryJComboBox.setMaximumSize( Settings.filenameFieldMaximumSize );
        add( directoryJComboBox );

        directoryChooserJButton.setPreferredSize( Settings.threeDotButtonSize );
        directoryChooserJButton.setMinimumSize( Settings.threeDotButtonSize );
        directoryChooserJButton.setMaximumSize( Settings.threeDotButtonSize );
        add( directoryChooserJButton );
    }

    /**
     * Add listeners to the GUI components
     */
    private void addBehaviour() {
        directoryJComboBox.addActionListener(( ActionEvent e ) -> sendChangeNotification());

        directoryJTextField.setInputVerifier( new InputVerifier() {

            @Override
            public boolean verify( JComponent input ) {
                sendChangeNotification();
                return true;
            }
        } );

        directoryChooserJButton.addActionListener(( ActionEvent e ) -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
            jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "genericSelectText" ) );
            jFileChooser.setDialogTitle( chooserTitle );
            jFileChooser.setCurrentDirectory( new File( getText() ) );
            
            int returnVal = jFileChooser.showOpenDialog( DirectoryChooser.this );
            if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                setText( jFileChooser.getSelectedFile().getPath() );
                sendChangeNotification();
            }
        });

    }

    /**
     * Puts the directory into the DirectoryChooserTest
     *
     * @param directory The string of the directory to be set in the field
     */
    public void setText( String directory ) {
        directoryJComboBox.setSelectedItem( makeObj( directory ) );
    }

    /**
     * Set the DirectoryChooserTest to the supplied File
     *
     * @param directory The directory to be set in the DirectoryChooserTest
     */
    public void setFile( File directory ) {
        setText( directory.toString() );
    }
    /**
     * The color to use if the directory doesn't pass validation
     */
    private static final Color COLOR_BAD = Color.red;
    /**
     * The color to use if the directory passes validation
     */
    private static final Color COLOR_GOOD = Color.black;

    /**
     * This method checks the path specified in the directory chooser and sets
     * the color to red if it doesn't meet the validation criteria or black if
     * it does.
     */
    private void setColor() {
        if ( checkDirectory( getDirectory(), validationType ) ) {
            directoryJTextField.setForeground( COLOR_GOOD );
        } else {
            directoryJTextField.setForeground( COLOR_BAD );
        }
    }
    
    /**
     * Constant that indicates that the directory must exist
     */
    static final int DIR_MUST_EXIST = 1;
    /**
     * Constant that indicates that the directory must exist and be writable;
     */
    public static final int DIR_MUST_BE_WRITABLE = DIR_MUST_EXIST + 1;

    /**
     * Test the supplied File on whether it is a directory and whether is can be
     * written to.
     *
     * @param testDir Directory to test
     * @param validationType the flag for which test is to be performed
     * @return true if good, false if bad
     */
    private static boolean checkDirectory(File testDir, int validationType) {
        switch ( validationType ) {
            case DIR_MUST_EXIST:
                return testDir.exists() && testDir.isDirectory();
            case DIR_MUST_BE_WRITABLE:
                if ( testDir.exists() ) {
                    return testDir.canWrite() && testDir.isDirectory();
                } else {
                    File testDirParent = testDir.getParentFile();
                    if ( testDirParent != null ) {
                        return checkDirectory( testDirParent, validationType );
                    } else {
                        return false;
                    }
                }
            default:
                return false;
        }
    }
    
    /**
     * Variable to memorise what was in the field the last time to detect real
     * changes
     */
    private String oldFieldContents = "";

    /**
     * Checks whether the field was changed and if so sends a notification to
     * the registered listeners.
     */
    private void sendChangeNotification() {
        String newFieldContents = getText();
        if ( !oldFieldContents.equals( newFieldContents ) ) {
            LOGGER.fine( String.format( "The field changed from %s to %s", oldFieldContents, newFieldContents ) );
            setColor();
            oldFieldContents = newFieldContents;
            synchronized ( changeListeners ) {
                for (ChangeListener changeListener : changeListeners) {
                    changeListener.stateChanged(new ChangeEvent(this));
                }
            }
        }
    }
    /**
     * The ChangeListeners that want to be notified when the directory changes.
     */
    private final Set<ChangeListener> changeListeners = Collections.synchronizedSet(new HashSet<>() );

    /**
     * Adds a change listener that will be notified whenever the text in the
     * field changes
     *
     * @param listener The listener that should be notified
     */
    public void addChangeListener( ChangeListener listener ) {
        changeListeners.add( listener );
    }

    /**
     * Removes a change listener that no longer wants to know about text changes
     *
     * @param listener The listener to remove
     */
    public void removeChangeListener( ChangeListener listener ) {
        changeListeners.remove( listener );
    }

    /**
     * Returns a string representation of the currently selected item in the
     * dropdown list
     *
     * @return the selected dropdown entry as a string or "" if none is
     * selected.
     */
    private String getText() {
        Object o = directoryJComboBox.getSelectedItem();
        if ( o != null ) {
            return o.toString();
        } else {
            return "";
        }
    }

    /**
     * Returns the directory of the chooser as a new File object
     *
     * @return The directory currently selected
     */
    public File getDirectory() {
        return new File( getText() );
    }

    /**
     * This method creates an Object from a String. As discussed in the Java API
     * for JComboBox.addItem
     * @param item item to add
     * @return The created object
     */
    private Object makeObj( final String item ) {
        return new Object() {

            @Override
            public String toString() {
                return item;
            }
        };
    }

    /**
     * This method adds the supplied directory to the dropdown if it is a valid
     * directory
     *
     * @param directory The directory to be added to the dropdown list
     */
    private void addDirToDropdown(@NotNull String directory) {
        File f = new File( directory );
        if ( f.exists() && f.isDirectory() ) {
            directoryJComboBox.addItem( makeObj( directory ) );
        }
    }

    /**
     * Sets the DirectoryChooser to enabled or unenabled. Delegates this down to
     * the component Swing components.
     *
     * @param enabled True if enabled, false if not.
     */
    @Override
    public void setEnabled( boolean enabled ) {
        directoryJComboBox.setEnabled( enabled );
        directoryJTextField.setEnabled( enabled );
        directoryChooserJButton.setEnabled( enabled );
    }
}
