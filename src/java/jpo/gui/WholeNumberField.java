package jpo.gui;

import jpo.dataModel.Settings;
import jpo.*;
import javax.swing.*;
import javax.swing.text.*;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Logger;


/*
I would guess this is at least partially if not wholly copyright of Sun Microsystems 
since I lifted this code from their Java Swing Tutorial. Richard Eigenmann February 2002.

WholeNumberField.java:  a textfield that allows only entry of proper numbers

Copyright (C) 2002-2009  Richard Eigenmann (for the modifications over the original I copied)
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
 *  a clever JTextField that allows only numbers to be captured
 */
public class WholeNumberField extends JTextField {

       /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( WholeNumberField.class.getName() );
    
    /**
     *  not quite sure what this is for.
     */
    private NumberFormat integerFormatter;


    /**
     *  Constructor.
     *  @param 	value	The initial value of the field
     *  @param	columns	The width of the field
     */
    public WholeNumberField( int value, int columns ) {
        super( columns );
        integerFormatter = NumberFormat.getNumberInstance( Settings.getCurrentLocale() );
        integerFormatter.setParseIntegerOnly( true );
        setValue( value );
    }


    /**
     *  indicate whether the WholeNumberField is supposed to allow decimals
     *
     * @param allowDecimal
     */
    public void setAllowDecimal( boolean allowDecimal ) {
        integerFormatter.setParseIntegerOnly( false );
    }


    /**
     *  method that returns the value of the WholeNumberField
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
     *  method that returns the value of the WholeNumberField
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
     *  method that sets the value of the WholeNumberField
     *
     * @param value
     */
    public void setValue( int value ) {
        logger.fine("WholeNumberField.setValue("+Integer.toString(value)+")");
        setText( Integer.toString( value ) );
    }


    /**
     *  method that sets the value of the WholeNumberField
     *
     * @param value
     */
    public void setValue( double value ) {
        setText( integerFormatter.format( value ) );
    }


    /**
     *  part of the inner workings
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
    protected class WholeNumberDocument extends PlainDocument {

        /**
         * 
         * @param offs
         * @param str
         * @param a
         * @throws javax.swing.text.BadLocationException
         */
        @Override
        public void insertString( int offs,
                String str,
                AttributeSet a )
                throws BadLocationException {

            char[] source = str.toCharArray();
            char[] result = new char[source.length];
            int j = 0;

            for ( int i = 0; i < result.length; i++ ) {
                if ( Character.isDigit( source[i] ) ) {

                    result[j++] = source[i];
                } else {
                    logger.info( "WholdNumberField.WholdNumberDocument.insertString_ Refusing to insert character: " + source[i] );
                }
            }
            super.insertString( offs, new String( result, 0, j ), a );
        }
    }
}
