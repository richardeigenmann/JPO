package org.jpo.eventbus;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2017-2020  Richard Eigenmann.
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
 * A Singleton for the JPO Application EventBus.
 * @see <a href="https://github.com/google/guava/wiki/EventBusExplained">https://github.com/google/guava/wiki/EventBusExplained</a>
 * @author Richard Eigenmann
 */
public class JpoEventBus extends EventBus {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( JpoEventBus.class.getName() );


    /**
     * Returns the EventBus for the JPO application
     *
     * @return the EventBus singleton
     */
    public static JpoEventBus getInstance() {
        return JpoEventBusHolder.INSTANCE;
    }
    /**
     * The EventBus singleton
     */
    private JpoEventBus() {
        register( new DeadEventSubscriber() );
    }

    /**
     * Singleton for the EventBus
     */
    private static class JpoEventBusHolder {

        /**
         * The instance of the event bus
         */
        private static final JpoEventBus INSTANCE = new JpoEventBus();
        
        /**
         * Explicit private constructor to prevent the default constructor.
         * @see <a href="https://sonarcloud.io/organizations/richardeigenmann-github/rules#rule_key=squid%3AS1118">https://sonarcloud.io/organizations/richardeigenmann-github/rules#rule_key=squid%3AS1118</a>
         */
        private JpoEventBusHolder() {
            throw new IllegalStateException("Utility class");
        }

    }

    /**
     * A subscriber for a dead event
     */
    private static class DeadEventSubscriber {

        /**
         * Gets called with dead events
         *
         * @param deadEvent the dead event
         */
        @Subscribe
        public void handleDeadEvent(final DeadEvent deadEvent) {
            LOGGER.log(Level.WARNING, "Dead event of class: {0}", deadEvent.getClass().getCanonicalName());
        }

    }

}
