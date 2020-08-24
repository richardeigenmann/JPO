package org.jpo.datamodel;

import org.jpo.datamodel.Settings.FieldCodes;

/*
SortOption.java:  a String, int object that helps with the sortoptions

Copyright (C) 2002, 2014  Richard Eigenmann.
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
 * A class that holds a description of what will be sorted and it's key
 * @author  Richard Eigenmann
 */
public class SortOption {

    /**
     * remember the description of the sort field
     */
    private final String sortOption;

    /**
     * remember the code for this field
     */
    private final FieldCodes sortCode;


    /**
     * Constructs a SortOption with the description of the sort and the sort key
     * @param sortOption the description of the sort
     * @param sortCode the code for the sort
     */
    SortOption( String sortOption, FieldCodes sortCode ) {
        this.sortOption = sortOption;
        this.sortCode = sortCode;
    }


    /**
     * Returns the sort option description
     * @return the sort option description
     */
    public String getDescription() {
        return sortOption;
    }


    /**
     * Returns the sort option code
     * @return the sort option code
     */
    public FieldCodes getSortCode() {
        return sortCode;
    }


    /**
     * Returns the Description
     * @return the description
     */
    @Override
    public String toString() {
        return getDescription();
    }
}

