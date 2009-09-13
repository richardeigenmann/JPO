package jpo.gui;

import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.*;
import jpo.dataModel.Camera;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.logging.Logger;

/*
AddFromCamera.java:
a class that creates a GUI and then adds the pictures from the camera to your collection.
 
Copyright (C) 2002-2009 Richard Eigenmann.
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
 *   This class creates a JFrame and then presents the user with the dialog to add pictures directly
 *   from the camera.
 *
 */
public class AddFromCamera
        extends 	JFrame
        implements 	ActionListener, CategoryGuiListenerInterface {
    
     /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( AddFromCamera.class.getName() );
    
    /**
     *  The name of the camera
     */
    JComboBox cameraNameJComboBox = new JComboBox();
    
    
    
    /**
     *   holds the target directory where the images are to be copied to
     */
    private DirectoryChooser targetDirChooser =
            new DirectoryChooser( Settings.jpoResources.getString("targetDirJLabel"),
            DirectoryChooser.DIR_MUST_EXIST );
    
    
    
    /**
     *  Ok Button
     **/
    private JButton okJButton = new JButton( Settings.jpoResources.getString("AddFromCameraOkJButton") );
    
    
    /**
     *  Cancel Button
     **/
    private JButton cancelJButton = new JButton( Settings.jpoResources.getString("genericCancelText") );
    
    
    /**
     *  Category Button
     **/
    private JButton categoriesJButton = new JButton( Settings.jpoResources.getString("categoriesJButton") );
    
    /**
     *  Category Button
     **/
    private JButton cameraEditorJButton = new JButton( Settings.jpoResources.getString("editCameraJButton") );
    
    /**
     *  a reference to the root node with which shall be added to.
     */
    private SortableDefaultMutableTreeNode rootNode;
    
    /**
     *   Radio Button that indicates that all the pictures in the camera should be loaded.
     */
    private JRadioButton allPicturesJRadioButton = new JRadioButton( Settings.jpoResources.getString("allPicturesJRadioButton") );
    
    /**
     *  Radio Button that indicates that only the new pictures in the camera should be loaded.
     */
    private JRadioButton newPicturesJRadioButton = new JRadioButton( Settings.jpoResources.getString("newPicturesJRadioButton") );
    
    /**
     *  Radio Button that indicates that only those pictures missing in the collection should be loaded
     */
    private JRadioButton missingPicturesJRadioButton = new JRadioButton( Settings.jpoResources.getString("missingPicturesJRadioButton") );
    
    /**
     *  Checkbox that allows the user to specify whether directory structures should be retained
     */
    private JCheckBox retainDirectoriesJCheckBox = new JCheckBox( Settings.jpoResources.getString("retainDirectoriesJCheckBox") );
    
    
    /**
     *  this vector holds the list of categories to be applied to newly loaded pictures.
     */
    private HashSet<Object> selectedCategories = null;
    
    
    
    /**
     *   Creates a JFrame with the GUI elements and buttons that can
     *   start and stop the reconciliation. The reconciliation itself
     *   runs in it's own Thread.
     *
     *   @param	rootNode	The node which should be used as
     *				a starting point for the reconciliation.
     *				Will probably always be the root node of
     *				the tree.
     */
    public AddFromCamera( SortableDefaultMutableTreeNode rootNode ) {
        this.rootNode = rootNode;
        
        setSize( 500, 300 );
        setLocationRelativeTo( Settings.anchorFrame );
        setTitle( Settings.jpoResources.getString( "AddFromCamera" ) );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                getRid();
            }
        });
        
        
        JPanel controlJPanel = new JPanel();
        controlJPanel.setLayout( new GridBagLayout() );
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0; constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(4, 4, 4, 4);
        
        
        // The camera panel
        
        JPanel cameraJPanel = new JPanel();
        cameraJPanel.setLayout( new GridBagLayout() );
        cameraJPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Camera") );
        constraints.gridy++; constraints.gridx = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        controlJPanel.add( cameraJPanel, constraints );
        
        JLabel cameraNameJLabel = new JLabel( Settings.jpoResources.getString("cameraNameJLabel") );
        constraints.gridy =0; constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(4, 4, 4, 4);
        cameraJPanel.add( cameraNameJLabel, constraints );
        
        constraints.gridy++; constraints.gridx = 0;
        constraints.gridwidth = 1;
        cameraJPanel.add( cameraNameJComboBox, constraints );
        cameraNameJComboBox.setEditable( false );
        
        
        
        // end of Camera Panel
        
        
        constraints.gridx = 0; constraints.gridy++;
        constraints.fill = GridBagConstraints.NONE;
        categoriesJButton.setPreferredSize( Settings.defaultButtonDimension );
        categoriesJButton.setMinimumSize( Settings.defaultButtonDimension );
        categoriesJButton.setMaximumSize( Settings.defaultButtonDimension );
        categoriesJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        categoriesJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                CategoryUsageJFrame cujf = new CategoryUsageJFrame();
                cujf.updateCategories();
                cujf.addCategoryGuiListener( AddFromCamera.this );
            }
        } );
        controlJPanel.add( categoriesJButton, constraints );
        
        
        constraints.gridx++;
        constraints.fill = GridBagConstraints.NONE;
        cameraEditorJButton.setPreferredSize( Settings.defaultButtonDimension );
        cameraEditorJButton.setMinimumSize( Settings.defaultButtonDimension );
        cameraEditorJButton.setMaximumSize( Settings.defaultButtonDimension );
        cameraEditorJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        cameraEditorJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new CamerasEditor();
            }
        } );
        controlJPanel.add( cameraEditorJButton, constraints );
        
        
        
        //Create the radio buttons.
        constraints.gridx = 0; constraints.gridy++;
        constraints.insets = new Insets(0, 4, 0, 0);
        controlJPanel.add( allPicturesJRadioButton, constraints );
        
        constraints.gridy++;
        controlJPanel.add( newPicturesJRadioButton, constraints );
        
        constraints.gridy++;
        controlJPanel.add( missingPicturesJRadioButton, constraints );
        
        
        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add( allPicturesJRadioButton );
        group.add( newPicturesJRadioButton );
        group.add( missingPicturesJRadioButton );
        newPicturesJRadioButton.setSelected( true );
        
        
        retainDirectoriesJCheckBox.setSelected( false );
        constraints.gridy++;
        controlJPanel.add( retainDirectoriesJCheckBox, constraints );
        
        
        JLabel targetDirJLabel = new JLabel( Settings.jpoResources.getString("targetDirJLabel") );
        constraints.gridy++; constraints.gridx = 0;
        constraints.gridwidth = 2;
        controlJPanel.add( targetDirJLabel, constraints );
        
        constraints.gridy++;
        controlJPanel.add( targetDirChooser, constraints );
        
        
        JPanel buttonJPanel = new JPanel();
        
        okJButton.setPreferredSize( Settings.defaultButtonDimension );
        okJButton.setMinimumSize( Settings.defaultButtonDimension );
        okJButton.setMaximumSize( Settings.defaultButtonDimension );
        okJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        okJButton.setDefaultCapable( true );
        getRootPane().setDefaultButton( okJButton );
        okJButton.addActionListener( this );
        buttonJPanel.add( okJButton );
        
        
        cancelJButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
        cancelJButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        cancelJButton.addActionListener( this );
        buttonJPanel.add( cancelJButton );
        
        constraints.gridwidth = 2;
        constraints.gridy++; constraints.gridx=0;
        constraints.fill = GridBagConstraints.NONE;
        controlJPanel.add( buttonJPanel, constraints );
        
        getContentPane().add( controlJPanel );
        
        // As per http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
        Runnable runner = new FrameShower( this );
        EventQueue.invokeLater(runner);
        
        cameraNameJComboBox.setModel( new DefaultComboBoxModel( Settings.Cameras ) );
        cameraNameJComboBox.setSelectedIndex( 0 );
    }
    
    
    
    
    
    /**
     *  method that closes the frame and gets rid of it
     */
    private void getRid() {
        setVisible( false );
        dispose();
    }
    
    
    
    /**
     *  method that analyses the user initiated action and performs what the user requested
     *
     * @param e
     */
    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == cancelJButton ) {
            File targetDir = targetDirChooser.getDirectory();
            if ( targetDir.exists() ) {
                Settings.memorizeCopyLocation( targetDir.toString() );
            }
            getRid();
        } else if ( e.getSource() == okJButton ) {
            //new Thread(this).start();
            Thread t = new Thread() {
                @Override
                public void run() {
                    Camera cam = (Camera) cameraNameJComboBox.getSelectedItem();
                    File targetDir = targetDirChooser.getDirectory();
                    Settings.memorizeCopyLocation( targetDir.toString() );
                    addPictures( rootNode, cam, targetDir, newPicturesJRadioButton.isSelected(), missingPicturesJRadioButton.isSelected(), retainDirectoriesJCheckBox.isSelected(), selectedCategories );
                }
            };
            t.start();
            getRid();
        }
    }
    
    /**
     *  this method adds the pictures from the camera. It will best be
     *  called from inside another thread.
     *
     *   @param  rootNode   The node at which to add the pictures
     *   @param  cam  The Camera object for which the pictures are to be loaded.
     *   @param  targetDir  The target directory. It doesn't have to exist; it and it's parents will be created.
     *   @param  newPictures  Indicates that only the new pictures should be added.
     *   @param  missingPictures  Indicates that only the missing pictures should be added.
     *   @param  retainDirectories  Indicates that the directory structure should be retained in the added pictures.
     *   @param  selectedCategories Categories that are applied to the loaded pictures.
     */
    public static void addPictures( SortableDefaultMutableTreeNode rootNode, Camera cam, File targetDir, boolean newPictures, boolean missingPictures, boolean retainDirectories, HashSet<Object> selectedCategories ) {
        logger.info("AddFromCamera.addPictures: running");
        File sourceDir = new File( cam.getCameraMountPoint() );
        // give the OS time to mount properly:
        // try { sleep (1000); } catch ( InterruptedException x) {}
        if ( ! Tools.hasPictures( sourceDir ) ) {
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString("copyAddPicturesNoPicturesError"),
                    Settings.jpoResources.getString("genericError"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        targetDir.mkdirs();
        
        String groupName = cam.getDescription()
                + " "
                + Tools.currentDate( Settings.addFromCameraDateFormat );
        
        
        SortableDefaultMutableTreeNode newNode = null;
        if ( newPictures ) {
            logger.info("AddFromCamera.addPictures: only new pictures should be loaded from camera");
            newNode = rootNode.copyAddPictures( sourceDir, targetDir, groupName, cam, retainDirectories, selectedCategories );
        } else if ( missingPictures ) {
            logger.info("AddFromCamera.addPictures: only missing pictures should be loaded from camera");
            newNode = rootNode.copyAddPictures( sourceDir, targetDir, groupName, true, retainDirectories, selectedCategories );
        } else {
            logger.info("AddFromCamera.addPictures: AllPictures should be loaded from camera");
            newNode = rootNode.copyAddPictures( sourceDir, targetDir, groupName, false, retainDirectories, selectedCategories  );
        }
        
        if ( newNode != null ) {
            Jpo.positionToNode( newNode );
        }
    }
    
    
    /**
     *  This method gets invoked from the CategoryUsageJFrame object when a selection has been made.
     */
    public void categoriesChosen(  HashSet<Object> selectedCategories  ) {
        this.selectedCategories = selectedCategories;
    }

    
}
