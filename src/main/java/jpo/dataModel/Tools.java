package jpo.dataModel;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import jpo.gui.XmlFilter;


/*
 Tools.java:  utilities for the JPO application
 *
 Copyright (C) 2002-2017  Richard Eigenmann.
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
 * separate class to hold a collection of static methods that are frequently
 * needed.
 *
 */
public class Tools {

    /**
     * Private constructor to hide implicit public one. Explanation: Utility
     * classes, which are collections of static members, are not meant to be
     * instantiated. Even abstract utility classes, which can be extended,
     * should not have public constructors. From Sonarcloud bug report
     *
     * Java adds an implicit public constructor to every class which does not
     * define at least one explicitly. Hence, at least one non-public
     * constructor should be defined.
     */
    private Tools() {
        throw new IllegalStateException( "Utility class" );
    }

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( Tools.class.getName() );


    /**
     * Translates characters which are problematic in a filename into
     * unproblematic characters
     *
     * @param string The filename to clean up
     * @return The cleaned up filename
     */
    public static String cleanupFilename( String string ) {
        String returnString = string;
        if ( returnString.contains( " " ) ) {
            returnString = returnString.replaceAll( " ", "_" );  // replace blank with underscore
        }
        if ( returnString.contains( "%20" ) ) {
            returnString = returnString.replaceAll( "%20", "_" );  // replace blank with underscore
        }
        if ( returnString.contains( "&" ) ) {
            returnString = returnString.replace( "&", "_and_" );  // replace ampersand with _and_
        }
        if ( returnString.contains( "|" ) ) {
            returnString = returnString.replace( "|", "l" );  // replace pipe with lowercase L
        }
        if ( returnString.contains( "<" ) ) {
            returnString = returnString.replace( "<", "_" );
        }
        if ( returnString.contains( ">" ) ) {
            returnString = returnString.replace( ">", "_" );
        }
        if ( returnString.contains( "@" ) ) {
            returnString = returnString.replace( "@", "_" );
        }
        if ( returnString.contains( ":" ) ) {
            returnString = returnString.replace( ":", "_" );
        }
        if ( returnString.contains( "$" ) ) {
            returnString = returnString.replace( "$", "_" );
        }
        if ( returnString.contains( "£" ) ) {
            returnString = returnString.replace( "£", "_" );
        }
        if ( returnString.contains( "^" ) ) {
            returnString = returnString.replace( "^", "_" );
        }
        if ( returnString.contains( "~" ) ) {
            returnString = returnString.replace( "~", "_" );
        }
        if ( returnString.contains( "\"" ) ) {
            returnString = returnString.replace( "\"", "_" );
        }
        if ( returnString.contains( "'" ) ) {
            returnString = returnString.replace( "'", "_" );
        }
        if ( returnString.contains( "`" ) ) {
            returnString = returnString.replace( "`", "_" );
        }
        if ( returnString.contains( "?" ) ) {
            returnString = returnString.replace( "?", "_" );
        }
        if ( returnString.contains( "[" ) ) {
            returnString = returnString.replace( "[", "_" );
        }
        if ( returnString.contains( "]" ) ) {
            returnString = returnString.replace( "]", "_" );
        }
        if ( returnString.contains( "{" ) ) {
            returnString = returnString.replace( "{", "_" );
        }
        if ( returnString.contains( "}" ) ) {
            returnString = returnString.replace( "}", "_" );
        }
        if ( returnString.contains( "(" ) ) {
            returnString = returnString.replace( "(", "_" );
        }
        if ( returnString.contains( ")" ) ) {
            returnString = returnString.replace( ")", "_" );
        }
        if ( returnString.contains( "*" ) ) {
            returnString = returnString.replace( "*", "_" );
        }
        if ( returnString.contains( "+" ) ) {
            returnString = returnString.replace( "+", "_" );
        }
        if ( returnString.contains( "/" ) ) {
            returnString = returnString.replace( "/", "_" );
        }
        if ( returnString.contains( "\\" ) ) {
            returnString = returnString.replaceAll( "\\\\", "_" );
        }
        if ( returnString.contains( "%" ) ) {
            returnString = returnString.replace( "%", "_" );  //Important for this one to be at the end as the loading into JPO converts funny chars to %xx values
        }

        return returnString;
    }

    /**
     * This method converts the special characters to codes that HTML can deal
     * with. Taken from http://www.rgagnon.com/javadetails/java-0306.html
     *
     * @param string String to check
     * @return cleansed string
     */
    public static String stringToHTMLString( String string ) {
        StringBuilder sb = new StringBuilder( string.length() );
        // true if last char was blank
        boolean lastWasBlankChar = false;
        int len = string.length();
        char c;

        for ( int i = 0; i < len; i++ ) {
            c = string.charAt( i );
            if ( c == ' ' ) {
                // blank gets extra work,
                // this solves the problem you get if you replace all
                // blanks with &nbsp;, if you do that you loss
                // word breaking
                if ( lastWasBlankChar ) {
                    lastWasBlankChar = false;
                    sb.append( "&nbsp;" );
                } else {
                    lastWasBlankChar = true;
                    sb.append( ' ' );
                }
            } else {
                lastWasBlankChar = false;
                //
                // HTML Special Chars
                switch ( c ) {
                    case '"':
                        sb.append( "&quot;" );
                        break;
                    case '&':
                        sb.append( "&amp;" );
                        break;
                    case '<':
                        sb.append( "&lt;" );
                        break;
                    case '>':
                        sb.append( "&gt;" );
                        break;
                    case '\n':
                        sb.append( "&lt;br/&gt;" );
                        break;
                    default:
                        int ci = 0xffff & c;
                        if ( ci < 160 ) // nothing special only 7 Bit
                        {
                            sb.append( c );
                        } else {
                            // Not 7 Bit use the unicode system
                            sb.append( "&#" );
                            sb.append( Integer.toString( ci ) );
                            sb.append( ';' );
                        }
                        break;
                }

            }
        }
        return sb.toString();
    }

    /**
     * returns the file extension of the indicated url
     *
     * @param url The URL object for which the extension is being requested
     * @return file extension
     */
    public static String getExtension( URL url ) {
        return getExtension( url.toString() );
    }

    /**
     * return the file extension of a string
     *
     * @param s The string for which the extension is being requested
     * @return the file extension
     */
    public static String getExtension( String s ) {
        String ext = null;
        int i = s.lastIndexOf( '.' );

        if ( i > 0 && i < s.length() - 1 ) {
            ext = s.substring( i + 1 );
        }
        return ext;
    }

    /**
     * return everything of the filename up to the extension.
     *
     * @param s The string for which the root of the filename is being requested
     * @return the filename
     */
    public static String getFilenameRoot( String s ) {
        String fnroot = null;
        int i = s.lastIndexOf( '.' );

        if ( i > 0 && i < s.length() - 1 ) {
            fnroot = s.substring( 0, i );
        }
        return fnroot;
    }

    /**
     * method that tests the file extension of a File object for being the
     * correct extension. Either the same file object is returned or a new one
     * is created with the correct extension. If not the correct extension is
     * added. The case of the extension is ignored.
     *
     * @param extension The extension
     * @param testFile File to test
     * @return the file
     */
    public static File correctFilenameExtension( String extension, File testFile ) {
        if ( !testFile.getName().toUpperCase().endsWith( extension.toUpperCase() ) ) {
            return new File( testFile.getPath() + "." + extension );
        }
        return testFile;
    }

    /**
     * Counts the number of real files in the array of files.
     *
     * @param fileArray The files to count
     * @return the number of real files in the array of files
     */
    public static int countfiles( File[] fileArray ) {
        if ( fileArray == null ) {
            return 0;
        }

        int numFiles = 0;
        for ( File fileEntry : fileArray ) {
            try {
                if ( !fileEntry.isDirectory() ) {
                    numFiles++;
                } else {
                    numFiles += countfiles( fileEntry.listFiles() );
                }
            } catch ( SecurityException x ) {
                // Log the error and ignore it and continue
                LOGGER.log( Level.INFO, "Tools.countfiles: got a SecurityException on file: {0} \n{1}", new Object[]{ fileEntry.toString(), x.getMessage() } );
            }
        }
        return numFiles;
    }
    /**
     * Constant that indicates that the directory must exist
     */
    public static final int DIR_MUST_EXIST = 1;
    /**
     * Constant that indicates that the directory must exist and be writable;
     */
    public static final int DIR_MUST_BE_WRITABLE = DIR_MUST_EXIST + 1;

    /**
     * Test the supplied File on whether it is a directory and whether is can be
     * written to.
     *
     * @param testDir Directory to test
     * @param validationType the flag for which test is to be performed
     * @return true if good, false if bad
     */
    public static boolean checkDirectory( File testDir, int validationType ) {
        switch ( validationType ) {
            case DIR_MUST_EXIST:
                return testDir.exists() && testDir.isDirectory();
            case DIR_MUST_BE_WRITABLE:
                if ( testDir.exists() ) {
                    return testDir.canWrite() && testDir.isDirectory();
                } else {
                    File testDirParent = testDir.getParentFile();
                    if ( testDirParent != null ) {
                        return checkDirectory( testDirParent, validationType );
                    } else {
                        return false;
                    }
                }
            default:
                return false;
        }
    }

    /**
     * This method checks whether the JVM has an image reader for the supplied
     * File.
     *
     * @param testFile	The file to be checked
     * @return true if the JVM has a reader false if not.
     */
    public static boolean jvmHasReader( File testFile ) {

        boolean hasReader;
        try ( FileImageInputStream testStream = new FileImageInputStream( testFile ) ) {
            hasReader = ImageIO.getImageReaders( testStream ).hasNext();
            return hasReader;

        } catch ( IOException x ) {
            LOGGER.log( Level.INFO, x.getLocalizedMessage() );
            return false;
        }
    }

    /**
     * This method looks into the supplied subdirectory and tries to see if
     * there is at least one picture in it for which our Java Environment has a
     * decoder.
     *
     * @param subDirectory	The File representing the subdirectory to be
     * recursively searched
     * @return true if there is at least one picture in the subdirectory, false
     * if there is nothing.
     */
    public static boolean hasPictures( File subDirectory ) {
        File[] fileArray = subDirectory.listFiles();
        //logger.info( "Tools.hasPictures: directory " + subDirectory.toString() + " has " + Integer.toString(fileArray.length) + " entries" );
        if ( fileArray == null ) {
            return false;
        }

        for ( File file : fileArray ) {
            if ( file.isDirectory() ) {
                if ( hasPictures( file ) ) {
                    return true;
                }
            } else if ( Tools.jvmHasReader( file ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * method to copy any file from sourceUrl source location to sourceUrl
     * target location
     *
     * @param sourceUrl source url
     * @param targetUrl target url
     * @return the crc
     */
    public static long copyPicture( URL sourceUrl, URL targetUrl ) {
        try (
                InputStream in = sourceUrl.openStream();
                OutputStream out = targetUrl.openConnection().getOutputStream(); ) {
            BufferedInputStream bin = new BufferedInputStream( in );
            BufferedOutputStream bout = new BufferedOutputStream( out );

            long crc = copyBufferedStream( bin, bout );

            return crc;

        } catch ( IOException e ) {
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "copyPictureError1" ) + sourceUrl.toString() + Settings.jpoResources.getString( "copyPictureError2" ) + targetUrl.toString() + Settings.jpoResources.getString( "copyPictureError3" ) + e.getMessage(),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return Long.MIN_VALUE;
        }
    }

    /**
     * method to copy any file from sourceUrl source location to sourceUrl
     * target File location. Works better because files are writable whilst most
     * URL are read only.
     *
     * @param sourceUrl source URL
     * @param targetFile target file
     * @return sourceUrl long for the CRC
     */
    public static long copyPicture( URL sourceUrl, File targetFile ) {
        try (
                InputStream in = sourceUrl.openStream();
                OutputStream out = new FileOutputStream( targetFile ); ) {

            BufferedInputStream bin = new BufferedInputStream( in );
            BufferedOutputStream bout = new BufferedOutputStream( out );

            long crc = copyBufferedStream( bin, bout );

            return crc;

        } catch ( IOException e ) {
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "copyPictureError1" ) + sourceUrl.toString() + Settings.jpoResources.getString( "copyPictureError2" ) + targetFile.toString() + Settings.jpoResources.getString( "copyPictureError3" ) + e.getMessage(),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return Long.MIN_VALUE;
        }
    }

    /**
     * Copy any file from sourceFile source File to sourceFile target File
     * location.
     *
     * @param sourceFile the source file location
     * @param targetFile the target file location
     * @return The crc of the copied picture.
     */
    public static long copyPicture( File sourceFile, File targetFile ) {
        LOGGER.fine( String.format( "Copying file %s to file %s", sourceFile.toString(), targetFile.toString() ) );
        try (
                InputStream in = new FileInputStream( sourceFile );
                OutputStream out = new FileOutputStream( targetFile ); ) {

            BufferedInputStream bin = new BufferedInputStream( in );
            BufferedOutputStream bout = new BufferedOutputStream( out );

            long crc = copyBufferedStream( bin, bout );

            return crc;

        } catch ( IOException e ) {
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "copyPictureError1" ) + sourceFile.toString() + Settings.jpoResources.getString( "copyPictureError2" ) + targetFile.toString() + Settings.jpoResources.getString( "copyPictureError3" ) + e.getMessage(),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return Long.MIN_VALUE;
        }
    }

    /**
     * Copies an input stream to an output stream
     *
     * @param input the input stream
     * @param output the output stream
     * @throws IOException The exception it can throw
     */
    public static void streamcopy( InputStream input, OutputStream output ) throws IOException {
        // 4MB buffer
        byte[] BUFFER = new byte[4096 * 1024];
        int bytesRead;
        while ( ( bytesRead = input.read( BUFFER ) ) != -1 ) {
            output.write( BUFFER, 0, bytesRead );
        }
    }

    /**
     * method to copy any file from a source stream to a output stream
     *
     * @param bin Buffered Input Stream
     * @param bout Buffered Output Stream
     * @return the crc of the file
     * @throws IOException Exception of error
     */
    public static long copyBufferedStream( BufferedInputStream bin,
            BufferedOutputStream bout )
            throws IOException {

        Adler32 crc = new Adler32();
        int c;

        while ( ( c = bin.read() ) != -1 ) {
            bout.write( c );
            crc.update( c );
        }

        bin.close();
        bout.close();
        return crc.getValue();

    }

    /**
     * This method moves the source file to the target file. It tries a java
     * File.renameTo. This doesn't work across different mounted filesystems so
     * if that fails it tries to copy the data from the source file to the
     * target file and deletes the source.
     *
     * Don't forget to call correctReferences if required.
     *
     * @deprecated use apache.commons moveFile instead
     * @param sourceFile The file to be moved
     * @param targetFile The target file it is to be moved to.
     * @return true if successful, false if not.
     */
    public static boolean moveFile( File sourceFile, File targetFile ) {
        if ( !sourceFile.exists() ) {
            LOGGER.severe( String.format( "Source File %s doesn't exist. Move aborted!", sourceFile.toString() ) );
            return false;
        }
        if ( targetFile.exists() ) {
            LOGGER.warning( String.format( "Target File %s already exists. Move aborted!", targetFile.toString() ) );
            return false;
        }
        if ( !sourceFile.canWrite() ) {
            LOGGER.warning( String.format( "Source File %s is write protected. Move aborted!", sourceFile.getPath() ) );
            return false;
        }
        if ( sourceFile.renameTo( targetFile ) ) {
            LOGGER.info( String.format( "Successfully renamed %s to %s", sourceFile.toString(), targetFile.toString() ) );
        } else {
            // perhaps target was on a different filesystem. Trying copying
            LOGGER.info( String.format( "Rename from %s to %s failed. Trying to copy and delete...", sourceFile.toString(), targetFile.toString() ) );
            if ( copyPicture( sourceFile, targetFile ) > Long.MIN_VALUE ) {
                LOGGER.info( "... copy worked. Now deleting source file...." );
                if ( !sourceFile.delete() ) {
                    LOGGER.info( "Nope, deleting source file failed." );
                    return false;
                } else {
                    LOGGER.info( "...delete worked." );
                }
            } else {
                LOGGER.info( "Nope, Copy failed too." );
                return false;
            }
        }

        return true;
    }

    /**
     * Searches for any references in the current collection to the source file
     * and updates them to the target file.
     *
     * @param oldReference The file that was moved
     * @param newReference The new location of the source file
     */
    public static void correctReferences( File oldReference, File newReference ) {
        warnOnEDT();
        //  search for other picture nodes in the tree using this image file
        SortableDefaultMutableTreeNode node;
        Object nodeObject;
        int count = 0;
        Enumeration e = Settings.getPictureCollection().getRootNode().preorderEnumeration();
        while ( e.hasMoreElements() ) {
            node = (SortableDefaultMutableTreeNode) e.nextElement();
            nodeObject = node.getUserObject();
            if ( nodeObject instanceof PictureInfo ) {
                File imageFile = ( (PictureInfo) nodeObject ).getImageFile();
                if ( imageFile != null && imageFile.equals( oldReference ) ) {
                    ( (PictureInfo) nodeObject ).setImageLocation( newReference );
                    count++;
                }
            }
        }
        LOGGER.info( String.format( "%d other Picture Nodes were pointing at the same picture and were corrected", count ) );
    }

    /**
     * Converts a long value into a human readable size such a 245 B, 15 KB, 3
     * MB, 85 GB, 2 TB
     *
     * @param size the input number
     * @return the human readable number
     */
    public static String fileSizeToString( long size ) {
        String suffix = " B";
        if ( size > 1024 ) {
            size /= 1024;
            suffix = " KB";
        }
        if ( size > 1024 ) {
            size /= 1024;
            suffix = " MB";
        }
        if ( size > 1024 ) {
            size /= 1024;
            suffix = " GB";
        }
        if ( size > 1024 ) {
            size /= 1024;
            suffix = " TB";
        }
        suffix = Long.toString( size ) + suffix;
        return suffix;
    }

    /**
     * Method that returns a file handle for a picture that does not exist in
     * the target directory. It tries the combination of path and name first and
     * then tries to suffix _0 _1 _2 etc to the name. If it returns null then it
     * failed
     *
     * @param targetDir the directory in which the picture needs to go
     * @param startName the name to start from
     * @return the new picture filename
     */
    public static File inventPicFilename( File targetDir, String startName ) {
        File testFile = new File( targetDir, startName );
        if ( !testFile.exists() ) {
            return testFile;
        }

        int dotPoint = startName.lastIndexOf( '.' );
        String startNameRoot = startName.substring( 0, dotPoint );
        String startNameSuffix = startName.substring( dotPoint );

        for ( int i = 1; i < 50; i++ ) {
            testFile = new File( targetDir, startNameRoot + "_" + Integer.toString( i ) + startNameSuffix );
            if ( !testFile.exists() ) {
                return testFile;
            }
        }
        LOGGER.severe( String.format( "Could not invent a picture filename for the directory %s and the name %s", targetDir.toString(), startName ) );
        return null;
    }

    /**
     * convenience method to log the amount of free memory
     *
     * @return the free memory
     */
    public static int freeMem() {
        int memory = (int) Runtime.getRuntime().freeMemory() / 1024 / 1024;
        LOGGER.log( Level.INFO, "Free memory: {0}MB", memory );
        return memory;
    }

    /**
     * convenience method to log the amount of free memory. Shows freeMemory,
     * totalMemory and maxMemory
     *
     * @return free memory
     */
    public static String freeMemory() {
        int freeMemory = (int) Runtime.getRuntime().freeMemory() / 1024 / 1024;
        int totalMemory = (int) Runtime.getRuntime().totalMemory() / 1024 / 1024;
        int maxMemory = (int) Runtime.getRuntime().maxMemory() / 1024 / 1024;
        return ( Settings.jpoResources.getString( "freeMemory" ) + Integer.toString( freeMemory ) + "MB/" + Integer.toString( totalMemory ) + "MB/" + Integer.toString( maxMemory ) + "MB" );
    }

    /**
     * Brings up a popup about having run out of memory and runs a Garbage
     * Collection
     */
    public static void dealOutOfMemoryError() {
        Tools.freeMem();
        SwingUtilities.invokeLater(
                () -> JOptionPane.showMessageDialog( Settings.anchorFrame,
                        Settings.jpoResources.getString( "outOfMemoryError" ),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE )
        );

        System.gc();
        System.runFinalization();

        LOGGER.info( "ScalablePicture.scalePicture: JPO has now run a garbage collection and finalization." );
        Tools.freeMem();
    }

    /**
     * method that strips out the root filename from a File object.
     * <p>
     * Example: c:\directory\geysir.jpg returns geysir
     *
     * @param file The file object from which to strip out the name
     * @return the name of the file without extension
     */
    public static String stripOutFilenameRoot( File file ) {
        String description = file.getName();
        int lastDotIndex = description.lastIndexOf( '.' );
        if ( lastDotIndex > -1 ) {
            description = description.substring( 0, lastDotIndex );
        }
        int lastDirectorySeparator = description.lastIndexOf( File.pathSeparator );
        if ( lastDirectorySeparator > -1 ) {
            description = description.substring( lastDirectorySeparator );
        }
        return description;
    }

    /**
     * Method that chooses an xml file or returns null
     *
     * @return the xml file or null
     */
    public static File chooseXmlFile() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode( javax.swing.JFileChooser.FILES_ONLY );
        jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "fileOpenButtonText" ) );
        jFileChooser.setDialogTitle( Settings.jpoResources.getString( "fileOpenHeading" ) );
        jFileChooser.setFileFilter( new XmlFilter() );
        jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );

        int returnVal = jFileChooser.showOpenDialog( Settings.anchorFrame );
        if ( returnVal == javax.swing.JFileChooser.APPROVE_OPTION ) {
            return jFileChooser.getSelectedFile();
        } else {
            return null;
        }
    }

    /**
     * Analyses the drag event and sets the cursor to the appropriate style.
     *
     * @param event the DragSourceDragEvent for which the cursor is to be
     * adjusted
     */
    public static void setDragCursor( DragSourceDragEvent event ) {
        //logger.info( "Tools.setDragCursor: invoked");
        DragSourceContext context = event.getDragSourceContext();
        int dndCode = event.getDropAction();
        if ( ( dndCode & DnDConstants.ACTION_COPY ) != 0 ) {
            context.setCursor( DragSource.DefaultCopyDrop );
        } else if ( ( dndCode & DnDConstants.ACTION_MOVE ) != 0 ) {
            context.setCursor( DragSource.DefaultMoveDrop );
        } else {
            //logger.info( "ACTION_COPY is: " + Integer.toString( DnDConstants.ACTION_COPY ) );
            //logger.info( "ACTION_COPY_OR_MOVE is: " + Integer.toString( DnDConstants.ACTION_COPY_OR_MOVE ) );
            //logger.info( "ACTION_LINK is: " + Integer.toString( DnDConstants.ACTION_LINK ) );
            //logger.info( "ACTION_MOVE is: " + Integer.toString( DnDConstants.ACTION_MOVE ) );
            //logger.info( "ACTION_NONE is: " + Integer.toString( DnDConstants.ACTION_NONE ) );
            //logger.info( "ACTION_REFERENCE is: " + Integer.toString( DnDConstants.ACTION_REFERENCE ) );
            context.setCursor( DragSource.DefaultMoveNoDrop );
        }
    }

    /**
     * Returns a checksum out of the contents of the the supplied File
     *
     * @param file The file to checksum
     * @return returns the checksum as a Long or Long.MIN_VALUE to indicate
     * failure.
     */
    public static long calculateChecksum( File file ) {
        long checksum;
        try {
            checksum = calculateChecksum( new BufferedInputStream( new FileInputStream( file ) ) );
        } catch ( FileNotFoundException x ) {
            checksum = Long.MIN_VALUE;
        }
        return checksum;
    }

    /**
     * Returns a checksum from the supplied input stream using Adler32 crc.
     * Originally taken from: Java ist auch eine Insel (2. Aufl.) von Christian
     * Ullenboom Programmieren fuer die Java 2-Plattform inputStream der Version
     * 1.4
     *
     * @param inputStream The InputStream to read
     * @return returns the checksum as a Long or Long.MIN_VALUE to indicate
     * failure.
     */
    public static long calculateChecksum( InputStream inputStream ) {
        warnOnEDT();
        Adler32 crc = new Adler32();
        int blockLen;

        try {
            while ( ( blockLen = inputStream.available() ) > 0 ) {
                byte ba[] = new byte[blockLen];
                inputStream.read( ba );
                crc.update( ba );
            }
            return crc.getValue();
        } catch ( IOException x ) {
            LOGGER.log( Level.INFO, "Tools.calculateChecksum trapped an IOException. Aborting. Reason:\n{0}", x.getMessage() );
            return Long.MIN_VALUE;
        }
    }

    /**
     * returns the current date and time formatted per the formatting string.
     * See the API doc on SimpleDateFormat for the meaning of the letters.
     *
     * @param formatString The format string
     * @return current date and time
     */
    public static String currentDate( String formatString ) {
        SimpleDateFormat formatter = new SimpleDateFormat( formatString );
        Date currentTime = new Date();
        String dateString = formatter.format( currentTime );
        return dateString;
    }

    /**
     * This method tries it's best to parse the supplied date into a Java Date
     * object.
     *
     * @param dateString the String to be parsed
     * @return the Java Calendar object or null if it could not be parsed.
     */
    public static Calendar parseDate( String dateString ) {
        SimpleDateFormat df = new SimpleDateFormat();
        df.setLenient( true );
        String[] patterns = { "dd.MM.yyyy HH:mm",
            "yyyy:MM:dd HH:mm:ss",
            "dd.MM.yyyy",
            "MM.yyyy",
            "MM-yyyy",
            "dd-MM-yyyy",
            "dd.MM.yy",
            "dd-MM-yy",
            "MM/dd/yy",
            "MM/dd/yyyy",
            "dd MMM yyyy",
            "dd MMM yy",
            "yyyy"
        };
        Date d = null;
        boolean notFound = true;
        for ( int i = 0; ( i < patterns.length ) && notFound; i++ ) {
            try {
                df.applyPattern( patterns[i] );
                d = df.parse( dateString );
                notFound = false;
            } catch ( ParseException x ) {
            }
        }
        if ( d != null ) {
            Calendar cal = Calendar.getInstance();
            cal.setTime( d );
            return cal;
        } else {
            return null;
        }
    }

    /**
     * This method fires up a user function if it can. User functions are only
     * valid on PictureInfo nodes.
     *
     * @param userFunction	The user function to be executed in the array
     * Settings.userFunctionCmd
     * @param myObject The PictureInfo upon which the user function should be
     * executed.
     */
    public static void runUserFunction( int userFunction, PictureInfo myObject ) {
        if ( ( userFunction < 0 ) || ( userFunction >= Settings.maxUserFunctions ) ) {
            LOGGER.info( "Error: called with an out of bounds index" );
            return;
        }
        String command = Settings.userFunctionCmd[userFunction];
        if ( ( command == null ) || ( command.length() == 0 ) ) {
            LOGGER.log( Level.INFO, "Command {0} is not properly defined", Integer.toString( userFunction ) );
            return;
        }

        String filename = ( myObject ).getImageFile().toString();
        command = command.replaceAll( "%f", filename );

        String escapedFilename = filename.replaceAll( "\\s", "\\\\\\\\ " );
        command = command.replaceAll( "%e", escapedFilename );

        URL pictureURL = ( myObject ).getImageURLOrNull();
        if ( pictureURL == null ) {
            LOGGER.info( "The picture doesn't have a valid URL. This is bad. Aborted." );
            return;
        }
        command = command.replaceAll( "%u", pictureURL.toString() );

        LOGGER.log( Level.INFO, "Command to run is: {0}", command );
        try {
            // Had big issues here because the simple exec (String) calls a StringTokenizer
            // which messes up the filename parameters
            int blank = command.indexOf( ' ' );
            if ( blank > -1 ) {
                String[] cmdarray = new String[2];
                cmdarray[0] = command.substring( 0, blank );
                cmdarray[1] = command.substring( blank + 1 );
                Runtime.getRuntime().exec( cmdarray );
            } else {
                String[] cmdarray = new String[1];
                cmdarray[0] = command;
                Runtime.getRuntime().exec( cmdarray );
            }
        } catch ( IOException x ) {
            LOGGER.log( Level.INFO, "Runtime.exec collapsed with and IOException: {0}", x.getMessage() );
        }
    }

    /**
     * This helper method checks if the execution is on the EventDisplayThread
     * and throws an Error if it is not. All Swing operations must be done on
     * the EDT. This method allows easy checking by writing:
     * <code>Tools.checkEDT()</code>
     */
    public static void checkEDT() {
        if ( !SwingUtilities.isEventDispatchThread() ) {
            throw new Error( "Not on EDT! Throwing error." );
        }
    }

    /**
     * This method writes a warning to the log that we are on the EDT and should
     * not be. It also dumps a stack trace. Intended for debugging slow running
     * processes that should not be on the EDT.
     */
    public static void warnOnEDT() {
        if ( SwingUtilities.isEventDispatchThread() ) {
            LOGGER.warning( "We are on the EDT and should not be! This is inefficient Continuing normally." );
            Thread.dumpStack();
            for ( StackTraceElement trace : new Throwable().getStackTrace() ) {
                LOGGER.fine( trace.toString() );
            }
        }
    }

    /**
     * Writes the contents of the specified text file which we have packaged in
     * the jar of the distribution to a File. Useful for stylesheets, dtd and
     * robots.txt.
     *
     * @param rootClass The class from which to search in the jar to help find
     * the file
     * @param fileInJar The name of the file in the jar
     * @param targetDir The target directory
     * @param targetFilename the target filename TODO: Look at the error
     * message! RE 17.10.2010
     */
    public static void copyFromJarToFile( Class rootClass, String fileInJar,
            File targetDir,
            String targetFilename ) {
        warnOnEDT();
        LOGGER.fine( String.format( "Copying File %s from classpath %s to filename %s in directory %s", fileInJar, rootClass.toString(), targetFilename, targetDir ) );
        String textLine;
        try ( InputStream in = rootClass.getResourceAsStream( fileInJar );
                BufferedReader bin = new BufferedReader( new InputStreamReader( in ) );
                FileOutputStream out = new FileOutputStream( new File( targetDir, targetFilename ) );
                OutputStreamWriter osw = new OutputStreamWriter( out );
                BufferedWriter bout = new BufferedWriter( osw ); ) {
            while ( ( textLine = bin.readLine() ) != null ) {
                bout.write( textLine );
                bout.newLine();
            }
            bout.flush();
            bout.close();
            osw.close();
            out.close();
            bin.close();
        } catch ( IOException x ) {
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "CssCopyError" ) + targetFilename + "\n" + x.getMessage(),
                    Settings.jpoResources.getString( "genericWarning" ),
                    JOptionPane.ERROR_MESSAGE );
        }
    }

    /**
     * Returns the content of the specified file which we have packaged in the
     * jar of the distribution in a String.
     *
     * @param rootClass The class from which to search in the jar to help find
     * the file
     * @param fileInJar The name of the file in the jar
     * @return The contents of the file in a string
     */
    public static String copyFromJarToString( Class rootClass, String fileInJar ) {
        LOGGER.info( String.format( "Reading File %s from class %s", fileInJar, rootClass.toString() ) );
        String fileContent = "";
        try (
                InputStream in = rootClass.getResourceAsStream( fileInJar );
                BufferedReader bin = new BufferedReader( new InputStreamReader( in ) );
                Scanner scanner = new Scanner( bin ).useDelimiter( "\\Z" ); ) {
            fileContent = scanner.next();
            scanner.close();
            bin.close();
            in.close();
        } catch ( IOException x ) {
            fileContent = String.format( "<html><head></head><body>Failed to read file %s from Class %s because of exception: %s</body></html>", fileInJar, rootClass.toString(), x.getMessage() );
            LOGGER.log( Level.SEVERE, "Exception: {0}", x.getMessage() );
        }
        return fileContent;
    }
}
