package jpo;

import java.io.Serializable;


/*
Category.java:  A class which represents a Category

Copyright (C) 2002  Richard Eigenmann.
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
	public Object key;
	public Object value;
		
	public Category ( Object key, Object value ) {
		setKey( key );
		setValue( value );
	}
		
	public String toString() {
		return (String) value;
	}
	
	public Object getKey() {
		return key;
	}
	
	public void setKey( Object key ) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue( Object value ) {
		this.value = value;
	}
	
	
}
