package org.jpo.eventbus;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/*
 Copyright (C) 2017 -2022  Richard Eigenmann.
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

/**
 * This request indicates that a file is to be loaded and shown
 * <p>
 * It will not check for unsaved updates. To check for those wrap this in a
 * UnsavedUpdatesDialogRequest:
 * <p>
 * {@code JpoEventBus.getInstance().post( new UnsavedUpdatesDialogRequest( new FileLoadRequest()) ); }
 *
 * @param fileToLoad the file to load
 * @author Richard Eigenmann
 */
public record FileLoadRequest(@NotNull File fileToLoad) {
    // TODO: Switch to using nio Files
    /**
     * Constructor validates that the file exists, is readable and is not a directory.
     *
     * @param fileToLoad the file to load
     */
    public FileLoadRequest {
        if (!fileToLoad.exists()) {
            throw new IllegalArgumentException(String.format("File \"%s\" must exist before we can load it!", fileToLoad));
        }
        if (!fileToLoad.canRead()) {
            throw new IllegalArgumentException(String.format("File \"%s\" must be readable for FileLoadRequest!", fileToLoad));
        }
        if (fileToLoad.isDirectory()) {
            throw new IllegalArgumentException(String.format("\"%s\" is a directory. FileLoadRequest can only handle actual files.", fileToLoad));
        }
    }
}
