package org.jpo.gui;

import org.apache.commons.lang3.StringUtils;
import org.jpo.datamodel.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/*
 Copyright (C) 2017-2020 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 * GUI to remove old thumbnails that have been discovered when loading a
 * collection
 *
 * @author Richard Eigenmann
 */
public class ClearThumbnailsJFrame extends JFrame {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ClearThumbnailsJFrame.class.getName());

    /**
     * Creates new form ClearThumbnailsJFrame
     *
     * @param lowresUrls The lowres Urls
     */
    public ClearThumbnailsJFrame(final StringBuilder lowresUrls) {
        initComponents();
        setLocationRelativeTo(Settings.getAnchorFrame());
        this.lowresUrls.setText(lowresUrls.toString());
        stopButton.setVisible(false);
        closeButton.setVisible(false);
        setVisible(true);
    }

    private void initComponents() {
        final JTextArea jTextArea1 = new JTextArea();
        final JLabel jLabel1 = new JLabel();
        final JLabel jLabel2 = new JLabel();
        final JScrollPane jScrollPane2 = new JScrollPane();
        lowresUrls = new JTextArea();
        closeButton = new JButton();
        removeButton = new JButton();
        stopButton = new JButton();
        ignoreButton = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("JPO now has an improved cache for thumbnail pictures. The old thumbnail images should be removed.");
        jTextArea1.setFocusable(false);

        jLabel1.setFont(new Font("Dialog", Font.BOLD, 24)); // NOI18N
        jLabel1.setText("Remove Old Thumbnails");

        jLabel2.setText("You will only be asked once!");

        lowresUrls.setColumns(20);
        lowresUrls.setRows(5);
        jScrollPane2.setViewportView(lowresUrls);

        closeButton.setText("Close");
        closeButton.addActionListener(this::closeButtonActionPerformed);

        removeButton.setText("Remove");
        removeButton.addActionListener(this::removeButtonActionPerformed);

        stopButton.setText("Stop");
        stopButton.addActionListener(this::stopButtonActionPerformed);

        ignoreButton.setText("Ignore");
        ignoreButton.addActionListener(this::closeButtonActionPerformed);

        final GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ignoreButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stopButton))
                    .addComponent(jScrollPane2)
                    .addComponent(jTextArea1))
                .addContainerGap())
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(325, 325, 325))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel1)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextArea1, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(closeButton)
                    .addComponent(removeButton)
                    .addComponent(stopButton)
                    .addComponent(ignoreButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE))
        );

        pack();
    }

    private void closeButtonActionPerformed(final ActionEvent evt) {
        setVisible(false);
        dispose();
    }

    private void removeButtonActionPerformed(final ActionEvent evt) {
        stopButton.setVisible(true);
        removeButton.setVisible(false);
        ignoreButton.setVisible(false);
        thumbnailRemover.execute();
    }

    private void stopButtonActionPerformed(ActionEvent evt) {
        thumbnailRemover.cancel(true);
    }

    private final transient ThumbnailRemover thumbnailRemover = new ThumbnailRemover();

    private JButton closeButton;
    private JButton ignoreButton;
    private JTextArea lowresUrls;
    private JButton removeButton;
    private JButton stopButton;

    private class ThumbnailRemover extends SwingWorker<Void, String> {

        private boolean firsttime = true;

        /**
         * Reads each line from the textarea and deletes the thumbnail file if
         * it exists and is writable. After the deletion the parent directory is
         * deleted too if it is empty and writable.
         *
         * @return nothing.
         */
        @Override
        protected Void doInBackground() {
            publish( "Log:\n" );
            final String[] lines = lowresUrls.getText().split(System.getProperty("line.separator"));
            for ( String line : lines ) {
                if (isCancelled()) {
                    publish("Removal cancelled.");
                    break;
                }

                line = StringUtils.chomp(line);
                if (!"".equals(line)) {
                    try {
                        deleteThumbnail(new File(new URI(line)));
                    } catch (final URISyntaxException ex) {
                        LOGGER.severe(ex.getLocalizedMessage());
                        publish(line + "   doesn't parse to a file --> nothing to delete --> OK\n");
                    }
                }
            }
            return null;
        }

        private void deleteThumbnail(final File thumbnail) {
            if (!thumbnail.exists()) {
                publish(thumbnail + "   doesn't exist. --> nothing to delete --> OK\n");
            } else if (!thumbnail.canWrite()) {
                publish(thumbnail + "   isn't modifiable --> Can't delete --> you have to delete this file yourself\n");
            } else {
                try {
                    Files.delete(thumbnail.toPath());
                    publish(thumbnail + "   successfully deleted.\n");
                } catch (final IOException e) {
                    publish(thumbnail + "   failed to delete --> you have to delete this file yourself" + e.getMessage() + "\n");
                }
                deleteParentDirectoryIfEmpty(thumbnail);
            }
        }

        private void deleteParentDirectoryIfEmpty(final File thumbnail) {
            // check if the parent directory is empty and writable and then delete it
            final File parentDirectory = thumbnail.getParentFile();
            if (parentDirectory != null && parentDirectory.canWrite() && Objects.requireNonNull(parentDirectory.list()).length == 0) {
                try {
                    Files.delete(parentDirectory.toPath());
                    publish(String.format("Parent directory %s successfully deleted%n", parentDirectory.toString()));
                } catch (final IOException e) {
                    publish(String.format("Parent directory %s failed to delete --> you have to delete this directory yourself%n", parentDirectory.toString()));
                }
            }
        }

        @Override
        protected void done() {
            stopButton.setVisible(false);
            closeButton.setVisible(true);
            if (!isCancelled()) {
                publish("\nRemoval complete.\n");
            }
        }

        @Override
        protected void process(final List<String> chunks) {
            if (firsttime) {
                lowresUrls.setText("");
                firsttime = false;
            }
            chunks.forEach(chunk -> lowresUrls.append(chunk));
        }

    }

}
