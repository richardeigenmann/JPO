package jpo.dataModel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 XmlDistiller.java:  Writes the JPO collection to an xml formatted file

 Copyright (C) 2002-2014  Richard Eigenmann.
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
public class XmlDistiller
        implements Runnable {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( XmlDistiller.class.getName() );

    /**
     * variable to hold the name of the output file
     */
    private final File xmlOutputFile;

    /**
     * the node to start from
     */
    private final SortableDefaultMutableTreeNode startNode;

    /**
     * Indicates that the pictures should be copied too.
     */
    private final boolean copyPics;

    /**
     * @param xmlOutputFile The name of the file that is to be created
     * @param startNode	The node from which this is all to be built.
     * @param copyPics	Flag which instructs pictures to be copied too
     * @param runAsThread Flag which can instruct this job not to run as a
     * thread.
     */
    public XmlDistiller( File xmlOutputFile,
            SortableDefaultMutableTreeNode startNode, boolean copyPics,
            boolean runAsThread ) {
        this.xmlOutputFile = xmlOutputFile;
        this.startNode = startNode;
        this.copyPics = copyPics;

        if ( runAsThread ) {
            Thread t = new Thread( this, "XmlDistiller" );
            t.start();
        } else {
            run();
        }
    }


    /**
     * Holds the target directory for the pictures if copyPics is true
     */
    private File highresTargetDir;
    
    /**
     * method that is invoked by the thread to do things asynchronously
     */
    @Override
    public final void run() {
        try {
            if ( copyPics ) {
                highresTargetDir = new File( xmlOutputFile.getParentFile(), "Highres" );

                highresTargetDir.mkdirs();
                if ( !( highresTargetDir.canWrite() ) ) {
                    LOGGER.severe( String.format( "There was a problem creating dir %s", highresTargetDir.toString() ) );
                    return;
                }
            }

            BufferedWriter bufferedWriter = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( xmlOutputFile ), "UTF-8" ) );

            // header
            bufferedWriter.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
            bufferedWriter.newLine();
            bufferedWriter.write( "<!DOCTYPE collection SYSTEM \"" + Settings.COLLECTION_DTD + "\">" );
            bufferedWriter.newLine();

            enumerateGroup( startNode, bufferedWriter );

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

        } catch ( SecurityException x ) {
            LOGGER.log( Level.INFO, x.getMessage() );
            JOptionPane.showMessageDialog( null, x.getMessage(),
                    "XmlDistiller: SecurityException",
                    JOptionPane.ERROR_MESSAGE );
        } catch ( IOException x ) {
            //x.printStackTrace();
            LOGGER.log( Level.INFO, "XmlDistiller.run: IOException: {0}", x.getMessage() );
            JOptionPane.showMessageDialog( null, x.getMessage(),
                    "XmlDistiller: IOExeption",
                    JOptionPane.ERROR_MESSAGE );
        }
    }

    /**
     * recursively invoked method to report all groups.
     */
    private void enumerateGroup( SortableDefaultMutableTreeNode groupNode, BufferedWriter bufferedWriter ) throws IOException {
        GroupInfo groupInfo = (GroupInfo) groupNode.getUserObject();

        groupInfo.dumpToXml( bufferedWriter, groupNode == startNode, groupNode.getPictureCollection().getAllowEdits() );

        SortableDefaultMutableTreeNode childNode;
        Enumeration kids = groupNode.children();
        while ( kids.hasMoreElements() ) {
            childNode = (SortableDefaultMutableTreeNode) kids.nextElement();
            if ( childNode.getUserObject() instanceof GroupInfo ) {
                enumerateGroup( childNode, bufferedWriter );
            } else {
                writePicture( childNode, bufferedWriter );
            }
        }

        groupInfo.endGroupXML( bufferedWriter, groupNode == startNode );
    }

    /**
     * write a picture to the output
     */
    private void writePicture( SortableDefaultMutableTreeNode pictureNode, BufferedWriter bufferedWriter ) throws IOException {
        PictureInfo pictureInfo = (PictureInfo) pictureNode.getUserObject();

        if ( copyPics ) {
            File targetHighresFile = Tools.inventPicFilename( highresTargetDir, pictureInfo.getHighresFilename() );
            Tools.copyPicture( pictureInfo.getHighresURL(), targetHighresFile );
            pictureInfo.dumpToXml( bufferedWriter,
                    targetHighresFile.toURI().toURL().toString()
            );
        } else {
            pictureInfo.dumpToXml( bufferedWriter );
        }
    }

    /**
     * Write the collection.dtd file to the target directory.
     *
     * @param directory
     */
    public void writeCollectionDTD( File directory ) {
        ClassLoader cl = this.getClass().getClassLoader();
        try (
                InputStream in = cl.getResource( "jpo/collection.dtd" ).openStream();
                FileOutputStream outStream = new FileOutputStream( new File( directory, "collection.dtd" ) );
                BufferedInputStream bin = new BufferedInputStream( in );
                BufferedOutputStream bout = new BufferedOutputStream( outStream ); ) {
            int c;

            while ( ( c = bin.read() ) != -1 ) {
                outStream.write( c );
            }

            in.close();
            outStream.close();

        } catch ( IOException e ) {
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "DtdCopyError" ) + e.getMessage(),
                    Settings.jpoResources.getString( "genericWarning" ),
                    JOptionPane.ERROR_MESSAGE );
        }
    }
}
