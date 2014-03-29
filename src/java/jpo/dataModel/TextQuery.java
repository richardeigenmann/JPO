package jpo.dataModel;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;



/*
TextQuery.java:  The parameters for a search

Copyright (C) 2002 - 2009  Richard Eigenmann.
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
 * This class stores the parameters for a search and can return an ArrayList of the search results.
 */
public class TextQuery implements Serializable, Query {

    /**
     *  If defined this String will be checked against any field on the nodes
     **/
    public String anyField;

    /**
     *  This flag indicates whether a clone of the original node should be returned or
     *  whether we want the node which matches.
     */
    //public boolean clone = true;  // must be for the time being because we change the parent of the node later on
    /**
     *  This flag indicates whether dates that can't be parsed should be
     *  treaded as matches or fails	;
     */
    public boolean includeNullDates = true;


    /**
     *   Constructor to create a new Query object.
     *
     *   @param	anyField	the search key to be checked for in any of the data fields.
     **/
    public TextQuery( String anyField ) {
        this.anyField = anyField;
    }

    /**
     *  The lower date range of the search. If null the lower date will be ignored.
     */
    private Calendar lowerDateRange = null;


    /**
     * Method to set the lower date range of the search. If null the lower date will be ignored.
     * @param lowerDateRange
     */
    public void setLowerDateRange( Calendar lowerDateRange ) {
        this.lowerDateRange = lowerDateRange;
    }


    /**
     * Method to get the lower date range of the search.
     * @return returns the lower date range
     */
    public Calendar getLowerDateRange() {
        return lowerDateRange;
    }

    /**
     *  The upper date range of the search. If null the upper date will be ignored.
     */
    private Calendar upperDateRange = null;


    /**
     * Method to set the upper date range of the search. If null the upper date will be ignored.
     * @param upperDateRange  The upper date range
     */
    public void setUpperDateRange( Calendar upperDateRange ) {
        this.upperDateRange = upperDateRange;
    }


    /**
     *   Method to get the upper date range of the search.
     * @return the upper date range
     */
    public Calendar getUpperDateRange() {
        return upperDateRange;
    }

    /**
     *   the start node for the search
     */
    private SortableDefaultMutableTreeNode startNode;


    /**
     *  set the start node for the search
     * @param n
     */
    public void setStartNode( SortableDefaultMutableTreeNode n ) {
        this.startNode = n;
    }

    /**
     *  Variable for the resultSet so that the query is not reexecuted every time some object wants
     *  to know something.
     */
    private ArrayList<SortableDefaultMutableTreeNode> searchResults = null;


    /**
     *  Returns an ArrayList of nodes which match the query criteria beneath the supplied node.
     *
     *  @return  The ArrayList of nodes.
     */
    public ArrayList<SortableDefaultMutableTreeNode> getSearchResults() {
        if ( searchResults == null ) {
            searchResults = extractSearchResults();
        }
        return searchResults;
    }


    /**
     *  On a group we return the number of children in the group.
     */
    @Override
    public int getNumberOfResults() {
        if ( searchResults == null ) {
            searchResults = extractSearchResults();
        }
        return searchResults.size();
    }


    /**
     *  This method returns the SDMTN node for the indicated position in the group
     *  If there are more Thumbnails than nodes in the group it returns null.
     *
     *  @param index   The component index that is to be returned.
     */
    @Override
    public SortableDefaultMutableTreeNode getIndex( int index ) {
        if ( index >= getNumberOfResults() ) // forces execute of query if not yet executed
        {
            return null;
        } else {
            return searchResults.get( index );
        }
    }


    /**
     *  Returns an ArrayList of nodes which match the query criteria beneath the supplied node.
     *
     *  @return  The ArrayList of nodes.
     */
    public ArrayList<SortableDefaultMutableTreeNode> extractSearchResults() {
        SortableDefaultMutableTreeNode testNode;
        searchResults = new ArrayList<SortableDefaultMutableTreeNode>();

        for ( Enumeration e = startNode.breadthFirstEnumeration(); e.hasMoreElements(); ) {
            testNode = (SortableDefaultMutableTreeNode) e.nextElement();
            if ( isMatch( testNode ) ) {
                searchResults.add( testNode );
            }
        }
        return searchResults;
    }


    /**
     *  Forces the query to re-executed
     */
    @Override
    public void refresh() {
        extractSearchResults();
    }


    /**
     *   this method returns whether the supplied node matches the search criteria.
     *
     *   @param  n  the Node which is to be tested.
     *   @return true if the node matches the query, false if not.
     */
    public boolean isMatch( SortableDefaultMutableTreeNode n ) {
        Object nodeObject = n.getUserObject();
        if ( !( nodeObject instanceof PictureInfo ) ) {
            // it's not a pictureinfo node so it can't be a batch.
            return false;
        }

        PictureInfo pi = (PictureInfo) nodeObject;

        boolean match = false;
        if ( anyField != null ) {
            match = pi.anyMatch( anyField );
        }

        Calendar testNodeDate = pi.getCreationTimeAsDate();
        if ( match && ( lowerDateRange != null ) ) {
            // test for the lower date range
            if ( testNodeDate == null ) {
                match = match && includeNullDates;
            } else {
                match = match && ( testNodeDate.compareTo( lowerDateRange ) >= 0 );
            }
        }

        if ( match && ( upperDateRange != null ) ) {
            // test for the lower date range
            if ( testNodeDate == null ) {
                match = match && includeNullDates;
            } else {
                match = match && ( testNodeDate.compareTo( upperDateRange ) <= 0 );
            }
        }

        return match;
    }


    /**
     *  returns a title for the search that can be used to display the search results under.
     */
    @Override
    public String getTitle() {
        String nodeDescription = ( startNode == null ) ? "" : startNode.getUserObject().toString();

        String title = Settings.jpoResources.getString( "ThumbnailSearchResults" ) + anyField + Settings.jpoResources.getString( "ThumbnailSearchResults2" ) + nodeDescription;
        return title;
    }


    /**
     *  returns a the title for the search that can be used to display the search results under.
     */
    @Override
    public String toString() {
        return getTitle();
    }
}
