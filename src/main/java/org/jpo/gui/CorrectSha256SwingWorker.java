package org.jpo.gui;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.FileSaveRequest;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.StartNewCollectionHandler;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
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

/**
 * Looks for missing SHA256 code and corrects them.
 */
public class CorrectSha256SwingWorker extends SwingWorker<Integer, String> {

    private SortableDefaultMutableTreeNode startNode;


    final PrivateFileSaveAsHandler privateFileSaveAsHandler = new PrivateFileSaveAsHandler();
    final PrivateStartNewCollectionHandler privateStartNewCollectionHandler = new PrivateStartNewCollectionHandler();


    private final JProgressBar progressBar = new JProgressBar();


    final JFrame frame = new JFrame();

    public CorrectSha256SwingWorker(final SortableDefaultMutableTreeNode startNode) {
        this.startNode = startNode;

        final var jPanel = new JPanel();
        jPanel.setLayout( new BoxLayout( jPanel, BoxLayout.PAGE_AXIS ) );

        jPanel.add(new JLabel("Creating hash codes..."));


        var picsToProcess = startNode.getChildPictureNodesDFS().filter(
                node -> {
                    final var pictureInfo = (PictureInfo) node.getUserObject();
                    return pictureInfo.getSha256().equals("");
                }).count();
        publish(String.format("%d PictureInfo objects have missing sha256 file hashes%n", picsToProcess));

        progressBar.setMinimum( 0 );
        progressBar.setMaximum( (int) picsToProcess );
        progressBar.setStringPainted( true );
        jPanel.add( progressBar );
        final var stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> stopTheJob.set(true));
        jPanel.add( stopButton );

        frame.getContentPane().add(jPanel);
        frame.setLocationRelativeTo(Settings.getAnchorFrame());
        frame.pack();
        frame.setVisible(true);

        JpoEventBus.getInstance().register(privateFileSaveAsHandler);
        JpoEventBus.getInstance().register(privateStartNewCollectionHandler);
    }

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(CorrectSha256SwingWorker.class.getName());

    @Override
    protected Integer doInBackground() {
        try {
            startNode
                .getChildPictureNodesDFS()
                .filter( node -> {
                    final var pictureInfo = (PictureInfo) node.getUserObject();
                    return pictureInfo.getSha256().equals("");
                })
                .forEach(node -> {
                    final var pictureInfo = (PictureInfo) node.getUserObject();
                    pictureInfo.setSha256();
                    publish(pictureInfo.getImageLocation());
                    if (stopTheJob.get()) {
                        throw new BreakException();
                    }
                });
        } catch (BreakException e) {
            // OK, we need to stop here
        }

        return null;
    }

    @Override
    protected void process(final List<String> chunks) {
        chunks.forEach(e -> LOGGER.log(Level.INFO, e));
        SwingUtilities.invokeLater(
                () -> progressBar.setValue(progressBar.getValue() + chunks.size())
        );
    }

    /**
     * The Swing Worked calls this method when done.
     */
    @Override
    protected void done() {
        progressBar.setValue(progressBar.getMaximum());
        JpoEventBus.getInstance().unregister(privateFileSaveAsHandler);
        JpoEventBus.getInstance().unregister(privateStartNewCollectionHandler);
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
