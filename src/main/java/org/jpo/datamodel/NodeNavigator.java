package org.jpo.datamodel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
 Copyright (C) 2006-2025 Richard Eigenmann, Zürich, Switzerland
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
 * This class implements the NodeNavigatorListener functionality required by the
 * NodeNavigatorInterface but the other methods need to be implements by the
 * extending class.
 */
public abstract class NodeNavigator
        implements NodeNavigatorInterface {

    /**
     * The listeners to notify about a structural change
     */
    private final Set<NodeNavigatorListener> nodeNavigatorListeners = Collections.synchronizedSet( new HashSet<>() );

    /**
     * Registers a NodeNavigatorListener
     */
    @Override
    public void addNodeNavigatorListener(final NodeNavigatorListener nodeNavigatorListener) {
        nodeNavigatorListeners.add(nodeNavigatorListener);
    }

    /**
     * Removes a NodeNavigatorListener
     */
    @Override
    public void removeNodeNavigatorListener(final NodeNavigatorListener nodeNavigatorListener) {
        nodeNavigatorListeners.remove(nodeNavigatorListener);
    }

    /**
     * Method that notifies the NodeNavigatorListener of a structural change
     * that they need to respond to.
     */
    @Override
    public void notifyNodeNavigatorListeners() {
        synchronized ( nodeNavigatorListeners ) {
            nodeNavigatorListeners.forEach(NodeNavigatorListener::nodeLayoutChanged);
        }
    }

}
