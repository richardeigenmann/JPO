package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.gui.Settings;
import org.jpo.datamodel.Tools;
import org.jpo.gui.JpoResources;

import javax.swing.*;

/*
 Copyright (C) 2023-2025 Richard Eigenmann.
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
public class UnsavedUpdatesDialogHandler {

    /**
     * Brings the unsaved updates dialog if there are unsaved updates and then
     * fires the next request. Logic is: if unsavedChanges then show dialog
     * submit next request
     * <p>
     * The dialog has choices:
     * 0 : discard unsaved changes and go to next request
     * 1 : fire save request then send next request
     * 2 : fire save-as request then send next request
     * 3 : cancel - don't proceed with next request
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final UnsavedUpdatesDialogRequest request) {
        Tools.checkEDT();

        // a good time to save the window coordinates
        JpoEventBus.getInstance().post(new SaveDockablesPositionsRequest());

        if (Settings.getPictureCollection().getUnsavedUpdates()) {
            final Object[] options = {
                    JpoResources.getResource("discardChanges"),
                    JpoResources.getResource("genericSaveButtonLabel"),
                    JpoResources.getResource("FileSaveAsMenuItemText"),
                    JpoResources.getResource("genericCancelText")};
            int option = JOptionPane.showOptionDialog(
                    Settings.getAnchorFrame(),
                    JpoResources.getResource("unsavedChanges"),
                    JpoResources.getResource("genericWarning"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (option) {
                // discard
                case 0 -> JpoEventBus.getInstance().post(request.nextRequest());
                // save
                case 1 -> {
                    final var fileSaveRequest = new FileSaveRequest(request.pictureCollection(), request.nextRequest());
                    JpoEventBus.getInstance().post(fileSaveRequest);
                }
                // save as
                case 2 -> {
                    final var fileSaveAsRequest = new FileSaveAsRequest(request.pictureCollection(), request.nextRequest());
                    JpoEventBus.getInstance().post(fileSaveAsRequest);
                }
                // cancel
                default -> {
                    // do a cancel if no other option was chosen
                }
            }
        } else {
            JpoEventBus.getInstance().post(request.nextRequest());
        }

    }
}
