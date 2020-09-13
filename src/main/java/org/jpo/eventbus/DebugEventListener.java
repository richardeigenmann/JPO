package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2020  Richard Eigenmann.
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
 * A dummy event listener that listens to all events and simply logs them.
 * Should be helpful to debug what is going on.
 *
 * @author Richard Eigenmann
 */
public class DebugEventListener {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( DebugEventListener.class.getName() );

    /**
     * This event listener registers so it receives all types of event objects
     * from the EventBus and then logs them on the console.
     *
     * @param o the event
     */
    @Subscribe
    public void handleAllEvents(final Object o) {
        LOGGER.log(Level.INFO, "Event propagating: {0}", o.getClass());
    }

}
