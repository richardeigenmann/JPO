package org.jpo.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*
 Copyright (C) 2006-2024 Richard Eigenmann.
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
 * This class implements the {@link Query} interface to show all the nodes
 * attached to a category.
 */
public class CategoryQuery implements Query {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( CategoryQuery.class.getName() );

    /**
     * The collection for which this query should run
     */

    private final PictureCollection pictureCollection;

    /**
     * the category key for the Query
     */
    private final Integer key;

    /**
     * A List of the nodes that represent these images
     */
    private List<SortableDefaultMutableTreeNode> resultList = new ArrayList<>();

    /**
     * Constructor for a Category Query
     *
     * @param key The key for the category
     */
    public CategoryQuery( final PictureCollection pictureCollection, final Integer key ) {
        this.pictureCollection = pictureCollection;
        this.key = key;
        refresh();
    }

    /**
     * The query must be able to say how many results it will return.
     *
     * @return the number of results
     */
    @Override
    public int getNumberOfResults() {
        return resultList.size();
    }

    /**
     * This method returns the SDMTN node for the indicated position in the
     * query. If the index is out of bounds it returns null.
     *
     * @param index The component index that is to be returned.
     * @return the node for the position
     */
    @Override
    public SortableDefaultMutableTreeNode getIndex( int index ) {
        if ( ( index < 0 ) || ( index >= resultList.size() ) ) {
            return null;
        }
        return resultList.get( index );
    }

    /**
     * returns a title for the search that can be used to display the search
     * results under.
     *
     * @return a title
     */
    @Override
    public String getTitle() {
        return toString();
    }

    /**
     * returns the title for the search that can be used to display the search
     * results under.
     *
     * @return the title for the search
     */
    @Override
    public String toString() {
        return Settings.getJpoResources().getString("CategoryQuery") + pictureCollection.getCategory(key);
    }

    /**
     * This method retrieves a new List of nodes that match the category.
     */
    @Override
    public void refresh() {
        resultList.clear();
        collectMatchingNodes(key, pictureCollection.getRootNode());
    }

    /**
     * Returns a List of the nodes that match this category
     *
     * @param key       The key of the category to find
     * @param startNode the node at which to start
     * @return the list of nodes
     */
    private void collectMatchingNodes(
            final Integer key,
            final SortableDefaultMutableTreeNode startNode) {
        startNode.getChildPictureNodes(true)
                .stream()
                .filter( node -> ((PictureInfo) node.getUserObject()).containsCategory( key ))
                .forEach(resultList::add);
    }

}
