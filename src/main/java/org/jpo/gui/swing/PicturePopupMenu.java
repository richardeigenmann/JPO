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
 Copyright (C) 2002-2023 Richard Eigenmann.
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
     * Creates a popup menu for one node pointing at a picture
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
     * @param stringWithEscapedSpaces The source string
     * @return an Optional with the replaced string. The Optional isPresent() and can be
     * retrieved with get() if the name was different. If there was nothing to translate the
     * isPresent() method returns false. This allows the caller to easily tell if there is any
     * point in proposing a rename.
     */
    public static Optional<String> replaceEscapedSpaces(@NonNull final String stringWithEscapedSpaces) {
        Objects.requireNonNull(stringWithEscapedSpaces);
        final var newString = stringWithEscapedSpaces.replaceAll("(%20)+", " ");
        if (newString.equals(stringWithEscapedSpaces)) {
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
     * @param stringWithUnderscores The source string
     * @return an Optional with the replaced string. The Optional isPresent() and can be
     * retrieved with get() if the name was different. If there was nothing to translate the
     * isPresent() method returns false. This allows the caller to easily tell if there is any
     * point in proposing a rename.
     */
    public static Optional<String> replaceUnderscore(@NonNull String stringWithUnderscores) {
        Objects.requireNonNull(stringWithUnderscores);
        final var newString = stringWithUnderscores.replaceAll("_+", " ");
        if (newString.equals(stringWithUnderscores)) {
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
        add(showPictureJMenuItem());
        add(navigateToJMenuItem());
        addSeparator();
        add(mailSelectJMenuItem());
        add(mailUnSelectJMenuItem());
        add(mailUnselectAllJMenuItem());
        add(userFunctionsJMenu());
        add(rotationJMenu());
        add(refreshJMenuItem());
        add(moveJMenu(popupNode));
        add(copyJMenu());
        add(openFolderJMenuItem());
        add(getPictureNodeRemove());
        add(fileOperationsMenu());
        add(getAssignCategoryWindow());
        add(getConsolidateHereMenuItem());
        addSeparator();
        add(getShowPictureInfoEditorMenuItem());
    }

    private JMenuItem getAssignCategoryWindow() {
        final var assignCategoryWindowJMenuItem = new JMenuItem(Settings.getJpoResources().getString("assignCategoryWindowJMenuItem"));
        assignCategoryWindowJMenuItem.addActionListener((ActionEvent e) -> {
            final ArrayList<SortableDefaultMutableTreeNode> nodesToAssign = new ArrayList<>();
            final var actionNode = mySetOfNodes.getNode(index);
            if ((popupNode.getPictureCollection().countSelectedNodes() > 1) && (popupNode.getPictureCollection().isSelected(actionNode))) {
                for (var selectedNode : popupNode.getPictureCollection().getSelection()) {
                    if (selectedNode.getUserObject() instanceof PictureInfo) {
                        nodesToAssign.add(selectedNode);
                    }
                }
            } else {
                nodesToAssign.add(popupNode);
            }
            JpoEventBus.getInstance().post(new CategoryAssignmentWindowRequest(nodesToAssign));
        });
        assignCategoryWindowJMenuItem.setVisible(popupNode.getPictureCollection().getAllowEdits());
        return assignCategoryWindowJMenuItem;
    }

    private JMenuItem getTitleJMenuItem(final String title) {
        final var titleJMenuItem = new JMenuItem(title);
        titleJMenuItem.setEnabled(false);
        return titleJMenuItem;
    }

    private JMenuItem showPictureJMenuItem() {
        final var showPictureMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureShowJMenuItemLabel"));
        showPictureMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowPictureRequest(mySetOfNodes, index)));
        return showPictureMenuItem;
    }

    private JMenuItem openFolderJMenuItem() {
        final var openFolderJMenuItem = new JMenuItem(Settings.getJpoResources().getString("openFolderJMenuItem"));
        openFolderJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new OpenFileExplorerRequest(((PictureInfo) popupNode.getUserObject()).getImageFile().getParentFile())));
        return openFolderJMenuItem;
    }

    private JMenuItem navigateToJMenuItem() {
        final var navigateMenuItem = new JMenu(Settings.getJpoResources().getString("navigationJMenu"));
        final var linkingNodes = popupNode.getPictureCollection().findLinkingGroups(popupNode);
        for (final var linkingNode : linkingNodes) {
            final var navigateTargetRoute = new JMenuItem(linkingNode.getUserObject().toString());
            navigateTargetRoute.addActionListener(e -> JpoEventBus.getInstance().post(new ShowGroupRequest(linkingNode)));
            navigateMenuItem.add(navigateTargetRoute);
        }
        return navigateMenuItem;
    }


    /**
     * Adds a picture to the selection of pictures to be mailed.
     */
    private JMenuItem mailSelectJMenuItem() {
        final var pictureMailSelectJMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureMailSelectJMenuItem"));
        pictureMailSelectJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new AddPictureNodesToEmailSelectionRequest(getNodesToActOn())));
        pictureMailSelectJMenuItem.setVisible(isEmailSelectable(popupNode.getPictureCollection()));
        return pictureMailSelectJMenuItem;
    }

    /**
     * if there is no selection, and we click on a node which is not email selected
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
    private JMenuItem mailUnSelectJMenuItem() {
        final var pictureMailUnSelectJMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureMailUnselectJMenuItem"));
        pictureMailUnSelectJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new RemovePictureNodesFromEmailSelectionRequest(getNodesToActOn())));
        pictureMailUnSelectJMenuItem.setVisible(isEmailUnSelectable());
        return pictureMailUnSelectJMenuItem;
    }

    /**
     * if there is no selection, and we click on a node which is email selected
     * then offer to unselect it
     * if there is a selection and email selected, offer to unselect them
     * if there is a selection but the selected node is not part of it
     * and the node is selected then offer to unselect it
     */
    private boolean isEmailUnSelectable() {
        final var pictureCollection = popupNode.getPictureCollection();
        var emailUnSelectable = false;
        if ((pictureCollection.countSelectedNodes() == 0)
                || (!pictureCollection.isSelected(popupNode))) {

            // deal with single node
            emailUnSelectable = pictureCollection
                    .isMailSelected(popupNode);

        } else {
            // we have a selection and the popup node is part of it
            for (final var selectedNode : pictureCollection.getSelection()) {
                if ((selectedNode.getUserObject() instanceof PictureInfo)
                        && (pictureCollection.isMailSelected(selectedNode))) {
                    emailUnSelectable = true;
                    break;
                }
            }
        }
        return emailUnSelectable;
    }

    private JMenuItem mailUnselectAllJMenuItem() {
        final var pictureMailUnselectAllJMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureMailUnselectAllJMenuItem"));
        pictureMailUnselectAllJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ClearEmailSelectionRequest(popupNode.getPictureCollection())));
        pictureMailUnselectAllJMenuItem.setVisible(popupNode.getPictureCollection().countMailSelectedNodes() > 0);
        return pictureMailUnselectAllJMenuItem;
    }

    private JMenuItem refreshJMenuItem() {
        final var pictureRefreshJMenuItem = new JMenuItem(Settings.getJpoResources().getString("pictureRefreshJMenuItem"));
        pictureRefreshJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new RefreshThumbnailRequest(getNodesToActOn(), false, QUEUE_PRIORITY.HIGH_PRIORITY)));

        return pictureRefreshJMenuItem;
    }

    private JMenuItem getPictureNodeRemove() {
        final var pictureNodeRemove = new JMenuItem(Settings.getJpoResources().getString("pictureNodeRemove"));
        pictureNodeRemove.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new RemoveNodeRequest(getNodesToActOn())));
        pictureNodeRemove.setVisible(popupNode.getPictureCollection().getAllowEdits());
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
        consolidateHereMenuItem.setVisible(popupNode.getPictureCollection().getAllowEdits());
        return consolidateHereMenuItem;
    }


    private JMenu copyJMenu() {
        final var copyJMenu = new JMenu(Settings.getJpoResources().getString("copyImageJMenuLabel"));
        copyJMenu.add(copyToNewLocationJMenuItem());
        copyJMenu.addSeparator();
        addCopyLocationsJMenuItems(copyJMenu);
        labelCopyLocations();
        copyJMenu.addSeparator();
        copyJMenu.add(copyToNewZipfileJMenuItem());
        copyJMenu.add(copyToClipboard());
        copyJMenu.add(copyPathToClipboard());
        addMemorizedZipFileJMenuItems(copyJMenu);
        return copyJMenu;
    }

    @NotNull
    private JMenuItem copyToNewLocationJMenuItem() {
        final var copyToNewLocationJMenuItem = new JMenuItem(Settings.getJpoResources().getString("copyToNewLocationJMenuItem"));
        copyToNewLocationJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new CopyToNewLocationRequest(getNodesToActOn())));
        return copyToNewLocationJMenuItem;
    }

    private void addCopyLocationsJMenuItems(final JMenu copyJMenu) {
        final String[] copyLocationsArray = Settings.getCopyLocations().toArray(new String[0]);
        for (var i = 0; i < Settings.MAX_MEMORISE; i++) {
            final var loc = i < copyLocationsArray.length ? new File(copyLocationsArray[i]) : new File(".");
            copyLocationJMenuItems[i] = new JMenuItem();
            copyLocationJMenuItems[i].addActionListener((ActionEvent ae) -> JpoEventBus.getInstance().post(new CopyToDirRequest(getNodesToActOn(), loc)));
            copyJMenu.add(copyLocationJMenuItems[i]);
        }
    }

    private JMenuItem copyToNewZipfileJMenuItem() {
        final var copyToNewZipfileJMenuItem = new JMenuItem(Settings.getJpoResources().getString("copyToNewZipfileJMenuItem"));
        copyToNewZipfileJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new CopyToNewZipfileRequest(getNodesToActOn())));
        return copyToNewZipfileJMenuItem;
    }

    private JMenuItem copyToClipboard() {
        final var copyToClipboard = new JMenuItem(Settings.getJpoResources().getString("copyToClipboard"));
        copyToClipboard.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new CopyImageToClipboardRequest(getNodesToActOn())));

        return copyToClipboard;
    }

    private JMenuItem copyPathToClipboard() {
        final var copyPathToClipboard = new JMenuItem(Settings.getJpoResources().getString("copyPathToClipboard"));
        copyPathToClipboard.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new CopyPathToClipboardRequest(getNodesToActOn())));

        return copyPathToClipboard;
    }

    /**
     * Returns the nodes to act on:
     * If no nodes are selected and a node has been picked, this node is returned.
     * If there are nodes selected, and we have acted on a node that is part of the selection, return the selection.
     * If there are nodes selected but the action was on a non-selected node, ignore the selection and return the action node.
     *
     * @return one or many nodes to act on
     */
    Collection<SortableDefaultMutableTreeNode> getNodesToActOn() {
        if ( popupNode.getPictureCollection().getSelection().contains(popupNode) ) {
            return popupNode.getPictureCollection().getSelection();
        } else {
            return List.of(popupNode);
        }
    }

    private void addMemorizedZipFileJMenuItems(final JMenu copyJMenu) {
        final var memorizedZipFileJMenuItems = new JMenuItem[Settings.MAX_MEMORISE];
        final String[] memorizedZipFilesArray = Settings.getMemorizedZipFiles().toArray(new String[0]);
        for (var i = 0; i < Settings.MAX_MEMORISE; i++) {
            final var loc = (i < memorizedZipFilesArray.length) ? new File(memorizedZipFilesArray[i]) : new File(".");
            memorizedZipFileJMenuItems[i] = new JMenuItem();
            memorizedZipFileJMenuItems[i].addActionListener((ActionEvent ae) -> JpoEventBus.getInstance().post(new CopyToZipfileRequest(getNodesToActOn(), loc)));
            copyJMenu.add(memorizedZipFileJMenuItems[i]);
            if (i < memorizedZipFilesArray.length) {
                memorizedZipFileJMenuItems[i].setText(memorizedZipFilesArray[i]);
                memorizedZipFileJMenuItems[i].setVisible(true);
            } else {
                memorizedZipFileJMenuItems[i].setVisible(false);
            }
        }
    }


    private JMenu moveJMenu(final SortableDefaultMutableTreeNode popupNode) {
        final var moveJMenu = new JMenu(Settings.getJpoResources().getString("moveNodeJMenuLabel"));

        addRecentDropNodes(moveJMenu);
        moveJMenu.add(movePictureNodeSeparator);
        labelRecentDropNodes();

        moveJMenu.add(moveToTopJMenuItem());
        moveJMenu.add(moveUpJMenuItem());
        moveJMenu.add(moveDownJMenuItem());
        moveJMenu.add(moveToBottomJMenuItem());
        moveJMenu.add(indentJMenuItem());
        moveJMenu.add(outdentJMenuItem());
        moveJMenu.setVisible(popupNode.getPictureCollection().getAllowEdits());
        return moveJMenu;
    }

    private void addRecentDropNodes(final JMenu moveJMenu) {
        for (var i = 0; i < Settings.getMaxDropnodes(); i++) {
            final int dropnode = i;
            recentDropNodeJMenuItems[i] = new JMenuItem();
            recentDropNodeJMenuItems[i].addActionListener((ActionEvent event) -> {
                    final var targetNode = Settings.getRecentDropNodes().toArray(new SortableDefaultMutableTreeNode[0])[dropnode];
                    JpoEventBus.getInstance().post(new MoveNodeToNodeRequest(getNodesToActOn(), targetNode));
                    Settings.memorizeGroupOfDropLocation(Settings.getRecentDropNodes().toArray(new SortableDefaultMutableTreeNode[0])[dropnode]);
                    JpoEventBus.getInstance().post(new RecentDropNodesChangedEvent());
                }
            );
            moveJMenu.add(recentDropNodeJMenuItems[i]);
        }
    }

    private JMenuItem moveToTopJMenuItem() {
        final var movePictureToTopJMenuItem = new JMenuItem(Settings.getJpoResources().getString("movePictureToTopJMenuItem"));
        movePictureToTopJMenuItem.addActionListener((ActionEvent event) -> JpoEventBus.getInstance().post(new MoveNodeToTopRequest(getNodesToActOn())));

        return movePictureToTopJMenuItem;
    }

    private JMenuItem moveUpJMenuItem() {
        final var movePictureUpJMenuItem = new JMenuItem(Settings.getJpoResources().getString("movePictureUpJMenuItem"));
        movePictureUpJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new MoveNodeUpRequest(getNodesToActOn())));
        return movePictureUpJMenuItem;
    }

    private JMenuItem moveDownJMenuItem() {
        final var movePictureDownJMenuItem = new JMenuItem(Settings.getJpoResources().getString("movePictureDownJMenuItem"));
        movePictureDownJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new MoveNodeDownRequest(getNodesToActOn())));

        return movePictureDownJMenuItem;
    }

    private JMenuItem moveToBottomJMenuItem() {
        final var movePictureToBottomJMenuItem = new JMenuItem(Settings.getJpoResources().getString("movePictureToBottomJMenuItem"));
        movePictureToBottomJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new MoveNodeToBottomRequest(getNodesToActOn())));

        return movePictureToBottomJMenuItem;
    }

    private JMenuItem indentJMenuItem() {
        final var indentJMenuItem = new JMenuItem(Settings.getJpoResources().getString("indentJMenuItem"));
        indentJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new MoveIndentRequest(getNodesToActOn())));

        return indentJMenuItem;
    }

    private JMenuItem outdentJMenuItem() {
        final var outdentJMenuItem = new JMenuItem(Settings.getJpoResources().getString("outdentJMenuItem"));
        outdentJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new MoveOutdentRequest(getNodesToActOn())));

        return outdentJMenuItem;
    }

    private JMenu rotationJMenu() {
        final var rotationMenu = new JMenu(Settings.getJpoResources().getString("rotation"));

        final var rotate90JMenuItem = new JMenuItem(Settings.getJpoResources().getString("rotate90"));
        rotate90JMenuItem.addActionListener((ActionEvent e) ->
                JpoEventBus.getInstance().post(new RotatePicturesRequest(getNodesToActOn(), 90, QUEUE_PRIORITY.HIGH_PRIORITY)));
        rotationMenu.add(rotate90JMenuItem);

        final var rotate180JMenuItem = new JMenuItem(Settings.getJpoResources().getString("rotate180"));
        rotate180JMenuItem.addActionListener((ActionEvent e) ->
                JpoEventBus.getInstance().post(new RotatePicturesRequest(getNodesToActOn(), 180, QUEUE_PRIORITY.HIGH_PRIORITY)));
        rotationMenu.add(rotate180JMenuItem);

        final var rotate270JMenuItem = new JMenuItem(Settings.getJpoResources().getString("rotate270"));
        rotate270JMenuItem.addActionListener((ActionEvent e) ->
                JpoEventBus.getInstance().post(new RotatePicturesRequest(getNodesToActOn(), 270, QUEUE_PRIORITY.HIGH_PRIORITY)));
        rotationMenu.add(rotate270JMenuItem);

        final var rotate0JMenuItem = new JMenuItem(Settings.getJpoResources().getString("rotate0"));
        rotate0JMenuItem.addActionListener((ActionEvent e) ->
                JpoEventBus.getInstance().post(new SetPictureRotationRequest(getNodesToActOn(), 0f, QUEUE_PRIORITY.HIGH_PRIORITY)));
        rotationMenu.add(rotate0JMenuItem);
        rotationMenu.setVisible(popupNode.getPictureCollection().getAllowEdits());
        return rotationMenu;
    }

    @NotNull
    private JMenu userFunctionsJMenu() {
        final var userFunctionsJMenu = new JMenu(Settings.getJpoResources().getString("userFunctionsJMenu"));
        for (var i = 0;
             i < Settings.MAX_USER_FUNCTIONS;
             i++) {
            final var userFunction = i;
            userFunctionJMenuItems[i] = new JMenuItem();
            userFunctionJMenuItems[i].addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new RunUserFunctionRequest(userFunction, getNodesToActOn())));
            userFunctionsJMenu.add(userFunctionJMenuItems[i]);
        }
        labelUserFunctions();
        return userFunctionsJMenu;
    }

    private JMenu fileOperationsMenu() {
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
        fileDeleteJMenuItem.addActionListener((final ActionEvent e) -> JpoEventBus.getInstance().post(new DeleteNodeFileRequest(getNodesToActOn())));
        fileOperationsJMenu.add(fileDeleteJMenuItem);

        fileOperationsJMenu.setVisible(popupNode.getPictureCollection().getAllowEdits());
        fileRenameJMenu.setVisible(popupNode.getPictureCollection().getAllowEdits());
        fileDeleteJMenuItem.setVisible(popupNode.getPictureCollection().getAllowEdits());
        return fileOperationsJMenu;
    }

    /**
     * Adds the Rename > Rename menu item. The button will trigger a rename on the selected picture
     * or the set of pictures if the popup was triggered on one of the selection.
     *
     * @param fileRenameJMenu the JMenu to which to attach the menu entry
     */
    private void addRenameMenuItems(final JMenu fileRenameJMenu) {
        //TODO: EventBus missing!
        for (final var component : RenameMenuItems.getRenameMenuItems(getNodesToActOn())) {
            fileRenameJMenu.add(component);
        }
    }

    private void addMoveLocationTargets(final JMenu fileMoveJMenu) {
        final String[] moveLocationsArray = Settings.getCopyLocations().toArray(new String[0]);
        for (var i = 0; i < Settings.MAX_MEMORISE; i++) {
            final File loc = i < moveLocationsArray.length ? new File(moveLocationsArray[i]) : new File(".");
            moveLocationJMenuItems[i] = new JMenuItem();
            moveLocationJMenuItems[i].addActionListener((ActionEvent ae) -> JpoEventBus.getInstance().post(new MoveToDirRequest(getNodesToActOn(), loc)));
            fileMoveJMenu.add(moveLocationJMenuItems[i]);
        }
    }

    @NotNull
    private JMenuItem getMoveToNewLocationJMenuItem() {
        final var moveToNewLocationJMenuItem = new JMenuItem(Settings.getJpoResources().getString("moveToNewLocationJMenuItem"));
        moveToNewLocationJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new MoveToNewLocationRequest(getNodesToActOn())));
        return moveToNewLocationJMenuItem;
    }

    private String getFilenameMenuText() {
        if (popupNode.getPictureCollection().countSelectedNodes() > 1
                && popupNode.getPictureCollection().isSelected(popupNode)) {
            return popupNode.getPictureCollection().countSelectedNodes() + " pictures";
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
     * Otherwise, it returns the description of the picture.
     *
     * @return the title for the popup menu
     */
    private String getTitle() {
        String title;
        final var pictureCollection = popupNode.getPictureCollection();
        if ((pictureCollection.countSelectedNodes() > 1) && (pictureCollection.isSelected(popupNode))) {
            title = String.format("%d nodes", pictureCollection.countSelectedNodes());
        } else {
            if (popupNode.getUserObject() instanceof PictureInfo pictureInfo) {
                String description = pictureInfo.getDescription();
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
            if ((Settings.getUserFunctionNames()[i] != null) && (!Settings.getUserFunctionNames()[i].isEmpty()) && (Settings.getUserFunctionCmd()[i] != null) && (!Settings.getUserFunctionCmd()[i].isEmpty())) {
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
