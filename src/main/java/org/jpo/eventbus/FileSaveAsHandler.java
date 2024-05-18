package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;
import org.jpo.gui.XmlFilter;

import javax.swing.*;

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
 * Handler for the FileSaveAsRequest request
 */
@EventHandler
public class FileSaveAsHandler {
    /**
     * method that saves the entire index in XML format. It prompts for the
     * filename first.
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final FileSaveAsRequest request) {
        final var jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        jFileChooser.setDialogTitle(Settings.getJpoResources().getString("fileSaveAsTitle"));
        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.setFileFilter(new XmlFilter());
        if (request.pictureCollection().getXmlFile() != null) {
            jFileChooser.setCurrentDirectory(request.pictureCollection().getXmlFile());
        } else {
            jFileChooser.setCurrentDirectory(Settings.getMostRecentCopyLocation());
        }

        final var returnVal = jFileChooser.showSaveDialog(Settings.getAnchorFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            var chosenFile = jFileChooser.getSelectedFile();
            chosenFile = Tools.correctFilenameExtension("xml", chosenFile);
            if (chosenFile.exists()) {
                int answer = JOptionPane.showConfirmDialog(Settings.getAnchorFrame(),
                        Settings.getJpoResources().getString("confirmSaveAs"),
                        Settings.getJpoResources().getString("genericWarning"),
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (answer == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }

            request.pictureCollection().setXmlFile(chosenFile);
            request.pictureCollection().fileSave();

            Settings.memorizeCopyLocation(chosenFile.getParent());
            JpoEventBus.getInstance().post(new CopyLocationsChangedEvent());
            Settings.pushRecentCollection(chosenFile.toString());
            JpoEventBus.getInstance().post(new RecentCollectionsChangedEvent());
            JpoEventBus.getInstance().post(new AfterFileSaveRequest(request.pictureCollection()));
            if (request.onSuccessNextRequest() != null) {
                JpoEventBus.getInstance().post(request.onSuccessNextRequest());
            }
        }
    }

}
