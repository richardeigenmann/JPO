package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.io.FileUtils;
import org.jpo.datamodel.PictureInfo;
import org.jpo.gui.Settings;
import org.jpo.datamodel.Tools;
import org.jpo.gui.JpoResources;
import org.jspecify.annotations.NonNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.gui.ApplicationStartupHandler.GENERIC_INFO;

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
public class CopyToDirHandler {
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(CopyToDirHandler.class.getName());

    /**
     * Copies the pictures of the supplied nodes to the target directory
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final CopyToDirRequest request) {

        var picsCopied = 0;
        for (final var node : request.nodes()) {
            if (node.getUserObject() instanceof PictureInfo pictureInfo) {
                if (copyPicture(pictureInfo, request.targetDirectory())) {
                    picsCopied++;
                }
            } else {
                LOGGER.log(Level.INFO, "Node {0} is not a picture. Can''t copy other nodes to a directory.", node);
            }
        }
        JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                String.format(JpoResources.getResource("copyToNewLocationSuccess"), picsCopied, request.nodes().size()),
                GENERIC_INFO,
                JOptionPane.INFORMATION_MESSAGE);

    }

    /**
     * Validates the target of the picture copy instruction and tries to find
     * the appropriate thing to do. It does the following steps:<br>
     * 1: If the target is an existing file then it invents a new filename<br>
     * 2: If the target is a directory the filename of the original is used.<br>
     * 3: If the target directory doesn't exist then the directories are
     * created.<br>
     * 4: The file extension is made to be that of the original if it isn't
     * already that.<br>
     * When all preconditions are met the image is copied
     *
     * @param targetDirectory The target location for the new Picture.
     * @return true if successful, false if not
     */
    private static boolean copyPicture(@NonNull final PictureInfo pictureInfo, @NonNull File targetDirectory) {
        Objects.requireNonNull(targetDirectory, "targetDirectory must not be null");

        final var originalFile = pictureInfo.getImageFile();
        final var targetFile = Tools.inventFilename(targetDirectory, originalFile.getName());
        try {
            FileUtils.copyFile(originalFile, targetFile);
        } catch (final IOException e) {
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    "IOException: " + e.getMessage(),
                    JpoResources.getResource("genericError"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

}
