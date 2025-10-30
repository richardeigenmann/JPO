package org.jpo.gui.swing;

import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;
import org.jpo.gui.JpoResources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Objects;

/*
 Copyright (C) 2002-2025 Richard Eigenmann, Zürich, Switzerland
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
 * Creates a navigation Bar with several icons to navigate the Picture Viewer
 *
 * @author Richard Eigenmann richard.eigenmann@gmail.com
 */
public class PictureViewerNavBar extends JToolBar {

    /**
     * Button that is put in the NavigationPanel to allow the user to navigate
     * to the previous picture. Depending on the context (previous pictures in
     * the group, picture in previous group, beginning of pictures) different
     * icons are shown.
     *
     */
    public final LeftRightButton previousJButton = new LeftRightButton();

    /**
     * Button to move to the next image.
     */
    private final LeftRightButton nextJButton = new LeftRightButton();

    /**
     * Returns a next button
     *
     * @return the next button
     */
    public JButton getNextJButton() {
        return nextJButton;
    }

    /**
     * A button for the rotation to the left
     */
    public final JButton rotateLeftJButton = new NavBarButton(new ImageIcon(Objects.requireNonNull(PictureViewerNavBar.class.getClassLoader().getResource("icon_RotCCDown.gif"))));
    /**
     * A button for the rotation to the right
     */
    public final JButton rotateRightJButton = new NavBarButton(new ImageIcon(Objects.requireNonNull(PictureViewerNavBar.class.getClassLoader().getResource("icon_RotCWDown.gif"))));

    /**
     * A button to zoom in with
     */
    public final JButton zoomInJButton = new NavBarButton( new ImageIcon(Objects.requireNonNull(PictureViewerNavBar.class.getClassLoader().getResource("MagnifyPlus.gif"))) );

    /**
     * A button to zoom out with
     */
    public final JButton zoomOutJButton = new NavBarButton( new ImageIcon(Objects.requireNonNull(PictureViewerNavBar.class.getClassLoader().getResource("MagnifyMinus.gif"))) );

    /**
     * A button to bring up the screen sizes button
     */
    public final JButton fullScreenJButton = new NavBarButton( new ImageIcon(Objects.requireNonNull(PictureViewerNavBar.class.getClassLoader().getResource("icon_Frames.gif"))) );
    /**
     * A button to bring up the popup menu
     */
    public final JButton popupMenuJButton = new NavBarButton( new ImageIcon(Objects.requireNonNull(PictureViewerNavBar.class.getClassLoader().getResource("icon_FingerUp.gif"))) );
    /**
     * A button to show the info overlay
     */
    public final JButton infoJButton = new NavBarButton( new ImageIcon(Objects.requireNonNull(PictureViewerNavBar.class.getClassLoader().getResource("icon_info.gif"))) );
    /**
     * A button to close the panel
     */
    public final JButton closeJButton = new NavBarButton( new ImageIcon(Objects.requireNonNull(PictureViewerNavBar.class.getClassLoader().getResource("icon_close2.gif"))) );

    /**
     * Button for the automatic advance timer.
     */
    public final ClockButton clockJButton = new ClockButton( );

    /**
     * Button to resize the image so that it fits in the screen.
     */
    public final JButton resetJButton = new NavBarButton( new ImageIcon(Objects.requireNonNull(PictureViewerNavBar.class.getClassLoader().getResource("icon_reset.gif"))) );

    /**
     * Constructor for a new instance of PictureViewerNavBar
     */
    public PictureViewerNavBar() {
        super(JpoResources.getResource("NavigationPanel"));
        Tools.checkEDT();

        setBackground(Settings.getPictureviewerBackgroundColor());
        setFloatable(true);
        setRollover( true );
        setBorderPainted( false );

        previousJButton.setMnemonic(KeyEvent.VK_P);
        previousJButton.setToolTipText(JpoResources.getResource("previousJButton.ToolTipText"));
        add(previousJButton);

        nextJButton.setMnemonic(KeyEvent.VK_N);
        nextJButton.setToolTipText(JpoResources.getResource("nextJButton.ToolTipText"));
        add(nextJButton);

        rotateLeftJButton.setMnemonic(KeyEvent.VK_L);
        rotateLeftJButton.setToolTipText(JpoResources.getResource("rotateLeftJButton.ToolTipText"));
        add(rotateLeftJButton);

        rotateRightJButton.setMnemonic(KeyEvent.VK_R);
        rotateRightJButton.setToolTipText(JpoResources.getResource("rotateRightJButton.ToolTipText"));
        add(rotateRightJButton);

        zoomInJButton.setToolTipText(JpoResources.getResource("zoomInJButton.ToolTipText"));
        add(zoomInJButton);

        zoomOutJButton.setToolTipText(JpoResources.getResource("zoomOutJButton.ToolTipText"));
        add(zoomOutJButton);

        fullScreenJButton.setMnemonic(KeyEvent.VK_F);
        fullScreenJButton.setToolTipText(JpoResources.getResource("fullScreenJButton.ToolTipText"));
        add(fullScreenJButton);

        popupMenuJButton.setMnemonic(KeyEvent.VK_M);
        popupMenuJButton.setToolTipText(JpoResources.getResource("popupMenuJButton.ToolTipText"));
        add(popupMenuJButton);

        infoJButton.setMnemonic(KeyEvent.VK_I);
        infoJButton.setToolTipText(JpoResources.getResource("infoJButton.ToolTipText"));
        add(infoJButton);

        resetJButton.setMnemonic(KeyEvent.VK_ESCAPE);
        resetJButton.setToolTipText(JpoResources.getResource("resetJButton.ToolTipText"));
        add(resetJButton);

        clockJButton.setToolTipText(JpoResources.getResource("clockJButton.ToolTipText"));
        add(clockJButton);

        speedSlider.setVisible( false );
        speedSlider.setMinimumSize( new Dimension( 60, 24 ) );
        speedSlider.setPreferredSize( new Dimension( 60, 24 ) );
        speedSlider.setMaximumSize( new Dimension( 100, 24 ) );
        add( speedSlider );

        closeJButton.setMnemonic(KeyEvent.VK_C);
        closeJButton.setToolTipText(JpoResources.getResource("closeJButton.ToolTipText"));
        add(closeJButton);
    }
    /**
     * The delay timer that is shown only when auto advance is on.
     */
    public final JSlider speedSlider = new JSlider( 1, 60, 4 );

    /**
     * Turns on the showing of the delay slider
     */
    public void showDelaySlider() {
        speedSlider.setVisible(true);
    }

    /**
     * Turns off the showing of the delay slider
     */
    public void hideDelaySlider() {
        speedSlider.setVisible(false);
    }

    /**
     * Sets the next button to have the HAS_RIGHT style
     */
    public void setNextButtonHasRight() {
        nextJButton.setDecoration(LeftRightButton.BUTTON_STATE.HAS_RIGHT);
    }

    /**
     * Sets the next button to have the HAS_NEXT style
     */
    public void setNextButtonHasNext() {
        nextJButton.setDecoration(LeftRightButton.BUTTON_STATE.HAS_NEXT);
    }

    /**
     * Sets the next button to have the END style
     */
    public void setNextButtonEnd() {
        nextJButton.setDecoration(LeftRightButton.BUTTON_STATE.END);
    }

    /**
     * Sets the left button to HAS_LEFT style
     */
    public void setPreviousButtonHasLeft() {
        previousJButton.setDecoration(LeftRightButton.BUTTON_STATE.HAS_LEFT);
    }

    /**
     * Sets the left button to the HAS_PREVIOUS style
     */
    public void setPreviousButtonHasPrevious() {
        previousJButton.setDecoration(LeftRightButton.BUTTON_STATE.HAS_PREVIOUS);
    }

    /**
     * Sets the left button to the BEGINNING style
     */
    public void setPreviousButtonBeginning() {
        previousJButton.setDecoration(LeftRightButton.BUTTON_STATE.BEGINNING);
    }

}
