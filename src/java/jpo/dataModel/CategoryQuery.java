package jpo.dataModel;

import java.util.*;
import java.util.logging.Logger;


/*
CategoryQuery.java:  A type of query for Categories

Copyright (C) 2006-2009  Richard Eigenmann.
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
 *  This class implements the {@link Query} interface to show all the nodes attached to a category.
 */
public class CategoryQuery implements Query {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( CategoryQuery.class.getName() );

    /**
     * the category key for the Query
     */
    private Integer key = null;

    /**
     * An ArrayList of the nodes that represent these images
     */
    private ArrayList<SortableDefaultMutableTreeNode> resultList = null;


    /**
     * Constructor for a Category Query
     * @param key
     */
    public CategoryQuery( Integer key ) {
        this.key = key;
        this.refresh();
    }


    /**
     *  The query must be able to say how many results it will return.
     * @return the number of results
     */
    public int getNumberOfResults() {
        if ( resultList == null ) {
            logger.info( "CategoryQuery.getNumberOfResults: called on a null result set." );
            return 0;
        }
        return resultList.size();
    }


    /**
     *  This method returns the SDMTN node for the indicated position in the query. If the
     *  index is out of bounds it returns null.
     *
     *  @param index   The component index that is to be returned.
     * @return the node for the position
     */
    public SortableDefaultMutableTreeNode getIndex( int index ) {
        if ( resultList == null ) {
            logger.info( "CategoryQuery.getIndex: called on a null result set." );
            return null;
        }
        if ( ( index < 0 ) || ( index >= resultList.size() ) ) {
            return null;
        }
        return resultList.get( index );
    }


    /**
     *  returns a title for the search that can be used to display the search results under.
     * @return a title
     */
    public String getTitle() {
        return toString();
    }


    /**
     *  returns a the title for the search that can be used to display the search results under.
     * @return the title for the search
     */
    @Override
    public String toString() {
        return Settings.jpoResources.getString( "CategoryQuery" ) + Settings.pictureCollection.getCategory( key ).toString();
    }


    /**
     *  This method retrieves a new ArrayList of nodes that match the category.
     */
    public void refresh() {
        resultList = null;
        resultList = PictureCollection.getCategoryUsageNodes( key, Settings.pictureCollection.getRootNode() );
    }
}
