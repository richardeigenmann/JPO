package org.jpo.export;

import com.jcraft.jsch.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.*;
import org.jpo.eventbus.GenerateWebsiteRequest;
import org.jpo.gui.ProgressGui;
import org.jpo.gui.ScalablePicture;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.jpo.gui.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_ERROR;

/*
 * Copyright (C) 2002-2020 Richard Eigenmann.
 *
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the License,
 * or any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA. The license is in gpl.txt. See
 * http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * This class generates a set of HTML pages that allows a user to browse groups
 * of pictures in a web-browser. The resulting html pages can be posted to the
 * Internet. Relative addressing has been used throughout.
 */
public class WebsiteGenerator extends SwingWorker<Integer, String> {

    public static final String JPO_JS = "jpo.js";
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(WebsiteGenerator.class.getName());
    private static final String JPO_CSS = "jpo.css";
    private static final String ROBOTS_TXT = "robots.txt";
    private static final String INDEX_PAGE = "index.htm";
    /**
     * static size of the buffer to be used in copy operations
     */
    private static final int BUFFER_SIZE = 2048;

    /**
     * Array of the files created
     */
    private final List<File> files = new ArrayList<>();
    /**
     * The preferences that define how to render the Html page.
     */
    private final GenerateWebsiteRequest request;
    /**
     * This object holds a reference to the progress GUI for the user.
     */
    private final ProgressGui progressGui;
    /**
     * counter that is incremented with every new picture and is used to
     * determine the number for the next one.
     */
    private int picsWroteCounter = 1;
    /**
     * Indicator that gets set to true if group nodes are being written so that
     * the folder icon is created.
     */
    private boolean folderIconRequired;
    /**
     * Handle for the zipfile
     */
    private ZipOutputStream zipFile;

    /**
     * Creates and starts a Swing Worker that renders the web page files to the
     * target directory. Must be called on the EDT
     *
     * @param request The parameters the user chose on how to render the pages
     */
    private WebsiteGenerator(final GenerateWebsiteRequest request) {
        this.request = request;
        Tools.checkEDT();
        progressGui = new ProgressGui(Integer.MAX_VALUE,
                Settings.getJpoResources().getString("HtmlDistillerThreadTitle"),
                String.format(Settings.getJpoResources().getString("HtmlDistDone"), 0));

        class GetCountWorker extends SwingWorker<Integer, Object> {

            @Override
            public Integer doInBackground() {
                return NodeStatistics.countPicturesRecursively(request.getStartNode());
            }

            @Override
            protected void done() {
                try {
                    progressGui.setMaximum(get());
                    progressGui.setDoneString(String.format(Settings.getJpoResources().getString("HtmlDistDone"), get()));
                } catch (final InterruptedException | ExecutionException ignore) {
                    // Restore interrupted state...
                    Thread.currentThread().interrupt();
                }
            }
        }
        new GetCountWorker().execute();
        execute();
    }

    public static WebsiteGenerator generateWebsite(final GenerateWebsiteRequest request) {
        return new WebsiteGenerator(request);
    }

    static final Map<String, String> CHARACTER_TRANSLATION = new HashMap<>();

    static {
        CHARACTER_TRANSLATION.put(" ", "_");
        CHARACTER_TRANSLATION.put("%20", "_");
        CHARACTER_TRANSLATION.put("&", "_and_");
        CHARACTER_TRANSLATION.put("|", "l");
        CHARACTER_TRANSLATION.put("<", "_");
        CHARACTER_TRANSLATION.put(">", "_");
        CHARACTER_TRANSLATION.put("@", "_");
        CHARACTER_TRANSLATION.put(":", "_");
        CHARACTER_TRANSLATION.put("$", "_");
        CHARACTER_TRANSLATION.put("£", "_");
        CHARACTER_TRANSLATION.put("^", "_");
        CHARACTER_TRANSLATION.put("~", "_");
        CHARACTER_TRANSLATION.put("\"", "_");
        CHARACTER_TRANSLATION.put("'", "_");
        CHARACTER_TRANSLATION.put("`", "_");
        CHARACTER_TRANSLATION.put("?", "_");
        CHARACTER_TRANSLATION.put("[", "_");
        CHARACTER_TRANSLATION.put("]", "_");
        CHARACTER_TRANSLATION.put("{", "_");
        CHARACTER_TRANSLATION.put("}", "_");
        CHARACTER_TRANSLATION.put("(", "_");
        CHARACTER_TRANSLATION.put(")", "_");
        CHARACTER_TRANSLATION.put("*", "_");
        CHARACTER_TRANSLATION.put("+", "_");
        CHARACTER_TRANSLATION.put("/", "_");
        CHARACTER_TRANSLATION.put("\\", "_");
        CHARACTER_TRANSLATION.put("%", "_");
    }

    /**
     * Translates characters which are problematic in a filename into
     * unproblematic characters
     *
     * @param string The filename to clean up
     * @return The cleaned up filename
     */
    public static String cleanupFilename(final String string) {
        String returnString = string;
        for (final Map.Entry<String, String> entry : CHARACTER_TRANSLATION.entrySet()) {
            if (returnString.contains(entry.getKey())) {
                returnString = returnString.replace(entry.getKey(), entry.getValue());
            }
        }
        return returnString;
    }

    private static int checkAck(final InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) {
            return b;
        }
        if (b == -1) {
            return b;
        }

        if (b == 1 || b == 2) {
            final StringBuilder sb = new StringBuilder();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1) { // error
                LOGGER.log(Level.INFO, "{0}", sb);
            }
            if (b == 2) { // fatal error
                LOGGER.log(Level.INFO, "{0}", sb);
            }
        }
        return b;
    }

    /**
     * Adds an image to the ZipFile
     *
     * @param zipFile     The zip to which to add
     * @param pictureInfo The image to add
     * @param highresFile The name of the file to add
     */
    public static void addToZipFile(final ZipOutputStream zipFile, final PictureInfo pictureInfo, File highresFile) {
        LOGGER.log(Level.FINE, "Adding to zipfile: {0}", highresFile);
        try (
                final InputStream in = new FileInputStream(pictureInfo.getImageFile());
                final BufferedInputStream bin = new BufferedInputStream(in)) {

            final ZipEntry entry = new ZipEntry(highresFile.getName());
            zipFile.putNextEntry(entry);

            int count;
            final byte[] data = new byte[BUFFER_SIZE];
            while ((count = bin.read(data, 0, BUFFER_SIZE)) != -1) {
                zipFile.write(data, 0, count);
            }
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Could not create zipfile entry for {0}\n{1}", new Object[]{highresFile, e});
        }

    }

    /**
     * Write the org.jpo.css file to the target directory.
     *
     * @param directory The directory to write to
     */
    public static void writeCss(final File directory) {
        ClassLoader cl = JpoWriter.class.getClassLoader();
        try (
                final InputStream in = Objects.requireNonNull(cl.getResource(JPO_CSS)).openStream();
                final FileOutputStream outStream = new FileOutputStream(new File(directory, JPO_CSS));
                final BufferedInputStream bin = new BufferedInputStream(in);
                final BufferedOutputStream bout = new BufferedOutputStream(outStream)) {
            int c;

            while ((c = bin.read()) != -1) {
                outStream.write(c);
            }

        } catch (final IOException e) {
            JOptionPane.showMessageDialog(
                    Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("CssCopyError") + e.getMessage(),
                    Settings.getJpoResources().getString("genericWarning"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Write the robots.txt file to the target directory.
     *
     * @param directory The directory to write to
     */
    public static void writeRobotsTxt(final File directory) {
        final ClassLoader cl = JpoWriter.class.getClassLoader();
        try (
                final InputStream in = Objects.requireNonNull(cl.getResource(ROBOTS_TXT)).openStream();
                final FileOutputStream outStream = new FileOutputStream(new File(directory, ROBOTS_TXT));
                final BufferedInputStream bin = new BufferedInputStream(in);
                final BufferedOutputStream bout = new BufferedOutputStream(outStream)) {
            int c;

            while ((c = bin.read()) != -1) {
                bout.write(c);
            }

        } catch (final IOException e) {
            JOptionPane.showMessageDialog(
                    Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("CssCopyError") + e.getMessage(),
                    Settings.getJpoResources().getString("genericWarning"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Write the org.jpo.js file to the target directory.
     *
     * @param directory The directory to write to
     */
    public static void writeJpoJs(final File directory) {
        final ClassLoader cl = JpoWriter.class.getClassLoader();
        try (
                final InputStream in = Objects.requireNonNull(cl.getResource(JPO_JS)).openStream();
                final FileOutputStream outStream = new FileOutputStream(new File(directory, JPO_JS));
                final BufferedInputStream bin = new BufferedInputStream(in);
                final BufferedOutputStream bout = new BufferedOutputStream(outStream)) {
            int c;

            while ((c = bin.read()) != -1) {
                bout.write(c);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("CssCopyError") + e.getMessage(),
                    Settings.getJpoResources().getString("genericWarning"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Entry point for the SwingWorker when execute() is called.
     *
     * @return an Integer (not sure what to do with this...)
     * @throws Exception hopefully not
     */
    @Override
    protected Integer doInBackground() throws Exception {
        LOGGER.info("Hitting doInBackground");

        if (request.isGenerateZipfile()) {
            try (
                    final FileOutputStream destination = new FileOutputStream(new File(request.getTargetDirectory(), request.getDownloadZipFileName()));
                    final BufferedOutputStream bout = new BufferedOutputStream(destination)) {
                zipFile = new ZipOutputStream(bout);
            } catch (final FileNotFoundException x) {
                LOGGER.log(Level.SEVERE, "Error creating Zipfile. Continuing without Zip\n{0}", x.toString());
                request.setGenerateZipfile(false);
            }
        }

        writeCss(request.getTargetDirectory());
        files.add(new File(request.getTargetDirectory(), JPO_CSS));
        if (request.isWriteRobotsTxt()) {
            writeRobotsTxt(request.getTargetDirectory());
            files.add(new File(request.getTargetDirectory(), ROBOTS_TXT));
        }
        LOGGER.info("Done static files");

        writeGroup(request.getStartNode());

        try {
            if (request.isGenerateZipfile()) {
                zipFile.close();
            }
        } catch (final IOException x) {
            LOGGER.log(Level.SEVERE, "Error closing Zipfile. Continuing.\n{0}", x.getMessage());
            request.setGenerateZipfile(false);
        }

        if (folderIconRequired) {
            final File folderIconFile = new File(request.getTargetDirectory(), "jpo_folder_icon.gif");
            try (
                    final InputStream inStream = Objects.requireNonNull(WebsiteGenerator.class.getClassLoader().getResource("org/jpo/images/icon_folder.gif")).openStream();
                    final FileOutputStream outStream = new FileOutputStream(folderIconFile);
                    final BufferedInputStream bin = new BufferedInputStream(inStream);
                    final BufferedOutputStream bout = new BufferedOutputStream(outStream)) {
                files.add(folderIconFile);

                int count;
                final byte[] data = new byte[BUFFER_SIZE];
                while ((count = bin.read(data, 0, BUFFER_SIZE)) != -1) {
                    bout.write(data, 0, count);
                }
            } catch (final IOException x) {
                JOptionPane.showMessageDialog(
                        Settings.getAnchorFrame(),
                        "got an IOException copying icon_folder.gif\n" + x.getMessage(),
                        "IOException",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        if (request.getOutputTarget() == GenerateWebsiteRequest.OutputTarget.OUTPUT_SSH_LOCATION) {
            sshCopyToServer(files);
        } else if (request.getOutputTarget() == GenerateWebsiteRequest.OutputTarget.OUTPUT_FTP_LOCATION) {
            ftpCopyToServer(files);
        }

        return Integer.MAX_VALUE;
    }

    /**
     * This method is called by SwingWorker when the background process sends a
     * publish.
     *
     * @param messages A message that will be written to the logfile.
     */
    @Override
    protected void process(final List<String> messages) {
        progressGui.progressIncrement(messages.size());
        messages.stream().forEach(message -> LOGGER.log(Level.INFO, "Message: {0}", message));
    }

    /**
     * SwingWorker calls here when the background task is done.
     */
    @Override
    protected void done() {
        progressGui.switchToDoneMode();
        if (request.isOpenWebsiteAfterRendering()) {
            try {
                final File indexPage = new File(request.getTargetDirectory(), INDEX_PAGE);
                Desktop.getDesktop().browse(indexPage.toURI());
            } catch (final IOException ex) {
                LOGGER.severe(ex.getLocalizedMessage());
            }
        }
    }

    /**
     * This method writes out an HTML page with the small images aligned next to
     * each other. Each Group and picture is created in an html file called
     * jpo_1234.htm except for the first one that gets named index.htm. 1234 is
     * the internal hashCode of the node so that we can translate parents and
     * children to each other.
     *
     * @param groupNode The node at which the extraction is to start.
     */
    public void writeGroup(final SortableDefaultMutableTreeNode groupNode) {
        LOGGER.log(Level.INFO, "Processing Group: {0}", groupNode);
        try {
            publish(String.format("Processing Group: %s", groupNode.toString()));

            File groupFile;
            if (groupNode.equals(request.getStartNode())) {
                groupFile = new File(request.getTargetDirectory(), INDEX_PAGE);
            } else {
                int hashCode = groupNode.hashCode();
                groupFile = new File(request.getTargetDirectory(), "jpo_" + hashCode + ".htm");
            }
            files.add(groupFile);
            final BufferedWriter out = new BufferedWriter(new FileWriter(groupFile));
            final DescriptionsBuffer descriptionsBuffer = new DescriptionsBuffer(request.getPicsPerRow(), out);

            LOGGER.log(Level.INFO, "Writing: {0}", groupFile);
            out.write("<!DOCTYPE HTML>");
            out.newLine();
            out.write("<html xml:lang=\"en\" lang=\"en\">");
            out.newLine();

            out.write("<head>\n\t<link rel=\"StyleSheet\" href=\"org.jpo.css\" type=\"text/css\" media=\"screen\" />\n\t<title>"
                    + ((GroupInfo) groupNode.getUserObject()).getGroupNameHtml()
                    + "</title>\n</head>");
            out.newLine();

            // write body
            out.write("<body>");
            out.newLine();

            out.write("<table  style=\"border-spacing: " + request.getCellspacing()
                    + "px; width: "
                    + (request.getPicsPerRow() * request.getThumbnailWidth() + (request.getPicsPerRow() - 1) * request.getCellspacing())
                    + "px\">");
            out.newLine();

            out.write(String.format("<tr><td colspan=\"%d\">", request.getPicsPerRow()));

            out.write(String.format("<h2>%s</h2>", ((GroupInfo) groupNode.getUserObject()).getGroupNameHtml()));

            if (groupNode.equals(request.getStartNode())) {
                if (request.isGenerateZipfile()) {
                    out.newLine();
                    out.write(String.format("<a href=\"%s\">Download High Resolution Pictures as a Zipfile</a>", request.getDownloadZipFileName()));
                    out.newLine();
                }
            } else {
                //link to parent
                final SortableDefaultMutableTreeNode parentNode = groupNode.getParent();
                String parentLink = "jpo_" + parentNode.hashCode() + ".htm";
                if (parentNode.equals(request.getStartNode())) {
                    parentLink = INDEX_PAGE;
                }

                out.write(String.format("<p>Up to: <a href=\"%s\">%s</a>", parentLink, parentNode.toString()));
                out.newLine();
            }

            out.write("</td></tr>\n<tr>");
            out.newLine();

            int childCount = groupNode.getChildCount();
            int childNumber = 1;
            final Enumeration<TreeNode> kids = groupNode.children();
            while (kids.hasMoreElements() && (!progressGui.getInterruptSemaphore().getShouldInterrupt())) {
                final SortableDefaultMutableTreeNode node = (SortableDefaultMutableTreeNode) kids.nextElement();
                if (node.getUserObject() instanceof GroupInfo gi) {

                    out.write("<td class=\"groupThumbnailCell\" valign=\"bottom\" align=\"left\">");

                    out.write("<a href=\"jpo_" + node.hashCode() + ".htm\">"
                            + "<img src=\"jpo_folder_icon.gif\" width=\"32\" height=\"27\" /></a>");

                    out.write("</td>");
                    out.newLine();

                    descriptionsBuffer.putDescription(gi.getGroupName());

                    // recursively call the method to output that group.
                    writeGroup(node);
                    folderIconRequired = true;
                } else {
                    writePicture(node, out, groupFile, childNumber, childCount, descriptionsBuffer);
                }
                childNumber++;
            }
            if (progressGui.getInterruptSemaphore().getShouldInterrupt()) {
                progressGui.setDoneString(Settings.getJpoResources().getString("htmlDistillerInterrupt"));
            }

            out.write("</tr>");
            descriptionsBuffer.flushDescriptions();

            out.write(String.format("%n<tr><td colspan=\"%d\">", request.getPicsPerRow()));
            out.write(Settings.getJpoResources().getString("LinkToJpo"));
            out.write("</td></tr></table>");
            out.newLine();
            out.write("</body></html>");
            out.close();

        } catch (final IOException x) {
            LOGGER.severe(x.getMessage());
            JOptionPane.showMessageDialog(
                    Settings.getAnchorFrame(),
                    "got an IOException??",
                    "IOException",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * Write html for a picture in the set of webpages.
     *
     * @param pictureNode        The node for which the HTML is to be written
     * @param out                The opened output stream of the overview page to which the
     *                           thumbnail tags should be written
     * @param groupFile          The name of the html file that holds the small
     *                           thumbnails of the parent group
     * @param childNumber        The current position of the picture in the group
     * @param childCount         The total number of pictures in the group
     * @param descriptionsBuffer A buffer for the thumbnails page
     * @throws IOException If there was some sort of IO Error.
     */
    private void writePicture(
            final SortableDefaultMutableTreeNode pictureNode,
            final BufferedWriter out,
            final File groupFile,
            final int childNumber,
            final int childCount,
            final DescriptionsBuffer descriptionsBuffer)
            throws IOException {

        final PictureInfo pictureInfo = (PictureInfo) pictureNode.getUserObject();
        publish(String.format("Writing picture node %d: %s", picsWroteCounter, pictureInfo.toString()));
        descriptionsBuffer.putDescription(pictureInfo.getDescription());

        final String extension = FilenameUtils.getExtension(pictureInfo.getImageFile().getName());
        final File lowresFile = getOutputImageFilename(pictureInfo, "_l." + extension);
        final File midresFile = getOutputImageFilename(pictureInfo, "_m." + extension);
        final File highresFile = getOutputImageFilename(pictureInfo, "_h." + extension);
        final File midresHtmlFile = getOutputImageFilename(pictureInfo, ".htm");
        LOGGER.log(Level.INFO, "Filenames: Lowres: {0}, Midres: {1}, Highres {2}, MidresHtml: {3}", new Object[]{lowresFile, midresFile, highresFile, midresHtmlFile});

        files.add(lowresFile);
        files.add(midresFile);
        if (request.isGenerateZipfile()) {
            addToZipFile(zipFile, pictureInfo, highresFile);
        }

        LOGGER.log(Level.INFO, "Loading: {0}", pictureInfo.getImageLocation());
        final ScalablePicture scp = loadScalablePicture(pictureInfo);
        writeHighresPicture(pictureInfo, highresFile, scp);
        writeLowres(out, pictureInfo, lowresFile, midresFile, midresHtmlFile, scp);
        final Dimension midresDimension = writeMidresPicture(pictureInfo, midresFile, scp);
        if (request.isGenerateMidresHtml()) {
            writeMidres(pictureNode, groupFile, childNumber, childCount, pictureInfo, extension, lowresFile, midresFile, highresFile, midresHtmlFile, midresDimension);
        }
        picsWroteCounter++;
    }

    private void writeMidres(SortableDefaultMutableTreeNode pictureNode, File groupFile, int childNumber, int childCount, PictureInfo pictureInfo, String extension, File lowresFile, File midresFile, File highresFile, File midresHtmlFile, Dimension midresDimension) throws IOException {
        files.add(midresHtmlFile);
        try (
                final BufferedWriter midresHtmlWriter = new BufferedWriter(new FileWriter(midresHtmlFile))) {
            final String groupDescriptionHtml
                    = StringEscapeUtils.escapeHtml4(pictureNode.getParent().getUserObject().toString());

            midresHtmlWriter.write("<!DOCTYPE HTML>");
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("<head>\n\t<link rel=\"StyleSheet\" href=\"org.jpo.css\" type=\"text/css\" media=\"screen\" />\n\t<title>" + groupDescriptionHtml + "</title>\n</head>");
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("<body onload=\"changetext(content[0])\">");
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("<table>");
            midresHtmlWriter.write("<tr><td colspan=\"2\"><h2>" + groupDescriptionHtml + "</h2></td></tr>");
            midresHtmlWriter.newLine();
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("<tr><td class=\"midresPictureCell\">");
            final String imgTag = "<img src=\"%s\" width= \"%d\" height=\"%d\" alt=\"%s\" />".formatted(midresFile.getName(), midresDimension.width, midresDimension.height, StringEscapeUtils.escapeHtml4(pictureInfo.getDescription()));

            if (request.isLinkToHighres()) {
                writeHyperlink(midresHtmlWriter, pictureInfo.getImageLocation(), imgTag);
            } else if (request.isExportHighres()) {
                writeHyperlink(midresHtmlWriter, highresFile.getName(), imgTag);
            } else {
                midresHtmlWriter.write(imgTag);
            }
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("<p>" + StringEscapeUtils.escapeHtml4(pictureInfo.getDescription()));
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("</td>");
            midresHtmlWriter.newLine();
            midresHtmlWriter.newLine();

            // now do the right column
            midresHtmlWriter.write("<td class=\"midresSidebarCell\">");

            if (request.isGenerateMap()) {
                midresHtmlWriter.write("<div id=\"map\"></div>");
                midresHtmlWriter.write("<br />");
                midresHtmlWriter.newLine();
            }

            // Do the matrix with the pictures to click
            final int indexBeforeCurrent = 15;
            final int indexPerRow = 5;
            final int indexToShow = 35;
            final int matrixWidth = 130;
            midresHtmlWriter.write(String.format("Picture %d of %d", childNumber, childCount));
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("<table class=\"numberPickTable\">");
            midresHtmlWriter.newLine();
            final String htmlFriendlyDescription = StringEscapeUtils.escapeHtml4(pictureInfo.getDescription().replace("\'", "\\\\'"));
            final StringBuilder dhtmlArray = startDhtmlArray(childNumber, childCount, pictureInfo, htmlFriendlyDescription);

            int startNumber = (int) Math.floor((childNumber - indexBeforeCurrent - 1) / (double) indexPerRow) * indexPerRow + 1;
            if (startNumber < 1) {
                startNumber = 1;
            }
            int endNumber = startNumber + indexToShow;
            if (endNumber > childCount) {
                endNumber = childCount + 1;
            }
            endNumber = endNumber + indexPerRow - (childCount % indexPerRow);

            for (int i = startNumber; i < endNumber; i++) {
                if ((i - 1) % indexPerRow == 0) {
                    midresHtmlWriter.write("<tr>");
                    midresHtmlWriter.newLine();
                }
                midresHtmlWriter.write("<td class=\"numberPickCell\">");
                if (i <= childCount) {
                    String nodeUrl;
                    String lowresFn;

                    final SortableDefaultMutableTreeNode nde = (SortableDefaultMutableTreeNode) pictureNode.getParent().getChildAt(i - 1);
                    if (nde.getUserObject() instanceof PictureInfo pi) {
                        switch (request.getPictureNaming()) {
                            case PICTURE_NAMING_BY_ORIGINAL_NAME:
                                final String rootName = cleanupFilename(FilenameUtils.getBaseName(pi.getImageFile().getName()));
                                nodeUrl = rootName + ".htm";
                                lowresFn = rootName + "_l." + extension;
                                break;
                            case PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
                                final String convertedNumber = Integer.toString(picsWroteCounter + request.getSequentialStartNumber() + i - childNumber - 1);
                                final String padding = "00000";
                                final String root = padding.substring(convertedNumber.length()) + convertedNumber;
                                nodeUrl = "jpo_" + root + ".htm";
                                lowresFn = "jpo_" + root + "_l." + extension;
                                break;
                            default:  //case GenerateWebsiteRequest.PICTURE_NAMING_BY_HASH_CODE:
                                final int hashCode = nde.hashCode();
                                nodeUrl = "jpo_" + hashCode + ".htm";
                                lowresFn = "jpo_" + nde.hashCode() + "_l." + extension;
                                break;
                        }

                        midresHtmlWriter.write("<a href=\"" + nodeUrl + "\"");
                        final String htmlFriendlyDescription2 = StringEscapeUtils.escapeHtml4(((PictureInfo) nde.getUserObject()).getDescription().replace("\'", "\\\\'"));
                        if (request.isGenerateMouseover()) {
                            midresHtmlWriter.write(String.format(" onmouseover=\"changetext(content[%d])\" onmouseout=\"changetext(content[0])\"", i));
                            dhtmlArray.append(String.format("content[%d]='", i));

                            dhtmlArray.append(String.format("<p>Picture %d/%d:</p>", i, childCount));
                            dhtmlArray.append(String.format("<p><img src=\"%s\" width=%d alt=\"Thumbnail\"></p>", lowresFn, matrixWidth - 10));
                            dhtmlArray.append("<p><i>").append(htmlFriendlyDescription2).append("</i></p>'\n");
                        } else {
                            dhtmlArray.append(String.format("<p>Item %d/%d:</p>", i, childCount));
                            dhtmlArray.append("<p><i>").append(htmlFriendlyDescription2).append("</p></i>'\n");
                        }
                    }
                    midresHtmlWriter.write(">");
                    if (i == childNumber) {
                        midresHtmlWriter.write("<b>");
                    }
                    midresHtmlWriter.write(Integer.toString(i));
                    if (i == childNumber) {
                        midresHtmlWriter.write("</b>");
                    }
                    midresHtmlWriter.write("</a>");
                } else {
                    midresHtmlWriter.write("&nbsp;");
                }
                midresHtmlWriter.write("</td>");
                midresHtmlWriter.newLine();
                if (i % indexPerRow == 0) {
                    midresHtmlWriter.write("</tr>");
                    midresHtmlWriter.newLine();
                }
            }
            midresHtmlWriter.write("</table>");
            midresHtmlWriter.newLine();
            // End of picture matrix

            midresHtmlWriter.newLine();

            writeLinks(pictureNode, groupFile, childNumber, childCount, pictureInfo, lowresFile, highresFile, midresHtmlWriter);

            midresHtmlWriter.newLine();
            midresHtmlWriter.write("<p>" + Settings.getJpoResources().getString("LinkToJpo") + "</p>");
            midresHtmlWriter.newLine();

            if (request.isGenerateMouseover()) {
                midresHtmlWriter.write("<ilayer id=\"d1\" width=\"" + matrixWidth + "\" height=\"200\" visibility=\"hide\">");
                midresHtmlWriter.newLine();
                midresHtmlWriter.write("<layer id=\"d2\" width=\"" + matrixWidth + "\" height=\"200\">");
                midresHtmlWriter.newLine();
                midresHtmlWriter.write("<div id=\"descriptions\" class=\"sidepanelMouseover\">");
                midresHtmlWriter.newLine();
                midresHtmlWriter.write("</div></layer></ilayer>");
            }

            midresHtmlWriter.newLine();
            midresHtmlWriter.write("</td></tr>");
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("</table>");
            midresHtmlWriter.newLine();

            if (request.isGenerateMouseover()) {
                writeJpoJs(request.getTargetDirectory());
                files.add(new File(request.getTargetDirectory(), JPO_JS));
                midresHtmlWriter.write("<script type=\"text/javascript\" src=\"org.jpo.js\" ></script>");
                midresHtmlWriter.newLine();
                midresHtmlWriter.write("<script type=\"text/javascript\">");
                midresHtmlWriter.newLine();
                midresHtmlWriter.write("<!-- ");
                midresHtmlWriter.newLine();
                midresHtmlWriter.write("/* Textual Tooltip Script- (c) Dynamic Drive (www.dynamicdrive.com) For full source code, installation instructions, 100's more DHTML scripts, and Terms Of Use, visit dynamicdrive.com */ ");
                midresHtmlWriter.newLine();
                midresHtmlWriter.write("var content=new Array() ");
                midresHtmlWriter.newLine();
                midresHtmlWriter.write(dhtmlArray.toString());
                midresHtmlWriter.newLine();
                midresHtmlWriter.write("//-->");
                midresHtmlWriter.newLine();
                midresHtmlWriter.write("</script>");
                midresHtmlWriter.newLine();
            }

            if (request.isGenerateMap()) {
                midresHtmlWriter.write("<script type=\"text/javascript\"> <!--");
                midresHtmlWriter.newLine();
                midresHtmlWriter.write(String.format("var lat=%f; var lng=%f;", pictureInfo.getLatLng().x, pictureInfo.getLatLng().y));
                midresHtmlWriter.newLine();
                midresHtmlWriter.write("--> </script>");
                midresHtmlWriter.newLine();
                midresHtmlWriter.write("<script type=\"text/javascript\" src=\"http://maps.google.com/maps/api/js?sensor=false\"></script>");
                midresHtmlWriter.newLine();
            }

            midresHtmlWriter.write("</body></html>");
        }
    }

    private void writeHyperlink(final Writer out, final String target, final String linkText) throws IOException {
        out.write("<a href=\"" + target + "\">" + linkText + "</a>");
    }

    @NotNull
    private Dimension writeMidresPicture(PictureInfo pictureInfo, File midresFile, ScalablePicture scp) {
        final Dimension midresDimension = new Dimension();
        scp.setScaleSize(request.getMidresDimension());
        LOGGER.log(Level.FINE, "Scaling: {0}", pictureInfo.getImageLocation());
        scp.scalePicture();
        LOGGER.log(Level.FINE, "Writing: {0}", midresFile);
        scp.setJpgQuality(request.getMidresJpgQuality());
        scp.writeScaledJpg(midresFile);
        midresDimension.width = scp.getScaledWidth();
        midresDimension.height = scp.getScaledHeight();
        return midresDimension;
    }

    private void writeLowres(BufferedWriter out, PictureInfo pictureInfo, File lowresFile, File midresFile, File midresHtmlFile, ScalablePicture scp) throws IOException {
        scp.setScaleSize(request.getThumbnailDimension());
        LOGGER.log(Level.INFO, "Scaling: {0}", pictureInfo.getImageLocation());
        scp.scalePicture();
        LOGGER.log(Level.INFO, "Writing: {0}", lowresFile);
        scp.setJpgQuality(request.getLowresJpgQuality());
        scp.writeScaledJpg(lowresFile);
        int w = scp.getScaledWidth();
        int h = scp.getScaledHeight();

        out.write("<td class=\"pictureThumbnailCell\" id=\"" + StringEscapeUtils.escapeHtml4(lowresFile.getName()) + "\">");

        // write an anchor so the up come back
        // but only if we are generating MidresHTML pages
        out.write("<a href=\"");
        if (request.isGenerateMidresHtml()) {
            out.write(midresHtmlFile.toPath().getFileName().toString());
        } else {
            out.write(midresFile.getName());
        }
        out.write("\">" + "<img src=\""
                + lowresFile.getName()
                + "\" width=\""
                + w
                + "\" height=\""
                + h
                + "\" alt=\""
                + StringEscapeUtils.escapeHtml4(pictureInfo.getDescription())
                + "\" "
                + " />"
                + "</a>");

        out.write("</td>");
        out.newLine();
    }

    private void writeHighresPicture(PictureInfo pictureInfo, File highresFile, ScalablePicture scp) {
        // copy the picture to the target directory
        if (request.isExportHighres()) {
            files.add(highresFile);
            if (request.isRotateHighres() && (pictureInfo.getRotation() != 0)) {
                LOGGER.log(Level.FINE, "Copying and rotating picture {0} to {1}", new Object[]{pictureInfo.getImageLocation(), highresFile});
                scp.setScaleFactor(1);
                scp.scalePicture();
                scp.setJpgQuality(request.getMidresJpgQuality());
                scp.writeScaledJpg(highresFile);
            } else {
                LOGGER.log(Level.FINE, "Copying picture {0} to {1}", new Object[]{pictureInfo.getImageLocation(), highresFile});
                Tools.copyPicture(pictureInfo.getImageFile(), highresFile);
            }
        }
    }

    @NotNull
    private ScalablePicture loadScalablePicture(PictureInfo pictureInfo) throws IOException {
        final ScalablePicture scp = new ScalablePicture();
        scp.setQualityScale();
        scp.setScaleSteps(request.getScalingSteps());
        scp.loadPictureImd(pictureInfo.getImageFile(), pictureInfo.getRotation());

        LOGGER.log(Level.INFO, "Done Loading: {0}", pictureInfo.getImageLocation());
        if (scp.getStatusCode() == SCALABLE_PICTURE_ERROR) {
            LOGGER.log(Level.SEVERE, "Problem reading image {0} using ThumbnailPicture instead", pictureInfo.getImageLocation());
            File file;
            try {
                file = new File(Objects.requireNonNull(WebsiteGenerator.class.getClassLoader().getResource("org/jpo/images/broken_thumbnail.gif")).toURI());
            } catch (final URISyntaxException e) {
                throw new IOException("Could not load the broken_thumbnail.gif resource: " + e.getMessage());
            }
            scp.loadPictureImd(file, 0f);
        }
        return scp;
    }

    @NotNull
    private StringBuilder startDhtmlArray(int childNumber, int childCount, PictureInfo pictureInfo, String htmlFriendlyDescription) {
        final StringBuilder dhtmlArray = new StringBuilder(String.format("content[0]='" + "<p><strong>Picture</strong> %d of %d:</p><p><b>Description:</b><br>%s</p>", childNumber, childCount, htmlFriendlyDescription));
        if (pictureInfo.getCreationTime().length() > 0) {
            dhtmlArray.append("<p><strong>Date:</strong><br>").append(pictureInfo.getCreationTime().replace("\'", "\\\\'")).append("</p>");
        }

        if (pictureInfo.getPhotographer().length() > 0) {
            dhtmlArray.append("<strong>Photographer:</strong><br>").append(pictureInfo.getPhotographer().replace("\'", "\\\\'")).append("<br>");
        }
        if (pictureInfo.getComment().length() > 0) {
            dhtmlArray.append("<b>Comment:</b><br>").append(pictureInfo.getComment().replace("\'", "\\\\'")).append("<br>");
        }
        if (pictureInfo.getFilmReference().length() > 0) {
            dhtmlArray.append("<strong>Film Reference:</strong><br>").append(pictureInfo.getFilmReference().replace("\'", "\\\\'")).append("<br>");
        }
        if (pictureInfo.getCopyrightHolder().length() > 0) {
            dhtmlArray.append("<strong>Copyright Holder:</strong><br>").append(pictureInfo.getCopyrightHolder().replace("\'", "\\\\'")).append("<br>");
        }

        dhtmlArray.append("'\n");
        return dhtmlArray;
    }

    private void writeLinks(final SortableDefaultMutableTreeNode pictureNode, final File groupFile, final int childNumber, final int childCount, final PictureInfo pictureInfo, final File lowresFile, final File highresFile, final BufferedWriter midresHtmlWriter) throws IOException {
        writeHyperlink(midresHtmlWriter, groupFile.getName() + "#" + StringEscapeUtils.escapeHtml4(lowresFile.getName()), "Up");
        midresHtmlWriter.write("&nbsp;");
        midresHtmlWriter.newLine();
        if (childNumber != 1) {
            writeHyperlink(midresHtmlWriter, getPreviousHtmlFilename(pictureNode, childNumber), "Previous");
            midresHtmlWriter.write("&nbsp;");
        }
        if (request.isLinkToHighres()) {
            writeHyperlink(midresHtmlWriter, pictureInfo.getImageLocation(), "Highres");
            midresHtmlWriter.write("&nbsp;");
        } else if (request.isExportHighres()) {
            // Link to Highres in target directory
            writeHyperlink(midresHtmlWriter, highresFile.getName(), "Highres");
            midresHtmlWriter.write("&nbsp;");
        }
        if (childNumber != childCount) {
            writeHyperlink(midresHtmlWriter, getNextHtmlFilename(pictureNode, childNumber), "Next");
            midresHtmlWriter.newLine();
        }
        if (request.isGenerateZipfile()) {
            midresHtmlWriter.write("<br>");
            writeHyperlink(midresHtmlWriter, request.getDownloadZipFileName(), "Download Zip");
            midresHtmlWriter.newLine();
        }
        midresHtmlWriter.write("</p>");
    }

    @NotNull
    private String getPreviousHtmlFilename(SortableDefaultMutableTreeNode pictureNode, int childNumber) {
        String previousHtmlFilename;
        switch (request.getPictureNaming()) {
            case PICTURE_NAMING_BY_ORIGINAL_NAME:
                SortableDefaultMutableTreeNode priorNode = (SortableDefaultMutableTreeNode) (pictureNode.getParent()).getChildAt(childNumber - 2);
                Object userObject = priorNode.getUserObject();
                if (userObject instanceof PictureInfo pi) {
                    previousHtmlFilename = cleanupFilename(FilenameUtils.getBaseName(pi.getImageFile().getName() + ".htm"));
                } else {
                    previousHtmlFilename = INDEX_PAGE; // actually something has gone horribly wrong
                }
                break;
            case PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
                final String convertedNumber = Integer.toString(picsWroteCounter + request.getSequentialStartNumber() - 2);
                final String padding = "00000";
                final String formattedNumber = padding.substring(convertedNumber.length()) + convertedNumber;
                previousHtmlFilename = "jpo_" + formattedNumber + ".htm";
                break;
            default:  //case GenerateWebsiteRequest.PICTURE_NAMING_BY_HASH_CODE:
                int hashCode = (pictureNode.getParent()).getChildAt(childNumber - 2).hashCode();
                previousHtmlFilename = "jpo_" + hashCode + ".htm";
                break;
        }
        return previousHtmlFilename;
    }

    private String getNextHtmlFilename(final SortableDefaultMutableTreeNode pictureNode, final int childNumber) throws IOException {
        String nextHtmlFilename;
        switch (request.getPictureNaming()) {
            case PICTURE_NAMING_BY_ORIGINAL_NAME:
                SortableDefaultMutableTreeNode priorNode = (SortableDefaultMutableTreeNode) (pictureNode.getParent()).getChildAt(childNumber);
                Object userObject = priorNode.getUserObject();
                if (userObject instanceof PictureInfo pi) {
                    nextHtmlFilename = cleanupFilename(FilenameUtils.getBaseName((pi.getImageFile().getName()) + ".htm"));
                } else {
                    nextHtmlFilename = INDEX_PAGE; // actually something has gone horribly wrong
                }
                break;
            case PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
                final String convertedNumber = Integer.toString(picsWroteCounter + request.getSequentialStartNumber());
                final String padding = "00000";
                final String formattedNumber = padding.substring(convertedNumber.length()) + convertedNumber;
                nextHtmlFilename = "jpo_" + formattedNumber + ".htm";
                break;
            default:  //case GenerateWebsiteRequest.PICTURE_NAMING_BY_HASH_CODE:
                final int hashCode = (pictureNode.getParent()).getChildAt(childNumber).hashCode();
                nextHtmlFilename = "jpo_" + hashCode + ".htm";
                break;
        }
        return nextHtmlFilename;
    }

    private File getOutputImageFilename(final PictureInfo pictureInfo, final String extension) {
        switch (request.getPictureNaming()) {
            case PICTURE_NAMING_BY_ORIGINAL_NAME -> {
                final String rootName = cleanupFilename(FilenameUtils.getBaseName(pictureInfo.getImageFile().getName()));
                return new File(request.getTargetDirectory(), rootName + extension);
            }
            case PICTURE_NAMING_BY_SEQUENTIAL_NUMBER -> {
                final String convertedNumber = Integer.toString(picsWroteCounter + request.getSequentialStartNumber() - 1);
                final String padding = "00000";
                final String formattedNumber = padding.substring(convertedNumber.length()) + convertedNumber;
                final String root = "jpo_" + formattedNumber;
                return new File(request.getTargetDirectory(), root + extension);
            }
            default -> {
                final String fn = "jpo_" + pictureInfo.hashCode();
                return new File(request.getTargetDirectory(), fn + extension);
            }
        }
    }

    private void sshCopyToServer(final List<File> files) {
        LOGGER.info("Setting up ssh connection:");
        final JSch jsch = new JSch();
        try {
            LOGGER.log(Level.INFO, "Setting up session for user: {0} server: {1} port: {2} and connecting...", new Object[]{request.getSshUser(), request.getSshServer(), request.getSshPort()});
            final Session session = jsch.getSession(request.getSshUser(), request.getSshServer(), request.getSshPort());
            if (request.getSshAuthType().equals(GenerateWebsiteRequest.SshAuthType.SSH_AUTH_PASSWORD)) {
                session.setPassword(request.getSshPassword());
            } else {
                jsch.addIdentity(request.getSshKeyFile());
            }
            final Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            for (final File file : files) {
                publish(String.format("scp %s", file.getName()));
                scp(session, file);
            }

            session.disconnect();
        } catch (final JSchException | IOException ex) {
            LOGGER.severe(ex.getMessage());
        }
    }

    private void scp(final Session session, final File file) throws JSchException, IOException {
        // exec 'scp -t rfile' remotely
        String command = "cd " + request.getSshTargetDir() + "; scp -p -t " + file.getName();

        LOGGER.info("Opening Channel \"exec\"...");
        Channel channel = session.openChannel("exec");
        LOGGER.log(Level.INFO, "Setting command: {0}", command);
        ((ChannelExec) channel).setCommand(command);

        try (
                // get I/O streams for remote scp
                final OutputStream out = channel.getOutputStream();
                final InputStream in = channel.getInputStream()) {
            LOGGER.info("Connecting Channel...");
            channel.connect();

            if (checkAck(in) != 0) {
                LOGGER.info("No Ack 1");
            }

            command = "T " + (file.lastModified() / 1000) + " 0";
            // The access time should be sent here,
            // but it is not accessible with JavaAPI ;-<
            command += (" " + (file.lastModified() / 1000) + " 0\n");
            LOGGER.log(Level.INFO, "Command: {0}", command);
            out.write(command.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                LOGGER.info("No Ack 2");
            }

            // send "C0644 filesize filename", where filename should not include '/'
            long filesize = file.length();
            command = "C0644 " + filesize + " ";
            command += file.getName();
            command += "\n";
            LOGGER.log(Level.INFO, "Command: {0}", command);
            out.write(command.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                LOGGER.info("No Ack 3");
            }

            // send a content of lfile
            try (
                    final FileInputStream fis = new FileInputStream(file)) {
                final byte[] buf = new byte[1024];
                while (true) {
                    LOGGER.log(Level.INFO, "Sending bytes: {0}", buf.length);
                    int len = fis.read(buf, 0, buf.length);
                    if (len <= 0) {
                        break;
                    }
                    out.write(buf, 0, len);
                }

                LOGGER.info("Sending \0");
                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
            }
            if (checkAck(in) != 0) {
                LOGGER.info("No Ack 4");
            }

            LOGGER.info(command);
            channel.disconnect();
        }
    }

    /*
     * to test you can start up a simple ftp server with docker:
     * docker run -d -v <directory on your host>:/home/vsftpd  -p 20:20 -p 21:21 -p 47400-47470:47400-47470 -e FTP_USER=user -e FTP_PASS=password -e PASV_ADDRESS=<ip addres, yes ip, not hostname!> --name ftp --restart=always bogem/ftp
     * username = user
     * password = password
     */
    private void ftpCopyToServer(final Collection<File> files) {
        LOGGER.info("Setting up ftp connection:");
        final FTPClient ftp = new FTPClient();
        try {
            ftp.connect(request.getFtpServer(), request.getFtpPort());
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                LOGGER.severe("FTP server refused connection.");
                throw new IOException("FTP server refused connection.");
            }

            LOGGER.info("Good connection:");
            if (!ftp.login(request.getFtpUser(), request.getFtpPassword())) {
                LOGGER.severe("Could not log on.");
                throw new IOException("Could not log on");
            }

            LOGGER.info("Remote system is " + ftp.getSystemType());
            //ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
            ftp.addProtocolCommandListener(new ProtocolCommandListener() {
                @Override
                public void protocolCommandSent(ProtocolCommandEvent event) {
                    LOGGER.log(Level.INFO, "Sent: {0}", event.getMessage());
                }

                @Override
                public void protocolReplyReceived(ProtocolCommandEvent event) {
                    LOGGER.log(Level.INFO, "Received: {0}", event.getMessage());
                }
            });
            boolean binarymode = ftp.setFileType(FTP.BINARY_FILE_TYPE);
            LOGGER.log(Level.INFO, "Binarymode: {0}", binarymode);
            ftp.enterLocalPassiveMode();
            for (final File file : files) {
                try (final InputStream input = new BufferedInputStream(new FileInputStream(file));) {
                    final String remote = request.getFtpTargetDir() + file.getName();
                    LOGGER.log(Level.INFO, "Putting file {0} to {1}:{2}", new Object[]{file, request.getFtpServer(), remote});
                    boolean done = ftp.storeFile(remote, input);
                    LOGGER.log(Level.INFO, "stored file successfully: {0}", done);
                }
            }
        } catch (final IOException ex) {
            Logger.getLogger(WebsiteGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (ftp.isConnected()) {
                    ftp.logout();
                    ftp.disconnect();
                }
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Inner class that keeps a buffer of the picture descriptions and will
     * output a table row with the buffered descriptions when the buffer has
     * reached it's limit.
     */
    private class DescriptionsBuffer {

        /**
         * The number of columns on the Thumbnail page.
         */
        private final int columns;
        /**
         * The HTML page for the Thumbnails.
         */
        private final BufferedWriter out;
        /**
         * An array holding the strings of the pictures.
         */
        private final String[] descriptions;
        /**
         * A counter variable.
         */
        private int picCounter;

        /**
         * Creates a Description buffer with the indicated number of columns.
         *
         * @param columns The number of columns being generated
         * @param out     The Thumbnail page
         */
        DescriptionsBuffer(final int columns, final BufferedWriter out) {
            this.columns = columns;
            this.out = out;
            descriptions = new String[request.getPicsPerRow()];
        }

        /**
         * Adds the supplied string to the buffer and performs a check whether
         * the buffer is full If the buffer is full it flushes it.
         *
         * @param description The String to be added.
         * @throws IOException if anything went wrong with the writing.
         */
        public void putDescription(final String description) throws IOException {
            descriptions[picCounter] = description;
            picCounter++;
            flushIfNescessary();
        }

        /**
         * Checks whether the buffer is full and if so will terminate the
         * current line, flush the buffer and start a new line.
         *
         * @throws IOException if something went wrong with writing.
         */
        public void flushIfNescessary() throws IOException {
            if (picCounter == columns) {
                out.write("</tr>");
                flushDescriptions();
                out.write("<tr>");

            }
        }

        /**
         * method that writes the descriptions[] array to the html file. As each
         * picture's img tag was written to the file the description was kept in
         * an array. This method is called each time the row of img is full. The
         * method is also called when the last picture has been written. The
         * array elements are set to null after writing so that the last row can
         * determine when to stop writing the pictures (the row can of course be
         * incomplete).
         *
         * @throws IOException If writing didn't work.
         */
        public void flushDescriptions() throws IOException {
            out.newLine();
            out.write("<tr>");
            out.newLine();

            for (int i = 0; i < columns; i++) {
                if (descriptions[i] != null) {
                    out.write("<td class=\"descriptionCell\">");

                    out.write(StringEscapeUtils.escapeHtml4(descriptions[i]));

                    out.write("</td>");
                    out.newLine();
                    descriptions[i] = null;
                }
            }
            picCounter = 0;
            out.write("</tr>");
            out.newLine();
        }
    }


}
