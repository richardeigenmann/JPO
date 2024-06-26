package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.Settings;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * Brings up the dialog after a file save and allows the saved collection to
 * be set as the default start up collection.
 */
@EventHandler
public class AfterFileSaveHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(AfterFileSaveHandler.class.getName());

    /**
     * Brings up the dialog after a file save and allows the saved collection to
     * be set as the default start up collection.
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final AfterFileSaveRequest request) {
        final var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel(Settings.getJpoResources().getString("collectionSaveBody") + request.pictureCollection().getXmlFile().toString()));
        final var autoLoadJCheckBox = new JCheckBox(Settings.getJpoResources().getString("setAutoload"));
        if (Settings.getAutoLoad() != null && ((new File(Settings.getAutoLoad())).compareTo(request.pictureCollection().getXmlFile()) == 0)) {
            autoLoadJCheckBox.setSelected(true);
        }
        panel.add(autoLoadJCheckBox);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        final var jDialog = new JDialog(Settings.getAnchorFrame(), Settings.getJpoResources().getString("collectionSaveTitle"));

        final var okButton = new JButton("OK");
        okButton.addActionListener(e -> closeAfterFileSaveDialog(request, autoLoadJCheckBox, jDialog));
        final var buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        panel.add(buttonPanel);

        jDialog.setContentPane(panel);
        jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jDialog.setLocationRelativeTo(Settings.getAnchorFrame());
        jDialog.pack();
        jDialog.setVisible(true);

        // auto-close this dialog after 15 seconds if the user hasn't made a choice
        final var timer = new Timer(15000, e -> closeAfterFileSaveDialog(request, autoLoadJCheckBox, jDialog));
        timer.setRepeats(false);
        timer.start();


    }

    private static void closeAfterFileSaveDialog(final AfterFileSaveRequest request, final JCheckBox autoLoadJCheckBox, final JDialog jDialog) {
        if (autoLoadJCheckBox.isSelected()) {
            LOGGER.log(Level.INFO, "Setting JPO Autostart to load file: {0}", request.pictureCollection().getXmlFile() );
            Settings.setAutoLoad(request.pictureCollection().getXmlFile().toString());
            Settings.writeSettings();
        }
        jDialog.dispose();
    }

}
