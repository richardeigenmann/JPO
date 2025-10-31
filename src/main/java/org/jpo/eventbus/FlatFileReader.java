package org.jpo.eventbus;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.JpoImageIO;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.gui.JpoResources;
import org.jpo.gui.Settings;

import javax.swing.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright (C) 2017-2025 Richard Eigenmann, Zurich, Switzerland
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
 * Class to import a flat file of pictures into the supplied node
 *
 * @author Richard Eigenmann
 */
public class FlatFileReader {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( FlatFileReader.class.getName() );

    private FlatFileReader() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Constructs a FlatFileReader and imports the pictures listed in the file
     *
     * @param request The request
     */
    public static void handleRequest(final AddFlatFileRequest request) {
        final SortableDefaultMutableTreeNode newNode = new SortableDefaultMutableTreeNode(
                new GroupInfo(request.flatfile().getName()));

        try (final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(request.flatfile()), StandardCharsets.UTF_8))) {
            while (in.ready()) {
                final var line = in.readLine();
                final var testFile = getFile(line);

                if (!testFile.canRead()) {
                    LOGGER.log(Level.INFO, "Can''t read file: {0}", line);
                } else if (JpoImageIO.jvmHasReader(testFile)) {

                    LOGGER.log(Level.INFO, "adding file to node: {0}", line);
                    final SortableDefaultMutableTreeNode newPictureNode = new SortableDefaultMutableTreeNode(
                            new PictureInfo(testFile, FilenameUtils.getBaseName(testFile.getName())));
                    newNode.add(newPictureNode);
                }
            }
            request.node().add(newNode);
            request.node().getPictureCollection().sendNodeStructureChanged(request.node());
            request.node().getPictureCollection().setUnsavedUpdates(false);
            JpoEventBus.getInstance().post(new ShowGroupRequest(newNode));
        } catch ( final IOException ex ) {
            LOGGER.severe( ex.getLocalizedMessage() );
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    ex.getLocalizedMessage(),
                    JpoResources.getResource("genericError"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @NotNull
    private static File getFile(final String filename) {
        File testFile;
        try {
            testFile = new File(new URI(filename));
        } catch (final URISyntaxException | IllegalArgumentException x) {
            LOGGER.info(x.getLocalizedMessage());
            // The filename might just be a plain filename without URI format try this:
            testFile = new File(filename);
        }
        return testFile;
    }


}
