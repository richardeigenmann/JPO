package jpo;

import java.io.*;
import java.net.*;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/*
HtmlDistillerThread.java:  class that can write html files

Copyright (C) 2002  Richard Eigenmann.
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
	 *  The directory into which the web page will be generated.
	 */
	private  File htmlDirectory;




	/**
	 *   How many pictures should be placed next to each 
	 *   other in the html table.
	 */
	private int picsPerRow;


	/**
	 *  The width the thumbnail must not exceed.
	 */
	private int thumbnailWidth;

	/**
	 *  The height the thumbnail must not exceed.
	 */
	private int thumbnailHeight;

	/**
	 *  The width the midres picture must not exceed.
	 */
	private int midresWidth;

	/**
	 *  The height the midres picture must not exceed.
	 */
	private int midresHeight;


	/**
	 *  The padding between two adjacent cells in the output table.
	 */
	private int cellspacing;


	/**
	 *   Indicates whether a highres image should be copied as well.
	 */
	private boolean exportHighres;

	/**
	 *   Indicates whether the highres pictures should be linked to.
	 */
	private boolean linkToHighres;


	/**
	 *  The first node from which the export is to be done.
	 */
	private SortableDefaultMutableTreeNode startNode;


	/**
	 *  Temporary object to scale the image for the html output.
	 */
	private ScalablePicture scp= new ScalablePicture();


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
	 *  Lablel to show what is being processed.
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
	 *   The compression rate passed to the jpg compressor 0 - 1. A value of 0 means maximum
	 *   compression and crap quality, 1 means best quality minimal compression. 
	 *   0.8 is a good value.
	 */
	private float jpgQuality;


	/**
	 *   A flag to indicate whether DHTML elements should be generated. 
	 */
	private boolean generateDHTML ;
	
	/**
	 *  The background color for the web pages
	 */
	private Color backgroundColor;
	
	/**
	 *  The color to be used for the fonts.
	 */
	private Color fontColor;

	/**
	 *  Creates and starts a Thread that writes the picture nodes from the specified
	 *  startNode to the target directory.
	 *
	 *
	 *  @param  htmlDirectory    	The directory that is supposed to receive the 
	 *				html files.
	 *  @param  picsPerRow		The numer of pictures desired side by side
	 *  @param  thumbnailWidth	The width the thumbnail may not exceed
	 *  @param  thumbnailHeight	The height the thumbnail may not exceed
	 *  @param  midresWidth		The width the midres picture may not exceed
	 *  @param  midresHeight	The height the midres picture may not exceed
	 *  @param  cellspacing		The space between the cells when more then 1
	 * 				picture is to be output side by side. Ususally 10.
	 *  @param  exportHighres	An indicator whether to copy the highres imgaes to the target dir structure
	 *  @param  linkToHighres	An indicator whether a link to the highres 
	 *				pictures should be build into the output html 
	 *  @param  jpgQuality		The Quality with which to compress the jpg images.
	 *  @param startNode		The node from which this is all to be built.
	 *  @param generateDHTML	Set to true if DHTML effects should be generated
	 *  @param backgroundColor	The background color for the web page.
	 *  @param fontColor		The color to be used for texts.
	 */
	public HtmlDistillerThread (File htmlDirectory, 
		int picsPerRow,
		int thumbnailWidth,
		int thumbnailHeight,
		int midresWidth,
		int midresHeight,
		int cellspacing,
		boolean exportHighres,
		boolean linkToHighres,
		float jpgQuality,
		SortableDefaultMutableTreeNode startNode,
		boolean generateDHTML,
		Color backgroundColor,
		Color fontColor ) {

		if ( exportHighres && linkToHighres )
			linkToHighres = false;   // if we are copying highres we will automatically link to them. If linkToHihgres were on this would make a mess of the html output.

		this.htmlDirectory = htmlDirectory;
		this.picsPerRow = picsPerRow;
		this.thumbnailWidth = thumbnailWidth;
		this.thumbnailHeight = thumbnailHeight;
		this.midresWidth = midresWidth;
		this.midresHeight = midresHeight;
		this.cellspacing = cellspacing;
		this.exportHighres = exportHighres;
		this.linkToHighres = linkToHighres;
		this.jpgQuality = jpgQuality;
		this.startNode = startNode;
		this.generateDHTML = generateDHTML;
		this.backgroundColor = backgroundColor;
		this.fontColor = fontColor;

		start();
	}
	

	
	/**
	 *  Method that is invoked by the thread to do things asynchroneousely.
	 */
	public void run() {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(4, 4, 4, 4);

		JPanel progPanel = new JPanel();
		progPanel.setBorder( BorderFactory.createEmptyBorder(5,5,5,5) );
		progPanel.setLayout( new GridBagLayout() );


		progressLabel = new JLabel();
		progressLabel.setPreferredSize(new Dimension(600,20));
		progressLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		c.gridx = 0; c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		progPanel.add( progressLabel, c );

		progBar = new JProgressBar(0, countNodes(startNode));
		progBar.setBorder( BorderFactory.createLineBorder( Color.gray, 1 ) );
		progBar.setStringPainted( true );
		progBar.setPreferredSize( new Dimension(140, 20) );
		progBar.setMaximumSize( new Dimension(240, 20) );
		progBar.setMinimumSize( new Dimension(140, 20) );
		progBar.setValue(0);
		c.gridy++;
		c.fill = GridBagConstraints.NONE;
		progPanel.add( progBar, c );	
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ButtonListener());
		cancelButton.setPreferredSize( Settings.defaultButtonDimension );
		cancelButton.setMaximumSize( Settings.defaultButtonDimension );
		cancelButton.setMinimumSize( Settings.defaultButtonDimension );

		c.gridx++;
		c.anchor = GridBagConstraints.EAST;
		progPanel.add( cancelButton, c );

		progressFrame = new JFrame( Settings.jpoResources.getString("HtmlDistillerThreadTitle") );
		progressFrame.getContentPane().add(progPanel);
		progressFrame.pack();
		progressFrame.show();
		progressFrame.setLocationRelativeTo ( Settings.anchorFrame );

		scp.setJpgQuality( jpgQuality );
		writeStylesheet( htmlDirectory );
		writeAsHtml ( startNode );

		if ( folderIconRequired ) {
			try {
				InputStream inStream  = Settings.cl.getResource("jpo/images/icon_folder.gif").openStream();
				FileOutputStream outStream  = new FileOutputStream(new File(htmlDirectory, "jpo_folder_icon.gif"));
			
				BufferedInputStream bin = new BufferedInputStream( inStream );
				BufferedOutputStream bout = new BufferedOutputStream( outStream );
			
				int count;
				byte data[] = new byte[ 2048 ];;
				while (( count = bin.read( data, 0, 2048 )) != -1)
					bout.write(data, 0, count);

				inStream.close();
				outStream.close();
			} catch  (IOException x) {
				x.printStackTrace();
				System.err.println( x.getMessage() );
				JOptionPane.showMessageDialog(
					Settings.anchorFrame, 
					"got an IOException copying icon_folder.gif\n" + x.getMessage(), 
					"IOExeption",
					JOptionPane.ERROR_MESSAGE);
			}
		}
		
		
		progressFrame.dispose();
		
	}
	

		 	
	/** 
	 *  This method writes out an HTML page with the small images alined next to each other.
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
	public void writeAsHtml ( SortableDefaultMutableTreeNode groupNode ) {
		try {
		//groupCounter++;
		progBar.setValue( progBar.getValue() + 1 );
		progBar.setString( Integer.toString( progBar.getValue() )
			+ "/"
			+ Integer.toString( progBar.getMaximum() ) );

		
		File groupFile;
		//if (groupCounter == 1) 
		if ( groupNode.equals( startNode ) )
			groupFile = new File ( htmlDirectory, "index.htm");
		else  {
			int hashCode = groupNode.hashCode();
			groupFile = new File ( htmlDirectory, "jpo_" + Integer.toString( hashCode ) + ".htm");
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(groupFile));			
		DescriptionsBuffer descriptionsBuffer = new DescriptionsBuffer( picsPerRow, out );
		
		progressLabel.setText("writing " + groupFile.toString() );
		// write header
		out.write("<HTML>\n<HEAD>\n\t<LINK rel=\"StyleSheet\" href=\"jpo.css\" type=\"text/css\" media=\"screen\">\n\t<TITLE>" + ((GroupInfo) groupNode.getUserObject()).getGroupName() + "</TITLE>\n</HEAD>");
		out.newLine();

		
		// write body
		out.write( "<BODY>" );
		out.newLine();

		out.write("<TABLE BORDER=0 ALIGN=CENTER CELLPADDING=0 CELLSPACING="
			+ Integer.toString(cellspacing) + " WIDTH="
			+ Integer.toString(picsPerRow * thumbnailWidth 
				+ (picsPerRow - 1) * cellspacing)
			+ ">");
		out.newLine();

		out.write("<TR><TD colspan=" + Integer.toString(picsPerRow) + ">");
		out.newLine();
		
		out.write("<H2>" + ((GroupInfo) groupNode.getUserObject()).getGroupName() + "</H2>");
		out.newLine();
		
		//link to parent
		if ( ! groupNode.equals( startNode ) ) {
			SortableDefaultMutableTreeNode parentNode = (SortableDefaultMutableTreeNode) groupNode.getParent();
			String parentLink = "jpo_" + Integer.toString( parentNode.hashCode() ) + ".htm";
			if ( parentNode.equals( startNode ) ) parentLink = "index.htm";
		
			out.write("<p>Up to: <a href=\""
				+ parentLink + "\">"
				+ parentNode.toString() + "</a>");
			out.newLine();
		}

		out.write("</TD><TR>");
		out.newLine();


		int childCount = groupNode.getChildCount();
		int childNumber = 1;
		Enumeration kids = groupNode.children();
		SortableDefaultMutableTreeNode n;
		
		while ( kids.hasMoreElements() && (! interrupt) ) {
			n = (SortableDefaultMutableTreeNode) kids.nextElement();
			if (n.getUserObject() instanceof GroupInfo) {
			
				out.write("<TD VALIGN=BOTTOM ALIGN=LEFT WIDTH=" 
					+ Integer.toString( thumbnailWidth ) 
					+ ">");
		
				out.write("<a href=\"jpo_" 
					+ Integer.toString( n.hashCode() )
					+ ".htm\">" 
				        +"<img border=0 src=\"jpo_folder_icon.gif\" width=32 height=27></a>");

		
				out.write("</TD>");			
				out.newLine();

				descriptionsBuffer.putCheckFlush( ((GroupInfo) n.getUserObject()).getGroupName() );



			
				/*if (picCounter > 0) flushDescriptions(out);
				out.write("</TR><TR><TD colspan=" 
					+ Integer.toString(picsPerRow)
					+ "><a href=group_" 
					+ Integer.toString(groupCounter)
					+ ".htm>" 
					+ ((GroupInfo) n.getUserObject()).getGroupName()
					+ "</a></TD>");
				out.newLine();*/
				
				// recursively call the method to output that group.
				
				writeAsHtml( n );
				folderIconRequired = true;

				// Increase PicCounter so that it starts on the left
				// of a new row.
				// picCounter =0;

			} else {
				writeHtmlPicture( n, out, groupFile, childNumber, childCount, descriptionsBuffer );
				progBar.setValue(progBar.getValue() + 1);
				progBar.setString( Integer.toString( progBar.getValue() )
					+ "/"
					+ Integer.toString( progBar.getMaximum() ) );
			}
			childNumber++;
		}


		out.write( "</TR>" );
		descriptionsBuffer.flushDescriptions();
		
		out.write("</TR><TR><TD colspan=" + Integer.toString(picsPerRow) + ">");
		out.newLine();
		out.write( Settings.jpoResources.getString("LinkToJpo") ); 
		out.newLine();
		out.write("</TD></TR></TABLE>");
		out.newLine();
		out.write( "</BODY></HTML>" );
		out.close();
		
			
		
		} catch  (IOException x) {
			x.printStackTrace();
			System.err.println( x.getMessage() );
			JOptionPane.showMessageDialog(
				Settings.anchorFrame, 
				"got an IOException??", 
				"IOExeption",
				JOptionPane.ERROR_MESSAGE);
		}
	
	}	




	/** 
	 *  Write html about a picture to the output.
	 *  @param	n	The node for which the HTML is to be written
	 *  @param	out	The opened output stream to which the thumbnail tags should be written
	 *  @param	groupFile	???
	 *  @param	childNumber	???
	 *  @param	childCount	???
	 *  @param	descriptionsBuffer	A buffer for the thumbnails page
	 *  @throws IOException if there was some sort of IO Error.
	 */
	private void writeHtmlPicture (
		SortableDefaultMutableTreeNode n, 
		BufferedWriter out, 
		File groupFile,
		int childNumber,
		int childCount,
		DescriptionsBuffer descriptionsBuffer ) 
		throws IOException {
		
		PictureInfo p = (PictureInfo) n.getUserObject();
		

		//Tools.log( Integer.toString( n.hashCode() ) + " - " + p.getDescription() );

		String fn = "jpo_" + Integer.toString( n.hashCode() );
		picsWroteCounter++;

		File lowresFilename = new File( htmlDirectory, fn + "_l.jpg" );
		File midresFilename = new File( htmlDirectory, fn + "_m.jpg" );
		File highresFilename = new File( htmlDirectory, fn + "_h.jpg" );
		
		
		// copy the picture to the target directory
		if ( exportHighres) {
			progressLabel.setText("copying picture " 
				+ p.getHighresLocation()
				+ " to " 
				+ highresFilename.toString());
			Tools.copyPicture ( p.getHighresURL(), highresFilename );
		}
	

		progressLabel.setText("testing size of thumbnail " + p.getLowresURL().toString());
		
		int wOrig = 0;
		int hOrig = 0;
		
		int w = 0;
		int h = 0;
		//if (new File(p.getLowresLocation()).exists()) {
		try {
			InputStream inputStream = p.getLowresURL().openStream();
			inputStream.close();
			scp.loadPictureImd( p.getLowresURL(), p.getRotation() );
			wOrig = scp.getOriginalWidth();
			hOrig = scp.getOriginalHeight();
			
		} catch (IOException x) {
			Tools.log("got an IO error on opening " + p.getLowresURL());
		}
		

		boolean loaded = false;
		if ((wOrig == thumbnailWidth) || (hOrig == thumbnailWidth) ) {
			progressLabel.setText("copying picture " 
				+ p.getLowresLocation()
				+ " to " 
				+ lowresFilename.toString() );
			Tools.copyPicture ( p.getLowresURL(), lowresFilename );
			w = wOrig;
			h = hOrig;
		} else {
			// it needs scaling
			progressLabel.setText("loading " + p.getHighresLocation());
			scp.loadPictureImd( p.getHighresURL(), p.getRotation() );
			
			if ( scp.getStatusCode() == ScalablePicture.ERROR ) {
				Tools.log( "HtmlDistillerThread.writeHtmlPicture: problem reading image using brokenThumbnailPicture instead");
				scp.loadPictureImd( Settings.cl.getResource( "jpo/images/broken_thumbnail.gif" ), 0f );
			}
			
			scp.setScaleSize( new Dimension(thumbnailWidth, thumbnailWidth) );
			progressLabel.setText("scaling " + p.getHighresLocation());
			scp.scalePicture();
			progressLabel.setText("writing " + lowresFilename.toString() );
			scp.writeScaledJpg( lowresFilename );
			w = scp.getScaledWidth();
			h = scp.getScaledHeight();
			loaded = true;
		}
			
			
		out.write("<TD VALIGN=BOTTOM ALIGN=CENTER WIDTH=" 
			+ Integer.toString( thumbnailWidth ) 
			+ ">");
		
		out.write("<a href=\"" + fn + ".htm\">"
		        +"<img border=0 src=\"" + fn  + "_l.jpg"
			+ "\" width= " + Integer.toString( w ) 
			+ " height= " + Integer.toString( h ) + ">"
			+"</a>");

		
		out.write("</TD>");			
		out.newLine();

		descriptionsBuffer.putCheckFlush( p.getDescription() );

		
		
		// scale the midres picture
		if ( ! loaded ) {
			progressLabel.setText("loading " + p.getHighresLocation());
			scp.loadPictureImd( p.getHighresURL(), p.getRotation() );
		}
		scp.setScaleSize( new Dimension(midresWidth, midresHeight) );
		progressLabel.setText("scaling " + p.getHighresLocation());
		scp.scalePicture();
		progressLabel.setText("writing " + midresFilename.toString() );
		scp.writeScaledJpg( midresFilename );
		w = scp.getScaledWidth();
		h = scp.getScaledHeight();



		
		File midresHtmlFile = new File ( htmlDirectory, fn + ".htm");
		BufferedWriter midresHtmlWriter = new BufferedWriter( new FileWriter( midresHtmlFile ) );
		String groupDescription =
			( (SortableDefaultMutableTreeNode) n.getParent() ).getUserObject().toString();
			
		midresHtmlWriter.write("<HTML>\n<HEAD>\n\t<LINK rel=\"StyleSheet\" href=\"jpo.css\" type=\"text/css\" media=\"screen\">\n\t<TITLE>" + groupDescription + "</TITLE>\n</HEAD>");
		midresHtmlWriter.newLine();
		midresHtmlWriter.write("<BODY onLoad=\"changetext(content[0])\"><CENTER>");
		midresHtmlWriter.newLine();
		midresHtmlWriter.write("<TABLE cellpadding=0 cellspacing=10>");
		midresHtmlWriter.write("<TR><TD colspan=2><CENTER><H2>" + groupDescription  + "</H2></CENTER></TD></TR>");
		midresHtmlWriter.newLine();
		midresHtmlWriter.newLine();
		midresHtmlWriter.write("<TR><TD align=\"CENTER\" valign=\"TOP\" width=" + Integer.toString( midresWidth ) + ">");
		String imgTag = "<IMG border=0 src=\"" + fn + "_m.jpg"
			+ "\" width= " + Integer.toString( w ) 
			+ " height= " + Integer.toString( h ) + ">";

		if ( linkToHighres ) 
			midresHtmlWriter.write( "<A HREF=\"" + p.getHighresLocation() + "\">" + imgTag + "</A>" );
		else if ( exportHighres ) 
			midresHtmlWriter.write( "<A HREF=\"" + fn + "_h.jpg\">" + imgTag + "</A>" );
		else 
			midresHtmlWriter.write( imgTag );		
		midresHtmlWriter.newLine();
		midresHtmlWriter.write( "<P>" + p.getDescription() );
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
		midresHtmlWriter.write( "<TD align=\"CENTER\" valign=\"TOP\" width=" + Integer.toString( matrixWidth) + ">" );
		midresHtmlWriter.write( "Picture " + Integer.toString( childNumber ) + " of " + Integer.toString( childCount ) );
		midresHtmlWriter.newLine();
		midresHtmlWriter.write( "<TABLE cellpadding=3 cellspacing=1 border=1>" );
		midresHtmlWriter.newLine();
		StringBuffer dhtmlArray = new StringBuffer( "content[0]='"
								+ font 
								+ "<p><b>Picture</b> "
								+ Integer.toString( childNumber ) 
								+ " of " + Integer.toString( childCount )
								+ ":<p>"
								+ "<b>Description:</b><br>"
								+ p.getDescription().replaceAll("\'", "\\\\'").replaceAll("\n", " ")
								+ "<p>" );
		if ( p.getCreationTime() != "" )
			dhtmlArray.append( "<b>Date:</b><br>" 
						+ p.getCreationTime().replaceAll("\'", "\\\\'").replaceAll("\n", " ")
						+ "<p>" );
						
		if ( p.getPhotographer() != "" )
			dhtmlArray.append( "<b>Photographer:</b><br>"
						+ p.getPhotographer().replaceAll("\'", "\\\\'").replaceAll("\n", " ")
						+ "<br>" );
		if ( p.getComment() != "" )
			dhtmlArray.append( "<b>Comment:</b><br>"
						+ p.getComment().replaceAll("\'", "\\\\'").replaceAll("\n", " ")
						+ "<br>" );
		if ( p.getFilmReference() != "" )
			dhtmlArray.append( "<b>Film Reference:</b><br>"
						+ p.getFilmReference().replaceAll("\'", "\\\\'").replaceAll("\n", " ")
						+ "<br>" );
		if ( p.getCopyrightHolder() != "" )
			dhtmlArray.append( "<b>Copyright Holder:</b><br>"
						+ p.getCopyrightHolder().replaceAll("\'", "\\\\'").replaceAll("\n", " ")
						+ "<br>" );

		dhtmlArray.append( "</font>'\n" );
		

		int startNumber = (int) Math.floor( (childNumber - indexBeforeCurrent -1) / indexPerRow ) * indexPerRow + 1;
		if ( startNumber < 1 ) 
			startNumber = 1;
		int endNumber = startNumber + indexToShow;
		if ( endNumber > childCount ) 
			endNumber = childCount + 1;
		endNumber = endNumber + indexPerRow - ( childCount % indexPerRow );
		
		for ( int i = startNumber; i < endNumber; i++ ) {
			if ( (i - 1) % indexPerRow == 0 ) {
				midresHtmlWriter.write( "<TR>" );
				midresHtmlWriter.newLine();
			}
			midresHtmlWriter.write( "<TD>" );
			if ( i <= childCount ) {
				nde = (SortableDefaultMutableTreeNode) n.getParent().getChildAt( i-1 );
				int hashCode = nde.hashCode();
				midresHtmlWriter.write( "<A HREF=\"" 
					+ "jpo_" + Integer.toString( hashCode ) + ".htm\"");
				if ( generateDHTML ) {
					midresHtmlWriter.write( " onMouseover=\"changetext(content["
						+ Integer.toString(i)
						+ "])\" onMouseout=\"changetext(content[0])\")");
					dhtmlArray.append( "content[" 
						+ Integer.toString(i) 
						+ "]='" );
						
					if ( nde.getUserObject() instanceof PictureInfo ) {
						dhtmlArray.append( font
							+ "<p>Picture "
							+ Integer.toString(i) 
							+ "/"
							+ Integer.toString( childCount ) 
							+ ":<p>"
							+ "<center><img src=\"" 
							+ "jpo_" + Integer.toString( hashCode ) + "_l.jpg\""
							+  " width="
							+  Integer.toString( matrixWidth-10 )
							+  ">"
							+ "</center><p><i>"
							+ ((PictureInfo) nde.getUserObject() ).getDescription().replaceAll("\'", "\\\\'").replaceAll("\n", " ")
							+ "</i></font>'\n" );
					} else {
						dhtmlArray.append( font
							+ "<p>Item "
							+ Integer.toString(i) 
							+ "/"
							+ Integer.toString( childCount ) 
							+ ":<p>"
							+ "<i><b>Group:</b><br>"
							+ ((GroupInfo) nde.getUserObject() ).getGroupName().replaceAll("\'", "\\\\'").replaceAll("\n", " ")
							+ "</i></font>'\n" );
					}
				}
				midresHtmlWriter.write( ">" );
				if ( i == childNumber ) 
					midresHtmlWriter.write( "<B>" );
				midresHtmlWriter.write( Integer.toString( i ) );
				if ( i == childNumber ) 
					midresHtmlWriter.write( "</B>" );
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
			int hashCode = ((SortableDefaultMutableTreeNode) n.getParent() ).getChildAt( childNumber-2 ).hashCode();
			midresHtmlWriter.write( "<A HREF=\"" + "jpo_" + Integer.toString( hashCode ) + ".htm\">Previous</A>" );
			midresHtmlWriter.write( "&nbsp" );
		}
		if ( linkToHighres ) {
			midresHtmlWriter.write( "<A HREF=\"" + p.getHighresLocation() + "\">Highres</A>" );
			midresHtmlWriter.write( "&nbsp" );
		} else if ( exportHighres ) {
			int hashCode = ((SortableDefaultMutableTreeNode) n.getParent() ).getChildAt( childNumber-1 ).hashCode();
			midresHtmlWriter.write( "<A HREF=\"jpo_" + Integer.toString( hashCode ) + "_h.jpg\">Highres</A>" );
			midresHtmlWriter.write( "&nbsp" );
		}
		if ( childNumber != childCount ) {
			int hashCode = ((SortableDefaultMutableTreeNode) n.getParent() ).getChildAt( childNumber ).hashCode();
			midresHtmlWriter.write( "<A HREF=\"" + "jpo_" + Integer.toString( hashCode ) + ".htm\">Next</A>" );
			midresHtmlWriter.newLine();
		}
		midresHtmlWriter.newLine();
		midresHtmlWriter.write( "<P>" + Settings.jpoResources.getString("LinkToJpo") ); 
		midresHtmlWriter.newLine();
		
		if ( generateDHTML ) {
			midresHtmlWriter.write( "<ilayer id=\"d1\" width=" + Integer.toString( matrixWidth) + " height=200 visibility=\"hide\">" );
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "<layer id=\"d2\" width=" + Integer.toString( matrixWidth) + " height=200>" );
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

		if ( generateDHTML ) {
			midresHtmlWriter.write( "<script> ");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "<!-- ");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "/* Textual Tooltip Script- (c) Dynamic Drive (www.dynamicdrive.com) For full source code, installation instructions, 100's more DHTML scripts, and Terms Of Use, visit dynamicdrive.com */ ");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "var content=new Array() ");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( dhtmlArray.toString() );
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "function regenerate(){ window.location.reload() } ");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "function regenerate2(){ if (document.layers){ appear()");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "setTimeout(\"window.onresize=regenerate\",450) } }");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "function changetext(whichcontent){");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "if (document.all||document.getElementById){");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "cross_el=document.getElementById? document.getElementById(\"descriptions\"):document.all.descriptions");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "cross_el.innerHTML=whichcontent");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "}");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "else if (document.layers){");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "document.d1.document.d2.document.write(whichcontent)");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "document.d1.document.d2.document.close()");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "}");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "}");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "function appear(){ document.d1.visibility='show' }");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "window.onload=regenerate2");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "//-->");
			midresHtmlWriter.newLine();
			midresHtmlWriter.write( "</script>");
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
		String [] descriptions;
		
		
		/**
		 *  Creates a Description buffer with the indicated number of columns.
		 *  @param	columns	The number of columns being generated
		 *  @param	out	The Thumbnail page
		 */
		DescriptionsBuffer ( int columns, BufferedWriter out ) {
			this.columns = columns;
			this.out = out;
			descriptions = new String[picsPerRow];
		}
		
		
		
		/**
		
		 *  Adds the supplied string to the buffer.
		 *  @param description	The string to be added
		 */
		public void put( String description ) {
			descriptions[ picCounter ] = description;
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
		public void putCheckFlush ( String description ) throws IOException {
			put ( description );
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
				out.write("</TR>");
				flushDescriptions();
				out.write("<TR>");

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
			out.write("<TR>");
			out.newLine();
			
			for (int i=0; i < columns; i++) {
				if ( descriptions[i] != null ) {
					out.write("<TD VALIGN=TOP WIDTH="
						+ Integer.toString(thumbnailWidth) 
						+ ">");
		
					out.write(descriptions[i]);
			
					out.write("</TD>");
					out.newLine();
					descriptions[i] = null;
				}
			}
			picCounter = 0;
			out.write("</TR>");
		}


	}	




	/**
	 *  Returns the the total number of nodes belonging to the indicated node.
	 *  @param startNode	The node from which the count shall begin.
	 *  @return The number of Nodes.
	 */
	public static int countNodes(SortableDefaultMutableTreeNode startNode) {
		int count = 1;

		Enumeration kids = startNode.children();
		SortableDefaultMutableTreeNode n;
		
		while (kids.hasMoreElements()) {
			n = (SortableDefaultMutableTreeNode) kids.nextElement();
			if (n.getChildCount() > 0) 
				count += countNodes(n);
			else
				count++;
		}
		return count;
	}



	
	
	/**
	 *  writes the collection.dtd file to the target directory.
	 *  This file war written manually and added to the jar.
	 */
	
	public void writeStylesheet ( File directory ) {
		try {
			//ClassLoader cl = this.getClass().getClassLoader();
			//InputStream in  = cl.getResource( "jpo/jpo.css" ).openStream();
			FileOutputStream out  = new FileOutputStream(new File(directory, "jpo.css"));
			
			OutputStreamWriter osw = new OutputStreamWriter( out );
			//BufferedInputStream bin = new BufferedInputStream(in);
			BufferedWriter bout = new BufferedWriter( osw );
			
			bout.write("BODY {background-color: " + Tools.getColor( backgroundColor ) + ";\n"); 
			bout.write("	  font-family: Verdana, Arial, Helvetica, sans-serif;\n");
			bout.write("	  color: " + Tools.getColor( fontColor ) + ";}\n");
			bout.write("H2   {color: " + Tools.getColor( fontColor ) + ";}\n");
			
			/*int c;
		
			while (( c = bin.read()) != -1)
				out.write(c);
			
			in.close();*/
			bout.close();
			osw.close();
			out.close();
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
				Settings.anchorFrame, 
				Settings.jpoResources.getString("CssCopyError") + e.getMessage(), 
				Settings.jpoResources.getString("genericWarning"), 
				JOptionPane.ERROR_MESSAGE);

		}
	}





	/**
	 *  A button listener for the cancel button on the progress frame.
	 */
	private class ButtonListener implements ActionListener {
		/**
		 *  Traps the event.
		 *  @param evt  The event.
		 */
		public void actionPerformed(ActionEvent evt) {
			progressLabel.setText( Settings.jpoResources.getString("htmlDistillerInterrupt") );
			interrupt = true;
		}
	}
	
		
}
