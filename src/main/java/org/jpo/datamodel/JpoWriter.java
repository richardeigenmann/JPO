package org.jpo.datamodel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.TestOnly;
import org.jpo.eventbus.ExportGroupToCollectionRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Objects;
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
 * Writes the JPO Collection to a xml formatted file
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
     * Writes the collection as per the request object using the tread it is called on.
     *
     * @param request The request
     */
    public static void write(final ExportGroupToCollectionRequest request) throws IOException, SecurityException {
        write(request.targetFile(), request.node(), request.exportPictures());
    }


    /**
     * method that is invoked by the thread to do things asynchronously
     *
     * @param xmlOutputFile The output file to write to
     * @param startNode     The node to start from
     * @param copyPics      whether to copy the pictures to the target dir
     */
    private static void write(
            final File xmlOutputFile,
            final SortableDefaultMutableTreeNode startNode,
            final boolean copyPics
        ) throws IOException, SecurityException {
        final var highresTargetDir = getHighresTargetDir(copyPics, xmlOutputFile);
        final var baseDir = startNode.getPictureCollection().getCommonPath();
        final BufferedWriter xmlOutput = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlOutputFile), StandardCharsets.UTF_8));
        writeXmlHeader(xmlOutput);
        writeDTD(xmlOutput);

        writeCollectionHeader(startNode, xmlOutput, baseDir);
        enumerateGroup(startNode, startNode, xmlOutput, highresTargetDir, copyPics, baseDir);
        writeCategoriesBlock(startNode.getPictureCollection(), xmlOutput);

        xmlOutput.write("</collection>");
        xmlOutput.newLine();

        writeCollectionDTD(xmlOutputFile.getParentFile());
        xmlOutput.close();
    }

    private static void writeCollectionHeader(final SortableDefaultMutableTreeNode startNode, final BufferedWriter xmlOutput, final Path baseDir) throws IOException {
        final var groupInfo = (GroupInfo) startNode.getUserObject();
        final var protection = startNode.getPictureCollection().getAllowEdits();
        final String newline = System. lineSeparator();
        xmlOutput.write("<collection collection_name=\""
            + StringEscapeUtils.escapeXml11(groupInfo.getGroupName())
            + "\" collection_created=\""
            + DateFormat.getDateInstance().format(Calendar.getInstance().getTime())
            + "\""
            + (protection ? " collection_protected=\"No\"" : " collection_protected=\"Yes\"")
            + " basedir=\"" + baseDir.toString() + "\""
            + ">" + newline);
    }

    /**
     * Accessor to write the xml header so that it can be unit tested
     */
    @TestOnly
    public static void writeCollectionHeaderTestOnly(final SortableDefaultMutableTreeNode startNode, final BufferedWriter xmlOutput, final Path baseDir) throws IOException {
        writeCollectionHeader(startNode,xmlOutput, baseDir);
    }

    @TestOnly
    public static void writeXmlHeader(final BufferedWriter xmlOutput) throws IOException {
        xmlOutput.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xmlOutput.newLine();
    }

    /**
     * Accessor to write the xml header so that it can be unit tested
     *
     * @param xmlOutput the writer where the output goes
     * @throws IOException Can throw an IOException
     */
    @TestOnly
    public static void writeXmlHeaderTestOnly(final BufferedWriter xmlOutput) throws IOException {
        writeXmlHeader(xmlOutput);
    }

    private static void writeCategoriesBlock(final PictureCollection pictureCollection, final BufferedWriter xmlOutput) throws IOException {
        xmlOutput.write("<categories>");
        xmlOutput.newLine();

        final Iterator<Integer> i = pictureCollection.getCategoryIterator();
        Integer key;
        while (i.hasNext()) {
            key = i.next();
            xmlOutput.write("\t<category index=\"" + key.toString() + "\">");
            xmlOutput.newLine();
            xmlOutput.write("\t\t<categoryDescription><![CDATA[" + pictureCollection.getCategory(key) + "]]></categoryDescription>");
            xmlOutput.newLine();
            xmlOutput.write("\t</category>");
            xmlOutput.newLine();
        }

        xmlOutput.write("</categories>");
        xmlOutput.newLine();
    }

    /**
     * Only to be used in the unit tests
     *
     * @param pictureCollection The picture collection
     * @param xmlOutput    The buffered writer
     * @throws IOException if something goes wrong in the IO
     */
    @TestOnly
    public static void writeCategoriesBlockTestOnly(final PictureCollection pictureCollection, final BufferedWriter xmlOutput) throws IOException {
        writeCategoriesBlock(pictureCollection, xmlOutput);
    }

    private static File getHighresTargetDir(final boolean copyPics, final File xmlOutputFile) {
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
     * this method writes all attribute
     * s of the group to the JPO xml data
     * format with the highres locations passed in as parameters.
     *
     * @param out        The BufferedWriter receiving the xml data. I use a BufferedWriter because it has a newLine method
     * @param rootNode   The starting node
     * @param protection Whether the collection is protected or not
     * @throws IOException If there was an IO error
     */
    @TestOnly
    public static void dumpToXml(final GroupInfo groupInfo, final BufferedWriter out, final boolean rootNode, final boolean protection)
            throws IOException {

        if (!rootNode) {
            out.write( "<group group_name=\"" + StringEscapeUtils.escapeXml11( groupInfo.getGroupName() ) + "\">" );
        }
        out.newLine();
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
                                       final boolean copyPics,
                                       final Path baseDir) throws IOException {
        final var groupInfo = (GroupInfo) groupNode.getUserObject();

        dumpToXml(groupInfo, bufferedWriter, groupNode.equals(startNode), groupNode.getPictureCollection().getAllowEdits());

        final var kids = groupNode.children();
        while (kids.hasMoreElements()) {
            final var childNode = (SortableDefaultMutableTreeNode) kids.nextElement();
            if (childNode.getUserObject() instanceof GroupInfo) {
                enumerateGroup(startNode, childNode, bufferedWriter, highresTargetDir, copyPics, baseDir);
            } else if (childNode.getUserObject() instanceof PictureInfo pictureInfo) {
                writePicture(pictureInfo, bufferedWriter, highresTargetDir, copyPics, baseDir);
            } else {
                LOGGER.log(Level.SEVERE, "Can not write node {0}", childNode);
            }
        }

        if ( ! groupNode.equals(startNode) ) {  // if it is root Node then the XmlDistiller adds the categories and end collection tag.
            bufferedWriter.write( "</group>" );
        }
        bufferedWriter.newLine();

    }

    /**
     * this method writes all attributes of the picture in the JPO xml data
     * format with the highres and lowres locations passed in as parameters.
     * This became necessary because when the XmlDistiller copies the pictures
     * to a new location we don't want to write the URLs of the original
     * pictures whilst all other attributes are retained.
     *
     * @param out The Buffered Writer receiving the xml data
     * @throws IOException If there was an IO error
     */
    public static void dumpToXml(final PictureInfo pictureInfo, final BufferedWriter out, final Path baseDir)
            throws IOException {
        out.write("<picture>");
        out.newLine();
        out.write("\t<description><![CDATA[" + pictureInfo.getDescription() + "]]></description>");
        out.newLine();

        if ((pictureInfo.getCreationTime() != null) && (!pictureInfo.getCreationTime().isEmpty())) {
            out.write("\t<CREATION_TIME><![CDATA[" + pictureInfo.getCreationTime() + "]]></CREATION_TIME>");
            out.newLine();
        }

        if (! pictureInfo.getImageFile().toURI().toString().isEmpty()) {
            final var file = pictureInfo.getImageFile();
            final var relativeImageFile = pictureInfo.getRelativePath(file, baseDir);
            out.write("\t<file>" + StringEscapeUtils.escapeXml11(relativeImageFile.toString()) + "</file>");
            out.newLine();
        }

        if ((!pictureInfo.getSha256().equals("")) && (!pictureInfo.getSha256().equals("N/A"))) {
            out.write("\t<sha256>" + pictureInfo.getSha256() + "</sha256>");
            out.newLine();
        }

        if (! pictureInfo.getComment().isEmpty()) {
            out.write("\t<COMMENT>" + StringEscapeUtils.escapeXml11(pictureInfo.getComment()) + "</COMMENT>");
            out.newLine();
        }

        if (! pictureInfo.getPhotographer().isEmpty()) {
            out.write("\t<PHOTOGRAPHER>" + StringEscapeUtils.escapeXml11(pictureInfo.getPhotographer()) + "</PHOTOGRAPHER>");
            out.newLine();
        }

        if (! pictureInfo.getFilmReference().isEmpty()) {
            out.write("\t<film_reference>" + StringEscapeUtils.escapeXml11(pictureInfo.getFilmReference()) + "</film_reference>");
            out.newLine();
        }

        if (! pictureInfo.getCopyrightHolder().isEmpty()) {
            out.write("\t<COPYRIGHT_HOLDER>" + StringEscapeUtils.escapeXml11(pictureInfo.getCopyrightHolder()) + "</COPYRIGHT_HOLDER>");
            out.newLine();
        }

        if (pictureInfo.getRotation() != 0) {
            out.write(String.format("\t<ROTATION>%f</ROTATION>", pictureInfo.getRotation()));
            out.newLine();
        }

        final var latLng = pictureInfo.getLatLng();
        if (latLng != null && latLng.x != 0 && latLng.y != 0) {
            out.write(String.format("\t<LATLNG>%fx%f</LATLNG>", latLng.x, latLng.y));
            out.newLine();
        }

        if (pictureInfo.getCategoryAssignments() != null) {
            for (final var categoryAssignment : pictureInfo.getCategoryAssignments()) {
                out.write("\t<categoryAssignment index=\"" + categoryAssignment + "\"/>");
                out.newLine();
            }
        }

        out.write("</picture>");
        out.newLine();
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
                                     final boolean copyPics,
                                     final Path baseDir
    ) throws IOException {
        if (copyPics) {
            final var targetHighresFile = Tools.inventFilename(highresTargetDir, pictureInfo.getImageFile().getName());
            FileUtils.copyFile(pictureInfo.getImageFile(), targetHighresFile);
            final var tempPictureInfo = pictureInfo.getClone();
            tempPictureInfo.setImageLocation(targetHighresFile);
            dumpToXml(tempPictureInfo, bufferedWriter, baseDir);
        } else {
            dumpToXml(pictureInfo, bufferedWriter, baseDir);
        }
    }

    /**
     * Only to be used in the unit tests
     *
     * @param pictureInfo      The PictureInfo
     * @param bufferedWriter   The buffered writer
     * @param highresTargetDir The highres target directory
     * @param copyPics         whether to copy the pictures
     * @throws IOException throws an IOException when things go wrong
     */
    @TestOnly
    public static void writePictureTestOnly(final PictureInfo pictureInfo,
                                            final BufferedWriter bufferedWriter,
                                            final File highresTargetDir,
                                            final boolean copyPics,
                                            final Path baseDir
                                        ) throws IOException {
        writePicture(pictureInfo, bufferedWriter, highresTargetDir, copyPics, baseDir);
    }


    /**
     * Write the collection.dtd file to the target directory.
     *
     * @param directory The directory to write to
     * @return An empty Optional on success, or an Optional containing the error message on failure.
     */
    private static void writeCollectionDTD(final File directory) throws IOException{
        final var bufferedInputStream = new BufferedInputStream(Objects.requireNonNull(JpoWriter.class.getClassLoader().getResource("collection.dtd")).openStream());
        final var targetFile = new File(directory, "collection.dtd");
        Files.copy(
                bufferedInputStream,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        bufferedInputStream.close();
    }

    /**
     * Only to be used in the unit tests
     * @param directory the directory to use
     */
    @TestOnly
    public static void writeCollectionDTDTestOnly(final File directory) throws IOException {
        writeCollectionDTD(directory);
    }

    /**
     * Write the collection.dtd to the output stream
     *
     * @param xmlOutput The directory to write to
     */
    private static void writeDTD(final BufferedWriter xmlOutput) throws IOException {
        try (
                final var bufferedInputStream = new BufferedInputStream(Objects.requireNonNull(JpoWriter.class.getClassLoader().getResource("inline.dtd")).openStream())
        ) {
            IOUtils.copy(bufferedInputStream, xmlOutput, StandardCharsets.UTF_8);
        }
    }


}
