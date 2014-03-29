package jpo.gui.swing;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import jpo.dataModel.Settings;

/*
 ThreeDotButton.java:  This class ovewrrides a JButton and sets the size and text.

 Copyright (C) 2010  Richard Eigenmann, Zurich, Switzerland
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
 * This class overrides a JButton and sets the size and text.
 *
 * @author Richard Eigenmann
 */
public class ThreeDotButton
        extends JButton {

    /**
     * Overriden JButton constructor
     */
    public ThreeDotButton() {
        super();
        setThreeDotAppearance();
    }

    /**
     * Overriden JButton constructor
     *
     * @param icon
     */
    public ThreeDotButton( Icon icon ) {
        super( icon );
        setThreeDotAppearance();
    }

    /**
     * Overriden JButton constructor
     *
     * @param text
     */
    public ThreeDotButton( String text ) {
        super( text );
        setThreeDotAppearance();
    }

    /**
     * Overriden JButton constructor
     *
     * @param a
     */
    public ThreeDotButton( Action a ) {
        super( a );
        setThreeDotAppearance();
    }

    /**
     * Overriden JButton constructor
     *
     * @param text
     * @param icon
     */
    public ThreeDotButton( String text,
            Icon icon ) {
        super( text, icon );
        setThreeDotAppearance();
    }

    /**
     * Sets the three dot text and the preferred size
     */
    private void setThreeDotAppearance() {
        setText( Settings.jpoResources.getString( "threeDotText" ) );
        setPreferredSize( Settings.threeDotButtonDimension );
        setMinimumSize( Settings.threeDotButtonDimension );
        setMaximumSize( Settings.threeDotButtonDimension );

    }
}
