package org.jpo.eventbus;

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

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the request to rename a file
 */
@EventHandler
public class RenameFileHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(RenameFileHandler.class.getName());

    /**
     * Bring up a Dialog where the user can input a new name for a file and
     * rename it. If the target file already exists and would overwrite the existing file
     * A new name is suggested that the user can accept or abort the rename.
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(@NonNull final RenameFileRequest request) {
        final PictureInfo pictureInfo = (PictureInfo) request.node().getUserObject();
        LOGGER.log(Level.INFO, "Renaming node: {0}\nfrom File: {1}\nto new file: {2}", new Object[]{request.node(), pictureInfo.getImageFile().getPath(), request.newFileName()});
        final var imageFile = pictureInfo.getImageFile();
        final var newName = request.newFileName();
        final var newFile = new File(imageFile.getParentFile(), newName);
        if (imageFile.renameTo(newFile)) {
            LOGGER.log(Level.INFO, "Successfully renamed:\n{0}\nto: {1}", new Object[]{imageFile, newName});
            pictureInfo.setImageLocation(newFile);
            request.node().getPictureCollection().setUnsavedUpdates();
        } else {
            LOGGER.log(Level.INFO, "Rename failed from:\n{0}\nto: {1}", new Object[]{imageFile, newName});
        }

    }
}
