package org.jpo.gui.swing;

import org.jpo.datamodel.Settings;
import org.jpo.gui.XmlFilter;

import javax.swing.*;
import java.io.File;

/*
 Copyright (C) 2023 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

public class Filechoosers {
    private Filechoosers() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Method that chooses a xml file or returns null
     *
     * @return the xml file or null
     */
    public static File chooseXmlFile() {
        final var jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setApproveButtonText(Settings.getJpoResources().getString("fileOpenButtonText"));
        jFileChooser.setDialogTitle(Settings.getJpoResources().getString("fileOpenHeading"));
        jFileChooser.setFileFilter(new XmlFilter());
        jFileChooser.setCurrentDirectory(Settings.getMostRecentCopyLocation());

        int returnVal = jFileChooser.showOpenDialog(Settings.getAnchorFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return jFileChooser.getSelectedFile();
        } else {
            return null;
        }
    }
}
