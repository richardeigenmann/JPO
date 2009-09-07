package jpo.gui;

import java.io.File;
import java.util.HashSet;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;

/*
PictureFileChooser.java:  a controller that brings up a filechooser and then adds the pictures


Copyright (C) 2009  Richard Eigenmann.
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
 * A Class which brings up a progress bar and adds pictures to the specified node.
 * @author Richard Eigenmann
 */
public class PictureAdder {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger(PictureFileChooser.class.getName());

    /**
     *  Adds the indicated files to the current node if they are valid pictures. If the newOnly
     *  Flag is on then the collection is checked to see if the picture is already present. It
     *  also opens a progress Gui to provide feedback to the user.
     *
     *  @param startNode
     *  @param chosenFiles
     *  @param newOnly indicates whether to check if the picture is already in the collection
     *  @param recurseDirectories  indicates whether to scan down into directories for more pictures.
     *  @param retainDirectories  indicates whether to preserve the directory structure.
     *  @param selectedCategories
     *  @return In case this is of interest to the caller we return here the node to be displayed; null if no pictures were added.
     */
    public static SortableDefaultMutableTreeNode addPictures(SortableDefaultMutableTreeNode startNode, File[] chosenFiles, boolean newOnly, boolean recurseDirectories, boolean retainDirectories, HashSet<Object> selectedCategories) {
        final ProgressGui progGui = new ProgressGui(Tools.countfiles(chosenFiles),
                Settings.jpoResources.getString("PictureAdderProgressDialogTitle"),
                Settings.jpoResources.getString("picturesAdded"));
        Settings.pictureCollection.setSendModelUpdates(false);

        SortableDefaultMutableTreeNode displayNode = null;
        SortableDefaultMutableTreeNode addedNode = null;

        // add all the files from the array as nodes to the start node.
        for (int i = 0; (i < chosenFiles.length) && (!progGui.getInterruptor().getShouldInterrupt()); i++) {
            File addFile = chosenFiles[i];
            if (!addFile.isDirectory()) {
                // the file is not a directory
                if (startNode.addSinglePicture(addFile, newOnly, selectedCategories)) {
                    Runnable r = new Runnable() {

                        public void run() {
                            progGui.progressIncrement();
                        }
                    };
                    SwingUtilities.invokeLater(r);
                } else {
                    // addSinglePicture failed
                    Runnable r = new Runnable() {

                        public void run() {
                            progGui.decrementTotal();
                        }
                    };
                    SwingUtilities.invokeLater(r);

                }
            } else {
                // the file is a directory
                if (Tools.hasPictures(addFile)) {
                    addedNode = addDirectory(startNode, addFile, newOnly, recurseDirectories, retainDirectories, progGui, selectedCategories);
                    if (displayNode == null) {
                        displayNode = addedNode;
                    }
                } else {
                    logger.info("PictureAdder.run: no pictures in directory " + addFile.toString());
                }
            }
        }
        Settings.pictureCollection.setSendModelUpdates(true);
        Settings.pictureCollection.sendNodeStructureChanged(startNode);

        progGui.switchToDoneMode();
        if (displayNode == null) {
            displayNode = startNode;
        }
        return displayNode;
    }

    /**
     *  method that is invoked recursively on each directory encountered. It adds
     *  a new group to the tree and then adds all the pictures found therein to that
     *  group. The ImageIO.getImageReaders method is queried to see whether a reader
     *  exists for the image that is attempted to be loaded.
     *  @param retainDirectories  indicates whether to preserve the directory structure
     *  @return returns the node that was added or null if none was.
     */
    private static SortableDefaultMutableTreeNode addDirectory(SortableDefaultMutableTreeNode startNode, File dir, boolean newOnly, boolean recurseDirectories, boolean retainDirectories, final ProgressGui progGui, HashSet<Object> selectedCategories) {
        SortableDefaultMutableTreeNode newNode;
        if (retainDirectories) {
            newNode = new SortableDefaultMutableTreeNode(new GroupInfo(dir.getName()));
            startNode.add(newNode);
            Settings.pictureCollection.setUnsavedUpdates();
        } else {
            newNode = startNode;
        }

        File[] fileArray = dir.listFiles();
        for (int i = 0; (i < fileArray.length) && (!progGui.getInterruptor().getShouldInterrupt()); i++) {
            if (fileArray[i].isDirectory() && recurseDirectories) {
                if (Tools.hasPictures(fileArray[i])) {
                    newNode = addDirectory(newNode, fileArray[i], newOnly, recurseDirectories, retainDirectories, progGui, selectedCategories);
                }
            } else {
                if (newNode.addSinglePicture(fileArray[i], newOnly, selectedCategories)) {
                    Runnable r = new Runnable() {

                        public void run() {
                            progGui.progressIncrement();
                        }
                    };
                    SwingUtilities.invokeLater(r);
                } else {
                    Runnable r = new Runnable() {

                        public void run() {
                            progGui.decrementTotal();
                        }
                    };
                    SwingUtilities.invokeLater(r);
                }
            }
        }
        // it can happen that we end up adding no pictures and could be returning a new empty group
        if (retainDirectories && (newNode.getChildCount() == 0)) {
            newNode.deleteNode();
            return startNode;
        } else {
            return newNode;
        }
    }
}
