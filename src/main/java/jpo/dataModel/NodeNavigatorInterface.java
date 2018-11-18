package jpo.dataModel;

/*
 NodeNavigatorInterface.java:  defines the methods that a "set of nodes" must 
 implement so that the Controller can  identify the Nodes to be displayed.

 Copyright (C) 2002 - 2017  Richard Eigenmann, Zürich, Switzerland
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
 * This interface defines the methods that a "set of nodes" must implement so
 * that the Controller can identify the Nodes to be displayed.
 */
public interface NodeNavigatorInterface {

    /**
     * The implementing class must return the title for the images being shown.
     * This is displayed in the top part of the ThumbnailJScrollPane.
     *
     * @return a title for the browser
     */
    String getTitle();

    /**
     * The implementing class must return the number of nodes it contains.
     *
     * @return the number of nodes
     */
    int getNumberOfNodes();

    /**
     * This method must return the SortableDefaultMutableTreeNode indicated by
     * the position number passed as parameter. If the index is out of bounds
     * null is returned.
     *
     * @param componentNumber The number between 0 and #getNumberOfNodes()
     * @return the node for the index
     */
    SortableDefaultMutableTreeNode getNode(int componentNumber);

    /**
     * Registers a NodeNavigatorListener
     *
     * @param nodeNavigatorListener a NodeNavigatorListener to notify
     */
    void addNodeNavigatorListener(NodeNavigatorListener nodeNavigatorListener);

    /**
     * Removes a NodeNavigatorListener
     *
     * @param nodeNavigatorListener the listener to remove
     */
    void removeNodeNavigatorListener(NodeNavigatorListener nodeNavigatorListener);

    /**
     * Node Navigators must be able to send notifications to their listeners.
     */
    void notifyNodeNavigatorListeners();


}
