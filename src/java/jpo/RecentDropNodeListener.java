package jpo;

/*
RecentDropNodeInterface.java:  Interface to notify that array Settings.recentDropNodes has changed

Copyright (C) 2004  Richard Eigenmann.
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
 *  This interface allows the Settings object to notify any interested object that the 
 *  array Settings.recentDropNodes has changed. The PicturePopupMenu can use this to 
 *  update the menu entries.
 *  
 **/
 
public interface RecentDropNodeListener {

	/**
	 *  Inform the listener that the Settings.recentDropNodes array of recent drop targets has changed
	 */
	public void recentDropNodesChanged ();


}

