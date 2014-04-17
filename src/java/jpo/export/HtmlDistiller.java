package jpo.export;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.NodeStatistics;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;
import jpo.gui.ProgressGui;
import jpo.gui.ScalablePicture;
import static jpo.gui.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_ERROR;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/*
 * HtmlDistiller.java: class that can write html files 
 * Copyright (C) 2002-2013 Richard Eigenmann. 
 * 
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the License,
 * or any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA. The license is in gpl.txt. See
 * http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 * This class generates a set of HTML pages that allows a user to browse groups
 * of pictures in a web-browser. The resulting html pages can be posted to the
 * Internet. Relative addressing has been used throughout to facilitate this.
 */
public class HtmlDistiller
        extends SwingWorker<Integer, String> {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( HtmlDistiller.class.getName() );

    /*{
     LOGGER.setLevel( Level.ALL );
     }*/
    /**
     * Temporary object to scale the image for the html output.
     */
    private ScalablePicture scp = new ScalablePicture();
    /**
     * counter that is incremented with every new picture and is used to
     * determine the number for the next one.
     */
    private int picsWroteCounter = 1;
    /**
     * Indicator that gets set to true if group nodes are being written so that
     * the folder icon is created.
     */
    private boolean folderIconRequired = false;
    /**
     * Handle for the zipfile
     */
    private ZipOutputStream zipFile;
    /**
     * Array of the files created
     */
    private ArrayList<File> files = new ArrayList<File>();
    /**
     * static size of the buffer to be used in copy operations
     */
    private static final int BUFFER_SIZE = 2048;
    /**
     * The preferences that define how to render the Html page.
     */
    private final HtmlDistillerOptions options;

    /**
     * Creates and starts a Swing Worker that renders the web page files to the
     * target directory.
     *
     * @param options The parameters the user chose on how to render the pages
     */
    public HtmlDistiller( final HtmlDistillerOptions options ) {
        this.options = options;
        Tools.checkEDT();
        //int totalNodes = ( new NodeStatistics( options.getStartNode() ) ).getNumberOfNodes();
        progGui = new ProgressGui( Integer.MAX_VALUE,
                Settings.jpoResources.getString( "HtmlDistillerThreadTitle" ),
                String.format( Settings.jpoResources.getString( "HtmlDistDone" ), 0 ) );

        class getCountWorker extends SwingWorker<Integer, Object> {

            @Override
            public Integer doInBackground() {
                return NodeStatistics.countPictures( options.getStartNode(), true );
            }

            @Override
            protected void done() {
                try {
                    progGui.setMaxiumum( get() );
                    progGui.setDoneString( String.format( Settings.jpoResources.getString( "HtmlDistDone" ), get() ) );
                } catch ( InterruptedException ignore ) {
                } catch ( ExecutionException ignore ) {
                }
            }
        }
        ( new getCountWorker() ).execute();
        execute();
    }

    /**
     * Entry point for the SwingWorker when execute() is called.
     *
     * @return an Integer (not sure what to do with this...)
     * @throws Exception hopefully not
     */
    @Override
    protected Integer doInBackground() throws Exception {
        scp.setQualityScale();
        scp.setScaleSteps( options.getScalingSteps() );

        // create zip
        try {
            if ( options.isGenerateZipfile() ) {
                FileOutputStream dest = new FileOutputStream( new File( options.getTargetDirectory(), options.getDownloadZipFileName() ) );
                zipFile = new ZipOutputStream( new BufferedOutputStream( dest ) );
            }
        } catch ( FileNotFoundException x ) {
            LOGGER.log( Level.SEVERE, "Error creating Zipfile. Coninuing without Zip\n{0}", x.toString() );
            options.setGenerateZipfile( false );
        }

        Tools.copyFromJarToFile( HtmlDistiller.class, "jpo.css", options.getTargetDirectory(), "jpo.css" );
        files.add( new File( options.getTargetDirectory(), "jpo.css" ) );
        if ( options.isWriteRobotsTxt() ) {
            Tools.copyFromJarToFile( HtmlDistiller.class, "robots.txt", options.getTargetDirectory(), "robots.txt" );
            files.add( new File( options.getTargetDirectory(), "robots.txt" ) );
        }
        writeGroup( options.getStartNode() );

        try {
            if ( options.isGenerateZipfile() ) {
                zipFile.close();
            }
        } catch ( IOException x ) {
            LOGGER.log( Level.SEVERE, "Error closing Zipfile. Coninuing.\n{0}", x.toString() );
            options.setGenerateZipfile( false );
        }

        if ( folderIconRequired ) {
            try {
                InputStream inStream = Settings.CLASS_LOADER.getResource( "jpo/images/icon_folder.gif" ).openStream();
                File folderIconFile = new File( options.getTargetDirectory(), "jpo_folder_icon.gif" );
                FileOutputStream outStream = new FileOutputStream( folderIconFile );
                files.add( folderIconFile );

                BufferedInputStream bin = new BufferedInputStream( inStream );
                BufferedOutputStream bout = new BufferedOutputStream( outStream );

                int count;
                byte data[] = new byte[BUFFER_SIZE];
                while ( ( count = bin.read( data, 0, BUFFER_SIZE ) ) != -1 ) {
                    bout.write( data, 0, count );
                }

                bin.close();
                bout.close();

                inStream.close();
                outStream.close();
            } catch ( IOException x ) {
                JOptionPane.showMessageDialog(
                        Settings.anchorFrame,
                        "got an IOException copying icon_folder.gif\n" + x.getMessage(),
                        "IOExeption",
                        JOptionPane.ERROR_MESSAGE );
            }
        }
        switch ( options.getOutputTarget() ) {
            case OUTPUT_SSH_LOCATION:
                sshCopyToServer( files );
                break;
            case OUTPUT_FTP_LOCATION:
                ftpCopyToServer( files );
                break;
        }

        return Integer.MAX_VALUE;
    }
    /**
     * This object holds a reference to the progress GUI for the user.
     */
    private ProgressGui progGui;

    /**
     * This method is called by SwingWorker when the background process sends a
     * publish.
     *
     * @param messages A message that will be written to the logfile.
     */
    @Override
    protected void process( List<String> messages ) {
        for ( String message : messages ) {
            LOGGER.info( String.format( "messge: %s", message ) );
            progGui.progressIncrement();
        }
    }

    /**
     * SwingWorker calls here when the background task is done.
     */
    @Override
    protected void done() {
        progGui.switchToDoneMode();
        URI uri;
        try {
            uri = new URI( "file://" + options.getTargetDirectory() + "/index.htm" );
            Desktop.getDesktop().browse( uri );
        } catch ( IOException ex ) {
            Logger.getLogger( HtmlDistiller.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( URISyntaxException ex ) {
            Logger.getLogger( HtmlDistiller.class.getName() ).log( Level.SEVERE, null, ex );
        }

    }

    /**
     * This method writes out an HTML page with the small images aligned next to
     * each other. Each Group and picture is created in an html file called
     * jpo_1234.htm except for the first one that gets named index.htm. 1234 is
     * the internal hashCode of the node so that we can translate parents and
     * children to each other.
     *
     * @param groupNode	The node at which the extraction is to start.
     *
     */
    public void writeGroup( SortableDefaultMutableTreeNode groupNode ) {
        try {
            publish( String.format( "Writing a picture for group node: %s", groupNode.toString() ) );

            File groupFile;
            if ( groupNode.equals( options.getStartNode() ) ) {
                groupFile = new File( options.getTargetDirectory(), "index.htm" );
            } else {
                int hashCode = groupNode.hashCode();
                groupFile = new File( options.getTargetDirectory(), "jpo_" + Integer.toString( hashCode ) + ".htm" );
            }
            files.add( groupFile );
            BufferedWriter out = new BufferedWriter( new FileWriter( groupFile ) );
            DescriptionsBuffer descriptionsBuffer = new DescriptionsBuffer( options.getPicsPerRow(), out );

            LOGGER.fine( String.format( "Writing: %s", groupFile.toString() ) );
            // write header
            out.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
            out.newLine();
            out.write( "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" );
            out.newLine();
            out.write( "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">" );
            out.newLine();

            out.write( "<head>\n\t<link rel=\"StyleSheet\" href=\"jpo.css\" type=\"text/css\" media=\"screen\" />\n\t<title>" + Tools.stringToHTMLString( ( (GroupInfo) groupNode.getUserObject() ).getGroupName() ) + "</title>\n</head>" );
            out.newLine();

            // write body
            out.write( "<body>" );
            out.newLine();

            out.write( "<table border=\"0\" cellpadding=\"0\" cellspacing=\"" + Integer.toString( options.getCellspacing() ) + "\" width=\"" + Integer.toString( options.getPicsPerRow() * options.getThumbnailWidth() + ( options.getPicsPerRow() - 1 ) * options.getCellspacing() ) + "\">" );
            out.newLine();

            out.write( "<tr><td colspan=\"" + Integer.toString( options.getPicsPerRow() ) + "\">" );

            out.write( String.format( "<h2>%s</h2>", Tools.stringToHTMLString( ( (GroupInfo) groupNode.getUserObject() ).getGroupName() ) ) );

            if ( groupNode.equals( options.getStartNode() ) ) {
                if ( options.isGenerateZipfile() ) {
                    out.newLine();
                    out.write( String.format( "<a href=\"%s\">Download High Resolution Pictures as a Zipfile</a>", options.getDownloadZipFileName() ) );
                    out.newLine();
                }
            } else {
                //link to parent
                SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) groupNode.getParent();
                String parentLink = "jpo_" + Integer.toString( parentNode.hashCode() ) + ".htm";
                if ( parentNode.equals( options.getStartNode() ) ) {
                    parentLink = "index.htm";
                }

                out.write( String.format( "<p>Up to: <a href=\"%s\">%s</a>", parentLink, parentNode.toString() ) );
                out.newLine();
            }

            out.write( "</td></tr>\n<tr>" );
            out.newLine();

            int childCount = groupNode.getChildCount();
            int childNumber = 1;
            Enumeration kids = groupNode.children();
            SortableDefaultMutableTreeNode n;

            while ( kids.hasMoreElements() && ( !progGui.getInterruptor().getShouldInterrupt() ) ) {
                n = (SortableDefaultMutableTreeNode) kids.nextElement();
                if ( n.getUserObject() instanceof GroupInfo ) {

                    out.write( "<td valign=\"bottom\" align=\"left\">" );

                    out.write( "<a href=\"jpo_" + Integer.toString( n.hashCode() ) + ".htm\">" + "<img src=\"jpo_folder_icon.gif\" width=\"32\" height=\"27\" /></a>" );

                    out.write( "</td>" );
                    out.newLine();

                    descriptionsBuffer.putDescription( ( (GroupInfo) n.getUserObject() ).getGroupName() );

                    // recursively call the method to output that group.
                    writeGroup( n );
                    folderIconRequired = true;
                } else {
                    writePicture( n, out, groupFile, childNumber, childCount, descriptionsBuffer );
                }
                childNumber++;
            }
            if ( progGui.getInterruptor().getShouldInterrupt() ) {
                progGui.setDoneString( Settings.jpoResources.getString( "htmlDistillerInterrupt" ) );
            }

            out.write( "</tr>" );
            descriptionsBuffer.flushDescriptions();

            out.write( "\n<tr><td colspan=\"" + Integer.toString( options.getPicsPerRow() ) + "\">" );
            out.write( Settings.jpoResources.getString( "LinkToJpo" ) );
            out.write( "</td></tr></table>" );
            out.newLine();
            out.write( "</body></html>" );
            out.close();

        } catch ( IOException x ) {
            LOGGER.severe( x.getMessage() );
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    "got an IOException??",
                    "IOExeption",
                    JOptionPane.ERROR_MESSAGE );
        }

    }

    /**
     * Write html for a picture in the set of webpages.
     *
     * @param	pictureNode	The node for which the HTML is to be written
     * @param	out	The opened output stream of the overview page to which the
     * thumbnail tags should be written
     * @param	groupFile	The name of the html file that holds the small
     * thumbnails of the parent group
     * @param	childNumber	The current position of the picture in the group
     * @param	childCount	The total number of pictures in the group
     * @param	descriptionsBuffer	A buffer for the thumbnails page
     * @throws IOException If there was some sort of IO Error.
     */
    private void writePicture(
            SortableDefaultMutableTreeNode pictureNode,
            BufferedWriter out,
            File groupFile,
            int childNumber,
            int childCount,
            DescriptionsBuffer descriptionsBuffer )
            throws IOException {

        PictureInfo pictureInfo = (PictureInfo) pictureNode.getUserObject();
        publish( String.format( "Writing picture node %d: %s", picsWroteCounter, pictureInfo.toString() ) );

        String extension = Tools.getExtension( pictureInfo.getHighresFilename() );
        File lowresFile;
        File midresFile;
        File highresFile;
        String midresHtmlFileName;

        switch ( options.getPictureNaming() ) {
            case PICTURE_NAMING_BY_ORIGINAL_NAME:
                String rootName = Tools.cleanupFilename( Tools.getFilenameRoot( pictureInfo.getHighresFilename() ) );
                lowresFile = new File( options.getTargetDirectory(), rootName + "_l." + extension );
                midresFile = new File( options.getTargetDirectory(), rootName + "_m." + extension );
                highresFile = new File( options.getTargetDirectory(), rootName + "_h." + extension );
                midresHtmlFileName = rootName + ".htm";
                break;
            case PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
                String convertedNumber = Integer.toString( picsWroteCounter + options.getSequentialStartNumber() - 1 );
                String padding = "00000";
                String formattedNumber = padding.substring( convertedNumber.length() ) + convertedNumber;
                String root = "jpo_" + formattedNumber;
                lowresFile = new File( options.getTargetDirectory(), root + "_l." + extension );
                midresFile = new File( options.getTargetDirectory(), root + "_m." + extension );
                highresFile = new File( options.getTargetDirectory(), root + "_h." + extension );
                midresHtmlFileName = "jpo_" + formattedNumber + ".htm";
                break;
            default:  //case HtmlDistillerOptions.PICTURE_NAMING_BY_HASH_CODE:
                String fn = "jpo_" + Integer.toString( pictureNode.hashCode() );
                lowresFile = new File( options.getTargetDirectory(), fn + "_l." + extension );
                midresFile = new File( options.getTargetDirectory(), fn + "_m." + extension );
                highresFile = new File( options.getTargetDirectory(), fn + "_h." + extension );
                midresHtmlFileName = fn + ".htm";
                break;
        }
        files.add( lowresFile );
        files.add( midresFile );

        if ( options.isGenerateZipfile() ) {
            LOGGER.fine( String.format( "Adding to zipfile: %s", highresFile.toString() ) );
            try {
                InputStream in = pictureInfo.getHighresURL().openStream();
                BufferedInputStream bin = new BufferedInputStream( in );

                ZipEntry entry = new ZipEntry( highresFile.getName() );
                zipFile.putNextEntry( entry );

                int count;
                byte data[] = new byte[BUFFER_SIZE];
                while ( ( count = bin.read( data, 0, BUFFER_SIZE ) ) != -1 ) {
                    zipFile.write( data, 0, count );
                }

                bin.close();
                in.close();
            } catch ( IOException e ) {
                LOGGER.log( Level.SEVERE, "Could not create zipfile entry for {0}\n{1}", new Object[]{ highresFile.toString(), e.toString() } );
            }
        }

        LOGGER.log( Level.FINE, "testing size of thumbnail {0}", pictureInfo.getLowresURL().toString() );

        LOGGER.fine( String.format( "Loading: %s", pictureInfo.getHighresLocation() ) );
        scp.loadPictureImd( pictureInfo.getHighresURL(), pictureInfo.getRotation() );

        if ( scp.getStatusCode() == SCALABLE_PICTURE_ERROR ) {
            LOGGER.log( Level.SEVERE, "Problem reading image {0} using brokenThumbnailPicture instead", pictureInfo.getHighresLocation() );
            scp.loadPictureImd( Settings.CLASS_LOADER.getResource( "jpo/images/broken_thumbnail.gif" ), 0f );
        }

        // copy the picture to the target directory
        if ( options.isExportHighres() ) {
            files.add( highresFile );
            if ( options.isRotateHighres() && ( pictureInfo.getRotation() != 0 ) ) {
                LOGGER.fine( String.format( "Copying and rotating picture %s to %s", pictureInfo.getHighresLocation(), highresFile.toString() ) );
                scp.setScaleFactor( 1 );
                scp.scalePicture();
                scp.setJpgQuality( options.getMidresJpgQuality() );
                scp.writeScaledJpg( highresFile );
            } else {
                LOGGER.fine( String.format( "Copying picture %s to %s", pictureInfo.getHighresLocation(), highresFile.toString() ) );
                Tools.copyPicture( pictureInfo.getHighresURL(), highresFile );
            }
        }

        scp.setScaleSize( options.getThumbnailDimension() );
        LOGGER.fine( String.format( "Scaling: %s", pictureInfo.getHighresLocation() ) );
        scp.scalePicture();
        LOGGER.fine( String.format( "Writing: %s", lowresFile.toString() ) );
        scp.setJpgQuality( options.getLowresJpgQuality() );
        scp.writeScaledJpg( lowresFile );
        int w = scp.getScaledWidth();
        int h = scp.getScaledHeight();

        out.write( "<td valign=\"bottom\">" );

        // write an anchor so the up come back
        // but only if we are generating MidresHTML pages
        if ( options.isGenerateMidresHtml() ) {
            out.write( String.format( "<a name=\"%s\" />", Tools.stringToHTMLString( lowresFile.getName() ) ) );
        }

        out.write( "<a href=\"" );
        if ( options.isGenerateMidresHtml() ) {
            out.write( midresHtmlFileName );
        } else {
            out.write( midresFile.getName() );
        }
        out.write( "\">" + "<img src=\"" + lowresFile.getName() + "\" width=\"" + Integer.toString( w ) + "\" height=\"" + Integer.toString( h ) + "\" alt=\"" + Tools.stringToHTMLString( pictureInfo.getDescription() ) + "\" " + " />" + "</a>" );

        out.write( "</td>" );
        out.newLine();

        descriptionsBuffer.putDescription( pictureInfo.getDescription() );

        scp.setScaleSize( options.getMidresDimension() );
        LOGGER.log( Level.FINE, "Scaling: {0}", pictureInfo.getHighresLocation() );
        scp.scalePicture();
        LOGGER.log( Level.FINE, "Writing: {0}", midresFile.toString() );
        scp.setJpgQuality( options.getMidresJpgQuality() );
        scp.writeScaledJpg( midresFile );
        w = scp.getScaledWidth();
        h = scp.getScaledHeight();

        if ( options.isGenerateMidresHtml() ) {

            File midresHtmlFile = new File( options.getTargetDirectory(), midresHtmlFileName );
            files.add( midresHtmlFile );

            BufferedWriter midresHtmlWriter = new BufferedWriter( new FileWriter( midresHtmlFile ) );
            String groupDescription
                    = ( (SortableDefaultMutableTreeNode) pictureNode.getParent() ).getUserObject().toString();

            midresHtmlWriter.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "<head>\n\t<link rel=\"StyleSheet\" href=\"jpo.css\" type=\"text/css\" media=\"screen\" />\n\t<title>" + Tools.stringToHTMLString( groupDescription ) + "</title>\n</head>" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "<body onload=\"changetext(content[0])\">" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "<table cellpadding=\"0\" cellspacing=\"10\">" );
            midresHtmlWriter.write( "<tr><td colspan=\"2\"><h2>" + Tools.stringToHTMLString( groupDescription ) + "</h2></td></tr>" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "<tr><td align=\"center\" valign=\"top\">" );
            String imgTag = "<img src=\"" + midresFile.getName() + "\" width= \"" + Integer.toString( w ) + "\" height=\"" + Integer.toString( h ) + "\" alt=\"" + Tools.stringToHTMLString( pictureInfo.getDescription() ) + "\" />";

            if ( options.isLinkToHighres() ) {
                midresHtmlWriter.write( "<a href=\"" + pictureInfo.getHighresLocation() + "\">" + imgTag + "</a>" );
            } else if ( options.isExportHighres() ) {
                midresHtmlWriter.write( "<a href=\"" + highresFile.getName() + "\">" + imgTag + "</a>" );
            } else {
                midresHtmlWriter.write( imgTag );
            }
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "<p/>" + Tools.stringToHTMLString( pictureInfo.getDescription() ) );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "</td>" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.newLine();

            // now do the right column
            midresHtmlWriter.write( "<td align=\"center\" valign=\"top\">" );

            if ( options.isGenerateMap() ) {
                StringBuilder map = new StringBuilder( "" );
                map.append( "<div id=\"map\"></div>" );
                map.append( "<br />" );
                midresHtmlWriter.write( map.toString() );
                midresHtmlWriter.newLine();
            }

            // Do the matrix with the pictures to click
            final int indexBeforeCurrent = 15;
            final int indexPerRow = 5;
            final int indexToShow = 35;
            final int matrixWidth = 130;
            final String font = "<font face=\"Helvetica\" size=\"small\">";
            SortableDefaultMutableTreeNode nde;
            midresHtmlWriter.write( String.format( "Picture %d of %d", childNumber, childCount ) );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "<table cellpadding=\"3\" cellspacing=\"1\" border=\"1\">" );
            midresHtmlWriter.newLine();
            StringBuilder dhtmlArray = new StringBuilder( "content[0]='" + font + "<p><b>Picture</b> " + Integer.toString( childNumber ) + " of " + Integer.toString( childCount ) + ":<p>" + "<b>Description:</b><br>" + Tools.stringToHTMLString( pictureInfo.getDescription().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) ) + "<p>" );
            if ( pictureInfo.getCreationTime().length() > 0 ) {
                dhtmlArray.append( "<b>Date:</b><br>" ).append( pictureInfo.getCreationTime().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) ).append( "<p>" );
            }

            if ( pictureInfo.getPhotographer().length() > 0 ) {
                dhtmlArray.append( "<b>Photographer:</b><br>" ).append( pictureInfo.getPhotographer().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) ).append( "<br>" );
            }
            if ( pictureInfo.getComment().length() > 0 ) {
                StringBuilder append = dhtmlArray.append( "<b>Comment:</b><br>" ).append( pictureInfo.getComment().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) ).append( "<br>" );
            }
            if ( pictureInfo.getFilmReference().length() > 0 ) {
                dhtmlArray.append( "<b>Film Reference:</b><br>" ).append( pictureInfo.getFilmReference().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) ).append( "<br>" );
            }
            if ( pictureInfo.getCopyrightHolder().length() > 0 ) {
                dhtmlArray.append( "<b>Copyright Holder:</b><br>" ).append( pictureInfo.getCopyrightHolder().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) ).append( "<br>" );
            }

            dhtmlArray.append( "</font>'\n" );

            int startNumber = (int) Math.floor( ( childNumber - indexBeforeCurrent - 1 ) / indexPerRow ) * indexPerRow + 1;
            if ( startNumber < 1 ) {
                startNumber = 1;
            }
            int endNumber = startNumber + indexToShow;
            if ( endNumber > childCount ) {
                endNumber = childCount + 1;
            }
            endNumber = endNumber + indexPerRow - ( childCount % indexPerRow );

            for ( int i = startNumber; i < endNumber; i++ ) {
                if ( ( i - 1 ) % indexPerRow == 0 ) {
                    midresHtmlWriter.write( "<tr>" );
                    midresHtmlWriter.newLine();
                }
                midresHtmlWriter.write( "<td>" );
                if ( i <= childCount ) {
                    String nodeUrl = "";
                    String lowresFn = "";

                    nde = (SortableDefaultMutableTreeNode) pictureNode.getParent().getChildAt( i - 1 );
                    if ( nde.getUserObject() instanceof PictureInfo ) {
                        switch ( options.getPictureNaming() ) {
                            case PICTURE_NAMING_BY_ORIGINAL_NAME:
                                PictureInfo pi = (PictureInfo) nde.getUserObject();
                                String rootName = Tools.cleanupFilename( Tools.getFilenameRoot( pi.getHighresFilename() ) );
                                nodeUrl = rootName + ".htm";
                                lowresFn = rootName + "_l." + extension;
                                break;
                            case PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
                                String convertedNumber = Integer.toString( picsWroteCounter + options.getSequentialStartNumber() + i - childNumber - 1 );
                                String padding = "00000";
                                String root = padding.substring( convertedNumber.length() ) + convertedNumber;
                                nodeUrl = "jpo_" + root + ".htm";
                                lowresFn = "jpo_" + root + "_l." + extension;
                                break;
                            default:  //case HtmlDistillerOptions.PICTURE_NAMING_BY_HASH_CODE:
                                int hashCode = nde.hashCode();
                                nodeUrl = "jpo_" + Integer.toString( hashCode ) + ".htm";
                                lowresFn = "jpo_" + nde.hashCode() + "_l." + extension;
                                break;
                        }

                        midresHtmlWriter.write( "<a href=\"" + nodeUrl + "\"" );
                        if ( options.isGenerateDHTML() ) {
                            midresHtmlWriter.write( String.format( " onmouseover=\"changetext(content[%d])\" onmouseout=\"changetext(content[0])\"", i ) );
                            dhtmlArray.append( "content[" ).append( Integer.toString( i ) ).append( "]='" );

                            dhtmlArray.append( font ).append( "<p />Picture " ).append( Integer.toString( i ) ).append( "/" ).append( Integer.toString( childCount ) ).append( ":<p />" + "<img src=\"" ).append( lowresFn ).append( "\" width=\"" ).append( Integer.toString( matrixWidth - 10 ) ).append( "\" alt=\"Thumbnail\" />" + "<p /><i>" ).append( Tools.stringToHTMLString( ( (PictureInfo) nde.getUserObject() ).getDescription().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) ) ).append( "</i></font>'\n" );
                        } else {
                            dhtmlArray.append( font ).append( "<p />Item " ).append( Integer.toString( i ) ).append( "/" ).append( Integer.toString( childCount ) ).append( ":<p />" ).append( Tools.stringToHTMLString( ( (PictureInfo) nde.getUserObject() ).getDescription().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) ) ).append( "</i></font>'\n" );
                        }
                    }
                    midresHtmlWriter.write( ">" );
                    if ( i == childNumber ) {
                        midresHtmlWriter.write( "<b>" );
                    }
                    midresHtmlWriter.write( Integer.toString( i ) );
                    if ( i == childNumber ) {
                        midresHtmlWriter.write( "</b>" );
                    }
                    midresHtmlWriter.write( "</a>" );
                } else {
                    midresHtmlWriter.write( "&nbsp;" );
                }
                midresHtmlWriter.write( "</td>" );
                midresHtmlWriter.newLine();
                if ( i % indexPerRow == 0 ) {
                    midresHtmlWriter.write( "</tr>" );
                    midresHtmlWriter.newLine();
                }
            }
            midresHtmlWriter.write( "</table>" );
            midresHtmlWriter.newLine();
            // End of picture matrix

            midresHtmlWriter.newLine();
            //Up Link
            midresHtmlWriter.write( "<p /><a href=\"" + groupFile.getName() + "#" + Tools.stringToHTMLString( lowresFile.getName() ) + "\">Up</a>" );
            midresHtmlWriter.write( "&nbsp;" );
            midresHtmlWriter.newLine();
            // Link to Previous
            if ( childNumber != 1 ) {
                String previousHtmlFilename = "";
                switch ( options.getPictureNaming() ) {
                    case PICTURE_NAMING_BY_ORIGINAL_NAME:
                        SortableDefaultMutableTreeNode priorNode = (SortableDefaultMutableTreeNode) ( (SortableDefaultMutableTreeNode) pictureNode.getParent() ).getChildAt( childNumber - 2 );
                        Object userObject = priorNode.getUserObject();
                        if ( userObject instanceof PictureInfo ) {
                            previousHtmlFilename = Tools.cleanupFilename( Tools.getFilenameRoot( ( (PictureInfo) userObject ).getHighresFilename() ) ) + ".htm";
                        } else {
                            previousHtmlFilename = "index.htm"; // actually something has gone horribly wrong
                        }
                        break;
                    case PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
                        String convertedNumber = Integer.toString( picsWroteCounter + options.getSequentialStartNumber() - 2 );
                        String padding = "00000";
                        String formattedNumber = padding.substring( convertedNumber.length() ) + convertedNumber;
                        previousHtmlFilename = "jpo_" + formattedNumber + ".htm";
                        break;
                    default:  //case HtmlDistillerOptions.PICTURE_NAMING_BY_HASH_CODE:
                        int hashCode = ( (SortableDefaultMutableTreeNode) pictureNode.getParent() ).getChildAt( childNumber - 2 ).hashCode();
                        previousHtmlFilename = "jpo_" + Integer.toString( hashCode ) + ".htm";
                        break;
                }
                midresHtmlWriter.write( String.format( "<a href=\"%s\">Previous</a>", previousHtmlFilename ) );
                midresHtmlWriter.write( "&nbsp;" );
            }
            if ( options.isLinkToHighres() ) {
                midresHtmlWriter.write( "<a href=\"" + pictureInfo.getHighresLocation() + "\">Highres</a>" );
                midresHtmlWriter.write( "&nbsp;" );
            } else if ( options.isExportHighres() ) {
                // Link to Highres in target directory
                midresHtmlWriter.write( "<a href=\"" + highresFile.getName() + "\">Highres</a>" );
                midresHtmlWriter.write( "&nbsp;" );
            }
            // Linkt to Next
            if ( childNumber != childCount ) {
                String nextHtmlFilename = "";
                switch ( options.getPictureNaming() ) {
                    case PICTURE_NAMING_BY_ORIGINAL_NAME:
                        SortableDefaultMutableTreeNode priorNode = (SortableDefaultMutableTreeNode) ( (SortableDefaultMutableTreeNode) pictureNode.getParent() ).getChildAt( childNumber );
                        Object userObject = priorNode.getUserObject();
                        if ( userObject instanceof PictureInfo ) {
                            nextHtmlFilename = Tools.cleanupFilename( Tools.getFilenameRoot( ( (PictureInfo) userObject ).getHighresFilename() ) ) + ".htm";
                        } else {
                            nextHtmlFilename = "index.htm"; // actually something has gone horribly wrong
                        }
                        break;
                    case PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
                        String convertedNumber = Integer.toString( picsWroteCounter + options.getSequentialStartNumber() );
                        String padding = "00000";
                        String formattedNumber = padding.substring( convertedNumber.length() ) + convertedNumber;
                        nextHtmlFilename = "jpo_" + formattedNumber + ".htm";
                        break;
                    default:  //case HtmlDistillerOptions.PICTURE_NAMING_BY_HASH_CODE:
                        int hashCode = ( (SortableDefaultMutableTreeNode) pictureNode.getParent() ).getChildAt( childNumber ).hashCode();
                        nextHtmlFilename = "jpo_" + Integer.toString( hashCode ) + ".htm";
                        break;
                }

                midresHtmlWriter.write( "<a href=\"" + nextHtmlFilename + "\">Next</a>" );
                midresHtmlWriter.newLine();
            }
            if ( options.isGenerateZipfile() ) {
                midresHtmlWriter.write( "<br /><a href=\"" + options.getDownloadZipFileName() + "\">Download Zip</a>" );
                midresHtmlWriter.newLine();
            }

            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "<p />" + Settings.jpoResources.getString( "LinkToJpo" ) );
            midresHtmlWriter.newLine();

            if ( options.isGenerateDHTML() ) {
                midresHtmlWriter.write( "<ilayer id=\"d1\" width=\"" + Integer.toString( matrixWidth ) + "\" height=\"200\" visibility=\"hide\">" );
                midresHtmlWriter.newLine();
                midresHtmlWriter.write( "<layer id=\"d2\" width=\"" + Integer.toString( matrixWidth ) + "\" height=\"200\">" );
                midresHtmlWriter.newLine();
                midresHtmlWriter.write( "<div id=\"descriptions\" class=\"sidepanel\">" );
                midresHtmlWriter.newLine();
                midresHtmlWriter.write( "</div></layer></ilayer>" );
            }

            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "</td></tr>" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "</table>" );
            midresHtmlWriter.newLine();

            if ( options.isGenerateDHTML() ) {
                Tools.copyFromJarToFile( HtmlDistiller.class, "jpo.js", options.getTargetDirectory(), "jpo.js" );
                files.add( new File( options.getTargetDirectory(), "jpo.js" ) );
                midresHtmlWriter.write( "<script type=\"text/javascript\" src=\"jpo.js\" ></script>" );
                midresHtmlWriter.newLine();
                midresHtmlWriter.write( "<script type=\"text/javascript\">" );
                midresHtmlWriter.newLine();
                midresHtmlWriter.write( "<!-- " );
                midresHtmlWriter.newLine();
                midresHtmlWriter.write( "/* Textual Tooltip Script- (c) Dynamic Drive (www.dynamicdrive.com) For full source code, installation instructions, 100's more DHTML scripts, and Terms Of Use, visit dynamicdrive.com */ " );
                midresHtmlWriter.newLine();
                midresHtmlWriter.write( "var content=new Array() " );
                midresHtmlWriter.newLine();
                midresHtmlWriter.write( dhtmlArray.toString() );
                midresHtmlWriter.newLine();
                midresHtmlWriter.write( "//-->" );
                midresHtmlWriter.newLine();
                midresHtmlWriter.write( "</script>" );
                midresHtmlWriter.newLine();
            }

            if ( options.isGenerateMap() ) {
                midresHtmlWriter.write( "<script type=\"text/javascript\"> <!--" );
                midresHtmlWriter.newLine();
                midresHtmlWriter.write( String.format( "var lat=%f; var lng=%f;", pictureInfo.getLatLng().x, pictureInfo.getLatLng().y ) );
                midresHtmlWriter.newLine();
                midresHtmlWriter.write( "--> </script>" );
                midresHtmlWriter.newLine();
                midresHtmlWriter.write( "<script type=\"text/javascript\" src=\"http://maps.google.com/maps/api/js?sensor=false\"></script>" );
                midresHtmlWriter.newLine();
            }

            midresHtmlWriter.write( "</body></html>" );
            midresHtmlWriter.close();

        }
        picsWroteCounter++;
    }

    /**
     * Inner class that keeps a buffer of the picture descriptions and will
     * output a table row with the buffered descriptions when the buffer has
     * reached it's limit.
     */
    private class DescriptionsBuffer {

        /**
         * The number of columns on the Thumbnail page.
         */
        private final int columns;
        /**
         * The HTML page for the Thumbnails.
         */
        private final BufferedWriter out;
        /**
         * A counter variable.
         */
        private int picCounter = 0;
        /**
         * An array holding the strings of the pictures.
         */
        private final String[] descriptions;

        /**
         * Creates a Description buffer with the indicated number of columns.
         *
         * @param	columns	The number of columns being generated
         * @param	out	The Thumbnail page
         */
        public DescriptionsBuffer( int columns, BufferedWriter out ) {
            this.columns = columns;
            this.out = out;
            descriptions = new String[options.getPicsPerRow()];
        }

        /**
         * Adds the supplied string to the buffer and performs a check whether
         * the buffer is full If the buffer is full it flushes it.
         *
         * @param description	The String to be added.
         * @throws IOException if anything went wrong with the writing.
         */
        public void putDescription( String description ) throws IOException {
            descriptions[picCounter] = description;
            picCounter++;
            flushIfNescessary();
        }

        /**
         * Checks whether the buffer is full and if so will terminate the
         * current line, flush the buffer and start a new line.
         *
         * @throws IOException if something went wrong with wrting.
         */
        public void flushIfNescessary() throws IOException {
            if ( picCounter == columns ) {
                out.write( "</tr>" );
                flushDescriptions();
                out.write( "<tr>" );

            }
        }

        /**
         * method that writes the descriptions[] array to the html file. As each
         * picture's img tag was written to the file the description was kept in
         * an array. This method is called each time the row of img is full. The
         * method is also called when the last picture has been written. The
         * array elements are set to null after writing so that the last row can
         * determine when to stop writing the pictures (the row can of course be
         * incomplete).
         *
         * @throws IOException	If writing didn't work.
         */
        public void flushDescriptions() throws IOException {
            out.newLine();
            out.write( "<tr>" );
            out.newLine();

            for ( int i = 0; i < columns; i++ ) {
                if ( descriptions[i] != null ) {
                    out.write( "<td valign=\"top\">" );

                    out.write( Tools.stringToHTMLString( descriptions[i] ) );

                    out.write( "</td>" );
                    out.newLine();
                    descriptions[i] = null;
                }
            }
            picCounter = 0;
            out.write( "</tr>" );
            out.newLine();
        }
    }

    private void sshCopyToServer( ArrayList<File> files ) {
        LOGGER.info( "Setting up ssh connection:" );
        String response = "";
        JSch jsch = new JSch();
        try {
            LOGGER.info( String.format( "Setting up session for user: %s server: %s port: %d and connecting...", options.getSshUser(), options.getSshServer(), options.getSshPort() ) );
            Session session = jsch.getSession( options.getSshUser(), options.getSshServer(), options.getSshPort() );
            if ( options.getSshAuthType().equals( HtmlDistillerOptions.SshAuthType.SSH_AUTH_PASSWORD ) ) {
                session.setPassword( options.getSshPassword() );
            } else {
                jsch.addIdentity( options.getSshKeyFile() );
            }
            //jsch.setKnownHosts( "/home"+ options.getSshUser() + "/.ssh/known_hosts");
            Properties config = new Properties();
            config.put( "StrictHostKeyChecking", "no" );
            session.setConfig( config );
            session.connect();

            for ( File file : files ) {
                publish( String.format( "scp %s", file.getName() ) );
                scp( session, file );
            }

            session.disconnect();
        } catch ( JSchException ex ) {
            LOGGER.severe( ex.getMessage() );
        } catch ( IOException ex ) {
            LOGGER.severe( ex.getMessage() );
        }
    }

    private void scp( Session session, File file ) throws JSchException, IOException {
        // preserve timestamp
        boolean ptimestamp = true;
        // exec 'scp -t rfile' remotely
        String command = "cd " + options.getSshTargetDir() + "; scp " + ( ptimestamp ? "-p" : "" ) + " -t " + file.getName();

        LOGGER.info( "Opening Channel \"exec\"..." );
        Channel channel = session.openChannel( "exec" );
        LOGGER.info( "Setting command: " + command );
        ( (ChannelExec) channel ).setCommand( command );

        // get I/O streams for remote scp
        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();

        LOGGER.info( "Connecting Channel..." );
        channel.connect();

        if ( checkAck( in ) != 0 ) {
            LOGGER.info( "No Ack 1" );
            Thread.dumpStack();
        }

        if ( ptimestamp ) {
            command = "T " + ( file.lastModified() / 1000 ) + " 0";
            // The access time should be sent here,
            // but it is not accessible with JavaAPI ;-<
            command += ( " " + ( file.lastModified() / 1000 ) + " 0\n" );
            LOGGER.info( "Command: " + command );
            out.write( command.getBytes() );
            out.flush();
            if ( checkAck( in ) != 0 ) {
                LOGGER.info( "No Ack 2" );
                Thread.dumpStack();
            }
        }

        // send "C0644 filesize filename", where filename should not include '/'
        long filesize = file.length();
        command = "C0644 " + filesize + " ";
        command += file.getName();
        command += "\n";
        LOGGER.info( "Command: " + command );
        out.write( command.getBytes() );
        out.flush();
        if ( checkAck( in ) != 0 ) {
            LOGGER.info( "No Ack 3" );
            Thread.dumpStack();
        }

        // send a content of lfile
        FileInputStream fis = null;
        fis = new FileInputStream( file );
        byte[] buf = new byte[1024];
        while ( true ) {
            LOGGER.info( "Sending bytes: " + buf.length );
            int len = fis.read( buf, 0, buf.length );
            if ( len <= 0 ) {
                break;
            }
            out.write( buf, 0, len ); //out.flush();
        }
        fis.close();
        fis = null;

        LOGGER.info( "Sending \0" );
        // send '\0'
        buf[0] = 0;
        out.write( buf, 0, 1 );
        out.flush();
        if ( checkAck( in ) != 0 ) {
            LOGGER.info( "No Ack 4" );
            Thread.dumpStack();
        }
        out.close();

        LOGGER.info( command );
        channel.disconnect();
    }

    static int checkAck( InputStream in ) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if ( b == 0 ) {
            return b;
        }
        if ( b == -1 ) {
            return b;
        }

        if ( b == 1 || b == 2 ) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append( (char) c );
            } while ( c != '\n' );
            if ( b == 1 ) { // error
                System.out.print( sb.toString() );
            }
            if ( b == 2 ) { // fatal error
                System.out.print( sb.toString() );
            }
        }
        return b;
    }

    private void ftpCopyToServer( ArrayList<File> files ) {
        LOGGER.info( "Setting up ftp connection:" );
        final FTPClient ftp = new FTPClient();
        int reply;
        try {
            ftp.connect( options.getFtpServer(), options.getFtpPort() );
            reply = ftp.getReplyCode();
            if ( !FTPReply.isPositiveCompletion( reply ) ) {
                ftp.disconnect();
                LOGGER.severe( "FTP server refused connection." );
                return;
            }

            LOGGER.info( "Good connection:" );
            boolean error = false;
            __main:
            {
                if ( !ftp.login( options.getFtpUser(), options.getFtpPassword() ) ) {
                    ftp.logout();
                    error = true;
                    LOGGER.info( "Could not log in." );
                    break __main;
                }

                System.out.println( "Remote system is " + ftp.getSystemType() );
                ftp.setFileType( FTP.BINARY_FILE_TYPE );
                ftp.enterLocalPassiveMode();
            }

            for ( File file : files ) {
                InputStream input = new BufferedInputStream( new FileInputStream( file ) );
                String remote = options.getFtpTargetDir() + file.getName();
                LOGGER.info( String.format( "Putting file %s to %s", file.getAbsolutePath(), remote ) );
                ftp.storeFile( remote, input );
                input.close();
            }

        } catch ( SocketException ex ) {
            Logger.getLogger( HtmlDistiller.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( IOException ex ) {
            Logger.getLogger( HtmlDistiller.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }
}
