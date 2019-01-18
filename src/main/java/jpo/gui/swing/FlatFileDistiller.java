package jpo.gui.swing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import jpo.EventBus.ExportGroupToFlatFileRequest;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;
import jpo.gui.swing.FlatFileDistiller.DistillerResult;

/*
 FlatFileDistiller.java:  class that writes the filenames of the pictures to a flat file
 *
 Copyright (C) 2002-2017  Richard Eigenmann.
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
 * a class that exports a tree of chapters to an XML file
 */
public class FlatFileDistiller extends SwingWorker<DistillerResult, String> {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(FlatFileDistiller.class.getName());

    /**
     * the node to start from
     */
    private SortableDefaultMutableTreeNode startNode;

    /**
     * The file to write to
     */
    private File outputFile;

    /**
     * Buffered writer to write to
     */
    private BufferedWriter out;

    /**
     * First opens a filechooser for the output file. Optionally asks if the
     * file should be overwritten then
     *
     * @param request Request
     */
    public FlatFileDistiller(ExportGroupToFlatFileRequest request) {
        Tools.checkEDT();
        javax.swing.JFileChooser jFileChooser = new javax.swing.JFileChooser();
        jFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        jFileChooser.setDialogTitle(Settings.jpoResources.getString("saveFlatFileTitle"));
        jFileChooser.setApproveButtonText(Settings.jpoResources.getString("saveFlatFileButtonLabel"));
        jFileChooser.setCurrentDirectory(Settings.getMostRecentCopyLocation());
        int returnVal = jFileChooser.showSaveDialog(Settings.anchorFrame);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        outputFile = jFileChooser.getSelectedFile();
        if (outputFile.exists()) {
            int returnCode = JOptionPane.showConfirmDialog(Settings.anchorFrame, "Overwrite file\n" + outputFile.toString(),
                    "File already exists warning",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (returnCode == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        try {
            out = new BufferedWriter(new FileWriter(outputFile));
        } catch (SecurityException exception) {
            JOptionPane.showMessageDialog(Settings.anchorFrame, "Security Exception:\n" + exception.getLocalizedMessage(),
                    "SecurityException",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(Settings.anchorFrame, "Input Output Exception:\n" + ex.getMessage(),
                    "IOExeption",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        startNode = request.getNode();
        this.execute();

    }

    @Override
    protected DistillerResult doInBackground() {
        DistillerResult distillerResult = new DistillerResult(true, null);
        try {
            enumerateGroup(startNode, out);
        } catch (IOException ex) {
            LOGGER.severe("catching it" + ex.getLocalizedMessage());
            return new DistillerResult(false, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                distillerResult = new DistillerResult(false, ex);
            }
        }
        return distillerResult;
    }

    /**
     * recursively invoked method to report all groups.
     *
     * @param groupNode group to work on
     * @throws IOException if there is a failure
     */
    private static void enumerateGroup(SortableDefaultMutableTreeNode groupNode, BufferedWriter out) throws IOException {
        //GroupInfo groupInfo = (GroupInfo) groupNode.getUserObject();
        Enumeration kids = groupNode.children();
        while (kids.hasMoreElements()) {
            SortableDefaultMutableTreeNode childNode = (SortableDefaultMutableTreeNode) kids.nextElement();
            if (childNode.getUserObject() instanceof GroupInfo) {
                enumerateGroup(childNode, out);
            } else {
                PictureInfo pictureInfo = (PictureInfo) childNode.getUserObject();
                out.write(pictureInfo.getImageLocation());
                out.newLine();

            }
        }
    }

    @Override
    protected void done() {
        DistillerResult result; // = new DistillerResult( false, new Exception( "Swing Worker failed" ) );

        try {
            result = get();
        } catch (InterruptedException ex) {
            JOptionPane.showMessageDialog(Settings.anchorFrame, "Interrupted Exception:\n" + ex.getLocalizedMessage(),
                    "InterruptedException",
                    JOptionPane.ERROR_MESSAGE);
            Thread.currentThread().interrupt();
            return;
        } catch (ExecutionException ex) {
            JOptionPane.showMessageDialog(Settings.anchorFrame, "Execution Exception:\n" + ex.getLocalizedMessage(),
                    "ExecutionException",
                    JOptionPane.ERROR_MESSAGE);
            Thread.currentThread().interrupt();
            return;
        }
        if (!result.success) {
            JOptionPane.showMessageDialog(Settings.anchorFrame, "Exception:\n" + result.getException().getLocalizedMessage(),
                    "Exception",
                    JOptionPane.ERROR_MESSAGE);
            Thread.currentThread().interrupt();
        } else {
            Settings.memorizeCopyLocation(outputFile.getParent());
            JOptionPane.showMessageDialog(Settings.anchorFrame, "Sucessfully wrote file.\n" + outputFile.toString(),
                    "Confirmation",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Distiller Result
     */
    public class DistillerResult {

        private final boolean success;
        private final Exception exception;

        /**
         * returns the Distiller Result
         *
         * @param success   whether the distiller succeeded or not
         * @param exception Exception
         */
        public DistillerResult(boolean success, Exception exception) {
            this.success = success;
            this.exception = exception;
        }

        /**
         * Returns true if the writing was successful
         *
         * @return true if successful
         */
        public boolean getSuccess() {
            return success;
        }

        /**
         * Returns the exception
         *
         * @return the exception
         */
        public Exception getException() {
            return exception;
        }

    }

}
