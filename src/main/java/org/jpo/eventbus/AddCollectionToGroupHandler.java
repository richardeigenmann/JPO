package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;
import org.jpo.gui.JpoResources;
import org.jpo.gui.swing.LabelFrame;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import static org.jpo.datamodel.SortableDefaultMutableTreeNode.GENERIC_ERROR;

/*
 Copyright (C) 2023-2024 Richard Eigenmann.
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
 * Adds a collection to a group node
 */
@EventHandler
public class AddCollectionToGroupHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(AddCollectionToGroupHandler.class.getName());


    /**
     * Handles the request to add a collection supplied as a file to the
     * supplied group node
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final AddCollectionToGroupRequest request) {
        Tools.checkEDT();
        final var popupNode = request.node();
        final var fileToLoad = request.collectionFile();

        final var newNode = popupNode.addGroupNode("New Group");
        try {
            final var loadProgressGui = new LabelFrame(JpoResources.getResource("org.jpo.dataModel.XmlReader.loadProgressGuiTitle"));
            PictureCollection.fileLoad(
                    fileToLoad, 
                    newNode,
                    loadProgressGui,
                    () -> JpoEventBus.getInstance().post(new CollectionLockNotification(newNode.getPictureCollection())));
        } catch (final FileNotFoundException x) {
            LOGGER.severe(x.getMessage());
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    "File not found:\n" + fileToLoad.getPath(),
                    GENERIC_ERROR,
                    JOptionPane.ERROR_MESSAGE);
        }
        newNode.getPictureCollection().setUnsavedUpdates(true);
        JpoEventBus.getInstance().post(new ShowGroupRequest(newNode));
    }

}
