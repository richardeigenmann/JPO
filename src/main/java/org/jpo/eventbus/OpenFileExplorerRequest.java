package org.jpo.eventbus;

import java.io.File;

/*
 Copyright (C) 2019  Richard Eigenmann.
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
 * The receiver of this request is supposed to open the operating system file explorer
 * 
 * @author Richard Eigenmann
 */
public class OpenFileExplorerRequest implements Request {

    private final File directory;

    /**
     * A request to show the supplied directory in a file explorer
     * @param directory The directory to open
     */
    public OpenFileExplorerRequest(File directory ) {
        this.directory = directory;
    }

    /**
     * Returns the directory for which the explorer is to be shown.
     * @return the File of the directory
     */
    public File getDirectory() {
        return directory;
    }

}
