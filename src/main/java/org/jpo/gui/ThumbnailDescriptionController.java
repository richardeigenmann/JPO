package org.jpo.gui;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.TestOnly;
import org.jpo.datamodel.*;
import org.jpo.gui.swing.PicturePopupMenu;
import org.jpo.gui.swing.ThumbnailDescriptionPanel;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import java.util.logging.Logger;

import static java.awt.event.MouseEvent.BUTTON3;
import static org.jpo.gui.ThumbnailDescriptionController.DescriptionSize.LARGE_DESCRIPTION;
import static org.jpo.gui.ThumbnailDescriptionController.DescriptionSize.MINI_INFO;

/*
 ThumbnailDescriptionController.java:  class that creates a panel showing the description of a thumbnail

 Copyright (C) 2002 - 2020  Richard Eigenmann, Zürich, Switzerland
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
 * ThumbnailDescriptionJPanel is a JPanel that displays the metadata of a
 * thumbnail. It knows the node it is representing. It can be told to change the
 * node it is showing. It can be mute. It knows it's x and y position in the
 * grid
 */
public class ThumbnailDescriptionController
        implements PictureInfoChangeListener,
        TreeModelListener {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ThumbnailDescriptionController.class.getName());

    /**
     * a link to the SortableDefaultMutableTreeNode in the data model. This
     * allows thumbnails to be selected by sending a nodeSelected event to the
     * data model.
     */
    protected SortableDefaultMutableTreeNode referringNode;

    private final ThumbnailDescriptionPanel panel = new ThumbnailDescriptionPanel();

    public JPanel getPanel() {
        return panel;
    }





    /**
     * choices for the Description size
     */
    public enum DescriptionSize {

        /**
         * Descriptions should be in a large font
         */
        LARGE_DESCRIPTION,
        /**
         * Descriptions should be in a small font
         */
        MINI_INFO
    }

    /**
     * This field controls how the description panel is shown. It can be set to
     * ThumbnailDescriptionJPanel.LARGE_DESCRIPTION,
     * ThumbnailDescriptionJPanel.MINI_INFO,
     */
    private DescriptionSize displayMode = LARGE_DESCRIPTION;





    /**
     * Construct a new ThumbnailDescrciptionJPanel
     */
    public ThumbnailDescriptionController() {
        initComponents();
    }

    private void initComponents() {
        // attach this panel to the tree model so that it is notified about changes
        Settings.getPictureCollection().getTreeModel().addTreeModelListener(this);


        panel.getPictureDescriptionJTA().setInputVerifier(new InputVerifier() {

            @Override
            public boolean verify(JComponent component) {
                return true;
            }

            @Override
            public boolean shouldYieldFocus(JComponent source, JComponent target) {
                doUpdate();
                return true;
            }
        });



        panel.getPictureDescriptionJTA().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == BUTTON3) {
                    Optional<JPopupMenu> optional = correctTextPopupMenu(panel.getDescription(), panel.getPictureDescriptionJTA());
                    if (optional.isPresent()) {
                        JPopupMenu popupmenu = optional.get();
                        popupmenu.show(panel.getPictureDescriptionJTA(), e.getX(), e.getY());
                    }
                }
            }
        });

        panel.getPictureDescriptionJSP().getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> panel.setTextAreaSize());

        setVisible(false);

    }
    public static Optional<JPopupMenu> correctTextPopupMenu (@NonNull String text, @NonNull JTextArea textArea) {
        Optional<String> oSpace = PicturePopupMenu.replaceEscapedSpaces(text);
        Optional<String> oUnderstore = PicturePopupMenu.replaceUnderscore(text);
        if (oSpace.isPresent() || oUnderstore.isPresent()) {
            JPopupMenu popupmenu = new JPopupMenu();

            if (oSpace.isPresent()) {
                JMenuItem replaceSpace = new JMenuItem("Replace with: " + oSpace.get());
                replaceSpace.addActionListener(e1 -> textArea.setText(oSpace.get()));
                popupmenu.add(replaceSpace);
            }
            if (oUnderstore.isPresent()) {
                JMenuItem replaceUnderscore = new JMenuItem("Replace with: " + oUnderstore.get());
                replaceUnderscore.addActionListener(e1 -> textArea.setText(oUnderstore.get()));
                popupmenu.add(replaceUnderscore);
            }
            if (oUnderstore.isPresent() && oSpace.isPresent()) {
                Optional<String> spaceUnderscore = PicturePopupMenu.replaceUnderscore(oSpace.get());
                if (spaceUnderscore.isPresent()) {
                    // to be expected...
                    JMenuItem replaceSpaceAndUnderscore = new JMenuItem("Replace with: " + spaceUnderscore.get());
                    replaceSpaceAndUnderscore.addActionListener(e1 -> textArea.setText(spaceUnderscore.get()));
                    popupmenu.add(replaceSpaceAndUnderscore);
                }
            }
            return Optional.of(popupmenu);
        }
        return Optional.empty();
    }

    /**
     * doUpdate writes the changed text back to the data model and submits an
     * nodeChanged notification on the model. It gets called by the
     * Inputverifier on the text area.
     */
    public void doUpdate() {
        if (referringNode == null) {
            return;
        }
        Object userObject = referringNode.getUserObject();
        if (userObject != null  && !panel.getDescription().equals(userObject.toString())) {
            // the description was changed
            if (userObject instanceof PictureInfo) {
                ((PictureInfo) referringNode.getUserObject()).setDescription(panel.getDescription());
            } else if (userObject instanceof GroupInfo) {
                ((GroupInfo) referringNode.getUserObject()).setGroupName(panel.getDescription());
            }
        }
    }

    /**
     * This method sets the node which the ThumbnailDescriptionJPanel should
     * display. If it should display nothing then set it to null.
     *
     * @param referringNode The Node to be displayed
     */
    public void setNode(SortableDefaultMutableTreeNode referringNode) {
        if (this.referringNode == referringNode) {
            // Don't refresh the node if it hasn't changed
            return;
        }

        // flush any uncommitted changes
        doUpdate();

        // unattach the change Listener
        if ((this.referringNode != null) && (this.referringNode.getUserObject() instanceof PictureInfo)) {
            PictureInfo pi = (PictureInfo) this.referringNode.getUserObject();
            pi.removePictureInfoChangeListener(this);
        }

        this.referringNode = referringNode;

        // attach the change Listener
        if ((referringNode != null) && (referringNode.getUserObject() instanceof PictureInfo)) {
            PictureInfo pictureInfo = (PictureInfo) referringNode.getUserObject();
            pictureInfo.addPictureInfoChangeListener(this);
        }

        String legend;
        if (referringNode == null) {
            legend = Settings.jpoResources.getString("ThumbnailDescriptionNoNodeError");
            setVisible(false);
        } else if (referringNode.getUserObject() instanceof PictureInfo) {
            PictureInfo pi = (PictureInfo) referringNode.getUserObject();
            legend = pi.getDescription();
            panel.getHighresLocationJTextField().setText(pi.getImageLocation());
            setVisible(true);
        } else if (referringNode.getUserObject() instanceof GroupInfo) {
            legend = ((GroupInfo) referringNode.getUserObject()).getGroupName();
            panel.getHighresLocationJTextField().setText("");
            setVisible(true);
        } else {
            legend = "Error";
            panel.getHighresLocationJTextField().setText("");
            setVisible(true);
        }
        panel.setDescription(legend);

        formatDescription();
        showSlectionStatus();
    }

    @TestOnly
    public String getDescription() {
        return panel.getDescription();
    }

    /**
     * This method how the description panel is shown. It can be set to
     * ThumbnailDescriptionJPanel.LARGE_DESCRIPTION,
     * ThumbnailDescriptionJPanel.MINI_INFO,
     *
     * @param displayMode display Mode
     */
    public void setDisplayMode(DescriptionSize displayMode) {
        this.displayMode = displayMode;
    }

    /**
     * This method formats the text information fields for the indicated node.
     */
    public void formatDescription() {
        if (displayMode == LARGE_DESCRIPTION) {
            panel.getPictureDescriptionJTA().setFont(panel.getLargeFont());
        } else {
            // i.e.  MINI_INFO
            panel.getPictureDescriptionJTA().setFont(panel.getSmallFont());
        }
        panel.setTextAreaSize();

        if ((referringNode != null) && (referringNode.getUserObject() instanceof PictureInfo) && (displayMode == MINI_INFO)) {
            panel.getHighresLocationJTextField().setVisible(true);
        } else {
            panel.getHighresLocationJTextField().setVisible(false);
        }

    }



    /**
     * Overridden method to allow the better tuning of visibility
     *
     * @param visibility Send in true or false
     */
    public void setVisible(boolean visibility) {
        panel.setVisible(visibility);
        panel.getPictureDescriptionJTA().setVisible(visibility);
        panel.getPictureDescriptionJSP().setVisible(visibility);
    }

    /**
     * changes the colour so that the user sees whether the thumbnail is part of
     * the selection
     */
    public void showSlectionStatus() {
        panel.showAsSelected(Settings.getPictureCollection().isSelected(referringNode));
    }



    /**
     * Returns the preferred size for the ThumbnailDescription as a Dimension
     * using the thumbnailSize as width and height.
     *
     * @return Returns the preferred size for the ThumbnailDescription as a
     * Dimension using the thumbnailSize as width and height.
     */
    public Dimension getPreferredSize() {
        Dimension d = panel.getPreferredSize();
        int height = 0;
        if (panel.isVisible()) {
            height = d.height;
        }
        return new Dimension(d.width, height);
    }

    /**
     * This method sets the scaling factor for the display of a thumbnail
     * description
     *
     * @param thumbnailSizeFactor Factor
     */
    public void setFactor(float thumbnailSizeFactor) {
        panel.setThumbnailSizeFactor(thumbnailSizeFactor);
    }

    /**
     * returns the current node
     *
     * @return the current node
     */
    public SortableDefaultMutableTreeNode getNode() {
        return referringNode;
    }

    /**
     * here we get notified by the PictureInfo object that something has
     * changed.
     */
    @Override
    public void pictureInfoChangeEvent(final PictureInfoChangeEvent pictureInfoChangeEvent) {
        Runnable runnable = () -> {
            if (pictureInfoChangeEvent.getDescriptionChanged()) {
                panel.setDescription(pictureInfoChangeEvent.getPictureInfo().getDescription());
            }

            if (pictureInfoChangeEvent.getHighresLocationChanged()) {
                panel.getHighresLocationJTextField().setText(pictureInfoChangeEvent.getPictureInfo().getImageLocation());
            }

            if (pictureInfoChangeEvent.getWasSelected()) {
                panel.showAsSelected();
            } else if (pictureInfoChangeEvent.getWasUnselected()) {
                panel.showAsUnselected();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }

    }

    // Here we are not that interested in TreeModel change events other than to find out if our
    // current node was removed in which case we close the Window.

    /**
     * implemented here to satisfy the TreeModelListener interface; not used.
     *
     * @param e event
     */
    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        // find out whether our node was changed
        Object[] children = e.getChildren();
        if (children == null) {
            // the root node does not have children as it doesn't have a parent
            return;
        }

        for (Object child : children) {
            if ( child instanceof  SortableDefaultMutableTreeNode ) {
                SortableDefaultMutableTreeNode childNode = (SortableDefaultMutableTreeNode) child;
                if (childNode.equals(referringNode)) {
                    // we are displaying a changed node. What changed?
                    Object userObject = referringNode.getUserObject();
                    if (userObject instanceof GroupInfo) {
                        String legend = ((GroupInfo) userObject).getGroupName();
                        if (!legend.equals(panel.getDescription())) {
                            panel.setDescription(legend);
                        }
                    }
                }
            }
        }
    }

    /**
     * implemented here to satisfy the TreeModelListener interface; not used.
     *
     * @param e event
     */
    @Override
    public void treeNodesInserted(TreeModelEvent e) {
    }

    /**
     * The TreeModelListener interface tells us of tree node removal events.
     *
     * @param e event
     */
    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
    }

    /**
     * implemented here to satisfy the TreeModelListener interface; not used.
     *
     * @param e event
     */
    @Override
    public void treeStructureChanged(TreeModelEvent e) {
    }
}
