package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.Settings;

import javax.swing.*;

import static org.jpo.datamodel.SortableDefaultMutableTreeNode.GENERIC_ERROR;

/*
 Copyright (C) 2022  Richard Eigenmann.
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

public class CopyToNewLocationHandler {
    /**
     * Brings up a JFileChooser to select the target location and then copies
     * the images to the target location
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final CopyToNewLocationRequest request) {
        final var jFileChooser = new JFileChooser();

        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.setApproveButtonText(Settings.getJpoResources().getString("CopyImageDialogButton"));
        jFileChooser.setDialogTitle(Settings.getJpoResources().getString("CopyImageDialogTitle"));
        jFileChooser.setCurrentDirectory(Settings.getMostRecentCopyLocation());

        final int returnVal = jFileChooser.showSaveDialog(Settings.getAnchorFrame());
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        final var targetDirectory = jFileChooser.getSelectedFile();
        Settings.memorizeCopyLocation(targetDirectory.toString());
        JpoEventBus.getInstance().post(new CopyLocationsChangedEvent());

        if (!targetDirectory.canWrite()) {
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("htmlDistCanWriteError"),
                    GENERIC_ERROR,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JpoEventBus.getInstance().post(new CopyToDirRequest(request.nodes(), targetDirectory));
    }

}
