package org.jpo.gui.swing;

import org.jpo.datamodel.*;
import org.jpo.eventbus.ExportGroupToFlatFileRequest;
import org.jpo.gui.swing.FlatFileDistiller.DistillerResult;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/*
 FlatFileDistiller.java:  class that writes the filenames of the pictures to a flat file
 *
 Copyright (C) 2002-2022  Richard Eigenmann.
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
    public FlatFileDistiller(final ExportGroupToFlatFileRequest request) {
        Tools.checkEDT();
        final var jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        jFileChooser.setDialogTitle(Settings.getJpoResources().getString("saveFlatFileTitle"));
        jFileChooser.setApproveButtonText(Settings.getJpoResources().getString("saveFlatFileButtonLabel"));
        jFileChooser.setCurrentDirectory(Settings.getMostRecentCopyLocation());
        final int returnVal = jFileChooser.showSaveDialog(Settings.getAnchorFrame());
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        outputFile = jFileChooser.getSelectedFile();
        if (outputFile.exists()) {
            int returnCode = JOptionPane.showConfirmDialog(Settings.getAnchorFrame(), "Overwrite file\n" + outputFile.toString(),
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
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(), "Security Exception:\n" + exception.getLocalizedMessage(),
                    "SecurityException",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(), "Input Output Exception:\n" + ex.getMessage(),
                    "IOExeption",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        startNode = request.node();
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
    private static void enumerateGroup(final SortableDefaultMutableTreeNode groupNode, final BufferedWriter out) throws IOException {
        final Enumeration<TreeNode> kids = groupNode.children();
        while (kids.hasMoreElements()) {
            final SortableDefaultMutableTreeNode childNode = (SortableDefaultMutableTreeNode) kids.nextElement();
            if (childNode.getUserObject() instanceof GroupInfo) {
                enumerateGroup(childNode, out);
            } else {
                final PictureInfo pictureInfo = (PictureInfo) childNode.getUserObject();
                out.write(pictureInfo.getImageLocation());
                out.newLine();

            }
        }
    }

    @Override
    protected void done() {
        DistillerResult result;

        try {
            result = get();
        } catch (final InterruptedException ex) {
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(), "Interrupted Exception:\n" + ex.getLocalizedMessage(),
                    "InterruptedException",
                    JOptionPane.ERROR_MESSAGE);
            Thread.currentThread().interrupt();
            return;
        } catch (final ExecutionException ex) {
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(), "Execution Exception:\n" + ex.getLocalizedMessage(),
                    "ExecutionException",
                    JOptionPane.ERROR_MESSAGE);
            Thread.currentThread().interrupt();
            return;
        }

        if (!result.success) {
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(), "Exception:\n" + result.getException().getLocalizedMessage(),
                    "Exception",
                    JOptionPane.ERROR_MESSAGE);
            Thread.currentThread().interrupt();
        } else {
            Settings.memorizeCopyLocation(outputFile.getParent());
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(), "Successfully wrote file.\n" + outputFile.toString(),
                    "Confirmation",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Distiller Result
     */
    public static class DistillerResult {

        private final boolean success;
        private final Exception exception;

        /**
         * returns the Distiller Result
         *
         * @param success   whether the distiller succeeded or not
         * @param exception Exception
         */
        public DistillerResult(final boolean success, final Exception exception) {
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
