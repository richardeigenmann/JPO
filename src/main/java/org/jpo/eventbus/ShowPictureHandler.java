package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.FlatGroupNavigator;
import org.jpo.datamodel.ImageIO;
import org.jpo.datamodel.MimeTypes;
import org.jpo.datamodel.PictureInfo;
import org.jpo.gui.PictureViewer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

public class ShowPictureHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ShowPictureHandler.class.getName());

    /**
     * When we see a ShowPictureRequest this method will open a {@link PictureViewer}
     * and will tell it to show the {@link FlatGroupNavigator} based on the pictures
     * parent node starting at the current position
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
        if (!ImageIO.jvmHasReader(file)) {
            LOGGER.log(Level.SEVERE, "Can''t find a JVM reader for file: {0}", file);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            final var pictureViewer = new PictureViewer();
            pictureViewer.showNode(request.nodeNavigator(), request.currentIndex());
        });
    }

}
