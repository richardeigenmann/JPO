package jpo.gui;

import java.awt.*;

/*
FrameShower.java:  helper class to implement Multithreading in Swing Tech Tip
http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1

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
 *  As per http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
 *  Multithreading in Swing Tech Tip
 */
public class FrameShower 
       	implements Runnable {
		
        final private Frame frame;
	final private Component anchor;

	/**
	 *  As per http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
	 *  Multithreading in Swing Tech Tip
         *
         * @param frame
         */
       	public FrameShower( Frame frame ) {
       		this( frame, null);
       	}

	/**
	 *  As per http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
	 *  Multithreading in Swing Tech Tip
         *
         * @param frame
         * @param anchor 
         */
       	public FrameShower( Frame frame, Component anchor ) {
       		this.frame = frame;
		this.anchor = anchor;
       	}


        /**
         *
         */
        public void run() {
		frame.pack();
		if ( anchor != null ) {
			frame.setLocationRelativeTo( anchor );
		}
		frame.setVisible( true );
		
	}
}
   


