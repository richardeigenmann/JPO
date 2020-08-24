package org.jpo.gui;

import org.jpo.datamodel.NodeNavigatorInterface;

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
    void startAdvanceTimer(int seconds);

    /**
     * Puts the picture of the indicated node onto the viewer panel
     *
     * @param mySetOfNodes The set of nodes from which one picture is to be
     * shown
     * @param myIndex The index of the set of nodes to be shown.
     */
    void showNode(NodeNavigatorInterface mySetOfNodes, int myIndex);
}
