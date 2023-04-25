package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.Settings;

import javax.swing.*;
import java.io.File;

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

/**
 * Brings up the dialog after a file save and allows the saved collection to
 * be set as the default start up collection.
 */
public class AfterFileSaveHandler {
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
        panel.add(new JLabel(Settings.getJpoResources().getString("collectionSaveBody") + request.pictureCollection().getXmlFile().toString()));
        final var setAutoload = new JCheckBox(Settings.getJpoResources().getString("setAutoload"));
        if (Settings.getAutoLoad() != null && ((new File(Settings.getAutoLoad())).compareTo(request.pictureCollection().getXmlFile()) == 0)) {
            setAutoload.setSelected(true);
        }
        panel.add(setAutoload);
         /*JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                panel,
                Settings.getJpoResources().getString("collectionSaveTitle"),
                JOptionPane.INFORMATION_MESSAGE);*/

        JOptionPane jOptionPane = new JOptionPane(panel);
        final JDialog jDialog = new JDialog(Settings.getAnchorFrame(), Settings.getJpoResources().getString("collectionSaveTitle"));
        jDialog.setContentPane(jOptionPane);
        jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jDialog.setLocationRelativeTo(Settings.getAnchorFrame());
        jDialog.pack();
        // auto-close this dialog after 15 seconds if the user hasn't made a choice
        new Timer(15000, e -> jDialog.dispose()).start();
        jDialog.setVisible(true);

        if (setAutoload.isSelected()) {
            Settings.setAutoLoad(request.pictureCollection().getXmlFile().toString());
            Settings.writeSettings();
        }
    }

}
