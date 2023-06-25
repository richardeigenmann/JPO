package org.jpo.datamodel;

import org.jpo.datamodel.ScalablePicture.ScalablePictureStatus;
import org.jpo.datamodel.SourcePicture.SourcePictureStatus;


/*
 Copyright (C) 2002-2023 Richard Eigenmann.
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
 * This interface allows the image loaders to update the view about
 * changes in the image loading status.
 */
public interface ScalablePictureListener {

    /**
     * inform the listener that the status has changed
     *
     * @param statusCode    status code
     * @param statusMessage status message
     */
    void scalableStatusChange(final ScalablePictureStatus statusCode, final String statusMessage);

    /**
     * inform the listener of progress on the loading of the image
     *
     * @param statusCode status code
     * @param percentage the percentage done
     */
    void sourceLoadProgressNotification(final SourcePictureStatus statusCode, final int percentage);

}
