package jpo.gui;

import java.util.List;
import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.PictureInfo;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import jpo.dataModel.NodeStatistics;

/*
ConsolidateGroup.java:  class that consolidated the pictures of a group in one directory

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
 *  This class moves all pictures of a group node to a target directory.
 */
public class ConsolidateGroup
        extends SwingWorker<Integer, String> {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( ConsolidateGroup.class.getName() );

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
    public ConsolidateGroup( File targetDirectory,
            SortableDefaultMutableTreeNode startNode, boolean recurseGroups,
            boolean moveLowres, File targetLowresDirectory ) {
        this.targetDirectory = targetDirectory;
        this.startNode = startNode;
        this.recurseGroups = recurseGroups;
        this.moveLowres = moveLowres;
        this.targetLowresDirectory = targetLowresDirectory;


        if ( !targetDirectory.exists() ) {
            logger.severe( String.format( "Aborting because target directory %s doesn't exist", targetDirectory.getPath() ) );
            return;
        }
        if ( !targetDirectory.canWrite() ) {
            logger.severe( String.format( "Aborting because directory %s can't be written to", targetDirectory.getPath() ) );
            return;
        }
        if ( moveLowres && ( !targetLowresDirectory.exists() ) ) {
            logger.info( String.format( "Aborting because lowres target directory %s doesn't exist", targetLowresDirectory.getPath() ) );
            return;
        }
        if ( moveLowres && ( !targetLowresDirectory.canWrite() ) ) {
            logger.info( String.format( "Aborting because lowres target directory %s can't be written to", targetLowresDirectory.getPath() ) );
            return;
        }

        progGui = new ProgressGui( NodeStatistics.countPictures( startNode, recurseGroups ),
                Settings.jpoResources.getString( "ConsolitdateProgBarTitle" ),
                Settings.jpoResources.getString( "ConsolitdateProgBarDone" ) );
        execute();
    }

    /**
     *   This object holds a reference to the progress GUI for the user.
     */
    private ProgressGui progGui;


    /**
     *  The run method is fired by starting the thread. It creates a ProgressGui and does the work.
     * @return
     */
    public Integer doInBackground() {
        consolidateGroup( startNode );

        return Integer.MAX_VALUE;
    }


    @Override
    protected void process( List<String> messages ) {
        for ( String message : messages ) {
            progGui.progressIncrement();
        }
    }


    /**
     *
     */
    @Override
    protected void done() {
        progGui.switchToDoneMode();
    }


    /**
     *  This method consolidates all the nodes of the supplied group.
     *
     *  @param  groupNode  the Group whose nodes are to be consolidated.
     *  @return  True if ok, false if a problem occured.
     */
    private boolean consolidateGroup( SortableDefaultMutableTreeNode groupNode ) {
        Object userObject = groupNode.getUserObject();
        if ( !( userObject instanceof GroupInfo ) ) {
            logger.severe( String.format( "Node %s is not a GroupInfo.", groupNode.toString() ) );
            return false;
        }
        if ( moveLowres && ( !groupNode.isRoot() ) ) {
            // we should move the group thumbnail
            if ( !moveLowresPicture( userObject ) ) {
                logger.severe( String.format( "Could not move lowres picture of node %s", groupNode.toString() ) );
                return false;
            }
        }

        SortableDefaultMutableTreeNode n;
        Enumeration kids = groupNode.children();
        while ( kids.hasMoreElements() && ( !progGui.getInterruptor().getShouldInterrupt() ) ) {
            n = (SortableDefaultMutableTreeNode) kids.nextElement();
            userObject = n.getUserObject();
            if ( userObject instanceof GroupInfo ) {
                if ( recurseGroups ) {
                    if ( !consolidateGroup( n ) ) {
                        // stop if there was a problem.
                        return false;
                    }
                }
            } else {
                if ( !moveHighresPicture( (PictureInfo) userObject ) ) {
                    logger.severe( String.format( "Could not move highres picture of node %s", n.toString() ) );
                    return false;
                }
                if ( moveLowres ) {
                    if ( !moveLowresPicture( userObject ) ) {
                        logger.severe( String.format( "Could not move lowres picture of node %s", n.toString() ) );
                        return false;
                    }
                }
                publish( String.format( "moved %s", n.toString() ) );
            }
        }
        return true;
    }

     

    /**
     *   This method moves a highres file from an indicated SortableDefaultMutableTreeNode 's
     *   PictureInfo object to the target directory. It returns true if the move was successful or ignored
     *   false if there was a problem
     *
     *   @param	p  the userObject of type PictureInfo of the Node to be moved
     *   @return 	True if the move was successful or False if it was not.
     */
    private boolean moveHighresPicture( PictureInfo p ) {
        try {
            File oldFile = p.getHighresFile();
            if ( oldFile == null ) {
                logger.info( "ConsolidateGroupTread.moveHighresPicture: FAILED: can only move picture Files. " + p.getHighresLocation() );
                return false;
            }

            File oldFileParent = p.getHighresFile().getParentFile();
            if ( ( oldFileParent != null ) && ( oldFileParent.equals( targetDirectory ) ) ) {
                //logger.info( "ConsolidateGroup.moveHighresPicture: path is identical (" + oldFileParent.toString() + "==" + p.getHighresFile().getParentFile().toString() + ") . Not Moving Picture: " + p.getHighresLocation() );
                return true;
            }

            //newFile = Tools.inventPicURL( targetDirectory, p.getHighresFilename() );
            File newFile = Tools.inventPicFilename( targetDirectory, p.getHighresFilename() );
            //logger.info( "ConsolidateGroupTread.moveHighresPicture: returned URL: " + newFile );
            if ( Tools.moveFile( oldFile, newFile ) ) {
                return true;
            } else {
                logger.info( "ConsolidateGroupThread.moveHighresPicture: failed to move " + oldFile.toString() + " to " + newFile.toString() );
                return false;
            }

        } catch ( IOException x ) {
            logger.severe( String.format( "Aborting the move of this image because I got an IOException: %s", x.getMessage() ) );
            return false;
        }

    }


    /**
     *   This method moves a highres file from an indicated SortableDefaultMutableTreeNode 's
     *   PictureInfo object to the target directory. It returns true if the move was successful or ignored
     *   false if there was a problem
     *
     *   @param	o  the userObject of the Node to be moved
     *   @return 	True if the move was successful or False if it was not.
     */
    private boolean moveLowresPicture( Object o ) {
        try {
            File oldFile;
            if ( o instanceof GroupInfo ) {
                GroupInfo gi = (GroupInfo) o;
                oldFile = gi.getLowresFile();
            } else {
                PictureInfo pi = (PictureInfo) o;
                oldFile = pi.getLowresFile();
            }

            if ( oldFile == null ) {
                logger.info( String.format( "The Lowres file of node %s is null. Ignoring.", o.toString() ) );
                return true;
            }
            File oldFileParent = oldFile.getParentFile();

            //URL newFileUrl = Tools.inventPicURL( targetLowresDirectory, oldFile.getName() );
            File newFile = Tools.inventPicFilename( targetLowresDirectory, oldFile.getName() );
            if ( ( oldFileParent != null ) && ( oldFileParent.equals( targetLowresDirectory ) ) ) {
                return true;
            }

            if ( !oldFile.exists() ) {
                //logger.info( "ConsolidateGoupThread.moveLowresPicture: There was no Lowres Image to move. Enhancement: The URL should be corrected anyway." );
                return true;
            }

            if ( Tools.moveFile( oldFile, newFile ) ) {
                return true;
            } else {
                //logger.info( "ConsolidateGroup.moveLowresPicture: failed to move " + oldFile.toString() + " to " + newFileUrl.toString() );
                return false;
            }

        } catch ( IOException x ) {
            logger.severe( String.format( "Aborting the move of this image because I got an IOException: %s", x.getMessage() ) );
            return false;
        }
    }
}


