package jpo.gui;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import jpo.dataModel.Settings;


/*
 LicenseWindow.java:  Creates the License window

 Copyright (C) 2007 - 2017 Richard Eigenmann, Zürich, Switzerland
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
 * This class creates and Shows the License window
 */
public class LicenseWindow {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( LicenseWindow.class.getName() );

    /**
     * Creates the License Window
     */
    public LicenseWindow() {
        JTextArea licenseJTextArea = new JTextArea( "reading the file gpl.txt" );
        licenseJTextArea.setWrapStyleWord( true );
        licenseJTextArea.setLineWrap( true );
        licenseJTextArea.setEditable( false );
        JScrollPane jScrollPane = new JScrollPane( licenseJTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        jScrollPane.setPreferredSize( new Dimension( 500, 400 ) );

        StringBuilder sb = new StringBuilder( "" );
        String textLine;
        try (
                InputStream in = LicenseWindow.class.getClassLoader().getResourceAsStream( "gpl.txt" );
                BufferedReader bin = new BufferedReader( new InputStreamReader( in ) ); ) {
            while ( ( textLine = bin.readLine() ) != null ) {
                sb.append( textLine ).append( "\n" );
            }
        } catch ( IOException | NullPointerException e ) {
            LOGGER.log( Level.SEVERE, "Jpo.java: Error while reading gpl.txt: {0}", e.getMessage());
        }
        licenseJTextArea.setText( sb.toString() );
        licenseJTextArea.setCaretPosition( 0 );

        Object[] License = { jScrollPane };

        final String OK = "OK";
        Object[] options = { OK };

        JOptionPane pane = new JOptionPane( License,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.OK_OPTION,
                null,
                options,
                options[0] );

        JDialog dialog = pane.createDialog( Settings.anchorFrame, "GNU General Public License" );
        dialog.setVisible( true );
    }
}
