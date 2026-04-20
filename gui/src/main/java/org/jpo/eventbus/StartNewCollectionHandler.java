package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.gui.Settings;

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
public class StartNewCollectionHandler {
    /**
     * Clears the collection and starts a new one. Remember to wrap this request
     * in an UnsavedUpdatesDialogRequest if you care about unsaved changes as
     * this request will not check for unsaved changes
     *
     * @param event the event
     */
    @Subscribe
    public void handleEvent(final StartNewCollectionRequest event) {
        SwingUtilities.invokeLater(
                () -> {
                    Settings.getPictureCollection().clearCollection();
                    Settings.getRecentDropNodes().clear();
                    JpoEventBus.getInstance().post(new CollectionLockNotification(Settings.getPictureCollection()));
                    JpoEventBus.getInstance().post(new ShowGroupRequest(Settings.getPictureCollection().getRootNode()));
                }
        );
    }

}
