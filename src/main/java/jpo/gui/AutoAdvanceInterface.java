package jpo.gui;

import jpo.dataModel.NodeNavigatorInterface;

/**
 *
 * @author Richard Eigenmann%
 */
public interface AutoAdvanceInterface {

    /**
     * This method sets up the Advance Timer
     *
     * @param seconds Seconds
     */
    public void startAdvanceTimer( int seconds );

    /**
     * Puts the picture of the indicated node onto the viewer panel
     *
     * @param mySetOfNodes The set of nodes from which one picture is to be
     * shown
     * @param myIndex The index of the set of nodes to be shown.
     */
    public void showNode( NodeNavigatorInterface mySetOfNodes, int myIndex );
}
