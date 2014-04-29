package jpo.dataModel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 XmlDistiller.java:  class that writes the xml file

 Copyright (C) 2002-2010  Richard Eigenmann.
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
 * a class that exports a tree of chapters to an XML file
 */
public class XmlDistiller
        implements Runnable {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( XmlDistiller.class.getName() );

    /**
     * output file handle
     */
    private BufferedWriter out;

    /**
     * variable to hold the name of the output file
     */
    private final File xmlOutputFile;

    /**
     * highres picture directory if pictures need to be copied
     */
    private File highresTargetDir;

    /**
     * lowres picture directory if pictures need to be copied
     */
    private File lowresTargetDir;

    /**
     * the node to start from
     */
    private final SortableDefaultMutableTreeNode startNode;

    /**
     * temporary variable that indicates that the pictures should be copied too.
     */
    private final boolean copyPics;

    /**
     * @param xmlOutputFile The name of the file that is to be created
     * @param startNode	The node from which this is all to be built.
     * @param copyPics	Flag which instructs pictures to be copied too
     * @param runAsThread	Flag which can instruct this job not to run as a
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
     * method that is invoked by the thread to do things asynchroneousely
     */
    @Override
    public final void run() {
        try {
            if ( copyPics ) {
                highresTargetDir = new File( xmlOutputFile.getParentFile(), "Highres" );
                lowresTargetDir = new File( xmlOutputFile.getParentFile(), "Lowres" );

                highresTargetDir.mkdirs();
                lowresTargetDir.mkdirs();
                if ( !( highresTargetDir.canWrite() && lowresTargetDir.canWrite() ) ) {
                    LOGGER.severe( String.format( "There was a problem creating dir %s or dir %s", highresTargetDir.toString(), lowresTargetDir.toString() ) );
                    return;
                }
            }

            FileWriter fw = new FileWriter( xmlOutputFile );
            out = new BufferedWriter( fw );

            // header
            out.write( "<?xml version='1.0' encoding='" + fw.getEncoding() + "'?>" );
            out.newLine();
            out.write( "<!DOCTYPE collection SYSTEM \"" + Settings.COLLECTION_DTD + "\">" );
            out.newLine();

            enumerateGroup( startNode );

            // categories
            out.write( "<categories>" );
            out.newLine();

            Iterator i = startNode.getPictureCollection().getCategoryIterator();
            Integer key;
            String category;
            while ( i.hasNext() ) {
                key = (Integer) i.next();
                category = startNode.getPictureCollection().getCategory( key );
                out.write( "\t<category index=\"" + key.toString() + "\">" );
                out.newLine();
                out.write( "\t\t<categoryDescription><![CDATA[" + category + "]]></categoryDescription>" );
                out.newLine();
                out.write( "\t</category>" );
                out.newLine();
            }

            out.write( "</categories>" );
            out.newLine();

            out.write( "</collection>" );
            out.newLine();

            out.close();

            writeCollectionDTD( xmlOutputFile.getParentFile() );

        } catch ( SecurityException x ) {
            //e.printStackTrace();
            LOGGER.log( Level.INFO, "XmlDistiller.run: SecurityException: {0}", x.getMessage() );
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
    private void enumerateGroup( SortableDefaultMutableTreeNode groupNode ) throws IOException {
        GroupInfo groupInfo = (GroupInfo) groupNode.getUserObject();

        //if ( copyPics && groupInfo.getLowresFile().canRead() ) {
        //File targetLowresFile = Tools.inventPicFilename( lowresTargetDir, groupInfo.getLowresFilename() );
        //Tools.copyPicture( groupInfo.getLowresURL(), targetLowresFile );
        //  groupInfo.dumpToXml( out, groupNode == startNode, groupNode.getPictureCollection().getAllowEdits() );
        //} else {
        groupInfo.dumpToXml( out, groupNode == startNode, groupNode.getPictureCollection().getAllowEdits() );
        //}

        SortableDefaultMutableTreeNode childNode;
        Enumeration kids = groupNode.children();
        while ( kids.hasMoreElements() ) {
            childNode = (SortableDefaultMutableTreeNode) kids.nextElement();
            if ( childNode.getUserObject() instanceof GroupInfo ) {
                enumerateGroup( childNode );
            } else {
                writePicture( childNode );
            }
        }

        groupInfo.endGroupXML( out, groupNode == startNode );
    }

    /**
     * write a picture to the output
     */
    private void writePicture( SortableDefaultMutableTreeNode pictureNode ) throws IOException {
        PictureInfo pictureInfo = (PictureInfo) pictureNode.getUserObject();

        if ( copyPics ) {
            File targetHighresFile = Tools.inventPicFilename( highresTargetDir, pictureInfo.getHighresFilename() );
            //File targetLowresFile = Tools.inventPicFilename( lowresTargetDir, pictureInfo.getLowresFilename() );
            Tools.copyPicture( pictureInfo.getHighresURL(), targetHighresFile );
            //if ( pictureInfo.getLowresFile().canRead() ) {
            //    Tools.copyPicture( pictureInfo.getLowresURL(), targetLowresFile );
            //}
            pictureInfo.dumpToXml( out,
                    targetHighresFile.toURI().toURL().toString()
            //targetLowresFile.toURI().toURL().toString() 
            );
        } else {
            pictureInfo.dumpToXml( out );
        }
    }

    /**
     * writes the collection.dtd file to the target directory. This file war
     * written manually and added to the jar.
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
