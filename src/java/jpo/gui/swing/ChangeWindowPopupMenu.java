package jpo.gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import jpo.dataModel.Settings;
import jpo.gui.ChangeWindowInterface;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_BOTTOM_LEFT;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_BOTTOM_RIGHT;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_DEFAULT;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_FULLSCREEN;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_LEFT;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_RIGHT;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_TOP_LEFT;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_TOP_RIGHT;

/*
 GroupPopupMenu.java: popup menu for groups
 Copyright (C) 2002-2011  Richard Eigenmann.
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
 * A class that generates a popup menu for a group node. This became necessary
 * primarily because the code was getting a bit long and was cluttering up a
 * different class. Separating out the popup menu and making it an object and
 * forcing an interface on the object instantiating it is probably more in line
 * with the OO philosophy.
 *
 */
public class ChangeWindowPopupMenu extends JPopupMenu
        implements ActionListener {

    /**
     * Menu item that indicates that a Fullscreen window should be created.
     *
     */
    private final JMenuItem fullScreenJMenuItem = new JMenuItem( Settings.jpoResources.getString( "fullScreenLabel" ) );
    /**
     * Menu item that indicates that the window should be created on the LEFT
     * half of the display.
     *
     */
    private final JMenuItem leftWindowJMenuItem = new JMenuItem( Settings.jpoResources.getString( "leftWindowLabel" ) );
    /**
     * Menu item that indicates that the window should be created on the RIGHT
     * half of the display.
     *
     */
    private final JMenuItem rightWindowJMenuItem = new JMenuItem( Settings.jpoResources.getString( "rightWindowLabel" ) );
    /**
     * Menu item that indicates that the window should be created on the TOP
     * LEFT quarter of the display.
     *
     */
    private final JMenuItem topLeftWindowJMenuItem = new JMenuItem( Settings.jpoResources.getString( "topLeftWindowLabel" ) );
    /**
     * Menu item that indicates that the window should be created on the TOP
     * RIGHT quarter of the display.
     *
     */
    private final JMenuItem topRightWindowJMenuItem = new JMenuItem( Settings.jpoResources.getString( "topRightWindowLabel" ) );
    /**
     * Menu item that indicates that the window should be created on the BOTTOM
     * LEFT quarter of the display.
     *
     */
    private final JMenuItem bottomLeftWindowJMenuItem = new JMenuItem( Settings.jpoResources.getString( "bottomLeftWindowLabel" ) );
    /**
     * Menu item that indicates that the window should be created on the BOTTOM
     * RIGHT quarter of the display.
     *
     */
    private final JMenuItem bottomRightWindowJMenuItem = new JMenuItem( Settings.jpoResources.getString( "bottomRightWindowLabel" ) );
    /**
     * Menu item that indicates that the window should be created on the BOTTOM
     * RIGHT quarter of the display.
     *
     */
    private final JMenuItem defaultWindowJMenuItem = new JMenuItem( Settings.jpoResources.getString( "defaultWindowLabel" ) );
    /**
     * Menu item that indicates that the window decorations should be shown.
     *
     */
    private final JMenuItem windowDecorationsJMenuItem = new JMenuItem( Settings.jpoResources.getString( "windowDecorationsLabel" ) );
    /**
     * Menu item that indicates that the window decorations should not be shown.
     *
     */
    private final JMenuItem windowNoDecorationsJMenuItem = new JMenuItem( Settings.jpoResources.getString( "windowNoDecorationsLabel" ) );
    /**
     * Object that must implement the functions dealing with the user request.
     */
    private final ChangeWindowInterface caller;

    /**
     * Creates a popup menu which allows the user to choose how he would like
     * his window to be positioned and whether it should have decorations.
     *
     * @param caller	The object requesting the menu.
     */
    public ChangeWindowPopupMenu( ChangeWindowInterface caller ) {
        this.caller = caller;
        initComponents();
    }

    private void initComponents() {
        fullScreenJMenuItem.addActionListener( this );
        add( fullScreenJMenuItem );

        leftWindowJMenuItem.addActionListener( this );
        add( leftWindowJMenuItem );

        rightWindowJMenuItem.addActionListener( this );
        add( rightWindowJMenuItem );

        topLeftWindowJMenuItem.addActionListener( this );
        add( topLeftWindowJMenuItem );

        topRightWindowJMenuItem.addActionListener( this );
        add( topRightWindowJMenuItem );

        bottomLeftWindowJMenuItem.addActionListener( this );
        add( bottomLeftWindowJMenuItem );

        bottomRightWindowJMenuItem.addActionListener( this );
        add( bottomRightWindowJMenuItem );

        defaultWindowJMenuItem.addActionListener( this );
        add( defaultWindowJMenuItem );

        addSeparator();

        windowDecorationsJMenuItem.addActionListener( this );
        add( windowDecorationsJMenuItem );

        windowNoDecorationsJMenuItem.addActionListener( this );
        add( windowNoDecorationsJMenuItem );
    }

    /**
     * Method that analyses the user initiated action and performs what the user
     * requested.
     *
     * @param actionEvent The Action Event relieved.
     *
     */
    @Override
    public void actionPerformed( ActionEvent actionEvent ) {
        // Group popup menu				

        if ( actionEvent.getSource() == fullScreenJMenuItem ) {
            caller.switchWindowMode( WINDOW_FULLSCREEN );
        } else if ( actionEvent.getSource() == leftWindowJMenuItem ) {
            caller.switchWindowMode( WINDOW_LEFT );
        } else if ( actionEvent.getSource() == rightWindowJMenuItem ) {
            caller.switchWindowMode( WINDOW_RIGHT );
        } else if ( actionEvent.getSource() == topLeftWindowJMenuItem ) {
            caller.switchWindowMode( WINDOW_TOP_LEFT );
        } else if ( actionEvent.getSource() == topRightWindowJMenuItem ) {
            caller.switchWindowMode( WINDOW_TOP_RIGHT );
        } else if ( actionEvent.getSource() == bottomLeftWindowJMenuItem ) {
            caller.switchWindowMode( WINDOW_BOTTOM_LEFT );
        } else if ( actionEvent.getSource() == bottomRightWindowJMenuItem ) {
            caller.switchWindowMode( WINDOW_BOTTOM_RIGHT );
        } else if ( actionEvent.getSource() == defaultWindowJMenuItem ) {
            caller.switchWindowMode( WINDOW_DEFAULT );
        } else if ( actionEvent.getSource() == windowDecorationsJMenuItem ) {
            caller.showWindowDecorations( true );
        } else if ( actionEvent.getSource() == windowNoDecorationsJMenuItem ) {
            caller.showWindowDecorations( false );
        } else {
            JOptionPane.showMessageDialog( null,
                    "Unknown event",
                    "Error",
                    JOptionPane.ERROR_MESSAGE );
        }

    }
}
