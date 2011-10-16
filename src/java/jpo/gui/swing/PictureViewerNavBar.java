package jpo.gui.swing;

import javax.swing.event.ChangeEvent;
import jpo.dataModel.Settings;
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
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import jpo.dataModel.Tools;
import jpo.gui.PictureViewerActions;

/*
PictureViewerNavBar.java:  Does the navigation icons and sends the events back to the PictureViewer

Copyright (C) 2002-2011  Richard Eigenmann, Zürich, Switzerland
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
     * A handle back to the Controller so the buttons can request actions
     */
    private PictureViewerActions pictureViewerController = null;

    /** Constructor for a new instance of PictureViewerNavBar
     */
    public PictureViewerNavBar() {
        super( Settings.jpoResources.getString( "NavigationPanel" ) );
        Tools.checkEDT();
        
        setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        setFloatable( true );
        setRollover( true );
        setBorderPainted( false );
        
        add( previousJButton );
        add( nextJButton );
        
        final JButton rotateLeftJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_RotCCDown.gif" ) ) ) {
            
            {
                setMnemonic( KeyEvent.VK_L );
                addActionListener( new ActionListener() {
                    
                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        pictureViewerController.rotate( 270 );
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "rotateLeftJButton.ToolTipText" ) );
            }
        };
        add( rotateLeftJButton );
        
        final JButton rotateRightJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_RotCWDown.gif" ) ) ) {
            
            {
                setMnemonic( KeyEvent.VK_R );
                addActionListener( new ActionListener() {
                    
                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        pictureViewerController.rotate( 90 );
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "rotateRightJButton.ToolTipText" ) );
            }
        };
        add( rotateRightJButton );
        
        final JButton zoomInJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/MagnifyPlus.gif" ) ) ) {
            
            {
                addActionListener( new ActionListener() {
                    
                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        pictureViewerController.zoomIn();
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "zoomInJButton.ToolTipText" ) );
            }
        };
        add( zoomInJButton );
        
        final JButton zoomOutJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/MagnifyMinus.gif" ) ) ) {
            
            {
                addActionListener( new ActionListener() {
                    
                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        pictureViewerController.zoomOut();
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "zoomOutJButton.ToolTipText" ) );
            }
        };
        add( zoomOutJButton );
        
        
        final JButton fullScreenJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_Frames.gif" ) ) ) {
            
            {
                setMnemonic( KeyEvent.VK_F );
                addActionListener( new ActionListener() {
                    
                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        pictureViewerController.requestScreenSizeMenu();
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "fullScreenJButton.ToolTipText" ) );
            }
        };
        
        
        add( fullScreenJButton );
        final JButton popupMenuJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_FingerUp.gif" ) ) ) {
            
            {
                setMnemonic( KeyEvent.VK_M );
                addActionListener( new ActionListener() {
                    
                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        pictureViewerController.requestPopupMenu();
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "popupMenuJButton.ToolTipText" ) );
            }
        };
        
        add( popupMenuJButton );
        
        final JButton infoJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_info.gif" ) ) ) {
            
            {
                setMnemonic( KeyEvent.VK_I );
                addActionListener( new ActionListener() {
                    
                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        pictureViewerController.cylceInfoDisplay();
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "infoJButton.ToolTipText" ) );
            }
        };
        add( infoJButton );

        /**
         *  Button to resize the image so that it fits in the screen.
         */
        final JButton resetJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_reset.gif" ) ) ) {
            
            {
                setMnemonic( KeyEvent.VK_ESCAPE );
                addActionListener( new ActionListener() {
                    
                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        pictureViewerController.resetPicture();
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "resetJButton.ToolTipText" ) );
            }
        };
        add( resetJButton );
        
        add( clockJButton );
        
        speedSlider.setVisible( false );
        speedSlider.setMinimumSize( new Dimension( 60, 24 ) );
        speedSlider.setPreferredSize( new Dimension( 60, 24 ) );
        speedSlider.setMaximumSize( new Dimension( 100, 24 ) );
        speedSlider.addChangeListener( new ChangeListener() {
            
            @Override
            public void stateChanged( ChangeEvent ce ) {
                pictureViewerController.setTimerDelay( speedSlider.getValue() );
            }
        } );
        add( speedSlider );
        
        final JButton closeJButton = new NavBarButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_close2.gif" ) ) ) {
            
            {
                setMnemonic( KeyEvent.VK_C );
                addActionListener( new ActionListener() {
                    
                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        pictureViewerController.closeViewer();
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "closeJButton.ToolTipText" ) );
            }
        };
        add( closeJButton );
    }
    /**
     * The delay timer that is shown only when auto advance is on.
     */
    private final JSlider speedSlider = new JSlider( 1, 60, 4 );
    
    public void showDelaySilder() {
        speedSlider.setVisible( true );
    }
    
    public void hideDelaySilder() {
        speedSlider.setVisible( false );
    }

    /**
     * Extends the default JButton with no border, standard background color, standard 
     * dimensions of 24 pixels and tooltip at 0, -20 Uses the Settings.PICTUREVIEWER_BACKGROUND_COLOR
     * for the background.
     */
    private class NavBarButton
            extends JButton {
        
        final Dimension navButtonSize = new Dimension( 24, 24 );

        /**
         * Constructs the NavBarButton
         */
        NavBarButton( final Icon icon ) {
            super( icon );
            setBorderPainted( false );
            setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
            setMinimumSize( navButtonSize );
            setPreferredSize( navButtonSize );
            setMaximumSize( navButtonSize );
        }

        /**
         * Overriding the position of the tooltip so that it comes 
         * 20 pixels above the mouse pointer
         */
        @Override
        public Point getToolTipLocation( MouseEvent event ) {
            return new Point( 0, -20 );
        }
    }
    /**
     * Icon pointing right
     */
    private static final ImageIcon ICON_ARROW_RIGHT = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_next.gif" ) );
    /**
     * Double right pointing icon
     */
    private static final ImageIcon ICON_DOUBLE_ARROW_RIGHT = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_nextnext.gif" ) );
    /**
     * Icon pointing right at a bar to indicate you can't go right
     */
    private static final ImageIcon ICON_ARROW_RIGHT_STOP = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_nonext.gif" ) );
    /**
     * Icon pointing left
     */
    private static final ImageIcon ICON_ARROW_LEFT = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_previous.gif" ) );
    /**
     * Double left pointing icon
     */
    private static final ImageIcon ICON_DOUBLE_ARROW_LEFT = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_prevprev.gif" ) );
    /**
     * Icon pointing left with a bar to indicate you can't go left
     */
    private static final ImageIcon ICON_ARROW_LEFT_STOP = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_noprev.gif" ) );
    /**
     *  Button that is put in the NavigationPanel to allow the user to navigate to the previous
     *  picture. Depending on the context (previous pictures in the group, picture
     *  in previous group, beginning of pictures) the icon {@link #ICON_ARROW_LEFT}, {@link #ICON_DOUBLE_ARROW_LEFT}
     *  {@link #ICON_ARROW_LEFT_STOP} should be shown as appropriate.
     *  @see #setIconDecorations() 
     */
    private final JButton previousJButton = new NavBarButton( ICON_ARROW_LEFT ) {
        
        {
            setMnemonic( KeyEvent.VK_P );
            addActionListener( new ActionListener() {
                
                @Override
                public void actionPerformed( ActionEvent e ) {
                    pictureViewerController.requestPriorPicture();
                }
            } );
            setToolTipText( Settings.jpoResources.getString( "previousJButton.ToolTipText" ) );
        }
    };
    /**
     *  Button to move to the next image.
     *  @see #setIconDecorations() 
     */
    private final JButton nextJButton = new NavBarButton( ICON_ARROW_RIGHT ) {
        
        {
            setMnemonic( KeyEvent.VK_N );
            addActionListener( new ActionListener() {
                
                @Override
                public void actionPerformed( ActionEvent e ) {
                    pictureViewerController.requestNextPicture();
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
        if ( pictureViewerController.getCurrentNode() != null ) {
            DefaultMutableTreeNode NextNode = pictureViewerController.getCurrentNode().getNextSibling();
            if ( NextNode != null ) {
                Object nodeInfo = NextNode.getUserObject();
                if ( nodeInfo instanceof PictureInfo ) {
                    // because there is a next sibling object of type
                    // PictureInfo we should set the next icon to the
                    // icon that indicates a next picture in the group
                    nextJButton.setIcon( ICON_ARROW_RIGHT );
                } else {
                    // it must be a GroupInfo node
                    // since we must descend into it it gets a nextnext icon.
                    nextJButton.setIcon( ICON_DOUBLE_ARROW_RIGHT );
                }
            } else {
                // the getNextSibling() method returned null
                // if the getNextNode also returns null this was the end of the album
                // otherwise there are more pictures in the next group.
                if ( pictureViewerController.getCurrentNode().getNextNode() != null ) {
                    nextJButton.setIcon( ICON_DOUBLE_ARROW_RIGHT );
                } else {
                    nextJButton.setIcon( ICON_ARROW_RIGHT_STOP );
                }
            }

            // let's see what we have in the way of previous siblings..

            if ( pictureViewerController.getCurrentNode().getPreviousSibling() != null ) {
                previousJButton.setIcon( ICON_ARROW_LEFT );
            } else {
                // deterine if there are any previous nodes that are not groups.
                DefaultMutableTreeNode testNode;
                testNode = pictureViewerController.getCurrentNode().getPreviousNode();
                while ( ( testNode != null ) && ( !( testNode.getUserObject() instanceof PictureInfo ) ) ) {
                    testNode = testNode.getPreviousNode();
                }
                if ( testNode == null ) {
                    previousJButton.setIcon( ICON_ARROW_LEFT_STOP );
                } else {
                    previousJButton.setIcon( ICON_DOUBLE_ARROW_LEFT );
                }
            }
        }
    }
    /**
     * Icon to indicate that the timer is available
     */
    public static final ImageIcon ICON_CLOCK_OFF = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_clock_off.gif" ) );
    /**
     * Icon to indicate that the timer is active
     */
    public static final ImageIcon ICON_CLOCK_ON = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_clock_on.gif" ) );
    /**
     * Button for the automatic advance timer.
     */
    public final JButton clockJButton = new NavBarButton( ICON_CLOCK_OFF ) {
        
        {
            addActionListener( new ActionListener() {
                
                @Override
                public void actionPerformed( ActionEvent e ) {
                    pictureViewerController.requestAutoAdvance();
                }
            } );
            setToolTipText( Settings.jpoResources.getString( "clockJButton.ToolTipText" ) );
        }
    };

    /**
     * Switches the clock to busy mode
     */
    public void setClockBusy() {
        clockJButton.setIcon( PictureViewerNavBar.ICON_CLOCK_ON );
    }

    /**
     * Switches the clock icon to idle mode
     */
    public void setClockIdle() {
        clockJButton.setIcon( PictureViewerNavBar.ICON_CLOCK_OFF );
    }

    /**
     * Sets the pictureViewer so that the buttons has a target to send their requests to
     * @param pictureViewerActions 
     */
    public void setPictureViewer( final PictureViewerActions pictureViewerActions ) {
        this.pictureViewerController = pictureViewerActions;
    }
}
