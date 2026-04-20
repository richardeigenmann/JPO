package org.jpo.eventbus;

import org.jpo.gui.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.gui.JpoResources;

import java.awt.*;
import java.io.File;

/*
 Copyright (C) 2008-2025 Richard Eigenmann, Zürich
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
 * This object holds the details of how the WebsiteGenerator is supposed to
 * generate the output pages. It simplifies the interaction between the GUI and
 * the worker thread significantly.
 * <p>
 * This is not a record because of all the setters.
 */
public class GenerateWebsiteRequest {

    /**
     * The directory into which the web page will be generated.
     */
    private File targetDirectory;

    /**
     * Returns the background color
     * @return the background color
     */
    private Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the background color for the webpage
     * @param backgroundColor background color for the website
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * returns the Cellspacing
     * @return the cellspacing
     */
    public int getCellspacing() {
        return cellspacing;
    }

    /**
     * sets the Cellspacing
     *
     * @param cellspacing Cellspacing in the output
     */
    public void setCellspacing(final int cellspacing) {
        this.cellspacing = cellspacing;
    }

    /**
     * Returns if we should export highres pictures
     * @return true if highres pictures are to be exported
     */
    public boolean isExportHighres() {
        return exportHighres;
    }

    public void setExportHighres(final boolean exportHighres) {
        this.exportHighres = exportHighres;
    }

    private Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(final Color fontColor) {
        this.fontColor = fontColor;
    }
    /**
     * A flag to indicate whether midres HTML pages should be generated.
     */
    private boolean generateMidresHtml;

    /**
     * @return the generateMidresHtml
     */
    public boolean isGenerateMidresHtml() {
        return generateMidresHtml;
    }

    /**
     * @param generateMidresHtml the generateMidresHtml to set
     */
    public void setGenerateMidresHtml(final boolean generateMidresHtml) {
        this.generateMidresHtml = generateMidresHtml;
    }

    public boolean isGenerateMap() {
        return generateMap;
    }

    public void setGenerateMap(final boolean generateMap) {
        this.generateMap = generateMap;
    }

    public boolean isGenerateMouseover() {
        return generateMouseover;
    }

    public void setGenerateMouseover(final boolean generateDHTML) {
        this.generateMouseover = generateDHTML;
    }

    public boolean isGenerateZipfile() {
        return generateZipfile;
    }

    public void setGenerateZipfile(final boolean generateZipfile) {
        this.generateZipfile = generateZipfile;
    }

    /**
     * The directory the web pages should be written to
     *
     * @return The directory where the web pages should be written to
     */
    public File getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(final File htmlDirectory) {
        this.targetDirectory = htmlDirectory;
    }

    public boolean isLinkToHighres() {
        return linkToHighres;
    }

    public void setLinkToHighres(boolean linkToHighres) {
        this.linkToHighres = linkToHighres;
    }

    //------------------Lowres-------------------
    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setThumbnailHeight(int thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailWidth(int thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }

    /**
     * Convenience method that returns the size of the thumbnails in a new
     * Dimension object.
     *
     * @return The desired dimensions of the Thumbnail size and width.
     */
    public Dimension getThumbnailDimension() {
        return new Dimension( getThumbnailWidth(), getThumbnailHeight() );
    }
    /**
     * The number of scaling steps. Interestingly the quality gets better if
     * scaling is done in several steps.
     */
    private int scalingSteps = 1;

    /**
     * The number of scaling steps. Interestingly the quality gets better if
     * scaling is done in several steps.
     *
     * @return the number of scaling steps
     */
    public int getScalingSteps() {
        return scalingSteps;
    }

    /**
     * Sets the number of scaling steps. Interestingly the quality gets better
     * if scaling is done in several steps.
     *
     * @param scalingSteps the new number of scaling Steps
     */
    public void setScalingSteps(int scalingSteps) {
        this.scalingSteps = scalingSteps;
    }

    /**
     * The compression rate passed to the jpg compressor 0 - 1. A value of 0
     * means maximum compression and crap quality, 1 means best quality minimal
     * compression. 0.8 is a good value.
     */
    private float lowresJpgQuality;

    /**
     * Returns the lowres jpg quality factor
     *
     * @return the lowres jpg quality factor
     */
    public float getLowresJpgQuality() {
        return lowresJpgQuality;
    }

    /**
     * Same as
     *
     * @return the lowres quality number
     * @see #getLowresJpgQuality() but returned as int and multiplied by 100
     */
    public int getLowresJpgQualityPercent() {
        return (int) ( getLowresJpgQuality() * 100 );
    }

    /**
     * sets the lowers quality number
     *
     * @param lowresJpgQuality the lowres quality
     */
    public void setLowresJpgQuality( float lowresJpgQuality ) {
        this.lowresJpgQuality = lowresJpgQuality;
    }

    /**
     * Convenience method that allows the quality to be specified between 0 and
     * 100 as an integer. If the value is out of bounds it is raised to 0 or
     * lowered to 100.
     *
     * @param lowresJpgQualityPercent the lowres quality
     */
    public void setLowresJpgQualityPercent( int lowresJpgQualityPercent ) {
        if ( lowresJpgQualityPercent > 100 ) {
            lowresJpgQualityPercent = 100;
        } else if ( lowresJpgQualityPercent < 0 ) {
            lowresJpgQualityPercent = 0;
        }
        setLowresJpgQuality((float) lowresJpgQualityPercent / 100 );
    }
    
    
    //------------------Midres-------------------
    /**
     * The width the midres picture must not exceed.
     */
    private int midresWidth;
    /**
     * The height the midres picture must not exceed.
     */
    private int midresHeight;

    public int getMidresHeight() {
        return midresHeight;
    }

    public void setMidresHeight(final int midresHeight) {
        this.midresHeight = midresHeight;
    }

    public float getMidresJpgQuality() {
        return midresJpgQuality;
    }

    public void setMidresJpgQuality(final float midresJpgQuality) {
        this.midresJpgQuality = midresJpgQuality;
    }

    /**
     * Same as {@link #getMidresJpgQuality} but returned as int and multiplied
     * by 100
     *
     * @return the midres quality number
     */
    public int getMidresJpgQualityPercent() {
        return (int) ( getMidresJpgQuality() * 100 );
    }

    /**
     * Convenience method that allows the quality to be specified between 0 and
     * 100 as an integer. If the value is out of bounds it is raised to 0 or
     * lowered to 100.
     *
     * @param midresJpgQuality the desired quality between 0 and 100
     */
    public void setMidresJpgQualityPercent( float midresJpgQuality ) {
        if ( midresJpgQuality > 100 ) {
            midresJpgQuality = 100;
        } else if ( midresJpgQuality < 0 ) {
            midresJpgQuality = 0;
        }
        setMidresJpgQuality( midresJpgQuality / 100 );
    }

    /**
     * Returns the width of the midres images
     *
     * @return the width of the midres images
     */
    public int getMidresWidth() {
        return midresWidth;
    }

    public void setMidresWidth(final int midresWidth) {
        this.midresWidth = midresWidth;
    }

    /**
     * Convenience method the generates a new Dimension object with the Midres
     * dimensions.
     *
     * @return A new object with the Midres dimensions.
     */
    public Dimension getMidresDimension() {
        return new Dimension( getMidresWidth(), getMidresHeight() );
    }
    /**
     * The compression rate passed to the jpg compressor 0 - 1. A value of 0
     * means maximum compression and crap quality, 1 means best quality minimal
     * compression. 0.8 is a good value.
     */
    private float midresJpgQuality;

    /**
     * The number of rows that should be generated on the group overview page
     *
     * @return The number of rows that should be generated on the group overview
     * page
     */
    public int getPicsPerRow() {
        return picsPerRow;
    }

    public void setPicsPerRow(final int picsPerRow) {
        this.picsPerRow = picsPerRow;
    }

    /**
     * The note from which to start
     *
     * @return The node from which to start
     */
    public SortableDefaultMutableTreeNode getStartNode() {
        return startNode;
    }

    public void setStartNode(final SortableDefaultMutableTreeNode startNode) {
        this.startNode = startNode;
    }
    /**
     * How many pictures should be placed next to each other in the html table.
     */
    private int picsPerRow = 3;
    /**
     * The width the thumbnail must not exceed.
     */
    private int thumbnailWidth;
    /**
     * The height the thumbnail must not exceed.
     */
    private int thumbnailHeight;
    /**
     * The padding between two adjacent cells in the output table.
     */
    private int cellspacing;
    /**
     * Indicates whether a highres image should be copied as well.
     */
    private boolean exportHighres;
    /**
     * Indicates whether a highres image should be rotated.
     */
    private boolean rotateHighres;

    /**
     * Indicate that a highres image should be rotated
     *
     * @param rotateHighres whether to rotate highres images
     */
    public void setRotateHighres( boolean rotateHighres ) {
        this.rotateHighres = rotateHighres;
    }

    /**
     * returns whether a highres image should be rotated
     *
     * @return true if the highres image should be rotated, false if not
     */
    public boolean isRotateHighres() {
        return rotateHighres;
    }
    /**
     * Indicates whether the highres pictures should be linked to.
     */
    private boolean linkToHighres;
    /**
     * The first node from which the export is to be done.
     */
    private SortableDefaultMutableTreeNode startNode;
    /**
     * Indicator that gets set to true if group nodes are being written so that
     * the folder icon is created.
     */
    private boolean folderIconRequired;

    public boolean isFolderIconRequired() {
        return folderIconRequired;
    }

    public void setFolderIconRequired(final boolean folderIconRequired) {
        this.folderIconRequired = folderIconRequired;
    }

    /**
     * A flag to indicate whether a map should be generated.
     */
    private boolean generateMap;

    private String googleMapsApiKey;

    /**
     * A flag to indicate whether DHTML elements should be generated.
     */
    private boolean generateMouseover;
    /**
     * A flag to indicate whether a Zipfile with Highres Images should be
     * generated.
     */
    private boolean generateZipfile;
    /**
     * The background colour for the web pages
     */
    private Color backgroundColor;
    /**
     * The colour to be used for the fonts.
     */
    private Color fontColor;
    /**
     * File name for the Zip File
     */
    private String downloadZipFileName;

    /**
     * Returns the name for the ZipFile containing the downloadable images
     *
     * @return The name of the file to create (no path information)
     */
    public String getDownloadZipFileName() {
        return downloadZipFileName;
    }

    /**
     * Sets the name of the Zip file to be created
     *
     * @param downloadZipFileName the name of the zip file to create
     */
    public void setDownloadZipFileName(final String downloadZipFileName) {
        this.downloadZipFileName = downloadZipFileName;
    }

    /**
     * @return the ftpTargetDir
     */
    public String getFtpTargetDir() {
        return ftpTargetDir;
    }

    /**
     * @param ftpTargetDir the ftpTargetDir to set
     */
    public void setFtpTargetDir(final String ftpTargetDir) {
        this.ftpTargetDir = ftpTargetDir;
    }

    /**
     * @return the sshServer
     */
    public String getSshServer() {
        return sshServer;
    }

    /**
     * @param sshServer the sshServer to set
     */
    public void setSshServer(final String sshServer) {
        this.sshServer = sshServer;
    }

    /**
     * @return the sshUser
     */
    public String getSshUser() {
        return sshUser;
    }

    /**
     * @param sshUser the sshUser to set
     */
    public void setSshUser(final String sshUser) {
        this.sshUser = sshUser;
    }
    /**
     * The ssh password
     */
    private String sshPassword = "";

    /**
     * @return the sshPassword
     */
    public String getSshPassword() {
        return sshPassword;
    }

    /**
     * @param sshPassword the sshPassword to set
     */
    public void setSshPassword(final String sshPassword) {
        this.sshPassword = sshPassword;
    }

    /**
     * @return the sshTargetDir
     */
    public String getSshTargetDir() {
        return sshTargetDir;
    }

    /**
     * @param sshTargetDir the sshTargetDir to set
     */
    public void setSshTargetDir(final String sshTargetDir) {
        this.sshTargetDir = sshTargetDir;
    }

    /**
     * @return the sshKeyFile
     */
    public String getSshKeyFile() {
        return sshKeyFile;
    }

    /**
     * @param sshKeyFile the sshKeyFile to set
     */
    public void setSshKeyFile(final String sshKeyFile) {
        this.sshKeyFile = sshKeyFile;
    }

    /**
     * Returns the memorised Google Maps API key
     *
     * @return the Google Maps API key
     */
    public String getGoogleMapsApiKey() {
        return googleMapsApiKey;
    }

    /**
     * Rememebrs the Google Maps API key
     *
     * @param googleMapsApiKey the key to remember
     */
    public void setGoogleMapsApiKey(String googleMapsApiKey) {
        this.googleMapsApiKey = googleMapsApiKey;
    }

    /**
     * Define the types of output naming convention
     */
    public enum PictureNamingType {

        PICTURE_NAMING_BY_HASH_CODE, PICTURE_NAMING_BY_SEQUENTIAL_NUMBER, PICTURE_NAMING_BY_ORIGINAL_NAME
    }

    /**
     * field to store the type of picture naming convention
     */
    private PictureNamingType pictureNaming = PictureNamingType.PICTURE_NAMING_BY_HASH_CODE;

    /**
     * Returns the method for picture Naming
     *
     * @return the picture naming code
     */
    public PictureNamingType getPictureNaming() {
        return pictureNaming;
    }

    /**
     * Sets the method for picture naming. Validates that the number is in the
     * bounds
     *
     * @param pictureNaming Picture Naming Type
     */
    public void setPictureNaming(final PictureNamingType pictureNaming) {
        this.pictureNaming = pictureNaming;
    }
    /**
     * The start number for the sequential numbering
     */
    private int sequentialStartNumber = 1;

    /**
     * @return the setSequentialStartNumber
     */
    public int getSequentialStartNumber() {
        return sequentialStartNumber;
    }

    /**
     * @param setSequentialStartNumber the setSequentialStartNumber to set
     */
    public void setSequentialStartNumber(final int setSequentialStartNumber) {
        this.sequentialStartNumber = setSequentialStartNumber;
    }
    /**
     * Whether to write the robots.txt file
     */
    private boolean writeRobotsTxt;  // default is false

    /**
     * @return the writeRobotsTxt
     */
    public boolean isWriteRobotsTxt() {
        return writeRobotsTxt;
    }

    /**
     * @param writeRobotsTxt the writeRobotsTxt to set
     */
    public void setWriteRobotsTxt(final boolean writeRobotsTxt) {
        this.writeRobotsTxt = writeRobotsTxt;
    }

    /**
     * Define the types of output
     */
    public enum OutputTarget {

        OUTPUT_LOCAL_DIRECTORY, OUTPUT_FTP_LOCATION, OUTPUT_SSH_LOCATION
    }
    /**
     * field to store the type of picture naming convention
     */
    private OutputTarget outputTarget = OutputTarget.OUTPUT_LOCAL_DIRECTORY;

    /**
     * Returns the output target choice
     *
     * @return the output target choice
     */
    public OutputTarget getOutputTarget() {
        return outputTarget;
    }

    /**
     * Sets the method for output targets
     *
     * @param outputTarget the output target
     */
    public void setOutputTarget(final OutputTarget outputTarget) {
        this.outputTarget = outputTarget;
    }
    
        /**
     * Define the types of SSH Authentication
     */
    public enum SshAuthType {
        SSH_AUTH_PASSWORD, SSH_AUTH_KEYFILE
    }
    /**
     * field to store the type of picture naming convention
     */
    private SshAuthType sshAuthType = SshAuthType.SSH_AUTH_PASSWORD;

    /**
     * Returns the output target choice
     *
     * @return the output target choice
     */
    public SshAuthType getSshAuthType() {
        return sshAuthType;
    }

    /**
     * Sets the method for output targets
     *
     * @param sshAuthType see the documentation of ssh
     */
    public void setSshAuthType(final SshAuthType sshAuthType) {
        this.sshAuthType = sshAuthType;
    }

    
    
    
    /**
     * The FTP Port
     */
    private int ftpPort = 21;

    /**
     * Returns the ftp port
     *
     * @return the ftp port
     */
    public int getFtpPort() {
        return ftpPort;
    }

    /**
     * Sets the ftp port
     *
     * @param ftpPort the ftp port
     */
    public void setFtpPort(final int ftpPort) {
        this.ftpPort = ftpPort;
    }
    /**
     * The SSH Port
     */
    private int sshPort = 22;

    /**
     * Returns the ssh port
     *
     * @return the ssh port
     */
    public int getSshPort() {
        return sshPort;
    }

    /**
     * Sets the ssh port
     *
     * @param sshPort the ssh port
     */
    public void setSshPort(final int sshPort) {
        this.sshPort = sshPort;
    }
    /**
     * The ftp server
     */
    private String ftpServer = "";

    /**
     * @return the ftpServer
     */
    public String getFtpServer() {
        return ftpServer;
    }

    /**
     * @param ftpServer the server address
     */
    public void setFtpServer(final String ftpServer) {
        this.ftpServer = ftpServer;
    }
    /**
     * The ftp user
     */
    private String ftpUser = "";

    /**
     * @return the ftpUser
     */
    public String getFtpUser() {
        return ftpUser;
    }

    /**
     * @param ftpUser the ftpUser to set
     */
    public void setFtpUser(final String ftpUser) {
        this.ftpUser = ftpUser;
    }
    /**
     * The ftp password
     */
    private String ftpPassword = "";

    /**
     * @param ftpPassword the ftpPassword to set
     */
    public void setFtpPassword(final String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    /**
     * @return the ftpUser
     */
    public String getFtpPassword() {
        return ftpPassword;
    }
    /**
     * The ftp target directory
     */
    private String ftpTargetDir = "";
    /**
     * The ssh server
     */
    private String sshServer = "";
    /**
     * The ssh user
     */
    private String sshUser = "";
    /**
     * The ssh target dir
     */
    private String sshTargetDir = "";
    /**
     * The ssh key file
     */
    private String sshKeyFile = "";

    public boolean isOpenWebsiteAfterRendering() {
        return openWebsiteAfterRendering;
    }

    public void setOpenWebsiteAfterRendering(boolean openWebsiteAfterRendering) {
        this.openWebsiteAfterRendering = openWebsiteAfterRendering;
    }

    private boolean openWebsiteAfterRendering = true;


    /**
     * Formats a neat summary of the options
     *
     * @return A nicely formatted summary of the option that are set.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(JpoResources.getResource("HtmlDistThumbnails") + "\n");
        sb.append(JpoResources.getResource("picsPerRowText")).append(" ").append(getPicsPerRow()).append("\n");
        sb.append(JpoResources.getResource("thumbnailSizeJLabel")).append(" ").append(getThumbnailWidth()).append(" x ").append(getThumbnailHeight()).append("\n");
        sb.append(JpoResources.getResource("lowresJpgQualitySlider")).append(" ").append(getLowresJpgQualityPercent()).append("\n");
        sb.append(JpoResources.getResource("scalingSteps")).append(" ").append(getScalingSteps()).append("\n");

        sb.append("\n").append(JpoResources.getResource("HtmlDistMidres")).append("\n");
        sb.append(isGenerateMidresHtml() ? JpoResources.getResource("HtmlDistMidresHtml") + "\n" : "No medium size navigation pages\n");
        sb.append(isGenerateMap() ? JpoResources.getResource("GenerateMap") + "\n" : "No map\n");
        sb.append(isGenerateMap() ? "Google Maps APIKEY: " + getGoogleMapsApiKey() + "\n" : "");
        sb.append(isGenerateMouseover() ? JpoResources.getResource("org.jpo.export.GenerateWebsiteWizard3Midres.generateMouseoverJCheckBox") + "\n" : "No  DHTML mouseover effects\n");
        sb.append(JpoResources.getResource("midresSizeJLabel")).append(" ").append(getMidresWidth()).append(" x ").append(getMidresHeight()).append("\n");
        sb.append(JpoResources.getResource("midresJpgQualitySlider")).append(" ").append(getMidresJpgQualityPercent()).append("\n");

        sb.append("\n").append(JpoResources.getResource("HtmlDistHighres")).append("\n");
        sb.append(isExportHighres() ? "Export Highres Pictures\n" : "Do not export Highres Pictures\n");
        sb.append(isRotateHighres() ? "Rotate Highres Pictures\n" : "Do not rotate Highres Pictures\n");
        sb.append(isGenerateZipfile() ? JpoResources.getResource("generateZipfileJCheckBox") + "\n" : "No Zipfile for download of Highres Pictures\n");
        sb.append("Filename for Download Zipfile: ").append(getDownloadZipFileName()).append("\n");
        sb.append(isLinkToHighres() ? JpoResources.getResource("linkToHighresJCheckBox") + "\n" : "No Link to high resolution pictures at current location\n");

        sb.append("\n").append("Output mode: ").append(getOutputTarget().name());

        sb.append("\n").append(JpoResources.getResource("genericTargetDirText")).append(getTargetDirectory().getPath());
        sb.append("\n");

        sb.append( "\n" ).append( "Ftp Server: " ).append( getFtpServer() );
        sb.append( "\n" ).append( "Ftp Port: " ).append( getFtpPort() );
        sb.append( "\n" ).append( "Ftp User: " ).append( getFtpUser() );
        sb.append( "\n" ).append( "Ftp Password: " ).append( getFtpPassword() );
        sb.append( "\n" ).append( "Ftp Target Dir: " ).append( getFtpTargetDir() );
        sb.append( "\n" );

        sb.append("\n").append("SSH Server: ").append(getSshServer());
        sb.append("\n").append("SSH Port: ").append(getSshPort());
        sb.append("\n").append("SSH User: ").append(getSshUser());
        sb.append("\n").append("SSH Authentication: ").append(getSshAuthType().name());
        sb.append("\n").append("SSH Password: ").append(getSshPassword());
        sb.append("\n").append("SSH Key File: ").append(getSshKeyFile());
        sb.append("\n").append("SSH Target Dir: ").append(getSshTargetDir());
        sb.append("\n");


        sb.append("\n").append(JpoResources.getResource("HtmlDistOptions")).append("\n");
        sb.append(JpoResources.getResource("HtmlDistillerNumbering")).append(" ");
        switch (getPictureNaming()) {
            case PICTURE_NAMING_BY_HASH_CODE -> sb.append(JpoResources.getResource("hashcodeRadioButton"));
            case PICTURE_NAMING_BY_ORIGINAL_NAME -> sb.append(JpoResources.getResource("originalNameRadioButton"));
            default -> {
                sb.append(JpoResources.getResource("sequentialRadioButton"));
                sb.append(JpoResources.getResource("sequentialRadioButtonStart")).append(" ").append(getSequentialStartNumber());
            }
        }

        sb.append("\n");
        sb.append("Webpage Font Color: ").append(getFontColor().toString()).append("\n");
        sb.append("Webpage Background Color: ").append(getBackgroundColor().toString()).append("\n");
        sb.append(isWriteRobotsTxt() ? (JpoResources.getResource("generateRobotsJCheckBox") + "\n") : "Do not write robots.txt\n");

        return sb.toString();
    }

    /**
     * This optional method saves the options into the Settings object so that
     * they can be remembered for the next time Note: Not all of them (yet?)
     */
    public void saveToSettings() {
        Settings.memorizeCopyLocation(getTargetDirectory().getPath());
        Settings.setDefaultHtmlPicsPerRow(getPicsPerRow());
        Settings.setDefaultHtmlThumbnailWidth(getThumbnailWidth());
        Settings.setDefaultHtmlThumbnailHeight(getThumbnailHeight());
        Settings.setDefaultHtmlLowresQuality(getLowresJpgQuality());
        Settings.setDefaultGenerateMidresHtml(isGenerateMidresHtml());
        Settings.setDefaultGenerateMap(isGenerateMap());
        Settings.setDefaultGoogleMapsApiKey(getGoogleMapsApiKey());
        Settings.setDefaultGenerateDHTML(isGenerateMouseover());
        Settings.setDefaultHtmlMidresWidth(getMidresWidth());
        Settings.setDefaultHtmlMidresHeight(getMidresHeight());
        Settings.setDefaultHtmlMidresQuality(getMidresJpgQuality());
        Settings.setDefaultGenerateZipfile(isGenerateZipfile());
        Settings.setDefaultLinkToHighres(isLinkToHighres());
        Settings.setDefaultExportHighres(isExportHighres());
        Settings.setDefaultRotateHighres(isRotateHighres());
        Settings.setDefaultHtmlPictureNaming(getPictureNaming());
        Settings.setDefaultHtmlOutputTarget(getOutputTarget());
        Settings.setDefaultHtmlFtpServer(getFtpServer());
        Settings.setDefaultHtmlFtpPort(getFtpPort());
        Settings.setDefaultHtmlFtpUser(getFtpUser());
        Settings.setDefaultHtmlFtpPassword(getFtpPassword());
        Settings.setDefaultHtmlFtpTargetDir(getFtpTargetDir());
        Settings.setDefaultHtmlSshServer(getSshServer());
        Settings.setDefaultHtmlSshPort(getSshPort());
        Settings.setDefaultHtmlSshUser(getSshUser());
        Settings.setDefaultHtmlSshAuthType(getSshAuthType());
        Settings.setDefaultHtmlSshPassword(getSshPassword());
        Settings.setDefaultHtmlSshTargetDir(getSshTargetDir());
        Settings.setDefaultHtmlSshKeyFile(getSshKeyFile());
        Settings.setHtmlBackgroundColor(getBackgroundColor());
        Settings.setHtmlFontColor(getFontColor());
        Settings.setWriteRobotsTxt(isWriteRobotsTxt());
        Settings.setUnsavedSettingChanges(true);
    }
}
