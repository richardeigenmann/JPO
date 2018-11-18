package jpo.dataModel;

import java.io.Serializable;


/*
 Copyright (C) 2002-2016  Richard Eigenmann.
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
 * A class which represents a Category
 *
 */
public class Category implements Serializable {

    private static final long serialVersionUID = 1;

    /**
     * variable that records the Category
     */
    private Integer key;
    /**
     * variable that records the value of the Category
     */
    private String value;
    /**
     * This status represents the category as being in undefined state
     */
    public static final int UNDEFINED = 0;
    /**
     * This status represents the category as being selected
     *
     */
    public static final int SELECTED = UNDEFINED + 1;
    /**
     * this status represents the category as being unselected
     *
     */
    public static final int UN_SELECTED = SELECTED + 1;
    /**
     * This status represents the category being both selected and unselected at
     * the same time used for situations where we have more than one category
     * selected at a time
     */
    public static final int BOTH = UN_SELECTED + 1;
    /**
     * variable that records the state of the Category
     *
     * @see Category#UNDEFINED , Category#SELECTED , Category#UN_SELECTED ,
     * Category#BOTH
     */
    private int status;  // default is 0

    /**
     * Constructs a new category for the key and value
     *
     * @param key The numeric key for the category
     * @param value the value
     *
     */
    public Category( Integer key, String value ) {
        setKey( key );
        setValue( value );
    }

    /**
     * Returns the key of the Category
     *
     * @return They key of the Category
     *
     */
    public Integer getKey() {
        return key;
    }

    /**
     * Sets the key of the Category
     *
     * @param key The new key
     */
    public void setKey( Integer key ) {
        this.key = key;
    }

    /**
     * Call this method to set the state of the Category to selected or not
     * selected.
     *
     * @param newState The new state
     */
    public void setStatus( int newState ) {
        status = newState;
    }

    /**
     * Call this method to find out if the Category is selected
     *
     * @return true if it is selected, false if it is not selected or partially
     * selected
     */
    public int getStatus() {
        return status;
    }

    /**
     * Returns the value of the Category
     *
     * @return They value of the Category
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the Category
     *
     * @param value The new value
     */
    public void setValue( String value ) {
        this.value = value;
    }

    /**
     * Returns the value of the Category.
     *
     * @return the value of the Category
     */
    @Override
    public String toString() {
        return getValue();
    }
}
