package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.Settings;
import org.jpo.gui.ApplicationStartupHandler;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
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

public class OpenRecentCollectionHandler {
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(OpenRecentCollectionHandler.class.getName());

    /**
     * Title for error dialogs
     */
    private static final String GENERIC_ERROR = Settings.getJpoResources().getString("genericError");

    /**
     * Handles the request to open a recent collection
     * <p>
     * Remember to wrap this request in an UnsavedUpdatesDialogRequest if you
     * care about unsaved changes as this request will not check for unsaved
     * changes
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final OpenRecentCollectionRequest request) {
        final int i = request.index();

        new Thread("OpenRecentCollectionRequest") {

            @Override
            public void run() {
                final var fileToLoad = new File(Settings.getRecentCollections()[i]);
                try {
                    Settings.getPictureCollection().fileLoad(fileToLoad);
                } catch (final FileNotFoundException ex) {
                    Logger.getLogger(ApplicationStartupHandler.class.getName()).log(Level.SEVERE, null, ex);
                    LOGGER.log(Level.INFO, "FileNotFoundException: {0}", ex.getMessage());
                    JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                            ex.getMessage(),
                            GENERIC_ERROR,
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JpoEventBus.getInstance().post(new ShowGroupRequest(Settings.getPictureCollection().getRootNode()));

                Settings.pushRecentCollection(fileToLoad.toString());
                JpoEventBus.getInstance().post(new RecentCollectionsChangedEvent());
            }
        }.start();
    }

}
