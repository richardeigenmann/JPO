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

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/*
 * Copyright (C) 2002-2024 Richard Eigenmann.
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
    private static final String END_TD = "</td>";
    private static final String END_TR = "</tr>";
    private static final String END_TABLE = "</table>";
    private static final String NBSP = "&nbsp;";
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
     * Collection of the files that make up the website
     */
    private final List<File> websiteMemberFiles = new ArrayList<>();

    /**
     * The request for the website with all the detailed settings desired
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
        var returnString = string;
        for (final var entry : CHARACTER_TRANSLATION.entrySet()) {
            if (returnString.contains(entry.getKey())) {
                returnString = returnString.replace(entry.getKey(), entry.getValue());
            }
        }
        return returnString;
    }

    private static int checkAck(final InputStream inputStream) throws IOException {
        int b = inputStream.read();
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
                c = inputStream.read();
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
     * @param targetDirectory The directory to write to
     */
    public static void writeCss(final File targetDirectory, final List<File> websiteMemberFiles) {
        writeFileFromJar(JPO_CSS, targetDirectory, websiteMemberFiles);
    }

    /**
     * Write the robots.txt file to the target directory.
     *
     * @param targetDirectory The directory to write to
     */
    public static void writeRobotsTxt(final File targetDirectory, final List<File> websiteMemberFiles) {
        writeFileFromJar(ROBOTS_TXT, targetDirectory, websiteMemberFiles);
    }

    /**
     * Write the jpo.js file to the target directory.
     *
     * @param targetDirectory    The directory to write to
     * @param websiteMemberFiles A reference to the list of files where we shall add our entry.
     */
    public static void writeJpoJs(final File targetDirectory, final List<File> websiteMemberFiles) {
        writeFileFromJar(JPO_JS, targetDirectory, websiteMemberFiles);
    }

    /**
     * Write the specified file from the classloader to the target directory.
     *
     * @param file               the file to copy from the classloader
     * @param targetDirectory    The directory to write to
     * @param websiteMemberFiles A reference to the list of files where we shall add our entry.
     */
    public static void writeFileFromJar(final String file, final File targetDirectory, final List<File> websiteMemberFiles) {
        final var targetFile = new File(targetDirectory, file);
        websiteMemberFiles.add(targetFile);
        try (
                final var bufferedInputStream = new BufferedInputStream(Objects.requireNonNull(JpoWriter.class.getClassLoader().getResource(file)).openStream());
                final var bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(targetFile))) {
            bufferedInputStream.transferTo(bufferedOutputStream);
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

    private static void writeFolderIcon(final GenerateWebsiteRequest request, final List<File> websiteMemberFiles) throws IOException {
        final var folderIconFile = new File(request.getTargetDirectory(), FOLDER_ICON);
        try (
                final var bufferedInputStream = new BufferedInputStream(Objects.requireNonNull(WebsiteGenerator.class.getClassLoader().getResource(FOLDER_ICON)).openStream());
                final var bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(folderIconFile))) {
            websiteMemberFiles.add(folderIconFile);
            bufferedInputStream.transferTo(bufferedOutputStream);
        }
    }

    @TestOnly
    public static void generateZipfileTest(final GenerateWebsiteRequest request) {
        generateZipfile(request);
    }

    /**
     * Searches for all the pictures in the request's receivingNode and adds them to a zipfile if isGenerateZipfile is true;
     *
     * @param request the request
     */
    private static void generateZipfile(final GenerateWebsiteRequest request) {
        if (!request.isGenerateZipfile()) {
            return;
        }
        LOGGER.log(Level.INFO, "Generating Zipfile for node {0}, sequentialStartNumber: {1}",
                new Object[]{request.getCellspacing(), request.getSequentialStartNumber()});
        try (
                final var fileOutputStream
                        = new FileOutputStream(new File(request.getTargetDirectory(), request.getDownloadZipFileName()));
                final var bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                final var zipOutputStream = new ZipOutputStream(bufferedOutputStream)) {
            var picWroteCounter = request.getSequentialStartNumber();
            for (final var sortableDefaultMutableTreeNode : request.getStartNode().getChildPictureNodes(true)) {
                final var pictureInfo = (PictureInfo) sortableDefaultMutableTreeNode.getUserObject();
                addZipFileEntry(request, zipOutputStream, picWroteCounter, sortableDefaultMutableTreeNode, pictureInfo);
                picWroteCounter++;
            }
        } catch (final IOException x) {
            LOGGER.log(Level.SEVERE, "Error creating Zipfile. Continuing without Zip\n{0}", x.toString());
            request.setGenerateZipfile(false);
        }
    }

    private static void addZipFileEntry(final GenerateWebsiteRequest request, final ZipOutputStream zipFile, final int picWroteCounter, final SortableDefaultMutableTreeNode p, final PictureInfo pictureInfo) {
        try (
                final var fileInputStream = new FileInputStream(pictureInfo.getImageFile());
                final var bufferedInputStream = new BufferedInputStream(fileInputStream)) {

            final var outputImageFile = getOutputImageFile(request, pictureInfo, "_h.", picWroteCounter, true);
            final var zipEntry = new ZipEntry(outputImageFile.getName());
            LOGGER.log(Level.INFO, "Adding to zipfile: {0}", outputImageFile);
            zipFile.putNextEntry(zipEntry);
            bufferedInputStream.transferTo(zipFile);
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Could not create zipfile entry for {0}\n{1}", new Object[]{p, e});
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

        writeCss(request.getTargetDirectory(), websiteMemberFiles);
        if (request.isWriteRobotsTxt()) {
            writeRobotsTxt(request.getTargetDirectory(), websiteMemberFiles);
        }
        if (request.isGenerateMouseover()) {
            writeJpoJs(request.getTargetDirectory(), websiteMemberFiles);
        }
        LOGGER.info("Done static files");

        picsWroteCounter = request.getSequentialStartNumber();
        assignFilenames(request);
        writeLowresGroup(request.getStartNode());

        if (folderIconRequired) {
            writeFolderIcon(request, websiteMemberFiles);
        }
        if (request.getOutputTarget() == GenerateWebsiteRequest.OutputTarget.OUTPUT_SSH_LOCATION) {
            sshCopyToServer(websiteMemberFiles);
        } else if (request.getOutputTarget() == GenerateWebsiteRequest.OutputTarget.OUTPUT_FTP_LOCATION) {
            ftpCopyToServer(websiteMemberFiles);
        }

        return Integer.MAX_VALUE;
    }


    final Map<Integer, File> highresFiles = new HashMap<>();
    final Map<Integer, File> midresFiles = new HashMap<>();
    final Map<Integer, File> lowresFiles = new HashMap<>();
    final Map<Integer, File> lowresHtmlFiles = new HashMap<>();
    final Map<Integer, File> midresHtmlFiles = new HashMap<>();

    /**
     * The sequential counting of files gets complicated in tree structures, so I will
     * pre-allocate the filenames in a first pass and then look them up.
     *
     * @param request The request
     */
    private void assignFilenames(final GenerateWebsiteRequest request) {
        final var treeNodeEnumeration = request.getStartNode().breadthFirstEnumeration();
        while (treeNodeEnumeration.hasMoreElements()) {
            final var sortableDefaultMutableTreeNode = (SortableDefaultMutableTreeNode) treeNodeEnumeration.nextElement();
            if (sortableDefaultMutableTreeNode.getUserObject() instanceof PictureInfo pictureInfo) {
                lowresFiles.put(sortableDefaultMutableTreeNode.hashCode(), getOutputImageFile(request, pictureInfo, "_l.", picsWroteCounter, true));
                midresFiles.put(sortableDefaultMutableTreeNode.hashCode(), getOutputImageFile(request, pictureInfo, "_m.", picsWroteCounter, true));
                highresFiles.put(sortableDefaultMutableTreeNode.hashCode(), getOutputImageFile(request, pictureInfo, "_h.", picsWroteCounter, true));
                midresHtmlFiles.put(sortableDefaultMutableTreeNode.hashCode(), getOutputImageFile(request, pictureInfo, ".htm", picsWroteCounter, false));
                picsWroteCounter++;
            } else {
                if (sortableDefaultMutableTreeNode == request.getStartNode()) {
                    lowresHtmlFiles.put(sortableDefaultMutableTreeNode.hashCode(), new File(request.getTargetDirectory(), INDEX_PAGE));
                    midresHtmlFiles.put(sortableDefaultMutableTreeNode.hashCode(), new File(request.getTargetDirectory(), INDEX_PAGE));
                } else {
                    lowresHtmlFiles.put(sortableDefaultMutableTreeNode.hashCode(), new File(request.getTargetDirectory(), "jpo_" + sortableDefaultMutableTreeNode.hashCode() + ".htm"));
                    midresHtmlFiles.put(sortableDefaultMutableTreeNode.hashCode(), new File(request.getTargetDirectory(), "jpo_" + sortableDefaultMutableTreeNode.hashCode() + ".htm"));
                }
                lowresFiles.put(sortableDefaultMutableTreeNode.hashCode(), new File(request.getTargetDirectory(), FOLDER_ICON));
            }
        }
    }

    private static File getOutputImageFile(final GenerateWebsiteRequest request, final PictureInfo pictureInfo, final String suffix, final int picsWroteCounter, final boolean keepExtension) {
        final var extension = keepExtension ? FilenameUtils.getExtension(pictureInfo.getImageFile().getName()) : "";
        switch (request.getPictureNaming()) {
            case PICTURE_NAMING_BY_ORIGINAL_NAME -> {
                final var rootName = cleanupFilename(FilenameUtils.getBaseName(pictureInfo.getImageFile().getName()));
                return new File(request.getTargetDirectory(), rootName + suffix + extension);
            }
            case PICTURE_NAMING_BY_SEQUENTIAL_NUMBER -> {
                final var convertedNumber = Integer.toString(picsWroteCounter);
                final var padding = "00000";
                final var formattedNumber = padding.substring(convertedNumber.length()) + convertedNumber;
                final var root = "jpo_" + formattedNumber;
                return new File(request.getTargetDirectory(), root + suffix + extension);
            }
            default -> {
                final String fn = "jpo_" + pictureInfo.hashCode();
                return new File(request.getTargetDirectory(), fn + suffix + extension);
            }
        }
    }

    /**
     * This method is called by SwingWorker when the background process sends a
     * publish event.
     *
     * @param messages A message that will be written to the logfile.
     */
    @Override
    protected void process(final List<String> messages) {
        progressGui.progressIncrement(messages.size());
        messages.forEach(message -> LOGGER.log(Level.INFO, "Message: {0}", message));
    }

    /**
     * SwingWorker calls here when the background task is done.
     */
    @Override
    protected void done() {
        progressGui.switchToDoneMode();
        if (request.isOpenWebsiteAfterRendering()) {
            try {
                final var indexPage = new File(request.getTargetDirectory(), INDEX_PAGE);
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
    public void writeLowresGroup(final SortableDefaultMutableTreeNode groupNode) {
        try {
            publish(String.format("Processing Group: %s", groupNode.toString()));

            final var lowresGroupFile = lowresHtmlFiles.get(groupNode.hashCode());
            websiteMemberFiles.add(lowresGroupFile);

            final var lowresGroupWriter = new BufferedWriter(new FileWriter(lowresGroupFile));
            final var title = ((GroupInfo) groupNode.getUserObject()).getGroupNameHtml();

            writeHtmlHeader(lowresGroupWriter, title);
            startGroupTable(groupNode, lowresGroupWriter, request);
            writeLinkToZipFile(groupNode, lowresGroupWriter);
            writeLinkToParentGroup(groupNode, lowresGroupWriter);

            lowresGroupWriter.write(END_TD + END_TR + "\n<tr>");
            lowresGroupWriter.newLine();

            final List<String> rowDescriptions = new ArrayList<>();
            for (var i = 0; i < groupNode.getChildCount(); i++) {
                final var node = (SortableDefaultMutableTreeNode) groupNode.getChildAt(i);
                rowDescriptions.add(writeLowresCell(lowresGroupWriter, node, i));
                if ((rowDescriptions.size() % request.getPicsPerRow() == 0) || (i == groupNode.getChildCount() - 1)) {
                    // if we have a full fow of descriptions or if we are processing the last child
                    writeAndClearRowDescriptions(lowresGroupWriter, rowDescriptions);
                }
            }

            writeGroupCellFooter(lowresGroupWriter);
            lowresGroupWriter.close();

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

    private String writeLowresCell(final BufferedWriter lowresGroupWriter, final SortableDefaultMutableTreeNode node, final int childNumber) throws IOException {
        if (node.getUserObject() instanceof GroupInfo gi) {
            writeLowresGroupCell(lowresGroupWriter, node);
            return gi.getGroupName();
        } else {
            final var pictureInfo = (PictureInfo) node.getUserObject();
            publish(String.format("Processing picture node: %s", pictureInfo.toString()));
            LOGGER.log(Level.INFO, "Loading: {0}", pictureInfo.getImageFile());
            final var scalablePicture = loadScalablePicture(pictureInfo);

            writeLowres(lowresGroupWriter, node, scalablePicture, websiteMemberFiles);
            final var midresDimension = writeScaledPicture(midresFiles.get(node.hashCode()), request.getMidresDimension(), request.getMidresJpgQuality(), scalablePicture, websiteMemberFiles);
            writeHighresPicture(request, node, scalablePicture, websiteMemberFiles);

            if (request.isGenerateMidresHtml()) {
                writeMidres(node, childNumber, midresDimension);
            }
            return ((PictureInfo) node.getUserObject()).getDescription();
        }
    }

    private void writeLowresGroupCell(final BufferedWriter out, final SortableDefaultMutableTreeNode node) throws IOException {
        out.write("<td class=\"groupThumbnailCell\" valign=\"bottom\" align=\"left\">");

        out.write(A_HREF + lowresHtmlFiles.get(node.hashCode()).getName() + "\">"
                + "<img src=\"" + lowresFiles.get(node.hashCode()).getName() + "\" width=\"350\" height=\"295\" /></a>");

        out.write(END_TD);
        out.newLine();

        // recursively call the method to output that group.
        writeLowresGroup(node);
        folderIconRequired = true;
    }


    private void writeAndClearRowDescriptions(final BufferedWriter out, final List<String> rowDescriptions) throws IOException {
        out.write(END_TD);
        out.newLine();
        out.write("<tr>");
        out.newLine();

        for (String description : rowDescriptions) {
            out.write("<td class=\"descriptionCell\">");
            out.write(StringEscapeUtils.escapeHtml4(description));
            out.write(END_TD);
            out.newLine();
        }
        rowDescriptions.clear();
        out.write(END_TR);
        out.newLine();
    }

    private void writeGroupCellFooter(BufferedWriter out) throws IOException {
        out.write(String.format("%n<tr><td colspan=\"%d\">", request.getPicsPerRow()));
        out.write(Settings.getJpoResources().getString("LinkToJpo"));
        out.write(END_TD + END_TR + END_TABLE);
        out.newLine();
        out.write("</body></html>");
    }

    private void writeLinkToParentGroup(SortableDefaultMutableTreeNode groupNode, BufferedWriter out) throws IOException {
        if (!groupNode.equals(request.getStartNode())) {
            //link to parent
            final var parentNode = groupNode.getParent();
            var parentLink = "jpo_" + parentNode.hashCode() + ".htm";
            if (parentNode.equals(request.getStartNode())) {
                parentLink = INDEX_PAGE;
            }

            out.write(String.format("<p>Up to: <a href=\"%s\">%s</a>", parentLink, parentNode));
            out.newLine();
        }
    }

    private void writeLinkToZipFile(SortableDefaultMutableTreeNode groupNode, BufferedWriter out) throws IOException {
        if (groupNode.equals(request.getStartNode())
                && request.isGenerateZipfile()) {
            out.newLine();
            out.write(String.format("<a href=\"%s\">Download High Resolution Pictures as a Zipfile</a>", request.getDownloadZipFileName()));
            out.newLine();

        }
    }

    private void startGroupTable(final SortableDefaultMutableTreeNode groupNode, final BufferedWriter out, final GenerateWebsiteRequest request) throws IOException {
        final var tableWidth = (request.getPicsPerRow() * request.getThumbnailWidth() + (request.getPicsPerRow() - 1) * request.getCellspacing());
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

    private void writeMidres(final SortableDefaultMutableTreeNode pictureNode, final int childNumber, final Dimension midresDimension) throws IOException {
        final var highresFile = highresFiles.get(pictureNode.hashCode());
        final var midresHtmlFile = midresHtmlFiles.get(pictureNode.hashCode());
        websiteMemberFiles.add(midresHtmlFile);
        try ( final var midresHtmlWriter = new BufferedWriter(new FileWriter(midresHtmlFile)) ) {
            final var groupDescriptionHtml
                    = StringEscapeUtils.escapeHtml4(pictureNode.getParent().getUserObject().toString());

            writeMidresPageHeader(midresHtmlWriter, groupDescriptionHtml);
            midresHtmlWriter.write("<body onload=\"changetext(content[0])\">");
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("<table>");
            midresHtmlWriter.write("<tr><td colspan=\"2\"><h2>" + groupDescriptionHtml + "</h2>" + END_TD + END_TR);
            midresHtmlWriter.newLine();
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("<tr>");

            midresHtmlWriter.write("<td class=\"midresPictureCell\">");
            final var pictureInfo = (PictureInfo) pictureNode.getUserObject();
            writeMidresImgTag(pictureNode, midresHtmlWriter, midresDimension);
            midresHtmlWriter.newLine();
            midresHtmlWriter.write("<p>" + StringEscapeUtils.escapeHtml4(pictureInfo.getDescription()));
            midresHtmlWriter.newLine();
            midresHtmlWriter.write(END_TD);
            midresHtmlWriter.newLine();

            final var previewArray = writeRightColumnNavAndPreview(pictureNode, childNumber, highresFile, midresHtmlWriter);

            midresHtmlWriter.write(END_TR);
            midresHtmlWriter.newLine();
            midresHtmlWriter.write(END_TABLE);
            midresHtmlWriter.newLine();

            if (request.isGenerateMouseover()) {
                writeMouseOverJavaScript(midresHtmlWriter, previewArray);
            }

            if (request.isGenerateMap()) {
                writeMapJavaScript(pictureInfo, midresHtmlWriter);
            }

            midresHtmlWriter.write("</body></html>");
            midresHtmlWriter.flush();
        }
    }

    private static void writeMidresPageHeader(final BufferedWriter midresHtmlWriter, final String pageTitle) throws IOException {
        midresHtmlWriter.write("<!DOCTYPE HTML>");
        midresHtmlWriter.newLine();
        midresHtmlWriter.write("<head>\n\t<link rel=\"StyleSheet\" href=\"" + JPO_CSS + "\" type=\"text/css\" media=\"screen\" />\n\t<title>" + pageTitle + "</title>\n</head>");
        midresHtmlWriter.newLine();
    }

    @NotNull
    private StringBuilder writeRightColumnNavAndPreview(final SortableDefaultMutableTreeNode pictureNode, int childNumber, final File highresFile, final BufferedWriter midresHtmlWriter) throws IOException {
        // now do the right column
        midresHtmlWriter.write("<td class=\"midresSidebarCell\">");

        if (request.isGenerateMap()) {
            writeMapDiv(midresHtmlWriter);
        }

        int childCount = pictureNode.getParent().getChildCount();
        midresHtmlWriter.write(String.format("Picture %d of %d", childNumber + 1, childCount));
        midresHtmlWriter.newLine();
        final var matrixWidth = 130;
        final StringBuilder previewArray = writeNumberPickTable(pictureNode, childNumber, midresHtmlWriter, matrixWidth);
        midresHtmlWriter.newLine();
        writeMidresLinks(pictureNode, highresFile, midresHtmlWriter);

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
        midresHtmlWriter.write(END_TD);
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
        writeJpoJs(request.getTargetDirectory(), websiteMemberFiles);
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
    private StringBuilder writeNumberPickTable(final SortableDefaultMutableTreeNode pictureNode, final int childNumber, final BufferedWriter midresHtmlWriter, final int matrixWidth) throws IOException {
        // Do the matrix with the pictures to click
        final var indexPerRow = 5;

        midresHtmlWriter.write("<table class=\"numberPickTable\">");
        midresHtmlWriter.newLine();
        final var pictureInfo = (PictureInfo) pictureNode.getUserObject();
        final var htmlFriendlyDescription = StringEscapeUtils.escapeHtml4(pictureInfo.getDescription().replace("'", "\\\\'"));
        int childCount = pictureNode.getParent().getChildCount();
        final var dhtmlArray = startDhtmlArray(childNumber, childCount, pictureInfo, htmlFriendlyDescription);

        final var startIndex = getStartIndex(childNumber, indexPerRow);
        final var endIndex = getEndIndex(startIndex, childCount, indexPerRow);


        for (var i = startIndex; i < endIndex; i++) {
            writeTrIfNeeded(midresHtmlWriter, indexPerRow, i);
            midresHtmlWriter.write("<td class=\"numberPickCell");
            if (i == childNumber) {
                midresHtmlWriter.write(" selfHighlight");
            }
            midresHtmlWriter.write("\">");
            if (i < childCount) {
                writeLinkToNodeNumber(pictureNode, midresHtmlWriter, matrixWidth, dhtmlArray, i);
            } else {
                midresHtmlWriter.write(NBSP);
            }
            midresHtmlWriter.write(END_TD);
            midresHtmlWriter.newLine();
            writeEndTrIfNeeded(midresHtmlWriter, indexPerRow, i);
        }
        midresHtmlWriter.write(END_TABLE);
        midresHtmlWriter.newLine();
        return dhtmlArray;
    }

    private void writeLinkToNodeNumber(final SortableDefaultMutableTreeNode pictureNode, final BufferedWriter midresHtmlWriter, final int matrixWidth, final StringBuilder dhtmlArray, final int i) throws IOException {
        final var sortableDefaultMutableTreeNode = (SortableDefaultMutableTreeNode) pictureNode.getParent().getChildAt(i);

        if (sortableDefaultMutableTreeNode.getUserObject() instanceof PictureInfo) {
            writePictureTableHyperlink(midresHtmlWriter, sortableDefaultMutableTreeNode, matrixWidth, dhtmlArray, i);
        } else if (sortableDefaultMutableTreeNode.getUserObject() instanceof GroupInfo) {
            midresHtmlWriter.write(A_HREF + "jpo_" + sortableDefaultMutableTreeNode.hashCode() + ".htm\">");
        }
        midresHtmlWriter.write(Integer.toString(i));
        midresHtmlWriter.write("</a>");
    }

    private void writeEndTrIfNeeded(BufferedWriter midresHtmlWriter, int indexPerRow, int i) throws IOException {
        if ((i + 1) % indexPerRow == 0) {
            midresHtmlWriter.write(END_TR);
            midresHtmlWriter.newLine();
        }
    }

    private void writeTrIfNeeded(final BufferedWriter midresHtmlWriter, final int indexPerRow, final int i) throws IOException {
        if ((i) % indexPerRow == 0) {
            midresHtmlWriter.write("<tr>");
            midresHtmlWriter.newLine();
        }
    }

    public static int getStartIndex(int childNumber, int indexPerRow) {
        final var indexBeforeCurrent = 15;
        var startIndex = (int) Math.floor((childNumber - indexBeforeCurrent) / (double) indexPerRow) * indexPerRow;
        if (startIndex < 0) {
            startIndex = 0;
        }
        return startIndex;
    }

    public static int getEndIndex(final int startIndex, final int childCount, final int indexPerRow) {
        final var numbersToShow = 35;
        int endIndex = startIndex + numbersToShow;
        if (endIndex > childCount) {
            endIndex = ((childCount + indexPerRow) / indexPerRow) * indexPerRow;
        }
        return endIndex;
    }


    private void writePictureTableHyperlink(final BufferedWriter midresHtmlWriter, final SortableDefaultMutableTreeNode node, final int matrixWidth, final StringBuilder dhtmlArray, final int i) throws IOException {
        final var nodeUrl = midresHtmlFiles.get(node.hashCode()).getName();
        midresHtmlWriter.write(A_HREF + nodeUrl + "\"");
        final var pictureInfo = (PictureInfo) node.getUserObject();
        final var htmlFriendlyDescription2 = StringEscapeUtils.escapeHtml4((pictureInfo.getDescription().replace("\'", "\\\\'")));
        if (request.isGenerateMouseover()) {
            midresHtmlWriter.write(String.format(" onmouseover=\"changetext(content[%d])\" onmouseout=\"changetext(content[0])\">", i));
            dhtmlArray.append(String.format("content[%d]='", i));

            dhtmlArray.append(String.format("<p>Picture %d/%d:</p>", i, node.getChildCount()));
            final var lowresFn = lowresFiles.get(node.hashCode()).getName();
            dhtmlArray.append(String.format("<p><img src=\"%s\" width=%d alt=\"Thumbnail\"></p>", lowresFn, matrixWidth - 10));
            dhtmlArray.append("<p><i>").append(htmlFriendlyDescription2).append("</i></p>'\n");
        } else {
            dhtmlArray.append(String.format("<p>Item %d/%d:</p>", i, node.getChildCount()));
            dhtmlArray.append("<p><i>").append(htmlFriendlyDescription2).append("</p></i>'\n");
        }
    }


    private void writeMapDiv(BufferedWriter midresHtmlWriter) throws IOException {
        midresHtmlWriter.write("<div id=\"map\"></div>");
        midresHtmlWriter.write("<br />");
        midresHtmlWriter.newLine();
    }

    private void writeMidresImgTag(final SortableDefaultMutableTreeNode node, final BufferedWriter midresHtmlWriter, final Dimension midresDimension) throws IOException {
        final var pictureInfo = (PictureInfo) node.getUserObject();
        final var imgTag = "<img src=\"%s\" width= \"%d\" height=\"%d\" alt=\"%s\" />".formatted(midresFiles.get(node.hashCode()).getName(), midresDimension.width, midresDimension.height, StringEscapeUtils.escapeHtml4(pictureInfo.getDescription()));
        if (request.isLinkToHighres()) {
            writeHyperlink(midresHtmlWriter, pictureInfo.getImageLocation(), imgTag);
        } else if (request.isExportHighres()) {
            writeHyperlink(midresHtmlWriter, highresFiles.get(node.hashCode()).getName(), imgTag);
        } else {
            midresHtmlWriter.write(imgTag);
        }
    }

    private void writeHyperlink(final Writer out, final String target, final String linkText) throws IOException {
        out.write(A_HREF + target + "\">" + linkText + "</a>");
    }


    @NotNull
    private static Dimension writeScaledPicture(final File targetFile, final Dimension targetDimension, final float targetQuality, final ScalablePicture scp, final List<File> websiteMemberFiles) {
        scp.setScaleSize(targetDimension);
        scp.scalePicture();
        scp.setJpgQuality(targetQuality);
        scp.writeScaledJpg(targetFile);

        websiteMemberFiles.add(targetFile);

        final var scaledDimension = new Dimension();
        scaledDimension.width = scp.getScaledWidth();
        scaledDimension.height = scp.getScaledHeight();
        LOGGER.log(Level.INFO, "Wrote image to file {0}, scaled to {1}x{2}", new Object[]{targetFile, scaledDimension.width, scaledDimension.height});
        return scaledDimension;
    }


    private void writeLowres(final BufferedWriter out, final SortableDefaultMutableTreeNode node, final ScalablePicture scp, final List<File> websiteMemberFiles) throws IOException {
        final var lowresFile = lowresFiles.get(node.hashCode());
        final var lowresDimension = writeScaledPicture(lowresFile, request.getThumbnailDimension(), request.getLowresJpgQuality(), scp, websiteMemberFiles);

        out.write("<td class=\"pictureThumbnailCell\" id=\"" + StringEscapeUtils.escapeHtml4(lowresFile.getName()) + "\">");

        // write an anchor so the up come back
        // but only if we are generating MidresHTML pages
        out.write(A_HREF);
        if (request.isGenerateMidresHtml()) {
            out.write(midresHtmlFiles.get(node.hashCode()).toPath().getFileName().toString());
        } else {
            out.write(midresFiles.get(node.hashCode()).toString());
        }
        out.write("\">" + "<img src=\""
                + lowresFile.getName()
                + "\" width=\""
                + lowresDimension.width
                + "\" height=\""
                + lowresDimension.height
                + "\" alt=\""
                + StringEscapeUtils.escapeHtml4(((PictureInfo) node.getUserObject()).getDescription())
                + "\" "
                + " />"
                + "</a>");

        out.write(END_TD);
        out.newLine();
    }

    private void writeHighresPicture(final GenerateWebsiteRequest request, final SortableDefaultMutableTreeNode node, final ScalablePicture scp, final List<File> websiteMemberFiles) throws IOException {
        // copy the picture to the target directory
        if (request.isExportHighres()) {
            final var highresFile = highresFiles.get(node.hashCode());
            final var pictureInfo = (PictureInfo) node.getUserObject();
            if (request.isRotateHighres() && (pictureInfo.getRotation() != 0)) {
                LOGGER.log(Level.FINE, "Copying and rotating picture {0} to {1}", new Object[]{pictureInfo.getImageLocation(), highresFile});
                scp.setScaleFactor(1);
                scp.scalePicture();
                scp.setJpgQuality(request.getMidresJpgQuality());
                scp.writeScaledJpg(highresFile);
                websiteMemberFiles.add(highresFile);
            } else {
                Files.copy(pictureInfo.getImageFile().toPath(), highresFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    @NotNull
    private ScalablePicture loadScalablePicture(final PictureInfo pictureInfo) throws IOException {
        final var scalablePicture = new ScalablePicture();
        scalablePicture.setQualityScale();
        scalablePicture.setScaleSteps(request.getScalingSteps());
        scalablePicture.loadPictureImd(pictureInfo.getSha256(), pictureInfo.getImageFile(), pictureInfo.getRotation());

        LOGGER.log(Level.INFO, "Done Loading: {0}", pictureInfo.getImageLocation());
        if (scalablePicture.getOriginalImage() == null) {
            LOGGER.log(Level.SEVERE, "Problem reading image {0} using BrokenThumbnailImage instead", pictureInfo.getImageLocation());
            BrokenThumbnailImage.getImage(scalablePicture);
        }
        return scalablePicture;
    }

    @NotNull
    private StringBuilder startDhtmlArray(final int childNumber, final int childCount, final PictureInfo pictureInfo, final String htmlFriendlyDescription) {
        final var dhtmlArray = new StringBuilder(String.format("content[0]='" + "<p><strong>Picture</strong> %d of %d:</p><p><b>Description:</b><br>%s</p>", childNumber, childCount, htmlFriendlyDescription));
        if (!pictureInfo.getCreationTime().isEmpty()) {
            dhtmlArray.append("<p><strong>Date:</strong><br>").append(pictureInfo.getCreationTime().replace("'", "\\\\'")).append("</p>");
        }

        if (!pictureInfo.getPhotographer().isEmpty()) {
            dhtmlArray.append("<strong>Photographer:</strong><br>").append(pictureInfo.getPhotographer().replace("'", "\\\\'")).append("<br>");
        }
        if (!pictureInfo.getComment().isEmpty()) {
            dhtmlArray.append("<b>Comment:</b><br>").append(pictureInfo.getComment().replace("'", "\\\\'")).append("<br>");
        }
        if (!pictureInfo.getFilmReference().isEmpty()) {
            dhtmlArray.append("<strong>Film Reference:</strong><br>").append(pictureInfo.getFilmReference().replace("'", "\\\\'")).append("<br>");
        }
        if (!pictureInfo.getCopyrightHolder().isEmpty()) {
            dhtmlArray.append("<strong>Copyright Holder:</strong><br>").append(pictureInfo.getCopyrightHolder().replace("'", "\\\\'")).append("<br>");
        }

        dhtmlArray.append("'\n");
        return dhtmlArray;
    }

    private void writeMidresLinks(final SortableDefaultMutableTreeNode pictureNode, final File highresFile, final BufferedWriter midresHtmlWriter) throws IOException {
        writeHyperlink(midresHtmlWriter, lowresHtmlFiles.get(pictureNode.getParent().hashCode()).getName() + "#" + StringEscapeUtils.escapeHtml4(lowresFiles.get(pictureNode.hashCode()).getName()), "Up");
        midresHtmlWriter.write(NBSP);
        midresHtmlWriter.newLine();
        if (pictureNode != pictureNode.getParent().getFirstChild()) {
            writeHyperlink(midresHtmlWriter, midresHtmlFiles.get(pictureNode.getPreviousSibling().hashCode()).getName(), "Previous");
            midresHtmlWriter.write(NBSP);
        }
        if (request.isLinkToHighres()) {
            final PictureInfo pictureInfo = (PictureInfo) pictureNode.getUserObject();
            writeHyperlink(midresHtmlWriter, pictureInfo.getImageLocation(), "Highres");
            midresHtmlWriter.write(NBSP);
        } else if (request.isExportHighres()) {
            // Link to Highres in target directory
            writeHyperlink(midresHtmlWriter, highresFile.getName(), "Highres");
            midresHtmlWriter.write(NBSP);
        }
        if (pictureNode != pictureNode.getParent().getLastChild()) {
            writeHyperlink(midresHtmlWriter, midresHtmlFiles.get(pictureNode.getNextSibling().hashCode()).getName(), "Next");
            midresHtmlWriter.newLine();
        }
        if (request.isGenerateZipfile()) {
            midresHtmlWriter.write("<br>");
            writeHyperlink(midresHtmlWriter, request.getDownloadZipFileName(), "Download Zip");
            midresHtmlWriter.newLine();
        }
        midresHtmlWriter.write("</p>");
    }

    private void sshCopyToServer(final List<File> files) {
        LOGGER.info("Setting up ssh connection:");
        final var jsch = new JSch();
        try {
            final Session session = getSshSession(jsch, request);

            for (final File file : files) {
                publish(String.format("scp %s", file.getName()));
                scp(session, file);
            }

            session.disconnect();
        } catch (final JSchException | IOException ex) {
            LOGGER.severe(ex.getMessage());
        }
    }

    @NotNull
    public static Session getSshSession(final JSch jsch, final GenerateWebsiteRequest request) throws JSchException {
        LOGGER.log(Level.INFO, "Setting up session for user: {0} server: {1} port: {2} and connecting...", new Object[]{request.getSshUser(), request.getSshServer(), request.getSshPort()});
        final var session = jsch.getSession(request.getSshUser(), request.getSshServer(), request.getSshPort());
        if (request.getSshAuthType().equals(GenerateWebsiteRequest.SshAuthType.SSH_AUTH_PASSWORD)) {
            session.setPassword(request.getSshPassword());
        } else {
            jsch.addIdentity(request.getSshKeyFile());
        }
        final var properties = new Properties();
        properties.put("StrictHostKeyChecking", "no");
        session.setConfig(properties);
        session.connect();
        return session;
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
            final var outputStream = channel.getOutputStream();
            final var inputStream = channel.getInputStream()) {
            LOGGER.info("Connecting Channel...");
            channel.connect();

            if (checkAck(inputStream) != 0) {
                LOGGER.info("No Ack 1");
            }

            command = "T " + (file.lastModified() / 1000) + " 0";
            // The access time should be sent here,
            // but it is not accessible with JavaAPI ;-<
            command += (" " + (file.lastModified() / 1000) + " 0\n");
            LOGGER.log(Level.INFO, "Command: {0}", command);
            outputStream.write(command.getBytes());
            outputStream.flush();
            if (checkAck(inputStream) != 0) {
                LOGGER.info("No Ack 2");
            }

            // send "C0644 filesize filename", where filename should not include '/'
            long filesize = file.length();
            command = "C0644 " + filesize + " ";
            command += file.getName();
            command += "\n";
            LOGGER.log(Level.INFO, "Command: {0}", command);
            outputStream.write(command.getBytes());
            outputStream.flush();
            if (checkAck(inputStream) != 0) {
                LOGGER.info("No Ack 3");
            }

            // send a content of lfile
            try (
                final var fileInputStream = new FileInputStream(file)) {
                final var buf = new byte[1024];
                while (true) {
                    LOGGER.log(Level.INFO, "Sending bytes: {0}", buf.length);
                    int len = fileInputStream.read(buf, 0, buf.length);
                    if (len <= 0) {
                        break;
                    }
                    outputStream.write(buf, 0, len);
                }

                LOGGER.info("Sending \0");
                // send '\0'
                buf[0] = 0;
                outputStream.write(buf, 0, 1);
                outputStream.flush();
            }
            if (checkAck(inputStream) != 0) {
                LOGGER.info("No Ack 4");
            }

            LOGGER.info(command);
            channel.disconnect();
        }
    }

    /*
     * to test you can start up a simple ftp server with docker:
     * docker run -d -v <directory on your host>:/home/vsftpd  -p 20:20 -p 21:21 -p 47400-47470:47400-47470 -e FTP_USER=user -e FTP_PASS=password -e PASV_ADDRESS=<ip address, yes ip, not hostname!> --name ftp --restart=always bogem/ftp
     * username = user
     * password = password
     */
    private void ftpCopyToServer(final Collection<File> files) {
        LOGGER.info("Setting up ftp connection:");
        final var ftp = new FTPClient();
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
            for (final var file : files) {
                try (final var bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
                    final var remote = request.getFtpTargetDir() + file.getName();
                    LOGGER.log(Level.INFO, "Putting file {0} to {1}:{2}", new Object[]{file, request.getFtpServer(), remote});
                    boolean done = ftp.storeFile(remote, bufferedInputStream);
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
