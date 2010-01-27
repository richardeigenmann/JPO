package jpo.dataModel;

/*
PictureInfoChangeEvent.java:  This event holds information about how the picture changed

Copyright (C) 2002 - 2009  Richard Eigenmann.
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
 *  Objects of this class have details about what was changed in a PictureInfo 
 *  object. Several things may have changed in a single event.
 *
 * @see PictureInfo
 */
public class GroupInfoChangeEvent {

    /**
     *  a reference to the GroupInfo firing off the event
     */
    private GroupInfo gi;


    /**
     * Constructor for the PictureInforChangeEvent
     *
     * @param gi
     */
    public GroupInfoChangeEvent( GroupInfo gi ) {
        this.gi = gi;
    }


    /**
     *  returns the GroupInfo object that created the event
     *
     * @return the GroupInfo object that changed
     */
    public GroupInfo getGroupeInfo() {
        return gi;
    }


    /**
     * toString method that returns the descrition of the group
     *
     * @return The description of the group
     */
    @Override
    public String toString() {
        return "This is a GroupInfoChangeEvent from " + gi.toString();
    }

//-----------------
    /**
     *  indicates whether the description was changed.
     */
    private boolean descriptionChanged = false;


    /**
     *  sets the event to reflect that the description changed
     **/
    public void setGroupNameChanged() {
        descriptionChanged = true;
    }


    /**
     *  returns whether the description was changed
     *
     * @return true if the description has changes, false if not
     */
    public boolean getGroupNameChanged() {
        return descriptionChanged;
    }

//-----------------
    /**
     *  indicates whether the lowresLocation description was changed.
     */
    private boolean lowresLocationChanged = false;


    /**
     *  sets the event to reflect that the lowres Location changed
     **/
    public void setLowresLocationChanged() {
        lowresLocationChanged = true;
    }


    /**
     *  returns whether the lowres Location was changed
     *
     * @return true if the lowres location changed, false if not
     */
    public boolean getLowresLocationChanged() {
        return lowresLocationChanged;
    }

    //-----------------
    /**
     *  indicates whether the thumbnail was changed.
     */
    private boolean thumbnailChanged = false;


    /**
     *  sets the event to reflect that the thumbnail changed
     **/
    public void setThumbnailChanged() {
        thumbnailChanged = true;
    }


    /**
     *  returns whether the thumbnail was changed
     *
     * @return ture or false
     */
    public boolean getThumbnailChanged() {
        return thumbnailChanged;
    }

//-----------------
    /**
     *  indicates whether the node was selected
     */
    private boolean wasSelected = false;


    /**
     *  sets the event to reflect that the selection was set
     **/
    public void setWasSelected() {
        wasSelected = true;
    }


    /**
     *  returns whether the event was about the selection status being set
     *
     * @return true or false
     */
    public boolean getWasSelected() {
        return wasSelected;
    }

//-----------------
    /**
     *  indicates whether the node was unselected
     */
    private boolean wasUnselected = false;


    /**
     *  sets the event to reflect that the selection was removed
     **/
    public void setWasUnselected() {
        wasUnselected = true;
    }


    /**
     *  returns whether the selection was removed
     *
     * @return true if the selection was removed, false if not
     */
    public boolean getWasUnselected() {
        return wasUnselected;
    }
}
