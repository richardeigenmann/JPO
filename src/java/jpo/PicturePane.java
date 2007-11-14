package jpo;

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.Point;
import javax.swing.*;
import javax.swing.event.*;
import java.text.*;
import java.util.Vector;
import java.util.Enumeration;


/*
PicturePane.java:  a component that can display an image

Copyright (C) 2002-2007 Richard Eigenmann.
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
 *   The job of this extended JComponent is to load, scale and display a 
 *   picture while 
 *   providing mouse control over which area is being shown and at which magnification.
 *   It notifies the registered parent about status changes so that a description object can
 *   be updated. When the image has been rendered and displayed the legend of the image is 
 *   send to the parent with the ready status.<p>
 *
 *   The user can zoom in on a picture coordinate by clicking the left mouse button. The middle
 *   button scales the picture so that it fits in the available space and centres it there.
 *   The right mouse button zooms out.<p>
 *
 *   The image is centred on the component to the {@link #focusPoint} in the coordinate space 
 *   of the image. This translated using the {@link ScalablePicture#setScaleFactor( double )} to the coordinate 
 *   space of the JComponent<p>
 *
 *   <img src=../Mathematics.png border=0><p>
 *
 * 
 *   The {@link #showInfo} flag controls whether information about the picture is overlayed 
 *   on the image.
 */
public class PicturePane extends JComponent implements ScalablePictureListener {
	
	
	/**
	 *   The currently displayed ScalablePicture.
	 */
	public ScalablePicture sclPic = new ScalablePicture();
	
	
	/**
	 *  Flag that lets the object know if the mouse is in dragging mode.
	 */	
	private boolean Dragging = false;
	
	
	/**
	 *  Flag that lets this JComponent know if the picture is to be fitted into the available space 
	 *  when the threads return the scaled picture.
	 */
	private boolean centerWhenScaled;


	/**
	 *   the point inside the picture that will be put at the middle of the screen. 
	 *   the coordinates are in x,y in the coordinate space of the picture.
	 */
	 private Point focusPoint = new Point();


	/**
	 *  used in dragging to find out how much the mouse has moved from the last time
	 */
	private int last_x, last_y;


	/**
	 *  The legend of the picture. Is sent to the listener when the image is ready.
	 */
	private String legend = null;
	
	
	/**
	 *   location of the info texts if shown
	 */
	private static final Point infoPoint = new Point(15, 15);
	
	
	/**
	 *   line spacing for the info text that can be superimposed on the picture
	 */
	private static final int lineSpacing = 12;
	
	/**
	 *   line spacing for the info text that can be superimposed on the picture
	 */
	private static final int tabstop = 90;
	
	/**
	 *   Font for the info if shown.
	 */
	//private static final Font infoFont =  new Font("Arial", Font.PLAIN, 10);
	private static final Font infoFont =  Font.decode( Settings.jpoResources.getString("PicturePaneInfoFont") );
	


	
	
	/**
	 *  Color for the info overly
	 */
	private static final Color infoFontColor = Color.white;
	
	
	
	/**
	 *  This object is a reference to an Exif Info object that tries to keep tabs on the 
	 *  information in the image.
	 */
	 
	private ExifInfo ei = new ExifInfo();
	
	
	
	
	
	/**
	 *  class to format the scale
	 */
	 
	private DecimalFormat twoDecimalFormatter = new DecimalFormat("###0.00");
	 
	 

	/**
	 *   Constructor
	 **/

	public PicturePane() {

		// register an interest in mouse events
		Listener MouseListener = new Listener();
		addMouseListener( MouseListener );
		addMouseMotionListener( MouseListener );


		// make graphics faster
		this.setDoubleBuffered( false );


		sclPic.addStatusListener( this );
		if ( Settings.pictureViewerFastScale ) 
			sclPic.setFastScale();
		else
			sclPic.setQualityScale();

		this.addComponentListener(new ComponentAdapter() {
		        public void componentResized(ComponentEvent event) {
				zoomToFit();
		        }
		});


		//twoDecimalFormatter = new DecimalFormat("###0.00");
		setFont(infoFont);


	}



	/**
	 *  brings up the indicated picture on the display.
	 *  @param pi  The PicutreInfo object that should be displayed
	 */
	public void setPicture( PictureInfo pi ) {
		URL pictureURL;
		String description;
		double rotation = 0;
		try {
			pictureURL = pi.getHighresURL();
			description = pi.getDescription();
			rotation = pi.getRotation();
		} catch ( MalformedURLException x ) {
			Tools.log("PicturePane.changePicture: MarformedURLException trapped on: " + pi.getHighresLocation() + "\nReason: " + x.getMessage());
			return;
		}
		setPicture( pictureURL, description, rotation );
	}



	/**
	 *  brings up the indicated picture on the display.
	 *  @param filenameURL  The URL of the picture to display
	 *  @param legendParam	The description of the picture
	 *  @param rotation  The rotation that should be applied
	 */
	public void setPicture( URL filenameURL, String legendParam, double rotation ) {
		legend = legendParam;
		centerWhenScaled = true;
		sclPic.setScaleSize(getSize());
		
		sclPic.stopLoadingExcept( filenameURL );
		sclPic.loadAndScalePictureInThread( filenameURL, Thread.MAX_PRIORITY, rotation );
		ei.setUrl( filenameURL );
		ei.decodeExifTags();
	}




	public void setDragging(boolean parameter) {
		Dragging = parameter;
	}









	/////////////////////////
	// Zooming Methods     //
	/////////////////////////


	/**
	 * Multiplies the scale factor so that paint() method scales the
	 * image larger. This method calls
	 * {@link ScalablePicture#createScaledPictureInThread(int)} which in
	 * turn will tell this object by means of the status update that
	 * the image is ready and should be repainted.
	 */
	public void zoomIn() {
		double OldScaleFactor = sclPic.getScaleFactor();
		double NewScaleFactor = OldScaleFactor * 1.5;

		// If scaling goes from scale down to scale up, set ScaleFactor to exactly 1
		if ((OldScaleFactor < 1) && (NewScaleFactor > 1))
			NewScaleFactor = 1;


		// Check if the picture would get to large and cause the system to "hang"		
		if ((sclPic.getOriginalWidth() * sclPic.getScaleFactor() < Settings.maximumPictureSize) 
		&&  (sclPic.getOriginalHeight() * sclPic.getScaleFactor() < Settings.maximumPictureSize) ) {
			sclPic.setScaleFactor(NewScaleFactor);
			sclPic.createScaledPictureInThread( Thread.MAX_PRIORITY );
		}
	}



	/**
	 *  method that zooms out on the image leaving the center where it is.
	 *  This method calls
	 * {@link ScalablePicture#createScaledPictureInThread(int)} which in
	 * turn will tell this oject by means of the status update that
	 * the image is ready and should be repainted.
	 */
	public void zoomOut() {
		sclPic.setScaleFactor(sclPic.getScaleFactor() / 1.5);
		sclPic.createScaledPictureInThread( Thread.MAX_PRIORITY );
	}



	/**
	 *  this method sets the desired scaled size of the ScalablePicture 
	 *  to the size of the JPanel and fires off a createScaledPictureInThread 
	 *  request if the ScalablePicture has been loaded or is ready.
	 *
 	 *  @see ScalablePicture#createScaledPictureInThread(int)
	 *
	 */
	public void zoomToFit() {
		sclPic.setScaleSize( getSize() );
		// prevent useless rescale events when the picture is not ready
		if ( sclPic.getStatusCode() == sclPic.LOADED 
		  || sclPic.getStatusCode() == sclPic.READY ) {
			sclPic.createScaledPictureInThread( Thread.MAX_PRIORITY );
		}
	}

	/**
	 *  method that zooms the image to 100%.
	 *  This method calls
	 * {@link ScalablePicture#createScaledPictureInThread(int)} which in
	 * turn will tell this object by means of the status update that
	 * the image is ready and should be repainted.
	 */
	public void zoomFull() {
		sclPic.setScaleFactor( 1);
		sclPic.createScaledPictureInThread( Thread.MAX_PRIORITY );
	}



	///////////////////////////////////////////////////////////////
	// Scrolling Methods                                         //
	///////////////////////////////////////////////////////////////


	/**
	 * Set image to center of panel by putting the coordinates of the middle of the original image into the
	 * Center to X and Center to Y varaibles by invoking the setCenterLoaction method. 
	 * This method
	 * calls <code>repaint()</code> directly since no time consuming image operations need to take place.
	 */
	public void centerImage() {
		if (sclPic.getOriginalImage() != null) {
			setCenterLocation((int) sclPic.getOriginalWidth() / 2, (int) sclPic.getOriginalHeight() / 2);
			repaint();
		}
       	}



	/**
	 *  method that moves the image up by 10% of the pixels shown on the screen.
	 * This method
	 * calls <code>repaint()</code> directly since no time consuming image operations need to take place.
	 *  <p><img src=../scrollUp.png border=0><p>
	 *  @see #scrollUp()
	 *  @see #scrollDown()
	 *  @see #scrollLeft()
	 *  @see #scrollRight()
	 *
	 */
	public void scrollUp() {
		// if the bottom edge of the picture is visible, do not scroll
		if (((sclPic.getOriginalHeight() - focusPoint.y) * sclPic.getScaleFactor()) + getSize().height /2 > getSize().height) {
			focusPoint.y = focusPoint.y + (int) (getSize().height * 0.1 / sclPic.getScaleFactor()) ;
			repaint();
		} else  {
			Tools.log ("PicturePane.scrollUp rejected because bottom of picture is already showing.");
		}
	}
	
	
	/**
	 *  method that moves the image down by 10% of the pixels shown on the screen.
	 * This method
	 * calls <code>repaint()</code> directly since no time consuming image operations need to take place.
	 *  <p><img src=../scrollDown.png border=0><p>
	 *  @see #scrollUp()
	 *  @see #scrollDown()
	 *  @see #scrollLeft()
	 *  @see #scrollRight()
	 */
	public void scrollDown() {
		if (getSize().height / 2 - focusPoint.y * sclPic.getScaleFactor() < 0) {
			focusPoint.y = focusPoint.y - (int) (getSize().height * 0.1 / sclPic.getScaleFactor()) ;
			repaint();
		} else {
			Tools.log ("PicturePane.scrollDown rejected because top edge is aready visible");
		}
	}


	/**
	 *  method that moves the image left by 10% of the pixels shown on the screen. 
	 * This method
	 * calls <code>repaint()</code> directly since no time consuming image operations need to take place.
	 *  works just like {@link #scrollUp()}.
	 *  @see #scrollUp()
	 *  @see #scrollDown()
	 *  @see #scrollLeft()
	 *  @see #scrollRight()
	 */
	public void scrollLeft() {
		// if the bottom edge of the picture is visible, do not scroll
		if (((sclPic.getOriginalWidth() - focusPoint.x) * sclPic.getScaleFactor()) + getSize().width /2 > getSize().width) {
			focusPoint.x = focusPoint.x + (int) (getSize().width * 0.1 / sclPic.getScaleFactor()) ;
			repaint();
		} else {
			Tools.log("PicturePane.scrollLeft rejected because right edge of picture is already showing.");
		}
	}

	
	
	/**
	 *  method that moves the image right by 10% of the pixels shown on the screen. 
	 * This method
	 * calls <code>repaint()</code> directly since no time consuming image operations need to take place.
	 *  works just liks {@link #scrollDown()}.
	 *  @see #scrollUp()
	 *  @see #scrollDown()
	 *  @see #scrollLeft()
	 *  @see #scrollRight()
	  */
	public void scrollRight() {
		if (getSize().width / 2 - focusPoint.x * sclPic.getScaleFactor() < 0) {
			focusPoint.x = focusPoint.x - (int) (getSize().width * 0.1 / sclPic.getScaleFactor()) ;
			repaint();
		} else {
			Tools.log ("PicturePane.scrollRight rejected because left edge is aready visible");
		}
	}



	/**
	 *  method to set the center of the image to the true coordinates in the picture but doesn't call <code>repaint()</code>
	 **/
	public void setCenterLocation(int Xparameter, int Yparameter) {
		focusPoint.setLocation(Xparameter, Yparameter);
	}






	/**
	 *   we are overriding the default paintComponent method, grabbing the Graphics 
	 *   handle and doing our own drawing here. Essentially this method draws a large
	 *   black rectangle. A drawRenderedImage is then painted doing an affine transformation
	 *   on the scaled image to position it so the the desired point is in the middle of the 
	 *   Graphics object. The picture is not scaled here because this is a slow operation
	 *   and only needs to be done once, while moving the image is something the user is
	 *   likely to do more often.
	 */
	public void paintComponent(Graphics g) {
		int WindowWidth = getSize().width;
		int WindowHeight = getSize().height;

		if (Dragging == false) {  //otherwise it's already a move Cursor
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
		}

		if ( sclPic.getScaledPicture() != null ) {
			Graphics2D g2d = (Graphics2D)g;

			int X_Offset = (int) ((double) (WindowWidth / 2) - (focusPoint.x * sclPic.getScaleFactor()));
			int Y_Offset = (int) ((double) (WindowHeight / 2) - (focusPoint.y * sclPic.getScaleFactor()));

			// clear damaged component area
		 	Rectangle clipBounds = g2d.getClipBounds();
			g2d.setColor( getBackground() ); 
			g2d.fillRect(clipBounds.x, 
				     clipBounds.y,
			             clipBounds.width, 
			      	     clipBounds.height);
				     

			g2d.drawRenderedImage(sclPic.getScaledPicture(), AffineTransform.getTranslateInstance((int) X_Offset, (int) Y_Offset));

			g2d.setColor( infoFontColor );
			switch ( showInfo ) {
				case DISPLAY_NONE:
					break;
				case DISPLAY_PHOTOGRAPHIC:
					g2d.drawString( Settings.jpoResources.getString("ExifInfoCamera")
						, infoPoint.x
						, infoPoint.y + ( 0 * lineSpacing ));
					g2d.drawString( ei.camera
						, infoPoint.x + tabstop
						, infoPoint.y + ( 0 * lineSpacing ));
					g2d.drawString( Settings.jpoResources.getString("ExifInfoLens")
						, infoPoint.x
						, infoPoint.y + ( 1 * lineSpacing ));
					g2d.drawString( ei.lens
						, infoPoint.x + tabstop
						, infoPoint.y + ( 1 * lineSpacing ));
					g2d.drawString( Settings.jpoResources.getString("ExifInfoShutterSpeed")
						, infoPoint.x
						, infoPoint.y + ( 2 * lineSpacing ));
					g2d.drawString( ei.shutterSpeed
						, infoPoint.x + tabstop
						, infoPoint.y + ( 2 * lineSpacing ));
					g2d.drawString( Settings.jpoResources.getString("ExifInfoAperture")
						, infoPoint.x
						, infoPoint.y + ( 3 * lineSpacing ));
					g2d.drawString( ei.aperture
						, infoPoint.x + tabstop
						, infoPoint.y + ( 3 * lineSpacing ));
					g2d.drawString( Settings.jpoResources.getString("ExifInfoFocalLength")
						, infoPoint.x
						, infoPoint.y + ( 4 * lineSpacing ));
					g2d.drawString( ei.focalLength
						, infoPoint.x + tabstop
						, infoPoint.y + ( 4 * lineSpacing ));
					g2d.drawString( Settings.jpoResources.getString("ExifInfoISO")
						, infoPoint.x
						, infoPoint.y + ( 5 * lineSpacing ));
					g2d.drawString( ei.iso
						, infoPoint.x + tabstop
						, infoPoint.y + ( 5 * lineSpacing ));
					g2d.drawString( Settings.jpoResources.getString( "ExifInfoTimeStamp" )
						, infoPoint.x
						, infoPoint.y + ( 6 * lineSpacing ));
					g2d.drawString( ei.dateTime
						, infoPoint.x + tabstop
						, infoPoint.y + ( 6 * lineSpacing ));
					break;
				case DISPLAY_APPLICATION:
					g2d.drawString( legend
						, infoPoint.x
						, infoPoint.y + ( 0 * lineSpacing ));
					g2d.drawString( Settings.jpoResources.getString( "PicturePaneSize" )
						+ Integer.toString(sclPic.getOriginalWidth())
						+ " x " 
						+ Integer.toString(sclPic.getOriginalHeight()) 
						+ Settings.jpoResources.getString( "PicturePaneMidpoint" ) 
						+ Integer.toString(focusPoint.x)
						+ " x "
						+ Integer.toString(focusPoint.y) 
						+ " Scale: " 
						+ twoDecimalFormatter.format(sclPic.getScaleFactor())
						,infoPoint.x, infoPoint.y + ( 1 * lineSpacing ) );
					g2d.drawString("File: " + sclPic.getFilename()
						, infoPoint.x
						, infoPoint.y + ( 2 * lineSpacing ) );
					g2d.drawString( Settings.jpoResources.getString( "PicturePaneLoadTime" ) 
						+ twoDecimalFormatter.format( sclPic.getSourcePicture().loadTime / 1000F )
						+ Settings.jpoResources.getString( "PicturePaneSeconds" ) 
						, infoPoint.x
						, infoPoint.y + ( 3 * lineSpacing ) );
					g2d.drawString( Settings.jpoResources.getString( "PicturePaneFreeMemory" ) 
						+ Tools.freeMemory()
						, infoPoint.x
						, infoPoint.y + ( 4 * lineSpacing ) );
					break;
			}
		} else {
			// paint a black square
			g.setClip(0, 0, WindowWidth, WindowHeight);
			g.setColor(Color.black);
			g.fillRect(0,0,WindowWidth,WindowHeight);
		}

		if (Dragging == false) {  //otherwise a move Cursor and should remain
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); 
		}
	}





	/**
	 *  This class deals with the mouse events. Is built so that the picture can be dragged if
	 *  the mouse button is pressed and the mouse moved. If the left button is clicked the picture is 
	 *  zoomed in, middle resets to full screen, right zooms out.
	 */
	class Listener extends MouseInputAdapter {
	
		/**
		 *   This method traps the mouse events and changes the scale and position of the displayed
		 *   picture.
		 */
		public void mouseClicked( MouseEvent e ) {
			//Tools.log("PicturePane.mouseClicked");
			if ( e.getButton() == 3 ) {
				// Right Mousebutton zooms out
				centerWhenScaled = false;
				zoomOut();
			} else if ( e.getButton() == 2 ) {
				// Middle Mousebutton resets
				zoomToFit();
				centerWhenScaled = true;
			} else if ( e.getButton() == 1 ) {
				// Left Mousebutton zooms in on selected spot
				// Convert screen coordinates of the mouse click into true
				// coordinates on the picture:

		      		int WindowWidth = getSize().width;
				int WindowHeight = getSize().height;

				int X_Offset = e.getX() - (int) (WindowWidth / 2);
				int Y_Offset = e.getY() - (int) (WindowHeight / 2);
				
				setCenterLocation(
					focusPoint.x + (int) (X_Offset / sclPic.getScaleFactor()), 
					focusPoint.y + (int) (Y_Offset / sclPic.getScaleFactor())
				);
				centerWhenScaled = false;
				zoomIn ();
	 		}
		}
			
		

		/**
		 * method that is invoked when the
		 * user drags the mouse with a button pressed. Moves the picture around
		 */
		public void mouseDragged(MouseEvent e) {
			if ( ! Dragging ) {
				// Switch into dragging mode and record current coordinates
				//Tools.log("PicturePane.mouseDragged: Switching to drag mode.");
				last_x = e.getX(); 
				last_y = e.getY();
				
				Dragging = true;
				setCursor( new Cursor( Cursor.MOVE_CURSOR ) );

			} else {
				// was already dragging
				int x = e.getX(), y = e.getY();

				focusPoint.setLocation( (int) ((double) focusPoint.x + ((last_x - x) / sclPic.getScaleFactor())),
							(int) ((double) focusPoint.y + ((last_y - y) / sclPic.getScaleFactor())));
				last_x = x; last_y = y;

				Dragging = true;
				repaint();
			}
			centerWhenScaled = false;
		}
		
		/**
		 * method that is invoked when the
		 * user releases the mouse button.
		 */
		public void mouseReleased( MouseEvent e ) {
			//Tools.log("PicturePane.mouseReleased.");
			if ( Dragging ) {
				//Dragging has ended
				Dragging = false;
				setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
			} 
		}		
		
	}  //end class Listener




	/**
	 *  Constant to indicate that no information should be overlaid on the picture
	 */
	public static final int DISPLAY_NONE = 0;
	
	/**
	 *  Constant to indicate that photographic information should be displayed on the picture
	 */
	public static final int DISPLAY_PHOTOGRAPHIC = DISPLAY_NONE + 1;
	
	/**
	 *  Constant to indicate that Application related information should be displayed on the picture
	 */
	public static final int DISPLAY_APPLICATION = DISPLAY_PHOTOGRAPHIC + 1;


	/**
	 *  Code that determines what info is to be displayed over the picture.
	 */
	private int showInfo = DISPLAY_NONE;


	/**
	 *  This function cycles to the next info display. The first is DISPLAY_NONE, DISPLAY_PHOTOGRAPHIC 
	 *  and DISPLAY_APPLICATION
	 **/
	public void cylceInfoDisplay() {
		switch ( showInfo ) {
			case DISPLAY_NONE:
				showInfo = DISPLAY_PHOTOGRAPHIC;
				break;
			case DISPLAY_PHOTOGRAPHIC:
				showInfo = DISPLAY_APPLICATION;
				break;
			case DISPLAY_APPLICATION:
				showInfo = DISPLAY_NONE;
				break;
		}
		repaint();
	}
	



	/**
	 *  method that gets invoked from the ScalablePicture object to notify of status changes.
	 *  The ScalablePicture goes through several statuses: UNINITIALISED, GARBAGE_COLLECTION, 
	 *  LOADING, SCALING, READY, ERROR.<p>
	 *  Each status is passed to the listener upon receipt.<p>
	 *  When the ScalablePicture signals that it is READY the legend of the picture is sent 
	 *  to the listener. The method {@link #centerImage} is called and a repaint is requested.
	 */
	public void scalableStatusChange(int pictureStatusCode, String pictureStatusMessage) {
		//Tools.log("PicturePane.scalableStatusChange: got a status change: " + pictureStatusMessage);

		if ( pictureStatusCode == ScalablePicture.READY ) {
			//Tools.log("PicturePane.scalableStatusChange: a READY status");
			//pictureStatusMessage = legend;
			pictureStatusMessage = Settings.jpoResources.getString("PicturePaneReadyStatus");
			if ( centerWhenScaled ) {
				//Tools.log("PicturePane.scalableStatusChange: centering image");
				centerImage();
			}
			//Tools.log("PicturePane.scalableStatusChange: forcing Panel repaint");
			repaint();
		}

		Enumeration e = picturePaneListeners.elements();
		while ( e.hasMoreElements() ) {
			( (ScalablePictureListener) e.nextElement() )
				.scalableStatusChange( pictureStatusCode, pictureStatusMessage );
		}
	}



	/**
	 *  pass messages about progress onto the PictureViewer for updating of the progress bar
	 */
	public void sourceLoadProgressNotification( int statusCode, int percentage ) {
		Enumeration e = picturePaneListeners.elements();
		while ( e.hasMoreElements() ) {
			( (ScalablePictureListener) e.nextElement() )
				.sourceLoadProgressNotification( statusCode, percentage );
		}
	}
	
	

	/** 
	 *  This Vector hold references to objects that would like to
	 *  receive notifications about what is going on with the ScalablePicture
	 *  being displayed in this PicturePane. These objects
	 *  must implement the ScalablePictureListener interface.
	 */
	protected Vector picturePaneListeners = new Vector();



	/**
	 *  method to register the listening object of the status events
	 */
	public void addStatusListener ( ScalablePictureListener listener) {
		picturePaneListeners.add( listener );
	}


	/**
	 *  method to register the listening object of the status events
	 */
	public void removeStatusListener ( ScalablePictureListener listener ) {
		picturePaneListeners.remove( listener );
	}



	/**
	 *  method that returns a handle to the scalable picture that this component is displaying
	 */
	public ScalablePicture getScalablePicture() {
		return sclPic;
	}


	/**
	 *  tidy up when closing down object
	 */
	public void finalize() {
		//Tools.log("PicturePane.finalize: called");
		sclPic = null;
	}

}
