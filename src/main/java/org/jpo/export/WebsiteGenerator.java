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
import org.jetbrains.annotations.TestOnly;
import org.jpo.datamodel.*;
import org.jpo.eventbus.GenerateWebsiteRequest;
import org.jpo.gui.ProgressGui;
import org.jpo.gui.ScalablePicture;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
    public static final String A_HREF = "<a href=\"";
    static final Map<String, String> CHARACTER_TRANSLATION = new HashMap<>();
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(WebsiteGenerator.class.getName());
    private static final String JPO_CSS = "jpo.css";
    private static final String ROBOTS_TXT = "robots.txt";
    private static final String INDEX_PAGE = "index.htm";
    public static final String FOLDER_ICON = "icon_folder_large.jpg";

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
     * Write the jpo.css file to the target directory.
     *
     * @param directory The directory to write to
     */
    public static void writeCss(final File directory) {
        try (
                final InputStream in = Objects.requireNonNull(JpoWriter.class.getClassLoader().getResource(JPO_CSS)).openStream();
                final FileOutputStream outStream = new FileOutputStream(new File(directory, JPO_CSS));
                final BufferedInputStream bin = new BufferedInputStream(in);
                final BufferedOutputStream bout = new BufferedOutputStream(outStream)) {
            bin.transferTo(bout);

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
        try (
                final InputStream in = Objects.requireNonNull(JpoWriter.class.getClassLoader().getResource(ROBOTS_TXT)).openStream();
                final FileOutputStream outStream = new FileOutputStream(new File(directory, ROBOTS_TXT));
                final BufferedInputStream bin = new BufferedInputStream(in);
                final BufferedOutputStream bout = new BufferedOutputStream(outStream)) {
            bin.transferTo(bout);

        } catch (final IOException e) {
            JOptionPane.showMessageDialog(
                    Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("CssCopyError") + e.getMessage(),
                    Settings.getJpoResources().getString("genericWarning"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Write the jpo.js file to the target directory.
     *
     * @param directory The directory to write to
     */
    public static void writeJpoJs(final File directory) {
        try (
                final InputStream in = Objects.requireNonNull(JpoWriter.class.getClassLoader().getResource(JPO_JS)).openStream();
                final FileOutputStream outStream = new FileOutputStream(new File(directory, JPO_JS));
                final BufferedInputStream bin = new BufferedInputStream(in);
                final BufferedOutputStream bout = new BufferedOutputStream(outStream)) {
            bin.transferTo(bout);

        } catch (final IOException e) {
            JOptionPane.showMessageDialog(
                    Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("CssCopyError") + e.getMessage(),
                    Settings.getJpoResources().getString("genericWarning"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @TestOnly
    public static void writeFolderIconTest(final GenerateWebsiteRequest request, final List<File> files) throws IOException {
        writeFolderIcon(request, files);
    }

    private static void writeFolderIcon(final GenerateWebsiteRequest request, final List<File> files) throws IOException {
        final File folderIconFile = new File(request.getTargetDirectory(), FOLDER_ICON);
        try (
                final InputStream inStream = Objects.requireNonNull(WebsiteGenerator.class.getClassLoader().getResource(FOLDER_ICON)).openStream();
                final FileOutputStream outStream = new FileOutputStream(folderIconFile);
                final BufferedInputStream bin = new BufferedInputStream(inStream);
                final BufferedOutputStream bout = new BufferedOutputStream(outStream);) {
            files.add(folderIconFile);
            bin.transferTo(bout);
        } catch (final IOException e) {
            throw (e);
        }
    }

    @TestOnly
    public static void generateZipfileTest(final GenerateWebsiteRequest request) {
        generateZipfile(request);
    }

    /**
     * Searches for all the pictures in the request's startNode and adds them to a zipfile if isGenerateZipfile is true;
     *
     * @param request
     */
    private static void generateZipfile(final GenerateWebsiteRequest request) {
        if (!request.isGenerateZipfile()) {
            return;
        }
        LOGGER.log(Level.INFO, "Generating Zipfile for node {0}, sequentialStartNumber: {1}",
                new Object[]{request.getCellspacing(), request.getSequentialStartNumber()});
        try (
                final FileOutputStream destination
                        = new FileOutputStream(new File(request.getTargetDirectory(), request.getDownloadZipFileName()));
                final BufferedOutputStream bout = new BufferedOutputStream(destination);
                final ZipOutputStream zipFile = new ZipOutputStream(bout);) {
            int picWroteCounter = request.getSequentialStartNumber();
            for (final SortableDefaultMutableTreeNode p : request.getStartNode().getChildPictureNodes(true)) {
                final PictureInfo pictureInfo = (PictureInfo) p.getUserObject();
                addZipFileEntry(request, zipFile, picWroteCounter, p, pictureInfo);
                picWroteCounter++;
            }
        } catch (final IOException x) {
            LOGGER.log(Level.SEVERE, "Error creating Zipfile. Continuing without Zip\n{0}", x.toString());
            request.setGenerateZipfile(false);
        }
    }

    private static void addZipFileEntry(final GenerateWebsiteRequest request, final ZipOutputStream zipFile, final int picWroteCounter, final SortableDefaultMutableTreeNode p, final PictureInfo pictureInfo) {
        try (
                final InputStream in = new FileInputStream(pictureInfo.getImageFile());
                final BufferedInputStream bin = new BufferedInputStream(in)) {

            final File highresFile = getOutputImageFile(request, pictureInfo, "_h.", picWroteCounter, true);
            final ZipEntry entry = new ZipEntry(highresFile.getName());
            LOGGER.log(Level.INFO, "Adding to zipfile: {0}", highresFile);
            zipFile.putNextEntry(entry);
            bin.transferTo(zipFile);
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Could not create zipfile entry for {0}\n{1}", new Object[]{p, e});
        }
    }

    private static void writePageHeader(final BufferedWriter midresHtmlWriter, final String pageTitle) throws IOException {
        midresHtmlWriter.write("<!DOCTYPE HTML>");
        midresHtmlWriter.newLine();
        midresHtmlWriter.write("<head>\n\t<link rel=\"StyleSheet\" href=\"" + JPO_CSS + "\" type=\"text/css\" media=\"screen\" />\n\t<title>" + pageTitle + "</title>\n</head>");
        midresHtmlWriter.newLine();
    }

    private static File getOutputImageFile(final GenerateWebsiteRequest request, final PictureInfo pictureInfo, final String suffix, final int picsWroteCounter, final boolean keepExtension) {
        final String extension = keepExtension ? FilenameUtils.getExtension(pictureInfo.getImageFile().getName()) : "";
        switch (request.getPictureNaming()) {
            case PICTURE_NAMING_BY_ORIGINAL_NAME -> {
                final String rootName = cleanupFilename(FilenameUtils.getBaseName(pictureInfo.getImageFile().getName()));
                return new File(request.getTargetDirectory(), rootName + suffix + extension);
            }
            case PICTURE_NAMING_BY_SEQUENTIAL_NUMBER -> {
                final String convertedNumber = Integer.toString(picsWroteCounter);
                final String padding = "00000";
                final String formattedNumber = padding.substring(convertedNumber.length()) + convertedNumber;
                final String root = "jpo_" + formattedNumber;
                return new File(request.getTargetDirectory(), root + suffix + extension);
            }
            default -> {
                final String fn = "jpo_" + pictureInfo.hashCode();
                return new File(request.getTargetDirectory(), fn + suffix + extension);
            }
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

        generateZipfile(request);

        writeCss(request.getTargetDirectory());
        files.add(new File(request.getTargetDirectory(), JPO_CSS));
        if (request.isWriteRobotsTxt()) {
            writeRobotsTxt(request.getTargetDirectory());
            files.add(new File(request.getTargetDirectory(), ROBOTS_TXT));
        }
        LOGGER.info("Done static files");

        picsWroteCounter = request.getSequentialStartNumber();
        assignFilenames(request);
        writeGroup(request.getStartNode());

        if (folderIconRequired) {
            writeFolderIcon(request, files);
        }
        if (request.getOutputTarget() == GenerateWebsiteRequest.OutputTarget.OUTPUT_SSH_LOCATION) {
            sshCopyToServer(files);
        } else if (request.getOutputTarget() == GenerateWebsiteRequest.OutputTarget.OUTPUT_FTP_LOCATION) {
            ftpCopyToServer(files);
        }

        return Integer.MAX_VALUE;
    }


    final Map<SortableDefaultMutableTreeNode, File> highresFiles = new HashMap<>();
    final Map<SortableDefaultMutableTreeNode, File> midresFiles = new HashMap<>();
    final Map<SortableDefaultMutableTreeNode, File> lowresFiles = new HashMap<>();
    final Map<SortableDefaultMutableTreeNode, File> lowresHtmlFiles = new HashMap<>();
    final Map<SortableDefaultMutableTreeNode, File> midresHtmlFiles = new HashMap<>();

    /**
     * The sequential counting of files gets complicated in tree structures so I will pre-
     * allocate the filenames in a first pass and then look them up.
     *
     * @param request The request
     */
    private void assignFilenames(final GenerateWebsiteRequest request) {
        final Enumeration<TreeNode> e = request.getStartNode().breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            final SortableDefaultMutableTreeNode node = (SortableDefaultMutableTreeNode) e.nextElement();
            if (node.getUserObject() instanceof PictureInfo pictureInfo) {
                lowresFiles.put(node, getOutputImageFile(request, pictureInfo, "_l.", picsWroteCounter, true));
                midresFiles.put(node, getOutputImageFile(request, pictureInfo, "_m.", picsWroteCounter, true));
                highresFiles.put(node, getOutputImageFile(request, pictureInfo, "_h.", picsWroteCounter, true));
                midresHtmlFiles.put(node, getOutputImageFile(request, pictureInfo, ".htm", picsWroteCounter, false));
            } else {
                lowresHtmlFiles.put(node, new File(request.getTargetDirectory(), "jpo_" + node.hashCode() + ".htm"));
                lowresFiles.put(node, new File(request.getTargetDirectory(), FOLDER_ICON));
            }
        }

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
        try {
            publish(String.format("Processing Group: %s", groupNode.toString()));

            final File groupFile = getGroupFile(groupNode);
            files.add(groupFile);
            final BufferedWriter out = new BufferedWriter(new FileWriter(groupFile));
            final String title = ((GroupInfo) groupNode.getUserObject()).getGroupNameHtml();

            writeHtmlHeader(out, title);
            startGroupTable(groupNode, out, request);
            writeLinkToZipFile(groupNode, out);
            writeLinkToParentGroup(groupNode, out);

            out.write("</td></tr>\n<tr>");
            out.newLine();

            final List<String> rowDescriptions = new ArrayList<>();
            for (int i = 0; i < groupNode.getChildCount(); i++) {
                final SortableDefaultMutableTreeNode node = (SortableDefaultMutableTreeNode) groupNode.getChildAt(i);
                rowDescriptions.add(writeLowresCell(out, node, groupFile, i));
                if ((rowDescriptions.size() % request.getPicsPerRow() == 0) || (i == groupNode.getChildCount() - 1)) {
                    // if we have a full fow of descriptions or if we are processing the last child
                    writeAndClearRowDescriptions(out, rowDescriptions);
                }
            }

            writeGroupCellFooter(out);
            out.close();

            if (progressGui.getInterruptSemaphore().getShouldInterrupt()) {
                progressGui.setDoneString(Settings.getJpoResources().getString("htmlDistillerInterrupt"));
            }
        } catch (final IOException x) {
            LOGGER.severe(x.getMessage());
            JOptionPane.showMessageDialog(
                    Settings.getAnchorFrame(),
                    "got an IOException??",
                    "IOException",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String writeLowresCell(final BufferedWriter out, final SortableDefaultMutableTreeNode node, final File groupFile, final int childNumber) throws IOException {
        if (node.getUserObject() instanceof GroupInfo gi) {
            writeLowresGroupCell(out, node);
            return gi.getGroupName();
        } else {
            writePicture(node, out, groupFile, childNumber);
            return ((PictureInfo) node.getUserObject()).getDescription();
        }
    }

    private void writeLowresGroupCell(final BufferedWriter out, final SortableDefaultMutableTreeNode node) throws IOException {
        out.write("<td class=\"groupThumbnailCell\" valign=\"bottom\" align=\"left\">");

        out.write("<a href=\"jpo_" + node.hashCode() + ".htm\">"
                + "<img src=\"" + FOLDER_ICON + "\" width=\"350\" height=\"295\" /></a>");

        out.write("</td>");
        out.newLine();

        // recursively call the method to output that group.
        writeGroup(node);
        folderIconRequired = true;
    }

    /**
     * Write html for a picture in the set of webpages.
     *
     * @param pictureNode The node for which the HTML is to be written
     * @param out         The opened output stream of the overview page to which the
     *                    thumbnail tags should be written
     * @param groupFile   The name of the html file that holds the small
     *                    thumbnails of the parent group
     * @param childNumber The current position of the picture in the group
     * @throws IOException If there was some sort of IO Error.
     */
    private void writePicture(
            final SortableDefaultMutableTreeNode pictureNode,
            final BufferedWriter out,
            final File groupFile,
            final int childNumber)
            throws IOException {

        final PictureInfo pictureInfo = (PictureInfo) pictureNode.getUserObject();
        publish(String.format("Writing picture node %d: %s", picsWroteCounter, pictureInfo.toString()));


        final File lowresFile = getOutputImageFile(request, pictureInfo, "_l.", picsWroteCounter, true);
        final File midresFile = getOutputImageFile(request, pictureInfo, "_m.", picsWroteCounter, true);
        final File highresFile = getOutputImageFile(request, pictureInfo, "_h.", picsWroteCounter, true);
        final File midresHtmlFile = getOutputImageFile(request, pictureInfo, ".htm", picsWroteCounter, false);
        LOGGER.log(Level.INFO, "Filenames: Lowres: {0}, Midres: {1}, Highres {2}, MidresHtml: {3}", new Object[]{lowresFile, midresFile, highresFile, midresHtmlFile});

        files.add(lowresFile);
        files.add(midresFile);

        LOGGER.log(Level.INFO, "Loading: {0}", pictureInfo.getImageLocation());
        final ScalablePicture scp = loadScalablePicture(pictureInfo);
        writeHighresPicture(request, pictureInfo, highresFile, scp);
        writeLowres(out, pictureInfo, lowresFile, midresFile, midresHtmlFile, scp);
        final Dimension midresDimension = writeMidresPicture(pictureInfo, midresFile, scp);
        if (request.isGenerateMidresHtml()) {
            writeMidres(pictureNode, groupFile, childNumber, lowresFile, midresFile, highresFile, midresHtmlFile, midresDimension);
        }
        picsWroteCounter++;
    }

    private void writeAndClearRowDescriptions(final BufferedWriter out, final List<String> rowDescriptions) throws IOException {
        out.write("</tr>");
        out.newLine();
        out.write("<tr>");
        out.newLine();

        for (String description : rowDescriptions) {
            out.write("<td class=\"descriptionCell\">");
            out.write(StringEscapeUtils.escapeHtml4(description));
            out.write("</td>");
            out.newLine();
        }
        rowDescriptions.clear();
        out.write("</tr>");
        out.newLine();
    }

    private void writeGroupCellFooter(BufferedWriter out) throws IOException {
        out.write(String.format("%n<tr><td colspan=\"%d\">", request.getPicsPerRow()));
        out.write(Settings.getJpoResources().getString("LinkToJpo"));
        out.write("</td></tr></table>");
        out.newLine();
        out.write("</body></html>");
    }

    private void writeLinkToParentGroup(SortableDefaultMutableTreeNode groupNode, BufferedWriter out) throws IOException {
        if (!groupNode.equals(request.getStartNode())) {
            //link to parent
            final SortableDefaultMutableTreeNode parentNode = groupNode.getParent();
            String parentLink = "jpo_" + parentNode.hashCode() + ".htm";
            if (parentNode.equals(request.getStartNode())) {
                parentLink = INDEX_PAGE;
            }

            out.write(String.format("<p>Up to: <a href=\"%s\">%s</a>", parentLink, parentNode.toString()));
            out.newLine();
        }
    }

    private void writeLinkToZipFile(SortableDefaultMutableTreeNode groupNode, BufferedWriter out) throws IOException {
        if (groupNode.equals(request.getStartNode())) {
            if (request.isGenerateZipfile()) {
                out.newLine();
                out.write(String.format("<a href=\"%s\">Download High Resolution Pictures as a Zipfile</a>", request.getDownloadZipFileName()));
                out.newLine();
            }
        }
    }

    private void startGroupTable(final SortableDefaultMutableTreeNode groupNode, final BufferedWriter out, final GenerateWebsiteRequest request) throws IOException {
        final int tableWidth = (request.getPicsPerRow() * request.getThumbnailWidth() + (request.getPicsPerRow() - 1) * request.getCellspacing());
        out.write("<table  style=\"border-spacing: " + request.getCellspacing()
                + "px; width: "
                + tableWidth
                + "px\">");
        out.newLine();

        out.write(String.format("<tr><td colspan=\"%d\">", request.getPicsPerRow()));

        out.write(String.format("<h2>%s</h2>", ((GroupInfo) groupNode.getUserObject()).getGroupNameHtml()));
    }

    private void writeHtmlHeader(BufferedWriter out, String title) throws IOException {
        out.write("<!DOCTYPE HTML>");
        out.newLine();
        out.write("<html xml:lang=\"en\" lang=\"en\">");
        out.newLine();

        out.write("<head>\n\t<link rel=\"StyleSheet\" href=\"jpo.css\" type=\"text/css\" media=\"screen\" />\n\t<title>"
                + title
                + "</title>\n</head>");
        out.newLine();
        // write body
        out.write("<body>");
        out.newLine();
    }

    @NotNull
    private File getGroupFile(final SortableDefaultMutableTreeNode groupNode) {
        File groupFile;
        if (groupNode.equals(request.getStartNode())) {
            groupFile = new File(request.getTargetDirectory(), INDEX_PAGE);
        } else {
            int hashCode = groupNode.hashCode();
            groupFile = new File(request.getTargetDirectory(), "jpo_" + hashCode + ".htm");
        }
        return groupFile;
    }

    private void writeMidres(final SortableDefaultMutableTreeNode pictureNode, final File groupFile, final int childNumber, final File lowresFile, final File midresFile, final File highresFile, final File midresHtmlFile, final Dimension midresDimension) throws IOException {
        files.add(midresHtmlFile);
        try (
                final BufferedWriter midresHtmlWriter = new BufferedWriter(new FileWriter(midresHtmlFile))) {
            final String groupDescriptionHtml
                    = StringEscapeUtils.escapeHtml4(pictureNode.getParent().getUserObject().toString());

            writePageHeader(midresHtmlWriter, groupDescriptionHtml);
            midresHtmlWriter.write("<body onload=\"changetext(content[0])\">");
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("<table>");
            midresHtmlWriter.write("<tr><td colspan=\"2\"><h2>" + groupDescriptionHtml + "</h2></td></tr>");
            midresHtmlWriter.newLine();
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("<tr>");

            midresHtmlWriter.write("<td class=\"midresPictureCell\">");
            final PictureInfo pictureInfo = (PictureInfo) pictureNode.getUserObject();
            writeMidresImgTag(pictureInfo, highresFile, midresFile, midresHtmlWriter, midresDimension);
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("<p>" + StringEscapeUtils.escapeHtml4(pictureInfo.getDescription()));
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("</td>");
            midresHtmlWriter.newLine();

            final StringBuilder previewArray = writeRightColumnNavAndPreview(pictureNode, groupFile, childNumber, lowresFile, highresFile, midresHtmlWriter);

            midresHtmlWriter.write("</tr>");
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("</table>");
            midresHtmlWriter.newLine();

            if (request.isGenerateMouseover()) {
                writeMouseOverJavaScript(midresHtmlWriter, previewArray);
            }

            if (request.isGenerateMap()) {
                writeMapJavaScript(pictureInfo, midresHtmlWriter);
            }

            midresHtmlWriter.write("</body></html>");
        }
    }

    @NotNull
    private StringBuilder writeRightColumnNavAndPreview(final SortableDefaultMutableTreeNode pictureNode, final File groupFile, int childNumber, final File lowresFile, final File highresFile, final BufferedWriter midresHtmlWriter) throws IOException {
        // now do the right column
        midresHtmlWriter.write("<td class=\"midresSidebarCell\">");

        if (request.isGenerateMap()) {
            writeMapDiv(midresHtmlWriter);
        }

        // Do the matrix with the pictures to click
        final int indexBeforeCurrent = 15;
        final int indexPerRow = 5;
        final int indexToShow = 35;
        final int matrixWidth = 130;
        int childCount = pictureNode.getParent().getChildCount();
        midresHtmlWriter.write(String.format("Picture %d of %d", childNumber, childCount));
        midresHtmlWriter.newLine();
        final StringBuilder previewArray = writeNumberPickTable(pictureNode, childNumber, midresHtmlWriter, indexBeforeCurrent, indexPerRow, indexToShow, matrixWidth);
        midresHtmlWriter.newLine();
        writeLinks(pictureNode, groupFile, childNumber, childCount, lowresFile, highresFile, midresHtmlWriter);

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
        midresHtmlWriter.write("</td>");
        midresHtmlWriter.newLine();
        return previewArray;
    }

    private void writeMapJavaScript(PictureInfo pictureInfo, BufferedWriter midresHtmlWriter) throws IOException {
        midresHtmlWriter.write("<script type=\"text/javascript\">");
        midresHtmlWriter.write(String.format("const lat=%f; var lng=%f;", pictureInfo.getLatLng().x, pictureInfo.getLatLng().y));
        midresHtmlWriter.write("</script>");
        midresHtmlWriter.newLine();
        midresHtmlWriter.write("<script defer src=\"https://maps.googleapis.com/maps/api/js?key=" + request.getGoogleMapsApiKey() + "&callback=initMap\"></script>\n");
    }

    private void writeMouseOverJavaScript(final BufferedWriter midresHtmlWriter, final StringBuilder previewArray) throws IOException {
        writeJpoJs(request.getTargetDirectory());
        files.add(new File(request.getTargetDirectory(), JPO_JS));
        midresHtmlWriter.write("<script type=\"text/javascript\" src=\"" + JPO_JS + "\" ></script>");
        midresHtmlWriter.newLine();
        midresHtmlWriter.write("<script type=\"text/javascript\">");
        midresHtmlWriter.newLine();
        midresHtmlWriter.write("const content=new Array() ");
        midresHtmlWriter.newLine();
        midresHtmlWriter.write(previewArray.toString());
        midresHtmlWriter.newLine();
        midresHtmlWriter.write("</script>");
        midresHtmlWriter.newLine();
    }

    @NotNull
    private StringBuilder writeNumberPickTable(final SortableDefaultMutableTreeNode pictureNode, final int childNumber, final BufferedWriter midresHtmlWriter, final int indexBeforeCurrent, final int indexPerRow, final int indexToShow, final int matrixWidth) throws IOException {
        midresHtmlWriter.write("<table class=\"numberPickTable\">");
        midresHtmlWriter.newLine();
        final PictureInfo pictureInfo = (PictureInfo) pictureNode.getUserObject();
        final String htmlFriendlyDescription = StringEscapeUtils.escapeHtml4(pictureInfo.getDescription().replace("\'", "\\\\'"));
        int childCount = pictureNode.getParent().getChildCount();
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
                final SortableDefaultMutableTreeNode nde = (SortableDefaultMutableTreeNode) pictureNode.getParent().getChildAt(i - 1);

                if (nde.getUserObject() instanceof PictureInfo pi) {
                    final String nodeUrl = getNodeUrl(nde, i, childNumber);
                    final String extension = FilenameUtils.getExtension(pictureInfo.getImageFile().getName());
                    final String lowresFn = getLowresFilename(nde, extension, i, childNumber);
                    writePictureTableHyperlink(childCount, midresHtmlWriter, matrixWidth, dhtmlArray, i, nodeUrl, lowresFn, pi);
                } else if (nde.getUserObject() instanceof GroupInfo gi) {
                    midresHtmlWriter.write("<a href=\"jpo_" + nde.hashCode() + ".htm\">");
                }
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
        return dhtmlArray;
    }

    private void writePictureTableHyperlink(final int childCount, final BufferedWriter midresHtmlWriter, final int matrixWidth, final StringBuilder dhtmlArray, final int i, final String nodeUrl, final String lowresFn, final PictureInfo pi) throws IOException {
        midresHtmlWriter.write(A_HREF + nodeUrl + "\"");
        final String htmlFriendlyDescription2 = StringEscapeUtils.escapeHtml4((pi.getDescription().replace("\'", "\\\\'")));
        if (request.isGenerateMouseover()) {
            midresHtmlWriter.write(String.format(" onmouseover=\"changetext(content[%d])\" onmouseout=\"changetext(content[0])\">", i));
            dhtmlArray.append(String.format("content[%d]='", i));

            dhtmlArray.append(String.format("<p>Picture %d/%d:</p>", i, childCount));
            dhtmlArray.append(String.format("<p><img src=\"%s\" width=%d alt=\"Thumbnail\"></p>", lowresFn, matrixWidth - 10));
            dhtmlArray.append("<p><i>").append(htmlFriendlyDescription2).append("</i></p>'\n");
        } else {
            dhtmlArray.append(String.format("<p>Item %d/%d:</p>", i, childCount));
            dhtmlArray.append("<p><i>").append(htmlFriendlyDescription2).append("</p></i>'\n");
        }
    }

    private String getLowresFilename(final SortableDefaultMutableTreeNode nde, final String extension, final int i, final int childNumber) {
        String lowresFn = "";
        if (nde.getUserObject() instanceof PictureInfo pi) {
            switch (request.getPictureNaming()) {
                case PICTURE_NAMING_BY_ORIGINAL_NAME:
                    final String rootName = cleanupFilename(FilenameUtils.getBaseName(pi.getImageFile().getName()));
                    lowresFn = rootName + "_l." + extension;
                    break;
                case PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
                    final String convertedNumber = Integer.toString(picsWroteCounter + request.getSequentialStartNumber() + i - childNumber - 1);
                    final String padding = "00000";
                    final String root = padding.substring(convertedNumber.length()) + convertedNumber;
                    lowresFn = "jpo_" + root + "_l." + extension;
                    break;
                default:  //case GenerateWebsiteRequest.PICTURE_NAMING_BY_HASH_CODE:
                    lowresFn = "jpo_" + nde.hashCode() + "_l." + extension;
                    break;
            }

        }
        return lowresFn;
    }

    private String getNodeUrl(final SortableDefaultMutableTreeNode nde, final int i, final int childNumber) {
        if (nde.getUserObject() instanceof PictureInfo pi) {
            switch (request.getPictureNaming()) {
                case PICTURE_NAMING_BY_ORIGINAL_NAME:
                    final String rootName = cleanupFilename(FilenameUtils.getBaseName(pi.getImageFile().getName()));
                    return rootName + ".htm";
                case PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
                    final String convertedNumber = Integer.toString(picsWroteCounter + request.getSequentialStartNumber() + i - childNumber - 1);
                    final String padding = "00000";
                    final String root = padding.substring(convertedNumber.length()) + convertedNumber;
                    return "jpo_" + root + ".htm";
                default:  //case GenerateWebsiteRequest.PICTURE_NAMING_BY_HASH_CODE:
                    final int hashCode = nde.hashCode();
                    return "jpo_" + hashCode + ".htm";
            }
        }
        return "";
    }

    private void writeMapDiv(BufferedWriter midresHtmlWriter) throws IOException {
        midresHtmlWriter.write("<div id=\"map\"></div>");
        midresHtmlWriter.write("<br />");
        midresHtmlWriter.newLine();
    }

    private void writeMidresImgTag(final PictureInfo pictureInfo, final File highresFile, final File midresFile, final BufferedWriter midresHtmlWriter, final Dimension midresDimension) throws IOException {
        final String imgTag = "<img src=\"%s\" width= \"%d\" height=\"%d\" alt=\"%s\" />".formatted(midresFile.getName(), midresDimension.width, midresDimension.height, StringEscapeUtils.escapeHtml4(pictureInfo.getDescription()));
        if (request.isLinkToHighres()) {
            writeHyperlink(midresHtmlWriter, pictureInfo.getImageLocation(), imgTag);
        } else if (request.isExportHighres()) {
            writeHyperlink(midresHtmlWriter, highresFile.getName(), imgTag);
        } else {
            midresHtmlWriter.write(imgTag);
        }
    }

    private void writeHyperlink(final Writer out, final String target, final String linkText) throws IOException {
        out.write(A_HREF + target + "\">" + linkText + "</a>");
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
        out.write(A_HREF);
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

    private void writeHighresPicture(final GenerateWebsiteRequest request, final PictureInfo pictureInfo, final File highresFile, final ScalablePicture scp) throws IOException {
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
                Files.copy(pictureInfo.getImageFile().toPath(), highresFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
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

    private void writeLinks(final SortableDefaultMutableTreeNode pictureNode, final File groupFile, final int childNumber, final int childCount, final File lowresFile, final File highresFile, final BufferedWriter midresHtmlWriter) throws IOException {
        writeHyperlink(midresHtmlWriter, groupFile.getName() + "#" + StringEscapeUtils.escapeHtml4(lowresFile.getName()), "Up");
        midresHtmlWriter.write("&nbsp;");
        midresHtmlWriter.newLine();
        if (childNumber != 1) {
            writeHyperlink(midresHtmlWriter, getPreviousHtmlFilename(pictureNode, childNumber), "Previous");
            midresHtmlWriter.write("&nbsp;");
        }
        if (request.isLinkToHighres()) {
            final PictureInfo pictureInfo = (PictureInfo) pictureNode.getUserObject();
            writeHyperlink(midresHtmlWriter, pictureInfo.getImageLocation(), "Highres");
            midresHtmlWriter.write("&nbsp;");
        } else if (request.isExportHighres()) {
            // Link to Highres in target directory
            writeHyperlink(midresHtmlWriter, highresFile.getName(), "Highres");
            midresHtmlWriter.write("&nbsp;");
        }
        if (pictureNode != pictureNode.getParent().getLastChild()) {
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



}
