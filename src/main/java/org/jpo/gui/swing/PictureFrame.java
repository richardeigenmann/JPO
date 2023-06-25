package org.jpo.gui.swing;

import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;
import org.jpo.gui.OverlayedPictureController;
import org.jpo.datamodel.ScalablePicture;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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
 * Class that manages the frame and display of the Picture.
 *
 * @author Richard Eigenmann
 */
public class PictureFrame {

    /**
     * The pane that handles the image drawing aspects.
     */
    private final OverlayedPictureController pictureController = new OverlayedPictureController(new ScalablePicture());
    /**
     * Navigation Panel
     */
    private final PictureViewerNavBar navButtonPanel = new PictureViewerNavBar();
    /**
     * The root JPanel
     */
    private final JPanel viewerPanel;
    /**
     * The Window in which the viewer will place its components.
     */
    private final ResizableJFrame myJFrame;
    /**
     * progress bar to track the pictures loaded so far
     */
    private final JProgressBar loadJProgressBar = new JProgressBar();
    /**
     * This textarea shows the description of the picture being shown
     */
    private final JTextArea descriptionJTextField = new JTextArea();

    /**
     * Constructor. Initialises the GUI widgets.
     */
    public PictureFrame() {
        viewerPanel = new JPanel();
        initializeGui();
        myJFrame = new ResizableJFrame(Settings.getJpoResources().getString("PictureViewerTitle"));
        myJFrame.getContentPane().add(viewerPanel);
        myJFrame.pack();
        switch (Settings.getNewViewerSizeChoice()) {
            case 0 -> myJFrame.switchWindowMode(ResizableJFrame.WindowSize.WINDOW_DECORATED_FULLSCREEN);
            case 1 -> myJFrame.switchWindowMode(ResizableJFrame.WindowSize.WINDOW_DECORATED_PRIMARY);
            case 2 -> myJFrame.switchWindowMode(ResizableJFrame.WindowSize.WINDOW_DECORATED_SECONDARY);
            default -> myJFrame.switchWindowMode(ResizableJFrame.WindowSize.WINDOW_CUSTOM_SIZE_LAST_VIEWER);
        }
        myJFrame.setVisible(true);
    }

    /**
     * Provides direct access to the panel that shows the picture.
     *
     * @return the Picture Controller
     */
    public OverlayedPictureController getPictureController() {
        return pictureController;
    }

    /**
     * Provides direct access to the navButtonPanel
     *
     * @return the navButtonPanel
     */
    public PictureViewerNavBar getPictureViewerNavBar() {
        return navButtonPanel;
    }

    /**
     * Returns the Component of the Description Text Area so that others can
     * attach a FocusListener to it.
     *
     * @return Component of the Description Text Area
     */
    public Component getFocussableDescriptionField() {
        return descriptionJTextField;
    }

    /**
     * This method creates all the GUI widgets and connects them for the
     * PictureViewer.
     */
    private void initializeGui() {
        Tools.checkEDT();

        viewerPanel.setBackground(Settings.getPictureviewerBackgroundColor());
        viewerPanel.setOpaque(true);
        viewerPanel.setFocusable(false);
        viewerPanel.setLayout(new MigLayout("insets 0", "[grow, fill]", "[grow, fill][]"));
        viewerPanel.add(pictureController, "span, grow");

        viewerPanel.add(getLowerBar());
    }

    @NotNull
    private JPanel getLowerBar() {
        final var lowerBar = new JPanel(new MigLayout("insets 0, wrap 3", "[left][grow, fill][right]", "[]"));
        lowerBar.setBackground(Settings.getPictureviewerBackgroundColor());
        lowerBar.setOpaque(true);
        lowerBar.setFocusable(false);

        loadJProgressBar.setPreferredSize(new Dimension(120, 20));
        loadJProgressBar.setMaximumSize(new Dimension(140, 20));
        loadJProgressBar.setMinimumSize(new Dimension(80, 20));
        loadJProgressBar.setBackground(Settings.getPictureviewerBackgroundColor());
        loadJProgressBar.setBorderPainted(true);
        loadJProgressBar.setBorder(BorderFactory.createLineBorder(Color.gray, 1));

        loadJProgressBar.setMinimum(0);
        loadJProgressBar.setMaximum(100);
        loadJProgressBar.setStringPainted(true);
        loadJProgressBar.setVisible(false);

        lowerBar.add(loadJProgressBar, "hidemode 2");

        // The Description_Panel
        descriptionJTextField.setFont(Font.decode(Settings.getJpoResources().getString("PictureViewerDescriptionFont")));
        descriptionJTextField.setWrapStyleWord(true);
        descriptionJTextField.setLineWrap(true);
        descriptionJTextField.setEditable(true);
        descriptionJTextField.setForeground(Settings.getPictureviewerTextColor());
        descriptionJTextField.setBackground(Settings.getPictureviewerBackgroundColor());
        descriptionJTextField.setCaretColor(Settings.getPictureviewerTextColor());
        descriptionJTextField.setOpaque(true);
        descriptionJTextField.setBorder(new EmptyBorder(2, 12, 0, 0));
        descriptionJTextField.setMinimumSize(new Dimension(80, 26));

        final var descriptionJScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        descriptionJScrollPane.setViewportView(descriptionJTextField);
        descriptionJScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        descriptionJScrollPane.setBackground(Settings.getPictureviewerBackgroundColor());
        descriptionJScrollPane.setOpaque(true);
        lowerBar.add(descriptionJScrollPane);

        lowerBar.add(navButtonPanel);
        return lowerBar;
    }

    /**
     * Returns the description that the user could have modified
     *
     * @return the description
     */
    public String getDescription() {
        return descriptionJTextField.getText();
    }

    /**
     * Sets the description to the supplied string.
     *
     * @param newDescription The new description to be shown
     */
    public void setDescription(String newDescription) {
        Tools.checkEDT();
        descriptionJTextField.setText(newDescription);
    }

    /**
     * The location and size of the Window can be changed by a call to this
     * method
     *
     * @param newMode new mode
     */
    public void switchWindowMode(final ResizableJFrame.WindowSize newMode) {
        myJFrame.switchWindowMode(newMode);
    }

    /**
     * Gets rid of the frame.
     */
    public void getRid() {
        Settings.setLastViewerCoordinates(myJFrame.getBounds());
        myJFrame.dispose();
    }

    /**
     * Returns the resizeable frame object
     *
     * @return the frame object
     */
    public ResizableJFrame getResizableJFrame() {
        return myJFrame;
    }

    /**
     * Passes the parameter to the setVisible method of the ProgressBar
     *
     * @param visible true if visible, false if not.
     */
    public void setProgressBarVisible(final boolean visible) {
        loadJProgressBar.setVisible(visible);
    }

    /**
     * Passes the parameter to the setValue method of the ProgressBar
     *
     * @param value the progress bar value
     */
    public void setProgressBarValue(final int value) {
        loadJProgressBar.setValue(value);
    }

    /**
     * moves the info display to the next panel and requests focus into the window
     */
    public void cycleInfoDisplay() {
        pictureController.cycleInfoDisplay();
        pictureController.requestFocusInWindow();
    }

}
