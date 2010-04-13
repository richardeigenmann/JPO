package jpo.gui;

import jpo.dataModel.Settings;
import jpo.*;
import jpo.dataModel.PictureInfo;
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
import jpo.dataModel.Tools;

/*
PictureViewerNavBar.java:  Does the navigation icons and sends the events back to the PictureViewer

Copyright (C) 2002-2009  Richard Eigenmann, Zürich, Switzerland
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
public class PictureViewerNavBar
        extends JToolBar {

    /**
     * A handle back to the PictureViewer so that the buttons can request actions
     * Should be an Interface
     */
    private final PictureViewer pv;


    /** Constructor for a new instance of PictureViewerNavBar
     * @param pv
     */
    public PictureViewerNavBar( final PictureViewer pv ) {
        super( Settings.jpoResources.getString( "NavigationPanel" ) );
        Tools.checkEDT();
        this.pv = pv;
        final int numButtons = 8;

        setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        setFloatable( true );
        setMinimumSize( new Dimension( 36 * numButtons, 26 ) );
        setPreferredSize( new Dimension( 36 * numButtons, 26 ) );
        setMaximumSize( new Dimension( 36 * numButtons, 50 ) );
        setRollover( true );
        setBorderPainted( false );

        add( previousJButton );
        add( nextJButton );


        /**
         *  Button to rotate left
         */
        JButton rotateLeftJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_RotCCDown.gif" ) ) ) {

            {
                setMnemonic( KeyEvent.VK_L );
                addActionListener( new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
                        actionRotateLeft();
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "rotateLeftJButton.ToolTipText" ) );
            }
        };
        add( rotateLeftJButton );

        /**
         *  Button to rotate right
         */
        JButton rotateRightJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_RotCWDown.gif" ) ) ) {

            {
                setMnemonic( KeyEvent.VK_R );
                addActionListener( new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
                        actionRotateRight();
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "rotateRightJButton.ToolTipText" ) );
            }
        };
        add( rotateRightJButton );
        add( fullScreenJButton );
        JButton popupMenuJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_FingerUp.gif" ) ) ) {

            {
                setMnemonic( KeyEvent.VK_M );
                addActionListener( new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
                        actionPopupClicked();
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "popupMenuJButton.ToolTipText" ) );
            }
        };

        add( popupMenuJButton );
        /**
         *  Button to turn on the blending in of info or turn it off.
         */
        JButton infoJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_info.gif" ) ) ) {

            {
                setMnemonic( KeyEvent.VK_I );
                addActionListener( new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
                        actionInfoClicked();
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "infoJButton.ToolTipText" ) );
            }
        };
        add( infoJButton );
        /**
         *  Button to resize the image so that it fits in the screen.
         */
        JButton resetJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_reset.gif" ) ) ) {

            {
                setMnemonic( KeyEvent.VK_ESCAPE );
                addActionListener( new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
                        actionResetClicked();
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "resetJButton.ToolTipText" ) );
            }
        };
        add( resetJButton );
        add( clockJButton );
        /**
         *  button to close the window
         */
        JButton closeJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_close2.gif" ) ) ) {

            {
                setMnemonic( KeyEvent.VK_C );
                addActionListener( new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
                        actionCloseClicked();
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "closeJButton.ToolTipText" ) );
            }
        };
        add( closeJButton );
    }

    /**
     * Defines a JButton with no border, standard background color, standard dimensions and tooltip at 0, -20
     */
    private class NavBarButton
            extends JButton {

        /**
         * Constructor
         */
        NavBarButton( Icon icon ) {
            super( icon );
            setBorderPainted( false );
            setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
            final Dimension navButtonSize = new Dimension( 24, 24 );
            setPreferredSize( navButtonSize );
        }


        /**
         *  overriding the position of the tooltip
         */
        @Override
        public Point getToolTipLocation( MouseEvent event ) {
            return new Point( 0, -20 );
        }
    }

    /**
     *   icon for the simple next picture
     */
    private static final ImageIcon nextImageIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_next.gif" ) );

    /**
     *   icon to indicate the next picture is from a new group
     */
    private static final ImageIcon iconNextNext = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_nextnext.gif" ) );

    /**
     *   icon to indicate the next picture is from a new group
     */
    private static final ImageIcon iconNoNext = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_nonext.gif" ) );

    /**
     *   icon to indicate that there is a previous image in the same group
     */
    private static final ImageIcon iconPrevious = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_previous.gif" ) );

    /**
     *   icon to indicate that there is an image in the previous group
     */
    private static final ImageIcon iconPrevPrev = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_prevprev.gif" ) );

    /**
     *   icon to indicate that there are no images before the current one in the album
     */
    private static final ImageIcon iconNoPrev = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_noprev.gif" ) );

    /**
     *  Button that is put in the NavigationPanel to allow the user to navigate to the previous
     *  picture. Depending on the context (previous pictures in the group, picture
     *  in previous group, beginning of pictures) the icon {@link #iconPrevious}, {@link #iconPrevPrev}
     *  {@link #iconNoPrev} should be shown as appropriate.
     */
    private JButton previousJButton = new NavBarButton( iconPrevious ) {

        {
            setMnemonic( KeyEvent.VK_P );
            addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    actionGoLeft();
                }
            } );
            setToolTipText( Settings.jpoResources.getString( "previousJButton.ToolTipText" ) );
        }
    };

    /**
     *   Button to move to the next image.
     */
    private JButton nextJButton = new NavBarButton( nextImageIcon ) {

        {
            setMnemonic( KeyEvent.VK_N );
            addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    actionGoRight();
                }
            } );
            setToolTipText( Settings.jpoResources.getString( "nextJButton.ToolTipText" ) );
        }
    };


    /**
     *  This method looks at the position the currentNode is in regard to it's siblings and
     *  changes the forward and back icons to reflect the position of the current node.
     * TODO: This code is not browser aware. It needs to be.
     */
    public void setIconDecorations() {
        // Set the next and back icons
        if ( pv.getCurrentNode() != null ) {
            DefaultMutableTreeNode NextNode = pv.getCurrentNode().getNextSibling();
            if ( NextNode != null ) {
                Object nodeInfo = NextNode.getUserObject();
                if ( nodeInfo instanceof PictureInfo ) {
                    // because there is a next sibling object of type
                    // PictureInfo we should set the next icon to the
                    // icon that indicates a next picture in the group
                    nextJButton.setIcon( nextImageIcon );
                } else {
                    // it must be a GroupInfo node
                    // since we must descend into it it gets a nextnext icon.
                    nextJButton.setIcon( iconNextNext );
                }
            } else {
                // the getNextSibling() method returned null
                // if the getNextNode also returns null this was the end of the album
                // otherwise there are more pictures in the next group.
                if ( pv.getCurrentNode().getNextNode() != null ) {
                    nextJButton.setIcon( iconNextNext );
                } else {
                    nextJButton.setIcon( iconNoNext );
                }
            }

            // let's see what we have in the way of previous siblings..

            if ( pv.getCurrentNode().getPreviousSibling() != null ) {
                previousJButton.setIcon( iconPrevious );
            } else {
                // deterine if there are any previous nodes that are not groups.
                DefaultMutableTreeNode testNode;
                testNode = pv.getCurrentNode().getPreviousNode();
                while ( ( testNode != null ) && ( !( testNode.getUserObject() instanceof PictureInfo ) ) ) {
                    testNode = testNode.getPreviousNode();
                }
                if ( testNode == null ) {
                    previousJButton.setIcon( iconNoPrev );
                } else {
                    previousJButton.setIcon( iconPrevPrev );
                }
            }
        }
    }

    /**
     *  Button to expand the window to full screen or a different window size.
     */
    public JButton fullScreenJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_Frames.gif" ) ) ) {

        {
            setMnemonic( KeyEvent.VK_F );
            addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    actionFullScreenClicked();
                }
            } );
            setToolTipText( Settings.jpoResources.getString( "fullScreenJButton.ToolTipText" ) );
        }
    };

    /**
     *   icon to indicate that the timer is available
     */
    public static final ImageIcon iconClockOff = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_clock_off.gif" ) );

    /**
     *   icon to indicate that the timer is active
     */
    public static final ImageIcon iconClockOn = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_clock_on.gif" ) );

    /**
     *   Button to start the auto timer or turn it off.
     */
    public JButton clockJButton = new NavBarButton( iconClockOff ) {

        {
            addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    actionClockClicked();
                }
            } );
            setToolTipText( Settings.jpoResources.getString( "clockJButton.ToolTipText" ) );
        }
    };


    /**
     * hat to do when requested to go left
     */
    private void actionGoLeft() {
        pv.requestPriorPicture();

    }


    /**
     * hat to do when requested to go left
     */
    private void actionGoRight() {
        pv.requestNextPicture();
    }


    /**
     * What to do when the rotate left button is pressed
     */
    private void actionRotateLeft() {
        pv.rotate( 270 );
    }


    /**
     * What to do when the rotate right button is pressed
     */
    private void actionRotateRight() {
        pv.rotate( 90 );
    }


    /**
     * What to do when the popup button is clicked.
     */
    private void actionPopupClicked() {
        pv.requestPopupMenu();
    }


    /**
     * What to do when the info button is clicked
     */
    private void actionInfoClicked() {
        pv.cylceInfoDisplay();
    }


    /**
     * What to do when the reset button is clicked
     */
    private void actionResetClicked() {
        pv.resetPicture();
    }


    /**
     * What to do when the close button is clicked
     */
    private void actionCloseClicked() {
        pv.closeViewer();

    }


    /**
     * What to do when the Full Screen button is clicked
     */
    private void actionFullScreenClicked() {
        pv.requestScreenSizeMenu();

    }


    /**
     * What to do when the clock button is clicked
     */
    private void actionClockClicked() {
        pv.requestAutoAdvance();
    }
}
