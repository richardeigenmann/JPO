package org.jpo.EventBus;

import java.io.File;

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
 * This request indicates that a file is to be loaded and shown
 * <p>
 * It will not check for unsaved updates. To check for those wrap this in a
 * UnsavedUpdatesDialogRequest:
 * <p>
 * {@code JpoEventBus.getInstance().post( new UnsavedUpdatesDialogRequest( new FileLoadRequest()) ); }
 *
 * @author Richard Eigenmann
 */
public class FileLoadRequest implements Request {

    
    /**
     * The file to load
     */
    private final File fileToLoad;

    /**
     * A request to load a file
     * @param fileToLoad the file to load
     */
    public FileLoadRequest( File fileToLoad) {
        this.fileToLoad = fileToLoad;
    }

    /**
     * Returns the file to be loaded.
     *
     * @return The file to load
     */
    public File getFileToLoad() {
        return fileToLoad;
    }

}
