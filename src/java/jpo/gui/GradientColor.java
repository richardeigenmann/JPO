package jpo.gui;

import java.awt.Color;

/*
GradientColor.java:  A helper to find the color along a gradient Line.

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
 * A helper to find the color along a gradient Line.
 *
 * @author Richard Eigenmann
 */
/**
 * This class can be used as a regular class or it can be used for it's
 * static getColor method.
 * @author richi
 */
public class GradientColor {

    /**
     * Sample gradient colors for use in the application
     */
    public final static Color[] SAMPLE_GRADIENT_COLORS = { new Color( 0x099716 ), new Color( 0x18c928 ),
        new Color( 0x36e410 ), new Color( 0x64e410 ), new Color( 0xa1e70c ),
        new Color( 0xc3d000 ), new Color( 0xe8e410 ), new Color( 0xdcaf1e ),
        new Color( 0xe87514 ), new Color( 0xed723b ) };

    /**
     * Predefined color for a Black to White gradient
     */
    public final static Color[] BLACK_WHITE_COLORS = { Color.BLACK, Color.WHITE };

    /**
     * Predefined color gradient points for a blue gradient
     */
    public final static Color[] SHADES_OF_LIGHT_BLUE = {new Color( 0x355ddb), new Color( 0x7aa5f4)};


    /**
     * This method returns a color along a multi point color gradiant. The
     * Gradiant color points are specified by an array of Colors. The Array must
     * have at least 2 color entries. If it has none then a black-white gradient
     * will be used, if it has less than one the gradient is from the supplied color
     * to white.
     * @param availableColors An Array of Colors
     * @param factor between 0 and 1
     * @return The Color in the gradient
     */
    public static Color getColor( Color[] availableColors, double factor ) {
        // Never trust input
        if ( availableColors.length < 1 ) {
            availableColors = BLACK_WHITE_COLORS;
        } else if (availableColors.length == 1 ) {
            Color[] newColors = { availableColors[0], Color.WHITE };
            availableColors = newColors;
        }
        if ( factor > 1 ) {
            factor = 1;
        }
        if ( factor < 0 ) {
            factor = 0;
        }

        // figure out the closest two color points
        int lowerIndex = (int) (factor * ( availableColors.length - 2 ));
        int higherIndex = lowerIndex + 1;
        Color lowerColor = availableColors[lowerIndex];
        Color higherColor = availableColors[higherIndex];

        double lowerIndexFactor = (double) lowerIndex / ( availableColors.length - 1 );
        double higherIndexFactor = (double) higherIndex / ( availableColors.length - 1 );
        double interpolationFactor = ( factor - lowerIndexFactor ) / ( higherIndexFactor - lowerIndexFactor );

        //System.out.println( String.format( "Lower Index: %d Factor: %f ", lowerIndex, lowerIndexFactor ) );
        //System.out.println( String.format( "Higher Index: %d Factor: %f ", higherIndex, higherIndexFactor ) );
        //System.out.println( String.format( "Interpolation: %f ", interpolationFactor ) );

        int r1 = lowerColor.getRed();
        int r2 = higherColor.getRed();
        int newRed = r1 + (int) ( ( r2 - r1 ) * interpolationFactor );
        //System.out.println( String.format( "Red: lower= %d higher= %d new=%d", r1, r2, newRed ) );
        int g1 = lowerColor.getGreen();
        int g2 = higherColor.getGreen();
        int newGreen = g1 + (int) ( ( g2 - g1 ) * interpolationFactor );
        //System.out.println( String.format( "Red: lower= %d higher= %d new=%d", g1, g2, newGreen ) );
        int b1 = lowerColor.getBlue();
        int b2 = higherColor.getBlue();
        int newBlue = b1 + (int) ( ( b2 - b1 ) * interpolationFactor );
        //System.out.println( String.format( "Red: lower= %d higher= %d new=%d", b1, b2, newBlue ) );
        Color gradientColor = new Color( newRed, newGreen, newBlue );

        return gradientColor;

    }
}
