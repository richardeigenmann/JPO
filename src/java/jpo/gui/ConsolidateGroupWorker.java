package jpo.gui;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;

/*
 ConsolidateGroup.java:  class that consolidated the pictures of a group in one directory

 Copyright (C) 2002 - 2015  Richard Eigenmann.
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
 * This class moves all pictures of a group node to a target directory.
 */
public class ConsolidateGroupWorker extends SwingWorker<Void, String> {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ConsolidateGroupWorker.class.getName() );

    /**
     * the directory where the pictures are to be moved to
     */
    private final File targetDirectoryHighres;

    /**
     * the node to start from
     */
    private final SortableDefaultMutableTreeNode startNode;
    /**
     * flag that indicates that the subgroups should also be considered
     */
    private final boolean recurseGroups;

    /**
     * Creates a Thread which runs the consolidation.
     *
     * @param targetDirectoryHighres	Where we want the files moved to
     * @param startNode	The node from which this is all to be built.
     * @param recurseGroups Flag indicating subgroups should be included if the
     * moveLowres flag is true
     * @param progGui A Progress Gui
     */
    public ConsolidateGroupWorker( File targetDirectoryHighres,
            SortableDefaultMutableTreeNode startNode, boolean recurseGroups,
            ProgressGui progGui ) {
        this.targetDirectoryHighres = targetDirectoryHighres;
        this.startNode = startNode;
        this.recurseGroups = recurseGroups;
        this.progGui = progGui;

        if ( !targetDirectoryHighres.exists() ) {
            LOGGER.severe( String.format( "Aborting because target directory %s doesn't exist", targetDirectoryHighres.getPath() ) );
            return;
        }
        if ( !targetDirectoryHighres.canWrite() ) {
            LOGGER.severe( String.format( "Aborting because directory %s can't be written to", targetDirectoryHighres.getPath() ) );
            return;
        }

        execute();
    }
    /**
     * This object holds a reference to the progress GUI for the user.
     */
    private final ProgressGui progGui;

    /**
     * The run method is fired by starting the thread. It creates a ProgressGui
     * and does the work.
     *
     * @return Integer.MAX_VALUE
     */
    @Override
    public Void doInBackground() {
        consolidateGroup( startNode );

        return null;
    }

    @Override
    protected void process( List<String> messages ) {
        for ( String message : messages ) {
            progGui.progressIncrement();
        }
    }

    @Override
    protected void done() {
        progGui.switchToDoneMode();

        if (errorCount > 0 ) {
                            JOptionPane.showMessageDialog( progGui,
                            String.format( "Could not move %d pictures", errorCount ),
                            Settings.jpoResources.getString( "genericError" ),
                            JOptionPane.ERROR_MESSAGE );
        }

    }

    private int errorCount;  // default is 0
    
    /**
     * This method consolidates all the nodes of the supplied group.
     *
     * @param groupNode the Group whose nodes are to be consolidated.
     */
    private void consolidateGroup( SortableDefaultMutableTreeNode groupNode ) {
        Object userObject = groupNode.getUserObject();
        if ( !( userObject instanceof GroupInfo ) ) {
            return;
        }

        LOGGER.fine( String.format( "The node %s has %d children", groupNode.toString(), groupNode.getChildCount() ) );
        LOGGER.fine( String.format( "prog GUI interrupt: %b", progGui.getInterruptor().getShouldInterrupt() ) );
        @SuppressWarnings( "unchecked" )
        List<SortableDefaultMutableTreeNode> nodes = Collections.<SortableDefaultMutableTreeNode>list( groupNode.children() );
        for ( SortableDefaultMutableTreeNode node : nodes ) {
            userObject = node.getUserObject();
            if ( ( userObject instanceof GroupInfo ) && recurseGroups ) {
                consolidateGroup( node );
            } else {
                // it's a PictureInfo object
                PictureInfo pictureInfo = (PictureInfo) userObject; // let's make sure
                if ( !moveHighresPicture( pictureInfo ) ) {
                    LOGGER.severe( String.format( "Could not move highres picture of node %s. Aborting.", node.toString() ) );
                    errorCount++;
                } else {
                    LOGGER.info( String.format( "Successfully Moved Highres file of node %s", pictureInfo.toString() ) );
                    publish( String.format( "Consolidated node: %s", node.toString() ) );
                }
            }
        }
        LOGGER.fine( String.format( "End of loop prog GUI interrupt: %b", progGui.getInterruptor().getShouldInterrupt() ) );
    }

    /**
     * This method moves a highres file from an indicated
     * SortableDefaultMutableTreeNode 's PictureInfo object to the target
     * directory. It returns true if the move was successful or ignored false if
     * there was a problem
     *
     * @param	pictureInfo the userObject of type PictureInfo of the Node to be
     * moved
     * @return True if the move was successful or False if it was not.
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

}
