package jpo;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.tree.DefaultMutableTreeNode;

/*
PictureViewerNavBar.java:  Does the navigation icons and event handling for the PictureViewer
 
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
 *  Does the navigation icons and event handling for the PictureViewer
 */
public class PictureViewerNavBar extends JToolBar {
    
    /**
     * handle back to the PictureViewer.
     * Should probably be an Interface
     */
    final PictureViewer pv;
    
    /** Creates a new instance of PictureViewerNavBar */
    public PictureViewerNavBar( final PictureViewer pv ) {
        super( Settings.jpoResources.getString("NavigationPanel") );
        this.pv = pv;
        final int numButtons = 8;
        final Dimension navButtonSize = new Dimension( 24, 24);
        
        setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        setFloatable( true );
        setMinimumSize(new Dimension(36 * numButtons, 26));
        setPreferredSize(new Dimension(36 * numButtons, 26));
        setMaximumSize(new Dimension(36 * numButtons, 50));
        setRollover( true );
        setBorderPainted( false );
        
        previousJButton.setMnemonic(KeyEvent.VK_P);
        previousJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pv.requestPriorPicture();
                pv.myJFrame.getGlassPane().requestFocusInWindow();
            }
        });
        previousJButton.setToolTipText( Settings.jpoResources.getString("previousJButton.ToolTipText") );
        previousJButton.setBorderPainted(false);
        previousJButton.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        previousJButton.setPreferredSize( navButtonSize );
        add(previousJButton);
        
        
        nextJButton.setMnemonic(KeyEvent.VK_N);
        nextJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pv.requestNextPicture();
                pv.myJFrame.getGlassPane().requestFocusInWindow();
            }
        });
        nextJButton.setToolTipText( Settings.jpoResources.getString("nextJButton.ToolTipText") );
        nextJButton.setBorderPainted(false);
        nextJButton.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        nextJButton.setPreferredSize( navButtonSize );
        add(nextJButton);
        
        
        rotateLeftJButton.setMnemonic(KeyEvent.VK_L);
        rotateLeftJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pv.currentNode.rotatePicture( 270 );
                pv.myJFrame.getGlassPane().requestFocusInWindow();
            }
        });
        rotateLeftJButton.setToolTipText( Settings.jpoResources.getString("rotateLeftJButton.ToolTipText") );
        rotateLeftJButton.setBorderPainted( false );
        rotateLeftJButton.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        rotateLeftJButton.setPreferredSize( navButtonSize );
        add( rotateLeftJButton );
        
        
        
        
        rotateRightJButton.setMnemonic(KeyEvent.VK_R);
        rotateRightJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pv.currentNode.rotatePicture( 90 );
                pv.myJFrame.getGlassPane().requestFocusInWindow();
            }
        });
        rotateRightJButton.setToolTipText( Settings.jpoResources.getString("rotateRightJButton.ToolTipText") );
        rotateRightJButton.setBorderPainted( false );
        rotateRightJButton.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        rotateRightJButton.setPreferredSize( navButtonSize );
        add( rotateRightJButton );
        
        
        fullScreenJButton.setMnemonic( KeyEvent.VK_F );
        fullScreenJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pv.requestScreenSizeMenu();
                pv.myJFrame.getGlassPane().requestFocusInWindow();
            }
        });
        fullScreenJButton.setBorderPainted( false );
        fullScreenJButton.setToolTipText( Settings.jpoResources.getString("fullScreenJButton.ToolTipText") );
        fullScreenJButton.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        fullScreenJButton.setPreferredSize( navButtonSize );
        add( fullScreenJButton );
        
        
        popupMenuJButton.setMnemonic( KeyEvent.VK_M );
        popupMenuJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pv.requestPopupMenu();
                pv.myJFrame.getGlassPane().requestFocusInWindow();
            }
        });
        popupMenuJButton.setBorderPainted( false );
        popupMenuJButton.setToolTipText( Settings.jpoResources.getString("popupMenuJButton.ToolTipText") );
        popupMenuJButton.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        popupMenuJButton.setPreferredSize( navButtonSize );
        popupMenuJButton.setVisible( true );
        add( popupMenuJButton );
        
        infoJButton.setMnemonic(KeyEvent.VK_I);
        infoJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pv.pictureJPanel.cylceInfoDisplay();
                pv.myJFrame.getGlassPane().requestFocusInWindow();
            }
        });
        infoJButton.setBorderPainted(false);
        infoJButton.setToolTipText( Settings.jpoResources.getString("infoJButton.ToolTipText") );
        infoJButton.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        infoJButton.setPreferredSize( navButtonSize );
        add(infoJButton);
        
        resetJButton.setMnemonic(KeyEvent.VK_ESCAPE);
        resetJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pv.resetPicture();
                pv.myJFrame.getGlassPane().requestFocusInWindow();
            }
        });
        resetJButton.setBorderPainted( false );
        resetJButton.setToolTipText( Settings.jpoResources.getString("resetJButton.ToolTipText") );
        resetJButton.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        resetJButton.setPreferredSize( navButtonSize );
        add(resetJButton);
        
        clockJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pv.requestAutoAdvance();
                pv.myJFrame.getGlassPane().requestFocusInWindow();
            }
        });
        clockJButton.setBorderPainted( false );
        clockJButton.setToolTipText( Settings.jpoResources.getString("clockJButton.ToolTipText") );
        clockJButton.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        clockJButton.setPreferredSize( navButtonSize );
        add( clockJButton );
        
        closeJButton.setMnemonic(KeyEvent.VK_C);
        closeJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                pv.closeViewer();
            }
        });
        closeJButton.setToolTipText( Settings.jpoResources.getString("closeJButton.ToolTipText") );
        closeJButton.setBorderPainted( false );
        closeJButton.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        closeJButton.setPreferredSize( navButtonSize );
        add( closeJButton );
        
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
     *   icon to rotate right
     */
    private static final ImageIcon iconRotateRight = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_RotCWDown.gif" ) );
    
    /**
     *  button to rotate right
     */
    private JButton rotateRightJButton = new JButton( iconRotateRight );
    
    /**
     *   icon to rotate left
     */
    private static final ImageIcon iconRotateLeft = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_RotCCDown.gif" ) );
    
    
    /**
     *  button to rotate left
     */
    private JButton rotateLeftJButton = new JButton( iconRotateLeft );
    
    
    /**
     *  Button that is put in the NavigationPanel to allow the user to navigate to the previous
     *  picture. Depending on the context (previous pictures in the group, picture
     *  in previous group, beginning of pictures) the icon {@link #iconPrevious}, {@link #iconPrevPrev}
     *  {@link #iconNoPrev} should be shown as appropriate.
     */
    private JButton previousJButton = new JButton( iconPrevious );
    
    
    /**
     *   icon to indicate that the timer is available
     */
    public static final ImageIcon iconClockOff = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_clock_off.gif" ) );
    
    
    /**
     *   icon to indicate that the timer is active
     */
    public static final ImageIcon iconClockOn = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_clock_on.gif" ) );
    
    /**
     *   Button to move to the next image.
     */
    private JButton nextJButton = new JButton( nextImageIcon );
    
    /**
     *  Button to expand the windo to full screen or a different window size.
     */
    public JButton fullScreenJButton = new JButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_Frames.gif" ) ) );
    
    /**
     *  Button to bring up the popup menu to do things to the image.
     */
    private JButton popupMenuJButton = new JButton(new ImageIcon( Settings.cl.getResource( "jpo/images/icon_FingerUp.gif")));
    
    /**
     *  Button to turn on the blending in of info or turn it off.
     */
    private JButton infoJButton = new JButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_info.gif" ) ) );
    
    /**
     *  Button to rezise the image so that it fits in the screen.
     */
    private JButton resetJButton = new JButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_reset.gif" ) ) );
    
    /**
     *   Button to start the auto timer or turn it off.
     */
    public JButton clockJButton = new JButton( iconClockOff );
    
    
    /**
     *   icon to close the image
     */
    private static final ImageIcon closeIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_close2.gif" ) );
    
    /**
     *  button to close the window
     */
    private JButton closeJButton = new JButton( closeIcon );
    

       
    /**
     *  This method looks at the position the currentNode is in regard to it's siblings and
     *  changes the forward and back icons to reflect the position of the current node.
     */
    public void setIconDecorations() {
        // Set the next and back icons
        if ( pv.currentNode != null ) {
            DefaultMutableTreeNode NextNode = pv.currentNode.getNextSibling();
            if ( NextNode != null ) {
                Object nodeInfo = NextNode.getUserObject();
                if (nodeInfo instanceof PictureInfo) {
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
                if ( pv.currentNode.getNextNode() != null )
                    nextJButton.setIcon( iconNextNext );
                else
                    nextJButton.setIcon( iconNoNext );
            }
            
            // let's see what we have in the way of previous siblings..
            
            if (pv.currentNode.getPreviousSibling() != null)
                previousJButton.setIcon(iconPrevious);
            else {
                // deterine if there are any previous nodes that are not groups.
                DefaultMutableTreeNode testNode;
                testNode = pv.currentNode.getPreviousNode();
                while ((testNode != null) && (! (testNode.getUserObject() instanceof PictureInfo))) {
                    testNode = testNode.getPreviousNode();
                }
                if (testNode == null)
                    previousJButton.setIcon(iconNoPrev);
                else
                    previousJButton.setIcon(iconPrevPrev);
            }
        }
    }
    
    
 
    
    
}
