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

Copyright (C) 2002, 2006  Richard Eigenmann.
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
 *  This class moves all pictures of a group node to a target directory.
 */

public class ConsolidateGroupThread extends Thread {
	
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
	 *  This flag says whether to consolidate Lowres images too
	 */
	private boolean moveLowres;
	
	

	/**
   	 *  Creates a Thread which runs the consolidation.
	 *
	 *  @param targetDirectory	Where we want the files moved to
	 *  @param startNode		The node from which this is all to be built.
	 *  @param recurseGroups	Flag indicating subgroups should be included
	 *  @param moveLowres		Flag indication that Lowres should be moved too
	 *  @param targetLowresDirectory  Where to move the lowres files to. Only used if the moveLowres flag is true
	 */
	public ConsolidateGroupThread ( File targetDirectory, SortableDefaultMutableTreeNode startNode, boolean recurseGroups, boolean moveLowres, File targetLowresDirectory ) {
		this.targetDirectory = targetDirectory;
		this.startNode = startNode;
		this.recurseGroups = recurseGroups;
		this.moveLowres = moveLowres;
		this.targetLowresDirectory = targetLowresDirectory;

		//Tools.log("ConsolidateGroupThread: invoked with targetDirectory: " + targetDirectory.getPath());
		//Tools.log("                        and targetLowresDirectory: " + targetLowresDirectory.getPath());
		
		if ( ! targetDirectory.exists() ) {
			Tools.log( "ConsolidateGroupThread aborted because target directory doesn't exist: " + targetDirectory.getPath() );
			return;
		}
		if ( ! targetDirectory.canWrite() ) {
			Tools.log( "ConsolidateGroupThread aborted because target directory can't be written tot: " + targetDirectory.getPath() );
			return;
		}
		if ( moveLowres && ( ! targetLowresDirectory.exists() ) ) {
			Tools.log( "ConsolidateGroupThread aborted because lowres target directory doesn't exist: " + targetLowresDirectory.getPath() );
			return;
		}
		if ( moveLowres && ( ! targetLowresDirectory.canWrite() ) ) {
			Tools.log( "ConsolidateGroupThread aborted because lowres target directory can't be written tot: " + targetLowresDirectory.getPath() );
			return;
		}
	
		start();
	}



	/**
	 *   This object holds a reference to the progress GUI for the user.
	 */
	private ProgressGui progGui;
	
	
	/**
	 *  The run method is fired by starting the thread. It creates a ProgressGui and does the work.
	 */
	public void run() {
		progGui = new ProgressGui( Tools.countPictures( startNode, recurseGroups ),
			Settings.jpoResources.getString("ConsolitdateProgBarTitle"),
			Settings.jpoResources.getString("ConsolitdateProgBarDone") );
		consolidateGroup( startNode );
		progGui.switchToDoneMode();
	}
	





	/**
	 *  temporary node used in the Enumeration of the kids of the Group
	 */
	private SortableDefaultMutableTreeNode n;

	/**
	 *  temporary variable to hold the userObject from the node of the data model
	 */
	private Object uo;



	/** 
	 *  This method consolidates all the nodes of the supplied group.
	 *
	 *  @param  groupNode  the Group whose nodes are to be consolidated.
	 *  @return  True if ok, false if a problem occured.
	 */
	private boolean consolidateGroup ( SortableDefaultMutableTreeNode groupNode )  {
		uo = groupNode.getUserObject();
		if ( moveLowres  && ( ! groupNode.isRoot() ) 
		  && ( ( ! ( (GroupInfo) uo ).getLowresLocation().equals("") ) ) ) {
			if ( ! moveLowresPicture ( uo ) ) {
				JOptionPane.showMessageDialog(
					Settings.anchorFrame, 
					Settings.jpoResources.getString("ConsolidateFailure"), 
					Settings.jpoResources.getString("genericError"), 
					JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
				
		Enumeration kids = groupNode.children();
		while ( kids.hasMoreElements() && ( ! progGui.getInterruptor().getShouldInterrupt() ) ) {
			n = (SortableDefaultMutableTreeNode) kids.nextElement();
			uo = n.getUserObject();
			if ( uo instanceof GroupInfo ) {
				if ( recurseGroups ) {
					if ( ! consolidateGroup( n ) ) {
						// stop if there was a problem.
						return false;
					}
				}
			} else {
				if ( ! moveHighresPicture ( (PictureInfo) uo ) ) {
					JOptionPane.showMessageDialog(
						Settings.anchorFrame, 
						Settings.jpoResources.getString("ConsolidateFailure"), 
						Settings.jpoResources.getString("genericError"), 
						JOptionPane.ERROR_MESSAGE);
				
					return false;
				}
				if ( moveLowres ) {
					if ( ! moveLowresPicture ( uo ) ) {
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
	 *  temp var at class level to reduce the number ob objects being created.
	 */
	private File oldFile;


	/** 
	 *  temp var at class level to reduce the number ob objects being created.
	 */
	private File oldFileParent;


	/** 
	 *  temp var at class level to reduce the number ob objects being created.
	 */
	private URL newFile;


	 
	/** 
	 *   This method moves a highres file from an indicated SortableDefaultMutableTreeNode 's 
	 *   PictureInfo object to the target directory. It returns true if the move was successful or ignored
	 *   false if there was a problem
	 *
	 *   @param	p  the userObject of type PictureInfo of the Node to be moved
	 *   @return 	True if the move was successfull or False if it was not.
	 */
	private boolean moveHighresPicture ( PictureInfo p ) {
		//Tools.log("ConsolidateGroupThread.moveHighresPicture: invoked on PictureInfo: " + p.toString() );
		try {		
			oldFile = p.getHighresFile();
			if ( oldFile == null ) {
				Tools.log("ConsolidateGroupTread.moveHighresPicture: FAILED: can only move picture Files. "  + p.getHighresLocation());
				return false;
			}

			oldFileParent = p.getHighresFile().getParentFile();
			if ( ( oldFileParent != null ) && ( oldFileParent.equals( targetDirectory) ) ) {
				Tools.log("ConsolidateGroupThread.moveHighresPicture: path is identical (" + oldFileParent.toString() + "==" + p.getHighresFile().getParentFile().toString() + ") . Not Moving Picture: " + p.getHighresLocation() );
				return true;
			}
			
			newFile = Tools.inventPicURL( targetDirectory, p.getHighresFilename() );
			//Tools.log( "ConsolidateGroupTread.moveHighresPicture: returned URL: " + newFile );
			if ( Tools.movePicture ( oldFile, newFile ) ) {
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
	 *   @param	o  the userObject of the Node to be moved
	 *   @return 	True if the move was successfull or False if it was not.
	 */
	private boolean moveLowresPicture ( Object o ) {
		//Tools.log("ConsolidateGroupThread.moveLowresPicture: invoked on userObject: " + o.toString() );
		try {		
			if ( o instanceof GroupInfo ) {
				oldFile = ( (GroupInfo) o ).getLowresFile();
				oldFileParent = ( (GroupInfo) o ).getLowresFile().getParentFile();
			} else {
				oldFile = ( (PictureInfo) o ).getLowresFile();
				oldFileParent = ( (PictureInfo) o ).getLowresFile().getParentFile();
			}
		
			//Tools.log("ConsolidateGroupThread.moveLowresPicture: oldFile: " + oldFile.toString() + " / oldFileParent: " + oldFileParent.toString() );

			if ( oldFile == null ) {
				Tools.log ( "ConsolidateGroupThread.moveLowresPicture: oldFile is null" );
				return true;
			}
			URL newFile = Tools.inventPicURL( targetLowresDirectory, oldFile.getName() );
			if ( ( oldFileParent != null ) && ( oldFileParent.equals( targetLowresDirectory ) ) ) {
				Tools.log("ConsolidateGroupThread.moveLowresPicture: path is identical " + oldFileParent.toString() + " not moving picture." );
				return true;
			}
			
			if ( ! oldFile.exists() ) {
				Tools.log("ConsolidateGoupThread.moveLowresPicture: There was no Lowres Image to move. Enhancement: The URL should be corrected anyway.");
				return true;
			}
			if ( Tools.movePicture ( oldFile, newFile ) ) {
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


