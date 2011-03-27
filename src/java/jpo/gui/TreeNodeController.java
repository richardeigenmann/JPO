package jpo.gui;

import java.util.logging.Level;
import jpo.dataModel.Settings;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.*;
import jpo.dataModel.PictureInfo;

/*
TreeNodeController.java: This class should handle all the interactive GUI stuff for a Tree Node

Copyright (C) 2007 - 2009  Richard Eigenmann.
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
 *   This class should handle all the interactive GUI stuff for a Tree Node. Formerly the
 *   SortableDefaultMutableTreeNode was doing data stuff and GUI stuff. It should really
 *   only do data stuff.
 *
 *   @author  Richard Eigenmann
 */
public class TreeNodeController {

    /**
     * Defines a LOGGER for this class
     */
    private static Logger LOGGER = Logger.getLogger( TreeNodeController.class.getName() );

    /**
     *  This method brings up a Filechooser and then loads the images off the specified flat file.
     * @param  targetNode   The node at which to add the flat file.
     */
    public static void addFlatFile( SortableDefaultMutableTreeNode targetNode ) {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode( javax.swing.JFileChooser.FILES_ONLY );
        jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "fileOpenButtonText" ) );
        jFileChooser.setDialogTitle( Settings.jpoResources.getString( "addFlatFileTitle" ) );
        jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );

        int returnVal = jFileChooser.showOpenDialog( Settings.anchorFrame );
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File chosenFile = jFileChooser.getSelectedFile();

            try {
                targetNode.addFlatFile( chosenFile );
            } catch ( IOException e ) {
                LOGGER.log( Level.INFO, "IOException {0}", e.getMessage() );
                JOptionPane.showMessageDialog( Settings.anchorFrame,
                        e.getMessage(),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
            }

        }
    }

    /**
     *   Brings up a JFileChooser to select the target location and then copies the images to the target location
     *
     * @param nodes The Vector of nodes to be copied to a new location
     */
    public static void copyToNewLocation( SortableDefaultMutableTreeNode[] nodes ) {
        JFileChooser jFileChooser = new JFileChooser();

        jFileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "CopyImageDialogButton" ) );
        jFileChooser.setDialogTitle( Settings.jpoResources.getString( "CopyImageDialogTitle" ) );
        jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );

        int returnVal = jFileChooser.showSaveDialog( Settings.anchorFrame );
        if ( returnVal != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        copyToLocation( nodes, jFileChooser.getSelectedFile() );
    }

        /**
     *   Brings up a JFileChooser to select the target location and then copies the images to the target location
     *
     * @param nodes The Vector of nodes to be copied to a new location
     */
    public static void copyToLocation( SortableDefaultMutableTreeNode[] nodes, File targetLocation ) {
        int picsCopied = 0;
        for ( SortableDefaultMutableTreeNode node : nodes ) {
            if ( node.getUserObject() instanceof PictureInfo ) {
                node.validateAndCopyPicture( targetLocation );
                picsCopied++;

            } else {
                LOGGER.info( String.format( "Skipping non PictureInfo node %s", node.toString() ) );
            }
        }
        JOptionPane.showMessageDialog( Settings.anchorFrame,
                String.format( Settings.jpoResources.getString( "copyToNewLocationSuccess" ), picsCopied, nodes.length ),
                Settings.jpoResources.getString( "genericInfo" ),
                JOptionPane.INFORMATION_MESSAGE );

    }

    
    /**
     *   Bring up a Dialog where the user can input a new name for a file and rename it.
     * @param node
     */
    public static void fileRename( SortableDefaultMutableTreeNode node ) {
        Object userObject = node.getUserObject();
        if ( !( userObject instanceof PictureInfo ) ) {
            return;
        }

        PictureInfo pi = (PictureInfo) userObject;
        File highresFile = pi.getHighresFile();
        if ( highresFile == null ) {
            return;
        }

        Object object = Settings.jpoResources.getString( "FileRenameLabel1" ) + highresFile.toString() + Settings.jpoResources.getString( "FileRenameLabel2" );
        String selectedValue = JOptionPane.showInputDialog(
                Settings.anchorFrame, // parent component
                object, // message
                highresFile.toString() );				// initialSelectionValue
        if ( selectedValue != null ) {
            File newName = new File( selectedValue );
            if ( highresFile.renameTo( newName ) ) {
                LOGGER.log( Level.INFO, "Sucessufully renamed: {0} to: {1}", new Object[] { highresFile.toString(), selectedValue } );
                try {
                    pi.setHighresLocation( newName.toURI().toURL() );
                } catch ( MalformedURLException x ) {
                    LOGGER.log( Level.INFO, "Caught a MalformedURLException because of: {0}", x.getMessage() );
                }
            } else {
                LOGGER.log( Level.INFO, "Rename failed from : {0} to: {1}", new Object[] { highresFile.toString(), selectedValue } );
            }
        }
    }

    /**
     *  This function brings up a PictureInfoEditor of a GroupInfoEditor
     * @param node
     */
    public static void showEditGUI( SortableDefaultMutableTreeNode node ) {
        if ( node == null ) {
            LOGGER.severe( "How come this method was called with a null node?" );
            Thread.dumpStack();
            return;
        }
        if ( node.getUserObject() instanceof PictureInfo ) {
            new PictureInfoEditor( node );
        } else if ( node.getUserObject() instanceof GroupInfo ) {
            new GroupInfoEditor( node );
        } else {
            LOGGER.info( String.format( "Don't know what kind of editor to use on node %s Ignoring request.", node.toString() ) );
        }
    }

    /**
     *  This function opens the CateGoryUsageEditor.
     * @param node
     */
    public static void showCategoryUsageGUI( SortableDefaultMutableTreeNode node ) {
        //logger.info("SDMTN.showCategoryUsageGUI invoked");
        if ( node.getUserObject() instanceof PictureInfo ) {
            CategoryUsageJFrame cujf = new CategoryUsageJFrame();
            Vector<SortableDefaultMutableTreeNode> nodes = new Vector<SortableDefaultMutableTreeNode>();
            nodes.add( node );
            cujf.setSelection( nodes );
        } else if ( node.getUserObject() instanceof GroupInfo ) {
            CategoryUsageJFrame cujf = new CategoryUsageJFrame();
            cujf.setGroupSelection( node, false );
        } else {
            LOGGER.info( "SortableDefaultMutableTreeNode.showCategoryUsageGUI: doesn't know what kind of editor to use. Ignoring request." );
        }
    }
}
