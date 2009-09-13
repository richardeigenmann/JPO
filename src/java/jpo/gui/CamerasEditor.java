package jpo.gui;

import jpo.dataModel.Settings;
import jpo.*;
import jpo.dataModel.Camera;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import jpo.dataModel.Tools;

/*
CamerasEditor.java: A Controller and View of the Cameras allows adding and removing

Copyright (C) 2002 - 2009  Richard Eigenmann.
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
 * A Controller and View of the camera. The user can
 * add, remove and modify the cameras. The class uses the CameraEditor to edit the individual
 * attributes of the cameras.
 * @author Richard Eigenmann  richard.eigenmann@gmail.com
 */
public class CamerasEditor
        extends JFrame {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( CamerasEditor.class.getName() );

    /**
     * The root node of the JTree
     */
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode( "Cameras" );

    /**
     * The default tree model with a special handler to save default editor changes
     * into the userObjects instead of replacing the user objects with a String.
     * @see <a href="http://www.informit.com/articles/article.aspx?p=26327&seqNum=25">InformIT article</a>
     */
    private DefaultTreeModel treeModel = new DefaultTreeModel( rootNode ) {

        @Override
        public void valueForPathChanged( TreePath path, Object newValue ) {
            logger.info( String.format( "valueForPathchanged on node %s, to value %s", path.toString(), newValue.toString() ) );
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            Camera cam = (Camera) node.getUserObject();
            cameraRenamed( cam, newValue.toString() );

        }
    };

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
    private CameraEditor singleCameraEditor = new CameraEditor();


    /**
     * Constructor
     */
    public CamerasEditor() {
        Tools.checkEDT();
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
        } );

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
        cameraJTree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        cameraJTree.putClientProperty( "JTree.lineStyle", "Angled" );
        cameraJTree.setOpaque( true );
        cameraJTree.setShowsRootHandles( true );
        cameraJTree.expandPath( new TreePath( rootNode ) );
        cameraJTree.addTreeSelectionListener( new TreeSelectionListener() {

            public void valueChanged( TreeSelectionEvent e ) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) cameraJTree.getLastSelectedPathComponent();
                if ( n == null ) {
                    return;
                }
                Object o = n.getUserObject();
                if ( o instanceof Camera ) {
                    Camera cam = (Camera) o;
                    cameraPicked( cam );
                } else {
                    logger.fine( "Very odd: the camera JTree repored a valueChanged but there is no selected camera. Could be the root node" );
                }
            }
        } );
        cameraJTree.setEditable( true );
        cameraJTree.addKeyListener( new KeyAdapter() {

            @Override
            public void keyTyped( KeyEvent e ) {
                super.keyTyped( e );
                if ( e.getKeyChar() == KeyEvent.VK_DELETE ) {
                    deleteCameraAction();
                }
            }
        } );

        JScrollPane camerasJScrollPane = new JScrollPane( cameraJTree );
        camerasJScrollPane.setPreferredSize( new Dimension( 165, 200 ) );

        JPanel addDeleteButtonPanel = new JPanel();
        JButton addJButton = new JButton( Settings.jpoResources.getString( "addJButton" ) );
        addJButton.setPreferredSize( Settings.defaultButtonDimension );
        addJButton.setMinimumSize( Settings.defaultButtonDimension );
        addJButton.setMaximumSize( Settings.defaultButtonDimension );
        addJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        addJButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
                        addCameraAction();
                    }
                } );
        addDeleteButtonPanel.add( addJButton );

        JButton deleteJButton = new JButton( Settings.jpoResources.getString( "deleteJButton" ) );
        deleteJButton.setPreferredSize( Settings.defaultButtonDimension );
        deleteJButton.setMinimumSize( Settings.defaultButtonDimension );
        deleteJButton.setMaximumSize( Settings.defaultButtonDimension );
        deleteJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        deleteJButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                deleteCameraAction();
            }
        } );
        addDeleteButtonPanel.add( deleteJButton );

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.Y_AXIS ) );
        leftPanel.add( camerasJScrollPane );
        leftPanel.add( addDeleteButtonPanel );

        JSplitPane hjsp = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, leftPanel, singleCameraEditor );


        //  Button Panel
        JPanel buttonJPanel = new JPanel();




        JButton cancelJButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );
        cancelJButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
        cancelJButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        cancelJButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                Settings.Cameras = backupCameras;
                getRid();

            }
        } );
        buttonJPanel.add( cancelJButton );

        JButton closeJButton = new JButton( Settings.jpoResources.getString( "closeJButton" ) );
        closeJButton.setPreferredSize( Settings.defaultButtonDimension );
        closeJButton.setMinimumSize( Settings.defaultButtonDimension );
        closeJButton.setMaximumSize( Settings.defaultButtonDimension );
        closeJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        closeJButton.setDefaultCapable( true );
        getRootPane().setDefaultButton( closeJButton );
        closeJButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                singleCameraEditor.saveCamera();
                getRid();

            }
        } );
        buttonJPanel.add( closeJButton );

        JSplitPane vjsp = new JSplitPane( JSplitPane.VERTICAL_SPLIT, hjsp, buttonJPanel );
        getContentPane().add( vjsp );

        Object o = treeModel.getChild( rootNode, 0 );
        if ( o != null ) {
            cameraJTree.setSelectionPath( new TreePath( ( (DefaultMutableTreeNode) o ).getPath() ) );
        }

        pack();
        setLocationRelativeTo( Settings.anchorFrame );
        setVisible( true );


    }


    /**
     * Executes the steps to add a camera.
     */
    private void addCameraAction() {
        singleCameraEditor.saveCamera();
        Camera cam = new Camera();
        Settings.Cameras.add( cam );
        singleCameraEditor.setCamera( cam );
        DefaultMutableTreeNode newChild = new DefaultMutableTreeNode( cam );
        int childNodes = rootNode.getChildCount();
        treeModel.insertNodeInto( newChild, rootNode, childNodes );
        cameraJTree.setSelectionRow( childNodes + 1 );
        cameraJTree.scrollRowToVisible( childNodes + 1 );
    }


    /**
     * Deletes camera from the list
     */
    private void deleteCameraAction() {
        DefaultMutableTreeNode n = (DefaultMutableTreeNode) cameraJTree.getLastSelectedPathComponent();
        if ( n == null ) {
            logger.warning( "got a delete event without a node. Ignorning" );
            return;
        }
        if ( n.isRoot() ) {
            logger.fine( "Not allowing the root node to be deleted" );
            return;
        }
        DefaultMutableTreeNode nextsibling = n.getNextSibling();
        DefaultMutableTreeNode previousNode = n.getPreviousNode();
        treeModel.removeNodeFromParent( n );
        synchronized ( Settings.Cameras ) {
            Settings.Cameras.remove( n.getUserObject() );
        }
        if ( nextsibling != null ) {
            cameraJTree.setSelectionPath( new TreePath( nextsibling.getPath() ) );
        } else {
            cameraJTree.setSelectionPath( new TreePath( previousNode.getPath() ) );
        }

    }


    /**
     * Gets called when a camera is picked in the tree
     * @param cam The camera that the user picked
     */
    private void cameraPicked( Camera cam ) {
        singleCameraEditor.setCamera( cam );
    }


    /**
     * Gets called when a camera is renamed in the JTree
     * @param cam The camera that the user renamed
     * @param newName The new name it got
     */
    private void cameraRenamed( Camera cam, String newName ) {
        cam.setDescription( newName );
        singleCameraEditor.setCamera( cam );
    }


    /**
     * Closes the frame and gets rid of it and set all Cameras to disconnected
     * to that the daemon can scan them afresh.
     */
    private void getRid() {
        for ( Camera c : Settings.Cameras ) {
            c.setLastConnectionStatus( false ); // so that the daemon gets a chance
        }

        setVisible( false );
        dispose();

    }


    /**
     * Empties and reloads the cameras JTree
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
}
