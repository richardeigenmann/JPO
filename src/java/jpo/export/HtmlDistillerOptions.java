package jpo.export;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import javax.swing.SpinnerNumberModel;
import jpo.Settings;
import jpo.SortableDefaultMutableTreeNode;

/*
HtmlDistillerOptions.java:  Holds the options that configure the html output.
Copyright (C) 2008  Richard Eigenmann.
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
 *  This object holds the details of how the HtmlDistiller is supposed to generate the 
 *  output pages. It simplifies the interaction between the GUI and the worker thread significantly.
 */
public class HtmlDistillerOptions {

    /**
     *  The directory into which the web page will be generated.
     */
    private File targetDirectory;

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor( Color backgroundColor ) {
        this.backgroundColor = backgroundColor;
    }

    public int getCellspacing() {
        return cellspacing;
    }

    public void setCellspacing( int cellspacing ) {
        this.cellspacing = cellspacing;
    }

    public boolean isExportHighres() {
        return exportHighres;
    }

    public void setExportHighres( boolean exportHighres ) {
        this.exportHighres = exportHighres;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor( Color fontColor ) {
        this.fontColor = fontColor;
    }
    /**
     *   A flag to indicate whether midres HTML pages should be generated. 
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
    public void setGenerateMidresHtml( boolean generateMidresHtml ) {
        this.generateMidresHtml = generateMidresHtml;
    }

    public boolean isGenerateDHTML() {
        return generateDHTML;
    }

    public void setGenerateDHTML( boolean generateDHTML ) {
        this.generateDHTML = generateDHTML;
    }

    public boolean isGenerateZipfile() {
        return generateZipfile;
    }

    public void setGenerateZipfile( boolean generateZipfile ) {
        this.generateZipfile = generateZipfile;
    }

    /**
     * The directory the pages should be written to
     * @return
     */
    public File getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory( File htmlDirectory ) {
        this.targetDirectory = htmlDirectory;
    }

    public boolean isLinkToHighres() {
        return linkToHighres;
    }

    public void setLinkToHighres( boolean linkToHighres ) {
        this.linkToHighres = linkToHighres;
    }

    //------------------Lowres-------------------
    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setThumbnailHeight( int thumbnailHeight ) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailWidth( int thumbnailWidth ) {
        this.thumbnailWidth = thumbnailWidth;
    }
    /**
     *   The compression rate passed to the jpg compressor 0 - 1. A value of 0 means maximum
     *   compression and crap quality, 1 means best quality minimal compression. 
     *   0.8 is a good value.
     */
    private float lowresJpgQuality;

    public float getLowresJpgQuality() {
        return lowresJpgQuality;
    }

    /** 
     * Same as @see getLowresJpgQuality but returned as int and multiplied by 100
     * @return
     */
    public int getLowresJpgQualityPercent() {
        return (int) ( getLowresJpgQuality() * 100 );
    }

    public void setLowresJpgQuality( float lowresJpgQuality ) {
        this.lowresJpgQuality = lowresJpgQuality;
    }

    /**
     * Convenience method that allows the quality to be specified between 0 and 100 
     * as an integer. If the value is out of bounds it is raised to 0 or lowered
     * to 100.
     * @param lowresJpgQuality
     */
    public void setLowresJpgQualityPercent( int lowresJpgQuality ) {
        if ( lowresJpgQuality > 100 ) {
            lowresJpgQuality = 100;
        } else if ( lowresJpgQuality < 0 ) {
            lowresJpgQuality = 0;
        }
        setLowresJpgQuality( (float) lowresJpgQuality / 100 );
    }
    //------------------Midres-------------------
    /**
     *  The width the midres picture must not exceed.
     */
    private int midresWidth;
    /**
     *  The height the midres picture must not exceed.
     */
    private int midresHeight;

    public int getMidresHeight() {
        return midresHeight;
    }

    public void setMidresHeight( int midresHeight ) {
        this.midresHeight = midresHeight;
    }

    public float getMidresJpgQuality() {
        return midresJpgQuality;
    }

    public void setMidresJpgQuality( float midresJpgQuality ) {
        this.midresJpgQuality = midresJpgQuality;
    }

    /**
     * Same as @see getMidresJpgQuality but returned as int and multiplied by 100
     * @return
     */
    public int getMidresJpgQualityPercent() {
        return (int) ( getMidresJpgQuality() * 100 );
    }

    /**
     * Convenience method that allows the quality to be specified between 0 and 100 
     * as an integer. If the value is out of bounds it is raised to 0 or lowered
     * to 100.
     * @param lowresJpgQuality
     */
    public void setMidresJpgQualityPercent( float midresJpgQuality ) {
        if ( midresJpgQuality > 100 ) {
            midresJpgQuality = 100;
        } else if ( midresJpgQuality < 0 ) {
            midresJpgQuality = 0;
        }
        setMidresJpgQuality( (float) midresJpgQuality / 100 );
    }

    public int getMidresWidth() {
        return midresWidth;
    }

    public void setMidresWidth( int midresWidth ) {
        this.midresWidth = midresWidth;
    }

    /**
     * Convenience method the generates a NEW Dimension object with the 
     * Midres dimensions.
     * @return A new object with the Midres dimensions.
     */
    public Dimension getMidresDimension() {
        return new Dimension( getMidresWidth(), getMidresHeight() );
    }
    /**
     *   The compression rate passed to the jpg compressor 0 - 1. A value of 0 means maximum
     *   compression and crap quality, 1 means best quality minimal compression. 
     *   0.8 is a good value.
     */
    private float midresJpgQuality;

    public int getPicsPerRow() {
        return picsPerRow;
    }

    public void setPicsPerRow( int picsPerRow ) {
        this.picsPerRow = picsPerRow;
    }

    public SortableDefaultMutableTreeNode getStartNode() {
        return startNode;
    }

    public void setStartNode( SortableDefaultMutableTreeNode startNode ) {
        this.startNode = startNode;
    }
    /**
     *   How many pictures should be placed next to each 
     *   other in the html table.
     */
    private int picsPerRow;
    /**
     *  The width the thumbnail must not exceed.
     */
    private int thumbnailWidth;
    /**
     *  The height the thumbnail must not exceed.
     */
    private int thumbnailHeight;
    /**
     *  The padding between two adjacent cells in the output table.
     */
    private int cellspacing;
    /**
     *   Indicates whether a highres image should be copied as well.
     */
    private boolean exportHighres;
    /**
     *   Indicates whether the highres pictures should be linked to.
     */
    private boolean linkToHighres;
    /**
     *  The first node from which the export is to be done.
     */
    private SortableDefaultMutableTreeNode startNode;
    /**
     *   Indicator that gets set to true if group nodes are being written so that
     *   the folder icon is created.
     */
    private boolean folderIconRequired;

    public boolean isFolderIconRequired() {
        return folderIconRequired;
    }

    public void setFolderIconRequired( boolean folderIconRequired ) {
        this.folderIconRequired = folderIconRequired;
    }
    /**
     *   A flag to indicate whether DHTML elements should be generated. 
     */
    private boolean generateDHTML;
    /**
     *   A flag to indicate whether a Zipfile with Highres Images should be generated. 
     */
    private boolean generateZipfile;
    /**
     *  The background color for the web pages
     */
    private Color backgroundColor;
    /**
     *  The color to be used for the fonts.
     */
    private Color fontColor;
    /**
     * File name for the Zip File
     */
    private String downloadZipFileName;

    /**
     * Returns the name for the ZipFile containing the downloadable images
     * @return The name of the file to create (no path information)
     */
    public String getDownloadZipFileName() {
        return downloadZipFileName;
    }

    /**
     * Sets the name of the Zip file to be created
     * @param zipFileName
     */
    public void setDownloadZipFileName( String downloadZipFileName ) {
        this.downloadZipFileName = downloadZipFileName;
    }
    /**
     * The method used to determine the picture filename naming 
     */
    //private int pictureNaming = PICTURE_NAMING_BY_HASH_CODE;
    private int pictureNaming = PICTURE_NAMING_BY_HASH_CODE;

    /**
     * Returns the method for picture Naming
     * @return
     */
    public int getPictureNaming() {
        return pictureNaming;
    }

    /**
     * Sets the method for picture naming. Validates that the number is in the bounds
     * @param pictureNaming
     */
    public void setPictureNaming( int pictureNaming ) {
        if ( ( pictureNaming < PICTURE_NAMING_BY_HASH_CODE ) || ( pictureNaming > PICTURE_NAMING_BY_ORIGINAL_NAME ) ) {
            pictureNaming = PICTURE_NAMING_BY_HASH_CODE;
        }
        this.pictureNaming = pictureNaming;
    }
    public static final int PICTURE_NAMING_BY_HASH_CODE = 1;
    public static final int PICTURE_NAMING_BY_SEQUENTIAL_NUMBER = PICTURE_NAMING_BY_HASH_CODE + 1;
    public static final int PICTURE_NAMING_BY_ORIGINAL_NAME = PICTURE_NAMING_BY_SEQUENTIAL_NUMBER + 1;
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
    public void setSequentialStartNumber( int setSequentialStartNumber ) {
        this.sequentialStartNumber = setSequentialStartNumber;
    }
    /**
     * Whether to write the robots.txt file
     */
    private boolean writeRobotsTxt = false;

    /**
     * @return the writeRobotsTxt
     */
    public boolean isWriteRobotsTxt() {
        return writeRobotsTxt;
    }

    /**
     * @param writeRobotsTxt the writeRobotsTxt to set
     */
    public void setWriteRobotsTxt( boolean writeRobotsTxt ) {
        this.writeRobotsTxt = writeRobotsTxt;
    }

    /**
     * Formats a neat summary of the options
     * @return A nicely formatted summary of the option that are set.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer( Settings.jpoResources.getString( "HtmlDistThumbnails" ) + "\n" );
        sb.append( Settings.jpoResources.getString( "picsPerRowText" ) + " " + getPicsPerRow() + "\n" );
        sb.append( Settings.jpoResources.getString( "thubnailSizeJLabel" ) + " " + Integer.toString( getThumbnailWidth() ) + " x " + Integer.toString( getThumbnailHeight() ) + "\n" );
        sb.append( Settings.jpoResources.getString( "lowresJpgQualitySlider" ) + " " + Integer.toString( getLowresJpgQualityPercent() ) + "\n" );

        sb.append( "\n" + Settings.jpoResources.getString( "HtmlDistMidres" ) +"\n" );
        sb.append( isGenerateMidresHtml() ? Settings.jpoResources.getString( "HtmlDistMidresHtml" ) + "\n" : "No medium size navigation pages\n" );
        sb.append( isGenerateDHTML() ? Settings.jpoResources.getString( "generateDHTMLJCheckBox" ) + "\n" : "No  DHTML mouseover effects\n" );
        sb.append( Settings.jpoResources.getString( "midresSizeJLabel" ) + " " + Integer.toString( getMidresWidth() ) + " x " + Integer.toString( getMidresHeight() ) + "\n" );
        sb.append( Settings.jpoResources.getString( "midresJpgQualitySlider" ) + " " + Integer.toString( getMidresJpgQualityPercent() ) + "\n" );

        sb.append( "\n" + Settings.jpoResources.getString( "HtmlDistHighres" ) + "\n" );
        sb.append( isExportHighres() ? "Export Highres Pictures\n" : "Do not export Highres Pictures\n" );
        sb.append( isGenerateZipfile() ? Settings.jpoResources.getString( "generateZipfileJCheckBox" ) + "\n" : "No Zipfile for download of Highres Pictures\n" );
        sb.append( "Filename for Download Zipfile: " + getDownloadZipFileName() + "\n" );
        sb.append( isLinkToHighres() ? Settings.jpoResources.getString( "linkToHighresJCheckBox" ) + "\n" : "No Link to high resolution pictures at current location\n" );

        sb.append( "\n" + Settings.jpoResources.getString( "genericTargetDirText" ) + getTargetDirectory().getPath() );
        sb.append( "\n" );

        sb.append( "\n" + Settings.jpoResources.getString( "HtmlDistOptions" ) + "\n" );
        sb.append( Settings.jpoResources.getString( "HtmlDistillerNumbering" ) + " " );
        switch ( getPictureNaming() ) {
            case PICTURE_NAMING_BY_HASH_CODE:
                sb.append( Settings.jpoResources.getString( "hashcodeRadioButton" ) );
                break;
            case PICTURE_NAMING_BY_ORIGINAL_NAME:
                sb.append( Settings.jpoResources.getString( "originalNameRadioButton" ) );
                break;
            case PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
                sb.append( Settings.jpoResources.getString( "sequentialRadioButton" ) );
                sb.append( Settings.jpoResources.getString( "sequentialRadioButtonStart" ) + " " + Integer.toString( getSequentialStartNumber() ) );
                break;
        }

        sb.append( "\n" );
        sb.append( "Webpage Font Color: " + getFontColor().toString() + "\n" );
        sb.append( "Webpage Background Color: " + getBackgroundColor().toString() + "\n" );
        sb.append( isWriteRobotsTxt() ? ( Settings.jpoResources.getString( "generateRobotsJCheckBox" ) + "\n" ) : "Do not write robots.txt\n" );

        return sb.toString();
    }

    /**
     * This optional method saves the options into the Settings object so that they can be remembered for the next time
     * Note: Not all of them (yet?)
     */
    public void saveToSettings() {
        Settings.memorizeCopyLocation( getTargetDirectory().getPath() );
        Settings.defaultHtmlPicsPerRow = getPicsPerRow();
        Settings.defaultHtmlThumbnailWidth = getThumbnailWidth();
        Settings.defaultHtmlThumbnailHeight = getThumbnailHeight();
        Settings.defaultHtmlLowresQuality = getLowresJpgQuality();
        Settings.defaultGenerateMidresHtml = isGenerateMidresHtml();
        Settings.defaultGenerateDHTML = isGenerateDHTML();
        Settings.defaultHtmlMidresWidth = getMidresWidth();
        Settings.defaultHtmlMidresHeight = getMidresHeight();
        Settings.defaultHtmlMidresQuality = getMidresJpgQuality();
        Settings.defaultGenerateZipfile = isGenerateZipfile();
        Settings.defaultLinkToHighres = isLinkToHighres();
        Settings.defaultExportHighres = isExportHighres();
        Settings.defaultHtmlPictureNaming = getPictureNaming();
        Settings.htmlBackgroundColor = getBackgroundColor();
        Settings.htmlFontColor = getFontColor();
        Settings.writeRobotsTxt = isWriteRobotsTxt();
        Settings.unsavedSettingChanges = true;
    }
}
