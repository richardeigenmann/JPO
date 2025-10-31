package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.JpoWriter;
import org.jpo.gui.JpoResources;
import org.jpo.gui.Settings;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

/*
 Copyright (C) 2022-2025 Richard Eigenmann.
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
public class ExportGroupToCollectionHandler {
    /**
     * Fulfill the export to new collection request
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final ExportGroupToCollectionRequest request) {
        new SwingWorker<Void, Void>() {
            /**
             * Runs the long-running task off the EDT.
             */
            @Override
            protected Void doInBackground() throws Exception {
                // The actual blocking I/O operation
                JpoWriter.write(request);
                return null;
            }

            /**
             * Executes on the Event Dispatch Thread (EDT) after doInBackground() completes.
             * This is where UI updates (like JOptionPane) must occur.
             */
            @Override
            protected void done() {
                try {
                    // Calling get() checks the result. If write(request) threw an exception,
                    // get() wraps and re-throws it as an ExecutionException.
                    get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore the interrupt status
                    showError(e);
                } catch (ExecutionException e) {
                    // FAILURE: The exception thrown by write(request) is caught here
                    // e.getCause() gives you the original exception
                    showError(e.getCause());
                }
            }

            /**
             * Helper to safely display the error message on the EDT.
             */
            private void showError(Throwable e) {
                JOptionPane.showMessageDialog(
                        Settings.getAnchorFrame(),
                        e.getMessage(),
                        JpoResources.getResource("genericError"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }.execute(); // Execute starts the background thread


    }

}
