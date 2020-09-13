package org.jpo.datamodel;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.TestOnly;
import org.jpo.eventbus.ExportGroupToCollectionRequest;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2002-2020  Richard Eigenmann.
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
 * Writes the JPO Collection to an xml formatted file
 */
public class JpoWriter {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(JpoWriter.class.getName());


    /**
     * Don't use the constructor call JpoWriter.write directly.
     */
    private JpoWriter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Writes the collection in a thread to the file.
     *
     * @param request The request
     */
    public static void writeInThread(final ExportGroupToCollectionRequest request) {
        new Thread(()
                -> write(request)
        ).start();
    }

    /**
     * Writes the collection as per the request object
     *
     * @param request The request
     */
    public static void write(final ExportGroupToCollectionRequest request) {
        write(request.getTargetFile(), request.getNode(), request.getExportPictures());
    }


    /**
     * method that is invoked by the thread to do things asynchronously
     *
     * @param xmlOutputFile The output file to write to
     * @param startNode     The node to start from
     * @param copyPics      whether to copy the pictures to the target dir
     */
    private static void write(final File xmlOutputFile, final SortableDefaultMutableTreeNode startNode, final boolean copyPics) {
        final File highresTargetDir = getHighresTargetDir(copyPics, xmlOutputFile);
        try (final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlOutputFile), StandardCharsets.UTF_8))) {
            writeXmlHeader(bufferedWriter);
            enumerateGroup(startNode, startNode, bufferedWriter, highresTargetDir, copyPics);
            writeCategoriesBlock(startNode.getPictureCollection(), bufferedWriter);

            bufferedWriter.write("</collection>");
            bufferedWriter.newLine();

            writeCollectionDTD(xmlOutputFile.getParentFile());
        } catch (IOException x) {
            LOGGER.log(Level.INFO, "IOException: {0}", x.getMessage());
            JOptionPane.showMessageDialog(null, x.getMessage(),
                    "JpoWriter: IOException",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SecurityException x) {
            LOGGER.log(Level.INFO, x.getMessage());
            JOptionPane.showMessageDialog(null, x.getMessage(),
                    "XmlDistiller: SecurityException",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void writeXmlHeader(final BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        bufferedWriter.newLine();
        bufferedWriter.write("<!DOCTYPE collection SYSTEM \"" + Settings.COLLECTION_DTD + "\">");
        bufferedWriter.newLine();
    }

    @TestOnly
    public static void writeXmlHeaderTestOnly(final BufferedWriter bufferedWriter) throws IOException {
        writeXmlHeader(bufferedWriter);
    }

    private static void writeCategoriesBlock(final PictureCollection pictureCollection, final BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write("<categories>");
        bufferedWriter.newLine();

        final Iterator<Integer> i = pictureCollection.getCategoryIterator();
        Integer key;
        while (i.hasNext()) {
            key = i.next();
            bufferedWriter.write("\t<category index=\"" + key.toString() + "\">");
            bufferedWriter.newLine();
            bufferedWriter.write("\t\t<categoryDescription><![CDATA[" + pictureCollection.getCategory(key) + "]]></categoryDescription>");
            bufferedWriter.newLine();
            bufferedWriter.write("\t</category>");
            bufferedWriter.newLine();
        }

        bufferedWriter.write("</categories>");
        bufferedWriter.newLine();
    }

    @TestOnly
    public static void writeCategoriesBlockTestOnly(final PictureCollection pictureCollection, final BufferedWriter bufferedWriter) throws IOException {
        writeCategoriesBlock(pictureCollection, bufferedWriter);
    }

    private static File getHighresTargetDir(boolean copyPics, File xmlOutputFile) {
        File highresTargetDir = null;

        if (copyPics) {
            highresTargetDir = new File(xmlOutputFile.getParentFile(), "Highres");


            if ((!highresTargetDir.mkdirs()) && (!highresTargetDir.canWrite())) {
                LOGGER.log(Level.SEVERE, "There was a problem creating dir {0}", highresTargetDir);
                return null;
            }
        }
        return highresTargetDir;
    }

    /**
     * recursively invoked method to report all groups.
     *
     * @param groupNode      The group node
     * @param bufferedWriter The writer
     * @throws IOException bubble-up IOException
     */
    private static void enumerateGroup(final SortableDefaultMutableTreeNode startNode, final SortableDefaultMutableTreeNode groupNode,
                                       final BufferedWriter bufferedWriter,
                                       final File highresTargetDir,
                                       final boolean copyPics) throws IOException {
        final GroupInfo groupInfo = (GroupInfo) groupNode.getUserObject();

        groupInfo.dumpToXml(bufferedWriter, groupNode.equals(startNode), groupNode.getPictureCollection().getAllowEdits());

        SortableDefaultMutableTreeNode childNode;
        final Enumeration<TreeNode> kids = groupNode.children();
        while (kids.hasMoreElements()) {
            childNode = (SortableDefaultMutableTreeNode) kids.nextElement();
            if (childNode.getUserObject() instanceof GroupInfo) {
                enumerateGroup(startNode, childNode, bufferedWriter, highresTargetDir, copyPics);
            } else if (childNode.getUserObject() instanceof PictureInfo pi) {
                writePicture(pi, bufferedWriter, highresTargetDir, copyPics);
            } else {
                LOGGER.log(Level.SEVERE, "Can't write node {0}", childNode);
            }
        }

        groupInfo.endGroupXML(bufferedWriter, groupNode.equals(startNode));
    }

    /**
     * write a picture to the output
     *
     * @param pictureInfo    the picture to write
     * @param bufferedWriter the writer to which to write
     * @throws IOException bubble-up IOException
     */
    private static void writePicture(final PictureInfo pictureInfo,
                                     final BufferedWriter bufferedWriter,
                                     final File highresTargetDir,
                                     final boolean copyPics
    ) throws IOException {
        if (copyPics) {
            final File targetHighresFile = Tools.inventPicFilename(highresTargetDir, pictureInfo.getImageFile().getName());
            FileUtils.copyFile(pictureInfo.getImageFile(), targetHighresFile);
            final PictureInfo tempPi = pictureInfo.getClone();
            tempPi.setImageLocation(targetHighresFile);
            tempPi.dumpToXml(bufferedWriter);
        } else {
            pictureInfo.dumpToXml(bufferedWriter);
        }
    }

    @TestOnly
    public static void writePictureTestOnly(final PictureInfo pictureInfo,
                                            final BufferedWriter bufferedWriter,
                                            final File highresTargetDir,
                                            final boolean copyPics) throws IOException {
        writePicture(pictureInfo, bufferedWriter, highresTargetDir, copyPics);
    }


    /**
     * Write the collection.dtd file to the target directory.
     *
     * @param directory The directory to write to
     */
    private static void writeCollectionDTD(final File directory) {
        final ClassLoader cl = JpoWriter.class.getClassLoader();
        try (
                final InputStream in = Objects.requireNonNull(cl.getResource("collection.dtd")).openStream();
                final BufferedInputStream bin = new BufferedInputStream(in);) {
            final File targetFile = new File(directory, "collection.dtd");
            Files.copy(
                    bin,
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            JOptionPane.showMessageDialog(
                    Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("DtdCopyError") + e.getMessage(),
                    Settings.getJpoResources().getString("genericWarning"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @TestOnly
    public static void writeCollectionDTDTestOnly(final File directory) {
        writeCollectionDTD(directory);
    }
}
