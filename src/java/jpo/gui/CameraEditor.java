package jpo.gui;

import jpo.dataModel.Settings;
import jpo.*;
import jpo.dataModel.Camera;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/*
CameraEditor.java: a class that creates a GUI that allows the user to edit the definitions of his cameras.
 
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
 *   This class creates a JFrame and then presents the user with the cameras JPO knows about. The user can
 *   add, remove and modify the cameras. The class uses the SingleCameraEditor to edit the individual
 *   attributes of the cameras.
 *   @author Richard Eigenmann  richard.eigenmann@gmail.com
 */
public class CameraEditor extends JFrame implements ActionListener {
    
    /**
     * The root node of the JTree
     */
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode( "Cameras" );
    
    /**
     *  The default tree model
     */
    private DefaultTreeModel treeModel = new DefaultTreeModel( rootNode );
    /**
     * The JTree to select and manipulate the cameras
     */
    private JTree cameraJTree = new JTree( treeModel );
    
    
    /**
     *  keep a copy of the old cameras so we can restore them with the cancel button.
     */
    private final Vector<Camera> backupCameras;
            
    /**
     *  This component handles all the editing of the camera information.
     */
    private SingleCameraEditor singleCameraEditor = new SingleCameraEditor();
    
    /**
     *   Creates a JFrame with the GUI elements and buttons that can
     *   start and stop the reconciliation. The reconciliation itself
     *   runs in it's own Thread.
     *
     */
    public CameraEditor() {
        setSize( 500, 400 );
        setLocationRelativeTo( Settings.anchorFrame );
        setTitle( Settings.jpoResources.getString( "CameraEditor" ) );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                singleCameraEditor.saveCamera();
                getRid();
            }
        });
        
        // take a backup
        backupCameras = new Vector<Camera>();
        for ( Camera c : Settings.Cameras ) {
            Camera b = new Camera();
            b.setDescription( c.getDescription() );
            b.setCameraMountPoint( c.getCameraMountPoint() );
            b.setLastConnectionStatus( c.getLastConnectionStatus() );
            b.setMonitorForNewPictures( c.getMonitorForNewPictures() );
            b.setOldImage( c.getOldImage() ); // shallow copy!
            b.setUseFilename( c.getUseFilename() );
            backupCameras.add( b );
        }
        
        loadTree();
        cameraJTree. getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        cameraJTree. putClientProperty( "JTree.lineStyle", "Angled" );
        cameraJTree. setOpaque( true );
        cameraJTree. setShowsRootHandles( true );
        cameraJTree.expandPath( new TreePath( rootNode ) );
        cameraJTree.addTreeSelectionListener( new TreeSelectionListener() {
            public void valueChanged( TreeSelectionEvent e ) {
                singleCameraEditor.saveCamera();
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) cameraJTree.getLastSelectedPathComponent();
                if ( n != null ) {
                    Object o = n.getUserObject();
                    if ( o instanceof Camera ) {
                        singleCameraEditor.setCamera( (Camera) o );
                    } else {
                        singleCameraEditor.setCamera( null );
                    }
                } else {
                    singleCameraEditor.setCamera( null );
                }
            }
        } );
        
        
        JSplitPane hjsp = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, cameraJTree, singleCameraEditor );
        
        
        //  Button Panel
        JPanel buttonJPanel = new JPanel();
        
        
        JButton addJButton = new JButton( Settings.jpoResources.getString("addJButton") );
        addJButton.setPreferredSize( Settings.defaultButtonDimension );
        addJButton.setMinimumSize( Settings.defaultButtonDimension );
        addJButton.setMaximumSize( Settings.defaultButtonDimension );
        addJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        addJButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                singleCameraEditor.saveCamera();
                Camera cam = new Camera();
                Settings.Cameras.add( cam );
                singleCameraEditor.setCamera( cam );
                DefaultMutableTreeNode newChild = new DefaultMutableTreeNode( cam );
                treeModel.insertNodeInto( newChild, rootNode,
                        rootNode.getChildCount());
                
            }
        } );
        buttonJPanel.add( addJButton );
        
        JButton deleteJButton = new JButton( Settings.jpoResources.getString("deleteJButton") );
        deleteJButton.setPreferredSize( Settings.defaultButtonDimension );
        deleteJButton.setMinimumSize( Settings.defaultButtonDimension );
        deleteJButton.setMaximumSize( Settings.defaultButtonDimension );
        deleteJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        deleteJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) cameraJTree.getLastSelectedPathComponent();
                if ( n != null ) {
                    treeModel.removeNodeFromParent( n );
                    synchronized( Settings.Cameras ) {
                        Settings.Cameras.remove( n.getUserObject() );
                    }
                }
            }
        });
        buttonJPanel.add( deleteJButton );
        
        JButton cancelJButton = new JButton( Settings.jpoResources.getString("genericCancelText") );
        cancelJButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
        cancelJButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        cancelJButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Settings.Cameras = backupCameras;
                getRid();
            }
        } );
        buttonJPanel.add( cancelJButton );
        
        JButton closeJButton = new JButton( Settings.jpoResources.getString("closeJButton") );
        closeJButton.setPreferredSize( Settings.defaultButtonDimension );
        closeJButton.setMinimumSize( Settings.defaultButtonDimension );
        closeJButton.setMaximumSize( Settings.defaultButtonDimension );
        closeJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        closeJButton.setDefaultCapable( true );
        getRootPane().setDefaultButton( closeJButton );
        closeJButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                singleCameraEditor.saveCamera();
                getRid();
            }
        } );
        buttonJPanel.add( closeJButton );
        
        JSplitPane vjsp = new JSplitPane( JSplitPane.VERTICAL_SPLIT, hjsp, buttonJPanel );
        getContentPane().add( vjsp );
        
        //  As per http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
        Runnable runner = new FrameShower( this );
        EventQueue.invokeLater(runner);
        
        Object o = treeModel.getChild( rootNode, 0 );
        if ( o != null ) {
            cameraJTree.setSelectionPath( new TreePath( ((DefaultMutableTreeNode) o).getPath() ) );
        }
    }
    
    /**
     *  method that closes the frame and gets rid of it
     */
    private void getRid() {
        for ( Camera c : Settings.Cameras ) {
            c.setLastConnectionStatus( false ); // so that the daemon gets a chance
        }
        setVisible( false );
        dispose();
    }
    
    /**
     *  empties and reloads the cameras JTree
     */
    private void loadTree() {
        rootNode.removeAllChildren();
        treeModel.nodeStructureChanged( rootNode );
        for ( Camera c : Settings.Cameras ) {
            DefaultMutableTreeNode cameraNode = new DefaultMutableTreeNode( c );
            rootNode.add( cameraNode );
        }
        treeModel.nodeStructureChanged( rootNode );
    }
    
    /**
     *  Implementing a listener so that we find out if the save button was clicked on the singleCameraEditor
     * @param e 
     */
    public void actionPerformed( ActionEvent e ) {
        loadTree();
    }
}
