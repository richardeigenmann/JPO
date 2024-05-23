package org.jpo.export;

import org.jpo.datamodel.SortableDefaultMutableTreeNode;

/*
 * Copyright (C) 2012-2024 RichardEigenmann.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA. The license is in
 * gpl.txt. See http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 *
 * @author Richard Eigenmann
 */
public class PicasaUploadRequest {
    /**
     * The Group Node that is to be uploaded
     */
    private SortableDefaultMutableTreeNode node;

    /**
     * @return the node
     */
    public SortableDefaultMutableTreeNode getNode () {
        return node;
    }

    /**
     * @param node the node to set
     */
    public void setNode ( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }
    /**
     * The Google username
     */
    private String username;

    /**
     * @return the username
     */
    public String getUsername () {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername ( String username ) {
        this.username = username;
    }
    /**
     * The Google password
     */
    private String password;

    /**
     * @return the password
     */
    public String getPassword () {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword ( String password ) {
        this.password = password;
    }
    
    /**
     * Static Picasa address to be prepended to the user
     */
    public static final String PICASA_URL = "https://picasaweb.google.com/data/feed/api/user/%s";

    /**
     * Returns the Picasa URL for communication with the Google server.
     * @return the URL
     */
    public String getFormattedPicasaUrl () {
        return String.format ( PICASA_URL, getUsername () );
    }

    /**
     * semaphore to indicate that the upload should stop
     */
    private boolean interrupt;  // default is false

    /**
     * @return the interrupt
     */
    public boolean isInterrupt () {
        return interrupt;
    }

    /**
     * @param interrupt the interrupt to set
     */
    public void setInterrupt ( boolean interrupt ) {
        this.interrupt = interrupt;
    }
    

    /**
     * The URL of the album
     */
    private String albumPostUrl;

    /**
     * @return the albumPostUrl
     */
    public String getAlbumPostUrl () {
        return albumPostUrl;
    }

    /**
     * @param albumPostUrl the albumPostUrl to set
     */
    public void setAlbumPostUrl ( String albumPostUrl ) {
        this.albumPostUrl = albumPostUrl;
    }
    
    
}
