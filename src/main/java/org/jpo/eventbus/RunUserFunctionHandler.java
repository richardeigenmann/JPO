package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;

import java.io.IOException;
import java.net.MalformedURLException;
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
public class RunUserFunctionHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(RunUserFunctionHandler.class.getName());


    /**
     * Handles the RunUserFunctionRequest request
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final RunUserFunctionRequest request) {
        try {
            request
                    .nodes()
                    .stream()
                    .filter(e -> e.getUserObject() instanceof PictureInfo )
                    .forEach(e -> runUserFunction(request.userFunctionIndex(), ((PictureInfo) e.getUserObject())));
        } catch (ClassCastException | NullPointerException x) {
            LOGGER.severe(x.getMessage());
        }

    }


    /**
     * This method fires up a user function if it can. User functions are only
     * valid on PictureInfo nodes.
     *
     * @param userFunction The user function to be executed in the array
     *                     Settings.userFunctionCmd
     * @param myObject     The PictureInfo upon which the user function should be
     *                     executed.
     */
    private static void runUserFunction(final int userFunction, final PictureInfo myObject) {
        if ((userFunction < 0) || (userFunction >= Settings.MAX_USER_FUNCTIONS)) {
            LOGGER.info("Error: called with an out of bounds index");
            return;
        }
        String command = Settings.getUserFunctionCmd()[userFunction];
        if ((command == null) || (command.isEmpty())) {
            LOGGER.log(Level.INFO, "Command {0} is not properly defined", Integer.toString(userFunction));
            return;
        }

        final var filename = (myObject).getImageFile().toString();
        command = command.replace("%f", filename);

        final var escapedFilename = filename.replaceAll("\\s", "\\\\\\\\ ");
        command = command.replace("%e", escapedFilename);

        try {
            final var pictureURL = myObject.getImageFile().toURI().toURL();
            command = command.replace("%u", pictureURL.toString());
        } catch (final MalformedURLException x) {
            LOGGER.log(Level.SEVERE, "Could not substitute %u with the URL: {0}", x.getMessage());
            return;
        }


        LOGGER.log(Level.INFO, "Command to run is: {0}", command);
        try {
            // Had big issues here because the simple exec (String) calls a StringTokenizer
            // which messes up the filename parameters
            final var blank = command.indexOf(' ');
            final String[] cmdarray;
            if (blank > -1) {
                cmdarray = new String[2];
                cmdarray[0] = command.substring(0, blank);
                cmdarray[1] = command.substring(blank + 1);
            } else {
                cmdarray = new String[1];
                cmdarray[0] = command;
            }
            Runtime.getRuntime().exec(cmdarray);
        } catch (final IOException x) {
            LOGGER.log(Level.INFO, "Runtime.exec collapsed with and IOException: {0}", x.getMessage());
        }
    }
}
