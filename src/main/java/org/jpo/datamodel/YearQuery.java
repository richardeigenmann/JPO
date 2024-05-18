package org.jpo.datamodel;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/*
 Copyright (C) 2014-2024 Richard Eigenmann.
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
 * This class stores the parameters for a search and can return a List of the
 * search results.
 */
public class YearQuery implements Serializable, Query {

    /**
     * Keep serialisation happy
     */
    @Serial
    private static final long serialVersionUID = 1;

    /**
     * This flag indicates whether dates that can't be parsed should be treated
     * as matches or fails
     */
    private boolean includeNullDates;  // default is false

    private final String year;

    /**
     * Constructor to create a new Query object.
     *
     * @param	year	the year we want to search for
     *
     */
    public YearQuery( String year ) {
        this.year = year;
        setLowerDateRange( Tools.parseDate( "01.01." + year ) );
        setUpperDateRange( Tools.parseDate( "31.12." + year ) );
    }

    /**
     * The lower date range of the search. If null the lower date will be
     * ignored.
     */
    private Calendar lowerDateRange;

    /**
     * Method to set the lower date range of the search. If null the lower date
     * will be ignored.
     *
     * @param lowerDateRange Lower date range
     */
    public void setLowerDateRange( Calendar lowerDateRange ) {
        this.lowerDateRange = lowerDateRange;
    }

    /**
     * Method to get the lower date range of the search.
     *
     * @return returns the lower date range
     */
    public Calendar getLowerDateRange() {
        return lowerDateRange;
    }

    /**
     * The upper date range of the search. If null the upper date will be
     * ignored.
     */
    private Calendar upperDateRange;

    /**
     * Method to set the upper date range of the search. If null the upper date
     * will be ignored.
     *
     * @param upperDateRange The upper date range
     */
    public void setUpperDateRange( Calendar upperDateRange ) {
        this.upperDateRange = upperDateRange;
    }

    /**
     * Method to get the upper date range of the search.
     *
     * @return the upper date range
     */
    public Calendar getUpperDateRange() {
        return upperDateRange;
    }

    /**
     * the start node for the search
     */
    private SortableDefaultMutableTreeNode startNode;

    /**
     * set the start node for the search
     *
     * @param n node
     */
    public void setStartNode( SortableDefaultMutableTreeNode n ) {
        this.startNode = n;
    }

    /**
     * Variable for the resultSet so that the query is not reexecuted every time
     * some object wants to know something.
     */
    private List<SortableDefaultMutableTreeNode> searchResults;

    /**
     * Returns a List of nodes which match the query criteria beneath the
     * supplied node.
     *
     * @return The List of nodes.
     */
    public List<SortableDefaultMutableTreeNode> getSearchResults() {
        if ( searchResults == null ) {
            searchResults = extractSearchResults();
        }
        return searchResults;
    }

    /**
     * On a group we return the number of children in the group.
     */
    @Override
    public int getNumberOfResults() {
        if ( searchResults == null ) {
            searchResults = extractSearchResults();
        }
        return searchResults.size();
    }

    /**
     * This method returns the SDMTN node for the indicated position in the
     * group If there are more Thumbnails than nodes in the group it returns
     * null.
     *
     * @param index The component index that is to be returned.
     */
    @Override
    public SortableDefaultMutableTreeNode getIndex(final int index) {
        if (index >= getNumberOfResults()) // forces execute of query if not yet executed
        {
            return null;
        } else {
            return searchResults.get(index);
        }
    }

    /**
     * Returns a List of nodes which match the query criteria beneath the
     * supplied node.
     *
     * @return The List of nodes.
     */
    public List<SortableDefaultMutableTreeNode> extractSearchResults() {
        SortableDefaultMutableTreeNode testNode;
        searchResults = new ArrayList<>();

        for (final var enumeration = startNode.breadthFirstEnumeration(); enumeration.hasMoreElements(); ) {
            testNode = (SortableDefaultMutableTreeNode) enumeration.nextElement();
            if (isMatch(testNode)) {
                searchResults.add(testNode);
            }
        }
        return searchResults;
    }

    /**
     * Forces the query to be re-executed
     */
    @Override
    public void refresh() {
        extractSearchResults();
    }

    /**
     * this method returns whether the supplied node matches the search
     * criteria.
     *
     * @param n the Node which is to be tested.
     * @return true if the node matches the query, false if not.
     */
    public boolean isMatch(final SortableDefaultMutableTreeNode n) {
        if (!(n.getUserObject() instanceof final PictureInfo pictureInfo)) {
            // it's not a pictureInfo node, so it can't be a match.
            return false;
        }

        var match = true;
        final var testNodeDate = pictureInfo.getCreationTimeAsDate();
        if ( lowerDateRange != null ) {
            // test for the lower date range
            if ( testNodeDate == null ) {
                match = includeNullDates;
            } else {
                match = testNodeDate.compareTo( lowerDateRange ) >= 0 ;
            }
        }

        if ( match && ( upperDateRange != null ) ) {
            // test for the lower date range
            if ( testNodeDate == null ) {
                match = includeNullDates;
            } else {
                match = testNodeDate.compareTo( upperDateRange ) <= 0;
            }
        }

        return match;
    }

    /**
     * returns a title for the search that can be used to display the search
     * results under.
     */
    @Override
    public String getTitle() {
        return year;
    }

    /**
     * returns the title for the search that can be used to display the search
     * results under.
     */
    @Override
    public String toString() {
        return getTitle();
    }

    public PictureCollection getPictureCollection() {
        return startNode.getPictureCollection();
    }

}
