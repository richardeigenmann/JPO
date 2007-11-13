package jpo;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
DirectoryChooser.java:  a object that displays a JTextFiled and has a button
next to it which allows you to bring up a filechooser
 
Copyright (C) 2002, 2007  Richard Eigenmann.
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
 *   An object of this class displays a text field with a dropdown button
 *   to select the previously used directories and has a button next to it which allows you to bring
 *   up a filechooser. The selected directory is validated as to whether it exists and is writable.
 *   If this is not the case the characters of the textfield are displayed in red.
 *
 *   @author  Richard Eigenmann
 */
public class DirectoryChooser extends JPanel {
    
    
    /**
     *   Constant that indicates that the directory must exist
     */
    public static final int DIR_MUST_EXIST = 1;
    
    
    /**
     *   Constant that indicates that the directory must exist and be writable;
     */
    public static final int DIR_MUST_BE_WRITABLE = DIR_MUST_EXIST + 1;
    
    
    /**
     *  Variable that records what type of validation this object must perform.
     *  Valid Types are DIR_MUST_EXIST  and DIR_MUST_BE_WRITABLE.
     */
    private int validationType;
    
    
    /**
     *   the drop down list of the previously used directories
     */
    private JComboBox directoryJComboBox = new JComboBox();
    
    
    /**
     *   field that allows the user to capture the directory which is a sub object of the JComboBox
     */
    public JTextField directoryJTextField;
    
    
    /**
     *  button that brings up a file chooser for the directory
     **/
    private JButton directoryChooserJButton = new JButton();
    
    
    /**
     *   Creates the GUI
     *
     *   @param chooserTitle 	the title for a JFileChooser window
     *   @param validationType	The type of validation that must be performed
     *				on the directory. Can either be DIR_MUST_EXIST or
     *				DIR_MUST_BE_WRITABLE
     */
    public DirectoryChooser( final String chooserTitle, int validationType ) {
        this.validationType = validationType;
        setLayout( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(4,4,4,4);
        setBorder( BorderFactory.createEmptyBorder() );
        
        c.gridx = 0; c.gridy = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        directoryJComboBox.setEditable( true );
        
        for ( int i=0; i < Settings.copyLocations.length; i++ ) {
            addDir( Settings.copyLocations[ i ] );
        }
        
        directoryJComboBox.setPreferredSize( Settings.filenameFieldPreferredSize );
        directoryJComboBox.setMinimumSize( Settings.filenameFieldMinimumSize );
        directoryJComboBox.setMaximumSize( Settings.filenameFieldMaximumSize );
        directoryJComboBox.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e ) {
                checkDirectory();
            }
        } );
        add( directoryJComboBox, c );
        
        // potentially dangerous code since we are assuming that the editor will
        // be implemented as a JTextField. The 1.4.0 manual says ComboBoxEditors
        // are implemented as such.
        directoryJTextField = (JTextField) directoryJComboBox.getEditor().getEditorComponent();
        directoryJTextField.setInputVerifier( new FieldVerifier() );
        
        
        c.weightx = 0;
        c.gridx++;
        c.fill = GridBagConstraints.NONE;
        directoryChooserJButton.setText( Settings.jpoResources.getString("threeDotText") );
        directoryChooserJButton.setPreferredSize( Settings.threeDotButtonSize );
        directoryChooserJButton.setMinimumSize( Settings.threeDotButtonSize ) ;
        directoryChooserJButton.setMaximumSize( Settings.threeDotButtonSize );
        final Component parentComponent = this;
        directoryChooserJButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
                jFileChooser.setApproveButtonText( Settings.jpoResources.getString("genericSelectText") );
                jFileChooser.setDialogTitle( chooserTitle );
                jFileChooser.setCurrentDirectory( new File( getText() ) );
                
                int returnVal = jFileChooser.showOpenDialog( parentComponent );
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    setText( jFileChooser.getSelectedFile().getPath() );
                    checkDirectory();
                }
            }
        } );
        add( directoryChooserJButton, c );
    }
    
    
    /**
     *  puts the text into the field
     */
    public void setText( String text ) {
        directoryJComboBox.setSelectedItem( makeObj( text ) );
    }
    
    
    /**
     *  sets the choice to the indicated File
     */
    public void setFile( File f ) {
        setText( f.toString() );
    }
    
    
    /**
     *  Returns a string representation of the currently selected item in the dropdown list
     *  @return the selected dropdown entry as a string or "" if none is selected.
     */
    public String getText() {
        Object o = directoryJComboBox.getSelectedItem();
        if ( o != null ) {
            return o.toString();
        } else {
            return "";
        }
    }
    
    /**
     *  gets the text from the field
     */
    public File getFile() {
        return new File( getText() );
    }
    
    
    
    /**
     *   this method verifies that the path specified in the thumbnailPathJTextField
     *   is valid. It sets the color of the font to red if this is not ok and
     *   returns false to the caller. If all is fine it returns true.
     *
     *   @return returns true if the directory passes the validation, false if it doesn't
     */
    public boolean checkDirectory() {
        File testDir = new File( getText() );
        if ( checkDirectory( testDir, validationType ) ) {
            directoryJTextField.setForeground( Color.black );
            return true;
        } else {
            directoryJTextField.setForeground( Color.red );
            return false;
        }
    }
    
    
    
    /**
     *   test the supplied File on whether it is a directory and whether is can be written to.
     */
    public static boolean checkDirectory( File testDir, int validationType ) {
        switch ( validationType ) {
            case DIR_MUST_EXIST :
                return testDir.exists() && testDir.isDirectory();
            case DIR_MUST_BE_WRITABLE :
                if ( testDir.exists() )  {
                    return  testDir.canWrite() && testDir.isDirectory();
                } else {
                    File testDirParent = testDir.getParentFile();
                    if ( testDirParent != null )
                        return checkDirectory( testDirParent, validationType );
                    else
                        return false;
                }
        }
        return false;
    }
    
    
    /**
     *  This method creates an Object from a String. As discussed in the Java API for JComboBox.addItem
     */
    private Object makeObj(final String item)  {
        return new Object() {
            public String toString() {
                return item;
            }
        };
    }
    
    /**
     *  This method adds the supplied directory if it is a valid directory
     *  @param  directory	The directory to be added to the dropdown list
     *  @return 	true if the directory was added, false if not.
     */
    public boolean addDir( String directory )  {
        if ( directory == null ) {
            return false;
        }
        
        File f = new File( directory );
        if ( f.exists() && f.isDirectory() ) {
            directoryJComboBox.addItem( makeObj( directory ) );
            return true;
        } else {
            return false;
        }
    }
    
    
    /**
     *  special inner class that verifies whether the path indicated by the component is
     *  valid. It is invoked every time the cursor tries to leave the text field.
     */
    private class FieldVerifier extends InputVerifier {
        public boolean verify( JComponent input ) {
            checkDirectory();
            return true;
        }
    }
    
    
    /**
     *  pass the setEnabled down to the textfield and buttons
     */
    public void setEnabled( boolean enabled ) {
        directoryJComboBox.setEnabled( enabled );
        directoryJTextField.setEnabled( enabled );
        directoryChooserJButton.setEnabled( enabled );
    }
    
}


