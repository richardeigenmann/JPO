package org.jpo.EventBus;

import org.jpo.dataModel.PictureInfo;

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
 * Requests a user function to be run
 *
 * @author Richard Eigenmann
 */
public class RunUserFunctionRequest implements Request {

    private final int userFunctionIndex;
    private final PictureInfo pictureInfo;

    /**
     * A request to run a user function
     *
     * @param userFunctionIndex The user function to run
     * @param pictureInfo the picture against which we want to run the user function
     */
    public RunUserFunctionRequest( int userFunctionIndex, PictureInfo pictureInfo ) {
        this.userFunctionIndex = userFunctionIndex;
        this.pictureInfo = pictureInfo;
    }

    /**
     * Returns the number of the user function to run
     *
     * @return the user function
     */
    public int getUserFunctionIndex() {
        return userFunctionIndex;
    }

    /**
     * Returns the node against which to run the user function
     *
     * @return the user function node
     */
    public PictureInfo getPictureInfo() {
        return pictureInfo;
    }

}
