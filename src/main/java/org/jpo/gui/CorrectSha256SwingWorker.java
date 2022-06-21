package org.jpo.gui;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.FileSaveRequest;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.StartNewCollectionHandler;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 IntegrityCheckerJFrame.java:  creates a frame and checks the integrity of the collection

 Copyright (C) 2022  Richard Eigenmann, Zurich, Switzerland
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

public class CorrectSha256SwingWorker extends SwingWorker<Integer, String> {

    private SortableDefaultMutableTreeNode startNode;

    public CorrectSha256SwingWorker(final SortableDefaultMutableTreeNode startNode) {
        this.startNode = startNode;
    }

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(CorrectSha256SwingWorker.class.getName());

    @Override
    protected Integer doInBackground() {

        final var privateFileSaveAsHandler = new PrivateFileSaveAsHandler();
        final var privateStartNewCollectionHandler = new PrivateStartNewCollectionHandler();
        JpoEventBus.getInstance().register(privateFileSaveAsHandler);
        JpoEventBus.getInstance().register(privateStartNewCollectionHandler);

        var missing = startNode.getChildPictureNodesDFS().filter(
                node -> {
                    final var pictureInfo = (PictureInfo) node.getUserObject();
                    return pictureInfo.getSha256().equals("");
                }).count();
        publish(String.format("%d PictureInfo objects have missing sha256 file hashes%n", missing));

        final var hashCodesFixed = new AtomicInteger();

        try {
            startNode.getChildPictureNodesDFS().filter(
                    node -> {
                        final var pictureInfo = (PictureInfo) node.getUserObject();
                        return pictureInfo.getSha256().equals("");
                    }).forEach(node -> {
                final var pictureInfo = (PictureInfo) node.getUserObject();
                pictureInfo.setSha256();
                hashCodesFixed.getAndIncrement();
                if (hashCodesFixed.get() % 50 == 0) {
                    publish(String.format("%d sha-256 hash codes populated out of %d nodes", hashCodesFixed.get(), missing));
                }
                if (stopTheJob.get()) {
                    throw new BreakException();
                }
            });
        } catch (BreakException e) {
            // OK, we need to stop here
        }

        JpoEventBus.getInstance().unregister(privateFileSaveAsHandler);
        JpoEventBus.getInstance().unregister(privateStartNewCollectionHandler);

        return hashCodesFixed.get();
    }

    @Override
    protected void process(final List<String> chunks) {
        chunks.forEach(e -> LOGGER.log(Level.INFO, e));
    }

    AtomicBoolean stopTheJob = new AtomicBoolean(false);

    private class PrivateFileSaveAsHandler {
        @Subscribe
        public void handleEvent(final FileSaveRequest request) {
            stopTheJob.set(true);
        }
    }

    private class PrivateStartNewCollectionHandler {
        @Subscribe
        public void handleEvent(final StartNewCollectionHandler request) {
            stopTheJob.set(true);
        }
    }

    private class BreakException extends RuntimeException {
    }

}
