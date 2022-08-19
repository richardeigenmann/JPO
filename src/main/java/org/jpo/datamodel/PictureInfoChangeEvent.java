package org.jpo.datamodel;

/*
Copyright (C) 2002-2022  Richard Eigenmann.
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version. This program is distributed 
in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Without even the implied warranty of MERCHANTABILITY or FITNESS
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
    private final PictureInfo pictureInfo;


    /**
     *  Constructor for the PictureInforChangeEvent
     *
     * @param pictureInfo Picture object
     */
    public PictureInfoChangeEvent( final PictureInfo pictureInfo ) {
        this.pictureInfo = pictureInfo;
    }


    /**
     *  returns the PictureInfo object that created the event
     *
     * @return the pictureInfo object
     */
    public PictureInfo getPictureInfo() {
        return pictureInfo;
    }


    /**
     * This overriden toString reports the status of all flags in the PictureInfoChangeEvent
     *
     * @return a verbose one line description of the flags in the PictureInforChangeEvent
     */
    @Override
    public String toString() {
        return String.format( "PictureInfoChangeEvent from PictureInfo %s, descriptionChanged: %b, highresLocationChanged %b, checksumChanged: %b, thumbnailChanged %b, creationTimeChanged %b, filmReferenceChanged: %b, rotationChanged %b, commentChanged: %b, photographerChanged: %b, copyrightHolderChanged: %b, latLngChanged: %b, categoryAssignmentsChanged: %b, wasSelected: %b, wasUnselected: %b, wasMailSelected: %b, wasMailUnselected: %b",
                pictureInfo.toString(), getDescriptionChanged(), getHighresLocationChanged(),
                getChecksumChanged(), getThumbnailChanged(),
                getCreationTimeChanged(), getFilmReferenceChanged(),
                getRotationChanged(), getCommentChanged(), getPhotographerChanged(),
                getCopyrightHolderChanged(), getLatLngChanged(),
                getCategoryAssignmentsChanged(),
                getWasSelected(), getWasUnselected(), getWasMailSelected(),
                getWasMailUnselected() );
    }

//-----------------
    /**
     *  indicates whether the description was changed.
     */
    private boolean descriptionChanged;  // default is false


    /**
     *  sets the event to reflect that the description changed
     **/
    public void setDescriptionChanged() {
        descriptionChanged = true;
    }


    /**
     *  returns whether the description was changed
     *
     * @return true if the description was changed
     */
    public boolean getDescriptionChanged() {
        return descriptionChanged;
    }

//-----------------
    /**
     *  indicates whether the highresLocation was changed.
     */
    private boolean highresLocationChanged;  // default is false


    /**
     *  sets the event to reflect that the highres Location changed
     **/
    public void setHighresLocationChanged() {
        highresLocationChanged = true;
    }


    /**
     *  returns whether the highres Location was changed
     *
     * @return true if the highres location was changed
     */
    public boolean getHighresLocationChanged() {
        return highresLocationChanged;
    }

//-----------------
    /**
     *  indicates whether the checksum was changed.
     */
    @Deprecated
    private boolean checksumChanged;  // default is false


    /**
     *  sets the event to reflect that the checksum changed
     **/
    @Deprecated
    public void setChecksumChanged() {
        checksumChanged = true;
    }


    /**
     *  returns whether the checksum was changed
     *
     * @return true if the checksum was changed
     */
    @Deprecated
    public boolean getChecksumChanged() {
        return checksumChanged;
    }

    /**
     * indicates that the sha256 was changed.
     */
    private boolean sha256Changed;  // default is false


    /**
     *  sets the event to reflect that the sha256 changed
     **/
    public void setSha256Changed() {
        sha256Changed = true;
    }


    /**
     *  returns whether the sha256 was changed
     *
     * @return true if the sha256 was changed
     */
    public boolean getSha256Changed() {
        return sha256Changed;
    }


    /**
     *  indicates whether the thumbnail was changed.
     */
    private boolean thumbnailChanged;  // default is false


    /**
     *  sets the event to reflect that the thumbnail changed
     **/
    public void setThumbnailChanged() {
        thumbnailChanged = true;
    }


    /**
     *  returns whether the thumbnail was changed
     *
     * @return true if changed, false if not
     */
    public boolean getThumbnailChanged() {
        return thumbnailChanged;
    }

//-----------------
    /**
     *  indicates whether the creation time was changed.
     */
    private boolean creationTimeChanged;  // default is false


    /**
     *  sets the event to reflect that the creation time was changed
     **/
    public void setCreationTimeChanged() {
        creationTimeChanged = true;
    }


    /**
     *  returns whether the creation time was changed was changed
     *
     * @return true if changed, false if not
     */
    public boolean getCreationTimeChanged() {
        return creationTimeChanged;
    }

//-----------------
    /**
     *  indicates whether the film reference was changed.
     */
    private boolean filmReferenceChanged;  // default is false


    /**
     *  sets the event to reflect that the film reference changed
     **/
    public void setFilmReferenceChanged() {
        filmReferenceChanged = true;
    }


    /**
     *  returns whether the film reference was changed
     *
     * @return true if changed, false if not
     */
    public boolean getFilmReferenceChanged() {
        return filmReferenceChanged;
    }

//-----------------
    /**
     *  indicates whether the rotation was changed.
     */
    private boolean rotationChanged;  // default is false


    /**
     *  sets the event to reflect that the rotation changed
     **/
    public void setRotationChanged() {
        rotationChanged = true;
    }


    /**
     *  returns whether the rotation changed
     *
     * @return true if changed, false if not
     */
    public boolean getRotationChanged() {
        return rotationChanged;
    }

//-----------------
    /**
     *  indicates whether the comment was changed.
     */
    private boolean commentChanged;  // default is false


    /**
     *  sets the event to reflect that the comment changed
     **/
    public void setCommentChanged() {
        commentChanged = true;
    }


    /**
     *  returns whether the comment changed
     *
     * @return true if changed, false if not
     */
    public boolean getCommentChanged() {
        return commentChanged;
    }

//-----------------
    /**
     *  indicates whether the photographer was changed.
     */
    private boolean photographerChanged;  // default is false


    /**
     *  sets the event to reflect that the photographer changed
     **/
    public void setPhotographerChanged() {
        photographerChanged = true;
    }


    /**
     *  returns whether the photographer changed
     *
     * @return true if the photographer changed
     */
    public boolean getPhotographerChanged() {
        return photographerChanged;
    }

//-----------------
    /**
     *  indicates whether the copyright holder was changed.
     */
    private boolean copyrightHolderChanged;  // default is false


    /**
     *  sets the event to reflect that the copyright holder changed
     **/
    public void setCopyrightHolderChanged() {
        copyrightHolderChanged = true;
    }


    /**
     *  returns whether the copyright holder was changed
     *
     * @return true if changed, false if not
     */
    public boolean getCopyrightHolderChanged() {
        return copyrightHolderChanged;
    }

    //-----------------
    /**
     *  indicates whether the Latitude or Longitude was changed.
     */
    private boolean latLngChanged;  // default is false


    /**
     *  sets the event to reflect that the Latitude or Longitude was changed
     **/
    public void setLatLngChanged() {
        latLngChanged = true;
    }


    /**
     *  returns whether the latitude or longitude was changed
     *
     * @return true if the lat or lng was changed
     */
    public boolean getLatLngChanged() {
        return latLngChanged;
    }

//-----------------
    /**
     *  indicates whether the category assignments were changed changed.
     */
    private boolean categoryAssignmentsChanged;  // default is false


    /**
     *  sets the event to reflect that the copyright holder changed
     **/
    public void setCategoryAssignmentsChanged() {
        categoryAssignmentsChanged = true;
    }


    /**
     *  returns whether the category assignment was changed
     *
     * @return true if changed, false if not
     */
    public boolean getCategoryAssignmentsChanged() {
        return categoryAssignmentsChanged;
    }

//-----------------
    /**
     *  indicates whether the node was selected
     */
    private boolean wasSelected;  // default is false


    /**
     *  sets the event to reflect that the selection was set
     **/
    public void setWasSelected() {
        wasSelected = true;
    }


    /**
     *  returns whether the event was about the selection status being set
     *
     * @return true if selected, false if not
     */
    public boolean getWasSelected() {
        return wasSelected;
    }

//-----------------
    /**
     *  indicates whether the node was unselected
     */
    private boolean wasUnselected;


    /**
     *  sets the event to reflect that the selection was removed
     **/
    public void setWasUnselected() {
        wasUnselected = true;
    }


    /**
     *  returns whether the selection was removed
     *
     * @return true if the selection was removed
     */
    public boolean getWasUnselected() {
        return wasUnselected;
    }

//-----------------
    /**
     *  indicates whether the node was mailSelected
     */
    private boolean wasMailSelected;  // default is false


    /**
     *  sets the event to reflect that the selection was set
     **/
    public void setWasMailSelected() {
        wasMailSelected = true;
    }


    /**
     *  returns whether the event was about the selection status being set
     *
     * @return true if the mail was selected
     */
    public boolean getWasMailSelected() {
        return wasMailSelected;
    }

//-----------------
    /**
     *  indicates whether the node was mailUnselected
     */
    private boolean wasMailUnselected;


    /**
     *  sets the event to reflect that the selection was removed
     **/
    public void setWasMailUnselected() {
        wasMailUnselected = true;
    }


    /**
     *  returns whether the mail selection was removed
     *
     * @return true if removed, false if not
     */
    public boolean getWasMailUnselected() {
        return wasMailUnselected;
    }
}
