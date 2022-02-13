package org.jpo.gui;

import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.datamodel.Tools;
import org.jpo.eventbus.PictureAdderRequest;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
PictureAdder.java:  A Class which brings up a progress bar and adds pictures to the specified node.

Copyright (C) 2009-2022  Richard Eigenmann.
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
 * A Class which brings up a progress bar and adds pictures to the specified node.
 *
 * @author Richard Eigenmann
 */
public class PictureAdder
        extends SwingWorker<Integer, Integer> {

    /**
     * Constructor
     *
     * @param request The request to fulfill
     */
    public PictureAdder(final PictureAdderRequest request) {
        this.request = request;
        LOGGER.log(Level.FINE, "Invoked for node: {0}, with {1} files, newOnly: {2}, recurseDirectories: {3}, retainDirectories: {4}", new Object[]{request.startNode(), request.chosenFiles().length, request.newOnly(), request.recurseDirectories(), request.retainDirectories()});
        progGui = new ProgressGui(Tools.countFiles(request.chosenFiles()),
                Settings.getJpoResources().getString("PictureAdderProgressDialogTitle"),
                Settings.getJpoResources().getString("picturesAdded"));
        Settings.getPictureCollection().setSendModelUpdates(false);
    }

    /**
     * The picture add request
     */
    private final PictureAdderRequest request;

    /**
     * A Progress Gui with a cancel button.
     */
    private final ProgressGui progGui;


    /**
     * Adds the indicated files to the current node if they are valid pictures. If the newOnly
     * Flag is on then the collection is checked to see if the picture is already present. It
     * also opens a progress Gui to provide feedback to the user.
     *
     * @return A string
     */
    @Override
    public Integer doInBackground() {
        // add all the files from the array as nodes to the start node.
        //for (int i = 0; (i < request.chosenFiles().length) && (!progGui.getInterruptSemaphore().getShouldInterrupt()); i++) {
        //final var addFile = request.chosenFiles()[i];
        for (final var addFile : request.chosenFiles()) {
            if (progGui.getInterruptSemaphore().getShouldInterrupt()) {
                break;
            }
            LOGGER.log(Level.INFO, "Considering file: {0}", addFile);
            if (!addFile.isDirectory()) {
                addPicture(request.startNode(), addFile);
            } else {
                // the file is a directory
                if (Tools.hasPictures(addFile)) {
                    addDirectory(request.startNode(), addFile);
                } else {
                    LOGGER.log(Level.FINE, "No pictures in directory: {0}", addFile);
                }
            }

        }
        return 1;
    }


    /**
     * method that is invoked recursively on each directory encountered. It adds
     * a new group to the tree and then adds all the pictures found therein to that
     * group. The ImageIO.getImageReaders method is queried to see whether a reader
     * exists for the image that is attempted to be loaded.
     *
     * @param parentNode the node to which to add
     * @param dir        the directory to add
     */
    private void addDirectory(final SortableDefaultMutableTreeNode parentNode, final File dir) {
        final SortableDefaultMutableTreeNode directoryNode;
        if (request.retainDirectories()) {
            directoryNode = new SortableDefaultMutableTreeNode(new GroupInfo(dir.getName()));
            parentNode.add(directoryNode);
            Settings.getPictureCollection().setUnsavedUpdates();
        } else {
            directoryNode = parentNode;
        }

        final File[] fileArray = dir.listFiles();
        if (fileArray != null) {
            for (int i = 0; (i < fileArray.length) && (!progGui.getInterruptSemaphore().getShouldInterrupt()); i++) {
                if (fileArray[i].isDirectory() && request.recurseDirectories() && Tools.hasPictures(fileArray[i])) {
                    addDirectory(directoryNode, fileArray[i]);
                } else {
                    addPicture(directoryNode, fileArray[i]);
                }
            }
        }
        removeEmptyDirectoryNode(directoryNode);
    }

    private void addPicture(final SortableDefaultMutableTreeNode groupNode, final File file) {
        if (groupNode.addSinglePicture(file, request.newOnly(), request.selectedCategories())) {
            publish(1);
        } else {
            publish(-1);
        }
    }

    private void removeEmptyDirectoryNode(final SortableDefaultMutableTreeNode directoryNode) {
        // it can happen that we end up adding no pictures and could be returning a new empty group
        if (request.retainDirectories() && (directoryNode.getChildCount() == 0)) {
            directoryNode.deleteNode();
        }
    }


    /**
     * The Swing Worker sends the publish() events here on the EDT when it feels like it.
     *
     * @param chunks Send 1 to increment the count of pictures processed, -1 to decrement the total
     */
    @Override
    protected void process(final List<Integer> chunks) {
        chunks.forEach(i -> {
            if (i > 0) {
                progGui.progressIncrement();
            } else {
                progGui.decrementTotal();
            }
        });
    }


    /**
     * Sends a model notification about the change and updates the cancel button to an OK button
     */
    @Override
    protected void done() {
        Settings.getPictureCollection().setSendModelUpdates(true);
        Settings.getPictureCollection().sendNodeStructureChanged(request.startNode());
        progGui.switchToDoneMode();
    }

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(PictureAdder.class.getName());
}
