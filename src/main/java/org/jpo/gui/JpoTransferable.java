package org.jpo.gui;

import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import java.awt.datatransfer.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 JpoTransferable.java:  a transferable to drag and drop nodes of the Jpo application

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
    public JpoTransferable(final List<SortableDefaultMutableTreeNode> transferableNodes) {
        this.transferableNodes = transferableNodes;
    }

    /**
     * The nodes being transferred
     */
    private final List<SortableDefaultMutableTreeNode> transferableNodes;

    /**
     * Definition of the data flavor as a org.jpo internal object
     */
    public static final DataFlavor jpoNodeFlavor = new DataFlavor(Object.class, "JpoTransferable");

    /**
     * Definition of the data flavors supported by this Transferable.
     */
    private static final DataFlavor[] flavors = {
            jpoNodeFlavor,
            //DataFlavor.imageFlavor,
            DataFlavor.javaFileListFlavor,
            DataFlavor.stringFlavor
    };

    /**
     * Returns a well formated description of the supported transferables
     *
     * @return a well formated description of the supported transferables
     */
    private static String flavorsToString() {
        final StringBuilder sb = new StringBuilder(String.format("%d Transferable flavors supported: ", flavors.length));

        for (DataFlavor flavor : flavors) {
            sb.append(flavor.toString()).append(", ");
        }
        return (sb.toString());

    }

    /**
     * Returns the supported transferable data flavors.
     *
     * @return The transferable data flavors
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        LOGGER.log(Level.FINE, flavorsToString());

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
        final StringBuilder filenames = new StringBuilder();
        for (final SortableDefaultMutableTreeNode node : transferableNodes) {
            if (node.getUserObject() instanceof PictureInfo pi) {
                filenames.append("\"").append(pi.getImageFile()).append("\", ");
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
        final List<File> fileList = new ArrayList<>();
        for (final Object transferableNode : transferableNodes) {
            if ((transferableNode instanceof SortableDefaultMutableTreeNode n)
                    && (n.getUserObject() instanceof PictureInfo pi)) {
                fileList.add(pi.getImageFile());
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
        for (final Object transferableNode : transferableNodes) {
            if ((transferableNode instanceof SortableDefaultMutableTreeNode node)
                    && (node.getUserObject() instanceof PictureInfo pi)) {
                final SourcePicture sourcePicture = new SourcePicture();
                sourcePicture.loadPicture(pi.getImageFile(), pi.getRotation());
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
