package jpo.gui;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;

/*
PictureAdder.java:  A Class which brings up a progress bar and adds pictures to the specified node.


Copyright (C) 2009-2014  Richard Eigenmann.
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
 * A Class which brings up a progress bar and adds pictures to the specified node.
 * @author Richard Eigenmann
 */
public class PictureAdder
        extends SwingWorker<Integer, Integer> {

    /**
     * Constructor
     *  @param startNode  The node on which to add the pictures
     *  @param chosenFiles  The array of Files to add
     *  @param newOnly indicates whether to check if the picture is already in the collection
     *  @param recurseDirectories  indicates whether to scan down into directories for more pictures.
     *  @param retainDirectories  indicates whether to preserve the directory structure.
     *  @param selectedCategories  The categories to give the pictures
     */
    public PictureAdder( SortableDefaultMutableTreeNode startNode,
            File[] chosenFiles, boolean newOnly, boolean recurseDirectories,
            boolean retainDirectories, HashSet<Object> selectedCategories ) {

        this.startNode = startNode;
        this.chosenFiles = chosenFiles;
        this.newOnly = newOnly;
        this.recurseDirectories = recurseDirectories;
        this.retainDirectories = retainDirectories;
        this.selectedCategories = selectedCategories;

        LOGGER.fine( String.format( "Invoked for node: %s, with %d files, newOnly: %b, recurseDirectories: %b, retainDirectories: %b", startNode.toString(), chosenFiles.length, newOnly, recurseDirectories, retainDirectories ) );

        progGui = new ProgressGui( Tools.countfiles( chosenFiles ),
                Settings.jpoResources.getString( "PictureAdderProgressDialogTitle" ),
                Settings.jpoResources.getString( "picturesAdded" ) );
        Settings.pictureCollection.setSendModelUpdates( false );


    }

    /**
     * The files selected by the filechooser
     */
    private final File[] chosenFiles;

    /**
     * The node into which to add pictures
     */
    private final SortableDefaultMutableTreeNode startNode;

    /**
     * A Progress Gui with a cancel button.
     */
    private final ProgressGui progGui;

    /**
     * newOnly indicates whether to check if the picture is already in the collection
     */
    private final boolean newOnly;

    private final boolean recurseDirectories;

    private final boolean retainDirectories;

    private final HashSet<Object> selectedCategories;


    /**
     *  Adds the indicated files to the current node if they are valid pictures. If the newOnly
     *  Flag is on then the collection is checked to see if the picture is already present. It
     *  also opens a progress Gui to provide feedback to the user.
     * @return A string
     */
    @Override
    public Integer doInBackground() {
        // add all the files from the array as nodes to the start node.
        for ( int i = 0; ( i < chosenFiles.length ) && ( !progGui.getInterruptor().getShouldInterrupt() ); i++ ) {
            File addFile = chosenFiles[i];
            LOGGER.fine( String.format( "File %d of %d: %s", i + 1, chosenFiles.length, addFile.toString() ) );
            if ( !addFile.isDirectory() ) {
                if ( startNode.addSinglePicture( addFile, newOnly, selectedCategories ) ) {
                    publish( 1 );
                } else {
                    publish( -1 );
                }
            } else {
                // the file is a directory
                if ( Tools.hasPictures( addFile ) ) {
                    addDirectory( startNode, addFile );
                } else {
                    LOGGER.fine( String.format( "No pictures in directory: %s", addFile.toString() ) );
                }
            }

        }
        return 1;
    }


    /**
     *  method that is invoked recursively on each directory encountered. It adds
     *  a new group to the tree and then adds all the pictures found therein to that
     *  group. The ImageIO.getImageReaders method is queried to see whether a reader
     *  exists for the image that is attempted to be loaded.
     *  @param parentNode the node to which to add
     *  @param dir the directory to add
     */
    private void addDirectory(
            SortableDefaultMutableTreeNode parentNode, File dir ) {
        SortableDefaultMutableTreeNode directoryNode;
        if ( retainDirectories ) {
            directoryNode = new SortableDefaultMutableTreeNode( new GroupInfo( dir.getName() ) );
            parentNode.add( directoryNode );
            Settings.pictureCollection.setUnsavedUpdates();
        } else {
            directoryNode = parentNode;
        }

        File[] fileArray = dir.listFiles();
        for ( int i = 0; ( i < fileArray.length ) && ( !progGui.getInterruptor().getShouldInterrupt() ); i++ ) {
            if ( fileArray[i].isDirectory() && recurseDirectories ) {
                if ( Tools.hasPictures( fileArray[i] ) ) {
                    addDirectory( directoryNode, fileArray[i] );
                }
            } else {
                if ( directoryNode.addSinglePicture( fileArray[i], newOnly, selectedCategories ) ) {
                    publish( 1 );
                } else {
                    publish( -1 );
                }
            }
        }
        // it can happen that we end up adding no pictures and could be returning a new empty group
        if ( retainDirectories && ( directoryNode.getChildCount() == 0 ) ) {
            directoryNode.deleteNode();
        }
    }


    /**
     * The Swing Worker sends the publish() events here on the EDT when it feels like it.
     * @param chunks  Send 1 to increment the count of pictures processed, -1 to decrement the total
     */
    @Override
    protected void process( List<Integer> chunks ) {
        for ( int i : chunks ) {
            if ( i > 0 ) {
                progGui.progressIncrement();
            } else {
                progGui.decrementTotal();
            }

        }
    }


    /**
     * Sends a model notification about the change and updates the cancel button to an OK button
     */
    @Override
    protected void done() {
        Settings.pictureCollection.setSendModelUpdates( true );
        Settings.pictureCollection.sendNodeStructureChanged( startNode );
        progGui.switchToDoneMode();
    }

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( PictureFileChooser.class.getName() );
}
