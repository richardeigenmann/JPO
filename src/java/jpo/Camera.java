package jpo;

import java.io.*;
import java.util.*;


/*
Camera.java:  information about the digital camera as seen by the filesystem
 
Copyright (C) 2002-2007  Richard Eigenmann.
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
 * A class which holds information about the digital camera as seen by the file system and can tell
 * if there are new pictures on the camera.
 *
 */

public class Camera implements Serializable {
    /**
     *  The description of the Camera
     **/
    private String description = Settings.jpoResources.getString("newCamera");
    public void setDescription( String newDescription) {
        description = newDescription;
    }
    public String getDescription() {
        return description;
    }
    
    
    
    /**
     *  The mount point of the camera in the computer's file system.
     *  Could by E:\ in Windows /media/NIKON_D100 on Linux
     */
    private String cameraMountPoint = System.getProperty("java.io.tmpdir");
    
    /**
     *  This method returns the mount point of the camera in the computer's file system.
     */
    public String getCameraMountPoint() {
        return cameraMountPoint;
    }
    
    /**
     *  This method sets the mount point of the camera in the computer's file system
     */
    public void setCameraMountPoint( String newDir ) {
        cameraMountPoint = newDir;
    }
    
  
    
    
    /**
     *  Indicator that tells the program to find new pictures based on filename if true.
     */
    private boolean useFilename = true;
    public boolean getUseFilename() {
        return useFilename;
    }

    public void setUseFilename(boolean useFilename) {
        this.useFilename = useFilename;
    }
    
    
    
    /**
     *  This HashMap records the old images held on the camera so that we can determine
     *  which pictures are new.
     */
    private HashMap oldImage = new HashMap();
    public HashMap getOldImage() {
        return oldImage;
    }

    public void setOldImage(HashMap oldImage) {
        this.oldImage = oldImage;
    }
    
    /**
     *  This HashMap is used temporarily when getting new pictures. It should be empty unless
     *  pictures are being loaded.
     */
    private HashMap newImage = new HashMap();
    
    
    
    /**
     *   toString method that returns the description of the camera
     **/
    public String toString() {
        return description;
    }
    
    
    
    /**
     *   stores the checksum and file in the provided HashMap
     */
    public static void storePicture( HashMap hm, File f, long checksum ) {
        hm.put( f, new Long( checksum ) );
    }
    
    /**
     *   stores the checksum and file in the newImage HashMap
     */
    public void storePictureNewImage( File f, long checksum ) {
        storePicture( newImage, f, checksum);
    }
    
    
    /**
     *  returns whether the provided checksum or file is registered in the old camera image.
     */
    public boolean inOldImage( File f, long checksum ) {
        return inOldImage( f ) || inOldImage( checksum );
    }
    
    /**
     *  returns whether the provided checksum registered in the old camera image.
     *  it determines whether to check by checksum or file based on the useChecksum and useFilename
     *  flags.
     */
    public boolean inOldImage( long checksum) {
        //Tools.log("Camera.inOldImage: Checking Checksum: " + Long.toString(checksum) );
        return getOldImage().containsValue( new Long( checksum ) );
    }
    
    /**
     *  returns whether the provided file is registered in the old camera image.
     *  it determines whether to check by checksum or file based on the useChecksum and useFilename
     *  flags.
     */
    public boolean inOldImage( File f ) {
        //Tools.log("Camera.inOldImage: Checking File: " + f.toString() );
        return getOldImage().containsKey( f );
    }
    
    
    /**
     *  copies the entry specified by the file in the oldImage HashMap to the newImage HashMap.
     */
    public void copyToNewImage( File f ) {
        Long checksumLong = (Long) getOldImage().get( f );
        if ( checksumLong != null )
            storePictureNewImage( f, checksumLong.longValue() );
    }
    
    
    
    /**
     *   deletes all entries in the new Image.
     */
    public void zapNewImage() {
        newImage.clear();
    }
    
    /**
     *   deletes all entries in the new Image.
     */
    public void zapOldImage() {
        getOldImage().clear();
    }
    
    /**
     *   build a list of old image from the files on the camera-directory. This method
     *   creates a ProgressGui.
     */
    public void buildOldImage() {
        File rootDir = new File( this.cameraMountPoint );
        if ( ! rootDir.isDirectory() ) {
            Tools.log("Camera.buildOldImage was attempted on a non directory: " + this.cameraMountPoint );
            return;
        }
        int countFiles = Tools.countfiles( rootDir.listFiles() );
        if ( countFiles < 1 ) {
            Tools.log("Camera.buildOldImage was attempted for no files" );
            return;
        }
        ProgressGui progGui = new ProgressGui( countFiles,
                Settings.jpoResources.getString( "countingChecksum"),
                Settings.jpoResources.getString( "countingChecksumComplete" ) );
        
        buildOldImage( progGui, progGui.getInterruptor() );
        
        progGui.switchToDoneMode();
    }
    
    /**
     *   build a list of old image from the files on the camera-directory. This method
     *   notifies a ProgressListener if one is defined.
     */
    public void buildOldImage( ProgressListener progressListener, InterruptSemaphore interrupter  ) {
        File rootDir = new File( this.cameraMountPoint );
        if ( ! rootDir.isDirectory() ) {
            Tools.log("Camera.buildOldImage was attempted on a non directory: " + this.cameraMountPoint );
            return;
        }
        zapOldImage();
        buildOldImage( rootDir.listFiles(), progressListener, interrupter );
    }
    
    
    /**
     *   this method recursively goes through the directories to identify the checksums
     *   of the pictures in the camera directory.
     *   @param files  The files to add to the old image
     *   @param progressListener an object that would like to get prgoressIncrements
     *   @param interrupter a object that signals to abort the thread.
     */
    protected void buildOldImage( File[] files, ProgressListener progressListener, InterruptSemaphore interrupter ) {
        for ( int i = 0; (i < files.length) && ( ! interrupter.getShouldInterrupt() ); i++ ) {
            File f = files[i];
            if ( ! f.isDirectory() ) {
                long checksum = Tools.calculateChecksum( f );
                storePicture( getOldImage(), f, checksum );
                if ( progressListener != null ) { 
                    progressListener.progressIncrement();
                };
            } else {
                if ( Tools.hasPictures( f ) ) {
                    buildOldImage( f.listFiles(), progressListener, interrupter );
                }
            }
        }
    }

    
    
    /**
     *  This method returns a collection of new pictures found on the camera not previously found there
     */
    public Collection<File> getNewPictures() {
        HashSet newPics = new HashSet();
        if ( getCameraMountPoint() == null ) return newPics;
        File rootDir = new File( getCameraMountPoint() );
        if ( rootDir == null ) return newPics;
        Tools.log( getClass().toString() + ".getNewPictures invoked on directory: " + rootDir.toString() );
        return getNewPicturesLoop( rootDir.listFiles(), newPics );
    }
    
    
    /**
     *  This method returns a collection of new pictures found on the camera not previously found there
     */
    protected Collection<File> getNewPicturesLoop( File [] files, Collection<File> newFiles ) {
        Tools.log( getClass().toString() + ".getNewPicturesLoop invoked on files: " + files.toString() );
        for ( File f : files ) {
            Tools.log("Checking file: " + f.toString() );
            if ( ! f.isDirectory() ) {
                if ( Tools.jvmHasReader( f ) ) {
                    if ( ! inOldImage( f ) ) {
                        newFiles.add( f );
                    }
                }
            } else {
                getNewPicturesLoop( f.listFiles(), newFiles );
            }
        }
        return newFiles;
    }
    
    
    /**
     *  copies the entries in the newImage to the oldImage and zaps the new image.
     */
    public void storeNewImage() {
        getOldImage().putAll( newImage );
        zapNewImage();
    }
    
    /**
     *  counts the number of pictures for which a checksum is held in the HashMap
     */
    public String getOldIndexCountAsString() {
        return Integer.toString( getOldImage().size() );
    }
    
    
    /**
     *  This method tries to find out if the camera is connected to the computer. It does this
     *  by checking whether the directory of the camera is empty.
     */
    public boolean isCameraConnected() {
        File rootDir = new File( getCameraMountPoint() );
        if ( rootDir == null ) return false; // if it's not defined then it's not connected
        
        File [] files = rootDir.listFiles();
        if ( files == null ) return false; // if the File is not valid it's not connected
        
        return (files.length > 0 );
    }

    /**
     *  Flag to tell the CameraWatchDaemon whether to monitor the camera or not.
     */
    private boolean monitorForNewPictures = false;
    /**
     *  returns whether to monitor for new Pictures
     */
    public boolean getMonitorForNewPictures() {
        return monitorForNewPictures;
    }
    /**
     *  sets whether to monitor for new pictures
     */
    public void setMonitorForNewPictures(boolean monitorForNewPictures) {
        this.monitorForNewPictures = monitorForNewPictures;
    }
    
    /**
     *  This variable tracks the last connection status
     */
    private boolean lastConnectionStatus = false;
    
    /**
     *  Sets the last connection status.
     *  @param  newStatus  Send true to indicate that last time we checked the camera was
     *                      connected, send false to indicate that the last time we checked it was disconnected
     *
     */
    public void setLastConnectionStatus( boolean newStatus ) {
        lastConnectionStatus = newStatus;
    }
    
    /**
     *  Returns the last connection status.
     *  @return returns true if the last time we checked the camera was connected,
     *  returns false if it was disconnected.
     */
    public boolean getLastConnectionStatus() {
        return lastConnectionStatus;
    }


}
