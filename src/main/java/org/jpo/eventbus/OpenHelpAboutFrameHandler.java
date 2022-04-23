package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
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


public class OpenHelpAboutFrameHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(OpenHelpAboutFrameHandler.class.getName());


    /**
     * Opens the Help About window
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final OpenHelpAboutFrameRequest request) {
        JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                Settings.getJpoResources().getString("HelpAboutText")
                        + Settings.getJpoResources().getString("HelpAboutUser") + System.getProperty("user.name") + "\n"
                        + Settings.getJpoResources().getString("HelpAboutOs") + System.getProperty("os.name")
                        + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch") + "\n"
                        + Settings.getJpoResources().getString("HelpAboutJvm")
                        + System.getProperty("java.vendor") + " " + System.getProperty("java.version") + "\n"
                        + Settings.getJpoResources().getString("HelpAboutJvmMemory")
                        + Long.toString(Runtime.getRuntime().maxMemory() / 1024 / 1024, 0) + " MB\n"
                        + Settings.getJpoResources().getString("HelpAboutJvmFreeMemory")
                        + Long.toString(Runtime.getRuntime().freeMemory() / 1024 / 1024, 0) + " MB\n"
                        + "Cores: " + Runtime.getRuntime().availableProcessors() + "\n");

        // while we're at it dump the stuff to the log
        LOGGER.info("HelpAboutWindow: Help About showed the following information");
        LOGGER.log(Level.INFO, "User: {0}", System.getProperty("user.name"));
        LOGGER.log(Level.INFO, "Operating System: {0}  {1}", new Object[]{System.getProperty("os.name"), System.getProperty("os.version")});
        LOGGER.log(Level.INFO, "Java: {0}", System.getProperty("java.version"));
        LOGGER.log(Level.INFO, "Max Memory: {0} MB", Long.toString(Runtime.getRuntime().maxMemory() / 1024 / 1024, 0));
        LOGGER.log(Level.INFO, "Free Memory: {0} MB", Long.toString(Runtime.getRuntime().freeMemory() / 1024 / 1024, 0));
        LOGGER.log(Level.INFO, "Cores: {0}", Runtime.getRuntime().availableProcessors());
    }

}
