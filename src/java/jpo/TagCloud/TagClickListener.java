package jpo.TagCloud;

/*
CopyLocationsChangeListener.java:  Interface to notify that the Settings.copyLocations array has changed.

Copyright (C) 2009  Richard Eigenmann.
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
 * Interface to tell a listener that a tag was clicked
 * @author richi
 */
public interface TagClickListener {

    /**
     * This method is fired when the user clicks on a tag in the tag cloud
     * @param key The word that was clicked on
     */
    public void tagClicked( String key );
}
