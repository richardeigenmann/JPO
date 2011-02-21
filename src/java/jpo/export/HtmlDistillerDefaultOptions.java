package jpo.export;

import jpo.dataModel.Settings;
import java.io.File;

/*
HtmlDistillerDefaultOptions.java:  Extends an HtmlDistillerOptions class and 
pre-populates the options with default values.

Copyright (C) 2008-2011  Richard Eigenmann, Zürich
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
 * Extends an HtmlDistillerOptions class and pre-populates the options with 
 * default values.
 * 
 * @author Richard Eigenmann
 */
public class HtmlDistillerDefaultOptions extends HtmlDistillerOptions {

    /**
     * Constructor that creates a HtmlDistillerOptions object and sets default 
     * values.
     */
    public HtmlDistillerDefaultOptions() {
        super();
        setFolderIconRequired( false );
        setDownloadZipFileName( "download.zip" );
        setCellspacing( 10 );
        setTargetDirectory( new File( Settings.getMostRecentCopyLocation().toString() ) );
        setPicsPerRow( Settings.defaultHtmlPicsPerRow );
        setThumbnailWidth( Settings.defaultHtmlThumbnailWidth );
        setThumbnailHeight( Settings.defaultHtmlThumbnailHeight );
        setLowresJpgQuality( Settings.defaultHtmlLowresQuality );
        setMidresWidth( Settings.defaultHtmlMidresWidth );
        setMidresHeight( Settings.defaultHtmlMidresHeight );
        setMidresJpgQuality( Settings.defaultHtmlMidresQuality );
        setGenerateMidresHtml( Settings.defaultGenerateMidresHtml );
        setGenerateMap( Settings.defaultGenerateMap );
        setGenerateDHTML( Settings.defaultGenerateDHTML );
        setGenerateZipfile( Settings.defaultGenerateZipfile );
        setLinkToHighres( Settings.defaultLinkToHighres );
        setExportHighres( Settings.defaultExportHighres );
        setPictureNaming( Settings.defaultHtmlPictureNaming );

        setWriteRobotsTxt( Settings.writeRobotsTxt );
        setBackgroundColor( Settings.htmlBackgroundColor );
        setFontColor( Settings.htmlFontColor );
        setScalingSteps( 8 );

    }
}
