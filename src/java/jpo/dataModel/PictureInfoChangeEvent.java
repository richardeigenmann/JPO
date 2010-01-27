package jpo.dataModel;

/*
PictureInfoChangeEvent.java:  This event holds information about how the picture changed

Copyright (C) 2002 - 2010  Richard Eigenmann.
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
public class PictureInfoChangeEvent {

    /**
     *  a reference to the PictureInfo firing off the event
     */
    private PictureInfo pi;


    /**
     *  Constructor for the PictureInforChangeEvent
     *
     * @param pi
     */
    public PictureInfoChangeEvent( PictureInfo pi ) {
        this.pi = pi;
    }


    /**
     *  returns the PictureInfo object that created the event
     *
     * @return
     */
    public PictureInfo getPictureInfo() {
        return pi;
    }


    /**
     * This overriden toString reports the status of all flags in the PictureInfoChangeEvent
     *
     * @return a verbose one line description of the flags in the PictureInforChangeEvent
     */
    @Override
    public String toString() {
        return String.format( "PictureInfoChangeEvent from PictureInfo %s, descriptionChanged: %b, highresLocationChanged %b, lowresLocationChanged: %b, checksumChanged: %b, thumbnailChanged %b, creationTimeChanged %b, filmReferenceChanged: %b, rotationChanged %b, commentChanged: %b, photographerChanged: %b, copyrightHolderChanged: %b, categoryAssignmentsChanged: %b, wasSelected: %b, wasUnselected: %b, wasMailSelected: %b, wasMailUnselected: %b",
                pi.toString(), getDescriptionChanged(), getHighresLocationChanged(),
                getLowresLocationChanged(), getChecksumChanged(), getThumbnailChanged(),
                getCreationTimeChanged(), getFilmReferenceChanged(),
                getRotationChanged(), getCommentChanged(), getPhotographerChanged(),
                getCopyrightHolderChanged(), getCategoryAssignmentsChanged(),
                getWasSelected(), getWasUnselected(), getWasMailSelected(),
                getWasMailUnselected() );
    }

//-----------------
    /**
     *  indicates whether the description was changed.
     */
    private boolean descriptionChanged = false;


    /**
     *  sets the event to reflect that the description changed
     **/
    public void setDescriptionChanged() {
        descriptionChanged = true;
    }


    /**
     *  returns whether the description was changed
     *
     * @return
     */
    public boolean getDescriptionChanged() {
        return descriptionChanged;
    }

//-----------------
    /**
     *  indicates whether the highresLocation was changed.
     */
    private boolean highresLocationChanged = false;


    /**
     *  sets the event to reflect that the highres Location changed
     **/
    public void setHighresLocationChanged() {
        highresLocationChanged = true;
    }


    /**
     *  returns whether the highres Location was changed
     *
     * @return
     */
    public boolean getHighresLocationChanged() {
        return highresLocationChanged;
    }

//-----------------
    /**
     *  indicates whether the checksum was changed.
     */
    private boolean checksumChanged = false;


    /**
     *  sets the event to reflect that the checksum changed
     **/
    public void setChecksumChanged() {
        checksumChanged = true;
    }


    /**
     *  returns whether the checksum was changed
     *
     * @return
     */
    public boolean getChecksumChanged() {
        return checksumChanged;
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
     * @return
     */
    public boolean getLowresLocationChanged() {
        return lowresLocationChanged;
    }

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
     * @return
     */
    public boolean getThumbnailChanged() {
        return thumbnailChanged;
    }

//-----------------
    /**
     *  indicates whether the lowresLocation description was changed.
     */
    private boolean creationTimeChanged = false;


    /**
     *  sets the event to reflect that the lowres Location changed
     **/
    public void setCreationTimeChanged() {
        creationTimeChanged = true;
    }


    /**
     *  returns whether the lowres Location was changed
     *
     * @return
     */
    public boolean getCreationTimeChanged() {
        return creationTimeChanged;
    }

//-----------------
    /**
     *  indicates whether the film reference was changed.
     */
    private boolean filmReferenceChanged = false;


    /**
     *  sets the event to reflect that the film reference changed
     **/
    public void setFilmReferenceChanged() {
        filmReferenceChanged = true;
    }


    /**
     *  returns whether the film reference was changed
     *
     * @return
     */
    public boolean getFilmReferenceChanged() {
        return filmReferenceChanged;
    }

//-----------------
    /**
     *  indicates whether the rotation was changed.
     */
    private boolean rotationChanged = false;


    /**
     *  sets the event to reflect that the rotation changed
     **/
    public void setRotationChanged() {
        rotationChanged = true;
    }


    /**
     *  returns whether the rotation changed
     *
     * @return
     */
    public boolean getRotationChanged() {
        return rotationChanged;
    }

//-----------------
    /**
     *  indicates whether the comment was changed.
     */
    private boolean commentChanged = false;


    /**
     *  sets the event to reflect that the comment changed
     **/
    public void setCommentChanged() {
        commentChanged = true;
    }


    /**
     *  returns whether the comment changed
     *
     * @return
     */
    public boolean getCommentChanged() {
        return commentChanged;
    }

//-----------------
    /**
     *  indicates whether the photographer was changed.
     */
    private boolean photographerChanged = false;


    /**
     *  sets the event to reflect that the photographer changed
     **/
    public void setPhotographerChanged() {
        photographerChanged = true;
    }


    /**
     *  returns whether the photographer changed
     *
     * @return
     */
    public boolean getPhotographerChanged() {
        return photographerChanged;
    }

//-----------------
    /**
     *  indicates whether the copyright holder was changed.
     */
    private boolean copyrightHolderChanged = false;


    /**
     *  sets the event to reflect that the copyright holder changed
     **/
    public void setCopyrightHolderChanged() {
        copyrightHolderChanged = true;
    }


    /**
     *  returns whether the copyright holder was changed
     *
     * @return
     */
    public boolean getCopyrightHolderChanged() {
        return copyrightHolderChanged;
    }

//-----------------
    /**
     *  indicates whether the category assignments were changed changed.
     */
    private boolean categoryAssignmentsChanged = false;


    /**
     *  sets the event to reflect that the copyright holder changed
     **/
    public void setCategoryAssignmentsChanged() {
        categoryAssignmentsChanged = true;
    }


    /**
     *  returns whether the copyright holder was changed
     *
     * @return
     */
    public boolean getCategoryAssignmentsChanged() {
        return categoryAssignmentsChanged;
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
     * @return
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
     * @return
     */
    public boolean getWasUnselected() {
        return wasUnselected;
    }

//-----------------
    /**
     *  indicates whether the node was mailSelected
     */
    private boolean wasMailSelected = false;


    /**
     *  sets the event to reflect that the selection was set
     **/
    public void setWasMailSelected() {
        wasMailSelected = true;
    }


    /**
     *  returns whether the event was about the selection status being set
     *
     * @return
     */
    public boolean getWasMailSelected() {
        return wasMailSelected;
    }

//-----------------
    /**
     *  indicates whether the node was mailUnselected
     */
    private boolean wasMailUnselected = false;


    /**
     *  sets the event to reflect that the selection was removed
     **/
    public void setWasMailUnselected() {
        wasMailUnselected = true;
    }


    /**
     *  returns whether the selection was removed
     *
     * @return
     */
    public boolean getWasMailUnselected() {
        return wasMailUnselected;
    }
}
