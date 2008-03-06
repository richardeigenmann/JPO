package jpo;

import java.io.*;
import java.net.*;
import java.awt.Dimension;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.zip.*;

/*
HtmlDistillerThread.java:  class that can write html files
Copyright (C) 2002-2008  Richard Eigenmann.
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
 *  This thread object generates a web of html pages that allows you to browse
 *  groups of pictures using a web browsers. The resulting html pages can be
 *  posted to the internet. Relative addressing has been used throughout to facilitate 
 *  this.
 */
public class HtmlDistillerThread extends Thread {

    /**
     *  Temporary object to scale the image for the html output.
     */
    private ScalablePicture scp = new ScalablePicture();
    /**
     *   Variable that signals to the thread to stop immediately.
     */
    public boolean interrupt = false;
    /**
     *   counter that is incremented with every new picture and is used to 
     *   determine the number for the next one.
     */
    private int picsWroteCounter = 1;
    /**
     *   Frame to show what the thread is doing.
     */
    private JFrame progressFrame;
    /**
     *  Label to show what is being processed.
     */
    private JLabel progressLabel;
    /**
     *  Progress Indicator.
     */
    private JProgressBar progBar;
    /**
     *  Cancel Button.
     */
    private JButton cancelButton;
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
     *  Creates and starts a Thread that writes the picture nodes from the specified
     *  startNode to the target directory.
     *
     *  @param  options The options on how to render the pages
     */
    public HtmlDistillerThread( HtmlDistillerOptions options ) {
        this.options = options;
        start();
    }

    /**
     *  Method that is invoked by the thread to do things asynchroneousely.
     */
    public void run() {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets( 4, 4, 4, 4 );

        JPanel progPanel = new JPanel();
        progPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        progPanel.setLayout( new GridBagLayout() );


        progressLabel = new JLabel();
        progressLabel.setPreferredSize( new Dimension( 600, 20 ) );
        progressLabel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        progPanel.add( progressLabel, c );

        progBar = new JProgressBar( 0, countNodes( options.getStartNode() ) );
        progBar.setBorder( BorderFactory.createLineBorder( Color.gray, 1 ) );
        progBar.setStringPainted( true );
        progBar.setPreferredSize( new Dimension( 140, 20 ) );
        progBar.setMaximumSize( new Dimension( 240, 20 ) );
        progBar.setMinimumSize( new Dimension( 140, 20 ) );
        progBar.setValue( 0 );
        c.gridy++;
        c.fill = GridBagConstraints.NONE;
        progPanel.add( progBar, c );

        cancelButton = new JButton( "Cancel" );
        cancelButton.addActionListener( new ButtonListener() );
        cancelButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelButton.setMinimumSize( Settings.defaultButtonDimension );

        c.gridx++;
        c.anchor = GridBagConstraints.EAST;
        progPanel.add( cancelButton, c );

        progressFrame = new JFrame( Settings.jpoResources.getString( "HtmlDistillerThreadTitle" ) );
        progressFrame.getContentPane().add( progPanel );
        progressFrame.pack();
        progressFrame.setVisible( true );
        progressFrame.setLocationRelativeTo( Settings.anchorFrame );


        // create zip
        try {
            if ( options.isGenerateZipfile() ) {
                FileOutputStream dest = new FileOutputStream( new File( options.getHtmlDirectory(), options.getDownloadZipFileName() ) );
                zipFile = new ZipOutputStream( new BufferedOutputStream( dest ) );
            }
        } catch ( IOException x ) {
            Tools.log( "HtmlDistillerThread.run: Error creating Zipfile. Coninuing without Zip\n" + x.toString() );
            options.setGenerateZipfile( false );
        }


        //scp.setQualityScale();
        writeStylesheet( options.getHtmlDirectory() );
        writeAsHtml( options.getStartNode() );


        try {
            if ( options.isGenerateZipfile() ) {
                zipFile.close();
            }
        } catch ( IOException x ) {
            Tools.log( "HtmlDistillerThread.run: Error closing Zipfile. Coninuing.\n" + x.toString() );
            options.setGenerateZipfile( false );
        }


        if ( folderIconRequired ) {
            try {
                InputStream inStream = Settings.cl.getResource( "jpo/images/icon_folder.gif" ).openStream();
                FileOutputStream outStream = new FileOutputStream( new File( options.getHtmlDirectory(), "jpo_folder_icon.gif" ) );

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


        progressFrame.dispose();

    }

    /** 
     *  This method writes out an HTML page with the small images aligned next to each other.
     *  Each Group and picture is created in an html file called jpo_1234.htm except for the first one that
     *  gets named index.htm. 1234 is the internal hashCode of the node so that we can translate parents and
     *  children to each other.
     *
     *  <p>The object wide groupCounter is used to track how many groups have been created
     *  so far.
     *
     *  @param groupNode		The node at which the extraction is to start.
     *
     */
    public void writeAsHtml( SortableDefaultMutableTreeNode groupNode ) {
        try {
            progBar.setValue( progBar.getValue() + 1 );
            progBar.setString( Integer.toString( progBar.getValue() ) + "/" + Integer.toString( progBar.getMaximum() ) );


            File groupFile;
            if ( groupNode.equals( options.getStartNode() ) ) {
                groupFile = new File( options.getHtmlDirectory(), "index.htm" );
            } else {
                int hashCode = groupNode.hashCode();
                groupFile = new File( options.getHtmlDirectory(), "jpo_" + Integer.toString( hashCode ) + ".htm" );
            }
            BufferedWriter out = new BufferedWriter( new FileWriter( groupFile ) );
            DescriptionsBuffer descriptionsBuffer = new DescriptionsBuffer( options.getPicsPerRow(), out );

            progressLabel.setText( "writing " + groupFile.toString() );
            // write header
            out.write( "<HTML>\n<HEAD>\n\t<LINK rel=\"StyleSheet\" href=\"jpo.css\" type=\"text/css\" media=\"screen\">\n\t<TITLE>" + ( (GroupInfo) groupNode.getUserObject() ).getGroupName() + "</TITLE>\n</HEAD>" );
            out.newLine();


            // write body
            out.write( "<BODY>" );
            out.newLine();

            out.write( "<TABLE BORDER=0 ALIGN=CENTER CELLPADDING=0 CELLSPACING=" + Integer.toString( options.getCellspacing() ) + " WIDTH=" + Integer.toString( options.getPicsPerRow() * options.getThumbnailWidth() + ( options.getPicsPerRow() - 1 ) * options.getCellspacing() ) + ">" );
            out.newLine();

            out.write( "<TR><TD colspan=" + Integer.toString( options.getCellspacing() ) + ">" );
            out.newLine();

            out.write( "<H2>" + stringToHTMLString( ( (GroupInfo) groupNode.getUserObject() ).getGroupName() ) + "</H2>" );
            out.newLine();

            if ( groupNode.equals( options.getStartNode() ) ) {
                if ( options.isGenerateZipfile() ) {
                    out.write( "<a href=\"Download.zip\">Download High Resolution Pictures as a Zipfile</a>" );
                    out.newLine();
                }
            } else {
                //link to parent
                SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) groupNode.getParent();
                String parentLink = "jpo_" + Integer.toString( parentNode.hashCode() ) + ".htm";
                if ( parentNode.equals( options.getStartNode() ) ) {
                    parentLink = "index.htm";
                }

                out.write( "<p>Up to: <a href=\"" + parentLink + "\">" + parentNode.toString() + "</a>" );
                out.newLine();
            }

            out.write( "</TD><TR>" );
            out.newLine();


            int childCount = groupNode.getChildCount();
            int childNumber = 1;
            Enumeration kids = groupNode.children();
            SortableDefaultMutableTreeNode n;

            while ( kids.hasMoreElements() && ( !interrupt ) ) {
                n = (SortableDefaultMutableTreeNode) kids.nextElement();
                if ( n.getUserObject() instanceof GroupInfo ) {

                    out.write( "<TD VALIGN=BOTTOM ALIGN=LEFT WIDTH=" + Integer.toString( options.getThumbnailWidth() ) + ">" );

                    out.write( "<a href=\"jpo_" + Integer.toString( n.hashCode() ) + ".htm\">" + "<img border=0 src=\"jpo_folder_icon.gif\" width=32 height=27></a>" );

                    out.write( "</TD>" );
                    out.newLine();

                    descriptionsBuffer.putCheckFlush( ( (GroupInfo) n.getUserObject() ).getGroupName() );

                    // recursively call the method to output that group.
                    writeAsHtml( n );
                    picsWroteCounter++;
                    folderIconRequired = true;
                } else {
                    writeHtmlPicture( n, out, groupFile, childNumber, childCount, descriptionsBuffer );
                    progBar.setValue( progBar.getValue() + 1 );
                    progBar.setString( Integer.toString( progBar.getValue() ) + "/" + Integer.toString( progBar.getMaximum() ) );
                }
                childNumber++;
            }


            out.write( "</TR>" );
            descriptionsBuffer.flushDescriptions();

            out.write( "</TR><TR><TD colspan=" + Integer.toString( options.getPicsPerRow() ) + ">" );
            out.newLine();
            out.write( Settings.jpoResources.getString( "LinkToJpo" ) );
            out.newLine();
            out.write( "</TD></TR></TABLE>" );
            out.newLine();
            out.write( "</BODY></HTML>" );
            out.close();



        } catch ( IOException x ) {
            x.printStackTrace();
            System.err.println( x.getMessage() );
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    "got an IOException??",
                    "IOExeption",
                    JOptionPane.ERROR_MESSAGE );
        }

    }

    /** 
     *  Write html about a picture to the output.
     *  @param	n	The node for which the HTML is to be written
     *  @param	out	The opened output stream to which the thumbnail tags should be written
     *  @param	groupFile	The name of the html file that holds the small thumbnails of the parent group
     *  @param	childNumber	The current position of the picture in the group
     *  @param	childCount	The total number of pictures in the group
     *  @param	descriptionsBuffer	A buffer for the thumbnails page
     *  @throws IOException if there was some sort of IO Error.
     */
    private void writeHtmlPicture(
            SortableDefaultMutableTreeNode n,
            BufferedWriter out,
            File groupFile,
            int childNumber,
            int childCount,
            DescriptionsBuffer descriptionsBuffer )
            throws IOException {

        PictureInfo p = (PictureInfo) n.getUserObject();



        String fn = "jpo_" + Integer.toString( n.hashCode() );
        String extension = Tools.getExtension( p.getHighresFilename() );
        File lowresFilename;
        File midresFilename;
        File highresFilename;
        picsWroteCounter++;

        switch ( options.getPictureNaming() ) {
            case HtmlDistillerOptions.PICTURE_NAMING_BY_ORIGINAL_NAME:
                String rootName = Tools.getFilenameRoot( p.getHighresFilename() );
                lowresFilename = new File( options.getHtmlDirectory(), rootName + "_l." + extension );
                midresFilename = new File( options.getHtmlDirectory(), rootName + "_m." + extension );
                highresFilename = new File( options.getHtmlDirectory(), rootName + "_h." + extension );
                break;
            case HtmlDistillerOptions.PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
                String convertedNumber = Integer.toString( picsWroteCounter );
                String padding = "00000";
                String root = "jpo_img" + padding.substring( convertedNumber.length() ) + convertedNumber;
                lowresFilename = new File( options.getHtmlDirectory(), root + "_l." + extension );
                midresFilename = new File( options.getHtmlDirectory(), root + "_m." + extension );
                highresFilename = new File( options.getHtmlDirectory(), root + "_h." + extension );
                break;
            default:  //case HtmlDistillerOptions.PICTURE_NAMING_BY_HASH_CODE:
                lowresFilename = new File( options.getHtmlDirectory(), fn + "_l." + extension );
                midresFilename = new File( options.getHtmlDirectory(), fn + "_m." + extension );
                highresFilename = new File( options.getHtmlDirectory(), fn + "_h." + extension );
                break;
        }

        // copy the picture to the target directory
        if ( options.isExportHighres() ) {
            progressLabel.setText( "copying picture " + p.getHighresLocation() + " to " + highresFilename.toString() );
            Tools.copyPicture( p.getHighresURL(), highresFilename );
        }


        if ( options.isGenerateZipfile() ) {
            progressLabel.setText( "adding to zipfile: " + highresFilename.toString() );
            try {
                InputStream in = p.getHighresURL().openStream();
                BufferedInputStream bin = new BufferedInputStream( in );

                ZipEntry entry = new ZipEntry( highresFilename.getName() );
                zipFile.putNextEntry( entry );

                int count;
                byte data[] = new byte[BUFFER_SIZE];
                while ( ( count = bin.read( data, 0, BUFFER_SIZE ) ) != -1 ) {
                    zipFile.write( data, 0, count );
                }

                bin.close();
                in.close();
            } catch ( IOException e ) {
                Tools.log( "HtmDistillerThrea.run: Could not create zipfile entry for " + highresFilename.toString() + "\n" + e.toString() );
            } catch ( Exception e ) {
                Tools.log( "HtmDistillerThrea.run: Could not create zipfile entry for " + highresFilename.toString() + "\n" + e.toString() );
            }
        }



        progressLabel.setText( "testing size of thumbnail " + p.getLowresURL().toString() );
        //Tools.log( "testing size of thumbnail " + p.getLowresURL().toString() );

        int wOrig = 0;
        int hOrig = 0;

        int w = 0;
        int h = 0;
        try {
            InputStream inputStream = p.getLowresURL().openStream();
            inputStream.close();
            scp.loadPictureImd( p.getLowresURL(), p.getRotation() );
            wOrig = scp.getOriginalWidth();
            hOrig = scp.getOriginalHeight();
        //Tools.log( "Image: " + p.getLowresURL().toString() + " is size: w=" + Integer.toString( wOrig ) + " h=" + Integer.toString( hOrig ) );

        } catch ( IOException x ) {
            Tools.log( "got an IO error on opening " + p.getLowresURL() );
        }


        boolean loaded = false;
        if ( ( wOrig == options.getThumbnailWidth() ) || ( hOrig == options.getThumbnailHeight() ) ) {
            progressLabel.setText( "copying picture " + p.getLowresLocation() + " to " + lowresFilename.toString() );
            Tools.log( "copying picture " + p.getLowresLocation() + " to " + lowresFilename.toString() + " w=" + Integer.toString( wOrig ) + " h=" + Integer.toString( hOrig ) );
            Tools.copyPicture( p.getLowresURL(), lowresFilename );
            w = wOrig;
            h = hOrig;
        } else {
            // it needs scaling
            progressLabel.setText( "loading " + p.getHighresLocation() );
            scp.loadPictureImd( p.getHighresURL(), p.getRotation() );

            if ( scp.getStatusCode() == ScalablePicture.ERROR ) {
                Tools.log( "HtmlDistillerThread.writeHtmlPicture: problem reading image using brokenThumbnailPicture instead" );
                scp.loadPictureImd( Settings.cl.getResource( "jpo/images/broken_thumbnail.gif" ), 0f );
            }

            scp.setScaleSize( new Dimension( options.getThumbnailWidth(), options.getThumbnailWidth() ) );
            progressLabel.setText( "scaling " + p.getHighresLocation() );
            scp.scalePicture();
            progressLabel.setText( "writing " + lowresFilename.toString() );
            scp.setJpgQuality( options.getLowresJpgQuality() );
            scp.writeScaledJpg( lowresFilename );
            w = scp.getScaledWidth();
            h = scp.getScaledHeight();
            loaded = true;
        }


        out.write( "<TD VALIGN=BOTTOM ALIGN=CENTER WIDTH=" + Integer.toString( options.getThumbnailWidth() ) + ">" );

        out.write( "<a href=\"" + fn + ".htm\">" + "<img border=0 src=\"" + lowresFilename + "\" width= " + Integer.toString( w ) + " height= " + Integer.toString( h ) + ">" + "</a>" );


        out.write( "</TD>" );
        out.newLine();

        descriptionsBuffer.putCheckFlush( p.getDescription() );



        // scale the midres picture
        if ( !loaded ) {
            progressLabel.setText( "loading " + p.getHighresLocation() );
            scp.loadPictureImd( p.getHighresURL(), p.getRotation() );
        }
        scp.setScaleSize( options.getMidresDimension() );
        progressLabel.setText( "scaling " + p.getHighresLocation() );
        scp.scalePicture();
        progressLabel.setText( "writing " + midresFilename.toString() );
        scp.setJpgQuality( options.getMidresJpgQuality() );
        scp.writeScaledJpg( midresFilename );
        w = scp.getScaledWidth();
        h = scp.getScaledHeight();




        File midresHtmlFile = new File( options.getHtmlDirectory(), fn + ".htm" );
        BufferedWriter midresHtmlWriter = new BufferedWriter( new FileWriter( midresHtmlFile ) );
        String groupDescription =
                ( (SortableDefaultMutableTreeNode) n.getParent() ).getUserObject().toString();

        midresHtmlWriter.write( "<HTML>\n<HEAD>\n\t<LINK rel=\"StyleSheet\" href=\"jpo.css\" type=\"text/css\" media=\"screen\">\n\t<TITLE>" + stringToHTMLString( groupDescription ) + "</TITLE>\n</HEAD>" );
        midresHtmlWriter.newLine();
        midresHtmlWriter.write( "<BODY onLoad=\"changetext(content[0])\"><CENTER>" );
        midresHtmlWriter.newLine();
        midresHtmlWriter.write( "<TABLE cellpadding=0 cellspacing=10>" );
        midresHtmlWriter.write( "<TR><TD colspan=2><CENTER><H2>" + stringToHTMLString( groupDescription ) + "</H2></CENTER></TD></TR>" );
        midresHtmlWriter.newLine();
        midresHtmlWriter.newLine();
        midresHtmlWriter.write( "<TR><TD align=\"CENTER\" valign=\"TOP\" width=" + Integer.toString( options.getMidresWidth() ) + ">" );
        String imgTag = "<IMG border=0 src=\"" + midresFilename + "\" width= " + Integer.toString( w ) + " height= " + Integer.toString( h ) + ">";

        if ( options.isLinkToHighres() ) {
            midresHtmlWriter.write( "<A HREF=\"" + p.getHighresLocation() + "\">" + imgTag + "</A>" );
        } else if ( options.isExportHighres() ) {
            midresHtmlWriter.write( "<A HREF=\"" + highresFilename + "\">" + imgTag + "</A>" );
        } else {
            midresHtmlWriter.write( imgTag );
        }
        midresHtmlWriter.newLine();
        midresHtmlWriter.write( "<P>" + stringToHTMLString( p.getDescription() ) );
        midresHtmlWriter.newLine();
        midresHtmlWriter.write( "</TD>" );
        midresHtmlWriter.newLine();
        midresHtmlWriter.newLine();


        // Do the matrix with the pictures to click
        final int indexBeforeCurrent = 15;
        final int indexPerRow = 5;
        final int indexToShow = 35;
        final int matrixWidth = 130;
        final String font = "<font face=\"Helvetica\" size=\"small\">";
        SortableDefaultMutableTreeNode nde;
        midresHtmlWriter.write( "<TD align=\"CENTER\" valign=\"TOP\" width=" + Integer.toString( matrixWidth ) + ">" );
        midresHtmlWriter.write( "Picture " + Integer.toString( childNumber ) + " of " + Integer.toString( childCount ) );
        midresHtmlWriter.newLine();
        midresHtmlWriter.write( "<TABLE cellpadding=3 cellspacing=1 border=1>" );
        midresHtmlWriter.newLine();
        StringBuffer dhtmlArray = new StringBuffer( "content[0]='" + font + "<p><b>Picture</b> " + Integer.toString( childNumber ) + " of " + Integer.toString( childCount ) + ":<p>" + "<b>Description:</b><br>" + stringToHTMLString( p.getDescription().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) ) + "<p>" );
        if ( p.getCreationTime() != "" ) {
            dhtmlArray.append( "<b>Date:</b><br>" + p.getCreationTime().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) + "<p>" );
        }

        if ( p.getPhotographer() != "" ) {
            dhtmlArray.append( "<b>Photographer:</b><br>" + p.getPhotographer().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) + "<br>" );
        }
        if ( p.getComment() != "" ) {
            dhtmlArray.append( "<b>Comment:</b><br>" + p.getComment().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) + "<br>" );
        }
        if ( p.getFilmReference() != "" ) {
            dhtmlArray.append( "<b>Film Reference:</b><br>" + p.getFilmReference().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) + "<br>" );
        }
        if ( p.getCopyrightHolder() != "" ) {
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
                midresHtmlWriter.write( "<TR>" );
                midresHtmlWriter.newLine();
            }
            midresHtmlWriter.write( "<TD>" );
            if ( i <= childCount ) {
                nde = (SortableDefaultMutableTreeNode) n.getParent().getChildAt( i - 1 );
                int hashCode = nde.hashCode();
                midresHtmlWriter.write( "<A HREF=\"" + "jpo_" + Integer.toString( hashCode ) + ".htm\"" );
                if ( options.isGenerateDHTML() ) {
                    midresHtmlWriter.write( " onMouseover=\"changetext(content[" + Integer.toString( i ) + "])\" onMouseout=\"changetext(content[0])\")" );
                    dhtmlArray.append( "content[" + Integer.toString( i ) + "]='" );

                    if ( nde.getUserObject() instanceof PictureInfo ) {
                        String lowresFn = "";
                        switch ( options.getPictureNaming() ) {
                            case HtmlDistillerOptions.PICTURE_NAMING_BY_ORIGINAL_NAME:
                                String rootName = Tools.getFilenameRoot( p.getHighresFilename() );
                                lowresFn = rootName + "_l." + extension;
                                break;
                            case HtmlDistillerOptions.PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
                                String convertedNumber = Integer.toString( picsWroteCounter + i - childNumber );
                                String padding = "00000";
                                String root = "jpo_img" + padding.substring( convertedNumber.length() ) + convertedNumber;
                                lowresFn = root + "_l." + extension;
                                break;
                            default:  //case HtmlDistillerOptions.PICTURE_NAMING_BY_HASH_CODE:
                                lowresFn = nde.hashCode() + "_l." + extension;
                                break;
                        }
                        dhtmlArray.append( font + "<p>Picture " + Integer.toString( i ) + "/" + Integer.toString( childCount ) + ":<p>" + "<center><img src=\"" + lowresFn + " width=" + Integer.toString( matrixWidth - 10 ) + ">" + "</center><p><i>" + stringToHTMLString( ( (PictureInfo) nde.getUserObject() ).getDescription().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) ) + "</i></font>'\n" );
                    } else {
                        dhtmlArray.append( font + "<p>Item " + Integer.toString( i ) + "/" + Integer.toString( childCount ) + ":<p>" + "<i><b>Group:</b><br>" + ( (GroupInfo) nde.getUserObject() ).getGroupName().replaceAll( "\'", "\\\\'" ).replaceAll( "\n", " " ) + "</i></font>'\n" );
                    }
                }
                midresHtmlWriter.write( ">" );
                if ( i == childNumber ) {
                    midresHtmlWriter.write( "<B>" );
                }
                midresHtmlWriter.write( Integer.toString( i ) );
                if ( i == childNumber ) {
                    midresHtmlWriter.write( "</B>" );
                }
                midresHtmlWriter.write( "</A>" );
            } else {
                midresHtmlWriter.write( "&nbsp" );
            }
            midresHtmlWriter.write( "</TD>" );
            midresHtmlWriter.newLine();
            if ( i % indexPerRow == 0 ) {
                midresHtmlWriter.write( "</TR>" );
                midresHtmlWriter.newLine();
            }
        }
        midresHtmlWriter.write( "</TABLE>" );
        midresHtmlWriter.newLine();
        // End of picture matrix


        midresHtmlWriter.newLine();
        midresHtmlWriter.write( "<P><A HREF=\"" + groupFile.getName() + "\">Up</A>" );
        midresHtmlWriter.write( "&nbsp" );
        midresHtmlWriter.newLine();
        if ( childNumber != 1 ) {
            int hashCode = ( (SortableDefaultMutableTreeNode) n.getParent() ).getChildAt( childNumber - 2 ).hashCode();
            midresHtmlWriter.write( "<A HREF=\"" + "jpo_" + Integer.toString( hashCode ) + ".htm\">Previous</A>" );
            midresHtmlWriter.write( "&nbsp" );
        }
        if ( options.isLinkToHighres() ) {
            midresHtmlWriter.write( "<A HREF=\"" + p.getHighresLocation() + "\">Highres</A>" );
            midresHtmlWriter.write( "&nbsp" );
        } else if ( options.isExportHighres() ) {
            int hashCode = ( (SortableDefaultMutableTreeNode) n.getParent() ).getChildAt( childNumber - 1 ).hashCode();
            midresHtmlWriter.write( "<A HREF=\"jpo_" + Integer.toString( hashCode ) + "_h.jpg\">Highres</A>" );
            midresHtmlWriter.write( "&nbsp" );
        }
        if ( childNumber != childCount ) {
            int hashCode = ( (SortableDefaultMutableTreeNode) n.getParent() ).getChildAt( childNumber ).hashCode();
            midresHtmlWriter.write( "<A HREF=\"" + "jpo_" + Integer.toString( hashCode ) + ".htm\">Next</A>" );
            midresHtmlWriter.newLine();
        }
        if ( options.isGenerateZipfile() ) {
            midresHtmlWriter.write( "<br><a href=\"Download.zip\">Download Zip</a>" );
            midresHtmlWriter.newLine();
        }


        midresHtmlWriter.newLine();
        midresHtmlWriter.write( "<P>" + Settings.jpoResources.getString( "LinkToJpo" ) );
        midresHtmlWriter.newLine();

        if ( options.isGenerateDHTML() ) {
            midresHtmlWriter.write( "<ilayer id=\"d1\" width=" + Integer.toString( matrixWidth ) + " height=200 visibility=\"hide\">" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "<layer id=\"d2\" width=" + Integer.toString( matrixWidth ) + " height=200>" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "<div id=\"descriptions\" align=\"left\">" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "</div></layer></ilayer>" );
        }

        midresHtmlWriter.newLine();
        midresHtmlWriter.write( "</TD></TR>" );
        midresHtmlWriter.newLine();
        midresHtmlWriter.write( "</TABLE>" );
        midresHtmlWriter.newLine();
        midresHtmlWriter.write( "</CENTER>" );
        midresHtmlWriter.newLine();

        if ( options.isGenerateDHTML() ) {
            midresHtmlWriter.write( "<script> " );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "<!-- " );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "/* Textual Tooltip Script- (c) Dynamic Drive (www.dynamicdrive.com) For full source code, installation instructions, 100's more DHTML scripts, and Terms Of Use, visit dynamicdrive.com */ " );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "var content=new Array() " );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( dhtmlArray.toString() );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "function regenerate(){ window.location.reload() } " );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "function regenerate2(){ if (document.layers){ appear()" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "setTimeout(\"window.onresize=regenerate\",450) } }" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "function changetext(whichcontent){" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "if (document.all||document.getElementById){" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "cross_el=document.getElementById? document.getElementById(\"descriptions\"):document.all.descriptions" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "cross_el.innerHTML=whichcontent" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "}" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "else if (document.layers){" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "document.d1.document.d2.document.write(whichcontent)" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "document.d1.document.d2.document.close()" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "}" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "}" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "function appear(){ document.d1.visibility='show' }" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "window.onload=regenerate2" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "//-->" );
            midresHtmlWriter.newLine();
            midresHtmlWriter.write( "</script>" );
            midresHtmlWriter.newLine();
        }


        midresHtmlWriter.write( "</BODY></HTML>" );
        midresHtmlWriter.close();




    }

    /**
     *  Inner class that keeps a buffer of the picture descriptions and will
     *  output a table row with the buffered descriptions when the buffer has
     *  reached it's limit.
     */
    class DescriptionsBuffer {

        /**
         *   The number of columns on the Thumbnail page.
         */
        int columns;
        /**
         *   The HTML page for the Thumbnails.
         */
        BufferedWriter out;
        /**
         *   A counter variable.
         */
        int picCounter = 0;
        /**
         *   An array holding the strings of the pictures.
         */
        String[] descriptions;

        /**
         *  Creates a Description buffer with the indicated number of columns.
         *  @param	columns	The number of columns being generated
         *  @param	out	The Thumbnail page
         */
        DescriptionsBuffer( int columns, BufferedWriter out ) {
            this.columns = columns;
            this.out = out;
            descriptions = new String[options.getPicsPerRow()];
        }

        /**
         *  Adds the supplied string to the buffer.
         *  @param description	The string to be added
         */
        public void put( String description ) {
            descriptions[picCounter] = description;
            picCounter++;
        }

        /**
         *  Adds the supplied string to the buffer and performs a check 
         *  whether the buffer is full
         *  If the buffer is full it flushes it.
         *
         *  @param  description	The String to be added.
         *  @throws IOException if anything went wrong with the writing.
         */
        public void putCheckFlush( String description ) throws IOException {
            put( description );
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
                out.write( "</TR>" );
                flushDescriptions();
                out.write( "<TR>" );

            }
        }

        /**
         *  method that writes the descriptions[] array to the html file.
         *  as each pictures's img tag was written to the file the description
         *  was kept in an array. This method is called each time the row 
         *  of img is full. The method is also called when the last picture has
         *  been written. The array elements are set to null after writing so
         *  that the last row can determine when to stop writing the pictures (the
         *  row can of course be incomplete).
         *
         *  @throws IOException	If writing didn't work.
         */
        public void flushDescriptions() throws IOException {
            out.write( "<TR>" );
            out.newLine();

            for ( int i = 0; i < columns; i++ ) {
                if ( descriptions[i] != null ) {
                    out.write( "<TD VALIGN=TOP WIDTH=" + Integer.toString( options.getThumbnailWidth() ) + ">" );

                    out.write( stringToHTMLString( descriptions[i] ) );

                    out.write( "</TD>" );
                    out.newLine();
                    descriptions[i] = null;
                }
            }
            picCounter = 0;
            out.write( "</TR>" );
        }
    }

    /**
     *  Returns the the total number of nodes belonging to the indicated node.
     *  @param startNode	The node from which the count shall begin.
     *  @return The number of Nodes.
     */
    public static int countNodes( SortableDefaultMutableTreeNode startNode ) {
        int count = 1;

        Enumeration kids = startNode.children();
        SortableDefaultMutableTreeNode n;

        while ( kids.hasMoreElements() ) {
            n = (SortableDefaultMutableTreeNode) kids.nextElement();
            if ( n.getChildCount() > 0 ) {
                count += countNodes( n );
            } else {
                count++;
            }
        }
        return count;
    }

    /**
     *  writes the collection.dtd file to the target directory.
     *  This file war written manually and added to the jar.
     */
    public void writeStylesheet( File directory ) {
        try {
            FileOutputStream out = new FileOutputStream( new File( directory, "jpo.css" ) );

            OutputStreamWriter osw = new OutputStreamWriter( out );
            BufferedWriter bout = new BufferedWriter( osw );

            bout.write( "BODY {background-color: " + Tools.getColor( options.getBackgroundColor() ) + ";\n" );
            bout.write( "	  font-family: Verdana, Arial, Helvetica, sans-serif;\n" );
            bout.write( "	  color: " + Tools.getColor( options.getFontColor() ) + ";}\n" );
            bout.write( "H2   {color: " + Tools.getColor( options.getFontColor() ) + ";}\n" );

            bout.close();
            osw.close();
            out.close();

        } catch ( IOException e ) {
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "CssCopyError" ) + e.getMessage(),
                    Settings.jpoResources.getString( "genericWarning" ),
                    JOptionPane.ERROR_MESSAGE );

        }
    }

    /**
     *  A button listener for the cancel button on the progress frame.
     *  ToDo: Remove this class and integrate it into the button
     */
    private class ButtonListener implements ActionListener {

        /**
         *  Traps the event.
         *  @param evt  The event.
         */
        public void actionPerformed( ActionEvent evt ) {
            progressLabel.setText( Settings.jpoResources.getString( "htmlDistillerInterrupt" ) );
            interrupt = true;
        }
    }

    /**
     *  This method converts the special characters to codes that HTML can deal with.
     *  Taken from http://www.rgagnon.com/javadetails/java-0306.html
     */
    public static String stringToHTMLString( String string ) {
        StringBuffer sb = new StringBuffer( string.length() );
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
                if ( c == '"' ) {
                    sb.append( "&quot;" );
                } else if ( c == '&' ) {
                    sb.append( "&amp;" );
                } else if ( c == '<' ) {
                    sb.append( "&lt;" );
                } else if ( c == '>' ) {
                    sb.append( "&gt;" );
                } else if ( c == '\n' ) // Handle Newline
                {
                    sb.append( "&lt;br/&gt;" );
                } else {
                    int ci = 0xffff & c;
                    if ( ci < 160 ) // nothing special only 7 Bit
                    {
                        sb.append( c );
                    } else {
                        // Not 7 Bit use the unicode system
                        sb.append( "&#" );
                        sb.append( new Integer( ci ).toString() );
                        sb.append( ';' );
                    }
                }
            }
        }
        return sb.toString();
    }
}
