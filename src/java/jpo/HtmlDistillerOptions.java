package jpo;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

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
    private File htmlDirectory;

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

    public File getHtmlDirectory() {
        return htmlDirectory;
    }

    public void setHtmlDirectory( File htmlDirectory ) {
        this.htmlDirectory = htmlDirectory;
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
        if ( lowresJpgQuality >100 ) lowresJpgQuality = 100;
        else if ( lowresJpgQuality < 0 ) lowresJpgQuality = 0;
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
     * Convenience method that allows the quality to be specified between 0 and 100 
     * as an integer. If the value is out of bounds it is raised to 0 or lowered
     * to 100.
     * @param lowresJpgQuality
     */
    public void setMidresJpgQualityPercent( float midresJpgQuality ) {
        if ( midresJpgQuality >100 ) midresJpgQuality = 100;
        else if ( midresJpgQuality < 0 ) midresJpgQuality = 0;
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
        if ( ( pictureNaming < PICTURE_NAMING_BY_HASH_CODE ) || ( pictureNaming > PICTURE_NAMING_BY_ORIGINAL_NAME ) )
                pictureNaming = PICTURE_NAMING_BY_HASH_CODE;
        this.pictureNaming = pictureNaming;
    }
    
    public static final int PICTURE_NAMING_BY_HASH_CODE = 1;
    public static final int PICTURE_NAMING_BY_SEQUENTIAL_NUMBER = PICTURE_NAMING_BY_HASH_CODE + 1;
    public static final int PICTURE_NAMING_BY_ORIGINAL_NAME = PICTURE_NAMING_BY_SEQUENTIAL_NUMBER + 1;
    
    
}
