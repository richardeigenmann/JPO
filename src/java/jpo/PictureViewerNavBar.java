package jpo;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.tree.DefaultMutableTreeNode;

/*
PictureViewerNavBar.java:  Does the navigation icons and sends the events back to the PictureViewer

Copyright (C) 2002-2007  Richard Eigenmann, Zürich, Switzerland
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
 *  Creates a navigation Bar with several icons to navigate the Picture Viewer
 *  @author Richard Eigenmann richard.eigenmann@gmail.com
 */
public class PictureViewerNavBar extends JToolBar {

    /**
     * A handle back to the PictureViewer so that the buttons can request actions
     * Should be an Interface
     */
    private final PictureViewer pv;

    /** Constructor for a new instance of PictureViewerNavBar */
    public PictureViewerNavBar(final PictureViewer pv) {
        super(Settings.jpoResources.getString("NavigationPanel"));
        this.pv = pv;
        final int numButtons = 8;

        setBackground(Settings.PICTUREVIEWER_BACKGROUND_COLOR);
        setFloatable(true);
        setMinimumSize(new Dimension(36 * numButtons, 26));
        setPreferredSize(new Dimension(36 * numButtons, 26));
        setMaximumSize(new Dimension(36 * numButtons, 50));
        setRollover(true);
        setBorderPainted(false);

        add(previousJButton);
        add(nextJButton);
        add(rotateLeftJButton);
        add(rotateRightJButton);
        add(fullScreenJButton);
        JButton popupMenuJButton = new NavBarButton(new ImageIcon(Settings.cl.getResource("jpo/images/icon_FingerUp.gif"))) {

            {
                setMnemonic(KeyEvent.VK_M);
                addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        pv.requestPopupMenu();
                        pv.pictureJPanel.requestFocusInWindow();
                    }
                });
                setToolTipText(Settings.jpoResources.getString("popupMenuJButton.ToolTipText"));
            }
        };

        add(popupMenuJButton);
        add(infoJButton);
        add(resetJButton);
        add(clockJButton);
        add(closeJButton);
    }

    /**
     * Inner class to define common attributes of the NavBarButtons.
     */
    private class NavBarButton extends JButton {

        /**
         * Constructor
         */
        NavBarButton(Icon icon) {
            super(icon);
            setBorderPainted(false);
            setBackground(Settings.PICTUREVIEWER_BACKGROUND_COLOR);
            final Dimension navButtonSize = new Dimension(24, 24);
            setPreferredSize(navButtonSize);
        }

        /**
         *  overriding the position of the tooltip
         */
        @Override
        public Point getToolTipLocation(MouseEvent event) {
            return new Point(0, -20);
        }
    }
    /**
     *   icon for the simple next picture
     */
    private static final ImageIcon nextImageIcon = new ImageIcon(Settings.cl.getResource("jpo/images/icon_next.gif"));
    /**
     *   icon to indicate the next picture is from a new group
     */
    private static final ImageIcon iconNextNext = new ImageIcon(Settings.cl.getResource("jpo/images/icon_nextnext.gif"));
    /**
     *   icon to indicate the next picture is from a new group
     */
    private static final ImageIcon iconNoNext = new ImageIcon(Settings.cl.getResource("jpo/images/icon_nonext.gif"));
    /**
     *   icon to indicate that there is a previous image in the same group
     */
    private static final ImageIcon iconPrevious = new ImageIcon(Settings.cl.getResource("jpo/images/icon_previous.gif"));
    /**
     *   icon to indicate that there is an image in the previous group
     */
    private static final ImageIcon iconPrevPrev = new ImageIcon(Settings.cl.getResource("jpo/images/icon_prevprev.gif"));
    /**
     *   icon to indicate that there are no images before the current one in the album
     */
    private static final ImageIcon iconNoPrev = new ImageIcon(Settings.cl.getResource("jpo/images/icon_noprev.gif"));
    /**
     *  Button that is put in the NavigationPanel to allow the user to navigate to the previous
     *  picture. Depending on the context (previous pictures in the group, picture
     *  in previous group, beginning of pictures) the icon {@link #iconPrevious}, {@link #iconPrevPrev}
     *  {@link #iconNoPrev} should be shown as appropriate.
     */
    private JButton previousJButton = new NavBarButton(iconPrevious) {

        {
            setMnemonic(KeyEvent.VK_P);
            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    pv.requestPriorPicture();
                }
            });
            setToolTipText(Settings.jpoResources.getString("previousJButton.ToolTipText"));
        }
    };
    /**
     *   Button to move to the next image.
     */
    private JButton nextJButton = new NavBarButton(nextImageIcon) {

        {
            setMnemonic(KeyEvent.VK_N);
            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    pv.requestNextPicture();
                }
            });
            setToolTipText(Settings.jpoResources.getString("nextJButton.ToolTipText"));
        }
    };

    /**
     *  This method looks at the position the currentNode is in regard to it's siblings and
     *  changes the forward and back icons to reflect the position of the current node.
     */
    public void setIconDecorations() {
        // Set the next and back icons
        if (pv.getCurrentNode() != null) {
            DefaultMutableTreeNode NextNode = pv.getCurrentNode().getNextSibling();
            if (NextNode != null) {
                Object nodeInfo = NextNode.getUserObject();
                if (nodeInfo instanceof PictureInfo) {
                    // because there is a next sibling object of type
                    // PictureInfo we should set the next icon to the
                    // icon that indicates a next picture in the group
                    nextJButton.setIcon(nextImageIcon);
                } else {
                    // it must be a GroupInfo node
                    // since we must descend into it it gets a nextnext icon.
                    nextJButton.setIcon(iconNextNext);
                }
            } else {
                // the getNextSibling() method returned null
                // if the getNextNode also returns null this was the end of the album
                // otherwise there are more pictures in the next group.
                if (pv.getCurrentNode().getNextNode() != null) {
                    nextJButton.setIcon(iconNextNext);
                } else {
                    nextJButton.setIcon(iconNoNext);
                }
            }

            // let's see what we have in the way of previous siblings..

            if (pv.getCurrentNode().getPreviousSibling() != null) {
                previousJButton.setIcon(iconPrevious);
            } else {
                // deterine if there are any previous nodes that are not groups.
                DefaultMutableTreeNode testNode;
                testNode = pv.getCurrentNode().getPreviousNode();
                while ((testNode != null) && (!(testNode.getUserObject() instanceof PictureInfo))) {
                    testNode = testNode.getPreviousNode();
                }
                if (testNode == null) {
                    previousJButton.setIcon(iconNoPrev);
                } else {
                    previousJButton.setIcon(iconPrevPrev);
                }
            }
        }
    }
    /**
     *  Button to rotate right
     */
    private JButton rotateRightJButton = new NavBarButton(new ImageIcon(Settings.cl.getResource("jpo/images/icon_RotCWDown.gif"))) {

        {
            setMnemonic(KeyEvent.VK_R);
            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    pv.getCurrentNode().rotatePicture(90);
                    pv.pictureJPanel.requestFocusInWindow();
                }
            });
            setToolTipText(Settings.jpoResources.getString("rotateRightJButton.ToolTipText"));
        }
    };
    /**
     *  Button to rotate left
     */
    private JButton rotateLeftJButton = new NavBarButton(new ImageIcon(Settings.cl.getResource("jpo/images/icon_RotCCDown.gif"))) {

        {
            setMnemonic(KeyEvent.VK_L);
            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    pv.getCurrentNode().rotatePicture(270);
                    pv.pictureJPanel.requestFocusInWindow();
                }
            });
            setToolTipText(Settings.jpoResources.getString("rotateLeftJButton.ToolTipText"));
        }
    };
    /**
     *  Button to expand the window to full screen or a different window size.
     */
    public JButton fullScreenJButton = new NavBarButton(new ImageIcon(Settings.cl.getResource("jpo/images/icon_Frames.gif"))) {

        {
            setMnemonic(KeyEvent.VK_F);
            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    pv.requestScreenSizeMenu();
                    pv.pictureJPanel.requestFocusInWindow();
                }
            });
            setToolTipText(Settings.jpoResources.getString("fullScreenJButton.ToolTipText"));
        }
    };
    /**
     *  Button to turn on the blending in of info or turn it off.
     */
    private JButton infoJButton = new NavBarButton(new ImageIcon(Settings.cl.getResource("jpo/images/icon_info.gif"))) {

        {
            setMnemonic(KeyEvent.VK_I);
            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    pv.pictureJPanel.cylceInfoDisplay();
                    pv.pictureJPanel.requestFocusInWindow();
                }
            });
            setToolTipText(Settings.jpoResources.getString("infoJButton.ToolTipText"));
        }
    };
    /**
     *  Button to resize the image so that it fits in the screen.
     */
    private JButton resetJButton = new NavBarButton(new ImageIcon(Settings.cl.getResource("jpo/images/icon_reset.gif"))) {

        {
            setMnemonic(KeyEvent.VK_ESCAPE);
            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    pv.resetPicture();
                    pv.pictureJPanel.requestFocusInWindow();
                }
            });
            setToolTipText(Settings.jpoResources.getString("resetJButton.ToolTipText"));
        }
    };
    /**
     *   icon to indicate that the timer is available
     */
    public static final ImageIcon iconClockOff = new ImageIcon(Settings.cl.getResource("jpo/images/icon_clock_off.gif"));
    /**
     *   icon to indicate that the timer is active
     */
    public static final ImageIcon iconClockOn = new ImageIcon(Settings.cl.getResource("jpo/images/icon_clock_on.gif"));
    /**
     *   Button to start the auto timer or turn it off.
     */
    public JButton clockJButton = new NavBarButton(iconClockOff) {

        {
            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    pv.requestAutoAdvance();
                    pv.pictureJPanel.requestFocusInWindow();
                }
            });
            setToolTipText(Settings.jpoResources.getString("clockJButton.ToolTipText"));
        }
    };
    /**
     *  button to close the window
     */
    private JButton closeJButton = new NavBarButton(new ImageIcon(Settings.cl.getResource("jpo/images/icon_close2.gif"))) {

        {
            setMnemonic(KeyEvent.VK_C);
            addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    pv.closeViewer();
                }
            });
            setToolTipText(Settings.jpoResources.getString("closeJButton.ToolTipText"));
        }
    };
}
