package jpo.dataModel;

import java.util.AbstractList;

/**
 *
 * @author Richard Eigenmann
 */
public class StaticNodesQuery implements Query {

    AbstractList<SortableDefaultMutableTreeNode> nodes;

    /**
     * Creates a static nodes query
     * @param title the title
     * @param nodes  the nodes
     */
    public StaticNodesQuery( String title, AbstractList<SortableDefaultMutableTreeNode> nodes ) {
        this.title = title;
        this.nodes = nodes;
    }

    @Override
    public int getNumberOfResults() {
        return nodes.size();
    }

    @Override
    public SortableDefaultMutableTreeNode getIndex( int index ) {
        return nodes.get( index );
    }

    private String title = "";
    
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void refresh() {
        // do nothing.
    }

}
