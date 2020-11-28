package org.jpo.gui.swing;

import com.google.common.eventbus.Subscribe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jpo.cache.QUEUE_PRIORITY;
import org.jpo.datamodel.*;
import org.jpo.eventbus.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.isNull;

/*
 PicturePopupMenu.java:  a popup menu for pictures

 Copyright (C) 2002 - 2020  Richard Eigenmann.
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
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger(PicturePopupMenu.class.getName());


    /**
     * array of menu items that allows the user to call up a user function
     */
    private final JMenuItem[] userFunctionJMenuItems = new JMenuItem[Settings.MAX_USER_FUNCTIONS];

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
    private final JMenuItem[] recentDropNodeJMenuItems = new JMenuItem[Settings.getMaxDropnodes()];
    /**
     * The node the popup menu was created for
     */
    private final SortableDefaultMutableTreeNode popupNode;
    /**
     * Reference to the {@link NodeNavigatorInterface} which indicates the nodes
     * being displayed.
     */
    private final transient NodeNavigatorInterface mySetOfNodes;
    /**
     * Index of the {@link #mySetOfNodes} being popped up.
     */
    private final int index;  // default is 0


    /**
     * Creates a popup menu for a node holding a picture
     *
     * @param setOfNodes The set of nodes from which the popup picture is coming
     * @param idx        The picture of the set for which the popup is being shown.
     */
    public PicturePopupMenu(final NodeNavigatorInterface setOfNodes, final int idx) {
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
     *
     * @param s The source string
     * @return an Optional with the replaced string. The Optional isPresent() and can be
     * retrieved with get() if the name was different. If there was nothing to translate the
     * isPresent() method returns false. This allows the caller to easily tell if there is any
     * point in proposing a rename.
     */
    public static Optional<String> replaceEscapedSpaces(@NonNull final String s) {
        Objects.requireNonNull(s);
        final String newString = s.replaceAll("(%20)+", " ");
        if (newString.equals(s)) {
            return Optional.empty();
        } else {
            return Optional.of(newString);
        }
    }

    /**
     * Method to replace the %2520 that some filenames may have instead of a space char
     *
     * @param s The source string
     * @return an Optional with the replaced string. The Optional isPresent() and can be
     * retrieved with get() if the name was different. If there was nothing to translate the
     * isPresent() method returns false. This allows the caller to easily tell if there is any
     * point in proposing a rename.
     */
    public static Optional<String> replace2520(@NonNull final String s) {
        Objects.requireNonNull(s);
        final String newString = s.replaceAll("(%2520)+", " ");
        if (newString.equals(s)) {
            return Optional.empty();
        } else {
            return Optional.of(newString);
        }
    }

    /**
     * Method to replace the underscores that some filenames may have instead of a space char
     *
     * @param s The source string
     * @return an Optional with the replaced string. The Optional isPresent() and can be
     * retrieved with get() if the name was different. If there was nothing to translate the
     * isPresent() method returns false. This allows the caller to easily tell if there is any
     * point in proposing a rename.
     */
    public static Optional<String> replaceUnderscore(@NonNull String s) {
        Objects.requireNonNull(s);
        final String newString = s.replaceAll("_+", " ");
        if (newString.equals(s)) {
            return Optional.empty();
        } else {
            return Optional.of(newString);
        }
    }

    /**
     * initialises the GUI components
     */
    private void initComponents() {
        final String title = getTitle();
        setLabel(title);

        final JMenuItem titleJMenuItem = new JMenuItem(title);
        titleJMenuItem.setEnabled(false);
        add(titleJMenuItem);

        addSeparator();

        final JMenuItem showPictureMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureShowJMenuItemLabel"));
        showPictureMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowPictureRequest(popupNode)));
        add(showPictureMenuItem);

        final JMenuItem showMapMenuItem = new JMenuItem(Settings.getJpoResources().getString("mapShowJMenuItemLabel"));
        showMapMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowPictureOnMapRequest(popupNode)));
        add(showMapMenuItem);

        final JMenuItem openFolderJMenuItem = new JMenuItem(Settings.getJpoResources().getString("openFolderJMenuItem"));
        openFolderJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new OpenFileExplorerRequest(((PictureInfo) popupNode.getUserObject()).getImageFile().getParentFile())));
        add(openFolderJMenuItem);


        final JMenu navigateMenuItem = new JMenu(Settings.getJpoResources().getString("navigationJMenu"));
        final Set<SortableDefaultMutableTreeNode> linkingNodes = Settings.getPictureCollection().findLinkingGroups(popupNode);
        for (final SortableDefaultMutableTreeNode linkingNode : linkingNodes) {
            final JMenuItem navigateTargetRoute = new JMenuItem(linkingNode.getUserObject().toString());
            navigateTargetRoute.addActionListener(e -> JpoEventBus.getInstance().post(new ShowGroupRequest(linkingNode)));
            navigateMenuItem.add(navigateTargetRoute);
        }

        add(navigateMenuItem);

        final JMenuItem showCategoryUsageJMenuItemMenuItem = new JMenuItem(Settings.getJpoResources().getString("categoryUsageJMenuItem"));
        showCategoryUsageJMenuItemMenuItem.addActionListener((ActionEvent e) -> {
            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                final HashSet<SortableDefaultMutableTreeNode> hashSet = new HashSet<>();
                hashSet.add(popupNode);
                JpoEventBus.getInstance().post(new ShowCategoryUsageEditorRequest(hashSet));
            } else if (!Settings.getPictureCollection().isSelected(popupNode)) {
                Settings.getPictureCollection().clearSelection();
                final HashSet<SortableDefaultMutableTreeNode> hashSet = new HashSet<>();
                hashSet.add(popupNode);
                JpoEventBus.getInstance().post(new ShowCategoryUsageEditorRequest(hashSet));
            } else {
                final HashSet<SortableDefaultMutableTreeNode> hashSet = new HashSet<>(Settings.getPictureCollection().getSelection());
                JpoEventBus.getInstance().post(new ShowCategoryUsageEditorRequest(hashSet));

            }
        });
        add(showCategoryUsageJMenuItemMenuItem);

        final PictureCollection pictureCollection = Settings.getPictureCollection();
        final JMenuItem pictureMailSelectJMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureMailSelectJMenuItem"));
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
            final ArrayList<SortableDefaultMutableTreeNode> nodesList = new ArrayList<>(1);
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

        final JMenuItem pictureMailUnSelectJMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureMailUnselectJMenuItem"));
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

        final JMenuItem pictureMailUnselectAllJMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureMailUnselectAllJMenuItem"));
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

        pictureMailUnselectAllJMenuItem.setVisible(Settings.getPictureCollection()
                .countMailSelectedNodes() > 0);

        final JMenu userFunctionsJMenu = new JMenu(Settings.getJpoResources().getString("userFunctionsJMenu"));
        for (int i = 0;
             i < Settings.MAX_USER_FUNCTIONS;
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
            final JMenu rotationMenu = new JMenu(Settings.getJpoResources().getString("rotation"));

            final JMenuItem rotate90JMenuItem = new JMenuItem(Settings.getJpoResources().getString("rotate90"));
            rotate90JMenuItem.addActionListener((ActionEvent e) ->
                    JpoEventBus.getInstance().post(new RotatePictureRequest(popupNode, 90, QUEUE_PRIORITY.HIGH_PRIORITY)));
            rotationMenu.add(rotate90JMenuItem);

            final JMenuItem rotate180JMenuItem = new JMenuItem(Settings.getJpoResources().getString("rotate180"));
            rotate180JMenuItem.addActionListener((ActionEvent e) ->
                    JpoEventBus.getInstance().post(new RotatePictureRequest(popupNode, 180, QUEUE_PRIORITY.HIGH_PRIORITY)));
            rotationMenu.add(rotate180JMenuItem);

            final JMenuItem rotate270JMenuItem = new JMenuItem(Settings.getJpoResources().getString("rotate270"));
            rotate270JMenuItem.addActionListener((ActionEvent e) ->
                    JpoEventBus.getInstance().post(new RotatePictureRequest(popupNode, 270, QUEUE_PRIORITY.HIGH_PRIORITY)));
            rotationMenu.add(rotate270JMenuItem);

            final JMenuItem rotate0JMenuItem = new JMenuItem(Settings.getJpoResources().getString("rotate0"));
            rotate0JMenuItem.addActionListener((ActionEvent e) ->
                    JpoEventBus.getInstance().post(new SetPictureRotationRequest(popupNode, 0f, QUEUE_PRIORITY.HIGH_PRIORITY)));
            rotationMenu.add(rotate0JMenuItem);
            add(rotationMenu);
        }

        final JMenuItem pictureRefreshJMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureRefreshJMenuItem"));

        pictureRefreshJMenuItem.addActionListener((ActionEvent e) -> {
            if (!Settings.getPictureCollection().isSelected(popupNode)) {
                final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
                nodes.add(popupNode);
                JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, QUEUE_PRIORITY.HIGH_PRIORITY));
            } else {
                JpoEventBus.getInstance().post(new RefreshThumbnailRequest(Settings.getPictureCollection().getSelection(), QUEUE_PRIORITY.HIGH_PRIORITY));
            }
        });
        add(pictureRefreshJMenuItem);

        final JMenu moveJMenu = new JMenu(Settings.getJpoResources().getString("moveNodeJMenuLabel"));
        add(moveJMenu);

        for (int i = 0; i < Settings.getMaxDropnodes(); i++) {
            final int dropnode = i;
            recentDropNodeJMenuItems[i] = new JMenuItem();
            recentDropNodeJMenuItems[i].addActionListener((ActionEvent event) -> {
                        final SortableDefaultMutableTreeNode targetNode = Settings.getRecentDropNodes().toArray(new SortableDefaultMutableTreeNode[0])[dropnode];
                        final List<SortableDefaultMutableTreeNode> movingNodes = new ArrayList<>();
                        if ((Settings.getPictureCollection().countSelectedNodes() > 0) && (Settings.getPictureCollection().isSelected(popupNode))) {
                            movingNodes.addAll(Settings.getPictureCollection().getSelection());
                            Settings.getPictureCollection().clearSelection();
                        } else {
                            movingNodes.add(popupNode);
                        }
                        JpoEventBus.getInstance().post(new MoveNodeToNodeRequest(movingNodes, targetNode));

                        Settings.memorizeGroupOfDropLocation(Settings.getRecentDropNodes().toArray(new SortableDefaultMutableTreeNode[0])[dropnode]);
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

        final JMenuItem movePictureToTopJMenuItem = new JMenuItem(Settings.getJpoResources().getString("movePictureToTopJMenuItem"));
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

        final JMenuItem movePictureUpJMenuItem = new JMenuItem(Settings.getJpoResources().getString("movePictureUpJMenuItem"));
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

        final JMenuItem movePictureDownJMenuItem = new JMenuItem(Settings.getJpoResources().getString("movePictureDownJMenuItem"));
        movePictureDownJMenuItem.addActionListener((ActionEvent e) -> {
            if ((Settings.getPictureCollection().countSelectedNodes() < 1) || (!pictureCollection.isSelected(popupNode))) {
                JpoEventBus.getInstance().post(new MoveNodeDownRequest(popupNode));
            } else {
                for (final SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        JpoEventBus.getInstance().post(new MoveNodeDownRequest(selectedNode));
                        selectedNode.moveNodeDown();
                    }
                }
            }
        });
        moveJMenu.add(movePictureDownJMenuItem);

        final JMenuItem movePictureToBottomJMenuItem = new JMenuItem(Settings.getJpoResources().getString("movePictureToBottomJMenuItem"));
        movePictureToBottomJMenuItem.addActionListener((ActionEvent e) -> {
            if ((Settings.getPictureCollection().countSelectedNodes() < 1)
                    || (!pictureCollection.isSelected(popupNode))) {
                JpoEventBus.getInstance().post(new MoveNodeToBottomRequest(popupNode));
            } else {
                for (final SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        JpoEventBus.getInstance().post(new MoveNodeToBottomRequest(selectedNode));
                    }
                }
            }
        });
        moveJMenu.add(movePictureToBottomJMenuItem);

        final JMenuItem indentJMenuItem = new JMenuItem(Settings.getJpoResources().getString("indentJMenuItem"));
        indentJMenuItem.addActionListener((ActionEvent e) -> {
            final ArrayList<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
            if ((Settings.getPictureCollection().countSelectedNodes() < 1)
                    || (!pictureCollection.isSelected(popupNode))) {
                nodes.add(popupNode);
            } else {
                for (final SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        nodes.add(selectedNode);
                    }
                }
            }
            JpoEventBus.getInstance().post(new MoveIndentRequest(nodes));
        });
        moveJMenu.add(indentJMenuItem);

        final JMenuItem outdentJMenuItem = new JMenuItem(Settings.getJpoResources().getString("outdentJMenuItem"));
        outdentJMenuItem.addActionListener((ActionEvent e) -> {
            final ArrayList<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
            if ((Settings.getPictureCollection().countSelectedNodes() < 1)
                    || (!pictureCollection.isSelected(popupNode))) {
                nodes.add(popupNode);
            } else {
                for (final SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
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

        final JMenu copyJMenu = new JMenu(Settings.getJpoResources().getString("copyImageJMenuLabel"));
        add(copyJMenu);

        final JMenuItem copyToNewLocationJMenuItem = new JMenuItem(Settings.getJpoResources().getString("copyToNewLocationJMenuItem"));
        copyToNewLocationJMenuItem.addActionListener((ActionEvent e) -> {
            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
                nodes.add(popupNode);
                JpoEventBus.getInstance().post(new CopyToNewLocationRequest(nodes));
            } else {
                JpoEventBus.getInstance().post(new CopyToNewLocationRequest(Settings.getPictureCollection().getSelection()));
            }
        });
        copyJMenu.add(copyToNewLocationJMenuItem);

        copyJMenu.addSeparator();

        final String[] copyLocationsArray = Settings.getCopyLocations().toArray(new String[0]);
        for (int i = 0; i < Settings.MAX_MEMORISE; i++) {
            final File loc = i < copyLocationsArray.length ? new File(copyLocationsArray[i]) : new File(".");
            copyLocationJMenuItems[i] = new JMenuItem();
            copyLocationJMenuItems[i].addActionListener((ActionEvent ae) -> {
                if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                    final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
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

        final JMenuItem copyToNewZipfileJMenuItem = new JMenuItem(Settings.getJpoResources().getString("copyToNewZipfileJMenuItem"));
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

        final JMenuItem copyToClipboard = new JMenuItem("Copy Image to Clipboard");
        copyToClipboard.addActionListener((ActionEvent e) -> {
            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                ArrayList<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
                nodes.add(popupNode);
                JpoEventBus.getInstance().post(new CopyImageToClipboardRequest(nodes));
            } else {
                JpoEventBus.getInstance().post(new CopyImageToClipboardRequest(Settings.getPictureCollection().getSelection()));
            }
        });
        copyJMenu.add(copyToClipboard);

        final JMenuItem copyPathToClipboard = new JMenuItem("Copy Image Path to Clipboard");
        copyPathToClipboard.addActionListener((ActionEvent e) -> {
            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                ArrayList<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
                nodes.add(popupNode);
                JpoEventBus.getInstance().post(new CopyPathToClipboardRequest(nodes));
            } else {
                JpoEventBus.getInstance().post(new CopyPathToClipboardRequest(Settings.getPictureCollection().getSelection()));
            }
        });
        copyJMenu.add(copyPathToClipboard);


        final JMenuItem[] memorizedZipFileJMenuItems = new JMenuItem[Settings.MAX_MEMORISE];
        final String[] memorizedZipFilesArray = Settings.getMemorizedZipFiles().toArray(new String[0]);
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

        final JMenuItem pictureNodeRemove = new JMenuItem(Settings.getJpoResources().getString("pictureNodeRemove"));
        pictureNodeRemove.addActionListener((ActionEvent e) -> {
            final ArrayList<SortableDefaultMutableTreeNode> nodesToRemove = new ArrayList<>();
            final SortableDefaultMutableTreeNode actionNode = mySetOfNodes.getNode(index);
            if ((Settings.getPictureCollection().countSelectedNodes() > 1) && (Settings.getPictureCollection().isSelected(actionNode))) {
                for (SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        nodesToRemove.add(selectedNode);
                    }
                }
            } else {
                nodesToRemove.add(popupNode);
            }
            JpoEventBus.getInstance().post(new RemoveNodeRequest(nodesToRemove));
        });
        add(pictureNodeRemove);
        pictureNodeRemove.setVisible(pictureCollection.getAllowEdits());

        add(getFileOperationsMenu());
        add(getAssignCategoryMenu());

        final JMenuItem showPictureInfoEditorMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureEditJMenuItemLabel"));
        showPictureInfoEditorMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowPictureInfoEditorRequest(popupNode)));
        add(showPictureInfoEditorMenuItem);

        final JMenuItem consolidateHereMenuItem = new JMenuItem("Consolidate Here");

        final File imageFile = ((PictureInfo) popupNode.getUserObject()).getImageFile();
        if (!isNull(imageFile)) {
            consolidateHereMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(
                    new ConsolidateGroupDialogRequest(
                            popupNode.getParent(),
                            imageFile.getParentFile())
            ));
        }
        add(consolidateHereMenuItem);

    }

    private JMenu getFileOperationsMenu() {
        final JMenu fileOperationsJMenu = new JMenu(Settings.getJpoResources().getString("FileOperations"));

        final JMenuItem filenameJMenuItem = new JMenuItem();
        filenameJMenuItem.setEnabled(false);
        filenameJMenuItem.setText(getFilenameMenuText());
        fileOperationsJMenu.add(filenameJMenuItem);
        fileOperationsJMenu.addSeparator();

        final JMenu fileMoveJMenu = new JMenu(Settings.getJpoResources().getString("fileMoveJMenu"));
        fileOperationsJMenu.add(fileMoveJMenu);

        final JMenuItem moveToNewLocationJMenuItem = getMoveToNewLocationJMenuItem();
        fileMoveJMenu.add(moveToNewLocationJMenuItem);
        fileMoveJMenu.addSeparator();

        addMoveLocationTargets(fileMoveJMenu);
        labelMoveLocations();

        final JMenu fileRenameJMenu = new JMenu(Settings.getJpoResources().getString("renameJMenu"));
        fileOperationsJMenu.add(fileRenameJMenu);

        addRenameMenuItems(fileRenameJMenu);

        final JMenuItem fileDeleteJMenuItem = new JMenuItem(Settings.getJpoResources().getString("fileDeleteJMenuItem"));
        fileDeleteJMenuItem.addActionListener((ActionEvent e) -> {
            final SortableDefaultMutableTreeNode actionNode = mySetOfNodes.getNode(index);
            if ((Settings.getPictureCollection().countSelectedNodes() > 1) && (Settings.getPictureCollection().isSelected(actionNode))) {
                JpoEventBus.getInstance().post(new DeleteMultiNodeFileRequest(Settings.getPictureCollection().getSelection()));
            } else {
                JpoEventBus.getInstance().post(new DeleteNodeFileRequest(actionNode));
            }
        });
        fileOperationsJMenu.add(fileDeleteJMenuItem);

        fileOperationsJMenu.setVisible(Settings.getPictureCollection().getAllowEdits());
        fileRenameJMenu.setVisible(Settings.getPictureCollection().getAllowEdits());
        fileDeleteJMenuItem.setVisible(Settings.getPictureCollection().getAllowEdits());
        return fileOperationsJMenu;
    }

    private void addRenameMenuItems(final JMenu fileRenameJMenu) {
        final Collection<SortableDefaultMutableTreeNode> renameNodes = new ArrayList<>();
        if (Settings.getPictureCollection().countSelectedNodes() < 1) {
            renameNodes.add(popupNode);
        } else {
            renameNodes.addAll(Settings.getPictureCollection().getSelection());
        }
        for (final JComponent c : RenameMenuItems.getRenameMenuItems(renameNodes)) {
            fileRenameJMenu.add(c);
        }
    }

    private void addMoveLocationTargets(final JMenu fileMoveJMenu) {
        final String[] moveLocationsArray = Settings.getCopyLocations().toArray(new String[0]);
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
    }

    @NotNull
    private JMenuItem getMoveToNewLocationJMenuItem() {
        final JMenuItem moveToNewLocationJMenuItem = new JMenuItem(Settings.getJpoResources().getString("moveToNewLocationJMenuItem"));
        moveToNewLocationJMenuItem.addActionListener((ActionEvent e) -> {
            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
                nodes.add(popupNode);
                JpoEventBus.getInstance().post(new MoveToNewLocationRequest(nodes));
            } else {
                JpoEventBus.getInstance().post(new MoveToNewLocationRequest(Settings.getPictureCollection().getSelection()));
            }
        });
        return moveToNewLocationJMenuItem;
    }

    private String getFilenameMenuText() {
        if (Settings.getPictureCollection().countSelectedNodes() < 1) {
            final File imageFile = ((PictureInfo) popupNode.getUserObject()).getImageFile();
            if (isNull(imageFile)) {
                LOGGER.log(Level.SEVERE, "Node {0} doesn''t have an imageFile!", popupNode);
                return "Missing Filename";
            } else {
                return imageFile.getPath();
            }
        } else {
            return Settings.getPictureCollection().countSelectedNodes() + " pictures";
        }
    }

    /**
     * Creates a JMenu of categories that can be assigned
     *
     * @return a JMenu of categories that can be assigned
     */
    private JMenu getAssignCategoryMenu() {
        final JMenu assignCategorisJMenu = new JMenu("Assign Category");
        if ((Settings.getPictureCollection().countSelectedNodes() > 1) && (Settings.getPictureCollection().isSelected(popupNode))) {
            // if many nodes selected and the user clicked on one of them
            CategoryPopupMenu.addMenuItems(assignCategorisJMenu, Settings.getPictureCollection().getSelection());
        } else {
            // act only on the selected node
            CategoryPopupMenu.addMenuItems(assignCategorisJMenu, Collections.singletonList(popupNode));
        }
        return assignCategorisJMenu;
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
            if (popupNode.getUserObject() instanceof PictureInfo pi) {
                String description = pi.getDescription();
                title = description.length() > 25
                        ? description.substring(0, 25) + "..." : description;
            } else {
                title = "Picture Popup Menu";
            }
        }
        return title;
    }

    /**
     * Here we update the labels on the recent drop nodes. Those that are null
     * are not shown. If no drop targets are shown at all the separator in the
     * submenu is not shown either.
     */
    private void labelRecentDropNodes() {
        boolean dropNodesVisible = false;
        final SortableDefaultMutableTreeNode[] nodes = Settings.getRecentDropNodes().toArray(new SortableDefaultMutableTreeNode[0]);
        for (int i = 0; i < Settings.getMaxDropnodes(); i++) {
            if (i < nodes.length && nodes[i] != null) {
                recentDropNodeJMenuItems[i].setText(
                        Settings.getJpoResources().getString("recentDropNodePrefix") + nodes[i].toString());
                recentDropNodeJMenuItems[i].setVisible(true);
                dropNodesVisible = true;
            } else {
                recentDropNodeJMenuItems[i].setVisible(false);
            }
        }
        movePictureNodeSeparator.setVisible(dropNodesVisible);
    }

    /**
     * Here we update the labels of the copy locations.
     */
    private void labelCopyLocations() {
        final String[] copyLocationsAsArray = Settings.getCopyLocations().toArray(new String[0]);
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
        final String[] moveLocationsAsArray = Settings.getCopyLocations().toArray(new String[0]);
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
     * This method populates the user functions sub entries on the menu.
     */
    private void labelUserFunctions() {
        for (int i = 0; i < Settings.MAX_USER_FUNCTIONS; i++) {
            if ((Settings.getUserFunctionNames()[i] != null) && (Settings.getUserFunctionNames()[i].length() > 0) && (Settings.getUserFunctionCmd()[i] != null) && (Settings.getUserFunctionCmd()[i].length() > 0)) {
                userFunctionJMenuItems[i].setText(Settings.getUserFunctionNames()[i]);
                userFunctionJMenuItems[i].setVisible(true);
            } else {
                userFunctionJMenuItems[i].setVisible(false);
            }
        }
    }

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
        public void handleRecentDropNodeChangedEventHandler(final RecentDropNodesChangedEvent event) {
            SwingUtilities.invokeLater(PicturePopupMenu.this::labelRecentDropNodes);

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

}
