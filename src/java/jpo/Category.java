package jpo;

import java.io.Serializable;


/*
Category.java:  A class which represents a Category
 
Copyright (C) 2002-2007  Richard Eigenmann.
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
    
    /**
     *  variable that records the Category
     */
    private Object key;

    /**
     *  variable that records the value of the Category
     */
    private Object value;

    
    /**
     * This status represents the category as being in undefined state
     */
    public static final int undefined = 0;
    
    /**
     *  This status represents the category as being selected
     **/
    public static final int selected = undefined + 1;
    
    /**
     *  this status represents the category as being unselected
     **/
    public static final int unSelected = selected + 1;
    
    /**
     *  This status represents the categoring being both selected and unselected at the same time
     *  used for situations where we have more than one category selected at a time
     */
    public static final int both = unSelected + 1;
    
    /**
     *  variable that records the state of the Category
     *  @see #undefined, #selected, #unSelected, #both
     */
    private int status = 0;
    
    /**
     *  Constructs a new category for the key and value
     * @param  key   The numeric key for the category
     * @param  value the value
     **/
    public Category( Object key, Object value ) {
        setKey( key );
        setValue( value );
    }
    
        /*public String toString() {
                return (String) value;
        }*/
    
    public Object getKey() {
        return key;
    }
    
    public void setKey( Object key ) {
        this.key = key;
    }
    
    
    /**
     *  Call this method to set the state of the Category to selected or not selected.
     */
    public void setStatus( int newState ) {
        status = newState;
    }
    
    
    /**
     *  Call this method to find out if the Category is selected
     *  @return  true if it is selected, false if it is not selected or partially selected
     */
    public int getStatus() {
        return status;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue( Object value ) {
        this.value = value;
    }
    
    
    
}
