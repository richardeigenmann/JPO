package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.Settings;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2023 Richard Eigenmann.
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

public class FileSaveHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(FileSaveHandler.class.getName());


    /**
     * Calls the {@link org.jpo.datamodel.PictureCollection#fileSave} method that
     * saves the current collection under its present name and if it was never
     * saved before brings up a popup window.
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final FileSaveRequest request) {
        if (request.pictureCollection().getXmlFile() == null) {
            final var fileSaveAsRequest = new FileSaveAsRequest(request.pictureCollection(), request.onSuccessNextRequest());
            JpoEventBus.getInstance().post(fileSaveAsRequest);
        } else {
            LOGGER.log(Level.INFO, "Saving under the name: {0}", Settings.getPictureCollection().getXmlFile());
            request.pictureCollection().fileSave();
            JpoEventBus.getInstance().post(new AfterFileSaveRequest(request.pictureCollection()));
            if (request.onSuccessNextRequest() != null) {
                JpoEventBus.getInstance().post(request.onSuccessNextRequest());
            }
        }
    }

}
