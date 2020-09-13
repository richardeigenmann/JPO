package org.jpo.gui;

import org.jpo.datamodel.Camera;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*
CamerasEditor.java: A Controller and View of the cameras allows adding and removing

Copyright (C) 2002 - 2020 Richard Eigenmann.
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
 *
 * @author Richard Eigenmann  richard.eigenmann@gmail.com
 */
public class CamerasEditor extends JFrame {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(CamerasEditor.class.getName());

    /**
     * The root node of the JTree of cameras
     */
    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Cameras");

    /**
     * The default tree model with a special handler to save default editor changes
     * into the userObjects instead of replacing the user objects with a String.
     *
     * @see <a href="http://www.informit.com/articles/article.aspx?p=26327&seqNum=25">InformIT article</a>
     */
    private final DefaultTreeModel treeModel = new DefaultTreeModel(rootNode) {

        @Override
        public void valueForPathChanged(final TreePath path, final Object newValue) {
            LOGGER.info(String.format("valueForPathChanged on node %s, to value %s", path.toString(), newValue.toString()));
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            final Camera cam = (Camera) node.getUserObject();
            cam.setDescription(newValue.toString());
            singleCameraEditor.setCamera(cam);
        }
    };

    /**
     * The JTree to select and manipulate the cameras
     */
    private final JTree cameraJTree;

    /**
     * keep a copy of the old cameras so we can restore them with the cancel button.
     */
    private final List<Camera> backupCameras;

    /**
     * This component handles all the editing of the camera information.
     */
    private final CameraEditor singleCameraEditor = new CameraEditor();


    /**
     * Constructor
     */
    public CamerasEditor() {
        Tools.checkEDT();
        this.cameraJTree = new JTree(treeModel);
        setSize(500, 400);
        setLocationRelativeTo(Settings.getAnchorFrame());
        setTitle(Settings.jpoResources.getString("CameraEditor"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                singleCameraEditor.saveCamera();
                getRid();
            }
        });

        // take a backup
        backupCameras = new ArrayList<>();
        Settings.getCameras().stream().map((c) -> {
            Camera b = new Camera();
            b.setDescription(c.getDescription());
            b.setCameraMountPoint(c.getCameraMountPoint());
            b.setLastConnectionStatus(c.getLastConnectionStatus());
            b.setMonitorForNewPictures(c.getMonitorForNewPictures());
            b.setOldImage(c.getOldImage()); // shallow copy!
            b.setUseFilename(c.getUseFilename());
            return b;
        }).forEach(backupCameras::add);

        loadTree();
        cameraJTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        cameraJTree.putClientProperty("JTree.lineStyle", "Angled");
        cameraJTree.setOpaque(true);
        cameraJTree.setShowsRootHandles(true);
        cameraJTree.expandPath(new TreePath(rootNode));
        cameraJTree.addTreeSelectionListener((TreeSelectionEvent e) -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) cameraJTree.getLastSelectedPathComponent();
            if (node == null) {
                return;
            }
            Object object = node.getUserObject();
            if (object instanceof Camera) {
                Camera camera = (Camera) object;
                cameraPicked(camera);
            } else {
                LOGGER.fine("Very odd: the camera JTree reported a valueChanged but there is no selected camera. Could be the root node");
            }
        });
        cameraJTree.setEditable(true);
        cameraJTree.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyChar() == KeyEvent.VK_DELETE) {
                    deleteCameraAction();
                }
            }
        });

        final JScrollPane camerasJScrollPane = new JScrollPane(cameraJTree);
        camerasJScrollPane.setPreferredSize(new Dimension(165, 200));

        final JPanel addDeleteButtonPanel = new JPanel();
        final JButton addJButton = new JButton(Settings.jpoResources.getString("addJButton"));
        addJButton.setPreferredSize(Settings.defaultButtonDimension);
        addJButton.setMinimumSize(Settings.defaultButtonDimension);
        addJButton.setMaximumSize(Settings.defaultButtonDimension);
        addJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        addJButton.addActionListener((ActionEvent e) -> addCameraAction());
        addDeleteButtonPanel.add(addJButton);

        final JButton deleteJButton = new JButton(Settings.jpoResources.getString("deleteJButton"));
        deleteJButton.setPreferredSize(Settings.defaultButtonDimension);
        deleteJButton.setMinimumSize(Settings.defaultButtonDimension);
        deleteJButton.setMaximumSize(Settings.defaultButtonDimension);
        deleteJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        deleteJButton.addActionListener((ActionEvent e) -> deleteCameraAction());
        addDeleteButtonPanel.add(deleteJButton);

        final JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(camerasJScrollPane);
        leftPanel.add(addDeleteButtonPanel);

        final JSplitPane hjsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, singleCameraEditor);


        //  Button Panel
        final JPanel buttonJPanel = new JPanel();


        final JButton cancelJButton = new JButton(Settings.jpoResources.getString("genericCancelText"));
        cancelJButton.setPreferredSize(Settings.defaultButtonDimension);
        cancelJButton.setMinimumSize(Settings.defaultButtonDimension);
        cancelJButton.setMaximumSize(Settings.defaultButtonDimension);
        cancelJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        cancelJButton.addActionListener((ActionEvent e) -> {
            Settings.setCameras(backupCameras);
            getRid();
        });
        buttonJPanel.add(cancelJButton);

        final JButton closeJButton = new JButton(Settings.jpoResources.getString("closeJButton"));
        closeJButton.setPreferredSize(Settings.defaultButtonDimension);
        closeJButton.setMinimumSize(Settings.defaultButtonDimension);
        closeJButton.setMaximumSize(Settings.defaultButtonDimension);
        closeJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        closeJButton.setDefaultCapable(true);
        getRootPane().setDefaultButton(closeJButton);
        closeJButton.addActionListener((ActionEvent e) -> {
            singleCameraEditor.saveCamera();
            getRid();
        });
        buttonJPanel.add(closeJButton);

        final JSplitPane vjsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, hjsp, buttonJPanel);
        getContentPane().add(vjsp);

        pack();
        setLocationRelativeTo(Settings.getAnchorFrame());
        setVisible(true);
    }


    /**
     * Executes the steps to add a camera.
     */
    private void addCameraAction() {
        singleCameraEditor.saveCamera();
        final Camera cam = new Camera();
        Settings.getCameras().add(cam);
        singleCameraEditor.setCamera(cam);
        final DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(cam);
        final int childNodes = rootNode.getChildCount();
        treeModel.insertNodeInto(newChild, rootNode, childNodes);
        cameraJTree.setSelectionRow(childNodes + 1);
        cameraJTree.scrollRowToVisible(childNodes + 1);
    }


    /**
     * Deletes camera from the list
     */
    private void deleteCameraAction() {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) cameraJTree.getLastSelectedPathComponent();
        if (node == null) {
            LOGGER.warning("got a delete event without a node. Ignoring");
            return;
        }
        if (node.isRoot()) {
            LOGGER.fine("Not allowing the root node to be deleted");
            return;
        }
        final DefaultMutableTreeNode nextSibling = node.getNextSibling();
        final DefaultMutableTreeNode previousNode = node.getPreviousNode();
        treeModel.removeNodeFromParent(node);
        synchronized (Settings.getCameras()) {
            Settings.getCameras().remove(node.getUserObject());
        }
        if (nextSibling != null) {
            cameraJTree.setSelectionPath(new TreePath(nextSibling.getPath()));
        } else {
            cameraJTree.setSelectionPath(new TreePath(previousNode.getPath()));
        }

    }


    /**
     * Gets called when a camera is picked in the tree
     *
     * @param cam The camera that the user picked
     */
    private void cameraPicked(final Camera cam) {
        singleCameraEditor.setCamera(cam);
    }


    /**
     * Closes the frame and gets rid of it and set all cameras to disconnected
     * to that the daemon can scan them afresh.
     */
    private void getRid() {
        Settings.getCameras().forEach((c) -> {
            c.setLastConnectionStatus(false); // so that the daemon gets a chance
        });

        setVisible(false);
        dispose();

    }


    /**
     * Empties and reloads the cameras JTree
     */
    private void loadTree() {
        Tools.checkEDT();
        rootNode.removeAllChildren();
        treeModel.nodeStructureChanged(rootNode);
        for (final Camera c : Settings.getCameras()) {
            DefaultMutableTreeNode cameraNode = new DefaultMutableTreeNode(c);
            rootNode.add(cameraNode);
        }

        treeModel.nodeStructureChanged(rootNode);
    }
}
