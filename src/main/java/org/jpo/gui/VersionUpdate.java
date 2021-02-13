package org.jpo.gui;

import org.jetbrains.annotations.TestOnly;
import org.jpo.datamodel.Settings;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.awt.event.ItemEvent.SELECTED;

/*
 Copyright (C) 2021  Richard Eigenmann.
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
 * This class can figure out if the current JPO version is out of date
 * and offers to open the download page in the browser for the user
 * to upgrade.
 */
public class VersionUpdate {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(VersionUpdate.class.getName());


    /**
     * Constructs an object that checks the latest version and then pops up a dialog
     * offering to that the user to the download page if the version is out of date.
     */
    public VersionUpdate() {
        try {
            final String latestVersion = getLatestJpoVersion();
            if (Float.valueOf(latestVersion) > Float.valueOf(Settings.JPO_VERSION)) {
                EventQueue.invokeLater(() -> showOutOfDateDialog(latestVersion));
            }
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Could not determine latest version: {0}", e.getMessage());
        }
    }

    /**
     * Reads a String document from the supplied URL
     *
     * @param requestURL The URL to read from
     * @return The contents of URL as a String
     * @throws IOException if something went wrong.
     */
    private static String readFromURL(final String requestURL) throws IOException {
        try (final Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    /**
     * Reads the latest version number from the special Json document on the Jpo homepage.
     *
     * @return The latest version number of JPO
     * @throws IOException If something went wrong
     */
    private static String getLatestJpoVersion() throws IOException {
        final String jsonData = readFromURL(Settings.JPO_VERSION_URL);
        LOGGER.log(Level.INFO, jsonData);
        final JSONObject obj = new JSONObject(jsonData);
        return obj.getString("currentVersion");
    }

    @TestOnly
    public static String getLatestJpoVersionTestOnly() throws IOException {
        return getLatestJpoVersion();
    }


    /**
     * This method checks the Settings to determine if we are allowed to check for version updates. This
     * is only allowed if the ignoreVersionAlerts flag is not set and if that is OK, then we must not
     * check before the snooze time has expired.
     *
     * @return true if we may check for updates, false if not
     */
    public static boolean mayCheckForUpdates() {
        return !Settings.isIgnoreVersionAlerts() && LocalDateTime.now().isAfter(Settings.getSnoozeVersionAlertsExpiryDateTime());
    }

    private void showOutOfDateDialog(final String latestVersion) {
        final String outdatedMessage = String.format(
                Settings.getJpoResources().getString("VersionUpdate.outdatedMessage"),
                Settings.JPO_VERSION, latestVersion, Settings.JPO_DOWNLOAD_URL, Settings.JPO_DOWNLOAD_URL);
        final JEditorPane ep = new JEditorPane("text/html", outdatedMessage);

        ep.addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
                openDownloadLinkInBrowser();
        });
        ep.setEditable(false);
        ep.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

        final JCheckBox snoozeJCheckBox = new JCheckBox(Settings.getJpoResources().getString("VersionUpdate.snoozeJCheckBox"));

        ep.setFont(snoozeJCheckBox.getFont());

        snoozeJCheckBox.addItemListener(snoozeListener -> {
            final LocalDateTime now = LocalDateTime.now();
            final LocalDateTime fornight = now.plusDays(14);
            if (snoozeListener.getStateChange() == SELECTED) {
                Settings.setSnoozeVersionAlertsExpiryDateTime(fornight);
            } else {
                Settings.setSnoozeVersionAlertsExpiryDateTime(now);
            }
        });

        snoozeJCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JCheckBox neverShowJCheckBox = new JCheckBox(Settings.getJpoResources().getString("VersionUpdate.neverShowJCheckBox"));
        neverShowJCheckBox.addItemListener(neverShowListener -> Settings.setIgnoreVersionAlerts(neverShowListener.getStateChange() == SELECTED));

        final JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        ep.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(ep);
        content.add(snoozeJCheckBox);
        content.add(neverShowJCheckBox);

        int choice = JOptionPane.showConfirmDialog(Settings.getAnchorFrame(), content, "Title",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == 0) {
            openDownloadLinkInBrowser();
        }
    }

    /**
     * Opens the JPO Download page in the system browser
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    private void openDownloadLinkInBrowser() {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(Settings.JPO_DOWNLOAD_URL));
            } catch (final IOException | URISyntaxException e) {
                LOGGER.log(Level.SEVERE, "Could not open JPO download link in browser: {0}", e.getMessage());
            }
        }
    }
}
