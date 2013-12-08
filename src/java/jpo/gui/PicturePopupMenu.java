package jpo.gui;

import jpo.dataModel.NodeNavigatorInterface;
import jpo.dataModel.UserFunctionsChangeListener;
import jpo.dataModel.Tools;
import jpo.dataModel.CopyLocationsChangeListener;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.PictureInfo;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpo.dataModel.RecentDropNodeListener;
import webserver.Webserver;

/*
 PicturePopupMenu.java:  a popup menu for pictures

 Copyright (C) 2002 - 2013  Richard Eigenmann.
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
 * This class generates a popup menu over a picture node.
 *
 */
public class PicturePopupMenu
        extends JPopupMenu
        implements RecentDropNodeListener,
        CopyLocationsChangeListener,
        UserFunctionsChangeListener {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger(PicturePopupMenu.class.getName());
    /**
     * array of menu items that allows the user to call up a user function
     *
     *
     */
    private final JMenuItem[] userFunctionsJMenuItems = new JMenuItem[Settings.maxUserFunctions];
    /**
     * array of menu items that allows the user to copy the picture to a
     * memorised file location
     *
     *
     */
    private final JMenuItem[] copyLocationsJMenuItems = new JMenuItem[Settings.MAX_MEMORISE];
    /**
     * array of menu items that allows the user to copy pictures to a recently
     * used zip file
     *
     *
     */
    private final JMenuItem[] memorizedZipFileJMenuItems = new JMenuItem[Settings.MAX_MEMORISE];
    /**
     * a separator for the Move menu. Declared here because other class methods
     * want to turn on and off visible.
     */
    private final JSeparator movePictureNodeSeparator = new JSeparator();
    /**
     * This array of JMenuItems memorises the most recent drop locations and
     * allows The user to quickly select a recently used drop location for the
     * next drop.
     */
    private final JMenuItem[] recentDropNodes = new JMenuItem[Settings.MAX_DROPNODES];

    /**
     * Constructor for the PicturePopupMenu where we do have a
     * {@link PictureViewer} that should receive the picture. TODO: Fix the way
     * this is being called because the whole business of figuring out whether
     * this is a single node or multi node is silly TODO: Decouple the
     * presentation from the functionality
     *
     * @param setOfNodes The set of nodes from which the popup picture is coming
     * @param idx	The picture of the set for which the popup is being shown.
     */
    public PicturePopupMenu(NodeNavigatorInterface setOfNodes, int idx) {
        this.mySetOfNodes = setOfNodes;
        this.index = idx;
        this.popupNode = mySetOfNodes.getNode(index);

        // Title
        String title = "Picture Popup Menu";
        if ((Settings.pictureCollection.countSelectedNodes() > 1) && (Settings.pictureCollection.isSelected(popupNode))) {
            title = String.format("%d nodes", Settings.pictureCollection.countSelectedNodes());
        } else {
            Object uo = popupNode.getUserObject();
            if (uo instanceof PictureInfo) {
                title = ((PictureInfo) uo).getDescription();
            }
            // trim title length to 25 characters if longer
            if (title.length() > 25) {
                title = title.substring(0, 25) + "...";
            }

        }

        setLabel(title);
        JMenuItem menuTitle = new JMenuItem(title);
        menuTitle.setEnabled(false);
        add(menuTitle);
        addSeparator();

        // Show Picture button
        JMenuItem pictureShowJMenuItem = new JMenuItem(Settings.jpoResources.getString("pictureShowJMenuItemLabel"));
        pictureShowJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                requestShowPicture();
            }
        });
        add(pictureShowJMenuItem);

        // Map button
        JMenuItem mapShowJMenuItem = new JMenuItem(Settings.jpoResources.getString("mapShowJMenuItemLabel"));
        mapShowJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                requestShowMap();
            }
        });
        add(mapShowJMenuItem);

        // Navigate to menu
        SortableDefaultMutableTreeNode[] parentNodes = Settings.pictureCollection.findParentGroups(popupNode);
        JMenu navigationJMenu = new JMenu(Settings.jpoResources.getString("navigationJMenu"));
        for (SortableDefaultMutableTreeNode parentNode : parentNodes) {
            JMenuItem navigateToRootNode = new JMenuItem(parentNode.getUserObject().toString());
            final SortableDefaultMutableTreeNode targetNode = parentNode;
            navigateToRootNode.addActionListener(new ActionListener() {

                final SortableDefaultMutableTreeNode node = targetNode;

                @Override
                public void actionPerformed(ActionEvent e) {
                    Jpo.positionToNode(node);
                }
            });
            navigationJMenu.add(navigateToRootNode);
        }
        add(navigationJMenu);

        // Categories
        JMenuItem categoryUsagetJMenuItem = new JMenuItem(Settings.jpoResources.getString("categoryUsagetJMenuItem"));
        categoryUsagetJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                requestCategories();
            }
        });
        add(categoryUsagetJMenuItem);

        JMenuItem pictureMailSelectJMenuItem = new JMenuItem();
        JMenuItem pictureMailUnselectAllJMenuItem = new JMenuItem(Settings.jpoResources.getString("pictureMailUnselectAllJMenuItem"));
        if (popupNode.getPictureCollection().isMailSelected(popupNode)) {
            pictureMailSelectJMenuItem.setText(Settings.jpoResources.getString("pictureMailUnselectJMenuItem"));
            pictureMailSelectJMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    requestSelectForEmail();

                }
            });
            add(pictureMailSelectJMenuItem);

            pictureMailUnselectAllJMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    doClearMailSelection();
                }
            });
            add(pictureMailUnselectAllJMenuItem);
        } else {
            pictureMailSelectJMenuItem.setText(Settings.jpoResources.getString("pictureMailSelectJMenuItem"));
            pictureMailSelectJMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    addToMailSelection();
                }
            });
            add(pictureMailSelectJMenuItem);
        }
        JMenu userFunctionsJMenu = new JMenu(Settings.jpoResources.getString("userFunctionsJMenu"));
        for (int i = 0; i < Settings.maxUserFunctions; i++) {
            final int userfunction = i;
            userFunctionsJMenuItems[i] = new JMenuItem();
            userFunctionsJMenuItems[i].addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Tools.runUserFunction(userfunction, (PictureInfo) popupNode.getUserObject());
                    } catch (ClassCastException x) {
                        LOGGER.severe(x.getMessage());
                        // Well, it was the wrong type
                    } catch (NullPointerException x) {
                        // Well, it wasn't a good node anyway.
                        LOGGER.severe(x.getMessage());
                    }
                }
            });
            userFunctionsJMenu.add(userFunctionsJMenuItems[i]);
        }
        add(userFunctionsJMenu);
        userFunctionsChanged();

        JMenu rotationJMenu = new JMenu(Settings.jpoResources.getString("rotation"));
        if (popupNode.getPictureCollection().getAllowEdits()) {
            add(rotationJMenu);
            JMenuItem rotate90JMenuItem = new JMenuItem(Settings.jpoResources.getString("rotate90"));
            rotate90JMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    rotatePicture(90);
                }
            });
            JMenuItem rotate180JMenuItem = new JMenuItem(Settings.jpoResources.getString("rotate180"));
            rotate180JMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    rotatePicture(180);
                }
            });
            JMenuItem rotate270JMenuItem = new JMenuItem(Settings.jpoResources.getString("rotate270"));
            rotate270JMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    rotatePicture(270);
                }
            });
            JMenuItem rotate0JMenuItem = new JMenuItem(Settings.jpoResources.getString("rotate0"));
            rotate0JMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    noRotation();
                }
            });
            rotationJMenu.add(rotate90JMenuItem);
            rotationJMenu.add(rotate180JMenuItem);
            rotationJMenu.add(rotate270JMenuItem);
            rotationJMenu.add(rotate0JMenuItem);
        }
        JMenuItem pictureRefreshJMenuItem = new JMenuItem(Settings.jpoResources.getString("pictureRefreshJMenuItem"));

        pictureRefreshJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                refreshSelectedPictures();
            }
        });
        add(pictureRefreshJMenuItem);

        if (popupNode.getPictureCollection().getAllowEdits()) {
            JMenu movePictureNodeJMenu = new JMenu(Settings.jpoResources.getString("moveNodeJMenuLabel"));
            add(movePictureNodeJMenu);

            for (int i = 0; i < Settings.MAX_DROPNODES; i++) {
                final int dropnode = i;
                recentDropNodes[i] = new JMenuItem();
                recentDropNodes[i].addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        moveToLastChild(Settings.recentDropNodes[dropnode]);
                        Settings.memorizeGroupOfDropLocation(Settings.recentDropNodes[dropnode]);
                    }
                });
                movePictureNodeJMenu.add(recentDropNodes[i]);
            }
            movePictureNodeJMenu.add(movePictureNodeSeparator);
            recentDropNodesChanged();

            JMenuItem movePictureToTopJMenuItem = new JMenuItem(Settings.jpoResources.getString("movePictureToTopJMenuItem"));
            movePictureToTopJMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (Settings.pictureCollection.countSelectedNodes() < 1) {
                        popupNode.moveNodeToTop();
                    } else {
                        for (SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes()) {
                            if (selectedNode.getUserObject() instanceof PictureInfo) {
                                selectedNode.moveNodeToTop();
                            }
                        }
                    }
                }
            });
            movePictureNodeJMenu.add(movePictureToTopJMenuItem);

            JMenuItem movePictureUpJMenuItem = new JMenuItem(Settings.jpoResources.getString("movePictureUpJMenuItem"));
            movePictureUpJMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (Settings.pictureCollection.countSelectedNodes() < 1) {
                        popupNode.moveNodeUp();
                    } else {
                        for (SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes()) {
                            if (selectedNode.getUserObject() instanceof PictureInfo) {
                                selectedNode.moveNodeUp();
                            }
                        }
                    }
                }
            });
            movePictureNodeJMenu.add(movePictureUpJMenuItem);

            JMenuItem movePictureDownJMenuItem = new JMenuItem(Settings.jpoResources.getString("movePictureDownJMenuItem"));
            movePictureDownJMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (Settings.pictureCollection.countSelectedNodes() < 1) {
                        popupNode.moveNodeDown();
                    } else {
                        /*Enumeration<SortableDefaultMutableTreeNode> selection = Settings.pictureCollection.getSelectedNodesAsVector().elements();
                         SortableDefaultMutableTreeNode n;
                         while ( selection.hasMoreElements() ) {
                         n = selection.nextElement();
                         if ( n.getUserObject() instanceof PictureInfo ) {
                         n.moveNodeDown();
                         }
                         }*/
                        for (SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes()) {
                            if (selectedNode.getUserObject() instanceof PictureInfo) {
                                selectedNode.moveNodeDown();
                            }
                        }
                    }
                }
            });
            movePictureNodeJMenu.add(movePictureDownJMenuItem);

            JMenuItem movePictureToBottomJMenuItem = new JMenuItem(Settings.jpoResources.getString("movePictureToBottomJMenuItem"));
            movePictureToBottomJMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (Settings.pictureCollection.countSelectedNodes() < 1) {
                        popupNode.moveNodeToBottom();
                    } else {
                        for (SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes()) {
                            if (selectedNode.getUserObject() instanceof PictureInfo) {
                                selectedNode.moveNodeToBottom();
                            }
                        }
                    }
                }
            });
            movePictureNodeJMenu.add(movePictureToBottomJMenuItem);

            JMenuItem indentJMenuItem = new JMenuItem(Settings.jpoResources.getString("indentJMenuItem"));
            indentJMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (Settings.pictureCollection.countSelectedNodes() < 1) {
                        popupNode.indentNode();
                    } else {
                        for (SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes()) {
                            if (selectedNode.getUserObject() instanceof PictureInfo) {
                                selectedNode.indentNode();
                            }
                        }
                    }
                }
            });
            movePictureNodeJMenu.add(indentJMenuItem);

            JMenuItem outdentJMenuItem = new JMenuItem(Settings.jpoResources.getString("outdentJMenuItem"));
            outdentJMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (Settings.pictureCollection.countSelectedNodes() < 1) {
                        popupNode.outdentNode();
                    } else {
                        for (SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes()) {
                            if (selectedNode.getUserObject() instanceof PictureInfo) {
                                selectedNode.outdentNode();
                            }
                        }
                    }
                }
            });
            movePictureNodeJMenu.add(outdentJMenuItem);

        }

        final JMenu copyImageJMenu = new JMenu(Settings.jpoResources.getString("copyImageJMenuLabel"));
        add(copyImageJMenu);

        final JMenuItem copyToNewLocationJMenuItem = new JMenuItem(Settings.jpoResources.getString("copyToNewLocationJMenuItem"));
        copyToNewLocationJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (Settings.pictureCollection.countSelectedNodes() < 1) {
                    SortableDefaultMutableTreeNode[] nodes = new SortableDefaultMutableTreeNode[1];
                    nodes[0] = popupNode;
                    TreeNodeController.copyToNewLocation(nodes);
                } else {
                    TreeNodeController.copyToNewLocation(Settings.pictureCollection.getSelectedNodes());
                }
            }
        });
        copyImageJMenu.add(copyToNewLocationJMenuItem);

        copyImageJMenu.addSeparator();

        for (int i = 0; i < Settings.MAX_MEMORISE; i++) {
            final int item = i;
            copyLocationsJMenuItems[i] = new JMenuItem();
            copyLocationsJMenuItems[i].addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (Settings.pictureCollection.countSelectedNodes() < 1) {
                        SortableDefaultMutableTreeNode[] nodes = new SortableDefaultMutableTreeNode[1];
                        nodes[0] = popupNode;
                        TreeNodeController.copyToLocation(nodes, new File(Settings.copyLocations[item]));
                    } else {
                        TreeNodeController.copyToLocation(Settings.pictureCollection.getSelectedNodes(), new File(Settings.copyLocations[item]));
                    }
                }
            });
            copyImageJMenu.add(copyLocationsJMenuItems[i]);
        }
        copyLocationsChanged();

        copyImageJMenu.addSeparator();

        final JMenuItem copyToNewZipfileJMenuItem = new JMenuItem(Settings.jpoResources.getString("copyToNewZipfileJMenuItem"));
        copyToNewZipfileJMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (Settings.pictureCollection.countSelectedNodes() < 1) {
                    SortableDefaultMutableTreeNode[] nodes = new SortableDefaultMutableTreeNode[1];
                    nodes[0] = popupNode;
                    TreeNodeController.copyToNewZipfile(nodes);
                } else {
                    TreeNodeController.copyToNewZipfile(Settings.pictureCollection.getSelectedNodes());
                }
            }
        });
        copyImageJMenu.add(copyToNewZipfileJMenuItem);

        for (int i = 0; i < Settings.MAX_MEMORISE; i++) {
            final int item = i;
            memorizedZipFileJMenuItems[i] = new JMenuItem();
            memorizedZipFileJMenuItems[i].addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (Settings.pictureCollection.countSelectedNodes() < 1) {
                        SortableDefaultMutableTreeNode[] nodes = new SortableDefaultMutableTreeNode[1];
                        nodes[0] = popupNode;
                        TreeNodeController.copyToZipfile(nodes, new File(Settings.memorizedZipFiles[item]));
                    } else {
                        TreeNodeController.copyToZipfile(Settings.pictureCollection.getSelectedNodes(), new File(Settings.memorizedZipFiles[item]));
                    }
                }
            });
            copyImageJMenu.add(memorizedZipFileJMenuItems[i]);
            if (Settings.memorizedZipFiles[i] != null) {
                memorizedZipFileJMenuItems[i].setText(Settings.memorizedZipFiles[i]);
                memorizedZipFileJMenuItems[i].setVisible(true);
            } else {
                memorizedZipFileJMenuItems[i].setVisible(false);
            }
        }

        // remove node
        if (popupNode.getPictureCollection().getAllowEdits()) {
            JMenuItem pictureNodeRemove = new JMenuItem(Settings.jpoResources.getString("pictureNodeRemove"));
            pictureNodeRemove.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    requestRemoveNode();

                }
            });
            add(pictureNodeRemove);

            JMenu fileOperationsJMenu = new JMenu(Settings.jpoResources.getString("FileOperations"));
            add(fileOperationsJMenu);

            JMenuItem fileRenameJMenuItem = new JMenuItem(Settings.jpoResources.getString("fileRenameJMenuItem"));
            fileRenameJMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (Settings.pictureCollection.countSelectedNodes() < 1) {
                        TreeNodeController.fileRename(popupNode);
                    } else {
                        for (SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes()) {
                            if (selectedNode.getUserObject() instanceof PictureInfo) {
                                TreeNodeController.fileRename(selectedNode);
                            }
                        }
                    }
                }
            });
            fileOperationsJMenu.add(fileRenameJMenuItem);

            // Delete
            JMenuItem fileDeleteJMenuItem = new JMenuItem(Settings.jpoResources.getString("fileDeleteJMenuItem"));
            fileDeleteJMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    requestDelete();
                }
            });
            fileOperationsJMenu.add(fileDeleteJMenuItem);
        }

        // Properties
        JMenuItem picturePropertiesMenuItem = new JMenuItem(Settings.jpoResources.getString("pictureEditJMenuItemLabel"));
        picturePropertiesMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                requestProperties();
            }
        });
        add(picturePropertiesMenuItem);

        JMenuItem consolidateHereMenuItem = new JMenuItem("Consolidate Here");
        consolidateHereMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                consolidateHere();
            }
        });
        add(consolidateHereMenuItem);

    }

    /**
     * Request to remove a node. What happens depends on the selection: If the
     * node on which the popup was performed is one of a selection of nodes, the
     * multi-delete dialog is opened. If, however it is not part of a
     * potentially existing selection then only the specified node will be
     * deleted.
     */
    private void requestRemoveNode() {
        SortableDefaultMutableTreeNode actionNode = mySetOfNodes.getNode(index);
        if ((Settings.pictureCollection.countSelectedNodes() > 1) && (Settings.pictureCollection.isSelected(actionNode))) {
            for (SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes()) {
                if (selectedNode.getUserObject() instanceof PictureInfo) {
                    selectedNode.deleteNode();
                }
            }
        } else {
            popupNode.deleteNode();
        }
    }

    /**
     * the Delete menu was clicked. What happens depends on the selection: If
     * the node on which the popup was performed is one of a selection of nodes,
     * the multi-delete dialog is opened. If, however it is not part of a
     * potentially existing selection then only the specified node will be
     * deleted.
     */
    private void requestDelete() {
        SortableDefaultMutableTreeNode actionNode = mySetOfNodes.getNode(index);
        if ((Settings.pictureCollection.countSelectedNodes() > 1) && (Settings.pictureCollection.isSelected(actionNode))) {
            multiDeleteDialog();
        } else {
            fileDelete(actionNode);
        }
    }

    /**
     * Brings up an are you sure dialog and then deletes the file.
     *
     * @param nodeToDelete The node to be deleted
     */
    public void fileDelete(SortableDefaultMutableTreeNode nodeToDelete) {
        Object userObj = nodeToDelete.getUserObject();
        if (!(userObj instanceof PictureInfo)) {
            return;
        }

        PictureInfo pi = (PictureInfo) userObj;
        File highresFile = pi.getHighresFile();
        if (highresFile == null) {
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                Settings.anchorFrame,
                Settings.jpoResources.getString("FileDeleteLabel") + highresFile.toString() + "\n" + Settings.jpoResources.getString("areYouSure"),
                Settings.jpoResources.getString("FileDeleteTitle"),
                JOptionPane.OK_CANCEL_OPTION);

        if (option == 0) {
            boolean ok = false;
            File lowresFile = pi.getLowresFile();
            if ((lowresFile != null) && (lowresFile.exists())) {
                ok = lowresFile.delete();
                if (!ok) //logger.info("File deleted: " + lowresFile.toString() );
                // else
                {
                    LOGGER.log(Level.INFO, "File deleted failed on: {0}", lowresFile.toString());
                }
            }

            if (highresFile.exists()) {
                ok = highresFile.delete();
                if (!ok) //logger.info("File deleted: " + highresFile.toString() );
                //else
                {
                    LOGGER.log(Level.INFO, "File deleted failed on: {0}", highresFile.toString());
                }
            }

            nodeToDelete.deleteNode();

            if (!ok) {
                JOptionPane.showMessageDialog(Settings.anchorFrame,
                        Settings.jpoResources.getString("fileDeleteError") + highresFile.toString(),
                        Settings.jpoResources.getString("genericError"),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Multi delete dialog
     */
    private void multiDeleteDialog() {
        JTextArea textArea = new JTextArea();
        textArea.setText("");
        for (SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes()) {
            if (selectedNode.getUserObject() instanceof PictureInfo) {
                textArea.append(((PictureInfo) selectedNode.getUserObject()).getHighresLocation() + "\n");
            }
        }
        textArea.append(Settings.jpoResources.getString("areYouSure"));

        int option = JOptionPane.showConfirmDialog(
                Settings.anchorFrame, //very annoying if the main window is used as it forces itself into focus.
                textArea,
                Settings.jpoResources.getString("FileDeleteLabel"),
                JOptionPane.OK_CANCEL_OPTION);

        if (option == 0) {
            for (SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes()) {
                PictureInfo pi;
                if (selectedNode.getUserObject() instanceof PictureInfo) {
                    pi = (PictureInfo) selectedNode.getUserObject();
                    boolean ok = false;
                    File lowresFile = pi.getLowresFile();
                    if ((lowresFile != null) && (lowresFile.exists())) {
                        ok = lowresFile.delete();
                        if (!ok) {
                            LOGGER.info("File deleted failed on: " + lowresFile.toString());
                        }
                    }

                    File highresFile = pi.getHighresFile();
                    if (highresFile.exists()) {
                        ok = highresFile.delete();
                        if (!ok) {
                            LOGGER.info("File deleted failed on: " + highresFile.toString());
                        }
                    }

                    selectedNode.deleteNode();

                    if (!ok) {
                        JOptionPane.showMessageDialog(Settings.anchorFrame,
                                Settings.jpoResources.getString("fileDeleteError") + highresFile.toString(),
                                Settings.jpoResources.getString("genericError"),
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            Settings.pictureCollection.clearSelection();
        }

    }
    //  Controller type Stuff
    /**
     * The node the popup menu was created for
     */
    private final SortableDefaultMutableTreeNode popupNode;
    /**
     * Reference to the {@link NodeNavigatorInterface} which indicates the nodes
     * being displayed.
     */
    private NodeNavigatorInterface mySetOfNodes = null;
    /**
     * Index of the {@link #mySetOfNodes} being popped up.
     */
    private int index = 0;

    /**
     * The "Show Picture" menu button calls this function
     */
    private void requestShowPicture() {
        Jpo.browsePictures(popupNode);
    }

    /**
     * The "Show Map" menu button calls this function
     */
    private void requestShowMap() {
        Webserver.getInstance().browse(popupNode);
    }

    /**
     * Show the Properties GUI
     */
    private void requestProperties() {
        new PictureInfoEditor(mySetOfNodes, index);
    }

    /**
     * handle the Categories click
     */
    private void requestCategories() {
        if (Settings.pictureCollection.countSelectedNodes() < 1) {
            TreeNodeController.showCategoryUsageGUI(popupNode);
        } else {
            if (!Settings.pictureCollection.isSelected(popupNode)) {
                Settings.pictureCollection.clearSelection();
                TreeNodeController.showCategoryUsageGUI(popupNode);
            } else {
                CategoryUsageJFrame categoryUsageJFrame = new CategoryUsageJFrame();
                SortableDefaultMutableTreeNode[] selectedNodes = Settings.pictureCollection.getSelectedNodes();
                categoryUsageJFrame.setSelection(new ArrayList<SortableDefaultMutableTreeNode>(Arrays.asList(selectedNodes)));
            }
        }
    }

    /**
     * request to select for Email
     */
    private void requestSelectForEmail() {
        if (Settings.pictureCollection.countSelectedNodes() < 1) {
            popupNode.getPictureCollection().removeFromMailSelection(popupNode);
        } else {
            /*Enumeration<SortableDefaultMutableTreeNode> selection = Settings.pictureCollection.getSelectedNodesAsVector().elements();
             SortableDefaultMutableTreeNode n;
             while ( selection.hasMoreElements() ) {
             n = selection.nextElement();
             if ( n.getUserObject() instanceof PictureInfo ) {
             n.getPictureCollection().removeFromMailSelection( n );
             }
             }*/
            for (SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes()) {
                if (selectedNode.getUserObject() instanceof PictureInfo) {
                    selectedNode.getPictureCollection().removeFromMailSelection(selectedNode);
                }
            }
        }
    }

    /**
     * Switches the picture back to no rotation
     */
    private void noRotation() {
        Object o = popupNode.getUserObject();
        PictureInfo pi = (PictureInfo) o;
        pi.setRotation(0);
        popupNode.refreshThumbnail();
    }

    /**
     * Rotates the picture by the indicated angle
     *
     * @param angle 0..360 degrees
     */
    private void rotatePicture(int angle) {
        Object o = popupNode.getUserObject();
        PictureInfo pi = (PictureInfo) o;
        pi.rotate(angle);
        popupNode.refreshThumbnail();
    }

    /**
     * Here we receive notification that the nodes have been updated. The method
     * then updates the menu items for the drop targets. Those that are null are
     * not shown. If no drop targets are shown at all the seerator in the
     * submenu is not shown either.
     */
    @Override
    public void recentDropNodesChanged() {
        boolean dropNodesVisible = false;
        for (int i = 0; i < Settings.MAX_DROPNODES; i++) {
            if (Settings.recentDropNodes[i] != null) {
                recentDropNodes[i].setText(
                        Settings.jpoResources.getString("recentDropNodePrefix") + Settings.recentDropNodes[i].toString());
                recentDropNodes[i].setVisible(true);
                dropNodesVisible = true;
            } else {
                recentDropNodes[i].setVisible(false);
            }
        }
        if (dropNodesVisible) {
            movePictureNodeSeparator.setVisible(true);
        } else {
            movePictureNodeSeparator.setVisible(false);
        }
    }

    /**
     * Here we receive notification that the copy locations were updated and
     * then go and update the targets on the menu.
     */
    @Override
    public void copyLocationsChanged() {
        for (int i = 0; i < Settings.copyLocations.length; i++) {
            if (Settings.copyLocations[i] != null) {
                copyLocationsJMenuItems[i].setText(Settings.copyLocations[i]);
                copyLocationsJMenuItems[i].setVisible(true);
            } else {
                copyLocationsJMenuItems[i].setVisible(false);
            }
        }
    }

    /**
     * This method populates the user functions sub entries on the menu. It is
     * called by a listener when user functions are added.
     *
     */
    @Override
    public void userFunctionsChanged() {
        for (int i = 0; i < Settings.maxUserFunctions; i++) {
            if ((Settings.userFunctionNames[i] != null) && (Settings.userFunctionNames[i].length() > 0) && (Settings.userFunctionCmd[i] != null) && (Settings.userFunctionCmd[i].length() > 0)) {
                userFunctionsJMenuItems[i].setText(Settings.userFunctionNames[i]);
                userFunctionsJMenuItems[i].setVisible(true);
            } else {
                userFunctionsJMenuItems[i].setVisible(false);
            }
        }
    }

    /**
     * Clears the set of images selected for emailing
     */
    private void doClearMailSelection() {
        popupNode.getPictureCollection().clearMailSelection();
    }

    /**
     * Adds a picture to the selection of pictures to be mailed.
     */
    private void addToMailSelection() {
        if (Settings.pictureCollection.countSelectedNodes() < 1) {
            popupNode.getPictureCollection().addToMailSelection(popupNode);
        } else {
            for (SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes()) {
                if (selectedNode.getUserObject() instanceof PictureInfo) {
                    selectedNode.getPictureCollection().addToMailSelection(selectedNode);
                }
            }
        }
    }

    /**
     * Refresh the selected pictures
     */
    private void refreshSelectedPictures() {
        if (!Settings.pictureCollection.isSelected(popupNode)) {
            Settings.pictureCollection.clearSelection();
            popupNode.refreshThumbnail();
        } else {
            Object[] selectedNodes = Settings.pictureCollection.getSelectedNodes();
            for (Object node : selectedNodes) {
                ((SortableDefaultMutableTreeNode) node).refreshThumbnail();
            }
        }

    }

    /**
     * Handle the consolidate here click request
     */
    private void consolidateHere() {
        new ConsolidateGroupJFrame((SortableDefaultMutableTreeNode) popupNode.getParent(), ((PictureInfo) popupNode.getUserObject()).getHighresFile().getParentFile());
    }

    /**
     * This method moves the popup node or the selection (if the popup node is
     * part of the selection) to the end of the selected node:
     *
     * TODO: This is "Controller Stuff" and should be handled in a different
     * class
     *
     * @param targetNode To selected node where the node should go
     */
    private void moveToLastChild(SortableDefaultMutableTreeNode targetNode) {

        if ((Settings.pictureCollection.countSelectedNodes() > 0) && (Settings.pictureCollection.isSelected(popupNode))) {
            // move the selected nodes and then unselect them
            LOGGER.info("Moving the selection.");
            for (SortableDefaultMutableTreeNode selectedNode : Settings.pictureCollection.getSelectedNodes()) {
                if (selectedNode.getUserObject() instanceof PictureInfo) {
                    selectedNode.moveToLastChild(targetNode);
                }
            }

            Settings.pictureCollection.clearSelection();
        } else {
            // move only the popup node
            popupNode.moveToLastChild(targetNode);
        }

    }

    /**
     * Intercepting this method to attach the Settings change listeners when the
     * menu becomes visible so that they can also be removed when the menu goes
     * away again.
     */
    @Override
    protected void firePopupMenuWillBecomeVisible() {
        LOGGER.fine("registering PicturePopupMenu listeners");
        Settings.addRecentDropNodeListener(this);
        Settings.addCopyLocationsChangeListener(this);
        Settings.addUserFunctionsChangeListener(this);

        super.firePopupMenuWillBecomeVisible();
    }

    /**
     * Intercepting this method to attach the Setting's change listeners when
     * the menu becomes visible so that they can also be removed when the menu
     * goes away again.
     */
    @Override
    protected void firePopupMenuWillBecomeInvisible() {
        LOGGER.fine("deregistering PicturePopupMenu listeners");
        Settings.removeRecentDropNodeListener(this);
        Settings.removeCopyLocationsChangeListener(this);
        Settings.removeUserFunctionsChangeListener(this);

        super.firePopupMenuWillBecomeInvisible();
    }
}
