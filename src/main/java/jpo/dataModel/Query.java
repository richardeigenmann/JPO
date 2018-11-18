package jpo.dataModel;


/*
Query.java:  An Interface that specifies what a query must implement so it can be shown

Copyright (C) 2006 - 2009  Richard Eigenmann.
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
 *  An Interface that specifies what a query must implement so it can be shown
 */
public interface Query {

    /**
     *  The query must be able to say how many results it will return.
     *
     * @return the number of results
     */
    int getNumberOfResults();


    /**
     *  This method returns the {@link SortableDefaultMutableTreeNode} node for the indicated position
     *  in the query. If the
     *  index is out of bounds it returns null. The valid range is from 0 to {@link #getNumberOfResults}.
     *
     *  @param index   The component index that is to be returned 0 to {@link #getNumberOfResults}.
     *  @return the node for the index
     */
    SortableDefaultMutableTreeNode getIndex(int index);


    /**
     *  Returns a title for the search that can be used to display the search results under.
     *
     *  @return a string for the title
     */
    String getTitle();


    /**
     *  returns a the title for the search that can be used to display the search results under.
     *
     *  @return a title
     */
    @Override
    String toString();


    /**
     *  Forces the results to be refreshed
     */
    void refresh();
}
