package org.jpo.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2010-2024 Richard Eigenmann.
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
 * This class finds duplicates and adds them to a query object
 *
 * @author Richard Eigenmann
 */
public class DuplicatesQuery
        implements Query {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( DuplicatesQuery.class.getName() );

    public DuplicatesQuery(final PictureCollection pictureCollection) {
        this.pictureCollection = pictureCollection;
        extractSearchResults();
    }

    private final PictureCollection pictureCollection;

    /**
     * The number of entries found
     *
     * @return the number of entries found
     */
    @Override
    public int getNumberOfResults() {
        if ( searchResults == null ) {
            extractSearchResults();
        }
        return searchResults.size();
    }

    /**
     * Returns the element specified in the index
     *
     * @param index The element to be returned
     * @return returns the node or null if the query is not right.
     */
    @Override
    public SortableDefaultMutableTreeNode getIndex( int index ) {
        if ( ( index < 0 ) || ( index >= getNumberOfResults() ) ) {
            return null;
        }
        return searchResults.get( index );
    }

    /**
     * Returns a title for the query
     *
     * @return The title for the query
     */
    @Override
    public String getTitle() {
        return "Duplicates";
    }

    /**
     * returns the title for the search that can be used to display the search
     * results under. The JTree asks for toString()
     */
    @Override
    public String toString() {
        return getTitle();
    }

    /**
     * Refreshes the search results
     */
    @Override
    public void refresh() {
        LOGGER.info( "refresh called" );
        searchResults.clear();
        extractSearchResults();
    }

    /**
     * ResultSet so that the query is not reexecuted every time a user clicks
     */
    private List<SortableDefaultMutableTreeNode> searchResults = new ArrayList<>();

    /**
     * Finds the duplicates
     */
    private void extractSearchResults() {
        var nodeList = pictureCollection.getRootNode().getChildPictureNodes(true);
        int size = nodeList.size();
        LOGGER.log(Level.INFO, "Built a list of {0} picture nodes.", size );

        for (int i = 0; i < size; i++ ) {
            final var baseNode = nodeList.get( i );
            final var baseNodePictureInfo = (PictureInfo) baseNode.getUserObject();
            if ( i % 250 == 0 ) {
                LOGGER.log(Level.INFO, "Processed {0} potential duplicates out of {1}]", new Object[]{i, size});
            }
            for (int j = i + 1; j < size; j++ ) {
                final var compareNodePictureInfo = (PictureInfo) nodeList.get( j ).getUserObject();
                if ((baseNodePictureInfo.getImageFile().equals(compareNodePictureInfo.getImageFile()))
                        || ((!baseNodePictureInfo.getSha256().equals("")) && (baseNodePictureInfo.getSha256().equals(compareNodePictureInfo.getSha256())))) {
                    LOGGER.log(Level.INFO, "Found a duplicate: {0} = {1}", new Object[]{baseNode, nodeList.get(j)});
                    searchResults.add(baseNode);
                    searchResults.add(nodeList.get(j));
                }
            }
        }
    }

    public PictureCollection getPictureCollection() {
        return pictureCollection;
    }

}
