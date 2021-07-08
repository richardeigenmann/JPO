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

 Copyright (C) 2002 - 2021  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
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
        final var newString = s.replaceAll("(%20)+", " ");
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
        final var newString = s.replaceAll("(%2520)+", " ");
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
        final var newString = s.replaceAll("_+", " ");
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
        final var title = getTitle();
        setLabel(title);
        add(getTitleJMenuItem(title));
        addSeparator();
        add(getShowPictureMenuItem());
        add(getShowMapMenuItem());
        add(getOpenFolderJMenuItem());
        add(getNavigateMenuItem());
        add(getShowCategoryUsageJMenuItemMenuItem());
        add(getPictureMailSelectJMenuItem());
        add(getPictureMailUnSelectJMenuItem());
        add(getPictureMailUnselectAllJMenuItem());
        add(getUserFunctionsJMenu());
        if (popupNode.getPictureCollection().getAllowEdits()) {
            add(getRotationJMenu(popupNode));
        }
        add(getPictureRefreshJMenuItem());
        add(getMoveJMenu(popupNode));
        add(getCopyJMenu());
        add(getPictureNodeRemove());
        add(getFileOperationsMenu());
        add(getAssignCategoryWindow());
        add(getShowPictureInfoEditorMenuItem());
        add(getConsolidateHereMenuItem());
    }

    private JMenuItem getAssignCategoryWindow() {
        final var assignCategoryWindowJMenuItem = new JMenuItem(Settings.getJpoResources().getString("assignCategoryWindowJMenuItem"));
        assignCategoryWindowJMenuItem.addActionListener((ActionEvent e) -> {
            final ArrayList<SortableDefaultMutableTreeNode> nodesToAssign = new ArrayList<>();
            final SortableDefaultMutableTreeNode actionNode = mySetOfNodes.getNode(index);
            if ((Settings.getPictureCollection().countSelectedNodes() > 1) && (Settings.getPictureCollection().isSelected(actionNode))) {
                for (SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        nodesToAssign.add(selectedNode);
                    }
                }
            } else {
                nodesToAssign.add(popupNode);
            }
            JpoEventBus.getInstance().post(new CategoryAssignmentWindowRequest(nodesToAssign));
        });
        return assignCategoryWindowJMenuItem;
    }

    private JMenuItem getTitleJMenuItem(String title) {
        final var titleJMenuItem = new JMenuItem(title);
        titleJMenuItem.setEnabled(false);
        return titleJMenuItem;
    }

    private JMenuItem getShowPictureMenuItem() {
        final var showPictureMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureShowJMenuItemLabel"));
        showPictureMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowPictureRequest(mySetOfNodes, index)));
        return showPictureMenuItem;
    }

    private JMenuItem getShowMapMenuItem() {
        final var showMapMenuItem = new JMenuItem(Settings.getJpoResources().getString("mapShowJMenuItemLabel"));
        showMapMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowPictureOnMapRequest(popupNode)));
        return showMapMenuItem;
    }

    private JMenuItem getOpenFolderJMenuItem() {
        final var openFolderJMenuItem = new JMenuItem(Settings.getJpoResources().getString("openFolderJMenuItem"));
        openFolderJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new OpenFileExplorerRequest(((PictureInfo) popupNode.getUserObject()).getImageFile().getParentFile())));
        return openFolderJMenuItem;
    }

    private JMenuItem getNavigateMenuItem() {
        final var navigateMenuItem = new JMenu(Settings.getJpoResources().getString("navigationJMenu"));
        final Set<SortableDefaultMutableTreeNode> linkingNodes = Settings.getPictureCollection().findLinkingGroups(popupNode);
        for (final SortableDefaultMutableTreeNode linkingNode : linkingNodes) {
            final var navigateTargetRoute = new JMenuItem(linkingNode.getUserObject().toString());
            navigateTargetRoute.addActionListener(e -> JpoEventBus.getInstance().post(new ShowGroupRequest(linkingNode)));
            navigateMenuItem.add(navigateTargetRoute);
        }
        return navigateMenuItem;
    }

    private JMenuItem getShowCategoryUsageJMenuItemMenuItem() {
        final var showCategoryUsageJMenuItemMenuItem = new JMenuItem(Settings.getJpoResources().getString("categoryUsageJMenuItem"));
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
        return showCategoryUsageJMenuItemMenuItem;
    }


    /**
     * Adds a picture to the selection of pictures to be mailed.
     */
    private JMenuItem getPictureMailSelectJMenuItem() {
        final var pictureCollection = Settings.getPictureCollection();
        final var pictureMailSelectJMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureMailSelectJMenuItem"));
        pictureMailSelectJMenuItem.addActionListener((ActionEvent e) -> {
            final ArrayList<SortableDefaultMutableTreeNode> nodesToMailSelect = new ArrayList<>(1);
            if ((pictureCollection.countSelectedNodes() < 1)
                    || (!pictureCollection.isSelected(popupNode))) {
                // if there are no selected nodes or the action fired on a node that is not part of the selection,
                // add just this node to the new mail selection
                nodesToMailSelect.add(popupNode);
            } else {
                // add the lot
                for (final SortableDefaultMutableTreeNode selectedNode : pictureCollection.getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        nodesToMailSelect.add(selectedNode);
                    }
                }
            }
            JpoEventBus.getInstance().post(new AddPictureNodesToEmailSelectionRequest(nodesToMailSelect));
        });


        pictureMailSelectJMenuItem.setVisible(isEmailSelectable(pictureCollection));
        return pictureMailSelectJMenuItem;
    }

    /**
     * if there is no selection and we click on a node which is not email selected
     * then offer to email select it
     * if there is a selection but some are not email selected, offer to select them
     * if there is a selection but the selected node is not part of it
     * and the node is not selected then offer to select it
     */
    private boolean isEmailSelectable(PictureCollection pictureCollection) {
        var emailSelectable = false;
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
        return emailSelectable;
    }

    /**
     * 1. If no nodes are selected, mail-unselect the node.
     * <p>
     * 2. If multiple nodes are selected but the popup node is not one of them
     * then mail-unselect the node
     * <p>
     * 3. If multiple nodes are selected and the popup node is one of them then
     * mail-unselect them all
     */
    private JMenuItem getPictureMailUnSelectJMenuItem() {
        final var pictureMailUnSelectJMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureMailUnselectJMenuItem"));
        pictureMailUnSelectJMenuItem.addActionListener((ActionEvent e) -> {
            ArrayList<SortableDefaultMutableTreeNode> nodesList = new ArrayList<>(1);
            if ((Settings.getPictureCollection().countSelectedNodes() == 0)
                    || (!Settings.getPictureCollection().isSelected(popupNode))) {
                nodesList.add(popupNode);
            } else {
                for (SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        nodesList.add(selectedNode);
                    }
                }
            }
            JpoEventBus.getInstance().post(new RemovePictureNodesFromEmailSelectionRequest(nodesList));
        });


        pictureMailUnSelectJMenuItem.setVisible(isEmailUnSelectable());
        return pictureMailUnSelectJMenuItem;
    }

    /**
     * if there is no selection and we click on a node which is email selected
     * then offer to unselect it
     * if there is a selection and email selected, offer to unselect them
     * if there is a selection but the selected node is not part of it
     * and the node is selected then offer to unselect it
     */
    private boolean isEmailUnSelectable() {
        var emailUnSelectable = false;
        if ((Settings.getPictureCollection().countSelectedNodes() == 0)
                || (!Settings.getPictureCollection().isSelected(popupNode))) {

            // deal with single node
            emailUnSelectable = popupNode.getPictureCollection()
                    .isMailSelected(popupNode);

        } else {
            // we have a selection and the popup node is part of it
            for (final SortableDefaultMutableTreeNode selectedNode : Settings.getPictureCollection().getSelection()) {
                if ((selectedNode.getUserObject() instanceof PictureInfo)
                        && (Settings.getPictureCollection().isMailSelected(selectedNode))) {
                    emailUnSelectable = true;
                    break;
                }
            }
        }
        return emailUnSelectable;
    }

    private JMenuItem getPictureMailUnselectAllJMenuItem() {
        final var pictureMailUnselectAllJMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureMailUnselectAllJMenuItem"));
        pictureMailUnselectAllJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ClearEmailSelectionRequest()));
        pictureMailUnselectAllJMenuItem.setVisible(Settings.getPictureCollection().countMailSelectedNodes() > 0);
        return pictureMailUnselectAllJMenuItem;
    }

    private JMenuItem getPictureRefreshJMenuItem() {
        final var pictureRefreshJMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureRefreshJMenuItem"));

        pictureRefreshJMenuItem.addActionListener((ActionEvent e) -> {
            if (!Settings.getPictureCollection().isSelected(popupNode)) {
                final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
                nodes.add(popupNode);
                JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, QUEUE_PRIORITY.HIGH_PRIORITY));
            } else {
                JpoEventBus.getInstance().post(new RefreshThumbnailRequest(Settings.getPictureCollection().getSelection(), QUEUE_PRIORITY.HIGH_PRIORITY));
            }
        });

        return pictureRefreshJMenuItem;
    }

    private JMenuItem getPictureNodeRemove() {
        final var pictureNodeRemove = new JMenuItem(Settings.getJpoResources().getString("pictureNodeRemove"));
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
        pictureNodeRemove.setVisible(Settings.getPictureCollection().getAllowEdits());
        return pictureNodeRemove;
    }

    private JMenuItem getShowPictureInfoEditorMenuItem() {
        final var showPictureInfoEditorMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureEditJMenuItemLabel"));
        showPictureInfoEditorMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowPictureInfoEditorRequest(popupNode)));
        return showPictureInfoEditorMenuItem;
    }

    private JMenuItem getConsolidateHereMenuItem() {
        final var consolidateHereMenuItem = new JMenuItem("Consolidate Here");

        final var imageFile = ((PictureInfo) popupNode.getUserObject()).getImageFile();
        if (!isNull(imageFile)) {
            consolidateHereMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(
                    new ConsolidateGroupDialogRequest(
                            popupNode.getParent(),
                            imageFile.getParentFile())
            ));
        }
        return consolidateHereMenuItem;
    }


    private JMenu getCopyJMenu() {
        final var copyJMenu = new JMenu(Settings.getJpoResources().getString("copyImageJMenuLabel"));
        copyJMenu.add(getCopyToNewLocationJMenuItem());
        copyJMenu.addSeparator();
        addCopyLocationsJMenuItems(copyJMenu);
        labelCopyLocations();
        copyJMenu.addSeparator();
        copyJMenu.add(getCopyToNewZipfileJMenuItem());
        copyJMenu.add(getCopyToClipboard());
        copyJMenu.add(getCopyPathToClipboard());
        addMemorizedZipFileJMenuItems(copyJMenu);
        return copyJMenu;
    }

    @NotNull
    private JMenuItem getCopyToNewLocationJMenuItem() {
        final var copyToNewLocationJMenuItem = new JMenuItem(Settings.getJpoResources().getString("copyToNewLocationJMenuItem"));
        copyToNewLocationJMenuItem.addActionListener((ActionEvent e) -> {
            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
                nodes.add(popupNode);
                JpoEventBus.getInstance().post(new CopyToNewLocationRequest(nodes));
            } else {
                JpoEventBus.getInstance().post(new CopyToNewLocationRequest(Settings.getPictureCollection().getSelection()));
            }
        });
        return copyToNewLocationJMenuItem;
    }

    private void addCopyLocationsJMenuItems(final JMenu copyJMenu) {
        final String[] copyLocationsArray = Settings.getCopyLocations().toArray(new String[0]);
        for (var i = 0; i < Settings.MAX_MEMORISE; i++) {
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
    }

    private JMenuItem getCopyToNewZipfileJMenuItem() {
        final var copyToNewZipfileJMenuItem = new JMenuItem(Settings.getJpoResources().getString("copyToNewZipfileJMenuItem"));
        copyToNewZipfileJMenuItem.addActionListener((ActionEvent e) -> {
            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                List<SortableDefaultMutableTreeNode> selection = new ArrayList<>();
                selection.add(popupNode);
                JpoEventBus.getInstance().post(new CopyToNewZipfileRequest(selection));
            } else {
                JpoEventBus.getInstance().post(new CopyToNewZipfileRequest(Settings.getPictureCollection().getSelection()));
            }
        });
        return copyToNewZipfileJMenuItem;
    }

    private JMenuItem getCopyToClipboard() {
        final var copyToClipboard = new JMenuItem("Copy Image to Clipboard");
        copyToClipboard.addActionListener((ActionEvent e) -> {
            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                ArrayList<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
                nodes.add(popupNode);
                JpoEventBus.getInstance().post(new CopyImageToClipboardRequest(nodes));
            } else {
                JpoEventBus.getInstance().post(new CopyImageToClipboardRequest(Settings.getPictureCollection().getSelection()));
            }
        });

        return copyToClipboard;
    }

    private JMenuItem getCopyPathToClipboard() {
        final var copyPathToClipboard = new JMenuItem("Copy Image Path to Clipboard");
        copyPathToClipboard.addActionListener((ActionEvent e) -> {
            if (Settings.getPictureCollection().countSelectedNodes() < 1) {
                ArrayList<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
                nodes.add(popupNode);
                JpoEventBus.getInstance().post(new CopyPathToClipboardRequest(nodes));
            } else {
                JpoEventBus.getInstance().post(new CopyPathToClipboardRequest(Settings.getPictureCollection().getSelection()));
            }
        });

        return copyPathToClipboard;
    }

    private void addMemorizedZipFileJMenuItems(final JMenu copyJMenu) {
        final var memorizedZipFileJMenuItems = new JMenuItem[Settings.MAX_MEMORISE];
        final String[] memorizedZipFilesArray = Settings.getMemorizedZipFiles().toArray(new String[0]);
        for (var i = 0; i < Settings.MAX_MEMORISE; i++) {
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
    }


    private JMenu getMoveJMenu(final SortableDefaultMutableTreeNode popupNode) {
        final var moveJMenu = new JMenu(Settings.getJpoResources().getString("moveNodeJMenuLabel"));

        addRecentDropNodes(popupNode, moveJMenu);
        moveJMenu.add(movePictureNodeSeparator);
        labelRecentDropNodes();

        final var pictureCollection = Settings.getPictureCollection();
        moveJMenu.add(getMovePictureToTopJMenuItem(pictureCollection));
        moveJMenu.add(getMovePictureUpJMenuItem(pictureCollection));
        moveJMenu.add(getMovePictureDownJMenuItem(pictureCollection));
        moveJMenu.add(getMovePictureToBottomJMenuItem(pictureCollection));
        moveJMenu.add(getIndentJMenuItem(pictureCollection));
        moveJMenu.add(getOutdentJMenuItem(pictureCollection));
        moveJMenu.setVisible(popupNode.getPictureCollection().getAllowEdits());
        return moveJMenu;
    }

    private void addRecentDropNodes(final SortableDefaultMutableTreeNode popupNode, final JMenu moveJMenu) {
        for (var i = 0; i < Settings.getMaxDropnodes(); i++) {
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
    }

    private JMenuItem getMovePictureToTopJMenuItem(final PictureCollection pictureCollection) {
        final var movePictureToTopJMenuItem = new JMenuItem(Settings.getJpoResources().getString("movePictureToTopJMenuItem"));
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

        return movePictureToTopJMenuItem;
    }

    private JMenuItem getMovePictureUpJMenuItem(final PictureCollection pictureCollection) {
        final var movePictureUpJMenuItem = new JMenuItem(Settings.getJpoResources().getString("movePictureUpJMenuItem"));
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
        return movePictureUpJMenuItem;
    }

    private JMenuItem getMovePictureDownJMenuItem(final PictureCollection pictureCollection) {
        final var movePictureDownJMenuItem = new JMenuItem(Settings.getJpoResources().getString("movePictureDownJMenuItem"));
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

        return movePictureDownJMenuItem;
    }

    private JMenuItem getMovePictureToBottomJMenuItem(final PictureCollection pictureCollection) {
        final var movePictureToBottomJMenuItem = new JMenuItem(Settings.getJpoResources().getString("movePictureToBottomJMenuItem"));
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

        return movePictureToBottomJMenuItem;
    }

    private JMenuItem getIndentJMenuItem(final PictureCollection pictureCollection) {
        final var indentJMenuItem = new JMenuItem(Settings.getJpoResources().getString("indentJMenuItem"));
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

        return indentJMenuItem;
    }

    private JMenuItem getOutdentJMenuItem(final PictureCollection pictureCollection) {
        final var outdentJMenuItem = new JMenuItem(Settings.getJpoResources().getString("outdentJMenuItem"));
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

        return outdentJMenuItem;
    }

    private JMenu getRotationJMenu(SortableDefaultMutableTreeNode popupNode) {
        final var rotationMenu = new JMenu(Settings.getJpoResources().getString("rotation"));

        final var rotate90JMenuItem = new JMenuItem(Settings.getJpoResources().getString("rotate90"));
        rotate90JMenuItem.addActionListener((ActionEvent e) ->
                JpoEventBus.getInstance().post(new RotatePictureRequest(popupNode, 90, QUEUE_PRIORITY.HIGH_PRIORITY)));
        rotationMenu.add(rotate90JMenuItem);

        final var rotate180JMenuItem = new JMenuItem(Settings.getJpoResources().getString("rotate180"));
        rotate180JMenuItem.addActionListener((ActionEvent e) ->
                JpoEventBus.getInstance().post(new RotatePictureRequest(popupNode, 180, QUEUE_PRIORITY.HIGH_PRIORITY)));
        rotationMenu.add(rotate180JMenuItem);

        final var rotate270JMenuItem = new JMenuItem(Settings.getJpoResources().getString("rotate270"));
        rotate270JMenuItem.addActionListener((ActionEvent e) ->
                JpoEventBus.getInstance().post(new RotatePictureRequest(popupNode, 270, QUEUE_PRIORITY.HIGH_PRIORITY)));
        rotationMenu.add(rotate270JMenuItem);

        final var rotate0JMenuItem = new JMenuItem(Settings.getJpoResources().getString("rotate0"));
        rotate0JMenuItem.addActionListener((ActionEvent e) ->
                JpoEventBus.getInstance().post(new SetPictureRotationRequest(popupNode, 0f, QUEUE_PRIORITY.HIGH_PRIORITY)));
        rotationMenu.add(rotate0JMenuItem);
        return rotationMenu;
    }

    @NotNull
    private JMenu getUserFunctionsJMenu() {
        final var userFunctionsJMenu = new JMenu(Settings.getJpoResources().getString("userFunctionsJMenu"));
        for (var i = 0;
             i < Settings.MAX_USER_FUNCTIONS;
             i++) {
            final var userFunction = i;
            userFunctionJMenuItems[i] = new JMenuItem();
            userFunctionJMenuItems[i].addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new RunUserFunctionRequest(userFunction, (PictureInfo) popupNode.getUserObject())));
            userFunctionsJMenu.add(userFunctionJMenuItems[i]);
        }
        labelUserFunctions();
        return userFunctionsJMenu;
    }

    private JMenu getFileOperationsMenu() {
        final var fileOperationsJMenu = new JMenu(Settings.getJpoResources().getString("FileOperations"));

        final var filenameJMenuItem = new JMenuItem();
        filenameJMenuItem.setEnabled(false);
        filenameJMenuItem.setText(getFilenameMenuText());
        fileOperationsJMenu.add(filenameJMenuItem);
        fileOperationsJMenu.addSeparator();

        final var fileMoveJMenu = new JMenu(Settings.getJpoResources().getString("fileMoveJMenu"));
        fileOperationsJMenu.add(fileMoveJMenu);

        final var moveToNewLocationJMenuItem = getMoveToNewLocationJMenuItem();
        fileMoveJMenu.add(moveToNewLocationJMenuItem);
        fileMoveJMenu.addSeparator();

        addMoveLocationTargets(fileMoveJMenu);
        labelMoveLocations();

        final var fileRenameJMenu = new JMenu(Settings.getJpoResources().getString("renameJMenu")); // Rename
        fileOperationsJMenu.add(fileRenameJMenu);

        addRenameMenuItems(fileRenameJMenu);

        final var fileDeleteJMenuItem = new JMenuItem(Settings.getJpoResources().getString("fileDeleteJMenuItem"));
        fileDeleteJMenuItem.addActionListener((final ActionEvent e) -> {
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

    /**
     * Adds a the Rename > Rename menu item. The button will trigger a rename on the selected picture
     * or the set of pictures if the popup was triggered on on of the selection.
     *
     * @param fileRenameJMenu the JMenu to which to attach the menu entry
     */
    private void addRenameMenuItems(final JMenu fileRenameJMenu) {
        final Collection<SortableDefaultMutableTreeNode> renameNodes = new ArrayList<>();
        if (Settings.getPictureCollection().countSelectedNodes() > 1
                && Settings.getPictureCollection().isSelected(popupNode)) {
            renameNodes.addAll(Settings.getPictureCollection().getSelection());
        } else {
            renameNodes.add(popupNode);
        }
        for (final JComponent c : RenameMenuItems.getRenameMenuItems(renameNodes)) {
            fileRenameJMenu.add(c);
        }
    }

    private void addMoveLocationTargets(final JMenu fileMoveJMenu) {
        final String[] moveLocationsArray = Settings.getCopyLocations().toArray(new String[0]);
        for (var i = 0; i < Settings.MAX_MEMORISE; i++) {
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
        final var moveToNewLocationJMenuItem = new JMenuItem(Settings.getJpoResources().getString("moveToNewLocationJMenuItem"));
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
        if (Settings.getPictureCollection().countSelectedNodes() > 1
                && Settings.getPictureCollection().isSelected(popupNode)) {
            return Settings.getPictureCollection().countSelectedNodes() + " pictures";
        } else {
            final var imageFile = ((PictureInfo) popupNode.getUserObject()).getImageFile();
            if (isNull(imageFile)) {
                LOGGER.log(Level.SEVERE, "Node {0} doesn''t have an imageFile!", popupNode);
                return "Missing Filename";
            } else {
                return imageFile.getPath();
            }
        }
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
        var dropNodesVisible = false;
        final SortableDefaultMutableTreeNode[] nodes = Settings.getRecentDropNodes().toArray(new SortableDefaultMutableTreeNode[0]);
        for (var i = 0; i < Settings.getMaxDropnodes(); i++) {
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
        for (var i = 0; i < copyLocationJMenuItems.length; i++) {
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
        for (var i = 0; i < moveLocationJMenuItems.length; i++) {
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
        for (var i = 0; i < Settings.MAX_USER_FUNCTIONS; i++) {
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
