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
ExifViewerJFrame.java: GUI to display the Exif tags extracted with Drew's programs

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
public class ExifViewerJFrame extends JFrame implements ActionListener {

	/**
	 *  button to start the export
	 **/
	private JButton okJButton = new JButton ( Settings.jpoResources.getString("genericOKText") );



	/** 
    	 *   Constructor to create the GUI
	 */
	public ExifViewerJFrame( SortableDefaultMutableTreeNode pictureNode ) {
		this( ((PictureInfo) pictureNode.getUserObject()).getHighresURLOrNull() );
	}



	/** 
    	 *   Constructor to create the GUI
	 */
	public ExifViewerJFrame( PictureInfo pi ) {
		this( pi.getHighresURLOrNull() );
	}



	/** 
    	 *   Constructor to create the GUI
	 */
	public ExifViewerJFrame( URL pictureUrl ) {
		JTextArea exifTagsJTextArea = new JTextArea(); // out here so we can use it in the catch stmt
		exifTagsJTextArea.setWrapStyleWord( true );
		exifTagsJTextArea.setLineWrap( false );
		exifTagsJTextArea.setEditable( true );
		exifTagsJTextArea.setRows ( 20 );
		exifTagsJTextArea.setColumns ( 40 );
		
		// stop undesired scrolling in the window when doing append
		NonFocussedCaret dumbCaret = new NonFocussedCaret(); 
		exifTagsJTextArea.setCaret( dumbCaret ); 
		


		JScrollPane jScrollPane = new JScrollPane( exifTagsJTextArea );//, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
		jScrollPane.setWheelScrollingEnabled( true );
										
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
			}
	        });  


		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(4, 4, 4, 4);

		JPanel contentJPanel = new JPanel();
		contentJPanel.setLayout(new GridBagLayout());

		constraints.gridx = 0; constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.weightx = 1;
		constraints.weighty = 1;
		contentJPanel.add( jScrollPane, constraints );


		okJButton.setPreferredSize( Settings.defaultButtonDimension );
	        okJButton.setMinimumSize( Settings.defaultButtonDimension );
        	okJButton.setMaximumSize( Settings.defaultButtonDimension );
		okJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
		okJButton.setDefaultCapable( true );
		this.getRootPane().setDefaultButton ( okJButton );
	        okJButton.addActionListener( this );

		constraints.gridy++;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.SOUTH;
		contentJPanel.add( okJButton, constraints );

				
		this.setTitle( Settings.jpoResources.getString("ExifTitle") );
		this.setLocationRelativeTo ( Settings.anchorFrame );
		this.getContentPane().add( contentJPanel );
		this.pack();
		this.setVisible( true );

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
			
			Iterator directories = metadata.getDirectoryIterator();
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
						Tools.log ("ExifViewerJFrame: problem wiht tag: " + x.getMessage());
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




	/** 
	 *  method that analyses the user initiated action and performs what the user requested
	 **/
	public void actionPerformed(ActionEvent e) {
	 	if (e.getSource() == okJButton) {
			setVisible ( false );
			dispose ();
		} 
	}




    
}
