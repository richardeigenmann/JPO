package org.jpo.gui;

import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.datamodel.SourcePicture;

import java.awt.datatransfer.*;
import java.util.ArrayList;
import java.util.Arrays;
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
 * A transferable for the JPO application
 */
public class JpoTransferable
        implements Transferable, ClipboardOwner {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(JpoTransferable.class.getName());

    /**
     * Constructs a JpoTransferable
     *
     * @param transferableNodes The nodes to be transferred
     */
    public JpoTransferable(final Collection<SortableDefaultMutableTreeNode> transferableNodes) {
        this.transferableNodes = transferableNodes;
    }

    /**
     * The nodes being transferred
     */
    private final Collection<SortableDefaultMutableTreeNode> transferableNodes;

    /**
     * Definition of the data flavor as an org.jpo internal object
     */
    public static final DataFlavor jpoNodeFlavor = new DataFlavor(Object.class, "JpoTransferable");

    /**
     * Definition of the data flavors supported by this Transferable.
     */
    private static final DataFlavor[] flavors = {
        jpoNodeFlavor,
        DataFlavor.javaFileListFlavor,
        DataFlavor.stringFlavor
    };

    /**
     * Returns the supported transferable data flavors.
     *
     * @return The transferable data flavors
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    /**
     * Returns if a requested flavor is supported.
     *
     * @param flavor The flavor to query
     * @return whether it is supported or not
     */
    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        LOGGER.log(Level.INFO, "Requested flavor {0} is supported: {1}", new Object[]{flavor, Arrays.asList(flavors).contains(flavor)});
        return (Arrays.asList(flavors).contains(flavor));
    }

    /**
     * Returns the transferable in the requested flavor
     *
     * @param flavor The flavor to return the transferable in
     * @return The transferable
     * @throws UnsupportedFlavorException You get this exception if you request
     *                                    something that is not supported
     */
    @NotNull
    @Override
    public Object getTransferData(final DataFlavor flavor)
            throws UnsupportedFlavorException {
        LOGGER.log(Level.FINE, "Transferable requested as DataFlavor: {0}", flavor);
        if (flavor.equals(jpoNodeFlavor)) {
            LOGGER.log(Level.FINE, "returning the Java array of nodes as a transferable");
            return transferableNodes;
        } else if (flavor.equals(DataFlavor.stringFlavor)) {
            return getStringTransferData();
        } else if (flavor.equals(DataFlavor.javaFileListFlavor)) {
            return getJavaFileListTransferable();
        } else if (flavor.equals(DataFlavor.imageFlavor)) {
            return getImageTransferable();
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /**
     * Returns the transfer data in the DataFlavor format for a string
     *
     * @return the transfer data as a String
     */
    private Object getStringTransferData() {
        final var filenames = new StringBuilder();
        for (final var node : transferableNodes) {
            if (node.getUserObject() instanceof PictureInfo pictureInfo) {
                filenames.append("\"").append(pictureInfo.getImageFile()).append("\", ");
            }
        }
        LOGGER.log(Level.INFO, "Returning the following String as stringFlavor: {0}", filenames);
        return filenames.toString();
    }

    /**
     * Returns the transfer data as a List for the javaFileListFlavor
     *
     * @return the transferable as a List
     */
    private Object getJavaFileListTransferable() {
        final var fileList = new ArrayList<>();
        for (final var transferableNode : transferableNodes) {
            if ( transferableNode.getUserObject() instanceof PictureInfo pictureInfo ) {
                fileList.add(pictureInfo.getImageFile());
            }
        }
        LOGGER.log(Level.INFO, "Returning {0} files in a list", fileList.size());
        return fileList;
    }

    /**
     * Returns the transfer data as a List of Images
     *
     * @return the transferable as a List of Images
     */
    private Object getImageTransferable() {
        final List<Object> imageList = new ArrayList<>();
        for (final var transferableNode : transferableNodes) {
            if ((transferableNode.getUserObject() instanceof PictureInfo pictureInfo)) {
                final var sourcePicture = new SourcePicture();
                sourcePicture.loadPicture(pictureInfo.getSha256(), pictureInfo.getImageFile(), pictureInfo.getRotation());
                imageList.add(sourcePicture.getSourceBufferedImage());
            }
        }
        LOGGER.info("Returning a BufferedImage in the Transferable");
        return imageList;
    }

    /**
     * Returns information about the transferable
     *
     * @return information about the transferable
     */
    @Override
    public String toString() {
        final StringBuilder objectDescriptions = new StringBuilder(String.format("JpoTransferable for %d nodes: ", transferableNodes.size()));

        transferableNodes.forEach(o -> objectDescriptions.append(o.toString()).append(", "));
        return objectDescriptions.toString();
    }

    /**
     * Comes from the clipboard owner interface
     *
     * @param clipboard The clipboard
     * @param contents  The transferable
     */
    @Override
    public void lostOwnership(final Clipboard clipboard, final Transferable contents) {
        LOGGER.log(Level.INFO, "lostOwnership clipboard: {0}, Transferable: {1}", new Object[]{clipboard, contents});
    }
}