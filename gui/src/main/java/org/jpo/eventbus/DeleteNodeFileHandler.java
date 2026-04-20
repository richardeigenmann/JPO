package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jetbrains.annotations.TestOnly;
import org.jpo.datamodel.PictureInfo;
import org.jpo.gui.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.gui.JpoResources;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

/**
 * Handles the request to delete the picture files of nodes
 */
@EventHandler
public class DeleteNodeFileHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(DeleteNodeFileHandler.class.getName());

    /**
     * Title for error dialogs
     */
    private static final String GENERIC_ERROR = JpoResources.getResource("genericError");


    /**
     * Deletes the file and the node
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final DeleteNodeFileRequest request) {
        final var textArea = new JTextArea();
        textArea.setText(getFilenames(request));
        textArea.append("\n");
        textArea.append(JpoResources.getResource("areYouSure"));

        final int option = JOptionPane.showConfirmDialog(
                Settings.getAnchorFrame(),
                textArea,
                JpoResources.getResource("FileDeleteLabel"),
                JOptionPane.OK_CANCEL_OPTION);

        if (option == 0) {
            for (final var node : request.nodes()) {
                try {
                    deleteNodeAndFile(node);
                } catch (final IOException e) {
                    LOGGER.log(Level.INFO, "File deleted failed on: {0} Exception: {1}", new Object[]{node, e.getMessage()});
                    JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                            JpoResources.getResource("fileDeleteError") + node,
                            GENERIC_ERROR,
                            JOptionPane.ERROR_MESSAGE);
                }
            }
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
        if (node.getUserObject() instanceof PictureInfo pictureInfo) {
            final var highresFile = pictureInfo.getImageFile();
            if (highresFile.exists()) {
                Files.delete(highresFile.toPath());
            }
        }
        node.removeFromParent();
    }



    private String getFilenames(final DeleteNodeFileRequest request) {
        return  request
        .nodes()
        .stream()
        .filter(node -> node.getUserObject() instanceof PictureInfo)
        .map(node -> ((PictureInfo) node.getUserObject()).getImageFile().toString() )
        .collect(Collectors.joining("\n"));
    }

}
