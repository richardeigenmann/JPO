package jpo.gui;

/*
ApplicationMenuInterface.java:  defines an interface for the main application menu

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
 *  This interface defines the method that an object must implement if it 
 *  wants to create an ApplicationJMenuBar.
 *  
 **/
 
public interface ApplicationMenuInterface {
	/**
	 *   Signals that the user wants to create a new empty collection.
	 */
	public void requestFileNew();


	/**
	 *   Signals that the user wants to add pictures to his collection.
	 */
	public void requestFileAdd();
	
	
	/**
	 *   Signals that the user wants to add pictures from the camera to his collection.
	 */
	public void requestFileAddFromCamera();


	/**
	 *   Signals that the user wants to load a collection file.
	 */
	public void requestFileLoad();


	/**
	 *  Signals that the user wants to load a recently opened file. The parameter i 
	 *  indicates which file to open in the Settings.recentCollections array.
	 *
	 *  @param 	i	the index in the {@link jpo.dataModel.Settings#recentCollections} array
	 *			indicating the file to load.
	 */
	public void requestOpenRecent( int i );
	

	/**
	 *   Signals that the user wants to save a collection file.
	 */
	public void requestFileSave();


	/**
	 *   Signals that the user wants to save a collection file under a new name.
	 */
	public void requestFileSaveAs();


	/**
	 *   Signals that the user wants to leave the application.
	 */
	public void requestExit();


	/**
	 *   Signals that the user wants to search the collection.
	 */
	public void openFindDialog();


	/**
	 *   Signals that the user wants to set up a camera
	 */
	public void requestEditCameras();


	/**
	 *   Signals that the user wants to reconcile pictures in a 
	 *   directory with those in his collection.
	 */
	public void requestCheckDirectories();


	/**
	 *   Signals that the user wants to see the Collection Properties dialog.
	 */
	public void requestCollectionProperties();


	/**
	 *   Signals that the user wants to have the Collection Integrity checked.
	 */
	public void requestCheckIntegrity();


	/**
	 *   Signals that the user wants to edit the settings of the application.
	 */
	public void requestEditSettings();


	/**
	 *   Signals that the user wants to see a random slideshow
	 */
	public void performSlideshow();

}

