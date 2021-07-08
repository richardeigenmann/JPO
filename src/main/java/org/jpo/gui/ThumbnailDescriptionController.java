package org.jpo.gui;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.TestOnly;
import org.jpo.datamodel.*;
import org.jpo.eventbus.CategoryAssignmentWindowRequest;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.RemoveCategoryFromPictureInfoRequest;
import org.jpo.eventbus.ShowQueryRequest;
import org.jpo.gui.swing.CategoryButton;
import org.jpo.gui.swing.PicturePopupMenu;
import org.jpo.gui.swing.RenameMenuItems;
import org.jpo.gui.swing.ThumbnailDescriptionPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static java.awt.event.MouseEvent.BUTTON3;
import static org.jpo.gui.ThumbnailDescriptionController.DescriptionSize.LARGE_DESCRIPTION;
import static org.jpo.gui.ThumbnailDescriptionController.DescriptionSize.MINI_INFO;

/*
 ThumbnailDescriptionController.java:  class that creates a panel showing the description of a thumbnail

 Copyright (C) 2002 - 2021  Richard Eigenmann, Zürich, Switzerland
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
 * ThumbnailDescriptionJPanel is a JPanel that displays the metadata of a
 * thumbnail. It knows the node it is representing. It can be told to change the
 * node it is showing. It can be mute. It knows it's x and y position in the
 * grid
 */
public class ThumbnailDescriptionController
        implements PictureInfoChangeListener, GroupInfoChangeListener {


    /**
     * The panel with the actual descriptions
     */
    private final ThumbnailDescriptionPanel panel = new ThumbnailDescriptionPanel();

    /**
     * a link to the SortableDefaultMutableTreeNode in the data model. This
     * allows thumbnails to be selected by sending a nodeSelected event to the
     * data model.
     */
    protected SortableDefaultMutableTreeNode referringNode;

    /**
     * This field controls how the description panel is shown. It can be set to
     * ThumbnailDescriptionJPanel.LARGE_DESCRIPTION,
     * ThumbnailDescriptionJPanel.MINI_INFO,
     */
    private DescriptionSize displayMode = LARGE_DESCRIPTION;

    /**
     * Tracks whether to show the filename or not
     */
    private Boolean showFilenameState = Settings.isShowFilenamesOnThumbnailPanel();

    /**
     * Construct a new ThumbnailDescriptionJPanel
     */
    public ThumbnailDescriptionController() {
        initComponents();
    }

    /**
     * Returns a popup menu if there are some text patterns that JPO knows how to clean up
     *
     * @param text     The text to clean up
     * @param textArea The widget upon which to open to popup menu
     * @return an optional pop up menu
     */
    public static Optional<JPopupMenu> correctTextPopupMenu(@NonNull String text, @NonNull JTextArea textArea) {
        final Optional<String> oSpace = PicturePopupMenu.replaceEscapedSpaces(text);
        final Optional<String> oUnderscore = PicturePopupMenu.replaceUnderscore(text);
        if (oSpace.isPresent() || oUnderscore.isPresent()) {
            final var popupmenu = new JPopupMenu();
            final var REPLACE_WITH = Settings.getJpoResources().getString("ReplaceWith");
            if (oSpace.isPresent()) {
                final var replaceSpace = new JMenuItem(REPLACE_WITH + oSpace.get());
                replaceSpace.addActionListener(e1 -> textArea.setText(oSpace.get()));
                popupmenu.add(replaceSpace);
            }
            if (oUnderscore.isPresent()) {
                final var replaceUnderscore = new JMenuItem(REPLACE_WITH + oUnderscore.get());
                replaceUnderscore.addActionListener(e1 -> textArea.setText(oUnderscore.get()));
                popupmenu.add(replaceUnderscore);
            }
            if (oUnderscore.isPresent() && oSpace.isPresent()) {
                final Optional<String> spaceUnderscore = PicturePopupMenu.replaceUnderscore(oSpace.get());
                if (spaceUnderscore.isPresent()) {
                    // to be expected...
                    final var replaceSpaceAndUnderscore = new JMenuItem(REPLACE_WITH + spaceUnderscore.get());
                    replaceSpaceAndUnderscore.addActionListener(e1 -> textArea.setText(spaceUnderscore.get()));
                    popupmenu.add(replaceSpaceAndUnderscore);
                }
            }
            return Optional.of(popupmenu);
        }
        return Optional.empty();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void initComponents() {
        panel.getPictureDescriptionJTA().setInputVerifier(new InputVerifier() {

            @Override
            public boolean verify(final JComponent component) {
                return true;
            }

            @Override
            public boolean shouldYieldFocus(final JComponent source, final JComponent target) {
                doUpdate();
                return true;
            }
        });


        panel.getPictureDescriptionJTA().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == BUTTON3) {
                    final Optional<JPopupMenu> optional = correctTextPopupMenu(panel.getDescription(), panel.getPictureDescriptionJTA());
                    if (optional.isPresent()) {
                        final JPopupMenu popupmenu = optional.get();
                        popupmenu.show(panel.getPictureDescriptionJTA(), e.getX(), e.getY());
                    }
                }
            }
        });

        panel.getPictureDescriptionJSP().getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> panel.setTextAreaSize());

        panel.getHighresLocationJTextField().addMouseListener(new MouseAdapter() {
                                                                  @Override
                                                                  public void mouseReleased(MouseEvent e) {
                                                                      if (e.getButton() == BUTTON3) {
                                                                          final var popupmenu = new JPopupMenu();
                                                                          for (final JComponent c : RenameMenuItems.getRenameMenuItems(Collections.singleton(referringNode))) {
                                                                              popupmenu.add(c);
                                                                          }
                                                                          popupmenu.show(panel.getHighresLocationJTextField(), e.getX(), e.getY());
                                                                      }
                                                                  }
                                                              }
        );

        setVisible(false);

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
        final var userObject = referringNode.getUserObject();
        if (userObject != null && !panel.getDescription().equals(userObject.toString())) {
            // the description was changed
            if (userObject instanceof PictureInfo pi) {
                pi.setDescription(panel.getDescription());
            } else if (userObject instanceof GroupInfo gi) {
                gi.setGroupName(panel.getDescription());
            }
        }
    }

    @TestOnly
    public String getDescription() {
        return panel.getDescription();
    }

    private void setDescription() {
        String legend;
        if (referringNode == null) {
            legend = Settings.getJpoResources().getString("ThumbnailDescriptionNoNodeError");
        } else if (referringNode.getUserObject() instanceof PictureInfo pi) {
            legend = pi.getDescription();
        } else if (referringNode.getUserObject() instanceof GroupInfo gi) {
            legend = gi.getGroupName();
        } else {
            legend = "Error";
        }
        panel.setDescription(legend);
    }

    private void setFileLocation() {
        if (referringNode == null) {
            panel.getHighresLocationJTextField().setText("null");
        } else if (referringNode.getUserObject() instanceof PictureInfo pi) {
            if (pi.getImageFile() != null) {
                panel.getHighresLocationJTextField().setText(pi.getImageFile().toString());
            }
        } else if (referringNode.getUserObject() instanceof GroupInfo) {
            panel.getHighresLocationJTextField().setText("");
        } else {
            panel.getHighresLocationJTextField().setText("Error");
        }
    }

    /**
     * makes the ThumbnailDescriptionPanel show all the categories of the PictureInfo
     */
    private void setCategories() {
        panel.clearCategories();
        if (referringNode != null && referringNode.getUserObject() instanceof PictureInfo pi) {
            final Collection<Integer> categories = pi.getCategoryAssignments();
            if (categories != null) {
                categories.forEach(category -> {
                    final var categoryDescription = Settings.getPictureCollection().getCategory(category);
                    final var categoryButton = new CategoryButton(categoryDescription);
                    panel.addToCategopriesJPanel(categoryButton);
                    categoryButton.addRemovalListener(e ->
                            JpoEventBus.getInstance().post(
                                    new RemoveCategoryFromPictureInfoRequest(category, pi)
                            )
                    );
                    categoryButton.addClickListener(e ->
                            JpoEventBus.getInstance().post(
                                    new ShowQueryRequest(new CategoryQuery(category))
                            )
                    );

                });
            }
            panel.addCategoryMenu();
        }
    }

    @TestOnly
    public String getFileLocation() {
        return panel.getHighresLocationJTextField().getText();
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
            panel.getPictureDescriptionJTA().setFont(ThumbnailDescriptionPanel.getLargeFont());
        } else {
            // i.e.  MINI_INFO
            panel.getPictureDescriptionJTA().setFont(ThumbnailDescriptionPanel.getSmallFont());
        }
        panel.setTextAreaSize();

        panel.showFilename(
                (referringNode != null)
                        && (referringNode.getUserObject() instanceof PictureInfo)
                        && (displayMode == MINI_INFO)
        );

    }

    /**
     * Overridden method to allow the better tuning of visibility
     *
     * @param visibility Send in true or false
     */
    public void setVisible(boolean visibility) {
        panel.setVisible(visibility);
    }

    /**
     * changes the colour so that the user sees whether the thumbnail is part of
     * the selection
     */
    public void showSelectionStatus() {
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
        final Dimension d = panel.getPreferredSize();
        var height = 0;
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
    public void setFactor(final float thumbnailSizeFactor) {
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
     * This method sets the node which the ThumbnailDescriptionJPanel should
     * display. If it should display nothing then set it to null.
     *
     * @param referringNode The Node to be displayed
     */
    public void setNode(final SortableDefaultMutableTreeNode referringNode) {
        if (this.referringNode == referringNode) {
            // Don't refresh the node if it hasn't changed
            return;
        }

        // flush any uncommitted changes
        doUpdate();

        // unattach the change Listener
        if (this.referringNode != null) {
            if (this.referringNode.getUserObject() instanceof PictureInfo pi) {
                pi.removePictureInfoChangeListener(this);
            } else if (this.referringNode.getUserObject() instanceof GroupInfo gi) {
                gi.removeGroupInfoChangeListener(this);
            }
            for (ActionListener al : panel.getCategoryMenuPopupButton().getActionListeners()) {
                panel.getCategoryMenuPopupButton().removeActionListener(al);
            }
        }

        this.referringNode = referringNode;

        // attach the change Listener
        if (referringNode != null) {
            if (referringNode.getUserObject() instanceof PictureInfo pi) {
                pi.addPictureInfoChangeListener(this);
                panel.getCategoryMenuPopupButton().addActionListener(e ->
                        JpoEventBus.getInstance().post(new CategoryAssignmentWindowRequest(Collections.singletonList(referringNode)))
                );

            } else if (referringNode.getUserObject() instanceof GroupInfo gi) {
                gi.addGroupInfoChangeListener(this);
            }
        }

        // If the Controller is not showing a node then it should not be visible
        setVisible(referringNode != null);
        setDescription();
        setFileLocation();
        setCategories();

        formatDescription();
        showSelectionStatus();
    }

    /**
     * here we get notified by the PictureInfo object that something has
     * changed.
     */
    @Override
    public void pictureInfoChangeEvent(final PictureInfoChangeEvent pictureInfoChangeEvent) {
        Runnable runnable = () -> {
            if (pictureInfoChangeEvent.getDescriptionChanged()) {
                setDescription();
            }

            if (pictureInfoChangeEvent.getHighresLocationChanged()) {
                setFileLocation();
            }

            if (pictureInfoChangeEvent.getCategoryAssignmentsChanged()) {
                setCategories();
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

    @Override
    public void groupInfoChangeEvent(final GroupInfoChangeEvent groupInfoChangeEvent) {
        final Runnable runnable = () -> {
            if (groupInfoChangeEvent.getGroupNameChanged()) {
                setDescription();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    /**
     * Makes the filename visible or hidden depending on the supplied parameter.
     *
     * @param showFilename send true to make it visible, false to hide it.
     */
    public void showFilename(final boolean showFilename) {
        if (Boolean.TRUE.equals(showFilenameState)
                && (referringNode != null)
                && (referringNode.getUserObject() != null)
                && !(referringNode.getUserObject() instanceof PictureInfo)) {
            // ignore the instruction and turn it off:
            panel.showFilename(false);
        }
        panel.showFilename(showFilename);
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

}
