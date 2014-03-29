package jpo.gui;

/*
AdvanceTimerInterface.java:  an interface that specifies that what 
methods the object must implement that should be the receiver of AdvanceTimer events

Copyright (C) 2002-2009  Richard Eigenmann.
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
 *  This interface defines what methods the implementing object must have if it wants  
 *  be the receiver of AdvanceTimer events.
 *  
 **/
 
public interface AdvanceTimerInterface {


	/**
	 *  This method is invoked on the target when a picture should be advanced.
	 */
	public void requestAdvance();
	
	
	/**
	 *  The implementing class must support this function which indicates whether it is ready 
	 *  for a picture advance. This is necessary to prevent race situations as it takes a while
	 *  for a large picture to load.
	 *  
	 *  @return   True if the next picture can be advanced, false if not.
	 */
	public boolean readyToAdvance();

}

