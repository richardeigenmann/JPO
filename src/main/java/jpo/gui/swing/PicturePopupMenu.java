package jpo.gui.swing;

import com.google.common.eventbus.Subscribe;
import jpo.EventBus.*;
import jpo.cache.ThumbnailQueueRequest.QUEUE_PRIORITY;
import jpo.dataModel.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;

/*
 PicturePopupMenu.java:  a popup menu for pictures

 Copyright (C) 2002 - 2019  Richard Eigenmann.
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
 * This class generates a popup menu on a picture node.
 */
public class PicturePopupMenu extends JPopupMenu {


    /**
     * array of menu items that allows the user to call up a user function
     */
    private final JMenuItem[] userFunctionJMenuItems = new JMenuItem[Settings.maxUserFunctions];

    /**
     * array of menu items that allows the user to copy the picture to a
     * memorised file location
     */
    private final JMenuItem[] copyLocationJMenuItems = new JMenuItem[Settings.MAX_MEMORISE];

    /**
     * array of menu items that allows the user to copy the picture to a
     * memorised file location
     */
    private final JMenuItem[] moveLocationJMenuItems = new JMenuItem[Settings.MAX_MEMORISE];

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
    private final JMenuItem[] recentDropNodeJMenuItems = new JMenuItem[Settings.MAX_DROPNODES];

    /**
     * Creates a popup menu for a node holding a picture
     *
     * @param setOfNodes The set of nodes from which the popup picture is coming
     * @param idx        The picture of the set for which the popup is being shown.
     */
    public PicturePopupMenu(NodeNavigatorInterface setOfNodes, int idx) {
        this.mySetOfNodes = setOfNodes;
        this.index = idx;
        this.popupNode = mySetOfNodes.getNode(index);
        JpoEventBus.getInstance().register(new RecentDropNodeChangedEventHandler());
        JpoEventBus.getInstance().register(new CopyLocationsChangedEventHandler());
        JpoEventBus.getInstance().register(new UserFunctionsChangedEventHandler());

        initComponents();
    }

    /**
     * Method to replace the %20 that some filenames may have instead of a space char
     * @param s The source string
     * @return an Optional with the replaced string. The Optional isPresent() and can be
     * retrieved with get() if the name was different. If there was nothing to translate the
     * isPresent() method returns false. This allows the caller to easily tell if there is any
     * point in proposing a rename.
     */
    public static Optional<String> replaceEscapedSpaces(@NonNull String s) {
        Objects.requireNonNull(s);

        String newString = s.replaceAll("(%20)+", " ");
        if ( newString.equals(s)) {
            return Optional.empty();
        } else {
            return Optional.of(newString);
        }
    }

    /**
     * Method to replace the underscores that some filenames may have instead of a space char
     * @param s The source string
     * @return an Optional with the replaced string. The Optional isPresent() and can be
     * retrieved with get() if the name was different. If there was nothing to translate the
     * isPresent() method returns false. This allows the caller to easily tell if there is any
     * point in proposing a rename.
     */
    public static Optional<String> replaceUnderscore(@NonNull String s) {
        Objects.requireNonNull(s);

        String newString = s.replaceAll("_+", " ");
        if ( newString.equals(s)) {
            return Optional.empty();
        } else {
            return Optional.of(newString);
        }
    }


    /**
     * initialises the GUI components
     */
    private void initComponents() {
        String title = getTitle();
        setLabel(title);

        JMenuItem titleJMenuItem = new JMenuItem(title);
        titleJMenuItem.setEnabled(false);
        add(titleJMenuItem);

        addSeparator();

        JMenuItem showPictureMenuItem = new JMenuItem(Settings.jpoResources.getString("pictureShowJMenuItemLabel"));
        showPictureMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowPictureRequest(popupNode)));
        add(showPictureMenuItem);

        JMenuItem showMapMenuItem = new JMenuItem(Settings.jpoResources.getString("mapShowJMenuItemLabel"));
        showMapMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowPictureOnMapRequest(popupNode)));
        add(showMapMenuItem);

        JMenuItem openFolderJMenuItem = new JMenuItem(Settings.jpoResources.getString("openFolderJMenuItem") );
        openFolderJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new OpenFileExplorerRequest(((PictureInfo)popupNode.getUserObject()).getImageFile().getParentFile())));
        add(openFolderJMenuItem);


        JMenu navigateMenuItem = new JMenu(Settings.jpoResources.getString("navigationJMenu"));
        SortableDefaultMutableTreeNode[] parentNodes = Settings.getPictureCollection().findParentGroups(popupNode);
        for (SortableDefaultMutableTreeNode parentNode : parentNodes) {
            JMenuItem navigateTargetRoute = new JMenuItem(parentNode.getUserObject().toString());
            final SortableDefaultMutableTreeNode targetNode = parentNode;
            navigateTargetRoute.addActionListener(e -> JpoEventBus.getInstance().post(new ShowGroupRequest(targetNode)));
            navigateMenuItem.add(navigateTargetRoute);
        }

        add(navigateMenuItem);

        JMenuItem showCategoryUsageJMenuItemMenuItem = new JMenuItem(Settings.jpoResources.getString("categoryUsageJMenuItem"));
        showCategoryUsageJMenuItemMenuItem.addActionListener((ActionEvent e) -> {
            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                HashSet<SortableDefaultMutableTreeNode> hashSet = new HashSet<>();
                hashSet.add(popupNode);
                JpoEventBus.getInstance().post(new ShowCategoryUsageEditorRequest(hashSet));
            } else if (!Settings.getPictureCollection().isSelected(popupNode)) {
                Settings.getPictureCollection().clearSelection();
                HashSet<SortableDefaultMutableTreeNode> hashSet = new HashSet<>();
                hashSet.add(popupNode);
                JpoEventBus.getInstance().post(new ShowCategoryUsageEditorRequest(hashSet));
            } else {
                HashSet<SortableDefaultMutableTreeNode> hashSet = new HashSet<>(Settings.getPictureCollection().getSelection());
                JpoEventBus.getInstance().post(new ShowCategoryUsageEditorRequest(hashSet));

            }
        });
        add(showCategoryUsageJMenuItemMenuItem);

        final PictureCollection pictureCollection = Settings.getPictureCollection();
        JMenuItem pictureMailSelectJMenuItem = new JMenuItem(Settings.jpoResources.getString("pictureMailSelectJMenuItem"));
        pictureMailSelectJMenuItem.addActionListener((ActionEvent e) -> {
            /*
             * Adds a picture to the selection of pictures to be mailed.
             *
             * 1. If no nodes are selected, mail-select the node.
             *
             * 2. If multiple nodes are selected but the popup node is not one of them
             * then mail-select the node
             *
             * 3. If multiple nodes are selected and the popup node is one of them then
             * mail-select them all
             */
            ArrayList<SortableDefaultMutableTreeNode> nodesList = new ArrayList<>(1);
            if ((pictureCollection.countSelectedNodes() < 1)
                    || (!pictureCollection.isSelected(popupNode))) {
                nodesList.add(popupNode);
            } else {
                for (SortableDefaultMutableTreeNode selectedNode : pictureCollection.getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        nodesList.add(selectedNode);
                    }
                }
            }
            JpoEventBus.getInstance().post(new AddPictureNodesToEmailSelectionRequest(nodesList));
        });
        add(pictureMailSelectJMenuItem);

        JMenuItem pictureMailUnSelectJMenuItem = new JMenuItem(Settings.jpoResources.getString("pictureMailUnselectJMenuItem"));
        pictureMailUnSelectJMenuItem.addActionListener((ActionEvent e) -> {
            ArrayList<SortableDefaultMutableTreeNode> nodesList = new ArrayList<>(1);
            if ((pictureCollection.countSelectedNodes() == 0)
                    || (!pictureCollection.isSelected(popupNode))) {
                nodesList.add(popupNode);
            } else {
                for (SortableDefaultMutableTreeNode selectedNode : pictureCollection.getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        nodesList.add(selectedNode);
                    }
                }
            }
            JpoEventBus.getInstance().post(new RemovePictureNodesFromEmailSelectionRequest(nodesList));
        } /*
         * 1. If no nodes are selected, mail-unselect the node.
         *
         * 2. If multiple nodes are selected but the popup node is not one of them
         * then mail-unselect the node
         *
         * 3. If multiple nodes are selected and the popup node is one of them then
         * mail-unselect them all
         */);
        add(pictureMailUnSelectJMenuItem);

        JMenuItem pictureMailUnselectAllJMenuItem = new JMenuItem(Settings.jpoResources.getString("pictureMailUnselectAllJMenuItem"));
        pictureMailUnselectAllJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ClearEmailSelectionRequest()));
        add(pictureMailUnselectAllJMenuItem);

        boolean emailSelectable = false;
        /*
         * if there is no selection and we click on a node which is not email selected
         *   then offer to email select it
         * if there is a selection but some are not email selected, offer to select them
         * if there is a selection but the selected node is not part of it
         *   and the node is not selected then offer to select it
         */
        if ((pictureCollection.countSelectedNodes() == 0)
                || (!pictureCollection.isSelected(popupNode))) {

            // deal with single node
            emailSelectable = !popupNode.getPictureCollection()
                    .isMailSelected(popupNode);

        } else {
            // we have a selection and the popup node is part of it
            for (SortableDefaultMutableTreeNode selectedNode : pictureCollection.getSelection()) {
                if ((selectedNode.getUserObject() instanceof PictureInfo)
                        && (!pictureCollection.isMailSelected(selectedNode))) {
                    emailSelectable = true;
                    break;
                }
            }
        }
        pictureMailSelectJMenuItem.setVisible(emailSelectable);

        boolean emailUnSelectable = false;
        /*
         * if there is no selection and we click on a node which is email selected
         *   then offer to unselect it
         * if there is a selection and email selected, offer to unselect them
         * if there is a selection but the selected node is not part of it
         *   and the node is selected then offer to unselect it
         */
        if ((pictureCollection.countSelectedNodes() == 0)
                || (!pictureCollection.isSelected(popupNode))) {

            // deal with single node
            emailUnSelectable = popupNode.getPictureCollection()
                    .isMailSelected(popupNode);

        } else {
            // we have a selection and the popup node is part of it
            for (SortableDefaultMutableTreeNode selectedNode : pictureCollection.getSelection()) {
                if ((selectedNode.getUserObject() instanceof PictureInfo)
                        && (pictureCollection.isMailSelected(selectedNode))) {
                    emailUnSelectable = true;
                    break;
                }
            }
        }
        pictureMailUnSelectJMenuItem.setVisible(emailUnSelectable);

        if (Settings.getPictureCollection()
                .countMailSelectedNodes() > 0) {
            pictureMailUnselectAllJMenuItem.setVisible(true);
        } else {
            pictureMailUnselectAllJMenuItem.setVisible(false);
        }

        JMenu userFunctionsJMenu = new JMenu(Settings.jpoResources.getString("userFunctionsJMenu"));
        for (int i = 0;
             i < Settings.maxUserFunctions;
             i++) {
            final int userFunction = i;
            userFunctionJMenuItems[i] = new JMenuItem();
            userFunctionJMenuItems[i].addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new RunUserFunctionRequest(userFunction, (PictureInfo) popupNode.getUserObject())));
            userFunctionsJMenu.add(userFunctionJMenuItems[i]);
        }

        add(userFunctionsJMenu);
        labelUserFunctions();

        if (popupNode.getPictureCollection()
                .getAllowEdits()) {
            JMenu rotationMenu = new JMenu(Settings.jpoResources.getString("rotation"));

            JMenuItem rotate90JMenuItem = new JMenuItem(Settings.jpoResources.getString("rotate90"));
            rotate90JMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new RotatePictureRequest(popupNode, 90, QUEUE_PRIORITY.HIGH_PRIORITY)));
            rotationMenu.add(rotate90JMenuItem);

            JMenuItem rotate180JMenuItem = new JMenuItem(Settings.jpoResources.getString("rotate180"));
            rotate180JMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new RotatePictureRequest(popupNode, 180, QUEUE_PRIORITY.HIGH_PRIORITY)));
            rotationMenu.add(rotate180JMenuItem);

            JMenuItem rotate270JMenuItem = new JMenuItem(Settings.jpoResources.getString("rotate270"));
            rotate270JMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new RotatePictureRequest(popupNode, 270, QUEUE_PRIORITY.HIGH_PRIORITY)));
            rotationMenu.add(rotate270JMenuItem);

            JMenuItem rotate0JMenuItem = new JMenuItem(Settings.jpoResources.getString("rotate0"));
            rotate0JMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new SetPictureRotationRequest(popupNode, 0f, QUEUE_PRIORITY.HIGH_PRIORITY)));
            rotationMenu.add(rotate0JMenuItem);
            add(rotationMenu);
        }

        JMenuItem pictureRefreshJMenuItem = new JMenuItem(Settings.jpoResources.getString("pictureRefreshJMenuItem"));

        pictureRefreshJMenuItem.addActionListener((ActionEvent e) -> {
            if (!Settings.getPictureCollection().isSelected(popupNode)) {
                List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
                nodes.add(popupNode);
                JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, QUEUE_PRIORITY.HIGH_PRIORITY));
            } else {
                JpoEventBus.getInstance().post(new RefreshThumbnailRequest(Settings.getPictureCollection().getSelection(), QUEUE_PRIORITY.HIGH_PRIORITY));
            }
        });
        add(pictureRefreshJMenuItem);

        JMenu moveJMenu = new JMenu(Settings.jpoResources.getString("moveNodeJMenuLabel"));
        add(moveJMenu);

        for (int i = 0; i < Settings.MAX_DROPNODES; i++) {
            final int dropnode = i;
            recentDropNodeJMenuItems[i] = new JMenuItem();
            recentDropNodeJMenuItems[i].addActionListener((ActionEvent event) -> {
                        SortableDefaultMutableTreeNode targetNode = Settings.recentDropNodes.toArray(new SortableDefaultMutableTreeNode[0])[dropnode];
                        List<SortableDefaultMutableTreeNode> movingNodes = new ArrayList<>();
                        if ((Settings.getPictureCollection().countSelectedNodes() > 0) && (Settings.getPictureCollection().isSelected(popupNode))) {
                            movingNodes.addAll(Settings.getPictureCollection().getSelection());
                            Settings.getPictureCollection().clearSelection();
                        } else {
                            movingNodes.add(popupNode);
                        }
                        JpoEventBus.getInstance().post(new MoveNodeToNodeRequest(movingNodes, targetNode));

                        Settings.memorizeGroupOfDropLocation(Settings.recentDropNodes.toArray(new SortableDefaultMutableTreeNode[0])[dropnode]);
                        JpoEventBus.getInstance().post(new RecentDropNodesChangedEvent());
                    }
                    /*
                     * Moves the selected nodes to the picked destination. If no
                     * nodes are in the selection then the popup node is moved. If
                     * the node for the popup is not in the selection then this node
                     * only is moved. After the move of the selected nodes they are
                     * cleared.
                     *
                     * @param event
                     */);
            moveJMenu.add(recentDropNodeJMenuItems[i]);
        }
        moveJMenu.add(movePictureNodeSeparator);
        labelRecentDropNodes();

        JMenuItem movePictureToTopJMenuItem = new JMenuItem(Settings.jpoResources.getString("movePictureToTopJMenuItem"));
        movePictureToTopJMenuItem.addActionListener((ActionEvent event) -> {
            if ((Settings.getPictureCollection().countSelectedNodes() < 1)
                    || (!pictureCollection.isSelected(popupNode))) {
                JpoEventBus.getInstance().post(new MoveNodeToTopRequest(popupNode));
            } else {
                for (SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        JpoEventBus.getInstance().post(new MoveNodeToTopRequest(selectedNode));
                    }
                }
            }
        });
        moveJMenu.add(movePictureToTopJMenuItem);

        JMenuItem movePictureUpJMenuItem = new JMenuItem(Settings.jpoResources.getString("movePictureUpJMenuItem"));
        movePictureUpJMenuItem.addActionListener((ActionEvent e) -> {
            if ((Settings.getPictureCollection().countSelectedNodes() < 1)
                    || (!pictureCollection.isSelected(popupNode))) {
                JpoEventBus.getInstance().post(new MoveNodeUpRequest(popupNode));
            } else {
                for (SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        JpoEventBus.getInstance().post(new MoveNodeUpRequest(selectedNode));
                    }
                }
            }
        });
        moveJMenu.add(movePictureUpJMenuItem);

        JMenuItem movePictureDownJMenuItem = new JMenuItem(Settings.jpoResources.getString("movePictureDownJMenuItem"));
        movePictureDownJMenuItem.addActionListener((ActionEvent e) -> {
            if ((Settings.getPictureCollection().countSelectedNodes() < 1) || (!pictureCollection.isSelected(popupNode))) {
                JpoEventBus.getInstance().post(new MoveNodeDownRequest(popupNode));
            } else {
                for (SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        JpoEventBus.getInstance().post(new MoveNodeDownRequest(selectedNode));
                        selectedNode.moveNodeDown();
                    }
                }
            }
        });
        moveJMenu.add(movePictureDownJMenuItem);

        JMenuItem movePictureToBottomJMenuItem = new JMenuItem(Settings.jpoResources.getString("movePictureToBottomJMenuItem"));
        movePictureToBottomJMenuItem.addActionListener((ActionEvent e) -> {
            if ((Settings.getPictureCollection().countSelectedNodes() < 1)
                    || (!pictureCollection.isSelected(popupNode))) {
                JpoEventBus.getInstance().post(new MoveNodeToBottomRequest(popupNode));
            } else {
                for (SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        JpoEventBus.getInstance().post(new MoveNodeToBottomRequest(selectedNode));
                    }
                }
            }
        });
        moveJMenu.add(movePictureToBottomJMenuItem);

        JMenuItem indentJMenuItem = new JMenuItem(Settings.jpoResources.getString("indentJMenuItem"));
        indentJMenuItem.addActionListener((ActionEvent e) -> {
            ArrayList<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
            if ((Settings.getPictureCollection().countSelectedNodes() < 1)
                    || (!pictureCollection.isSelected(popupNode))) {
                nodes.add(popupNode);
                //popupNode.indentNode();
            } else {
                for (SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        nodes.add(selectedNode);
                        //selectedNode.indentNode();
                    }
                }
            }
            JpoEventBus.getInstance().post(new MoveIndentRequest(nodes));
        });
        moveJMenu.add(indentJMenuItem);

        JMenuItem outdentJMenuItem = new JMenuItem(Settings.jpoResources.getString("outdentJMenuItem"));
        outdentJMenuItem.addActionListener((ActionEvent e) -> {
            ArrayList<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
            if ((Settings.getPictureCollection().countSelectedNodes() < 1)
                    || (!pictureCollection.isSelected(popupNode))) {
                nodes.add(popupNode);
            } else {
                for (SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        nodes.add(selectedNode);
                    }
                }
            }
            JpoEventBus.getInstance().post(new MoveOutdentRequest(nodes));
        });
        moveJMenu.add(outdentJMenuItem);

        moveJMenu.setVisible(popupNode.getPictureCollection()
                .getAllowEdits());

        JMenu copyJMenu = new JMenu(Settings.jpoResources.getString("copyImageJMenuLabel"));
        add(copyJMenu);

        final JMenuItem copyToNewLocationJMenuItem = new JMenuItem(Settings.jpoResources.getString("copyToNewLocationJMenuItem"));
        copyToNewLocationJMenuItem.addActionListener((ActionEvent e) -> {
            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
                nodes.add(popupNode);
                JpoEventBus.getInstance().post(new CopyToNewLocationRequest(nodes));
            } else {
                JpoEventBus.getInstance().post(new CopyToNewLocationRequest(Settings.getPictureCollection().getSelection()));
            }
        });
        copyJMenu.add(copyToNewLocationJMenuItem);

        copyJMenu.addSeparator();

        final String[] copyLocationsArray = Settings.copyLocations.toArray(new String[0]);
        for (int i = 0; i < Settings.MAX_MEMORISE; i++) {
            final File loc = i < copyLocationsArray.length ? new File(copyLocationsArray[i]) : new File(".");
            copyLocationJMenuItems[i] = new JMenuItem();
            copyLocationJMenuItems[i].addActionListener((ActionEvent ae) -> {
                if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                    List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
                    nodes.add(popupNode);
                    JpoEventBus.getInstance().post(new CopyToDirRequest(nodes, loc));
                } else {
                    JpoEventBus.getInstance().post(new CopyToDirRequest(Settings.getPictureCollection().getSelection(), loc));
                }
            });
            copyJMenu.add(copyLocationJMenuItems[i]);
        }
        labelCopyLocations();

        copyJMenu.addSeparator();

        final JMenuItem copyToNewZipfileJMenuItem = new JMenuItem(Settings.jpoResources.getString("copyToNewZipfileJMenuItem"));
        copyToNewZipfileJMenuItem.addActionListener((ActionEvent e) -> {
            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                List<SortableDefaultMutableTreeNode> selection = new ArrayList<>();
                selection.add(popupNode);
                JpoEventBus.getInstance().post(new CopyToNewZipfileRequest(selection));
            } else {
                JpoEventBus.getInstance().post(new CopyToNewZipfileRequest(Settings.getPictureCollection().getSelection()));
            }
        });
        copyJMenu.add(copyToNewZipfileJMenuItem);

        final JMenuItem copyToClipboard = new JMenuItem("Copy to Clipboard");
        copyToClipboard.addActionListener((ActionEvent e) -> {
            ArrayList<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                nodes.add(popupNode);

            } else {
                JpoEventBus.getInstance().post(new CopyToClipboardRequest(Settings.getPictureCollection().getSelection()));
            }
            JpoEventBus.getInstance().post(new CopyToClipboardRequest(nodes));
        });
        copyJMenu.add(copyToClipboard);

        JMenuItem[] memorizedZipFileJMenuItems = new JMenuItem[Settings.MAX_MEMORISE];
        String[] memorizedZipFilesArray = Settings.memorizedZipFiles.toArray(new String[0]);
        for (int i = 0; i < Settings.MAX_MEMORISE; i++) {
            final File loc = (i < memorizedZipFilesArray.length) ? new File(memorizedZipFilesArray[i]) : new File(".");
            memorizedZipFileJMenuItems[i] = new JMenuItem();
            memorizedZipFileJMenuItems[i].addActionListener((ActionEvent ae) -> {
                if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                    List<SortableDefaultMutableTreeNode> selection = new ArrayList<>();
                    selection.add(popupNode);
                    JpoEventBus.getInstance().post(new CopyToZipfileRequest(selection, loc));
                } else {
                    JpoEventBus.getInstance().post(new CopyToZipfileRequest(Settings.getPictureCollection().getSelection(), loc));
                }
            });
            copyJMenu.add(memorizedZipFileJMenuItems[i]);
            if (i < memorizedZipFilesArray.length) {
                memorizedZipFileJMenuItems[i].setText(memorizedZipFilesArray[i]);
                memorizedZipFileJMenuItems[i].setVisible(true);
            } else {
                memorizedZipFileJMenuItems[i].setVisible(false);
            }
        }

        JMenuItem pictureNodeRemove = new JMenuItem(Settings.jpoResources.getString("pictureNodeRemove"));
        pictureNodeRemove.addActionListener((ActionEvent e) -> {
            ArrayList<SortableDefaultMutableTreeNode> nodesToRemove = new ArrayList<>();
            SortableDefaultMutableTreeNode actionNode = mySetOfNodes.getNode(index);
            if ((Settings.getPictureCollection().countSelectedNodes() > 1) && (Settings.getPictureCollection().isSelected(actionNode))) {
                for (SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        nodesToRemove.add(selectedNode);
                        //selectedNode.deleteNode();
                    }
                }
            } else {
                nodesToRemove.add(popupNode);
                //popupNode.deleteNode();
            }
            JpoEventBus.getInstance().post(new RemoveNodeRequest(nodesToRemove));
        });
        add(pictureNodeRemove);

        JMenu fileOperationsJMenu = new JMenu(Settings.jpoResources.getString("FileOperations"));
        add(fileOperationsJMenu);

        JMenuItem filenameJMenuItem = new JMenuItem();
        filenameJMenuItem.setEnabled(false);
        if (Settings.getPictureCollection().countSelectedNodes() < 1) {
            filenameJMenuItem.setText( ((PictureInfo) popupNode.getUserObject()).getImageFile().getPath() );
        } else {
            filenameJMenuItem.setText( Settings.getPictureCollection().countSelectedNodes() + " pictures");
        }
        fileOperationsJMenu.add(filenameJMenuItem);
        fileOperationsJMenu.addSeparator();


        JMenu fileMoveJMenu = new JMenu(Settings.jpoResources.getString("fileMoveJMenu"));
        fileOperationsJMenu.add(fileMoveJMenu);


        final JMenuItem moveToNewLocationJMenuItem = new JMenuItem(Settings.jpoResources.getString("moveToNewLocationJMenuItem"));
        moveToNewLocationJMenuItem.addActionListener((ActionEvent e) -> {
            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
                nodes.add(popupNode);
                JpoEventBus.getInstance().post(new MoveToNewLocationRequest(nodes));
            } else {
                JpoEventBus.getInstance().post(new MoveToNewLocationRequest(Settings.getPictureCollection().getSelection()));
            }
        });
        fileMoveJMenu.add(moveToNewLocationJMenuItem);

        fileMoveJMenu.addSeparator();

        final String[] moveLocationsArray = Settings.copyLocations.toArray(new String[0]);
        for (int i = 0; i < Settings.MAX_MEMORISE; i++) {
            final File loc = i < moveLocationsArray.length ? new File(moveLocationsArray[i]) : new File(".");
            moveLocationJMenuItems[i] = new JMenuItem();
            moveLocationJMenuItems[i].addActionListener((ActionEvent ae) -> {
                if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                    List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
                    nodes.add(popupNode);
                    JpoEventBus.getInstance().post(new MoveToDirRequest(nodes, loc));
                } else {
                    JpoEventBus.getInstance().post(new MoveToDirRequest(Settings.getPictureCollection().getSelection(), loc));
                }
            });
            fileMoveJMenu.add(moveLocationJMenuItems[i]);
        }
        labelMoveLocations();


        JMenu renameJMenu = new JMenu(Settings.jpoResources.getString("renameJMenu"));
        fileOperationsJMenu.add(renameJMenu);


        JMenuItem fileRenameJMenuItem = new JMenuItem(Settings.jpoResources.getString("fileRenameJMenuItem"));
        fileRenameJMenuItem.addActionListener((ActionEvent e) -> {
            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                JpoEventBus.getInstance().post(new RenamePictureRequest(popupNode));
            } else {
                for (SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        JpoEventBus.getInstance().post(new RenamePictureRequest(selectedNode));
                    }
                }
            }
        });
        renameJMenu.add(fileRenameJMenuItem);

        if  (Settings.getPictureCollection().countSelectedNodes() < 1) {
            PictureInfo pi = (PictureInfo) popupNode.getUserObject();
            Optional<String> potentialNewFilename  = replaceEscapedSpaces(pi.getImageFile().getName());
            if ( potentialNewFilename.isPresent() ) {
                File suggestedFileName = Tools.inventPicFilename(pi.getImageFile().getParentFile(), potentialNewFilename.get());
                JMenuItem renameSpaceJMenuItem = new JMenuItem( "To: " + suggestedFileName.getName());
                renameSpaceJMenuItem.addActionListener(e -> JpoEventBus.getInstance().post(new RenameFileRequest(popupNode,suggestedFileName.getName())));
                renameJMenu.add(renameSpaceJMenuItem);
            }
        }

        if  (Settings.getPictureCollection().countSelectedNodes() < 1) {
            PictureInfo pi = (PictureInfo) popupNode.getUserObject();
            Optional<String> potentialNewFilename  = replaceUnderscore(pi.getImageFile().getName());
            if ( potentialNewFilename.isPresent() ) {
                File suggestedFileName = Tools.inventPicFilename(pi.getImageFile().getParentFile(), potentialNewFilename.get());
                JMenuItem renameUnderscoreJMenuItem = new JMenuItem( "To: " + suggestedFileName.getName());
                renameUnderscoreJMenuItem.addActionListener(e -> JpoEventBus.getInstance().post(new RenameFileRequest(popupNode,suggestedFileName.getName())));
                renameJMenu.add(renameUnderscoreJMenuItem);
            }
        }

        JMenuItem fileDeleteJMenuItem = new JMenuItem(Settings.jpoResources.getString("fileDeleteJMenuItem"));
        fileDeleteJMenuItem.addActionListener((ActionEvent e) -> {
            SortableDefaultMutableTreeNode actionNode = mySetOfNodes.getNode(index);
            if ((Settings.getPictureCollection().countSelectedNodes() > 1) && (Settings.getPictureCollection().isSelected(actionNode))) {
                JpoEventBus.getInstance().post(new DeleteMultiNodeFileRequest(Settings.getPictureCollection().getSelection()));
            } else {
                JpoEventBus.getInstance().post(new DeleteNodeFileRequest(actionNode));
            }
        });
        fileOperationsJMenu.add(fileDeleteJMenuItem);

        pictureNodeRemove.setVisible(pictureCollection.getAllowEdits());
        fileOperationsJMenu.setVisible(pictureCollection.getAllowEdits());
        fileRenameJMenuItem.setVisible(pictureCollection.getAllowEdits());
        fileDeleteJMenuItem.setVisible(pictureCollection.getAllowEdits());

        JMenuItem showPictureInfoEditorMenuItem = new JMenuItem(Settings.jpoResources.getString("pictureEditJMenuItemLabel"));
        showPictureInfoEditorMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowPictureInfoEditorRequest(popupNode)));
        add(showPictureInfoEditorMenuItem);

        JMenuItem consolidateHereMenuItem = new JMenuItem("Consolidate Here");

        consolidateHereMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(
                new ConsolidateGroupDialogRequest(
                        popupNode.getParent(),
                        ((PictureInfo) popupNode.getUserObject()).getImageFile().getParentFile())
        ));
        add(consolidateHereMenuItem);

    }

    /**
     * Returns the title for the popup Menu. If nodes are selected and the
     * selected node is one of them it returns the number of selected nodes.
     * Otherwise it returns the description of the picture.
     *
     * @return the title for the popup menu
     */
    private String getTitle() {
        String title;
        if ((Settings.getPictureCollection().countSelectedNodes() > 1) && (Settings.getPictureCollection().isSelected(popupNode))) {
            title = String.format("%d nodes", Settings.getPictureCollection().countSelectedNodes());
        } else {
            Object userObject = popupNode.getUserObject();
            if (userObject instanceof PictureInfo) {
                String description = ((PictureInfo) userObject).getDescription();
                title = description.length() > 25
                        ? description.substring(0, 25) + "..." : description;
            } else {
                title = "Picture Popup Menu";
            }
        }
        return title;
    }

    /**
     * The node the popup menu was created for
     */
    private final SortableDefaultMutableTreeNode popupNode;

    /**
     * Reference to the {@link NodeNavigatorInterface} which indicates the nodes
     * being displayed.
     */
    private final NodeNavigatorInterface mySetOfNodes;

    /**
     * Index of the {@link #mySetOfNodes} being popped up.
     */
    private final int index;  // default is 0

    /**
     * Handler for the RecentDropNodeChangedEvent
     */
    private class RecentDropNodeChangedEventHandler {

        /**
         * Handle the event by updating the submenu items
         *
         * @param event event
         */
        @Subscribe
        public void handleRecentDropNodeChangedEventHandler(RecentDropNodesChangedEvent event) {
            SwingUtilities.invokeLater(PicturePopupMenu.this::labelRecentDropNodes);

        }
    }

    /**
     * Here we update the labels on the recent drop nodes. Those that are null
     * are not shown. If no drop targets are shown at all the separator in the
     * submenu is not shown either.
     */
    private void labelRecentDropNodes() {
        boolean dropNodesVisible = false;
        SortableDefaultMutableTreeNode[] nodes = Settings.recentDropNodes.toArray(new SortableDefaultMutableTreeNode[0]);
        for (int i = 0; i < Settings.MAX_DROPNODES; i++) {
            if (i < nodes.length && nodes[i] != null) {
                recentDropNodeJMenuItems[i].setText(
                        Settings.jpoResources.getString("recentDropNodePrefix") + nodes[i].toString());
                recentDropNodeJMenuItems[i].setVisible(true);
                dropNodesVisible = true;
            } else {
                recentDropNodeJMenuItems[i].setVisible(false);
            }
        }
        if (dropNodesVisible) {
            movePictureNodeSeparator.setVisible(true);
        } else {
            movePictureNodeSeparator.setVisible(false);

        }
    }

    /**
     * Handler for the CopyLocationsChangedEvent
     */
    private class CopyLocationsChangedEventHandler {

        /**
         * Handle the event by updating the submenu items
         *
         * @param event event
         */
        @Subscribe
        public void handleCopyLocationsChangedEvent(CopyLocationsChangedEvent event) {
            SwingUtilities.invokeLater(PicturePopupMenu.this::labelCopyLocations);
            SwingUtilities.invokeLater(PicturePopupMenu.this::labelMoveLocations);
        }
    }

    /**
     * Here we update the labels of the copy locations.
     */
    private void labelCopyLocations() {
        String[] copyLocationsAsArray = Settings.copyLocations.toArray(new String[0]);
        for (int i = 0; i < copyLocationJMenuItems.length; i++) {
            if (i < copyLocationsAsArray.length) {
                copyLocationJMenuItems[i].setText(copyLocationsAsArray[i]);
                copyLocationJMenuItems[i].setVisible(true);
            } else {
                copyLocationJMenuItems[i].setVisible(false);

            }
        }
    }

    /**
     * Here we update the labels of the move locations.
     */
    private void labelMoveLocations() {
        String[] moveLocationsAsArray = Settings.copyLocations.toArray(new String[0]);
        for (int i = 0; i < moveLocationJMenuItems.length; i++) {
            if (i < moveLocationsAsArray.length) {
                moveLocationJMenuItems[i].setText(moveLocationsAsArray[i]);
                moveLocationJMenuItems[i].setVisible(true);
            } else {
                moveLocationJMenuItems[i].setVisible(false);

            }
        }
    }

    /**
     * Handler for the UserFunctionsChangedEvent
     */
    private class UserFunctionsChangedEventHandler {

        /**
         * Handle the event by updating the submenu items
         *
         * @param event event
         */
        @Subscribe
        public void handleUserFunctionsChangedEvent(UserFunctionsChangedEvent event) {
            SwingUtilities.invokeLater(PicturePopupMenu.this::labelUserFunctions
            );
        }
    }

    /**
     * This method populates the user functions sub entries on the menu.
     */
    private void labelUserFunctions() {
        for (int i = 0; i < Settings.maxUserFunctions; i++) {
            if ((Settings.userFunctionNames[i] != null) && (Settings.userFunctionNames[i].length() > 0) && (Settings.userFunctionCmd[i] != null) && (Settings.userFunctionCmd[i].length() > 0)) {
                userFunctionJMenuItems[i].setText(Settings.userFunctionNames[i]);
                userFunctionJMenuItems[i].setVisible(true);
            } else {
                userFunctionJMenuItems[i].setVisible(false);
            }
        }
    }

}
