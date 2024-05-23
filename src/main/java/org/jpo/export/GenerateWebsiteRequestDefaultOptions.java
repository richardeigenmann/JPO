package org.jpo.export;

import org.jpo.datamodel.Settings;
import org.jpo.eventbus.GenerateWebsiteRequest;

import java.io.File;

/*
 Copyright (C) 2008-2024  Richard Eigenmann, Zürich
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
        setFolderIconRequired(false);
        setDownloadZipFileName("download.zip");
        setCellspacing(10);
        setTargetDirectory(new File(Settings.getMostRecentCopyLocation().toString()));
        setPicsPerRow(Settings.getDefaultHtmlPicsPerRow());
        setThumbnailWidth(Settings.getDefaultHtmlThumbnailWidth());
        setThumbnailHeight(Settings.getDefaultHtmlThumbnailHeight());
        setLowresJpgQuality(Settings.getDefaultHtmlLowresQuality());
        setMidresWidth(Settings.getDefaultHtmlMidresWidth());
        setMidresHeight(Settings.getDefaultHtmlMidresHeight());
        setMidresJpgQuality(Settings.getDefaultHtmlMidresQuality());
        setGenerateMidresHtml(Settings.isDefaultGenerateMidresHtml());
        setGenerateMap(Settings.isDefaultGenerateMap());
        setGoogleMapsApiKey(Settings.getDefaultGoogleMapsApiKey());
        setGenerateMouseover(Settings.isDefaultGenerateDHTML());
        setGenerateZipfile(Settings.isDefaultGenerateZipfile());
        setLinkToHighres(Settings.isDefaultLinkToHighres());
        setExportHighres(Settings.isDefaultExportHighres());
        setRotateHighres(Settings.isDefaultRotateHighres());
        setPictureNaming(Settings.getDefaultHtmlPictureNaming());

        setOutputTarget(Settings.getDefaultHtmlOutputTarget());
        setFtpServer(Settings.getDefaultHtmlFtpServer());
        setFtpPort(Settings.getDefaultHtmlFtpPort());
        setFtpUser(Settings.getDefaultHtmlFtpUser());
        setFtpPassword(Settings.getDefaultHtmlFtpPassword());
        setFtpTargetDir(Settings.getDefaultHtmlFtpTargetDir());
        setSshServer(Settings.getDefaultHtmlSshServer());
        setSshPort(Settings.getDefaultHtmlSshPort());
        setSshUser(Settings.getDefaultHtmlSshUser());
        setSshAuthType(Settings.getDefaultHtmlSshAuthType());
        setSshPassword(Settings.getDefaultHtmlSshPassword());
        setSshTargetDir(Settings.getDefaultHtmlSshTargetDir());
        setSshKeyFile(Settings.getDefaultHtmlSshKeyFile());

        setWriteRobotsTxt(Settings.isWriteRobotsTxt());
        setBackgroundColor(Settings.getHtmlBackgroundColor());
        setFontColor(Settings.getHtmlFontColor());
        setScalingSteps(8);

    }
}
