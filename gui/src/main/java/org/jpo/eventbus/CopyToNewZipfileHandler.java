package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.gui.Settings;
import org.jpo.gui.JpoResources;

import javax.swing.*;

/*
 Copyright (C) 2022-2025 Richard Eigenmann.
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
@EventHandler
public class CopyToNewZipfileHandler {
    /**
     * Brings up a JFileChooser to select the target zip file and then copies
     * the images there
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final CopyToNewZipfileRequest request) {
        final var jFileChooser = new JFileChooser();

        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setApproveButtonText(JpoResources.getResource("genericSelectText"));
        jFileChooser.setDialogTitle("Pick the zipfile to which the pictures should be added");
        jFileChooser.setCurrentDirectory(Settings.getMostRecentCopyLocation());

        final int returnVal = jFileChooser.showDialog(Settings.getAnchorFrame(), "Select");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        final var chosenFile = jFileChooser.getSelectedFile();
        Settings.memorizeZipFile(chosenFile.getPath());

        JpoEventBus.getInstance().post(new CopyToZipfileRequest(request.nodes(), chosenFile));
    }

}
