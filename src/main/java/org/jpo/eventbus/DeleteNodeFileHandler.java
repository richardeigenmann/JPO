package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jetbrains.annotations.TestOnly;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2022  Richard Eigenmann.
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
 * Handles the request to delete the files of a node
 */
public class DeleteNodeFileHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(DeleteNodeFileHandler.class.getName());

    /**
     * Title for error dialogs
     */
    private static final String GENERIC_ERROR = Settings.getJpoResources().getString("genericError");

    /**
     * Deletes the file and the node
     *
     * @param request the request the request
     */
    @Subscribe
    public void handleEvent(final DeleteNodeFileRequest request) {
        request
                .nodes()
                .stream()
                .filter(e -> e.getUserObject() instanceof PictureInfo)
                .forEach(this::deletePictureInfo);
    }

    /**
     * Deletes the file and the node
     *
     * @param node the node to delete
     */
    void deletePictureInfo(final SortableDefaultMutableTreeNode node) {
        var pictureInfo = (PictureInfo) node.getUserObject();
        final var highresFile = pictureInfo.getImageFile();
        final int option = JOptionPane.showConfirmDialog(
                Settings.getAnchorFrame(),
                Settings.getJpoResources().getString("FileDeleteLabel") + highresFile + "\n" + Settings.getJpoResources().getString("areYouSure"),
                Settings.getJpoResources().getString("FileDeleteTitle"),
                JOptionPane.OK_CANCEL_OPTION);

        if (option == 0 && highresFile.exists()) {
            try {
                deleteNodeAndFile(node);
            } catch (IOException e) {
                LOGGER.log(Level.INFO, "File deleted failed on file {0}: {1}", new Object[]{highresFile, e.getMessage()});
                JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                        Settings.getJpoResources().getString("fileDeleteError") + highresFile + e.getMessage(),
                        GENERIC_ERROR,
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /**
     * Deletes the file and the node
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final DeleteMultiNodeFileRequest request) {
        final var textArea = new JTextArea();
        textArea.setText(getFilenames(request.nodes()));
        textArea.append(Settings.getJpoResources().getString("areYouSure"));

        final int option = JOptionPane.showConfirmDialog(
                Settings.getAnchorFrame(), //very annoying if the main window is used as it forces itself into focus.
                textArea,
                Settings.getJpoResources().getString("FileDeleteLabel"),
                JOptionPane.OK_CANCEL_OPTION);

        if (option == 0) {
            for (final SortableDefaultMutableTreeNode selectedNode : request.nodes()) {
                try {
                    deleteNodeAndFile(selectedNode);
                } catch (final IOException e) {
                    LOGGER.log(Level.INFO, "File deleted failed on: {0} Exception: {1}", new Object[]{selectedNode, e.getMessage()});
                    JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                            Settings.getJpoResources().getString("fileDeleteError") + selectedNode.toString(),
                            GENERIC_ERROR,
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            Settings.getPictureCollection().clearSelection();
        }
    }

    /**
     * Don't use: public accessor to private deleteNodeAndFileTest method for unit tests.
     *
     * @param node the node to test
     * @throws IOException can throw
     */
    @TestOnly
    public static void deleteNodeAndFileTest(final SortableDefaultMutableTreeNode node) throws IOException {
        deleteNodeAndFile(node);
    }

    private static void deleteNodeAndFile(final SortableDefaultMutableTreeNode node) throws IOException {
        if (node.getUserObject() instanceof PictureInfo pi) {
            final var highresFile = pi.getImageFile();
            if (highresFile.exists()) {
                Files.delete(highresFile.toPath());
            }
        }
        node.deleteNode();
    }


    private String getFilenames(final Collection<SortableDefaultMutableTreeNode> nodes) {
        final var sb = new StringBuilder();
        for (final SortableDefaultMutableTreeNode selectedNode : nodes) {
            if (selectedNode.getUserObject() instanceof PictureInfo pictureInfo) {
                sb.append(pictureInfo.getImageLocation() + "\n");
            }
        }
        return sb.toString();
    }

}
