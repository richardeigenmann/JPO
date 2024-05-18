package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2022-2024 Richard Eigenmann.
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
 * Handles the request  CopyToZipfileRequest
 */
@EventHandler
public class CopyToZipfileHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(CopyToZipfileHandler.class.getName());

    /**
     * Title for Info Boxes
     */
    public static final String GENERIC_INFO = Settings.getJpoResources().getString("genericInfo");

    /**
     * Copies the pictures of the supplied nodes to the target zipfile, creating
     * it if need be. This method does append to the zipfile by writing to a
     * temporary file and then copying the old zip file over to this one as the
     * API doesn't support directly appending to a zip file.
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final CopyToZipfileRequest request) {
        final var tempFile = new File(request.targetZipfile().getAbsolutePath() + ".org.jpo.temp");
        var picsCopied = 0;
        try (final var zipArchiveOutputStream = new ZipArchiveOutputStream(tempFile)) {
            zipArchiveOutputStream.setLevel(9);
            picsCopied += addPicturesToZip(zipArchiveOutputStream, request.nodes());

            if (request.targetZipfile().exists()) {
                // copy the old entries over
                try (
                        final var oldZipFile = new ZipFile(request.targetZipfile())) {
                    final var entries = oldZipFile.getEntries();
                    while (entries.hasMoreElements()) {
                        final var entry = entries.nextElement();
                        LOGGER.log(Level.INFO, "streamCopy: {0}", entry.getName());
                        zipArchiveOutputStream.putArchiveEntry(entry);
                        if (!entry.isDirectory()) {
                            oldZipFile.getInputStream(entry).transferTo(zipArchiveOutputStream);
                        }
                        zipArchiveOutputStream.closeArchiveEntry();
                    }
                }
            }
            zipArchiveOutputStream.finish();
        } catch (final IOException ex) {
            try {
                Files.delete(tempFile.toPath());
            } catch (final IOException e) {
                LOGGER.log(Level.SEVERE, "Could not delete tempFile: {0} Exception: {1}", new Object[]{tempFile, e.getMessage()});
            }
        }

        if (request.targetZipfile().exists()) {
            LOGGER.log(Level.INFO, "Deleting old file {0}", request.targetZipfile().getAbsolutePath());
            try {
                Files.delete(request.targetZipfile().toPath());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to delete file {0}, Exception: {1}", new Object[]{request.targetZipfile().getAbsolutePath(), e.getMessage()});
            }
        }
        LOGGER.log(Level.INFO, "Renaming temp file {0} to {1}", new Object[]{tempFile.getAbsolutePath(), request.targetZipfile().getAbsolutePath()});
        boolean ok = tempFile.renameTo(request.targetZipfile());
        if (!ok) {
            LOGGER.log(Level.SEVERE, "Failed to rename temp file {0} to {1}", new Object[]{tempFile.getAbsolutePath(), request.targetZipfile().getAbsolutePath()});
        }

        JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                String.format("Copied %d files of %d to zipfile %s", picsCopied, request.nodes().size(), request.targetZipfile()),
                GENERIC_INFO,
                JOptionPane.INFORMATION_MESSAGE);

    }


    private int addPicturesToZip(
            final ZipArchiveOutputStream zipArchiveOutputStream,
            final Collection<SortableDefaultMutableTreeNode> nodes)
            throws IOException {
        var picsCopied = 0;
        for (final var node : nodes) {
            if (node.getUserObject() instanceof PictureInfo pi) {
                final var sourceFile = pi.getImageFile();
                LOGGER.log(Level.INFO, "Processing file {0}", sourceFile);

                final var entry = new ZipArchiveEntry(sourceFile, sourceFile.getName());
                zipArchiveOutputStream.putArchiveEntry(entry);

                try (final var fis = new FileInputStream(sourceFile)) {
                    fis.transferTo(zipArchiveOutputStream);
                }
                zipArchiveOutputStream.closeArchiveEntry();

                picsCopied++;

            } else {
                LOGGER.log(Level.INFO, "Skipping non PictureInfo node {0}", node);
            }
        }
        return picsCopied;
    }

}
