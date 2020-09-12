package org.jpo.gui;

import org.jpo.cache.QUEUE_PRIORITY;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/*
 Copyright (C) 2002-2019  Richard Eigenmann.
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
    public CameraDownloadWorker(CameraDownloadWizardData dataModel,
                                JProgressBar progressBar, CameraDownloadWizardStep7 step7) {
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
            LOGGER.fine(String.format("Adding a new group %s to node %s", dataModel.getNewGroupDescription(), dataModel.getTargetNode().toString()));
            SortableDefaultMutableTreeNode newGroupNode = dataModel.getTargetNode().addGroupNode(dataModel.getNewGroupDescription());
            dataModel.setTargetNode(newGroupNode);
        }
        Settings.memorizeGroupOfDropLocation(dataModel.getTargetNode());
        JpoEventBus.getInstance().post(new RecentDropNodesChangedEvent());

        LOGGER.fine(String.format("About to copyAddPictures to node %s", dataModel.getTargetNode().toString()));
        dataModel.getTargetNode().copyAddPictures(dataModel.getNewPictures(),
                dataModel.getTargetDir(),
                dataModel.getCopyMode(),
                progressBar);
        LOGGER.fine(String.format("Sorting node %s by code %s", dataModel.getTargetNode().toString(), dataModel.getSortCode()));
        dataModel.getTargetNode().sortChildren(dataModel.getSortCode());
        List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
        nodes.add(dataModel.getTargetNode());
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, QUEUE_PRIORITY.LOWEST_PRIORITY));

        InterruptSemaphore interrupter = new InterruptSemaphore();
        dataModel.getCamera().buildOldImage(this, interrupter);// this, interrupter );
        Settings.writeCameraSettings();
        return "Done";
    }

    /**
     * The Swing Worked calls this method when done.
     */
    @Override
    protected void done() {
        progressBar.setValue(progressBar.getMaximum());
        JpoEventBus.getInstance().post(new GroupSelectionEvent(dataModel.getTargetNode()));
        step7.done();

    }

    @Override
    public void progressIncrement() {
        SwingUtilities.invokeLater(
                () -> progressBar.setValue(progressBar.getValue() + 1)
        );
    }
}
