package jpo;

/*
PictureInfoChangeEvent.java:  This event holds information about how the picture changed

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
	 **/			      
	public PictureInfoChangeEvent( PictureInfo pi ) {
		this.pi = pi;
	}

	/**
	 *  returns the PictureInfo object that created the event
	 */
	public PictureInfo getPictureInfo() {
		return pi;
	}



	/**
	 *   toString method that returns the descrition of the group
	 **/
        public String toString() {
		return "This is a PictureInfoChangeEvent from " + pi.toString();
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
	 */
	public boolean getHighresLocationChanged() {
		return highresLocationChanged;
	}


//-----------------


	/**
	 *  indicates whether the highresLocation was changed.
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
	 */
	public boolean getWasMailUnselected() {
		return wasMailUnselected;
	}




}
