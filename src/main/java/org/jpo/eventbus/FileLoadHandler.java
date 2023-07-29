package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.Settings;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.datamodel.SortableDefaultMutableTreeNode.GENERIC_ERROR;

/*
 Copyright (C) 2023 Richard Eigenmann.
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
public class FileLoadHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(FileLoadHandler.class.getName());


    /**
     * Loads the file by calling
     * {@link PictureCollection#fileLoad}. If there is a problem
     * creates a new collection.
     * <p>
     * Remember to wrap this request in an UnsavedUpdatesDialogRequest if you
     * care about unsaved changes as this request will not check for unsaved
     * changes
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final FileLoadRequest request) {
        final var fileToLoad = request.fileToLoad();
        new Thread("FileLoadRequest") {

            @Override
            public void run() {
                try {
                    Settings.getPictureCollection().fileLoad(fileToLoad);
                    Settings.pushRecentCollection(fileToLoad.toString());
                    JpoEventBus.getInstance().post(new RecentCollectionsChangedEvent());
                    JpoEventBus.getInstance().post(new ShowGroupRequest(Settings.getPictureCollection().getRootNode()));
                    JpoEventBus.getInstance().post(new CheckForCollectionProblemsRequest(Settings.getPictureCollection()));
                } catch (final FileNotFoundException ex) {

                    SwingUtilities.invokeLater(() -> {
                                LOGGER.log(Level.INFO, "FileNotFoundException: {0}", ex.getMessage());
                                JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                                        ex.getMessage(),
                                        GENERIC_ERROR,
                                        JOptionPane.ERROR_MESSAGE);
                                JpoEventBus.getInstance().post(new StartNewCollectionRequest());
                            }
                    );
                }
            }
        }.start();
    }

}
