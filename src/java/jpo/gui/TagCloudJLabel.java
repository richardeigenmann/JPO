package jpo.gui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/*
TagCloudJLabel.java:  A widget that shows a word in a Tag Cloud

Copyright (C) 2009  Richard Eigenmann.
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
 *  A Widget that shows a word in a Tag Cloud
 *
 * @author Richard Eigenmann
 */
public class TagCloudJLabel extends JLabel {

    /**
     * An Array of increasing size fonts for the label to choose from
     */
    static final Font[] fonts = {
        new Font( "SansSerif", Font.PLAIN, 10 ),
        new Font( "SansSerif", Font.PLAIN, 14 ),
        new Font( "SansSerif", Font.PLAIN, 18 ),
        new Font( "SansSerif", Font.PLAIN, 19 ),
        new Font( "SansSerif", Font.PLAIN, 20 ),
        new Font( "SansSerif", Font.PLAIN, 21 ),
        new Font( "SansSerif", Font.PLAIN, 22 ),
        new Font( "SansSerif", Font.PLAIN, 23 ),
        new Font( "SansSerif", Font.PLAIN, 24 ),
        new Font( "SansSerif", Font.PLAIN, 25 ) };

    /**
     * I have chosen a the GradientColor.SHADES_OF_LIGHT_BLUE colors for
     * this component.
     */
    static final Color[] gradientColor = GradientColor.SHADES_OF_LIGHT_BLUE;


    /**
     * Constructs a Word Label
     * @param word The word to show
     * @param weight The weight between 0 and 1 to show the weight of the word
     */
    public TagCloudJLabel( String word, float weight ) {
        this( word, weight, weight );
    }


    /**
     * Constructs a Word Label
     * @param word The word to show
     * @param sizeWeight The weight between 0 and 1 to determine the size of the font 
     * @param colorWeight The weight between 0 and 1 to determine the color of the font
     */
    public TagCloudJLabel( String word, float sizeWeight, float colorWeight ) {
        super( word );
        // never trust imputs
        if ( sizeWeight > 1f ) {
            sizeWeight = 1;
        }
        if ( sizeWeight < 0f ) {
            sizeWeight = 0;
        }
        if ( colorWeight > 1f ) {
            colorWeight = 1;
        }
        if ( colorWeight < 0f ) {
            colorWeight = 0;
        }


        final int index = (int) ( sizeWeight * ( fonts.length - 1 ) );
        final float finalColorWeight = colorWeight;
        Runnable r = new Runnable() {

            public void run() {
                setFont( fonts[index] );
                setForeground( GradientColor.getColor( gradientColor, finalColorWeight ) );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }
    }
}