package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SingleNodeNavigator;
import org.jpo.gui.ThumbnailController;

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

/**
 * Refreshes a thumbnail
 */
public class RefreshThumbnailHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(RefreshThumbnailHandler.class.getName());

    /**
     * Handles the RefreshThumbnailRequest
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final RefreshThumbnailRequest request) {
        for (final var node : request.nodes()) {
            if (node.isRoot()) {
                LOGGER.fine("Ignoring the request for a thumbnail refresh on the Root Node as the query for it's parent's children will fail");
                return;
            }
            LOGGER.log(Level.FINE, "refreshing the thumbnail on the node {0}%nAbout to create the thumbnail", this);
            final var t = new ThumbnailController(Settings.getThumbnailSize());
            t.setNode(new SingleNodeNavigator(node), 0);
        }
    }
}
