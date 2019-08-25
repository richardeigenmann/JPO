package org.jpo.export;

import java.io.File;

import org.jpo.EventBus.GenerateWebsiteRequest;
import org.jpo.dataModel.Settings;

/*
 GenerateWebsiteRequestDefaultOptions.java:  Extends an GenerateWebsiteRequest class and
 pre-populates the options with default values.

 Copyright (C) 2008-2012  Richard Eigenmann, Zürich
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
 * Extends an GenerateWebsiteRequest class and pre-populates the options with
 * default values.
 *
 * @author Richard Eigenmann
 */
public class GenerateWebsiteRequestDefaultOptions extends GenerateWebsiteRequest {

    /**
     * Constructor that creates a GenerateWebsiteRequest object and sets default
     * values.
     */
    public GenerateWebsiteRequestDefaultOptions() {
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
        setGenerateMouseover( Settings.defaultGenerateDHTML );
        setGenerateZipfile( Settings.defaultGenerateZipfile );
        setLinkToHighres( Settings.defaultLinkToHighres );
        setExportHighres( Settings.defaultExportHighres );
        setRotateHighres( Settings.defaultRotateHighres );
        setPictureNaming( Settings.defaultHtmlPictureNaming );

        setOutputTarget( Settings.defaultHtmlOutputTarget );
        setFtpServer( Settings.defaultHtmlFtpServer );
        setFtpPort( Settings.defaultHtmlFtpPort );
        setFtpUser( Settings.defaultHtmlFtpUser );
        setFtpPassword( Settings.defaultHtmlFtpPassword );
        setFtpTargetDir( Settings.defaultHtmlFtpTargetDir );
        setSshServer( Settings.defaultHtmlSshServer );
        setSshPort( Settings.defaultHtmlSshPort );
        setSshUser( Settings.defaultHtmlSshUser );
        setSshAuthType( Settings.defaultHtmlSshAuthType );
        setSshPassword( Settings.defaultHtmlSshPassword );
        setSshTargetDir( Settings.defaultHtmlSshTargetDir );
        setSshKeyFile( Settings.defaultHtmlSshKeyFile );

        setWriteRobotsTxt( Settings.writeRobotsTxt );
        setBackgroundColor( Settings.htmlBackgroundColor );
        setFontColor( Settings.htmlFontColor );
        setScalingSteps( 8 );

    }
}
