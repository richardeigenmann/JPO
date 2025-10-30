package org.jpo.gui;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.Settings;
import org.jpo.eventbus.*;

import java.io.File;
import java.util.logging.Logger;

/*
 Copyright (C) 2014-2024 Richard Eigenmann.
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
 * Handles application startup
 *
 * @author Richard Eigenmann
 */
@EventHandler
public class ApplicationStartupHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ApplicationStartupHandler.class.getName());

    /**
     * Title for Info Boxes
     */
    public static final String GENERIC_INFO = JpoResources.getResource("genericInfo");


    /**
     * Handles the application startup by posting an {@link OpenMainWindowRequest},
     * starting the {@link StartCameraWatchDaemonRequest}, starting the
     * {@link StartThumbnailCreationDaemonRequest}. If an autoLoad is defined in the Settings it
     * will load that or start a new collection with {@link StartNewCollectionRequest}.
     *
     * @param request the startup request
     * @see OpenMainWindowRequest
     * @see StartCameraWatchDaemonRequest
     * @see StartThumbnailCreationDaemonRequest
     * @see FileLoadRequest
     * @see StartNewCollectionRequest
     */
    @Subscribe
    public void handleEvent(final ApplicationStartupRequest request) {
        LOGGER.info("------------------------------------------------------------\n      Starting JPO");

        Settings.loadSettings();
        // This needs to happen very early on before the graphics subsystem initialises.
        System.setProperty("sun.java2d.uiScale", String.valueOf(Settings.getUiScale()));
        final var pictureCollection = new PictureCollection();
        Settings.setPictureCollection(pictureCollection);

        JpoEventBus.getInstance().post(new OpenMainWindowRequest());
        JpoEventBus.getInstance().post(new StartCameraWatchDaemonRequest());

        JpoEventBus.getInstance().post(new StartThumbnailCreationDaemonWatchDogRequest());

        if ((Settings.getAutoLoad() != null) && (! Settings.getAutoLoad().isEmpty())) {
            final var xmlFile = new File(Settings.getAutoLoad());
            JpoEventBus.getInstance().post(new FileLoadRequest(pictureCollection, xmlFile));
        } else {
            JpoEventBus.getInstance().post(new StartNewCollectionRequest());
        }
        JpoEventBus.getInstance().post(new CheckForUpdatesRequest(false));

    }


}
