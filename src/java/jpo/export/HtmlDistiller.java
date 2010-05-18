package jpo.export;

import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import jpo.dataModel.GroupInfo;
import jpo.gui.ScalablePicture;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.PictureInfo;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.*;
import jpo.gui.ProgressGui;

/*
HtmlDistiller.java:  class that can write html files
 *
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
 *  This class generates a set of HTML pages that allows a user to browse
 *  groups of pictures in a web-browser. The resulting html pages can be
 *  posted to the Internet. Relative addressing has been used throughout to facilitate 
 *  this.
 */
public class HtmlDistiller
        extends SwingWorker<Integer, String> {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( HtmlDistiller.class.getName() );


    /*{
    logger.setLevel( Level.ALL );
    }*/
    /**
     *  Temporary object to scale the image for the html output.
     */
    private ScalablePicture scp = new ScalablePicture();

    /**
     *   counter that is incremented with every new picture and is used to 
     *   determine the number for the next one.
     */
    private int picsWroteCounter = 1;

    /**
     *   Indicator that gets set to true if groupnodes are being written so that
     *   the folder icon is created.
     */
    private boolean folderIconRequired = false;

    /**
     *  Handle for the zipfile
     */
    private ZipOutputStream zipFile;

    /**
     *  static size of the buffer to be used in copy operations
     */
    private static final int BUFFER_SIZE = 2048;

    /**
     * The preferences that define how to render the Html page.
     */
    private HtmlDistillerOptions options;


    /**
     *  Creates and starts a Thread that renders the web page files
     *  to the target directory.
     *
     *  @param  options The parameters the user chose on how to render the pages
     */
    public HtmlDistiller( HtmlDistillerOptions options ) {
        this.options = options;
        Tools.checkEDT();
        int totalNodes = Tools.countNodes( options.getStartNode() );
        progGui = new ProgressGui( totalNodes,
                Settings.jpoResources.getString( "HtmlDistillerThreadTitle" ),
                String.format( Settings.jpoResources.getString( "HtmlDistDone" ), totalNodes ) );
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
        } catch ( IOException x ) {
            logger.severe( "Error creating Zipfile. Coninuing without Zip\n" + x.toString() );
            options.setGenerateZipfile( false );
        }

        Tools.copyFromJarToFile( HtmlDistiller.class, "jpo.css", options.getTargetDirectory(), "jpo.css" );
        if ( options.isWriteRobotsTxt() ) {
            Tools.copyFromJarToFile( HtmlDistiller.class, "robots.txt", options.getTargetDirectory(), "robots.txt" );
        }
        writeGroup( options.getStartNode() );


        try {
            if ( options.isGenerateZipfile() ) {
                zipFile.close();
            }
        } catch ( IOException x ) {
            logger.severe( "Error closing Zipfile. Coninuing.\n" + x.toString() );
            options.setGenerateZipfile( false );
        }


        if ( folderIconRequired ) {
            try {
                InputStream inStream = Settings.cl.getResource( "jpo/images/icon_folder.gif" ).openStream();
                FileOutputStream outStream = new FileOutputStream( new File( options.getTargetDirectory(), "jpo_folder_icon.gif" ) );

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
                x.printStackTrace();
                System.err.println( x.getMessage() );
                JOptionPane.showMessageDialog(
                        Settings.anchorFrame,
                        "got an IOException copying icon_folder.gif\n" + x.getMessage(),
                        "IOExeption",
                        JOptionPane.ERROR_MESSAGE );
            }
        }



        return Integer.MAX_VALUE;
    }

    /**
     *   This object holds a reference to the progress GUI for the user.
     */
    private ProgressGui progGui;


    /**
     * This method is called by SwingWorker when the background process
     * sends a publish.
     * @param messages A message that will be written to the logfile.
     */
    @Override
    protected void process( List<String> messages ) {
        for ( String message : messages ) {
            logger.info( String.format( "messge: %s", message ) );
            progGui.progressIncrement();
        }
    }


    /**
     * SwingWorker calls here when the background task is done.
     */
    @Override
    protected void done() {
        progGui.switchToDoneMode();
    }


    /**
     *  This method writes out an HTML page with the small images aligned next to each other.
     *  Each Group and picture is created in an html file called jpo_1234.htm except for the first one that
     *  gets named index.htm. 1234 is the internal hashCode of the node so that we can translate parents and
     *  children to each other.
     *
     *  @param groupNode		The node at which the extraction is to start.
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
            BufferedWriter out = new BufferedWriter( new FileWriter( groupFile ) );
            DescriptionsBuffer descriptionsBuffer = new DescriptionsBuffer( options.getPicsPerRow(), out );

            logger.fine( String.format( "Writing: %s", groupFile.toString() ) );
            // write header
            out.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
            out.newLine();
            out.write( "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" );
            out.newLine();
            out.write( "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">" );
            out.newLine();

            out.write( "<head>\n\t<link rel=\"StyleSheet\" href=\"jpo.css\" type=\"text/css\" media=\"screen\" />\n\t<title>" + ( (GroupInfo) groupNode.getUserObject() ).getGroupName() + "</title>\n</head>" );
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
                    picsWroteCounter++;
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
            logger.severe( x.getMessage() );
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    "got an IOException??",
                    "IOExeption",
                    JOptionPane.ERROR_MESSAGE );
        }

    }


    /**
     *  Write html for a picture in the set of webpages.
     *  @param	pictureNode	The node for which the HTML is to be written
     *  @param	out	The opened output stream of the overview page to which the thumbnail tags should be written
     *  @param	groupFile	The name of the html file that holds the small thumbnails of the parent group
     *  @param	childNumber	The current position of the picture in the group
     *  @param	childCount	The total number of pictures in the group
     *  @param	descriptionsBuffer	A buffer for the thumbnails page
     *  @throws IOException If there was some sort of IO Error.
     */
    private void writePicture(
            SortableDefaultMutableTreeNode pictureNode,
            BufferedWriter out,
            File groupFile,
            int childNumber,
            int childCount,
            DescriptionsBuffer descriptionsBuffer )
            throws IOException {

        PictureInfo p = (PictureInfo) pictureNode.getUserObject();
        publish( String.format( "Writing picture node: %s", p.toString() ) );

        String extension = Tools.getExtension( p.getHighresFilename() );
        File lowresFile;
        File midresFile;
        File highresFile;
        String midresHtmlFileName;

        switch ( options.getPictureNaming() ) {
            case HtmlDistillerOptions.PICTURE_NAMING_BY_ORIGINAL_NAME:
                String rootName = Tools.cleanupFilename( Tools.getFilenameRoot( p.getHighresFilename() ) );
                lowresFile = new File( options.getTargetDirectory(), rootName + "_l." + extension );
                midresFile = new File( options.getTargetDirectory(), rootName + "_m." + extension );
                highresFile = new File( options.getTargetDirectory(), rootName + "_h." + extension );
                midresHtmlFileName = rootName + ".htm";
                break;
            case HtmlDistillerOptions.PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
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

        // copy the picture to the target directory
        if ( options.isExportHighres() ) {
            logger.fine( String.format( "Copying picture %s to %s", p.getHighresLocation(), highresFile.toString() ) );
            Tools.copyPicture( p.getHighresURL(), highresFile );
        }


        if ( options.isGenerateZipfile() ) {
            logger.fine( String.format( "Adding to zipfile: %s", highresFile.toString() ) );
            try {
                InputStream in = p.getHighresURL().openStream();
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
                logger.severe( "Could not create zipfile entry for " + highresFile.toString() + "\n" + e.toString() );
            } catch ( Exception e ) {
                logger.severe( "Could not create zipfile entry for " + highresFile.toString() + "\n" + e.toString() );
            }
        }

        logger.fine( "testing size of thumbnail " + p.getLowresURL().toString() );

        int wOrig = 0;
        int hOrig = 0;

        int w = 0;
        int h = 0;

        logger.fine( String.format( "Loading: %s", p.getHighresLocation() ) );
        scp.loadPictureImd( p.getHighresURL(), p.getRotation() );

        if ( scp.getStatusCode() == ScalablePicture.ERROR ) {
            logger.severe( "Problem reading image " + p.getHighresLocation() + " using brokenThumbnailPicture instead" );
            scp.loadPictureImd( Settings.cl.getResource( "jpo/images/broken_thumbnail.gif" ), 0f );
        }

        scp.setScaleSize( options.getThumbnailDimension() );
        logger.fine( String.format( "Scaling: %s", p.getHighresLocation() ) );
        scp.scalePicture();
        logger.fine( String.format( "Writing: %s", lowresFile.toString() ) );
        scp.setJpgQuality( options.getLowresJpgQuality() );
        scp.writeScaledJpg( lowresFile );
        w = scp.getScaledWidth();
        h = scp.getScaledHeight();

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
        out.write( "\">" + "<img src=\"" + lowresFile.getName() + "\" width=\"" + Integer.toString( w ) + "\" height=\"" + Integer.toString( h ) + "\" alt=\"" + Tools.stringToHTMLString( p.getDescription() ) + "\" " + " />" + "</a>" );


        out.write( "</td>" );
        out.newLine();

        descriptionsBuffer.putDescription( p.getDescription() );

        scp.setScaleSize( options.getMidresDimension() );
        logger.fine( "Scaling: " + p.getHighresLocation() );
        scp.scalePicture();
        logger.fine( "Writing: " + midresFile.toString() );
        scp.setJpgQuality( options.getMidresJpgQuality() );
        scp.writeScaledJpg( midresFile );
        w = scp.getScaledWidth();
        h = scp.getScaledHeight();


        if ( options.isGenerateMidresHtml() ) {

            File midresHtmlFile = new File( options.getTargetDirectory(), midresHtmlFileName );
            BufferedWriter midresHtmlWriter = new BufferedWriter( new FileWriter( midresHtmlFile ) );
            String groupDescription =
                    ( (SortableDefaultMutableTreeNode) pictureNode.getParent() ).getUserObject().toString();

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
            String imgTag = "<img src=\"" + midresFile.getName() + "\" width= \"" + Integer.toString( w ) + "\" height=\"" + Integer.toString( h ) + "\" alt=\"" + Tools.stringToHTMLString( p.getDescription() ) + "\" />";

            if ( options.isLinkToHighres() ) {
                midresHtmlWriter.write( "<a href=\"" + p.getHighresLocation() + "\">" + imgTag + "</a>" );
            } else if ( options.isExportHighres() ) {
                midresHtmlWriter.write( "<a href=\"" + highresFile.getName() + "\">" + imgTag + "</a>" );
            } else {
                midresHtmlWriter.write( imgTag );
            }
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "<p/>" + Tools.stringToHTMLString( p.getDescription() ) );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "</td>" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.newLine();


            // Do the matrix with the pictures to click
            final int indexBeforeCurrent = 15;
            final int indexPerRow = 5;
            final int indexToShow = 35;
            final int matrixWidth = 130;
            final String font = "<font face=\"Helvetica\" size=\"small\">";
            SortableDefaultMutableTreeNode nde;
            midresHtmlWriter.write( "<td align=\"center\" valign=\"top\">" );
            midresHtmlWriter.write( String.format( "Picture %d of %d", childNumber, childCount ) );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "<table cellpadding=\"3\" cellspacing=\"1\" border=\"1\">" );
            midresHtmlWriter.newLine();
            StringBuffer dhtmlArray = new StringBuffer( "content[0]='" + font + "<p><b>Picture</b> " + Integer.toString( childNumber ) + " of " + Integer.toString( childCount ) + ":<p>" + "<b>Description:</b><br>" + Tools.stringToHTMLString( p.getDescription().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) ) + "<p>" );
            if ( p.getCreationTime().length() > 0 ) {
                dhtmlArray.append( "<b>Date:</b><br>" + p.getCreationTime().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) + "<p>" );
            }

            if ( p.getPhotographer().length() > 0 ) {
                dhtmlArray.append( "<b>Photographer:</b><br>" + p.getPhotographer().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) + "<br>" );
            }
            if ( p.getComment().length() > 0 ) {
                dhtmlArray.append( "<b>Comment:</b><br>" + p.getComment().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) + "<br>" );
            }
            if ( p.getFilmReference().length() > 0 ) {
                dhtmlArray.append( "<b>Film Reference:</b><br>" + p.getFilmReference().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) + "<br>" );
            }
            if ( p.getCopyrightHolder().length() > 0 ) {
                dhtmlArray.append( "<b>Copyright Holder:</b><br>" + p.getCopyrightHolder().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) + "<br>" );
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
                            case HtmlDistillerOptions.PICTURE_NAMING_BY_ORIGINAL_NAME:
                                PictureInfo pi = (PictureInfo) nde.getUserObject();
                                String rootName = Tools.cleanupFilename( Tools.getFilenameRoot( pi.getHighresFilename() ) );
                                nodeUrl = rootName + ".htm";
                                lowresFn = rootName + "_l." + extension;
                                break;
                            case HtmlDistillerOptions.PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
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
                            dhtmlArray.append( "content[" + Integer.toString( i ) + "]='" );

                            dhtmlArray.append( font + "<p />Picture " + Integer.toString( i ) + "/" + Integer.toString( childCount ) + ":<p />" + "<img src=\"" + lowresFn + "\" width=\"" + Integer.toString( matrixWidth - 10 ) + "\" alt=\"Thumbnail\" />" + "<p /><i>" + Tools.stringToHTMLString( ( (PictureInfo) nde.getUserObject() ).getDescription().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) ) + "</i></font>'\n" );
                        } else {
                            //dhtmlArray.append( font + "<p />Item " + Integer.toString( i ) + "/" + Integer.toString( childCount ) + ":<p />" + "<i><b>Group:</b><br />" + ( (GroupInfo) nde.getUserObject() ).getGroupName().replaceAll( "\'", "\\\\'" ).replaceAll( "\pictureNode", " " ) + "</i></font>'\pictureNode" );
                            dhtmlArray.append( font + "<p />Item " + Integer.toString( i ) + "/" + Integer.toString( childCount ) + ":<p />" + Tools.stringToHTMLString( ( (PictureInfo) nde.getUserObject() ).getDescription().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) ) + "</i></font>'\n" );
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
                    case HtmlDistillerOptions.PICTURE_NAMING_BY_ORIGINAL_NAME:
                        SortableDefaultMutableTreeNode priorNode = (SortableDefaultMutableTreeNode) ( (SortableDefaultMutableTreeNode) pictureNode.getParent() ).getChildAt( childNumber - 2 );
                        Object userObject = priorNode.getUserObject();
                        if ( userObject instanceof PictureInfo ) {
                            previousHtmlFilename = Tools.cleanupFilename( Tools.getFilenameRoot( ( (PictureInfo) userObject ).getHighresFilename() ) ) + ".htm";
                        } else {
                            previousHtmlFilename = "index.htm"; // actually something has gone horribly wrong
                        }
                        break;
                    case HtmlDistillerOptions.PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
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
                midresHtmlWriter.write( "<a href=\"" + p.getHighresLocation() + "\">Highres</a>" );
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
                    case HtmlDistillerOptions.PICTURE_NAMING_BY_ORIGINAL_NAME:
                        SortableDefaultMutableTreeNode priorNode = (SortableDefaultMutableTreeNode) ( (SortableDefaultMutableTreeNode) pictureNode.getParent() ).getChildAt( childNumber );
                        Object userObject = priorNode.getUserObject();
                        if ( userObject instanceof PictureInfo ) {
                            nextHtmlFilename = Tools.cleanupFilename( Tools.getFilenameRoot( ( (PictureInfo) userObject ).getHighresFilename() ) ) + ".htm";
                        } else {
                            nextHtmlFilename = "index.htm"; // actually something has gone horribly wrong
                        }
                        break;
                    case HtmlDistillerOptions.PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
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


            midresHtmlWriter.write( "</body></html>" );
            midresHtmlWriter.close();

        }


    }

    /**
     *  Inner class that keeps a buffer of the picture descriptions and will
     *  output a table row with the buffered descriptions when the buffer has
     *  reached it's limit.
     */
    private class DescriptionsBuffer {

        /**
         *   The number of columns on the Thumbnail page.
         */
        private int columns;

        /**
         *   The HTML page for the Thumbnails.
         */
        private BufferedWriter out;

        /**
         *   A counter variable.
         */
        private int picCounter = 0;

        /**
         *   An array holding the strings of the pictures.
         */
        private String[] descriptions;


        /**
         *  Creates a Description buffer with the indicated number of columns.
         *  @param	columns	The number of columns being generated
         *  @param	out	The Thumbnail page
         */
        public DescriptionsBuffer( int columns, BufferedWriter out ) {
            this.columns = columns;
            this.out = out;
            descriptions = new String[options.getPicsPerRow()];
        }


     
        /**
         *  Adds the supplied string to the buffer and performs a check 
         *  whether the buffer is full
         *  If the buffer is full it flushes it.
         *
         *  @param  description	The String to be added.
         *  @throws IOException if anything went wrong with the writing.
         */
        public void putDescription( String description ) throws IOException {
            descriptions[picCounter] = description;
            picCounter++;
            flushIfNescessary();
        }


        /**
         *  Checks whether the buffer is full and if so will
         *  terminate the current line, flush the buffer and 
         *  start a new line.
         *
         *  @throws IOException if something went wrong with wrting.
         */
        public void flushIfNescessary() throws IOException {
            if ( picCounter == columns ) {
                out.write( "</tr>" );
                flushDescriptions();
                out.write( "<tr>" );

            }
        }


        /**
         *  method that writes the descriptions[] array to the html file.
         *  As each pictures's img tag was written to the file the description
         *  was kept in an array. This method is called each time the row 
         *  of img is full. The method is also called when the last picture has
         *  been written. The array elements are set to null after writing so
         *  that the last row can determine when to stop writing the pictures (the
         *  row can of course be incomplete).
         *
         *  @throws IOException	If writing didn't work.
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
}
