package jpo.dataModel;

import jpo.EventBus.ExportGroupToCollectionRequest;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2002-2019  Richard Eigenmann.
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
 * Writes the JPO Collection to an xml formatted file
 */
public class JpoWriter {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( JpoWriter.class.getName() );

    /**
     * Writes the collection to disk.
     * @param xmlOutputFile The name of the file that is to be created
     * @param startNode	The node from which this is all to be built.
     * @param copyPics	Flag which instructs pictures to be copied too
     */
    public JpoWriter( File xmlOutputFile,
            SortableDefaultMutableTreeNode startNode, boolean copyPics ) {

        write( xmlOutputFile, startNode, copyPics );
    }

    /**
     * Writes the collection in a thread to the file.
     * @param request The request
     */
    public JpoWriter(ExportGroupToCollectionRequest request) {
        Thread t = new Thread( ()
                -> write(request.getTargetFile(), request.getNode(), request.getExportPictures())
        );
        t.start();
   }

    /**
     * method that is invoked by the thread to do things asynchronously
     * @param xmlOutputFile The output file to write to
     * @param startNode The node to start from
     * @param copyPics whether to copy the pictures to the target dir
     */
    public static void write( File xmlOutputFile, SortableDefaultMutableTreeNode startNode, boolean copyPics ) {
        File highresTargetDir = null;
        try {
            if ( copyPics ) {
                highresTargetDir = new File( xmlOutputFile.getParentFile(), "Highres" );

                if ( (! highresTargetDir.mkdirs() ) && (! highresTargetDir.canWrite() ) ) {
                    LOGGER.severe( String.format( "There was a problem creating dir %s", highresTargetDir.toString() ) );
                    return;
                }
            }

            try (BufferedWriter bufferedWriter = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( xmlOutputFile ), StandardCharsets.UTF_8) )) {

                // header
                bufferedWriter.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
                bufferedWriter.newLine();
                bufferedWriter.write( "<!DOCTYPE collection SYSTEM \"" + Settings.COLLECTION_DTD + "\">" );
                bufferedWriter.newLine();

                enumerateGroup( startNode, startNode, bufferedWriter, highresTargetDir, copyPics );

                // categories
                bufferedWriter.write( "<categories>" );
                bufferedWriter.newLine();

                Iterator i = startNode.getPictureCollection().getCategoryIterator();
                Integer key;
                String category;
                while ( i.hasNext() ) {
                    key = (Integer) i.next();
                    category = startNode.getPictureCollection().getCategory( key );
                    bufferedWriter.write( "\t<category index=\"" + key.toString() + "\">" );
                    bufferedWriter.newLine();
                    bufferedWriter.write( "\t\t<categoryDescription><![CDATA[" + category + "]]></categoryDescription>" );
                    bufferedWriter.newLine();
                    bufferedWriter.write( "\t</category>" );
                    bufferedWriter.newLine();
                }

                bufferedWriter.write( "</categories>" );
                bufferedWriter.newLine();

                bufferedWriter.write( "</collection>" );
                bufferedWriter.newLine();

                bufferedWriter.close();

                writeCollectionDTD( xmlOutputFile.getParentFile() );
            } catch ( IOException x ) {
                //x.printStackTrace();
                LOGGER.log( Level.INFO, "IOException: {0}", x.getMessage() );
                JOptionPane.showMessageDialog( null, x.getMessage(),
                        "JpoWriter: IOException",
                        JOptionPane.ERROR_MESSAGE );
            }

        } catch ( SecurityException x ) {
            LOGGER.log( Level.INFO, x.getMessage() );
            JOptionPane.showMessageDialog( null, x.getMessage(),
                    "XmlDistiller: SecurityException",
                    JOptionPane.ERROR_MESSAGE );

        }
    }

    /**
     * recursively invoked method to report all groups.
     *
     * @param groupNode The group node
     * @param bufferedWriter The writer
     * @throws IOException bubble-up IOException
     */
    private static void enumerateGroup( SortableDefaultMutableTreeNode startNode, SortableDefaultMutableTreeNode groupNode,
            BufferedWriter bufferedWriter,
            File highresTargetDir,
            boolean copyPics ) throws IOException {
        GroupInfo groupInfo = (GroupInfo) groupNode.getUserObject();

        groupInfo.dumpToXml( bufferedWriter, groupNode.equals( startNode), groupNode.getPictureCollection().getAllowEdits() );

        SortableDefaultMutableTreeNode childNode;
        Enumeration kids = groupNode.children();
        while ( kids.hasMoreElements() ) {
            childNode = (SortableDefaultMutableTreeNode) kids.nextElement();
            if ( childNode.getUserObject() instanceof GroupInfo ) {
                enumerateGroup( startNode, childNode, bufferedWriter, highresTargetDir, copyPics );
            } else {
                writePicture( childNode, bufferedWriter, highresTargetDir, copyPics );
            }
        }

        groupInfo.endGroupXML( bufferedWriter, groupNode.equals(startNode) );
    }

    /**
     * write a picture to the output
     *
     * @param pictureNode the picture to write
     * @param bufferedWriter the writer to which to write
     * @throws IOException bubble-up IOException
     */
    private static void writePicture( SortableDefaultMutableTreeNode pictureNode,
            BufferedWriter bufferedWriter,
            File highresTargetDir,
            boolean copyPics
    ) throws IOException {
        PictureInfo pictureInfo = (PictureInfo) pictureNode.getUserObject();

        if ( copyPics ) {
            File targetHighresFile = Tools.inventPicFilename( highresTargetDir, pictureInfo.getImageFile().getName() );
            FileUtils.copyFile(pictureInfo.getImageFile(), targetHighresFile);
            PictureInfo tempPi = pictureInfo.getClone();
            tempPi.setImageLocation(targetHighresFile);
            tempPi.dumpToXml( bufferedWriter );
        } else {
            pictureInfo.dumpToXml( bufferedWriter );
        }
    }

    /**
     * Write the collection.dtd file to the target directory.
     *
     * @param directory The directory to write to
     */
    public static void writeCollectionDTD( File directory ) {
        ClassLoader cl = JpoWriter.class.getClassLoader();
        try (
                InputStream in = Objects.requireNonNull(cl.getResource("collection.dtd")).openStream();
                FileOutputStream outStream = new FileOutputStream( new File( directory, "collection.dtd" ) );
                BufferedInputStream bin = new BufferedInputStream( in );
                BufferedOutputStream bout = new BufferedOutputStream( outStream )) {
            int c;

            while ( ( c = bin.read() ) != -1 ) {
                bout.write( c );
            }
        } catch ( IOException e ) {
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "DtdCopyError" ) + e.getMessage(),
                    Settings.jpoResources.getString( "genericWarning" ),
                    JOptionPane.ERROR_MESSAGE );
        }
    }
}
