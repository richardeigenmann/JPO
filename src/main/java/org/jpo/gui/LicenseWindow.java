package org.jpo.gui;

import org.apache.commons.io.IOUtils;
import org.jpo.datamodel.Settings;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


/*
 Copyright (C) 2007-2024 Richard Eigenmann, Zürich, Switzerland
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
     * Creates the License Window
     */
    public LicenseWindow() {
        final JTextArea licenseJTextArea = new JTextArea( "reading the file gpl.txt" );
        licenseJTextArea.setWrapStyleWord( true );
        licenseJTextArea.setLineWrap( true );
        licenseJTextArea.setEditable( false );
        final JScrollPane jScrollPane = new JScrollPane( licenseJTextArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
        jScrollPane.setPreferredSize( new Dimension( 500, 400 ) );

        String license;
        try {
            license = IOUtils.toString(Objects.requireNonNull(LicenseWindow.class.getClassLoader().getResourceAsStream("gpl.txt")), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            license = "Error loading the license text. The license is still GPL.";
        }

        licenseJTextArea.setText( license );
        licenseJTextArea.setCaretPosition( 0 );

        final Object[] objects = {jScrollPane};

        final String OK = "OK";
        final Object[] options = { OK };

        final JOptionPane pane = new JOptionPane(objects,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.OK_OPTION,
                null,
                options,
                options[0]);

        final JDialog dialog = pane.createDialog(Settings.getAnchorFrame(), "GNU General Public License");
        dialog.setVisible( true );
    }
}
