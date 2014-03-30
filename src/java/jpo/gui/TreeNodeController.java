package jpo.gui;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import jpo.dataModel.Settings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Enumeration;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import jpo.dataModel.PictureInfo;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

/*
 TreeNodeController.java: This class should handle all the interactive GUI stuff for a Tree Node

 Copyright (C) 2007 - 2013  Richard Eigenmann.
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
 * This class should handle all the interactive GUI stuff for a Tree Node.
 * Formerly the SortableDefaultMutableTreeNode was doing data stuff and GUI
 * stuff. It should really only do data stuff.
 *
 * @author Richard Eigenmann
 */
public class TreeNodeController {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( TreeNodeController.class.getName() );

    /**
     * This method brings up a Filechooser and then loads the images off the
     * specified flat file.
     *
     * @param targetNode The node at which to add the flat file.
     */
    public static void addFlatFile( SortableDefaultMutableTreeNode targetNode ) {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode( javax.swing.JFileChooser.FILES_ONLY );
        jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "fileOpenButtonText" ) );
        jFileChooser.setDialogTitle( Settings.jpoResources.getString( "addFlatFileTitle" ) );
        jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );

        int returnVal = jFileChooser.showOpenDialog( Settings.anchorFrame );
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File chosenFile = jFileChooser.getSelectedFile();

            try {
                targetNode.addFlatFile( chosenFile );
            } catch ( IOException e ) {
                LOGGER.log( Level.INFO, "IOException {0}", e.getMessage() );
                JOptionPane.showMessageDialog( Settings.anchorFrame,
                        e.getMessage(),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
            }

        }
    }

    /**
     * Brings up a JFileChooser to select the target location and then copies
     * the images to the target location
     *
     * @param nodes The Vector of nodes to be copied to a new location
     */
    public static void copyToNewLocation( SortableDefaultMutableTreeNode[] nodes ) {
        JFileChooser jFileChooser = new JFileChooser();

        jFileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "CopyImageDialogButton" ) );
        jFileChooser.setDialogTitle( Settings.jpoResources.getString( "CopyImageDialogTitle" ) );
        jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );

        int returnVal = jFileChooser.showSaveDialog( Settings.anchorFrame );
        if ( returnVal != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        copyToLocation( nodes, jFileChooser.getSelectedFile() );
    }

    /**
     * Copies the pictures of the supplied nodes to the target directory
     *
     * @param nodes The array of nodes to be copied to a new location
     * @param targetLocation the target directory
     */
    public static void copyToLocation( SortableDefaultMutableTreeNode[] nodes, File targetLocation ) {
        int picsCopied = 0;
        for ( SortableDefaultMutableTreeNode node : nodes ) {
            if ( node.getUserObject() instanceof PictureInfo ) {
                node.validateAndCopyPicture( targetLocation );
                picsCopied++;

            } else {
                LOGGER.info( String.format( "Skipping non PictureInfo node %s", node.toString() ) );
            }
        }
        JOptionPane.showMessageDialog( Settings.anchorFrame,
                String.format( Settings.jpoResources.getString( "copyToNewLocationSuccess" ), picsCopied, nodes.length ),
                Settings.jpoResources.getString( "genericInfo" ),
                JOptionPane.INFORMATION_MESSAGE );

    }

    /**
     * Brings up a JFileChooser to select the target zip file and then copies
     * the images there
     *
     * @param nodes The Vector of nodes to be copied to a new location
     */
    public static void copyToNewZipfile( SortableDefaultMutableTreeNode[] nodes ) {
        final JFileChooser jFileChooser = new JFileChooser();

        jFileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        // TODO: internationalise this Settings.jpoResources.getString( "CopyImageDialogButton" )
        jFileChooser.setApproveButtonText( "Select" );
        jFileChooser.setDialogTitle( "Pick the zipfile to which the pictures should be added" );
        jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );

        int returnVal = jFileChooser.showDialog( Settings.anchorFrame, "Select" );
        if ( returnVal != JFileChooser.APPROVE_OPTION ) {
            return;
        }
        //LOGGER.info( String.format( "Name chosen: %s", jFileChooser.getSelectedFile() ) );
        final File chosenFile = jFileChooser.getSelectedFile();
        Settings.memorizeZipFile( chosenFile.getPath() );
        copyToZipfile( nodes, chosenFile );
    }
    // 4MB buffer
    private static final byte[] BUFFER = new byte[4096 * 1024];

    public static void streamcopy( InputStream input, OutputStream output ) throws IOException {
        int bytesRead;
        while ( ( bytesRead = input.read( BUFFER ) ) != -1 ) {
            output.write( BUFFER, 0, bytesRead );
        }
    }

    /**
     * Copies the pictures of the supplied nodes to the target zipfile, creating
     * it if need be. This method does append to the zipfile by riting to a
     * temporary file and then copying the old zip file over to this one as the
     * API doesn't support directly appending to a zip file.
     *
     * @param nodes The array of nodes to be copied to a new location
     * @param zipfile the target zip file
     */
    public static void copyToZipfile( SortableDefaultMutableTreeNode[] nodes, File zipfile ) {
        File tempfile = new File( zipfile.getAbsolutePath() + ".jpo.temp" );
        int picsCopied = 0;
        PictureInfo pictureInfo;
        File sourceFile;
        byte[] buf = new byte[1024];
        try {
            ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream( tempfile );
            zipArchiveOutputStream.setLevel( 9 );
            for ( SortableDefaultMutableTreeNode node : nodes ) {
                if ( node.getUserObject() instanceof PictureInfo ) {
                    pictureInfo = (PictureInfo) node.getUserObject();
                    sourceFile = pictureInfo.getHighresFile();
                    LOGGER.info( String.format( "Processing file %s", sourceFile.toString() ) );

                    ZipArchiveEntry entry = new ZipArchiveEntry( sourceFile, sourceFile.getName() );
                    zipArchiveOutputStream.putArchiveEntry( entry );

                    FileInputStream fis = new FileInputStream( sourceFile );
                    streamcopy( fis, zipArchiveOutputStream );
                    fis.close();
                    zipArchiveOutputStream.closeArchiveEntry();

                    picsCopied++;

                } else {
                    LOGGER.info( String.format( "Skipping non PictureInfo node %s", node.toString() ) );
                }
            }

            if ( zipfile.exists() ) {
                // copy the old entries over
                org.apache.commons.compress.archivers.zip.ZipFile oldzip = new org.apache.commons.compress.archivers.zip.ZipFile( zipfile );
                Enumeration entries = oldzip.getEntries();
                while ( entries.hasMoreElements() ) {
                    ZipArchiveEntry e = (ZipArchiveEntry) entries.nextElement();
                    LOGGER.info( String.format( "streamcopy: %s", e.getName() ) );
                    zipArchiveOutputStream.putArchiveEntry( e );
                    if ( !e.isDirectory() ) {
                        streamcopy( oldzip.getInputStream( e ), zipArchiveOutputStream );
                    }
                    zipArchiveOutputStream.closeArchiveEntry();
                }
            }
            zipArchiveOutputStream.finish();
            zipArchiveOutputStream.close();
        } catch ( FileNotFoundException ex ) {
            Logger.getLogger( TreeNodeController.class.getName() ).log( Level.SEVERE, null, ex );
            tempfile.delete();
        } catch ( IOException ex ) {
            Logger.getLogger( TreeNodeController.class.getName() ).log( Level.SEVERE, null, ex );
            tempfile.delete();
        }

        if ( zipfile.exists() ) {
            LOGGER.info( String.format( "Deleting old file %s", zipfile.getAbsolutePath() ) );
            zipfile.delete();
        }
        LOGGER.info( String.format( "Renaming temp file %s to %s", tempfile.getAbsolutePath(), zipfile.getAbsolutePath() ) );
        tempfile.renameTo( zipfile );

        JOptionPane.showMessageDialog( Settings.anchorFrame,
                String.format( "Copied %d files of %d to zipfile %s", picsCopied, nodes.length, zipfile.toString() ),
                Settings.jpoResources.getString( "genericInfo" ),
                JOptionPane.INFORMATION_MESSAGE );

    }

    /**
     * Copies the pictures of the supplied nodes to the target zipfile, creating
     * it if need be.
     *
     * @param nodes The array of nodes to be copied to a new location
     * @param zipfile the target zip file
     */
    public static void copyToZipfileBAD( SortableDefaultMutableTreeNode[] nodes, File zipfile ) {
        ZipOutputStream zipOutputStream = null;
        int picsCopied = 0;
        byte[] buf = new byte[1024];
        PictureInfo pictureInfo;
        File sourceFile;
        try {
            zipOutputStream = new ZipOutputStream(
                    (OutputStream) new FileOutputStream( zipfile ) );
            zipOutputStream.setLevel( 9 );
            for ( SortableDefaultMutableTreeNode node : nodes ) {
                if ( node.getUserObject() instanceof PictureInfo ) {
                    pictureInfo = (PictureInfo) node.getUserObject();
                    sourceFile = pictureInfo.getHighresFile();
                    LOGGER.info( String.format( "Processing file %s", sourceFile.toString() ) );

                    ZipEntry entry = new ZipEntry( sourceFile.getName() );
                    zipOutputStream.putNextEntry( entry );

                    FileInputStream fis = new FileInputStream( sourceFile );
                    streamcopy( fis, zipOutputStream );
                    fis.close();
                    zipOutputStream.closeEntry();

                    picsCopied++;

                } else {
                    LOGGER.info( String.format( "Skipping non PictureInfo node %s", node.toString() ) );
                }
            }
            zipOutputStream.finish();
            zipOutputStream.close();

        } catch ( FileNotFoundException ex ) {
            Logger.getLogger( TreeNodeController.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( IOException ex ) {
            Logger.getLogger( TreeNodeController.class.getName() ).log( Level.SEVERE, null, ex );
        } finally {
            try {
                zipOutputStream.close();
            } catch ( IOException ex ) {
                Logger.getLogger( TreeNodeController.class.getName() ).log( Level.SEVERE, null, ex );
            }
        }

        JOptionPane.showMessageDialog( Settings.anchorFrame,
                String.format( "Copied %d files of %d to zipfile %s", picsCopied, nodes.length, zipfile.toString() ),
                Settings.jpoResources.getString( "genericInfo" ),
                JOptionPane.INFORMATION_MESSAGE );

    }

  

  
}
