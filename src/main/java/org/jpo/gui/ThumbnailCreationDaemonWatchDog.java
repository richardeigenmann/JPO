package org.jpo.gui;

import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.StartThumbnailCreationDaemonRequest;
/*
Copyright (C) 2025 Richard Eigenmann.
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
 * instance of this class will monitor whether we have run out of live ThumbnailCreationDaemons
 */
public class ThumbnailCreationDaemonWatchDog implements Runnable {

    @Override
    public void run() {
        while (true) {
            var missingDaemons = Settings.getDefaultThumbnailCreationThreads() - ThumbnailCreationExecutor.getInstance().getLiveDaemonsCount();
            for (var i = missingDaemons; i > 0; i--) {
                JpoEventBus.getInstance().post(new StartThumbnailCreationDaemonRequest());
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException _) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
