package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.gui.VersionUpdate;

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

/**
 * Listens to the CheckForUpdatesRequest and fulfills it if the conditions are met
 */
@EventHandler
public class CheckForUpdatesHandler {
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(CheckForUpdatesHandler.class.getName());

    /**
     * Listens to the CheckForUpdatesRequest and fulfills it if the conditions are met
     *
     * @param request the request object which contains the forceCheck flag
     */
    @Subscribe
    public void handleEvent(final CheckForUpdatesRequest request) {
        LOGGER.log(Level.INFO, "Caught the request to check for Updates");
        if (request.forceCheck() || VersionUpdate.mayCheckForUpdates()) {
            new VersionUpdate();
        }
    }
}
