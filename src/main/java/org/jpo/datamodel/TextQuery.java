package org.jpo.datamodel;

import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

/*
 TextQuery.java:  The parameters for a search

 Copyright (C) 2002 - 2020  Richard Eigenmann.
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
public class TextQuery implements Serializable, Query {

    /**
     * Keep serialisation happy
     */
    private static final long serialVersionUID = 1;

    /**
     * If defined this String will be checked against any field on the nodes
     *
     */
    public final String anyField;

    /**
     * This flag indicates whether dates that can't be parsed should be treaded
     * as matches or fails	;
     */
    private static final boolean includeNullDates = true;

    /**
     * Constructor to create a new Query object.
     *
     * @param	anyField	the search key to be checked for in any of the data
     * fields.
     *
     */
    public TextQuery( final String anyField ) {
        this.anyField = anyField;
    }

    /**
     * The lower date range of the search. If null the lower date will be
     * ignored.
     */
    private Calendar lowerDateRange = null;

    /**
     * Method to set the lower date range of the search. If null the lower date
     * will be ignored.
     *
     * @param lowerDateRange The lower date range as String
     */
    public void setLowerDateRange( final Calendar lowerDateRange ) {
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
    private Calendar upperDateRange = null;

    /**
     * Method to set the upper date range of the search. If null the upper date
     * will be ignored.
     *
     * @param upperDateRange The upper date range
     */
    public void setUpperDateRange( final Calendar upperDateRange ) {
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
     * @param startNode The start node
     */
    public void setStartNode( final SortableDefaultMutableTreeNode startNode ) {
        this.startNode = startNode;
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

        for (Enumeration<TreeNode> e = startNode.breadthFirstEnumeration(); e.hasMoreElements(); ) {
            testNode = (SortableDefaultMutableTreeNode) e.nextElement();
            if ( isMatch( testNode ) ) {
                searchResults.add( testNode );
            }
        }
        return searchResults;
    }

    /**
     * Forces the query to re-executed
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
    public boolean isMatch( final SortableDefaultMutableTreeNode n ) {
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

        final Calendar testNodeDate = pi.getCreationTimeAsDate();
        if ( match && ( lowerDateRange != null ) ) {
            // test for the lower date range
            if ( testNodeDate == null ) {
                match = includeNullDates;
            } else {
                match =  testNodeDate.compareTo( lowerDateRange ) >= 0;
            }
        }

        if ( match && ( upperDateRange != null ) ) {
            // test for the lower date range
            if ( testNodeDate == null ) {
                match = includeNullDates;
            } else {
                match = testNodeDate.compareTo( upperDateRange ) <= 0 ;
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
        String nodeDescription = ( startNode == null ) ? "" : startNode.getUserObject().toString();

        return Settings.getJpoResources().getString("ThumbnailSearchResults") + anyField + Settings.getJpoResources().getString("ThumbnailSearchResults2") + nodeDescription;
    }

    /**
     * returns a the title for the search that can be used to display the search
     * results under.
     */
    @Override
    public String toString() {
        return getTitle();
    }
}
