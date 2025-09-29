package org.jpo.cache;

import java.io.ByteArrayInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.attribute.FileTime;
import java.time.Instant;


/*
 Copyright (C) 2014-2025 Richard Eigenmann.
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
 * Serializable array of bytes representing an Image
 *
 * @author Richard Eigenmann
 */
public class ImageBytes implements Serializable {

    @Serial
    private static final long serialVersionUID = 4;
    private Instant lastModification;
    private final byte[] bytes;

    /**
     * Can be queried to see if the image came from the cache or was loaded from disk
     *
     * @return true if retrieved from cache
     */
    public boolean isRetrievedFromCache() {
        return retrievedFromCache;
    }

    /**
     * Sets the retrievedFromCache flag.
     *
     * @param retrievedFromCache the new value for the flag
     */
    public void setRetrievedFromCache(boolean retrievedFromCache) {
        this.retrievedFromCache = retrievedFromCache;
    }

    private boolean retrievedFromCache = false;


    /**
     * Constructs a new ImageBytes object with the key and the bytes as read from disk
     * @param bytes the bytes
     */
    public ImageBytes(final byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Returns the image bytes from the stored bytes
     *
     * @return the bytes as a ByteArrayInputStream
     */
    public ByteArrayInputStream getByteArrayInputStream() {
        return new ByteArrayInputStream( bytes );
    }

    /**
     * Returns the image bytes from the stored bytes array
     *
     * @return the bytes[]
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Remembers the last modification time of the source file
     *
     * @param lastModification the last modification time
     */
    public void setLastModification(final FileTime lastModification) {
        this.lastModification = lastModification.toInstant();
    }

    /**
     * Returns the last modification time of the data that was stored in the byte array
     * @return the last modification time
     */
    public FileTime getLastModification() {
        return FileTime.from(lastModification);
    }



}
