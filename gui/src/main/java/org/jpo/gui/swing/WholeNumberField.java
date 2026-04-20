package org.jpo.gui.swing;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 Copyright (C) 2002-2024 Richard Eigenmann (for the modifications over the original I copied)
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
 * This class extends the JTextField and allow only entry of digits.
 * This was the suggested method in the Swing tutorials in February 2002 when I
 * wrote this class. Java probably didn't have NumberFormatter,
 * FormattedTextFields etc. in those days.
 *
 */
public class WholeNumberField extends JTextField {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( WholeNumberField.class.getName() );

    /**
     * @see NumberFormat
     */
    private final NumberFormat integerFormatter;

    public WholeNumberField() {
        super( 8 );
        integerFormatter = NumberFormat.getNumberInstance( Locale.getDefault() );
        integerFormatter.setParseIntegerOnly( true );
        setValue( 0 );
    }

    /**
     * Constructor.
     *
     * @param defaultValue The initial value of the field
     * @param width The width of the field in number of characters
     */
    public WholeNumberField( int defaultValue, int width ) {
        super( width );
        integerFormatter = NumberFormat.getNumberInstance( Locale.getDefault() );
        integerFormatter.setParseIntegerOnly( true );
        setValue( defaultValue );
    }

    /**
     * method that returns the value of the WholeNumberField
     *
     * @return the value of the field
     */
    public int getValue() {
        int retVal = 0;
        try {
            retVal = integerFormatter.parse( getText() ).intValue();
        } catch ( ParseException e ) {
            // This should never happen because insertString allows
            // only properly formatted data to get in the field.
        }
        return retVal;
    }

    /**
     * method that returns the value of the WholeNumberField
     *
     * @return the value of the field
     */
    public double getValueAsDouble() {
        double retVal = 0;
        try {
            retVal = integerFormatter.parse( getText() ).doubleValue();
        } catch ( ParseException e ) {
            // This should never happen because insertString allows
            // only properly formatted data to get in the field.
        }
        return retVal;
    }

    /**
     * method that sets the value of the WholeNumberField
     *
     * @param value value
     */
    public void setValue( int value ) {
        setText( Integer.toString( value ) );
    }

    /**
     * method that sets the value of the WholeNumberField
     *
     * @param value value
     */
    public void setValue( double value ) {
        setText( integerFormatter.format( value ) );
    }

    /**
     * part of the inner workings
     *
     * @return the document
     */
    @Override
    protected Document createDefaultModel() {
        return new WholeNumberDocument();
    }

    /**
     * part of the inner workings
     */
    protected static class WholeNumberDocument extends PlainDocument {

        /**
         * Inserts String
         * @param offset Offset
         * @param string String
         * @param attributeSet attribute set
         * @throws javax.swing.text.BadLocationException when it goes bad
         */
        @Override
        public void insertString( int offset,
                String string,
                AttributeSet attributeSet )
                throws BadLocationException {

            final var source = string.toCharArray();
            final var result = new char[source.length];
            int j = 0;

            for ( int i = 0; i < result.length; i++ ) {
                if ( Character.isDigit( source[i] ) ) {
                    result[j++] = source[i];
                } else {
                    LOGGER.log( Level.INFO, "Refusing to insert character: {0}", source[i] );
                }
            }
            super.insertString( offset, new String( result, 0, j ), attributeSet );
        }
    }
}
