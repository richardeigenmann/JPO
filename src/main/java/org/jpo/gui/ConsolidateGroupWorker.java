package org.jpo.gui;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.datamodel.Tools;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.io.FileUtils.moveFile;
import static org.jpo.datamodel.Tools.warnOnEDT;

/*
 Copyright (C) 2002 - 2020  Richard Eigenmann.
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
 * This class moves all pictures of a group node to a target directory.
 */
public class ConsolidateGroupWorker extends SwingWorker<String, String> {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ConsolidateGroupWorker.class.getName() );

    /**
     * the directory where the pictures are to be moved to
     */
    private final File targetDirectory;

    /**
     * the node to start from
     */
    private final SortableDefaultMutableTreeNode startNode;
    /**
     * flag that indicates that the subgroups should also be considered
     */
    private final boolean recurseGroups;

    /**
     * Creates a Thread which runs the consolidation.
     *
     * @param targetDirectory Where we want the files moved to
     * @param startNode       The node from which this is all to be built.
     * @param recurseGroups   Flag indicating subgroups should be included
     * @param progGui         A Progress Gui
     */
    public ConsolidateGroupWorker(final File targetDirectory,
                                  final SortableDefaultMutableTreeNode startNode, final boolean recurseGroups,
                                  final ProgressGui progGui) {
        this.targetDirectory = targetDirectory;
        this.startNode = startNode;
        this.recurseGroups = recurseGroups;
        this.progGui = progGui;

        if (!targetDirectory.exists()) {
            LOGGER.log(Level.SEVERE, "Aborting because target directory {0} doesn''t exist", targetDirectory.getPath());
            return;
        }
        if (!targetDirectory.canWrite()) {
            LOGGER.log(Level.SEVERE, "Aborting because directory {0} can''t be written to", targetDirectory.getPath() );
            return;
        }

        execute();
    }
    /**
     * This object holds a reference to the progress GUI for the user.
     */
    private final ProgressGui progGui;

    /**
     * The run method is fired by starting the thread. It creates a ProgressGui
     * and does the work.
     *
     * @return Integer.MAX_VALUE
     */
    @Override
    protected String doInBackground() {
        consolidateGroup( startNode );

        return "done";
    }

    @Override
    protected void process(final List<String> messages) {
        progGui.progressIncrement(messages.size());
    }

    @Override
    protected void done() {
        final String done = String.format(Settings.jpoResources.getString("ConsolidateProgBarDone"), consolidatedCount, movedCount);
        progGui.setDoneString(done);
        progGui.switchToDoneMode();

        if ( movedCount > 0 ) {
            startNode.getPictureCollection().setUnsavedUpdates();
        }

        if ( errorCount > 0 ) {
            JOptionPane.showMessageDialog( progGui,
                    String.format( "Could not move %d pictures", errorCount ),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
        }

    }

    private int errorCount;
    private int movedCount;
    private int consolidatedCount;

    /**
     * This method consolidates all the nodes of the supplied group.
     *
     * @param groupNode the Group whose nodes are to be consolidated.
     */
    private void consolidateGroup(final SortableDefaultMutableTreeNode groupNode) {
        final List<SortableDefaultMutableTreeNode> nodes = groupNode.getChildPictureNodes(recurseGroups);
        LOGGER.log(Level.INFO, "List Size: {0}", nodes.size());
        nodes.forEach(node -> {
            final PictureInfo pictureInfo = (PictureInfo) node.getUserObject();
            consolidatedCount++;
            LOGGER.info("node: " + pictureInfo.toString());
            if (needToMovePicture(pictureInfo, targetDirectory)) {
                if (movePicture(pictureInfo, targetDirectory)) {
                    movedCount++;
                    LOGGER.info(String.format("Successfully Moved Highres file of node %s", pictureInfo.toString()));
                    publish(String.format("Consolidated node: %s", node.toString() ) );
                } else {
                    LOGGER.severe( String.format( "Could not move highres picture of node %s. Aborting.", node.toString() ) );
                    errorCount++;
                }
            } else {
                publish( String.format( "No need to move node: %s", node.toString() ) );
            }
        } );
    }

    /**
     * Returns true if the picture needs to be moved, false if not
     *
     * @param pictureInfo the PictureInfo pointing to the image to move
     * @param targetDirectory the target directory to move it to
     * @return True if a move is needed False if not.
     */
    public static boolean needToMovePicture( @NonNull final PictureInfo pictureInfo, @NonNull final File targetDirectory ) {
        final File parentDirectory = Objects.requireNonNull(pictureInfo.getImageFile().getParentFile());
        return ! parentDirectory.equals( targetDirectory );
    }

    /**
     * This method moves a PictureInfo's file to the target directory if it
     * exists and can be moved necessary.
     *
     * @param pictureInfo the PictureInfo pointing to the highres file to move
     * @param targetDirectory the target directory
     * @return True if a real move was done False if not.
     */
    public static boolean movePicture(@NonNull final PictureInfo pictureInfo, @NonNull final File targetDirectory ) {
        Objects.requireNonNull(pictureInfo);
        Objects.requireNonNull(targetDirectory);

        final File pictureFile = pictureInfo.getImageFile();

        if (!isInTargetDirectory(targetDirectory, pictureFile)) {

            // make sure that we get a new filename. Some cameras might keep reusing the name DSC_01234.jpg
            // over and over again which would overwrite pictures in the worst case.
            final File newFile = Tools.inventPicFilename(targetDirectory, pictureInfo.getImageFile().getName());
            try {
                moveFile(pictureFile, newFile);
            } catch (final IOException ex) {
                LOGGER.log(Level.SEVERE, "Failed to move file {0} to {1}.\nException: {2}", new Object[]{pictureFile, newFile, ex.getLocalizedMessage()});
                return false;
            }
            pictureInfo.setImageLocation(newFile);
            correctReferences(pictureFile, newFile);
        }
        return true;
    }

    private static boolean isInTargetDirectory(@NotNull File targetDirectory, File pictureFile) {
        final File parentDirectory = pictureFile.getParentFile();
        return ((parentDirectory != null) && (parentDirectory.equals(targetDirectory)));
    }

    /**
     * Searches for any references in the current collection to the source file
     * and updates them to the target file.
     *
     * @param oldReference The file that was moved
     * @param newReference The new location of the source file
     */
    private static void correctReferences(final File oldReference, final File newReference) {
        warnOnEDT();
        //  search for other picture nodes in the tree using this image file
        SortableDefaultMutableTreeNode node;
        Object nodeObject;
        int count = 0;
        final Enumeration<TreeNode> e = Settings.getPictureCollection().getRootNode().preorderEnumeration();
        while ( e.hasMoreElements()) {
            node = (SortableDefaultMutableTreeNode) e.nextElement();
            nodeObject = node.getUserObject();
            if (nodeObject instanceof PictureInfo pi) {
                final File imageFile = pi.getImageFile();
                if (imageFile != null && imageFile.equals(oldReference)) {
                    pi.setImageLocation(newReference);
                    count++;
                }
            }
        }
        LOGGER.log(Level.INFO, "{0} other Picture Nodes were pointing at the same picture and were corrected", count );
    }
}
