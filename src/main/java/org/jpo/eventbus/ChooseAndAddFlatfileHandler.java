package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.Settings;

import javax.swing.*;

/*
 Copyright (C) 2022-2023 Richard Eigenmann.
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

/**
 * Brings up a chooser to pick a flat file and add them to the group.
 */
@EventHandler
public class ChooseAndAddFlatfileHandler {
    /**
     * Brings up a chooser to pick a flat file and add them to the group.
     *
     * @param request the Request
     */
    @Subscribe
    public void handleEvent(final ChooseAndAddFlatfileRequest request) {
        final var jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        jFileChooser.setApproveButtonText(Settings.getJpoResources().getString("fileOpenButtonText"));
        jFileChooser.setDialogTitle(Settings.getJpoResources().getString("addFlatFileTitle"));
        jFileChooser.setCurrentDirectory(Settings.getMostRecentCopyLocation());

        final int returnVal = jFileChooser.showOpenDialog(Settings.getAnchorFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final var chosenFile = jFileChooser.getSelectedFile();
            JpoEventBus.getInstance().post(new AddFlatFileRequest(request.node(), chosenFile));

        }
    }

}
