package jpo;

import java.io.*;
import java.net.*;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.util.*;
import javax.swing.*;
import java.text.*;
import java.lang.Math.*;

/*
ConsolidateGroupThread.java:  class that consolidated the pictures of a group in one directory

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
 *  a class that moves all pictures of a group to a target directory
 */

public class ConsolidateGroupThread extends Thread {


	/**
	 *  temporary variable to hold the userObject from the node of the data model
	 */
	private Object o;


	/**
	 *  temporary variable to hold the group information from the user object of the node
	 */
	private GroupInfo g;


	/**
	 *  temporary variable to hold the picture information from the user object of the node
	 */
	private PictureInfo p;


	/** 
	 *  temp var
	 */
	private File oldFileParent;


	/** 
	 *  temp var
	 */
	private URL oldFileURL;

	/** 
	 *  temp var
	 */
	private String oldFile;


	/**
	 *  temporary node used in the Enumeration of the kids of the Group
	 */
	private SortableDefaultMutableTreeNode n;

	
	
	/** 
	 *  the directory where the pictures are to be moved to
	 */
	private File targetDirectory;

	
	/** 
	 *  the directory where the lowres pictures are to be moved to
	 */
	private File targetLowresDirectory;
	
	
	/**
	 *  the node to start from
	 */
	private SortableDefaultMutableTreeNode startNode;



	/**
	 *  flag that indicates that the subgroups should also be considered
	 */
	private boolean recurseGroups;


	/**
	 *   This object holds a reference to the progress GUI for the user.
	 */
	private ProgressGui progGui;
	
	/**
	 *  This flag says whether to consolidate Lowres images too
	 */
	private boolean moveLowres;
	
	

	/**
	 *  @param targetDirectory	Where we want the files moved to
	 *  @param startNode		The node from which this is all to be built.
	 *  @param recurseGroups	Flag indicating subgroups should be included
	 *  @param moveLowres
	 */
	public ConsolidateGroupThread ( File targetDirectory, SortableDefaultMutableTreeNode startNode, boolean recurseGroups, boolean moveLowres ) {
		this.targetDirectory = targetDirectory;
		this.startNode = startNode;
		this.recurseGroups = recurseGroups;
		this.moveLowres = moveLowres;
		if ( moveLowres ) {
			targetLowresDirectory = new File( targetDirectory, "/Lowres/" );
			targetLowresDirectory.mkdirs();
		}
	
		start();
	}

	
	
	/**
	 *  method that is invoked by the thread to do things asynchroneousely
	 */
	public void run() {
		progGui = new ProgressGui( Tools.countPictures( startNode, recurseGroups ),
			Settings.jpoResources.getString("ConsolitdateProgBarTitle"),
			Settings.jpoResources.getString("ConsolitdateProgBarDone") );
		enumerateGroup( startNode );
		progGui.switchToDoneMode();
	}
	


	/** 
	 *  recursively invoked method to report all groups.
	 *  
	 *  @return  True if ok, false if a problem occured.
	 */
	private boolean enumerateGroup ( SortableDefaultMutableTreeNode groupNode )  {
		g = (GroupInfo) groupNode.getUserObject();
		if ( moveLowres  && ( ! groupNode.isRoot() ) && ( ( ! g.getLowresLocation().equals("") ) ) ) {
			if ( ! moveLowresPicture ( groupNode ) ) {
				JOptionPane.showMessageDialog(
					Settings.anchorFrame, 
					Settings.jpoResources.getString("ConsolidateFailure"), 
					Settings.jpoResources.getString("genericError"), 
					JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
				
		Enumeration kids = groupNode.children();
		while ( kids.hasMoreElements() && ( ! progGui.interrupt ) ) {
			n = (SortableDefaultMutableTreeNode) kids.nextElement();
			if (n.getUserObject() instanceof GroupInfo) {
				if ( recurseGroups ) {
					if ( ! enumerateGroup(n) ) {
						// stop if there was a problem.
						return false;
					}
				}
			} else {
				if ( ! moveHighresPicture ( n ) ) {
					JOptionPane.showMessageDialog(
						Settings.anchorFrame, 
						Settings.jpoResources.getString("ConsolidateFailure"), 
						Settings.jpoResources.getString("genericError"), 
						JOptionPane.ERROR_MESSAGE);
				
					return false;
				}
				if ( moveLowres ) {
					if ( ! moveLowresPicture ( n ) ) {
						JOptionPane.showMessageDialog(
							Settings.anchorFrame, 
							Settings.jpoResources.getString("ConsolidateFailure"), 
							Settings.jpoResources.getString("genericError"), 
							JOptionPane.ERROR_MESSAGE);
						return false;
					}
				}
				progGui.progressIncrement();
			}
		}
		return true;
	}	


	 
	/** 
	 *   This method moves a highres file from an indicated SortableDefaultMutableTreeNode 's 
	 *   PictureInfo object to the target directory. It returns true if the move was successful or ignored
	 *   false if there was a problem
	 *
	 *   @return 	True if the move was successfull or False if it was not.
	 */
	private boolean moveHighresPicture ( SortableDefaultMutableTreeNode n ) {
		//Tools.log("ConsolidateGroupThread.moveHighresPicture: invoked");
		p = (PictureInfo) n.getUserObject();
		try {		
			if ( ! Tools.isUrlFile ( p.getHighresURL () ) ) {
				Tools.log ( "ConsolidateGroupThread.moveHighresPicture: Not a \"file:\" URL. Ignoring: " + p.getHighresLocation() );
				return true;
			}
			URL oldFile = p.getHighresURL ();
			URL newFile = Tools.inventPicURL( targetDirectory, p.getHighresFilename() );
			File oldFileParent = p.getHighresFile().getParentFile();
			
			if ( ( oldFileParent != null ) && ( oldFileParent.equals( targetDirectory) ) ) {
				Tools.log("ConsolidateGroupThread.moveHighresPicture: path is identical (" + oldFileParent.toString() + "==" + p.getHighresFile().getParentFile().toString() + ") . Not Moving Picture: " + p.getHighresLocation() );
				return true;
			}
			
			if ( Tools.movePicture ( oldFile, newFile ) ) {
				//p.setHighresLocation ( newFile.toString() );
				return true;
			} else {
				Tools.log("ConsolidateGroupThread.moveHighresPicture: failed to move " + oldFile.toString() + " to " + newFile.toString());
				return false;
			}
		} catch ( IOException x ) {
			Tools.log ( "Got an IOException: " + x + "\nAborting the move of this image.");
			return false;
		}
	}


	/** 
	 *   This method moves a highres file from an indicated SortableDefaultMutableTreeNode 's 
	 *   PictureInfo object to the target directory. It returns true if the move was successful or ignored
	 *   false if there was a problem
	 *
	 *   @return 	True if the move was successfull or False if it was not.
	 */
	private boolean moveLowresPicture ( SortableDefaultMutableTreeNode n ) {
		try {		
			o = n.getUserObject();
			if ( o instanceof GroupInfo ) {
				oldFileURL = ( (GroupInfo) o ).getLowresURL ();
				oldFile = ( (GroupInfo) o ).getLowresFilename();
				oldFileParent = ( (GroupInfo) o ).getLowresFile().getParentFile();
			} else {
				oldFileURL = ( (PictureInfo) o ).getLowresURL ();
				oldFile = ( (PictureInfo) o ).getLowresFilename();
				oldFileParent = ( (PictureInfo) o ).getLowresFile().getParentFile();
			}
		

			if ( ! Tools.isUrlFile ( oldFileURL ) ) {
				Tools.log ( "ConsolidateGroupThread.moveLowresPicture: Not a \"file:\" URL. Ignoring: " + oldFileURL.toString() );
				return true;
			}
			URL newFile = Tools.inventPicURL( targetLowresDirectory, oldFile );
			if ( ( oldFileParent != null ) && ( oldFileParent.equals( targetLowresDirectory ) ) ) {
				Tools.log("ConsolidateGroupThread.moveLowresPicture: path is identical " + oldFileParent.toString() + " not moving picture." );
				return true;
			}
			
			if ( Tools.movePicture ( oldFileURL, newFile ) ) {
				return true;
			} else {
				Tools.log("ConsolidateGroupThread.moveLowresPicture: failed to move " + oldFile.toString() + " to " + newFile.toString());
				return false;
			}
		} catch ( IOException x ) {
			Tools.log ( "ConsolidateGroupThread.moveLowresPicture: Got an IOException: " + x.getMessage() + " aborting the move of this image.");
			return false;
		}
	}

	
}


