package jpo;

/*
ScaleThread.java:  class that calls scalePicture methods of a ScalablePicture

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
 *  a Thread object that is called from a ScalablePicture 
 *  and calls it back on teh scalePicture method from a new Thread.
 */

public class ScaleThread extends Thread {


	/**
	 *  The reference to the scalablepicture on which to invoke the
	 *  thread.	
	 */
	private final ScalablePicture sclPic;


	/**
	 *  Constructor for the thread
	 *  @param sclPic The picture we are doing this for
	 */
	public ScaleThread ( ScalablePicture sclPic ) {
		this.sclPic = sclPic;
	}
		
	
	/**
	 *  method that is invoked by the thread to do 
	 *  things asynchroneousely
	 */
	public void run() {
		sclPic.scalePicture();
	}

}


