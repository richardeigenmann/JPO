package jpo.gui;

import java.io.File;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import jpo.EventBus.ConsolidateGroupRequest;
import jpo.EventBus.CopyLocationsChangedEvent;
import jpo.EventBus.JpoEventBus;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.NodeStatistics;
import jpo.dataModel.Settings;
import jpo.gui.swing.ConsolidateGroupJFrame;



/**
 * Controller to consolidate pictures of a node into a directory.
 */
public class ConsolidateGroupController implements ConsolidateGroupActionCallback {

    /**
     * The request to consolidate is memorised here
     */
    private final ConsolidateGroupRequest request;

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ConsolidateGroupController.class.getName());

    /**
     * Creates a GUI that allows the user to customise the parameters of the
     * desired picture consolidation.
     *
     * @param request The details of the request
     */
    public ConsolidateGroupController(ConsolidateGroupRequest request) {
        this.request = request;

        Object userObject = request.getNode().getUserObject();
        if (!(userObject instanceof GroupInfo)) {
            LOGGER.info(String.format("Node %s is not a group", request.getNode().toString()));
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString("ConsolidateFailure"),
                    Settings.jpoResources.getString("genericError"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        ConsolidateGroupJFrame conslidateGroupJFrame = new ConsolidateGroupJFrame(this, this);

        if (request.getTargetDir() != null) {
            conslidateGroupJFrame.setTargetDir(request.getTargetDir());
        }
    }

    /**
     * method that outputs the selected group to a directory
     * @param targetDirectory target directory
     */
    @Override
    public void consolidateGroupCallback(File targetDirectory, boolean rescurseSubgroups) {
        if (!targetDirectory.exists()) {
            try {
                if (!targetDirectory.mkdirs()) {
                    JOptionPane.showMessageDialog(Settings.anchorFrame,
                            String.format(Settings.jpoResources.getString("ConsolidateCreateDirFailure"), targetDirectory),
                            Settings.jpoResources.getString("genericError"),
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (SecurityException e) {
                JOptionPane.showMessageDialog(Settings.anchorFrame,
                        String.format(Settings.jpoResources.getString("ConsolidateCreateDirFailure"), targetDirectory),
                        Settings.jpoResources.getString("genericError"),
                        JOptionPane.ERROR_MESSAGE);
                LOGGER.severe(String.format("SecurityException when creating directory %s. Reason: %s", targetDirectory, e.getMessage()));
                return;
            }
        }

        if (!targetDirectory.canWrite()) {
            JOptionPane.showMessageDialog(Settings.anchorFrame,
                    String.format(Settings.jpoResources.getString("ConsolidateCantWrite"), targetDirectory),
                    Settings.jpoResources.getString("genericError"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Settings.memorizeCopyLocation(targetDirectory.toString());
        
        new ConsolidateGroupWorker(
                targetDirectory,
                request.getNode(),
                rescurseSubgroups,
                new ProgressGui(NodeStatistics.countPictures(request.getNode(), rescurseSubgroups),
                        Settings.jpoResources.getString("ConsolitdateProgBarTitle"),
                        Settings.jpoResources.getString("ConsolitdateProgBarDone")));
        
        JpoEventBus.getInstance().post(new CopyLocationsChangedEvent());

    }

}
