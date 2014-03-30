package jpo.EventBus;

import jpo.dataModel.Query;

/**
 * This request indicates that the user wants to see the results of a query
 *
 * @author Richard eigenmann
 */
public class ShowQueryRequest implements Request {

    private final Query query;

    /**
     * A request to show the thumbnails of a query
     *
     * @param query The node with the thumbnails to show
     */
    public ShowQueryRequest( Query query  ) {
        this.query = query;
    }

    /**
     * Returns the query for which the thumbnails are to be shown.
     *
     * @return the query
     */
    public Query getQuery() {
        return query;
    }

}
