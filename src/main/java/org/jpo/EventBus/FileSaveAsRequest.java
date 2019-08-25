package org.jpo.EventBus;

/*
 Copyright (C) 2017  Richard Eigenmann.
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
 * This request indicates that the user wants to save the collection under a new
 * name
 *
 * @author Richard Eigenmann
 */
public class FileSaveAsRequest implements Request {


    private Request onSucccessNextRequest;

    /**
     * Optional next request to call after successfully saving the file.
     *
     * @param onSuccessNextRequest the next request
     */
    public void setOnSuccessNextRequest( Request onSuccessNextRequest ) {
        this.onSucccessNextRequest = onSuccessNextRequest;
    }

    /**
     * Returns the next event to submit only if the file was successfully saved
     * @return The next event to execute on a successful save
     */
    public Request getOnSuccessNextRequest() {
        return onSucccessNextRequest;
    }

}
