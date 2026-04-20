package org.jpo.datamodel;

/*
Copyright (C) 2006-2025  Richard Eigenmann, Zürich Switzerland
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version. This program is distributed 
in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
more details. You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
The license is in gpl.txt.
See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * This class implements the {@link org.jpo.datamodel.NodeNavigator}  in the specific manner that is required for
 * displaying {@link Query} in the {@link org.jpo.gui.ThumbnailsPanelController}.
 */
public class QueryNavigator
        extends NodeNavigator {

    /**
     * A reference to the query group that shall be browsed
     */
    @SuppressWarnings("WeakerAccess")
    protected Query myQuery;


    /**
     * Constructs a new Group Browser object
     *
     * @param queryToBrowse the Query for the browser
     */
    public QueryNavigator(Query queryToBrowse) {
        setQuery(queryToBrowse);
    }


    /**
     * call this method to specify the query that should be browsed.
     *
     * @param queryToBrowse The {@link Query} which should be browsed.
     */
    public void setQuery(Query queryToBrowse) {
        myQuery = queryToBrowse;
        myQuery.refresh();
    }


    /**
     * returns the {@link Query} for this QueryNavigator
     *
     * @return the query for the browser
     */
    public Query getQuery() {
        return myQuery;
    }


    /**
     * returns the title of the Query being displayed
     *
     * @return The title of the query
     */
    @Override
    public String getTitle() {
        return getQuery().getTitle();
    }


    /**
     * On a group we return the number of children in the group.
     *
     * @return the number of nodes
     */
    @Override
    public int getNumberOfNodes() {
        if (myQuery == null) {
            return 0;
        } else {
            return myQuery.getNumberOfResults();
        }
    }


    /**
     * This method returns the {@link SortableDefaultMutableTreeNode} node for the indicated position in the group
     * If there are more Thumbnails than nodes in the group it returns null.
     *
     * @param index The component index that is to be returned.
     * @return the node for the index
     */
    @Override
    public SortableDefaultMutableTreeNode getNode(int index) {
        if (myQuery == null) {
            return null;
        } else {
            return myQuery.getIndex(index);
        }
    }


}
