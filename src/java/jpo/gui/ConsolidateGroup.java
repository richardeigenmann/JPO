package jpo.gui;

import java.util.List;
import java.util.logging.Level;
import jpo.dataModel.Tools;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.PictureInfo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/*
ConsolidateGroup.java:  class that consolidated the pictures of a group in one directory

Copyright (C) 2002 - 2011  Richard Eigenmann.
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
    private static final Logger LOGGER = Logger.getLogger( ConsolidateGroup.class.getName() );

    {
        LOGGER.setLevel( Level.ALL );
    }
    /**
     *  the directory where the pictures are to be moved to
     */
    private final File targetDirectoryHighres;
    /**
     *  the directory where the lowres pictures are to be moved to
     */
    private final File targetDirectoryLowres;
    /**
     *  the node to start from
     */
    private final SortableDefaultMutableTreeNode startNode;
    /**
     *  flag that indicates that the subgroups should also be considered
     */
    private final boolean recurseGroups;
    /**
     *  This flag says whether to consolidate Lowres images too
     */
    private final boolean moveLowres;

    /**
     *  Creates a Thread which runs the consolidation.
     *
     *  @param targetDirectoryHighres	Where we want the files moved to
     *  @param startNode		The node from which this is all to be built.
     *  @param recurseGroups            Flag indicating subgroups should be included
     *  @param moveLowres		Flag indication that Lowres should be moved too
     *  @param targetDirectoryLowres    Where to move the lowres files to. Only used if the moveLowres flag is true
     */
    public ConsolidateGroup( File targetDirectoryHighres,
            SortableDefaultMutableTreeNode startNode, boolean recurseGroups,
            boolean moveLowres, File targetDirectoryLowres,
            ProgressGui progGui ) {
        this.targetDirectoryHighres = targetDirectoryHighres;
        this.startNode = startNode;
        this.recurseGroups = recurseGroups;
        this.moveLowres = moveLowres;
        this.targetDirectoryLowres = targetDirectoryLowres;
        this.progGui = progGui;


        if ( !targetDirectoryHighres.exists() ) {
            LOGGER.severe( String.format( "Aborting because target directory %s doesn't exist", targetDirectoryHighres.getPath() ) );
            return;
        }
        if ( !targetDirectoryHighres.canWrite() ) {
            LOGGER.severe( String.format( "Aborting because directory %s can't be written to", targetDirectoryHighres.getPath() ) );
            return;
        }
        if ( moveLowres && ( !targetDirectoryLowres.exists() ) ) {
            LOGGER.info( String.format( "Aborting because lowres target directory %s doesn't exist", targetDirectoryLowres.getPath() ) );
            return;
        }
        if ( moveLowres && ( !targetDirectoryLowres.canWrite() ) ) {
            LOGGER.info( String.format( "Aborting because lowres target directory %s can't be written to", targetDirectoryLowres.getPath() ) );
            return;
        }


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
    @Override
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
     *  @return  True if OK, false if a problem occurred.
     */
    private void consolidateGroup( SortableDefaultMutableTreeNode groupNode ) {
        Object userObject = groupNode.getUserObject();
        if ( !( userObject instanceof GroupInfo ) ) {
            LOGGER.severe( String.format( "Node %s is not a GroupInfo.", groupNode.toString() ) );
            return;
        }
        if ( moveLowres && ( !groupNode.isRoot() ) ) {
            // we should move the group thumbnail
            if ( !moveLowresPicture( userObject ) ) {
                LOGGER.severe( String.format( "Could not move lowres picture of node %s. Continuing.", groupNode.toString() ) );
            }
            LOGGER.fine( String.format( "Moved GroupInfo (%s) Thumbnail file to %s", groupNode.toString(), ( (GroupInfo) userObject ).getLowresFilename() ) );
        }

        LOGGER.fine( String.format( "The node %s has %d children", groupNode.toString(), groupNode.getChildCount() ) );
        //Enumeration<SortableDefaultMutableTreeNode> childNodeEnumeration = groupNode.children();
        //while ( childNodeEnumeration.hasMoreElements() && ( !progGui.getInterruptor().getShouldInterrupt() ) ) {
        //    SortableDefaultMutableTreeNode node = childNodeEnumeration.nextElement();
        LOGGER.fine( String.format( "prog GUI interrupt: %b", progGui.getInterruptor().getShouldInterrupt() ) );
        ArrayList<SortableDefaultMutableTreeNode> nodeArrayList = Collections.list( groupNode.children() );
        //for ( Enumeration<SortableDefaultMutableTreeNode> nodes = groupNode.children(); nodes.hasMoreElements() && ( !  progGui.getInterruptor().getShouldInterrupt() );  ) {
        //    SortableDefaultMutableTreeNode node = nodes.nextElement();
        for ( SortableDefaultMutableTreeNode node : nodeArrayList ) {
            userObject = node.getUserObject();
            if ( ( userObject instanceof GroupInfo ) && recurseGroups ) {
                consolidateGroup( node );
            } else {
                // it's a PictureInfo object
                PictureInfo pictureInfo = (PictureInfo) userObject; // let's make sure
                if ( !moveHighresPicture( pictureInfo ) ) {
                    LOGGER.severe( String.format( "Could not move highres picture of node %s. Aborting.", node.toString() ) );
                }
                LOGGER.info( String.format( "Successfully Moved Highres file of node %s", pictureInfo.toString() ) );
                if ( moveLowres ) {
                    if ( !moveLowresPicture( pictureInfo ) ) {
                        LOGGER.severe( String.format( "Could not move lowres picture of node %s. Continuing.", node.toString() ) );
                    }
                }
                LOGGER.info( String.format( "Successfully consolidated node %s to highres directory %s and lowres directory %s", node.toString(), targetDirectoryHighres, targetDirectoryLowres ) );
            }
            publish( String.format( "Consolidated node: %s", node.toString() ) );
        }
        LOGGER.fine( String.format( "End of loop prog GUI interrupt: %b", progGui.getInterruptor().getShouldInterrupt() ) );
    }

    /**
     *   This method moves a highres file from an indicated SortableDefaultMutableTreeNode 's
     *   PictureInfo object to the target directory. It returns true if the move was successful or ignored
     *   false if there was a problem
     *
     *   @param	pictureInfo  the userObject of type PictureInfo of the Node to be moved
     *   @return 	True if the move was successful or False if it was not.
     */
    private boolean moveHighresPicture( PictureInfo pictureInfo ) {
        File oldFile = pictureInfo.getHighresFile();
        if ( oldFile == null ) {
            LOGGER.log( Level.INFO, "getHighresFile returned null on node {0}. Crashing here.", pictureInfo.toString() );
            return false;
        }

        File oldFileParent = pictureInfo.getHighresFile().getParentFile();
        if ( ( oldFileParent != null ) && ( oldFileParent.equals( targetDirectoryHighres ) ) ) {
            LOGGER.info( String.format( "Directory of file %s is already the correct location %s. Leaving file as is.", pictureInfo.toString(), targetDirectoryHighres ) );
            return true;
        }

        File newFile = Tools.inventPicFilename( targetDirectoryHighres, pictureInfo.getHighresFilename() );
        if ( Tools.moveFile( oldFile, newFile ) ) {
            return true;
        } else {
            LOGGER.log( Level.INFO, "Failed to move {0} to {1}. Returning false", new Object[]{ oldFile.toString(), newFile.toString() } );
            return false;
        }
    }

    /**
     *   This method moves a lowres file from an indicated SortableDefaultMutableTreeNode 's
     *   PictureInfo object to the target directory. It returns true if the move was successful or ignored
     *   false if there was a problem
     *
     *   @param	o  the userObject of the Node to be moved
     *   @return 	True if the move was successful or False if it was not.
     */
    private boolean moveLowresPicture( Object object ) {
        File oldFile;
        if ( object instanceof GroupInfo ) {
            GroupInfo gi = (GroupInfo) object;
            oldFile = gi.getLowresFile();
        } else {
            PictureInfo pi = (PictureInfo) object;
            oldFile = pi.getLowresFile();
        }

        if ( oldFile == null ) {
            LOGGER.info( String.format( "The Lowres file of node %s is null. Ignoring.", object.toString() ) );
            return true;
        }
        File oldFileParent = oldFile.getParentFile();

        File newFile = Tools.inventPicFilename( targetDirectoryLowres, oldFile.getName() );
        if ( ( oldFileParent != null ) && ( oldFileParent.equals( targetDirectoryLowres ) ) ) {
            LOGGER.info( String.format( "Directory of file %s is already the correct location %s. Leaving file as is.", oldFile.toString(), targetDirectoryLowres ) );
            return true;
        }

        if ( !oldFile.exists() ) {
            LOGGER.info( "ConsolidateGoupThread.moveLowresPicture: There was no Lowres Image to move. Enhancement: The URL should be corrected anyway." );
            return true;
        }

        if ( Tools.moveFile( oldFile, newFile ) ) {
            return true;
        } else {
            LOGGER.log( Level.INFO, "ConsolidateGroup.moveLowresPicture: failed to move {0} to {1}", new Object[]{ oldFile.toString(), newFile.toString() } );
            return false;
        }

    }
}
