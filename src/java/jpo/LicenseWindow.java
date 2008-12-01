package jpo;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/*
LicenseWindow.java:  Creates the License window
 
Copyright (C) 2007-2008 Richard Eigenmann, Zürich, Switzerland
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
 * This class creates the License window
 */

public class LicenseWindow {
    
    public LicenseWindow() {
        JTextArea licenseJTextArea  = new JTextArea("reading the file gpl.txt");
        licenseJTextArea.setWrapStyleWord(true);
        licenseJTextArea.setLineWrap(true);
        licenseJTextArea.setEditable(false);
        JScrollPane jsp = new JScrollPane( licenseJTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        jsp.setPreferredSize( new Dimension(500, 400) );
        
        
        String sb = new String("");
        String textLine;
        try {
            InputStream in = ApplicationJMenuBar.class.getResourceAsStream( "gpl.txt" );
            BufferedReader bin = new BufferedReader( new InputStreamReader( in ) );
            while ( ( textLine = bin.readLine() ) != null ) {
                sb += textLine + "\n";
            }
            bin.close();
            in.close();
        } catch (IOException e) {
            Tools.log( "Jpo.java: Error while reading gpl.txt: " + e.getMessage() );
        }
        licenseJTextArea.setText( sb );
        licenseJTextArea.setCaretPosition( 0 );

        
        Object[] License = {jsp};
        
        final String btnString1 = "OK";
        Object[] options = {btnString1};
        
        JOptionPane pane = new JOptionPane(License,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.OK_OPTION,
                null,
                options,
                options[0]);
        
        JDialog dialog = pane.createDialog( Settings.anchorFrame, "GNU General Public License");
        dialog.setVisible( true );
    }
}

