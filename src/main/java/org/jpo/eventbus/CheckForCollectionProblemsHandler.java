package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;

import javax.swing.*;
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
 * Checks the loaded collection for problems and suggests measures
 */
public class CheckForCollectionProblemsHandler {


    private static final Logger LOGGER = Logger.getLogger(CheckForCollectionProblemsHandler.class.getName());

    /**
     * Checks the loaded collection for problems and suggests measures
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final CheckForCollectionProblemsRequest request) {
        var missingSha256Count = Settings.getPictureCollection().getRootNode()
                .getChildPictureNodesDFS()
                .filter(e -> ((PictureInfo) e.getUserObject()).getSha256().equals(""))
                .count();

        SwingUtilities.invokeLater(() -> {
            var choice = JOptionPane.showConfirmDialog(Settings.getAnchorFrame(),
                    String.format("%d SHA256 hash codes missing in collection. Choose Yes to repair", missingSha256Count),
                    "WARNING",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                LOGGER.log(Level.INFO, "Correct NOW!");
            } else {
                LOGGER.log(Level.INFO, "No Correct !");
            }
        });


    }


}
