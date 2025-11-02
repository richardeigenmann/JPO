package org.jpo.datamodel;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.jpo.gui.InterruptSemaphore;
import org.jpo.gui.JpoResources;
import org.jpo.gui.ProgressListener;

import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2002-2025 Richard Eigenmann.
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
 * A class which holds information about the digital camera as seen by the file
 * system and can tell if there are new pictures on the camera.
 *
 */
public class Camera implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( Camera.class.getName() );

    /**
     * The description of the Camera
     */
    private String description = JpoResources.getResource("newCamera");

    /**
     * Sets a new description for the camera
     *
     * @param newDescription The description of the camera
     */
    public void setDescription( String newDescription ) {
        description = newDescription;
    }

    /**
     * returns the description of the camera
     *
     * @return the description of the camera
     */
    public String getDescription() {
        return description;
    }

    /**
     * The mount point of the camera in the computer's file system. Could by E:\
     * in Windows /media/NIKON_D100 on Linux
     */
    private String cameraMountPoint = System.getProperty( "java.io.tmpdir" );

    /**
     * This method returns the mount point of the camera in the computer's file
     * system.
     *
     * @return the mount point of the camera
     */
    public String getCameraMountPoint() {
        return cameraMountPoint;
    }

    /**
     * This method sets the mount point of the camera in the computer's file
     * system
     *
     * @param newDir The new mount point
     *
     */
    public void setCameraMountPoint( String newDir ) {
        cameraMountPoint = newDir;
    }

    /**
     * Indicator that tells the program to find new pictures based on filename
     * if true.
     */
    private boolean useFilename = true;

    /**
     * Returns whether the user wants to use just filenames to detect new images
     *
     * @return true if only filenames are to be used
     */
    public boolean getUseFilename() {
        return useFilename;
    }

    /**
     * Remembers the user wants to use filenames to identify new pictures
     *
     * @param useFilename true if we should use filenames
     */
    public void setUseFilename( boolean useFilename ) {
        this.useFilename = useFilename;
    }

    /**
     * This HashMap records the old images held on the camera so that we can
     * determine which pictures are new.
     */
    private Map<File, String> oldImage = new HashMap<>();

    /**
     * The old images held on the camera
     *
     * @return the old images held on the camera
     */
    public Map<File, String> getOldImage() {
        return oldImage;
    }

    /**
     * Remembers the old images on the camera
     *
     * @param oldImage the old images on the camera
     */
    public void setOldImage(Map<File, String> oldImage) {
        this.oldImage = oldImage;
    }

    /**
     * This HashMap is used temporarily when getting new pictures. It should be
     * empty unless pictures are being loaded.
     */
    private final HashMap<File, String> newImage = new HashMap<>();

    /**
     * toString method that returns the description of the camera
     *
     * @return the description of the camera
     */
    @Override
    public String toString() {
        return description;
    }

    /**
     * stores the checksum and file in the provided HashMap
     *
     * @param hm     The HashMap in which to store the picture
     * @param f      the file to store
     * @param sha256 the file's checksum
     */
    private static void storePicture(Map<File, String> hm, File f, String sha256) {
        hm.put(f, sha256);
    }

    /**
     * stores the checksum and file in the newImage HashMap
     *
     * @param f      The file
     * @param sha256 The checksum
     */
    public void storePictureNewImage(final File f, final String sha256) {
        storePicture(newImage, f, sha256);
    }

    /**
     * returns whether the provided checksum registered in the old camera image.
     * it determines whether to check by checksum or file based on the
     * useChecksum and useFilename flags.
     *
     * @param sha256 The checksum
     * @return true if the image was known before based on the checksum
     */
    public boolean inOldImage(final String sha256) {
        return getOldImage().containsValue(sha256);
    }

    /**
     * returns whether the provided file is registered in the old camera image.
     * it determines whether to check by checksum or file based on the
     * useChecksum and useFilename flags.
     *
     * @param f the file
     * @return true if file is found in old camera
     */
    public boolean inOldImage(final File f) {
        return getOldImage().containsKey(f);
    }

    /**
     * copies the entry specified by the file in the oldImage HashMap to the
     * newImage HashMap.
     *
     * @param f the file
     */
    public void copyToNewImage(final File f) {
        HashCode hash;
        try {
            hash = Files.asByteSource(f).hash(Hashing.sha256());
        } catch (IOException _) {
            hash = null;
        }
        if (hash != null) {
            storePictureNewImage(f, hash.toString().toUpperCase());
        }
    }

    /**
     * deletes all entries in the new Image.
     */
    public void zapNewImage() {
        newImage.clear();
    }

    /**
     * deletes all entries in the new Image.
     */
    public void zapOldImage() {
        getOldImage().clear();
    }

    /**
     * Returns the root directory of the camera or null if this is not a good
     * directory
     *
     * @return the root directory or null if the directory is not good
     */
    private File getRootDir() {
        File rootDir = new File( this.cameraMountPoint );
        if ( !rootDir.isDirectory() ) {
            LOGGER.log(Level.INFO, "{0} is not a directory", this.cameraMountPoint );
            return null;
        }
        return rootDir;
    }

    /**
     * Returns the number of files the camera directory tree holds. This
     * includes directories and non picture files.
     *
     * @return the number of files in the camera directory tree
     */
    public int countFiles() {
        return Tools.countFiles(Objects.requireNonNull(getRootDir()).listFiles());
    }

    /**
     * build a list of old image from the files on the camera-directory. This
     * method creates a ProgressGui.
     */
    public void buildOldImage(final ProgressListener progressListener) {
        int count = countFiles();
        progressListener.setMaximum(count);
        if (count < 1) {
            LOGGER.info("No files. Not building old image on camera as the camera is probably disconnected.");
            return;
        }

        buildOldImage(progressListener, progressListener.getInterruptSemaphore());

        progressListener.switchToDoneMode();
    }

    /**
     * build a list of old image from the files on the camera-directory. This
     * method notifies a ProgressListener if one is defined.
     *
     * @param progressListener The ProgressListener
     * @param interrupter The semaphore to interrupt the job
     */
    public void buildOldImage( ProgressListener progressListener, InterruptSemaphore interrupter ) {
        File rootDir = new File( this.cameraMountPoint );
        if ( !rootDir.isDirectory() ) {
            LOGGER.log(Level.INFO, "{0} is not a directory: ", this.cameraMountPoint );
            return;
        }
        zapOldImage();
        buildOldImage(
                Objects.requireNonNull(rootDir.listFiles()), progressListener, interrupter );
    }

    /**
     * this method recursively goes through the directories to identify the
     * checksums of the pictures in the camera directory.
     *
     * @param files The files to add to the old image
     * @param progressListener an object that would like to get
     * progressIncrements
     * @param interrupter an object that signals to abort the thread.
     */
    private void buildOldImage(File[] files, ProgressListener progressListener, InterruptSemaphore interrupter) {
        for ( File f : files ) {
            if ( interrupter.getShouldInterrupt() ) {
                break;
            }
            if ( !f.isDirectory()) {
                HashCode hash = null;
                try {
                    hash = Files.asByteSource(f).hash(Hashing.sha256());
                } catch (IOException _) {
                    //
                }
                storePicture(
                        getOldImage(), f, Objects.requireNonNull(hash).toString().toUpperCase());
                if (progressListener != null) {
                    progressListener.progressIncrement();
                }
            } else {
                if ( Tools.hasPictures( f ) ) {
                    buildOldImage(Objects.requireNonNull(f.listFiles()), progressListener, interrupter );
                }

            }
        }
    }

    /**
     * This method returns a collection of new pictures found on the camera not
     * previously found there
     *
     * @return a collection of new picture files
     */
    public Collection<File> getNewPictures() {
        HashSet<File> newPics = new HashSet<>();
        if ( getCameraMountPoint() == null ) {
            return newPics;
        }

        File rootDir = new File( getCameraMountPoint() );
        return getNewPicturesLoop(Objects.requireNonNull(rootDir.listFiles()), newPics );
    }

    /**
     * This method returns a collection of new pictures found on the camera not
     * previously found there
     *
     * @param files The files to test
     * @param newFiles The collection to which to add them
     * @return a collection of new picture files
     */
    private Collection<File> getNewPicturesLoop(File[] files, Collection<File> newFiles) {
        for ( File f : files ) {
            if ( !f.isDirectory() ) {
                if (JpoImageIO.jvmHasReader(f) && !inOldImage(f)) {
                    newFiles.add(f);
                }
            } else {
                getNewPicturesLoop(Objects.requireNonNull(f.listFiles()), newFiles );
            }

        }
        return newFiles;
    }

    /**
     * copies the entries in the newImage to the oldImage and zaps the new
     * image.
     */
    public void storeNewImage() {
        getOldImage().putAll( newImage );
        zapNewImage();

    }

    /**
     * counts the number of pictures for which a checksum is held in the HashMap
     *
     * @return the number of pictures previously known as a string
     */
    public String getOldIndexCountAsString() {
        return Integer.toString( getOldImage().size() );
    }

    /**
     * This method tries to find out if the camera is connected to the computer.
     * It does this by checking whether the directory of the camera is empty.
     *
     * @return true if the camera is connected
     */
    public boolean isCameraConnected() {
        File rootDir = new File( getCameraMountPoint() );

        File[] files = rootDir.listFiles();
        if ( files == null ) {
            return false; // if the File is not valid it's not connected
        }

        return ( files.length > 0 );
    }

    /**
     * Flag to tell the CameraWatchDaemon whether to monitor the camera or not.
     */
    private boolean monitorForNewPictures;  // default is false

    /**
     * returns whether to monitor for new pictures
     *
     * @return whether to monitor for new pictures
     */
    public boolean getMonitorForNewPictures() {
        return monitorForNewPictures;
    }

    /**
     * sets whether to monitor for new pictures
     *
     * @param monitorForNewPictures whether to monitor for new files
     */
    public void setMonitorForNewPictures( boolean monitorForNewPictures ) {
        this.monitorForNewPictures = monitorForNewPictures;
    }

    /**
     * This variable tracks the last connection status
     */
    private boolean lastConnectionStatus;  // default is false

    /**
     * Sets the last connection status.
     *
     * @param newStatus Send true to indicate that last time we checked the
     * camera was connected, send false to indicate that the last time we
     * checked it was disconnected
     *
     */
    public void setLastConnectionStatus( boolean newStatus ) {
        lastConnectionStatus = newStatus;
    }

    /**
     * Returns the last connection status.
     *
     * @return returns true if the last time we checked the camera was
     * connected, returns false if it was disconnected.
     */
    public boolean getLastConnectionStatus() {
        return lastConnectionStatus;
    }
}
