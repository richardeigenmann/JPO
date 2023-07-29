package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.Settings;
import org.jpo.gui.swing.MainWindow;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

@EventHandler
public class OpenMainWindowHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(OpenMainWindowHandler.class.getName());

    /**
     * Opens the MainWindow on the EDT thread by constructing a {@link MainWindow}. We then fire a
     * {@link LoadDockablesPositionsRequest}. We connect the picture collection with the {@link OpenMainWindowHandler.MainAppModelListener}
     *
     * @param request The request
     * @see MainWindow
     * @see LoadDockablesPositionsRequest
     * @see OpenMainWindowHandler.MainAppModelListener
     */
    @Subscribe
    public void handleEvent(final OpenMainWindowRequest request) {
        try {
            SwingUtilities.invokeAndWait(
                    () -> {
                        new MainWindow();
                        JpoEventBus.getInstance().post(new LoadDockablesPositionsRequest());
                        Settings.getPictureCollection().addTreeModelListener(new OpenMainWindowHandler.MainAppModelListener());
                    }
            );
        } catch (final InterruptedException | InvocationTargetException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Inner class that monitors the collection for changes and figures out
     * whether the root node changed and asks the application to change the
     * title of the Window accordingly
     */
    private static class MainAppModelListener
            implements TreeModelListener {

        @Override
        public void treeNodesChanged(final TreeModelEvent e) {
            final var tp = e.getTreePath();
            LOGGER.log(Level.FINE, "The main app model listener trapped a tree node change event on the tree path: {0}", tp);
            if (tp.getPathCount() == 1) { //if the root node sent the event
                LOGGER.fine("Since this is the root node we will update the ApplicationTitle");

                updateApplicationTitle();
            }
        }

        @Override
        public void treeNodesInserted(final TreeModelEvent e) {
            // ignore
        }

        @Override
        public void treeNodesRemoved(final TreeModelEvent e) {
            // ignore, the root can't be removed ... Really?
        }

        @Override
        public void treeStructureChanged(final TreeModelEvent e) {
            final var treePath = e.getTreePath();
            if (treePath.getPathCount() == 1) { //if the root node sent the event
                updateApplicationTitle();
            }
        }

        /**
         * Sets the application title to the default title based on the
         * resource bundle string ApplicationTitle and the file name of the
         * loaded xml file if any.
         */
        private void updateApplicationTitle() {
            final var xmlFile = Settings.getPictureCollection().getXmlFile();
            if (xmlFile != null) {
                JpoEventBus.getInstance().post(new UpdateApplicationTitleRequest(Settings.getJpoResources().getString("ApplicationTitle") + ":  " + xmlFile));
            } else {
                JpoEventBus.getInstance().post(new UpdateApplicationTitleRequest(Settings.getJpoResources().getString("ApplicationTitle")));
            }
        }
    }


}
