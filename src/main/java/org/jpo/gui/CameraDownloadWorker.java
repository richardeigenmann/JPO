package org.jpo.gui;

import org.apache.commons.io.FileUtils;
import org.jpo.cache.QUEUE_PRIORITY;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.datamodel.Tools;
import org.jpo.eventbus.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2002-2025 Richard Eigenmann.
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
 * Downloads the pictures on a background thread and updates the Progress bar
 *
 * @author Richard Eigenmann richard.eigenmann@gmail.com
 */
public class CameraDownloadWorker
        extends SwingWorker<String, String>
        implements ProgressListener {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(CameraDownloadWorker.class.getName());

    /**
     * A SwingWorker to download pictures from the camera
     *
     * @param dataModel   the data model
     * @param progressBar The progress bar
     * @param step7       step 7
     */
    public CameraDownloadWorker(final CameraDownloadWizardData dataModel,
                                final JProgressBar progressBar, final CameraDownloadWizardStep7 step7) {
        this.dataModel = dataModel;
        this.progressBar = progressBar;
        this.step7 = step7;
    }

    private final CameraDownloadWizardData dataModel;

    private final JProgressBar progressBar;

    private final CameraDownloadWizardStep7 step7;

    @Override
    protected String doInBackground() {
        Settings.memorizeCopyLocation(dataModel.getTargetDir().toString());
        JpoEventBus.getInstance().post(new CopyLocationsChangedEvent());
        if (dataModel.getShouldCreateNewGroup()) {
            LOGGER.log(Level.FINE, "Adding a new group {0} to node {1}", new Object[]{dataModel.getNewGroupDescription(), dataModel.getTargetNode()});
            final SortableDefaultMutableTreeNode newGroupNode
                    = new SortableDefaultMutableTreeNode(
                    new GroupInfo(dataModel.getNewGroupDescription()));
            dataModel.getTargetNode().add(newGroupNode);
            dataModel.setTargetNode(newGroupNode);
        }
        Settings.memorizeGroupOfDropLocation(dataModel.getTargetNode());
        JpoEventBus.getInstance().post(new RecentDropNodesChangedEvent());

        LOGGER.log(Level.FINE, "About to copyAddPictures to node {0}", dataModel.getTargetNode());
        copyAddPictures(
                dataModel.getTargetNode(),
                dataModel.getNewPictures(),
                dataModel.getTargetDir(),
                dataModel.getCopyMode(),
                progressBar);
        LOGGER.log(Level.FINE, "Sorting node {0} by code {1}", new Object[]{dataModel.getTargetNode(), dataModel.getSortCode()});
        dataModel.getTargetNode().sortChildren(dataModel.getSortCode());
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(List.of(dataModel.getTargetNode()), true, QUEUE_PRIORITY.LOWEST_PRIORITY));

        final InterruptSemaphore interrupter = new InterruptSemaphore();
        dataModel.getCamera().buildOldImage(this, interrupter);
        Settings.writeCameraSettings();
        return "Done";
    }

    /**
     * Copies the pictures from the source File collection into the target node while updating a supplied progress bar
     *
     * @param fileCollection A Collection framework of the new picture Files
     * @param targetDir      The target directory for the copy operation
     * @param copyMode       Set to true if you want to copy, false if you want to
     *                       move the pictures.
     * @param progressBar    The optional progressBar that should be incremented.
     */
    public void copyAddPictures(
            final SortableDefaultMutableTreeNode targetNode,
            final Collection<File> fileCollection, final File targetDir,
                                boolean copyMode, final JProgressBar progressBar) {
        LOGGER.log(Level.FINE, "Copy/Moving {0} pictures to target directory {1}", new Object[]{fileCollection.size(), targetDir});
        targetNode.getPictureCollection().setSendModelUpdates(false);
        for (final File file : fileCollection) {
            LOGGER.log(Level.FINE, "Processing file {}", file);
            if (progressBar != null) {
                SwingUtilities.invokeLater(
                        () -> progressBar.setValue(progressBar.getValue() + 1)
                );
            }
            final File targetFile = Tools.inventFilename(targetDir, file.getName());
            LOGGER.log(Level.FINE, "Target file name chosen as: {0}", new Object[]{targetFile});
            copyPicture(file, targetFile);

            if (!copyMode) {
                try {
                    Files.delete(file.toPath());
                } catch (final IOException _) {
                    LOGGER.log(Level.SEVERE, "File {} could not be deleted!", file);
                }
            }
            targetNode.addPicture(targetFile, null);
        }
        targetNode.getPictureCollection().setSendModelUpdates(true);
    }

    /**
     * Copy any file from sourceFile source File to sourceFile target File
     * location.
     *
     * @param sourceFile the source file location
     * @param targetFile the target file location
     */
    public static void copyPicture(final File sourceFile, final File targetFile) {
        LOGGER.log(Level.FINE, "Copying file {0} to file {1}", new Object[]{sourceFile, targetFile});
        try {
            FileUtils.copyFile(sourceFile, targetFile);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    Settings.getAnchorFrame(),
                    JpoResources.getResource("copyPictureError1")
                            + sourceFile
                            + JpoResources.getResource("copyPictureError2")
                            + targetFile.toString()
                            + JpoResources.getResource("copyPictureError3")
                            + e.getMessage(),
                    JpoResources.getResource("genericError"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * The Swing Worked calls this method when done.
     */
    @Override
    protected void done() {
        progressBar.setValue(progressBar.getMaximum());
        JpoEventBus.getInstance().post(new ShowGroupRequest(dataModel.getTargetNode()));
        step7.done();

    }

    @Override
    public void progressIncrement() {
        SwingUtilities.invokeLater(
                () -> progressBar.setValue(progressBar.getValue() + 1)
        );
    }

    @Override
    public void setMaximum(int max ) {
        SwingUtilities.invokeLater(
                () -> progressBar.setMaximum(max)
        );
    }

    @Override
    public void switchToDoneMode() {
    }

    @Override
    public InterruptSemaphore getInterruptSemaphore() {
        return new InterruptSemaphore();
    }
}
