package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.*;
import org.jpo.gui.PictureViewer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2022-2024 Richard Eigenmann.
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
public class ShowPictureHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ShowPictureHandler.class.getName());

    /**
     * If the node pointed at by the {@link NodeNavigatorInterface} is a document or a move
     * we ask the Operating System to open the node.
     * If it's a picture for which we have a reader we create a new PictureViewer to show the picture.
     *
     * @param request the {@link ShowPictureRequest}
     */
    @Subscribe
    public void handleEvent(final ShowPictureRequest request) {
        final var node = request.nodeNavigator().getNode(request.currentIndex());
        final var pictureInfo = (PictureInfo) node.getUserObject();
        final var file = pictureInfo.getImageFile();
        if ((MimeTypes.isADocument(file)) || (MimeTypes.isAMovie(file))) {
            try {
                Desktop.getDesktop().open(file);
            } catch (final IOException e) {
                LOGGER.log(Level.SEVERE, "Could not open file {0} with a default application: {1}", new Object[]{file, e.getMessage()});
            }
            return;
        }
        if (!JpoImageIO.jvmHasReader(file)) {
            LOGGER.log(Level.SEVERE, "Can''t find a JVM reader for file: {0}", file);
            return;
        }
        SwingUtilities.invokeLater(() -> new PictureViewer(request));
    }

}
