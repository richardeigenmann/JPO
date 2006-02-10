package jpo;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.io.*;
import java.net.*;
import com.sun.image.codec.jpeg.*;
import com.drew.metadata.*;
import com.drew.metadata.exif.*;
import com.drew.metadata.iptc.*;
import com.drew.imaging.jpeg.*;
import com.drew.imaging.jpeg.JpegSegmentReader.*;

/*
ExifViewerJScrollPane.java: GUI to display the Exif tags extracted with Drew's programs

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
 * GUI to display the Exif tags extracted with Drew's programs
 *
 * @author  Richard Eigenmann
 */
public class ExifViewerJScrollPane extends JScrollPane {


	/** 
    	 *   Constructor to create the GUI
	 */
	public ExifViewerJScrollPane( SortableDefaultMutableTreeNode pictureNode ) {
		this( ((PictureInfo) pictureNode.getUserObject()).getHighresURLOrNull() );
	}



	/** 
    	 *   Constructor to create the GUI
	 */
	public ExifViewerJScrollPane( PictureInfo pi ) {
		this( pi.getHighresURLOrNull() );
	}



	/** 
    	 *   Constructor to create the GUI
	 */
	public ExifViewerJScrollPane( URL pictureUrl ) {
		JTextArea exifTagsJTextArea = new JTextArea(); // out here so we can use it in the catch stmt
		exifTagsJTextArea.setWrapStyleWord( true );
		exifTagsJTextArea.setLineWrap( false );
		exifTagsJTextArea.setEditable( true );
		exifTagsJTextArea.setRows ( 17 );
		exifTagsJTextArea.setColumns ( 35 );
		
		// stop undesired scrolling in the window when doing append
		NonFocussedCaret dumbCaret = new NonFocussedCaret(); 
		exifTagsJTextArea.setCaret( dumbCaret ); 
		


		this.setViewportView( exifTagsJTextArea );//, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
		this.setWheelScrollingEnabled( true );
		//this.setPreferredSize( new Dimension (350, 400) );
										

		try {

			InputStream highresStream = pictureUrl.openStream();
			JpegSegmentReader reader = new JpegSegmentReader( new BufferedInputStream( highresStream ) );
			byte[] exifSegment = reader.readSegment( JpegSegmentReader.SEGMENT_APP1 );
			//InputStream highresStream2 = pictureUrl.openStream();
			//JpegSegmentReader reader2 = new JpegSegmentReader( new BufferedInputStream( highresStream2 ) );
			byte[] iptcSegment = reader.readSegment( JpegSegmentReader.SEGMENT_APPD );
			
			Metadata metadata = new Metadata();
			new ExifReader( exifSegment ).extract( metadata );
			new IptcReader( iptcSegment ).extract( metadata );
			
			exifTagsJTextArea.append( Settings.jpoResources.getString("ExifTitle") );
			Iterator directories = metadata.getDirectoryIterator();
			if ( ! directories.hasNext() ) {
				exifTagsJTextArea.append( Settings.jpoResources.getString("noExifTags") );
			}
			while ( directories.hasNext() ) {
				Directory directory = (Directory) directories.next();
				
				Iterator tags = directory.getTagIterator();
				while ( tags.hasNext() ) {
					Tag tag = (Tag) tags.next();
					try {
						exifTagsJTextArea.append( tag.getTagName()
							+ ": " 
							+ tag.getDescription()
							+ "\n");	
					} catch ( MetadataException x ) {
						exifTagsJTextArea.append( "Problem with tag: "
							+ tag.toString()
							+ "\n");
						Tools.log ("ExifViewerJScrollPane: problem wiht tag: " + x.getMessage());
					}	
				}
			}
		} catch ( MalformedURLException x ) { 
			exifTagsJTextArea.append( "MalformedURLException: " + x.getMessage() );
		} catch ( IOException x ) {
			exifTagsJTextArea.append( "IOException: " + x.getMessage() );
		} catch ( JpegProcessingException x ) { 
			x.printStackTrace();
			exifTagsJTextArea.append( "No EXIF header found\n" + x.getMessage() );
		}
		
	}





    
}
