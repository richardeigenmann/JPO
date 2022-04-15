package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.cache.JpoCache;
import org.jpo.datamodel.Settings;

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

public class ShutdownApplicationHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ShutdownApplicationHandler.class.getName());

    /**
     * Shuts down JPO no questions asked. Wrap it as a next request with a
     * UnsavedUpdatesDialogRequest
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final ShutdownApplicationRequest request) {
        if (Settings.isUnsavedSettingChanges()) {
            Settings.writeSettings();
        }

        JpoCache.shutdown();

        LOGGER.info("Exiting JPO\n------------------------------------------------------------");

        System.exit(0);
    }

}
