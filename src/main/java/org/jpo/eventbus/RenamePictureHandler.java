package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.datamodel.Tools;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2023-2025 Richard Eigenmann.
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
public class RenamePictureHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(RenamePictureHandler.class.getName());

    /**
     * Bring up a Dialog where the user can input a new name for a file and
     * rename it. If the target file already exists and would overwrite the existing file
     * A new name is suggested that the user can accept or abort the rename.
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(@NonNull final RenamePictureRequest request) {
        for (final var node : request.nodes()) {
            renameOnePictureRequest(node);
        }
    }

    /**
     * Handles the request to rename the picture indicated by a node. The new name
     * is chosen by {Tools.inventFilename}
     *
     * @param node the node whose picture needs a new filename.
     */
    private static void renameOnePictureRequest(@NonNull final SortableDefaultMutableTreeNode node) {
        final var pictureInfo = (PictureInfo) node.getUserObject();

        final var imageFile = pictureInfo.getImageFile();
        if (imageFile == null) {
            return;
        }

        final var object = Settings.getJpoResources().getString("FileRenameLabel1")
                + imageFile
                + Settings.getJpoResources().getString("FileRenameLabel2");
        final var selectedValue = JOptionPane.showInputDialog(Settings.getAnchorFrame(),
                object,
                imageFile.toString());
        if (selectedValue != null) {
            var newName = new File(selectedValue);

            if (newName.exists()) {
                final var alternativeNewName = Tools.inventFilename(newName.getParentFile(), newName.getName());
                int alternativeAnswer = JOptionPane.showConfirmDialog(Settings.getAnchorFrame(),
                        String.format(Settings.getJpoResources().getString("FileRenameTargetExistsText"), newName, alternativeNewName),
                        Settings.getJpoResources().getString("FileRenameTargetExistsTitle"),
                        JOptionPane.OK_CANCEL_OPTION);
                if (alternativeAnswer == JOptionPane.OK_OPTION) {
                    newName = alternativeNewName;
                } else {
                    LOGGER.log(Level.INFO, "File exists and new name was not accepted by user");
                    return;
                }
            }
            JpoEventBus.getInstance().post(new RenameFileRequest(node, newName.getName()));
        }
    }

}
