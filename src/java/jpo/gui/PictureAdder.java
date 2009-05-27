package jpo.gui;

import jpo.dataModel.Settings;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashSet;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import jpo.dataModel.Tools;


/*
PictureAdder.java:  a class that first brings up a filechooser and then invokes itself as a thread


Copyright (C) 2002, 2009  Richard Eigenmann.
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
 *   Brings up a Filechooser and then loads the selected pictures into the collection. It has 
 *   an asseccory panel that shows a thumbnail of the image.
 */
public class PictureAdder implements PropertyChangeListener {

    /**
     *  Checkbox that allows the user to specify that pictures already
     *  in the collection should be ignored.
     */
    private JCheckBox showThumbnailJCheckBox = new JCheckBox( Settings.jpoResources.getString( "showThumbnailJCheckBox" ) );

    /**
     *  This component shows the thumbnail. It is a JLabel as we can thus use the
     *  ImageIcon to display the pciture.
     */
    private JLabel thumbnailJLabel = new JLabel();

    /**
     *  preferred size of accessory panel
     */
    private static final Dimension OPTIONS_PANEL_DIMENSION = new Dimension( 200, 180 );


    /**
     *  Constructor for a PictureAdder. It creates a JFilechooser GUI and then fires off a
     *  thread to load the pictures in the background.
     *
     *  @param  startNode   The node to which the selected pictures are to be added.
     *                      It must be a GroupInfo Node.
     */
    public PictureAdder( final SortableDefaultMutableTreeNode startNode ) {
        if ( !SwingUtilities.isEventDispatchThread() ) {
            System.out.println( "PictureAdder Constructor is not on EDT" );
        }

        if ( !( startNode.getUserObject() instanceof GroupInfo ) ) {
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "notGroupInfo" ),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        //  Checkbox that allows the user to specifiy whether pictures in the subdirectories should be added or not.
        final JCheckBox recurseJCheckBox = new JCheckBox( Settings.jpoResources.getString( "recurseJCheckBox" ) );
        recurseJCheckBox.setSelected( true );

        //  Checkbox that allows the user to specify that pictures already in the collection should be ignored.
        final JCheckBox newOnlyJCheckBox = new JCheckBox( Settings.jpoResources.getString( "newOnlyJCheckBox" ) );
        newOnlyJCheckBox.setSelected( true );

        showThumbnailJCheckBox.setSelected( Settings.showThumbOnFileChooser );

        // Checkbox that allows the user to specifiy whether directory structures should be retained
        final JCheckBox retainDirectoriesJCheckBox = new JCheckBox( Settings.jpoResources.getString( "retainDirectoriesJCheckBox" ) );
        retainDirectoriesJCheckBox.setSelected( true );


        JPanel optionsJPanel = new JPanel();
        optionsJPanel.setLayout( new BoxLayout( optionsJPanel, BoxLayout.Y_AXIS ) );
        optionsJPanel.add( recurseJCheckBox, BorderLayout.WEST );
        optionsJPanel.add( newOnlyJCheckBox, BorderLayout.WEST );
        optionsJPanel.add( showThumbnailJCheckBox, BorderLayout.WEST );
        optionsJPanel.add( retainDirectoriesJCheckBox, BorderLayout.WEST );
        optionsJPanel.setPreferredSize( OPTIONS_PANEL_DIMENSION );
        thumbnailJLabel.setPreferredSize( OPTIONS_PANEL_DIMENSION );

        final CategoryJScrollPane categoryJScrollPane = new CategoryJScrollPane();
        categoryJScrollPane.loadCategories();


        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder( BorderFactory.createEmptyBorder( 0, 5, 0, 0 ) );
        tabbedPane.add( Settings.jpoResources.getString( "pictureAdderOptionsTab" ), optionsJPanel );
        tabbedPane.add( Settings.jpoResources.getString( "pictureAdderThumbnailTab" ), thumbnailJLabel );
        tabbedPane.add( Settings.jpoResources.getString( "pictureAdderCategoryTab" ), categoryJScrollPane );


        JFileChooser jFileChooser = new JFileChooser();

        jFileChooser.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
        jFileChooser.setMultiSelectionEnabled( true );
        jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "fileChooserAddButtonLabel" ) );
        jFileChooser.setDialogTitle( Settings.jpoResources.getString( "PictureAdderDialogTitle" ) );
        jFileChooser.setAccessory( tabbedPane );
        jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );
        jFileChooser.addPropertyChangeListener( this );

        if ( jFileChooser.showOpenDialog( Settings.anchorFrame ) == JFileChooser.APPROVE_OPTION ) {
            final File[] chosenFiles = jFileChooser.getSelectedFiles();
            Settings.memorizeCopyLocation( jFileChooser.getCurrentDirectory().getPath() );

            Thread t = new Thread() {

                @Override
                public void run() {
                    SortableDefaultMutableTreeNode displayNode = addPictures( startNode, chosenFiles, newOnlyJCheckBox.isSelected(), recurseJCheckBox.isSelected(), retainDirectoriesJCheckBox.isSelected(), categoryJScrollPane.getSelectedCategories() );
                    if ( target != null ) {
                        target.requestShowGroup( displayNode );
                        displayNode.refreshThumbnail();
                    }
                }
            };

            t.start();
            Settings.showThumbOnFileChooser = showThumbnailJCheckBox.isSelected();
        }
    }


    /**
     *  Adds the indicated files to the current node if they are valid pictures. If the newOnly
     *  Flag is on then the collection is checked to see if the picture is already present. It
     *  also opens a progress Gui to provide feedback to the user.
     *
     *  @param startNode
     *  @param chosenFiles
     *  @param newOnly indicates whether to check if the picture is already in the collection
     *  @param recurseDirectories  indicates whether to scan down into directories for more pictures.
     *  @param retainDirectories  indicates whether to preserve the directory structure.
     *  @param selectedCategories
     *  @return In case this is of interest to the caller we return here the node to be displayed; null if no pictures were added.
     */
    public static SortableDefaultMutableTreeNode addPictures( SortableDefaultMutableTreeNode startNode, File[] chosenFiles, boolean newOnly, boolean recurseDirectories, boolean retainDirectories, HashSet<Object> selectedCategories ) {
        final ProgressGui progGui = new ProgressGui( Tools.countfiles( chosenFiles ),
                Settings.jpoResources.getString( "PictureAdderProgressDialogTitle" ),
                Settings.jpoResources.getString( "picturesAdded" ) );
        Settings.pictureCollection.setSendModelUpdates( false );

        SortableDefaultMutableTreeNode displayNode = null;
        SortableDefaultMutableTreeNode addedNode = null;

        // add all the files from the array as nodes to the start node.
        for ( int i = 0; ( i < chosenFiles.length ) && ( !progGui.getInterruptor().getShouldInterrupt() ); i++ ) {
            File addFile = chosenFiles[i];
            if ( !addFile.isDirectory() ) {
                // the file is not a directory
                if ( startNode.addSinglePicture( addFile, newOnly, selectedCategories ) ) {
                    Runnable r = new Runnable() {

                        public void run() {
                            progGui.progressIncrement();
                        }
                    };
                    SwingUtilities.invokeLater( r );
                } else {
                    // addSinglePicture failed
                    Runnable r = new Runnable() {

                        public void run() {
                            progGui.decrementTotal();
                        }
                    };
                    SwingUtilities.invokeLater( r );

                }
            } else {
                // the file is a directory
                if ( Tools.hasPictures( addFile ) ) {
                    addedNode = addDirectory( startNode, addFile, newOnly, recurseDirectories, retainDirectories, progGui, selectedCategories );
                    if ( displayNode == null ) {
                        displayNode = addedNode;
                    }
                } else {
                    Tools.log( "PictureAdder.run: no pictures in directory " + addFile.toString() );
                }
            }
        }
        Settings.pictureCollection.setSendModelUpdates( true );
        Settings.pictureCollection.sendNodeStructureChanged( startNode );

        progGui.switchToDoneMode();
        if ( displayNode == null ) {
            displayNode = startNode;
        }
        return displayNode;
    }


    /**
     *  method that is invoked recursively on each directory encountered. It adds
     *  a new group to the tree and then adds all the pictures found therein to that
     *  group. The ImageIO.getImageReaders method is queried to see whether a reader
     *  exists for the image that is attempted to be loaded.
     *  @param retainDirectories  indicates whether to preserve the directory structure
     *  @return returns the node that was added or null if none was.
     */
    private static SortableDefaultMutableTreeNode addDirectory( SortableDefaultMutableTreeNode startNode, File dir, boolean newOnly, boolean recurseDirectories, boolean retainDirectories, final ProgressGui progGui, HashSet<Object> selectedCategories ) {
        SortableDefaultMutableTreeNode newNode;
        if ( retainDirectories ) {
            newNode = new SortableDefaultMutableTreeNode( new GroupInfo( dir.getName() ) );
            startNode.add( newNode );
            Settings.pictureCollection.setUnsavedUpdates();
        } else {
            newNode = startNode;
        }

        File[] fileArray = dir.listFiles();
        for ( int i = 0; ( i < fileArray.length ) && ( !progGui.getInterruptor().getShouldInterrupt() ); i++ ) {
            if ( fileArray[i].isDirectory() && recurseDirectories ) {
                if ( Tools.hasPictures( fileArray[i] ) ) {
                    newNode = addDirectory( newNode, fileArray[i], newOnly, recurseDirectories, retainDirectories, progGui, selectedCategories );
                }
            } else {
                if ( newNode.addSinglePicture( fileArray[i], newOnly, selectedCategories ) ) {
                    Runnable r = new Runnable() {

                        public void run() {
                            progGui.progressIncrement();
                        }
                    };
                    SwingUtilities.invokeLater( r );
                } else {
                    Runnable r = new Runnable() {

                        public void run() {
                            progGui.decrementTotal();
                        }
                    };
                    SwingUtilities.invokeLater( r );
                }
            }
        }
        // it can happen that we end up adding no pictures and could be returning a new empty group
        if ( retainDirectories && ( newNode.getChildCount() == 0 ) ) {
            newNode.deleteNode();
            return startNode;
        } else {
            return newNode;
        }
    }


    /**
     *  This method is invoked from the FileChooser and creates the thumbnail.
     *
     *  See http://java.sun.com/developer/JDCTechTips/index.html for the March 16 issue on
     *  Preview panels in the JFileChooser.
     * @param changeEvent
     */
    public void propertyChange( PropertyChangeEvent changeEvent ) {
        String changeName = changeEvent.getPropertyName();
        if ( changeName.equals( JFileChooser.SELECTED_FILE_CHANGED_PROPERTY ) ) {
            File file = (File) changeEvent.getNewValue();
            if ( ( file != null ) && ( showThumbnailJCheckBox.isSelected() ) ) {
                ImageIcon icon = new ImageIcon( file.getPath() );
                if ( icon.getIconWidth() > OPTIONS_PANEL_DIMENSION.getWidth() ) {
                    icon = new ImageIcon( icon.getImage().getScaledInstance( (int) OPTIONS_PANEL_DIMENSION.getWidth(), -1,
                            Image.SCALE_DEFAULT ) );
                    if ( icon.getIconHeight() > OPTIONS_PANEL_DIMENSION.getHeight() ) {
                        icon = new ImageIcon( icon.getImage().getScaledInstance( -1, (int) OPTIONS_PANEL_DIMENSION.getHeight(),
                                Image.SCALE_DEFAULT ) );
                    }
                }
                final ImageIcon setIcon = icon;
                Runnable r = new Runnable() {

                    public void run() {
                        thumbnailJLabel.setIcon( setIcon );
                    }
                };
                if ( SwingUtilities.isEventDispatchThread() ) {
                    r.run();
                } else {
                    SwingUtilities.invokeLater( r );
                }
            }
        }
    }

    /**
     *  This object refers to the target object that wil receive notification when the adding is done.
     */
    private GroupPopupInterface target = null;


    /**
     *  this method logs the object to call back when the pictures have been added.
     * @param target
     */
    public void setNotificationTarget( GroupPopupInterface target ) {
        this.target = target;
    }
}
